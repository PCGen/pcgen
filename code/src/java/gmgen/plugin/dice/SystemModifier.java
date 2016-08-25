package gmgen.plugin.dice;

/**
 * If the original value was 1, produces -9
 * If the original value was 20, producds 30
 * Otherwise produces results unchanged
 */
public class SystemModifier implements ResultModifier
{
	@Override
	public int[] resultAsModified(final int[] in)
	{
		int[] result = new int[in.length];
		for (int i = 0; i < in.length; ++i)
		{
			switch (in[i]) {
				case 1:
					result[i] = -9;
					break;
				case 20:
					result[i] = 30;
					break;
				default:
					result[i] = in[i];
			}
		}
		return result;

	}
}
