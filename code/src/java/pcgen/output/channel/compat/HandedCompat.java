package pcgen.output.channel.compat;

import java.util.Arrays;

import pcgen.cdom.enumeration.Handed;

/**
 * HandedCompat contains utility methods for communication of the PCs Handed value through
 * a channel.
 */
public final class HandedCompat
{

	private HandedCompat()
	{
		//Do not construct utility class
	}

	/**
	 * Returns the Default value for Handed.
	 * 
	 * @return The Default value for Handed
	 */
	public static Handed getDefaultHanded()
	{
		return Handed.Right;
	}

	/**
	 * Returns an array of the available Handed objects.
	 * 
	 * @return An array of the available Handed objects
	 */
	public static Handed[] getAvailableHanded()
	{
		return Handed.values();
	}

	/**
	 * Retrieve a Handed object to match the name ({@link #name()}) or localized name
	 * (output by {@link #toString()}). The localized lookup is kept for legacy purpose
	 * when the localized name was saved in the character files (instead of the
	 * {@link #name()}).
	 * 
	 * @param name
	 *            The localized display name of the Handed.
	 * @return The matching Handed.
	 */
	public static Handed getHandedByName(String name)
	{
		return Arrays.stream(getAvailableHanded())
				.filter(hand -> hand.toString().equals(name))
				.findFirst()
				.orElse(Handed.valueOf(name));
	}
}
