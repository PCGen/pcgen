/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens.deprecated;

import pcgen.core.Movement;
import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 * 
 */
public class MoveaLst implements GlobalLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "MOVEA";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		Movement cm = Movement.getMovementFrom(value);
		cm.setMoveRatesFlag(1);
		obj.setMovement(cm, anInt);
		return true;
	}

	public String getMessage(PObject obj, String value)
	{
		return getTokenName()
			+ " is deprecated.  Please use BONUS|MOVEADD or BONUS|POSTMOVEADD";
	}
}
