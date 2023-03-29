package top.meethigher.dbmonitor.service;


import top.meethigher.dbmonitor.constant.DBDriver;
import top.meethigher.dbmonitor.model.DBMonitorProperty;
import top.meethigher.dbmonitor.model.sizemonitor.DBSize;
import top.meethigher.dbmonitor.model.sizemonitor.TableSize;
import top.meethigher.dbmonitor.model.structuremonitor.ColumnInfo;
import top.meethigher.dbmonitor.model.structuremonitor.TableStructure;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 数据库监控内置实现
 *
 * @author chenchuancheng
 * @since 2022/7/14 9:40
 */
public abstract class AbstractDBMonitor implements DBMonitor<DBSize, TableStructure> {

    /**
     * 用于存储数据库连接源
     */
    private static ConcurrentHashMap<String, DBMonitorProperty> dataSourceMap = new ConcurrentHashMap<>();


    /**
     * 获取数据源Map
     *
     * @return 已存储的数据库连接源
     */
    public Map<String, DBMonitorProperty> getDataSourceMap() {
        return dataSourceMap;
    }


    @Override
    public DBSize queryDBSize(DBMonitorProperty property) {
        String driverClass = property.getDriver();
        DBDriver dbDriver = DBDriver.getByDriverClass(driverClass);
        Map<String, Long> map;
        switch (dbDriver) {
            case SQLSERVER:
                map = sqlserverInfo(property);
                break;
            case ORACLE:
                map = oracleInfo(property);
                break;
            case DB2:
                map = db2Info(property);
                break;
            case MYSQL5:
            case MYSQL8:
                map = mysqlInfo(property);
                break;
            case PSQL:
                map = psqlInfo(property);
                break;
            default:
                map = otherDBInfo(property);
        }
        if (map == null) {
            return null;
        }
        //转换单位，封装数据返回
        List<TableSize> list = new LinkedList<>();
        AtomicReference<Long> allSize = new AtomicReference<>(0L);
        map.forEach((k, v) -> {
            TableSize info = new TableSize();
            info.setTableName(k);
            info.setTableSize(sizeConvert(v));
            try {
                allSize.updateAndGet(v1 -> v1 + v);
            } catch (Exception e) {
                allSize.updateAndGet(v1 -> 0L);
            }
            list.add(info);
        });
        return new DBSize(sizeConvert(allSize.get()), list);
    }

