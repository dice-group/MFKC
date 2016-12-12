
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Testing {
	private static void t1(){
		File ds = new File("dbPediaPlaces.tsv");
		File dt = new File("linkedGeoPlaces_million.tsv");
		int k=200;
		double threshold=1.0d;
		//32=1,000 -  105=10,000 - 343=100,464 - 1249=1,001,642 - 3900=10,273,950
		ExperimentMFKC.generateExperiment(ds, dt, 3900, true, k, threshold);
		System.out.println("*Threshold="+threshold);
	}
	
	private static void tGoldStandard() throws FileNotFoundException, UnsupportedEncodingException
	{
		File goldFile = new File("dbpedia_yago_1000.tsv");
		ExperimentMFKC.generateExperiment(goldFile);
	}
	
	private static void t2(){
		String s="Today I went to shop";
		//String t="Today I went to shop and shoping more stuff, because I like to shop, that is why I'm alive";
		String t="Yesterday the shop was open";
		int k=99;
		double sim=AndreMFKC.sim(s, t, k);
		double simJaccard=Jaccard.jaccard_coeffecient(s, t);
		double simJaroWinkler=JaroWinkler.jaroWinkler(s, t);
		System.out.println("s="+s+"\nt="+t+"\nk="+k);
		System.out.println("AndreMFKC="+ sim);
		System.out.println("Jaccard="+ simJaccard);
		System.out.println("JaroWinkler="+ simJaroWinkler);
		
	}
	
	public static void tDocumentSimilarity() throws IOException{
		String s = "";
		String t = "";
		String line = null;
		Set<String> setPrint = new HashSet<String>();
		File dir = new File("files");
		Set<File> setFiles = Files.walk(Paths.get(dir.getPath())).filter(Files::isRegularFile).map(Path::toFile)
					.collect(Collectors.toSet());
		File[] files = setFiles.stream().toArray(File[]::new);
		for (int i = 0; i < files.length; i++) {
			s = getTxtFile(files[i]);
			for (int j = i + 1; j < files.length; j++) {
				t = getTxtFile(files[j]);
				long time = System.currentTimeMillis();
				double sim=AndreMFKC.sim(s, t, 999999999);
				time = System.currentTimeMillis() - time;
				line = files[i].getName() + "\t" + files[j].getName() + "\t" + "AndreMFKC" + "\t" + sim + "\t" + time;
				setPrint.add(line);
				
				time = System.currentTimeMillis();
				double simJaccard=Jaccard.jaccard_coeffecient(s, t);
				time = System.currentTimeMillis() - time;
				line = files[i].getName() + "\t" + files[j].getName() + "\t" + "Jaccard" + "\t" + simJaccard + "\t" + time;
				setPrint.add(line);
				
				time = System.currentTimeMillis();
				double simJaroWinkler=JaroWinkler.jaroWinkler(s, t);
				time = System.currentTimeMillis() - time;
				line = files[i].getName() + "\t" + files[j].getName() + "\t" + "JaroWinkler" + "\t" + simJaroWinkler + "\t" + time;
				setPrint.add(line);
			}
		}
		printFile(setPrint);
	}
	
	private static void printFile(Set<String> setPrint) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("experimentDocumentSim.csv", "UTF-8");
		writer.println("File1" + "\t" + "File2" + "\t" + "Algorithm" + "\t" + "Sim" + "\t" + "time(ms)");
		for (String elem : setPrint) {
			writer.println(elem);
		}
		writer.close();
	}

	private static String getTxtFile(File file) {
		String ret = "";
		String line;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while (((line = br.readLine()) != null)) {
				ret +=line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
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
	
	private static void t7(){
		int lim = 1000000;
		long startTime = System.currentTimeMillis();
		IntStream.range(0, lim).parallel().forEach(id -> {
			int ci=id;
			IntStream.range(0, lim).parallel().forEach(idt -> {
				int cd=id+idt;
			});	
		});
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total time parallel(J8): " + totalTime);
		
		startTime = System.currentTimeMillis();
		int c =0;
		for(int i=0;i<lim;i++)
		{
			c=i;
			for(int j=0;j<lim;j++)
			{
				c=i+j;
			}
		}
		endTime = System.currentTimeMillis();
		totalTime = endTime - startTime;
		System.out.println("Total time seq(J8): " + totalTime);
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
	
	public static void main(String[] args) throws IOException {
		//t1();
		//t2();
		//t3();
		//t4();
		//t5();
		//t6();
		//t7();
		//tParallelGPU();
		// tK();
		//tDocumentSimilarity();
		tGoldStandard();
		//int proc = Runtime.getRuntime().availableProcessors();
		//System.out.println("Number of CPUs: " + proc);
	}
}
