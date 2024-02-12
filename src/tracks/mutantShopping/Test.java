package tracks.mutantShopping;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;


import javax.swing.*;
import java.awt.*;
import java.util.*;
/**
 * Created with IntelliJ IDEA. User: Diego Date: 04/10/13 Time: 16:29 This is a
 * Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test {
    public static void createFrame(){
		JFrame frame = new JFrame("Mutant Shopping"); 
		ScrollPane scroll = new ScrollPane(); 
		JPanel panel = new JPanel(); 
		scroll.add(panel);
		panel.setLayout(new GridLayout(2,2, 10, 10)); 
		frame.add(scroll); 
		frame.setSize(2000, 1000);
		frame.setVisible(true);

		// Available tracks:
		String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
		String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
		String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";

		String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
        String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
        String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
		String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";

		//Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

		//Game settings
		boolean visuals = true;
		int seed = new Random().nextInt();

		// Game 1 and level 1 to play
		int game1Idx = 0;
		int level1Idx = 0; // level names from 0 to 4 (game_lvlN.txt).
		String game1Name = games[game1Idx][1];
		String game1 = games[game1Idx][0];
		String level1 = game1.replace(game1Name, game1Name + "_lvl" + level1Idx);


		// Game 1  and level 1 to play
		int game2Idx = 0;
		int level2Idx = 1; // level names from 0 to 4 (game_lvlN.txt).
		String game2Name = games[game2Idx][1];
		String game2 = games[game2Idx][0];
		String level2 = game2.replace(game2Name, game2Name + "_lvl" + level2Idx);

		// Game 1  and level 1 to play
		int game3Idx = 0;
		int level3Idx = 2; // level names from 0 to 4 (game_lvlN.txt).
		String game3Name = games[game3Idx][1];
		String game3 = games[game3Idx][0];
		String level3 = game3.replace(game3Name, game3Name + "_lvl" + level3Idx);

		// Game 1  and level 1 to play
		int game4Idx = 0;
		int level4Idx = 3; // level names from 0 to 4 (game_lvlN.txt).
		String game4Name = games[game3Idx][1];
		String game4 = games[game4Idx][0];
		String level4 = game3.replace(game4Name, game4Name + "_lvl" + level4Idx);



		String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
						// + levelIdx + "_" + seed + ".txt";
						// where to record the actions
						// executed. null if not to save.
		String recordActionsFile2 = null;
		// 1. This starts a game, in a level, played by a human.
		//ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);

		// 2. This plays a game in a level by the controller.
		RunGame runnable1 = new RunGame(game1, level1, visuals, sampleRHEAController, recordActionsFile, seed, 0, panel, frame); 
		RunGame runnable2 = new RunGame(game2, level2, visuals, sampleRHEAController, recordActionsFile2, 42, 0, panel, frame); 
		Thread thread1 = new Thread(runnable1); 
		Thread thread2 = new Thread(runnable2); 

		RunGame runnable3 = new RunGame(game3, level3, visuals, sampleRHEAController, recordActionsFile, seed, 0, panel, frame); 
		RunGame runnable4 = new RunGame(game4, level4, visuals, sampleRHEAController, recordActionsFile2, 42, 0, panel, frame); 
		Thread thread3 = new Thread(runnable3); 
		Thread thread4 = new Thread(runnable4); 

		

		thread1.start();

		try {
			TimeUnit.MILLISECONDS.sleep(400);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		thread2.start();

		try {
			TimeUnit.MILLISECONDS.sleep(400);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		thread3.start();

		try {
			TimeUnit.MILLISECONDS.sleep(400);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		thread4.start();


	
	}


	public static void testPlayView(){
		JFrame frame = new JFrame("Mutant Shopping");
		frame.setSize(2000, 1000);
		frame.setVisible(true);

		// Available tracks:
		String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
		String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
		String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";

		String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
        String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
        String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
		String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";

		//Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

		//Game settings
		boolean visuals = true;
		int seed = new Random().nextInt();

		// Game 1 and level 1 to play
		int gameIdx = 0;
		int level1Idx = 0; // level names from 0 to 4 (game_lvlN.txt).
		String game1Name = games[gameIdx][1];
		String game1 = games[gameIdx][0];
		String level1 = game1.replace(game1Name, game1Name + "_lvl" + level1Idx);


		// Game 1  and level 1 to play
	
		int level2Idx = 1; // level names from 0 to 4 (game_lvlN.txt).
		String game2Name = games[gameIdx][1];
		String game2 = games[gameIdx][0];
		String level2 = game2.replace(game2Name, game2Name + "_lvl" + level2Idx);

		// Game 1  and level 1 to play
		int level3Idx = 2; // level names from 0 to 4 (game_lvlN.txt).
		String game3Name = games[gameIdx][1];
		String game3 = games[gameIdx][0];
		String level3 = game3.replace(game3Name, game3Name + "_lvl" + level3Idx);

		// Game 1  and level 1 to play
	
		int level4Idx = 3; // level names from 0 to 4 (game_lvlN.txt).
		String game4Name = games[gameIdx][1];
		String game4 = games[gameIdx][0];
		String level4 = game3.replace(game4Name, game4Name + "_lvl" + level4Idx);



		String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
						// + levelIdx + "_" + seed + ".txt";
						// where to record the actions
						// executed. null if not to save.
		String[] game_list = {game1, game2, game3, game4}; 
		String[] levels = {level1, level2, level3, level4}; 
		GameGridDisplay grid = new GameGridDisplay(game_list, levels, sampleOLETSController, frame.getContentPane(), frame, seed);
		/*GamePlayView view1 = new GamePlayView(game1, level1, sampleOLETSController, panel, grid); 
		GamePlayView view2 = new GamePlayView(game2, level2, sampleOLETSController, panel, grid); 
		GamePlayView view3 = new GamePlayView(game3, level3, sampleOLETSController, panel, grid); 
		GamePlayView view4 = new GamePlayView(game4, level4, sampleOLETSController, panel, grid);*/  
	
	}

	public static void simpleGame(){
		JFrame frame = new JFrame("Mutant Shopping");
		frame.setSize(1000, 800);
		frame.setVisible(true);
		int seed = 42; 

		// Available tracks:
		String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
		String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
		String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";

		String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
        String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
        String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
		String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";
		String game = "src/tracks/mutantShopping/SimpleGame.txt"; 
		String level = "src/tracks/mutantShopping/SimpleLevel.txt"; 
<<<<<<< HEAD
		String[] game_list = {game, game, game, game}; 
		String[] levels = {level, level, level, level}; 
		GameGridDisplay grid = new GameGridDisplay(game_list, levels, sampleRHEAController, frame.getContentPane(), frame, seed);
=======
		String[] game_list = {game}; 
		String[] levels = {level}; 
		GameGridDisplay grid = new GameGridDisplay(game_list, levels, sampleRSController, frame.getContentPane(), frame, seed);
>>>>>>> grid-view

	}


    public static void main(String[] args) {

		//testPlayView();
		simpleGame();

		// Available tracks:
		/*String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
		String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
		String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";

		String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
        String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
        String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
		String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";

		//Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

		//Game settings
		boolean visuals = true;
		int seed = new Random().nextInt();

		// Game and level to play
		int gameIdx = 12;
		int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
		String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);

		String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
						// + levelIdx + "_" + seed + ".txt";
						// where to record the actions
						// executed. null if not to save.

		// 1. This starts a game, in a level, played by a human.
		//ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);

		// 2. This plays a game in a level by the controller.
		ArcadeMachine.runOneGame(game, level1, visuals, sampleRHEAController, recordActionsFile, seed, 0); */


		// 3. This replays a game from an action file previously recorded
	//	 String readActionsFile = recordActionsFile;
	//	 ArcadeMachine.replayGame(game, level1, visuals, readActionsFile);

		// 4. This plays a single game, in N levels, M times :
//		String level2 = new String(game).replace(gameName, gameName + "_lvl" + 1);
//		int M = 10;
//		for(int i=0; i<games.length; i++){
//			game = games[i][0];
//			gameName = games[i][1];
//			level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
//			ArcadeMachine.runGames(game, new String[]{level1}, M, sampleMCTSController, null);
//		}

		//5. This plays N games, in the first L levels, M times each. Actions to file optional (set saveActions to true).
//		int N = games.length, L = 2, M = 1;
//		boolean saveActions = false;
//		String[] levels = new String[L];
//		String[] actionFiles = new String[L*M];
//		for(int i = 0; i < N; ++i)
//		{
//			int actionIdx = 0;
//			game = games[i][0];
//			gameName = games[i][1];
//			for(int j = 0; j < L; ++j){
//				levels[j] = game.replace(gameName, gameName + "_lvl" + j);
//				if(saveActions) for(int k = 0; k < M; ++k)
//				actionFiles[actionIdx++] = "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
//			}
//			ArcadeMachine.runGames(game, levels, M, sampleRHEAController, saveActions? actionFiles:null);
//		}


    }
}
