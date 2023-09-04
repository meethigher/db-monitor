package top.meethigher.dboperation.model;

import java.io.Serializable;

/**
 * 数据库连接信息
 *
 * @author chenchuancheng
 * @since 2023/08/30 14:36
 */
public class DataConnectionInfo implements Serializable {

    /**
     * 驱动
     */
    private String driver;
    /**
     * jdbc url
     */
    private String jdbcUrl;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;


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
