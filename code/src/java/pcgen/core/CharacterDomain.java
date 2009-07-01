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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.helper.ClassSource;


/**
 * <code>CharacterDomain</code>.
 *
 * A cleric domain that is used by a character.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class CharacterDomain
{
	/** The object type for a domain from a PC Class */
	public static final String PC_CLASS_TYPE = "PCClass";

	private Domain domain; // reference to the domain

	private final ClassSource source;

	public CharacterDomain (ClassSource cs)
	{
		source = cs;
	}
	
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
			domain = aDomain;
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
		buff.append(PC_CLASS_TYPE);
		buff.append('|');
		buff.append(source.getPcclass().getKeyName());

		if (source.getLevel() > 0)
		{
			buff.append('|');
			buff.append(source.getLevel());
		}

		return buff.toString();
	}

	/**
	 * Returns whether the domain is from the given PC Class
	 * @param pcClassName String name of PC Clas to check
	 * @return boolean true if the domain is from the given PC Class
	 */
	public boolean isFromPCClass(PCClass pcClass)
	{
		return pcClass.equals(source.getPcclass());
	}

	public boolean hasSourceClass()
	{
		return source != null && source.getPcclass() != null;
	}

	/**
	 * The domain's source
	 * e.g: Cleric (if from the Cleric class)
	 * @return the source
	 */
	public String getSourceClassKey()
	{
		return source.getPcclass().getKeyName();
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
	@Override
	public String toString()
	{
		String string = "";

		if (domain != null)
		{
			final StringBuffer name = new StringBuffer(domain.getDisplayName());

			final PCClass aClass = source.getPcclass();

			if (aClass != null)
			{
				name.insert(0, aClass.getDisplayName() + ":");
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
		final PCClass aClass = aPC.getClassKeyed(source.getPcclass().getKeyName());
		return ((aClass != null) && (aClass.getLevel(aPC) >= source.getLevel()));
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
			StringBuilder prefix = new StringBuilder();
			prefix.append("DOMAIN:").append(aDomain.getKeyName()).append('|')
					.append(-9).append('|');

			for (VariableKey vk : aDomain.getVariableKeys())
			{
				StringBuilder sb = new StringBuilder();
				sb.append(prefix).append(vk.toString()).append('|').append(aDomain.get(vk));

				if (addIt)
				{
					aPC.addVariable(sb.toString());
				}
				else
				{
					aPC.removeVariable(sb.toString());
				}
			}
		}
	}

	/**
	 * @return Returns the domainType.
	 */
	public String getDomainType()
	{
		return PC_CLASS_TYPE;
	}
}
