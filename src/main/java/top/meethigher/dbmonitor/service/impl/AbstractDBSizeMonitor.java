package top.meethigher.dbmonitor.service.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import top.meethigher.dbmonitor.constant.DBDriver;
import top.meethigher.dbmonitor.model.DBMonitorProperty;
import top.meethigher.dbmonitor.model.sizemonitor.DBSize;
import top.meethigher.dbmonitor.model.sizemonitor.TableSize;
import top.meethigher.dbmonitor.service.DBSizeMonitor;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 查询数据库下所有表名及相应所占存储空间的大小
 *
 * @author chenchuancheng
 * @since 2022/6/24 9:12
 */
public abstract class AbstractDBSizeMonitor implements DBSizeMonitor<DBMonitorProperty, DBSize> {


    @Override
    public DBSize queryDBSize(DBMonitorProperty property) {
        String driverClass = property.getDriver();
        DBDriver dbDriver = DBDriver.getByDriverClass(driverClass);
        Map<String, Long> map;
        switch (dbDriver) {
            case PSQL:
                map = psqlInfo(property);
                break;
            case SQLSERVER:
                map = sqlserverInfo(property);
                break;
            case ORACLE:
                map = oracleInfo(property);
                break;
            case MYSQL8:
                map = mysqlInfo(property);
                break;
            case MONGO:
                map = mongoInfo(property);
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
            allSize.updateAndGet(v1 -> v1 + v);
            list.add(info);
        });
        return new DBSize(sizeConvert(allSize.get()), list);
    }

    /**
     * 转换大小
     *
     * @param size 单位为Byte
     * @return
     */
    private String sizeConvert(Long size) {

        //特殊情况
        if (size == null || size < 0) {
            return null;
        }

        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            return (size * 1.0 / 1024) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            return (size * 1.0 / (1024 * 1024)) + "MB";
        } else {
            return (size * 1.0 / (1024 * 1024 * 1024)) + "GB";
        }
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


    /**
     * 默认的psql实现
     *
     * @param property
     * @return Map, key为表名, value为大小(单位Byte，包含索引大小+数据大小)
     */
    public Map<String, Long> psqlInfo(DBMonitorProperty property) {
        String sql = "SELECT " + "relname AS name, " + "pg_relation_size(relid) AS size  " + "FROM " + "pg_stat_user_tables  " + "WHERE " + "schemaname = 'public'  " + "ORDER BY " + "pg_relation_size ( relid ) DESC;";
        Connection conn = null;
        try {
            conn = getConnection(DBDriver.PSQL, property);
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);
            Map<String, Long> map = new HashMap<>();
            while (rs.next()) {
                String name = rs.getString("name");
                Long size = rs.getLong("size");
                map.put(name, size);
            }
            return map;
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
     * 默认的mysql实现
     *
     * @param property
     * @return Map, key为表名, value为大小(单位Byte，包含索引大小+数据大小)
     */
    public Map<String, Long> mysqlInfo(DBMonitorProperty property) {
        String sql = "SELECT " + "table_name as name, " + "sum( data_length) AS size " + "FROM " + "information_schema.TABLES " + "WHERE " + "table_schema = ? " + "AND table_type = 'base table' " + "GROUP BY " + "table_name " + "ORDER BY " + "size DESC;";
        Connection conn = null;
        try {
            conn = getConnection(DBDriver.MYSQL8, property);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, property.getDatabase());
            ResultSet rs = ps.executeQuery();
            Map<String, Long> map = new HashMap<>();
            while (rs.next()) {
                String name = rs.getString("name");
                Long size = rs.getLong("size");
                map.put(name, size);
            }
            return map;
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
     * 默认的sqlserver实现
     *
     * @param property
     * @return Map, key为表名, value为大小(单位Byte，包含索引大小+数据大小)
     */
    public Map<String, Long> sqlserverInfo(DBMonitorProperty property) {
        return null;
    }

    /**
     * 默认的oracle实现
     *
     * @param property
     * @return Map, key为表名, value为大小(单位Byte，包含索引大小+数据大小)
     */
    public Map<String, Long> oracleInfo(DBMonitorProperty property) {
        String sql = "select " + "SEGMENT_NAME as name, " + "SUM( BYTES ) as allsize  " + "from " + "user_segments  " + "where " + "SEGMENT_TYPE = 'TABLE'  " + "group by SEGMENT_NAME " + "order by allsize";

        Connection conn = null;
        try {
            conn = getConnection(DBDriver.ORACLE, property);
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);
            Map<String, Long> map = new HashMap<>();
            while (rs.next()) {
                String name = rs.getString("name");
                Long size = rs.getLong("allsize");
                map.put(name, size);
            }
            return map;
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
     * 默认的mongo实现
     *
     * @param property
     * @return Map, key为表名, value为大小(单位Byte，包含索引大小+数据大小)
     */
    public Map<String, Long> mongoInfo(DBMonitorProperty property) {
//        MongoClient client;
//        if (property.getUsername() == null || property.getPassword() == null) {
//            client = new MongoClient(property.getHost(), property.getPort());
//        } else {
//            MongoCredential credential = MongoCredential.createCredential(property.getUsername(), property.getDatabase(), property.getUsername().toCharArray());
//            ServerAddress address = new ServerAddress(property.getHost(), property.getPort());
//            MongoClientOptions options = new MongoClientOptions.Builder().build();
//            client = new MongoClient(address, credential, options);
//        }
//
//        MongoDatabase db = client.getDatabase(property.getDatabase());
//        MongoIterable<String> collectionNames = db.listCollectionNames();
//        Map<String, Long> map = new HashMap<>();
//        for (String tableName : collectionNames) {
////            String str="{collStats : 'track',scale : 1024}";//KB为单位
//            String str = String.format("{collStats : '%s'}", tableName);//B为单位
//            Document parse = Document.parse(str);
//            Document document = db.runCommand(parse);
//            Integer storageSize = (Integer) document.get("storageSize");
//            Integer totalIndexSize = (Integer) document.get("totalIndexSize");
//            if (storageSize == null || totalIndexSize == null) {
//                map.put(tableName, null);
//            } else {
//                map.put(tableName, (long) (storageSize + totalIndexSize));
//            }
//        }
//        return map;
        return null;
    }

    /**
     * 供开发者自己实现指定数据库的入口
     * 可以自己在该方法里再定义一套这种逻辑实现 或 重新实现DBMonitor
     *
     * @param property
     * @return Map, key为表名, value为大小(单位Byte，包含索引大小+数据大小)
     */
    public abstract Map<String, Long> otherDBInfo(DBMonitorProperty property);


}
