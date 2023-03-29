package top.meethigher.dbmonitor.service;


import top.meethigher.dbmonitor.model.DBMonitorProperty;

/**
 * 数据库监控
 *
 * @author chenchuancheng
 * @since 2022/7/14 9:36
 */
public interface DBMonitor<V,T> {

    /**
     * 查询表大小信息
     *
     * @param k 数据库连接属性
     * @return 表大小信息
     */
    V queryDBSize(DBMonitorProperty k);


    /**
     * 查询数据库下某个表的结构
     *
     * @param k 连接信息
     * @param tableName 表名
     * @return 表结构
     */
    T queryTableStructure(DBMonitorProperty k, String tableName);
}
