package cn.iocoder.yudao.module.lowcode.querier.filter;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainContext;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * @author leo
 */
public class QueryDomainLoginFilter implements QueryDomainFilter {

    public static volatile QueryDomainLoginFilter INSTANCE = new QueryDomainLoginFilter();

    private QueryDomainLoginFilter() {
    }

    @Override
    public void doFilter(QueryDomainContext context) {
        if (getLoginUserId() == null) {
            var queryDomain = context.getDomain();
            if (Boolean.TRUE.equals(queryDomain.getLogin()) || StrUtil.isNotEmpty(queryDomain.getPermission())) {
                throw exception(UNAUTHORIZED);
            }
            var hasAuthTypeField = context.queryFieldFilterFirst((table, field) -> StrUtil.isNotEmpty(field.getAuthType()));
            if (hasAuthTypeField.isPresent()) {
                throw exception(UNAUTHORIZED);
            }
        }
    }

}
