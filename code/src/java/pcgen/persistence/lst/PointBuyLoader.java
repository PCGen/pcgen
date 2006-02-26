/*
 * PointBuyLoader.java
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
 * Created on October 08, 2003, 12:00 PM
 *
 * Current Ver: $Revision: 1.13 $ <br>
 * Last Editor: $Author: binkley $ <br>
 * Last Edited: $Date: 2005/10/18 20:23:53 $
 */
package pcgen.persistence.lst;

import pcgen.core.*;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import java.net.URL;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * This class is a LstFileLoader used to load point-buy methods.
 *
 * <p>
 * Current Ver: $Revision: 1.13 $ <br>
 * Last Editor: $Author: binkley $ <br>
 * Last Edited: $Date: 2005/10/18 20:23:53 $
 *
 * @author ad9c15
 */
public class PointBuyLoader extends LstLineFileLoader
{
	/**
	 * Constructor for PointBuyLoader.
	 */
	public PointBuyLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
	{
		boolean bError;
		if (lstLine.startsWith("STAT:"))
		{
			bError = parseStatLine(lstLine.substring(5));
		}
		else if (lstLine.startsWith("METHOD:"))
		{
			bError = parseMethodLine(lstLine.substring(7));
		}
		else
		{
			bError = true;
		}

		if (bError)
		{
			Logging.errorPrint("Illegal point buy info '" + lstLine + "' in " + sourceURL.toString());
		}
	}

	private boolean parseStatLine(String lstLine)
	{
		final StringTokenizer pbTok = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		int statValue;
		String aTag = pbTok.nextToken();
		try
		{
			statValue = Integer.parseInt(aTag);
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("NumberFormatException in Point Buy Line:" + Constants.s_LINE_SEP, exc);
			return true;
		}


		PointBuyCost pbc = new PointBuyCost(statValue);

		while(pbTok.hasMoreTokens())
		{
			aTag = pbTok.nextToken();
			if (aTag.startsWith("COST:"))
			{
				try
				{
					final int cost = Integer.parseInt(aTag.substring(5));
					pbc.setStatCost(cost);
				}
				catch (NumberFormatException exc)
				{
					Logging.errorPrint("NumberFormatException in Point Buy Line:" + Constants.s_LINE_SEP, exc);
					return true;
				}
			}
			else if (aTag.startsWith("PRE") || aTag.startsWith("!PRE") || aTag.startsWith("RESTRICT:"))
			{
				if (aTag.toUpperCase().equals("PRE:.CLEAR"))
				{
					pbc.clearPreReq();
				}
				else
				{
					try
					{
						PreParserFactory factory = PreParserFactory.getInstance();
						pbc.addPreReq(factory.parse(aTag));
					}
					catch (PersistenceLayerException ple)
					{
						Logging.errorPrint("PersistenceLayerException in Point Buy Line:" + Constants.s_LINE_SEP, ple);
						return true;
					}
				}
			}
			else
			{
				return true;
			}
		}

		SystemCollections.getGameModeNamed(getGameMode()).addPointBuyStatCost(pbc);

		return false;
	}

	private boolean parseMethodLine(String lstLine)
	{
		final StringTokenizer pbTok = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		final PObject dummy = new PObject();
		PointBuyMethod pbm = new PointBuyMethod(pbTok.nextToken(), "0");
		boolean bError = false;

		while(pbTok.hasMoreTokens())
		{
			final String aTag = pbTok.nextToken();
			if (aTag.startsWith("POINTS:"))
			{
				pbm.setPointFormula(aTag.substring(7));
			}
			else if (aTag.startsWith("BONUS:"))
			{
				//
				// Parse into dummy PObject object
				//
				try
				{
					if (!PObjectLoader.parseTag(dummy, aTag))
					{
						bError = true;
					}
				}
				catch(PersistenceLayerException ple)
				{
					bError = true;
				}
			}
			else
			{
				bError = true;
			}
		}
		if (!bError)
		{
			//
			// Copy bonus list into PointBuyMethod object
			//
			for (Iterator ab = dummy.getBonusList().iterator(); ab.hasNext();)
			{
				final BonusObj aBonus = (BonusObj) ab.next();

//				aBonus.setCreatorObject(null);
				pbm.addBonusList(aBonus);
			}
			SystemCollections.getGameModeNamed(getGameMode()).addPurchaseModeMethod(pbm);
		}
		return bError;
	}
}
