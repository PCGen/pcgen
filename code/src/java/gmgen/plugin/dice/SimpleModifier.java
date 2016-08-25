package gmgen.plugin.dice;

public class SimpleModifier implements ResultModifier
{
	private int mod;

	public SimpleModifier(int mod) {
		this.mod = mod;
	}

	@Override
	public int[] resultAsModified(final int[] in)
	{
		int[] result = new int[in.length];
		for (int i = 0; i < in.length; ++i)
		{
			result[i] = in[i] + mod;
		}
		return result;

	}
}
