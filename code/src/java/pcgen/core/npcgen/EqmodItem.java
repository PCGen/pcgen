package pcgen.core.npcgen;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.util.Logging;
import pcgen.util.PJEP;
import pcgen.util.PjepPool;

public class EqmodItem 
{
	private String theEqmod = Constants.EMPTY_STRING;
	private EqmodTable theLookupTable = null;
	private String theRollString = null;
	
	public List<String> getEqMods()
	{
		if ( theLookupTable != null )
		{
			return theLookupTable.getEqMod();
		}
		final List<String> ret = new ArrayList<String>();
		String eqMod = theEqmod;
		if ( theRollString != null )
		{
			final PJEP parser = PjepPool.getInstance().aquire(this, theRollString);
			parser.parseExpression(theRollString);
			if (parser.hasError())
			{
				Logging.errorPrint("Not a JEP expression: " + theRollString);
				return null;
			}
			eqMod = eqMod.replaceAll( "%ROLL", parser.getValueAsObject().toString());
			PjepPool.getInstance().release(parser);
		}
		ret.add(eqMod);
		return ret;
	}
	
	public void setEqmod( final String anEqmod )
	{
		theEqmod = anEqmod;
	}
	
	public void setLookup( final EqmodTable aTable )
	{
		theLookupTable = aTable;
	}
	
	public void setRollString( final String aRollString )
	{
		theRollString = "roll(\"" + aRollString + "\")";
	}
}
