import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class AndreMFKC_Parallel_File {

	static int D, L, N, A, good, AF_N, AF_L;
	static boolean bExit = false;
	//static List<String> lstResult = new ArrayList<String>();
	public static void SDF(String Ds[], String Dt[], double threshold,
			int k) {
		List<String> lstResult = new ArrayList<String>();
		lstResult.add("s \t t \t sim \t threshold");
		IntStream.range(0, Ds.length).parallel().forEach(id -> {
			String s=Ds[id];
			Map<Character, Integer> hs = hash(s);
			IntStream.range(0, Dt.length).parallel().forEach(idt -> {
				String t=Dt[idt];
				bExit = false;
				
				Map<Character, Integer> ht = hash(t);

				Map<Character, Integer> intersec = getIntersec(hs, ht);

				double sumFreq = 0.0d;
				for (Character c : intersec.keySet()) {
					sumFreq += intersec.get(c).doubleValue() / ((double) (s.length() + t.length()));
				}
				synchronized (lstResult) {
					lstResult.add(s + "\t" + t + "\t" + sumFreq + "\t" + threshold);
				}
			});
		});
		generateCSVFile(lstResult,"result.csv");
	}

	/*
	 * Return the similarity,
	 * 
	 * @return value between 0 and 1 You should convert to get 0% to 100%, just
	 * similarity * 100.
	 */
	public static double sim(String s, String t, int k) {
		Map<Character, Integer> hs = hash(s);
		Map<Character, Integer> ht = hash(t);
		Map<Character, Integer> intersec = getIntersec(hs, ht);

		if (intersec.size() == 0) { // Hash intesection Filter
			return 0.0d;
		}

		int i = 0;
		double sumFreq = 0.0d;
		double sim[] = new double[intersec.size()];
		for (Character c : intersec.keySet()) {
			if (i >= k) {
				return sim[i - 1];
			}
			sumFreq += intersec.get(c).doubleValue();
			sim[i] = sumFreq / ((double) (s.length() + t.length()));
			i++;
		}
		return sim[sim.length - 1];
	}

	private static Map<Character, Integer> getIntersec(
			Map<Character, Integer> hs, Map<Character, Integer> ht) {
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
	private static HashMap<Character, Integer> countElementOcurrences(
			char[] array) {

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
	private static <K, V extends Comparable<? super V>> HashMap<K, V> descendingSortByValues(
			HashMap<K, V> map) {

		List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(
				map.entrySet());
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
		HashMap<Character, Integer> countMap = countElementOcurrences(s
				.toCharArray());
		// System.out.println(countMap);
		Map<Character, Integer> map = descendingSortByValues(countMap);
		// System.out.println(map);
		
		return map;
	}
	
	private static void generateCSVFile(List<String> lLabels, String name) {
		try {
			PrintWriter writer = new PrintWriter("C:\\temp\\" + name, "UTF-8");
			for (String elem : lLabels) {
				writer.println(elem);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}