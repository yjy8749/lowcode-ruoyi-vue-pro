package cn.iocoder.yudao.module.lowcode.querier.filter;


import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainContext;

import javax.script.ScriptException;

/**
 * @author leo
 */
public interface QueryDomainFilter {

    void doFilter(QueryDomainContext context) throws ScriptException;

}
