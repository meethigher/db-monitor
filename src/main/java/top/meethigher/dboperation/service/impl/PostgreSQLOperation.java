package top.meethigher.dboperation.service.impl;

import top.meethigher.dboperation.model.DataConnectionInfo;
import top.meethigher.dboperation.model.DatabaseSize;
import top.meethigher.dboperation.model.TableStructure;
import top.meethigher.dboperation.service.AbstractDatabaseOperation;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * postgresql 操作
 *
 * @author chenchuancheng
 * @since 2023/08/30 14:46
 */
public class PostgreSQLOperation extends AbstractDatabaseOperation {

    private final String delimiter = ".";

    /**
     * 得到数据库大小map
     *
     * @param connection 连接
     * @return {@link Map}<{@link String}, {@link Long}> key为视图.表名，value为字节大小。
     * @throws SQLException sqlexception异常
     */
    private Map<String, Long> getDatabaseSizeMap(Connection connection) throws SQLException {
        Map<String, Long> map = new LinkedHashMap<>();
        String sql = "select relname as name, pg_relation_size(relid) as size,schemaname from pg_stat_user_tables order by name";
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            String name = rs.getString("name");
            long value = rs.getLong("size");
            String schemaname = rs.getString("schemaname");
            map.put(schemaname.concat(delimiter).concat(name), value);
        }
        return map;
    }

    /**
     * 得到当前数据库名
     *
     * @param connection 连接
     * @return {@link String}
     * @throws SQLException sqlexception异常
     */
    private String getCurrentDB(Connection connection) throws SQLException {
        Statement ps = connection.createStatement();
        ResultSet resultSet = ps.executeQuery("select current_database() as db_name");
        if (resultSet.next()) {
            return resultSet.getString("db_name");
        } else {
            return null;
        }
    }

    /**
     * 得到表名
     *
     * @param tableName 格式可以为 视图.表名 或者 表名
     * @return {@link String}
     */
    private String getTableName(String tableName) {
        if (tableName.contains(delimiter)) {
            return tableName.split("\\" + delimiter)[1];
        } else {
            return tableName;
        }
    }

    /**
     * 得到表模式
     *
     * @param tableName 格式可以为 视图.表名 或者 表名
     * @return {@link String}
     */
    private String getTableSchema(String tableName) {
        if (tableName.contains(delimiter)) {
            return tableName.split("\\" + delimiter)[0];
        } else {
            return null;
        }
    }

    /**
     * 得到表结构
     *
     * @param tableName  格式可以为 视图.表名 或者 表名
     * @param connection 连接信息
     * @return {@link TableStructure}
     * @throws SQLException sqlexception异常
     */
    private TableStructure getTableStructure(String tableName, Connection connection) throws SQLException {
        String tableSchema = getTableSchema(tableName);
        String shortTableName = getTableName(tableName);
        TableStructure tableStructure = new TableStructure();
        tableStructure.setTableName(tableName);
        DatabaseMetaData metaData = connection.getMetaData();
        //获取主键并去重存储
        ResultSet primaryKeys = metaData.getPrimaryKeys(null, tableSchema, shortTableName);
        Set<String> keySet = new HashSet<>();
        while (primaryKeys.next()) {
            keySet.add(primaryKeys.getString("column_name"));
        }
        //获取表结构
        ResultSet rs = metaData.getColumns(null, tableSchema, shortTableName, null);
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

    /**
     * 得到表列表
     *
     * @param connection 连接
     * @return {@link List}<{@link String}>
     * @throws SQLException sqlexception异常
     */
    private List<String> getTableNameList(Connection connection) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery("select relname as name, schemaname from pg_stat_user_tables order by name");
        List<String> list = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("name");
            String schemaname = rs.getString("schemaname");
            list.add(schemaname.concat(delimiter).concat(name));
        }
        return list;
    }

    @Override
    public DatabaseSize getDatabaseSize(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        Map<String, Long> databaseSizeMap = getDatabaseSizeMap(connection);
        String currentDB = getCurrentDB(connection);
        return getDatabaseSizeFromMap(currentDB, databaseSizeMap);
    }

    @Override
    public DatabaseSize getDatabaseSize(DataConnectionInfo info) throws SQLException, ClassNotFoundException {
        try (Connection connection = getConnection(info)) {
            Map<String, Long> databaseSizeMap = getDatabaseSizeMap(connection);
            String currentDB = getCurrentDB(connection);
            return getDatabaseSizeFromMap(currentDB, databaseSizeMap);
        }
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

    @Override
    public List<String> getTableNameList(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        return getTableNameList(connection);
    }


    @Override
    public List<String> getTableNameList(DataConnectionInfo info) throws SQLException, ClassNotFoundException {
        try (Connection connection = getConnection(info)) {
            return getTableNameList(connection);
        }
    }
}
