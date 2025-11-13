package cn.iocoder.yudao.module.lowcode.querier.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainContext;
import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainParams;
import cn.iocoder.yudao.module.lowcode.querier.utils.GraalJsScriptUtil;
import lombok.extern.slf4j.Slf4j;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.util.List;
import java.util.Map;

/**
 * @author leo
 */
@Slf4j
public class JavaScriptInterceptor implements QueryDomainInterceptor {

    private final CompiledScript preHandleScript;
    private final CompiledScript postHandleScript;

    public JavaScriptInterceptor(String preHandleScript, String postHandleScript) {
        if (StrUtil.isNotEmpty(preHandleScript)) {
            this.preHandleScript = GraalJsScriptUtil.compile(GraalJsScriptUtil.wrapWithIIFE(preHandleScript));
        } else {
            this.preHandleScript = null;
        }
        if (StrUtil.isNotEmpty(postHandleScript)) {
            this.postHandleScript = GraalJsScriptUtil.compile(GraalJsScriptUtil.wrapWithIIFE(postHandleScript));
        } else {
            this.postHandleScript = null;
        }
    }

    @Override
    public QueryDomainParams preHandle(QueryDomainContext context) throws ScriptException {
        if (this.preHandleScript != null) {
            var result = this.preHandleScript.eval(context.setToBindings(this.preHandleScript.getEngine().getBindings(ScriptContext.ENGINE_SCOPE)));
            log.info("{} JavaScriptInterceptor preHandle params {}", context.getDomain().getDesc(), result);
            if (result != null) {
                return (QueryDomainParams) result;
            }
        }
        return context.getParams();
    }

    @Override
    public List<Map<Object, Object>> postHandle(QueryDomainContext context) throws ScriptException {
        if (this.postHandleScript != null) {
            var result = this.postHandleScript.eval(context.setToBindings(this.postHandleScript.getEngine().getBindings(ScriptContext.ENGINE_SCOPE)));
            log.info("{} JavaScriptInterceptor postHandle results {}", context.getDomain().getDesc(), result);
            if (result != null) {
                return (List<Map<Object, Object>>) result;
            }
        }
        return context.getResults();
    }
}
