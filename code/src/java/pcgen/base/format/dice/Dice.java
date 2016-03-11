package pcgen.base.format.dice;

public class Dice
{
	private final Die die;
	private final int quantity;

	public Dice(int quantity, Die die)
	{
		if (quantity < 0)
		{
			throw new IllegalArgumentException(
				"Quantity cannot be < 0 in Dice");
		}
		if (die == null)
		{
			throw new IllegalArgumentException("Die cannot be null in Dice");
		}
		this.quantity = quantity;
		this.die = die;
	}

	public Die getDie()
	{
		return die;
	}

	public int getQuantity()
	{
		return quantity;
	}

}
