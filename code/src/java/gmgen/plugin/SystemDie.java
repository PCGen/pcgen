package gmgen.plugin;


/** A d20 die, applies a +10 on a 20, and a -10 on a 1
 * @author Soulcatcher
 * @since May 24, 2003
 */
public class SystemDie extends Die
{
	/**  Modifier to each roll. */
	public int aModifier;

	/** Constructor for the SystemDie object
	 * @param modifier Modifier to each roll
	 */
	public SystemDie(int modifier)
	{
		this.num = 1;
		this.sides = 20;
		this.aModifier = modifier;
		rolls = new int[num];
		roll();
	}

	/**  Constructor for the SystemDie object */
	public SystemDie()
	{
		this(0);
	}

	/** Roll the die.  If the roll is 20, return 30, if it's 1, returns -9.
	 * I made it final as it is called from the constructor (it's usually unwise to invoke overridable methods during the construction phase of an object.)
	 * @return result from the roll
	 */
    @Override
	public final int roll()
	{
		int value = 0;
		int i;
		total = 0;

		for (i = 0; i < num; i++)
		{
			int thisRoll = rand.nextInt(sides) + 1 + aModifier;
			rolls[i] = thisRoll;

			if (thisRoll == 1)
			{
				value -= 10;
			}

			if (thisRoll == 20)
			{
				value += 10;
			}

			value = thisRoll + value;
		}

		total = value;
		timesRolled++;

		return total;
	}

	/** Name of the die in nds+m form
	 * @return Name of the die
	 */
	@Override
	public String toString()
	{
		if (aModifier == 0)
		{
			return num + "d" + sides;
		}
		return num + "d" + sides + "+" + aModifier;
	}
}
