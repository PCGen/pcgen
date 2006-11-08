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
	protected String saSource = "";

	public SpecialAbility()
	{
		super();
	}

	public SpecialAbility(final String name)
	{
		super(name);
	}

	public SpecialAbility(final String name, final String saSource)
	{
		super(name);
		this.saSource = saSource;
	}

	public SpecialAbility(final String name, final String saSource, final String propDesc)
	{
		super(name, propDesc);
		this.saSource = saSource;
	}

	/** Set the class that is used to determine if a character should have this special
	 * ability
	 * @param oldClass The name of the original class
	 * @param newClass The name of the new class that is
	 * to be used for this special ability
	 */
	public void setQualificationClass(final String oldClass, final String newClass)
	{
		if ("".equals(saSource))
		{
			return;
		}

		try
		{
			final StringTokenizer aTok = new StringTokenizer(saSource, "|=", false);
			final String typeString = aTok.nextToken();
			final String classKey = aTok.nextToken();
			final String levelString = aTok.nextToken();

			if (classKey.equals(oldClass))
			{
				Logging.errorPrint("Source class changed from " + oldClass + " to " + newClass + " for " + displayName);

				setSASource(typeString + "=" + newClass + "|" + levelString);
			}
		}
		catch (Exception exc)
		{
			Logging.errorPrint("setQualificationClass:" + saSource, exc);
		}
	}

	public void setSADesc(final String saDesc)
	{
		setPropDesc(saDesc);
	}

	public String getSADesc()
	{
		return getPropDesc();
	}

	public void setSASource(final String saSource)
	{
		this.saSource = saSource;
	}

	public String getSASource()
	{
		return saSource;
	}

	public int compareTo(final Object obj)
	{
		if (obj instanceof SpecialAbility)
		{
			SpecialAbility sa = (SpecialAbility)obj;
			if (keyName.equals(sa.getKeyName()))
			{
				return saSource.compareToIgnoreCase(sa.saSource);
			}
		}

		return keyName.compareToIgnoreCase(obj.toString());
	}

	public String toString()
	{
		return displayName;
	}

	public boolean pcQualifiesFor(final PlayerCharacter aPC)
	{
		if ("".equals(saSource) || saSource.endsWith("|0"))
		{
			return true;
		}
		if (!PrereqHandler.passesAll(getPreReqList(), aPC, null))
		{
			return false;
		}

		// currently source is either empty or
		// PCCLASS|classKey|classlevel (means it's a chosen special ability)
		// PCCLASS=classKey|classlevel (means it's a defined special ability)
		// DEITY=deityKey|totallevels
		final StringTokenizer aTok = new StringTokenizer(saSource, "|=", false);
		final String aString = aTok.nextToken();
		final String aKey = aTok.nextToken();
		final PCClass aClass;
		final int anInt;

		try
		{
			anInt = Integer.parseInt(aTok.nextToken());
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("pcQualifiesFor:" + saSource, exc);

			return false;
		}

		if ("PCCLASS".equals(aString))
		{
			aClass = aPC.getClassKeyed(aKey);

			if (aClass == null)
			{
				return false;
			}

			return (aClass.getLevel() >= anInt);
		}

		return aPC.getTotalLevels() >= anInt;
	}
}
