package gmgen.plugin.dice;

import java.util.stream.IntStream;

public class SimpleSumCounter implements ResultCounter
{
	@Override
	public int totalCount(final int[] in)
	{
		return IntStream.of(in).sum();
	}
}
