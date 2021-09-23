package TF2ClassWarsStatTracker.util;

import TF2ClassWarsStatTracker.Mercenary;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConvertJSON {
    public static void main(String[] args) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        Mercenary merc = new Mercenary(Constants.BLU, 4);
        System.out.println(gson.toJson(merc));
    }
}
