/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens.deprecated;

import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.PropertyFactory;

/**
 * @author djones4
 *
 */
public class WeaponautoLst implements DeprecatedToken, GlobalLstToken {

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName() 
	{
		return "WEAPONAUTO"; //$NON-NLS-1$
	}

	/**
	 * @see pcgen.persistence.lst.GlobalLstToken#parse(pcgen.core.PObject, java.lang.String, int)
	 */
	public boolean parse(PObject obj, String value, int anInt) 
	{
		obj.addAutoArray("WEAPONPROF|" + value); //$NON-NLS-1$
//		obj.addWeaponProfAutos(value);
		return true;
	}

	/**
	 * @see pcgen.persistence.lst.DeprecatedToken#getMessage(pcgen.core.PObject, java.lang.String)
	 */
	public String getMessage(@SuppressWarnings("unused")final PObject anObj, 
							 @SuppressWarnings("unused")final String anValue)
	{
		return PropertyFactory.getString("Warnings.LstTokens.Deprecated.WeaponAutoToken"); //$NON-NLS-1$
	}
}

