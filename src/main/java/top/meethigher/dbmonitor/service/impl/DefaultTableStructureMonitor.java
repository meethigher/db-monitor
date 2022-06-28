package top.meethigher.dbmonitor.service.impl;

import top.meethigher.dbmonitor.model.DBMonitorProperty;
import top.meethigher.dbmonitor.model.structuremonitor.TableStructure;

/**
 * 默认的数据库表结构监控实现
 *
 * @author chenchuancheng
 * @since 2022/6/27 15:26
 */
public class DefaultTableStructureMonitor extends AbstractTableStructureMonitor {
    @Override
    public TableStructure otherDBStructure(DBMonitorProperty property, String tableName) {
        return null;
    }
}
