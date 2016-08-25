package gmgen.plugin.dice;

import java.util.Random;

public class AppendModifier implements ResultModifier
{

	private final int count;
	private final int max;
	private final Random rand;

	public AppendModifier(final int count, final int max, final Random rand) {
		this.count = count;
		this.max = max;
		this.rand = rand;
	}

	@Override
	public int[] resultAsModified(int[] in)
	{
		int[] newResults = new int[count + in.length];
		System.arraycopy(in, 0, newResults, 0, in.length);
		for (int i = 0; i < count; ++i)
		{
			int thisRoll = rand.nextInt(max) + 1;
			newResults[in.length + i] = thisRoll;
		}
		return newResults;
	}
}
