package net.gvsun.datasource;

import net.gvsun.datasource.custom.CustomDataSource;
import net.gvsun.datasource.custom.CustomDatasourceSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.servlet.ServletRequest;
import java.util.List;

/**
 * 数据源选择机制
 *
 * @author 陈敬
 * @since 1.1.0-SNAPSHOT
 */
public class ClientDataSourceRouter extends AbstractRoutingDataSource {
    public static boolean startSuccess = false;
    public static boolean strictMode;
    public static boolean subDatasource;
    private static final Logger logger = LoggerFactory.getLogger(ClientDataSourceRouter.class);
    private static ClientDatabaseContext clientDatabaseContext;
    private static CustomDatasourceSupplier customDatasourceSupplier;

    public ClientDataSourceRouter(ClientDatabaseContext clientDatabaseContext, boolean strictMode, boolean subDatasource) {
        ClientDataSourceRouter.clientDatabaseContext = clientDatabaseContext;
        this.strictMode = strictMode;
        this.subDatasource = subDatasource;
    }

    public static void switcher(ServletRequest request) {
        ClientDatabase.setTargetSchoolName(request, subDatasource);
    }

    public void setCustomDatasourceSupplier(CustomDatasourceSupplier customDatasourceSupplier) {
        this.customDatasourceSupplier = customDatasourceSupplier;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String schoolName = ClientDatabaseContextHolder.getClientDatabase();
        if (schoolName != null) {
            if (datasourceExist(schoolName)) {
                logger.info("正在切换数据源: {}", schoolName);
            } else {
                if (strictMode) {
                    throw new DatasourceNotFountException(String.format("指定的数据源{%s}不存在, 拒绝访问数据库", schoolName));
                } else {
                    logger.warn("指定的数据源{}不存在, 使用默认数据源", schoolName);
                }
            }
        } else {
            if (startSuccess) {
                if (strictMode) {
                    throw new DatasourceNotFountException("未指定数据源, 拒绝访问数据库");
                } else {
                    logger.warn("未指定数据源, 使用默认数据源");
                }
            }
        }
        return schoolName;
    }

    public boolean datasourceExist(String schoolName) {
        boolean exist = false;
        List<GvsunDataSource> dataSources = clientDatabaseContext.getClientDatabase().getDataSources();
        for (GvsunDataSource d : dataSources) {
            if (d.getSchoolName().equals(schoolName)) {
                exist = true;
                break;
            }
        }

        if (!exist && customDatasourceSupplier != null) {
            List<CustomDataSource> supplier = customDatasourceSupplier.supplier();
            for (CustomDataSource c : supplier) {
                if (c.getIdentifier().equals(schoolName)) {
                    exist = true;
                    break;
                }
            }
        }
        return exist;
    }
}

