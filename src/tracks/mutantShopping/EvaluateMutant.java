package tracks.mutantShopping;

import java.awt.geom.Line2D.Double;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import core.game.Event;
import core.game.ForwardModel;
import core.game.Observation;
import core.game.StateObservation;
import core.logging.Message;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
import java.util.Random;
//import tracks.singlePlayer.simple.doNothing.Agent;
//import tracks.singlePlayer.simple.sampleRandom.*;
//import tracks.singlePlayer.simple.sampleonesteplookahead.*;
//import tracks.singlePlayer.advanced.olets.*;


public class EvaluateMutant implements Comparable<EvaluateMutant> {
    int TRIAL_LENGTH = 4;
    int FEASIBILITY_STEP_LIMIT = 40;
    int EVALUATION_STEP_COUNT = 400; 
    int TIMESTEP = 10; 
    public  AbstractPlayer doNothingAgent;
    public AbstractPlayer randomAgent; 
    public AbstractPlayer simpleAgent; 
    public AbstractPlayer advancedAgent; 

    int width;
    int height;
    int badFrames;
    protected Mutant mutant;
    protected boolean humanGraded;
    StateObservation state;
    int humanEval;
    double fitness;

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
    
        if(state != null){

            try {
                doNothingAgent = (AbstractPlayer) new tracks.singlePlayer.simple.doNothing.Agent(state, null); 
            } catch (Exception e) {
                System.out.println("Can't make do nothing ");
            }
    
            try {
                randomAgent = (AbstractPlayer) new tracks.singlePlayer.simple.sampleRandom.Agent(state, null);
            } catch (Exception e) {
                System.out.println("Can't make random");
                e.printStackTrace();
            }
    
            try {
            
                simpleAgent = (AbstractPlayer) new tracks.singlePlayer.simple.sampleonesteplookahead.Agent(state, null);
            } catch (Exception e) {
                System.out.println("Can't make simple");
                e.printStackTrace();
            }
    
            try {
                advancedAgent =(AbstractPlayer) new tracks.singlePlayer.advanced.olets.Agent(state, null);
            } catch (Exception e) {
                System.out.println("Can't make advanced");
                e.printStackTrace();
            }

        }
       

