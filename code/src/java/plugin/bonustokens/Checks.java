/*
 * Checks.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 *
 * Created on December 13, 2002, 9:19 AM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.bonustokens;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Globals;
import pcgen.core.PCCheck;
import pcgen.core.bonus.BonusObj;

/**
 * <code>Checks</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public final class Checks extends BonusObj
{
	private static final String[] bonusHandled = {"CHECKS"};

	protected boolean parseToken(final String argToken)
	{
		boolean isBase = false;
		final String token;

		if (argToken.startsWith("BASE."))
		{
			token = argToken.substring(5);
			isBase = true;
		}
		else
		{
			token = argToken;
		}

		if ("%LIST".equals(token))
		{
			// Special case of:  BONUS:CHECKS|%LIST|x
			addBonusInfo(LIST_CHECK);
		}
		else if ("ALL".equals(token))
		{
			// Special case of:  BONUS:CHECKS|ALL|x
			/*
			 * TODO Prohibit use in Game Mode, or alternately, all areas where
			 * CHECKS are established need to test both CHECKS|Blah and
			 * CHECKS|ALL
			 */
			for (PCCheck check : Globals.getContext().ref
					.getConstructedCDOMObjects(PCCheck.class))
			{
				addBonusInfo(new CheckInfo(CDOMDirectSingleRef.getRef(check),
						isBase));
			}
		}
		else
		{
			CDOMReference<PCCheck> aCheck = Globals.getContext().ref
					.getCDOMReference(PCCheck.class, token);
			//Invalid name is caught by Unconstructed Reference system
			addBonusInfo(new CheckInfo(aCheck, isBase));
		}

		return true;
	}

	protected String unparseToken(final Object obj)
	{
		String token = "";

		if (obj.equals(LIST_CHECK))
		{
			return token + "%LIST";
		}
		else if (((CheckInfo) obj).isBase)
		{
			token = "BASE.";
		}

		return token + ((CheckInfo) obj).pobj.getLSTformat();
	}

	/**
	 * Deals with the CheckInfo
	 */
	public static class CheckInfo
	{
		/** The PObject */
		public final CDOMReference<PCCheck> pobj;
		/** whether this is a base check, True or False */
		public final boolean isBase;

		/**
		 * Constructor
		 * @param argPobj
		 * @param argIsBase
		 */
		public CheckInfo(final CDOMReference<PCCheck> argPobj, final boolean argIsBase)
		{
			pobj = argPobj;
			isBase = argIsBase;
		}
	}

	public static CheckInfo LIST_CHECK = new CheckInfo(null, false);

	protected String[] getBonusesHandled()
	{
		return bonusHandled;
	}
}
