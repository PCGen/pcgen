package pcgen.util.enumeration;

public enum Visibility
{

	HIDDEN("No"), // Does not show up either in the GUI or on the output sheet
	DEFAULT("Yes"), // Shows up both in the GUI and on the output sheet
	OUTPUT_ONLY("Export"), // Shows up on the output sheet, but not in the GUI
	DISPLAY_ONLY("Display"), //  Shows up in the GUI, but not on the output sheet
	QUALIFY("Qualify"); //Shows up only if qualified
	
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
	
	public String getLSTFormat()
	{
		return text.toUpperCase();
	}

	/**
	 * Determine if this visibility can be seen in the supplied view level.
	 * 
	 * @param view The view level.
	 * @param isExporting Is the visibility being determined for an export function
	 * @return true if the visibility can be viewed, false if not.
	 */
	public boolean isVisibleTo(View view)
	{
		switch (view)
		{
			case ALL:
				return true;

			case HIDDEN_DISPLAY:
				return (this == Visibility.HIDDEN || this == Visibility.OUTPUT_ONLY);

			case HIDDEN_EXPORT:
				return (this == Visibility.HIDDEN || this == Visibility.DISPLAY_ONLY);

			case VISIBLE_EXPORT:
				return (this == Visibility.DEFAULT || this == Visibility.OUTPUT_ONLY);
				
			case VISIBLE_DISPLAY:
			default:
				return (this == Visibility.DEFAULT || this == Visibility.DISPLAY_ONLY);
		}
		/*
		 * TODO Need to deal with QUALIFY
		 */
	}
	
}
