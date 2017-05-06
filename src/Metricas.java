import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.bson.Document;

public class Metricas {

	public double getPrecision5() {
		return precision5;
	}
	public void setPrecision5(double precision5) {
		this.precision5 = precision5;
	}
	public double getPrecision10() {
		return precision10;
	}
	public void setPrecision10(double precision10) {
		this.precision10 = precision10;
	}
	public double getRecall5() {
		return recall5;
	}
	public void setRecall5(double recall5) {
		this.recall5 = recall5;
	}
	public double getRecall10() {
		return recall10;
	}
	public void setRecall10(double recall10) {
		this.recall10 = recall10;
	}
	public double getF_measure5() {
		return f_measure5;
	}
	public void setF_measure5(double f_measure5) {
		this.f_measure5 = f_measure5;
	}
	public double getF_measure10() {
		return f_measure10;
	}
	public void setF_measure10(double f_measure10) {
		this.f_measure10 = f_measure10;
	}
	public double getR_rank1() {
		return r_rank1;
	}
	public void setR_rank1(double r_rank1) {
		this.r_rank1 = r_rank1;
	}
	public double getR_rank2() {
		return r_rank2;
	}
	public void setR_rank2(double r_rank2) {
		this.r_rank2 = r_rank2;
	}
	public double getAveragePrecision() {
		return averagePrecision;
	}
	public void setAveragePrecision(double averagePrecision) {
		this.averagePrecision = averagePrecision;
	}

	private double precision5;
	private double precision10;
	private double recall5;
	private double recall10;
	private double f_measure5;
	private double f_measure10;
	private double r_rank1;
	private double r_rank2;
	private double averagePrecision;
	private double NDCG10;
	private double NDCG100;
	
