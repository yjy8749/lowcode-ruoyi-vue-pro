package cn.iocoder.yudao.module.lowcode.querier.filter;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.security.core.service.SecurityFrameworkService;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainContext;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.FORBIDDEN;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * @author leo
 */
@Component
public class QueryDomainPermissionFilter implements QueryDomainFilter {

    @Resource
    SecurityFrameworkService ss;

    @Override
    public void doFilter(QueryDomainContext context) {
        var queryDomain = context.getDomain();
        if (StrUtil.isNotEmpty(queryDomain.getPermission()) && !ss.hasAnyPermissions(queryDomain.getPermission().split(","))) {
            throw exception(FORBIDDEN);
        }
    }

}
