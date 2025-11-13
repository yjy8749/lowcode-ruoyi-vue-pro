package cn.iocoder.yudao.module.lowcode.querier.valid;

import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainContext;

import javax.script.ScriptException;

/**
 * @author leo
 */
public interface QueryDomainValidTest {

    boolean doTest(QueryDomainContext context) throws ScriptException;

}
