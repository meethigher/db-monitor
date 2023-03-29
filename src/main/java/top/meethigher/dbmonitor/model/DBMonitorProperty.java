package top.meethigher.dbmonitor.model;


/**
 * DB监控-连接DB的基本属性
 *
 * @author chenchuancheng
 * @since 2022/6/24 8:53
 */
public class DBMonitorProperty {

    /**
     * 驱动
     * eg: AbstractDBMonitor.DBDriver.PSQL.driverClass
     *
     * @see top.meethigher.dbmonitor.constant.DBDriver
     */
    private String driver;


    /**
     * jdbcUrl
     */
    private String jdbcUrl;

    /**
     * 数据库用户名
     */
    private String username;

    /**
     * 数据库密码
     */
    private String password;


    public DBMonitorProperty() {
    }

    public DBMonitorProperty(String driver, String uri, String username, String password) {
        this.driver = driver;
        this.jdbcUrl = uri;
        this.username = username;
        this.password = password;
    }

    public DBMonitorProperty(String driver, String jdbcUrl) {
        this.driver = driver;
        this.jdbcUrl = jdbcUrl;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
