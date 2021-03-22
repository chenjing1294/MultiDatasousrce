package net.gvsun.datasource.http;

import net.gvsun.datasource.ClientDatabase;
import net.gvsun.datasource.ClientDatabaseContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * RestTemplate拦截器
 *
 * @author 陈敬
 * @since 1.2.0-SNAPSHOT
 */
public class InterceptorForDataSource implements ClientHttpRequestInterceptor {
    private SchoolNameSupplier supplier = null;

    public InterceptorForDataSource() {
        try {
            supplier = (SchoolNameSupplier) Class.forName("net.gvsun.datasource.http.SchoolNameSupplierImpl").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            supplier = new SchoolNameSupplier() {
                @Override
                public String schoolName() {
                    return ClientDatabaseContextHolder.getClientDatabase();
                }
            };
        }
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String schoolName = null;
        HttpHeaders headers = httpRequest.getHeaders();
        if (supplier != null)
            schoolName = supplier.schoolName();
        if (!StringUtils.isEmpty(schoolName)) {
            headers.add(ClientDatabase.HEADER_KEY, schoolName);
            headers.add(ClientDatabase.HEADER_KEY_NEW, schoolName);
        }
        return execution.execute(httpRequest, body);
    }
}
