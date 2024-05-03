package tracks.GameMarkovChain;

public class ConstantConstructor implements MutantContructorInterface{
    String game; 
    String level; 
    MarkovChain chain;
    int mutations;  
    public ConstantConstructor(String game, String level, MarkovChain chain, int numMutations){
        this.game = game; 
        this.level = level;
        this.chain = chain;  
        mutations = numMutations;
    }
    @Override
    public MutantInterface newMutant() {
        MutantInterface mutant  = new GrammarMutant(game, level, chain);
        for(int i = 0; i < mutations; i ++){
            mutant = mutant.mutate();
        }

        return mutant; 
    }
    
}
