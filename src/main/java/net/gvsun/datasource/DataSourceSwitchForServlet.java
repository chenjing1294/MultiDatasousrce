package net.gvsun.datasource;

import net.gvsun.datasource.custom.CustomSwitcher;
import org.aspectj.lang.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 数据源动态切换切面
 *
 * @author 陈敬
 * @since 1.1.0-SNAPSHOT
 */
@Aspect
public class DataSourceSwitchForServlet {
    private CustomSwitcher customSwitcher;

    public void setCustomSwitcher(CustomSwitcher customSwitcher) {
        this.customSwitcher = customSwitcher;
    }

    //定义切点（拦截Servlet）
    @Pointcut(value = "execution(* javax.servlet.Servlet.service(javax.servlet.ServletRequest, javax.servlet.ServletResponse))"
            + " && args(req,res)", argNames = "req,res")
    public void performance(ServletRequest req, ServletResponse res) {
    }

    @Before(value = "performance(req, res)", argNames = "req,res")
    public void silenceCellPhones(ServletRequest req, ServletResponse res) {
        ClientDataSourceRouter.switcher(req);
        if (customSwitcher != null)
            customSwitcher.switchover(ClientDatabaseContextHolder.getClientDatabase());
    }

    @After(value = "performance(req, res)", argNames = "req,res")
    public void takeSeats(ServletRequest req, ServletResponse res) {
        ClientDatabaseContextHolder.clear();
        if (customSwitcher != null)
            customSwitcher.clear();
    }

    @AfterThrowing(value = "performance(req, res)", argNames = "req,res,exception", throwing = "exception")
    public void demandRefund(ServletRequest req, ServletResponse res, Throwable exception) {
        ClientDatabaseContextHolder.clear();
        if (customSwitcher != null)
            customSwitcher.clear();


        if (exception.getCause() instanceof DatasourceNotFountException) {
            /**
             * AOP的AfterThrowing处理虽然可以对目标方法的异常进行处理，但这种处理与直接使用catch捕捉不同，
             * catch捕捉意味着完全处理该异常，如果catch块中没有重新抛出新的异常，则该方法可能正常结束；
             * 而AfterThrowing处理虽然处理了该异常，但它不能完全处理异常，该异常依然会传播到上一级调用者
             */
        }
    }
}
