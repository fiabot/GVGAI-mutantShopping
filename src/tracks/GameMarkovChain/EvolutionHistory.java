package tracks.GameMarkovChain;
import java.io.Serializable;
import java.util.ArrayList;

public class EvolutionHistory  implements Serializable{
    ArrayList<Integer> feasibleSize; 
    ArrayList<Integer> infeasibleSize; 
    ArrayList<Double> topFeasibleFitness; 
    ArrayList<Double> topInfeasibleFitness; 
    ArrayList<String> topFeasGameDescriptions; 
    ArrayList<String> topFeasLevels; 
    ArrayList<String> topInfeasGameDescriptions; 
    ArrayList<String> topInfeasLevels; 
    
    public EvolutionHistory(){
        feasibleSize = new ArrayList<Integer>(); 
        infeasibleSize = new ArrayList<Integer>(); 
        topFeasibleFitness =new ArrayList<Double>(); 
        topInfeasibleFitness = new ArrayList<Double>(); 
        topFeasGameDescriptions = new ArrayList<String>(); 
        topFeasLevels = new ArrayList<String>(); 
        topInfeasGameDescriptions = new ArrayList<String>(); 
        topInfeasLevels = new ArrayList<String>(); 

    } 

    public void trackGeneration(ArrayList<MutantInterface> feasiblePopulation, ArrayList<MutantInterface> infeasiblePopulation){
        feasibleSize.add(feasiblePopulation.size()); 
        infeasibleSize.add(infeasiblePopulation.size()); 

        if(feasiblePopulation.size() > 0){
            MutantInterface topMutant = feasiblePopulation.get(0);
            topFeasibleFitness.add(topMutant.getFitness()); 
            topFeasGameDescriptions.add(topMutant.getGame()); 
            topFeasLevels.add(topMutant.getLevel()); 
        }else{
            topFeasibleFitness.add(null); 
            topFeasGameDescriptions.add(null); 
            topFeasLevels.add(null); 
        }

        if(infeasiblePopulation.size() > 0){
            MutantInterface topMutant = infeasiblePopulation.get(0);
            topInfeasibleFitness.add(topMutant.getInfeasibleFitness()); 
            topInfeasGameDescriptions.add(topMutant.getGame()); 
            topInfeasLevels.add(topMutant.getLevel()); 
        }else{
            topInfeasibleFitness.add(null); 
            topInfeasGameDescriptions.add(null); 
            topInfeasLevels.add(null); 
        }
    }
}
