
package pcgen.persistence.lst;

import pcgen.core.kit.KitDeity;

/**
 * Interface for dealing with KIT DEITY tokens
 */
public interface KitDeityLstToken extends LstToken
{
	/**
	 * Parse
	 * @param kitDeity
	 * @param value
	 * @return true if OK
	 */
	public abstract boolean parse(KitDeity kitDeity, String value);
}
