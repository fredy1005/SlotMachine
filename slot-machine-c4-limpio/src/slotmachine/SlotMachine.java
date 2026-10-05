package slotmachine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;

/** Simulador de una máquina tragamonedas. */
public class SlotMachine {
    public static final int FIRST_WHEEL = 1;
    private static final String[] PALETTE = {"red", "green", "blue", "yellow", "orange",
        "purple", "pink", "cyan", "brown", "gray"};
    private static final int MARGIN = 20, COL_W = 110, CELL = 36;

    private final List<Wheel> wheels = new ArrayList<>();
    private boolean ok = true;
    private boolean visible;
    private JFrame frame;
    private JPanel panel;

    public SlotMachine() { }

    public SlotMachine(int n) {
        Random random = new Random();
        for (int w = 0; w < n; w++) {
            List<String> colors = new ArrayList<>();
            for (int s = 0; s < n; s++) colors.add(color(s, n));
            Collections.shuffle(colors, random);
            Wheel wheel = new NormalWheel();
            for (String c : colors) wheel.add(new NormalSymbol(c));
            wheels.add(wheel);
        }
    }

    private static String color(int i, int n) {
        if (i < PALETTE.length) return PALETTE[i];
        return String.format("#%06x", Color.HSBtoRGB(i / (float) n, 0.8f, 0.9f) & 0xffffff);
    }

    public void addWheel(int pos) { addWheel("normal", pos); }

    public void addWheel(String type, int pos) {
        Wheel wheel = WheelFactory.create(type);
        int index = pos - FIRST_WHEEL;
        if (wheel == null || index < 0 || index > wheels.size()) { ok = false; return; }
        Set<String> colors = new LinkedHashSet<>();
        for (Wheel w : wheels) for (Symbol s : w.symbols()) colors.add(s.color());
        for (String c : colors) wheel.add(new NormalSymbol(c));
        wheels.add(index, wheel);
        done();
    }

    public void delWheel(int pos) {
        Wheel w = wheel(pos);
        if (w == null || !w.canDelete()) { ok = false; return; }
        wheels.remove(w);
        done();
    }

    public void swap(int wheel1, int wheel2) {
        Wheel a = wheel(wheel1), b = wheel(wheel2);
        if (a == null || b == null || !a.canSwap() || !b.canSwap()) { ok = false; return; }
        Collections.swap(wheels, wheel1 - FIRST_WHEEL, wheel2 - FIRST_WHEEL);
        done();
    }

    public int lock(int wheel) {
        Wheel w = wheel(wheel);
        if (w == null || !w.lock()) { ok = false; return -1; }
        done();
        int locked = 0;
        for (Wheel x : wheels) if (x.isLocked()) locked++;
        return locked;
    }

    public void unlock(int wheel) {
        Wheel w = wheel(wheel);
        if (w == null) { ok = false; return; }
        w.unlock();
        done();
    }

    public Wheel wheel(int pos) {
        int index = pos - FIRST_WHEEL;
        return index < 0 || index >= wheels.size() ? null : wheels.get(index);
    }

    public void addSymbol(int pos, String color) { addSymbol("normal", pos, color); }

    public void addSymbol(String type, int pos, String color) {
        Wheel w = wheel(pos);
        Symbol s = color == null || color.isEmpty() ? null : SymbolFactory.create(type, color);
        if (w == null || s == null) { ok = false; return; }
        w.add(s);
        done();
    }

    public void delSymbol(String symbol) {
        boolean removed = false;
        if (symbol != null) for (Wheel w : wheels) removed |= w.remove(symbol);
        ok = removed;
        if (ok) refresh();
    }

    public void placeSymbol(int wheel, String symbol) {
        Wheel w = wheel(wheel);
        if (w == null || symbol == null || !w.place(symbol)) { ok = false; return; }
        done();
    }

    public void spin(int wheel) {
        ok = spinOne(wheel);
        refresh();
    }

    public void spin(int wheel, int steps) {
        boolean all = wheel(wheel) != null && steps >= 0;
        for (int k = 0; all && k < steps; k++) all = spinOne(wheel);
        ok = all;
        refresh();
    }

