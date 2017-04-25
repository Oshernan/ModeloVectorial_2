
public class Funcionalidades {

	public static int escalarTF(int frec, int peso){
		int tf=0;
		tf=frec*peso;
		return tf;
	}
	public static double escalarIDF(int frec,int peso, double docstotal, double aparece){
		double idf=0;
		idf = idfword(docstotal, aparece);
		idf=(frec*idf)*(peso*idf);
		return idf;
	}
	
	public static double cosenoTF(double tf,double acumuladorD, double acumuladorQ ){
		double formula = tf/(Math.sqrt(acumuladorD*acumuladorQ));
		return formula;
	}
	
	public static double cosenoTFIDF(double idf, double acumuladorIDF, double acumuladorQidf){
		double formula =idf/(Math.sqrt(acumuladorIDF*acumuladorQidf));
		return formula;
	}
	
	public static double idfword(double docstotal, double aparece){
		double idf = 0.0;
		if (aparece!=0){
			double log = docstotal/aparece;
			idf=Math.log10(log);
		}
		return idf;
	}
	
}
