import javax.xml.parsers.DocumentBuilderFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.bson.Document;

public class DocReader {

	public static  Document leerUnion(){
        File dir = new File("./docs/2010.union.trel");
        FileReader fr = null;
        BufferedReader br = null;
        Document doc = new Document("_id","union");
        try{
            fr= new FileReader(dir);
            br=new BufferedReader(fr);
            String ln;
            while((ln=br.readLine())!=null){
                String[] sp = ln.split("\t");
                doc.append(sp[1]+"|"+sp[2], sp[3]);
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return doc;
    }
    public static Document leerTopics(){
        File fXmlFile = new File("./docs/2010-topics.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Document r = new Document("_id","topic");
        try{
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("topic");
            for(int temp = 0; temp < nList.getLength(); temp++){
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String clave = eElement.getAttribute("id");
                    String consulta = eElement.getElementsByTagName("title").item(0).getTextContent();
                    r.append(clave, consulta);
                }
            }
        }catch(Exception e){
             e.printStackTrace();
        }
        return r;
    }
	
}
