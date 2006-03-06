package pcgen.persistence.lst;

import pcgen.core.kit.BaseKit;

public interface BaseKitLstToken extends LstToken {
	public abstract boolean parse(BaseKit baseKit, String value);

}
