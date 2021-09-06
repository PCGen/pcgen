/**
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
 */
package pcgen.gui2.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.GrantedAbilityFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ChangeEvent;
import pcgen.facade.util.event.ChangeListener;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;
import pcgen.util.enumeration.View;

/**
 * The Class {@code CharacterAbilities} manages the interaction between
 * the core and the user interface for abilities. It listens for changes in 
 * abilities in the core and then updates the lists provided to the UI to
 * reflect the changes. The lists automatically notify any listeners in the 
 * UI of the changes.
 *   
 * 
 * 
 */
public class CharacterAbilities
{

	private final PlayerCharacter theCharacter;
	private final CharacterDisplay charDisplay;
	private final UIDelegate delegate;

	private Map<AbilityCategory, DefaultListFacade<AbilityFacade>> abilityListMap;
	private DefaultListFacade<AbilityCategory> activeCategories;
	private CharID charID;
	private final DataSetFacade dataSetFacade;
	private final List<ChangeListener> abilityCatSelectionListeners;
	private final TodoManager todoManager;
	private GrantedAbilityChangeHandler grantedAbilityChangeHandler;

	/**
	 * Create a new instance of CharacterAbilities for a character.
	 * @param pc The character we are tracking abilities for.
	 * @param delegate The user interface delegate for notifying the user.
	 * @param dataSetFacade The datasets that the character is using.
	 * @param todoManager The user tasks tracker.
	 */
	public CharacterAbilities(PlayerCharacter pc, UIDelegate delegate, DataSetFacade dataSetFacade,
		TodoManager todoManager)
	{
		theCharacter = pc;
		charDisplay = pc.getDisplay();
		this.delegate = delegate;
		this.dataSetFacade = dataSetFacade;
		this.todoManager = todoManager;
		abilityCatSelectionListeners = new ArrayList<>();

		initForCharacter();
	}

	/**
	 * Tidy up character listeners when closing the character. 
	 */
	protected void closeCharacter()
	{
		GrantedAbilityFacet grantedAbilityFacet = FacetLibrary.getFacet(GrantedAbilityFacet.class);
		grantedAbilityFacet.removeDataFacetChangeListener(grantedAbilityChangeHandler);
	}

	private void initForCharacter()
	{
		abilityListMap = new LinkedHashMap<>();
		activeCategories = new DefaultListFacade<>();

		charID = theCharacter.getCharID();
		GrantedAbilityFacet grantedAbilityFacet = FacetLibrary.getFacet(GrantedAbilityFacet.class);

		//theCharacter.getAbilityList(cat, nature)
		rebuildAbilityLists();

		grantedAbilityChangeHandler = new GrantedAbilityChangeHandler();
		grantedAbilityFacet.addDataFacetChangeListener(grantedAbilityChangeHandler);
	}

	/**
	 * Rebuild the ability lists for the character to include the character's 
	 * current abilities.
	 */
	synchronized void rebuildAbilityLists()
	{
		Map<AbilityCategory, DefaultListFacade<AbilityFacade>> workingAbilityListMap = new LinkedHashMap<>();
		DefaultListFacade<AbilityCategory> workingActiveCategories = new DefaultListFacade<>();

		for (AbilityCategory category : dataSetFacade.getAbilities().getKeys())
		{
			for (CNAbility cna : theCharacter.getPoolAbilities(category))
			{
				addCategorisedAbility(cna, workingAbilityListMap);
			}

			// deal with visibility
			boolean visible = category.isVisibleTo(theCharacter, View.VISIBLE_DISPLAY);

			if (visible && !workingActiveCategories.containsElement(category))
			{
				int index = getCatIndex(category, workingActiveCategories);
				workingActiveCategories.addElement(index, category);
			}
			if (!visible && workingActiveCategories.containsElement(category))
			{
				workingActiveCategories.removeElement(category);
				//				updateAbilityCategoryTodo(cat);
			}

			if (visible)
			{
				adviseSelectionChangeLater(category);
			}
		}

		// Update map contents
		workingAbilityListMap.forEach((category, workingListFacade) -> {
			DefaultListFacade<AbilityFacade> masterListFacade = abilityListMap.get(category);
			if (masterListFacade == null)
			{
				abilityListMap.put(category, workingListFacade);
			}
			else
			{
				masterListFacade.updateContentsNoOrder(workingListFacade.getContents());
			}
			updateAbilityCategoryTodo(category);
		});

		Set<AbilityCategory> origCats = new HashSet<>(abilityListMap.keySet());
		for (AbilityCategory category : origCats)
		{
			if (!workingAbilityListMap.containsKey(category))
			{
				if (workingActiveCategories.containsElement(category))
				{
					abilityListMap.get(category).clearContents();
				}
				else
				{
					abilityListMap.remove(category);
				}
				updateAbilityCategoryTodo(category);
			}
		}
		activeCategories.updateContents(workingActiveCategories.getContents());
	}

