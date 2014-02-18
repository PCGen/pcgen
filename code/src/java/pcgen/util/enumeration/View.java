package pcgen.util.enumeration;

/**
 * <code>View</code> is an enumeration of possible view types. It is 
 * closely related to the Visibility enumeration.
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public enum View
{
	ALL("ALL"), // Show all visibility types
	HIDDEN_DISPLAY("HIDDEN_DISPLAY"), // Show only those not visible
	HIDDEN_EXPORT("HIDDEN_EXPORT"), // Show only those not visible
	VISIBLE_DISPLAY("VISIBLE_DISPLAY"), // Shows types visible to the GUI
	VISIBLE_EXPORT("VISIBLE_EXPORT"); // Shows types visible to the Export
	
	private final String text;

	/**
	 * Create a new view based on a name.
	 * @param s
	 */
	View(String s)
	{
		text = s;
	}

	@Override
	public String toString()
	{
		return text;
	}

	/**
	 * Retrieve a View matching the supplied name.
	 * @param name The name of the view
	 * @return The view, or null if not a view name.
	 */
	public static View getViewFromName(String name)
	{
		for (View view : View.values())
		{
			if (view.text.equalsIgnoreCase(name))
			{
				return view;
			}
		}

		return null;
	}
}
