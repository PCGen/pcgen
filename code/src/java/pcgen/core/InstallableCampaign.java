/*
 * InstallableCampaign.java
 * Copyright 2007 (C) James Dempsey
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
 * Created on 27/12/2007
 *
 * $Id$
 */

package pcgen.core;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;

/**
 * <code>InstallableCampaign</code> is a campaign (or data set) that can be 
 * installed into the PCGen instance.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class InstallableCampaign extends Campaign
{
	/**
	 * <code>Destination</code> is the full collection of possible 
	 * install destinations.
	 */
	public enum Destination
	{
		DATA("DATA"), 
		VENDORDATA("VENDORDATA");

		private final String text;
		
		Destination(String s)
		{
			text = s;
		}

		@Override
		public String toString()
		{
			return text;
		}
	}
	private Destination dest;
	
	/**
	 * Instantiates a new installable campaign.
	 */
	public InstallableCampaign()
	{
		super();
	}

	/**
	 * @return the dest
	 */
	public Destination getDest()
	{
		return dest;
	}

	/**
	 * @param dest the dest to set
	 */
	public void setDest(Destination dest)
	{
		this.dest = dest;
	}

	/**
	 * @return the minVer
	 */
	public String getMinVer()
	{
		String ver = stringChar.get(StringKey.MINVER);
		return ver == null ? Constants.EMPTY_STRING : ver;
	}

	/**
	 * @param minVer the minVer to set
	 */
	public void setMinVer(String minVer)
	{
		stringChar.put(StringKey.MINVER, minVer);
	}

	/**
	 * @return the minDevVer
	 */
	public String getMinDevVer()
	{
		String ver = stringChar.get(StringKey.MINDEVVER);
		return ver == null ? Constants.EMPTY_STRING : ver;
	}

	/**
	 * @param minDevVer the minDevVer to set
	 */
	public void setMinDevVer(String minDevVer)
	{
		stringChar.put(StringKey.MINDEVVER, minDevVer);
	}

}
