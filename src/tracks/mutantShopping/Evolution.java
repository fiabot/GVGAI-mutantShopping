package tracks.mutantShopping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Evolution implements EvaluateListener {

    String intialLevel;
    String intialGame;
    GradeGames view;
    ArrayList<EvaluateMutant> population;
    Random random;
    double bestFitness; 

    public Evolution(String initalGame, String intialLevel, String agent, int seed) {
        view = new GradeGames(agent, seed);
        view.addEvaluateListener(this);
        this.intialGame = initalGame;
        this.intialLevel = intialLevel;
        random = new Random(seed);
    }

    public void addChild(EvaluateMutant mutant) {
        double feasibleValue;
        try {
            feasibleValue = mutant.feasibility();
        } catch (Exception e) {
            mutant.fitness = -1; 
        }

        population.add(mutant);

        
    }

    public void RunEvolution(int populationSize, int mutationAmount, int elitism) throws IOException {
        bestFitness = -1; 
        population = new ArrayList<EvaluateMutant>();
        EvaluateMutant origin = new EvaluateMutant(new Mutant(intialGame, intialLevel, true));
        origin.feasibility();
        for (int j = 0; j < populationSize; j ++){
            population.add(origin);  
        }
        
        view.addMutant(origin); // show origin first 
        int i = 0; 
        while(true){
            i++; 
           
            Collections.sort(population);
            System.out.println("Generation:" +i + " top contraint:" +population.get(0).fitness + " top human: " + population.get(0).humanEval);

            ArrayList<EvaluateMutant> newPop = new ArrayList<EvaluateMutant>(); 

            for(int j =0 ; j < elitism; j++){
                newPop.add(population.get(j));
            }

            while(newPop.size() < populationSize){

                EvaluateMutant child = rankSelection(population);


                Mutant newMutant = child.mutant.Mutate(mutationAmount);
                addChild(new EvaluateMutant(newMutant));

            }

            population = newPop;


        

        }

    }

    @Override
    public void evaluate(EvaluateMutant mutant, int value) {

        // set value 
        mutant.humanEval = value; 

        // get next top that hasn't been evaluated 
        Collections.sort(population);
        EvaluateMutant nextTop = population.get(0); 

        int i = 1; 
        while(i < population.size() && nextTop.humanEval != -1){
            nextTop = population.get(i); 
            i++;
        }
        if (i < population.size()){
            view.addMutant(nextTop);
        }else{
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            evaluate(mutant, value);
        }
        

    }


    
    private EvaluateMutant rankSelection(ArrayList<EvaluateMutant> population) {
        double[] probabilities = new double[population.size()];
        probabilities[0] = 1.0;
        for (int i = 1; i < population.size(); i++) {
            probabilities[i] = probabilities[i - 1] + i;
        }
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] = probabilities[i] / probabilities[probabilities.length - 1];
        }

        double chosen = random.nextDouble();
        for (int i = 0; i < probabilities.length; i++) {
            if (chosen < probabilities[i]) {
                return population.get(i);
            }
        }
        return population.get(0);

    }

}
