package pcgen.persistence.lst;

import pcgen.core.kit.KitProf;

/**
 * Interface for dealing with Kit Proficiency tokens
 */
public interface KitProfLstToken extends LstToken
{
	/**
	 * Parse the token
	 * @param kitProf
	 * @param value
	 * @return true if OK
	 */
	public abstract boolean parse(KitProf kitProf, String value);
}
