package slotmachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Resuelve y simula la máquina. */
public class SlotMachineContest {
    private static final long PAUSE_MS = 600;

    private SlotMachineContest() { }

    public static int[][] solve(int n) {
        return solve(new SlotMachine(n));
    }

    public static int[][] solve(SlotMachine machine) {
        String[] config = machine.configuration();
        List<int[]> actions = new ArrayList<>();
        if (config.length == 0) return new int[0][];
        String target = config[0].split(",")[0];
        for (int i = 1; i < config.length; i++) {
            int steps = Arrays.asList(config[i].split(",")).indexOf(target);
            if (steps < 0) return null;
            if (steps > 0) actions.add(new int[] {i + SlotMachine.FIRST_WHEEL, steps});
        }
        return actions.toArray(new int[0][]);
    }

    public static void simulate(int n) {
        SlotMachine machine = new SlotMachine(n);
        int[][] actions = solve(machine);
        machine.makeVisible();
        pause();
        for (int[] a : actions) {
            machine.spin(a[0], a[1]);
            pause();
        }
        System.out.println("jackpot: " + machine.isJackpot());
    }

    private static void pause() {
        try {
            Thread.sleep(PAUSE_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
