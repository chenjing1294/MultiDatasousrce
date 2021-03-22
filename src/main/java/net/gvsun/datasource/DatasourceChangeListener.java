package net.gvsun.datasource;

/**
 * 通知数据源发生变换
 *
 * @author 陈敬
 * @since 2.7.8-SNAPSHOT
 */
public interface DatasourceChangeListener {
    /**
     * 数据源刷新后调用该方法
     */
    void afterDatasourceRefresh();
}
