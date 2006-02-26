/*
 * Campaign.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *
 * Current Ver: $Revision: 1.65 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/16 13:38:57 $
 *
 */
package pcgen.core;

import java.util.Collections;
import java.util.Set;

/**
 * <code>CharacterDomain</code>.
 *
 * A cleric domain that is used by a character.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.65 $
 */
public final class CharacterDomain
{
	/** The object type for a domain from a PC Class */
	public static final String PC_CLASS_TYPE = "PCClass";

	/** The object type for a domain from a Feat */
	public static final String ABILITY_CLASS_TYPE = "Feat";
	private Domain domain; // reference to the domain

	private String domainName = ""; // domain name
	private String domainType = ""; // type of domain -- feat, class, etc
	private boolean fromFeat; // true if domain is from a feat
	private boolean fromPCClass; // true if domain is from a PC Class
	private int level; // pre-req level

	/**
	 * Sets the domain
	 * @param aDomain Domain the domain to be set
	 * @param pc
	 * @return Domain
	 */
	public Domain setDomain(final Domain aDomain, final PlayerCharacter pc)
	{
		setSpecialAbilities(domain, false, pc);

		if (aDomain == null)
		{
			domain = null;
		}
		else
		{
			domain = (Domain) aDomain.clone();
			setSpecialAbilities(domain, true, pc);
		}

		return domain;
	}

	/** Returns the domain
	 * @return Domain the domain
	 */
	public Domain getDomain()
	{
		return domain;
	}


	/**
	 * Returns the source of the domain in the format "PObject|name[|level]"
	 * For example, "PCClass|Cleric|1"
	 * (since the level is relevant)
	 * For example, "Feat|Awesome Divinity" to attach a domain to a feat
	 *
	 * This method should NOT be called outside of file i/o routines
	 * DO NOT perform comparisons on this String
	 *
	 * @return String the source of the domain
	 */
	public String getDomainSourcePcgString()
	{
		final StringBuffer buff = new StringBuffer(30);
		buff.append(domainType);
		buff.append('|');
		buff.append(domainName);

		if (level > 0)
		{
			buff.append('|');
			buff.append(level);
		}

		return buff.toString();
	}

	/**
	 * Returns whether the domain is from a feat
	 * @return boolean true if the domain is from a feat, else false
	 */
	public boolean isFromFeat()
	{
		return fromFeat;
	}

	/**
	 * Sets whether the domain is from a PC Class
	 * @param isPCClass boolean true if the domain is from a PC Class
	 */
	public void setFromPCClass(final boolean isPCClass)
	{
		fromPCClass = isPCClass;
		domainType = PC_CLASS_TYPE;
	}

	/**
	 * Returns whether the domain is from a PC Class
	 * @return boolean true if the domain is from a PC Class, else false
	 */
	public boolean isFromPCClass()
	{
		return fromPCClass;
	}

	/**
	 * Returns whether the domain is from the given PC Class
	 * @param pcClassName String name of PC Clas to check
	 * @return boolean true if the domain is from the given PC Class
	 */
	public boolean isFromPCClass(final String pcClassName)
	{
		if (fromPCClass)
		{
			return domainName.equalsIgnoreCase(pcClassName);
		}
		return false;
	}

	/**
	 * Sets the name of the domain's source
	 * e.g: Cleric (if from the Cleric class)
	 * @param aName
	 */
	public void setObjectName(final String aName)
	{
		domainName = aName;
	}

	/**
	 * What the name of the domain's source is
	 * e.g: Cleric (if from the Cleric class)
	 * @return String the name of the source
	 */
	public String getObjectName()
	{
		return domainName;
	}

	/**
	 * Gets the variable names as a set that cannot be modified
	 * @return Set
	 */
	public Set getVariableNamesAsUnmodifiableSet()
	{
		if (domain != null)
		{
			return domain.getVariableNamesAsUnmodifiableSet();
		}

		return Collections.EMPTY_SET;
	}

	/**
	 * Converts this object to a String
	 * The String format is as follows (without the braces) :
	 * <ul>
	 * <li>[class name]:[domainName] (if from a class)</li>
	 * <li>[feat name]:[domainName] (if from a feat)</li>
	 * <li>[domainName] (if from a feat)</li>
	 * <li>[domainName] (if from a non-feat non-class source)</li>
	 * <li>An empty string (if the domain is unset)</li>
	 * </ul>
	 * @return String
	 */
	public String toString()
	{
		String string = "";

		if (domain != null)
		{
			final StringBuffer name = new StringBuffer(domain.getName());

			if (fromPCClass)
			{
				final PCClass aClass = Globals.getClassNamed(domainName);

				if (aClass != null)
				{
					name.insert(0, aClass.getName() + ":");
				}
			}
			else if (fromFeat)
			{
				final Ability aFeat = Globals.getAbilityNamed("FEAT", domainName);

				if (aFeat != null)
				{
					name.insert(0, aFeat.getName() + ":");
				}
			}

			string = name.toString();
		}

		return string;
	}

	/**
	 * Checks if a pc can take a domain
	 * @param aPC
	 * @return True if the domain is valid for pc
	 */
	boolean isDomainValidFor(final PlayerCharacter aPC)
	{
		boolean valid = false;

		if (domain == null)
		{
			valid = false;
		}

		if (fromPCClass)
		{
			final PCClass aClass = aPC.getClassNamed(domainName);
			valid = ((aClass != null) && (aClass.getLevel() >= level));
		}

		// Just preparing for the eventuality
		// that feats will add domains.
		// merton_monk@yahoo.com
		if (fromFeat)
		{
			valid = (aPC.hasRealFeatNamed(domainName) || aPC.hasFeatAutomatic(domainName) || aPC.hasFeatVirtual(domainName));
		}

		return valid;
	}

	/**
	 * Sets the minimum level for this domain
	 *
	 * @param level containing the new minimum level for the domain
	 */
	public void setLevel(final int level)
	{
		this.level = level;
	}

	/**
	 * This method adds/removes the special abilities
	 * on the current PC that have been granted by this domain
	 * @param aDomain Domain granting the abilities
	 * @param addIt boolean true if the abilities should be added, or
	 * @param pc The character to add/remove abilities to
	 * false if they should be removed
	 */
	private static void setSpecialAbilities(final Domain aDomain, final boolean addIt, final PlayerCharacter pc)
	{
		final PlayerCharacter aPC = pc;

		if ((aPC != null) && (aDomain != null))
		{
			final String aString = "DOMAIN:" + aDomain.getName() + '|';

			for (int i = 0; i < aDomain.getVariableCount(); i++)
			{
				final String aVar = aString + aDomain.getVariableDefinition(i);

				if (addIt)
				{
					aPC.addVariable(aVar);
				}
				else
				{
					aPC.removeVariable(aVar);
				}
			}
		}
	}
	/**
	 * @return Returns the domainName.
	 */
	public String getDomainName()
	{
		return domainName;
	}

	/**
	 * @param domainName The domainName to set.
	 */
	public void setDomainName(final String domainName)
	{
		this.domainName = domainName;
	}

	/**
	 * @return Returns the domainType.
	 */
	public String getDomainType()
	{
		return domainType;
	}

	/**
	 * @param domainType The domainType to set.
	 */
	public void setDomainType(final String domainType)
	{
		this.domainType = domainType;

		if (this.domainType.equalsIgnoreCase(PC_CLASS_TYPE))
		{
			fromPCClass = true;
		}
		else if (this.domainType.equalsIgnoreCase(ABILITY_CLASS_TYPE))
		{
			fromFeat = true;
		}
	}

}
