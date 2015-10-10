import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.jena.vocabulary.DB;

public class Testing {

	public static void main(String[] args) {
		 test1();
		// test2();
		// test3();
		// test4();
		//new Testing().test5();
		//test6(); 
	}

	private static void test1() {
		String s1 = "abaabc";
		String s2 = "aacddd";
		System.out.println(s1 + "\n" + s2);
		//System.out.println("Most frequent k characters: " + MFrequentKChar.SDFfunc(s1, s2, 10));
		//System.out.println("Novel SDF: " + NovelSDF.f(s1, s2, 10));
		System.out.println("SDF Test: " + SDFTest.f(s1, s2, 10));
		//System.out.println("Jaro Winkler: " + JaroWinkler1.jaroWinkler(s1, s2));
		//System.out.println("SDFGuava: " + SDFGuava.getSDF(s1, s2));
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
		File f1 = new File("D:\\task_axel1\\100000_labels.csv");
		DBpediaUtil dbp = new DBpediaUtil();
		dbp.generateExperimentSync(f1, 1000, true);
		System.out.println();
		dbp.generateExperimentSync(f1, 10000, true);
		System.out.println();
		dbp.generateExperimentSync(f1, 100000, true);
		System.out.println();
		
		System.out.println("Avoided labels with threshold = " + SDFThreshold.cThreshold);
	}
	
	int ct = 0;
	private void test5()
	{
		
//		IntStream.range(0, 9).parallel().forEach(nada -> {
//			synchronized(this){
//				System.out.println("ct="+ (ct++) + " nada=" + nada);
//			}
//		});
		
		IntStream.range(0, 9).parallel().forEachOrdered(nada -> {
			synchronized(this){
				System.out.println("ct="+ (ct++) + " nada=" + nada);
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
}
