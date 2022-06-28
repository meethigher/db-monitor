package top.meethigher.dbmonitor.model.structuremonitor;

/**
 * 列信息
 *
 * @author chenchuancheng
 * @since 2022/6/27 14:32
 */
public class ColumnInfo {
    /**
     * 列名
     */
    private String columnName;

    /**
     * 列类型--数据库中的类型
     */
    private String columnType;

    /**
     * 列大小
     * 对于数字数据，这是最大的精度。
     * 对于字符数据，这是以字符为单位的长度。
     * 对于datetime数据类型，这是String表示的字符长度(假设分数秒组件允许的最大精度)。
     * 对于二进制数据，这是以字节为单位的长度。
     * 对于ROWID数据类型，这是以字节为单位的长度。
     * 对于列大小不适用的数据类型，返回Null。
     */
    private String columnSize;

    /**
     * 列注释
     */
    private String columnRemark;

    /**
     * 是否是主键
     */
    private boolean isPrimaryKey;

    /**
     * 列可为空
     */
    private boolean columnNullable;

    public ColumnInfo(String columnName, String columnType,
                      String columnSize, String columnRemark,
                      boolean isPrimaryKey, boolean columnNullable) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnSize = columnSize;
        this.columnRemark = columnRemark;
        this.isPrimaryKey = isPrimaryKey;
        this.columnNullable = columnNullable;
    }

    public ColumnInfo() {
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(String columnSize) {
        this.columnSize = columnSize;
    }

    public String getColumnRemark() {
        return columnRemark;
    }

    public void setColumnRemark(String columnRemark) {
        this.columnRemark = columnRemark;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public boolean isColumnNullable() {
        return columnNullable;
    }

    public void setColumnNullable(boolean columnNullable) {
        this.columnNullable = columnNullable;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"columnName\":\"")
                .append(columnName).append('\"');
        sb.append(",\"columnType\":\"")
                .append(columnType).append('\"');
        sb.append(",\"columnSize\":\"")
                .append(columnSize).append('\"');
        sb.append(",\"columnRemark\":\"")
                .append(columnRemark).append('\"');
        sb.append(",\"isPrimaryKey\":")
                .append(isPrimaryKey);
        sb.append(",\"columnNullable\":")
                .append(columnNullable);
        sb.append('}');
        return sb.toString();
    }
}
