package top.meethigher.dboperation.service;

import top.meethigher.dboperation.model.DataConnectionInfo;
import top.meethigher.dboperation.model.DatabaseSize;
import top.meethigher.dboperation.model.TableStructure;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 针对数据操作，提取出公共的部分
 *
 * @author chenchuancheng
 * @since 2023/08/30 14:59
 */
public abstract class AbstractDatabaseOperation implements DatabaseOperation {

    /**
     * 转换大小
     *
     * @param size 单位为Byte
     * @return 转换单位后的大小
     */
    protected String convertToHumanReadable(Long size) {
        //特殊情况
        if (size == null || size < 0) {
            return "未知";
        }
        if (size < 1024) {
            return String.format("%.2fB", size * 1.0);
        } else if (size < 1024 * 1024) {
            return String.format("%.2fKB", (size * 1.0 / 1024));
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2fMB", (size * 1.0 / (1024 * 1024)));
        } else {
            return String.format("%.2fGB", (size * 1.0 / (1024 * 1024 * 1024)));
        }
    }

    /**
     * 将字符串的大小转换为字节
     *
     * @param size 示例如1KB、1MB、1GB、1B
     * @return 字节
     */
    protected Long convertFromHumanReadable(String size) {
        //特殊情况
        if (size == null) {
            return null;
        }
        long aLong;
        if (size.contains("KB")) {
            aLong = Long.parseLong(size.replace("KB", "").replaceAll(" ", ""));
            return aLong * 1024;
        } else if (size.contains("MB")) {
            aLong = Long.parseLong(size.replace("MB", "").replaceAll(" ", ""));
            return aLong * 1024 * 1024;
        } else if (size.contains("GB")) {
            aLong = Long.parseLong(size.replace("GB", "").replaceAll(" ", ""));
            return aLong * 1024 * 1024 * 1024;
        } else if (size.contains("B")) {
            return Long.valueOf(size.replace("B", "").replaceAll(" ", ""));
        } else {
            return -1L;
        }
    }

    /**
     * 获取Connection
     * 官方提供驱动包，遵循jdbc规范的
     *
     * @param info 连接信息
     * @return 连接
     */
    protected Connection getConnection(DataConnectionInfo info) throws SQLException, ClassNotFoundException {
        Class.forName(info.getDriver());
        return DriverManager.getConnection(info.getJdbcUrl(), info.getUsername(), info.getPassword());
    }

    /**
     * 根据map返回数据封装数据库大小信息
     *
     * @param dbName 数据库名字
     * @param map    key为表名，value为表大小
     * @return {@link DatabaseSize} 数据库大小信息
     */
    protected DatabaseSize getDatabaseSizeFromMap(String dbName, Map<String, Long> map) {
        DatabaseSize databaseSize = new DatabaseSize();
        databaseSize.setDbName(dbName);
        Long dbSize = 0L;
        for (String key : map.keySet()) {
            Long value = map.get(key);
            dbSize += value;
            DatabaseSize.TableSize tableSize = new DatabaseSize.TableSize();
            tableSize.setTableSize(convertToHumanReadable(value));
            tableSize.setTableName(key);
            databaseSize.getTableSizeList().add(tableSize);
        }
        databaseSize.setDbSize(convertToHumanReadable(dbSize));
        return databaseSize;
    }


    @Override
    public TableStructure getTableStructure(String tableName, DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        return getTableStructure(tableName, connection);
    }

    @Override
    public TableStructure getTableStructure(String tableName, DataConnectionInfo info) throws SQLException, ClassNotFoundException {
        try (Connection connection = getConnection(info)) {
            return getTableStructure(tableName, connection);
        }
    }

    private TableStructure getTableStructure(String tableName, Connection connection) throws SQLException {
        TableStructure tableStructure = new TableStructure();
        tableStructure.setTableName(tableName);
        DatabaseMetaData metaData = connection.getMetaData();
        //获取主键并去重存储
        ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
        Set<String> keySet = new HashSet<>();
        while (primaryKeys.next()) {
            keySet.add(primaryKeys.getString("column_name"));
        }
        //获取表结构
        ResultSet rs = metaData.getColumns(null, null, tableName, null);
        while (rs.next()) {
            TableStructure.ColumnInfo columnInfo = new TableStructure.ColumnInfo();
            columnInfo.setColumnName(rs.getString("column_name"));
            columnInfo.setColumnType(rs.getString("type_name"));
            columnInfo.setColumnSize(rs.getString("column_size"));
            columnInfo.setColumnRemark(rs.getString("remarks"));
            columnInfo.setPrimaryKey(keySet.contains(columnInfo.getColumnName()));
            columnInfo.setColumnNullable(DatabaseMetaData.columnNullable == rs.getInt("nullable"));
            tableStructure.getColumnInfos().add(columnInfo);
        }

        return tableStructure;
    }
}
