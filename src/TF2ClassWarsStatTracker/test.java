package TF2ClassWarsStatTracker;

import TF2ClassWarsStatTracker.util.FileHandler;

import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException {
        FileHandler.readXLFile("Delfys Fun Server Stats.xlsx");
    }
}
