package top.meethigher.dbmonitor.service;

/**
 * 监控DB下各个表的大小
 *
 * @author chenchuancheng
 * @since 2022/6/23 18:06
 */
public interface DBSizeMonitor<K, V> {


    /**
     * 查询表大小信息
     *
     * @param k 数据库连接属性
     * @return 表大小信息
     */
    V queryDBSize(K k);

}
