/*
 * EqmodToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.gear;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.helper.EqModRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.EquipmentModifier;
import pcgen.core.kit.KitGear;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * EQMOD Token for KitGear
 */
public class EqmodToken extends AbstractToken implements
		CDOMSecondaryToken<KitGear>
{
	private static final Class<EquipmentModifier> EQUIPMENT_MODIFIER_CLASS =
			EquipmentModifier.class;

	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "EQMOD";
	}

	public Class<KitGear> getTokenClass()
	{
		return KitGear.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, KitGear kitGear, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('.', value))
		{
			return false;
		}

		StringTokenizer dotTok = new StringTokenizer(value, Constants.DOT);

		while (dotTok.hasMoreTokens())
		{
			String aEqModName = dotTok.nextToken();

			if (aEqModName.equalsIgnoreCase(Constants.LST_NONE))
			{
				Logging.errorPrint("Embedded " + Constants.LST_NONE
					+ " is prohibited in " + getTokenName());
				return false;
			}
			if (hasIllegalSeparator('|', aEqModName))
			{
				return false;
			}

			StringTokenizer pipeTok =
					new StringTokenizer(aEqModName, Constants.PIPE);

			// The type of EqMod, eg: ABILITYPLUS
			final String eqModKey = pipeTok.nextToken();
			CDOMSingleRef<EquipmentModifier> eqMod =
					context.ref.getCDOMReference(EQUIPMENT_MODIFIER_CLASS,
						eqModKey);
			EqModRef modRef = new EqModRef(eqMod);

			while (pipeTok.hasMoreTokens())
			{
				String assocTok = pipeTok.nextToken();
				if (assocTok.indexOf(']') != -1)
				{
					if (assocTok.indexOf("[]") != -1)
					{
						Logging.errorPrint("Found empty assocation in "
							+ getTokenName() + ": " + value);
						return false;
					}
					StringTokenizer bracketTok =
							new StringTokenizer(assocTok, "]");
					while (bracketTok.hasMoreTokens())
					{
						String assoc = bracketTok.nextToken();
						int openBracketLoc = assoc.indexOf('[');
						if (openBracketLoc == -1)
						{
							Logging
								.errorPrint("Found close bracket without open bracket in assocation in "
									+ getTokenName() + ": " + value);
							return false;
						}
						if (openBracketLoc != assoc.lastIndexOf('['))
						{
							Logging
								.errorPrint("Found open bracket without close bracket in assocation in "
									+ getTokenName() + ": " + value);
							return false;
						}
					}
				}
				modRef.addChoice(assocTok);
			}
			kitGear.addModRef(modRef);
		}
		return true;
	}

	public String[] unparse(LoadContext context, KitGear kitGear)
	{
		if (!kitGear.hasEqMods())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (EqModRef modRef : kitGear.getEqMods())
		{
			String key = modRef.getRef().getLSTformat();
			StringBuilder sb = new StringBuilder();
			sb.append(key);
			for (String s : modRef.getChoices())
			{
				sb.append(Constants.PIPE).append(s);
			}
			set.add(sb.toString());
		}
		return new String[]{StringUtil.join(set, Constants.DOT)};
	}
}