	public double getNDCG10() {
		return NDCG10;
	}
	public void setNDCG10(double nDCG10) {
		NDCG10 = nDCG10;
	}
	public double getNDCG100() {
		return NDCG100;
	}
	public void setNDGC100(double nDCG100) {
		NDCG100 = nDCG100;
	}
	public Metricas(){
		
	}
    public void precision5 (int min_rel,TreeMap<String, Double> smap, String queryId, Document relevancias){
        //Double precision = null;
        //Document relevancias = (Document) mongo.getCollectionRelevancia().find().first();
        
        int i =1;
        int rec=0;
            for(Entry<String, Double> entry : smap.entrySet()){
                if(i>5) break;
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
            double result = (double)rec /(double)5;
            this.precision5= result;

    }
    public void precision10 (int min_rel,TreeMap<String, Double> smap, String queryId, Document relevancias){
        //Double precision = null;
        //Document relevancias = (Document) mongo.getCollectionRelevancia().find().first();
        
        int i =1;
        int rec=0;
            for(Entry<String, Double> entry : smap.entrySet()){
                if(i>10) break;
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
            double result = (double)rec /(double)10;
            this.precision10= result;
    }
    
    public void recall5 (TreeMap<String, Double> smap, String queryId, Document relevancias){
    	double relevantes = (double)documentosRelevantes(relevancias,queryId);
        int i =1;
        int rec=0;
            for(Entry<String, Double> entry : smap.entrySet()){
                if(i>5) break;
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
            this.recall5= result;
    }
    public void recall10 (TreeMap<String, Double> smap, String queryId, Document relevancias){
    	double relevantes = (double)documentosRelevantes(relevancias,queryId);
        int i =1;
        int rec=0;
            for(Entry<String, Double> entry : smap.entrySet()){
                if(i>10) break;
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
        this.recall10=result;
    }
    public void f_measure5 (double precision5, double recall5, double ratio){
    	double result = 0.0;
    	double numerador=0.0;
    	double denominador=0.0;
    	if(precision5!=0.0||recall5!=0){
        	numerador = (1 + Math.pow(ratio, 2)) * precision5 * recall5;
        	denominador = (Math.pow(ratio, 2) * precision5) + recall5;
        	this.f_measure5=numerador/denominador;
        	
    	}else{
    	
        this.f_measure5= result;
    	}
    }
    public void f_measure10 (double precision10, double recall10, double ratio){
    	double result = 0.0;
    	double numerador=0.0;
    	double denominador=0.0;
    	if(precision10!=0.0||recall10!=0){
        	numerador = (1 + Math.pow(ratio, 2)) * precision10 * recall10;
        	denominador = (Math.pow(ratio, 2) * precision10) + recall10;
        	this.f_measure10=numerador/denominador;
    	}
    	else{
    		this.f_measure10= result;
    	}
        
    }
    public void reciprocalRank1 (TreeMap<String, Double> smap,Document relevancias, String queryId){
    	double pos= (double)documentosRelevantesPosicion(smap, relevancias, queryId, 1);
        this.r_rank1= 1/pos;
    }
    public void reciprocalRank2 (TreeMap<String, Double> smap,Document relevancias, String queryId){
    	double pos= (double)documentosRelevantesPosicion(smap, relevancias, queryId, 2);
        this.r_rank2= 1/pos;
    }
    
    public void averageprecision (TreeMap<String, Double> smap, String queryId, Document relevancias){
    	//Sumatorio nmerador
        double numerador=0.0;
        double i=0.0;
        double j=0.0;
        for(Entry<String, Double> entry : smap.entrySet()){
             if(i>100) break;
             j++;
             //System.out.println(entry.getKey()+" "+ entry.getValue());
             Iterator it = relevancias.entrySet().iterator();
             while (it.hasNext()) {
                 Document.Entry e = (Document.Entry)it.next();
                 if((((String) e.getKey()).contains(queryId+"|"+entry.getKey()))&& (Integer.parseInt(e.getValue().toString())>=1)){
                 //System.out.println(e.getKey()+ " "+e.getValue() + "----------");
                 //Contamos
                     i++;
                     numerador=numerador+(i/j);
                     break;
                 }
             }
        i++;
        }
        int denominador = 100;
        double result= numerador/denominador;
        this.averagePrecision= result;
    }
    
    public void nDCG10 (TreeMap<String, Double> smap, String queryId, Document relevancias){
    	
		Double[] DGC = calcularDCG(10,smap,  queryId, relevancias);
    	Document auxorder = ordenarRelevancias(relevancias, queryId);
    	Double[] DGCi = calcularDGCideal(10,smap,  queryId, auxorder);
    	//System.out.println(auxorder);
    	//Meter todo en una funcion que haga todo y devuelva el valor DGC, pasar relevancias o auxorder
    	Double[] nDGC = new Double[10];
    	for (int i =0; i <DGC.length;i++){
    		if (DGC[i]==0)nDGC[i]=0.0;
    		else nDGC[i] = DGC[i]/DGCi[i];
    	}    	
    	this.NDCG10=nDGC[9];
    }
    
    public void nDCG100 (TreeMap<String, Double> smap, String queryId, Document relevancias){
    	
		Double[] DGC = calcularDCG(100,smap,  queryId, relevancias);
    	Document auxorder = ordenarRelevancias(relevancias, queryId);
    	Double[] DGCi = calcularDGCideal(100,smap,  queryId, auxorder);
    	//System.out.println(auxorder);
    	//Meter todo en una funcion que haga todo y devuelva el valor DGC, pasar relevancias o auxorder
    	Double[] nDGC = new Double[100];
    	for (int i =0; i <DGC.length;i++){
    		if (DGC[i]==0)nDGC[i]=0.0;
    		else nDGC[i] = DGC[i]/DGCi[i];
    	}    	
    	this.NDCG100=nDGC[99];
    }
    
    private static Double[] calcularDCG (int cortes,TreeMap<String, Double> smap, String queryId, Document relevancias){
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
}
