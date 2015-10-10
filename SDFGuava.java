import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.SortedMultiset;
import com.google.common.primitives.Chars;

public class SDFGuava {

	public static char getMax(final String word) {
		checkArgument(StringUtils.isNotEmpty(word), "empty input is not allowed");

		final Predicate<Character> allValidPredicate = new Predicate<Character>() {
			@Override
			public boolean apply(final Character c) {
				return CharMatcher.JAVA_LETTER_OR_DIGIT.matches(c);
			}
		};

		final List<Character> inputCharList = Chars.asList(word.toCharArray());
		final Iterable<Character> validInputChars = Iterables.filter(inputCharList, allValidPredicate);
		final Multiset<Character> countedChars = LinkedHashMultiset.create(validInputChars);
		checkArgument(!countedChars.isEmpty(), "Input does not contain any valid chars: %s", word);
		final Multiset<Character> highestCountFirst = Multisets.copyHighestCountFirst(countedChars);
		return highestCountFirst.iterator().next();
	}
	
	public static int getSDF(String s1, String s2)
	{
		int iRet=0;
		final Predicate<Character> allValidPredicate = new Predicate<Character>() {
			@Override
			public boolean apply(final Character c) {
				return CharMatcher.JAVA_LETTER_OR_DIGIT.matches(c);
			}
		};

		List<Character> inputCharList = Chars.asList(s1.toCharArray());
		Iterable<Character> validInputChars = Iterables.filter(inputCharList, allValidPredicate);
		Multiset<Character> countedChars = LinkedHashMultiset.create(validInputChars);
		checkArgument(!countedChars.isEmpty(), "Input does not contain any valid chars: %s", s1);
		Multiset<Character> h1 = Multisets.copyHighestCountFirst(countedChars);
		
		inputCharList = Chars.asList(s2.toCharArray());
		validInputChars = Iterables.filter(inputCharList, allValidPredicate);
		countedChars = LinkedHashMultiset.create(validInputChars);
		if(validInputChars.toString().length() == countedChars.size())
		{
			ImmutableMultiset<Character> top = Multisets.copyHighestCountFirst(countedChars);
			Collections.sort(top.asList());
			
		}
		checkArgument(!countedChars.isEmpty(), "Input does not contain any valid chars: %s", s2);
		final Multiset<Character> h2 = Multisets.copyHighestCountFirst(countedChars);
		
		System.out.println("Guava1:"+ h1);
		System.out.println("Guava2:"+ h2);
		return iRet;
	}
}