        humanEval = -1;
    }

        /**
	 * calculates the fitness, by comparing the scores of a naiveAI and a smart AI
	 * @param time	how much time to evaluate the chromosome
	 */
    public double getOptimizeFitness(){
		
		// reset bad frames
		this.badFrames = 0;
		// unique events that occurred in all the game simulations
		Set<String> events = new HashSet<String>();
		
		if(state == null) {
			// failed feasibility
			return 0; 
		}
		else {					
			//Play the game using the best agent
			double score = -200;
			ArrayList<Vector2d> SOs = new ArrayList<>();
			// protects the fitness evaluation from looping forever
	
			// big vars
			// keeps track of total number of simulated frames
			int frameCount = 0;
			// Best Agent
			double agentBestScore = -10000000; 
			double automatedScoreSum = 0.0;
			double automatedWinSum = 0.0;
			int bestSolutionSize = 0;
            StateObservation bestState = null; 
			for(int i=0; i< TRIAL_LENGTH; i++){
				StateObservation tempState = state.copy();
				cleanOpenloopAgents();
				int temp = getAgentResult(tempState, EVALUATION_STEP_COUNT, advancedAgent, TIMESTEP);
				// add temp to framesCount
				frameCount += temp;
				
				if(tempState.getGameScore() > agentBestScore) {
					agentBestScore = tempState.getGameScore();
					bestState = tempState;
					bestSolutionSize = temp;
				}
				
				score = tempState.getGameScore();
				automatedScoreSum += score;
				if(tempState.getGameWinner() == Types.WINNER.PLAYER_WINS){
					automatedWinSum += 1;
				} else if(tempState.getGameWinner() == Types.WINNER.NO_WINNER) {
					automatedWinSum += 0.5;
				}
				
				TreeSet s1 = tempState.getEventsHistory();
				Iterator<Event> iter1 = s1.iterator();
				while(iter1.hasNext()) {
					Event e = iter1.next();
					events.add(e.activeTypeId + "" + e.passiveTypeId);
				}
				score = -200;
			}
			 
			// Random Agent
			score = -200;
			 
			double randomScoreSum = 0.0;
			double randomWinSum = 0.0;
			StateObservation randomState = null;
			for(int i=0; i<TRIAL_LENGTH; i++){
				StateObservation tempState = state.copy();
				int temp = getAgentResult(tempState, bestSolutionSize, randomAgent, TIMESTEP);
				// add temp to framesCount
				frameCount += temp;
				randomState = tempState;
				
				score = randomState.getGameScore();
				
				randomScoreSum += score;
				if(randomState.getGameWinner() == Types.WINNER.PLAYER_WINS){
					randomWinSum += 1;
				} else if(randomState.getGameWinner() == Types.WINNER.NO_WINNER) {
					randomWinSum += 0.5;
				}
				
				// gather all unique interactions between objects in the naive agent
				TreeSet s1 = randomState.getEventsHistory();
				Iterator<Event> iter1 = s1.iterator();
				while(iter1.hasNext()) {
					Event e = iter1.next();
					events.add(e.activeTypeId + "" + e.passiveTypeId);
				}
				score = -200;
			}
			
			// Naive agent
			score = -200;
			StateObservation naiveState = null;
			double naiveScoreSum = 0.0;
			double naiveWinSum = 0.0;
			//playing the game using the naive agent
			for(int i=0; i<TRIAL_LENGTH; i++){
				StateObservation tempState = state.copy();
				int temp = getAgentResult(tempState, bestSolutionSize, simpleAgent, TIMESTEP);
				// add temp to framesCount
				frameCount += temp;
				naiveState = tempState;
				
				score = naiveState.getGameScore();
				if(score > -100) {
					naiveScoreSum += score;
					if(naiveState.getGameWinner() == Types.WINNER.PLAYER_WINS){
						naiveWinSum += 1;
					} else if(naiveState.getGameWinner() == Types.WINNER.NO_WINNER) {
						naiveWinSum += 0.5;
					}
				}
				
				// gather all unique interactions between objects in the best agent
				TreeSet s1 = naiveState.getEventsHistory();
				Iterator<Event> iter1 = s1.iterator();
				while(iter1.hasNext()) {
					Event e = iter1.next();
					events.add(e.activeTypeId + "" + e.passiveTypeId);
					}
				score = -200;
			}
			double badFramePercent = badFrames / (1.0 * frameCount);
//			if(badFramePercent > .3) {
//				// if we have bad frames, this is still not a good game
//				constrainFitness += 0.3 * (1 - badFrames / (1.0 * frameCount));
//				this.fitness.set(0, constrainFitness);
//			}
//			else {
				// find average scores and wins across playthroughs
				double avgBestScore = automatedScoreSum / TRIAL_LENGTH;
				double avgNaiveScore = naiveScoreSum / TRIAL_LENGTH;
				double avgRandomScore = randomScoreSum /TRIAL_LENGTH;
				
				double avgBestWin = automatedWinSum /TRIAL_LENGTH;
				double avgNaiveWin = naiveWinSum /TRIAL_LENGTH;
				double avgRandomWin = randomWinSum /TRIAL_LENGTH;
				
				// calc sigmoid function with the score as "t"
                double scoreSum = avgBestScore + avgNaiveScore + avgRandomScore; 
                if (scoreSum == 0){
                    scoreSum = 1; 
                }
				double sigBest = avgBestScore / (scoreSum);
				double sigNaive = avgNaiveScore / (scoreSum);
				double sigRandom = avgRandomScore / (scoreSum);
				
				// sum weighted win and sig-score values
				double summedBest = 0.9 * avgBestWin + 0.1 * sigBest;
				double summedNaive = 0.9 * avgNaiveWin + 0.1 * sigNaive;
				double summedRandom = 0.9 * avgRandomWin + 0.1 * sigRandom;
	
				// calc game score differences
				double gameScore = 0.5* (summedBest - summedNaive) + 0.5 * (summedNaive - summedRandom);
				
				// allows rounding up due to weird scores
				if(gameScore < -0.0005) {
					
					gameScore = 0;
				}
				// reward fitness for each unique interaction triggered
				int uniqueCount = events.size();
				// add a normalized unique count to the fitness
				double rulesTriggered = uniqueCount / (mutant.interaction.size()* 1.0f + 1);
				
				// fitness is calculated by weight summing the 2 variables together
				
				double fitness = 0.5 * (gameScore) +  0.5 * (rulesTriggered);

                System.out.println("Optimized Fitness: " + fitness);
                System.out.println("game score:" +  gameScore); 
                System.out.println("avg best:" +  sigBest); 
                System.out.println("avg best:" +  sigNaive); 
                System.out.println("rules Triggered: " + uniqueCount);
				
				return fitness;
		} 
	
    }



    private void cleanOpenloopAgents() {
		((tracks.singlePlayer.advanced.olets.Agent) advancedAgent).mctsPlayer = 
			new tracks.singlePlayer.advanced.olets.SingleMCTSPlayer(new Random(), 
				(tracks.singlePlayer.advanced.olets.Agent) advancedAgent);
	}

    public double feasibility() {
        if(state == null){
            fitness = -1; 
            return fitness;
        }else{
            // errors 
            int errors = mutant.sl.getErrors().size();
            fitness = (0.3) * 1.0 / (Math.max(errors, 20) + 1.0);

            // do nothing length 
            int doNothingLength = Integer.MAX_VALUE;
            for (int i = 0; i < TRIAL_LENGTH; i++) {
                int temp = this.getAgentResult(state.copy(), FEASIBILITY_STEP_LIMIT, doNothingAgent, TIMESTEP);
                if (temp < doNothingLength) {
                    doNothingLength = temp;
                }
            }
            fitness += 0.2 * (Math.max(40, doNothingLength)/ (40.0));

            fitness += 0.5 * (getOptimizeFitness());

            
            
            return fitness;
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

        // neither have been evaluate 
        if (this.humanEval == -1 && o.humanEval == -1) {
            if (this.fitness < o.fitness) {
                return 1;
            }
            if (this.fitness > o.fitness) {
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
