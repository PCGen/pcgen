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
	HIDDEN("HIDDEN"), // Show only those not visible
	VISIBLE("VISIBLE"); // Shows types visible to the GUI/Export as appropriate

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
