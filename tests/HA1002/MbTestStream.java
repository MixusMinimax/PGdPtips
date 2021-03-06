package pgdp.stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Maxi Barmetler, ge36yog
 * @version 0.2.1
 * <p>
 * -----INFORMATION-----
 * - adjust the parameters in the designated areas
 * - the global constant N_TESTS will override all individual n_tests, if it is greater than 0.
 * - the global constant STATIC_SEED will be used for RANDOM, if it isn't -1.
 */
public class MbTestStream
{
	/*------------------------PARAMETERS---*/
	private static final long STATIC_SEED = -1; // -1 for current time
	private static final int N_TESTS = 100;      // 0 for individual adjustments
	/*-------------------------------------*/

	private static final Random RANDOM = new Random(STATIC_SEED == -1 ? System.currentTimeMillis() : STATIC_SEED);

	/**
	 * random String, fixed size
	 *
	 * @param size - length of String
	 * @return random String
	 */
	private static String createRandomString(int size)
	{
		return RANDOM.ints(48, 123).filter(e -> e < 58 || (e > 64 && (e < 91 || e > 96))).limit(size)
				.mapToObj(e -> (char) e + "").collect(Collectors.joining());
	}

	/**
	 * random String, random size
	 *
	 * @param minSizeInclusive - minimum size of String (inclusive)
	 * @param maxSizeExclusive - maximum size of String (exclusive)
	 * @return random String
	 */
	private static String createRandomString(int minSizeInclusive, int maxSizeExclusive)
	{
		return createRandomString(RANDOM.nextInt(maxSizeExclusive - minSizeInclusive) + minSizeInclusive);
	}

	/**
	 * random String, random size between 20 and 50
	 *
	 * @return random String
	 */
	private static String createRandomString()
	{
		return createRandomString(20, 50);
	}

	/**
	 * Random numbers, fixed size
	 *
	 * @param size - size of List
	 * @return random List
	 */
	private static <T> List<T> createRandomList(Supplier<T> creator, int size)
	{
		return Stream.generate(creator).limit(size).collect(Collectors.toList());
	}

	/**
	 * Random integers, fixed size
	 *
	 * @param size - size of List
	 * @return random List
	 */
	private static List<Integer> createRandomIntList(int size)
	{
		return createRandomList(RANDOM::nextInt, size);
	}

	/**
	 * Random integers, random size
	 *
	 * @param minSizeInclusive - minimum size of List (inclusive)
	 * @param maxSizeExclusive - maximum size of List (exclusive)
	 * @return random List
	 */
	private static List<Integer> createRandomIntList(int minSizeInclusive, int maxSizeExclusive)
	{
		return createRandomIntList(RANDOM.nextInt(maxSizeExclusive - minSizeInclusive) + minSizeInclusive);
	}

	/**
	 * Random Strings, fixed size
	 *
	 * @param size - size of List
	 * @return random List
	 */
	private static List<String> createRandomStringList(int size)
	{
		return createRandomList(MbTestStream::createRandomString, size);
	}

	/**
	 * Random Strings, random size
	 *
	 * @param minSizeInclusive - minimum size of List (inclusive)
	 * @param maxSizeExclusive - maximum size of List (exclusive)
	 * @return random List
	 */
	private static List<String> createRandomStringList(int minSizeInclusive, int maxSizeExclusive)
	{
		return createRandomStringList(RANDOM.nextInt(maxSizeExclusive - minSizeInclusive) + minSizeInclusive);
	}

	private static List<DynamicTest> createTestList(Consumer<Long> tests, String name, int size)
	{
		return LongStream.range(0, size)
				.mapToObj(e -> DynamicTest.dynamicTest(name + " " + (e + 1), () -> tests.accept(e)))
				.collect(Collectors.toList());
	}

