package tracks.levelGeneration.MarkovChains;
import java.io.IOException;
import java.util.ArrayList;

import tools.Utils;
import tracks.ArcadeMachine;


public class Test {
    public static String spGamesCollection =  "examples/all_games_sp.csv";
	public static String[][] games = Utils.readGames(spGamesCollection);

    public static char[][] spaceInvaders(){
        int gameIdx = 0;
		//int level1Idx = 0; // level names from 0 to 4 (game_lvlN.txt).
		String game1Name = games[gameIdx][1];
		String game1 = games[gameIdx][0];
		String level1 = game1.replace(game1Name, game1Name + "_lvl" + 0);
        String level2 = game1.replace(game1Name, game1Name + "_lvl" + 1);
        String level3 = game1.replace(game1Name, game1Name + "_lvl" + 2);
        String level4 = game1.replace(game1Name, game1Name + "_lvl" + 3);
        String level5 = game1.replace(game1Name, game1Name + "_lvl" + 4);

        char[] tiles = new char[] {'.', '0', '1', '2', 'A'}; 

        MarkovRandomField alienFeild = new MarkovRandomField(tiles); 
        alienFeild.train(new String[] {level1, level2, level3, level4, level5});
        LevelGenerator generator = new LevelGenerator(alienFeild); 
        char[][] level = generator.generate(50); 
        return level; 
    }

    public static char[][] roguelike(){
        int gameIdx = 81;
		//int level1Idx = 0; // level names from 0 to 4 (game_lvlN.txt).
		String game1Name = games[gameIdx][1];
		String game1 = games[gameIdx][0];
		String level1 = game1.replace(game1Name, game1Name + "_lvl" + 0);
        String level2 = game1.replace(game1Name, game1Name + "_lvl" + 1);
        String level3 = game1.replace(game1Name, game1Name + "_lvl" + 2);
        String level4 = game1.replace(game1Name, game1Name + "_lvl" + 3);
        String level5 = game1.replace(game1Name, game1Name + "_lvl" + 4);

        char[] tiles = new char[] {'x', 's', 'g', 'r', 'p', 'h', 'k', 'l', 'm', 'A', '.', 'w'};

        MarkovRandomField rogueFeild = new MarkovRandomField(tiles); 
        rogueFeild.train(new String[] {level1, level2, level3, level4, level5});
        LevelGenerator generator = new LevelGenerator(rogueFeild); 
        char[][] level = generator.generate(500); 
        return level; 
    }

    public static char[][] painter(){
        int gameIdx = 70;
		//int level1Idx = 0; // level names from 0 to 4 (game_lvlN.txt).
		String game1Name = games[gameIdx][1];
		String game1 = games[gameIdx][0];
		String level1 = game1.replace(game1Name, game1Name + "_lvl" + 0);
        String level2 = game1.replace(game1Name, game1Name + "_lvl" + 1);
        String level3 = game1.replace(game1Name, game1Name + "_lvl" + 2);
        String level4 = game1.replace(game1Name, game1Name + "_lvl" + 3);
        String level5 = game1.replace(game1Name, game1Name + "_lvl" + 4);

        char[] tiles = new char[] {'x', 'A', '.', 'w'};

        MarkovRandomField rogueFeild = new MarkovRandomField(tiles); 
        rogueFeild.train(new String[] {level1, level2, level3, level4, level5});
        LevelGenerator generator = new LevelGenerator(rogueFeild); 
        char[][] level = generator.generate(500); 
        return level; 
    }

    public static String testLevel(String levelFilePath, String resultFilePath, char[][] level, int gameIdx) throws IOException{
        LevelGenerator.saveLevel(levelFilePath, level);
		//String gameName = games[gameIdx][1];
        String game = games[gameIdx][0];
        LevelTester tester = new LevelTester(levelFilePath, game); 
        tester.printResults();
        tester.saveResultsAsCsv(resultFilePath);
        return tester.getCSVRow(); 
    }

    public static void playLevel(String filePath, char[][] level, int gameIndex){
        LevelGenerator.saveLevel(filePath, level);
        int gameIdx = gameIndex;
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
        String recordActionsFile = null;
        //LevelTester tester = new LevelTester(filePath, game); 
        //tester.printResults();
        ArcadeMachine.playOneGame(game, filePath, recordActionsFile, 42);
    }
    public static void main(String[] args) {
        String folder = "src/tracks/levelGeneration/MarkovChains/InvadersTest3/";
        ArrayList<String> results = new ArrayList<String>(); 
        String header = "Do Nothing Steps, Advanced Win Rate, Advanced Score, One Step Score, Random Score"; 
        results.add(header); 

        for(int i =0; i < 10; i ++){
            char[][] level = spaceInvaders(); 
        for(int y = 0; y < level.length; y++){
			for(int x = 0; x <level[0].length; x++){
                System.out.print(level[y][x]); 
            }
            System.out.print("\n"); 
        }

            try{
                results.add(testLevel(folder + "level_" + i +".txt", folder + "result_" + i +".csv", level, 0));
            }catch (IOException e){
                System.out.println("Failed to create level CSV"); 
            }
        }
		
        try{
            LevelTester.createCSVFile(folder + "results.csv", results);
        }catch (IOException e){
            System.out.println("Failed to create CSV"); 
        }
        
    

    }
}
