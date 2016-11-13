/*
 * StartStatMinToken.java
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 12, 2005, 8:10 PM
 *
 * Current Ver: $Revision: 1.1 $
 *
 */
package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

import java.net.URI;


/**
 * {@code StartStatMinToken}
 *
 * @author  Greg Bingleman &lt;byngl@hotmail.com&gt;
 */
public class StartStatMinToken implements GameModeLstToken
{

    @Override
	public String getTokenName()
	{
		return "STARTSTATMIN";
	}




    @Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		try
		{
			gameMode.setStatMin(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException exc)
		{
			return false;
		}
	}
}
