package top.meethigher.dbmonitor.service;

/**
 * 表结构监控
 *
 * @author chenchuancheng
 * @since 2022/6/27 10:20
 */
public interface TableStructureMonitor<K, V> {


    /**
     * 查询数据库下某个表的结构
     *
     * @param k
     * @param tableName
     * @return
     */
    V queryTableStructure(K k, String tableName);
}
