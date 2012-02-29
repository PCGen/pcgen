/**
 * CharacterAbilities.java
 * Copyright James Dempsey, 2011
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
 * Created on 19/03/2011 12:06:34 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.ActiveAbilityFacet;
import pcgen.cdom.facet.CategorizedDataFacetChangeEvent;
import pcgen.cdom.facet.DataFacetChangeEvent;
import pcgen.cdom.facet.DataFacetChangeListener;
import pcgen.cdom.facet.DirectAbilityFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.GrantedAbilityFacet;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.facade.AbilityCategoryFacade;
import pcgen.core.facade.AbilityFacade;
import pcgen.core.facade.DataSetFacade;
import pcgen.core.facade.TodoFacade.CharacterTab;
import pcgen.core.facade.UIDelegate;
import pcgen.core.facade.event.ChangeEvent;
import pcgen.core.facade.event.ChangeListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import pcgen.util.enumeration.View;
import pcgen.util.enumeration.Visibility;

/**
 * The Class <code>CharacterAbilities</code> manages the interaction between 
 * the core and the user interface for abilities. It listens for changes in 
 * abilities in the core and then updates the lists provided to the UI to
 * reflect the changes. The lists automatically notify any listeners in the 
 * UI of the changes.
 *   
 * <br/>
 * 
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class CharacterAbilities
{
	private PlayerCharacter theCharacter;
	private UIDelegate delegate;

	private Map<AbilityCategoryFacade, DefaultListFacade<AbilityFacade>> abilityListMap;
	private DefaultListFacade<AbilityCategoryFacade> activeCategories;
	private CharID charID;
	private DataSetFacade dataSetFacade;
	private final List<ChangeListener> abilityCatSelectionListeners;
	private final TodoManager todoManager;

	/**
	 * Create a new instance of CharacterAbilities for a character.
	 * @param pc The character we are tracking abilities for.
	 * @param delegate The user interface delegate for notifying the user.
	 * @param dataSetFacade The datasets that the character is using.
	 * @param todoManager The user tasks tracker.
	 */
	public CharacterAbilities(PlayerCharacter pc, UIDelegate delegate,
		DataSetFacade dataSetFacade, TodoManager todoManager)
	{
		theCharacter = pc;
		this.delegate = delegate;
		this.dataSetFacade = dataSetFacade;
		this.todoManager = todoManager;
		abilityCatSelectionListeners = new ArrayList<ChangeListener>();
		
		initForCharacter();
		//TODO Remove listener on close character
	}

	private void initForCharacter()
	{
		abilityListMap = 
				new LinkedHashMap<AbilityCategoryFacade, DefaultListFacade<AbilityFacade>>();
		activeCategories = new DefaultListFacade<AbilityCategoryFacade>();

		charID = theCharacter.getCharID();
		GrantedAbilityFacet grantedAbilityFacet = FacetLibrary.getFacet(GrantedAbilityFacet.class);
		DirectAbilityFacet directAbilityFacet = FacetLibrary.getFacet(DirectAbilityFacet.class);
		ActiveAbilityFacet activeAbilityFacet = FacetLibrary.getFacet(ActiveAbilityFacet.class);
		
		//theCharacter.getAbilityList(cat, nature)
		rebuildAbilityLists();
		
		activeAbilityFacet.addDataFacetChangeListener(new DataFacetChangeListener<Ability>()
			{

				public void dataAdded(DataFacetChangeEvent<Ability> dfce)
				{
					if (dfce.getCharID() != charID)
					{
							Logging.debugPrint("CA for " + theCharacter.getName()
								+ ". Ignoring active ability added for character "
								+ dfce.getCharID());
						return;
					}
					Logging.debugPrint("Got active ability added of " + dfce.getCDOMObject());
					Ability ability = dfce.getCDOMObject();
					if (dfce instanceof CategorizedDataFacetChangeEvent<?>)
					{
						
						CategorizedDataFacetChangeEvent categorizedEvent =
								(CategorizedDataFacetChangeEvent) dfce;
						AbilityCategory cat = (AbilityCategory) categorizedEvent.getCategory();
						Nature nature = categorizedEvent.getNature();
						addAbilityToLists(cat, ability, nature);
					}
				}

				public void dataRemoved(DataFacetChangeEvent<Ability> dfce)
				{
					if (dfce.getCharID() != charID)
					{
							Logging
								.debugPrint("CA for "
									+ theCharacter.getName()
									+ ". Ignoring active ability removed for character "
									+ dfce.getCharID());
						return;
					}
					Logging.debugPrint("Got active ability removed of " + dfce.getCDOMObject());
					Ability ability = dfce.getCDOMObject();
					if (dfce instanceof CategorizedDataFacetChangeEvent<?>)
					{
						
						CategorizedDataFacetChangeEvent categorizedEvent =
								(CategorizedDataFacetChangeEvent) dfce;
						AbilityCategory cat = (AbilityCategory) categorizedEvent.getCategory();
						Nature nature = categorizedEvent.getNature();
						removeAbilityFromLists(cat, ability, nature);
					}
				}
			});
		grantedAbilityFacet.addDataFacetChangeListener(new DataFacetChangeListener<Ability>()
		{

			public void dataAdded(DataFacetChangeEvent<Ability> dfce)
			{
				if (dfce.getCharID() != charID)
				{
						Logging.debugPrint("CA for " + theCharacter.getName()
							+ ". Ignoring granted ability added for character "
							+ dfce.getCharID());
					return;
				}
				Logging.debugPrint("Got granted ability added of " + dfce.getCDOMObject());
				Ability ability = dfce.getCDOMObject();
				rebuildAbilityLists();
			}

			public void dataRemoved(DataFacetChangeEvent<Ability> dfce)
			{
				if (dfce.getCharID() != charID)
				{
						Logging
							.debugPrint("CA for "
								+ theCharacter.getName()
								+ ". Ignoring granted ability removed for character "
								+ dfce.getCharID());
					return;
				}
				Logging.debugPrint("Got granted ability removed of " + dfce.getCDOMObject());
				Ability ability = dfce.getCDOMObject();
				rebuildAbilityLists();
			}
		});
		directAbilityFacet.addDataFacetChangeListener(new DataFacetChangeListener<CategorizedAbilitySelection>()
		{

			public void dataAdded(
				DataFacetChangeEvent<CategorizedAbilitySelection> dfce)
			{
				if (dfce.getCharID() != charID)
				{
					// The change notification is not for this character, so ignore it.
					Logging.debugPrint("CA for " + theCharacter.getName()
						+ ". Ignoring direct ability added for character "
						+ dfce.getCharID());
					return;
				}
				CategorizedAbilitySelection cas = dfce.getCDOMObject();
				Logging.debugPrint("Got direct ability added of "
					+ cas.getAbilityKey() + " for cat "
					+ cas.getAbilityCategory());
				addElement(cas);
				updateAbilityCategoryLater(cas.getAbilityCategory());
			}

			public void dataRemoved(
				DataFacetChangeEvent<CategorizedAbilitySelection> dfce)
			{
				if (dfce.getCharID() != charID)
				{
					// The change notification is not for this character, so ignore it.
					return;
				}
				CategorizedAbilitySelection cas = dfce.getCDOMObject();
				Logging.debugPrint("Got direct ability removed of "
					+ cas.getAbilityKey() + " for cat "
					+ cas.getAbilityCategory());
				removeElement(cas);
				updateAbilityCategoryLater(cas.getAbilityCategory());
			}
		});
	}

	void addAbilityToLists(AbilityCategory cat, Ability ability, Nature nature)
	{
		addCategorisedAbility(cat, ability,nature);
		if (!activeCategories.containsElement(cat))
		{
			int index = getCatIndex(cat);
			activeCategories.addElement(index, cat);
		}
		else
		{
			adviseSelectionChangeLater(cat);				
		}
		updateAbilityCategoryLater(cat);		
	}

	void removeAbilityFromLists(AbilityCategory cat,
		Ability ability, Nature nature)
	{
		removeCategorisedAbility(cat, ability, nature);
		
		boolean stillActive =
				!theCharacter.getAutomaticAbilityList(cat).isEmpty()
					|| !theCharacter.getRealAbilitiesList(cat).isEmpty()
					|| !theCharacter.getVirtualAbilityList(cat).isEmpty()
					|| theCharacter.getAvailableAbilityPool(cat).intValue() > 0;
		if (!stillActive && activeCategories.containsElement(cat))
		{
			activeCategories.removeElement(cat);
		}
		else
		{
			adviseSelectionChangeLater(cat);				
		}
		updateAbilityCategoryLater(cat);		
	}
	
	/**
	 * Rebuild the ability lists for the character to include the character's 
	 * current abilities.
	 */
	void rebuildAbilityLists()
	{
		clearLists();
		ListFacade<AbilityCategoryFacade> categories = dataSetFacade.getAbilityCategories();
		for (AbilityCategoryFacade category : categories)
		{
			AbilityCategory cat = (AbilityCategory) category;
			boolean found = false;
			for (Ability ability : theCharacter.getAutomaticAbilityList(cat))
			{
				addCategorisedAbility(cat, ability, Nature.AUTOMATIC);
				found = true;
			}
			for (Ability ability : theCharacter.getRealAbilitiesList(cat))
			{
				addCategorisedAbility(cat, ability, Nature.NORMAL);
				found = true;
			}
			for (Ability ability : theCharacter.getVirtualAbilityList(cat))
			{
				addCategorisedAbility(cat, ability, Nature.VIRTUAL);
				found = true;
			}
			
			// Show the category if the character has pool points left
			found |= theCharacter.getAvailableAbilityPool(cat).intValue() > 0;

			// Finally  deal with visibility
			switch (cat.getVisibility())
			{
				case HIDDEN:
				case OUTPUT_ONLY:
					found = false;
					break;

				case DEFAULT:
				case DISPLAY_ONLY:
					found = true;
					break;

				case QUALIFY:
				default:
					break;
			}
			
			if (found && !activeCategories.containsElement(cat))
			{
				int index = getCatIndex(cat);
				activeCategories.addElement(index, cat);
			}
			if (!found && activeCategories.containsElement(cat))
			{
				activeCategories.removeElement(cat);
				updateAbilityCategoryTodo(cat);
			}
			
			if (found)
			{
				adviseSelectionChangeLater(cat);
			}
		}
	}


	private void updateAbilityCategoryTodo(Category<Ability> cat)
	{
		if (!(cat instanceof AbilityCategory))
		{
			return;
		}
		AbilityCategory category = (AbilityCategory) cat;
		
		int numSelections = theCharacter.getAvailableAbilityPool(category).intValue();
		if (category.getVisibility() == Visibility.HIDDEN
			|| category.getVisibility() == Visibility.OUTPUT_ONLY)
		{
			// Hide todos for categories that should not be displayed
			numSelections = 0;
		}				

		if (numSelections < 0)
		{
			todoManager.addTodo(new TodoFacadeImpl(
				CharacterTab.FeatsAbilitiesTab, category.getDisplayName(),
				"in_featTodoTooMany", category.getType(), 1));
			todoManager.removeTodo("in_featTodoRemain", category.getDisplayName());
		}
		else if (numSelections > 0)
		{
			todoManager.addTodo(new TodoFacadeImpl(
				CharacterTab.FeatsAbilitiesTab, category.getDisplayName(),
				"in_featTodoRemain", category.getType(), 1));
			todoManager.removeTodo("in_featTodoTooMany", category.getDisplayName());
		}
		else
		{
			todoManager.removeTodo("in_featTodoRemain", category.getDisplayName());
			todoManager.removeTodo("in_featTodoTooMany", category.getDisplayName());
		}
	}
	

	/**
	 * Determine where the ability category should be added to the active 
	 * category list. This will keep the activate categories in the same sort 
	 * order as the activity category list. 
	 * @param abilityCategory The category being added 
	 * @return The index at which to insert the category.
	 */
	private int getCatIndex(AbilityCategory abilityCategory)
	{
		ListFacade<AbilityCategoryFacade> allCategories = dataSetFacade.getAbilityCategories();
		int index = 0;
		for (int i = 0; i < allCategories.getSize(); i++)
		{
			AbilityCategoryFacade compCat = allCategories.getElementAt(i);
			if (compCat == abilityCategory || index >= activeCategories.getSize())
			{
				break;
			}
			if (activeCategories.getElementAt(index) == compCat)
			{
				index++;
			}
		}
		return index;
	}

	/**
	 * Add the ability to the categorised list held by CharacterAbilities. 
	 * One copy will be added for each choice.
	 * 
	 * @param cat The AbilityCategory that the ability is being added to.
	 * @param ability The ability being added.
	 * @param nature The nature via which the ability is being added.
	 */
	private void addCategorisedAbility(AbilityCategory cat,
		Ability ability, Nature nature)
	{
		List<CategorizedAbilitySelection> cas = new ArrayList<CategorizedAbilitySelection>();
		if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			List<String> choices = theCharacter.getAssoc(ability, AssociationKey.ASSOC_CHOICES);
			if (choices == null || choices.isEmpty())
			{
				cas.add(new CategorizedAbilitySelection(
						null, cat, ability, nature, ""));
			}
			else
			{
				for (String choice : choices)
				{
					cas.add(new CategorizedAbilitySelection(
						null, cat, ability, nature, choice));
				}
			}
			
		}
		else
		{
			cas.add(new CategorizedAbilitySelection(null, cat, ability,
				nature));
		}
		for (CategorizedAbilitySelection sel : cas)
		{
			addElement(sel);
		}
	}

	private void removeCategorisedAbility(AbilityCategory cat,
		Ability ability, Nature nature)
	{
		CategorizedAbilitySelection cas;
		if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			cas = new CategorizedAbilitySelection(
					null, cat, ability, nature, "");
		}
		else
		{
			cas = new CategorizedAbilitySelection(null,  cat, ability, nature);
		}
		removeElement(cas);
	}
	
	/**
	 * Process a request by the user to add an ability. The user will be informed 
	 * if the request cannot be allowed. Updates to the displayed lists are 
	 * handled by events (see initForCharacter).
	 * 
	 * @param categoryFacade The category in which the ability s bing added.
	 * @param abilityFacade The ability to be added.
	 */
	public void addAbility(AbilityCategoryFacade categoryFacade,
		AbilityFacade abilityFacade)
	{
		if (abilityFacade == null || !(abilityFacade instanceof Ability)
			|| categoryFacade == null
			|| !(categoryFacade instanceof AbilityCategory))
		{
			return;
		}

		Ability ability = (Ability) abilityFacade;
		AbilityCategory category = (AbilityCategory) categoryFacade;
		if (!checkAbilityQualify(ability, category))
		{
			return;
		}


		// we can only be here if the PC can add the ability
		try
		{
			theCharacter.setDirty(true);

			if (category == AbilityCategory.FEAT)
			{
				AbilityUtilities.modAbility(theCharacter, ability, null, AbilityCategory.FEAT);
			}
			else
			{
				addPCAbility(category, ability);
			}
		}
		catch (Exception exc)
		{
			Logging.errorPrint("Failed to add ability due to ", exc);
			ShowMessageDelegate.showMessageDialog(LanguageBundle
				.getFormattedString("in_iayAddAbility", exc.getMessage()),
				Constants.APPLICATION_NAME, MessageType.ERROR);
		}


		// Recalc the innate spell list
		theCharacter.getSpellList();

		theCharacter.aggregateFeatList();

		theCharacter.calcActiveBonuses();

		// update the ability info
		rebuildAbilityLists();
	}

	private void addPCAbility(AbilityCategory category, final Ability anAbility)
	{
		theCharacter.getSpellList();

		Ability pcAbility =
				theCharacter.addAbilityNeedCheck(category, anAbility);

		AbilityUtilities.finaliseAbility(pcAbility, Constants.EMPTY_STRING,
			theCharacter, category);
	}
	
	/**
	 * Process a request by the user to remove an ability. The user will be  
	 * informed if the request cannot be allowed. Updates to the displayed 
	 * lists are handled by events (see initForCharacter).
	 * 
	 * @param categoryFacade The category from which the ability is being removed.
	 * @param abilityFacade The ability to be removed.
	 */
	public void removeAbility(AbilityCategoryFacade categoryFacade,
		AbilityFacade abilityFacade)
	{
		if (abilityFacade == null || !(abilityFacade instanceof Ability)
			|| categoryFacade == null
			|| !(categoryFacade instanceof AbilityCategory))
		{
			return;
		}

		Ability anAbility = (Ability) abilityFacade;
		AbilityCategory theCategory = (AbilityCategory) categoryFacade;

		try
		{
			theCharacter.setDirty(true);
			if (!theCharacter.isImporting())
			{
				theCharacter.getSpellList();
			}

			Ability pcAbility =
					theCharacter.getMatchingAbility(theCategory, anAbility,
						Nature.NORMAL);

			if (pcAbility != null)
			{
				// how many sub-choices to make
				double abilityCount =
						(theCharacter
							.getSelectCorrectedAssociationCount(pcAbility) * pcAbility
							.getSafe(ObjectKey.SELECTION_COST).doubleValue());

				boolean adjustedAbilityPool = false;

				// adjust the associated List
				if (pcAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
				{
					if ("".equals(null) || null == null)
					{
						// Get modChoices to adjust the associated list and Feat Pool
						adjustedAbilityPool =
								ChooserUtilities.modChoices(pcAbility,
									new ArrayList(), new ArrayList(), true,
									theCharacter, false, theCategory);
					}
					else
					{
						theCharacter.removeAssociation(pcAbility, null);
					}
				}

				// if no sub choices made (i.e. all of them removed in Chooser box),
				// then remove the Feat
				boolean removed = false;
				boolean result =
						pcAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED)
							? theCharacter.hasAssociations(pcAbility) : false;

				if (!result)
				{
					removed =
							theCharacter.removeRealAbility(theCategory,
								pcAbility);
					CDOMObjectUtilities.removeAdds(pcAbility, theCharacter);
					CDOMObjectUtilities
						.restoreRemovals(pcAbility, theCharacter);
				}

				if (!adjustedAbilityPool
					&& (theCategory == AbilityCategory.FEAT))
				{
					AbilityUtilities.adjustPool(pcAbility, theCharacter, false,
						abilityCount, removed);
				}

				theCharacter.adjustMoveRates();
			}
		}
		catch (Exception exc)
		{
			Logging.errorPrintLocalised("in_iayFailedToRemoveAbility", exc);
			delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle
				.getString("in_iayRemoveAbility")
				+ ": " + exc.getMessage());
			return;
		}

		// Called for side effects
		theCharacter.aggregateFeatList();

		theCharacter.calcActiveBonuses();

		// update the ability info
		rebuildAbilityLists();
		return;
	}

	/**
	 * Retrieve the list of abilities for this category. The list
	 * will be updated when abilities are added and removed.
	 *  
	 * @param category The ability category to be retrieved.
	 * @return The list of abilities.
	 */
	public ListFacade<AbilityFacade> getAbilities(AbilityCategoryFacade category)
	{
		DefaultListFacade<AbilityFacade> abList = abilityListMap.get(category);
		if (abList == null)
		{
			abList = new DefaultListFacade<AbilityFacade>();
			abilityListMap.put(category, abList);
		}
		return abList;
	}
	
	/**
	 * @return The list of active ability categories.
	 */
	public ListFacade<AbilityCategoryFacade> getActiveAbilityCategories()
	{
		return activeCategories;
	}

	/**
	 * Get the total number of selections for this category.
	 * @param categoryFacade The ability category to be retrieved.
	 * @return The total number of choices.
	 */
	public int getTotalSelections(AbilityCategoryFacade categoryFacade)
	{
		if (categoryFacade == null
			|| !(categoryFacade instanceof AbilityCategory))
		{
			return 0;
		}

		AbilityCategory category = (AbilityCategory) categoryFacade;
		BigDecimal pool = theCharacter.getTotalAbilityPool(category);
		return pool.intValue();
	}

	/**
	 * Get the number of selections that are remaining for this category.
	 * @param categoryFacade The ability category to be retrieved.
	 * @return The number of choices left.
	 */
	public int getRemainingSelections(AbilityCategoryFacade categoryFacade)
	{
		if (categoryFacade == null
			|| !(categoryFacade instanceof AbilityCategory))
		{
			return 0;
		}

		AbilityCategory category = (AbilityCategory) categoryFacade;
		BigDecimal pool = theCharacter.getAvailableAbilityPool(category);
		return pool.intValue();
	}

	/**
	 * Set the number of selections that are remaining for this category.
	 * @param categoryFacade The ability category to be set.
	 * @param remaining The number of choices left.
	 */
	public void setRemainingSelection(AbilityCategoryFacade categoryFacade,
		int remaining)
	{
		if (categoryFacade == null
			|| !(categoryFacade instanceof AbilityCategory))
		{
			return;
		}

		AbilityCategory category = (AbilityCategory) categoryFacade;
		BigDecimal pool = theCharacter.getAvailableAbilityPool(category);

		final BigDecimal newRemain = new BigDecimal(remaining);
		if (pool.equals(newRemain))
		{
			return;
		}
		
		theCharacter.adjustAbilities(category, newRemain
			.subtract(pool));
	}

	/**
	 * Check if the character has an ability.
	 * @param category The ability category to be checked. 
	 * @param ability The ability to be checked.
	 * @return true if the character has the ability, false otherwise.
	 */
	public boolean hasAbility(AbilityCategoryFacade category,
		AbilityFacade ability)
	{
		DefaultListFacade<AbilityFacade> abList = abilityListMap.get(category);
		if (abList == null)
		{
			return false;
		}
		return abList.containsElement(ability);

	}

	/**
	 * Retrieves the choices the character has made for an ability. 
	 * 
	 * @param abilityFacade The ability to be reported.
	 * @return The human readable string of the choices made.
	 */
	public String getChoices(AbilityFacade abilityFacade)
	{
		if (!(abilityFacade instanceof Ability))
		{
			return "";
		}

		final Ability ability = (Ability) abilityFacade;
		StringBuilder result = new StringBuilder();

		if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			ChooseInformation<?> chooseInfo =
					ability.get(ObjectKey.CHOOSE_INFO);

			if (chooseInfo != null)
			{
				result.append(chooseInfo.getDisplay(theCharacter, ability));
			}
			else
			{
				result.append(StringUtil.joinToStringBuffer(
					theCharacter.getExpandedAssociations(ability), ","));
			}
		}
		return result.toString();
	}

	/**
	 * Register a listener to be advised of potential changes in the number of 
	 * selections for an ability category. 
	 * @param listener The class to be advised of a change.
	 */
	public void addAbilityCatSelectionListener(ChangeListener listener)
	{
		abilityCatSelectionListeners.add(listener);
	}

	/**
	 * Deregister a listener that should no longer be advised of potential changes
	 * in the number of selections for an ability category. 
	 * @param listener The class to no longer be advised of a change.
	 */
	public void removeAbilityCatSelectionListener(ChangeListener listener)
	{
		abilityCatSelectionListeners.remove(listener);
	}
	
	/**
	 * Advise any listeners that the number of selections may have changed. 
	 * @param cat The ability category that may have changed.
	 */
	private void fireAbilityCatSelectionUpdated(AbilityCategory cat)
	{
		ChangeEvent event = null;
		for (ChangeListener listener : abilityCatSelectionListeners)
		{
			if (event == null)
			{
				event = new ChangeEvent(cat);
			}
			listener.ItemChanged(event);
		}
	}
	
	/**
	 * After any other processing has finished, advise any listeners that 
	 * the number of selections may have changed. 
	 * @param cat The ability category that may have changed.
	 */
	private void adviseSelectionChangeLater(final AbilityCategory cat)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				updateAbilityCategoryTodo(cat);
				fireAbilityCatSelectionUpdated(cat);
			}
		});
	}
	
	/**
	 * After any other processing has finished, refresh the todo information. 
	 * This occurs as category totals are updated after we are notified of the 
	 * abilities being added or removed.
	 * @param category The ability category that may have changed.
	 */
	private void updateAbilityCategoryLater(final Category<Ability> category)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				updateAbilityCategoryTodo(category);
			}
		});
	}
	
	private boolean checkAbilityQualify(final Ability anAbility,
		AbilityCategory theCategory)
	{
		final String aKey = anAbility.getKeyName();
		Ability ability = theCharacter.getAbilityKeyed(theCategory, aKey);

		final boolean pcHasIt = (ability != null);

		if (pcHasIt && !ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle
				.getString("InfoAbility.Messages.Duplicate"));
			return false;
		}

		ability =
				Globals.getContext().ref.silentlyGetConstructedCDOMObject(
					Ability.class, theCategory, aKey);
		if (ability != null && !ability.qualifies(theCharacter, ability)
			&& !Globals.checkRule(RuleConstants.FEATPRE))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle
				.getString("InfoAbility.Messages.NotQualified"));
			return false;
		}

		if ((ability != null))
		{
			final BigDecimal cost = ability.getSafe(ObjectKey.SELECTION_COST);
			if (cost.compareTo(theCharacter
				.getAvailableAbilityPool(theCategory)) > 0)
			{
				delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle
					.getString("InfoAbility.Messages.NoPoints"));
				return false;
			}
		}

		return true;
	}

	private void addElement(CategorizedAbilitySelection cas)
	{
		Ability ability = cas.getAbility();
		if (!ability.getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE,
			false))
		{
			// Filter out hidden abilities
			return;
		}
		AbilityCategoryFacade cat = (AbilityCategoryFacade) cas.getAbilityCategory();
		DefaultListFacade<AbilityFacade> listFacade = abilityListMap.get(cat);
		if (listFacade == null)
		{
			listFacade = new  DefaultListFacade<AbilityFacade>();
			abilityListMap.put(cat, listFacade);
		}
		listFacade.addElement(ability);
	}

	private void removeElement(CategorizedAbilitySelection cas)
	{
		Ability ability = cas.getAbility();
		AbilityCategoryFacade cat = (AbilityCategoryFacade) cas.getAbilityCategory();
		DefaultListFacade<AbilityFacade> listFacade = abilityListMap.get(cat);
		if (listFacade != null)
		{
			listFacade.removeElement(ability);
		}
	}

	private void clearLists()
	{
		for (AbilityCategoryFacade cat : abilityListMap.keySet())
		{
			abilityListMap.get(cat).clearContents();
		}
	}
}
