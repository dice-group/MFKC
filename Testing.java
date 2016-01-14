import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import aksw.sim.proof.AndreMFKC;
import aksw.sim.proof.ExperimentMFKC;

public class Testing {
	public static void main(String[] args) {
		File ds = new File("D:\\task_axel1\\dbPediaPlaces.tsv");
		File dt = new File("D:\\task_axel1\\linkedGeoPlaces_million.tsv");
		int k=200;
		double threshold=0.9d;
		//32=1,000 -  105=10,000 - 343=100,000 - 1249=1,000,000 - 3900=10,000,000
		ExperimentMFKC.generateExperiment(ds, dt, 3900, true, k, threshold);
	}
}
