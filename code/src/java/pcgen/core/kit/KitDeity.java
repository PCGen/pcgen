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

public class KitDeity extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private String theDeityName = null;
	private String countFormula = "";
	private List theDomains = null;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient Deity theDeity = null;
	private transient List domainsToAdd = null;

	public KitDeity(final String aDeityName)
	{
		theDeityName = aDeityName;
	}

	/**
	 * Get the deityName
	 * @return the deityName
	 */
	public String getDeityName()
	{
		return theDeityName;
	}

	public void addDomain(final String aDomainName)
	{
		if (theDomains == null)
		{
			theDomains = new ArrayList(3);
		}
		theDomains.add(aDomainName);
	}

	public List getDomains()
	{
		if (theDomains == null)
		{
			return null;
		}

		return Collections.unmodifiableList(theDomains);
	}
	public void setCountFormula(final String argCountFormula)
	{
		countFormula = argCountFormula;
	}

	public String getCountFormula()
	{
		return countFormula;
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer();

		buf.append(theDeityName);

		if (theDomains != null && theDomains.size() > 0)
		{
			buf.append(" (");
			if (countFormula.length() > 0)
			{
				buf.append(countFormula);
				buf.append(" of ");
			}
			for (Iterator i = theDomains.iterator(); i.hasNext(); )
			{
				buf.append((String)i.next());
				if (i.hasNext())
				{
					buf.append(", ");
				}
			}
			buf.append(")");
		}

		return buf.toString();
	}

	public boolean testApply(Kit aKit, PlayerCharacter aPC, List warnings)
	{
		theDeity = null;
		domainsToAdd = null;
		if (theDeityName == null)
		{
			return false;
		}

		theDeity = Globals.getDeityNamed(theDeityName);
		if (theDeity == null)
		{
			warnings.add("DEITY: Could not find deity '" + getDeityName()
						 + "'");
			return false;
		}
		if (!aPC.canSelectDeity(theDeity))
		{
			warnings.add("DEITY: Cannot select deity \"" + theDeity.getName() +
						 "\"");
			return false;
		}
		aPC.setDeity(theDeity);

		List domains = getDomains();
		if (domains == null || (domains != null && domains.size() == 0))
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

		List xs;
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
						new ArrayList(),
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
		for (Iterator e = xs.iterator(); e.hasNext();)
		{
			final String domainName = (String)e.next();

			Domain domain = Globals.getDomainNamed(domainName);
			if (domain != null)
			{
				if (!domain.qualifiesForDomain(aPC))
				{
					warnings.add("DEITY: Not qualified for domain \"" +
								 domainName + "\"");
					continue;
				}
				CharacterDomain aCD = aPC.getCharacterDomainForDomain(domain.getName());

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
					domainsToAdd = new ArrayList();
				}
				domainsToAdd.add(domain);

				domain.setIsLocked(true, aPC);
				aCD.setDomain(domain, aPC);
				aPC.addCharacterDomain(aCD);
			}
			else
			{
				warnings.add("DEITY: Non-existant domain \""
							 + domainName + "\"");
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
		for (Iterator i = domainsToAdd.iterator(); i.hasNext(); )
		{
			Domain domain = (Domain) i.next();
			CharacterDomain aCD = aPC.getCharacterDomainForDomain(domain.getName());

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
	}

	public Object clone()
	{
		KitDeity aClone = (KitDeity)super.clone();

		aClone.theDeityName = theDeityName;
		aClone.countFormula = countFormula;
		aClone.theDomains = theDomains;

		return aClone;
	}

	public String getObjectName()
	{
		return "Deity";
	}
}