    public void spin(String[] setSymbols) {
        boolean all = setSymbols != null && setSymbols.length == wheels.size();
        for (int i = 0; all && i < wheels.size(); i++) {
            int wheel = i + FIRST_WHEEL;
            String target = setSymbols[i].toLowerCase();
            int tries = wheels.get(i).symbols().size();
            while (tries-- > 0 && !target.equals(topColor(wheels.get(i)))) {
                if (!spinOne(wheel)) break;
            }
            all = target.equals(topColor(wheels.get(i)));
        }
        ok = all;
        refresh();
    }

    public void spin() {
        for (int i = 0; i < wheels.size(); i++) spinOne(i + FIRST_WHEEL);
        done();
    }

    private boolean spinOne(int wheel) {
        Wheel w = wheel(wheel);
        if (w == null) return false;
        Wheel left = wheel - FIRST_WHEEL > 0 ? wheels.get(wheel - FIRST_WHEEL - 1) : null;
        return w.spin(left);
    }

    public String[] symbols() {
        String[] r = new String[wheels.size()];
        for (int i = 0; i < r.length; i++) r[i] = topColor(wheels.get(i));
        return r;
    }

    public int distinctSymbols() {
        Set<String> colors = new LinkedHashSet<>();
        for (Wheel w : wheels) if (w.top() != null && !w.top().isWild()) colors.add(w.top().color());
        return colors.size();
    }

    public String[] configuration() {
        String[] r = new String[wheels.size()];
        for (int i = 0; i < r.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (Symbol s : wheels.get(i).symbols()) sb.append(sb.length() == 0 ? "" : ",").append(s.color());
            r[i] = sb.toString();
        }
        return r;
    }

    public boolean isJackpot() {
        if (wheels.isEmpty()) return false;
        Symbol reference = null;
        for (Wheel w : wheels) {
            if (w.top() == null) return false;
            if (reference == null && !w.top().isWild()) reference = w.top();
        }
        if (reference == null) return true;
        for (Wheel w : wheels) if (!w.top().matches(reference)) return false;
        return true;
    }

    public void makeVisible() {
        visible = true;
        ok = true;
        if (GraphicsEnvironment.isHeadless()) return;
        if (frame == null) {
            panel = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    draw((Graphics2D) g);
                }
            };
            frame = new JFrame("Slot Machine");
            frame.add(panel);
        }
        refresh();
        frame.setVisible(true);
    }

    public void makeInvisible() {
        visible = false;
        ok = true;
        if (frame != null) frame.setVisible(false);
    }

    public void exit() {
        makeInvisible();
        if (frame != null) { frame.dispose(); frame = null; panel = null; }
    }

    public boolean ok() { return ok; }

    private void done() {
        ok = true;
        refresh();
    }

    private void refresh() {
        if (!visible || frame == null) return;
        int max = maxSymbols();
        panel.setPreferredSize(new java.awt.Dimension(
                Math.max(240, 2 * MARGIN + wheels.size() * COL_W), 2 * MARGIN + max * CELL + 60));
        frame.pack();
        panel.repaint();
    }

    private int maxSymbols() {
        int max = 1;
        for (Wheel w : wheels) max = Math.max(max, w.symbols().size());
        return max;
    }

    private void draw(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.drawString(isJackpot() ? "JACKPOT!" : "", MARGIN, 14);
        int h = maxSymbols() * CELL + 10;
        for (int i = 0; i < wheels.size(); i++) {
            Wheel w = wheels.get(i);
            int x = MARGIN + i * COL_W;
            List<Symbol> ss = w.symbols();
            for (int k = 0; k < ss.size(); k++) {
                int cy = MARGIN + 5 + k * CELL + CELL / 2;
                if (k == 0) {
                    g.setColor(new Color(255, 250, 205));
                    g.fillRect(x + 3, cy - CELL / 2, COL_W - 26, CELL);
                }
                ss.get(k).paint(g, x + (COL_W - 20) / 2, cy);
            }
            w.paintFrame(g, x, MARGIN, COL_W - 20, h);
            g.setColor(w.isLocked() ? Color.RED : Color.BLACK);
            g.drawString((i + FIRST_WHEEL) + ": " + w.type() + (w.isLocked() ? " [locked]" : ""),
                    x, MARGIN + h + 20);
        }
    }

    private static String topColor(Wheel w) {
        return w.top() == null ? "" : w.top().color();
    }
}
