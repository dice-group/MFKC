package aksw.sim.proof;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableGenerator {

	public static void main(String args[]) {
		System.out.println("Start...");
		File fds = new File("D:\\task_axel1\\dbPediaPlaces.tsv");
		File fdt = new File("D:\\task_axel1\\linkedGeoPlaces_million.tsv");
		//32=1,000 -  105=10,000 - 343=100,000 - 1249=1,000,000 - 3900=10,000,000
		int size=32;
		Set<String> ds = getLabels(fds, size);
		Set<String> dt = getLabels(fdt, size);
		generateTable(ds, dt, size);
		System.out.println("End...");
	}

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

	public static void generateTable(Set<String> Ds, Set<String> Dt, int size) {
		StringBuffer sb = new StringBuffer();
		sb.append("s	");
		sb.append("t	");
		sb.append("sim(s,t)	");
		sb.append("|s|	");
		sb.append("|t|	");
		sb.append("|s|+|t|	");
		sb.append("|s|-|t|	");
		sb.append("h(s)	");
		sb.append("h(t)	");
		sb.append("|h(s)|	");
		sb.append("|h(t)|	");
		sb.append("|h(s)|-|h(t)|	");
		sb.append("|h(s)|+|h(t)|	");
		sb.append("h(s) intersec h(t)	");
		sb.append("|h(s) intersec h(t)|	");
		sb.append("sum(f(s)+f(t))	");

		sb.append("|s|-|t| div |s|+|t|	");
		sb.append("|s|+|t| div |s|-|t|	");
		sb.append("|h(s) intersec h(t)| div |s|-|t|	");
		sb.append("|s|-|t| div |h(s) intersec h(t)|	");

		sb.append("|h(s) intersec h(t)| div |s|+|t|	");
		sb.append("|s|+|t| div |h(s) intersec h(t)|	");
		sb.append("\n");
		for (String s : Ds) {
			Map<Character, Integer> hs = hash(s);
			for (String t : Dt) {
				Map<Character, Integer> ht = hash(t);
				Map<Character, Integer> intersec = getIntersec(hs, ht);

				double sim = 0.0d;
				double sum = 0.0d;
				for (Character c : intersec.keySet()) {
					sum += intersec.get(c).doubleValue();
				}
				sim = sum / ((double) (s.length() + t.length()));

				sb.append(s + "	");
				sb.append(t + "	");
				sb.append(sim + "	");
				sb.append(s.length() + "	");
				sb.append(t.length() + "	");
				sb.append((s.length() + t.length()) + "	");
				sb.append(Math.abs(s.length() - t.length()) + "	");
				sb.append(hs.toString() + "	");
				sb.append(ht.toString() + "	");
				sb.append(hs.size() + "	");
				sb.append(ht.size() + "	");
				sb.append(Math.abs(hs.size() - ht.size()) + "	");
				sb.append((hs.size() + ht.size()) + "	");
				sb.append(intersec.toString() + "	");
				sb.append(intersec.size() + "	");
				sb.append(sum + "	");

				try {
					sb.append((double) (Math.abs(s.length() - t.length()) / (double)(s.length() + t.length()))
							+ "	");
				} catch(Exception e) {
					sb.append("NaN	");
				}
				
				try {
					sb.append((double) (s.length() + t.length()) / (double)(Math.abs(s.length() - t.length()))
							+ "	");
				} catch(Exception e) {
					sb.append("NaN	");
				}

				try {
					sb.append((double) (intersec.size() / (double)(s.length() - t.length()))
							+ "	");
				} catch(Exception e) {
					sb.append("NaN	");
				}
				
				try {
					sb.append((double) (Math.abs(s.length() - t.length()) /  (double)(intersec.size()))
							+ "	");
				} catch(Exception e) {
					sb.append("NaN	");
				}

				try {
					sb.append(
							(double) (intersec.size() / (double) (s.length() + t.length())) + "	");
				} catch(Exception e) {
					sb.append("NaN	");
				}

				try {
					sb.append((double) ((s.length() + t.length()) /  (double)(intersec.size()))
							+ "	");
				} catch(Exception e) {
					sb.append("NaN	");
				}
				sb.append("\n");
			}
		}
		generateTSVFile(sb.toString(), "table_" + size);
	}

	private static Map<Character, Integer> getIntersec(Map<Character, Integer> hs, Map<Character, Integer> ht) {
		Map<Character, Integer> intersec = new LinkedHashMap<Character, Integer>();

		for (Character c : hs.keySet()) {
			if (ht.containsKey(c))
				intersec.put(c, hs.get(c) + ht.get(c));
		}
		return intersec;
	}

	/**
	 * Counting the number of occurrences of each character
	 * 
	 * @param character
	 *            array
	 * @return hashmap : Key = char, Value = num of occurrence
	 */
	private static HashMap<Character, Integer> countElementOcurrences(char[] array) {

		HashMap<Character, Integer> countMap = new HashMap<Character, Integer>();

		for (char element : array) {
			Integer count = countMap.get(element);
			count = (count == null) ? 1 : count + 1;
			countMap.put(element, count);
		}

		return countMap;
	}

	/**
	 * Sorts the counted numbers of characters (keys, values) by java Collection
	 * List
	 * 
	 * @param HashMap
	 *            (with key as character, value as number of occurrences)
	 * @return sorted HashMap
	 */
	private static <K, V extends Comparable<? super V>> HashMap<K, V> descendingSortByValues(HashMap<K, V> map) {

		List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap<K, V> sortedHashMap = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

	/**
	 * get most frequent k characters
	 * 
	 * @param String
	 *            to be hashed.
	 * @return Sorted HashMap with Char and frequencies.
	 */
	private static Map<Character, Integer> hash(String s) {
		HashMap<Character, Integer> countMap = countElementOcurrences(s.toCharArray());
		// System.out.println(countMap);
		Map<Character, Integer> map = descendingSortByValues(countMap);
		// System.out.println(map);
		return map;
	}

	private static void generateTSVFile(String pS, String name) {
		try {
			PrintWriter writer = new PrintWriter("D:\\task_axel1\\experiments\\" + name + ".csv", "UTF-8");
			writer.print(pS);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
