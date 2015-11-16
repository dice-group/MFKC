import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MFKCHash {
	static int m = 0;
	public static int minMaxSkip;
	public static int fpSkip;

	private static List<String> lstGoodPairs = new ArrayList<String>();
	public static List<String> SDF(Set<String> Ds, Set<String> Dt, double threshold, int k)
	{
		lstGoodPairs.add("label1;label2;scoreThreshold;K_hash;total_K_lHash");
		for (String s : Ds) {
			Map<Character, Integer> hs = hash(s);
			for (String t : Dt) {
				calcSim(hs,s,t,k,threshold);
			}
		}
		return lstGoodPairs;
	}
	
	public static void calcSim(Map<Character, Integer> hs, String s, String t, int k, double threshold) {
		double [] sk = new double[s.length()+t.length()];
		int count=0, nSim=0;
		double ell=0.001d;
		
		if(normalSkip(s,t))
			return;
		
		Map<Character, Integer> ht = hash(t);
		if(minMaxSkip(hs,ht,threshold, k))
		{
			//System.out.println("MinMaxSkip: " + s + ";" + t);
			NewSDF.minMaxSkip++;
			return;
		}
		for (Character c : hs.keySet()) {
			if(ht.containsKey(c) && count <= k)
			{
				nSim += hs.get(c).intValue() + ht.get(c).intValue();
			}
			sk[count] = (double)nSim / ((double)s.length()+(double)t.length());
			if(sk[count] >= threshold)
			{
				lstGoodPairs.add(s + ";" + t +";" + sk[count] + ";" + (count+1) + ";" + Math.max(hs.size(), ht.size()));
				return;
			}
//			if(count > 0)
//			{
//				if((Math.abs(sk[count]) - Math.abs(sk[count-1]))/Math.abs(sk[count]) < ell)
//					return;
//			}
			count++;
		}
		
	}

	private static boolean normalSkip(String s, String t) {
		
		return false;
	}

	private static boolean minMaxSkip(Map<Character, Integer> hs, Map<Character, Integer> ht, double threshold, int k)
	{
		int count=0;
		double max=0;
		double min= (threshold/2);
		final int halfHs = (((hs.size() / 2) % 2) == 0) ? (hs.size() / 2) : (hs.size() / 2) +1;
		final int halfHt = (((ht.size() / 2) % 2) == 0) ? (ht.size() / 2) : (ht.size() / 2) +1;
		
		//if(((halfHs/2) < k) || ((halfHt/2) < k))
//		if((halfHs < k) || (halfHt < k))
//			return true;
		
		for (Character c : hs.keySet()) {
			
			if(count >= halfHs)
				break;
			
			if(ht.containsKey(c))
			{
				max += hs.get(c).intValue() + ht.get(c).intValue();
			}
			count++;
		}
		max=(max/(((double)halfHs+1)+((double)halfHt+1)));
		if (max < min)
			return true;
		
		return false;
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
	 * @param String to be hashed.
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
