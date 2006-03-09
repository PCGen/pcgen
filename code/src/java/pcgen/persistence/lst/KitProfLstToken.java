package pcgen.persistence.lst;

import pcgen.core.kit.KitProf;

public interface KitProfLstToken extends LstToken
{
	public abstract boolean parse(KitProf kitProf, String value);
}
