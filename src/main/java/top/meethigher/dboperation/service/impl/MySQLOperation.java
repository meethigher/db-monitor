package top.meethigher.dboperation.service.impl;

import top.meethigher.dboperation.model.DataConnectionInfo;
import top.meethigher.dboperation.model.DatabaseSize;
import top.meethigher.dboperation.service.AbstractDatabaseOperation;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * mysql 操作
 *
 * @author chenchuancheng
 * @since 2023/08/30 14:43
 */
public class MySQLOperation extends AbstractDatabaseOperation {


    private Map<String, Long> getDatabaseSizeMap(Connection connection, String dbName) throws SQLException {
        String sql = "select table_name as name, sum(data_length) as size from information_schema.TABLES" +
                " where table_schema = ? " +
                " and table_type = 'BASE TABLE' group by name order by name";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, dbName);
        Map<String, Long> map = new LinkedHashMap<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String name = rs.getString("name");
            Long size = rs.getLong("size");
            map.put(name, size);
        }
        return map;
    }

    private String getCurrentDB(Connection connection) throws SQLException {
        Statement ps = connection.createStatement();
        ResultSet resultSet = ps.executeQuery("select database() as db_name");
        if (resultSet.next()) {
            return resultSet.getString("db_name");
        } else {
            return null;
        }
    }

    private List<String> getTableNameList(Connection connection) throws SQLException {
        String sql = "select table_name as name from information_schema.TABLES" +
                " where table_schema = ? " +
                " and table_type = 'BASE TABLE' group by name order by name";
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(sql);
        List<String> list = new ArrayList<>();
        while (rs.next()) {
            list.add(rs.getString("name"));
        }
        return list;
    }

    @Override
    public DatabaseSize getDatabaseSize(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        String dbName = getCurrentDB(connection);
        if (dbName == null) {
            throw new SQLException("获取 dbName 失败");
        }
        Map<String, Long> map = getDatabaseSizeMap(connection, dbName);
        return getDatabaseSizeFromMap(dbName, map);
    }

    @Override
    public DatabaseSize getDatabaseSize(DataConnectionInfo info) throws SQLException, ClassNotFoundException {
        try (Connection connection = getConnection(info)) {
            String dbName = getCurrentDB(connection);
            if (dbName == null) {
                throw new SQLException("获取 dbName 失败");
            }
            Map<String, Long> map = getDatabaseSizeMap(connection, dbName);
            return getDatabaseSizeFromMap(dbName, map);
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
