package top.meethigher.dbmonitor.model.sizemonitor;

import java.util.List;

/**
 * 数据库大小信息
 *
 * @author chenchuancheng
 * @since 2022/6/27 10:31
 */
public class DBSize {

    /**
     * 所有表占用大小，单位自动转换Byte,KB,MB,GB
     */
    private String allSize;

    /**
     * 表信息
     */
    private List<TableSize> detailSize;

    public DBSize(String allSize, List<TableSize> detailSize) {
        this.allSize = allSize;
        this.detailSize = detailSize;
    }

    public DBSize() {
    }

    public String getAllSize() {
        return allSize;
    }

    public void setAllSize(String allSize) {
        this.allSize = allSize;
    }

    public List<TableSize> getDetailSize() {
        return detailSize;
    }

    public void setDetailSize(List<TableSize> detailSize) {
        this.detailSize = detailSize;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"allSize\":\"")
                .append(allSize).append('\"');
        sb.append(",\"detailSize\":")
                .append(detailSize);
        sb.append('}');
        return sb.toString();
    }
}
