//package net.gvsun.datasource.sessionManage;
//
//import net.gvsun.datasource.ClientDatabaseContextHolder;
//import org.springframework.context.ApplicationContext;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.util.StringUtils;
//import org.springframework.web.context.support.WebApplicationContextUtils;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//import java.security.Principal;
//
//public class SessionFilter implements Filter {
//    private SessionStorage sessionStorage = null;
//    private RedisTemplate<String, Object> redisTemplate = null;
//
//    @Override
//    public void init(FilterConfig filterConfig) {
//        ServletContext servletContext = filterConfig.getServletContext();
//        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
//        this.sessionStorage = ctx.getBean("datasource.sessionStorage", SessionStorage.class);
//        this.redisTemplate = ctx.getBean("datasourceRedisTemplate", RedisTemplate.class);
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        HttpSession session = httpServletRequest.getSession(false);
//        if (session != null) {
//            Principal userPrincipal = httpServletRequest.getUserPrincipal();
//            if (userPrincipal != null && userPrincipal.getName() != null) {
//                String schoolName = ClientDatabaseContextHolder.getClientDatabase();
//                if (StringUtils.isEmpty(schoolName)) {
//                    schoolName = "limsproduct";
//                }
//                String username = userPrincipal.getName();
//                String key = schoolName + "." + username;
//                if (!redisTemplate.opsForSet().isMember(SessionStorage.SESSION_KEY, key)) {
//                    session.invalidate();
//                    String requestURI = httpServletRequest.getRequestURI();
//                    httpServletResponse.sendRedirect(requestURI);
//                    return;
//                }
//            }
//        }
//        chain.doFilter(request, response);
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}
