package top.meethigher.dboperation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库大小
 *
 * @author chenchuancheng
 * @since 2023/08/30 14:14
 */
public class DatabaseSize implements Serializable {


    /**
     * 库名
     */
    private String dbName;

    /**
     * 所有表占用大小，单位自动转换Byte,KB,MB,GB
     */
    private String dbSize;

    /**
     * 表信息
     */
    private List<TableSize> tableSizeList = new ArrayList<>();

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbSize() {
        return dbSize;
    }

    public void setDbSize(String dbSize) {
        this.dbSize = dbSize;
    }

    public List<TableSize> getTableSizeList() {
        return tableSizeList;
    }

    public void setTableSizeList(List<TableSize> tableSizeList) {
        this.tableSizeList = tableSizeList;
    }

    public static class TableSize implements Serializable {
        /**
         * 表名
         */
        private String tableName;

        /**
         * 表大小
         */
        private String tableSize;


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
    }
}
