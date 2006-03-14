/*
 * PCLevelInfoStat.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on November 29, 2002, 10:365 PM
 *
 * $Id$
 */
package pcgen.core.pclevelinfo;

import java.io.Serializable;

/**
 * ???
 *
 * @author byngl
 * @version $Revision$
 */
public final class PCLevelInfoStat implements Serializable
{
	private String statAbb = "";
	private int mod = 0;

	PCLevelInfoStat(final String argStatAbb, final int argMod)
	{
		super();
		statAbb = argStatAbb;
		mod = argMod;
	}

	public String toString()
	{
		return statAbb + "=" + Integer.toString(mod);
	}

	public String getStatAbb()
	{
		return statAbb;
	}

	public int getStatMod()
	{
		return mod;
	}

	void modifyStat(final int argMod)
	{
		mod += argMod;
	}
}
