package cn.iocoder.yudao.module.lowcode.querier;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainContext;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainSymbolType;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainWhereParams;
import cn.iocoder.yudao.module.lowcode.querier.xml.QueryDomain;
import cn.iocoder.yudao.module.lowcode.querier.xml.QueryField;
import cn.iocoder.yudao.module.lowcode.querier.xml.QueryTable;
import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author leo
 */
@Slf4j
public class QueryDomainSqlBuilder {
    private final QueryDomainContext context;
    private final Set<String> invalidQueryTableIdSet;
    private final Map<String, QueryTable> queryTableMap;
    private final Map<String, QueryFieldInfo> queryFieldInfoMap;
    @Getter
    private final Map<String, Object> sqlParams = new HashMap<>();
    @Getter
    private final Map<String, String> sqlFragments = new HashMap<>();
    private String selectSql = "";
    private String columnSql = "";
    private String tableSql = "";
    private String whereSql = "";
    private String orderBySql = "";
    private String limitSql = "";

    public QueryDomainSqlBuilder(QueryDomainContext context) {
        this.context = context;
        this.queryTableMap = context.getTableList().stream().collect(Collectors.toMap(QueryTable::getId, v -> v));
        this.invalidQueryTableIdSet = getInvalidQueryTableIdSet();
        this.queryFieldInfoMap = getQueryFieldInfoMap();
        this.context.setTableFieldList(this.queryFieldInfoMap.values().stream().map(e -> e.queryField).toList());
    }

    private Set<String> getInvalidQueryTableIdSet() {
        var invalidSet = new HashSet<String>();
        if (CollectionUtil.isNotEmpty(context.getDomain().getMainTableList())) {
            for (QueryTable table : context.getDomain().getMainTableList()) {
                if (!this.queryTableMap.containsKey(table.getId())) {
                    invalidSet.add(table.getId());
                }
            }
        }
        if (CollectionUtil.isNotEmpty(context.getDomain().getQueryTableList())) {
            for (QueryTable table : context.getDomain().getQueryTableList()) {
                if (!this.queryTableMap.containsKey(table.getId())) {
                    invalidSet.add(table.getId());
                }
            }
        }
        return invalidSet;
    }

    // tableId 是否有效
    private boolean isTableIdValid(String id) {
        return this.context.getMainTable().getId().equals(id) || !invalidQueryTableIdSet.contains(id);
    }

