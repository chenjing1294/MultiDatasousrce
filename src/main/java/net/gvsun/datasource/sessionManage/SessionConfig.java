//package net.gvsun.datasource.sessionManage;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//
//
//public class SessionConfig {
//    @Bean
//    public FilterRegistrationBean testFilterRegistration(@Value("${datasource.enableSingleSignOut:false}") boolean enabled) {
//        FilterRegistrationBean registration = new FilterRegistrationBean(new SessionFilter());
//        registration.setEnabled(enabled);
//        registration.addUrlPatterns("/*");
//        registration.setOrder(Integer.MAX_VALUE);
//        return registration;
//    }
//
//    @Bean("datasource.sessionStorage")
//    public SessionStorage sessionStorage() {
//        return new SessionStorage();
//    }
//}
