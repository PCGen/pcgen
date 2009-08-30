/*
 * KitLangBonus.java
 * Copyright 2008 (C) James Dempsey
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
 * Created on 2/10/2008 16:50:38
 *
 * $Id: $
 */
package pcgen.core.kit;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;

/**
 * Deals with applying a bonus language via a Kit
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public class KitLangBonus extends BaseKit
{
	/** The list of language names. */
	private List<CDOMSingleRef<Language>> langList =
			new ArrayList<CDOMSingleRef<Language>>();

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient List<Language> theLanguages = new ArrayList<Language>();

	/**
	 * Actually applies the bonus languages to this PC.
	 * 
	 * @param aPC The PlayerCharacter the languages are to be applied to
	 */
	@Override
	public void apply(PlayerCharacter aPC)
	{
		for (Language l : theLanguages)
		{
			aPC.addStartingLanguage(l);
		}
	}

	/**
	 * Prepare the languages to be added and test their application.
	 * 
	 * @param aPC The character to be processed.
	 * @param aKit The kit being processed
	 * @param warnings List of warnigns.
	 * 
	 * @return true, if the languages could be added
	 */
	@Override
	public boolean testApply(Kit aKit, PlayerCharacter aPC,
		List<String> warnings)
	{
		final List<Language> availableLangs = new ArrayList<Language>();
		final List<Language> selectedLangs = new ArrayList<Language>();
		final List<Language> excludedLangs = new ArrayList<Language>();

		int numLanguages = aPC.languageNum(false);

		aPC.buildLangLists(availableLangs, selectedLangs, excludedLangs);

		numLanguages -= selectedLangs.size();
		if (numLanguages <= 0)
		{
			return false;
		}

		theLanguages = new ArrayList<Language>(numLanguages);
		for (CDOMSingleRef<Language> langKey : langList)
		{
			Language lang = langKey.resolvesTo();
			if (availableLangs.contains(lang))
			{
				theLanguages.add(lang);
				if (theLanguages.size() >= numLanguages)
				{
					break;
				}
			}
			else
			{
				warnings.add("LANGUAGE: Could not add bonus language \""
					+ langKey + "\"");
			}
		}

		if (langList.size() > numLanguages)
		{
			warnings.add("LANGUAGE: Too many bonus languages specified. "
				+ (langList.size() - numLanguages) + " had to be ignored.");
		}

		if (theLanguages.size() > 0)
		{
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.kit.BaseKit#getObjectName()
	 */
	@Override
	public String getObjectName()
	{
		return "Languages";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return StringUtil.join(langList, ", ");
	}

	public void addLanguage(CDOMSingleRef<Language> reference)
	{
		langList.add(reference);
	}

	public List<CDOMSingleRef<Language>> getLanguages()
	{
		return langList;
	}
}
