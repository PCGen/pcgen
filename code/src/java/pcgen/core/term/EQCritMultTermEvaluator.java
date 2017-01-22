/**
 * pcgen.core.term.EQCritMultTermEvaluator.java
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created 03-Oct-2008 02:14:17
 *
 *
 */

package pcgen.core.term;

import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.io.exporttoken.EqToken;
import pcgen.util.Logging;

public class EQCritMultTermEvaluator extends BaseEQTermEvaluator implements TermEvaluator
{
	public EQCritMultTermEvaluator(String expressionString)
	{
		this.originalText = expressionString;
	}

	@Override
	public Float resolve(
			Equipment eq,
			boolean primary,
			PlayerCharacter pc)
	{
		if (ControlUtilities.hasControlToken(Globals.getContext(),
			CControl.CRITMULT))
		{
			Logging
				.errorPrint("CRITMULT term is disabled when CRITMULT control is used");
		}
		if (primary)
		{
			return (float) eq.getCritMultiplier();
		}

		return (float) eq.getAltCritMultiplier();
	}

	@Override
	public String evaluate(
			Equipment eq,
			boolean primary,
			PlayerCharacter pc) {
		if (ControlUtilities.hasControlToken(Globals.getContext(),
			CControl.CRITMULT))
		{
			Logging
				.errorPrint("CRITMULT term is disabled when CRITMULT control is used");
		}
		if (primary)
		{
			return EqToken.multAsString(eq.getCritMultiplier());
		}

		return EqToken.multAsString(eq.getAltCritMultiplier());
	}
	
	@Override
	public boolean isSourceDependant()
	{
		return false;
	}

	public boolean isStatic()
	{
		return false;
	}
}
