package cn.iocoder.yudao.module.lowcode.querier.valid;

import cn.iocoder.yudao.module.lowcode.querier.common.QueryDomainContext;
import cn.iocoder.yudao.module.lowcode.querier.utils.GraalJsScriptUtil;
import lombok.extern.slf4j.Slf4j;

import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * @author leo
 */
@Slf4j
public class JavaScriptValidTest implements QueryDomainValidTest {

    private final CompiledScript script;

    public JavaScriptValidTest(String script) {
        this.script = GraalJsScriptUtil.compile(GraalJsScriptUtil.wrapWithIIFE(script));
    }

    @Override
    public boolean doTest(QueryDomainContext context) throws ScriptException {
        var result = this.script.eval(context.setToBindings(this.script.getEngine().createBindings()));
        log.info("{} JavaScriptValidTest result {}", context.getDomain().getDesc(), result);
        return Boolean.TRUE.equals(result);
    }
}
