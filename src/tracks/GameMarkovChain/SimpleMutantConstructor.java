package tracks.GameMarkovChain;

import java.io.IOException;

public class SimpleMutantConstructor implements MutantContructorInterface {
    MarkovChain chain; 

    public SimpleMutantConstructor (MarkovChain chain){
        this.chain = chain; 
    }

    public SimpleMutantConstructor(String csvFile) throws IOException{
        this.chain = new MarkovChain(); 
        chain.trainFromCsvFile(csvFile);
        
    }

    @Override
    public MutantInterface newMutant() {
        String game = chain.buildSimpleGame(); 
        String level = chain.buildLevel(game, 10); 
        return new GrammarMutant(game, level, chain); 
    }
    
}
