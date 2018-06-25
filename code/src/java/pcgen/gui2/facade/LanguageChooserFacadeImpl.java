/**
 * Copyright James Dempsey, 2010
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
 */
package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.display.CharacterDisplay;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.core.LanguageChooserFacade;
import pcgen.facade.core.LanguageFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;

/**
 * The Class {@code LanguageChooserFacadeImpl} is an implementation of the
 * LanguageChooserFacade for the gui2 package. It is responsible for managing 
 * details of a possible selection of languages. 
 *
 * 
 */
public final class LanguageChooserFacadeImpl implements LanguageChooserFacade
{
	private final PlayerCharacter theCharacter;
	private final CharacterDisplay charDisplay;
	private final ChooseDriver source;
	private final String name;
	private final DefaultListFacade<LanguageFacade> availableList;
	private final DefaultListFacade<LanguageFacade> selectedList;
	private final DefaultListFacade<LanguageFacade> originalSelectedList;
	private final DefaultReferenceFacade<Integer> numSelectionsRemain;
	private final CharacterFacadeImpl pcFacade;
	
	/**
	 * Create a new LanguageChooserFacadeImpl. This is initially empty but will be 
	 * populated upon the available list being requested. Called commit or rollback 
	 * ends the 'transaction'.
	 *  
	 * @param pcFacade The character facade managing this chooser facade.
	 * @param name The name of the chooser
	 * @param source The source of the languages list, null for racial bionus languages.
	 */
	public LanguageChooserFacadeImpl(CharacterFacadeImpl pcFacade, String name, ChooseDriver source)
	{
		this.pcFacade = pcFacade;
		this.theCharacter = pcFacade.getTheCharacter();
		this.charDisplay = theCharacter.getDisplay();
		this.name = name;
		this.source = source;
		
		availableList = new DefaultListFacade<>();
		selectedList = new DefaultListFacade<>();
		originalSelectedList = new DefaultListFacade<>();
		numSelectionsRemain = new DefaultReferenceFacade<>(0);
	}
	
	/**
	 * Populate the lists of available and selected languages ready for use by a chooser. 
	 */
	private void buildLanguageList()
	{
		if (!(source instanceof Skill))
		{
			buildBonusLangList();
		}
		else
		{
			buildObjectLangList();
		}
	}

	/**
	 * Build up the language lists for a choice of racial bonus languages.
	 */
	private void buildBonusLangList()
	{
		CNAbility cna = theCharacter.getBonusLanguageAbility();
		Ability a = cna.getAbility();

		List<Language> availLangs = new ArrayList<>();
		ChooseInformation<Language> chooseInfo =
				(ChooseInformation<Language>) a.get(ObjectKey.CHOOSE_INFO);
		availLangs.addAll(chooseInfo.getSet(theCharacter));

		List<? extends Language> selLangs =
				chooseInfo.getChoiceActor().getCurrentlySelected(cna,
					theCharacter);
		if (selLangs == null)
		{
			selLangs = Collections.emptyList();
		}

		availLangs.removeAll(charDisplay.getLanguageSet());
		refreshLangListContents(availLangs, availableList);
		refreshLangListContents(selLangs, selectedList);
		refreshLangListContents(selLangs, originalSelectedList);
		
		boolean allowBonusLangAfterFirst = Globals.checkRule(RuleConstants.INTBONUSLANG);
		boolean atFirstLvl = theCharacter.getTotalLevels() <= 1;
		if (allowBonusLangAfterFirst || atFirstLvl)
		{
			int bonusLangMax = theCharacter.getBonusLanguageCount();
			numSelectionsRemain.set(bonusLangMax-selLangs.size());
		}
		else
		{
			numSelectionsRemain.set(0);
		}
	}

