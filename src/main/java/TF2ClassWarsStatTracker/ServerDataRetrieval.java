package TF2ClassWarsStatTracker;

import TF2ClassWarsStatTracker.util.Print;
import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.SteamPlayer;
import com.github.koraktor.steamcondenser.steam.servers.SourceServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

public class ServerDataRetrieval implements Runnable {
    private SourceServer server;
    public static final String SERVER_IP = "31.186.251.170";
    public static final int PORT = 27015;
    private static final int POLLING_RATE = 5000;

    public ServerDataRetrieval() {
        Thread thread = new Thread(this, "Server Data Retrieval");
        thread.start();
    }

    @Override
    public void run() {
        try {
            server = new SourceServer(SERVER_IP, PORT);
            server.initialize();
            Timer timer = new Timer();
            TimerTask serverPoll = new TimerTask() {
                @Override
                public void run() {
                    try {
                        updateServerData();
//                        listInfo();
//                        listRules();
//                        listPlayers();
                    } catch (SteamCondenserException | TimeoutException ex) {
                        ex.printStackTrace();
                    }
                }
            };
            timer.schedule(serverPoll, 0, POLLING_RATE);
        } catch (SteamCondenserException | TimeoutException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void updateServerData() throws SteamCondenserException, TimeoutException {
        server.updateServerInfo();
        server.updateRules();
        server.updatePlayers();
    }

    private void listInfo() throws SteamCondenserException, TimeoutException {
        Map<String, Object> info = server.getServerInfo();
        if (info != null)
            info.forEach((k, v) -> Print.print(k + " " + v));
        else
            Print.error("ServerInfo is null.");
    }

    private void listRules() throws SteamCondenserException, TimeoutException {
        Map<String, String> rules = server.getRules();
        if (rules != null)
            rules.forEach((k, v) -> Print.print(k + " " + v));
        else
            Print.error("ServerRules is null.");
    }

    private void listPlayers() throws SteamCondenserException, TimeoutException {
        HashMap<String, SteamPlayer> players = server.getPlayers();
        if (players != null)
            players.forEach((k, v) -> Print.print(k + " " + v));
        else
            Print.error("ServerPlayers is null.");
    }

    public int ping() throws SteamCondenserException, TimeoutException {
        return server.getPing();
    }

    public String getGameTrackerServerBannerIframe() {
        return String.format(
                "<html>https://cache.gametracker.com/components/html0/?host=%s:%d&bgColor=333333&fontColor=cccccc&titleBgColor=222222&titleColor=ff9900&borderColor=555555&linkColor=ffcc00&borderLinkColor=222222&showMap=1&currentPlayersHeight=100&showCurrPlayers=1&topPlayersHeight=100&showTopPlayers=1&showBlogs=0&width=240</html>",
                SERVER_IP, PORT
        );
    }

    public static String getGameTrackerServerBannerIframe(String ip, int port, int width) {
        return String.format(
                "https://cache.gametracker.com/components/html0/?host=%s:%d&bgColor=333333&fontColor=cccccc&titleBgColor=222222&titleColor=ff9900&borderColor=555555&linkColor=ffcc00&borderLinkColor=222222&showMap=1&currentPlayersHeight=100&showCurrPlayers=1&topPlayersHeight=100&showTopPlayers=0&showBlogs=0&width=%d",
                ip, port, width
        );
    }

    public static String getGameTrackerServerBannerImage(String ip, int port, double scale) {
        int width = (int)(160 * scale);
        int height = (int)(248 * scale);
        return String.format(
                "<html><a href=\"https://www.gametracker.com/server_info/%s:%d/\" target=\"_blank\"><img src=\"https://cache.gametracker.com/server_info/%s:%d/b_160_400_1_ffffff_c5c5c5_ffffff_000000_0_1_0.png\" border=\"0\" width=\"%d\" height=\"%d\" alt=\"\"/></a></html>",
                ip, port, ip, port, width, height
        );
    }
}
