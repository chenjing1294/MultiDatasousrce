package net.gvsun.datasource.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class DatasourceServletContainerInitializer implements ServletContainerInitializer {
    private Logger logger = LoggerFactory.getLogger(DatasourceServletContainerInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        logger.info("注册数据源监听器：net.gvsun.datasource.SwitchListener");
        ctx.addListener("net.gvsun.datasource.SwitchListener");
    }
}
