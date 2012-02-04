/*
 * CharacterFacadeImpl.java
 * Copyright 2009 (C) James Dempsey
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
 * Created on 12/05/2009 6:43:46 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.undo.UndoManager;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.EquipmentLocation;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AgeSet;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.QualifiedObject;
import pcgen.core.Race;
import pcgen.core.RollingMethods;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.SimpleFacadeImpl;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.core.analysis.DomainApplication;
import pcgen.core.analysis.StatAnalysis;
import pcgen.core.character.EquipSet;
import pcgen.core.facade.AbilityCategoryFacade;
import pcgen.core.facade.AbilityFacade;
import pcgen.core.facade.AlignmentFacade;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.CharacterLevelFacade;
import pcgen.core.facade.CharacterLevelsFacade;
import pcgen.core.facade.CharacterLevelsFacade.CharacterLevelEvent;
import pcgen.core.facade.CharacterLevelsFacade.HitPointListener;
import pcgen.core.facade.ClassFacade;
import pcgen.core.facade.DataSetFacade;
import pcgen.core.facade.DefaultReferenceFacade;
import pcgen.core.facade.DeityFacade;
import pcgen.core.facade.DescriptionFacade;
import pcgen.core.facade.DomainFacade;
import pcgen.core.facade.EquipmentFacade;
import pcgen.core.facade.EquipmentListFacade;
import pcgen.core.facade.EquipmentListFacade.EquipmentListEvent;
import pcgen.core.facade.EquipmentListFacade.EquipmentListListener;
import pcgen.core.facade.EquipmentSetFacade;
import pcgen.core.facade.GenderFacade;
import pcgen.core.facade.InfoFacade;
import pcgen.core.facade.InfoFactory;
import pcgen.core.facade.LanguageChooserFacade;
import pcgen.core.facade.LanguageFacade;
import pcgen.core.facade.RaceFacade;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.SimpleFacade;
import pcgen.core.facade.SkillFacade;
import pcgen.core.facade.SpellSupportFacade;
import pcgen.core.facade.StatFacade;
import pcgen.core.facade.TempBonusFacade;
import pcgen.core.facade.TemplateFacade;
import pcgen.core.facade.TodoFacade;
import pcgen.core.facade.TodoFacade.CharacterTab;
import pcgen.core.facade.UIDelegate;
import pcgen.core.facade.event.ChangeListener;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.ListFacades;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.utils.CoreUtility;
import pcgen.gui.EQFrame;
import pcgen.io.ExportHandler;
import pcgen.io.PCGIOHandler;
import pcgen.io.exporttoken.WeightToken;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;
import pcgen.util.enumeration.Load;

/**
 * The Class <code>CharacterFacadeImpl</code> is an implementation of 
 * the CharacterFacade interface for the new user interface. It is 
 * intended to provide a full implementation of the new ui/core 
 * interaction layer.
 * <p>
 * <b>Issues needing resolution:</b>
 * <ul>
 * <li>Who is responsible for undo management and how will it work?</li>
 * </ul>
 * <br/>
 * Last Editor: $Author$ <br/>
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class CharacterFacadeImpl implements CharacterFacade,
		EquipmentListListener, ListListener<EquipmentFacade>, HitPointListener
{

	private List<ClassFacade> pcClasses;
	private DefaultListFacade<TempBonusFacade> appliedTempBonuses;
	private DefaultReferenceFacade<AlignmentFacade> alignment;
	private DefaultListFacade<EquipmentSetFacade> equipmentSets;
	private DefaultReferenceFacade<GenderFacade> gender;
	private DefaultListFacade<CharacterLevelFacade> pcClassLevels;
	private Map<StatFacade, DefaultReferenceFacade<Integer>> statScoreMap;
	private UndoManager undoManager;
	private DataSetFacade dataSet;
	private DefaultReferenceFacade<RaceFacade> race;
	private DefaultReferenceFacade<DeityFacade> deity;
	private DefaultReferenceFacade<String> tabName;
	private DefaultReferenceFacade<String> name;
	private DefaultReferenceFacade<String> playersName;
	private PlayerCharacter theCharacter;
	private DefaultReferenceFacade<EquipmentSetFacade> equipSet;
	private DefaultListFacade<LanguageFacade> languages;
	private EquipmentListFacadeImpl purchasedEquip;
	private DefaultReferenceFacade<File> file;
	private DefaultReferenceFacade<SimpleFacade> handedness;
	private UIDelegate delegate;
	private Set<Language> autoLanguagesCache;
	private CharacterLevelsFacadeImpl charLevelsFacade;
	private DefaultReferenceFacade<Integer> currentXP;
	private DefaultReferenceFacade<Integer> xpForNextlevel;
	private DefaultReferenceFacade<Integer> age;
	private DefaultReferenceFacade<SimpleFacade> ageCategory;
	private DefaultListFacade<SimpleFacade> ageCategoryList;
	private DefaultReferenceFacade<String> poolPointText;
	private DefaultReferenceFacade<String> statTotalLabelText;
	private DefaultReferenceFacade<String> statTotalText;
	private DefaultReferenceFacade<String> modTotalLabelText;
	private DefaultReferenceFacade<String> modTotalText;
	private DefaultReferenceFacade<Integer> numBonusLang;
	private DefaultReferenceFacade<Integer> numSkillLang;
	private DefaultReferenceFacade<Integer> hpRef;
	private DefaultReferenceFacade<Integer> rollMethodRef;
	private DefaultReferenceFacade<String> carriedWeightRef;
	private DefaultReferenceFacade<String> loadRef;
	private DefaultReferenceFacade<String> weightLimitRef;
	private DefaultListFacade<DomainFacade> domains;
	private DefaultListFacade<DomainFacade> availDomains;
	private DefaultReferenceFacade<Integer> maxDomains;
	private DefaultReferenceFacade<Integer> remainingDomains;
	private DefaultListFacade<TemplateFacade> templates;
	private DefaultListFacade<RaceFacade> raceList;
	private DefaultReferenceFacade<File> portrait;
	private RectangleReference cropRect;
	private String selectedGender;
	private List<Language> availableBonusLangs;
	private List<Language> currBonusLangs;
	private DefaultReferenceFacade<String> skinColor;
	private DefaultReferenceFacade<String> hairColor;
	private DefaultReferenceFacade<Integer> heightRef;
	private DefaultReferenceFacade<Integer> weightRef;

	private Gui2InfoFactory infoFactory;
	private CharacterAbilities characterAbilities;
	private DescriptionFacade descriptionFacade;
	private SpellSupportFacadeImpl spellSupportFacade;
	private TodoManager todoManager;

	/**
	 * Create a new character. The character will be blank with no race, 
	 * stats etc
	 * 
	 * @param delegate the UIDelegate for this CharacterFacade
	 * @param dataSetFacade The data set in use for the character
	 */
	public CharacterFacadeImpl(UIDelegate delegate, DataSetFacade dataSetFacade)
	{
		this(null, delegate, dataSetFacade);
	}

	/**
	 * Create a new character facade for an existing character.
	 * 
	 * @param pc The character to be represented
	 * @param delegate the UIDelegate for this CharacterFacade
	 * @param dataSetFacade The data set in use for the character
	 */
	public CharacterFacadeImpl(PlayerCharacter pc, UIDelegate delegate, DataSetFacade dataSetFacade)
	{
		this.delegate = delegate;
		if (pc == null)
		{
			@SuppressWarnings("rawtypes")
			List campaigns = ListFacades.wrap(dataSetFacade.getCampaigns());		
			theCharacter = new PlayerCharacter(false, campaigns);
		}
		else
		{
			theCharacter = pc;
		}
		dataSet = dataSetFacade;
		buildAgeCategories();
		initForCharacter(theCharacter);
		undoManager = new UndoManager();
	}

	/**
	 * 
	 */
	private void initForCharacter(PlayerCharacter pc)
	{
		todoManager = new TodoManager();

		infoFactory = new Gui2InfoFactory(pc);
		characterAbilities = new CharacterAbilities(pc, delegate, dataSet, todoManager);
		descriptionFacade = new DescriptionFacadeImpl(pc);
		spellSupportFacade = new SpellSupportFacadeImpl(pc, delegate, dataSet);
		
		//TODO: Init appliedTempBonuses
		appliedTempBonuses = new DefaultListFacade<TempBonusFacade>();
		
		statScoreMap = new HashMap<StatFacade, DefaultReferenceFacade<Integer>>();

		File portraitFile = null;
		if (!StringUtils.isEmpty(theCharacter.getPortraitPath()))
		{
			portraitFile = new File(theCharacter.getPortraitPath());
		}
		portrait = new DefaultReferenceFacade<File>(portraitFile);
		cropRect = new RectangleReference(theCharacter.getPortraitThumbnailRect());
		
		tabName = new DefaultReferenceFacade<String>(pc.getTabName());
		name = new DefaultReferenceFacade<String>(pc.getName());
		playersName = new DefaultReferenceFacade<String>(pc.getPlayersName());
		race = new DefaultReferenceFacade<RaceFacade>(pc.getRace());
		raceList = new DefaultListFacade<RaceFacade>();
		if (pc.getRace() != null && pc.getRace() != Globals.s_EMPTYRACE)
		{
			raceList.addElement(pc.getRace());
		}
		handedness = new DefaultReferenceFacade<SimpleFacade>();
		gender = new DefaultReferenceFacade<GenderFacade>();
		if (pc.getRace() != null)
		{
			for (SimpleFacade handsFacade : pc.getRace().getHands())
			{
				if (handsFacade.toString().equals(pc.getHanded()))
				{
					handedness.setReference(handsFacade);
					break;
				}
			}
			for (GenderFacade pcGender : race.getReference().getGenders())
			{
				if (pcGender.equals(pc.getGenderObject()))
				{
					gender.setReference(pcGender);
					break;
				}
			}
		}

		alignment = new DefaultReferenceFacade<AlignmentFacade>(pc.getPCAlignment());
		file = new DefaultReferenceFacade<File>(new File(pc.getFileName()));
		age = new DefaultReferenceFacade<Integer>(pc.getAge());
		ageCategory = new DefaultReferenceFacade<SimpleFacade>();
		updateAgeCategoryForAge();
		currentXP = new DefaultReferenceFacade<Integer>(pc.getXP());
		xpForNextlevel = new DefaultReferenceFacade<Integer>(pc.minXPForNextECL());
		hpRef = new DefaultReferenceFacade<Integer>(pc.hitPoints());

		skinColor = new DefaultReferenceFacade<String>(pc.getSkinColor());
		hairColor = new DefaultReferenceFacade<String>(pc.getHairColor());
		weightRef = new DefaultReferenceFacade<Integer>(pc.getWeight());
		heightRef = new DefaultReferenceFacade<Integer>(pc.getHeight());
		
		purchasedEquip = new EquipmentListFacadeImpl(pc.getEquipmentMasterList());
		carriedWeightRef = new DefaultReferenceFacade<String>();
		loadRef = new DefaultReferenceFacade<String>();
		weightLimitRef = new DefaultReferenceFacade<String>();
		initEquipSet(pc);

		GameMode game = (GameMode) dataSet.getGameMode();
		rollMethodRef = new DefaultReferenceFacade<Integer>(game.getRollMethod());
		
		charLevelsFacade = new CharacterLevelsFacadeImpl(pc, delegate,todoManager);
		pcClasses = new ArrayList<ClassFacade>();
		pcClassLevels = new DefaultListFacade<CharacterLevelFacade>();
		refreshClassLevelModel();
		charLevelsFacade.addHitPointListener(this);

		deity = new DefaultReferenceFacade<DeityFacade>(pc.getDeity());
		domains = new DefaultListFacade<DomainFacade>();
		maxDomains = new DefaultReferenceFacade<Integer>(pc.getMaxCharacterDomains());
		remainingDomains =
				new DefaultReferenceFacade<Integer>(pc.getMaxCharacterDomains()
					- domains.getSize());
		availDomains = new DefaultListFacade<DomainFacade>();
		buildAvailableDomainsList();
		
		templates = new DefaultListFacade<TemplateFacade>(theCharacter.getTemplateSet());
		
		initTodoList();
		
		statTotalLabelText = new DefaultReferenceFacade<String>();
		statTotalText = new DefaultReferenceFacade<String>();
		modTotalLabelText = new DefaultReferenceFacade<String>();
		modTotalText = new DefaultReferenceFacade<String>();
		updateScorePurchasePool(false);

		languages = new DefaultListFacade<LanguageFacade>();
		numBonusLang = new DefaultReferenceFacade<Integer>(0);
		numSkillLang = new DefaultReferenceFacade<Integer>(0);
		refreshLanguageList();

		purchasedEquip.addListListener(spellSupportFacade);
		purchasedEquip.addEquipmentListListener(spellSupportFacade);
		
	}

	/**
	 * Initialise the equipment set facades, ensuring that the character has a 
	 * default equipment set. 
	 * @param pc The character being loaded
	 */
	private void initEquipSet(PlayerCharacter pc)
	{
		equipSet = new DefaultReferenceFacade<EquipmentSetFacade>();
		
		// Setup the default EquipSet if not already present
		if (pc.getEquipSet().size() == 0)
		{
			String id = EquipmentSetFacadeImpl.getNewIdPath(pc, null);
			EquipSet eSet = new EquipSet(id, LanguageBundle.getString("in_ieDefault"));
			pc.addEquipSet(eSet);
			pc.setCalcEquipSetId(id);
		}

		// Make facades for each root equipset.
		equipmentSets = new DefaultListFacade<EquipmentSetFacade>();
		String currIdPath = pc.getCalcEquipSetId();
		for (EquipSet es : pc.getEquipSet())
		{
			if (es.getParentIdPath().equals("0"))
			{
				final EquipmentSetFacadeImpl facade =
						new EquipmentSetFacadeImpl(delegate, pc, es, dataSet);
				equipmentSets.addElement(facade);
				if (es.getIdPath().equals(currIdPath))
				{
					equipSet.setReference(facade);
				}
			}
		}

		EquipmentSetFacade set = equipSet.getReference();
		set.getEquippedItems().addListListener(this);
		set.getEquippedItems().addEquipmentListListener(this);
		refreshTotalWeight();
		
	}

	/**
	 * Create the list of known age categories in the current BioSet. 
	 */
	private void buildAgeCategories()
	{
		List<String> cats = new ArrayList<String>();
		for (String aString : Globals.getBioSet().getAgeCategories())
		{
			final int idx = aString.indexOf('\t');

			if (idx >= 0)
			{
				aString = aString.substring(0, idx);
			}

			if (!cats.contains(aString))
			{
				cats.add(aString);
			}
		}
		Collections.sort(cats);
		ageCategoryList = new DefaultListFacade<SimpleFacade>();
		for (String ageCat : cats)
		{
			ageCategoryList.addElement(new SimpleFacadeImpl(ageCat));
		}
	}

	/**
	 * Create an initial list of todo items 
	 */
	private void initTodoList()
	{
		if (isNewCharName(theCharacter.getName()))
		{
			todoManager.addTodo(new TodoFacadeImpl(CharacterTab.SummaryTab,
				"Name", "in_sumTodoName", 1));
		}
		if (theCharacter.getRace() == null
			|| Constants.NONESELECTED.equals(theCharacter.getRace()
				.getKeyName()))
		{
			todoManager.addTodo(new TodoFacadeImpl(CharacterTab.SummaryTab, "Race",
				"in_irTodoRace", 100));
		}
		
		// Stats todo already done in updateScorePurchasePool
		updateLevelTodo();
	}

	/**
	 * Identify if the supplied name is a default one generated by the system
	 * e.g. Unnamed 1 or Unnamed 2
	 * @param name The name to be checked.
	 * @return True if the name is a default.
	 */
	private boolean isNewCharName(String name)
	{
		if (name == null)
		{
			return true;
		}

		return name.startsWith("Unnamed"); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#addAbility(pcgen.core.facade.AbilityCategoryFacade, pcgen.core.facade.AbilityFacade)
	 */
	public void addAbility(AbilityCategoryFacade category, AbilityFacade ability)
	{
		characterAbilities.addAbility(category, ability);
		refreshLanguageList();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#removeAbility(pcgen.core.facade.AbilityCategoryFacade, pcgen.core.facade.AbilityFacade)
	 */
	public void removeAbility(AbilityCategoryFacade category,
							  AbilityFacade ability)
	{
		characterAbilities.removeAbility(category, ability);
		refreshLanguageList();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getAbilities(pcgen.core.facade.AbilityCategoryFacade)
	 */
	public ListFacade<AbilityFacade> getAbilities(
			AbilityCategoryFacade category)
	{
		return characterAbilities.getAbilities(category);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getActiveAbilityCategories()
	 */
	public ListFacade<AbilityCategoryFacade> getActiveAbilityCategories()
	{
		return characterAbilities.getActiveAbilityCategories();
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getRemainingSelections(pcgen.core.facade.AbilityCategoryFacade)
	 */
	public int getRemainingSelections(AbilityCategoryFacade category)
	{
		return characterAbilities.getRemainingSelections(category);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addAbilityCatSelectionListener(ChangeListener listener)
	{
		characterAbilities.addAbilityCatSelectionListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeAbilityCatSelectionListener(ChangeListener listener)
	{
		characterAbilities.removeAbilityCatSelectionListener(listener);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setRemainingSelection(pcgen.core.facade.AbilityCategoryFacade, int)
	 */
	public void setRemainingSelection(AbilityCategoryFacade category,
									  int remaining)
	{
		characterAbilities.setRemainingSelection(category, remaining);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#hasAbility(pcgen.core.facade.AbilityCategoryFacade, pcgen.core.facade.AbilityFacade)
	 */
	public boolean hasAbility(AbilityCategoryFacade category, AbilityFacade ability)
	{
		return characterAbilities.hasAbility(category, ability);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getAbilityNature(pcgen.core.facade.AbilityFacade)
	 */
	public Nature getAbilityNature(AbilityFacade ability)
	{
		if (ability == null || !(ability instanceof Ability))
		{
			return null;
		}
		return theCharacter.getAbilityNature((Ability) ability);
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#addCharacterLevels(pcgen.core.facade.ClassFacade[])
	 */
	public void addCharacterLevels(ClassFacade[] classes)
	{
		SettingsHandler.setShowHPDialogAtLevelUp(false);
		//SettingsHandler.setShowStatDialogAtLevelUp(false);

		int oldLevel = charLevelsFacade.getSize();
		
		for (ClassFacade classFacade : classes)
		{
			if (classFacade instanceof PCClass)
			{
				if (!validateAddLevel((PCClass) classFacade))
				{
					return;
				}
				theCharacter.incrementClassLevel(1, (PCClass) classFacade);
			}
			if (!pcClasses.contains(classFacade))
			{
				pcClasses.add(classFacade);
			}
			CharacterLevelFacadeImpl cl = new CharacterLevelFacadeImpl(classFacade, theCharacter, charLevelsFacade.getSize()+1);
			pcClassLevels.addElement(cl);
			charLevelsFacade.addLevelOfClass(cl);
		}
		postLevellingUpdates();
		delegate.showLevelUpInfo(this, oldLevel);
	}

	/**
	 * Ensure any items that could be affected by the level up or down are refreshed.
	 */
	private void postLevellingUpdates()
	{
		characterAbilities.rebuildAbilityLists();
		refreshLanguageList();
		currentXP.setReference(theCharacter.getXP());
		xpForNextlevel.setReference(theCharacter.minXPForNextECL());
		hpRef.setReference(theCharacter.hitPoints());
		age.setReference(theCharacter.getAge());

		updateLevelTodo();
		buildAvailableDomainsList();
		spellSupportFacade.refreshAvailableKnownSpells();
		updateScorePurchasePool(false);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#removeCharacterLevels(int)
	 */
	public void removeCharacterLevels(int levels)
	{
		for (int i = levels; i > 0 && !pcClassLevels.isEmpty(); i--)
		{
			ClassFacade classFacade =
					charLevelsFacade.getClassTaken(pcClassLevels
						.getElementAt(pcClassLevels.getSize() - 1));
			pcClassLevels.removeElement(pcClassLevels.getSize() - 1);
			if (classFacade instanceof PCClass)
			{
				theCharacter.incrementClassLevel(-1, (PCClass) classFacade);
			}
			charLevelsFacade.removeLastLevel();
		}

		// Clean up the class list 
		for (Iterator<ClassFacade> iterator = pcClasses.iterator(); iterator.hasNext();)
		{
			ClassFacade classFacade = iterator.next();
			boolean stillPresent = false;
			for (CharacterLevelFacade charLevel : pcClassLevels)
			{
				if (charLevelsFacade.getClassTaken(charLevel) == classFacade)
				{
					stillPresent = true;
					break;
				}
			}

			if (!stillPresent)
			{
				iterator.remove();
			}
		}
		postLevellingUpdates();
	}

	/**
	 * Update the todo list to reflect the change in level or experience.
	 */
	private void updateLevelTodo()
	{
		if (theCharacter.getXP() >= theCharacter.minXPForNextECL())
		{
			todoManager.addTodo(new TodoFacadeImpl(CharacterTab.SummaryTab, "Class",
				"in_clTodoLevelUp", 120));
		}
		else
		{
			todoManager.removeTodo("in_clTodoLevelUp");
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getClassLevel(pcgen.core.facade.ClassFacade)
	 */
	public int getClassLevel(ClassFacade c)
	{
		int clsLevel = 0;
		for (CharacterLevelFacade charLevel : pcClassLevels)
		{
			if (charLevelsFacade.getClassTaken(charLevel) == c)
			{
				clsLevel++;
			}
		}
		return clsLevel;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getLevels()
	 */
	public ListFacade<CharacterLevelFacade> getLevels()
	{
		return pcClassLevels;
	}

	private boolean validateAddLevel(PCClass theClass)
	{
		int levels = 1;

		if (theClass == null)
		{
			return false;
		}

		if (!theCharacter.isQualified(theClass))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle.getString("in_clYouAreNotQualifiedToTakeTheClass"));
			return false;
		}

		if (!theCharacter.canLevelUp())
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle.getString("in_Enforce_rejectLevelUp"));
			return false;
		}

		final PCClass aClass = theCharacter.getClassKeyed(theClass.getKeyName());

		// Check if the subclass (if any) is qualified for
		String subClassKey = theCharacter.getSubClassName(aClass);
		if (aClass != null && subClassKey != null)
		{
			final PCClass subClass =
					aClass.getSubClassKeyed(subClassKey);
			if (subClass != null && !theCharacter.isQualified(subClass))
			{
				delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle.getFormattedString(
						"in_sumYouAreNotQualifiedToTakeTheClass",//$NON-NLS-1$
						aClass.getDisplayName() + "/" + subClass.getDisplayName()));//$NON-NLS-1$
				return false;
			}
		}

		if (!Globals.checkRule(RuleConstants.LEVELCAP) && theClass.hasMaxLevel() &&
				((levels > theClass.getSafe(IntegerKey.LEVEL_LIMIT)) || ((aClass != null) &&
				((theCharacter.getLevel(aClass) + levels) > aClass.getSafe(IntegerKey.LEVEL_LIMIT)))))
		{
			delegate.showInfoMessage(Constants.APPLICATION_NAME, LanguageBundle.getFormattedString(
					"in_sumMaximumLevelIs", //$NON-NLS-1$
					String.valueOf(theClass.getSafe(IntegerKey.LEVEL_LIMIT))));
			return false;
		}

		// Check with the user on their first level up
		if (theCharacter.getTotalLevels() == 0)
		{
			if (SettingsHandler.getGame().isPurchaseStatMode() &&
					(theCharacter.getPointBuyPoints() > getUsedStatPool(theCharacter)))
			{
				if (!delegate.showWarningConfirm(LanguageBundle.getString("in_sumLevelWarnTitle"),//$NON-NLS-1$
												 LanguageBundle.getString("in_sumPoolWarning")))//$NON-NLS-1$
				{
					return false;
				}
			}
			else if (allAbilitiesAreZero())
			{
				if (!delegate.showWarningConfirm(LanguageBundle.getString("in_sumLevelWarnTitle"),
												 LanguageBundle.getString("in_sumAbilitiesZeroWarning")))
				{
					return false;
				}
			}
			else
			{
				Boolean proceed = delegate.maybeShowWarningConfirm(LanguageBundle.getString("in_sumLevelWarnTitle"),
																   LanguageBundle.getString("in_sumAbilitiesWarning"),
																   LanguageBundle.getString("in_sumAbilitiesWarningCheckBox"),
																   PCGenSettings.OPTIONS_CONTEXT,
																   PCGenSettings.OPTION_SHOW_WARNING_AT_FIRST_LEVEL_UP);
				if (Boolean.FALSE.equals(proceed))
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Determine if all of the character's stats are still set to 0.
	 *
	 * @return True if they are all zero, false if any are non-zero.
	 */
	private boolean allAbilitiesAreZero()
	{
		for (StatFacade stat : statScoreMap.keySet())
		{
			DefaultReferenceFacade<Integer> facade = statScoreMap.get(stat);

			if (facade.getReference() != 0)
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * This method gets the number of stat points used in the pool
	 * @param pc The PlayerCharacter to get used stat pool for
	 * @return used stat pool
	 */
	private static int getUsedStatPool(PlayerCharacter pc)
	{
		int i = 0;

		for (PCStat aStat : pc.getStatSet())
		{
			if (!aStat.getSafe(ObjectKey.ROLLED))
			{
				continue;
			}

			final int statValue = StatAnalysis.getBaseStatFor(pc, aStat);
			i += getPurchaseCostForStat(pc, statValue);
		}
		i += (int) pc.getTotalBonusTo("POINTBUY", "SPENT"); //$NON-NLS-1$ //$NON-NLS-2$
		return i;
	}

	private static int getPurchaseCostForStat(final PlayerCharacter aPC,
											  int statValue)
	{
		final int iMax = SettingsHandler.getGame().getPurchaseScoreMax(aPC);
		final int iMin = SettingsHandler.getGame().getPurchaseScoreMin(aPC);

		if (statValue > iMax)
		{
			statValue = iMax;
		}

		if (statValue >= iMin)
		{
			return SettingsHandler.getGame().getAbilityScoreCost(
					statValue - iMin);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getAvailableTempBonuses()
	 */
	public ListFacade<TempBonusFacade> getAvailableTempBonuses()
	{
		// TODO: this needs to be implemented
		return ListFacades.emptyList();
	}

	public void addTempBonus(TempBonusFacade bonus)
	{
		appliedTempBonuses.addElement(bonus);
		refreshLanguageList();
	}

	public void removeTempBonus(TempBonusFacade bonus)
	{
		appliedTempBonuses.removeElement(bonus);
		refreshLanguageList();
	}

	public ListFacade<TempBonusFacade> getTempBonuses()
	{
		return appliedTempBonuses;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#isTempBonusApplied(pcgen.core.facade.TempBonusFacade)
	 */
	public boolean isTempBonusApplied(TempBonusFacade bonus)
	{
		return appliedTempBonuses.containsElement(bonus);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getAlignmentRef()
	 */
	public ReferenceFacade<AlignmentFacade> getAlignmentRef()
	{
		return alignment;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setAlignment(pcgen.core.facade.AlignmentFacade)
	 */
	public void setAlignment(AlignmentFacade alignment)
	{
		if (!validateAlignmentChange(alignment))
		{
			return;
		}

		this.alignment.setReference(alignment);
		if (alignment instanceof PCAlignment)
		{
			theCharacter.setAlignment((PCAlignment) alignment);
		}
		refreshLanguageList();

	}

	/**
	 * Validate the new alignment matches those allowed for the character's 
	 * classes. If not offer the user a choice of backing out or making the 
	 * classes into ex-classes.
	 * 
	 * @param newAlign The alignment to be set
	 */
	private boolean validateAlignmentChange(AlignmentFacade newAlign)
	{
		AlignmentFacade oldAlign = this.alignment.getReference();

		if (oldAlign == null || newAlign.equals(oldAlign))
		{
			return true;
		}

		// We can't do any validation if the new alignment isn't a known class
		if (!(newAlign instanceof PCAlignment))
		{
			return true;
		}

		//
		// Get a list of classes that will become unqualified (and have an ex-class)
		//
		StringBuffer unqualified = new StringBuffer();
		List<PCClass> classList = theCharacter.getClassList();
		List<PCClass> exclassList = new ArrayList<PCClass>();
		PCAlignment savedAlignmnet = theCharacter.getPCAlignment();
		for (PCClass aClass : classList)
		{
			theCharacter.setAlignment((PCAlignment) newAlign);
			{
				if (!theCharacter.isQualified(aClass))
				{
					if (aClass.containsKey(ObjectKey.EX_CLASS))
					{
						if (unqualified.length() > 0)
						{
							unqualified.append(", "); //$NON-NLS-1$
						}

						unqualified.append(aClass.getKeyName());
						exclassList.add(aClass);
					}
				}
			}
		}

		//
		// Give the user a chance to bail
		//
		if (unqualified.length() > 0)
		{
			if (!delegate.showWarningConfirm(Constants.APPLICATION_NAME, LanguageBundle.getString("in_sumExClassesWarning") +
					Constants.LINE_SEPARATOR + unqualified))
			{
				theCharacter.setAlignment(savedAlignmnet);
				return false;
			}

		}

		//
		// Convert the class(es)
		//
		for (PCClass aClass : exclassList)
		{
			theCharacter.makeIntoExClass(aClass);
		}

		// Update the facade and UI
		refreshClassLevelModel();

		return true;
	}

	private void refreshClassLevelModel()
	{
		List<CharacterLevelFacade> newlevels = new ArrayList<CharacterLevelFacade>();
		List<PCClass> newClasses = theCharacter.getClassList();
		Collection<PCLevelInfo> levelInfo = theCharacter.getLevelInfo();

		Map<String, Integer> levelCount = new HashMap<String, Integer>();
		Map<String, PCClass> classMap = new HashMap<String, PCClass>();
		for (PCClass pcClass : newClasses)
		{
			levelCount.put(pcClass.getKeyName(), 0);
			classMap.put(pcClass.getKeyName(), pcClass);
		}

		for (PCLevelInfo lvlInfo : levelInfo)
		{
			final String classKeyName = lvlInfo.getClassKeyName();
			PCClass currClass = classMap.get(classKeyName);
			if (currClass == null)
			{
				Logging.errorPrint("No PCClass found for '" + classKeyName +
						"' in character's class list: " + newClasses);
				return;
			}

			int clsLvlNum = levelCount.get(classKeyName);
			levelCount.put(classKeyName, clsLvlNum + 1);
			//PCClassLevel classLevel = currClass.getClassLevel(clsLvlNum);
			CharacterLevelFacadeImpl cl =
					new CharacterLevelFacadeImpl(currClass, theCharacter,
						clsLvlNum + 1);
			newlevels.add(cl);
		}

		pcClasses.clear();
		pcClasses.addAll(newClasses);

		pcClassLevels.setContents(newlevels);
		// Now get the CharacterLevelsFacadeImpl to do a refresh too.
		charLevelsFacade.classListRefreshRequired();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getDataSet()
	 */
	public DataSetFacade getDataSet()
	{
		return dataSet;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getEquipmentSets()
	 */
	public ListFacade<EquipmentSetFacade> getEquipmentSets()
	{
		return equipmentSets;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getGenderRef()
	 */
	public ReferenceFacade<GenderFacade> getGenderRef()
	{
		return gender;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setGender(pcgen.cdom.enumeration.Gender)
	 */
	public void setGender(GenderFacade gender)
	{
		this.selectedGender = gender.toString();
		this.gender.setReference(gender);
		theCharacter.setGender((Gender) gender);
		refreshLanguageList();
	}

	public void setGender(String gender)
	{
		this.selectedGender = gender;
		if (theCharacter.getRace() != null)
		{
			for (GenderFacade raceGender : theCharacter.getRace().getGenders())
			{
				if (raceGender.toString().equals(gender))
				{
					setGender(raceGender);
				}
			}
		}
	}
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getModTotal(pcgen.core.facade.StatFacade)
	 */
	public int getModTotal(StatFacade stat)
	{
		if (stat instanceof PCStat && !theCharacter.isNonAbility((PCStat) stat))
		{
			return Integer.valueOf(StatAnalysis.getStatModFor(theCharacter,
				(PCStat) stat));
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getScoreTotalRef(pcgen.core.facade.StatFacade)
	 */
	public ReferenceFacade<Integer> getScoreBaseRef(StatFacade stat)
	{
		DefaultReferenceFacade<Integer> score = statScoreMap.get(stat);
		if (score == null)
		{
			score =
					new DefaultReferenceFacade<Integer>(StatAnalysis
						.getTotalStatFor(theCharacter, (PCStat) stat));
			statScoreMap.put(stat, score);
		}
		return score;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getScoreBase(pcgen.core.facade.StatFacade)
	 */
	public int getScoreBase(StatFacade stat)
	{
		if (!(stat instanceof PCStat))
		{
			return 0;
		}
		return StatAnalysis.getBaseStatFor(theCharacter, (PCStat) stat);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getScoreTotalString(pcgen.core.facade.StatFacade)
	 */
	public String getScoreTotalString(StatFacade stat)
	{
		if (!(stat instanceof PCStat))
		{
			return "";
		}
		if (theCharacter.isNonAbility((PCStat) stat))
		{
			return "*"; //$NON-NLS-1$
		}

		return SettingsHandler.getGame().getStatDisplayText(
			StatAnalysis.getTotalStatFor(theCharacter, (PCStat) stat));
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getScoreRaceBonus(pcgen.core.facade.StatFacade)
	 */
	public int getScoreRaceBonus(StatFacade stat)
	{
		if (!(stat instanceof PCStat))
		{
			return 0;
		}
		PCStat activeStat = (PCStat) stat;
		if (theCharacter.isNonAbility(activeStat))
		{
			return 0;
		}

		//return Integer.valueOf(currentStatAnalysis.getTotalStatFor(aStat) - currentStatAnalysis.getBaseStatFor(aStat));
		int rBonus =
				(int) theCharacter.getRaceBonusTo("STAT", activeStat.getAbb()); //$NON-NLS-1$
		rBonus += (int) theCharacter.getBonusDueToType("STAT", activeStat.getAbb(), "RACIAL");

		return rBonus;
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getScoreOtherBonus(pcgen.core.facade.StatFacade)
	 */
	public int getScoreOtherBonus(StatFacade stat)
	{
		if (!(stat instanceof PCStat))
		{
			return 0;
		}
		PCStat activeStat = (PCStat) stat;
		if (theCharacter.isNonAbility(activeStat))
		{
			return 0;
		}

		//return Integer.valueOf(currentStatAnalysis.getTotalStatFor(aStat) - currentStatAnalysis.getBaseStatFor(aStat));
		int iRace =
				(int) theCharacter.getRaceBonusTo("STAT", activeStat.getAbb()); //$NON-NLS-1$
		iRace += (int) theCharacter.getBonusDueToType("STAT", activeStat.getAbb(), "RACIAL");

		return StatAnalysis.getTotalStatFor(theCharacter, activeStat)
			- StatAnalysis.getBaseStatFor(theCharacter, activeStat) - iRace;
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setScoreBase(pcgen.core.facade.StatFacade, int)
	 */
	public void setScoreBase(StatFacade stat, int score)
	{
		DefaultReferenceFacade<Integer> facade = statScoreMap.get(stat);
		if (facade == null)
		{
			facade = new DefaultReferenceFacade<Integer>(score);
			statScoreMap.put(stat, facade);
		}

		PCStat pcStat = null;
		final int pcPlayerLevels = theCharacter.totalNonMonsterLevels();
		Collection<PCStat> pcStatList = theCharacter.getStatSet();
		for (PCStat aStat : pcStatList)
		{
			if (stat.getAbbreviation().equals(aStat.getAbbreviation()))
			{
				pcStat = aStat;
				break;
			}
		}
		if (pcStat == null)
		{
			Logging.errorPrint("Unexpected stat '" + stat
				+ "' found - ignoring.");
			return;
		}

		// Checking for bounds, locked stats and pool points
		String errorMsg = validateNewStatBaseScore(score, pcStat, pcPlayerLevels);
		if (StringUtils.isNotBlank(errorMsg))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, errorMsg);
			return;
		}

		final int baseScore =
				theCharacter.getAssoc(pcStat, AssociationKey.STAT_SCORE);
		// Deal with a point pool based game mode where you buy skills and feats as well as stats
		if (Globals.getGameModeHasPointPool())
		{
			if (pcPlayerLevels > 0)
			{
				int poolMod =
						getPurchaseCostForStat(theCharacter, score)
							- getPurchaseCostForStat(theCharacter, baseScore);
				//
				// Adding to stat
				//
				if (poolMod > 0)
				{
					if (poolMod > theCharacter.getSkillPoints())
					{
						delegate.showErrorMessage(Constants.APPLICATION_NAME,
							LanguageBundle.getFormattedString(
								"in_sumStatPoolEmpty", Globals //$NON-NLS-1$
									.getGameModePointPoolName()));
						return;
					}
				}
				else if (poolMod < 0)
				{
					if (theCharacter.getStatIncrease(pcStat, true) < Math
						.abs(score - baseScore))
					{
						delegate
							.showErrorMessage(Constants.APPLICATION_NAME,
								LanguageBundle
									.getString("in_sumStatStartedHigher")); //$NON-NLS-1$
						return;
					}
				}

				theCharacter.adjustFeats(-poolMod);
				showPointPool();
			}
		}

		theCharacter.setAssoc(pcStat, AssociationKey.STAT_SCORE, score);
		facade.setReference(score);
		theCharacter.saveStatIncrease(pcStat, score - baseScore, false);
		hpRef.setReference(theCharacter.hitPoints());

		updateScorePurchasePool(true);		
	}

	/**
	 * Assess if the new score is valid for the stat.
	 * 
	 * @param score The new score being checked.
	 * @param pcStat The stats being checked
	 * @param pcPlayerLevels The number of non moster levels the character currently has.
	 * @return An error message if the score is not valid.
	 */
	private String validateNewStatBaseScore(int score, PCStat pcStat,
		final int pcPlayerLevels)
	{
		if (theCharacter.isNonAbility(pcStat))
		{
			return LanguageBundle.getString("in_sumCannotModifyANonAbility"); //$NON-NLS-1$
		}
		else if (score < pcStat.getSafe(IntegerKey.MIN_VALUE))
		{
			return LanguageBundle.getFormattedString(
				"in_sumCannotLowerStatBelow", SettingsHandler.getGame() //$NON-NLS-1$
					.getStatDisplayText(pcStat.getSafe(IntegerKey.MIN_VALUE)));
		}
		else if (score > pcStat.getSafe(IntegerKey.MAX_VALUE))
		{
			return LanguageBundle.getFormattedString(
				"in_sumCannotRaiseStatAbove", SettingsHandler.getGame() //$NON-NLS-1$
					.getStatDisplayText(pcStat.getSafe(IntegerKey.MAX_VALUE)));
		}
		else if ((pcPlayerLevels < 2)
			&& SettingsHandler.getGame().isPurchaseStatMode())
		{
			final int maxPurchaseScore =
					SettingsHandler.getGame().getPurchaseScoreMax(theCharacter);

			if (score > maxPurchaseScore)
			{
				return LanguageBundle.getFormattedString(
					"in_sumCannotRaiseStatAbovePurchase", SettingsHandler //$NON-NLS-1$
						.getGame().getStatDisplayText(maxPurchaseScore));
			}

			final int minPurchaseScore =
					SettingsHandler.getGame().getPurchaseScoreMin(
						theCharacter);

			if (score < minPurchaseScore)
			{
				return LanguageBundle.getFormattedString(
					"in_sumCannotLowerStatBelowPurchase", SettingsHandler //$NON-NLS-1$
						.getGame().getStatDisplayText(minPurchaseScore));
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#rollStats()
	 */
	public void rollStats()
	{
		GameMode game = (GameMode) dataSet.getGameMode();
		if (game.getCurrentRollingMethod() == null)
		{
			return;
		}
		theCharacter.rollStats(Constants.CHARACTER_STAT_METHOD_ROLLED);
		//XXX This is here to stop the stat mod from being stale. Can be removed once we merge with CDOM
		theCharacter.calcActiveBonuses();
		for (StatFacade stat : statScoreMap.keySet())
		{
			DefaultReferenceFacade<Integer> score = statScoreMap.get(stat);
			if (stat instanceof PCStat)
			{
				score.setReference(StatAnalysis.getTotalStatFor(theCharacter,
					(PCStat) stat));
			}
		}
		updateScorePurchasePool(true);		
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#isStatRollEnabled()
	 */
	public boolean isStatRollEnabled()
	{
		return (charLevelsFacade.getSize() == 0);
	}
	
	/**
	 * Update the  
	 */
	private void showPointPool()
	{
		if (poolPointText == null)
		{
			return;
		}

		int poolPointsTotal = 0;

		for (PCLevelInfo pcl : theCharacter.getLevelInfo())
		{
			poolPointsTotal += pcl.getSkillPointsGained(theCharacter);
		}

		int poolPointsUsed = poolPointsTotal - theCharacter.getSkillPoints();

		poolPointText.setReference(Integer.toString(poolPointsUsed)
			+ " / " + Integer.toString(poolPointsTotal)); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getSkillModifier(pcgen.core.facade.SkillFacade)
	 */
	public int getSkillModifier(SkillFacade skill, CharacterLevelFacade level)
	{
		if (skill.getKeyStat() == null)
		{
			return 0;
		}

		for (StatFacade stat : statScoreMap.keySet())
		{
			if (skill.getKeyStat().equals(stat.getAbbreviation()))
			{
				return getModTotal(stat);
			}
		}

		return 0;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getSkillRanks(pcgen.core.facade.SkillFacade)
	 */
	public float getSkillRanks(SkillFacade skill, CharacterLevelFacade finallevel)
	{
		return charLevelsFacade.getSkillRanks(finallevel, skill);
//		float numRanks = 0.0f;
//		for (CharacterLevelFacade level : pcClassLevels)
//		{
//			numRanks += charLevelsFacade.getSkillRanks(finallevel, skill);
//			if (level == finallevel)
//			{
//				break;
//			}
//		}
//		return numRanks;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getSkillTotal(pcgen.core.facade.SkillFacade)
	 */
	public int getSkillTotal(SkillFacade skill, CharacterLevelFacade level)
	{
		return (int) (Math.floor(getSkillRanks(skill, level)) + getSkillModifier(skill, level));
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getMaxRanks(pcgen.cdom.enumeration.SkillCost, pcgen.core.facade.CharacterLevelFacade)
	 */
	public float getMaxRanks(SkillCost cost, CharacterLevelFacade level)
	{
		if (cost == null || level == null || !pcClassLevels.containsElement(level))
		{
			return 0.0f;
		}
		if (cost.getCost() == 0)
		{
			return Float.NaN;
		}
		return ((float) pcClassLevels.getIndexOfElement(level) + 4) / cost.getCost();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getUndoManager()
	 */
	public UndoManager getUndoManager()
	{
		return undoManager;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getRaceRef()
	 */
	public ReferenceFacade<RaceFacade> getRaceRef()
	{
		return race;
	}

	/**
	 * @return A reference to a list containing the character's race.
	 */
	public ListFacade<RaceFacade> getRaceAsList()
	{
		return raceList;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setRace(pcgen.core.facade.RaceFacade)
	 */
	public void setRace(RaceFacade race)
	{
		// TODO: We don't have a HP dialog implemented yet, so don't try to show it
		SettingsHandler.setShowHPDialogAtLevelUp(false);
		//SettingsHandler.setShowStatDialogAtLevelUp(false);
		int oldLevel = charLevelsFacade.getSize();

		if (race == null)
		{
			race = Globals.s_EMPTYRACE;
		}
		this.race.setReference(race);
		if (race instanceof Race && race != theCharacter.getRace())
		{
			theCharacter.setRace((Race) race);
			raceList.clearContents();
			if (race != Globals.s_EMPTYRACE)
			{
				raceList.addElement(race);
			}
		}
		refreshLanguageList();
		if (selectedGender != null)
		{
			setGender(selectedGender);
		}
		age.setReference(theCharacter.getAge());
		updateAgeCategoryForAge();
		refreshClassLevelModel();
		characterAbilities.rebuildAbilityLists();
		currentXP.setReference(theCharacter.getXP());
		xpForNextlevel.setReference(theCharacter.minXPForNextECL());
		hpRef.setReference(theCharacter.hitPoints());
		if (theCharacter.getRace() == null
			|| Constants.NONESELECTED.equals(theCharacter.getRace()
				.getKeyName()))
		{
			todoManager.addTodo(new TodoFacadeImpl(CharacterTab.SummaryTab, "Race",
				"in_irTodoRace", 100));
		}
		else
		{
			todoManager.removeTodo("in_irTodoRace");
		}
		
		if (oldLevel != charLevelsFacade.getSize())
		{
			delegate.showLevelUpInfo(this, oldLevel);
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getTabNameRef()
	 */
	public ReferenceFacade<String> getTabNameRef()
	{
		return tabName;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setTabName(java.lang.String)
	 */
	public void setTabName(String name)
	{
		tabName.setReference(name);
		theCharacter.setTabName(name);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getNameRef()
	 */
	public ReferenceFacade<String> getNameRef()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		this.name.setReference(name);
		theCharacter.setName(name);
		if (isNewCharName(theCharacter.getName()))
		{
			todoManager.addTodo(new TodoFacadeImpl(CharacterTab.SummaryTab, "Name",
				"in_sumTodoName", 1));
		}
		else
		{
			todoManager.removeTodo("in_sumTodoName");
		}
	}

	/**
	 * Check  whether the field should be output. 
	 * @param field The BiographyField to check export rules for.
	 * @return true if the field should be output, false if it may not be.
	 */
	public boolean getExportBioField(BiographyField field)
	{
		return !theCharacter.getSuppressBioField(field);
	}
	
	/**
	 * Set whether the field should be output. 
	 * @param field The BiographyField to set export rules for.
	 * @param export Should the field be shown in output.
	 */
	public void setExportBioField(BiographyField field, boolean export)
	{
		theCharacter.setSuppressBioField(field, !export);
	}

	/**
	 * {@inheritDoc}
	 */
	public ReferenceFacade<String> getSkinColorRef()
	{
		return skinColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSkinColor(String color)
	{
		skinColor.setReference(color);
		theCharacter.setSkinColor(color);
	}

	/**
	 * {@inheritDoc}
	 */
	public ReferenceFacade<String> getHairColorRef()
	{
		return hairColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setHairColor(String color)
	{
		hairColor.setReference(color);
		theCharacter.setHairColor(color);
	}

	/**
	 * {@inheritDoc}
	 */
	public ReferenceFacade<Integer> getHeightRef()
	{
		return heightRef;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setHeight(int height)
	{
		heightRef.setReference(height);
		theCharacter.setHeight(height);
	}

	/**
	 * {@inheritDoc}
	 */
	public ReferenceFacade<Integer> getWeightRef()
	{
		return weightRef;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setWeight(int weight)
	{
		weightRef.setReference(weight);
		theCharacter.setWeight(weight);
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getDeityRef()
	 */
	public ReferenceFacade<DeityFacade> getDeityRef()
	{
		return deity;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setDeity(pcgen.core.facade.DeityFacade)
	 */
	public void setDeity(DeityFacade deity)
	{
		this.deity.setReference(deity);
		if (deity instanceof Deity)
		{
			theCharacter.setDeity((Deity) deity);
		}
		refreshLanguageList();
		buildAvailableDomainsList();
	}


	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#addDomain(pcgen.core.facade.DomainFacade)
	 */
	public void addDomain(DomainFacade domainFacade)
	{
		if (!(domainFacade instanceof DomainFacadeImpl))
		{
			return;
		}
		DomainFacadeImpl domainFI = (DomainFacadeImpl) domainFacade;
		Domain domain = domainFI.getRawObject();
		if (theCharacter.hasDomain(domain))
		{
			return;
		}

		if (!isQualifiedFor(domainFacade))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle
				.getFormattedString("in_qualifyMess", domain.getDisplayName()));

			return;
		}

		// Check selected domains vs Max number allowed
		if (theCharacter.getDomainCount() >= theCharacter.getMaxCharacterDomains())
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle
				.getFormattedString("in_errorNoMoreDomains"));

			return;
		}
		
		domains.addElement(domainFI);
		theCharacter.addDomain(domain);
		DomainApplication.applyDomain(theCharacter, domain);

		theCharacter.calcActiveBonuses();

		remainingDomains.setReference(theCharacter.getMaxCharacterDomains()
			- theCharacter.getDomainCount());
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getDomains()
	 */
	public ListFacade<DomainFacade> getDomains()
	{
		return domains;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#removeDomain(pcgen.core.facade.DomainFacade)
	 */
	public void removeDomain(DomainFacade domain)
	{
		domains.removeElement(domain);
		theCharacter.removeDomain(((DomainFacadeImpl) domain).getRawObject());
		remainingDomains.setReference(theCharacter.getMaxCharacterDomains()
			- theCharacter.getDomainCount());
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getMaxDomains()
	 */
	public ReferenceFacade<Integer> getMaxDomains()
	{
		return maxDomains;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getRemainingDomainSelectionsRef()
	 */
	public ReferenceFacade<Integer> getRemainingDomainSelectionsRef()
	{
		return remainingDomains;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getAvailableDomains()
	 */
	public ListFacade<DomainFacade> getAvailableDomains()
	{
		return availDomains;
	}

	 
	/**
	 * This method returns all available domains, without filtering.
	 * 
	 * @param pcDeity
	 *            Deity selected for the current character
	 *            
	 * @return availDomainList
	 */
	private void buildAvailableDomainsList()
	{
		List<DomainFacadeImpl> availDomainList = new ArrayList<DomainFacadeImpl>();
		List<DomainFacadeImpl> selDomainList = new ArrayList<DomainFacadeImpl>();
		Deity pcDeity = theCharacter.getDeity();
		
		if (pcDeity != null)
		{
			for (CDOMReference<Domain> domains : pcDeity
				.getSafeListMods(Deity.DOMAINLIST))
			{
				Collection<AssociatedPrereqObject> assoc =
						pcDeity.getListAssociations(Deity.DOMAINLIST, domains);
				for (AssociatedPrereqObject apo : assoc)
				{
					for (Domain d : domains.getContainedObjects())
					{
						if (!isDomainInList(availDomainList, d))
						{
							availDomainList.add(new DomainFacadeImpl(d, apo
								.getPrerequisiteList()));
						}
					}
				}
			}
		}

		// Loop through the available prestige domains
		for (PCClass aClass : theCharacter.getClassList())
		{
			/*
			 * Need to do for the class, for compatibility, since level 0 is
			 * loaded into the class itself
			 */
			processDomainList(aClass, availDomainList);
			processAddDomains(aClass, availDomainList);
			for (int lvl = 0; lvl <= theCharacter.getLevel(aClass); lvl++)
			{
				PCClassLevel cl =
					theCharacter.getActiveClassLevel(aClass, lvl);
				processAddDomains(cl, availDomainList);
				processDomainList(cl, availDomainList);
			}
		}
		

		// Loop through the character's selected domains
		for (Domain d : theCharacter.getDomainSet())
		{
			DomainFacadeImpl domainFI = new DomainFacadeImpl(d);
			boolean found = false;
			for (DomainFacadeImpl row : availDomainList)
			{
				if (d.equals(row.getRawObject()))
				{
					domainFI = row;
					found = true;
					break;
				}
			}

			if (!found)
			{
				availDomainList.add(domainFI);
			}

			if (!isDomainInList(selDomainList, d))
			{
				selDomainList.add(domainFI);
			}
		}
		
		availDomains.setContents(availDomainList);
		domains.setContents(selDomainList);
		maxDomains.setReference(theCharacter.getMaxCharacterDomains());
		remainingDomains.setReference(theCharacter.getMaxCharacterDomains()
			- theCharacter.getDomainCount());
	}

	/**
	 * Check if a domain is a list of domains, irrespective of prerequisites.
	 *  
	 * @param qualDomainList The list of domains with their prerequisites.
	 * @param qualDomain The domain to search for.
	 * @return tue if the domain is in the list 
	 */
	private boolean isDomainInList(
		List<DomainFacadeImpl> qualDomainList,
		Domain domain)
	{
		for (DomainFacadeImpl row : qualDomainList)
		{
			if (domain.equals(row.getRawObject()))
			{
				return true;
			}
		}
		return false;
	}

	private void processAddDomains(CDOMObject cdo,
			final List<DomainFacadeImpl> availDomainList)
	{
		Collection<CDOMReference<Domain>> domains = cdo.getListMods(PCClass.ALLOWED_DOMAINS);
		if (domains != null)
		{
			for (CDOMReference<Domain> ref : domains)
			{
				Collection<AssociatedPrereqObject> assoc =
						cdo.getListAssociations(PCClass.ALLOWED_DOMAINS, ref);
				for (AssociatedPrereqObject apo : assoc)
				{
					for (Domain d : ref.getContainedObjects())
					{
						/*
						 * TODO This gate produces a rather interesting, and
						 * potentially wrong situation. What if two ADDDOMAINS
						 * exist with different PRE? Doesn't this fail?
						 */
						if (!isDomainInList(availDomainList, d))
						{
							availDomainList.add(new DomainFacadeImpl(d, apo
								.getPrerequisiteList()));
						}
					}
				}
			}
		}
	}

	private void processDomainList(CDOMObject obj,
			final List<DomainFacadeImpl> availDomainList)
	{
		for (QualifiedObject<CDOMSingleRef<Domain>> qo : obj.getSafeListFor(ListKey.DOMAIN))
		{
			CDOMSingleRef<Domain> ref = qo.getRawObject();
			Domain domain = ref.resolvesTo();
			if (!isDomainInList(availDomainList, domain))
			{
				availDomainList.add(new DomainFacadeImpl(domain, qo
						.getPrerequisiteList()));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getEquipmentSetRef()
	 */
	public ReferenceFacade<EquipmentSetFacade> getEquipmentSetRef()
	{
		return equipSet;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setEquipmentSet(pcgen.core.facade.EquipmentSetFacade)
	 */
	public void setEquipmentSet(EquipmentSetFacade set)
	{
		EquipmentSetFacade oldSet = equipSet.getReference();
		if (oldSet != null)
		{
			oldSet.getEquippedItems().removeListListener(this);
			oldSet.getEquippedItems().removeEquipmentListListener(this);
		}
		equipSet.setReference(set);
		set.getEquippedItems().addListListener(this);
		set.getEquippedItems().addEquipmentListListener(this);
		refreshTotalWeight();
	}

	/**
	 * Regenerate the character's list of languages.
	 * TODO: This needs to be invoked after a rank of speak language is added or removed to trigger the todo 
	 */
	void refreshLanguageList()
	{
		long startTime = new Date().getTime();
		List<Language> sortedLanguages = new ArrayList<Language>(theCharacter.getLanguageSet());
		Collections.sort(sortedLanguages);
		languages.setContents(sortedLanguages);
		autoLanguagesCache = null;
 
		int bonusLangMax = theCharacter.getBonusLanguageCount();;
		availableBonusLangs = new ArrayList<Language>();
		currBonusLangs = new ArrayList<Language>();
		Ability a =
				Globals.getContext().ref.silentlyGetConstructedCDOMObject(
					Ability.class, AbilityCategory.LANGBONUS, "*LANGBONUS");
		List<String> currBonusLangNameList = theCharacter.getAssociationList(a);
		for (LanguageFacade langFacade : languages)
		{
			Language lang = (Language) langFacade;
			if (currBonusLangNameList.contains(lang.getKeyName()))
			{
				currBonusLangs.add(lang);
			}
		}
		int bonusLangRemain = bonusLangMax - currBonusLangs.size();
		numBonusLang.setReference(bonusLangRemain);
		if (bonusLangRemain > 0)
		{
			todoManager.addTodo(new TodoFacadeImpl(CharacterTab.SummaryTab, "Languages",
				"in_sumTodoBonusLanguage", 110));
		}
		else
		{
			todoManager.removeTodo("in_sumTodoBonusLanguage");
		}
		
		int numSkillLangSelected = 0;
		int skillLangMax = 0;
		//TODO: Need to cope with multiple skill languages
		SkillFacade speakLangSkill = dataSet.getSpeakLanguageSkill();
		if (speakLangSkill != null)
		{
			Skill skill = (Skill) speakLangSkill;
			List<String> langList = theCharacter.getAssociationList(skill);
			numSkillLangSelected = langList.size();
			skillLangMax = (int) theCharacter.getSkillRank(skill);
		}
		
		int skillLangRemain = skillLangMax - numSkillLangSelected;
		numSkillLang.setReference(skillLangRemain);
		if (skillLangRemain > 0)
		{
			todoManager.addTodo(new TodoFacadeImpl(CharacterTab.SummaryTab, "Languages",
				"in_sumTodoSkillLanguage", 112));
		}
		else
		{
			todoManager.removeTodo("in_sumTodoSkillLanguage");
		}

		long endTime = new Date().getTime();
		Logging.log(Logging.DEBUG, "refreshLanguageList took " + (endTime-startTime) + " ms.");
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getLanguages()
	 */
	public ListFacade<LanguageFacade> getLanguages()
	{
		return languages;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setLanguages(java.util.List)
	 */
	public void setLanguages(List<LanguageFacade> languageList)
	{
		languages.setContents(languageList);
		autoLanguagesCache = null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getAvailBonusLangages()
	 */
	public List<LanguageFacade> getAvailBonusLangages()
	{
		return new ArrayList<LanguageFacade>(availableBonusLangs);
	}


	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getCurrBonusLangages()
	 */
	public List<LanguageFacade> getCurrBonusLangages()
	{
		return new ArrayList<LanguageFacade>(currBonusLangs);
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getNumBonusLanguagesOutstanding()
	 */
	public ReferenceFacade<Integer> getNumBonusLanguagesOutstanding()
	{
		return numBonusLang;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getNumSkillLanguagesOutstanding()
	 */
	public ReferenceFacade<Integer> getNumSkillLanguagesOutstanding()
	{
		return numSkillLang;
	}

	public ListFacade<LanguageChooserFacade> getLanguageChoosers()
	{
		Ability a =
			Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				Ability.class, AbilityCategory.LANGBONUS, "*LANGBONUS");
		DefaultListFacade<LanguageChooserFacade> chooserList = new DefaultListFacade<LanguageChooserFacade>();
		chooserList.addElement(new LanguageChooserFacadeImpl(this, "Bonus Language", a));

		SkillFacade speakLangSkill = dataSet.getSpeakLanguageSkill();
		if (speakLangSkill != null)
		{
			chooserList.addElement(new LanguageChooserFacadeImpl(this, "Language via Skill points", (Skill) speakLangSkill));
		}
		return chooserList;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getFileRef()
	 */
	public ReferenceFacade<File> getFileRef()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file.setReference(file);
		theCharacter.setFileName(file.getName());
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#export(pcgen.io.ExportHandler, java.io.BufferedWriter)
	 */
	public void export(ExportHandler theHandler, BufferedWriter buf)
	{
		final int maxRetries = 3;
		for (int i = 0; i < maxRetries ; i++)
		{
			try
			{
				theHandler.write(theCharacter, buf);
				SettingsHandler.setSelectedCharacterHTMLOutputSheet(theHandler
					.getTemplateFile().getAbsolutePath(), theCharacter);
				return;
			}
			catch (ConcurrentModificationException e)
			{
				Logging.log(Logging.INFO, "Retrying export after ConcurrentModificationException", e);
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e1)
				{
					Logging.errorPrint("Interrupted sleep - probably closing.");
					return;
					
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getHandedRef()
	 */
	public ReferenceFacade<SimpleFacade> getHandedRef()
	{
		return handedness;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setHanded(java.lang.String)
	 */
	public void setHanded(SimpleFacade handedness)
	{
		this.handedness.setReference(handedness);
		theCharacter.setHanded(handedness.toString());
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getPlayersNameRef()
	 */
	public ReferenceFacade<String> getPlayersNameRef()
	{
		return playersName;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setPlayersName(java.lang.String)
	 */
	public void setPlayersName(String name)
	{
		this.playersName.setReference(name);
		theCharacter.setPlayersName(name);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#isQualifiedFor(pcgen.core.facade.ClassFacade)
	 */
	public boolean isQualifiedFor(ClassFacade c)
	{
		if (c instanceof PCClass)
		{
			return theCharacter.isQualified((PCClass) c);
		}
		return false;
	}

	public UIDelegate getUIDelegate()
	{
		return delegate;
	}

	/**
	 * Save the character to disc using its filename. Note this method is not 
	 * part of the CharacterFacade and should only be used by the 
	 * ChracterManager class.
	 * 
	 * @throws NullPointerException 
	 * @throws IOException If the write fails
	 */
	public void save() throws NullPointerException, IOException
	{
		GameMode mode = (GameMode) dataSet.getGameMode();
		List campaigns = ListFacades.wrap(dataSet.getCampaigns());
		(new PCGIOHandler()).write(theCharacter, mode, campaigns, file.getReference().getAbsolutePath());
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#isAutomatic(pcgen.core.facade.LanguageFacade)
	 */
	public boolean isAutomatic(LanguageFacade language)
	{
		if (autoLanguagesCache == null)
		{
			autoLanguagesCache = theCharacter.getAutoLanguages();
		}
		return autoLanguagesCache.contains(language);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getCharacterLevelsFacade()
	 */
	public CharacterLevelsFacade getCharacterLevelsFacade()
	{
		return charLevelsFacade;
	}

	public DescriptionFacade getDescriptionFacade()
	{
		return descriptionFacade;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setXP(int)
	 */
	public void setXP(final int xp)
	{
		if (xp == currentXP.getReference())
		{
			// We've already processed this change, most likely via the adjustXP method
			return;
		}
		theCharacter.setXP(xp);
		currentXP.setReference(theCharacter.getXP());
		xpForNextlevel.setReference(theCharacter.minXPForNextECL());

		if (theCharacter.getXP() >= theCharacter.minXPForNextECL())
		{
			delegate.showInfoMessage(Constants.APPLICATION_NAME, SettingsHandler.getGame()
				.getLevelUpMessage());
		}
		updateLevelTodo();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getXPRef()
	 */
	public ReferenceFacade<Integer> getXPRef()
	{
		return currentXP;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#adjustXP(int)
	 */
	public void adjustXP(final int xp)
	{
		int currVal = currentXP.getReference();
		int newVal = currVal + xp;
		theCharacter.setXP(newVal);
		currentXP.setReference(theCharacter.getXP());
		xpForNextlevel.setReference(theCharacter.minXPForNextECL());

		if (theCharacter.getXP() >= theCharacter.minXPForNextECL())
		{
			delegate.showInfoMessage(Constants.APPLICATION_NAME, SettingsHandler.getGame()
				.getLevelUpMessage());
		}
		updateLevelTodo();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getXPForNextLevelRef()
	 */
	public ReferenceFacade<Integer> getXPForNextLevelRef()
	{
		return xpForNextlevel;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#setAge(int)
	 */
	public void setAge(final int age)
	{
		if (age == this.age.getReference())
		{
			// We've already processed this change, most likely via the setAgeCategory method
			return;
		}
		
		theCharacter.setAge(age);
		this.age.setReference(age);
		updateAgeCategoryForAge();
	}

	/**
	 * Update the character's age category based on their age.
	 */
	private void updateAgeCategoryForAge()
	{
		AgeSet ageSet = theCharacter.getAgeSet();
		if (ageSet != null)
		{
			String ageCatName = ageSet.getName();
			for (SimpleFacade ageCatFacade : ageCategoryList)
			{
				if (ageCatFacade.toString().equals(ageCatName))
				{
					ageCategory.setReference(ageCatFacade);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getAgeRef()
	 */
	public ReferenceFacade<Integer> getAgeRef()
	{
		return age;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getAgeCategories()
	 */
	public ListFacade<SimpleFacade> getAgeCategories()
	{
		return ageCategoryList;
	}

	public void setAgeCategory(final SimpleFacade ageCat)
	{
		if (ageCat == this.ageCategory.getReference())
		{
			// We've already processed this change, most likely via the setAge method
			return;
		}

		final Race pcRace = theCharacter.getRace();
		final String selAgeCat = ageCat.toString();

		if ((pcRace != null) && !pcRace.equals(Globals.s_EMPTYRACE))
		{
			if (selAgeCat != null)
			{
				final int idx = Globals.getBioSet().getAgeSetNamed(selAgeCat);

				if (idx >= 0)
				{
					ageCategory.setReference(ageCat);
					Globals.getBioSet().randomize(
							"AGECAT" + Integer.toString(idx), theCharacter);
					age.setReference(theCharacter.getAge());
					ageCategory.setReference(ageCat);
				}
			}
		}
	}

	public ReferenceFacade<SimpleFacade> getAgeCategoryRef()
	{
		return ageCategory;
	}


	/**
	 * This method updates the purchase point pool and the stat total text. The 
	 * stat total text will be updated whether we are in purchase mode or not. 
	 * displayed 
	 * @param checkPurchasePoints boolean true if the pool should be checked
	 * for available points before doing the update.
	 */
	private void updateScorePurchasePool(boolean checkPurchasePoints)
	{
		int usedStatPool = getUsedStatPool(theCharacter);

		// Handle purchase mode for stats
		if (SettingsHandler.getGame().isPurchaseStatMode())
		{
			// Let them dink on stats at 0th or 1st PC levels
			if (canChangePurchasePool())
			{
				theCharacter.setCostPool(usedStatPool);
				theCharacter.setPoolAmount(usedStatPool);
			}

			final String bString = Integer.toString(theCharacter.getCostPool());
			//	int availablePool = SettingsHandler.getPurchaseModeMethodPool();
			int availablePool = theCharacter.getPointBuyPoints();
			if (availablePool < 0)
			{
				availablePool =
						RollingMethods.roll(SettingsHandler.getGame()
							.getPurchaseModeMethodPoolFormula());
				theCharacter.setPointBuyPoints(availablePool);
			}

			if (availablePool != 0)
			{
				statTotalLabelText.setReference(LanguageBundle
					.getFormattedString("in_sumStatCost", SettingsHandler //$NON-NLS-1$
						.getGame().getPurchaseModeMethodName()));
				statTotalText.setReference(LanguageBundle.getFormattedString(
					"in_sumStatPurchaseDisplay", bString, availablePool)); //$NON-NLS-1$
				modTotalLabelText.setReference("");
				modTotalText.setReference("");
			}

			if (checkPurchasePoints && (availablePool != 0))
			{
				//
				// Let the user know that they've exceeded their goal, but allow them to keep going if they want...
				// Only do this at 1st level or lower
				//
				if (canChangePurchasePool() && (availablePool > 0)
					&& (usedStatPool > availablePool))
				{
					delegate.showInfoMessage(Constants.APPLICATION_NAME,
						LanguageBundle.getFormattedString(
							"in_sumYouHaveExcededTheMaximumPointsOf",//$NON-NLS-1$
							String.valueOf(availablePool), SettingsHandler
								.getGame().getPurchaseModeMethodName()));
				}
			}
		}

		// Non-purchase mode for stats
		if (!SettingsHandler.getGame().isPurchaseStatMode()
			|| (theCharacter.getPointBuyPoints() == 0))
		{
			int statTotal = 0;
			int modTotal = 0;

			for (PCStat aStat : theCharacter.getStatSet())
			{
				if (theCharacter.isNonAbility(aStat) || !aStat.getSafe(ObjectKey.ROLLED))
				{
					continue;
				}

				final int currentStat = StatAnalysis.getBaseStatFor(theCharacter, aStat);
				final int currentMod = StatAnalysis.getStatModFor(theCharacter, aStat);

				statTotal += currentStat;
				modTotal += currentMod;
			}

			statTotalLabelText.setReference(LanguageBundle
				.getString("in_sumStatTotalLabel")); //$NON-NLS-1$
			statTotalText.setReference(LanguageBundle.getFormattedString(
				"in_sumStatTotal", Integer.toString(statTotal)));
			modTotalLabelText.setReference(LanguageBundle
				.getString("in_sumModTotalLabel"));
			modTotalText.setReference(LanguageBundle.getFormattedString(
				"in_sumModTotal", Integer.toString(modTotal)));
		}
		
		if (charLevelsFacade.getSize() == 0
			&& (allAbilitiesAreZero() || (SettingsHandler.getGame()
				.isPurchaseStatMode() && (theCharacter.getPointBuyPoints() != getUsedStatPool(theCharacter)))))
		{
			todoManager.addTodo(new TodoFacadeImpl(CharacterTab.SummaryTab,
				"Ability Scores", "in_sumTodoStats", 50));
		}
		else
		{
			todoManager.removeTodo("in_sumTodoStats");
		}
	}

	/**
	 * Idenitfy if the character can stil change purchase pool values - spent 
	 * or available. This action is restricted by level. 
	 * @return true if the character is allowed to change the purchase pool
	 */
	public boolean canChangePurchasePool()
	{
		// This is a problem for races with non-0 level
		// adjustment so only count PC & NPC levels, not
		// monster levels XXX
		int pcPlayerLevels = theCharacter.totalNonMonsterLevels();
		
		int maxDiddleLevel;
		if (poolPointText != null)
		{
			maxDiddleLevel = 0;
		}
		else
		{
			maxDiddleLevel = 1;
		}
		return pcPlayerLevels <= maxDiddleLevel;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getStatTotalLabelTextRef()
	 */
	public ReferenceFacade<String> getStatTotalLabelTextRef()
	{
		return statTotalLabelText;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getStatTotalTextRef()
	 */
	public ReferenceFacade<String> getStatTotalTextRef()
	{
		return statTotalText;
	}

	/**
	 * @return A reference to the label text for the character's modifier total
	 */
	public ReferenceFacade<String> getModTotalLabelTextRef()
	{
		return modTotalLabelText;
	}

	/**
	 * @return A reference to the text for the character's modifier total
	 */
	public ReferenceFacade<String> getModTotalTextRef()
	{
		return modTotalText;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getTodoList()
	 */
	public ListFacade<TodoFacade> getTodoList()
	{
		return todoManager.getTodoList();
	}

	/**
	 * @return the PlayerCharacter the facade is fronting for.
	 */
	PlayerCharacter getTheCharacter()
	{
		return theCharacter;
	}

	public ReferenceFacade<Integer> getTotalHPRef()
	{
		return hpRef;
	}

	public ReferenceFacade<Integer> getRollMethodRef()
	{
		return rollMethodRef;
	}

	public void refreshRollMethod()
	{
		if (!canChangePurchasePool())
		{
			return;
		}
		GameMode game = (GameMode) dataSet.getGameMode();
		rollMethodRef.setReference(game.getRollMethod());
		if (SettingsHandler.getGame().isPurchaseStatMode())
		{
			int availablePool =
					RollingMethods.roll(SettingsHandler.getGame()
						.getPurchaseModeMethodPoolFormula());
			theCharacter.setPointBuyPoints(availablePool);

			// Make sure all scores are within the valid range
			for (StatFacade stat : statScoreMap.keySet())
			{
				DefaultReferenceFacade<Integer> score = statScoreMap.get(stat);
				if (score.getReference() < SettingsHandler.getGame()
					.getPurchaseScoreMin(theCharacter)
					&& stat instanceof PCStat)
				{
					setStatToPurchaseNeutral((PCStat) stat, score);
				}
			}

		}

		hpRef.setReference(theCharacter.hitPoints());
		updateScorePurchasePool(false);
	}

	/**
	 * Reset the stat score to the neutral value (usually 10) for 
	 * the point buy method.
	 * 
	 * @param pcStat The stata ebing adjusted.
	 * @param scoreRef The reference tothe current score.
	 */
	private void setStatToPurchaseNeutral(PCStat pcStat,
		DefaultReferenceFacade<Integer> scoreRef)
	{
		int newScore =
				SettingsHandler.getGame().getPurchaseModeBaseStatScore(
					theCharacter);
		if (StringUtils.isNotEmpty(validateNewStatBaseScore(newScore, pcStat,
			theCharacter.totalNonMonsterLevels())))
		{
			newScore =
					SettingsHandler.getGame().getPurchaseScoreMin(theCharacter);
			if (StringUtils.isNotEmpty(validateNewStatBaseScore(newScore, pcStat,
				theCharacter.totalNonMonsterLevels())))
			{
				return;
			}
		}

		theCharacter.setAssoc(pcStat, AssociationKey.STAT_SCORE, newScore);
		scoreRef.setReference(newScore);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getPurchasedEquipment()
	 */
	public EquipmentListFacade getPurchasedEquipment()
	{
		return purchasedEquip;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#addPurchasedEquipment(pcgen.core.facade.EquipmentFacade, int)
	 */
	public void addPurchasedEquipment(EquipmentFacade equipment, int quantity, boolean customize)
	{
		if (equipment == null || quantity <= 0)
		{
			return;
		}
		
//		int nextOutputIndex = 1;
		Equipment equipItemToAdjust = (Equipment) equipment;

		if (customize)
		{
			equipItemToAdjust = openCustomizer(equipItemToAdjust);
			if (equipItemToAdjust == null)
			{
				return;
			}
		}
		Equipment updatedItem =
				theCharacter.getEquipmentNamed(equipItemToAdjust.getName());

		if (updatedItem != null)
		{
			// item is already in inventory; update it
			final double prevQty =
					(updatedItem.qty() < 0) ? 0 : updatedItem.qty();
			final double newQty = prevQty + quantity;

			theCharacter.updateEquipmentQty(updatedItem, prevQty, newQty);
			Float qty = new Float(newQty);
			updatedItem.setQty(qty);
			purchasedEquip.setQuantity(equipment, qty.intValue());
		}
		else
		{
			// item is not in inventory; add it
			updatedItem = equipItemToAdjust.clone();

			if (updatedItem != null)
			{
				//TODO:  Calc the item's output order
//				if (autoSort.isSelected())
//				{
//					updatedItem.setOutputIndex(nextOutputIndex);
//				}
//				else
//				{
//					if (updatedItem.getOutputIndex() == 0)
//					{
//						updatedItem
//							.setOutputIndex(getHighestOutputIndex() + 1);
//						theCharacter.cacheOutputIndex(updatedItem);
//					}
//				}

				// Set the number carried and add it to the character
				Float qty = new Float(quantity);
				updatedItem.setQty(qty);
				theCharacter.addEquipment(updatedItem);
//				if (autoSort.isSelected())
//				{
//					resortSelected(ResortComparator.RESORT_NAME,
//						ResortComparator.RESORT_ASCENDING);
//				}
			}
			purchasedEquip.addElement(updatedItem, quantity);
		}

		// Update the PC and equipment
		theCharacter.setCalcEquipmentList();
		theCharacter.setDirty(true);
	}

	private Equipment openCustomizer(Equipment aEq)
	{
		if (aEq != null)
		{
			EQFrame eqFrame = new EQFrame(null, theCharacter);

			if (eqFrame.setEquipment(aEq))
			{
				eqFrame.setVisible(true);
				Equipment newEquip = eqFrame.getNewEquip();
				if (newEquip != null
					&& dataSet.getEquipment() instanceof DefaultListFacade<?>)
				{
					((DefaultListFacade<EquipmentFacade>) dataSet
						.getEquipment()).addElement(newEquip);
				}
				return eqFrame.isPurchase() ? newEquip : null;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#removePurchasedEquipment(pcgen.core.facade.EquipmentFacade, int)
	 */
	public void removePurchasedEquipment(EquipmentFacade equipment, int quantity)
	{
		if (equipment == null || quantity <= 0)
		{
			return;
		}
		
		Equipment equipItemToAdjust = (Equipment) equipment;

		Equipment updatedItem =
				theCharacter.getEquipmentNamed(equipItemToAdjust.getName());

		// see if item is already in inventory; update it
		if (updatedItem != null)
		{
			final double prevQty =
					(updatedItem.qty() < 0) ? 0 : updatedItem.qty();
			final double newQty = prevQty - quantity;

			//TODO: Check for presence in equipset and offer to remove
			if (newQty <= 0)
			{
				// completely remove item
				updatedItem.setNumberCarried(new Float(0));
				updatedItem.setLocation(EquipmentLocation.NOT_CARRIED);

				final Equipment eqParent =
						(Equipment) updatedItem.getParent();

				if (eqParent != null)
				{
					eqParent.removeChild(theCharacter, updatedItem);
				}

				theCharacter.removeEquipment(updatedItem);
				theCharacter.delEquipSetItem(updatedItem);
				purchasedEquip.removeElement(updatedItem);
			}
			else
			{
				// update item count
				theCharacter.updateEquipmentQty(updatedItem, prevQty, newQty);
				Float qty = new Float(newQty);
				updatedItem.setQty(qty);
				updatedItem.setNumberCarried(qty);
				purchasedEquip.setQuantity(equipment, qty.intValue());
			}
			
			theCharacter.updateEquipmentQty(updatedItem, prevQty, newQty);
			Float qty = new Float(newQty);
			updatedItem.setQty(qty);
			updatedItem.setNumberCarried(qty);
		}

		// Update the PC and equipment
		theCharacter.setCalcEquipmentList();
		theCharacter.setDirty(true);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#isQualifiedFor(pcgen.core.facade.EquipmentFacade)
	 */
	public boolean isQualifiedFor(EquipmentFacade equipment)
	{
		final Equipment equip = (Equipment) equipment;
		final boolean accept =
				PrereqHandler.passesAll(equip.getPrerequisiteList(),
					theCharacter, equip);

		if (accept && (equip.isShield() || equip.isWeapon() || equip.isArmor()))
		{
			return theCharacter.isProficientWith(equip);
		}

		return accept;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getEquipmentSizedForCharacter(pcgen.core.facade.EquipmentFacade)
	 */
	public EquipmentFacade getEquipmentSizedForCharacter(EquipmentFacade equipment)
	{
		final Equipment equip = (Equipment) equipment;
		final SizeAdjustment newSize = theCharacter.getSizeAdjustment();
		if (equip.getSizeAdjustment() == newSize
			|| !Globals.canResizeHaveEffect(theCharacter, equip, null))
		{
			return equipment;
		}
		
		final String existingKey = equip.getKeyName();
		final String newKey =
				equip.createKeyForAutoResize(newSize);

		Equipment potential = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				Equipment.class, newKey);

		if (newKey.equals(existingKey))
		{
			return equipment;
		}

		// If we've already resized this piece of equipment to this size
		// on a previous occasion, just substitute that piece of equipment
		// in place of the selected equipment.
		if (potential != null)
		{
			return potential;
		}

		final String newName =
					equip.createNameForAutoResize(newSize);
		potential = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(Equipment.class,
						newName);

		if (potential != null)
		{
			return potential;
		}

		final Equipment newEq =
				equip.clone();

		if (!newEq.containsKey(ObjectKey.BASE_ITEM))
		{
			newEq.put(ObjectKey.BASE_ITEM, CDOMDirectSingleRef
				.getRef(equip));
		}

		newEq.setName(newName);
		newEq.put(StringKey.OUTPUT_NAME, newName);
		newEq.put(StringKey.KEY_NAME, newKey);
		newEq.resizeItem(theCharacter, newSize);
		newEq.removeType(Type.AUTO_GEN);
		newEq.removeType(Type.STANDARD);
		if (!newEq.isType(Constants.TYPE_CUSTOM))
		{
			newEq.addType(Type.CUSTOM);
		}

		Globals.getContext().ref.importObject(newEq);

		return newEq;
	}

	/**
	 * Whether we should automatically resize all purchased gear to match the 
	 * character's size.
	 * @return true if equipment should be auto resize.
	 */
	public boolean isAutoResize()
	{
		return theCharacter.isAutoResize();
	}

	/**
	 * Update whether we should automatically resize all purchased gear to match  
	 * the character's size.
	 * 
	 * @param autoResize The new value for auto resize equipment option.
	 */
	public void setAutoResize(boolean autoResize)
	{
		theCharacter.setAutoResize(autoResize);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#createEquipmentSet(java.lang.String)
	 */
	public EquipmentSetFacade createEquipmentSet(String name)
	{
		String id = EquipmentSetFacadeImpl.getNewIdPath(theCharacter, null);
		EquipSet eSet = new EquipSet(id, name);
		theCharacter.addEquipSet(eSet);
		final EquipmentSetFacadeImpl facade =
			new EquipmentSetFacadeImpl(delegate, theCharacter, eSet, dataSet);
		equipmentSets.addElement(facade);
		
		return facade;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#deleteEquipmentSet(pcgen.core.facade.EquipmentSetFacade)
	 */
	public void deleteEquipmentSet(EquipmentSetFacade set)
	{
		if (set == null || !(set instanceof EquipmentSetFacadeImpl))
		{
			return;
		}
		EquipmentSetFacadeImpl setImpl = (EquipmentSetFacadeImpl) set;
		EquipSet eSet = setImpl.getEquipSet();

		theCharacter.delEquipSet(eSet);
		equipmentSets.removeElement(set);
	}

	public ReferenceFacade<String> getCarriedWeightRef()
	{
		return carriedWeightRef;
	}

	public ReferenceFacade<String> getLoadRef()
	{
		return loadRef;
	}

	public ReferenceFacade<String> getWeightLimitRef()
	{
		return weightLimitRef;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.EquipmentListFacade.EquipmentListListener#quantityChanged(pcgen.core.facade.EquipmentListFacade.EquipmentListEvent)
	 */
	public void quantityChanged(EquipmentListEvent e)
	{
		refreshTotalWeight();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.event.ListListener#elementAdded(pcgen.core.facade.event.ListEvent)
	 */
	public void elementAdded(ListEvent<EquipmentFacade> e)
	{
		refreshTotalWeight();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.event.ListListener#elementRemoved(pcgen.core.facade.event.ListEvent)
	 */
	public void elementRemoved(ListEvent<EquipmentFacade> e)
	{
		refreshTotalWeight();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.event.ListListener#elementsChanged(pcgen.core.facade.event.ListEvent)
	 */
	public void elementsChanged(ListEvent<EquipmentFacade> e)
	{
		refreshTotalWeight();
	}

	/**
	 * Refreshes the total weight by reading it from the current equipment set.  
	 */
	private void refreshTotalWeight()
	{
		String weight = Globals.getGameModeUnitSet().displayWeightInUnitSet(
			theCharacter.totalWeight().doubleValue());
		carriedWeightRef.setReference(weight);
		
		Load load = theCharacter.getLoadType();
		loadRef.setReference(CoreUtility.capitalizeFirstLetter(load.toString()));
		
		Float mult = SettingsHandler.getGame().getLoadInfo().getLoadMultiplier(
			load.toString());
		double limit = 0.0f;
		if (mult != null)
		{
			limit = WeightToken.getLoadToken(load.toString(),theCharacter);
		}
		weightLimitRef.setReference(Globals.getGameModeUnitSet()
			.displayWeightInUnitSet(limit));
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade.HitPointListener#hitPointsChanged(pcgen.core.facade.CharacterLevelsFacade.CharacterLevelEvent)
	 */
	public void hitPointsChanged(CharacterLevelEvent e)
	{
		hpRef.setReference(theCharacter.hitPoints());
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getInfoFactory()
	 */
	public InfoFactory getInfoFactory()
	{
		return infoFactory;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#isQualifiedFor(pcgen.core.facade.InfoFacade)
	 */
	public boolean isQualifiedFor(InfoFacade infoFacade)
	{
		if (!(infoFacade instanceof PObject))
		{
			return false;
		}

		PObject pObj = (PObject) infoFacade;
		if (!theCharacter.isQualified(pObj))
		{
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#isQualifiedFor(pcgen.core.facade.DomainFacade)
	 */
	public boolean isQualifiedFor(DomainFacade domainFacade)
	{
		if (!(domainFacade instanceof DomainFacadeImpl))
		{
			return false;
		}

		DomainFacadeImpl domainFI = (DomainFacadeImpl) domainFacade;
		Domain domain = domainFI.getRawObject();
		if (!PrereqHandler.passesAll(domainFI.getPrerequisiteList(),
			theCharacter, domain)
			|| !theCharacter.isQualified(domain))
		{
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#addTemplate(pcgen.core.facade.TemplateFacade)
	 */
	public void addTemplate(TemplateFacade templateFacade)
	{
		if (templateFacade == null || !(templateFacade instanceof PCTemplate))
		{
			return;
		}

		PCTemplate template = (PCTemplate) templateFacade;

		if (!PrereqHandler.passesAll(template.getPrerequisiteList(), theCharacter, template))
		{
			return;
		}

		if (!theCharacter.hasTemplate(template))
		{
			templates.addElement(template);
			theCharacter.addTemplate(template);
		}
		else
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle
				.getString("in_irHaveTemplate"));
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#removeTemplate(pcgen.core.facade.TemplateFacade)
	 */
	public void removeTemplate(TemplateFacade templateFacade)
	{
		if (templateFacade == null || !(templateFacade instanceof PCTemplate))
		{
			return;
		}

		PCTemplate template = (PCTemplate) templateFacade;

		if (theCharacter.hasTemplate(template))
		{
			theCharacter.removeTemplate(template);
			templates.removeElement(template);
		}
		else
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle
				.getString("in_irNotRemovable"));
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getTemplates()
	 */
	public ListFacade<TemplateFacade> getTemplates()
	{
		return templates;
	}


	public void addCharacterChangeListener(CharacterChangeListener listener)
	{
		//TODO: implement this
	}

	public void removeCharacterChangeListener(CharacterChangeListener listener)
	{
		//TODO: implement this
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterFacade#getSpellSupport()
	 */
	public SpellSupportFacade getSpellSupport()
	{
		return spellSupportFacade;
	}

	public ReferenceFacade<File> getPortraitRef()
	{
		return portrait;
	}

	public void setPortrait(File file)
	{
		portrait.setReference(file);
		theCharacter.setPortraitPath(file == null ? null : file
			.getAbsolutePath());
	}

	public ReferenceFacade<Rectangle> getThumbnailCropRef()
	{
		return cropRect;
	}

	public void setThumbnailCrop(Rectangle rect)
	{
		cropRect.setReference(rect);
		theCharacter.setPortraitThumbnailRect(rect);
	}

	/**
	 * Since Rectangles are modifiable we make sure that no references of the reference
	 * object are leaked to the outside world. This guarantees that the underlying reference
	 * object will not changed after it is set.
	 */
	private static class RectangleReference extends DefaultReferenceFacade<Rectangle>
	{

		/**
		 * Create a new reference based on the supplied rectangle.
		 * @param rect
		 */
		public RectangleReference(Rectangle rect) 
		{
			this.object = rect == null ? null : (Rectangle) rect.clone();
		}

		@Override
		public Rectangle getReference()
		{
			Rectangle rect = object;
			if (rect != null)
			{
				rect = (Rectangle) rect.clone();
			}
			return rect;
		}

		@Override
		public void setReference(Rectangle rect)
		{
			if (ObjectUtils.equals(this.object, rect))
			{
				return;
			}
			if (rect != null)
			{
				rect = (Rectangle) rect.clone();
			}
			Rectangle old = this.object;
			this.object = rect;
			if (rect != null)
			{
				rect = (Rectangle) rect.clone();
			}
			fireReferenceChangedEvent(this, old, rect);
		}

	}

}
