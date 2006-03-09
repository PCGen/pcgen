package pcgen.persistence.lst;

import pcgen.core.kit.KitFunds;

public interface KitFundsLstToken extends LstToken
{
	public abstract boolean parse(KitFunds kitFunds, String value);
}
