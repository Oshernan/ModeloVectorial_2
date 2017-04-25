
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import com.mongodb.BasicDBObject;

import org.bson.Document;
//import java.util.StringTokenizer;

public class Diccionarios {

	private static org.bson.Document idfDoc = new Document();
	private static int cargados;

	public static org.bson.Document getIdfDoc() {
		return idfDoc;
	}

	//Creacion del diccionario a partir del html
	public static void creardiccionario(String url, Conector mongo) throws IOException{
		idfDoc.append("_id", "idf");
		Path path = Paths.get(url);
		Map<String, Integer> map = new HashMap<String, Integer>();
		BasicDBObject query=new BasicDBObject("_id",url);
		Document myDoc = (Document) mongo.getCollectionPalabras().find(query).first();
		if(myDoc != null){
			System.out.println("Document already exists");
			
		//mongo.insertDiccionarios(url, map);
		}
		else{
			File file = new File(url);
			
			/*
			if (file.length()>2048000){
				System.out.println("Descartado..........."+ url);
				return null;
			}
			*/
			List<String> lines = Files.readAllLines(path);
			String out="";
			//int count=1;
			for (String line : lines) {
		        out=out+line;
		        //System.out.println("Linea leida  "+count+"....\n");
		        //count++;
		    }
			out = out.replace(">", "> ");
			org.jsoup.nodes.Document doc = Jsoup.parse(out);
			org.bson.Document doc1 = new Document();
			doc1.append("_id", url);
			//----
			
			String regex = "([^a-zA-Z0-9])"; //"([^a-zA-Z0-9\\.])|(\\.+\\s)"
			String[] str2 = doc.text().split(regex);
			for (int i=0; i<str2.length; i++){
				String value = str2[i].toLowerCase();
				if (!str2[i].equals("") && str2[i].length()>1){
					if (doc1.containsKey(value)){
						doc1.put(value, (int)doc1.get(value)+1);
					}
					else{
						doc1.put(value, 1);
						if (idfDoc.containsKey(value)){
							idfDoc.put(value, (int)idfDoc.get(value)+1);
						}else{
							idfDoc.put(value, 1);
						}
					}
			     }
			//System.out.println("palabra numero "+i+"..\n");	
			}
			mongo.insertDoc(doc1,"palabras");
			cargados++;
			System.out.println(cargados);
		}
	}
/*
		//----tokens
		StringTokenizer st = new StringTokenizer(doc.text()," ");  //con regex -- StringTokenizer(doc.text(), "-");
		//System.out.println("N�mero de palabras: " + st.countTokens());
		while (st.hasMoreTokens()) {
			String value = st.nextToken().toLowerCase();
			System.out.println(value);
			if (!value.equals("") && value.length()>1){
				if (map.containsKey(value)){
					map.put(value, (int)map.get(value)+1);
				}
				else{
					map.put(value, 1);
					if (idf.containsKey(value)){
						idf.put(value, (int)idf.get(value)+1);
					}else{
						idf.put(value, 1);
					}
				}    
			}
	    }
		return map;
	}*/
}
