package net.gvsun.datasource.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 条件化创建bean
 *
 * @author 陈敬
 * @since 2.7.8-SNAPSHOT
 */
public class FeignExistCondition implements Condition {
    private static final Logger logger = LoggerFactory.getLogger(FeignExistCondition.class);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            Class.forName("feign.RequestInterceptor");
            return true;
        } catch (ClassNotFoundException e) {
            logger.warn("未发现feign.RequestInterceptor,取消注册");
        }
        return false;
    }
}
