package top.meethigher.dbmonitor.constant;

/**
 * 基本的内置5种驱动
 *
 * @author chenchuancheng
 * @since 2022/6/27 10:24
 */
public enum DBDriver {
    /**
     * oracle-测试环境为11g与19c
     */
    ORACLE("oracle", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@%s:%d/%s"),

    /**
     * mysql8，新版驱动会兼容旧版本数据库
     */
    MYSQL8("mysql8", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://%s:%d/%s"),

    /**
     * mysql5
     */
    MYSQL5("mysql5", "com.mysql.jdbc.Driver", "jdbc:mysql://%s:%d/%s"),

    /**
     * sqlserver-测试环境为2019
     */
    SQLSERVER("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://%s:%d;DatabaseName=%s"),

    /**
     * db2
     */
    DB2("db2", "com.ibm.db2.jcc.DB2Driver", "jdbc:db2://%s:%d/%s"),

    /**
     * psql-测试环境为psql11
     */
    PSQL("postgresql", "org.postgresql.Driver", "jdbc:postgresql://%s:%d/%s");

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
     * @param driverClass 驱动名称
     * @return 驱动枚举
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
        return DBDriver.PSQL;
    }
}
