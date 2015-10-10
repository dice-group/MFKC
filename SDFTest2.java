/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SDFTest2 {

    /** Counting the number of occurrences of each character
     * @param character array
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
     * Sorts the counted numbers of characters (keys, values) by java Collection List
     * @param HashMap (with key as character, value as number of occurrences)
     * @return sorted HashMap
     */
  private static HashMap sortByValues(HashMap map) { 
       List list = new LinkedList(map.entrySet());
       // Defined Custom Comparator here
       Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
               return -1*((Comparable) ((Map.Entry) (o1)).getValue())
                  .compareTo(((Map.Entry) (o2)).getValue());
            }
       });

       // Here I am copying the sorted list in HashMap
       // using LinkedHashMap to preserve the insertion order
       HashMap sortedHashMap = new LinkedHashMap();
       for (Iterator it = list.iterator(); it.hasNext();) {
              Map.Entry entry = (Map.Entry) it.next();
              sortedHashMap.put(entry.getKey(), entry.getValue());
       } 
       return sortedHashMap;
  }
  /**
   * get most frequent k characters
   * @param array of characters
   * @param limit of k
   * @return hashed String 
   */
    public static String mostOcurrencesElement(char[] array, int k) {
        HashMap<Character, Integer> countMap = countElementOcurrences(array);
        System.out.println(countMap);
        Map<Integer, String> map = sortByValues(countMap); 
        System.out.println(map);
        Iterator it = map.entrySet().iterator();
        int i = 0;
        String output = new String();
        while (it.hasNext()&&i++<k) {
            Map.Entry pairs = (Map.Entry)it.next();
            output += ""+pairs.getKey()+pairs.getValue();
            it.remove(); // avoids a ConcurrentModificationException
        }
        return output;
    }
    /**
     * Calculates the similarity between two input strings
     * @param input string 1
     * @param input string 2
     * @param maximum possible limit value 
     * @return distance as integer
     */
    public int getDiff(String str1, String str2, int limit){
        int similarity=0;
        for(int i =0;i<str1.length();i+=2){
            //System.out.println(i);
            int pos = str2.indexOf(str1.charAt(i));
            //System.out.println(str2.charAt(i)+" - "+pos);
            if(pos>=0){
            	try{
            		similarity += Integer.parseInt(str2.substring(pos+1, pos+2))+Character.getNumericValue(str1.charAt(i+1));
            	}catch(Exception e){
            		e.printStackTrace();
            	}
            }
                
        }
            
        return limit-similarity;
    }
    /**
     * Wrapper function 
     * @param input string 1
     * @param input string 2
     * @param maximum possible limit value 
     * @return distance as integer
     */
    public int SDFfunc(String str1, String str2, int limit){
        return getDiff(mostOcurrencesElement(str1.toCharArray(),2),mostOcurrencesElement(str2.toCharArray(),2),limit);
    }

    public static void main(String[] args) {
        String input1 = new String("LCLYTHIGRNIYYGSYLYSETWNTGIMLLLITMATAFMGYVLPWGQMSFWGATVITNLFSAIPYIGTNLV");
        String input2 = new String("EWIWGGFSVDKATLNRFFAFHFILPFTMVALAGVHLTFLHETGSNNPLGLTSDSDKIPFHPYYTIKDFLG");
        SDFTest2 sdf = new SDFTest2();
        System.out.println (sdf.SDFfunc(input1,input2,100));
    }

}
