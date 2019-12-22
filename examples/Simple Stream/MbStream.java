import java.util.*;
import java.util.stream.Stream;

public class MbStream<T>
{
	private Collection<T> elements;

	public MbStream(Collection<T> collection)
	{
		elements = collection;
	}

	public <K> MbStream<K> map(MbFunction<T, K> mapper)
	{
		List<K> newList = new ArrayList<>();
		for (T t : elements)
			newList.add(mapper.accept(t));
		return new MbStream<K>(newList);
	}

	public MbStream<T> filter(MbPredicate<T> pred)
	{
		List<T> newList = new ArrayList<>();
		for (T t : elements)
			if (pred.accept(t))
				newList.add(t);
		return new MbStream<T>(newList);
	}

	public void foreach(MbConsumer<T> consumer)
	{
		for (T t : elements)
			consumer.accept(t);
	}

	public T reduce(T neutral, MbBifunction<T, T, T> bifun)
	{
		for (T t : elements)
			neutral = bifun.accept(neutral, t);
		return neutral;
	}

	/**
	 * Not like the real implementation of collect, this just returns the elements directly.
	 *
	 * @return elements
	 */
	public Collection<T> collect()
	{
		return elements;
	}

	public static <K> MbStream<K> of(K[] elements)
	{
		return new MbStream<>(Arrays.asList(elements));
	}
}
