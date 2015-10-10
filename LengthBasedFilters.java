
public class LengthBasedFilters {
	// (2/3) + (s1/(3*s2)) + l * p * ((1/3) - (s1/(3*t)))
	public static double lBasedFilter(String s1, String s2)
	{
		char str1 [] = s1.toCharArray();
		char str2 [] = s2.toCharArray();
		
	    int str1_len = s1.length();
	    int str2_len = s2.length();
	    
	 // if both strings are empty return 1
	    // if only one of the strings is empty return 0
	    if (str1_len == 0) return str2_len == 0 ? 1.0 : 0.0;

	    // max distance between two chars to be considered matching
	    // floor() is ommitted due to integer division rules
	    int match_distance = (int) Math.max(str1_len, str2_len)/2 - 1;

	    // arrays of bools that signify if that char in the matcing string has a match
	    //int *str1_matches = calloc(str1_len, sizeof(int));
	    //int *str2_matches = calloc(str2_len, sizeof(int));
	    boolean str1_matches [] = new boolean[str1_len];
	    boolean str2_matches [] = new boolean[str2_len];
	    
	    // number of matches and transpositions
	    double matches = 0.0;
	    double transpositions = 0.0;

	    // find the matches
	    for (int i = 0; i < str1_len; i++) {
	        // start and end take into account the match distance
	        int start = Math.max(0, i - match_distance);
	        int end = Math.min(i + match_distance + 1, str2_len);

	        // add comments...
	        for (int k = start; k < end; k++) {
	            // if str2 already has a match continue
	            if (str2_matches[k]) continue;
	            // if str1 and str2 are not
	            if (str1[i] != str2[k]) continue;
	            // otherwise assume there is a match
	            str1_matches[i] = true;
	            str2_matches[k] = true;
	            matches++;
	            break;
	        }
	    }

	    // if there are no matches return 0
	    if (matches == 0) {
	        return 0.0;
	    }

	    // count transpositions
	    int k = 0;
	    for (int i = 0; i < str1_len; i++) {
	        // if there are no matches in str1 continue
	        if (!str1_matches[i]) continue;
	        // while there is no match in str2 increment k
	        while (!str2_matches[k]) k++;
	        // increment transpositions
	        if (str1[i] != str2[k]) transpositions++;
	        k++;
	    }

	    // divide the number of transpositions by two as per the algorithm specs
	    // this division is valid because the counted transpositions include both
	    // instances of the transposed characters.
	    transpositions /= 2.0;
	    
	 // finds the number of common terms in the first 3 strings, max 3.
	    int prefix_length = 0;
	    if (s1.length() != 0 && s2.length() != 0)
	    {    
	    	//while (prefix_length < 3 && equal(*str1++, *str2++)) prefix_length++;
	    	int i = 0;
	    	while (prefix_length < 3)
	    	{
	    		if(str1[i] == str2[i])
	    			prefix_length++;
	    		else
	    			break;
	    		i++;
	    	}
	    }
	    
	    double p = 0.1;
	    // (2/3) + (s1/(3*s2)) + l * p * ((1/3) - (s1/(3*t)))
		return (2/3) + (str1_len/(3*str2_len)) + prefix_length * p * ((1/3) - (str1_len/(3*transpositions)));
	}
}
