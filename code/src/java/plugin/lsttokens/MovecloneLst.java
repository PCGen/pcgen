/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.Movement;
import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 *
 */
public class MovecloneLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "MOVECLONE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		Movement cm = Movement.getMovementFrom(value);
		cm.setMoveRatesFlag(2);
		obj.setMovement(cm, anInt);
		return true;
	}
}
