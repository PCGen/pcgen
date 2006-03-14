package pcgen.gui.utils.chooser;

/**
 * Holds a text/tooltip pair for chooser display.
 */
public final class TextAndToolTip
{
	/**
	 * The chooser entry display text.
	 */
	public final String text;
	/**
	 * The entry tooltip or the text if none.
	 */
	public final String toolTip;

	/**
	 * Constructs a new <code>TextAndToolTip</code> with the given <var>text</var>
	 * and <var>toolTip</var>.
	 * <p/>
	 * Uses the empty string for <var>text</var> if it is <code>null</code>.  Uses
	 * <var>text</var> for <var>toolTip</var> if <var>toolTip</var> is
	 * <code>null</code>.
	 *
	 * @param text the chooser entry display text
	 * @param toolTip the entry tooltip
	 */
	public TextAndToolTip(final String text, final String toolTip)
	{
		this.text = null == text ? "" : text;
		this.toolTip = null == toolTip ? text : toolTip;
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Returns the {@link #text} and ignores {@link #toolTip}.
	 *
	 * @return {@inheritDoc}
	 */
	public String toString()
	{
		return text;
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Compares only the {@link #text} between <code>TextAndToolTip</code>
	 * objects and ignores {@link #toolTip}.
	 *
	 * @param o {@inheritDoc}
	 *
	 * @return {@inheritDoc}
	 */
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		return text.equals(((pcgen.gui.utils.chooser.TextAndToolTip) o).text);
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Only hashes {@link #text} and ignores {@link #toolTip}.
	 *
	 * @return {@inheritDoc}
	 */
	public int hashCode()
	{
		return text.hashCode();
	}
}
