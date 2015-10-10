import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SDFTest {

	/**
	 * Counting the number of occurrences of each character
	 * 
	 * @param character
	 *            array
	 * @return hashmap : Key = char, Value = num of occurrence
	 */
	public static HashMap<Character, Integer> countElementOcurrences(char[] array) {

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
	 * @param array
	 *            of characters
	 * @param limit
	 *            of k
	 * @return hashed String
	 */
	public static String mostOcurrencesElement(char[] array, int k) { // from
																		// Naive
																		// implementation
		HashMap<Character, Integer> countMap = countElementOcurrences(array);
		System.out.println(countMap);
		Map<Character, Integer> map = descendingSortByValues(countMap);
		System.out.println(map);
		int i = 0;
		String output = "";
		for (Map.Entry<Character, Integer> pairs : map.entrySet()) {
			if (i++ >= k)
				break;
			output += "" + pairs.getKey() + pairs.getValue();
		}
		System.out.println(output);
		return output;
	}

	public static int f(String s1, String s2, int limit) {
		/*
		 * New, from Andre, avoid errors and save some runTime. if S1 and S2
		 * don't have at least 2 shared characters, the distance is to far...in
		 * our case does not matter. i.e. Comparing "my" with "a", x1[m,1,y,1],
		 * x2[a,1] it's clear and trivial that my this 2 string are not similar.
		 * why lose time comparing ?
		 */
		// .replaceAll("\\s+","") it's a new improvement that don't consider
		// white spaces.
		String s = s1.replaceAll("\\s+", "");
		String t = s2.replaceAll("\\s+", "");
//		if (s.length() < 2 && t.length() > 1)
//			return limit;
//
//		if (t.length() < 2 && s.length() > 1)
//			return limit;

		int similarity = 0;

		char x1[] = mostOcurrencesElement(s.toCharArray(), 2).toCharArray(); // from
																				// Naive
																				// implementation
		char x2[] = mostOcurrencesElement(t.toCharArray(), 2).toCharArray(); // from
																				// Naive
																				// implementation

		if (x1[0] == x2[0])
			similarity = similarity + (x1[1] - '0') + (x2[1] - '0');

		if (x1[0] == x2[2])
			similarity = similarity + (x1[1] - '0') + (x2[3] - '0');

		if (x1[2] == x2[0])
			similarity = similarity + (x1[3] - '0') + (x2[1] - '0');

		if (x1[2] == x2[2])
			similarity = similarity + (x1[3] - '0') + (x2[3] - '0');

		return limit - similarity;
	}
}
