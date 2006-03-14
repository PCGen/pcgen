/*
 * RuleCheckLoader.java
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
 * Created on November 22, 2003, 11:59 AM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.core.RuleCheck;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision$
 *
 **/
final class RuleCheckLoader
{
	/**
	 * RuleCheckLoader Constructor.
	 **/
	private RuleCheckLoader()
	{
	    // TODO: Exception needs to be handled
	}

	/**
	 * Parse each line of rules.lst and populate the current gameMode
	 * @param gameMode
	 * @param aLine
	 **/
	public static void parseLine(GameMode gameMode, String aLine)
	{
		RuleCheck rule = new RuleCheck();

		String inputLine = aLine.trim();
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		Map tokenMap = TokenStore.inst().getTokenMap(RuleCheckLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch(StringIndexOutOfBoundsException e) {
				// TODO Handle Exception
			}
			RuleCheckLstToken token = (RuleCheckLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, rule.getName(), "", value);
				if (!token.parse(rule, value))
				{
					Logging.errorPrint("Error parsing Rule Check " + rule.getName() + ':' + colString + "\"");
				}
			}
			else
			{
				Logging.errorPrint("Illegal Rule Check: " + inputLine);
			}
		}

		// Add the new rule to the current gameMode
		gameMode.addRule(rule);
	}
}
