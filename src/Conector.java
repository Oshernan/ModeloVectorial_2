
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.*;
import com.mongodb.*;
public class Conector {
	private MongoDatabase bbdd;
	private MongoCollection collectionPalabras;
	private MongoCollection collectionIdf;
	private MongoCollection collectionDocumentos;
	private MongoCollection collectionRelevancia;
	
	public Conector(){
		
		MongoClient m = establecerConexion();
		this.bbdd = abrirBBDD(m);


		this.collectionPalabras = this.bbdd.getCollection("palabras");
		this.collectionIdf = this.bbdd.getCollection("idf");
		this.collectionDocumentos = this.bbdd.getCollection("documentos");
		this.collectionRelevancia = this.bbdd.getCollection("relevancia");
		
		

	}
	public MongoDatabase getBbdd() {
		return bbdd;
	}
	public void setBbdd(MongoDatabase bbdd) {
		this.bbdd = bbdd;
	}
	public MongoCollection getCollectionPalabras() {
		return collectionPalabras;
	}
	public void setCollectionPalabras(MongoCollection collectionPalabras) {
		this.collectionPalabras = collectionPalabras;
	}
	public MongoCollection getCollectionIdf() {
		return collectionIdf;
	}
	public void setCollectionIdf(MongoCollection collectionIdf) {
		this.collectionIdf = collectionIdf;
	}
	public MongoCollection getCollectionDocumentos() {
		return collectionDocumentos;
	}
	public void setCollectionDocumentos(MongoCollection collectionDocumentos) {
		this.collectionDocumentos = collectionDocumentos;
	}
	public MongoCollection getCollectionRelevancia() {
		return collectionRelevancia;
	}
	public void setCollectionRelevancia(MongoCollection collectionRelevancia) {
		this.collectionRelevancia = collectionRelevancia;
	}
	public MongoClient establecerConexion(){
		
		MongoClient mongo =null;
		try{
		mongo = new MongoClient("localhost",27017);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return mongo;
	}
	public MongoDatabase abrirBBDD (MongoClient mongo){
		return mongo.getDatabase("mvectorial");
	}
	/*
	public void insertDiccionarios(String documento, Map<String,Integer> map){
		Document docD = new Document();
		
		docD.append("_id", documento);
		//docD.append("Palabras", doc);
		Iterator it = map.entrySet().iterator();
		 
	    while (it.hasNext()) {
	        Map.Entry e = (Map.Entry)it.next();
	        docD.append((String) e.getKey(), e.getValue());
	    }
		this.collectionPalabras.insertOne(docD);

	
	}*/
	public void insertDoc(Document doc, String p){
		if(p.equals("palabras")){
			
			this.collectionPalabras.insertOne(doc);
			
		}else if(p.equals("idf")){
			
			this.collectionIdf.insertOne(doc);
			
		}else if(p.equals("relevancia")){
			
			this.collectionRelevancia.insertOne(doc);
			
		}else{
			
			this.collectionDocumentos.insertOne(doc);
			
		}

	
	}
	
	
	public void removeBD(){
		
	this.collectionPalabras.drop();
	this.collectionDocumentos.drop();
	this.collectionIdf.drop();
	this.collectionRelevancia.drop();
	
	}
	
	public void resetFilesBD(){
		

	this.collectionDocumentos.drop();
	this.collectionRelevancia.drop();
	
	this.collectionDocumentos = this.bbdd.getCollection("documentos");
	this.collectionRelevancia = this.bbdd.getCollection("relevancia");
	}
	
	
}
