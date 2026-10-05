package slotmachine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/** Rueda rebelde. */
public class RebelWheel extends Wheel {
    @Override public String type() { return "rebel"; }

    @Override public boolean canLock() { return false; }
    @Override public boolean canSwap() { return false; }
    @Override public boolean canDelete() { return false; }

    @Override
    public void paintFrame(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(6f));
        g.drawRect(x, y, w, h);
        g.setStroke(new BasicStroke(1f));
    }
}
