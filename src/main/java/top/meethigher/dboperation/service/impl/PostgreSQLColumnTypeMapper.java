package top.meethigher.dboperation.service.impl;

import top.meethigher.dboperation.model.JavaType;
import top.meethigher.dboperation.service.AbstractDatabaseColumnTypeMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * postgresql
 *
 * @author chenchuancheng
 * @since 2023/09/04 10:58
 */
public class PostgreSQLColumnTypeMapper extends AbstractDatabaseColumnTypeMapper {

    private final Map<String, JavaType> map = new HashMap<>();

    {
        map.put("varchar", JavaType.STRING);
        map.put("int4", JavaType.INTEGER);
        map.put("int8", JavaType.LONG);
        map.put("float4", JavaType.FLOAT);
        map.put("float8", JavaType.DOUBLE);
        map.put("timestamp", JavaType.DATE);
        map.put("bool", JavaType.BOOLEAN);
    }


    private Class<?> getJavaTypeFromPostgreSQLColumnType(String columnType) {
        JavaType javaType = map.get(columnType);
        if (javaType == null) {
            return JavaType.OBJECT.clazz;
        } else {
            return javaType.clazz;
        }
    }

    private String getPostgreSQLColumnTypeFromJavaType(Class<?> javaType) {
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            JavaType type = map.get(key);
            if (javaType.equals(type.clazz)) {
                return key;
            }
        }
        throw new IllegalArgumentException("找不到对应 " + javaType + " 的类型");
    }

    @Override
    public Class<?> getJavaTypeFromColumnType(String columnType) {
        return getJavaTypeFromPostgreSQLColumnType(columnType);
    }

    @Override
    public String getColumnTypeFromJavaType(Class<?> javaType) {
        return getPostgreSQLColumnTypeFromJavaType(javaType);
    }
}
