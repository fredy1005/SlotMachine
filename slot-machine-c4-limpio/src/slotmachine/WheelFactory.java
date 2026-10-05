package slotmachine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Fábrica de ruedas. */
public final class WheelFactory {
    private static final Map<String, Supplier<Wheel>> TYPES = new HashMap<>();
    static {
        register("normal", NormalWheel::new);
        register("lefty", LeftyWheel::new);
        register("rebel", RebelWheel::new);
    }

    private WheelFactory() { }

    public static void register(String type, Supplier<Wheel> constructor) {
        TYPES.put(type.toLowerCase(), constructor);
    }

    public static Wheel create(String type) {
        Supplier<Wheel> s = type == null ? null : TYPES.get(type.toLowerCase());
        return s == null ? null : s.get();
    }
}
