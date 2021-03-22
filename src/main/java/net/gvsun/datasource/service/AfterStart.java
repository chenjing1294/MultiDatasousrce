package net.gvsun.datasource.service;

import net.gvsun.datasource.ClientDataSourceRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

/**
 * Spring启动成功后的回调
 */
public class AfterStart implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(AfterStart.class);

    @Override
    public void run(String... args) {
        ClientDataSourceRouter.startSuccess = true;
        if (ClientDataSourceRouter.strictMode)
            logger.warn("后续的数据库访问进入严格模式(必须明确指定数据源),单一源的系统可以不用开启此配置(datasource.strictMode:false)");
    }
}
