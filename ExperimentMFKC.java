package aksw.sim.proof;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

public class ExperimentMFKC {

	private static int ct;

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
					if(count > 1000) break;
				} else {
					String s[] = line.split(";");
					if(s.length > 1) 
					{	
						if(b2) sLabels.add(s[0]); else sLabels.add(s[1]);
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

				// sRet = sRet.substring(0, sRet.indexOf("@"));
				// break;
				 //if (sRet.trim().endsWith("@en"))
				if (sRet.trim().indexOf("@en") > 0)	 
				{
					 sRet = sRet.substring(0, sRet.indexOf("@"));
					 //break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sRet;
	}

	
	public static void generateExperiment(File pFile, int limit, boolean generateFile, int k, double threshold) {
		try {
			Set<String> ds = getLabels(pFile, realLimit, generateFile, false);
			Set<String> dt = getLabels(pFile, realLimit, generateFile, true);
			int realLimit=ds.size()*dt.size(); //because is a Cartesian product
			
			long startTime = System.currentTimeMillis();		
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			
			startTime = System.currentTimeMillis();
			AndreMFKC.SDF(ds, dt, threshold, k);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println(" ++++++++ AndreMFKC, , TotalTime for " + (realLimit) + " comparisons is: " + totalTime);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void generateExperiment(File fds, File fdt,  int limit, boolean generateFile, int k, double threshold) {
		
		int realLimit=0;
		try {
			Set<String> ds = getLabels(fds, limit);
			Set<String> dt = getLabels(fdt, limit);
			realLimit=ds.size()*dt.size(); //because is a Cartesian product
			
			long startTime = System.currentTimeMillis();		
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			
			startTime = System.currentTimeMillis();
			AndreMFKC.SDF(ds, dt, threshold, k);
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println(" ++++++++ AndreMFKC, , TotalTime for " + (realLimit) + " comparisons is: " + totalTime);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
