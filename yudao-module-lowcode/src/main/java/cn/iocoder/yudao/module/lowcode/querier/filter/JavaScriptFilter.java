package cn.iocoder.yudao.module.lowcode.querier.filter;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainContext;
import cn.iocoder.yudao.module.lowcode.querier.utils.GraalJsScriptUtil;
import lombok.extern.slf4j.Slf4j;

import javax.script.CompiledScript;
import javax.script.ScriptException;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception0;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.QUERY_DOMAIN_FILTER_FAILURE;

/**
 * @author leo
 */
@Slf4j
public class JavaScriptFilter implements QueryDomainFilter {
    private final CompiledScript script;

    public JavaScriptFilter(String script) {
        this.script = GraalJsScriptUtil.compile(GraalJsScriptUtil.wrapWithIIFE(script));
    }

    @Override
    public void doFilter(QueryDomainContext context) throws ScriptException {
        var result = this.script.eval(context.setToBindings(this.script.getEngine().createBindings()));
        log.info("{} JavaScriptFilter result {}", context.getDomain().getDesc(), result);
        if (result == null || Boolean.TRUE.equals(result)) {
            return;
        }
        var errorMsg = String.valueOf(result).trim();
        if (StrUtil.isNotEmpty(errorMsg)) {
            throw exception0(QUERY_DOMAIN_FILTER_FAILURE.getCode(), errorMsg);
        }
    }

}
