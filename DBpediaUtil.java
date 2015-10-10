import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.swing.GroupLayout.ParallelGroup;

import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.riot.WebContent;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

public class DBpediaUtil {

	private static int ct;

	// Load all sameAs links: "URI1;UR2;lbl1;lbl2"
	public static void generateExperiment(File pFile, int limit) {
		List<String> lstResult = new ArrayList<String>();
		int count = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(pFile))) {
			String line;
			while (((line = br.readLine()) != null) && (count <= limit)) {
				String s[] = line.split(";");
				String label1 = getLabel(s[0]);
				String label2 = getLabel(s[1]);
				int mfkc = MFrequentKChar.SDFfunc(label1, label2, 100);
				String lineProcessed = s[0] + ";" + s[1] + ";" + label1 + ";" + label2 + ";" + mfkc;
				lstResult.add(lineProcessed);
				count++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		generateCSVFile(lstResult, limit);
	}

	public static void generateExperiment(File pFile, int limit, boolean generateFile) {
		List<String> lstResult = new ArrayList<String>();
		int scoresNovel[] = new int[limit + 1];
		int scoresNaive[] = new int[limit + 1];
		int scoresSDFTest[] = new int[limit + 1];
		int scoresSDFTest2[] = new int[limit + 1];
		int scoresSDFThreshold[] = new int[limit + 1];
		double scoresJaroWinkler[] = new double[limit + 1];
		int scoresLevens[] = new int[limit + 1];
		int scoresParallel[] = new int[limit + 1];
		int scoresParallel2[] = new int[limit + 1];
		int count = 0;
		try {
			List<String> lLabels = getLabels(pFile, limit, generateFile);
			long startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				if (s.length > 1)
					scoresNovel[count++] = NovelSDF.f(s[0], s[1], 100);
			}
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Novel SDF, TotalTime for " + limit + " pair of labels is: " + totalTime);

			count = 0;
			startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				if (s.length > 1)
					scoresNaive[count++] = MFrequentKChar.SDFfunc(s[0], s[1], 100);
			}
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("Naive SDF, TotalTime for " + limit + " pair of labels is: " + totalTime);

			count = 0;
			startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				if (s.length > 1)
					scoresSDFTest[count++] = SDFTest.f(s[0], s[1], 100);
			}
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("SDF Test, TotalTime for " + limit + " pair of labels is: " + totalTime);

			SDFTest2 sdf2 = new SDFTest2();
			count = 0;
			startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				//if (s.length > 1)
					//scoresSDFTest2[count++] = sdf2.getDiff(s[0], s[1], 100);
			}
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("SDF Test2, TotalTime for " + limit + " pair of labels is: " + totalTime);

			count = 0;
			startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				if (s.length > 1)
					scoresJaroWinkler[count++] = JaroWinkler1.jaroWinkler(s[0], s[1]);
			}
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("JaroWinkler, TotalTime for " + limit + " pair of labels is: " + totalTime);

			count = 0;
			startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				if (s.length > 1)
					scoresLevens[count++] = Levenshtein.LevenshteinWordDistance(s[0], s[1]);
			}
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("Levenstein, TotalTime for " + limit + " pair of labels is: " + totalTime);

			SDFParallelGPU p1 = new SDFParallelGPU();
			ct = 0;
			Object[] sArr = lLabels.toArray();
			startTime = System.currentTimeMillis();
			IntStream.range(0, sArr.length).parallel().forEach(id -> {
				// for (String elem : lLabels) {
				String s[] = sArr[ct++].toString().split(";");
				if (s.length > 1)
					scoresParallel[ct] = p1.f(s[0], s[1], 100);
				// }
			});
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("SDFParallel, TotalTime for " + limit + " pair of labels is: " + totalTime);

			ct = 0;
			Object[] sArr2 = lLabels.toArray();
			startTime = System.currentTimeMillis();
			IntStream.range(0, sArr2.length).parallel().forEach(id -> {
				// for (String elem : lLabels) {
				String s[] = sArr2[ct++].toString().split(";");
				if (s.length > 1)
					scoresParallel2[ct] = SDFTest.f(s[0], s[1], 100);
				// }
			});
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("SDFParallel2, TotalTime for " + limit + " pair of labels is: " + totalTime);

			count = 0;
			startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				if (s.length > 1)
					scoresSDFThreshold[count++] = SDFThreshold.f(s[0], s[1], 100,0);
			}
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("SDF Threashold, TotalTime for " + limit + " pair of labels is: " + totalTime);
			
			if (generateFile) {
				List<String> nList = new ArrayList<String>();

				nList.add("Label1;Label2;scrNada;Naive;Novel;SDFTest;JaroWinkler;Levens;Parallel;Parallel2;SDFTest2;Threshold");

				int i = 0;
				for (String elem : lLabels) {
					nList.add(elem + ";" + scoresNaive[i] + ";" + scoresNovel[i] + ";" + scoresSDFTest[i] + ";"
							+ scoresJaroWinkler[i] + ";" + scoresLevens[i] + ";" + scoresParallel[i] + ";"
							+ scoresParallel2[i] + ";" + scoresSDFTest2[i]
							+ ";" + scoresSDFThreshold[i]);
					i++;
				}
				generateCSVFile(nList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> getLabels(File pFile, int limit, boolean generateFile) {
		List<String> lLabels = new ArrayList<String>();
		int count = 0;
		int cp = 1000;
		String line;
		long startTime = System.currentTimeMillis();
		long endTime = 0;
		long totalTime = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(pFile))) {
			while (((line = br.readLine()) != null) && (count <= limit)) {
				if (count == cp) {
					endTime = System.currentTimeMillis();
					totalTime = endTime - startTime;
					// System.out.println("RegisterNumber: " + count + "
					// Time(Milliseconds): " + totalTime);
					startTime = System.currentTimeMillis();
					cp += 1000;
				}
				if (generateFile && pFile.getName().endsWith(".nt")) {
					String s[] = line.split(";");
					String label1 = getLabel(s[0]);
					String label2 = getLabel(s[1]);
					lLabels.add(label1 + ";" + label2);
				} else {
					lLabels.add(line);
				}
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lLabels;
	}

	private static String getLabel(String pURI) {
		String sRet = null;
		QueryEngineHTTP qexec = null;
		String endPoint = "http://dbpedia.org/sparql";
		// String squery = "SELECT ?uri ?label " + "{ ?uri rdfs:label ?label
		// FILTER "
		// + "(?uri = <http://dbpedia.org/resource/Brazil> " + "|| ?uri =
		// <http://dbpedia.org/resource/Berlin>)}";
		String squery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + "SELECT ?uri ?label "
				+ "{ ?uri rdfs:label ?label FILTER " + "(?uri = <" + pURI + ">)}";
		boolean bErr = true;

		try {
			while (bErr) {
				try {
					bErr = false;
					qexec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(endPoint, squery);
					// request JSON results
					qexec.setSelectContentType(WebContent.contentTypeResultsJSON);
				} catch (QueryExceptionHTTP e) {
					bErr = true;
					System.err.println(endPoint + " is DOWN");
				}

			}

			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				sRet = soln.get("label").toString();

				sRet = sRet.substring(0, sRet.indexOf("@"));
				// break;
				// if (sRet.trim().endsWith("@en"))
				// {
				// sRet = sRet.substring(0, sRet.indexOf("@"));
				// break;
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sRet;
	}

	/*
	 * Return the local of generated CSV file.
	 */
	private static void generateCSVFile(List<String> pLstSameAs, int pLimit) {
		// DBpediaURI1;DBpediaURI2;Label1;Label2;MFKC
		// http://dbpedia.org/resource/Brazil;http://dbpedia.org/resource/Brezil;Brazil;Brezil;96
		// File f1 = new File("D:\\task_axel1\\sameaslimes.nt");
		// List lstSameAs = getAllSameASLabel(pFile, 100);
		try {
			PrintWriter writer = new PrintWriter("D:\\task_axel1\\results_" + pLimit + ".csv", "UTF-8");
			// writer.println("DBpediaURI1;DBpediaURI2;Label1;Label2;MFKC");
			for (String elem : pLstSameAs) {
				writer.println(elem);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void generateCSVFile(List<String> lLabels) {
		try {
			PrintWriter writer = new PrintWriter("D:\\task_axel1\\labels.csv", "UTF-8");
			for (String elem : lLabels) {
				writer.println(elem);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateExperimentSync(File pFile, int limit, boolean generateFile) {
		List<String> lstResult = new ArrayList<String>();
		int scoresNovel[] = new int[limit + 1];
		int scoresNaive[] = new int[limit + 1];
		int scoresSDFTest[] = new int[limit + 1];
		int scoresSDFThreshold[] = new int[limit + 1];
		double scoresJaroWinkler[] = new double[limit + 1];
		int scoresLevens[] = new int[limit + 1];
		int scoresParallel[] = new int[limit + 1];
		int scoresParallelThreshold[] = new int[limit + 1];
		int count = 0;
		try {
			List<String> lLabels = getLabels(pFile, limit, generateFile);
			long startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				if (s.length > 1)
					scoresNovel[count++] = NovelSDF.f(s[0], s[1], 100);
			}
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Novel SDF, TotalTime for " + limit + " pair of labels is: " + totalTime);

			count = 0;
			startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				if (s.length > 1)
					scoresNaive[count++] = MFrequentKChar.SDFfunc(s[0], s[1], 100);
			}
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("Naive SDF, TotalTime for " + limit + " pair of labels is: " + totalTime);

			count = 0;
			startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				if (s.length > 1)
					scoresSDFTest[count++] = SDFTest.f(s[0], s[1], 100);
			}

			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("SDF Test, TotalTime for " + limit + " pair of labels is: " + totalTime);

			count = 0;
			startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				if (s.length > 1)
					scoresJaroWinkler[count++] = JaroWinkler1.jaroWinkler(s[0], s[1]);
			}

			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("JaroWinkler, TotalTime for " + limit + " pair of labels is: " + totalTime);

			count = 0;
			startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				if (s.length > 1)
					scoresLevens[count++] = Levenshtein.LevenshteinWordDistance(s[0], s[1]);
			}

			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("Levenstein, TotalTime for " + limit + " pair of labels is: " + totalTime);

			//SDFParallelGPU p1 = new SDFParallelGPU();
			ct = -1;
			Object[] sArr = lLabels.toArray();
			startTime = System.currentTimeMillis();
			IntStream.range(0, sArr.length).parallel().forEach(id -> {
				synchronized (this) {
					ct++;
				}
				String s[] = sArr[ct].toString().split(";");
				if ((s.length > 1) && (ct < scoresParallel.length))
					scoresParallel[ct] = SDFTest.f(s[0], s[1], 100);
				// }
			});
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("SDFParallel, TotalTime for " + limit + " pair of labels is: " + totalTime);
			
			count = 0;
			startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				if (s.length > 1)
					scoresSDFThreshold[count++] = SDFThreshold.f(s[0], s[1], 100,0);
			}
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("SDF Threashold, TotalTime for " + limit + " pair of labels is: " + totalTime);

			ct = -1;
			startTime = System.currentTimeMillis();
			IntStream.range(0, sArr.length).parallel().forEach(id -> {
				synchronized (this) {
					ct++;
				}
				String s[] = sArr[ct].toString().split(";");
				if ((s.length > 1) && (ct < scoresParallel.length))
					scoresParallelThreshold[ct] = SDFThreshold.f(s[0], s[1], 100,0);
				// }
			});
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("SDF Parallel Threashold, TotalTime for " + limit + " pair of labels is: " + totalTime);

			
			if (generateFile) {
				List<String> nList = new ArrayList<String>();

				nList.add("Label1;Label2;scrNada;Naive;Novel;SDFTest;JaroWinkler;Levens;Parallel;Threshold;PThreshold");

				int i = 0;
				for (String elem : lLabels) {
					nList.add(elem + ";" + scoresNaive[i] + ";" + scoresNovel[i] + ";" + scoresSDFTest[i] + ";"
							+ scoresJaroWinkler[i] + ";" + scoresLevens[i] + ";" + scoresParallel[i]
							+ ";" + scoresSDFThreshold[i] + ";" + scoresParallelThreshold[i]);
					i++;
				}
				generateCSVFile(nList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateExperimentSync1(File pFile, int limit, boolean generateFile) {
		int scoresSDFThreshold[] = new int[limit + 1];
		int count = 0;
		try {
			List<String> lLabels = getLabels(pFile, limit, generateFile);
			long startTime = System.currentTimeMillis();
			
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Novel SDF, TotalTime for " + limit + " pair of labels is: " + totalTime);

			
			count = 0;
			startTime = System.currentTimeMillis();
			for (String elem : lLabels) {
				String s[] = elem.split(";");
				if (s.length > 1)
					scoresSDFThreshold[count++] = SDFThreshold.f(s[0], s[1], 100,5);
			}
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("SDF Threashold, TotalTime for " + limit + " pair of labels is: " + totalTime);

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
