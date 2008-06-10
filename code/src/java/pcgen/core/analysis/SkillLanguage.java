/*
 * Derived from Skill.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on June 9, 2008
 */
package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

public final class SkillLanguage
{

	/**
	 * Choose the language that is to be gained for the default language skill.
	 * The default language skill will be the first one found when scanning the
	 * PCs skill list. Note: This pops up a chooser so should not be used in
	 * batch mode.
	 * 
	 * @param aPC
	 *            The character to choose a language for.
	 * @return false if the laguage choice could not be offered. True otherwise.
	 */
	public static boolean chooseLanguageForSkill(PlayerCharacter aPC)
	{
		Skill languageSkill = null;

		List<Skill> skillList = new ArrayList<Skill>(aPC.getSkillList());
		for (Skill aSkill : skillList)
		{
			if (aSkill.getChoiceString().toLowerCase().indexOf("language") >= 0)
			{
				languageSkill = aSkill;
			}
		}

		return chooseLanguageForSkill(aPC, languageSkill);
	}

	/**
	 * Choose the language that is to be gained for the specified language
	 * skill. Note: This pops up a chooser so should not be used in batch mode.
	 * 
	 * @param aPC
	 *            The character to choose a language for.
	 * @param languageSkill
	 *            The language skill.
	 * @return false if the laguage choice could not be offered. True otherwise.
	 */
	public static boolean chooseLanguageForSkill(PlayerCharacter aPC,
			Skill languageSkill)
	{
		if (aPC != null)
		{
			if (languageSkill == null)
			{
				ShowMessageDelegate.showMessageDialog(
						"You do not have enough ranks in Speak Language",
						Constants.s_APPNAME, MessageType.ERROR);

				return false;
			}

			int numLanguages = languageSkill.getTotalRank(aPC).intValue();
			List<String> selectedLangNames = new ArrayList<String>();
			List<Language> selected = new ArrayList<Language>();
			List<Language> available = new ArrayList<Language>();
			List<Language> excludedLangs = new ArrayList<Language>();

			String reqType = null;
			if (languageSkill.getChoiceString().toLowerCase().indexOf(
					"language(") >= 0)
			{
				// We expect to have a choice string like Language(foo)
				// where foo is the type we have to limit choices by.
				String choiceParts[] = languageSkill.getChoiceString().split(
						"[\\(\\)]");
				if (choiceParts.length >= 2)
				{
					reqType = choiceParts[1];
				}
			}

			String[][] reqTypeArray;
			if (reqType == null)
			{
				reqTypeArray = null;
			}
			else
			{
				String[] rta = reqType.split(",");
				reqTypeArray = new String[rta.length][];
				for (int i = 0; i < rta.length; i++)
				{
					reqTypeArray[i] = rta[i].split("\\.");
				}
			}

			languageSkill.addAssociatedTo(selectedLangNames);

			for (String aString : selectedLangNames)
			{
				Language aLang = Globals.getContext().ref
						.silentlyGetConstructedCDOMObject(Language.class,
								aString);

				if (aLang == null)
				{
					continue;
				}
				if (reqTypeArray == null)
				{
					selected.add(aLang);
					continue;
				}
				SELARRAY: for (String[] types : reqTypeArray)
				{
					for (String type : types)
					{
						if (!aLang.isType(type))
						{
							continue SELARRAY;
						}
					}
					selected.add(aLang);
				}
			}

			for (Language lang : Globals.getContext().ref
					.getConstructedCDOMObjects(Language.class))
			{
				if (!PrereqHandler.passesAll(lang.getPreReqList(), aPC, lang))
				{
					continue;
				}
				if (reqTypeArray == null)
				{
					available.add(lang);
					continue;
				}
				AVARRAY: for (String[] types : reqTypeArray)
				{
					for (String type : types)
					{
						if (!lang.isType(type))
						{
							continue AVARRAY;
						}
					}
					available.add(lang);
				}
			}

			//
			// Do not give choice of automatic languages
			//
			for (Language lang : aPC.getAutoLanguages())
			{
				available.remove(lang);
				excludedLangs.add(lang);
			}

			//
			// Do not give choice of selected bonus languages
			//
			for (Language lang : aPC.getLanguagesList())
			{
				if (!selected.contains(lang))
				{
					if ((reqType == null || lang.isType(reqType)))
					{
						available.remove(lang);
					}
					excludedLangs.add(lang);
				}
			}

			Globals.sortChooserLists(available, selected);

			ChooserInterface lc = ChooserFactory.getChooserInstance();
			lc.setVisible(false);
			lc.setAvailableList(available);
			lc.setSelectedList(selected);
			lc.setTotalChoicesAvail(numLanguages);
			lc.setPoolFlag(false);
			lc.setVisible(true);

			aPC.clearLanguages();
			aPC.addLanguages(selected);

			// Add in all choice-excluded languages
			aPC.addLanguages(excludedLangs);
			languageSkill.clearAssociated();
			// TODO Fix this to allow Language objects.
			for (Iterator<?> i = lc.getSelectedList().iterator(); i.hasNext();)
			{
				languageSkill.addAssociated(((Language) i.next()).getKeyName());
			}
			aPC.setDirty(true);

			return true;
		}

		return false;
	}

}
