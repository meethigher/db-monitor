package top.meethigher.dbmonitor.model.structuremonitor;

import java.util.List;

/**
 * 表结构
 *
 * @author chenchuancheng
 * @since 2022/6/27 10:55
 */
public class TableStructure {

    /**
     * tableName 表名
     */
    private String tableName;

    /**
     * tableName下所有的列信息
     */
    private List<ColumnInfo> columnInfos;

    public TableStructure(String tableName, List<ColumnInfo> columnInfos) {
        this.tableName = tableName;
        this.columnInfos = columnInfos;
    }

    public TableStructure() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnInfo> getColumnInfos() {
        return columnInfos;
    }

    public void setColumnInfos(List<ColumnInfo> columnInfos) {
        this.columnInfos = columnInfos;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"tableName\":\"")
                .append(tableName).append('\"');
        sb.append(",\"columnInfos\":")
                .append(columnInfos);
        sb.append('}');
        return sb.toString();
    }
}
