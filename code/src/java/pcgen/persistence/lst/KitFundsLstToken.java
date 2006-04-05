package pcgen.persistence.lst;

import pcgen.core.kit.KitFunds;

/**
 * Interface for the KitFunds Lst Token
 */
public interface KitFundsLstToken extends LstToken
{
	/**
	 * Parse
	 * @param kitFunds
	 * @param value
	 * @return true if OK
	 */
	public abstract boolean parse(KitFunds kitFunds, String value);
}
