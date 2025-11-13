package cn.iocoder.yudao.module.lowcode.querier.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainContext;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainParams;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainSymbolType;
import cn.iocoder.yudao.module.lowcode.querier.utils.QueryDomainUtils;
import cn.iocoder.yudao.module.lowcode.querier.xml.QueryField;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.ERROR_CONFIGURATION;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * @author leo
 */
@Slf4j
public class QueryDomainAuthTypeInterceptor implements QueryDomainInterceptor {

    public static final String AUTH_USER_ID = "authUserId";

    public static volatile QueryDomainAuthTypeInterceptor INSTANCE = new QueryDomainAuthTypeInterceptor();

    private QueryDomainAuthTypeInterceptor() {
    }

    @Override
    public QueryDomainParams preHandle(QueryDomainContext context) {
        var authTypeQueryFields = context.queryFieldFilter((table, field) -> StrUtil.isNotEmpty(field.getAuthType()));
        if (CollectionUtil.isEmpty(authTypeQueryFields)) {
            return context.getParams();
        }
        Map<String, List<QueryField>> authTypeQueryFieldsMap = new HashMap<>();
        for (QueryField queryField : authTypeQueryFields) {
            if (!authTypeQueryFieldsMap.containsKey(queryField.getAuthType())) {
                authTypeQueryFieldsMap.put(queryField.getAuthType(), new ArrayList<>());
            }
            authTypeQueryFieldsMap.get(queryField.getAuthType()).add(queryField);
        }
        Map<String, Object> authTypeDataMap = new HashMap<>();
        authTypeDataMap.put(AUTH_USER_ID, getLoginUserId());

        var queryParams = context.getParams();
        authTypeQueryFieldsMap.forEach((authType, list) -> {
            if (authTypeDataMap.containsKey(authType)) {
                for (QueryField queryField : list) {
                    QueryDomainUtils.addWhere(queryParams, queryField.getName(), QueryDomainSymbolType.EQ, authTypeDataMap.get(authType));
                }
            } else {
                log.error(String.format("error auth type %s", authType));
                throw exception(ERROR_CONFIGURATION);
            }
        });

        return queryParams;
    }

    @Override
    public List<Map<Object, Object>> postHandle(QueryDomainContext context) {
        return context.getResults();
    }

}
