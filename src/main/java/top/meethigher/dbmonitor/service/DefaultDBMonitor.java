package top.meethigher.dbmonitor.service;


import top.meethigher.dbmonitor.model.structuremonitor.TableStructure;
import top.meethigher.dbmonitor.model.DBMonitorProperty;

import java.util.Map;

/**
 * 默认的DBMonitor实现
 *
 * @author chenchuancheng
 * @since 2022/7/14 9:44
 */
public class DefaultDBMonitor extends AbstractDBMonitor {
    @Override
    public Map<String, Long> otherDBInfo(DBMonitorProperty property) {
        return null;
    }

    @Override
    public TableStructure otherDBStructure(DBMonitorProperty property, String tableName) {
        return null;
    }
}
