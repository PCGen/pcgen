package pcgen.core.analysis;

import java.util.List;

import pcgen.core.Globals;
import pcgen.core.PCAlignment;

public class AlignmentConverter
{
	private static final Class<PCAlignment> ALIGNMENT_CLASS = PCAlignment.class;

	public static PCAlignment getPCAlignment(String desiredAlignIdentifier)
	{
		PCAlignment desiredAlign;
		try
		{
			final int align = Integer.parseInt(desiredAlignIdentifier);
			List<PCAlignment> alignments = Globals.getContext().ref
					.getOrderSortedCDOMObjects(ALIGNMENT_CLASS);
			desiredAlign = alignments.get(align);
		}
		catch (NumberFormatException e)
		{
			// If it isn't a number, we expect the exception
			desiredAlign = Globals.getContext().ref.getAbbreviatedObject(
					ALIGNMENT_CLASS, desiredAlignIdentifier);
		}
		return desiredAlign;
	}

	public static PCAlignment getNoAlignment()
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				ALIGNMENT_CLASS, "NONE");
	}
}
