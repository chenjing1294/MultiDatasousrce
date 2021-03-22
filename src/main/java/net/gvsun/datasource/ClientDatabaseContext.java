package net.gvsun.datasource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.gvsun.datasource.custom.CustomDataSource;
import net.gvsun.datasource.custom.CustomDatasourceSupplier;
import net.gvsun.datasource.dto.CommonConfig;
import net.gvsun.datasource.dto.GvsunDataSourceDto;
import net.gvsun.datasource.dto.SiteDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取当前数据源信息
 *
 * @author 陈敬
 * @since 1.3.1-SNAPSHOT
 */
public class ClientDatabaseContext {
    private static Logger logger = LoggerFactory.getLogger(ClientDatabaseContext.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private RedisTemplate<String, SiteDto> datasourceRedisTemplate;
    private RedisTemplate<String, String> configRedisTemplate;
    private ClientDatabase clientDatabase;
    private ClientDataSourceRouter clientDataSourceRouter;
    private CustomDatasourceSupplier customDatasourceSupplier = null;
    private final boolean subDatasource;
    private final DatasourceChangeListener datasourceChangeListener;

    ClientDatabaseContext(RedisTemplate<String, SiteDto> datasourceRedisTemplate,
                          RedisTemplate<String, String> configRedisTemplate,
                          ClientDatabase clientDatabase, boolean subDatasource,
                          DatasourceChangeListener datasourceChangeListener) {
        this.datasourceRedisTemplate = datasourceRedisTemplate;
        this.configRedisTemplate = configRedisTemplate;
        this.clientDatabase = clientDatabase;
        this.subDatasource = subDatasource;
        this.datasourceChangeListener = datasourceChangeListener;
    }

    void setClientDataSourceRouter(ClientDataSourceRouter clientDataSourceRouter) {
        this.clientDataSourceRouter = clientDataSourceRouter;
    }

    void setCustomDatasourceSupplier(CustomDatasourceSupplier customDatasourceSupplier) {
        this.customDatasourceSupplier = customDatasourceSupplier;
    }

    /**
     * 获取OAuth2的数据源信息
     */
    public SiteDto getOAuth2SiteDto() {
        return ClientDatabase.getPropertiesFromRedis(datasourceRedisTemplate, "oauth2");
    }

    /**
     * 获取当前系统的所有数据源
     */
    public SiteDto getSiteDto() {
        return ClientDatabase.getPropertiesFromRedis(datasourceRedisTemplate, clientDatabase.getProjectName());
    }

    /**
     * 获取当前正在使用的数据源信息
     */
    public GvsunDataSourceDto getCurrentDataSourceDto() {
        String schoolName = ClientDatabaseContextHolder.getClientDatabase();
        List<GvsunDataSource> dataSources = this.clientDatabase.getDataSources();
        GvsunDataSource df = null, tg = null;
        for (GvsunDataSource d : dataSources) {
            if (d.isDefaultDataSource()) {
                df = d;
            }
            if (d.getSchoolName().equals(schoolName)) {
                tg = d;
            }
        }

        if (tg == null && customDatasourceSupplier != null) {
            List<CustomDataSource> supplier = customDatasourceSupplier.supplier();
            for (CustomDataSource c : supplier) {
                if (c.getIdentifier().equals(schoolName)) {
                    GvsunDataSource d = new GvsunDataSource();
                    d.setSchoolName(c.getIdentifier());
                    d.setJdbcUrl(c.getJdbcUrl());
                    d.setUser(c.getUser());
                    d.setPassword(c.getPassword());
                    tg = d;
                }
            }
        }

        if (tg == null) {
            tg = df;
        }

        String sn = tg.getSchoolName();
        String sc = tg.getSchoolCname();
        String url = tg.getJdbcUrl();
        String username = tg.getUser();
        String password = tg.getPassword();
        boolean defaultDataSource = tg.isDefaultDataSource();

        GvsunDataSourceDto gvsunDataSourceDto = new GvsunDataSourceDto();
        gvsunDataSourceDto.setSchoolName(sn);
        gvsunDataSourceDto.setSchoolCname(sc);
        gvsunDataSourceDto.setUsername(username);
        gvsunDataSourceDto.setPassword(password);
        gvsunDataSourceDto.setUrl(url);
        gvsunDataSourceDto.setDefaultDataSource(defaultDataSource);

        return gvsunDataSourceDto;
    }

    /**
     * 获取当前系统的当前源的配置文件
     *
     * @param typeReference 你想把JSON字符串转换成什么类？
     */
    public <T> T getCurrentConfig(TypeReference<T> typeReference) throws IOException {
        return getConfig(getCurrentDataSourceDto().getSchoolName(), typeReference);
    }

    /**
     * 保存当前源的配置文件
     *
     * @param config 配置
     */
    public <T> void saveConfig(T config) throws JsonProcessingException {
        String schoolName = getCurrentDataSourceDto().getSchoolName();
        String projectName = clientDatabase.getProjectName();
        saveConfig(projectName, schoolName, config);
    }

    /**
     * 保存指定源的配置文件
     *
     * @param schoolName 哪个源
     * @param config     配置
     */
    public <T> void saveConfig(String schoolName, T config) throws JsonProcessingException {
        saveConfig(clientDatabase.getProjectName(), schoolName, config);
    }

    /**
     * 保存指定源、指定系统的配置（不允许写其他系统的配置文件）
     */
    protected <T> void saveConfig(String projectName, String schoolName, T config) throws JsonProcessingException {
        if (config != null && !StringUtils.isEmpty(projectName) && !StringUtils.isEmpty(schoolName)) {
            String json = objectMapper.writeValueAsString(config);
            configRedisTemplate.opsForHash().put("platform-config-" + projectName, schoolName, json);
        }
    }

    /**
     * 获取当前系统的指定源的配置文件
     *
     * @param schoolName    数据源名
     * @param typeReference 你想把JSON字符串转换成什么类？
     */
    public <T> T getConfig(String schoolName, TypeReference<T> typeReference) throws IOException {
        return getConfig(clientDatabase.getProjectName(), schoolName, typeReference);
    }

    /**
     * 获取指定系统的指定源的配置文件
     *
     * @param projectName   项目名
     * @param schoolName    数据源名
     * @param typeReference 你想把JSON字符串转换成什么类？
     */
    public <T> T getConfig(String projectName, String schoolName, TypeReference<T> typeReference) throws IOException {
        String o = (String) configRedisTemplate.opsForHash().get("platform-config-" + projectName, schoolName);
        return o != null ? objectMapper.readValue(o, typeReference) : null;
    }

    /**
     * 获取所有项目的公共配置（指定源）
     *
     * @param schoolName 数据源
     */
    public CommonConfig getCommonConfig(String schoolName) throws IOException {
        String o = (String) configRedisTemplate.opsForHash().get("platform-config-common", schoolName);
        return o != null ? objectMapper.readValue(o, CommonConfig.class) : null;
    }

    /**
     * 获取所有项目的公共配置（当前源）
     */
    public CommonConfig getCommonConfig() throws IOException {
        String schoolName = getCurrentDataSourceDto().getSchoolName();
        if (schoolName == null)
            return null;
        return getCommonConfig(schoolName);
    }

    /**
     * 刷新数据源
     */
    public boolean refresh() {
        try {
            if (clientDataSourceRouter == null) {
                logger.warn("无法更新数据源");
                return false;
            }
            SiteDto siteDto = ClientDatabase.getPropertiesFromRedis(datasourceRedisTemplate, clientDatabase.getProjectName());
            List<GvsunDataSource> newDataSourceList = ClientDatabase.getDataSourceList(siteDto);
            List<GvsunDataSource> oldDataSources = clientDatabase.getDataSources();
            Map<Object, Object> targetDataSources = new HashMap<>();
            int defaultDBCount = 0;
            for (GvsunDataSource db : newDataSourceList) {
                if (targetDataSources.containsKey(db.getSchoolName())) {
                    logger.warn("发现重复数据源{}，更新取消", db.getSchoolName());
                    return false;
                }
                targetDataSources.put(db.getSchoolName(), db);
                if (db.isDefaultDataSource()) {
                    defaultDBCount++;
                }
            }
            //获取额外的数据源
            if (customDatasourceSupplier != null) {
                List<CustomDataSource> others = customDatasourceSupplier.supplier();
                for (CustomDataSource c : others) {
                    String identifier = c.getIdentifier();
                    if (!targetDataSources.containsKey(identifier)) {
                        targetDataSources.put(identifier, c);
                    } else {
                        logger.warn("发现重复数据源{}，更新取消", identifier);
                        return false;
                    }
                }
                if (RoutingConfiguration.outDataSources != null) {
                    for (CustomDataSource c : RoutingConfiguration.outDataSources) {
                        c.close();
                    }
                }
                RoutingConfiguration.outDataSources = others;
            }
            if (defaultDBCount > 1) {
                logger.warn("默认数据源配置数量超过1个，请修改Redis配置");
                return false;
            }
            clientDatabase.setDataSources(newDataSourceList);
            clientDataSourceRouter.setTargetDataSources(targetDataSources);
            clientDataSourceRouter.afterPropertiesSet();
            for (GvsunDataSource d : oldDataSources) {
                if (!d.isDefaultDataSource())
                    d.close();
            }
            logger.info("数据源更新成功");
            if (datasourceChangeListener != null) {
                datasourceChangeListener.afterDatasourceRefresh();
            }
        } catch (Throwable e) {
            logger.warn("更新数据源信息出错: {}", e.getMessage());
            return false;
        }
        return true;
    }

    ClientDatabase getClientDatabase() {
        return clientDatabase;
    }
}
