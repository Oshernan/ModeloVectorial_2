import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;

public class WordsParser {
	
	private InputStream model;
	private Tokenizer token;
	private POSTaggerME _posTagger;
	
	public WordsParser(){
		
		try {
			   // Loading tokenizer model
			   model = getClass().getResourceAsStream("/en-pos-maxent.bin");
			   final POSModel posModel = new POSModel(model);
			   model.close();
			 
			   _posTagger = new POSTaggerME(posModel);
			 
			} catch (final IOException ioe) {
			   ioe.printStackTrace();
			} finally {
			   if (model != null) {
			      try {
			         model.close();
			      } catch (final IOException e) {} // oh well!
			   }
			}

	}
	
	
	public String[] getPOS(String [] tokens){
		String [] vuelta=_posTagger.tag(tokens);
		
		return convertSynseType(vuelta);
	}
	
	private String [] convertSynseType(String [] pos){
		String [] transform = new String[pos.length];
		for(int i=0;i<pos.length;i++){
			if(pos[i].equals("NN")||pos[i].equals("NNS")||pos[i].equals("NNP")||pos[i].equals("NNPS")){
				transform[i]="NOUN";
			}else {
				transform[i]="No";
			}
		}
		return transform;
	}

	/*public static void demoDP(LexicalizedParser lp, String filename) {
		  // This option shows loading and sentence-segment and tokenizing
		  // a file using DocumentPreprocessor
		  TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		  GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		  // You could also create a tokenier here (as below) and pass it
		  // to DocumentPreprocessor
		  for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
		    Tree parse = lp.apply(sentence);
		    parse.pennPrint();
		    System.out.println();
		    
		    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		    Collection tdl = gs.typedDependenciesCCprocessed(true);
		    System.out.println(tdl);
		    System.out.println();
		  }
		}*/
}
