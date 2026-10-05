package slotmachine;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Pruebas compartidas del ciclo 4. */
public class SlotMachineCC4Test {
    @Test
    public void rebelWheelShouldResistLockSwapAndDelete() {
        SlotMachine m = new SlotMachine();
        m.addWheel("rebel", 1);
        m.addSymbol(1, "red");
        m.addSymbol(1, "blue");
        m.addWheel("normal", 2);

        m.lock(1);
        assertFalse(m.ok());
        m.swap(1, 2);
        assertFalse(m.ok());
        m.delWheel(1);
        assertFalse(m.ok());
        assertEquals(2, m.configuration().length);
        assertEquals("red,blue", m.configuration()[0]);
    }

    @Test
    public void leftyWheelShouldCopyTheWheelOnItsLeftWhenSpinning() {
        SlotMachine m = new SlotMachine();
        m.addWheel(1);
        m.addSymbol(1, "red");
        m.addSymbol(1, "green");
        m.addSymbol(1, "blue");
        m.addWheel("lefty", 2);
        m.spin(1, 2);
        m.spin(2);
        assertTrue(m.ok());
        assertArrayEquals(m.configuration()[0].split(","), m.configuration()[1].split(","));
    }
}
