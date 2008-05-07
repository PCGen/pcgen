/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.Movement;
import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

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
		StringTokenizer moves = new StringTokenizer(value, Constants.COMMA);
		Movement cm;

		if (moves.countTokens() == 3)
		{
			cm = new Movement(2);
			cm.assignMovement(0, moves.nextToken(), "0");
			cm.assignMovement(1, moves.nextToken(), moves.nextToken());
		}
		else
		{
			Logging.errorPrint("Deprecated Version of MOVECLONE detected: "
				+ value + "\n  MOVECLONE now has 3 arguments: "
				+ "SourceMove,DestinationMove,Modifier");
			cm = Movement.getMovementFrom(value);
		}
		cm.setMoveRatesFlag(2);
		obj.setMovement(cm, anInt);
		return true;
	}
}
