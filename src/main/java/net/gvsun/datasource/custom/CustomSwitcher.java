package net.gvsun.datasource.custom;

/**
 * 额外的切换
 *
 * @author 陈敬
 * @since 1.3.0-SNAPSHOT
 */
public interface CustomSwitcher {
    /**
     * 根据当前所属的数据源切换
     *
     * @param schoolName 学校名（有可能为null）
     */
    void switchover(String schoolName);

    default void clear() {
        //do nothing
    }
}
