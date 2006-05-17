package pcgen.persistence.lst;

import pcgen.core.PObject;
import pcgen.util.Logging;

/**
 * Utility class to assist with LST files
 */
public class LstUtils {

	/**
	 * Checks a LST token to see if it's deprecated
	 * @param token
	 * @param obj
	 * @param value
	 */
	public static void deprecationCheck(LstToken token, PObject obj, String value) {
		if(token instanceof Deprecated) {
			StringBuffer sb = new StringBuffer();
			sb.append(token.getTokenName());
			sb.append(" deprecated. Tag was ");
			sb.append(value);
			sb.append(" in ");
			sb.append(obj.getDisplayName());
			sb.append(" of ");
			sb.append(obj.getSource());
			sb.append(". ");
			sb.append(((Deprecated)token).getMessage(obj, value));
			deprecationWarning(sb.toString());
		}
	}

	/**
	 * Checks to see if a LST Token is deprecated
	 * @param token
	 * @param name
	 * @param source
	 * @param value
	 */
	public static void deprecationCheck(LstToken token, String name, String source, String value) {
		if(token instanceof Deprecated) {
			StringBuffer sb = new StringBuffer();
			sb.append(token.getTokenName());
			sb.append(" deprecated. Tag was ");
			sb.append(value);
			sb.append(" in ");
			sb.append(name);
			sb.append(" of ");
			sb.append(source);
			sb.append(". ");
			sb.append(((Deprecated)token).getMessage(null, value));
			deprecationWarning(sb.toString());
		}
	}

	/**
	 * Log the deprecation warning
	 * @param warning
	 */
	public static void deprecationWarning(String warning) {
		Logging.errorPrint(warning);
	}
}
