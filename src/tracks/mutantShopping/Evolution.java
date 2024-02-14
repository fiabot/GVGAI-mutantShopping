package tracks.mutantShopping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Evolution implements EvaluateListener {

    String intialLevel;
    String intialGame;
    GradeGames view;
    ArrayList<EvaluateMutant> infeasible;
    ArrayList<EvaluateMutant> feasible;
    Random random;

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
            feasibleValue = -1;
        }

        // is infeasible
        if (feasibleValue < 1) {
            infeasible.add(mutant);
        } else {
            // let human evaluate
            System.out.println("Sending a new game");
            view.addMutant(mutant);
            feasible.add(mutant);

        }
    }

    public void RunEvolution(int generations, int mutationAmount) {

        infeasible = new ArrayList<EvaluateMutant>();
        feasible = new ArrayList<EvaluateMutant>();
        EvaluateMutant origin = new EvaluateMutant(new Mutant(intialGame, intialLevel));
        origin.feasibility();
        if (origin.constrainFitness < 1) {
            infeasible.add(origin);
        } else {
            feasible.add(origin);
        }
        int i = 0; 
        while(true){
            i++; 
            System.out.println("Generation:" +i);
            Collections.sort(infeasible);
            
            Collections.sort(feasible);
            if(infeasible.size() > 0){
                System.out.println(infeasible.get(0).constrainFitness);
            } 

            double roll = random.nextDouble();
            double prob = (double) (feasible.size()  * 2)/ (double) (feasible.size() + infeasible.size());
            EvaluateMutant child;
            if (roll < prob) {
                child = rankSelection(feasible);

            } else {
                child = rankSelection(infeasible);
            }

            Mutant newMutant = child.mutant.Mutate(mutationAmount);
            addChild(new EvaluateMutant(newMutant));

        }

    }

    @Override
    public void evaluate(EvaluateMutant mutant, int value) {
        mutant.setHumanEval(value);
        //feasible.add(mutant);
        System.out.println("Feasible Size: " + feasible.size());
    }

    /**
     * Performs rank selection on the given population
     * 
     * @param population the population to be performed upon
     * @return
     */
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
