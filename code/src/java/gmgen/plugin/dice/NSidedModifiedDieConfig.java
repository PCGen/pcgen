package gmgen.plugin.dice;


import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Random;

public class NSidedModifiedDieConfig implements DiceConfig
{
	private final int n;
	private final int sides;
	private final int bias;

	private final ResultCounter counter;
	private final ResultModifier[] modifiers;

	public NSidedModifiedDieConfig(final int n, final int sides, final int bias, final Random random) {
		this.n = n;
		this.sides = sides;
		this.bias = bias;
		counter = new SimpleSumCounter();
		modifiers = new ResultModifier[] {
			new AppendModifier(n, sides, random),
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
		if (bias == 0) {
			return MessageFormat.format("{0}d{1}", n, sides);
		}
		return MessageFormat.format("{0}d{1} + {2}", n, sides, bias);
	}
}
