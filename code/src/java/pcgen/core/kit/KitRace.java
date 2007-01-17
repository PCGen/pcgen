/*
 * KitRace.java
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

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Kit;
import pcgen.core.SettingsHandler;

/**
 * Deals with apply RACE via a KIT
 */
public class KitRace extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private String raceStr = null;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient Race theRace = null;

	/**
	 * Constructor
	 * @param aRace
	 */
	public KitRace(final String aRace)
	{
		raceStr = aRace;
	}

	/**
	 * Actually applies the race to this PC.
	 *
	 * @param aPC The PlayerCharacter the alignment is applied to
	 */
	public void apply(PlayerCharacter aPC)
	{
		setPCRace(aPC);
	}

	/**
	 * testApply
	 *
	 * @param aPC PlayerCharacter
	 * @param aKit Kit
	 * @param warnings List
	 */
	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		theRace = null;

		if (Constants.s_NONESELECTED.equals(raceStr))
		{
			return false;
		}
		theRace = Globals.getRaceKeyed(raceStr);

		if (theRace == null)
		{
			warnings.add("RACE: Race " + raceStr + " not found.");
			return false;
		}
		setPCRace(aPC);

		return true;
	}

	@Override
	public KitRace clone()
	{
		return (KitRace) super.clone();
	}

	public String getObjectName()
	{
		return "Race";
	}

	@Override
	public String toString()
	{
		return raceStr;
	}

	private void setPCRace(PlayerCharacter aPC)
	{
		// We want to level up as quietly as possible for kits.
		boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
		SettingsHandler.setShowHPDialogAtLevelUp(false);
		boolean tempFeatDlg = SettingsHandler.getShowFeatDialogAtLevelUp();
		SettingsHandler.setShowFeatDialogAtLevelUp(false);

		aPC.setRace(theRace);

		SettingsHandler.setShowFeatDialogAtLevelUp(tempFeatDlg);
		SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
	}
}
