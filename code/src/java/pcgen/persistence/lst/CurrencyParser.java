/*
 * CurrencyParser.java
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
 * Created on Oct 10, 2003, 12:30 PM
 *
 * Current Ver: $Revision: 1.7 $ <br>
 * Last Editor: $Author: binkley $ <br>
 * Last Edited: $Date: 2005/10/18 20:23:53 $
 */
package pcgen.persistence.lst;

import pcgen.core.money.DenominationList;
import pcgen.core.money.Denominations;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;

import java.math.BigDecimal;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * This class is a LstFileLoader for parsing currencies.
 *
 * <p>
 * Current Ver: $Revision: 1.7 $ <br>
 * Last Editor: $Author: binkley $ <br>
 * Last Edited: $Date: 2005/10/18 20:23:53 $
 *
 * @author AD9C15
 */
public class CurrencyParser extends LstLineFileLoader
{
	/**
	 * CurrencyParser Constructor.
	 *
	 */
	public CurrencyParser()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
		throws PersistenceLayerException
	{
		DenominationList denominationList = DenominationList.getInstance();

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		String colString;

		String region = "Global";
		String name = "";
		String abbr = "";
		int factor = 0;
		float weight = 0f;
		BigDecimal bdWeight;
		boolean isDefault = false;

		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken();

			if (colString.startsWith("COIN"))
			{
				colString = colString.replace('(', ' ');
				colString = colString.replace(')', ' ');
				colString = colString.substring(4);
				colString = colString.trim();
			}
			else if (colString.startsWith("COST:"))
			{
				String costString = colString.substring(5);

				try
				{
					factor = Integer.parseInt(costString);
				}
				catch (NumberFormatException e)
				{
					throw new PersistenceLayerException("Invalid Coin Cost '" + costString + "' in "
					    + sourceURL.toString());
				}
			}
			else if (colString.startsWith("ABBR:"))
			{
				abbr = colString.substring(5);
			}
			else if (colString.startsWith("WT:"))
			{
				bdWeight = new BigDecimal(colString.substring(3));
				weight = bdWeight.floatValue();
			}
			else if ("DEFAULT".equalsIgnoreCase(colString))
			{
				isDefault = true;
			}
		}

		if (!(("".equals(name) && "".equals(abbr)) || (factor == 0)))
		{
			if ("".equals(name))
			{
				name = abbr;
			}
			else if ("".equals(abbr))
			{
				abbr = name;
			}

			Denominations d = denominationList.getRegionalDenominations(region);

			if (d == null)
			{
				d = new Denominations();
				d.setRegion(region);
				denominationList.add(d);
			}

			d.addDenomination(name, abbr, factor, weight, isDefault);
		}
	}
}