	@TestFactory List<DynamicTest> artemisTests()
	{
		return createTestList((index) -> {
			switch (index.intValue())
			{
			case 0:
				assertEquals(0, pgdp.stream.Stream.of().count(), "Example 1: Wrong count");
				break;

			case 1:
				assertEquals(List.of(), pgdp.stream.Stream.of().toCollection(ArrayList::new), "Example 2: Wrong list");
				break;

			case 2:
				assertEquals(3, pgdp.stream.Stream.of(1, 2, 3).count(), "Example 3: Wrong count");
				break;

			case 3:
				assertEquals(List.of(1, 2, 3), pgdp.stream.Stream.of(1, 2, 3).toCollection(ArrayList::new),
						"Example 4: Wrong List");
				break;

			case 4:
				assertEquals(1, pgdp.stream.Stream.of(1, 2, 3).filter(i -> i % 2 == 0).count(),
						"Example 5: Wrong count");
				break;

			case 5:
				assertEquals(Optional.of(4),
						pgdp.stream.Stream.of(1, 2, 3).map(i -> i * i).filter(i -> i % 2 == 0).findFirst(),
						"Example 6: Wrong value");
				break;

			case 6:
				assertEquals(3, pgdp.stream.Stream.of(1, 2, 3).map(i -> {
					if (i % 2 == 0)
						throw new IllegalArgumentException();
					return i;
				}).count(), "Example 7: Wrong count");
				break;

			case 7:
				assertEquals(List.of(42, 3, 42, 5), pgdp.stream.Stream.of(1, 2, 3, 4, 5).map(i -> {
					if (i % 2 == 0)
						throw new IllegalArgumentException();
					return i;
				}).filter(i -> i > 1).onErrorMap(list -> 42).toCollection(ArrayList::new), "Example 8: Wrong List");
				break;

			case 8:
				assertEquals(List.of("1", "2", "x:3", "null", "x:6"),
						pgdp.stream.Stream.of(1, 2, 3, 4, 5, 6).mapChecked(i -> {
							if (i % 3 == 0)
								throw new IOException("x:" + i);
							return i;
						}).filter(i -> i != 5).map(i -> i == 4 ? null : i).map(Object::toString)
								.onErrorMap(list -> list.get(0).getMessage()).toCollection(ArrayList::new).stream()
								.map(e -> e == null ? "null" : e).collect(Collectors.toList()),
						"Example 9: Wrong List");
				break;

			case 9:
				assertEquals(List.of(2, 1),
						pgdp.stream.Stream.of(1, 2, 3, 4, 5, 6).map(i -> i / (i - 1)).distinct().onErrorFilter()
								.toCollection(ArrayList::new), "Example 10: Wrong List");
				break;

			case 10:
				Exception was = null;
				try
				{
					pgdp.stream.Stream.of(1, 2, 3, 4, 5, 6).mapChecked(i -> {
						if (i > 10)
							throw new IOException("x:" + i);
						return i;
					}).reduce(Integer::sum);
				} catch (CheckedStreamException e)
				{
					was = e;
				}
				assertNotNull(was, "Example 11: No Exception");
				assertEquals(CheckedStreamException.class, was.getClass(), "Example 11: Wrong Exception");

				break;
			}
		}, "artemisTest", 11);
	}

	@TestFactory List<DynamicTest> countKnownSize()
	{
		/*---PARAMETERS---*/
		int nTests = 100;
		/*----------------*/

		if (N_TESTS > 0)
			nTests = N_TESTS;

		nTests += 2; // edge cases

		return createTestList((index) -> {

			List<Integer> numbers;

			switch (index.intValue())
			{
			case 0:
				numbers = List.of();
				break;

			case 1:
				numbers = List.of(1, 2, 3);
				break;

			default:
				numbers = createRandomIntList(10, 100);
			}

			long expectedCount = numbers.size();
			long actualCount = pgdp.stream.Stream.of(numbers).count();

			assertEquals(expectedCount, actualCount, "Wrong count in test " + index);

		}, "countKnownSize", nTests);
	}

	@TestFactory List<DynamicTest> countUnknownSize()
	{
		/*---PARAMETERS---*/
		int nTests = 100;
		/*----------------*/

		if (N_TESTS > 0)
			nTests = N_TESTS;

		nTests += 2; // edge cases

		return createTestList((index) -> {

			List<Integer> numbers;

			switch (index.intValue())
			{
			case 0:
				numbers = List.of();
				break;

			case 1:
				numbers = List.of(1, 2, 3);
				break;

			default:
				numbers = createRandomIntList(10, 100);
			}

			long expectedCount = numbers.size();
			long actualCount = pgdp.stream.Stream.of(numbers.stream()).count();

			assertEquals(expectedCount, actualCount, "Wrong count in test " + index);

		}, "countUnknownSize", nTests);
	}

