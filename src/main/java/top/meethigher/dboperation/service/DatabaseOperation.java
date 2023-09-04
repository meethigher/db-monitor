package top.meethigher.dboperation.service;

import top.meethigher.dboperation.model.DataConnectionInfo;
import top.meethigher.dboperation.model.DatabaseSize;
import top.meethigher.dboperation.model.TableStructure;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * 数据库操作
 *
 * @author chenchuancheng
 * @since 2023/08/30 14:02
 */
public interface DatabaseOperation {

    /**
     * 得到数据库大小，适用多次高频使用
     *
     * @param dataSource 数据源
     * @return {@link DatabaseSize} 数据库大小
     */
    DatabaseSize getDatabaseSize(DataSource dataSource) throws SQLException;

    /**
     * 得到数据库大小，适用单次低频使用
     *
     * @param info 数据连接信息
     * @return {@link DatabaseSize} 数据库大小
     */
    DatabaseSize getDatabaseSize(DataConnectionInfo info) throws SQLException, ClassNotFoundException;


    /**
     * 得到表结构
     *
     * @param tableName 表名
     * @return {@link TableStructure} 表结构信息
     */
    TableStructure getTableStructure(String tableName, DataSource dataSource) throws SQLException;


    /**
     * 得到表结构
     *
     * @param tableName 表名
     * @param info      数据连接信息
     * @return {@link TableStructure} 表结构信息
     */
    TableStructure getTableStructure(String tableName, DataConnectionInfo info) throws SQLException, ClassNotFoundException;


    /**
     * 得到表列表
     *
     * @param dataSource 数据源
     * @return {@link List}<{@link String}>
     * @throws SQLException sqlexception异常
     */
    List<String> getTableNameList(DataSource dataSource) throws SQLException;

    /**
     * 得到表列表
     *
     * @param info 数据库连接信息
     * @return {@link List}<{@link String}>
     * @throws SQLException           sqlexception异常
     * @throws ClassNotFoundException 类没有发现异常
     */
    List<String> getTableNameList(DataConnectionInfo info) throws SQLException, ClassNotFoundException;
}
