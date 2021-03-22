package net.gvsun.datasource;

import net.gvsun.datasource.custom.CustomDataSource;
import net.gvsun.datasource.custom.CustomDatasourceSupplier;
import net.gvsun.datasource.custom.CustomSwitcher;
import net.gvsun.datasource.dto.Result;
import net.gvsun.datasource.dto.SiteDto;
import net.gvsun.datasource.http.FeignExistCondition;
import net.gvsun.datasource.http.InterceptorForDataSource;
import net.gvsun.datasource.redis.RedisConfig;
import net.gvsun.datasource.schedule.DataSourceTransferSchedule;
import net.gvsun.datasource.service.AfterStart;
import net.gvsun.datasource.service.ExecuteMultiDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 数据源配置类
 *
 * @author 陈敬
 * @since 1.1.0-SNAPSHOT
 */
@EnableAspectJAutoProxy//启用AspectJ自动代理
@Import({RedisConfig.class, DatasourceNotFountHandler.class})
public class RoutingConfiguration {
    public static List<CustomDataSource> outDataSources = null;//外置数据源
    private Logger logger = LoggerFactory.getLogger(RoutingConfiguration.class);
    private DatasourceChangeListener datasourceChangeListener = null;
    private CustomDatasourceSupplier customDatasourceSupplier = null;
    private CustomSwitcher customSwitcher = null;

    @Autowired(required = false)
    public void setDatasourceChangeListener(DatasourceChangeListener datasourceChangeListener) {
        this.datasourceChangeListener = datasourceChangeListener;
    }

    @Autowired(required = false)
    public void setCustomDatasourceSupplier(CustomDatasourceSupplier customDatasourceSupplier) {
        this.customDatasourceSupplier = customDatasourceSupplier;
    }

    @Autowired(required = false)
    public void setCustomSwitcher(CustomSwitcher customSwitcher) {
        this.customSwitcher = customSwitcher;
    }

    //使切面生效
    @Bean
    public DataSourceSwitchForServlet dataSourceSwitchForServlet() {
        DataSourceSwitchForServlet dataSourceSwitch = new DataSourceSwitchForServlet();
        if (customSwitcher != null) {
            dataSourceSwitch.setCustomSwitcher(customSwitcher);
        }
        return dataSourceSwitch;
    }

