import java.util.Comparator;
import java.util.Map;

public class MapComparator implements Comparator<String> {
	Map<String,Double> mapa;
	
	public MapComparator(Map<String,Double> mapa){
		this.mapa = mapa;
	}


	public int compare(String o1, String o2) {
		if (mapa.get(o1)>= mapa.get(o2)){
			return 1;
		}else{
			return -1;
		}
		
	}
}
