/*
 * KitKit.java
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on October 15, 2005, 10:00 PM
 *
 * $Id$
 */
package pcgen.core.kit;

import java.io.Serializable;
import java.util.List;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Kit;
import java.util.ArrayList;

/**
 * Applies the Kit
 */
public class KitKit extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private String kitStr = null;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient Kit theKit = null;
	private transient List<BaseKit> thingsToAdd = null;

	/**
	 * Constructor
	 * @param aKit
	 */
	public KitKit(final String aKit)
	{
		kitStr = aKit;
	}

	/**
	 * Actually applies the kit to this PC.
	 *
	 * @param aPC The PlayerCharacter the alignment is applied to
	 */
	public void apply(PlayerCharacter aPC)
	{
		theKit.processKit(aPC, thingsToAdd);
	}

	/**
	 * Test applying this kit to the character.
	 *
	 * @param aPC PlayerCharacter The character to apply the kit to.
	 * @param aKit Kit The kit that has requested the application of the kit.
	 * @param warnings List The warign list to be populated if anything fails.
	 */
	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		String key = kitStr;
		theKit = Globals.getKitKeyed(key);
		if (theKit == null)
		{
			warnings.add("KIT: Kit " + kitStr + " not found.");
			return false;
		}
		thingsToAdd = new ArrayList<BaseKit>();
		theKit.testApplyKit(aPC, thingsToAdd, warnings);
		// We actually want this kit to get applied to the temp pc
		theKit.processKit(aPC, thingsToAdd);
		return true;
	}

	@Override
	public KitKit clone()
	{
		return (KitKit) super.clone();
	}

	public String getObjectName()
	{
		return "Kit";
	}

	@Override
	public String toString()
	{
		return kitStr;
	}
}
