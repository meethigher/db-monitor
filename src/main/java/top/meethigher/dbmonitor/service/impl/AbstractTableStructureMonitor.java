package top.meethigher.dbmonitor.service.impl;

import top.meethigher.dbmonitor.constant.DBDriver;
import top.meethigher.dbmonitor.model.DBMonitorProperty;
import top.meethigher.dbmonitor.model.structuremonitor.ColumnInfo;
import top.meethigher.dbmonitor.model.structuremonitor.TableStructure;
import top.meethigher.dbmonitor.service.TableStructureMonitor;

import java.sql.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 提供内置数据库的基础实现的抽象类
 *
 * @author chenchuancheng
 * @since 2022/6/27 10:22
 */
public abstract class AbstractTableStructureMonitor implements TableStructureMonitor<DBMonitorProperty, TableStructure> {

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


    /**
     * 内置结构化数据库的表结构查询
     *
     * @param dbDriver  驱动
     * @param property  属性
     * @param tableName 表名
     * @return
     */
    public TableStructure basicDBStructure(DBDriver dbDriver, DBMonitorProperty property, String tableName) {
        Connection conn = null;
        try {
            TableStructure structure = new TableStructure();
            structure.setTableName(tableName);
            List<ColumnInfo> list = new LinkedList<>();
            conn = getConnection(dbDriver, property);
            DatabaseMetaData dbMetaData = conn.getMetaData();
            //获取主键
            ResultSet keys = dbMetaData.getPrimaryKeys(null, null, tableName);
            Set<String> keySet = new HashSet<>();
            while (keys.next()) {
                keySet.add(keys.getString("column_name"));
            }
            //获取表结构
            ResultSet rs = dbMetaData.getColumns(null, null, tableName, null);
            while (rs.next()) {
                ColumnInfo info = new ColumnInfo();
                info.setColumnName(rs.getString("column_name"));
                info.setColumnType(rs.getString("type_name"));
                info.setColumnSize(rs.getString("column_size"));
                info.setColumnRemark(rs.getString("remarks"));
                info.setPrimaryKey(keySet.contains(info.getColumnName()));
                info.setColumnNullable(DatabaseMetaData.columnNullable == rs.getInt("nullable"));
                list.add(info);
            }
            structure.setColumnInfos(list);
            return structure;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 自定义实现
     *
     * @param property
     * @param tableName
     * @return
     */
    public abstract TableStructure otherDBStructure(DBMonitorProperty property, String tableName);

    @Override
    public TableStructure queryTableStructure(DBMonitorProperty property, String tableName) {
        String driverClass = property.getDriver();
        DBDriver dbDriver = DBDriver.getByDriverClass(driverClass);
        switch (dbDriver) {
            case PSQL:
            case ORACLE:
            case SQLSERVER:
            case MYSQL8:
                return basicDBStructure(dbDriver, property, tableName);
            default:
                return otherDBStructure(property, tableName);
        }
    }
}
