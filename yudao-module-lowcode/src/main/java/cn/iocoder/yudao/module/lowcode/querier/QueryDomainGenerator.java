package cn.iocoder.yudao.module.lowcode.querier;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.infra.dal.dataobject.db.DataSourceConfigDO;
import cn.iocoder.yudao.module.infra.service.db.DataSourceConfigService;
import cn.iocoder.yudao.module.lowcode.utils.LowcodeJdbcUtils;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception0;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.QUERY_DOMAIN_GEN_XML_ERROR;

/**
 * @author leo
 */
@Lazy
@Slf4j
@Component
public class QueryDomainGenerator {

    @Resource
    private DataSourceConfigService dataSourceConfigService;

    public String genXml(Long dataSourceId, String tableName) {
        // 获得数据源配置
        DataSourceConfigDO config = dataSourceConfigService.getDataSourceConfig(dataSourceId);
        if (config == null) {
            throw exception0(QUERY_DOMAIN_GEN_XML_ERROR.getCode(), "数据源Id({}) 不存在！", dataSourceId);
        }

        var dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setUrl(config.getUrl());
        dataSourceProperty.setUsername(config.getUsername());
        dataSourceProperty.setPassword(config.getPassword());

        LowcodeJdbcUtils.addDataSource(config.getName(), dataSourceProperty);

        DataSource dataSource = LowcodeJdbcUtils.getDataSource(config.getName());

        if (dataSource == null) {
            throw exception0(QUERY_DOMAIN_GEN_XML_ERROR.getCode(), "数据源name({}) 不存在！", config.getName());
        }

        try {
            var connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();

            // 获取表的注释
            String tableComment = getTableComment(metaData, tableName);

            // 获取列的信息
            List<ColumnInfo> columns = getColumnInfo(metaData, tableName);
            if (CollectionUtil.isEmpty(columns)) {
                throw new IllegalArgumentException("查询表名不存在");
            }
            // 生成XML配置
            return generateXmlConfig(tableName, tableComment, columns);
        } catch (ServiceException e) {
            throw e;
        } catch (Throwable e) {
            log.error(String.valueOf(QUERY_DOMAIN_GEN_XML_ERROR), e);
            throw exception0(QUERY_DOMAIN_GEN_XML_ERROR.getCode(), e.getMessage());
        }
    }

    private String getTableComment(DatabaseMetaData metaData, String tableName) throws SQLException {
        try (ResultSet rs = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
            if (rs.next()) {
                return rs.getString("REMARKS");
            }
        }
        throw new IllegalArgumentException("查询表名不存在");
    }

    private List<ColumnInfo> getColumnInfo(DatabaseMetaData metaData, String tableName) throws SQLException {
        List<ColumnInfo> columns = Lists.newArrayList();
        try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String columnComment = rs.getString("REMARKS");
                columns.add(new ColumnInfo(columnName, columnComment));
            }
        }
        return columns;
    }

    private String generateXmlConfig(String tableName, String tableComment, List<ColumnInfo> columns) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append(String.format("<QueryDomain login=\"true\" desc=\"%s\">", tableComment));
        xmlBuilder.append(String.format("<MainTable id=\"%s\" table=\"%s\">", StrUtil.toCamelCase(tableName), tableName));
        for (ColumnInfo column : columns) {
            xmlBuilder.append(String.format("<QueryField id=\"%s\" name=\"%s\" comment=\"%s\" />", column.name(), StrUtil.toCamelCase(column.name()), column.comment()));
        }
        xmlBuilder.append("</MainTable>");
        xmlBuilder.append("</QueryDomain>");
        return XmlUtil.format(xmlBuilder.toString());
    }

    public record ColumnInfo(String name, String comment) {

    }

}
