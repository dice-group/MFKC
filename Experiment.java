import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.riot.WebContent;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

public class Experiment {

	private static int ct;

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
					if(b2) sLabels.add(label1); else sLabels.add(label2);
				} else {
					String s[] = line.split(";");
					if(s.length > 1) sLabels.add(s[0] +";"+ s[1]);
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
					if(s.length > 1) mLabels.put(s[0], s[1]);
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
			PrintWriter writer = new PrintWriter("D:\\task_axel1\\experiments\\results_" + pLimit + ".csv", "UTF-8");
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

	public static List<String> generateRelation(File pFile, int limit, boolean generateFile, int k, double threshold) {
		List<String> lstResult = new ArrayList<String>();
		lstResult.add("Label1;Label2;MFKC");
		final int realLimit=(int)Math.pow(10, (double)limit);
		double scoresMFKC[] = new double[realLimit +1];
		int count = 0;
		int cit=0;
		
		try {
			Map<String, String> mLabels = getLabels(pFile, realLimit, generateFile);
			long startTime = System.currentTimeMillis();

			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;

			count = 0;
			startTime = System.currentTimeMillis();
			while(cit < realLimit)
			{	
				for (String key : mLabels.keySet()) {
					for (String value : mLabels.values()) {
						//scoresMFKC[count] = MFKC.sim(key, value, k, threshold);
						scoresMFKC[count] = NewSDF.SDF(key, value, k, threshold);
						if (scoresMFKC[count] >= threshold) {
							lstResult.add(key + ";" + value + ";" + scoresMFKC[count]);
							count++;
						}
						cit++;
					}
					cit++;
				}
			}
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("MFKC(k="+k+"), TotalTime for " + (realLimit) + " pair of labels is: " + totalTime);			
			System.out.println("Iterations="+cit);
			
			if (generateFile) {
				generateCSVFile(lstResult,k);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lstResult;
	}
	
	public static List<String> generateExperiment(File pFile, int limit, boolean generateFile, int k, double threshold) {
		List<String> lstResult = new ArrayList<String>();
		lstResult.add("Label1;Label2;MFKC");
		int realLimit=(int)Math.pow(10, (double)limit);
		int cit=0;
		
		try {
			Set<String> ds = getLabels(pFile, realLimit, generateFile, false);
			Set<String> dt = getLabels(pFile, realLimit, generateFile, true);
			realLimit=ds.size() * dt.size();
			long startTime = System.currentTimeMillis();
			
			lstResult = MFKCHash.SDF(ds, dt, threshold, k);
			
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("MFKC(k="+k+"), TotalTime for " + (realLimit) + " pair of labels is: " + totalTime);			
			System.out.println("Iterations="+cit);
			
			if (generateFile) {
				generateCSVFile(lstResult,realLimit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lstResult;
	}
}
