/**
 * 
 */
package pcgen.io.filters;

/**
 * @author apsen
 *
 */
public interface OutputFilter
{
	/**
	 * Filter the supplied string according to the current output filter. This
	 * can do things such as escaping HTML entities.
	 *
	 * @param aString The string to be filtered
	 * @return The filtered string.
	 */
	String filterString(String aString);
}
