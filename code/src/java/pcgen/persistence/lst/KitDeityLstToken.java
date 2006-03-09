
package pcgen.persistence.lst;

import pcgen.core.kit.KitDeity;

public interface KitDeityLstToken extends LstToken
{
	public abstract boolean parse(KitDeity kitDeity, String value);
}
