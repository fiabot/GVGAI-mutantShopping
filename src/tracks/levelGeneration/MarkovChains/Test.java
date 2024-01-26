package tracks.levelGeneration.MarkovChains;
import tools.Utils;

public class Test {
    public static void main(String[] args) {
        String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);


		// Game 1 and level 1 to play
		int gameIdx = 0;
		int level1Idx = 0; // level names from 0 to 4 (game_lvlN.txt).
		String game1Name = games[gameIdx][1];
		String game1 = games[gameIdx][0];
		String level1 = game1.replace(game1Name, game1Name + "_lvl" + level1Idx);
        String level2 = game1.replace(game1Name, game1Name + "_lvl" + 1);
        String level3 = game1.replace(game1Name, game1Name + "_lvl" + 2);
        String level4 = game1.replace(game1Name, game1Name + "_lvl" + 3);
        String level5 = game1.replace(game1Name, game1Name + "_lvl" + 4);

        char[] tiles = new char[] {'.', '0', '1', '2', 'A'}; 

        MarkovRandomField alienFeild = new MarkovRandomField(tiles); 
        alienFeild.train(new String[] {level1, level2, level3, level4, level5});
        /*for(String key : alienFeild.probMap.keySet()){
            System.out.print(key + ":"); 
            System.out.print(alienFeild.probMap.get(key)[0] + ",");
            System.out.print(alienFeild.probMap.get(key)[1] + ",");
            System.out.print(alienFeild.probMap.get(key)[2] + ",");
            System.out.print(alienFeild.probMap.get(key)[3] + ",");
            System.out.print(alienFeild.probMap.get(key)[4]);
            System.out.print("\n"); 
        }*/ 
        System.out.println(alienFeild.getProbability('A', "...."));
    }
}