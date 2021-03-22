package net.gvsun.datasource;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RoutingConfiguration.class})
@ServletComponentScan(basePackages = "net.gvsun.datasource")
public @interface EnableMultiDatasource {
}
