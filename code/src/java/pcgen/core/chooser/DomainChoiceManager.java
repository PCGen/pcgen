/**
 * DomainChoiceManager.java
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
 * Current Version: $Revision: 285 $
 * Last Editor:     $Author: nuance $
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import pcgen.core.CharacterDomain;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

import java.util.Iterator;
import java.util.List;

/**
 * This is the chooser that deals with choosing a domain.
 */
public class DomainChoiceManager extends AbstractComplexChoiceManager {

	/**
	 * Make a new Armor Type chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public DomainChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title = "Domain Choice";
		chooserHandled = "DOMAIN";

		if (choices != null && choices.size() > 0 &&
				((String) choices.get(0)).equals(chooserHandled)) {
			choices = choices.subList(1, choices.size());
		}
	}


	/**
	 * Parse the Choice string and build a list of available choices.
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public void getChoices(
			final PlayerCharacter aPc,
			final List            availableList,
			final List            selectedList)
	{
		Iterator choiceIt = choices.iterator();
		while (choiceIt.hasNext())
		{
			String option = (String) choiceIt.next();
			if ("ANY".equals(option))
			{
				// returns a list of all loaded Domains.
				List domains = Globals.getDomainList();
				for (Iterator i = domains.iterator(); i.hasNext(); )
				{
					Domain domain = (Domain)i.next();
					availableList.add(domain);
				}
				break;
			}
			else if ("QUALIFY".equals(option))
			{
				// returns a list of loaded Domains the PC qualifies for
				// but does not have.
				List allDomains = Globals.getDomainList();
				for (Iterator i = allDomains.iterator(); i.hasNext(); )
				{
					Domain domain = (Domain)i.next();
					if (domain.qualifiesForDomain(aPc))
					{
						boolean found = false;
						List pcDomainList = aPc.getCharacterDomainList();
						for (Iterator j = pcDomainList.iterator(); j.hasNext();)
						{
							CharacterDomain cd = (CharacterDomain)j.next();
							if (domain.equals(cd.getDomain()))
							{
								found = true;
								break;
							}
						}
						if (found == false)
						{
							availableList.add(domain);
						}
					}
				}
				break;
			}
			else if ("PC".equals(option))
			{
				// returns a list of all domains a character actually has.
				List pcDomainList = aPc.getCharacterDomainList();
				for (Iterator i = pcDomainList.iterator(); i.hasNext();)
				{
					CharacterDomain cd = (CharacterDomain)i.next();
					availableList.add(cd.getDomain());
				}
				break;
			}
			else if (option.startsWith("DEITY"))
			{
				// returns a list of Domains granted by specified Diety.
				String deityName = option.substring(6);
				Deity deity = Globals.getDeityKeyed(deityName);
				if (deity != null)
				{
					List domainList = deity.getDomainList();
					for (Iterator i = domainList.iterator(); i.hasNext();)
					{
						Domain domain = (Domain)i.next();
						availableList.add(domain);
					}
				}
				break;
			}
			else
			{
				// returns a list of the specified domains.
				Domain domain = Globals.getDomainKeyed(option);
				if (domain != null)
				{
					availableList.add(domain);
				}
			}
		}

		pobject.addAssociatedTo(selectedList);
	}

}
