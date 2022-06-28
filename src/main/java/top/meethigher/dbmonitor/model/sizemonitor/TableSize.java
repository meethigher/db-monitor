package top.meethigher.dbmonitor.model.sizemonitor;

/**
 * 表大小
 *
 * @author chenchuancheng
 * @since 2022/6/24 9:00
 */
public class TableSize {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表大小
     */
    private String tableSize;

    public TableSize(String tableName, String tableSize) {
        this.tableName = tableName;
        this.tableSize = tableSize;
    }

    public TableSize() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableSize() {
        return tableSize;
    }

    public void setTableSize(String tableSize) {
        this.tableSize = tableSize;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"tableName\":\"")
                .append(tableName).append('\"');
        sb.append(",\"tableSize\":\"")
                .append(tableSize).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
