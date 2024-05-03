package tracks.GameMarkovChain;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import tracks.ArcadeMachine;
import tracks.levelGeneration.LevelGenMachine;

public class Test {

    public static void main(String[] args) throws IOException {
        // 

        MarkovChain chain = new MarkovChain(); 
        chain.trainFromCsvFile( "examples/all_games_sp.csv");


        System.out.println("GAME 1");
        String game = chain.buildGame(); 
        System.out.println(game);

        System.out.println("\n\n");
        game = chain.mutateGame(game);
        System.out.println(game);

        System.out.println("\n\n");
        game = chain.mutateGame(game);
        System.out.println(game);

        System.out.println("\n\n");
        game = chain.mutateGame(game);
        System.out.println(game);



        /*String game = MarkovChain.getFileAsString("src/tracks/GameMarkovChain/EvolutionGames/BasicTest8/game_0.txt");
        String level = MarkovChain.getFileAsString("src/tracks/GameMarkovChain/EvolutionGames/BasicTest8/game_0_level_0.txt"); 
        MutantInterface mutant = new GrammarMutant(game, level , new MarkovChain()); 
        mutant.getFitness(); */ 
        //ArcadeMachine.playOneGame("src/tracks/GameMarkovChain/EvolutionGames/BasicTest8/game_0.txt", "src/tracks/GameMarkovChain/EvolutionGames/BasicTest5/game_8_level_0.txt", null, 0);  

        /*String folder = "src/tracks/GameMarkovChain/EvolutionGames/NFeasTesst"; 
        String historyFile = folder + "/history50.txt";  

        MarkovChain chain = new MarkovChain(); 
        chain.trainFromCsvFile( "examples/all_games_sp.csv");
        SimpleMutantConstructor constructor = new SimpleMutantConstructor(chain); 

        Evolution evolution = new Evolution(constructor, historyFile); 
        double startTime = System.currentTimeMillis(); 
        evolution.evolveUntilNFeasible(4, 50,  4, 2, 0, true);
        double endTime = System.currentTimeMillis();

        System.out.println("TIME TAKEN: " + (endTime - startTime));

        int i = 0; 
        for(MutantInterface mutant: evolution.feasiblePop){
            PrintWriter out = new PrintWriter(folder + "/50game_" + i + ".txt");
            out.write(mutant.getGame());
            out.close();

            out = new PrintWriter(folder + "/50game_" + i + "_level_0" + ".txt");
            out.write(mutant.getLevel());
            out.close();

            i++;
        }

        historyFile = folder + "/history75.txt";  
        evolution = new Evolution(constructor, historyFile); 
        startTime = System.currentTimeMillis(); 
        evolution.evolveUntilNFeasible(4, 75,  4, 2, 0, true);
        endTime = System.currentTimeMillis();

        System.out.println("TIME TAKEN: " + (endTime - startTime));

        i = 0; 
        for(MutantInterface mutant: evolution.feasiblePop){
            PrintWriter out = new PrintWriter(folder + "/75game_" + i + ".txt");
            out.write(mutant.getGame());
            out.close();

            out = new PrintWriter(folder + "/75game_" + i + "_level_0" + ".txt");
            out.write(mutant.getLevel());
            out.close();

            i++;
        }

        historyFile = folder + "/history100.txt";
        evolution = new Evolution(constructor, historyFile); 
        startTime = System.currentTimeMillis(); 
        evolution.evolveUntilNFeasible(4, 100,  4, 2, 0, true);
        endTime = System.currentTimeMillis();
        System.out.println("TIME TAKEN: " + (endTime - startTime));

        i = 0; 
        for(MutantInterface mutant: evolution.feasiblePop){
            PrintWriter out = new PrintWriter(folder + "/100game_" + i + ".txt");
            out.write(mutant.getGame());
            out.close();

            out = new PrintWriter(folder + "/100game_" + i + "_level_0" + ".txt");
            out.write(mutant.getLevel());
            out.close();

            i++;
        }


        FileInputStream fi = new FileInputStream(new File(historyFile));
        ObjectInputStream oi = new ObjectInputStream(fi);

        // Read objects
        EvolutionHistory hist;
        try {
            hist = (EvolutionHistory) oi.readObject();
            System.out.println(hist.infeasibleSize); 
            System.out.println(hist.topInfeasibleFitness); 
            //System.out.println(hist.topFeasGameDescriptions.get());
            //System.out.println(hist.topFeasLevels.get(40));

            //GrammarMutant mutant = new GrammarMutant(hist.topFeasGameDescriptions.get(40), hist.topFeasLevels.get(40), chain);
            //System.out.println(mutant.getFitness()); 
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        

        oi.close();
        fi.close();  */ 

        /*String game1 = MarkovChain.getFileAsString("examples/gridphysics/zelda.txt");
        String level1 = MarkovChain.getFileAsString("examples/gridphysics/zelda_lvl0.txt");
        
        GrammarMutant mutant = new GrammarMutant(game1, level1, chain); 
        mutant.setUp();
        System.out.println(mutant.getGame()); 
        System.out.println(mutant.feasibility());
        System.out.println(mutant.getLevel());
        System.out.println(mutant.getOptimizeFitness());
        String recordActionsFile = null;
        //ArcadeMachine.playOneGame(mutant.getGame(), mutant.getLevel(), recordActionsFile, 42, false);*/ 


        

        //String gameFile1 =  "examples/gridphysics/aliens.txt"; 
        /*String gameFile1 = "examples/gridphysics/angelsdemons.txt";
        String game1 = MarkovChain.getFileAsString(gameFile1);
        String gameFile2 = "examples/gridphysics/zelda.txt";
        String game2 = MarkovChain.getFileAsString(gameFile2);

        //System.out.println(game2);

        String[] game2_levels = new String[5];
        for(int i =0; i < 5; i ++){
            game2_levels[i] = gameFile2.replace(".txt", "_lvl" + i +".txt"); 
        }

        String[] game1_levels = new String[5];
        for(int i =0; i < 5; i ++){
            game1_levels[i] = gameFile1.replace(".txt", "_lvl" + i +".txt"); 

        }

        String level1 = MarkovChain.getFileAsString(game1_levels[0]);
        String level2 = MarkovChain.getFileAsString(game2_levels[0]);

        GrammarMutant mutant1 = new GrammarMutant(game1, level1, chain); 
        GrammarMutant mutant2 = new GrammarMutant(game2, level2, chain); 

        //System.out.println(GrammarMutant.renameAllSprites(game1, "a"));


        ArrayList<ArrayList<String>> parts = MarkovChain.parseInteractions(game1); 
        ArrayList<String> sprites = MarkovChain.removeNestedSprites(parts.get(0)); 
      
        MutantInterface[] children = mutant1.crossover(mutant2); 

        System.out.println(children[0].getGame());
        System.out.println(children[0].getLevel());
        System.out.println("\n\n");
        System.out.println(children[1].getGame());
        System.out.println(children[1].getLevel());*/

        /*LevelChain newChain = new LevelChain(); 

        String[] game2_levels = new String[5];
        for(int i =0; i < 5; i ++){
            game2_levels[i] = gameFile2.replace(".txt", "_lvl" + i +".txt"); 

        }
        newChain.trainFromFiles(game2, game2_levels);

        String[] game1_levels = new String[5];
        for(int i =0; i < 5; i ++){
            game1_levels[i] = gameFile1.replace(".txt", "_lvl" + i +".txt"); 

        }
        newChain.trainFromFiles(game1, game1_levels);

        String gameFile3 = "examples/gridphysics/labyrinth.txt";
        String game3 = MarkovChain.getFileAsString(gameFile2);
        String[] game3_levels = new String[5];
        for(int i =0; i < 5; i ++){
            game3_levels[i] = gameFile3.replace(".txt", "_lvl" + i +".txt"); 

        }
        newChain.trainFromFiles(game3, game3_levels);


        MarkovChain chain = new MarkovChain(); 
        chain.trainFromCsvFile( "examples/all_games_sp.csv");
        String generatedGame1  = chain.buildGame(); 
        String generatedGame2  = chain.buildGame(); 


        String level1 = LevelChain.specifyLevel(game1, newChain.generate(5)); 
        String level2 = LevelChain.specifyLevel(game2, newChain.generate(5)); 

        System.out.println("level1: \n" + level1); 
        System.out.println("level2: \n" + level2); 

        String[] crossed = LevelChain.crossover(game1, game2, level1, level2);

        System.out.println("Child 1 : \n" + crossed[0]); 
        System.out.println("Child 2 : \n" +crossed[1]); */ 
        
    
        /*MarkovChain chain = new MarkovChain(); 
        chain.trainFromCsvFile( "examples/all_games_sp.csv");

        GrammarMutant mutant = new GrammarMutant(chain); 
        mutant.setUp();
        System.out.println(mutant.getOptimizeFitness());*/ 
        
        /*for(int i = 0; i < 1000; i ++){
            ArrayList<String> games = new ArrayList<String>(); 
            String game = chain.buildGame(); 
            String mutant = chain.mutateGame(game);

            games.add(game); 
            games.add(mutant);
            for(int j =0; j < 5; j ++){
                mutant = chain.mutateGame(mutant);
                //System.out.println(mutant);
      
                if(mutant == "false"){
                    for(String desc: games){
                        System.out.println(desc); 
                        System.out.println("\n\n");
                    }

                    break; 
                }else{
                    games.add(mutant);
                }
            }
        } */ 
       

    }

    
}
