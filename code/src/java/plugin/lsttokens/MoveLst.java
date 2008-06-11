/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.Equipment;
import pcgen.core.Movement;
import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class MoveLst implements GlobalLstToken
{
	/*
	 * FIXME Template's LevelToken needs adjustment before this can be converted
	 * to the new syntax, since this is level-dependent
	 */

	public String getTokenName()
	{
		return "MOVE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (obj instanceof Equipment)
		{
			return false;
		}
		StringTokenizer moves = new StringTokenizer(value, Constants.COMMA);
		Movement cm;

		if (moves.countTokens() == 1)
		{
			cm = new Movement(1);
			String mod = moves.nextToken();
			validateMove(value, mod);
			cm.assignMovement(0, "Walk", mod);
		}
		else
		{
			cm = new Movement(moves.countTokens() / 2);

			int x = 0;
			while (moves.countTokens() > 1)
			{
				String type = moves.nextToken();
				String mod = moves.nextToken();
				validateMove(value, mod);
				cm.assignMovement(x++, type, mod);
			}
			if (moves.countTokens() != 0)
			{
				Logging.errorPrint("Badly formed MOVE token "
					+ "(extra value at end of list): " + value);
			}
		}
		cm.setMoveRatesFlag(0);
		obj.setMovement(cm, anInt);
		return true;
	}

	private void validateMove(String value, String mod)
	{
		try
		{
			if (Integer.parseInt(mod) < 0)
			{
				Logging.errorPrint("Invalid movement (cannot be negative): "
					+ mod + " in MOVE: " + value);
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid movement (must be an integer >= 0): "
				+ mod + " in MOVE: " + value);
		}
	}
}
