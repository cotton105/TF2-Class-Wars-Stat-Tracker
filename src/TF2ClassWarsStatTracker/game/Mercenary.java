package TF2ClassWarsStatTracker.game;

import java.util.Map;
import java.util.HashMap;

public class Mercenary {
    public static final int
            SCOUT = 0, SOLDIER = 1, PYRO = 2,
            DEMOMAN = 3, HEAVY = 4, ENGINEER = 5,
            MEDIC = 6, SNIPER = 7, SPY = 8;
    private final int team, id;
    private Map<Integer, Integer> winsOverMerc;

    public Mercenary(int team, int id) {
        this.team = team;
        this.id = id;
        winsOverMerc = new HashMap<>();
        loadWins();
    }

    private void loadWins() {
//        winsOverMerc.put(0, 10);
//        winsOverMerc.put(1, 5);
    }
}
