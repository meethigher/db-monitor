package top.meethigher.dbmonitor;

import org.junit.Test;
import top.meethigher.dbmonitor.constant.DBDriver;
import top.meethigher.dbmonitor.model.DBMonitorProperty;
import top.meethigher.dbmonitor.model.sizemonitor.DBSize;
import top.meethigher.dbmonitor.model.structuremonitor.TableStructure;
import top.meethigher.dbmonitor.service.DBSizeMonitor;
import top.meethigher.dbmonitor.service.TableStructureMonitor;
import top.meethigher.dbmonitor.service.impl.DefaultDBSizeMonitor;
import top.meethigher.dbmonitor.service.impl.DefaultTableStructureMonitor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 单元测试
 *
 * @author chenchuancheng
 * @since 2022/6/27 15:40
 */
public class DBMonitorTest {
    private DBMonitorProperty property = new DBMonitorProperty();

    public void createPsqlDefaultInfo() {
        property.setDatabase("e-city-release");
        property.setHost("192.168.110.138");
        property.setPort(5432);
        property.setUsername("postgres");
        property.setPassword("postgres");
        property.setDriver(DBDriver.PSQL.driverClass);
    }

    public void createMysqlDefaultInfo() {
        property.setDatabase("tp_music");
        property.setHost("192.168.110.70");
        property.setPort(3306);
        property.setUsername("root");
        property.setPassword("sldt2018");
        property.setDriver(DBDriver.MYSQL8.driverClass);
    }

    public void createMongoDefaultInfo() {
        property.setDatabase("location");
        property.setHost("192.168.110.70");
        property.setPort(27017);
        property.setDriver(DBDriver.MONGO.driverClass);
    }

    public void createOracleDefaultInfo() {
        property.setDatabase("JRMCDB");
        property.setHost("192.168.110.70");
        property.setPort(1521);
        property.setUsername("C##TEST");
        property.setPassword("test");
        property.setDriver(DBDriver.ORACLE.driverClass);
    }

    /**
     * 获取Connection
     * 官方提供驱动包，遵循jdbc规范的，mongo除外
     *
     * @param dbDriver
     * @param property
     * @return
     */
    private Connection getConnection(DBDriver dbDriver, DBMonitorProperty property) throws SQLException, ClassNotFoundException {
        String jdbcUrl = String.format(dbDriver.jdbcUrl, property.getHost(), property.getPort(), property.getDatabase());
        Class.forName(dbDriver.driverClass);
        return DriverManager.getConnection(jdbcUrl, property.getUsername(), property.getPassword());
    }


    @Test
    public void dbSize() {
        DBSizeMonitor<DBMonitorProperty, DBSize> monitor = new DefaultDBSizeMonitor();
        System.out.println("================ 数据库大小监控 ======================");

//        createMongoDefaultInfo();
//        System.out.printf("mongo:%s\n", monitor.queryDBSize(property));

        createPsqlDefaultInfo();
        System.out.printf("psql:%s\n", monitor.queryDBSize(property));

//        createOracleDefaultInfo();
//        System.out.printf("oracle:%s\n", monitor.queryDBSize(property));
//
//        createMysqlDefaultInfo();
//        System.out.printf("mysql:%s\n", monitor.queryDBSize(property));
    }


    @Test
    public void tableStructure() throws Exception {
        TableStructureMonitor<DBMonitorProperty, TableStructure> monitor = new DefaultTableStructureMonitor();
        System.out.println("================ 表结构监控 ======================");

//        createMongoDefaultInfo();
//        System.out.printf("mongo:%s\n", monitor.queryTableStructure(property, "aaa"));

        createPsqlDefaultInfo();
        System.out.printf("psql:%s\n", monitor.queryTableStructure(property, "bike_auth_info"));

//        createOracleDefaultInfo();
//        System.out.printf("oracle:%s\n", monitor.queryTableStructure(property, "APP_INFO"));

//        createMysqlDefaultInfo();
//        System.out.printf("mysql:%s\n", monitor.queryTableStructure(property, "admin"));
    }
}
