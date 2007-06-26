/*
 * SpecialAbility.java
 * Copyright 2004 (C) Devon Jones
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.core;

import java.util.StringTokenizer;

import pcgen.core.prereq.PrereqHandler;
import pcgen.util.Logging;

/**
 * <code>SpecialAbility</code>.
 * 
 * @author Devon Jones
 * @version $Revision$
 */
public final class SpecialAbility extends TextProperty
{
	protected String saSource = ""; //$NON-NLS-1$

	/** Default constructor */
	public SpecialAbility()
	{
		super();
	}

	/**
	 * Constructor - with name
	 * 
	 * @param name
	 */
	public SpecialAbility(final String name)
	{
		super(name);
	}

	/**
	 * Constructor, with name and source
	 * 
	 * @param name
	 * @param saSource
	 */
	public SpecialAbility(final String name, final String saSource)
	{
		super(name);
		this.saSource = saSource;
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The name of the Special Ability
	 * @param saSource
	 *            The source of the Special Ability
	 * @param propDesc
	 *            NEEDDOC
	 * 
	 */
	public SpecialAbility(final String name, final String saSource,
		final String propDesc)
	{
		super(name, propDesc);
		this.saSource = saSource;
	}

	/**
	 * Set the class that is used to determine if a character should have this
	 * special ability
	 * 
	 * @param oldClass
	 *            The name of the original class
	 * @param newClass
	 *            The name of the new class that is to be used for this special
	 *            ability
	 */
	public void setQualificationClass(final String oldClass,
		final String newClass)
	{
		if ("".equals(saSource)) //$NON-NLS-1$
		{
			return;
		}

		try
		{
			final StringTokenizer aTok =
					new StringTokenizer(saSource, "|=", false);
			final String typeString = aTok.nextToken();
			final String classKey = aTok.nextToken();
			final String levelString = aTok.nextToken();

			if (classKey.equals(oldClass))
			{
				Logging.errorPrint("Source class changed from " + oldClass //$NON-NLS-1$
					+ " to " + newClass + " for " + displayName); //$NON-NLS-1$ //$NON-NLS-2$

				setSASource(typeString + "=" + newClass + "|" + levelString);
			}
		}
		catch (Exception exc)
		{
			Logging.errorPrint("setQualificationClass:" + saSource, exc); //$NON-NLS-1$
		}
	}

	/**
	 * Set the description of the Special Ability
	 * 
	 * @param saDesc
	 */
	public void setSADesc(final String saDesc)
	{
		setPropDesc(saDesc);
	}

	/**
	 * Get the description of the Special Ability
	 * 
	 * @return the description of the Special Ability
	 */
	public String getSADesc()
	{
		return getPropDesc();
	}

	/**
	 * Set the source of the special ability
	 * 
	 * @param saSource
	 */
	public void setSASource(final String saSource)
	{
		this.saSource = saSource;
	}

	/**
	 * Get the source of the Special Ability
	 * 
	 * @return source of the Special Ability
	 */
	public String getSASource()
	{
		return saSource;
	}

	/**
	 * @see pcgen.core.TextProperty#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Object obj)
	{
		if (obj instanceof SpecialAbility)
		{
			SpecialAbility sa = (SpecialAbility) obj;
			if (keyName.equals(sa.getKeyName()))
			{
				return saSource.compareToIgnoreCase(sa.saSource);
			}
		}

		return keyName.compareToIgnoreCase(obj.toString());
	}

	/**
	 * @see pcgen.core.TextProperty#toString()
	 */
	@Override
	public String toString()
	{
		return displayName;
	}

	/**
	 * Check if the PC qualifies for the Special Ability.
	 */
	@Override
	public boolean pcQualifiesFor(final PlayerCharacter aPC)
	{

		// If the PC doesn't meet all of pre-reqs then return false
		if (!PrereqHandler.passesAll(getPreReqList(), aPC, null))
		{
			return false;
		}

		// If there is an empty or '0' qualification, then return true
		if ("".equals(saSource) || saSource.endsWith("|0")) //$NON-NLS-1$
		{
			return true;
		}

		// currently source is either empty or
		// PCCLASS|classKey|classlevel (means it's a chosen special ability)
		// PCCLASS=classKey|classlevel (means it's a defined special ability)
		// DEITY=deityKey|totallevels
		final StringTokenizer aTok = new StringTokenizer(saSource, "|=", false);
		final String aString = aTok.nextToken();
		final String aKey = aTok.nextToken();
		final PCClass aClass;
		final int level;

		// Get the level, if not a valid number then return false
		try
		{
			level = Integer.parseInt(aTok.nextToken());
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("pcQualifiesFor:" + saSource, exc); //$NON-NLS-1$
			return false;
		}

		// If we're reading in PCCLASS then check the PC's level (for that
		// class) versus the level that we need to qualify for
		if ("PCCLASS".equals(aString))
		{
			aClass = aPC.getClassKeyed(aKey);

			// If the PC doesn't have that class then return false
			if (aClass == null)
			{
				return false;
			}

			return (aClass.getLevel() >= level);
		}

		// Otherwise by default we check against the total level
		return aPC.getTotalLevels() >= level;
	}
}
