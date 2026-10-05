package slotmachine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

/** Símbolo comodín. */
public class WildSymbol extends Symbol {
    public WildSymbol(String color) { super(color); }

    @Override public String type() { return "wild"; }

    @Override public boolean isWild() { return true; }

    @Override public Symbol copy() { return copyStateTo(new WildSymbol(color())); }

    @Override
    public void paint(Graphics2D g, int cx, int cy) {
        int h = size / 2 + 2;
        Polygon rhombus = new Polygon(new int[] {cx, cx + h, cx, cx - h},
                                      new int[] {cy - h, cy, cy + h, cy}, 4);
        g.setColor(Colors.of(color()));
        g.fillPolygon(rhombus);
        g.setColor(Color.BLACK);
        g.drawPolygon(rhombus);
    }
}
