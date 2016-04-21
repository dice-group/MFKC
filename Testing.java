
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;


public class Testing {
	private static void t1(){
		File ds = new File("dbPediaPlaces.tsv");
		File dt = new File("linkedGeoPlaces_million.tsv");
		int k=200;
		double threshold=1.0d;
		//32=1,000 -  105=10,000 - 343=100,464 - 1249=1,001,642 - 3900=10,273,950
		ExperimentMFKC.generateExperiment(ds, dt, 1249, true, k, threshold);
		System.out.println("*Threshold="+threshold);
	}
	
	private static void t2(){
		String s="mystring1";
		String t="mystring2";
		int k=8;
		double sim=AndreMFKC.sim(s, t, k);
		System.out.println("similarity("+s+","+t+", k="+k+")="+ sim);
	}
	
	private static void t3(){
		String s="seeking";
		String t="research";
		int sim=Naive.SDFfunc(s, t, 11);
		System.out.println("similarity("+s+","+t+")="+ sim);
	}
	
	private static void t4(){
		int k=3;
		double threshold=1.0d;
		ExperimentMFKC.generateExperiment(k, threshold);
	}
	
	private static void t5(){
		int k=3;
		double threshold=1.0d;
		ExperimentMFKC.generateExperimentA(k, threshold);
	}
	
	private static void t6(){
		int k=3;
		double threshold=1.0d;
		ExperimentMFKC.generateExperimentTrillion(k, threshold);
	}
	
	private static void tParallelGPU()
	{
		Set<String> ds = new HashSet<String>();
		Set<String> dt = new HashSet<String>();
		ds.add("Wari culture");
		ds.add("Chrabrany");
		ds.add("Accum");
		ds.add("Jaintia Kingdom");
		ds.add("Dominion of Melchizedek");

		dt.add("Wari culture");
		dt.add("Chrabrany");
		dt.add("Accum");
		dt.add("Kingdom of Kano");
		dt.add("Dominion of Melchizedek");
		
		String dsA[] = ds.toArray(new String[ds.size()]);
		String dtA[] = dt.toArray(new String[dt.size()]);
		
		IntStream.range(0, dsA.length).parallel().forEach(id -> {
			IntStream.range(0, dtA.length).parallel().forEach(idt -> {
				System.out.println(dsA[id] + ", "+ dtA[idt]);
			});	
		});
	}
	
	private static void tK(){
		File ds = new File("dbPediaPlaces.tsv");
		File dt = new File("linkedGeoPlaces_million.tsv");
		double threshold=0.95d;
		
		for(int k=64;k < 130; k++)
		{
			//32=1,000 -  105=10,000 - 343=100,464 - 1249=1,001,642 - 3900=10,273,950
			ExperimentMFKC.generateExperimentK(ds, dt, 1249, true, k, threshold);
			System.out.println("k="+k);
		}
		System.out.println("*Threshold="+threshold);
	}
	
	public static void main(String[] args) {
		t1();
		//t2();
		//t3();
		//t4();
		//t5();
		//t6();
		//tParallelGPU();
		// tK();
		int proc = Runtime.getRuntime().availableProcessors();
		System.out.println("Number of CPUs: " + proc);
	}
}