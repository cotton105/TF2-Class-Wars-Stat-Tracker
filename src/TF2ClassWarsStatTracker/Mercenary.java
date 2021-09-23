package TF2ClassWarsStatTracker;

import java.util.Map;
import java.util.HashMap;

public class Mercenary {
    private final int team, id;
    private Map<Integer, Integer> winsOverMerc;

    public Mercenary(int team, int id) {
        this.team = team;
        this.id = id;
        winsOverMerc = new HashMap<>();
        loadWins();
    }

    private void loadWins() {
        winsOverMerc.put(0, 10);
        winsOverMerc.put(1, 5);
    }
}
