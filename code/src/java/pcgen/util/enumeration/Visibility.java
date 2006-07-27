package pcgen.util.enumeration;

public enum Visibility {

	HIDDEN("No"),               // Does not show up either in the GUI or on the output sheet
	DEFAULT("Yes"),             // Shows up both in the GUI and on the output sheet
	OUTPUT_ONLY("Export"),      // Shows up on the output sheet, but not in the GUI
	DISPLAY_ONLY("Display");     //  Shows up in the GUI, but not on the output sheet

	private final String text;
	
	Visibility(String s)
	{
		text = s;
	}
	
	public String toString()
	{
		return text;
	}
}
