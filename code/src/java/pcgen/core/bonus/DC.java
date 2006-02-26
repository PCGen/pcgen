/*
 * DC.java
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
 * Current Ver: $Revision: 1.11 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/11/13 20:01:22 $
 *
 */
package pcgen.core.bonus;


/**
 * <code>DC</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
final class DC extends BonusObj
{
	private static final String[] bonusHandled =
		{
			"DC"
		};

	private static final String[] bonusTags = { "FEATBONUS", "ALLSPELLS" };

	/*
	 * is either a bonusTag[]
	 * or:
	 *    CLASS.<ClassName>
	 *    DESCRIPTOR.<Descrpt>
	 *    DOMAIN.<DomainName>
	 *    SCHOOL.<SchoolName>
	 *    SUBSCHOOL.<SubSchoolName>
	 *    TYPE.<CasterType>
	 *    SPELL.<SpellName>
	 */
	boolean parseToken(final String token)
	{
		for (int i = 0; i < bonusTags.length; ++i)
		{
			if (token.equals(bonusTags[i]))
			{
				addBonusInfo(new Integer(i));

				return true;
			}
		}

		// Must remove the %LIST if present

		/*
		   if (token.indexOf(".%LIST") > 0)
		   {
		       token = token.substring(0, token.indexOf(".%LIST"));
		   }
		 */
		if (token.startsWith("CLASS") || token.startsWith("DESCRIPTOR") || token.startsWith("DOMAIN")
			|| token.startsWith("SCHOOL") || token.startsWith("SUBSCHOOL") || token.startsWith("TYPE")
			|| token.startsWith("SPELL"))
		{
			addBonusInfo(token);

			return true;
		}

		return false;
	}

	String unparseToken(final Object obj)
	{
		if (obj instanceof Integer)
		{
			return bonusTags[((Integer) obj).intValue()];
		}

		return (String) obj;
	}

	String[] getBonusesHandled()
	{
		return bonusHandled;
	}
}
