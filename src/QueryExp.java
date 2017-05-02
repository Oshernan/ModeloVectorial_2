import java.util.ArrayList;

import edu.smu.tspell.wordnet.*;

public class QueryExp {

	public String getSynonims(String query){
		
	
					String consEXP="";
					String[] palabra = query.split("[^a-zA-Z0-9]");
					WordsParser wp = new WordsParser();
					String[] pos = wp.getPOS(palabra);
					WordNetDatabase database = WordNetDatabase.getFileInstance();
					System.setProperty("wordnet.database.dir", "./dict");
					
					String[] wordForms = null;
					ArrayList<String> query1 = new ArrayList<String>();
					Synset[] synsets= null;
					for(int k= 0;k<palabra.length; k++){
					//Synset[] synsets = database.getSynsets(palabra[k]);
						synsets = database.getSynsets(palabra[k], SynsetType.NOUN);
						if(pos[k].equals("ADVERB")){
							synsets = database.getSynsets(palabra[k], SynsetType.ADVERB);
						}else
						if(pos[k].equals("VERB")){
							synsets = database.getSynsets(palabra[k], SynsetType.VERB);
						}else
						if(pos[k].equals("ADJECTIVE")){
							synsets = database.getSynsets(palabra[k], SynsetType.ADJECTIVE);
						}else
						{
							synsets = database.getSynsets(palabra[k], SynsetType.NOUN);
						}
						
					if (synsets.length > 0){
						for (int i = 0; i < synsets.length; i++)					
						{
						wordForms = synsets[i].getWordForms();
						for (int j = 0; j < wordForms.length; j++)
							
						{
							consEXP = consEXP +" "+wordForms[j];
						}
					}
					}
					}
					
					/*
					ArrayList<String> p= removeRepeats(consulta);
					for (int b=0; b<p.size(); b++){
						consEXP= consEXP +" "+ p.get(b);
					}
					*/
					return query+" "+consEXP;
					}//for1
				

			
	public ArrayList<String> removeRepeats(String consEXP){
		String[] consulta = consEXP.split(" ");
		boolean si=true;
		ArrayList<String> distintas= new ArrayList<String>();
		for(int i=0; i<consulta.length; i++){
			si=true;
			if(i>0){
			for(int a=0; a<distintas.size();a++){
					if (consulta[i].equals(distintas.get(a))){
						si=false;
					}
			}
			}
			if(si){
				distintas.add(consulta[i]);
			}
		}
		//limpiarLetras(distintas);
		return distintas;
	}
	private ArrayList<String> limpiarLetras(ArrayList<String> palabras){
		
		for(int i=0; i<palabras.size(); i++){
			if(palabras.get(i).length()<=2){
				palabras.remove(i);
			}
		}
		return palabras;
	}
			
			
			
			


		
	}
	
	

