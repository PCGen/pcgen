/**
 * LanguageChooserFacadeImpl.java
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
 *
 * Created on 15/07/2010 4:08:09 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.facade.DefaultReferenceFacade;
import pcgen.core.facade.LanguageChooserFacade;
import pcgen.core.facade.LanguageFacade;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;

/**
 * The Class <code>LanguageChooserFacadeImpl</code> is an implementation of the 
 * LanguageChooserFacade for the gui2 package. It is responsible for managing 
 * details of a possible selection of languages. 
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public final class LanguageChooserFacadeImpl implements LanguageChooserFacade
{
	private PlayerCharacter theCharacter;
	private CDOMObject source;
	private String name;
	private DefaultListFacade<LanguageFacade> availableList;
	private DefaultListFacade<LanguageFacade> selectedList;
	private DefaultListFacade<LanguageFacade> originalSelectedList;
	private DefaultReferenceFacade<Integer> numSelectionsRemain;
	private CharacterFacadeImpl pcFacade;
	
	/**
	 * Create a new LanguageChooserFacadeImpl. This is initially empty but will be 
	 * populated upon the available list being requested. Called commit or rollback 
	 * ends the 'transaction'.
	 *  
	 * @param pcFacade The character facade managing this chooser facade.
	 * @param name The name of the chooser
	 * @param source The source of the languages list, null for racial bionus languages.
	 */
	public LanguageChooserFacadeImpl(CharacterFacadeImpl pcFacade, String name, CDOMObject source)
	{
		this.pcFacade = pcFacade;
		this.theCharacter = pcFacade.getTheCharacter();
		this.name = name;
		this.source = source;
		
		availableList = new DefaultListFacade<LanguageFacade>();
		selectedList = new DefaultListFacade<LanguageFacade>();
		originalSelectedList = new DefaultListFacade<LanguageFacade>();
		numSelectionsRemain = new DefaultReferenceFacade<Integer>(0);
	}
	
	/**
	 * Populate the lists of available and selected languages ready for use by a chooser. 
	 */
	private void buildLanguageList()
	{
		if (source == null || !(source instanceof Skill ))
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
		List<Language> availLangs = new ArrayList<Language>();
		List<Language> selLangs = new ArrayList<Language>();
		
		Ability a = Globals.getContext().ref
			.silentlyGetConstructedCDOMObject(Ability.class,
					AbilityCategory.LANGBONUS, "*LANGBONUS");

		ChooserUtilities.modChoices(a, availLangs,
			selLangs, false, theCharacter, false,
				AbilityCategory.LANGBONUS);
		
		refreshLangListContents(availLangs, availableList);
		refreshLangListContents(selLangs, selectedList);
		refreshLangListContents(selLangs, originalSelectedList);
		
		int bonusLangMax = theCharacter.getBonusLanguageCount();
		numSelectionsRemain.setReference(bonusLangMax-selLangs.size());
	}

	/**
	 * Build up the language lists for a choice of languages linked to a rules 
	 * object. e.g. The speak language skill.
	 */
	@SuppressWarnings("unchecked")
	private void buildObjectLangList()
	{
		final List<Language> availLangs = new ArrayList<Language>();
		final List<Language> selLangs  = new ArrayList<Language>();

		ChooserUtilities.modChoices(source, availLangs,
			selLangs, false, theCharacter, false, null);
		
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
			ChoiceManagerList aMan = ChooserUtilities.getConfiguredController((PObject) source, theCharacter, null, new ArrayList<String>());
			numSelections = aMan.getNumEffectiveChoices(selLangs, new ArrayList<String>());
		}
		numSelectionsRemain.setReference(numSelections);
	}

	/**
	 * Replace the contents of a list facade with the given languages.
	 *  
	 * @param langList The source list of languages
	 * @param langListFacade The list facade to be populated
	 */
	private void refreshLangListContents(List<Language> langList, DefaultListFacade<LanguageFacade> langListFacade)
	{
		Collections.sort(langList);
		langListFacade.clearContents();
		for (Language language : langList)
		{
			langListFacade.addElement(language);
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.LanguageChooserFacade#addSelected(pcgen.core.facade.LanguageFacade)
	 */
	@Override
	public void addSelected(LanguageFacade language)
	{
		if (numSelectionsRemain.getReference() <= 0)
		{
			return;
		}
		selectedList.addElement(language);
		availableList.removeElement(language);
		numSelectionsRemain.setReference(numSelectionsRemain.getReference()-1);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.LanguageChooserFacade#removeSelected(pcgen.core.facade.LanguageFacade)
	 */
	@Override
	public void removeSelected(LanguageFacade language)
	{
		selectedList.removeElement(language);
		availableList.addElement(language);
		numSelectionsRemain.setReference(numSelectionsRemain.getReference()+1);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.LanguageChooserFacade#getAvailableList()
	 */
	@Override
	public ListFacade<LanguageFacade> getAvailableList()
	{
		buildLanguageList();
		return availableList;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.LanguageChooserFacade#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.LanguageChooserFacade#getRemainingSelections()
	 */
	@Override
	public ReferenceFacade<Integer> getRemainingSelections()
	{
		return numSelectionsRemain;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.LanguageChooserFacade#getSelectedList()
	 */
	@Override
	public ListFacade<LanguageFacade> getSelectedList()
	{
		return selectedList;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.LanguageChooserFacade#commit()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void commit()
	{
		ChoiceManagerList<Language> choiceManager = ChooserUtilities.getChoiceManager(source, theCharacter);
		
		List<Language> selected = new ArrayList<Language>(selectedList.getSize());
		for (LanguageFacade langFacade : selectedList)
		{
			selected.add((Language) langFacade);
		}
		choiceManager.applyChoices(theCharacter, selected);

		// Update list on character facade
		pcFacade.refreshLanguageList();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.LanguageChooserFacade#rollback()
	 */
	@Override
	public void rollback()
	{
		buildLanguageList();
	}

}
