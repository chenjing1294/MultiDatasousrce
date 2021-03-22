package net.gvsun.datasource.custom;

import java.util.List;

/**
 * 提供你自定义的数据源，这些数据源将被添加到已有的数据源里面，
 * 可供你作额外的切换数据源使用。
 * <p>
 * 这里有一个例子{@link CustomDatasourceSupplierImpl}
 *
 * @author 陈敬
 * @since 1.2.6-SNAPSHOT
 */
public interface CustomDatasourceSupplier {
    /**
     * 提供数据源列表，这些额外的数据源会被添加到已有的数据源列表中，
     * 注意，和已有数据源具有相同的标识的数据源会被忽略
     */
    List<CustomDataSource> supplier();
}
