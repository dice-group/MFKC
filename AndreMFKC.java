package aksw.sim.proof;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AndreMFKC {

	static int D, L, N, A, good;

	public static void SDF(Set<String> Ds, Set<String> Dt, double threshold, int k) {
		boolean bExit = false;
		for (String s : Ds) {
			Map<Character, Integer> hs = hash(s);
			for (String t : Dt) {

				if (Math.abs((s.length() - t.length())) > 0) {
					double lFilter = (double) (Math.abs(s.length() - t.length())) / (double) (s.length() + t.length());
					if (lFilter >= threshold) {
						D++;
						bExit = true;
						continue;
					}
				}
				bExit = false;
				Map<Character, Integer> ht = hash(t);
				Map<Character, Integer> intersec = getIntersec(hs, ht);

				if (intersec.size() == 0) { // Hash intesection Filter
					L++;
					bExit = true;
					continue;
				}

				if (Math.abs((s.length() - t.length())) > 0) {
					double diff = (double) intersec.size() / (Math.abs((double) (s.length() - t.length())));
					if (diff < threshold) {
						N++;
						bExit = true;
						continue;
					}
				}
				// if(Math.abs(hs.size() - ht.size()) >= intersec.size())
				// { // Diff Filter
				// //N++;
				// bExit=true;
				// continue;
				// }

				int i = 0;
				double sumFreq = 0.0d;
				double sim[] = new double[intersec.size()];
				for (Character c : intersec.keySet()) {
					if (i >= k)
						break;

					sumFreq += intersec.get(c).doubleValue();
					sim[i] = sumFreq / ((double) (s.length() + t.length()));
					if (sim[i] >= threshold) {
						good++;
						bExit = true;
						break;
					}
					i++;
				}
				if (bExit == false) {
					A++;
					bExit = true;
				}
			}
		}
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
}
