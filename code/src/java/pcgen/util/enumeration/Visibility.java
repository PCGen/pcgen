package pcgen.util.enumeration;

public enum Visibility
{

	HIDDEN("No"), // Does not show up either in the GUI or on the output sheet
	DEFAULT("Yes"), // Shows up both in the GUI and on the output sheet
	OUTPUT_ONLY("Export"), // Shows up on the output sheet, but not in the GUI
	DISPLAY_ONLY("Display"); //  Shows up in the GUI, but not on the output sheet

	private final String text;

	Visibility(String s)
	{
		text = s;
	}

	@Override
	public String toString()
	{
		return text;
	}

	/**
	 * Determine if this visibility can be seen in the supplied view level.
	 * 
	 * @param view The view level.
	 * @param isExporting Is the visibility being detemerined for an export function
	 * @return true if the visibility can be viewed, false if not.
	 */
	public boolean isVisibileTo(View view, boolean isExporting)
	{
		if (view == View.ALL)
		{
			return true;
		}
		if (view == View.HIDDEN)
		{
			if (this == Visibility.HIDDEN || this == Visibility.DISPLAY_ONLY)
			{
				return true;
			}
		}
		else
		{
			if (this == Visibility.DEFAULT || this == Visibility.OUTPUT_ONLY)
			{
				return true;
			}
		}
		return false;
	}
}
