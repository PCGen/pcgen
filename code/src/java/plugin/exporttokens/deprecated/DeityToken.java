/*
 * DeityToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens.deprecated;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Deity;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpecialAbility;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Logging;

/**
 * Deal with tokens:
 * DEITY (Defaults to OUTPUTNAME)
 * DEITY.NAME
 * DEITY.OUTPUTNAME
 * DEITY.DOMAINLIST
 * DEITY.ALIGNMENT
 * DEITY.APPEARANCE
 * DEITY.DESCRIPTION
 * DEITY.HOLYITEM
 * DEITY.FAVOREDWEAPON
 * DEITY.PANTHEONLIST
 * DEITY.SOURCE
 * DEITY.SA
 * DEITY.TITLE
 * DEITY.WORSHIPPERS
 */
public class DeityToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "DEITY";

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		String retString = "";

		CharacterDisplay display = pc.getDisplay();
		Deity deity = display.getDeity();
		if (deity != null)
		{
			StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
			String subTag = "OUTPUTNAME";

			if (aTok.countTokens() > 1)
			{
				aTok.nextToken();
				subTag = aTok.nextToken();
			}

			if ("NAME".equals(subTag))
			{
				if (!display.getSuppressBioField(BiographyField.DEITY))
				{
					retString = deity.getDisplayName();
				}
			}
			else if ("OUTPUTNAME".equals(subTag))
			{
				if (!display.getSuppressBioField(BiographyField.DEITY))
				{
					retString = OutputNameFormatting.getOutputName(deity);
				}
			}
			else if ("DOMAINLIST".equals(subTag))
			{
				retString = getDomainListToken(deity);
			}
			else if ("FOLLOWERALIGNMENT".equals(subTag))
			{
				Logging.errorPrint("Output Sheet uses DEITY.FOLLOWERALIGN: " + "Function has been removed from PCGen");
			}
			else if ("ALIGNMENT".equals(subTag))
			{
				CDOMSingleRef<PCAlignment> al = deity.get(ObjectKey.ALIGNMENT);
				retString = al == null ? "" : al.get().getKeyName();
			}
			else if ("APPEARANCE".equals(subTag))
			{
				FactKey<String> fk = FactKey.valueOf("Appearance");
				String str = deity.getResolved(fk);
				retString = (str == null) ? "" : str;
			}
			else if ("DESCRIPTION".equals(subTag))
			{
				retString = pc.getDescription(deity);
			}
			else if ("HOLYITEM".equals(subTag))
			{
				FactKey<String> fk = FactKey.valueOf("Symbol");
				String str = deity.getResolved(fk);
				retString = (str == null) ? "" : str;
			}
			else if ("FAVOREDWEAPON".equals(subTag))
			{
				List<CDOMReference<WeaponProf>> dwp = deity.getSafeListFor(ListKey.DEITYWEAPON);
				retString = ReferenceUtilities.joinLstFormat(dwp, Constants.PIPE, true);
			}
			else if ("PANTHEONLIST".equals(subTag))
			{
				FactSetKey<String> fk = FactSetKey.valueOf("Pantheon");
				Set<String> pset = new TreeSet<>();
				for (Indirect<String> indirect : deity.getSafeSetFor(fk))
				{
					pset.add(indirect.get());
				}
				retString = StringUtil.join(pset, ", ");
			}
			else if ("SOURCE".equals(subTag))
			{
				retString = SourceFormat.getFormattedString(deity, Globals.getSourceDisplay(), true);
			}
			else if ("SA".equals(subTag))
			{
				retString = getSAToken(deity, display);
			}
			else if ("TITLE".equals(subTag))
			{
				FactKey<String> fk = FactKey.valueOf("Title");
				String str = deity.getResolved(fk);
				retString = (str == null) ? "" : str;
			}
			else if ("WORSHIPPERS".equals(subTag))
			{
				FactKey<String> fk = FactKey.valueOf("Worshippers");
				String str = deity.getResolved(fk);
				retString = (str == null) ? "" : str;
			}
		}

		return retString;
	}

	/**
	 * Get domain list sub token
	 * @param deity
	 * @return domain list sub token
	 */
	public static String getDomainListToken(Deity deity)
	{
		return ReferenceUtilities.joinDisplayFormat(deity.getSafeListMods(Deity.DOMAINLIST), ", ");
	}

	/**
	 * Get the SA sub token
	 * @param deity
	 * @return the SA sub token
	 */
	public static String getSAToken(Deity deity, CharacterDisplay display)
	{
		final List<SpecialAbility> saList = new ArrayList<>();
		saList.addAll(display.getResolvedUserSpecialAbilities(deity));
		saList.addAll(display.getResolvedSpecialAbilities(deity));

		if (saList.isEmpty())
		{
			return Constants.EMPTY_STRING;
		}

		StringBuilder returnString = new StringBuilder();
		boolean firstLine = true;
		for (SpecialAbility sa : saList)
		{
			if (!firstLine)
			{
				returnString.append(", "); //$NON-NLS-1$
			}

			firstLine = false;

			returnString.append(sa.getDisplayName());
		}

		return returnString.toString();
	}
}
