import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.bson.Document;

public class Metricas {

	public Metricas(){
		
	}
    public double precision (int cortes,TreeMap<String, Double> smap, String queryId, Document relevancias){
        //Double precision = null;
        //Document relevancias = (Document) mongo.getCollectionRelevancia().find().first();
        
        int i =1;
        int rec=0;
            for(Entry<String, Double> entry : smap.entrySet()){
                if(i>cortes) break;
                //System.out.println(entry.getKey()+" "+ entry.getValue());
                Iterator it = relevancias.entrySet().iterator();
                while (it.hasNext()) {
                    Document.Entry e = (Document.Entry)it.next();
                    if(!e.getKey().equals("_id")&&(((String) e.getKey()).contains(queryId+"|"+entry.getKey()))&& (Integer.parseInt(e.getValue().toString())>=1)){
                        //System.out.println(e.getKey()+ " "+e.getValue() + "----------");
                        //Contamos
                        rec++;
                    }
                }
            i++;
            }
            double result = (double)rec /(double)cortes;
        return result;
    }
    
    public double recall (int cortes,TreeMap<String, Double> smap, String queryId, Document relevancias){
    	double relevantes = (double)documentosRelevantes(relevancias,queryId);
        int i =1;
        int rec=0;
            for(Entry<String, Double> entry : smap.entrySet()){
                if(i>cortes) break;
                //System.out.println(entry.getKey()+" "+ entry.getValue());
                Iterator it = relevancias.entrySet().iterator();
                while (it.hasNext()) {
                    Document.Entry e = (Document.Entry)it.next();
                    if(!e.getKey().equals("_id")&&(((String) e.getKey()).contains(queryId+"|"+entry.getKey()))&& (Integer.parseInt(e.getValue().toString())>=1)){
                        //System.out.println(e.getKey()+ " "+e.getValue() + "----------");
                        //Contamos
                        rec++;
                    }
                }
            i++;
            }
            double result = (double)rec/relevantes;
        return result;
    }
    public double f_measure (double precision, double recall, double ratio){
    	double result = 0.0;
    	double numerador = Math.pow(1+ratio, 2) * precision * recall;
    	double denominador = (Math.pow(ratio, 2) * precision) + recall;
        return numerador/denominador;
    }
    public double reciprocalRank (TreeMap<String, Double> smap,Document relevancias, String queryId,int relevancia){
    	double pos= (double)documentosRelevantesPosicion(smap, relevancias, queryId, relevancia);
        return 1/pos;
    }
    
    private int documentosRelevantes(Document relevancias, String queryID){
    	Iterator it = relevancias.entrySet().iterator();
    	int count=0;
        while (it.hasNext()) {
        	Document.Entry e = (Document.Entry)it.next();
        	String todo=(String) e.getKey();
        	
        	if(!e.getKey().equals("_id")&&(((String) e.getKey()).contains(queryID)))
        	{
        		if (Integer.parseInt(e.getValue().toString())>=1){
        			count ++;
        		}
        				
        	}
        }
    	return count;
    }
    public int documentosRelevantesPosicion(TreeMap<String, Double> smap,Document relevancias, String queryId,int relevancia){

    	boolean finding = true;
        int rec=0;
            for(Entry<String, Double> entry : smap.entrySet()){
                if(!finding) break;
                rec++;
                //System.out.println(entry.getKey()+" "+ entry.getValue());
                Iterator it = relevancias.entrySet().iterator();
                while (it.hasNext()) {
                    Document.Entry e = (Document.Entry)it.next();
                    if(!e.getKey().equals("_id")&&(((String) e.getKey()).contains(queryId+"|"+entry.getKey()))&& (Integer.parseInt(e.getValue().toString())>=relevancia)){
                        //System.out.println(e.getKey()+ " "+e.getValue() + "----------");
                        //Encontrado
                        finding = false;
                    }
                }
            }
        
        return rec;
    }
    private void roundFourDecimals(double d) {
        DecimalFormat fourDForm = new DecimalFormat("0.0000");
        System.out.println(fourDForm.format(d));
    }
}
