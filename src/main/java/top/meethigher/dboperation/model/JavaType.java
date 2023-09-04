package top.meethigher.dboperation.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 常用java类型
 *
 * @author chenchuancheng
 * @since 2023/09/04 10:43
 */
public enum JavaType {

    STRING("字符串", String.class),
    INTEGER("4字节整型", Integer.class),
    LONG("8字节整型", Long.class),
    BOOLEAN("布尔型", Boolean.class),
    FLOAT("4字节浮点型", Float.class),
    DOUBLE("8字节浮点型", Double.class),
    DATE("日期类型", Date.class),
    LIST("单列集合", List.class),
    MAP("双列集合", Map.class),
    OBJECT("对象", Object.class),


    ;

    public String name;

    public Class<?> clazz;

    JavaType(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public static void main(String[] args) {
        System.out.println(JavaType.STRING.clazz);
    }
}