	private void updateAbilityCategoryTodo(Category<Ability> cat)
	{
		if (!(cat instanceof AbilityCategory category))
		{
			return;
		}

		int numSelections = theCharacter.getAvailableAbilityPool(category).intValue();
		if (category.getVisibility().isVisibleTo(View.HIDDEN_DISPLAY))
		{
			// Hide todos for categories that should not be displayed
			numSelections = 0;
		}

		if (numSelections < 0)
		{
			todoManager.addTodo(
				new TodoFacadeImpl(Tab.ABILITIES, category.getDisplayName(), "in_featTodoTooMany", //$NON-NLS-1$
				category.getType(), 1));
			todoManager.removeTodo("in_featTodoRemain", category.getDisplayName()); //$NON-NLS-1$
		}
		else if (numSelections > 0)
		{
			todoManager.addTodo(
				new TodoFacadeImpl(Tab.ABILITIES, category.getDisplayName(), "in_featTodoRemain", //$NON-NLS-1$
				category.getType(), 1));
			todoManager.removeTodo("in_featTodoTooMany", category.getDisplayName()); //$NON-NLS-1$
		}
		else
		{
			todoManager.removeTodo("in_featTodoRemain", category.getDisplayName()); //$NON-NLS-1$
			todoManager.removeTodo("in_featTodoTooMany", category.getDisplayName()); //$NON-NLS-1$
		}
	}

	/**
	 * Determine where the ability category should be added to the active 
	 * category list. This will keep the activate categories in the same sort 
	 * order as the activity category list. 
	 * @param abilityCategory The category being added 
	 * @return The index at which to insert the category.
	 */
	private int getCatIndex(AbilityCategory abilityCategory, ListFacade<AbilityCategory> catList)
	{
		Set<AbilityCategory> allCategories = dataSetFacade.getAbilities().getKeys();
		int index = 0;
		for (AbilityCategory compCat : allCategories)
		{
			if (compCat == abilityCategory || index >= catList.getSize())
			{
				break;
			}
			if (catList.getElementAt(index) == compCat)
			{
				index++;
			}
		}
		return index;
	}

