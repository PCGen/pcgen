package gmgen.plugin;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/** This class does the rolling of the dice for the GMGen system.
 * @author Expires 2003
 * @version 2.10
 */
public class DieEx extends Die
{
	/** Number of sides of the die that is being rolled */
	public int aModifier;

	/** Drop high roll */
	private boolean highDrop;

	/** Dice roll that is dropped */
	private int drops;

	/** Creates an instance of this class to vet values as a die roll.
	 * @param roll Roll that needs to be made
	 */
	public DieEx(String roll)
	{
		StringTokenizer strTok = new StringTokenizer(roll, "d ");
		String hold = "";
		num = Integer.parseInt(strTok.nextToken());
		sides = Integer.parseInt(strTok.nextToken());
		rolls = new int[num];

		if (strTok.hasMoreTokens())
		{
			try
			{
				hold = strTok.nextToken();
				hold = strTok.nextToken();
			}
			catch (NoSuchElementException e)
			{
				drops = 0;
			}

			try
			{
				drops = Integer.parseInt(hold);
			}
			catch (NoSuchElementException e)
			{
				drops = 0;
			}

			try
			{
				hold = strTok.nextToken();
			}
			catch (NoSuchElementException e)
			{
				hold = "";
			}

			highDrop = !(hold.equals("lowest") || hold.equals(""));
		}
	}

	/** Creates an instance of this class using the default roll */
	public DieEx()
	{
		this("1d6");
	}

	/** Method used for testing and running on it's own
	 * @param args Command line arguments
	 */
	public static void main(String[] args)
	{
		DieEx DieRoller;
		StringBuffer temp = new StringBuffer();

		for (int x = 0; x < args.length; x++)
		{
			temp.append(args[x]).append(" ");
		}

		DieRoller = new DieEx(temp.toString());
		System.out.println("you rolled " + DieRoller.roll());
	}

	/** Rolls the die using the paramaters set
	 * @return Value of the die rolls
	 */
    @Override
	public int roll()
	{
		total = 0;

		for (int x = 0; x < num; x++)
		{
			rolls[x] = rand.nextInt(sides) + 1;
			total += rolls[x];
		}

		if (drops != 0)
		{
			// sort rolls first or this doesn't work.
			Arrays.sort(rolls);

			if (!highDrop)
			{
				for (int x = 0; ((x < drops) && (x < rolls.length)); x++)
				{
					total -= rolls[x];
				}
			}
			else
			{
				for (int x = rolls.length - 1; x > (rolls.length - drops - 1); x--)
				{
					total -= rolls[x];
				}
			}
		}

		timesRolled++;

		return total;
	}

	/** Creates a <code>String</code> representation of this class
	 * @return This class as a <code>String</code>.
	 */
	@Override
	public String toString()
	{
		return num + "d" + sides;
	}
}
