package pcgen.persistence.lst;

import java.util.Map;

/**
 * Interface for SOURCE LST tokens
 */
public interface SourceLstToken extends LstToken
{

	/**
	 * Parse the SOURCE token
	 * @param sourceMap
	 * @param value
	 * @return true if parse OK
	 */
	public boolean parse(Map<String, String> sourceMap, String value);

}
