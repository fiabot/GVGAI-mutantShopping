package tracks.GameMarkovChain;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import tracks.levelGeneration.LevelGenMachine;

public class TestGameGeneration {

    public static String[] generateMarkovChainGames(MarkovChain chain, String folderName, int trials) throws IOException{
        String[] fileNames = new String[trials];
        for(int i = 0; i < trials; i ++){
            String gameFile = folderName + "/game_" + i +".txt"; 
            BufferedWriter writer = new BufferedWriter(new FileWriter(gameFile));
            writer.write(chain.buildGame());

            writer.close();

            fileNames[i] = gameFile;

        }

        return fileNames;
      
    }

    private static String getGameName(String gameFile){
        String name = gameFile; 
        String[] paths = gameFile.split("/"); 
        if(paths.length > 0){
            name = paths[paths.length - 1]; 
        }

        if(name.contains(".txt")){
            name =name.replace(".txt", "");
        }

        return name.strip(); 
    }

    public static HashMap<String, String[]> buildConstructiveLevelsForGames(String[] fileNames, String folder, int numLevels){
        String constructiveLevelGenerator = "tracks.levelGeneration.constructiveLevelGenerator.LevelGenerator";
        HashMap<String, String[]> gameToLevels = new HashMap<String, String[]>();
        for(String gameFile: fileNames){
            System.out.println(gameFile.split("."));
            gameToLevels.put(gameFile, new String[numLevels]); 
            for(int i = 0; i < numLevels; i++){
                String recordLevelFile = folder + "/" + getGameName(gameFile) + "_level" + i + ".txt"; 
                try{
                    if(LevelGenMachine.generateOneLevel(gameFile, constructiveLevelGenerator, recordLevelFile)) {
                        System.out.println("\t Successfully made level" + i +" for "+ gameFile);
                        gameToLevels.get(gameFile)[i] = recordLevelFile;
                    }else{
                        System.out.println("\t failed to make level" + i +" for "+ gameFile);
                    }
                }catch (Exception e){
                    System.out.print("\t ERROR to make level" + i +" for "+ gameFile);
                    System.out.println(e.getMessage());
                }
              
            }
            }   

            return gameToLevels;
    }   

    public static void TestGames(HashMap<String, String[]> filesAndLevels, String resultsFolder, int trialsPerFolder, boolean generated) throws IOException{
        LevelTester.TRIALS = trialsPerFolder;
        
        ArrayList<String> data = new ArrayList<String>(); 
        String header = "game, level, playable, do nothing steps, advanced wins, one step wins, random wins, advanced score, one step score, random score";
        data.add(header); 
        for(String gameFile: filesAndLevels.keySet()){
            for (String levelFile: filesAndLevels.get(gameFile)){
                if(levelFile == null){
                    continue; 
                }
                LevelTester test = new LevelTester(levelFile, gameFile); 

                // can play game 
                if(test.testLevel(generated)){
                    String row = getGameName(gameFile) + "," + getGameName(levelFile) + ", true," +  test.getCSVRow(); 
                    data.add(row); 
                    test.saveResultsAsCsv(resultsFolder + "/" + getGameName(gameFile) + "_" + getGameName(levelFile) + "_results.csv");
                }else{
                    String row = getGameName(gameFile) + "," + getGameName(levelFile) + ", false"; 
                    data.add(row);
                }
                LevelTester.createCSVFile(resultsFolder + "/results.csv" , data);
            }
    

        }

        LevelTester.createCSVFile(resultsFolder + "/results.csv" , data);
    }
    

