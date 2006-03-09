package pcgen.persistence.lst;

import pcgen.core.kit.KitClass;

public interface KitClassLstToken extends LstToken
{
	public abstract boolean parse(KitClass kitClass, String value);
}