	@TestFactory List<DynamicTest> findFirst()
	{
		/*---PARAMETERS---*/
		int nTests = 100;
		/*----------------*/

		if (N_TESTS > 0)
			nTests = N_TESTS;

		nTests += 2; // edge cases

		return createTestList((index) -> {

			List<Integer> numbers;

			switch (index.intValue())
			{
			case 0:
				numbers = List.of();
				break;

			case 1:
				numbers = List.of(1, 2, 3);
				break;

			default:
				numbers = createRandomIntList(10, 100);
			}

			Optional<Integer> expectedValue = numbers.isEmpty() ? Optional.empty() : Optional.of(numbers.get(0));
			Optional<Integer> actualValue = pgdp.stream.Stream.of(numbers).findFirst();

			assertEquals(expectedValue, actualValue, "Wrong value in test " + index);

		}, "findFirst", nTests);
	}

	@TestFactory List<DynamicTest> filter()
	{
		/*---PARAMETERS---*/
		int nTests = 10;
		int nFilters = 2;
		/*----------------*/

		if (N_TESTS > 0)
			nTests = N_TESTS;

		nTests += 2; // edge cases

		return createTestList((index) -> {

			List<Integer> numbers;

			numbers = createRandomIntList(10, 100);

			java.util.stream.Stream<Integer> realStream = numbers.stream();
			pgdp.stream.Stream<Integer> pgdpStream = pgdp.stream.Stream.of(numbers);

			Predicate<Integer> pred;
			switch (index.intValue())
			{
			case 0:
				realStream = realStream.filter(e -> true);
				pgdpStream = pgdpStream.filter(e -> true);
				break;

			case 1:
				realStream = realStream.filter(e -> false);
				pgdpStream = pgdpStream.filter(e -> false);
				break;

			default:
				for (int i = 0; i < nFilters; ++i)
				{
					final int mod = RANDOM.nextInt(63) + 1;
					pred = (num) -> num % mod != 0;

					realStream = realStream.filter(pred);
					pgdpStream = pgdpStream.filter(pred);
				}
			}

			List<Integer> expectedList = realStream.collect(Collectors.toList());
			List<Integer> actualList = (List<Integer>) pgdpStream.toCollection(ArrayList::new);

			assertEquals(expectedList, actualList, "Wrong List in test " + index);

		}, "filter", nTests);
	}

	@TestFactory List<DynamicTest> stringStream()
	{
		/*---PARAMETERS---*/
		int nTests = 10;
		/*----------------*/

		if (N_TESTS > 0)
			nTests = N_TESTS;

		return createTestList((index) -> {

			List<String> words;

			words = createRandomStringList(10, 100);

			java.util.stream.Stream<String> realStream = words.stream();
			pgdp.stream.Stream<String> pgdpStream = pgdp.stream.Stream.of(words);

			String append = createRandomString(10);

			Predicate<String> pred1 = e -> e.length() % 2 == 0;
			Function<String, String> mapper1 = e -> e + append;

			realStream = realStream.map(mapper1).filter(pred1).map(e -> {
				if (!e.contains("a"))
					return "error " + e.substring(0, 20);
				return e;
			}).filter(e->e.startsWith("error "));

			pgdpStream = pgdpStream.map(mapper1).filter(pred1).mapChecked(e -> {
				if (!e.contains("a"))
					throw new Exception("error " + e.substring(0, 20));
				return e;
			}).onErrorMapChecked(e->e.get(0).getLocalizedMessage()).mapChecked(e->{
				if (!e.startsWith("error "))
					throw new Exception();
				return e;
			}).onErrorFilter();

			List<String> expectedList = realStream.collect(Collectors.toList());
			List<String> actualList = (List<String>) pgdpStream.toCollection(ArrayList::new);

			assertEquals(expectedList, actualList, "Wrong List in test " + index);

		}, "stringStream", nTests);
	}
}
