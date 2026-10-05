package slotmachine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** Fábrica de símbolos. */
public final class SymbolFactory {
    private static final Map<String, Function<String, Symbol>> TYPES = new HashMap<>();
    static {
        register("normal", NormalSymbol::new);
        register("ephemeral", EphemeralSymbol::new);
        register("shy", ShySymbol::new);
        register("wild", WildSymbol::new);
    }

    private SymbolFactory() { }

    public static void register(String type, Function<String, Symbol> constructor) {
        TYPES.put(type.toLowerCase(), constructor);
    }

    public static Symbol create(String type, String color) {
        Function<String, Symbol> c = type == null ? null : TYPES.get(type.toLowerCase());
        return c == null ? null : c.apply(color);
    }
}
