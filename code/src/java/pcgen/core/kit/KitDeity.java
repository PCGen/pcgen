/*
 * KitDeity.java
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
 * Created on October 3, 2005, 5:55 PM
 *
 * $Id$
 */
package pcgen.core.kit;

import pcgen.core.*;
import pcgen.gui.CharacterInfo;
import pcgen.gui.PCGen_Frame1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Deal with Dieties via Kits
 */
public class KitDeity extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private String theDeityKey = null;
	private String countFormula = "";
	private List<String> theDomains = null;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient Deity theDeity = null;
	private transient List<Domain> domainsToAdd = null;

	/**
	 * Constructor
	 * @param aDeityName
	 */
	public KitDeity(final String aDeityKey)
	{
		theDeityKey = aDeityKey;
	}

	/**
	 * Get the deityName
	 * @return the deityName
	 */
	public String getDeityKey()
	{
		return theDeityKey;
	}

	/**
	 * Add the domain
	 * @param aDomainName
	 */
	public void addDomain(final String aDomainName)
	{
		if (theDomains == null)
		{
			theDomains = new ArrayList<String>(3);
		}
		theDomains.add(aDomainName);
	}

	/**
	 * Get domains
	 * @return list of domains
	 */
	public List<String> getDomains()
	{
		if (theDomains == null)
		{
			return null;
		}

		return Collections.unmodifiableList(theDomains);
	}

	/**
	 * Set the COUNT formula
	 * @param argCountFormula
	 */
	public void setCountFormula(final String argCountFormula)
	{
		countFormula = argCountFormula;
	}

	/**
	 * Get the COUNT formula
	 * @return COUNT formula
	 */
	public String getCountFormula()
	{
		return countFormula;
	}

	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();

		buf.append(theDeityKey);

		if (theDomains != null && theDomains.size() > 0)
		{
			buf.append(" (");
			if (countFormula.length() > 0)
			{
				buf.append(countFormula);
				buf.append(" of ");
			}
			for (Iterator<String> i = theDomains.iterator(); i.hasNext(); )
			{
				buf.append(i.next());
				if (i.hasNext())
				{
					buf.append(", ");
				}
			}
			buf.append(")");
		}

		return buf.toString();
	}

	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		theDeity = null;
		domainsToAdd = null;
		if (theDeityKey == null)
		{
			return false;
		}

		theDeity = Globals.getDeityKeyed(theDeityKey);
		if (theDeity == null)
		{
			warnings.add("DEITY: Could not find deity '" + getDeityKey()
						 + "'");
			return false;
		}
		if (!aPC.canSelectDeity(theDeity))
		{
			warnings.add("DEITY: Cannot select deity \"" + theDeity.getDisplayName() +
						 "\"");
			return false;
		}
		aPC.setDeity(theDeity);

		List<String> domains = getDomains();
		if (domains == null || domains.size() == 0)
		{
			// nothing else to do.
			return true;
		}

		if (aPC.getMaxCharacterDomains() <= 0)
		{
			warnings.add("DEITY: Not allowed to choose a domain");

			return true;
		}
		final String choiceFormula = getCountFormula();
		int          numberOfChoices;

		if (choiceFormula.length() == 0)
		{
			numberOfChoices = domains.size();
		}
		else
		{
			numberOfChoices = aPC.getVariableValue(choiceFormula, "").intValue();
		}

		//
		// Can't choose more entries than there are...
		//
		if (numberOfChoices > domains.size())
		{
			numberOfChoices = domains.size();
		}

		if (numberOfChoices == 0)
		{
			// No choices allowed, we are done.
			return true;
		}

		List<String> xs;
		if (numberOfChoices == domains.size())
		{
			xs = domains;
		}
		else
		{
			//
			// Force user to make enough selections
			//
			while (true)
			{
				xs = Globals.getChoiceFromList(
						"Choose Domains",
						domains,
						new ArrayList<String>(),
						numberOfChoices);

				if (xs.size() != 0)
				{
					break;
				}
			}
		}
		//
		// Add to list of things to add to the character
		//
		for (Iterator<String> e = xs.iterator(); e.hasNext();)
		{
			final String domainKey = e.next();

			Domain domain = Globals.getDomainKeyed(domainKey);
			if (domain != null)
			{
				if (!domain.qualifiesForDomain(aPC))
				{
					warnings.add("DEITY: Not qualified for domain \"" +
								 domain.getDisplayName() + "\"");
					continue;
				}
				CharacterDomain aCD = aPC.getCharacterDomainForDomain(domain.getKeyName());

				if (aCD == null)
				{
					aCD = aPC.getNewCharacterDomain();
				}

				if (aCD == null || (aPC.getCharacterDomainUsed() >= aPC.getMaxCharacterDomains()))
				{
					warnings.add("DEITY: No more allowed domains");

					return false;
				}

				if (domainsToAdd == null)
				{
					domainsToAdd = new ArrayList<Domain>();
				}
				domainsToAdd.add(domain);

				domain.setIsLocked(true, aPC);
				aCD.setDomain(domain, aPC);
				aPC.addCharacterDomain(aCD);
			}
			else
			{
				warnings.add("DEITY: Non-existant domain \""
							 + domainKey + "\"");
			}
		}
		aPC.calcActiveBonuses();
		return true;
	}

	public void apply(PlayerCharacter aPC)
	{
		if (theDeity == null)
		{
			return;
		}
		aPC.setDeity(theDeity);

		if (domainsToAdd == null)
		{
			return;
		}
		for ( Domain domain : domainsToAdd )
		{
			CharacterDomain aCD = aPC.getCharacterDomainForDomain(domain.getKeyName());

			if (aCD == null)
			{
				aCD = aPC.getNewCharacterDomain();
			}

			if (aCD == null)
			{
				// Shouldn't happen
				continue;
			}
			domain.setIsLocked(true, aPC);
			aCD.setDomain(domain, aPC);
			aPC.addCharacterDomain(aCD);
		}
		aPC.calcActiveBonuses();

		final CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoDomain());
		pane.refresh();

		theDeity = null;
		domainsToAdd = null;
	}

	@Override
	public KitDeity clone()
	{
		return (KitDeity) super.clone();
	}

	public String getObjectName()
	{
		return "Deity";
	}
}
