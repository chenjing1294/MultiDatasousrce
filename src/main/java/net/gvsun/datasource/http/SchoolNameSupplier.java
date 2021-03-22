package net.gvsun.datasource.http;

/**
 * 以下情况的非多数据源系统需要实现这个接口（实现类必须放在"net.gvsun.datasource.http"包路径下），
 * 多数据源系统调用非多数据源系统，然后这个非数据源系统再调用多数据源系统
 * 比如，实验室（多） -> 资源容器（非多） -> oauth2（多），那么资源容器需要实现这个
 * 接口，并提供由实验室传递过来的数据源信息:
 * package net.gvsun.datasource.http
 * public class SchoolNameSupplierImpl implements SchoolNameSupplier {
 * public String schoolName() {
 * //返回学校名
 * }
 * }
 * <p>
 * 另一种情况：实验室（非多） -> 资源容器（非多） -> oauth2（多），
 * 那么实验室和资源容器应该这样实现这个接口：
 * package net.gvsun.datasource.http
 * public class SchoolNameSupplierImpl implements SchoolNameSupplier {
 * public String schoolName() {
 * return null;
 * }
 * }
 *
 * @author 陈敬
 * @since 1.2.0-SNAPSHOT
 */
public interface SchoolNameSupplier {
    /**
     * 如果是所数据源系统需要覆盖这个方法
     *
     * @return 返回学校名
     */
    String schoolName();
}