    /**
     * 转换大小
     *
     * @param size 单位为Byte
     * @return 转换单位后的大小
     */
    private String sizeConvert(Long size) {

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
     * @param size 示例如1KB、1GB
     * @return 字节
     */
    private Long sizeConvert(String size) {

        //特殊情况
        if (size == null) {
            return null;
        }
        long aLong = 0L;
        if (size.contains("KB")) {
            aLong = Long.parseLong(size.replaceAll("KB", "").replaceAll(" ", ""));
            return aLong * 1024;
        } else if (size.contains("MB")) {
            aLong = Long.parseLong(size.replaceAll("MB", "").replaceAll(" ", ""));
            return aLong * 1024 * 1024;
        } else if (size.contains("GB")) {
            aLong = Long.parseLong(size.replaceAll("GB", "").replaceAll(" ", ""));
            return aLong * 1024 * 1024 * 1024;
        } else if (size.contains("B")) {
            return Long.valueOf(size.replaceAll("B", "").replaceAll(" ", ""));
        } else {
            return (long) Integer.MAX_VALUE;
        }
    }

    /**
     * 获取Connection
     * 官方提供驱动包，遵循jdbc规范的，mongo除外
     *
     * @param dbDriver 驱动枚举
     * @param property 连接信息
     * @return 连接
     */
    public Connection getConnection(DBDriver dbDriver, DBMonitorProperty property) throws SQLException, ClassNotFoundException {
        String jdbcUrl = property.getJdbcUrl();
        Class.forName(dbDriver.driverClass);
        return DriverManager.getConnection(jdbcUrl, property.getUsername(), property.getPassword());
    }


    /**
     * 默认的psql实现
     *
     * @param property 连接信息
     * @return Map, key为表名, value为大小(单位Byte，包含索引大小+数据大小)
     */
    private Map<String, Long> psqlInfo(DBMonitorProperty property) {
        String sql = "SELECT " + "relname AS name, " + "pg_relation_size(relid) AS size  " + "FROM " + "pg_stat_user_tables  "
                + "WHERE " + "schemaname = 'public'  order by name";
        Connection conn = null;
        try {
            conn = getConnection(DBDriver.PSQL, property);
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);
            Map<String, Long> map = new LinkedHashMap<>();
            while (rs.next()) {
                String name = rs.getString("name");
                Long size = rs.getLong("size");
                map.put(name, size);
            }
            s.close();
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * db2监控
     * db2根据目前掌握的知识，通过sql查询表大小尚未实现，目前只支持查询表结构
     *
     * @param property 连接信息
     * @return db2的表信息
     */
    private Map<String, Long> db2Info(DBMonitorProperty property) {
        Connection conn = null;
        try {
            DBDriver driver = DBDriver.getByDriverClass(property.getDriver());
            conn = getConnection(driver, property);
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, null, new String[]{"TABLE"});
            Map<String, Long> map = new LinkedHashMap<>();
            while (rs.next()) {
                String table_name = rs.getString("table_name");
                map.put(table_name, null);
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 默认的mysql实现，支持mysql
     *
     * @param property 连接信息
     * @return Map, key为表名, value为大小(单位Byte，包含索引大小+数据大小)
     */
    private Map<String, Long> mysqlInfo(DBMonitorProperty property) {
        String sql = "SELECT " + "table_name as name, " + "sum( data_length) AS size " + "FROM " + "information_schema.TABLES "
                + "WHERE " + "table_schema = ? " + "AND table_type = 'BASE TABLE' " + "GROUP BY " + "table_name order by name";
        Connection conn = null;
        try {
            DBDriver driver = DBDriver.getByDriverClass(property.getDriver());
            conn = getConnection(driver, property);
            PreparedStatement ps = conn.prepareStatement(sql);
            //jdbc链接获取库名
            String uri = property.getJdbcUrl();
            String databaseName;
            if (uri.contains("?")) {
                databaseName = uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf("?"));
            } else {
                databaseName = uri.substring(uri.lastIndexOf("/") + 1);
            }
            ps.setString(1, databaseName);
            ResultSet rs = ps.executeQuery();
            Map<String, Long> map = new LinkedHashMap<>();
            while (rs.next()) {
                String name = rs.getString("name");
                Long size = rs.getLong("size");
                map.put(name, size);
            }
            ps.close();
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 默认的sqlserver实现
     *
     * @param property 连接信息
     * @return Map, key为表名, value为大小(单位Byte，包含索引大小+数据大小)
     */
    private Map<String, Long> sqlserverInfo(DBMonitorProperty property) {
        String sql = "select name from sysobjects where xtype='U'";
        Connection conn = null;
        try {
            conn = getConnection(DBDriver.SQLSERVER, property);
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);
            Map<String, Long> map = new LinkedHashMap<>();
            while (rs.next()) {
                String name = rs.getString("name");
                map.put(name, 0L);
            }
            rs.close();
            s.close();
            if (!map.isEmpty()) {
                for (String name : map.keySet()) {
                    try {
                        s = conn.createStatement();
                        rs = s.executeQuery(String.format("exec sp_spaceused '%s'", name));
                        if (rs.next()) {
                            String reserved = rs.getString("reserved");
                            map.put(name, sizeConvert(reserved));
                        }
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                    } finally {
                        rs.close();
                        s.close();
                    }
                }
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 默认的oracle实现
     *
     * @param property 连接信息
     * @return Map, key为表名, value为大小(单位Byte，包含索引大小+数据大小)
     */
    private Map<String, Long> oracleInfo(DBMonitorProperty property) {
        String sql = "select " + "SEGMENT_NAME as name, " + "SUM( BYTES ) as allsize  " + "from " + "user_segments  "
                + "where " + "SEGMENT_TYPE = 'TABLE' and SEGMENT_NAME not like '%$%'  " + "group by SEGMENT_NAME order by name";

        Connection conn = null;
        try {
            conn = getConnection(DBDriver.ORACLE, property);
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);
            Map<String, Long> map = new LinkedHashMap<>();
            while (rs.next()) {
                String name = rs.getString("name");
                Long size = rs.getLong("allsize");
                map.put(name, size);
            }
            s.close();
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 供开发者自己实现指定数据库的入口
     * 可以自己在该方法里再定义一套这种逻辑实现 或 重新实现DBMonitor
     *
     * @param property 连接信息
     * @return Map, key为表名, value为大小(单位Byte，包含索引大小+数据大小)
     */
    public abstract Map<String, Long> otherDBInfo(DBMonitorProperty property);


    /**
     * 内置结构化数据库的表结构查询
     *
     * @param dbDriver  驱动
     * @param property  属性
     * @param tableName 表名
     * @return 表结构
     */
    private TableStructure basicDBStructure(DBDriver dbDriver, DBMonitorProperty property, String tableName) {
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
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 自定义实现
     *
     * @param property 连接信息
     * @param tableName 表名
     * @return 表结构
     */
    public abstract TableStructure otherDBStructure(DBMonitorProperty property, String tableName);

    @Override
    public TableStructure queryTableStructure(DBMonitorProperty property, String tableName) {
        String driverClass = property.getDriver();
        DBDriver dbDriver = DBDriver.getByDriverClass(driverClass);
        switch (dbDriver) {
            case DB2:
            case PSQL:
            case ORACLE:
            case SQLSERVER:
            case MYSQL5:
            case MYSQL8:
                return basicDBStructure(dbDriver, property, tableName);
            default:
                return otherDBStructure(property, tableName);
        }
    }

}
