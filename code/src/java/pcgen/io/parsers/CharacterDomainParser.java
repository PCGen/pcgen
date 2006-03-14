/*
 * CharacterDomainParser.java
 *
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 16-Jan-2004
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.io.parsers;

import pcgen.core.CharacterDomain;
import pcgen.util.Logging;

import java.util.StringTokenizer;

/**
 * @author wardc
 *
 */
public class CharacterDomainParser
{

	/**
	 * Set the source of the domain
	 * This method should NOT be called outside of file i/o routines!
	 * @param charDomain
	 * @param aSource the source to be set
	 * See getDomainSource() for details.
	 **/
	public void setDomainSource(CharacterDomain charDomain, String aSource)
	{
		final StringTokenizer aTok = new StringTokenizer(aSource, "|", false);

		if (aTok.countTokens() < 2)
		{
			Logging.errorPrint("Invalid Domain Source:" + aSource);

			return;
		}

		charDomain.setDomainType( aTok.nextToken().toUpperCase() );

		charDomain.setDomainName( aTok.nextToken().toUpperCase() );

		if (aTok.hasMoreTokens())
		{
			charDomain.setLevel( Integer.parseInt(aTok.nextToken()) );
		}

		//domainSource = aSource;
		//rebuildSource = false;
	}

}
