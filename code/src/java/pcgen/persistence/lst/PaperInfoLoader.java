/*
 * PaperInfoLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on February 27, 2002, 7:42 PM
 *
 * $Id: PaperInfoLoader.java,v 1.25 2005/12/23 09:34:13 jdempsey Exp $
 */
package pcgen.persistence.lst;

import pcgen.core.Globals;
import pcgen.core.PaperInfo;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

import java.net.URL;
import java.util.StringTokenizer;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.25 $
 */
final class PaperInfoLoader extends LstLineFileLoader
{
	/** Creates a new instance of PaperInfoLoader. */
	public PaperInfoLoader()
	{
	    // Empty Constructor
	}

	public void loadLstFile(String fileName) throws PersistenceLayerException
	{
		// We cannot clear the global list as we are only setting one 
		// game mode at a time now.
		//SystemCollections.clearPaperInfoList();
		super.loadLstFile(fileName);
		Globals.selectPaper(SettingsHandler.getPCGenOption("paperName", "A4"));
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
	{
		final PaperInfo psize = new PaperInfo();

		final StringTokenizer aTok = new StringTokenizer(lstLine, "\t");
		int iCount = 0;

		while (aTok.hasMoreElements())
		{
			final String colString = (String) aTok.nextElement();

			try
			{
				psize.setPaperInfo(iCount, colString);
			}
			catch (IndexOutOfBoundsException e)
			{
				Logging.errorPrint("Illegal paper size info '" + lstLine + "' in " + sourceURL.toString());
			}

			iCount += 1;
		}

		SystemCollections.addToPaperInfoList(psize, gameMode);
	}
}
