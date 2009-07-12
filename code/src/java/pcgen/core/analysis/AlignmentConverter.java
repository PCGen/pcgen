package pcgen.core.analysis;

import java.util.List;

import pcgen.core.Globals;
import pcgen.core.PCAlignment;

public class AlignmentConverter
{
	public static PCAlignment getPCAlignment(String desiredAlignIdentifier)
	{
		PCAlignment desiredAlign;
		try
		{
			final int align = Integer.parseInt(desiredAlignIdentifier);
			List<PCAlignment> alignments = Globals.getContext().ref
					.getOrderSortedCDOMObjects(PCAlignment.class);
			desiredAlign = alignments.get(align);
		}
		catch (NumberFormatException e)
		{
			// If it isn't a number, we expect the exception
			desiredAlign = Globals.getContext().ref.getAbbreviatedObject(
					PCAlignment.class, desiredAlignIdentifier);
		}
		return desiredAlign;
	}

}
