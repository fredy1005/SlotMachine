package slotmachine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/** Símbolo efímero. */
public class EphemeralSymbol extends Symbol {
    public static final int STEP = 4;
    public static final int DOT = 2;

    public EphemeralSymbol(String color) { super(color); }

    @Override public String type() { return "ephemeral"; }

    @Override public void onSpin() { size = Math.max(DOT, size - STEP); }

    @Override public Symbol copy() { return copyStateTo(new EphemeralSymbol(color())); }

    @Override
    public void paint(Graphics2D g, int cx, int cy) {
        g.setColor(Color.LIGHT_GRAY);
        g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[] {3f, 3f}, 0f));
        g.drawOval(cx - SIZE / 2, cy - SIZE / 2, SIZE, SIZE);
        g.setStroke(new BasicStroke(1f));
        g.setColor(Colors.of(color()));
        g.fillOval(cx - size / 2, cy - size / 2, size, size);
    }
}
