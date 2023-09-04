package top.meethigher.dboperation.service;

/**
 * 数据库列类型与java类型映射
 *
 * @author chenchuancheng
 * @since 2023/09/04 09:39
 */
public interface DatabaseColumnTypeMapper {


    /**
     * 通过列类型获取java类型
     *
     * @param columnType 列类型
     * @return {@link Class}<{@link ?}> java类型
     */
    Class<?> getJavaTypeFromColumnType(String columnType);

    /**
     * 通过java类型获取列类型
     *
     * @param javaType java类型
     * @return 列类型
     */
    String getColumnTypeFromJavaType(Class<?> javaType);
}
