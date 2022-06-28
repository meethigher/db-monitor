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
     * 数据库地址
     */
    private String host;

    /**
     * 数据库端口
     */
    private Integer port;

    /**
     * 数据库名称
     */
    private String database;

    /**
     * 数据库用户名
     */
    private String username;

    /**
     * 数据库密码
     */
    private String password;

    public DBMonitorProperty(String driver, String host, Integer port, String database, String username, String password) {
        this.driver = driver;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public DBMonitorProperty() {
    }


    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
