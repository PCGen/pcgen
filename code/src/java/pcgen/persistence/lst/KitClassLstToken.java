package pcgen.persistence.lst;

import pcgen.core.kit.KitClass;

/**
 * Interface for dealing with different types of Kit CLASS tokens
 */
public interface KitClassLstToken extends LstToken
{
	/**
	 * Parse the token
	 * @param kitClass
	 * @param value
	 * @return true if OK
	 */
	public abstract boolean parse(KitClass kitClass, String value);
}
