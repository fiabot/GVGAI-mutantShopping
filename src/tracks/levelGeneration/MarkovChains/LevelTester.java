package tracks.levelGeneration.MarkovChains;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import javax.sound.midi.Track;

import tracks.ArcadeMachine;

public class LevelTester {
    

    private String levelFile;
    private String gameFile;

    private double doNothingSteps; 
    private double[] randomScores; 
    private double[] oneStepScores; 
    private double[] advancedScores; 
    private boolean[] advancedwins; 

    public static int TRIALS = 10; 


    public LevelTester(String levelFile, String gameFile){
        this.levelFile = levelFile; 
        this.gameFile = gameFile; 
        testLevel();
    }

    void testLevel(){
        String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
		String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";

        String advanced ="tracks.singlePlayer.advanced.olets.Agent"; 

        String recordActionsFile = null;

        // test with do nothing 
		double[] results = ArcadeMachine.runOneGame(this.gameFile, this.levelFile, false, doNothingController, recordActionsFile, 42, 0);
        
        doNothingSteps = results[2]; 

        randomScores = new double[TRIALS]; 
        oneStepScores = new double[TRIALS]; 
        advancedScores = new double[TRIALS]; 
        advancedwins = new boolean[TRIALS];

        for (int trial = 0; trial < TRIALS; trial++){
            double[] rand_results = ArcadeMachine.runOneGame(this.gameFile, this.levelFile, false, sampleRandomController, recordActionsFile, trial, 0);
            randomScores[trial] = rand_results[1]; 

            double[] one_step_results = ArcadeMachine.runOneGame(this.gameFile, this.levelFile, false, sampleOneStepController, recordActionsFile,trial, 0);
            oneStepScores[trial] = one_step_results[1]; 

            double[] advancedResults = ArcadeMachine.runOneGame(this.gameFile, this.levelFile, false, advanced, recordActionsFile,trial, 0);
            advancedScores[trial] = advancedResults[1];
            advancedwins[trial] = advancedResults[0] ==1;  
        }
    }

    private double getAverage(double[] arr){
        double sum = 0; 
        for (double d: arr){
            sum+= d; 
        }
        return sum / arr.length; 
    }

    private int perTrue(boolean[] arr){
        int t = 0; 
        for(boolean b: arr){
            if(b){t++;}
        }

        return t / arr.length; 
    }

    public void printResults(){

        System.out.println("Do nothing steps: " + doNothingSteps); 
        System.out.println("Advanced Win rate:"  + perTrue(advancedwins)); 
        System.out.println("Advanced Score: " + getAverage(advancedScores)); 
        System.out.println("One Step score: " + getAverage(oneStepScores)); 
        System.out.println("Random score: " + getAverage(randomScores)); 

    }

    public String getCSVRow(){
        return doNothingSteps + "," + perTrue(advancedwins) + "," +  getAverage(advancedScores) + "," + getAverage(oneStepScores) + "," +  getAverage(randomScores); 
    }

    public void saveResultsAsCsv(String fileName) throws IOException{
        ArrayList<String> rows = new ArrayList<String>(); 
        String header = "trial, do nothing steps, advanced win, advanced score, one step scores, random score"; 
        rows.add(header); 
        for(int i = 0; i < TRIALS; i++){
            String newRow =  i + "," + doNothingSteps + "," + advancedwins[i] + "," + advancedScores[i] + "," + oneStepScores[i] + "," + randomScores[i]; 
            rows.add(newRow); 
        }

        createCSVFile(fileName, rows);
    }

    public static String escapeSpecialCharacters(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public static String convertToCSV(String[] data) {
        return Stream.of(data)
          .map(LevelTester::escapeSpecialCharacters)
          .collect(Collectors.joining(","));
    }

    public static void createCSVFile(String fileName, List<String> data) throws IOException {
        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            for(String line: data){
                pw.println(line);
            }
        }
        
    }

   

    
    
}
