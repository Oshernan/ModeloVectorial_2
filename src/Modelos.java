//Libreria Lucene , indizar
import java.awt.Desktop;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
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
		int consulta=1;
		while (exit ==0){
			
			System.out.println("Porfavor indique la operaci�n que desea realizar\n"
					+ " Para crear los diccionarios pulse:  1\n"
					+ " Para recuperar las consultas:  2\n"
					+ " Para realizar una consulta pulse:  3\n"
					+ " Para ver consultas de ejemplo pulse: 4\n"
					+ " Para salir pulse:  5\n");
			Scanner sc = new Scanner(System.in);
		    int input = sc.nextInt();
		    if (input < 1 || input >4) System.out.println("Opci�n incorrecta\n");
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
		    		/*if (indice.isEmpty()){
		    			System.out.println("Antes de realizar una consulta debe crear los diccionarios\n");
		    			break;
		    		}*/
		    		//Realizar consulta*********************
		    		Document doc =  (Document) mongo.getCollectionIdf().find().first();
					datos = consulta(mongo, (Document) doc, "");
					
					//Mostrar datos
					//final Object[][] table1 = view(datos, indice, fileNames);
					Scanner sc1 = new Scanner(System.in);
					System.out.println("Pulse ENTER para continuar\n\n");
					//export(table1, "consulta"+consulta+".txt");
					consulta ++;
					if(sc1.hasNextLine())break;
				
		    	case(4):
		    		if (indice.isEmpty()){
		    			System.out.println("Antes de realizar una consulta debe crear los diccionarios\n");
		    			break;
		    		}
		    		/* Realizar consulta ejemplos
					datos = consultasejemplos(indice, idf);
					Mostrar datos ejemplos*/
					//final Object[][] table2 = viewejemplos(datos, indice, fileNames);
					Scanner sc2 = new Scanner(System.in);
					System.out.println("Pulse ENTER para continuar\n\n");
					//export(table2, "consulta"+consulta+".txt");
					consulta ++;
					if(sc2.hasNextLine())break;
		    	break;
		    	case(5):
		    		//Exit
					exit=1;
		    		System.out.println("Finalizando...");
		    	break;
		    	}	
		    }		
		}
	}
	
	//Recorre nuestro indice , y diccionario para elaborar los resultados
	public static ArrayList<Double[]> consulta(Conector mongo,Document idf, String query1){
		long num=mongo.getCollectionPalabras().count();
		Double[] escalarTF = new Double[(int) num];
		Double[] escalarIDF = new Double[(int) num];
		Double[] cosenoTF = new Double[(int) num];
		Double[] cosenoIDF = new Double[(int) num];
		Double[]  acumuladorD= new Double[(int) num];
		Double[]  acumuladorIDF= new Double[(int) num];
		double acumuladorQidf= 0;
		double acumuladorQ = 0;
		ArrayList<Double[]> pesos = new ArrayList<Double[]>();

		for (int i=0; i<num;i++){
			escalarTF[i]=(double) 0;
			escalarIDF[i]=(double) 0;
			cosenoTF[i]= (double) 0;
			acumuladorD[i]= (double)0;
			acumuladorIDF[i] = (double) 0;
			
		}
		String query;
		/*if (query1==""){
			System.out.println("Introduzca la consulta que desea realizar: ");
			Scanner entradaEscaner = new Scanner (System.in); //Creaci�n de un objeto Scanner, para leer la consulta
	        query = entradaEscaner.nextLine();
		}
		else{
			//String query = "What video game won Spike best driving game award in 2006";
			
			query = query1;
		}*/
		query= "What processor obtained the best score in 2009 for the Photoshop benchmark?";
		//Separamos la query y sus palabras
        String[] palabra = query.split("[^a-zA-Z0-9]");
        Map<String, Integer> map = new HashMap<String, Integer>();
        //Creo mapa de la query
		for (int i=0; i<palabra.length; i++){
			String value = palabra[i].toLowerCase();
			if (!palabra[i].equals("") && palabra[i].length()>1){
				if (map.containsKey(value)){
					map.put(value, (int)map.get(value)+1);
				}
				else{
					map.put(value, 1);
				}
			}
		}
		FindIterable<Document> coleccion = mongo.getCollectionPalabras().find();
		for (Document diccionario : coleccion){
	
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
    	for(Document diccionario : coleccion){
    		Iterator it = diccionario.entrySet().iterator();
    		
    		while (it.hasNext()) 
    		{
    			
    			 Document.Entry e = (Document.Entry)it.next();
    			 if(!e.getKey().equals("_id")&&i<=1966){
    			 //System.out.println(e.getKey()+"-"+e.getValue());
    			acumuladorD[i]+=Math.pow((double)(int)e.getValue(), 2);
				acumuladorIDF[i]+=(Math.pow((double)(int) e.getValue()*Funcionalidades.idfword(num,(double)idf.getInteger(e.getKey())), 2));

    			 }
    		}
    		cosenoTF[i]=Funcionalidades.cosenoTF(escalarTF[i], acumuladorD[i], acumuladorQ);
			cosenoIDF[i]=Funcionalidades.cosenoTFIDF(escalarIDF[i], acumuladorIDF[i], acumuladorQidf);
			i++;
    	}

		/*
		for (int i =0; i<num; i ++){
			for (Entry<String, Integer> entry : indice.get(i).entrySet()) {
				acumuladorD[i]+=Math.pow(entry.getValue(), 2);
				acumuladorIDF[i]+=(Math.pow(entry.getValue()*Funcionalidades.idfword((double)indice.size(),(double)idf.get(entry.getKey()).intValue()), 2));
			}
			cosenoTF[i]=Funcionalidades.cosenoTF(escalarTF[i], acumuladorD[i], acumuladorQ);
			cosenoIDF[i]=Funcionalidades.cosenoTFIDF(escalarIDF[i], acumuladorIDF[i], acumuladorQidf);
		}	
		*/
		pesos.add(escalarTF);
		pesos.add(escalarIDF);
		pesos.add(cosenoTF);
		pesos.add(cosenoIDF);
		for(int a=0;a<cosenoIDF.length; a++){
			System.out.println(cosenoIDF[a]);
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
	
	/*Muestra la tabla de resultados de la consulta 
	public static Object[][] view(ArrayList<Double[]> datos, ArrayList<Map<String,Integer>> indice, List<String> fileNames ){
		final Object[][] table = new String[indice.size()+1][];
		table[0]= new String[] { "Docs ", "escalar TF", "escalar IDF", "coseno TF", "coseno IDF" };
		for(int i=0; i<indice.size();i++){	
			table[i+1] = new String[] {fileNames.get(i), datos.get(0)[i].toString(), String.format("%.4f", datos.get(1)[i]),String.format("%.4f", datos.get(2)[i]),String.format("%.4f", datos.get(3)[i]) };
		}
		for (final Object[] row : table) {
		    System.out.format("%-33s%-15s%-15s%-15s%-15s\n", row);
		}
		System.out.println("\n\n");
		return table;
	}
	
	//Realiza las consultas de ejemplos establecidas en el enunciado
	public static ArrayList<Double[]> consultasejemplos (ArrayList<Map<String,Integer>> indice, Map<String,Integer> idf){
		ArrayList<Double[]> datosQ1 = new ArrayList<Double[]>();
		ArrayList<Double[]> datosQ2 = new ArrayList<Double[]>();
		ArrayList<Double[]> datosQ3 = new ArrayList<Double[]>();
		datosQ1 = consulta(indice, idf, "What video game won Spike�s best driving game award in 2006?");
		datosQ2 = consulta(indice, idf, "What is the default combination of Kensington cables?");
		datosQ3 = consulta(indice, idf, "Who won the first ACM Gerard Salton prize?");
		
		ArrayList<Double[]> Q123 = new ArrayList<Double[]>();
		for (int i =0; i<4;i++){
			Q123.add(datosQ1.get(i));
			Q123.add(datosQ2.get(i));
			Q123.add(datosQ3.get(i));
		}
		
		return Q123;
	}
	
	//Crea la tabla de visualizacion de los ejemplos de querys requeridos
	public static Object[][] viewejemplos(ArrayList<Double[]> datos, ArrayList<Map<String,Integer>> indice, List<String> fileNames ){
		final Object[][] table = new String[((indice.size()+1)*4)+1][];
		table[0]= new String[] { "Docs ", "Q1", "Q2", "Q3" };
		table[1]= new String[] { "* RELEVANCIA: ProductoEscalarTF", "......", "......", "......"};
		for(int i=0; i<indice.size();i++){
			table[i+2] = new String[] {fileNames.get(i), datos.get(0)[i].toString(), datos.get(1)[i].toString(),datos.get(2)[i].toString()};
		}
		table[indice.size()+1+1]= new String[] { "* RELEVANCIA: ProductoEscalarTFIDF", "......", "......", "......"};
		for(int i=0; i<indice.size();i++){
			table[i+indice.size()+1+2] = new String[] {fileNames.get(i), String.format("%.4f",datos.get(3)[i]), String.format("%.4f",datos.get(4)[i]), String.format("%.4f",datos.get(5)[i])};
		}
		table[((indice.size()+1)*2)+1]= new String[] { "* RELEVANCIA: CosenoTF", "......", "......", "......"};
		for(int i=0; i<indice.size();i++){
			table[i+((indice.size()+1)*2)+2] = new String[] {fileNames.get(i), String.format("%.4f",datos.get(6)[i]), String.format("%.4f",datos.get(7)[i]), String.format("%.4f",datos.get(8)[i])};
		}
		table[((indice.size()+1)*3)+1]= new String[] { "* RELEVANCIA: CosenoTFIDF", "......", "......", "......"};
		for(int i=0; i<indice.size();i++){
			table[i+((indice.size()+1)*3)+2] = new String[] {fileNames.get(i), String.format("%.4f",datos.get(9)[i]), String.format("%.4f",datos.get(10)[i]),String.format("%.4f",datos.get(11)[i])};
		}
		
		for (final Object[] row : table) {
		    System.out.format("%-33s%-15s%-15s%-15s\n", row);
		}
		System.out.println("\n\n");
		
		return table;
	}
	
	//Extrae la tabla de resultados a un archivo .txt
	public static void export(final Object[][] table1, String output) throws IOException{
		File file = new File("./outputs/", output);
		FileWriter archivo = new FileWriter(file);
		for (int i=0;i<table1.length;i++){
			for (int j=0; j<table1[i].length;j++){
				archivo.write((table1[i][j]).toString());
				archivo.write("\t\t\t");
			}
			archivo.write("\n");
		}
		archivo.close();
		System.out.println(" Consulta guardada en la carpeta outputs, nombre : "+ output);
	}
*/
}
