package cn.iocoder.yudao.module.lowcode.querier.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainContext;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainParams;
import cn.iocoder.yudao.module.lowcode.querier.xml.QueryField;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author leo
 */
public class QueryFieldHiddenInterceptor implements QueryDomainInterceptor {

    public static volatile QueryFieldHiddenInterceptor INSTANCE = new QueryFieldHiddenInterceptor();

    private QueryFieldHiddenInterceptor() {
    }

    @Override
    public QueryDomainParams preHandle(QueryDomainContext context) {
        return context.getParams();
    }

    @Override
    public List<Map<Object, Object>> postHandle(QueryDomainContext context) {
        var hiddenFields = context.queryFieldFilter((table, field) -> Boolean.TRUE.equals(field.getHidden()));
        if (CollectionUtil.isEmpty(hiddenFields)) {
            return context.getResults();
        }
        var hiddenNames = hiddenFields.stream().map(QueryField::getName).collect(Collectors.toSet());
        var dataList = context.getResults();
        if (CollectionUtil.isNotEmpty(dataList)) {
            for (Map<Object, Object> data : dataList) {
                for (String hiddenName : hiddenNames) {
                    data.remove(hiddenName);
                }
            }
        }
        return dataList;
    }
}
