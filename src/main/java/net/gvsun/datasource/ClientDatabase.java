package net.gvsun.datasource;

import net.gvsun.datasource.dto.GvsunDataSourceDto;
import net.gvsun.datasource.dto.SiteDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyVetoException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据源基础配置，从Redis读取数据源配置信息
 *
 * @author 陈敬
 * @since 1.1.0-SNAPSHOT
 */
public class ClientDatabase {
    public static String KEY = "platform-oauth2-datasource";
    public static String HEADER_KEY = "header.datasource";      //不推荐，有时传不过去；使用HEADER_KEY_NEW替代
    public static String HEADER_KEY_NEW = "x-datasource";
    public static String COOKIE_KEY = "datasource.cookie";
    public static String QUERY_KEY = "query_datasource";
    private String projectName;
    private List<GvsunDataSource> dataSources;
    private static Logger logger = LoggerFactory.getLogger(ClientDatabase.class);

    public ClientDatabase(List<GvsunDataSource> dataSources, String projectName) {
        this.dataSources = dataSources;
        this.projectName = projectName;
    }

    public static SiteDto getPropertiesFromInputStream(Reader in) {
        //把属性文件转换为SiteDto
        throw new RuntimeException("不支持从输入流读取配置");
    }

    public static SiteDto getPropertiesFromRedis(RedisTemplate<String, SiteDto> redisTemplate, String projectName) {
        SiteDto siteDto = (SiteDto) redisTemplate.opsForHash().get(KEY, projectName);
        if (siteDto == null) {
            throw new RuntimeException(String.format("未在Redis里查询到数据库配置(%s:%s)", KEY, projectName));
        }

        //检查是否配责任人添加日期
        for (GvsunDataSourceDto d : siteDto.getDataSourceDtos()) {
            if (StringUtils.isEmpty(d.getPersonInCharge()) || StringUtils.isEmpty(d.getDate())) {
                throw new RuntimeException(String.format("数据源%s的负责人和添加日期禁止留空", d.getSchoolName()));
            }
        }
        return siteDto;
    }

    public static List<GvsunDataSource> getDataSourceList(SiteDto siteDto) throws PropertyVetoException {
        List<GvsunDataSource> dataSourceList = new ArrayList<>();
        for (GvsunDataSourceDto gd : siteDto.getDataSourceDtos()) {
            boolean defaultDataSource = gd.isDefaultDataSource();
            String url = gd.getUrl();
            String username = gd.getUsername();
            String password = gd.getPassword();
            String schoolCname = gd.getSchoolCname();
            String schoolName = gd.getSchoolName();
            GvsunDataSource dataSource = new GvsunDataSource();
            if (url.startsWith("jdbc:sqlserver://")) {
                logger.info("检测到sqlserver连接:{}", url);
                dataSource.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } else
                dataSource.setDriverClass("com.mysql.cj.jdbc.Driver");
            dataSource.setJdbcUrl(url);
            dataSource.setUser(username);
            dataSource.setPassword(password);
            //初始连接数
            dataSource.setInitialPoolSize(3);
            //最大空闲时间,60秒内未使用则连接被丢弃。若为0则永不丢弃
            dataSource.setMaxIdleTime(60);
            //每20秒检查所有连接池中的空闲连接
            dataSource.setIdleConnectionTestPeriod(20);
            //连接池中保留的最大连接数
            dataSource.setMaxPoolSize(30);
            //连接池中保留的最小连接数
            dataSource.setMinPoolSize(3);

            dataSource.setDefaultDataSource(defaultDataSource);
            dataSource.setSchoolName(schoolName);
            dataSource.setSchoolCname(schoolCname);
            dataSource.setPersonInCharge(gd.getPersonInCharge());
            dataSource.setDate(gd.getDate());
            dataSourceList.add(dataSource);
        }
        return dataSourceList;
    }

    /**
     * 获取要切换的学校，两种方式
     * 1. 前端调用后端接口，学校名放在cookie里
     * 2. 后端调用后端接口，学校名放在header里或查询参数里
     */
    public static void setTargetSchoolName(ServletRequest req, boolean subDatasource) {
        HttpServletRequest request = (HttpServletRequest) req;
        String schoolName = request.getHeader(HEADER_KEY);
        if (StringUtils.isEmpty(schoolName)) {
            schoolName = request.getHeader(HEADER_KEY_NEW);
        }

        if (StringUtils.isEmpty(schoolName)) {
            schoolName = request.getParameter(QUERY_KEY);
        }

        if (StringUtils.isEmpty(schoolName)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals(COOKIE_KEY)) {
                        schoolName = c.getValue();
                        break;
                    }
                }
            }
        }
        logger.info("从请求中获取到的数据源: sunDatasource={}, schoolName={}", subDatasource, schoolName);
        ClientDatabaseContextHolder.setOri(schoolName);
        ClientDatabaseContextHolder.set(schoolName);
    }

    public List<GvsunDataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<GvsunDataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public String getProjectName() {
        return projectName;
    }
}
