package slotmachine;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Rueda de la máquina. */
public abstract class Wheel {
    protected final List<Symbol> symbols = new ArrayList<>();
    private boolean locked;

    public abstract String type();

    public abstract void paintFrame(Graphics2D g, int x, int y, int w, int h);

    public boolean canLock() { return true; }
    public boolean canSwap() { return true; }
    public boolean canDelete() { return true; }

    public boolean lock() {
        if (!canLock()) return false;
        locked = true;
        return true;
    }

    public void unlock() { locked = false; }
    public boolean isLocked() { return locked; }

    public List<Symbol> symbols() { return Collections.unmodifiableList(symbols); }

    public Symbol top() { return symbols.isEmpty() ? null : symbols.get(0); }

    public void add(Symbol s) { symbols.add(s); }

    public boolean remove(String color) {
        String c = color.toLowerCase();
        return symbols.removeIf(s -> s.color().equals(c));
    }

    public boolean spin(Wheel left) {
        if (locked || symbols.isEmpty()) return false;
        step();
        return true;
    }

    public boolean place(String color) {
        if (locked) return false;
        String c = color.toLowerCase();
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).color().equals(c)) {
                Collections.rotate(symbols, -i);
                symbols.get(0).onSelected();
                return true;
            }
        }
        return false;
    }

    protected void step() {
        symbols.add(symbols.remove(0));
        for (Symbol s : symbols) s.onSpin();
    }

    protected void copyFrom(Wheel other) {
        symbols.clear();
        for (Symbol s : other.symbols) symbols.add(s.copy());
    }
}
