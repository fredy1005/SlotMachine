package slotmachine;

import java.awt.Graphics2D;

/** Símbolo de una rueda. */
public abstract class Symbol {
    public static final int SIZE = 28;

    private final String color;
    protected int size = SIZE;
    protected boolean visible = true;

    protected Symbol(String color) {
        this.color = color.toLowerCase();
    }

    public String color() { return color; }
    public int size() { return size; }
    public boolean isVisible() { return visible; }

    public boolean isWild() { return false; }

    public boolean matches(Symbol other) {
        return isWild() || other.isWild() || color.equals(other.color);
    }

    public void onSpin() { }

    public void onSelected() { }

    public abstract String type();

    public abstract Symbol copy();

    public abstract void paint(Graphics2D g, int cx, int cy);

    protected Symbol copyStateTo(Symbol target) {
        target.size = size;
        target.visible = visible;
        return target;
    }

    @Override
    public String toString() { return color; }
}
