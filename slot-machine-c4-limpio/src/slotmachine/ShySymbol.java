package slotmachine;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

/** Símbolo tímido. */
public class ShySymbol extends Symbol {
    public ShySymbol(String color) { super(color); }

    @Override public String type() { return "shy"; }

    @Override public void onSelected() { visible = !visible; }

    @Override public Symbol copy() { return copyStateTo(new ShySymbol(color())); }

    @Override
    public void paint(Graphics2D g, int cx, int cy) {
        g.setColor(Colors.of(color()));
        if (visible) {
            g.fillRoundRect(cx - size / 2, cy - size / 2, size, size, 8, 8);
        } else {
            g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10f, new float[] {2f, 3f}, 0f));
            g.drawRoundRect(cx - size / 2, cy - size / 2, size, size, 8, 8);
            g.setStroke(new BasicStroke(1f));
        }
    }
}
