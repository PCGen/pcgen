/*
 * Deity.java
 * Copyright 2001 (C) Bryan McRoberts (merton_monk@yahoo.com)
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
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Logging;

/**
 * <code>Deity</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Deity extends PObject
{
	private boolean d_allDomains = false;

	/**
	 * Deity Constructor.
	 */
	public Deity()
	{
		buildDomainList(null, null);
		listChar.initializeListFor(ListKey.PANTHEON);
		listChar.initializeListFor(ListKey.RACEPANTHEON);
		listChar.initializeListFor(ListKey.DOMAIN);
	}

	/**
	 * This method adds a single domain to the domains that this deity
	 * allows.
	 * @param domainName String name of the domain
	 * @param prereqs The list of deity specific prerequisites for taking this domain.
	 */
	public void addDomain(final String domainName, List<Prerequisite> prereqs) {
		final Domain domain = Globals.getDomainKeyed( domainName );
		if (domain != null)
		{
			listChar.addToListFor(ListKey.DOMAIN, new QualifiedObject<Domain>(
				domain, prereqs));
		}
		else
		{
			Logging.debugPrint("Can not find domain: '" + domainName + "'.");
		}
	}

	/**
	 * This method adds a single pantheon to the pantheons that this deity
	 * belongs to and also ensures that it is present in the global list
	 * of pantheon names.
	 * @param pantheon String name of a pantheon
	 */
	public void addPantheon(String pantheon)
	{
		pantheon = pantheon.trim();
		listChar.addToListFor(ListKey.PANTHEON, pantheon);
		Globals.getPantheons().add(pantheon);
	}

	/**
	 * This method adds a single pantheon to the pantheons that this deity
	 * belongs to and also ensures that it is present in the global list
	 * of pantheon names.
	 * @param race String name of a pantheon
	 */
	public void addRacePantheon(String race)
	{
		race = race.trim();
		listChar.addToListFor(ListKey.RACEPANTHEON, race);
		Globals.getPantheons().add(race);
	}

	/**
	 * Check whether this deity can be selected by a character with the
	 * given classes, alignment, race and gender.
	 *
	 * @param classList a list of PCClass objects.
	 * @param anAlignment 0 through 8 inclusive
	 * @param pc
	 * @return <code>true</code> means the deity can be a selected by a
	 * character with the given properties; <code>false</code> means the
	 * character cannot.
	 */
	public boolean canBeSelectedBy(final List<PCClass> classList, final int anAlignment, final PlayerCharacter pc)
	{
		boolean result;

		try
		{
			result = acceptableClass(classList.iterator())
							 && PrereqHandler.passesAll( getPreReqList(), pc, this);
		}
		catch (NumberFormatException nfe)
		{
			result = false;
		}

		return result;
	}

	/**
	 * Clones a Deity object
	 *
	 * @return A clone of the Deity object.
	 */
	@Override
	public Deity clone()
	{
		try
		{
			return (Deity) super.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
			return null;
		}
	}

	/**
	 * @return this deity's alignment
	 */
	public String getAlignment()
	{
		String characteristic = stringChar.get(StringKey.ALIGNMENT);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * @return the name of the appearance of this deity
	 */
	public String getAppearance()
	{
		String characteristic = stringChar.get(StringKey.APPEARANCE);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * @return a List of the domains this deity has
	 */
	public List<QualifiedObject<Domain>> getDomainList()
	{
		return getListFor(ListKey.DOMAIN);
	}

	/**
	 * @return a comma-separated string of the PI-formatted domains this deity has
	 */
	public String getDomainListPIString()
	{
		String domainListPIString = stringChar.get(StringKey.DOMAIN_LIST_PI);

		if (domainListPIString == null)
		{
			// In order to be lazy, we need to make sure we're
			// safely creating only one instance of the string.
			// This requires synchronization.
			//TODO: Are you sure this works? See http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
			synchronized (this)
			{
				if (domainListPIString == null)
				{
					final StringBuffer piString = new StringBuffer(100);
					piString.append("<html>");

					// Shortcut for all domains
					if (d_allDomains)
					{
						piString.append("ALL");
					}

					// Build string of domains separated by commas
					else
					{
						boolean started = false;
						
						for ( QualifiedObject<Domain> qualDomain : getDomainList() )
						{
							if (qualDomain != null)
							{
								Domain domain = qualDomain.getObject(null);
								if (started)
								{
									piString.append(',');
								}
								else
								{
									started = piString.length() > 0;
								}

								piString.append(domain.piSubString());
							}
						}
					}

					piString.append("</html>");

					stringChar.put(StringKey.DOMAIN_LIST_PI, piString.toString());
				}
				// end of double-locking
			}
			// end of synchronized block
		}
		// end of null-check

		return stringChar.get(StringKey.DOMAIN_LIST_PI);
	}

	/**
	 * @return the name of the favored weapon of this deity
	 */
	public String getFavoredWeapon()
	{
		String characteristic = stringChar.get(StringKey.FAVORED_WEAPON);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * @return the name of the holy item of this deity
	 */
	public String getHolyItem()
	{
		String characteristic = stringChar.get(StringKey.HOLY_ITEM);
		return characteristic == null ? Constants.s_NONE : characteristic;
	}

	/**
	 * This method returns the list of pantheons this deity belongs to
	 * @return List containing the names of the pantheons this deity belongs to
	 */
	public List<String> getPantheonList()
	{
		return getListFor(ListKey.PANTHEON);
	}

	/**
	 * This method gets the text used in outputting source files (.pcc files)
	 * Made public on 10 Dec 2002 by sage_sam to match PObject method
	 * @return String containing properly formatted pcc text for this deity.
	 */
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getDisplayName());

		List<QualifiedObject<Domain>> domainList = getListFor(ListKey.DOMAIN);
		if (domainList != null && domainList.size()!= 0)
		{
			txt.append("\tDOMAINS:");
			List<Prerequisite> lastPreReqs = null;
			boolean start = true;
			final Iterator<QualifiedObject<Domain>> iter =
					domainList.iterator();
			while (iter.hasNext())
			{
				final QualifiedObject<Domain> qualDomain = iter.next();
				final Domain domain = qualDomain.getObject(null);
				final List<Prerequisite> prereqs = qualDomain.getPrereqs();
				if (lastPreReqs != null && !lastPreReqs.equals(prereqs))
				{
					txt.append(PrerequisiteUtilities.getPrerequisitePCCText(
						lastPreReqs, "|"));
					txt.append("\tDOMAINS:");
					start = true;
				}
				else if (!start)
				{
					txt.append(",");
				}
				else
				{
					start = false;					
				}
				lastPreReqs = prereqs;
				txt.append(domain.getKeyName());
			}
			if (lastPreReqs != null)
			{
				txt.append(PrerequisiteUtilities.getPrerequisitePCCText(
					lastPreReqs, "|"));
			}
		}

		if (getHolyItem().length() != 0)
		{
			txt.append("\tSYMBOL:").append(getHolyItem());
		}

		if (getFavoredWeapon().length() != 0)
		{
			txt.append("\tDEITYWEAP:").append(getFavoredWeapon());
		}

		txt.append("\tALIGN:").append(getAlignment());

		List<String> pantheonList = getPantheonList();
		if (pantheonList.size() != 0)
		{
			txt.append("\tPANTHEON:").append(CoreUtility.join(pantheonList, "|"));
		}

		List<String> raceList = getRacePantheonList();
		if (raceList.size() != 0)
		{
			txt.append("\tRACE:").append(CoreUtility.join(raceList, "|"));
		}

		txt.append(super.getPCCText(false));

		return txt.toString();
	}

	/**
	 * This method gets the list of races (names) that are acceptable
	 * to this deity.
	 * @return List of String names of races
	 */
	public List<String> getRacePantheonList()
	{
		return getListFor(ListKey.RACEPANTHEON);
	}

	/**
	 * @return the name of the title of this deity
	 */
	public String getTitle()
	{
		String characteristic = stringChar.get(StringKey.TITLE);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * @return the name of the worshippers of this deity
	 */
	public final String getWorshippers()
	{
		String characteristic = stringChar.get(StringKey.WORSHIPPERS);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * @param aDomain
	 * @return true if the deity has the passed-in domain
	 */
	public boolean hasDomain(final Domain aDomain)
	{
		if (d_allDomains)
		{
			return true;
		}
		for (QualifiedObject<Domain> qualDomain : getDomainList())
		{
			if (qualDomain.getObject(null).equals(aDomain))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @param domainKey
	 * @return true if the deity has the passed-in domain
	 */
	public boolean hasDomainKeyed(final String domainKey)
	{
		final Domain testDomain = Globals.getDomainKeyed(domainKey);
		return hasDomain(testDomain);
	}

	/**
	 * This method gets the comma-delimited list of pre-requisites for selecting
	 * this deity in HTML format.
	 * @param aPC
	 * @param includeHeader boolean true if the &lt;html&gt; and &lt;/html&gt; tags
	 * should be included
	 * @return the list of prerequisites
	 */
	public String preReqHTMLStrings(final PlayerCharacter aPC, final boolean includeHeader)
	{
		final List<Prerequisite> prereqs = new ArrayList<Prerequisite>();
		addPreReqTo(prereqs);
		final List<Prerequisite> alignPrereqs = new ArrayList<Prerequisite>();

		String text = "";

		final String prereqText = PrerequisiteUtilities.preReqHTMLStringsForList(aPC, null, prereqs, includeHeader);
		if (!prereqText.equals(""))
		{
			text = prereqText;
		}
		return text;
	}

	/**
	 * This method adds a single domain to the domains that this deity
	 * allows.
	 * @param domainKey Key of the domain
	 */
	public void removeDomain(final String domainKey) {
		final Domain domain = Globals.getDomainKeyed( domainKey );
		if (domain != null)
		{
			for (QualifiedObject<Domain> qualDomain : listChar.getListFor(ListKey.DOMAIN))
			{
				if (domain.equals(qualDomain.getObject(null)))
				{
					listChar.removeFromListFor(ListKey.DOMAIN, qualDomain);
				}
			}
		}
		else
		{
			Logging.debugPrint("Can not find domain: '" + domainKey + "'.");
		}
	}

	/**
	 * This method sets the appearance of this deity.
	 * @param appearance String name of the appearance of this deity.
	 */
	public void setAppearance(final String appearance)
	{
		stringChar.put(StringKey.APPEARANCE, appearance);
	}

	/**
	 * This method sets the deity's alignment
	 * @param alignment String containing the short abbreviation for the
	 * deity's alignment
	 */
	public void setAlignment(final String alignment)
	{
		stringChar.put(StringKey.ALIGNMENT, alignment);
	}

	/**
	 * This method is called from I/O routines to pass the deity
	 * a delimited string of domain names that this deity has.
	 * This method should ONLY be called from I/O!
	 * @param domainList String list of domains
	 */
	public void setDomainList(final List<QualifiedObject<Domain>> domainList)
	{
		listChar.removeListFor(ListKey.DOMAIN);
		listChar.addAllToListFor(ListKey.DOMAIN, domainList);
		stringChar.put(StringKey.DOMAIN_LIST_PI, null);
	}

	/**
	 * This method is called from I/O routines to pass the deity
	 * a delimited string of domain names that this deity has.
	 * This method should ONLY be called from I/O!
	 * @param aDomainStringList String list of domains
	 */
	public void setDomainNameList(final List<String> aDomainStringList, final List<Prerequisite> prereqs)
	{
		stringChar.put(StringKey.DOMAIN_LIST_PI, null);
		d_allDomains = false;
		buildDomainList(aDomainStringList, prereqs);
	}

	/**
	 * This method sets the favored weapon of this deity.
	 * @param favoredWeapon String favored weapon of this deity.
	 */
	public void setFavoredWeapon(final String favoredWeapon)
	{
		stringChar.put(StringKey.FAVORED_WEAPON, favoredWeapon);
	}

	/**
	 * This method sets the holy weapon of this deity.
	 * @param holyItem String name of the holy weapon of this deity.
	 */
	public void setHolyItem(final String holyItem)
	{
		stringChar.put(StringKey.HOLY_ITEM, holyItem);
	}

	/**
	 * This method sets the list of pantheons that this deity belongs to.
	 * @param pantheonList a List of Strings which are the pantheons this Deity belongs to
	 */
	public void setPantheonList(final List<String> pantheonList)
	{
		for ( String pantheon : pantheonList )
		{
			addPantheon(pantheon);
		}
	}

	/**
	 * This method sets the list of races that this deity accepts.
	 * @param raceList A List of race names that this Deity will accept followers from
	 */
	public void setRacePantheonList(final List<String> raceList)
	{
		for ( String race : raceList )
		{
			addRacePantheon(race);
		}
	}

	/**
	 * This method sets the title of this deity.
	 * @param title String name of the title of this deity.
	 */
	public void setTitle(final String title)
	{
		stringChar.put(StringKey.TITLE, title);
	}

	/**
	 * This method sets the worshippers of this deity.
	 * @param worshippers String name of the worshippers of this deity.
	 */
	public final void setWorshippers(final String worshippers)
	{
		stringChar.put(StringKey.WORSHIPPERS, worshippers);
	}

	/**
	 * This method determines whether any of the classes that a character
	 * has is acceptable to this deity.
	 * @param classList Iterator pointing to the Collection of classes the
	 * character has
	 * @return boolean
	 */
	private boolean acceptableClass(final Iterator<PCClass> classList)
	{
		boolean flag = (!classList.hasNext());

		while (classList.hasNext() && !flag)
		{
			final PCClass aClass = classList.next();
			for ( String deity : aClass.getDeityList() )
			{
				if ("ANY".equals(deity) || "ALL".equals(deity) || getKeyName().equals(deity))
				{
					flag = true;
				}
			}
		}

		return flag;
	}

	/**
	 * This method builds the contents of the domain list from the
	 * domain list String.
	 * @param stringList
	 * @param prereqs The list of the deity's prerequisities for this set of domains.
	 */
	private void buildDomainList(final List<String> stringList, final List<Prerequisite> prereqs)
	{
		if ((stringList == null) || (stringList.size() == 0))
		{
			return;
		}
		else if (stringList.contains("ALL") || stringList.contains("ANY"))
		{
			// If it contains ALL or ANY we do not care what else it contains as
			// it will automatically contain all domains.
			for (Domain domain : Globals.getDomainList())
			{
				listChar.addToListFor(ListKey.DOMAIN,
					new QualifiedObject<Domain>(domain, prereqs));
			}
		}
		else
		{
			for ( String domainKey : stringList )
			{
				boolean add = true;
				if (domainKey.equals(".CLEAR"))
				{
					listChar.removeListFor(ListKey.DOMAIN);
					listChar.initializeListFor(ListKey.DOMAIN);
					continue;
				}
				else if (domainKey.startsWith(".CLEAR."))
				{
					// Remove single entry
					domainKey = domainKey.substring(7);
					add = false;
				}

				if (add)
				{
					addDomain(domainKey, prereqs);
				}
				else
				{
					removeDomain(domainKey);
				}
			}
		}
	}
}