    @Bean
    public ClientDatabase clientDatabase(@Qualifier("datasource") RedisTemplate<String, SiteDto> datasourceRedisTemplate,
                                         @Value("${datasource.projectName}") String projectName) {
        try {
            SiteDto siteDto = ClientDatabase.getPropertiesFromRedis(datasourceRedisTemplate, projectName);
            List<GvsunDataSource> dataSourceList = ClientDatabase.getDataSourceList(siteDto);
            return new ClientDatabase(dataSourceList, projectName);
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    @Bean(name = "clientDatasource")
    @Primary
    public ClientDataSourceRouter clientDatasource(ClientDatabaseContext clientDatabaseContext,
                                                   @Value("${datasource.strictMode:false}") boolean strictMode,
                                                   @Value("${datasource.subDatasource:false}") boolean subDatasource) {
        ClientDatabaseContextHolder.setClientDatabaseContext(clientDatabaseContext, subDatasource);
        Map<Object, Object> targetDataSources = new HashMap<>();

        List<GvsunDataSource> dataSources = clientDatabaseContext.getClientDatabase().getDataSources();
        GvsunDataSource defaultDB = null;
        int defaultDBCount = 0;
        for (GvsunDataSource db : dataSources) {
            if (targetDataSources.containsKey(db.getSchoolName())) {
                throw new RuntimeException(String.format("发现重复的数据源: %s", db.getSchoolName()));
            }
            targetDataSources.put(db.getSchoolName(), db);
            if (db.isDefaultDataSource()) {
                defaultDB = db;
                defaultDBCount++;
            }
        }

        //获取额外的数据源
        if (this.customDatasourceSupplier != null) {
            List<CustomDataSource> others = this.customDatasourceSupplier.supplier();
            outDataSources = others;
            for (CustomDataSource c : others) {
                String identifier = c.getIdentifier();
                if (!targetDataSources.containsKey(identifier)) {
                    targetDataSources.put(identifier, c);
                } else {
                    throw new RuntimeException(String.format("发现重复的数据源: %s", identifier));
                }
            }
        }

        ClientDataSourceRouter clientRoutingDatasource = new ClientDataSourceRouter(clientDatabaseContext, strictMode, subDatasource);
        clientRoutingDatasource.setCustomDatasourceSupplier(customDatasourceSupplier);
        clientRoutingDatasource.setTargetDataSources(targetDataSources);
        if (defaultDB == null) {
            throw new RuntimeException("未配置默认数据源");
        } else if (defaultDBCount > 1) {
            throw new RuntimeException("默认数据源配置数量超过1个，请修改Redis配置");
        } else {
            clientRoutingDatasource.setDefaultTargetDataSource(defaultDB);
        }
        logger.info("数据源[strictMode={}]初始化完成: {}", strictMode, targetDataSources);
        clientDatabaseContext.setClientDataSourceRouter(clientRoutingDatasource);
        return clientRoutingDatasource;
    }

    @Bean
    public DataSourceTransferSchedule dataSourceTransferSchedule(ClientDataSourceRouter dataSource,
                                                                 ClientDatabase clientDatabase,
                                                                 @Qualifier("datasource") RedisTemplate<String, SiteDto> datasourceRedisTemplate,
                                                                 @Value("${datasource.hotUpdate:false}") boolean hotUpdate) {
        DataSourceTransferSchedule dataSourceTransferSchedule = new DataSourceTransferSchedule(clientDatabase,
                datasourceRedisTemplate, dataSource, customDatasourceSupplier);
        if (hotUpdate)
            dataSourceTransferSchedule.go();
        return dataSourceTransferSchedule;
    }

    @Bean
    @Qualifier("datasourceRestTemplate")
    public RestTemplate datasourceRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (interceptors == null) {
            restTemplate.setInterceptors(Collections.singletonList(new InterceptorForDataSource()));
        } else {
            interceptors.add(new InterceptorForDataSource());
            restTemplate.setInterceptors(interceptors);
        }

        return restTemplate;
    }

    @Bean
    public ClientDatabaseContext clientDatabaseContext(@Qualifier("datasource") RedisTemplate<String, SiteDto> datasourceRedisTemplate,
                                                       @Qualifier("datasource-config") RedisTemplate<String, String> configRedisTemplate,
                                                       ClientDatabase clientDatabase,
                                                       @Value("${datasource.enableExecuteFlywayOnMultiDatasource:true}") boolean enable,
                                                       @Value("${datasource.flywayBaselineOnMigrate:false}") boolean baselineOnMigrate,
                                                       @Value("${datasource.subDatasource:false}") boolean subDatasource) {
        ClientDatabaseContext clientDatabaseContext = new ClientDatabaseContext(
                datasourceRedisTemplate,
                configRedisTemplate,
                clientDatabase,
                subDatasource,
                datasourceChangeListener
        );
        clientDatabaseContext.setCustomDatasourceSupplier(customDatasourceSupplier);

        if (enable) {
            logger.info("开始在多个数据源上执行数据库变化日志");
            ExecutorService executorService = Executors.newCachedThreadPool();
            List<GvsunDataSource> ds = clientDatabase.getDataSources();
            Map<String, Future<Result<String>>> map = new HashMap<>();
            int count = 0;
            for (GvsunDataSource d : ds) {
                Future<Result<String>> future = executorService.submit(new ExecuteMultiDataSource(d, baselineOnMigrate));
                map.put(String.format("%s(负责人:%s)", d.getSchoolName(), d.getPersonInCharge()), future);
            }

            Set<String> ks = map.keySet();
            for (String k : ks) {
                Future<Result<String>> future = map.get(k);
                try {
                    Result<String> res = future.get();
                    if (res.getCode() != 0) {
                        count++;
                        logger.error(String.format("执行数据库变化日志出错:%s:%s", k, res.getMsg()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            if (count > 0) {
                System.exit(1);
            }
        }
        return clientDatabaseContext;
    }

    @Bean
    public AfterStart afterStart() {
        return new AfterStart();
    }

    @Bean
    @Conditional(FeignExistCondition.class)
    public Object requestInterceptor(@Value("${datasource.strictMode:false}") boolean strictMode) throws Exception {
        try {
            Constructor<?> constructor = Class.forName("net.gvsun.datasource.http.InterceptorForDataSource2").getConstructor(boolean.class);
            return constructor.newInstance(strictMode);
        } catch (Exception e) {
            logger.error("构建InterceptorForDataSource2时出错", e);
            throw e;
        }
        //return new InterceptorForDataSource2(strictMode);
    }
}
