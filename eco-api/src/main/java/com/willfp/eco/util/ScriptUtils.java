package com.willfp.eco.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Utilities / API methods for kotlin scripts.
 */
public final class ScriptUtils {
    /**
     * The cache of compiled scripts.
     */
    private static final Cache<ScriptToCompile, CompiledScript> COMPILED_SCRIPTS = Caffeine.newBuilder()
            .build();

    /**
     * Empty (dummy) compiled script.
     */
    private static final CompiledScript EMPTY_SCRIPT = new CompiledScript() {
        @Override
        public Object eval(@NotNull final ScriptContext context) {
            return null;
        }

        @Override
        public ScriptEngine getEngine() {
            return ScriptUtils.getEngine();
        }
    };

    /**
     * Evaluate a script.
     *
     * @param script The script.
     * @return The return value of the script.
     */
    @Nullable
    public static Object eval(@NotNull final String script) {
        return eval(script, (x) -> {
            // Do nothing.
        });
    }

    /**
     * Evaluate a script.
     *
     * @param script          The script.
     * @param bindingsFactory The consumer to create bindings.
     * @return The return value of the script.
     */
    @Nullable
    public static Object eval(@NotNull final String script,
                              @NotNull final Consumer<Bindings> bindingsFactory) {
        Bindings toHash = getEngine().createBindings();
        bindingsFactory.accept(toHash);
        Map<String, Object> bindingsMap = new HashMap<>(toHash);

        try {
            return COMPILED_SCRIPTS.get(new ScriptToCompile(script, bindingsMap), (it) -> {
                try {
                    var engine = getEngine();
                    Bindings bindings = engine.createBindings();
                    bindingsFactory.accept(bindings);
                    engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
                    return engine.compile(it.script);
                } catch (ScriptException e) {
                    e.printStackTrace();
                    return EMPTY_SCRIPT;
                }
            }).eval();
        } catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get new engine instance.
     *
     * @param <T> Intersection type.
     * @return The engine.
     */
    @SuppressWarnings("unchecked")
    private static <T extends ScriptEngine & Compilable> T getEngine() {
        return (T) new ScriptEngineManager().getEngineByExtension("kts");
    }

    /**
     * Metadata to compile a script.
     *
     * @param script   The script.
     * @param bindings The bindings.
     */
    private record ScriptToCompile(@NotNull String script,
                                   @NotNull Map<String, Object> bindings) {

    }

    private ScriptUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
