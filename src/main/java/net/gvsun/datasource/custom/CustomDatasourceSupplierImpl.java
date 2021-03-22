package net.gvsun.datasource.custom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.gvsun.datasource.ClientDatabase;
import net.gvsun.datasource.dto.GvsunDataSourceDto;
import net.gvsun.datasource.dto.SiteDto;
import org.springframework.data.redis.core.RedisTemplate;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 这是CustomDatasourceSupplier的一个模拟实现，它不具备实际的功能。
 * 例如，在datashare项目中，除了需要切换datashare自己的数据源之外，还需要切换到同一个学校下的
 * 实验室、大仪、教学等数据源，你可以实现CustomDatasourceSupplier并提供这些系统在各个学校的数据源，然后根据业务
 * 需要进行切换。
 */
public class CustomDatasourceSupplierImpl implements CustomDatasourceSupplier {
    private RedisTemplate<String, Object> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<CustomDataSource> supplier() {
        //从Redis里获取你需要的数据源
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(ClientDatabase.KEY);
        ObjectMapper objectMapper = new ObjectMapper();
        //待填充的数据源数组
        List<CustomDataSource> dataSourceList = new ArrayList<>();
        entries.forEach((k, v) -> {
            try {
                SiteDto siteDto = objectMapper.readValue(objectMapper.writeValueAsString(v), new TypeReference<SiteDto>() {
                });

                List<GvsunDataSourceDto> dataSourceDtos = siteDto.getDataSourceDtos();

                for (GvsunDataSourceDto gd : dataSourceDtos) {
                    //根据需要填充你的多数据源数组
                    String url = null;
                    String username = null;
                    String password = null;
                    String identifier = "{schoolName}.{projectName}";//你的数据源标识符，在你切换时会用到


                    CustomDataSource dataSource = new CustomDataSource();
                    dataSource.setDriverClass("com.mysql.jdbc.Driver");
                    dataSource.setJdbcUrl(url);
                    dataSource.setUser(username);
                    dataSource.setPassword(password);
                    dataSource.setIdentifier(identifier);
                    dataSourceList.add(dataSource);
                }
            } catch (IOException | PropertyVetoException e) {
                e.printStackTrace();
            }
        });
        return dataSourceList;
    }
}
