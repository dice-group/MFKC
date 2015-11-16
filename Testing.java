import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Testing {

	public static void main(String[] args) {
		// test1();
		// test2();
		// test3();
		// test4();
		// new Testing().test5();
		// test6();
		// test7();
		 test8();
		// testHash();
	}

	private static void testHash()
	{
		double threshold = 0.8d;
		int k=4;
		Set<String> ds = new LinkedHashSet<String>();
		Set<String> dt = new LinkedHashSet<String>();
		ds.add("hello");
		ds.add("hello1");
		ds.add("hello2");
		ds.add("hello3");
		
		dt.add("hello");
		dt.add("hello4");
		dt.add("hi2");
		dt.add("hi3");

		List<String> result = MFKCHash.SDF(ds, dt, threshold, k);
		System.out.println(result);
	}
	
	private static void test1() {
		String s1 = "augustus platz 10";
	    String s2 = "rossplatz 100";
		s1 = s1.replaceAll("\\s+", "");
		s2 = s2.replaceAll("\\s+", "");
		int k = 543543;
		int n=5;
		double threshold = 0.8;
		
		System.out.println(s1 + "\n" + s2);
		System.out.println("vowels1=" + countVowels1(s1));
		System.out.println("vowels2=" + countVowels2(s1));
		// System.out.println("Most frequent k characters: " +
		// MFrequentKChar.SDFfunc(s1, s2, 10));
		// System.out.println("Novel SDF: " + NovelSDF.f(s1, s2, 10));
		// System.out.println("SDF Test: " + SDFTest.f(s1, s2, 10));
		// System.out.println("Jaro Winkler: " + JaroWinkler1.jaroWinkler(s1,
		// s2));
		// System.out.println("SDFGuava: " + SDFGuava.getSDF(s1, s2));
		System.out.println("Levenshtein: " + Levenshtein.LevenshteinWordDistance(s1, s2));
		System.out.println("Dice O(n): " + Dice.diceCoefficient(s1, s2));
		System.out.println("Dice O(nlg n): " + Dice.diceCoefficientOptimized(s1, s2));
		System.out.println("NewSDF(K=" + k + ")=" + NewSDF.SDF(s1, s2, k, threshold));
		System.out.println("NewSDF for all possible K(similarity normalized [between 0 and 1]): " + NewSDF.SDF(s1, s2));
		System.out.println("MFKC: " + MFKC.sim(s1, s2, k, 0.8));
		//System.out.println("NGrams(n="+n+"): " + NGrams.getNgrams(s1, n));
		//System.out.println("NGrams(n="+n+"): " + NGrams.getNgrams(s2, n));
		System.out.println("Length based filter: " + LengthBasedFilters.lBasedFilter(s1, s2));
	}

	private static int countVowels1(String pStr)
	{
		long startTime = System.nanoTime();
		
		int count = pStr.replaceAll("[^aeiouAEIOU]","").length();
		
		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("totalTime1=" + totalTime);
		return count;
	}
	
	private static int countVowels2(String pStr)
	{
		long startTime = System.nanoTime();
		
		String regex = "[aeiou]";               
	    Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);   
	    int vowelcount = 0;
	    Matcher m = p.matcher(pStr);
	    while (m.find()) {
	      vowelcount++;
	    }
		
		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("totalTime2=" + totalTime);
		return vowelcount;
	}
	
	private static void test2() {
		File f1 = new File("D:\\task_axel1\\100000_labels.csv");
		DBpediaUtil.generateExperiment(f1, 1000, true);
		System.out.println();
		DBpediaUtil.generateExperiment(f1, 10000, true);
		System.out.println();
		DBpediaUtil.generateExperiment(f1, 100000, true);
		System.out.println();
	}

	private static void test3() {
		File f1 = new File("D:\\task_axel1\\30000_labels.csv");
		List<String> lLabels = DBpediaUtil.getLabels(f1, 10000, true);
		// long startTime = System.currentTimeMillis();
		SDFThread sdf1 = new SDFThread("SDFTest.t1", lLabels);
		sdf1.start();
		SDFThread sdf2 = new SDFThread("SDFTest.t2", lLabels);
		sdf2.start();
		SDFThread sdf3 = new SDFThread("SDFTest.t3", lLabels);
		sdf3.start();

		SDFThread sdf4 = new SDFThread("SDFTest.t4", lLabels);
		sdf4.start();
		SDFThread sdf5 = new SDFThread("SDFTest.t5", lLabels);
		sdf5.start();
		SDFThread sdf6 = new SDFThread("SDFTest.t6", lLabels);
		sdf6.start();
		SDFThread sdf7 = new SDFThread("SDFTest.t7", lLabels);
		sdf7.start();
		SDFThread sdf8 = new SDFThread("SDFTest.t8", lLabels);
		sdf8.start();
		SDFThread sdf9 = new SDFThread("SDFTest.t9", lLabels);
		sdf9.start();
		SDFThread sdf10 = new SDFThread("SDFTest.t10", lLabels);
		sdf10.start();
		// long endTime = System.currentTimeMillis();
		// long totalTime = endTime - startTime;
		// System.out.println("SDF Thread, TotalTime for 30000 pair of labels
		// is: " + totalTime);
	}

	private static void test4() {
		File f1 = new File("D:\\task_axel1\\1_000_000_labels.csv");
		DBpediaUtil dbp = new DBpediaUtil();
		dbp.generateExperimentSync(f1, 1000, true);
		System.out.println();
		dbp.generateExperimentSync(f1, 10000, true);
		System.out.println();
		dbp.generateExperimentSync(f1, 100000, true);
		System.out.println();
		dbp.generateExperimentSync(f1, 1000000, true);
		System.out.println();
		System.out.println("Avoided labels with threshold = " + SDFThreshold.cThreshold);
	}

	int ct = 0;

	private void test5() {
		IntStream.range(0, 9).parallel().forEachOrdered(nada -> {
			synchronized (this) {
				System.out.println("ct=" + (ct++) + " nada=" + nada);
			}
		});
	}

	private static void test6() {
		File f1 = new File("D:\\task_axel1\\100000_labels.csv");
		DBpediaUtil dbp = new DBpediaUtil();
		dbp.generateExperimentSync1(f1, 100000, true);
		System.out.println();

		System.out.println("Avoided labels with threshold = " + SDFThreshold.cThreshold);
	}
	
	private static void test7() {
		Map<String, String> hm = new HashMap<String,String>();
		hm.put("cavaleiro", "cavalheiro");
		hm.put("andre", "andrea");
		hm.put("abcdef", "ghiajulio");
		hm.put("xxyzz", "abcdd");
		hm.put("research", "seeking");
		hm.put("mystring1", "mystring2");
		hm.put("ontologies", "relationships");
		hm.put("aaaa", "aaaa");
		hm.put("aabbcc", "aabbcc");
		hm.put("aabbbccdd", "aabbbccdd");
		int k = 66;
		double threshold = 0.8;
		double d1 [] = new double[20], d2 [] = new double[20], f1=0.0d,f2=0.0d;
		for (String key : hm.keySet())
		{	
			String s1 = key; String s2=hm.get(key);
			System.out.println("k;FloatingPoint;similarityMFKC("+ s1 + "," + s2 + ")");
			for(k=1;k<10;k++)
			{
				d1[k]=NewSDF.SDF(s1, s2, k, threshold);
				d2[k]=MFKC.sim(s1, s2, k, threshold);
				if(k>1){
					f1=fPoint(d1[k],d1[k-1]);
					f2=fPoint(d2[k],d2[k-1]);
				}
				if (f1 == -1)
					NewSDF.fpSkip++;
				
				System.out.println(k + ";" + f1 + ";" + d1[k]);
				//System.out.println(k + ";" + f2 + ";" + d2[k]);
				//System.out.println("NEWSDF(k="+k+")- fPoing="+f1+" -: " + d1[k]);
				//System.out.println("MFKC(k="+k+")* fPoing="+f2+" *: " + d2[k]);
			}
		}
		System.out.println("MinMax skipes="+ NewSDF.minMaxSkip);
		System.out.println("Floating-point skipes="+ NewSDF.fpSkip);
	}
	
	private static double fPoint(double sim, double previousSim) {
		double tolerance = 0.001;
		double dRet = (Math.abs(sim) - Math.abs(previousSim)) / Math.abs(sim);
		
		if(dRet < tolerance)
			dRet = -1;
		
		return dRet;
	}

	private static void test8()
	{
		File f1 = new File("D:\\task_axel1\\100000_labels.csv");
		//for(int k=1;k<200;k++)
		//{
			int k=500;
			double threshold=1.0;
			List<String> lst = Experiment.generateExperiment(f1, 6, true, k, threshold);
		//}
		System.out.println("Total of good pairs: " + lst.size());
		System.out.println("Threshold: " + threshold);
		System.out.println("k = " + k);
		System.out.println("MinMax skipes="+ NewSDF.minMaxSkip);
	}
}
