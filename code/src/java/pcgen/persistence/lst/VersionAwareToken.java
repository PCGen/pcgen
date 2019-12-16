/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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

import pcgen.util.Logging;

/**
 * VersionAwareToken is a token class that needs to verify a version.
 * 
 */
public abstract class VersionAwareToken
{

	public abstract String getTokenName();

	/**
	 * Check the supplied version number is in a valid format for a PCGen version number.
	 * @param version The version number to be checked.
	 * @return true if the version is valid.
	 */
	protected boolean validateVersionNumber(String version)
	{
		// extract the tokens from the version line
		String[] tokens = version.split(" |\\.|\\-", 4); //$NON-NLS-1$

		if (tokens.length < 3)
		{
			Logging.log(Logging.LST_ERROR, "Invalid version in " + getTokenName() + ':' + version
				+ " must have 3 number parts e.g. " + getTokenName() + ":6.0.2");
			return false;
		}

		for (int idx = 0;idx < 3;idx++)
		{
			try
			{
				Integer.parseInt(tokens[idx]);
			}
			catch (NumberFormatException e)
			{
				if (idx == 2 && (tokens[idx].startsWith("RC")))
				{
					// Ignore we are not concerned about Release candidates
				}
				else
				{
					Logging.log(Logging.LST_ERROR, "Invalid version in " + getTokenName() + ':' + version
						+ " must have 3 number parts e.g. " + getTokenName() + ":6.0.2");
					return false;
				}
			}
		}
		return true;
	}

}
