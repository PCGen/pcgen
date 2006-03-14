package pcgen.persistence.lst;

import pcgen.core.kit.BaseKit;

/**
 * Interface for BaseKit Lst token
 * @author martinv
 */
public interface BaseKitLstToken extends LstToken {

	/**
	 * Parse the Base Kit Token
	 * @param baseKit
	 * @param value
	 * @return true if parse OK
	 */
	public abstract boolean parse(BaseKit baseKit, String value);

}
