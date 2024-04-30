package tracks.GameMarkovChain;

import java.io.IOException;

public class GrammarConstructor implements MutantContructorInterface {
    MarkovChain chain; 

    public GrammarConstructor (MarkovChain chain){
        this.chain = chain; 
    }

    public GrammarConstructor(String csvFile) throws IOException{
        this.chain = new MarkovChain(); 
        chain.trainFromCsvFile(csvFile);
        
    }

    @Override
    public MutantInterface newMutant() {
        return new GrammarMutant(chain); 
    }
    
}
