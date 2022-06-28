package top.meethigher.dbmonitor.service.impl;


import top.meethigher.dbmonitor.model.DBMonitorProperty;

import java.util.Map;

/**
 * 默认的数据库监控表大小实现
 *
 * @author chenchuancheng
 * @since 2022/6/24 11:27
 */
public class DefaultDBSizeMonitor extends AbstractDBSizeMonitor {

    @Override
    public Map<String, Long> otherDBInfo(DBMonitorProperty property) {
        return null;
    }
}
