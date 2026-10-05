package slotmachine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/** Rueda normal. */
public class NormalWheel extends Wheel {
    @Override public String type() { return "normal"; }

    @Override
    public void paintFrame(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2f));
        g.drawRect(x, y, w, h);
        g.setStroke(new BasicStroke(1f));
    }
}
