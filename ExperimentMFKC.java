import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.riot.WebContent;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

public class ExperimentMFKC {

	public static Set<String> getLabels(File pFile, int limit) {
		Set<String> sLabels = new LinkedHashSet<String>();
		int count = 0;
		String line;
		try (BufferedReader br = new BufferedReader(new FileReader(pFile))) {
			while (((line = br.readLine()) != null) && (count <= limit)) {
				sLabels.add(line);
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sLabels;
	}

	public static Set<String> getLabels(File pFile, int limit, boolean generateFile, boolean b2) {
		Set<String> sLabels = new LinkedHashSet<String>();
		int count = 0;
		String line;
		try (BufferedReader br = new BufferedReader(new FileReader(pFile))) {
			while (((line = br.readLine()) != null) && (count <= limit)) {
				if (generateFile && pFile.getName().endsWith(".nt")) {
					String s[] = line.split(";");
					String label1 = getLabel(s[0]);
					String label2 = getLabel(s[1]);
					sLabels.add(label1 + ";" + label2);
					if (count > 1000)
						break;
				} else {
					String s[] = line.split(";");
					if (s.length > 1) {
						if (b2)
							sLabels.add(s[0]);
						else
							sLabels.add(s[1]);
					}
				}
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sLabels;
	}

	public static Map<String, String> getLabels(File pFile, int limit, boolean generateFile) {
		Map<String, String> mLabels = new HashMap<String, String>();
		int count = 0;
		String line;
		try (BufferedReader br = new BufferedReader(new FileReader(pFile))) {
			while (((line = br.readLine()) != null) && (count <= limit)) {
				if (generateFile && pFile.getName().endsWith(".nt")) {
					String s[] = line.split(";");
					String label1 = getLabel(s[0]);
					String label2 = getLabel(s[1]);
					mLabels.put(label1, label2);
				} else {
					String s[] = line.split(";");
					if (s.length > 1)
						mLabels.put(s[0], s[1]);
				}
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mLabels;
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

				// sRet = sRet.substring(0, sRet.indexOf("@"));
				// break;
				// if (sRet.trim().endsWith("@en"))
				if (sRet.trim().indexOf("@en") > 0) {
					sRet = sRet.substring(0, sRet.indexOf("@"));
					// break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sRet;
	}

	public static void generateExperiment(File fds, File fdt, int limit, boolean generateFile, int k,
			double threshold) {

		try {
			Set<String> ds = getLabels(fds, limit);
			Set<String> dt = getLabels(fdt, limit);

			long startTime = System.currentTimeMillis();
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			long count = 0;

			startTime = System.currentTimeMillis();
			for (String s : ds) {
				for (String t : dt) {
					count++;
					Naive.SDFfunc(s, t, 1000);
				}
			}
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("TotalTime NAIVE is: " + totalTime);
			System.out.println("Total pairs: " + count);

			startTime = System.currentTimeMillis();
			AndreMFKC_new.SDF(ds, dt, threshold, k);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("TotalTime ANDRE_new is: " + totalTime);

			startTime = System.currentTimeMillis();
			AndreMFKC.SDF(ds, dt, threshold, k);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("TotalTime (N+L+A) is: " + totalTime);

			startTime = System.currentTimeMillis();
			AndreMFKC_Sep.SDF_L_A(ds, dt, threshold, k);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("TotalTime (L+A) is: " + totalTime);

			startTime = System.currentTimeMillis();
			AndreMFKC_Sep.SDF_N_A(ds, dt, threshold, k);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("TotalTime (N+A) is: " + totalTime);

			startTime = System.currentTimeMillis();
			AndreMFKC_Sep.SDF_A(ds, dt, threshold, k);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("TotalTime (A) is: " + totalTime);

			String dsA[] = ds.toArray(new String[ds.size()]);
			String dtA[] = dt.toArray(new String[dt.size()]);
			startTime = System.currentTimeMillis();
			AndreMFKC_Parallel.SDF(dsA, dtA, threshold, k);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("TotalTime (Parallel) is: " + totalTime);
			
//			startTime = System.currentTimeMillis();
//			AndreMFKC_Parallel_File.SDF(dsA, dtA, threshold, k);
//			endTime = System.currentTimeMillis();
//			totalTime = endTime - startTime;
//			System.out.println("TotalTime (Parallel_File) is: " + totalTime);

			try {
				startTime = System.currentTimeMillis();
				for (String s : ds) {
					for (String t : dt) {
						JaroWinkler.jaroWinkler(s, t);
					}
				}
				endTime = System.currentTimeMillis();
				totalTime = endTime - startTime;
				System.out.println("TotalTime JaroWinkler is: " + totalTime);
			} catch (Exception e) {
				System.err.println("Error JaroWinker:" + e.getMessage());
			}
			
			try {
				startTime = System.currentTimeMillis();
				for (String s : ds) {
					for (String t : dt) {
						Jaccard.jaccard_coeffecient(s, t);
					}
				}
				endTime = System.currentTimeMillis();
				totalTime = endTime - startTime;
				System.out.println("TotalTime Jaccard is: " + totalTime);
			} catch (Exception e) {
				System.err.println("Error Jaccard:" + e.getMessage());
			}

			System.out.println("N=" + AndreMFKC.N);
			System.out.println("L=" + AndreMFKC.L);
			System.out.println("A=" + AndreMFKC.A);
			System.out.println("Good=" + AndreMFKC.good);
			System.out.println("AF_N=" + AndreMFKC.AF_N);
			System.out.println("AF_L=" + AndreMFKC.AF_L);
			int total=AndreMFKC.N+AndreMFKC.L+AndreMFKC.A;
			System.out.println("Precision Filter N=" + new BigDecimal((double) (AndreMFKC.N / total)));
			System.out.println("Precision Filter L=" + new BigDecimal((double) (AndreMFKC.L / total)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void generateExperiment(int k, double threshold) {

		try {
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

			AndreMFKC_new.SDF(ds, dt, threshold, k);
			System.out.println("Total pairs: " + (ds.size() * dt.size()));
			System.out.println("N=" + AndreMFKC_new.N);
			System.out.println("L=" + AndreMFKC_new.L);
			System.out.println("A=" + AndreMFKC_new.A);
			System.out.println("Good=" + AndreMFKC_new.good);
			System.out.println("AF_N=" + AndreMFKC_new.AF_N);
			System.out.println("AF_L=" + AndreMFKC_new.AF_L);

			System.out.println(
					"Precision Filter N=" + (double) ((double) AndreMFKC_new.good / (double) AndreMFKC_new.AF_N));
			System.out.println(
					"Precision Filter L=" + (double) ((double) AndreMFKC_new.good / (double) AndreMFKC_new.AF_L));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void generateExperimentA(int k, double threshold) {

		try {
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

			AndreMFKC.SDF(ds, dt, threshold, k);
			System.out.println("Total pairs: " + (ds.size() * dt.size()));
			System.out.println("N=" + AndreMFKC.N);
			System.out.println("L=" + AndreMFKC.L);
			System.out.println("A=" + AndreMFKC.A);
			System.out.println("Good=" + AndreMFKC.good);
			System.out.println("AF_N=" + AndreMFKC.AF_N);
			System.out.println("AF_L=" + AndreMFKC.AF_L);

			System.out.println("Precision Filter N=" + (double) ((double) AndreMFKC.good / (double) AndreMFKC.AF_N));
			System.out.println("Precision Filter L=" + (double) ((double) AndreMFKC.good / (double) AndreMFKC.AF_L));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void generateExperimentTrillion(int k, double threshold) {

		try {
			Set<String> ds = new HashSet<String>();
			Set<String> dt = new HashSet<String>();

			System.out.println("Starting...");

			for (int i = 0; i < 1000000; i++)
				ds.add("Wari culture" + i);

			for (int j = 0; j < 1000000; j++)
				dt.add("Wari culture" + j);

			long startTime = System.currentTimeMillis();
			AndreMFKC.SDF(ds, dt, threshold, k);
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;

			System.out.println("Total time: " + totalTime);
			System.out.println("Total pairs: " + (ds.size() * dt.size()));
			System.out.println("N=" + AndreMFKC.N);
			System.out.println("L=" + AndreMFKC.L);
			System.out.println("A=" + AndreMFKC.A);
			System.out.println("Good=" + AndreMFKC.good);
			System.out.println("AF_N=" + AndreMFKC.AF_N);
			System.out.println("AF_L=" + AndreMFKC.AF_L);

			System.out.println("Precision Filter N=" + (double) ((double) AndreMFKC.good / (double) AndreMFKC.AF_N));
			System.out.println("Precision Filter L=" + (double) ((double) AndreMFKC.good / (double) AndreMFKC.AF_L));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void generateExperimentK(File fds, File fdt, int limit, boolean generateFile, int k,
			double threshold) {
		try {
			Set<String> ds = getLabels(fds, limit);
			Set<String> dt = getLabels(fdt, limit);

			long startTime = System.currentTimeMillis();
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;

			startTime = System.currentTimeMillis();
			AndreMFKC.SDF(ds, dt, threshold, k);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("TotalTime (N+L+A) is: " + totalTime);

			startTime = System.currentTimeMillis();
			AndreMFKC_Sep.SDF_L_A(ds, dt, threshold, k);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("TotalTime (L+A) is: " + totalTime);

			startTime = System.currentTimeMillis();
			AndreMFKC_Sep.SDF_N_A(ds, dt, threshold, k);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("TotalTime (N+A) is: " + totalTime);

			startTime = System.currentTimeMillis();
			AndreMFKC_Sep.SDF_A(ds, dt, threshold, k);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("TotalTime (A) is: " + totalTime);

			String dsA[] = ds.toArray(new String[ds.size()]);
			String dtA[] = dt.toArray(new String[dt.size()]);
			startTime = System.currentTimeMillis();
			AndreMFKC_Parallel.SDF(dsA, dtA, threshold, k);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("TotalTime (Parallel) is: " + totalTime);

		} catch (Exception e) {
			System.err.println("Error JaroWinker:" + e.getMessage());
		}
	}
}