package top.meethigher.dbmonitor;

import org.junit.Test;
import top.meethigher.dbmonitor.constant.DBDriver;
import top.meethigher.dbmonitor.model.DBMonitorProperty;
import top.meethigher.dbmonitor.model.sizemonitor.DBSize;
import top.meethigher.dbmonitor.model.structuremonitor.TableStructure;
import top.meethigher.dbmonitor.service.DBMonitor;
import top.meethigher.dbmonitor.service.DefaultDBMonitor;
import top.meethigher.dboperation.model.DataConnectionInfo;
import top.meethigher.dboperation.model.DatabaseSize;
import top.meethigher.dboperation.service.DatabaseOperation;
import top.meethigher.dboperation.service.impl.PostgreSQLOperation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * 单元测试
 *
 * @author chenchuancheng
 * @since 2022/6/27 15:40
 */
public class DBMonitorTest {
    private DBMonitorProperty property = new DBMonitorProperty();

    public void createPsqlDefaultInfo() {
        property.setJdbcUrl("jdbc:postgresql://10.0.0.10:5432/data_server");
        property.setUsername("postgres");
        property.setPassword("postgres");
        property.setDriver(DBDriver.PSQL.driverClass);
    }

    public void createMysqlDefaultInfo() {
        property.setJdbcUrl("jdbc:mysql://10.0.0.10:3306/mysql");
        property.setUsername("root");
        property.setPassword("meethigher");
        property.setDriver(DBDriver.MYSQL8.driverClass);
    }

    public void createOracleDefaultInfo() {
        property.setJdbcUrl("jdbc:oracle:thin:@10.0.0.10:1521/JRMCDB");
        property.setUsername("C##TEST");
        property.setPassword("test");
        property.setDriver(DBDriver.ORACLE.driverClass);
    }

    public void createSqlserverDefaultInfo() {
        property.setJdbcUrl("jdbc:sqlserver://10.0.0.10:1433;DatabaseName=ctest");
        property.setUsername("sa");
        property.setDriver(DBDriver.SQLSERVER.driverClass);
    }

    public void createDB2DefaultInfo() {
        property.setJdbcUrl("jdbc:db2://10.0.0.10:50000/db2test");
        property.setUsername("db2inst1");
        property.setDriver(DBDriver.DB2.driverClass);
    }


    @Test
    public void name() {
        DBMonitor<DBSize, TableStructure> monitor = new DefaultDBMonitor();
        createMysqlDefaultInfo();
        DBSize dbSize = monitor.queryDBSize(property);
        System.out.println();
    }
}
