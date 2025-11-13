package cn.iocoder.yudao.module.lowcode.querier.interceptor;


import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainContext;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainParams;

import javax.script.ScriptException;
import java.util.List;
import java.util.Map;

/**
 * @author leo
 */
public interface QueryDomainInterceptor {

    QueryDomainParams preHandle(QueryDomainContext context) throws ScriptException;

    List<Map<Object, Object>> postHandle(QueryDomainContext context) throws ScriptException;

}
