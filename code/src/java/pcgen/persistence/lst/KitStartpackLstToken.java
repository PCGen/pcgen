package pcgen.persistence.lst;

import pcgen.core.Kit;

public interface KitStartpackLstToken extends LstToken
{
	public abstract boolean parse(Kit kit, String value);
}

