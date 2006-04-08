/*
 * ChoiceInfo.java
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
 * Current Version: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date: 2006-03-22 22:52:02 +0000 (Wed, 22 Mar 2006) $
 * 
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core;

/**
 * This tiny little class implements a way to associate prerequisites with a
 * simple string.  This is a subclass of AbilityInfo so we can reuse some of
 * its code
 *
 * @author  Andrew Wilson <nuance@sourceforge.net>
 */
public class ChoiceInfo extends AbilityInfo
{
	
	/**
	 * Make a new object to hold info about a string and some associated
	 * prerequisites.
	 *
	 * @param  key    the Key of the Ability
	 * @param  delim  the Ability's category
	 */
	public ChoiceInfo(String key, char delim)
	{
		super();
		
		this.category = "NONE";
		
		if (delim == '[') {
			this.delim = '[';
		}

		this.extractPrereqs(key);
	}

	public final Ability getAbility()
	{
		return null;
	}

	/**
	 * Compares this AbilityInfo Object with an Object passed in.  The object
	 * passed in should be either an AbilityInfo Object or a PObject.
	 *
	 * @param   obj  the object to test against
	 *
	 * @return  the result of the compare, negative integer if this should sort
	 *          before
	 */
	public int compareTo(Object obj)
	{
		return this.keyName.compareTo(obj.toString());
	}

}
