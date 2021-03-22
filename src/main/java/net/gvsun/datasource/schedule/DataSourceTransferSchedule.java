package net.gvsun.datasource.schedule;

import net.gvsun.datasource.ClientDataSourceRouter;
import net.gvsun.datasource.ClientDatabase;
import net.gvsun.datasource.custom.CustomDatasourceSupplier;
import net.gvsun.datasource.dto.SiteDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 动态更新数据源
 *
 * @author 陈敬
 * @since 1.1.0-SNAPSHOT
 */
public class DataSourceTransferSchedule {
    private long initialDelay;
    private long period;
    private TimeUnit timeUnit;
    private ClientDatabase clientDatabase;
    private RedisTemplate<String, SiteDto> redisTemplate;
    private ClientDataSourceRouter clientDataSourceRouter;
    private CustomDatasourceSupplier customDatasourceSupplier;
    private Logger logger = LoggerFactory.getLogger(DataSourceTransferSchedule.class);

    public DataSourceTransferSchedule(ClientDatabase clientDatabase,
                                      RedisTemplate<String, SiteDto> redisTemplate,
                                      ClientDataSourceRouter dataSource,
                                      CustomDatasourceSupplier customDatasourceSupplier) {
        this.customDatasourceSupplier = customDatasourceSupplier;
        this.clientDatabase = clientDatabase;
        this.redisTemplate = redisTemplate;
        initialDelay = 30;//延迟，等待服务启动
        period = 30;//每隔几分钟刷新一个数据源信息
        timeUnit = TimeUnit.MINUTES;
        this.clientDataSourceRouter = dataSource;
    }

    public DataSourceTransferSchedule setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public void go() {
        logger.warn("请调用ClientDatabaseContext.refresh()更新源");
//        logger.info("数据源更新计划:每{}{}更新一次", period, timeUnit);
//        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
//        executorService.scheduleAtFixedRate(new Task(), initialDelay, period, timeUnit);
    }

    public DataSourceTransferSchedule setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
        return this;
    }

    public DataSourceTransferSchedule setPeriod(long period) {
        this.period = period;
        return this;
    }
}
