package pcgen.base.format.dice;

import pcgen.base.util.BasicIndirect;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

public class DiceFormat implements FormatManager<Dice>
{

	@Override
	public Dice convert(String inputStr)
	{
		int dLoc = inputStr.indexOf("d");
		if (dLoc == -1)
		{
			throw new IllegalArgumentException(
				inputStr + " is not valid for Dice");
		}
		int quantity;
		if (dLoc == 0)
		{
			quantity = 1;
		}
		else
		{
			quantity = Integer.parseInt(inputStr.substring(0, dLoc));
		}
		int sides = Integer.parseInt(inputStr.substring(dLoc + 1));
		Die d = new Die(sides);
		return new Dice(quantity, d);
	}

	@Override
	public Indirect<Dice> convertIndirect(String inputStr)
	{
		return new BasicIndirect<>(this, convert(inputStr));
	}

	@Override
	public String unconvert(Dice d)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(d.getQuantity());
		sb.append('d');
		sb.append(d.getDie().getSides());
		return sb.toString();
	}

	@Override
	public Class<Dice> getManagedClass()
	{
		return Dice.class;
	}

	@Override
	public String getIdentifierType()
	{
		return "DICE";
	}

	@Override
	public FormatManager<?> getComponentManager()
	{
		return null;
	}

}
