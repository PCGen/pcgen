/**
 * pcgen.core.term.EQRangeTermEvaluator.java
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
 * Created 03-Oct-2008 02:57:13
 *
 *
 */

package pcgen.core.term;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

public class EQRangeTermEvaluator extends BaseEQTermEvaluator implements TermEvaluator
{
	public EQRangeTermEvaluator(String expressionString)
	{
		this.originalText = expressionString;
	}

	@Override
	public Float resolve(
			Equipment eq,
			boolean primary,
			PlayerCharacter pc)
	{
		return convertToFloat(originalText, evaluate(eq, primary, pc));
	}

	@Override
	public String evaluate(
			Equipment eq,
			boolean primary,
			PlayerCharacter pc) {
		if (ControlUtilities.hasControlToken(Globals.getContext(),
			CControl.EQRANGE))
		{
			Logging.errorPrint("RANGE term is deprecated (does not function)"
				+ " when RANGE CodeControl is used");
		}
		return String.valueOf(eq.getSafe(IntegerKey.RANGE));
	}

	@Override
	public boolean isSourceDependant()
	{
		return true;
	}

	public boolean isStatic()
	{
		return false;
	}
}
