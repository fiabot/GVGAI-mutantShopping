package tracks.mutantShopping;

import java.awt.geom.Line2D.Double;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import core.game.ForwardModel;
import core.game.Observation;
import core.game.StateObservation;
import core.logging.Message;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

public class EvaluateMutant implements Comparable<EvaluateMutant> {
    int TRIAL_LENGTH = 2;
    int FEASIBILITY_STEP_LIMIT = 300;
    public static AbstractPlayer doNothingAgent;
    int width;
    int height;
    int badFrames;
    protected Mutant mutant;
    protected boolean humanGraded;
    StateObservation state;
    int humanEval;
    double constrainFitness;

    public EvaluateMutant(Mutant mutant) {
        width = mutant.level_array[0].length;
        height = mutant.level_array.length;
        badFrames = 0;
        this.mutant = mutant;
        try{
            state = mutant.toPlay.getObservation();
        }catch (Exception e) {
            state = null;
        }
    

        try {
            Class agentClass = Class.forName("tracks.singlePlayer.simple.doNothing.Agent");
            Constructor agentConst = agentClass
                    .getConstructor(new Class[] { StateObservation.class, ElapsedCpuTimer.class });
            doNothingAgent = (AbstractPlayer) agentConst.newInstance(state, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        humanEval = -1;
    }

    public double feasibility() {
        if(state == null){
            constrainFitness = -1; 
            return constrainFitness;
        }else{
            int errors = mutant.sl.getErrors().size();
            constrainFitness = (0.5) * 1.0 / (errors + 1.0);
            if (constrainFitness >= 0.5) {
                int doNothingLength = Integer.MAX_VALUE;
                for (int i = 0; i < TRIAL_LENGTH; i++) {
                    int temp = this.getAgentResult(state.copy(), FEASIBILITY_STEP_LIMIT, doNothingAgent, 300);
                    if (temp < doNothingLength) {
                        doNothingLength = temp;
                    }
                }
                constrainFitness += 0.2 * (doNothingLength / (40.0));

            }

            return constrainFitness;
        }

        
    }

    /***
     * Checks to see if sprites are off screen
     * 
     * @param stateObs the temporary state observation of the game
     * @return the number of times sprites were off screen
     */
    private int checkIfOffScreen(StateObservation stateObs) {
        ArrayList<Observation> allSprites = new ArrayList<Observation>();
        ArrayList<Observation>[] temp = stateObs.getNPCPositions();
        if (temp != null) {
            for (ArrayList<Observation> list : temp) {
                allSprites.addAll(list);
            }
        }
        temp = stateObs.getImmovablePositions();
        if (temp != null) {
            for (ArrayList<Observation> list : temp) {
                allSprites.addAll(list);
            }
        }

        temp = stateObs.getMovablePositions();
        if (temp != null) {
            for (ArrayList<Observation> list : temp) {
                allSprites.addAll(list);
            }
        }

        // calculate screen size
        int xMin = -1 * stateObs.getBlockSize();
        int yMin = -1 * stateObs.getBlockSize();

        // add a 1 pixel buffer
        int xMax = (width + 1) * stateObs.getBlockSize();
        int yMax = (height + 1) * stateObs.getBlockSize();
        int counter = 0;
        // check to see if any sprites are out of screen
        boolean frameBad = false;
        for (Observation s : allSprites) {
            if (s.position.x < xMin || s.position.x > xMax || s.position.y < yMin || s.position.y > yMax) {
                if (!frameBad) {
                    counter++;
                    frameBad = true;
                }
            }
        }
        return counter;

    }

    /**
     * Play the current level using the naive player
     * 
     * @param stateObs the current stateObservation object that represent the level
     * @param steps    the maximum amount of steps that it shouldn't exceed it
     * @param agent    current agent to play the level
     * @return the number of steps that the agent stops playing after (<= steps)
     */
    private int getAgentResult(StateObservation stateObs, int steps, AbstractPlayer agent, int time) {
        int i = 0;
        int k = 0;
        for (i = 0; i < steps; i++) {
            if (stateObs.isGameOver()) {
                break;
            }
            ElapsedCpuTimer timer = new ElapsedCpuTimer();
            timer.setMaxTimeMillis(time);
            Types.ACTIONS bestAction = agent.act(stateObs, timer);
            stateObs.advance(bestAction);
            k += checkIfOffScreen(stateObs);

        }
        if (k > 0) {
            // add k to global var keeping track of this
            this.badFrames += k;
        }
        return i;
    }

    public void setHumanEval(int eval) {
        humanEval = eval;
    }

    public int getHumanEval() {
        return humanEval;
    }

    @Override
    public int compareTo(EvaluateMutant o) {

        // both are infeasible
        if (constrainFitness < 1 || o.constrainFitness < 1) {
            if (this.constrainFitness < o.constrainFitness) {
                return 1;
            }
            if (this.constrainFitness > o.constrainFitness) {
                return -1;
            }
            return 0;
        } else {
            if (this.humanEval < o.humanEval) {
                return 1;
            }
            if (this.humanEval > o.humanEval) {
                return -1;
            }
            return 0;
        }

    }

}
