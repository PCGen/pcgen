package pcgen.persistence.lst;

import java.net.URI;

import pcgen.core.Campaign;

/**
 * Interface for Install LST tokens
 */
public interface InstallLstToken extends LstToken
{
	/**
	 * Parses an Campaign object
	 * @param campaign The campaignbeing loaded
	 * @param value The value of the token
	 * @param sourceURI The source that contained the token
	 * @return true if parse OK
	 */
	public abstract boolean parse(Campaign campaign, String value, URI sourceURI);

}