    // 提取形如: 变量名.字段名 的字符串
    private List<String> extractVariableFieldPairs(String input) {
        List<String> results = new ArrayList<>();
        if (StrUtil.isNotBlank(input)) {
            String regex = "(?<!\\w)(\\w+)\\.(\\w+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                results.add(matcher.group(1) + "." + matcher.group(2));
            }
        }
        return results;
    }

    // 形如: 变量名.字段名 的 字段id 是否有效
    private boolean isVariableValid(String idVar) {
        String[] attrs = idVar.split("[.]");
        if (attrs.length != 2) {
            throw new IllegalArgumentException("提取变量 [" + idVar + "] 格式不正确, 无法正确定位到表Id");
        }
        return isTableIdValid(attrs[0]);
    }

    // 字段是否有效, id 不能为空, 自定义查询关联所有表全部有效
    private boolean isQueryFieldValid(QueryField queryField) {
        if (StrUtil.isNotBlank(queryField.getValue())) {
            for (String pairVar : extractVariableFieldPairs(queryField.getValue())) {
                if (!isVariableValid(pairVar)) {
                    return false;
                }
            }
            return true;
        }
        return StrUtil.isNotBlank(queryField.getId());
    }

    private void appendQueryFieldInfoMap(Map<String, QueryFieldInfo> map, QueryTable table, List<QueryField> queryFieldList) {
        for (QueryField queryField : queryFieldList) {
            if (isQueryFieldValid(queryField)) {
                if (StrUtil.isNotBlank(queryField.getValue())) {
                    map.put(queryField.getName(), new QueryFieldInfo(queryField, "(" + queryField.getValue() + ")"));
                } else {
                    String[] attrs = queryField.getId().split("[.]");
                    var fieldId = attrs.length == 1 && table != null ? table.getId() + "." + attrs[0] : queryField.getId();
                    map.put(queryField.getName(), new QueryFieldInfo(queryField, fieldId));
                }
            }
        }
    }

    private Map<String, QueryFieldInfo> getQueryFieldInfoMap() {
        Map<String, QueryFieldInfo> map = new LinkedHashMap<>();

        //主表字段
        appendQueryFieldInfoMap(map, this.context.getMainTable(), this.context.getMainTable().getQueryFieldList());

        //关联表字段
        for (QueryTable queryTable : this.context.getTableList()) {
            appendQueryFieldInfoMap(map, queryTable, queryTable.getQueryFieldList());
        }

        //全局字段
        appendQueryFieldInfoMap(map, null, this.context.getDomain().getQueryFieldList());
        return map;
    }

    public String getSelectListSql() {
        return buildSelectSql().buildColumnSql().buildTableSql().buildWhereSql().buildOrderBySql().buildLimitSql().buildSql();
    }

    public String getSelectOneSql() {
        return buildSelectSql().buildColumnSql().buildTableSql().buildWhereSql().buildOrderBySql().limitOne().buildSql();
    }

    public String getSelectCountSql() {
        return buildSelectSql().buildCountSql().buildTableSql().buildWhereSql().buildSql();
    }

    private QueryDomainSqlBuilder buildSelectSql() {
        this.selectSql = "select";
        return this;
    }

    private QueryDomainSqlBuilder buildCountSql() {
        this.columnSql = "count(*) as cnt";
        return this;
    }

    private QueryDomainSqlBuilder buildColumnSql() {
        StringBuilder sql = new StringBuilder();
        for (QueryFieldInfo info : queryFieldInfoMap.values()) {
            sql.append(info.sql).append(" as ").append(info.name()).append(",");
        }
        sql.deleteCharAt(sql.lastIndexOf(","));
        this.columnSql = sql.toString();
        return this;
    }

    private QueryDomainSqlBuilder buildTableSql() {
        StringBuilder sql = new StringBuilder();

        // 主表
        var mainTable = this.context.getMainTable();
        String mainTableSql = mainTable.getTable();
        if (mainTable.getQuerySql() != null && StrUtil.isNotBlank(mainTable.getQuerySql().getValue())) {
            mainTableSql = "(" + mainTable.getQuerySql().getValue() + ")";
        }
        sql.append("from ").append(mainTableSql).append(" as ").append(mainTable.getId());

        // 关联表
        for (QueryTable queryTable : this.context.getTableList()) {
            var tableSql = queryTable.getTable();
            if (queryTable.getQuerySql() != null && StrUtil.isNotBlank(queryTable.getQuerySql().getValue())) {
                tableSql = "(" + queryTable.getQuerySql().getValue() + ")";
            }
            if (StrUtil.isNotBlank(queryTable.getJoinOn())) {
                sql.append(" join ").append(tableSql).append(" as ").append(queryTable.getId()).append(" on ").append(queryTable.getJoinOn());
            } else if (StrUtil.isNotBlank(queryTable.getLeftJoinOn())) {
                sql.append(" left join ").append(tableSql).append(" as ").append(queryTable.getId()).append(" on ").append(queryTable.getLeftJoinOn());
            } else {
                throw new IllegalArgumentException("table join type not support");
            }
        }

        this.tableSql = sql.toString();
        return this;
    }

    private QueryDomainSqlBuilder buildWhereSql() {
        StringBuilder sql = new StringBuilder();

        var queryDomain = this.context.getDomain();
        //默认条件
        if (queryDomain.getQueryWhere() != null && StrUtil.isNotBlank(queryDomain.getQueryWhere().getValue())) {
            if (sql.isEmpty()) {
                sql.append("where ").append(queryDomain.getQueryWhere().getValue());
            } else {
                sql.append(" and ").append(queryDomain.getQueryWhere().getValue());
            }
        }

        if (sql.isEmpty()) {
            sql.append("where 1=1");
        }

        //租户与逻辑删除
        appendTenantAndLogicDeleteSql(sql, queryDomain, context.getMainTable());
        if (CollectionUtil.isNotEmpty(context.getTableList())) {
            for (QueryTable table : context.getTableList()) {
                appendTenantAndLogicDeleteSql(sql, queryDomain, table);
            }
        }

        var queryParams = this.context.getParams();
        if (CollectionUtil.isNotEmpty(queryParams.getWhereParams())) {
            for (QueryDomainWhereParams whereParams : queryParams.getWhereParams()) {
                String criteria = buildCriteria("", whereParams);
                if (StrUtil.isNotEmpty(criteria)) {
                    sql.append(" and (").append(criteria).append(")");
                }
            }
        }

        this.whereSql = sql.toString();
        return this;
    }

    private String buildCriteria(String parentName, QueryDomainWhereParams where) {
        Object value = where.getValue();
        List<Object> values = where.getValues();
        if (where.getSymbol() == null) {
            return "";
        }

        var alwaysTrueSql = "1=1";
        StringBuilder cb = new StringBuilder();

        if (QueryDomainSymbolType.NONE == where.getSymbol() && StrUtil.isEmpty(where.getName())) {
            cb.append(alwaysTrueSql);
        } else {
            if (value == null && values == null) {
                return "";
            }
            if (value instanceof String && StrUtil.isEmpty((String) value) && CollectionUtil.isEmpty(values)) {
                return "";
            }
            QueryFieldInfo queryFieldInfo = queryFieldInfoMap.get(where.getName());
            if (queryFieldInfo == null) {
                log.error(String.format("QueryField %s is not exist", where.getName()));
                return "";
            }
            if (StrUtil.isEmpty(queryFieldInfo.authType())) {
                if (StrUtil.isEmpty(queryFieldInfo.symbols()) || !queryFieldInfo.symbols().contains(where.getSymbol().name())) {
                    throw new IllegalArgumentException(String.format("QueryField %s symbol %s is not allowed query", where.getName(), where.getSymbol()));
                }
            }

            int startIndex = 0;
            var tempName = StrUtil.isEmpty(parentName) ? queryFieldInfo.name() : parentName + "_" + queryFieldInfo.name();
            var paramName = tempName;
            while (sqlParams.containsKey(tempName)) {
                tempName = tempName + "_" + (startIndex++);
            }
            switch (where.getSymbol()) {
                case NONE:
                    sqlParams.put(paramName, where.getValue() != null ? where.getValue() : where.getValues());
                    cb.append(alwaysTrueSql);
                    break;
                case LIKE:
                    sqlParams.put(paramName, where.getValue());
                    cb.append(queryFieldInfo.sql).append(" like CONCAT('%',#{").append(paramName).append("},'%')");
                    break;
                case LLIKE:
                    sqlParams.put(paramName, where.getValue());
                    cb.append(queryFieldInfo.sql).append(" like CONCAT('%',#{").append(paramName).append("})");
                    break;
                case RLIKE:
                    sqlParams.put(paramName, where.getValue());
                    cb.append(queryFieldInfo.sql).append(" like CONCAT(#{").append(paramName).append("},'%')");
                    break;
                case NOTLIKE:
                    sqlParams.put(paramName, where.getValue());
                    cb.append(queryFieldInfo.sql).append(" not like CONCAT('%',#{").append(paramName).append("},'%')");
                    break;
                case EQ:
                    sqlParams.put(paramName, where.getValue());
                    cb.append(queryFieldInfo.sql).append(" = #{").append(paramName).append("}");
                    break;
                case NE:
                    sqlParams.put(paramName, where.getValue());
                    cb.append(queryFieldInfo.sql).append(" != #{").append(paramName).append("}");
                    break;
                case GE:
                    sqlParams.put(paramName, where.getValue());
                    cb.append(queryFieldInfo.sql).append(" >= #{").append(paramName).append("}");
                    break;
                case GT:
                    sqlParams.put(paramName, where.getValue());
                    cb.append(queryFieldInfo.sql).append(" > #{").append(paramName).append("}");
                    break;
                case LE:
                    sqlParams.put(paramName, where.getValue());
                    cb.append(queryFieldInfo.sql).append(" <= #{").append(paramName).append("}");
                    break;
                case LT:
                    sqlParams.put(paramName, where.getValue());
                    cb.append(queryFieldInfo.sql).append(" < #{").append(paramName).append("}");
                    break;
                case IN:
                case NOTIN:
                    StringBuilder notString = new StringBuilder();
                    if (CollectionUtil.isNotEmpty(where.getValues())) {
                        for (int i = 0; i < where.getValues().size(); i++) {
                            var paramId = paramName + "_" + i;
                            sqlParams.put(paramId, where.getValues().get(i));
                            notString.append("#{").append(paramId).append("},");
                        }
                    }
                    if (where.getSymbol() == QueryDomainSymbolType.IN) {
                        cb.append(queryFieldInfo.sql).append(" in (");
                    } else {
                        cb.append(queryFieldInfo.sql).append(" not in (");
                    }
                    var sqlFragment = notString.substring(0, notString.length() - 1);
                    sqlFragments.put(paramName, sqlFragment);
                    cb.append(sqlFragment).append(")");
                    break;
                case BETWEEN:
                    sqlParams.put(paramName, where.getValues());
                    sqlParams.put(paramName + "Min", where.getValues().get(0));
                    sqlParams.put(paramName + "Max", where.getValues().get(1));
                    cb.append(queryFieldInfo.sql).append(" between #{").append(paramName).append("Min} and #{").append(paramName).append("Max}");
                    break;
                case ISNULL:
                    cb.append(queryFieldInfo.sql).append(" is null");
                    break;
                case NOTNULL:
                    cb.append(queryFieldInfo.sql).append(" is not null");
                    break;
                case JSON_CONTAINS:
                    sqlParams.put(paramName, "[" + where.getValue() + "]");
                    cb.append("json_contains(").append(queryFieldInfo.sql).append(",").append("#{").append(paramName).append("},'$')");
                    break;
                default:
                    throw new IllegalArgumentException(String.format("where params symbol %s not Supported!", where.getSymbol()));
            }
        }
        if (cb.length() <= 0) {
            return null;
        }
        if (where.getAnds() != null) {
            List<String> andSql = new ArrayList<>();
            for (QueryDomainWhereParams args : where.getAnds()) {
                andSql.add(buildCriteria(parentName, args));
            }
            var subSql = StringUtils.join(andSql, " and ");
            if (alwaysTrueSql.contentEquals(cb)) {
                cb = new StringBuilder(subSql);
            } else {
                cb = new StringBuilder(cb.append(" and ").append("(").append(subSql).append(")"));
            }
        }
        if (where.getOrs() != null) {
            List<String> orSql = new ArrayList<>();
            for (QueryDomainWhereParams args : where.getOrs()) {
                orSql.add(buildCriteria(parentName, args));
            }
            var subSql = "(" + StringUtils.join(orSql, ") or (") + ")";
            if (alwaysTrueSql.contentEquals(cb)) {
                cb = new StringBuilder(subSql);
            } else {
                cb = new StringBuilder(cb.append(" and ").append("(").append(subSql).append(")"));
            }
        }
        return cb.toString();
    }

    private QueryDomainSqlBuilder buildOrderBySql() {
        StringBuilder orderBySql = new StringBuilder();
        var queryParams = this.context.getParams();
        if (queryParams != null && queryParams.getPageParams() != null) {
            var queryPageParams = queryParams.getPageParams();
            StringBuilder sortSql = new StringBuilder();
            if (CollectionUtil.isNotEmpty(queryPageParams.getSortingFields())) {
                for (SortingField sortingField : queryPageParams.getSortingFields()) {
                    QueryFieldInfo queryFieldInfo = queryFieldInfoMap.get(sortingField.getField());
                    if (Boolean.TRUE.equals(queryFieldInfo.sortable())) {
                        sortSql.append(queryFieldInfo.sql).append(" ").append(sortingField.getOrder());
                    }
                }
                orderBySql.append("order by ").append(sortSql);
            }
        }
        this.orderBySql = orderBySql.toString();
        return this;
    }

    private QueryDomainSqlBuilder buildLimitSql() {
        StringBuilder limitSql = new StringBuilder();
        var queryParams = this.context.getParams();
        if (queryParams != null && queryParams.getPageParams() != null) {
            var queryPageParams = queryParams.getPageParams();
            int start = (queryPageParams.getPageNo() - 1) * queryPageParams.getPageSize();
            limitSql.append("limit ").append(start).append(",").append(queryPageParams.getPageSize());
        } else {
            Integer maxReturnRows = this.context.getDomain().getMaxReturnRows();
            limitSql.append("limit 0,").append(maxReturnRows == null ? 1000 : maxReturnRows);
        }
        this.limitSql = limitSql.toString();
        return this;
    }

    private QueryDomainSqlBuilder limitOne() {
        this.limitSql = "limit 1";
        return this;
    }

    public String buildSql() {
        log.info("buildSql with sqlFragment : {} ", JSON.toJSONString(sqlFragments));
        return replaceSqlFragment(String.format("%s %s %s %s %s %s", selectSql, columnSql, tableSql, whereSql, orderBySql, limitSql));
    }

    private String replaceSqlFragment(String sql) {
        if (CollectionUtil.isNotEmpty(sqlFragments)) {
            for (Map.Entry<String, String> fragment : sqlFragments.entrySet()) {
                sql = sql.replaceAll("#[{]" + fragment.getKey() + "[}]", fragment.getValue());
            }
        }
        return sql;
    }

    private boolean isTenantEnable(QueryTable table) {
        if (Boolean.FALSE.equals(this.context.getTenantEnable())) {
            return false;
        }
        return !Boolean.TRUE.equals(table.getDisableTenant());
    }

    private boolean isLogicDeleteEnable(QueryTable table) {
        return !Boolean.TRUE.equals(table.getDisableLogicDelete());
    }

    private void appendTenantAndLogicDeleteSql(StringBuilder sql, QueryDomain queryDomain, QueryTable table) {
        if (isTenantEnable(table)) {
            sqlParams.put("tenantId", TenantContextHolder.getTenantId());
            sql.append(" and ").append(table.getId()).append(".tenant_id = #{tenantId}");
        }
        if (isLogicDeleteEnable(table)) {
            sqlParams.put("logicDeleteValue", this.context.getLogicDeleteValue());
            sql.append(" and ").append(table.getId()).append(".deleted <> #{logicDeleteValue}");
        }
    }

    public record QueryFieldInfo(QueryField queryField, String sql) {
        public String name() {
            return queryField.getName();
        }

        public String symbols() {
            return queryField.getSymbols();
        }

        @Override
        public String sql() {
            return sql;
        }

        public Boolean sortable() {
            return queryField.getSortable();
        }

        public String authType() {
            return queryField.getAuthType();
        }

        public String comment() {
            return queryField.getComment();
        }
    }

}
