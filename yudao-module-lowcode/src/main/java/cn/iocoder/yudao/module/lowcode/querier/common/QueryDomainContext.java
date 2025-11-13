package cn.iocoder.yudao.module.lowcode.querier.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.module.lowcode.querier.xml.QueryDomain;
import cn.iocoder.yudao.module.lowcode.querier.xml.QueryField;
import cn.iocoder.yudao.module.lowcode.querier.xml.QueryTable;
import lombok.Data;

import javax.script.Bindings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author leo
 */
@Data
public class QueryDomainContext {

    /**
     * 请求Domain
     */
    private QueryDomain domain;

    /**
     * 是否开启多租户
     */
    private Boolean tenantEnable;

    /**
     * 逻辑已删除值(默认为 1)
     */
    private String logicDeleteValue;

    /**
     * 请求参数
     */
    private QueryDomainParams params;

    /**
     * 有效主表
     */
    private QueryTable mainTable;

    /**
     * 有效副表
     */
    private List<QueryTable> tableList;

    /**
     * 结果数据
     */
    private List<Map<Object, Object>> results;

    /**
     * 有效查询字段
     */
    private List<QueryField> tableFieldList;

    public void addQueryTable(QueryTable table) {
        if (this.tableList == null) {
            this.tableList = new ArrayList<>();
        }
        this.tableList.add(table);
    }

    public List<QueryField> queryFieldFilter(QueryFieldTest fieldTest) {
        return this.queryFieldFilter(fieldTest, null);
    }

    private List<QueryField> queryFieldFilter(QueryFieldTest fieldTest, Integer maxSize) {
        List<QueryField> result = new ArrayList<>();
        queryFieldFilter(this.mainTable, fieldTest, result, maxSize);
        if (maxSize != null && result.size() >= maxSize) {
            return result;
        }
        if (CollectionUtil.isNotEmpty(this.tableList)) {
            for (QueryTable queryTable : this.tableList) {
                queryFieldFilter(queryTable, fieldTest, result, maxSize);
                if (maxSize != null && result.size() >= maxSize) {
                    return result;
                }
            }
        }
        return result;
    }

    public Optional<QueryField> queryFieldFilterFirst(QueryFieldTest fieldTest) {
        var list = this.queryFieldFilter(fieldTest, 1);
        return list.stream().findFirst();
    }

    private void queryFieldFilter(QueryTable table, QueryFieldTest fieldTest, List<QueryField> result, Integer maxSize) {
        for (QueryField queryField : table.getQueryFieldList()) {
            if (fieldTest.call(table, queryField)) {
                result.add(queryField);
                if (maxSize != null && result.size() >= maxSize) {
                    return;
                }
            }
        }
    }

    @FunctionalInterface
    public interface QueryFieldTest {
        boolean call(QueryTable table, QueryField field);
    }

    public Bindings setToBindings(Bindings bindings) {
        bindings.put("$domain", domain);
        bindings.put("$params", params);
        bindings.put("$mainTable", mainTable);
        bindings.put("$tableList", tableList);
        bindings.put("$results", results);
        return bindings;
    }

}
