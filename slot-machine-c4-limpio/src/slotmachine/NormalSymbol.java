package slotmachine;

import java.awt.Graphics2D;

/** Símbolo normal. */
public class NormalSymbol extends Symbol {
    public NormalSymbol(String color) { super(color); }

    @Override public String type() { return "normal"; }

    @Override public Symbol copy() { return copyStateTo(new NormalSymbol(color())); }

    @Override
    public void paint(Graphics2D g, int cx, int cy) {
        g.setColor(Colors.of(color()));
        g.fillOval(cx - size / 2, cy - size / 2, size, size);
    }
}
