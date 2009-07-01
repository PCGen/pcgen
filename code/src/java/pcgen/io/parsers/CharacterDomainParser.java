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

import java.util.StringTokenizer;

import pcgen.cdom.helper.ClassSource;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.util.Logging;

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
	public ClassSource getDomainSource(String aSource)
	{
		final StringTokenizer aTok = new StringTokenizer(aSource, "|", false);

		if (aTok.countTokens() < 2)
		{
			Logging.errorPrint("Invalid Domain Source:" + aSource);
			return null;
		}

		aTok.nextToken(); //Throw away "PCClass"

		String classString = aTok.nextToken();
		PCClass cl = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				PCClass.class, classString);
		if (cl == null)
		{
			Logging.errorPrint("Invalid Class in Domain Source:" + aSource);
			return null;
		}
		ClassSource cs;
		if (aTok.hasMoreTokens())
		{
			int level = Integer.parseInt(aTok.nextToken());
			cs = new ClassSource(cl, level);
		}
		else
		{
			cs = new ClassSource(cl);
		}
		return cs;
	}

}