    public static void main(String[] args) {
        try {
            
            MarkovChain chain = new MarkovChain(); 
            chain.trainFromCsvFile( "examples/all_games_sp.csv");
            HashMap<String, String[]> gameWithLevels; 
            //String[]fileNames= generateMarkovChainGames(chain, "src/tracks/GameMarkovChain/GeneratedGames2", 25); 
            /*String[] fileNames = {"src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_700/game_0.txt", "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_700/game_1.txt", 
            "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_700/game_2.txt", "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_700/game_3.txt",
            "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_700/game_4.txt" };
            gameWithLevels = buildConstructiveLevelsForGames(fileNames, "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_700", 5);
            TestGames(gameWithLevels, "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_700/results", 5, true);


            String[] fileNames2 = {"src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_600/game_0.txt", "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_600/game_1.txt",
                 "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_600/game_2.txt", "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_600/game_3.txt",  
                 "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_600/game_4.txt", "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_600/game_5.txt", 
                 "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_600/game_6.txt", 
                 "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_600/game_7.txt",
                 "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_600/game_8.txt" };
            gameWithLevels = buildConstructiveLevelsForGames(fileNames2, "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_600", 5);
            TestGames(gameWithLevels, "src/tracks/GameMarkovChain/UniqueVAE/Dropout5/Dropout5_600/results", 5, true); 

            String[] fileNames3 = { "src/tracks/GameMarkovChain/UniqueVAE/Dropout3/Dropout3_600/game_3.txt"};
            gameWithLevels = buildConstructiveLevelsForGames(fileNames3, "src/tracks/GameMarkovChain/UniqueVAE/Dropout3/Dropout3_600", 5);
            TestGames(gameWithLevels, "src/tracks/GameMarkovChain/UniqueVAE/Dropout3/Dropout3_600/results", 5, true);*/ 

            String[] fileNames4 = {  "src/tracks/GameMarkovChain/UniqueVAE/Dropout3/Dropout3_700/game_0.txt", 
            "src/tracks/GameMarkovChain/UniqueVAE/Dropout3/Dropout3_700/game_2.txt", 
            "src/tracks/GameMarkovChain/UniqueVAE/Dropout3/Dropout3_700/game_4.txt",     "src/tracks/GameMarkovChain/UniqueVAE/Dropout3/Dropout3_700/game_9.txt", 
             "src/tracks/GameMarkovChain/UniqueVAE/Dropout3/Dropout3_700/game_10.txt",  "src/tracks/GameMarkovChain/UniqueVAE/Dropout3/Dropout3_700/game_11.txt"};
            gameWithLevels = buildConstructiveLevelsForGames(fileNames4, "src/tracks/GameMarkovChain/UniqueVAE/Dropout3/Dropout3_700", 5);
            TestGames(gameWithLevels, "src/tracks/GameMarkovChain/UniqueVAE/Dropout3/Dropout3_700/results", 5, true);

            String[] fileNames5 = {  "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch600/game_0.txt", "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch600/game_1.txt", 
            "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch600/game_2.txt",  "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch600/game_3.txt", 
            "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch600/game_4.txt", "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch600/game_5.txt",
            "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch600/game_6.txt",  "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch600/game_7.txt",
              "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch600/game_9.txt",
            "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch600/game_10.txt", "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch600/game_11.txt"};
            gameWithLevels = buildConstructiveLevelsForGames(fileNames5, "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch600", 5);
            TestGames(gameWithLevels, "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch600/results", 5, true);

            String[] fileNames6 = {  "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch700/game_0.txt", "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch700/game_1.txt", 
            "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch700/game_2.txt",  "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch700/game_3.txt", 
            "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch700/game_4.txt"};
            gameWithLevels = buildConstructiveLevelsForGames(fileNames6, "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch700", 5);
            TestGames(gameWithLevels, "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch700/results", 5, true);

        

            String[] fileNames7 = {  "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch800/game_0.txt",  
            "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch800/game_2.txt"};
            gameWithLevels = buildConstructiveLevelsForGames(fileNames7, "src/tracks/GameMarkovChain/UniqueVAE/Dropout8/Epoch800", 5);
            TestGames(gameWithLevels, "src/tracks/GameMarkovChain/UniqueVAE//Dropout8/Epoch800/results", 5, true);
            
        } catch (IOException e) {
            // TODO Auto-generated catch bloc
            e.printStackTrace();
        } 
    }
}
