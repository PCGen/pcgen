/*
 * Follower.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on July 10, 2002, 11:26 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core.character;

import pcgen.core.Constants;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;

/**
 * <code>Follower.java</code>
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision$
 **/
public final class Follower implements Comparable<Object>, Cloneable
{
	/*
	 *
	 * the Structure of each Follower is as follows:
	 *
	 * FOLLOWER:name:type:race:HD:/path/to/some.pcg
	 *
	 * String name = name of the follower
	 * String type = Familiars, Mounts, Followers
	 * String race = race of follower
	 * int HD = Number of "used" HD
	 * String fileName = path and file name
	 *
	 */
	private String fileName = "";
	private String name = "";
	private String race = "";
	private String type = "";
	private int usedHD;

	/**
	 * Constructor
	 * @param fName
	 * @param aName
	 * @param aType
	 */
	public Follower(final String fName, final String aName, final String aType)
	{
		fileName = fName;
		name = aName;
		type = aType;
	}

	/**
	 * Set file name
	 * @param x
	 */
	public void setFileName(final String x)
	{
		fileName = x;
	}

	/**
	 * Get file name
	 * @return file name
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * Set name
	 * @param x
	 */
	public void setName(final String x)
	{
		name = x;
	}

	/**
	 * Get name
	 * @return name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set race
	 * @param x
	 */
	public void setRace(final String x)
	{
		race = x;
	}

	/**
	 * Get race
	 * @return race
	 */
	public String getRace()
	{
		return race;
	}

	/** 
	 * Set relative path name
	 * 
	 * @param x - may be an absolute or relative path
	 */
	public void setRelativeFileName(final String x)
	{
		if (x.startsWith(SettingsHandler.getPcgPath().toString()))
		{
			fileName = x;
		}

		fileName = SettingsHandler.getPcgPath().toString() + x;
	}

	/**
	 * Get the file name relative to filename
	 * @return relative file name
	 */
	public String getRelativeFileName()
	{
		String result = fileName;

		if (fileName.startsWith(SettingsHandler.getPcgPath().toString()))
		{
			result = fileName.substring(SettingsHandler.getPcgPath().toString().length());
		}

		return result;
	}

	/**
	 * Set type
	 * @param x
	 */
	public void setType(final String x)
	{
		type = x;
	}

	/**
	 * Get type
	 * @return type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Set used HD
	 * @param x
	 */
	public void setUsedHD(final int x)
	{
		usedHD = x;
	}

	/**
	 * Get the HD used
	 * @return HD Used
	 */
	public int getUsedHD()
	{
		return usedHD;
	}

	public int compareTo(final Object obj)
	{
		final Follower aF = (Follower) obj;

		// Needs to be case-sensitive for filenames.
		return fileName.compareTo(aF.fileName);
	}

	public String toString()
	{
		return name;
	}

	public Object clone()
	{
		Follower aClone = null;
		try
		{
			aClone = (Follower)super.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}
		return aClone;
}
}
