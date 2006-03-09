package pcgen.persistence.lst;

import pcgen.core.kit.KitGear;

public interface KitGearLstToken extends LstToken
{
	public abstract boolean parse(KitGear kitGear, String value);
}
