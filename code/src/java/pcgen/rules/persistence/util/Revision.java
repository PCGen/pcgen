/**
 * 
 */
package pcgen.rules.persistence.util;

class Revision implements Comparable<Revision>
{
	private final int primarySequence;
	private final int secondarySequence;
	private final int tertiarySequence;

	public Revision(int a, int b, int c)
	{
		primarySequence = a;
		secondarySequence = b;
		tertiarySequence = c;
	}

	public int compareTo(Revision r)
	{
		if (primarySequence > r.primarySequence)
		{
			return -1;
		}
		else if (primarySequence < r.primarySequence)
		{
			return 1;
		}
		else if (secondarySequence > r.secondarySequence)
		{
			return -1;
		}
		else if (secondarySequence < r.secondarySequence)
		{
			return 1;
		}
		else if (tertiarySequence > r.tertiarySequence)
		{
			return -1;
		}
		else if (tertiarySequence < r.tertiarySequence)
		{
			return 1;
		}
		return 0;
	}

	@Override
	public String toString()
	{
		return primarySequence + "." + secondarySequence + "-"
				+ tertiarySequence;
	}
}