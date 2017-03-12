/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
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
		final List<String> ret = new ArrayList<>();
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
