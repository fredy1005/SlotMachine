package slotmachine;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Pruebas de unidad del ciclo 4. */
public class SlotMachineC4Test {
    private static SlotMachine machine(String... wheelTypes) {
        SlotMachine m = new SlotMachine();
        for (int i = 0; i < wheelTypes.length; i++) {
            m.addWheel(wheelTypes[i], i + SlotMachine.FIRST_WHEEL);
            m.addSymbol(i + SlotMachine.FIRST_WHEEL, "red");
            m.addSymbol(i + SlotMachine.FIRST_WHEEL, "green");
            m.addSymbol(i + SlotMachine.FIRST_WHEEL, "blue");
        }
        return m;
    }

    @Test
    public void normalWheelShouldLockAndUnlock() {
        SlotMachine m = machine("normal");
        assertEquals(1, m.lock(1));
        assertTrue(m.ok());
        m.spin(1);
        assertFalse(m.ok());
        assertEquals("red", m.symbols()[0]);
        m.unlock(1);
        m.spin(1);
        assertTrue(m.ok());
        assertEquals("green", m.symbols()[0]);
    }

    @Test
    public void rebelWheelShouldNotLock() {
        SlotMachine m = machine("rebel");
        assertEquals(-1, m.lock(1));
        assertFalse(m.ok());
        m.spin(1);
        assertTrue(m.ok());
    }

    @Test
    public void rebelWheelShouldNotSwapNorDelete() {
        SlotMachine m = machine("rebel", "normal");
        m.swap(1, 2);
        assertFalse(m.ok());
        assertEquals("rebel", m.wheel(1).type());
        m.delWheel(1);
        assertFalse(m.ok());
        assertNotNull(m.wheel(2));
        m.delWheel(2);
        assertTrue(m.ok());
        assertNull(m.wheel(2));
    }

    @Test
    public void normalWheelsShouldSwap() {
        SlotMachine m = machine("normal", "lefty");
        m.swap(1, 2);
        assertTrue(m.ok());
        assertEquals("lefty", m.wheel(1).type());
        assertEquals("normal", m.wheel(2).type());
    }

    @Test
    public void leftyWheelShouldCopyItsLeftNeighbour() {
        SlotMachine m = machine("normal", "lefty");
        m.spin(1, 2);
        m.spin(2);
        assertArrayEquals(m.configuration()[0].split(","), m.configuration()[1].split(","));
        assertEquals("blue", m.symbols()[1]);
    }

    @Test
    public void leftyWheelWithoutLeftNeighbourShouldSpinNormally() {
        SlotMachine m = machine("lefty");
        m.spin(1);
        assertEquals("green", m.symbols()[0]);
    }

    @Test
    public void lockedLeftyWheelShouldNotCopy() {
        SlotMachine m = machine("normal", "lefty");
        m.lock(2);
        m.spin(1);
        m.spin(2);
        assertFalse(m.ok());
        assertEquals("red", m.symbols()[1]);
    }

    @Test
    public void unknownTypesShouldFail() {
        SlotMachine m = machine("normal");
        m.addWheel("flying", 1);
        assertFalse(m.ok());
        m.addSymbol("ghost", 1, "pink");
        assertFalse(m.ok());
        assertEquals(1, m.configuration().length);
    }

    @Test
    public void ephemeralSymbolShouldShrinkUntilDot() {
        SlotMachine m = new SlotMachine();
        m.addWheel(1);
        m.addSymbol("ephemeral", 1, "red");
        Symbol s = m.wheel(1).top();
        assertEquals(Symbol.SIZE, s.size());
        m.spin(1);
        assertEquals(Symbol.SIZE - EphemeralSymbol.STEP, s.size());
        m.spin(1, 100);
        assertEquals(EphemeralSymbol.DOT, s.size());
        assertTrue(s.isVisible());
    }

    @Test
    public void normalSymbolShouldKeepItsSize() {
        SlotMachine m = machine("normal");
        m.spin(1, 10);
        for (Symbol s : m.wheel(1).symbols()) assertEquals(Symbol.SIZE, s.size());
    }

    @Test
    public void shySymbolShouldToggleVisibilityWhenSelected() {
        SlotMachine m = new SlotMachine();
        m.addWheel(1);
        m.addSymbol("shy", 1, "red");
        m.addSymbol(1, "blue");
        Symbol shy = m.wheel(1).top();
        assertTrue(shy.isVisible());
        m.placeSymbol(1, "red");
        assertFalse(shy.isVisible());
        m.placeSymbol(1, "red");
        assertTrue(shy.isVisible());
        m.spin(1);
        assertTrue(shy.isVisible());
    }

    @Test
    public void placeSymbolShouldFailIfMissingOrLocked() {
        SlotMachine m = machine("normal");
        m.placeSymbol(1, "pink");
        assertFalse(m.ok());
        m.lock(1);
        m.placeSymbol(1, "blue");
        assertFalse(m.ok());
    }

    @Test
    public void wildSymbolShouldMatchAnyColorInJackpot() {
        SlotMachine m = new SlotMachine();
        m.addWheel(1);
        m.addWheel(2);
        m.addSymbol(1, "red");
        m.addSymbol("wild", 2, "gold");
        assertTrue(m.isJackpot());
        assertEquals(1, m.distinctSymbols());
        m.addWheel(3);
        m.addSymbol(3, "blue");
        m.placeSymbol(3, "blue");
        assertFalse(m.isJackpot());
        assertEquals(2, m.distinctSymbols());
    }

    @Test
    public void everyTypeShouldHaveItsOwnName() {
        assertNotEquals(new NormalWheel().type(), new LeftyWheel().type());
        assertNotEquals(new LeftyWheel().type(), new RebelWheel().type());
        assertNotEquals(new NormalWheel().type(), new RebelWheel().type());
        String[] names = {new NormalSymbol("red").type(), new EphemeralSymbol("red").type(),
            new ShySymbol("red").type(), new WildSymbol("red").type()};
        for (int i = 0; i < names.length; i++)
            for (int j = i + 1; j < names.length; j++) assertNotEquals(names[i], names[j]);
    }

    @Test
    public void newWheelTypeCanBeRegisteredWithoutChangingTheMachine() {
        WheelFactory.register("stubborn", RebelWheel::new);
        SlotMachine m = new SlotMachine();
        m.addWheel("stubborn", 1);
        assertTrue(m.ok());
        m.delWheel(1);
        assertFalse(m.ok());
    }

    @Test
    public void solveShouldAlwaysReachJackpot() {
        for (int n = 1; n <= 12; n++) {
            SlotMachine m = new SlotMachine(n);
            for (int[] a : SlotMachineContest.solve(m)) m.spin(a[0], a[1]);
            assertTrue("n=" + n, m.isJackpot());
        }
    }

    @Test
    public void randomMachineShouldHaveNWheelsWithNDistinctSymbols() {
        SlotMachine m = new SlotMachine(5);
        assertEquals(5, m.configuration().length);
        for (String wheel : m.configuration()) assertEquals(5, wheel.split(",").length);
        assertTrue(m.distinctSymbols() >= 1 && m.distinctSymbols() <= 5);
    }

    @Test
    public void spinToSetSymbolsShouldShowThem() {
        SlotMachine m = machine("normal", "normal");
        m.spin(new String[] {"blue", "green"});
        assertTrue(m.ok());
        assertArrayEquals(new String[] {"blue", "green"}, m.symbols());
        m.spin(new String[] {"pink", "green"});
        assertFalse(m.ok());
    }
}
