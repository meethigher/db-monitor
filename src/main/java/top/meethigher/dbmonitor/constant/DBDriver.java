package top.meethigher.dbmonitor.constant;

/**
 * 基本的内置5种驱动
 *
 * @author chenchuancheng
 * @since 2022/6/27 10:24
 */
public enum DBDriver {
    /**
     * oracle
     */
    ORACLE("oracle", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@%s:%d/%s"),

    /**
     * mysql8，新版驱动应该会兼容旧版本数据库
     */
    MYSQL8("mysql8", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://%s:%d/%s"),

    /**
     * sqlserver
     */
    SQLSERVER("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://%s:%d;DatabaseName=%s"),

    /**
     * psql
     */
    PSQL("postgresql", "org.postgresql.Driver", "jdbc:postgresql://%s:%d/%s"),

    /**
     * mongo
     */
    MONGO("mongodb", "mongodb", "mongodb"),

    /**
     * 其他自定义的数据库
     */
    OTHER("other", "other", "other");
    final public String name;
    final public String driverClass;
    final public String jdbcUrl;

    DBDriver(String name, String driverClass, String jdbcUrl) {
        this.name = name;
        this.driverClass = driverClass;
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * 驱动class
     *
     * @param driverClass
     * @return
     */
    public static DBDriver getByDriverClass(String driverClass) {
        if (driverClass == null) {
            throw new IllegalArgumentException("driverClass不可为空");
        }
        for (DBDriver x : DBDriver.values()) {
            if (driverClass.equals(x.driverClass)) {
                return x;
            }
        }
        return DBDriver.OTHER;
    }
}
