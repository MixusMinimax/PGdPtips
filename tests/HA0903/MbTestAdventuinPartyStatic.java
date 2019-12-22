package pgdp.adventuin;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pgdp.color.RgbColor;
import pgdp.color.RgbColor8Bit;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MbTestAdventuinPartyStatic
{
	// change these parameters
	private static final double DELTA = 1e-15; // precision when comparing doubles
	private static final boolean USE_RANDOM_SEED = false;
	private static final Random RANDOM = new Random(USE_RANDOM_SEED ? new Date().getTime() : 1337);
	// -----------------------

	private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private static final PrintStream originalOut = System.out;
	private static final PrintStream originalErr = System.err;

	private static final String newline = System.getProperty("line.separator");
	private static final String greetingGerman = "Fröhliche Weihnachten wünscht dir %s!";
	private static final String greetingEnglish = "%s wishes you a Merry Christmas!";

	private MbTestAdventuinPartyStatic()
	{
		DoubleStream.of(1).average();
	}

	private static int randomInt(int bound)
	{
		return RANDOM.nextInt(bound);
	}

	private static int randomInt(int min, int max)
	{
		return RANDOM.nextInt(max - min) + min;
	}

	private static <T extends Enum<?>> T randomType(Class<T> _class)
	{
		int index = randomInt(_class.getEnumConstants().length);
		return _class.getEnumConstants()[index];
	}

	private static RgbColor randomColor()
	{
		return new RgbColor(8, randomInt(256), randomInt(256), randomInt(256));
	}

	private static <K> void assertEqualsDoubleMap(Map<K, Double> expected, Map<K, Double> actual)
	{
		for (K key : expected.keySet())
		{
			assertTrue(actual.containsKey(key));
			assertEquals(expected.get(key), actual.get(key), DELTA);
		}
	}

	@BeforeAll static void setUpStreams()
	{
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@Test void testGroupByHatType()
	{
		List<Adventuin> adventuins = List
				.of(new Adventuin("peng1", randomInt(20, 100), randomColor(), HatType.FISHY_HAT,
								randomType(Language.class)),
						new Adventuin("peng2", randomInt(20, 100), randomColor(), HatType.FISHY_HAT,
								randomType(Language.class)),
						new Adventuin("peng3", randomInt(20, 100), randomColor(), HatType.NO_HAT,
								randomType(Language.class)),
						new Adventuin("peng4", randomInt(20, 100), randomColor(), HatType.NO_HAT,
								randomType(Language.class)),
						new Adventuin("peng5", randomInt(20, 100), randomColor(), HatType.REINDEER,
								randomType(Language.class)),
						new Adventuin("peng6", randomInt(20, 100), randomColor(), HatType.REINDEER,
								randomType(Language.class)),
						new Adventuin("peng7", randomInt(20, 100), randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("peng8", randomInt(20, 100), randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)));

		Map<HatType, List<Adventuin>> mapExpected = new HashMap<>();
		mapExpected.put(HatType.FISHY_HAT, List.of(adventuins.get(0), adventuins.get(1)));
		mapExpected.put(HatType.NO_HAT, List.of(adventuins.get(2), adventuins.get(3)));
		mapExpected.put(HatType.REINDEER, List.of(adventuins.get(4), adventuins.get(5)));
		mapExpected.put(HatType.SANTA_CLAUS, List.of(adventuins.get(6), adventuins.get(7)));

		Map<HatType, List<Adventuin>> mapActual = AdventuinParty.groupByHatType(adventuins);

		assertEquals(mapExpected, mapActual);
	}

	@Test void testPrintLocalizedChristmasGreetings()
	{
		List<Adventuin> adventuins = List
				.of(new Adventuin("peng1", 1, randomColor(), randomType(HatType.class), Language.GERMAN),
						new Adventuin("peng2", 5, randomColor(), randomType(HatType.class), Language.ENGLISH),
						new Adventuin("peng3", 7, randomColor(), randomType(HatType.class), Language.GERMAN),
						new Adventuin("peng4", 2, randomColor(), randomType(HatType.class), Language.ENGLISH),
						new Adventuin("peng5", 9, randomColor(), randomType(HatType.class), Language.ENGLISH),
						new Adventuin("peng6", 10, randomColor(), randomType(HatType.class), Language.GERMAN),
						new Adventuin("peng7", 3, randomColor(), randomType(HatType.class), Language.GERMAN),
						new Adventuin("peng8", 8, randomColor(), randomType(HatType.class), Language.ENGLISH));

		AdventuinParty.printLocalizedChristmasGreetings(adventuins);

		String expectedOutput = String.format(greetingGerman, adventuins.get(0).getName()) + newline + String
				.format(greetingEnglish, adventuins.get(3).getName()) + newline + String
				.format(greetingGerman, adventuins.get(6).getName()) + newline + String
				.format(greetingEnglish, adventuins.get(1).getName()) + newline + String
				.format(greetingGerman, adventuins.get(2).getName()) + newline + String
				.format(greetingEnglish, adventuins.get(7).getName()) + newline + String
				.format(greetingEnglish, adventuins.get(4).getName()) + newline + String
				.format(greetingGerman, adventuins.get(5).getName()) + newline;
		assertEquals(expectedOutput, outContent.toString());
		outContent.reset();
	}

	@Test void testGetAdventuinsWithLongestNamesByHatType()
	{
		List<Adventuin> adventuins = List
				.of(new Adventuin("4444", 10, randomColor(), HatType.FISHY_HAT, randomType(Language.class)),
						new Adventuin("666666", 11, randomColor(), HatType.FISHY_HAT, randomType(Language.class)),
						new Adventuin("55555", 12, randomColor(), HatType.FISHY_HAT, randomType(Language.class)),
						new Adventuin("666666", 13, randomColor(), HatType.FISHY_HAT, randomType(Language.class)),
						new Adventuin("4444", 14, randomColor(), HatType.NO_HAT, randomType(Language.class)),
						new Adventuin("1", 15, randomColor(), HatType.NO_HAT, randomType(Language.class)),
						new Adventuin("333", 16, randomColor(), HatType.NO_HAT, randomType(Language.class)),
						new Adventuin("22", 17, randomColor(), HatType.NO_HAT, randomType(Language.class)),
						new Adventuin("55555", 18, randomColor(), HatType.REINDEER, randomType(Language.class)),
						new Adventuin("55555", 19, randomColor(), HatType.REINDEER, randomType(Language.class)),
						new Adventuin("22", 110, randomColor(), HatType.REINDEER, randomType(Language.class)),
						new Adventuin("55555", 111, randomColor(), HatType.REINDEER, randomType(Language.class)),
						new Adventuin("7777777", 112, randomColor(), HatType.SANTA_CLAUS, randomType(Language.class)),
						new Adventuin("333", 113, randomColor(), HatType.SANTA_CLAUS, randomType(Language.class)),
						new Adventuin("7777777", 114, randomColor(), HatType.SANTA_CLAUS, randomType(Language.class)),
						new Adventuin("333", 115, randomColor(), HatType.SANTA_CLAUS, randomType(Language.class)));

		Map<HatType, List<Adventuin>> expected = new HashMap<>();

		expected.put(HatType.FISHY_HAT, List.of(adventuins.get(1), adventuins.get(3)));
		expected.put(HatType.NO_HAT, List.of(adventuins.get(4)));
		expected.put(HatType.REINDEER, List.of(adventuins.get(8), adventuins.get(9), adventuins.get(11)));
		expected.put(HatType.SANTA_CLAUS, List.of(adventuins.get(12), adventuins.get(14)));

		Map<HatType, List<Adventuin>> actual = AdventuinParty.getAdventuinsWithLongestNamesByHatType(adventuins);

		assertEquals(expected, actual);
	}

	@Test void testGetAverageColorBrightnessByHeight()
	{
		List<Adventuin> adventuins = List
				.of(new Adventuin("0", 54, new RgbColor8Bit(0, 0, 0), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("1", 50, new RgbColor8Bit(127, 26, 5), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("2", 45, new RgbColor8Bit(67, 16, 246), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("3", 95, new RgbColor8Bit(192, 24, 64), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("4", 99, new RgbColor8Bit(25, 60, 159), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("5", 104, new RgbColor8Bit(167, 51, 101), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("6", 5, new RgbColor8Bit(106, 62, 130), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("7", 11, new RgbColor8Bit(44, 130, 237), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("8", 14, new RgbColor8Bit(139, 5, 23), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("9", 25, new RgbColor8Bit(244, 188, 35), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("10", 29, new RgbColor8Bit(46, 1, 9), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("11", 34, new RgbColor8Bit(44, 226, 183), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("12", 195, new RgbColor8Bit(223, 61, 186), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("13", 199, new RgbColor8Bit(96, 60, 171), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("14", 204, new RgbColor8Bit(39, 164, 182), randomType(HatType.class),
								randomType(Language.class)),
						new Adventuin("15", 4, new RgbColor8Bit(62, 159, 177), randomType(HatType.class),
								randomType(Language.class)));

		Map<Integer, Double> expected = new HashMap<>();

		expected.put(0, 0.5477545098039216);
		expected.put(50, 0.11686928104575163);
		expected.put(100, 0.26350745098039213);
		expected.put(200, 0.41680862745098035);
		expected.put(10, 0.3012990849673203);
		expected.put(30, 0.5022298039215687);

		Map<Integer, Double> actual = AdventuinParty.getAverageColorBrightnessByHeight(adventuins);

		assertEqualsDoubleMap(expected, actual);
	}

	@Test void testGetDiffOfAvgHeightDiffsToPredecessorByHatType()
	{
		List<Adventuin> adventuins = List
				.of(new Adventuin("zOZAPKZWppIJycpsQcwg", 113, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("XPEWUpZSVEMwqactxDDR", 69, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("WGlPgSKujFxzLPTfKnas", 172, randomColor(), HatType.FISHY_HAT,
								randomType(Language.class)),
						new Adventuin("puZXIjjeYcAuiyDdpnQl", 159, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("hHQgfZcWXFKoPQheuXlv", 55, randomColor(), HatType.FISHY_HAT,
								randomType(Language.class)),
						new Adventuin("RLbVdAvBDrLxsNlmwSOO", 90, randomColor(), HatType.REINDEER,
								randomType(Language.class)),
						new Adventuin("zNdjSwKcaMsGCQZtpWIY", 45, randomColor(), HatType.FISHY_HAT,
								randomType(Language.class)),
						new Adventuin("ljgCBckhebzgIruUDCwR", 42, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("hIFpAvTyISBEmCYBQdHE", 16, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("aDxlSpeRdjtHHRlZFybn", 96, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("PcAzoJyinqMvsEbUXGcR", 187, randomColor(), HatType.REINDEER,
								randomType(Language.class)),
						new Adventuin("illNBZTseCGbMsSdvUrf", 95, randomColor(), HatType.REINDEER,
								randomType(Language.class)),
						new Adventuin("KbiXsmMgmJwgRJzhEwMZ", 149, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("BMHVAPBGjgvYCwahYJsM", 162, randomColor(), HatType.REINDEER,
								randomType(Language.class)),
						new Adventuin("dBLZwYJhTWCNrIkZyyCF", 119, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("lNHJyhAjcRavIHFKHrae", 24, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("iEXSHJeDNDxIOnVQlZCt", 80, randomColor(), HatType.REINDEER,
								randomType(Language.class)),
						new Adventuin("STqjmBjuKVCouISitJCu", 191, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("xskyOaTbnLpXfumaTfpD", 30, randomColor(), HatType.NO_HAT,
								randomType(Language.class)),
						new Adventuin("WlpAUMSOJJTkUJgucyFW", 140, randomColor(), HatType.NO_HAT,
								randomType(Language.class)),
						new Adventuin("BTpFVRWTSHoqrjQehZuT", 177, randomColor(), HatType.REINDEER,
								randomType(Language.class)),
						new Adventuin("PjBjglubhkcjyRAvHwSv", 80, randomColor(), HatType.FISHY_HAT,
								randomType(Language.class)),
						new Adventuin("zVlCfAryOqPszGOVJCqe", 144, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("SgtHmzNZIvijgYgTlBsC", 179, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("wBiscWOidgsRYKVIAiqj", 14, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("vpDcyGxSiuOgjRXwOQbz", 93, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("JxtzmkHsQGFmTmmQseio", 90, randomColor(), HatType.REINDEER,
								randomType(Language.class)),
						new Adventuin("oAVHGUgZuAXKFazjIBhR", 108, randomColor(), HatType.NO_HAT,
								randomType(Language.class)),
						new Adventuin("qkbMWxBTnpguMnTrrNJO", 25, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)),
						new Adventuin("uoFfNYSUlWOiJHNzqyZS", 74, randomColor(), HatType.FISHY_HAT,
								randomType(Language.class)),
						new Adventuin("VXZOeXHDhCJUswIsLovU", 164, randomColor(), HatType.NO_HAT,
								randomType(Language.class)),
						new Adventuin("kmYlwFXCSDjePlUhXiIP", 137, randomColor(), HatType.SANTA_CLAUS,
								randomType(Language.class)));
		Map<HatType, Double> expected = new HashMap<>();
		expected.put(HatType.REINDEER, 174.0);
		expected.put(HatType.FISHY_HAT, 110.83333333333334);
		expected.put(HatType.SANTA_CLAUS, 156.44444444444446);
		expected.put(HatType.NO_HAT, 166.0);

		Map<HatType, Double> actual = AdventuinParty.getDiffOfAvgHeightDiffsToPredecessorByHatType(adventuins);

		assertEqualsDoubleMap(expected, actual);
	}

	@AfterAll static void restoreStreams()
	{
		System.setOut(originalOut);
		System.setErr(originalErr);
	}

}
