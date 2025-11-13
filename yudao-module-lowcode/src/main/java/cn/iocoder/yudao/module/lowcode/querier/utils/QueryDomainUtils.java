package cn.iocoder.yudao.module.lowcode.querier.utils;

import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainPageParams;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainParams;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainSymbolType;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainWhereParams;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leo
 */
public class QueryDomainUtils {

    public static void addWhere(QueryDomainParams params, String name, String symbol, Object value) {
        addWhere(params, name, QueryDomainSymbolType.valueOf(symbol.toUpperCase()), value);
    }

    public static void addWhere(QueryDomainParams params, String name, QueryDomainSymbolType symbol, Object value) {
        if (params.getWhereParamsList() == null) {
            params.setWhereParamsList(new ArrayList<>());
        }
        var exist = params.getWhereParamsList().stream().filter(i -> i.getName().equals(name)).findFirst();
        if (exist.isEmpty()) {
            if (value instanceof List<?>) {
                params.getWhereParamsList().add(new QueryDomainWhereParams(name, symbol, (List<Object>) value));
            } else {
                params.getWhereParamsList().add(new QueryDomainWhereParams(name, symbol, value));
            }
        } else {
            exist.get().setSymbol(symbol);
            if (value instanceof List<?>) {
                exist.get().setValues((List<Object>) value);
            } else {
                exist.get().setValue(value);
            }
        }
    }

    public static QueryDomainWhereParams getWhere(QueryDomainParams params, String name) {
        if (params.getWhereParamsList() == null) {
            return null;
        }
        return params.getWhereParamsList().stream().filter(i -> i.getName().equals(name)).findFirst().orElse(null);
    }

    public static void setPage(QueryDomainParams params, Integer pageNo, Integer pageSize) {
        if (params.getPageParams() == null) {
            params.setPageParams(new QueryDomainPageParams());
        }
        params.getPageParams().setPageNo(pageNo);
        params.getPageParams().setPageSize(pageSize);
    }

    public static void addSort(QueryDomainParams params, String field, String order) {
        if (params.getPageParams() == null) {
            params.setPageParams(new QueryDomainPageParams());
        }
        if (params.getPageParams().getSortingFields() == null) {
            params.getPageParams().setSortingFields(new ArrayList<>());
        }
        params.getPageParams().getSortingFields().add(new SortingField(field, order));
    }

}
