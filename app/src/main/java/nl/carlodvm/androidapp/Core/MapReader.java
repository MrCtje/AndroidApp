package nl.carlodvm.androidapp.Core;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MapReader {
    //Had wereldkaart gwn in /sdcard/ gezet dat was dit pad voor me
    private final String FILEPATH = Environment.getExternalStorageDirectory() + "/CarloDVM/";
    private final String FILENAME = "begane-grond-kaart.txt";
    private final String FULLPATH = FILEPATH + FILENAME;

    private BufferedReader br = null;
    private FileReader fr = null;

    private int allX;
    private int allY;
    private int destinationLineNumber;
    private int destinationCount;

    public ArrayList<Grid> GridList = new ArrayList<>();
    public ArrayList<Destination> destinations = new ArrayList<>();

    public World readFile(Activity activity) {
        try {
            FileManager fm = new FileManager(activity);
            if (fm.isExternalStorageWritable() && !fm.fileExists(FILEPATH, FILENAME)) {
                File outputDir = fm.createFile(FILEPATH);
                outputDir.mkdirs();
                FileManager.copy(activity.getAssets().open(FILENAME), new FileOutputStream(FULLPATH));
            }

            fr = new FileReader(FULLPATH);
            br = new BufferedReader(fr);

            String sCurrentLine;

            int lineCount = 0;

            while ((sCurrentLine = br.readLine()) != null) {

                if (lineCount == 0) {
                    String mapCoords[] = sCurrentLine.split(",");
                    allX = Integer.parseInt(mapCoords[0]);
                    allY = Integer.parseInt(mapCoords[1]);
                    destinationLineNumber = allY + 1;
                    lineCount++;
                    continue;
                }

                if (sCurrentLine.length() == allX) {
                    createGrids(sCurrentLine, lineCount);
                    lineCount++;
                    continue;
                }

                if (lineCount == destinationLineNumber) {
                    destinationCount = Integer.parseInt(sCurrentLine);
                    lineCount++;
                    continue;
                }

                if (lineCount > destinationLineNumber && lineCount < destinationLineNumber + destinationCount) {
                    createDestination(sCurrentLine, lineCount);
                }
            }


        } catch (IOException e) {
            Log.e("MapReader", e.getMessage());
        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        World world = new World(GridList, allX, allY);
        world.setDestinations(destinations);
        return world;
    }

    private void createDestination(String input, int lineCount) {
        String[] param = input.split(",");
        destinations.add(new Destination(Integer.parseInt(param[0]) - 1, Integer.parseInt(param[1]) - 1, param[2], Integer.parseInt(param[3]), param[4]));
    }

    private void createGrids(String nextLine, int lineCount) {
        int y = allY - lineCount;
        int x;
        for (int i = 0; i < nextLine.length(); i++) {
            x = i;

            if (nextLine.charAt(i) == 'N') {
                GridList.add(new Grid(x, y, false));
            }

            if (nextLine.charAt(i) == 'W') {
                GridList.add(new Grid(x, y, true));
            }
        }
    }

}
