/*
 * StatsAndChecksLoader.java
 * Copyright 2003 (C) David Hibbs <sage_sam@users.sourceforge.net>
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
 * Created on October 13, 2003, 11:50 AM
 *
 * Current Ver: $Revision$ <br>
 * Last Editor: $Author$ <br>
 * Last Edited: $Date$
 */
package pcgen.persistence.lst;

import pcgen.core.PCStat;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * This class is a LstFileLoader that processes the statsandchecks.lst file,
 * handing its multiple types of content off to the appropriate loader
 * for Attributes, Bonus Spells, Checks, and Alignments.
 *
 * @author AD9C15
 */
public class StatsAndChecksLoader extends LstLineFileLoader
{
	/**
	 * StatsAndChecksLoader Constructor.
	 *
	 */
	public StatsAndChecksLoader()
	{
		super();
	}

	/**
	 * @param fileName
	 * @throws PersistenceLayerException
	 * @see pcgen.persistence.lst.LstFileLoader#loadLstFiles(java.util.List)
	 */
	public void loadLstFile(String fileName) throws PersistenceLayerException
	{
		// Clear relevant Globals
		SettingsHandler.getGame().setAttribLong(null);
		SettingsHandler.getGame().setAttribShort(null);
		SettingsHandler.getGame().clearCheckList();
		SettingsHandler.getGame().clearAlignmentList();
		SettingsHandler.getGame().clearStatList();

		super.loadLstFile(fileName);

		// Reinit relevant globals from SystemCollections
		List statList = SettingsHandler.getGame().getUnmodifiableStatList();
		int statCount = statList.size();
		SettingsHandler.getGame().setAttribLong(new String[statCount]);
		SettingsHandler.getGame().setAttribShort(new String[statCount]);

		for (int i = 0; i < statCount; i++)
		{
			PCStat stat = (PCStat) statList.get(i);
			SettingsHandler.getGame().setAttribLong(i, stat.getDisplayName());
			SettingsHandler.getGame().setAttribShort(i, stat.getAbb());
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
	{
		final int idxColon = lstLine.indexOf(':');
		if (idxColon < 0)
		{
			return;
		}

		final String key = lstLine.substring(0, idxColon);
		Map tokenMap = TokenStore.inst().getTokenMap(StatsAndChecksLstToken.class);
		StatsAndChecksLstToken token = (StatsAndChecksLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, key, sourceURL.toString(), lstLine);
			if (!token.parse(lstLine, sourceURL))
			{
				Logging.errorPrint("Error parsing StatsAndChecks object: " + lstLine + '/' + sourceURL.toString());
			}
		}
		else
		{
			Logging.errorPrint("Illegal StatsAndChecks object: " + lstLine + '/' + sourceURL.toString());
		}
	}
}
