import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.bson.Document;

public class Metricas {

	public Metricas(){
		
	}
    public double precision (int cortes, int min_rel,TreeMap<String, Double> smap, String queryId, Document relevancias){
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
                    if(!e.getKey().equals("_id")&&(((String) e.getKey()).contains(queryId+"|"+entry.getKey()))&& (Integer.parseInt(e.getValue().toString())>=min_rel)){
                        //System.out.println(e.getKey()+ " "+e.getValue() + "----------");
                        //Contamos
                        rec++;
                        break;
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
                        break;
                    }
                }
            i++;
            }
            double result = (double)rec/relevantes;
        return result;
    }
    public double f_measure (double precision, double recall, double ratio){
    	double result = 0.0;
    	double numerador = (1 + Math.pow(ratio, 2)) * precision * recall;
    	double denominador = (Math.pow(ratio, 2) * precision) + recall;
        return numerador/denominador;
    }
    public double reciprocalRank (TreeMap<String, Double> smap,Document relevancias, String queryId,int relevancia){
    	double pos= (double)documentosRelevantesPosicion(smap, relevancias, queryId, relevancia);
        return 1/pos;
    }
    
    public double averageprecision (int cortes, TreeMap<String, Double> smap, String queryId, Document relevancias){
    	//Sumatorio nmerador
    	double numerador=0.0;
    	double i=0.0;
    	Iterator it = relevancias.entrySet().iterator();
        while (it.hasNext()) {
            Document.Entry e = (Document.Entry)it.next();
            if(!e.getKey().equals("_id")&&(((String) e.getKey()).contains(queryId+"|"))&& (Integer.parseInt(e.getValue().toString())>=1)){
                //System.out.println(e.getKey()+ " "+e.getValue() + "----------");
                i++;
                String[] findrel = e.getKey().toString().split("[\\|]");
	            String html = findrel[1];
	            double j=0.0;
	            for(Entry<String, Double> entry : smap.entrySet()){
	            	j++;
	                if(entry.getKey().equals(html)){
	                	numerador=numerador+(i/j);
	                	//System.out.println(i+" "+j+" "+ numerador);
	                	break;
	                }
	            }
            }       
        }
        int denominador = cortes;
    	double result= numerador/denominador;
    	return result;
    }
    
    public Double[] nDGC (int cortes, TreeMap<String, Double> smap, String queryId, Document relevancias){
    	
		Double[] DGC = calcularDGC(cortes,smap,  queryId, relevancias);
    	Document auxorder = ordenarRelevancias(relevancias, queryId);
    	Double[] DGCi = calcularDGCideal(cortes,smap,  queryId, auxorder);
    	//System.out.println(auxorder);
    	//Meter todo en una funcion que haga todo y devuelva el valor DGC, pasar relevancias o auxorder
    	Double[] nDGC = new Double[cortes];
    	for (int i =0; i <DGC.length;i++){
    		if (DGC[i]==0)nDGC[i]=0.0;
    		else nDGC[i] = DGC[i]/DGCi[i];
    	}    	
    	return nDGC;
    }
    
    private static Double[] calcularDGC (int cortes,TreeMap<String, Double> smap, String queryId, Document relevancias){
    	int i =0;
    	Double[] G = new Double[cortes];
    	Double[] CG = new Double[cortes];
    	
		for(Entry<String, Double> entry : smap.entrySet()){
			 boolean find = false;
			 if(i<cortes){
				 Iterator it = relevancias.entrySet().iterator();
			     while (it.hasNext()) {
			         Document.Entry e = (Document.Entry)it.next();
			         if(!e.getKey().equals("_id")&&(((String) e.getKey()).contains(queryId+"|"+entry.getKey()))){
			        	 if (i!=0){
			        		 CG[i]=CG[i-1]+ Double.parseDouble(e.getValue().toString());
			        	 }else{
			        		 CG[i]=Double.parseDouble(e.getValue().toString());
			        	 }
			        	 G[i]=Double.parseDouble(e.getValue().toString());
			        	 i++;
			        	 find =true;
			        	 break;
			         }
			     }
			     if (find==false){
			    	 if(i==0){
				    	 CG[i]=0.0;
				     }
			         else{
			        	 CG[i]=CG[i-1];
			         }
			         G[i]=0.0;
			         i++;
			     } 
			 }  
		}
    	Double[] DGC=new Double[cortes];
    	for (int j=0;j<cortes;j++){
    		if (j<1){
    			DGC[j]=CG[j];
    		}else{
    			if(G[j]!=0) DGC[j]=(double)(DGC[j-1]+((double)G[j]/(double)(Math.log10(j+1)/ (double)Math.log10(2))));
    			else DGC[j]=DGC[j-1];
    		}
    	}
    	return DGC;
    }
    
    private static Double[] calcularDGCideal (int cortes,TreeMap<String, Double> smap, String queryId, Document relevancias){
    	int i =0;
    	Double[] G = new Double[cortes];
    	Double[] CG = new Double[cortes];
	    Iterator it = relevancias.entrySet().iterator();
	    while (it.hasNext() && i<cortes) {
	         Document.Entry e = (Document.Entry)it.next();
        	 if (i!=0){
        		 CG[i]=CG[i-1]+ Double.parseDouble(e.getValue().toString());
        	 }else{
        		 CG[i]=Double.parseDouble(e.getValue().toString());
        	 }
        	 G[i]=Double.parseDouble(e.getValue().toString());
        	 i++;
	    }     
    	Double[] DGC=new Double[cortes];
    	for (int j=0;j<cortes;j++){
    		if (j<1){
    			DGC[j]=CG[j];
    		}else{
    			if(G[j]!=0) DGC[j]=(double)(DGC[j-1]+((double)G[j]/(double)(Math.log10(j+1)/ (double)Math.log10(2))));
    			else DGC[j]=DGC[j-1];
    		}
    	}
    	/*for(int k=0;k<G.length;k++){
    		System.out.println(G[k]);
    	}
    	for(int k=0;k<CG.length;k++){
    		System.out.println(CG[k]);
    	}
    	for(int k=0;k<DGC.length;k++){
    		System.out.println(DGC[k]);
    	}*/
    	return DGC;
    }
    private static Document ordenarRelevancias(Document relevancias, String queryId){
    	Document aux = new Document();
    	Iterator it = relevancias.entrySet().iterator();
        while (it.hasNext()) {
            Document.Entry e = (Document.Entry)it.next();
            if(!e.getKey().equals("_id")&&(((String) e.getKey()).contains(queryId+"|"))&& (Integer.parseInt(e.getValue().toString())==2)){
                //System.out.println(e.getKey()+ " "+e.getValue() + "----------");
                aux.append(e.getKey().toString(), e.getValue());
            }
        }
        it = relevancias.entrySet().iterator();
        while (it.hasNext()) {
            Document.Entry e = (Document.Entry)it.next();
            if(!e.getKey().equals("_id")&&(((String) e.getKey()).contains(queryId+"|"))&& (Integer.parseInt(e.getValue().toString())==1)){
                //System.out.println(e.getKey()+ " "+e.getValue() + "----------");
                aux.append(e.getKey().toString(), e.getValue());
            }
        }
        it = relevancias.entrySet().iterator();
        while (it.hasNext()) {
            Document.Entry e = (Document.Entry)it.next();
            if(!e.getKey().equals("_id")&&(((String) e.getKey()).contains(queryId+"|"))&& (Integer.parseInt(e.getValue().toString())==0)){
                //System.out.println(e.getKey()+ " "+e.getValue() + "----------");
                aux.append(e.getKey().toString(), e.getValue());
            }
        }
        it = relevancias.entrySet().iterator();
        while (it.hasNext()) {
            Document.Entry e = (Document.Entry)it.next();
            if(!e.getKey().equals("_id")&&(((String) e.getKey()).contains(queryId+"|"))&& (Integer.parseInt(e.getValue().toString())==-1)){
                //System.out.println(e.getKey()+ " "+e.getValue() + "----------");
                aux.append(e.getKey().toString(), e.getValue());
            }
        }
    	return aux;
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
    public String roundFourDecimals(double d) {
        DecimalFormat fourDForm = new DecimalFormat("0.0000");
        //System.out.println(fourDForm.format(d));
        return fourDForm.format(d);
    }
}
