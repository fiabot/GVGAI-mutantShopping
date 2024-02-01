package tracks.levelGeneration.MarkovChains;
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
        char[][] level = generator.generate(100); 
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

    

    public static void playLevel(String filePath, char[][] level, int gameIndex){
        LevelGenerator.saveLevel(filePath, level);
        int gameIdx = gameIndex;
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
        String recordActionsFile = null;
        ArcadeMachine.playOneGame(game, filePath, recordActionsFile, 42);
    }
    public static void main(String[] args) {

        for(int i =0; i < 10; i ++){
            char[][] level = painter(); 
        for(int y = 0; y < level.length; y++){
			for(int x = 0; x <level[0].length; x++){
                System.out.print(level[y][x]); 
            }
            System.out.print("\n"); 
        }

        playLevel("src/tracks/levelGeneration/MarkovChains/painter_" + i, level, 70);
        }
		

    }
}