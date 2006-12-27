package pcgen.persistence.lst;

import pcgen.core.Kit;

/**
 * Interface to deal with Kit Startpack LST tokens
 */
public interface KitStartpackLstToken extends LstToken
{
	/**
	 * Parse token
	 * @param kit
	 * @param value
	 * @return true if OK
	 */
	public abstract boolean parse(Kit kit, String value);
}
