package slotmachine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/** Rueda zurda. */
public class LeftyWheel extends Wheel {
    @Override public String type() { return "lefty"; }

    @Override
    public boolean spin(Wheel left) {
        if (isLocked()) return false;
        if (left == null || left.symbols.isEmpty()) return super.spin(null);
        copyFrom(left);
        return true;
    }

    @Override
    public void paintFrame(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[] {6f, 4f}, 0f));
        g.drawRect(x, y, w, h);
        g.setStroke(new BasicStroke(1f));
    }
}
