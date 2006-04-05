/*
 * KitClass.java
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
 * Created on August 20, 2005, 1640h
 *
 * $Id$
 */
package pcgen.core.kit;

import java.io.Serializable;
import java.util.List;

import pcgen.core.*;

/**
 * <code>KitClass</code> <strong>needs documentation</strong>.
 *
 * @author boomer70
 * @version $Id$
 */
public class KitClass extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long  serialVersionUID = 1;

	private String theClassName = null;
	private String theSubClass = null;
	private String theLevelString = "";

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient PCClass theClass = null;
	private transient int theLevel = -1;
	private transient boolean doLevelAbilities = true;

	/**
	 * Constructor
	 * @param aClassName
	 */
	public KitClass(final String aClassName)
	{
		theClassName = aClassName;
	}

	/**
	 * Get the class name
	 * @return class name
	 */
	public String getClassName()
	{
		return theClassName;
	}

	/**
	 * Set the level
	 * @param aLevelStr
	 */
	public void setLevel(final String aLevelStr)
	{
		theLevelString = aLevelStr;
	}

	/**
	 * Get the level string
	 * @return level string
	 */
	public String getLevelString()
	{
		return theLevelString;
	}

	/**
	 * Set the sub class
	 * @param aClassName
	 */
	public void setSubClass(final String aClassName)
	{
		theSubClass = aClassName;
	}

	/**
	 * Get the sub class
	 * @return sub class
	 */
	public String getSubClass()
	{
		return theSubClass;
	}

	public String toString()
	{
		StringBuffer ret = new StringBuffer(100);
		ret.append(theClassName).append(theLevelString);
		return ret.toString();
	}

	public boolean testApply(Kit aKit, PlayerCharacter aPC, List warnings)
	{
		theClass = null;
		theClass = Globals.getClassNamed(getClassName());

		if (theClass == null)
		{
			warnings.add("CLASS: Class not found \"" + getClassName() + "\"");

			return false;
		}

		if (getSubClass() != null)
		{
			// try and set a subclass too.
			theClass.setSubClassName(getSubClass());
		}

		doLevelAbilities = aKit.doLevelAbilities();

		// Temporarily increase the PCs level.
		theLevel = aPC.getVariableValue(theLevelString, "").intValue();
		addLevel(aPC, theLevel, theClass, doLevelAbilities);

		return true;
	}

	public void apply(PlayerCharacter aPC)
	{
		addLevel(aPC, theLevel, theClass, doLevelAbilities);
	}

	public Object clone()
	{
		KitClass aClone  = (KitClass)super.clone();
		aClone.theClassName = theClassName;
		aClone.theSubClass = theSubClass;
		aClone.theLevelString = theLevelString;
		return aClone;
	}

	private void addLevel(final PlayerCharacter pc, final int numLevels, final PCClass aClass, final boolean doLevelAbilitiesIn)
	{
		// We want to level up as quietly as possible for kits.
		boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
		SettingsHandler.setShowHPDialogAtLevelUp(false);
//		boolean tempFeatDlg = SettingsHandler.getShowFeatDialogAtLevelUp();
//		SettingsHandler.setShowFeatDialogAtLevelUp(false);
		int tempChoicePref = SettingsHandler.getSingleChoicePreference();
		SettingsHandler.setSingleChoicePreference(Constants.CHOOSER_SINGLECHOICEMETHOD_SELECTEXIT);

		boolean tempDoLevelAbilities = pc.doLevelAbilities();
		pc.setDoLevelAbilities(doLevelAbilitiesIn);
		pc.incrementClassLevel(numLevels, aClass, true);
		pc.setDoLevelAbilities(tempDoLevelAbilities);

		SettingsHandler.setSingleChoicePreference(tempChoicePref);
//		SettingsHandler.setShowFeatDialogAtLevelUp(tempFeatDlg);
		SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
	}

	public String getObjectName()
	{
		return "Classes";
	}
}