	/**
	 * Add the ability to the categorized list held by CharacterAbilities. 
	 * One copy will be added for each choice.
	 * @param cna 
	 * @param workingAbilityListMap The map to be adjusted.
	 */
	private void addCategorisedAbility(CNAbility cna,
		Map<AbilityCategory, DefaultListFacade<AbilityFacade>> workingAbilityListMap)
	{
		Ability ability = cna.getAbility();
		List<CNAbilitySelection> cas = new ArrayList<>();
		Category<Ability> cat = cna.getAbilityCategory();
		Nature nature = cna.getNature();
		if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			List<String> choices = theCharacter.getAssociationList(cna);
			if (choices == null || choices.isEmpty())
			{
				Logging.errorPrint("Ignoring Ability: " + ability + " (" + cat + " / " + nature
					+ ") that UI has as added to the PC, but it has no associations");
			}
			else
			{
				for (String choice : choices)
				{
					cas.add(new CNAbilitySelection(CNAbilityFactory.getCNAbility(cat, nature, ability), choice));
				}
			}
		}
		else
		{
			cas.add(new CNAbilitySelection(CNAbilityFactory.getCNAbility(cat, nature, ability)));
		}
		for (CNAbilitySelection sel : cas)
		{
			addElement(workingAbilityListMap, sel);
		}
	}

	/**
	 * Process a request by the user to add an ability. The user will be informed 
	 * if the request cannot be allowed. Updates to the displayed lists are 
	 * handled by events (see initForCharacter).
	 * 
	 * @param category The category in which the ability s bing added.
	 * @param abilityFacade The ability to be added.
	 */
	public void addAbility(AbilityCategory category, AbilityFacade abilityFacade)
	{
		if (abilityFacade == null || !(abilityFacade instanceof Ability ability) || category == null)
		{
			return;
		}

		if (!checkAbilityQualify(ability, category))
		{
			return;
		}

		// we can only be here if the PC can add the ability
		try
		{
			theCharacter.setDirty(true);

			theCharacter.getSpellList();

			CNAbility cna = CNAbilityFactory.getCNAbility(category, Nature.NORMAL, ability);
			AbilityUtilities.driveChooseAndAdd(cna, theCharacter, true);
		}
		catch (Exception exc)
		{
			Logging.errorPrint("Failed to add ability due to ", exc);
			ShowMessageDelegate.showMessageDialog(
				LanguageBundle.getFormattedString("in_iayAddAbility", exc.getMessage()), //$NON-NLS-1$
				Constants.APPLICATION_NAME, MessageType.ERROR);
		}

		// Recalc the innate spell list
		theCharacter.getSpellList();

		theCharacter.calcActiveBonuses();

		// update the ability info
		rebuildAbilityLists();
	}

	/**
	 * Process a request by the user to remove an ability. The user will be  
	 * informed if the request cannot be allowed. Updates to the displayed 
	 * lists are handled by events (see initForCharacter).
	 * 
	 * @param theCategory The category from which the ability is being removed.
	 * @param abilityFacade The ability to be removed.
	 */
	public void removeAbility(AbilityCategory theCategory, AbilityFacade abilityFacade)
	{
		if (abilityFacade == null || !(abilityFacade instanceof Ability anAbility) || theCategory == null)
		{
			return;
		}

		try
		{
			Ability pcAbility = theCharacter.getMatchingAbility(theCategory, anAbility, Nature.NORMAL);

			if (pcAbility != null)
			{
				CNAbility cna = CNAbilityFactory.getCNAbility(theCategory, Nature.NORMAL, anAbility);
				AbilityUtilities.driveChooseAndAdd(cna, theCharacter, false);
				theCharacter.adjustMoveRates();
			}
		}
		catch (Exception exc)
		{
			Logging.errorPrintLocalised("in_iayFailedToRemoveAbility", exc); //$NON-NLS-1$
			delegate.showErrorMessage(
				Constants.APPLICATION_NAME, LanguageBundle.getString("in_iayRemoveAbility") //$NON-NLS-1$
				+ ": " + exc.getMessage());
			return;
		}

		theCharacter.calcActiveBonuses();

		// update the ability info
		rebuildAbilityLists();
	}

	/**
	 * Retrieve the list of abilities for this category. The list
	 * will be updated when abilities are added and removed.
	 *  
	 * @param category The ability category to be retrieved.
	 * @return The list of abilities.
	 */
	public ListFacade<AbilityFacade> getAbilities(AbilityCategory category)
	{
		DefaultListFacade<AbilityFacade> abList = abilityListMap.get(category);
		if (abList == null)
		{
			abList = new DefaultListFacade<>();
			abilityListMap.put(category, abList);
		}
		return abList;
	}

	/**
	 * @return The list of active ability categories.
	 */
	public ListFacade<AbilityCategory> getActiveAbilityCategories()
	{
		return activeCategories;
	}

	/**
	 * Get the total number of selections for this category.
	 * @param category The ability category to be retrieved.
	 * @return The total number of choices.
	 */
	public int getTotalSelections(AbilityCategory category)
	{
		if (category == null)
		{
			return 0;
		}

		BigDecimal pool = theCharacter.getTotalAbilityPool(category);
		return pool.intValue();
	}

	/**
	 * Get the number of selections that are remaining for this category.
	 * @param category The ability category to be retrieved.
	 * @return The number of choices left.
	 */
	public int getRemainingSelections(AbilityCategory category)
	{
		if (category == null)
		{
			return 0;
		}

		BigDecimal pool = theCharacter.getAvailableAbilityPool(category);
		return pool.intValue();
	}

	/**
	 * Set the number of selections that are remaining for this category.
	 * @param category The ability category to be set.
	 * @param remaining The number of choices left.
	 */
	public void setRemainingSelection(AbilityCategory category, int remaining)
	{
		if (category == null)
		{
			return;
		}

		BigDecimal pool = theCharacter.getAvailableAbilityPool(category);

		final BigDecimal newRemain = new BigDecimal(remaining);
		if (pool.equals(newRemain))
		{
			return;
		}

		theCharacter.adjustAbilities(category, newRemain.subtract(pool));
	}

	/**
	 * Check if the character has an ability.
	 * @param category The ability category to be checked. 
	 * @param ability The ability to be checked.
	 * @return true if the character has the ability, false otherwise.
	 */
	public boolean hasAbility(AbilityCategory category, AbilityFacade ability)
	{
		DefaultListFacade<AbilityFacade> abList = abilityListMap.get(category);
		if (abList == null)
		{
			return false;
		}
		return abList.containsElement(ability);

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
		SwingUtilities.invokeLater(() -> {
            updateAbilityCategoryTodo(cat);
            fireAbilityCatSelectionUpdated(cat);
            refreshChoices(cat);
        });
	}

	/**
	 * Signal that any ability that could have choices has been modified. This 
	 * ensures that the choice display is up to date.
	 * @param category The ability category being refreshed.
	 */
	protected void refreshChoices(Category<Ability> category)
	{
		DefaultListFacade<AbilityFacade> listFacade = abilityListMap.get(category);
		if (listFacade == null)
		{
			return;
		}
		for (AbilityFacade abilityFacade : listFacade)
		{
			Ability ability = (Ability) abilityFacade;
			if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
			{
				listFacade.modifyElement(ability);
			}
		}
	}

	private boolean checkAbilityQualify(final Ability anAbility, AbilityCategory theCategory)
	{
		final String aKey = anAbility.getKeyName();
		boolean pcHasIt = theCharacter.hasAbilityKeyed(theCategory, aKey);

		if (pcHasIt && !anAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME,
				LanguageBundle.getString("InfoAbility.Messages.Duplicate")); //$NON-NLS-1$
			return false;
		}

		//TODO Why do we regrab the context-based Ability when an Ability was passed in?
		Ability ability =
				Globals.getContext().getReferenceContext().getManufacturerId(theCategory).getActiveObject(aKey);
		if (ability != null && !ability.qualifies(theCharacter, ability)
			&& (!Globals.checkRule(RuleConstants.FEATPRE) || !AbilityUtilities.isFeat(ability)))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME,
				LanguageBundle.getString("InfoAbility.Messages.NotQualified")); //$NON-NLS-1$
			return false;
		}

		if ((ability != null))
		{
			final BigDecimal cost = ability.getSafe(ObjectKey.SELECTION_COST);
			if (cost.compareTo(theCharacter.getAvailableAbilityPool(theCategory)) > 0)
			{
				delegate.showErrorMessage(Constants.APPLICATION_NAME,
					LanguageBundle.getString("InfoAbility.Messages.NoPoints")); //$NON-NLS-1$
				return false;
			}
		}

		return true;
	}

	private void addElement(Map<AbilityCategory, DefaultListFacade<AbilityFacade>> workingAbilityListMap,
		CNAbilitySelection cnas)
	{
		CNAbility cas = cnas.getCNAbility();
		Ability ability = cas.getAbility();
		if (!ability.getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE_DISPLAY))
		{
			// Filter out hidden abilities
			return;
		}
		AbilityCategory cat = (AbilityCategory) cas.getAbilityCategory();
		DefaultListFacade<AbilityFacade> listFacade = workingAbilityListMap.get(cat);
		if (listFacade == null)
		{
			listFacade = new DefaultListFacade<>();
			workingAbilityListMap.put(cat, listFacade);
		}
		if (!listFacade.containsElement(ability))
		{
			listFacade.addElement(ability);
		}
	}

	/**
	 * The Class {@code GrantedAbilityChangeHandler} responds to changes to
	 * the character's list of granted abilities.
	 */
	private final class GrantedAbilityChangeHandler implements DataFacetChangeListener<CharID, CNAbilitySelection>
	{
		@SuppressWarnings("nls")
		@Override
		public void dataAdded(DataFacetChangeEvent<CharID, CNAbilitySelection> dfce)
		{
			if (dfce.getCharID() != charID)
			{
				//					Logging.debugPrint("CA for " + theCharacter.getName()
				//						+ ". Ignoring granted ability added for character "
				//						+ dfce.getCharID());
				return;
			}
			if (Logging.isDebugMode())
			{
				Logging.debugPrint("Got granted ability added of " + dfce.getCDOMObject());
			}
			rebuildAbilityLists();
		}

		@SuppressWarnings("nls")
		@Override
		public void dataRemoved(DataFacetChangeEvent<CharID, CNAbilitySelection> dfce)
		{
			if (dfce.getCharID() != charID)
			{
				Logging.debugPrint("CA for " + charDisplay.getName()
					+ ". Ignoring granted ability removed for character " + dfce.getCharID());
				return;
			}
			if (Logging.isDebugMode())
			{
				Logging.debugPrint("Got granted ability removed of " + dfce.getCDOMObject());
			}
			rebuildAbilityLists();
		}
	}

}
