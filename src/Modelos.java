//Libreria Lucene , indizar
import java.awt.Desktop;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;

public class Modelos {

	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//Quitar mensajes advertencia
		Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
		mongoLogger.setLevel(Level.SEVERE);
		//
		DocReader lector = new DocReader();
		Conector mongo = new Conector();
		int exit=0;
		ArrayList<Map<String,Integer>> indice = new ArrayList<Map<String,Integer>>();
		List<String> fileNames = loadpages();
		ArrayList<Double[]> datos = new ArrayList<Double[]>();
		//Borrado de archivos de consulta anteriores
		String path = "./outputs/";
	    File dir = new File(path);
	    File[] files = dir.listFiles();
	    for (File text: files){
	    	text.delete();
	    }
		
		while (exit ==0){
			
			System.out.println("Porfavor indique la operación que desea realizar\n"
					+ " Para crear los diccionarios pulse:  1\n"
					+ " Para leer las relevancias asignadas a consultas:  2\n"
					+ " Para evaluar el Motor a partir de las consultas fijadas:  3\n"
					+ " Para evaluar el Motor de forma expandida a partir de las consultas fijadas: 4\n"
					+ " Para salir pulse:  5\n");
			Scanner sc = new Scanner(System.in);
		    int input = sc.nextInt();
		    if (input < 1 || input >6) System.out.println("Opciï¿½n incorrecta\n");
		    else{
		    	switch(input){
		    	
		    	case(1):
		    		mongo.removeBD();
		    		//Create Index
					for(String file : fileNames){
						Diccionarios.creardiccionario(file,mongo);
						System.out.println("Diccionario "+file+" creado.....\n");
					}
					mongo.insertDoc(Diccionarios.getIdfDoc(),"idf");
					System.out.println("Diccionarios creados****************\n");
					break;
					
		    	case(2):
		    		mongo.resetFilesBD();
		    		mongo.insertDoc(lector.leerTopics(), "documentos");
		    		System.out.println("Se ha leido el fichero de las consultas");
		    		mongo.insertDoc(lector.leerUnion(), "relevancia");
		    		System.out.println("Se ha leido el fichero de las relevancias");
					break;
		    	case(3):
		    		
		    		Document doc =  (Document) mongo.getCollectionIdf().find().first();
					datos = consulta(mongo, (Document) doc, false);
					
					//Mostrar datos
					//final Object[][] table1 = view(datos, indice, fileNames);
					Scanner sc1 = new Scanner(System.in);
					System.out.println("Pulse ENTER para continuar\n\n");
					//export(table1, "consulta"+consulta+".txt");
					
					if(sc1.hasNextLine())break;
				
		    	case(4):
		    		Document docExp =  (Document) mongo.getCollectionIdf().find().first();
					datos = consulta(mongo, (Document) docExp, true);
					
					//Mostrar datos
					//final Object[][] table1 = view(datos, indice, fileNames);
					Scanner scExp = new Scanner(System.in);
					System.out.println("Pulse ENTER para continuar\n\n");
					//export(table1, "consulta"+consulta+".txt");
					
					if(scExp.hasNextLine())break;
		    		
		    		/*--------------
		    		if (indice.isEmpty()){
		    			System.out.println("Antes de realizar una consulta debe crear los diccionarios\n");
		    			break;
		    		}
		    		Realizar consulta ejemplos
					datos = consultasejemplos(indice, idf);
					Mostrar datos ejemplos
					//final Object[][] table2 = viewejemplos(datos, indice, fileNames);
					Scanner sc2 = new Scanner(System.in);
					System.out.println("Pulse ENTER para continuar\n\n");
					//export(table2, "consulta"+consulta+".txt");
					consulta ++;
					if(sc2.hasNextLine())break;
					*/
		    	break;
		    	case(5):
		    		//Exit
					exit=1;
		    		System.out.println("Saliendo...");
		    	break;
		    	case(6):
		    		//Exit
		    		QueryExp q= new QueryExp();
		    	String query= "What processor obtained the best score in 2009 for the Photoshop benchmark?";
		    	System.out.println(q.getSynonims(query));
					
		    	break;
		    	}	
		    }		
		}
	}
	
	//Recorre nuestro indice , y diccionario para elaborar los resultados
	public static ArrayList<Double[]> consulta(Conector mongo,Document idf, boolean expand) throws IOException{
		long num=mongo.getCollectionPalabras().count();
		Document relevancias = (Document) mongo.getCollectionRelevancia().find().first();
		Document queries =  (Document) mongo.getCollectionDocumentos().find().first();
		Iterator itQ = queries.entrySet().iterator();
		Double[] escalarTF = new Double[(int) num];
		Double[] escalarIDF = new Double[(int) num];
		Double[] cosenoTF = new Double[(int) num];
		Double[] cosenoIDF = new Double[(int) num];
		Double[]  acumuladorD= new Double[(int) num];
		Double[]  acumuladorIDF= new Double[(int) num];
		double acumuladorQidf= 0;
		double acumuladorQ = 0;
		String idC;
		String contC;
		FindIterable<Document> coleccion = mongo.getCollectionPalabras().find();
		ArrayList<Double[]> pesos = new ArrayList<Double[]>();
		Metricas met = new Metricas();
		/*Para recorrer todas las consultas*/
		  while(itQ.hasNext()){	  
				for (int i=0; i<num;i++){
					escalarTF[i]=(double) 0;
					escalarIDF[i]=(double) 0;
					cosenoTF[i]= (double) 0;
					acumuladorD[i]= (double)0;
					acumuladorIDF[i] = (double) 0;
					
				}
				Map<String,Double> cosenoTFIDF = new HashMap<String, Double>();
				String query;
				Document.Entry a = (Document.Entry)itQ.next();
				 if(!a.getKey().equals("_id")){
				idC= (String) a.getKey();
				contC = (String) a.getValue();
				query= contC;
				if (expand==true){
					QueryExp q= new QueryExp();
					query=q.getSynonims(query);
				}
				
				//Separamos la query y sus palabras
		        String[] palabra = query.split("[^a-zA-Z0-9]");
		        Map<String, Integer> map = new HashMap<String, Integer>();
		        //Creo mapa de la query
				for (int i=0; i<palabra.length; i++){
					String value = palabra[i].toLowerCase();
					if (!palabra[i].equals("") && palabra[i].length()>2){
						if (map.containsKey(value)){
							map.put(value, (int)map.get(value)+1);
						}
						else{
							map.put(value, 1);
						}
					}
				}
	
				//Busco la palabra en mis docus
				for (Entry<String, Integer> entry : map.entrySet()) {
					String word = entry.getKey();
					int i=0;
		        	for(Document diccionario : coleccion){
		        		if(diccionario.containsKey(word)){
		        			//Acumulo valores para cada html en mi indice
		        			escalarTF[i]+=Funcionalidades.escalarTF(diccionario.getInteger(word),entry.getValue());
		        			escalarIDF[i]+=Funcionalidades.escalarIDF(diccionario.getInteger(word), entry.getValue(),num,(double)idf.getInteger(word));
		        		}
		        		i++;
		        	}
		        	acumuladorQ+=Math.pow(entry.getValue(), 2);
		        	double idf1 = 0.0;
		        	if(idf.containsKey(word)){
		        		idf1 = Funcionalidades.idfword(num,(double)idf.getInteger(word));
		        	}
		        	acumuladorQidf+=(Math.pow(entry.getValue()*idf1, 2));
		        	
		        }
				int i=0;
				String docName=null;
		    	for(Document diccionario : coleccion){
		    		Iterator it = diccionario.entrySet().iterator();
		    		
		    		while (it.hasNext()) 
		    		{
		    			
		    			 Document.Entry e = (Document.Entry)it.next();
		    			 if(!e.getKey().equals("_id")){
		    			 //System.out.println(e.getKey()+"-"+e.getValue());
		    			acumuladorD[i]+=Math.pow((double)(int)e.getValue(), 2);
						acumuladorIDF[i]+=(Math.pow((double)(int) e.getValue()*Funcionalidades.idfword(num,(double)idf.getInteger(e.getKey())), 2));
	
		    			 }
		    			 else{
		    				 docName = (String) e.getValue();
		    			 }
		    		}
		    		cosenoTF[i]=Funcionalidades.cosenoTF(escalarTF[i], acumuladorD[i], acumuladorQ);
					if(Math.sqrt(acumuladorIDF[i]*acumuladorQidf)==0){
						/*System.out.println(acumuladorIDF[i]);
						System.out.println(acumuladorQidf);*/
						cosenoIDF[i]=(double) -1;
					}
					else{
						cosenoIDF[i]=Funcionalidades.cosenoTFIDF(escalarIDF[i], acumuladorIDF[i], acumuladorQidf);	
					}
					cosenoTFIDF.put(docName, cosenoIDF[i]);
					i++;
		    	}
	
				pesos.add(escalarTF);
				pesos.add(escalarIDF);
				pesos.add(cosenoTF);
				pesos.add(cosenoIDF);
				
				MapComparator bvc= new MapComparator(cosenoTFIDF);
				TreeMap<String, Double> smap = new TreeMap<String, Double>(bvc);
				smap.putAll(cosenoTFIDF);
				
				int numero=0;
				Map<String,Double> top = new HashMap<String,Double>();
				for (Entry<String, Double> entrada : smap.entrySet()) {
					if (numero<100){
					top.put(entrada.getKey(), entrada.getValue());
					numero++;
					}
				}
				//System.out.println(smap);
				MapComparator bvc2= new MapComparator(top);
				TreeMap<String, Double> smap100 = new TreeMap<String, Double>(bvc);
				smap100.putAll(top);
	
				double precision5 = met.precision(5, 1, smap100, idC, relevancias);
				double precision10 = met.precision(10, 1, smap100, idC, relevancias);
				double recall5 = met.recall(5, smap100, idC, relevancias);
				double recall10 = met.recall(10, smap100, idC, relevancias);
				double fmeasure5 = met.f_measure(precision5, recall5, 1);
				double fmeasure10 = met.f_measure(precision10, recall10, 1);
				double rrank1 = met.reciprocalRank(smap100, relevancias, idC, 1);
				double rrank2 = met.reciprocalRank(smap100, relevancias, idC, 2);
				double average = met.averageprecision(100, smap100, idC, relevancias);
				Double[] nDGC = met.nDGC(10, smap100, idC, relevancias);
				Double[] nDGC100 = met.nDGC(100, smap100, idC, relevancias);
				
				
				//System.out.println(smap100);
				/*System.out.println(query);
				System.out.println(idC);
				System.out.println("Precision 5: "+ met.roundFourDecimals(precision5));
				System.out.println("Precision 10: "+met.roundFourDecimals(precision10));
				System.out.println("Recall 5: "+met.roundFourDecimals(recall5));
				System.out.println("Recall 10: "+met.roundFourDecimals(recall10));
				System.out.println("Fmeasure 5: "+met.roundFourDecimals(fmeasure5));
				System.out.println("Fmeasure 10: "+met.roundFourDecimals(fmeasure10));
				System.out.println("Reciprocal Rank(relevancia min 1): "+met.roundFourDecimals(rrank1));
				System.out.println("Reciprocal Rank(relevancia min 2): "+met.roundFourDecimals(rrank2));
				System.out.println("Average Precision: "+met.roundFourDecimals(average));
				for(int k=0;k<nDGC.length;k++){
					System.out.print("nDGC: "+k +" "+met.roundFourDecimals(nDGC[k])+", ");
		    	}
				System.out.println("");
		    	for(int k=0;k<nDGC100.length;k++){
					System.out.print("nDGC: "+k +" "+met.roundFourDecimals(nDGC100[k])+", ");
		    	}
		    	System.out.println("");
		    	System.out.println("");
		    	*/
				export(precision5, precision10, recall5, recall10, fmeasure5, fmeasure10, rrank1, rrank2, average, nDGC, nDGC100, query, idC, expand);
			  }
		  }
		return pesos;
	}
	//Carga el nombre de los documuntos html
	public static List<String> loadpages() throws IOException{
		String path = "./pags/";
		List<String> names = new ArrayList<String>();
	    File dir = new File(path);
	    File[] files = dir.listFiles();
	    for(int i = 0; i < files.length; i++){
	    	
	    	File[] filesD = files[i].listFiles();
	    	for(int j = 0; j < filesD.length; j++){
	    		names.add(filesD[j].toString());   
	    	}
	    	
	    }
	    return names;
	}
	
	//Extraer resultados a un archivo .txt
	public static void export( double precision5,double precision10,double recall5,double recall10,double fmeasure5,double fmeasure10,double rrank1,double rrank2,double average, Double[] nDGC, Double[] nDGC100, String query, String idC, boolean expand ) throws IOException{
		File file = new File("./outputs/", idC);
		if (expand == true)  file = new File("./outputs/", idC+"Exp");
		Metricas met = new Metricas();
		FileWriter archivo = new FileWriter(file);
		archivo.write("Consulta: "+query+"\n");
		archivo.write("ID consulta: "+idC+"\n");
		archivo.write("Precision 5: "+ met.roundFourDecimals(precision5)+"\n");
		archivo.write("Precision 10: "+ met.roundFourDecimals(precision10)+"\n");
		archivo.write("Recall 5: "+ met.roundFourDecimals(recall5)+"\n");
		archivo.write("Recall 10: "+ met.roundFourDecimals(recall10)+"\n");
		archivo.write("Fmeasure 5: "+ met.roundFourDecimals(fmeasure5)+"\n");
		archivo.write("Fmeasure 10: "+ met.roundFourDecimals(fmeasure10)+"\n");
		archivo.write("Reciprocal Rank(relevancia min 1): "+met.roundFourDecimals(rrank1)+"\n");
		archivo.write("Reciprocal Rank(relevancia min 2): "+met.roundFourDecimals(rrank2)+"\n");
		archivo.write("Average Precision: "+met.roundFourDecimals(average)+"\n");
		archivo.write("nDGC10: \n");
		for(int k=0;k<nDGC.length;k++){
			archivo.write(met.roundFourDecimals(nDGC[k])+"; ");
    	}
		archivo.write("\n");
		archivo.write("nDGC100: \n");
    	for(int k=0;k<nDGC100.length;k++){
    		archivo.write(met.roundFourDecimals(nDGC100[k])+"; ");
    	}
    	archivo.write("\n");

		archivo.close();
		if (expand == true) System.out.println(" Consulta guardada en la carpeta outputs, nombre : "+ idC+"Exp");
		else System.out.println(" Consulta guardada en la carpeta outputs, nombre : "+ idC);
	}

	
}
