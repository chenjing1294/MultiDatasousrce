package net.gvsun.datasource;

/**
 * 每个线程一个学校名
 *
 * @author 陈敬
 * @since 1.1.0-SNAPSHOT
 */
public class ClientDatabaseContextHolder {
    private static ThreadLocal<String> CONTEXT = new ThreadLocal<>();
    private static ThreadLocal<String> CONTEXT2 = new ThreadLocal<>();
    public static String DATASOURCE_SEPARATOR = "-";
    private static ClientDatabaseContext clientDatabaseContext;
    private static boolean subDatasource;

    static void setClientDatabaseContext(ClientDatabaseContext clientDatabaseContext, boolean subDatasource) {
        ClientDatabaseContextHolder.clientDatabaseContext = clientDatabaseContext;
        ClientDatabaseContextHolder.subDatasource = subDatasource;
    }

    public static void set(String schoolName) {
        if (!subDatasource && schoolName != null && schoolName.contains(DATASOURCE_SEPARATOR)) {
            CONTEXT.set(schoolName.substring(0, schoolName.indexOf(DATASOURCE_SEPARATOR)));
        } else {
            CONTEXT.set(schoolName);
        }

        if (CONTEXT.get() != null) {
            if (CONTEXT.get().contains(DATASOURCE_SEPARATOR))
                CONTEXT2.set(CONTEXT.get());
            else if (CONTEXT2.get() != null) {
                if (CONTEXT2.get().contains(DATASOURCE_SEPARATOR))
                    CONTEXT2.set(CONTEXT.get() + CONTEXT2.get().substring(CONTEXT2.get().indexOf(DATASOURCE_SEPARATOR)));
                else
                    CONTEXT2.set(CONTEXT.get());
            } else
                CONTEXT2.set(CONTEXT.get());
        }
    }

    public static void setOri(String schoolName) {
        CONTEXT2.set(schoolName);
    }

    /**
     * 获取当前正在使用的数据源，找不到则返回null
     */
    public static String getClientDatabase() {
        return CONTEXT.get();
    }

    public static String getClientDatabaseOri() {
        return CONTEXT2.get();
    }

    public static void clear() {
        CONTEXT.remove();
        CONTEXT2.remove();
    }
}
