package gmgen.plugin.dice;


import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Random;

public class SystemDieConfig implements DiceConfig
{
	private final int n;
	private final int sides;
	private final int bias;
	private final Random random;

	private final ResultCounter counter;
	private final ResultModifier[] modifiers;

	public SystemDieConfig(final int n, final int sides, final int bias, final Random random) {
		this.n = n;
		this.sides = sides;
		this.bias = bias;
		this.random = random;
		counter = new SimpleSumCounter();
		modifiers = new ResultModifier[] {
			new AppendModifier(n, sides, random),
			new SystemModifier(),
			new SimpleModifier(bias)
		};
	}

	@Override
	public int roll()
	{
		return counter.totalCount(
				ResultModifier.modify(modifiers)
		);
	}

	@Override
	public String toFormula()
	{
		return MessageFormat.format("system({0}d{1} + {2})", n, sides, bias);
	}

	@Override
	public String toString()
	{
		return "SystemDieConfig{" +
				"n=" + n +
				", sides=" + sides +
				", bias=" + bias +
				", random=" + random +
				", counter=" + counter +
				", modifiers=" + Arrays.toString(modifiers) +
				'}';
	}
}
