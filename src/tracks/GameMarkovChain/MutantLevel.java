package tracks.GameMarkovChain;

public class MutantLevel {

    char[][] generalLevel; 

    public MutantLevel(String currentGame, String currentLevel){
        generalLevel = LevelChain.generalizedLevel(currentGame, currentLevel); 
    }

    public MutantLevel(char[][] generalLevel){

        this.generalLevel = generalLevel; 

    }

    public String getSpecificLevel(String game){
        return LevelChain.specifyLevel(game, generalLevel); 
    }
    
}
