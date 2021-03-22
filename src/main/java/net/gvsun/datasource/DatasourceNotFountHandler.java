package net.gvsun.datasource;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * 所有Controller层的通知器，遇到DatasourceNotFountException异常，返回页面
 *
 * @author 陈敬
 * @since 2.5.3-SNAPSHOT
 */
@ControllerAdvice
public class DatasourceNotFountHandler {
    @ExceptionHandler(DatasourceNotFountException.class)
    public void handleDuplicateSpittle(HttpServletResponse res, HttpServletResponse response) throws IOException {
        InputStream in = this.getClass().getResourceAsStream("/err.html");
        byte[] bytes = new byte[1024];
        int length = 0;
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        while ((length = in.read(bytes)) != -1) {
            out.write(bytes, 0, length);
        }
        out.flush();
    }
}
