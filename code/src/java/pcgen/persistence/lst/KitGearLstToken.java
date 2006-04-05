package pcgen.persistence.lst;

import pcgen.core.kit.KitGear;

/**
 * Interface to deal with Kit Gear LST Tokens
 */
public interface KitGearLstToken extends LstToken
{
	/**
	 * parse
	 * @param kitGear
	 * @param value
	 * @return true if OK
	 */
	public abstract boolean parse(KitGear kitGear, String value);
}
