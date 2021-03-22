package net.gvsun.datasource.http;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import net.gvsun.datasource.ClientDatabase;
import net.gvsun.datasource.ClientDatabaseContextHolder;
import net.gvsun.datasource.DatasourceNotFountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * feign拦截器
 *
 * @author 陈敬
 * @since 2.6.5-SNAPSHOT
 */
public class InterceptorForDataSource2 implements RequestInterceptor {
    private SchoolNameSupplier supplier;
    private final boolean strictMode;
    private static final Logger logger = LoggerFactory.getLogger(InterceptorForDataSource2.class);

    public InterceptorForDataSource2(boolean strictMode) {
        this.strictMode = strictMode;
        try {
            supplier = (SchoolNameSupplier) Class.forName("net.gvsun.datasource.http.SchoolNameSupplierImpl").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            supplier = new SchoolNameSupplier() {
                @Override
                public String schoolName() {
                    return ClientDatabaseContextHolder.getClientDatabaseOri();
                }
            };
        }
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String schoolName = null;
        if (supplier != null)
            schoolName = supplier.schoolName();
        if (!StringUtils.isEmpty(schoolName)) {
            logger.debug("调用feign拦截器获取到数据源: {}", schoolName);
            requestTemplate.header(ClientDatabase.HEADER_KEY_NEW, schoolName);
        } else if (strictMode) {
            throw new DatasourceNotFountException("未指定数据源, feign调用失败");
        }
    }
}
