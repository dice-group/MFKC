
import java.io.File;


public class Testing {
	public static void main(String[] args) {
		File ds = new File("dbPediaPlaces.tsv");
		File dt = new File("linkedGeoPlaces_million.tsv");
		int k=200;
		double threshold=0.9d;
		//32=1,000 -  105=10,000 - 343=100,000 - 1249=1,000,000 - 3900=10,000,000
		ExperimentMFKC.generateExperiment(ds, dt, 3900, true, k, threshold);
	}
}
