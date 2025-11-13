package cn.iocoder.yudao.module.lowcode.querier.utils;

import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.StrUtil;
import cn.hutool.script.ScriptRuntimeException;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * @author leo
 */
public class GraalJsScriptUtil {

    private static final WeakConcurrentMap<String, ScriptEngine> CACHE = new WeakConcurrentMap<>();

    public static ScriptEngine createScript(String name) {
        if ("graalJs".equals(name)) {
            return GraalJSScriptEngine.create(null,
                    Context.newBuilder("js")
                            .allowHostAccess(HostAccess.ALL)
                            .allowHostClassLookup(s -> true)
                            .option("js.ecmascript-version", "2022"));
        }
        throw new NullPointerException(StrUtil.format("Script for [{}] not support !", name));
    }

    public static ScriptEngine getScript(String nameOrExtOrMime) {
        return CACHE.computeIfAbsent(nameOrExtOrMime, () -> createScript(nameOrExtOrMime));
    }

    public static ScriptEngine getJsEngine() {
        return getScript("graalJs");
    }

    public static String wrapWithIIFE(String script) {
        return String.format("(function (){\n%s\n})()",script);
    }

    public static CompiledScript compile(String script) throws ScriptRuntimeException {
        try {
            return compile(getJsEngine(), script);
        } catch (ScriptException e) {
            throw new ScriptRuntimeException(e);
        }
    }

    public static CompiledScript compile(ScriptEngine engine, String script) throws ScriptException {
        if (engine instanceof Compilable compEngine) {
            return compEngine.compile(script);
        }
        return null;
    }
}