	/**
	 * Build up the language lists for a choice of languages linked to a rules 
	 * object. e.g. The speak language skill.
	 */
	private void buildObjectLangList()
	{
		final List<Language> availLangs = new ArrayList<>();
		ChooseInformation<Language> chooseInfo =
				(ChooseInformation<Language>) source.getChooseInfo();
		availLangs.addAll(chooseInfo.getSet(theCharacter));

		List<? extends Language> selLangs =
				chooseInfo.getChoiceActor().getCurrentlySelected(source,
					theCharacter);
		if (selLangs == null)
		{
			selLangs = new ArrayList<>();
		}
		
		Set<Language> languageSet = charDisplay.getLanguageSet();
		availLangs.removeAll(languageSet);
		refreshLangListContents(availLangs, availableList);
		refreshLangListContents(selLangs, selectedList);
		refreshLangListContents(selLangs, originalSelectedList);
		
		int numSelections = 0;
		if (source instanceof Skill)
		{
			numSelections =
					SkillRankControl.getTotalRank(theCharacter, (Skill) source)
						.intValue()
						- selectedList.getSize();
		}
		else
		{
			ChoiceManagerList<Language> aMan =
					ChooserUtilities.getConfiguredController(source,
						theCharacter, null, new ArrayList<>());
			numSelections =
					aMan.getNumEffectiveChoices(selLangs,
                            new ArrayList<>(), theCharacter);
		}
		numSelectionsRemain.set(numSelections);
	}

	/**
	 * Replace the contents of a list facade with the given languages.
	 *  
	 * @param langList The source list of languages
	 * @param langListFacade The list facade to be populated
	 */
	private void refreshLangListContents(List<? extends Language> langList, DefaultListFacade<LanguageFacade> langListFacade)
	{
		Collections.sort(langList);
		langListFacade.clearContents();
		for (Language language : langList)
		{
			langListFacade.addElement(language);
		}
	}

	/**
	 * @see pcgen.facade.core.LanguageChooserFacade#addSelected(LanguageFacade)
	 */
	@Override
	public void addSelected(LanguageFacade language)
	{
		if (numSelectionsRemain.get() <= 0)
		{
			return;
		}
		selectedList.addElement(language);
		availableList.removeElement(language);
		numSelectionsRemain.set(numSelectionsRemain.get()-1);
	}

	/**
	 * @see pcgen.facade.core.LanguageChooserFacade#removeSelected(LanguageFacade)
	 */
	@Override
	public void removeSelected(LanguageFacade language)
	{
		selectedList.removeElement(language);
		availableList.addElement(language);
		numSelectionsRemain.set(numSelectionsRemain.get()+1);
	}

	/**
	 * @see pcgen.facade.core.LanguageChooserFacade#getAvailableList()
	 */
	@Override
	public ListFacade<LanguageFacade> getAvailableList()
	{
		buildLanguageList();
		return availableList;
	}

	/**
	 * @see pcgen.facade.core.LanguageChooserFacade#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * @see pcgen.facade.core.LanguageChooserFacade#getRemainingSelections()
	 */
	@Override
	public ReferenceFacade<Integer> getRemainingSelections()
	{
		return numSelectionsRemain;
	}

	/**
	 * @see pcgen.facade.core.LanguageChooserFacade#getSelectedList()
	 */
	@Override
	public ListFacade<LanguageFacade> getSelectedList()
	{
		return selectedList;
	}

	/**
	 * @see pcgen.facade.core.LanguageChooserFacade#commit()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void commit()
	{
		ChoiceManagerList<Language> choiceManager = ChooserUtilities.getChoiceManager(source, theCharacter);
		
		List<Language> selected = new ArrayList<>(selectedList.getSize());
		for (LanguageFacade langFacade : selectedList)
		{
			selected.add((Language) langFacade);
		}
		choiceManager.applyChoices(theCharacter, selected);

		// Update list on character facade
		pcFacade.refreshLanguageList();
	}

	/**
	 * @see pcgen.facade.core.LanguageChooserFacade#rollback()
	 */
	@Override
	public void rollback()
	{
		buildLanguageList();
	}

}
