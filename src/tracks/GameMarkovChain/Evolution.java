package tracks.GameMarkovChain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Evolution {
    MutantContructorInterface constructor; 

    ArrayList<MutantInterface> infeasiblePop; 
    ArrayList<MutantInterface> feasiblePop; 
    String historyFile; 
    EvolutionHistory history; 
    Random random; 
    private static int MIN_SIZE_PARALLEL = 1; 
    private static int MAX_CORES = 4; 
    public Evolution(MutantContructorInterface constructor, String historyFile){
        this.random = new Random(); 
        this.constructor = constructor; 
        history = new EvolutionHistory(); 
        this.historyFile = historyFile; 

    }

    private class EvaluateRunnable implements Runnable{
        MutantInterface mutant; 
        public EvaluateRunnable(MutantInterface mutant){
            this.mutant = mutant; 
        }

        public void run(){
            mutant.getFitness(); 
        }
    }

    protected void evaluate(ArrayList<MutantInterface> pop){
        // for small sizes, run in serial
        if(pop.size() < MIN_SIZE_PARALLEL){
            evaluateSerial(pop);
        }else{
            // for larger sizes, run in parallel 
            pop.stream().parallel().forEach(s -> {
                s.getFitness(); 
            });
        }
      
    }

    protected void evaluateSerial(ArrayList<MutantInterface> pop){
            for(MutantInterface child: pop){
                child.getFitness();
            }
    
    }

    // adds child based on feasiblity 
    // doesn't calculate optimization fitness 
    private void addChild(MutantInterface child, ArrayList<MutantInterface> feas, ArrayList<MutantInterface> infeas){
        if(child.isFeasible()){
            feas.add(child); 
        }else{
            infeas.add(child); 
        }
    }

    public void oneGeneration( int popsize, int elitism, double xOverRate, double feasibleBonus, boolean debug, int gen){
        Collections.sort(feasiblePop, Collections.reverseOrder());
            Collections.sort(infeasiblePop, Collections.reverseOrder());

            history.trackGeneration(feasiblePop, infeasiblePop);

            if(debug){
                System.out.println("Generation:" + gen); 
                System.out.println("\tNum Feasible: " + feasiblePop.size());
                if(infeasiblePop.size() > 0){
                    System.out.println("\ttop infeasible:" + infeasiblePop.get(0).getInfeasibleFitness());
                }
                if(feasiblePop.size() > 0){
                    System.out.println("\ttop feasible:" + feasiblePop.get(0).getFitness()); 
                }
                
            }

            ArrayList<MutantInterface> tempFeas = new ArrayList<MutantInterface>(); 
            ArrayList<MutantInterface> tempInfeas = new ArrayList<MutantInterface>(); 

            // add elites 
            for(int i = 0; i < elitism; i ++){
                if(i < infeasiblePop.size()){
                    tempInfeas.add(infeasiblePop.get(i));
                }

                if(i < feasiblePop.size()){
                    tempFeas.add(feasiblePop.get(i));
                }
            }

            while(tempFeas.size() + tempInfeas.size() < popsize){
                double feaseSize = (double) feasiblePop.size(); 
                double infeaseSize = (double) infeasiblePop.size(); 
                double chooseFeasRate =  (feaseSize / (feaseSize + infeaseSize)) * feasibleBonus; 

                MutantInterface parent1; 
                MutantInterface parent2; 

                // select from feasible population 
                if(random.nextDouble() < chooseFeasRate){
                    parent1 = rankSelection(feasiblePop);
                    parent2 = rankSelection(feasiblePop);

                }else{
                    parent1 = rankSelection(infeasiblePop);
                    parent2 = rankSelection(infeasiblePop);

                    
                }

                MutantInterface child1; 
                MutantInterface child2; 

                // cross over 
                if(random.nextDouble() < xOverRate){
                    MutantInterface[] children = parent1.crossover(parent2); 
                    child1 = children[0]; 
                    child2 = children[1]; 
                }else{
                    child1 = parent1; 
                    child2 = parent2; 
                }

                // mutate 
                child1 = child1.mutate(); 
                child2 = child2.mutate(); 

                addChild(child1, tempFeas, tempInfeas);
                addChild(child2, tempFeas, tempInfeas);
            }

            infeasiblePop = tempInfeas; 
            feasiblePop = tempFeas; 

            evaluate(feasiblePop);
            Collections.sort(feasiblePop, Collections.reverseOrder());
            Collections.sort(infeasiblePop, Collections.reverseOrder());
            
        }
    

    public void evolveUntilNFeasible(int n, int popsize,   int elitism, double feasibleBonus, double xOverRate, boolean debug ){
        infeasiblePop = new ArrayList<MutantInterface>(); 
        feasiblePop = new ArrayList<MutantInterface>(); 


        // inital population 
        for(int i = 0; i < popsize; i ++){
            MutantInterface child = constructor.newMutant(); 
            addChild(child, feasiblePop, infeasiblePop);
        }

        evaluate(feasiblePop);
        Collections.sort(feasiblePop, Collections.reverseOrder());
        Collections.sort(infeasiblePop, Collections.reverseOrder());

        int i = 0; 

        while(feasiblePop.size() < n){

            oneGeneration(popsize, elitism, feasibleBonus, xOverRate, debug, i);
            i++;
 
            
        }

        		// Write objects to file
		try {
            FileOutputStream f = new FileOutputStream(new File(historyFile));
		    ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(history);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Error saving history");
            e.printStackTrace();
        }

            
        
        }


    public void evolve(int generations, int popsize, int elitism, double feasibleBonus, double xOverRate, boolean debug){
        infeasiblePop = new ArrayList<MutantInterface>(); 
        feasiblePop = new ArrayList<MutantInterface>(); 


        // inital population 
        for(int i = 0; i < popsize; i ++){
            MutantInterface child = constructor.newMutant(); 
            addChild(child, feasiblePop, infeasiblePop);
        }

        evaluate(feasiblePop);
        Collections.sort(feasiblePop, Collections.reverseOrder());
        Collections.sort(infeasiblePop, Collections.reverseOrder());

        for(int gen =0; gen < generations; gen ++){

            oneGeneration(popsize, elitism, feasibleBonus, xOverRate, debug, gen);
 
            
        }

        

		// Write objects to file
		try {
            FileOutputStream f = new FileOutputStream(new File(historyFile));
		    ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(history);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Error saving history");
            e.printStackTrace();
        }

    }

    /*
     * Assumes popualtion is sorted in reverse order 
     */
    private MutantInterface rankSelection(ArrayList<MutantInterface> population) {
        double[] probabilities = new double[population.size()];
        probabilities[probabilities.length - 1] = 1.0;
        int add = 1; 
        for (int i = probabilities.length - 2; i >= 0; i--) {
            probabilities[i] = probabilities[i + 1] + add;
            add++;
        }
        for (int i = probabilities.length - 1; i >= 0; i--) {
            probabilities[i] = probabilities[i] / probabilities[0];
        }


        double chosen = random.nextDouble();
        for (int i = probabilities.length - 1; i >= 0; i--) {
            if (chosen < probabilities[i]) {
                return population.get(i);
            }
        }
        return population.get(0);

    }
}
