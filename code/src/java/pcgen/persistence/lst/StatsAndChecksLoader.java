/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
 *
 */
package pcgen.persistence.lst;

import java.net.URI;

import pcgen.cdom.base.Loadable;
import pcgen.cdom.content.BonusSpellInfo;
import pcgen.core.PCAlignment;
import pcgen.core.PCCheck;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * This class is a LstFileLoader that processes the statsandchecks.lst file
 */
public class StatsAndChecksLoader extends SimpleLoader<Loadable>
{

	public StatsAndChecksLoader()
	{
		super(Loadable.class);
	}

	@Override
	protected Loadable getLoadable(LoadContext context, String token, URI sourceURI)
	{
		final int colonLoc = token.indexOf(':');
		if (colonLoc == -1)
		{
			Logging.errorPrint("Invalid Token - does not contain a colon: '" + token + "' in " + sourceURI);
			return null;
		}
		else if (colonLoc == 0)
		{
			Logging.errorPrint("Invalid Token - starts with a colon: '" + token + "' in " + sourceURI);
			return null;
		}
		else if (colonLoc == (token.length() - 1))
		{
			Logging.errorPrint("Invalid Token - " + "ends with a colon (no value): '" + token + "' in " + sourceURI);
			return null;
		}
		String key = token.substring(0, colonLoc);
		Class<? extends Loadable> loadClass;
        switch (key)
        {
            case "STATNAME":
                Logging.deprecationPrint(
                        "Loading Stats in Game Mode is deprecated, " + "please use the STAT: token in a PCC file");
                loadClass = PCStat.class;
                break;
            case "CHECKNAME":
                Logging.deprecationPrint(
                        "Loading Checks/Saves in Game Mode is deprecated, " + "please use the SAVE: token in a PCC file");
                loadClass = PCCheck.class;
                break;
            case "BONUSSPELLLEVEL":
                loadClass = BonusSpellInfo.class;
                break;
            case "ALIGNMENTNAME":
                Logging.deprecationPrint(
                        "Loading Alignments in Game Mode is deprecated, " + "please use the ALIGNMENT: token in a PCC file");
                loadClass = PCAlignment.class;
                break;
            default:
                Logging.errorPrint("Invalid Token '" + key + "' as the first key in " + sourceURI);
                return null;
        }
		String name = token.substring(colonLoc + 1);
		if (name.isEmpty())
		{
			Logging.errorPrint("Invalid Token '" + key + "' had no value in " + sourceURI);
			return null;
		}
		Loadable loadable = context.getReferenceContext().constructCDOMObject(loadClass, name.intern());
		loadable.setSourceURI(sourceURI);
		return loadable;
	}

}
