/*
 * BonusStackLoader.java
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
 * Created on September 22, 2003, 11:29 AM
 *
 * Current Ver: $Revision: 1.17 $ <br>
 * Last Editor: $Author: soulcatcher $ <br>
 * Last Edited: $Date: 2006/02/21 01:55:28 $
 */
package pcgen.persistence.lst;

import java.net.URL;

import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

/**
 * This class is an LstFileLoader that handles loading of bonus-stacking
 * data.
 *
 * <p>
 * Current Ver: $Revision: 1.17 $ <br>
 * Last Editor: $Author: soulcatcher $ <br>
 * Last Edited: $Date: 2006/02/21 01:55:28 $
 *
 * @deprecated Now processed in miscinfo.lst
 * To be removed in 5.11.1
 * @author ad9c15
 */
public class BonusStackLoader extends LstLineFileLoader
{
	/**
	 * Constructor for BonusStackLoader.
	 */
	public BonusStackLoader()
	{
		super();
	}

	/**
	 * Load the LST file
	 * @param fileName
	 * @param gameModeIn
	 * @throws PersistenceLayerException
	 */
	public void loadLstFile(String fileName, String gameModeIn) throws PersistenceLayerException
	{
		//SystemCollections.getGameModeNamed(gameModeIn).clearBonusStacksList();
		super.loadLstFile(fileName, gameModeIn);
		Logging.errorPrint("Warning: bonusstacks.lst deprecated. use BONUSSTACKS in miscinfo.lst instead (GameMode: " + gameModeIn + ")");
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
	{
		SystemCollections.getGameModeNamed(getGameMode()).addToBonusStackList(lstLine.toUpperCase());
	}
}
