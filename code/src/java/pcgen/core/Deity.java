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

import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.utils.*;
import pcgen.util.Logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

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
		buildDomainList(null);
		listChar.initializeListFor(ListKey.PANTHEON);
		listChar.initializeListFor(ListKey.RACEPANTHEON);
		listChar.initializeListFor(ListKey.DOMAIN);
	}

	/**
	 * This method adds a single domain to the domains that this deity
	 * allows.
	 * @param domainName String name of the domain
	 */
	public void addDomain(final String domainName) {
		final Domain domain = Globals.getDomainKeyed( domainName );
		if (domain != null)
		{
			listChar.addToListFor(ListKey.DOMAIN, domain);
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
	public boolean canBeSelectedBy(final List classList, final int anAlignment, final PlayerCharacter pc)
	{
		boolean result;

		try
		{
			result = acceptableClass(classList.iterator())
							 &&	allowsAlignment(anAlignment,pc)
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
	public Object clone()
	{
		Deity d = null;

		try
		{
			d = (Deity) super.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		return d;
	}

	/**
	 * @return this deity's alignment
	 */
	public String getAlignment()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.ALIGNMENT);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * @return the name of the appearance of this deity
	 */
	public String getAppearance()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.APPEARANCE);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * @return a List of the domains this deity has
	 */
	public List getDomainList()
	{
		return getListFor(ListKey.DOMAIN);
	}

	/**
	 * @return a comma-separated string of the PI-formatted domains this deity has
	 */
	public String getDomainListPIString()
	{
		String domainListPIString = stringChar.getCharacteristic(StringKey.DOMAIN_LIST_PI);

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
						final Iterator iter = getDomainList().iterator();
						boolean started = false;

						while (iter.hasNext())
						{
							final Domain aDomain = (Domain) iter.next();

							if (aDomain != null)
							{
								if (started)
								{
									piString.append(',');
								}
								else
								{
									started = piString.length() > 0;
								}

								piString.append(aDomain.piSubString());
							}
						}
					}

					piString.append("</html>");

					stringChar.setCharacteristic(StringKey.DOMAIN_LIST_PI, piString.toString());
				}
				// end of double-locking
			}
			// end of synchronized block
		}
		// end of null-check

		return stringChar.getCharacteristic(StringKey.DOMAIN_LIST_PI);
	}

	/**
	 * @return the name of the favored weapon of this deity
	 */
	public String getFavoredWeapon()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.FAVORED_WEAPON);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * @return a comma-separated String of the alignments this deity can accept
	 */
	public String getFollowerAlignments()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.FOLLOWER_ALIGNMENTS);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * @return the name of the holy item of this deity
	 */
	public String getHolyItem()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.HOLY_ITEM);
		return characteristic == null ? Constants.s_NONE : characteristic;
	}

	/**
	 * This method returns the list of pantheons this deity belongs to
	 * @return List containing the names of the pantheons this deity belongs to
	 */
	public List getPantheonList()
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
		txt.append(getName());

		List domainList = getListFor(ListKey.DOMAIN);
		if (domainList != null && domainList.size()!= 0)
		{
			txt.append("\tDOMAINS:");
			final Iterator iter = domainList.iterator();
			while (iter.hasNext())
			{
				final Domain domain = (Domain) iter.next();
				txt.append(domain.getName());
				if (iter.hasNext())
				{
					txt.append(",");
				}
			}
		}

		String followerAlignments = getFollowerAlignments();
		if (followerAlignments.length() != 0)
		{
			txt.append("\tFOLLOWERALIGN:").append(followerAlignments);
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

		List pantheonList = getPantheonList();
		if (pantheonList.size() != 0)
		{
			txt.append("\tPANTHEON:").append(CoreUtility.join(pantheonList, "|"));
		}

		List raceList = getRacePantheonList();
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
	public List getRacePantheonList()
	{
		return getListFor(ListKey.RACEPANTHEON);
	}

	/**
	 * @return the name of the title of this deity
	 */
	public String getTitle()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.TITLE);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * @return the name of the worshippers of this deity
	 */
	public final String getWorshippers()
	{
		String characteristic = stringChar.getCharacteristic(StringKey.WORSHIPPERS);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * @param aDomain
	 * @return true if the deity has the passed-in domain
	 */
	public boolean hasDomain(final Domain aDomain)
	{
		return d_allDomains || getDomainList().contains(aDomain);
	}

	/**
	 * @param domainName
	 * @return true if the deity has the passed-in domain
	 */
	public boolean hasDomainNamed(final String domainName)
	{
		final Domain testDomain = Globals.getDomainNamed(domainName);
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
		final List prereqs = new ArrayList();
		addPreReqTo(prereqs);
		final List alignPrereqs = new ArrayList();

		String alignText = "";
		String followerAlignments = getFollowerAlignments();
		if (followerAlignments.length() != 0)
		{
			Logging.debugPrint("preReqHTMLStrings: " + followerAlignments);

			for (int i = 0; i < followerAlignments.length(); ++i)
			{
				final Prerequisite prereq = new Prerequisite();
				prereq.setKind("align");
				prereq.setOperator(PrerequisiteOperator.EQ);
				prereq.setKey(followerAlignments.substring(i, i + 1));
				alignPrereqs.add(prereq);
			}
			alignText = "One of (" + PrerequisiteUtilities.preReqHTMLStringsForList(aPC, null, alignPrereqs, includeHeader) + ")";
		}
		String text = alignText;

		final String prereqText = PrerequisiteUtilities.preReqHTMLStringsForList(aPC, null, prereqs, includeHeader);
		if (!prereqText.equals(""))
		{
			text = text + ", " + prereqText;
		}
		return text;
	}

	/**
	 * This method adds a single domain to the domains that this deity
	 * allows.
	 * @param domainName String name of the domain
	 */
	public void removeDomain(final String domainName) {
		final Domain domain = Globals.getDomainKeyed( domainName );
		if (domain != null)
		{
			listChar.removeFromListFor(ListKey.DOMAIN, domain);
		}
		else
		{
			Logging.debugPrint("Can not find domain: '" + domainName + "'.");
		}
	}

	/**
	 * This method sets the appearance of this deity.
	 * @param appearance String name of the appearance of this deity.
	 */
	public void setAppearance(final String appearance)
	{
		stringChar.setCharacteristic(StringKey.APPEARANCE, appearance);
	}

	/**
	 * This method sets the deity's alignment
	 * @param alignment String containing the short abbreviation for the
	 * deity's alignment
	 */
	public void setAlignment(final String alignment)
	{
		stringChar.setCharacteristic(StringKey.ALIGNMENT, alignment);
	}

	/**
	 * This method is called from I/O routines to pass the deity
	 * a delimited string of domain names that this deity has.
	 * This method should ONLY be called from I/O!
	 * @param domainList String list of domains
	 */
	public void setDomainList(final List domainList)
	{
		listChar.addAllToListFor(ListKey.DOMAIN, domainList);
		stringChar.setCharacteristic(StringKey.DOMAIN_LIST_PI, null);
	}

	/**
	 * This method is called from I/O routines to pass the deity
	 * a delimited string of domain names that this deity has.
	 * This method should ONLY be called from I/O!
	 * @param aDomainStringList String list of domains
	 */
	public void setDomainNameList(final List aDomainStringList)
	{
		stringChar.setCharacteristic(StringKey.DOMAIN_LIST_PI, null);
		d_allDomains = false;
		buildDomainList(aDomainStringList);
	}

	/**
	 * This method sets the favored weapon of this deity.
	 * @param favoredWeapon String favored weapon of this deity.
	 */
	public void setFavoredWeapon(final String favoredWeapon)
	{
		stringChar.setCharacteristic(StringKey.FAVORED_WEAPON, favoredWeapon);
	}

	/**
	 * This method sets the string containing the numeric
	 * alignments accepted by this deity.
	 * @param followerAlignments String containing the numeric alignments (with no spaces
	 * or other delimiters, i.e. 3678).
	 */
	public void setFollowerAlignments(final String followerAlignments)
	{
		stringChar.setCharacteristic(StringKey.FOLLOWER_ALIGNMENTS, followerAlignments);
	}

	/**
	 * This method sets the holy weapon of this deity.
	 * @param holyItem String name of the holy weapon of this deity.
	 */
	public void setHolyItem(final String holyItem)
	{
		stringChar.setCharacteristic(StringKey.HOLY_ITEM, holyItem);
	}

	/**
	 * This method sets the list of pantheons that this deity belongs to.
	 * @param pantheonList a List of Strings which are the pantheons this Deity belongs to
	 */
	public void setPantheonList(final List pantheonList)
	{
		listChar.addAllToListFor(ListKey.PANTHEON, pantheonList);
	}

	/**
	 * This method sets the list of races that this deity accepts.
	 * @param raceList A List of race names that this Deity will accept followers from
	 */
	public void setRacePantheonList(final List raceList)
	{
		listChar.addAllToListFor(ListKey.RACEPANTHEON, raceList);
	}

	/**
	 * This method sets the title of this deity.
	 * @param title String name of the title of this deity.
	 */
	public void setTitle(final String title)
	{
		stringChar.setCharacteristic(StringKey.TITLE, title);
	}

	/**
	 * This method sets the worshippers of this deity.
	 * @param worshippers String name of the worshippers of this deity.
	 */
	public final void setWorshippers(final String worshippers)
	{
		stringChar.setCharacteristic(StringKey.WORSHIPPERS, worshippers);
	}

	/**
	 * This method adds a group of abilities to the list of special
	 * abilities granted by thid deity.
	 * @param aList List of SpecialAbility objects granted by
	 * this deity.
	 * @param aPC
	 * @return List
	 */
	protected List addSpecialAbilitiesToList(final List aList, final PlayerCharacter aPC)
	{
		final List specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);

		if ((specialAbilityList == null) || specialAbilityList.isEmpty())
		{
			return aList;
		}

		for (Iterator i = specialAbilityList.iterator(); i.hasNext();)
		{
			final SpecialAbility sa = (SpecialAbility) i.next();

			if (sa.pcQualifiesFor(aPC))
			{
				aList.add(sa);
			}
		}

		return aList;
	}

	/**
	 * This method determines whether any of the classes that a character
	 * has is acceptable to this deity.
	 * @param classList Iterator pointing to the Collection of classes the
	 * character has
	 * @return boolean
	 */
	private boolean acceptableClass(final Iterator classList)
	{
		boolean flag = (!classList.hasNext());

		while (classList.hasNext() && !flag)
		{
			final PCClass aClass = (PCClass) classList.next();
			final List deityList = aClass.getDeityList();

			for (Iterator iter = deityList.iterator(); iter.hasNext();)
			{
				final String deity = (String) iter.next();
				if ("ANY".equals(deity) || "ALL".equals(deity) || getName().equals(deity))
				{
					flag = true;
				}
			}
		}

		return flag;
	}

	/**
	 * @param index An integer representation of an alignment
	 * @param pc
	 * @return true if this deity allows worshippers of the passed in alignment
	 */
	private boolean allowsAlignment(final int index, final PlayerCharacter pc)
	{
		//[VARDEFINED=SuneLG=0]367
		String followerAlignments = getFollowerAlignments();

		for (;;)
		{
			final int idxStart = followerAlignments.indexOf('[');

			if (idxStart < 0)
			{
				break;
			}

			final int idxEnd = followerAlignments.indexOf(']', idxStart);

			if (idxEnd < 0)
			{
				break;
			}

			final String subPre = followerAlignments.substring(idxStart + 1, idxEnd);
			final StringTokenizer pTok = new StringTokenizer(subPre, "=", false);

			if (pTok.countTokens() != 3)
			{
				break;
			}

			final String cond = pTok.nextToken();
			final String vName = pTok.nextToken();
			final String condAlignment = pTok.nextToken();
			boolean hasCond = false;

			if ("VARDEFINED".equals(cond))
			{
				final PlayerCharacter aPC = pc;

				if ((aPC != null) && aPC.hasVariable(vName))
				{
					hasCond = true;
				}
			}

			if (hasCond)
			{
				followerAlignments = followerAlignments.substring(0, idxStart) + condAlignment + followerAlignments.substring(idxEnd + 1);
			}
			else
			{
				followerAlignments = followerAlignments.substring(0, idxStart) + followerAlignments.substring(idxEnd + 1);
			}
		}

		if (followerAlignments.length() != 0)
		{
			return followerAlignments.lastIndexOf(String.valueOf(index)) >= 0;
		}
		return true;
	}

	/**
	 * This method builds the contents of the domain list from the
	 * domain list String.
	 * @param stringList
	 */
	private void buildDomainList(final List stringList)
	{
		if ((stringList == null) || (stringList.size() == 0))
		{
			return;
		}
		else if (stringList.contains("ALL") || stringList.contains("ANY"))
		{
			// If it contains ALL or ANY we do not care what else it contains as
			// it will automatically contain all domains.
			listChar.addAllToListFor(ListKey.DOMAIN, Globals.getDomainList());
		}
		else
		{
			for (Iterator iter = stringList.iterator(); iter.hasNext();)
			{
				String domainName = (String) iter.next();
				boolean add = true;
				if (domainName.equals(".CLEAR"))
				{
					listChar.removeListFor(ListKey.DOMAIN);
					listChar.initializeListFor(ListKey.DOMAIN);
					continue;
				}
				else if (domainName.startsWith(".CLEAR."))
				{
					//TODO: (DJ) this looks like it doesn't clear if it starts with .CLEAR. Is this right?
					domainName = domainName.substring(7);
					add = false;
				}

				if (add)
				{
					addDomain(domainName);
				}
				else
				{
					removeDomain(domainName);
				}
			}
		}
	}
}
