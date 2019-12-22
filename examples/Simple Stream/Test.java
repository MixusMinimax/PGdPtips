import java.util.function.Function;

public class Test
{
	public static void main(String[] args)
	{
		MbStream<String> stream = MbStream.of(new String[] {"1", "2", "3", "4", "5", "6", "7", "8"});

		// sum of even numbers:
		int sum = stream.map(Integer::parseInt).filter(e->e % 2 == 0).reduce(0, (e,f) -> e + f);

		System.out.println(sum);

		// The following functions are all equivalent:
		Function<String, Integer> fun1 = String::length;

		Function<String, Integer> fun2 = str -> str.length();

		Function<String, Integer> fun3 = new Function<String, Integer>()
		{
			@Override public Integer apply(String str)
			{
				return str.length();
			}
		};
	}
}
