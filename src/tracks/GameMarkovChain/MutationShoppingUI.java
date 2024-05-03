package tracks.GameMarkovChain;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class MutationShoppingUI {
    String folder; 
    MarkovChain chain; 
    ArrayList<MutantInterface> selectedMutants;
    ArrayList<MutantInterface> mutantsToSelect;  
    int numToSelect; 

    public MutationShoppingUI(String folder, int numToSelect) throws IOException{
        this.folder = folder; 
        this.chain = new MarkovChain(); 
        chain.trainFromCsvFile( "examples/all_games_sp.csv");
        selectedMutants = new ArrayList<MutantInterface>();
        mutantsToSelect = new ArrayList<MutantInterface>();
        this.numToSelect = numToSelect;

    }

    public void startShopping()  throws IOException{
        System.out.println("Generating the intial batch of games"); 
        generateFirstStep(); 
        System.out.println("Games are generated. Look at the selection folder");
        int i = 1; 
        while(userWantsToContinue()){
            MutantInterface selectedMutant = selectMutant(); 
            selectedMutants.add(selectedMutant); 


            System.out.println("Generating the next batch of games");
            // save mutant 
            PrintWriter out = new PrintWriter(folder + "/game_" + i + ".txt");
            out.write(selectedMutant.getGame());
            out.close();

            out = new PrintWriter(folder + "//game_" + i + "_level" + ".txt");
            out.write(selectedMutant.getLevel());
            out.close();

            // generate next games 
            generateNextStep(selectedMutant);
            System.out.println("Games are generated. Look at the selection folder");
            i++; 
        }

    
    }

    public void generateFirstStep() throws IOException{
        String historyFile = folder + "/intialStepHistory.txt"; 
        SimpleMutantConstructor constructor = new SimpleMutantConstructor(chain); 
        Evolution evolution = new Evolution(constructor, historyFile); 
        evolution.evolveUntilNFeasible(numToSelect, 100,  4, 2, 0, true);
        mutantsToSelect = new ArrayList<MutantInterface>();

        for(int i = 0; i < numToSelect; i++){
            MutantInterface mutant = evolution.feasiblePop.get(i);
            mutantsToSelect.add(mutant);

            PrintWriter out = new PrintWriter(folder + "/Selectors/game_" + i + ".txt");
            out.write(mutant.getGame());
            out.close();

            out = new PrintWriter(folder + "/Selectors/game_" + i + "_level" + ".txt");
            out.write(mutant.getLevel());
            out.close();

        }
    }

    public void generateNextStep(MutantInterface lastSelected) throws IOException{

        String historyFile = folder + "/step_" + selectedMutants.size() + "_history.txt"; 
        ConstantConstructor constructor = new ConstantConstructor(lastSelected.getGame(), lastSelected.getLevel(), this.chain, 4);  
        Evolution evolution = new Evolution(constructor, historyFile); 
        evolution.evolveUntilNFeasible(numToSelect, 50,  4, 2, 0, true);
        evolution.oneGeneration(25,  4, 2, 0, false, numToSelect);
        evolution.oneGeneration(25,  4, 2, 0, false, numToSelect);
        mutantsToSelect = new ArrayList<MutantInterface>();

        for(int i = 0; i < numToSelect; i++){
            MutantInterface mutant = evolution.feasiblePop.get(i);
            mutantsToSelect.add(mutant);

            PrintWriter out = new PrintWriter(folder + "/Selectors/game_" + i + ".txt");
            out.write(mutant.getGame());
            out.close();

            out = new PrintWriter(folder + "/Selectors/game_" + i + "_level" + ".txt");
            out.write(mutant.getLevel());
            out.close();

        }

    }

    public MutantInterface selectMutant(){
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter Number to Select");

        String input = myObj.nextLine();  // Read user input

        int mutantIndex; 
        try{
            mutantIndex = Integer.valueOf(input);
        }catch(Throwable t){
            System.out.println("Please enter an integer");
            return selectMutant(); 
        }


        if(mutantIndex < 0 || mutantIndex > numToSelect - 1){
            System.out.println("Please enter a value between 0 and " + (numToSelect - 1));
            return selectMutant(); 
        }
        

        return mutantsToSelect.get(mutantIndex); 

    }

    public boolean userWantsToContinue(){
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Do you want to continue?");

        String input = myObj.nextLine();  // Read user input\
        return input.toLowerCase().startsWith("y");
    }
    public static void main(String[] args) throws IOException {
        MutationShoppingUI ui = new MutationShoppingUI("src/tracks/GameMarkovChain/MutationShopping/Test4", 4);
        ui.startShopping();


    }

    
}
