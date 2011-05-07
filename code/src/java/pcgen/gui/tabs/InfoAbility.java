/*
 * InfoAbility.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 * 
 * based on InfoFeats.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.DisplayLocation;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfo;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.tabs.ability.AbilityInfoPanel;
import pcgen.gui.tabs.ability.AbilityPoolPanel;
import pcgen.gui.tabs.ability.AbilitySelectionPanel;
import pcgen.gui.tabs.ability.AvailableAbilityPanel;
import pcgen.gui.tabs.ability.IAbilityCategorySelectionListener;
import pcgen.gui.tabs.ability.IAbilitySelectionListener;
import pcgen.gui.tabs.ability.SelectedAbilityPanel;
import pcgen.gui.utils.PObjectNode;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Tab;

/**
 * <code>InfoAbility</code>.
 * This class is responsible for drawing an ability related window - including
 * indicating which abilities are available, which ones are selected, and handling
 * the selection/de-selection of abilities.
 *
 * @author boomer70 <boomer70@yahoo.com>
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * 
 * @version $Revision$
 */
public final class InfoAbility extends BaseCharacterInfoTab implements
		IAbilitySelectionListener, IAbilityCategorySelectionListener
{
	private static final Tab tab = Tab.ABILITIES;

	private static final String NO_QUALIFY_MESSAGE =
			PropertyFactory.getString("InfoAbility.Messages.NotQualified"); //$NON-NLS-1$
	private static final String DUPLICATE_MESSAGE =
			PropertyFactory.getString("InfoAbility.Messages.Duplicate"); //$NON-NLS-1$
	private static String POOL_FULL_MESSAGE =
			PropertyFactory.getString("InfoAbility.Messages.NoPoints"); //$NON-NLS-1$
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;

	private static final int ABILITY_OK = 0;
	private static final int ABILITY_DUPLICATE = 1;
	private static final int ABILITY_NOT_QUALIFIED = 2;
	private static final int ABILITY_FULL = 3;

	private AbilityCategory theCategory;

	private AbilitySelectionPanel theAvailablePane = null;
	private AbilitySelectionPanel theSelectedPane = null;
	private AbilityInfoPanel theInfoPanel = null;
	private AbilityPoolPanel thePoolPanel = null;

	private FlippingSplitPane splitBotLeftRight = null;
	private FlippingSplitPane splitTopBot = null;
	private FlippingSplitPane splitTopLeftRight = null;
	private JCheckBox chkViewAll = new JCheckBox();

	/** This flag is used to signal that we have set the divider locations */
	private boolean hasBeenSized = false;

	private String theOptionKey = "InfoAbility."; //$NON-NLS-1$

	private DisplayLocation theDisplayLocation;
	private List<AbilityCategory> categoryList;

	/**
	 * Constructor
	 * 
	 * @param pc The PC this ability information is displayed for.
	 * @param aCategory The category of ability this tab is displaying.
	 */
	public InfoAbility(PlayerCharacter pc, final AbilityCategory aCategory)
	{
		super(pc);
		theCategory = aCategory;
		theDisplayLocation = theCategory.getDisplayLocation();
		theOptionKey += theDisplayLocation;

		setName(theDisplayLocation.toString());

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				initComponents();
				initActionListeners();
			}
		});
	}

	/**
	 * @see pcgen.gui.tabs.BaseCharacterInfoTab#getTabOrder()
	 */
	@Override
	public int getTabOrder()
	{
		// TODO - This doesn't seem to be used.
		final String opt = ".Panel." + theOptionKey + ".Order"; //$NON-NLS-1$ //$NON-NLS-2$
		return SettingsHandler.getPCGenOption(opt, tab.ordinal());
	}

	/**
	 * @see pcgen.gui.tabs.BaseCharacterInfoTab#setTabOrder(int)
	 */
	@Override
	public void setTabOrder(int order)
	{
		// TODO - This doesn't seem to be used.
		final String opt = ".Panel." + theOptionKey + ".Order"; //$NON-NLS-1$ //$NON-NLS-2$
		SettingsHandler.setPCGenOption(opt, order);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * 
	 * <p>Checks to see if any abilities of this type have to be added or 
	 * removed.
	 * 
	 * @return List of task descriptions as Strings.
	 * 
	 * @see pcgen.gui.tabs.BaseCharacterInfoTab#getToDos()
	 */
	@Override
	public List<String> getToDos()
	{
		List<String> toDoList = new ArrayList<String>();
		if (getPc() != null)
		{
			for (AbilityCategory cat : categoryList)
			{
				if (cat.isVisible(getPc()))
				{
					final BigDecimal pool =
							getPc().getTotalAbilityPool(cat);
					final int dir =
							pool.compareTo(getPc().getAbilityPoolSpent(cat));
					if (dir > 0)
					{
						toDoList.add(PropertyFactory.getFormattedString(
							"in_featTodoRemain", cat.getPluralName())); //$NON-NLS-1$
					}
					else if (dir < 0)
					{
						toDoList.add(PropertyFactory.getFormattedString(
							"in_featTodoTooMany", cat.getPluralName())); //$NON-NLS-1$
					}
				}
			}
		}

		return toDoList;
	}

	/**
	 * Specifies whether the "match any" option should be available.
	 *   
	 * @return Always returns <tt>true</tt>.
	 * 
	 * @see pcgen.gui.filter.FilterAdapterPanel#isMatchAnyEnabled()
	 */
	@Override
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * Specifies whether the "negate/reverse" option should be available.
	 * 
	 * @return Always returns <tt>true</tt>.
	 * 
	 * @see pcgen.gui.filter.FilterAdapterPanel#isNegateEnabled()
	 */
	@Override
	public boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * Specifies the filter selection mode.
	 * 
	 * @return Always returns <tt>FilterConstants.MULTI_MULTI_MODE = 2</tt>
	 * 
	 * @see pcgen.gui.filter.FilterAdapterPanel#getSelectionMode()
	 */
	@Override
	public int getSelectionMode()
	{
		return FilterConstants.MULTI_MULTI_MODE;
	}

	/**
	 * Implementation of Filterable interface.
	 * 
	 * <p>Returns filters for Source and Feat.
	 * 
	 * @see pcgen.gui.filter.FilterAdapterPanel#initializeFilters()
	 */
	@Override
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllFeatFilters(this);
	}

	/**
	 * Implementation of Filterable interface.
	 * 
	 * <p>Forces an update of the whole tab.
	 * 
	 * @see pcgen.gui.filter.FilterAdapterPanel#refreshFiltering()
	 */
	@Override
	public void refreshFiltering()
	{
		forceRefresh();
	}

	private int checkAbilityQualify(final Ability anAbility)
	{
		final PlayerCharacter pc = getPc();

		final String aKey = anAbility.getKeyName();
		Ability ability = pc.getAbilityKeyed(theCategory, aKey);

		final boolean pcHasIt = (ability != null);

		if (pcHasIt && !ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			return ABILITY_DUPLICATE;
		}

		ability = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(Ability.class, theCategory, aKey);
		if (ability != null && !ability.qualifies(pc, ability)
				&& !Globals.checkRule(RuleConstants.FEATPRE))
		{
			return ABILITY_NOT_QUALIFIED;
		}

		if ((ability != null))
		{
			final BigDecimal cost = ability.getSafe(ObjectKey.SELECTION_COST);
			if (cost.compareTo(pc.getAvailableAbilityPool(theCategory)) > 0)
			{
				return ABILITY_FULL;
			}
		}

		return ABILITY_OK;
	}

	/**
	 * This is called when the tab is shown.
	 */
	private void formComponentShown()
	{
		requestFocus();

		PCGen_Frame1.setMessageAreaTextWithoutSaving(PropertyFactory
			.getString("InfoAbility.StatusLine.Info")); //$NON-NLS-1$

		//TODO: These setPC	calls may no longer be required now that they are also done in updateCharacterInfo
		if (theAvailablePane != null)
		{
			theAvailablePane.setPC(getPc());
		}
		if (theSelectedPane != null)
		{
			theSelectedPane.setPC(getPc());
		}
		if (theInfoPanel != null)
		{
			theInfoPanel.setPC(getPc());
		}
		if (thePoolPanel != null)
		{
			thePoolPanel.setPC(getPc());
		}

		refresh();

		if (!hasBeenSized)
		{
			hasBeenSized = true;
			if (splitTopLeftRight != null)
			{
				final int s =
						SettingsHandler.getPCGenOption(theOptionKey
							+ ".splitTopLeftRight", //$NON-NLS-1$
							(int) ((this.getSize().getWidth() * 6) / 10));
				splitTopLeftRight.setDividerLocation(s);
			}
			if (splitTopBot != null)
			{
				final int t =
						SettingsHandler.getPCGenOption(theOptionKey
							+ ".splitTopBot", //$NON-NLS-1$
							(int) ((this.getSize().getHeight() * 75) / 100));
				splitTopBot.setDividerLocation(t);
			}
			if (splitBotLeftRight != null)
			{
				final int u =
						SettingsHandler.getPCGenOption(theOptionKey
							+ ".splitBotLeftRight", //$NON-NLS-1$
							(int) ((this.getSize().getWidth() * 6) / 10));
				splitBotLeftRight.setDividerLocation(u);
			}
		}
	}

	private void initActionListeners()
	{
		chkViewAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(@SuppressWarnings("unused")
			ActionEvent evt)
			{
				chkViewAllActionPerformed();
			}
		});
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentShown(@SuppressWarnings("unused")
			ComponentEvent evt)
			{
				formComponentShown();
			}
		});

		FilterFactory.restoreFilterSettings(this);
	}

	private void initComponents()
	{
		if (Globals.getGameModeHasPointPool())
		{
			//			FEAT_FULL_MESSAGE  = "You do not have enough remaining " + Globals.getGameModePointPoolName() + " to select this " + getSingularTabName() + ".";
		}

		Collection<AbilityCategory> categoryCol =
				SettingsHandler.getGame().getAllAbilityCatsForDisplayLoc(
					theDisplayLocation);
		categoryList = new ArrayList<AbilityCategory>(categoryCol);
		boolean editable = false;
		for (AbilityCategory cat : categoryList)
		{
			if (cat.isEditable())
			{
				editable = true;
				break;
			}
		}
		final JPanel topPane = new JPanel();
		topPane.setLayout(new BorderLayout());

		//-------------------------------------------------------------
		// Top Pane - Left Available, Right Selected
		//
		if (editable)
		{
			theAvailablePane = new AvailableAbilityPanel(getPc(), theCategory);
			theAvailablePane.addAbilitySelectionListener(this);
			theAvailablePane.addFilterer(this);
		}
		theSelectedPane = new SelectedAbilityPanel(getPc(), categoryList);
		theSelectedPane.addAbilitySelectionListener(this);
		theSelectedPane.addAbilityCategorySelectionListener(this);
		theSelectedPane.addFilterer(this);

		if (editable)
		{
			splitTopLeftRight =
					new FlippingSplitPane(splitOrientation, theAvailablePane,
						theSelectedPane);
			splitTopLeftRight.setOneTouchExpandable(true);
			splitTopLeftRight.setDividerSize(10);
			// Register a listener so that we can save the location each time it
			// changes.
			splitTopLeftRight.addPropertyChangeListener(
				JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new PropertyChangeListener()
				{
					public void propertyChange(PropertyChangeEvent anEvt)
					{
						SettingsHandler.setPCGenOption(theOptionKey
							+ ".splitTopLeftRight", //$NON-NLS-1$ 
							anEvt.getNewValue().toString());
					}
				});

			topPane.add(splitTopLeftRight, BorderLayout.CENTER);
		}
		else
		{
			topPane.add(theSelectedPane, BorderLayout.CENTER);
		}

		//-------------------------------------------------------------
		// Bottom Pane - Left Info, Right Options / Data
		//
		final JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());

		theInfoPanel =
				new AbilityInfoPanel(getPc(), PropertyFactory
					.getFormattedString(
						"InfoAbility.Title", theCategory.getDisplayName())); //$NON-NLS-1$

		// Pool panel
		thePoolPanel = new AbilityPoolPanel(getPc(), categoryList);
		thePoolPanel.addAbilityCategorySelectionListener(this);

		splitBotLeftRight =
				new FlippingSplitPane(splitOrientation, theInfoPanel,
					thePoolPanel);

		splitBotLeftRight.setOneTouchExpandable(true);

		splitBotLeftRight.setDividerSize(10);
		// Register a listener so that we can save the location each time it
		// changes.
		splitBotLeftRight.addPropertyChangeListener(
			JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent anEvt)
				{
					SettingsHandler.setPCGenOption(theOptionKey
						+ ".splitBotLeftRight", //$NON-NLS-1$ 
						anEvt.getNewValue().toString());
				}
			});

		botPane.add(splitBotLeftRight, BorderLayout.CENTER);
//		}
//		else
//		{
//			botPane.add(theInfoPanel, BorderLayout.CENTER);
//		}

		//----------------------------------------------------------------------
		// Split Top and Bottom
		splitTopBot =
				new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, topPane,
					botPane);
		splitTopBot.setOneTouchExpandable(true);
		splitTopBot.setDividerSize(10);
		// Register a listener so that we can save the location each time it
		// changes.
		splitTopBot.addPropertyChangeListener(
			JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent anEvt)
				{
					SettingsHandler.setPCGenOption(theOptionKey
						+ ".splitTopBot", //$NON-NLS-1$ 
						anEvt.getNewValue().toString());
				}
			});

		this.setLayout(new BorderLayout());
		this.add(splitTopBot, BorderLayout.CENTER);

		addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(@SuppressWarnings("unused")
			FocusEvent evt)
			{
				refresh();
			}
		});
	}

	private void updateAvailableModel()
	{
		if (theAvailablePane != null)
		{
			theAvailablePane.setPC(getPc());
			theAvailablePane.setCategory(theCategory);
			theAvailablePane.update();
		}
	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently selected character.
	 * 
	 * @see pcgen.gui.tabs.BaseCharacterInfoTab#updateCharacterInfo()
	 */
	@Override
	protected void updateCharacterInfo()
	{
		// TODO - Fix this!!!!
		PObjectNode.resetPC(getPc());

		if ((getPc() == null) || needsUpdate() == false)
		{
			return;
		}

		if (theCategory == AbilityCategory.FEAT)
		{
			// Called for side effects
			getPc().aggregateFeatList();
		}

		if (theInfoPanel != null)
		{
			theInfoPanel.setPC(getPc());
			theInfoPanel.setCategory(theCategory);
			theInfoPanel.setAbility(null);
		}

		updateAvailableModel();
		updateSelectedModel();

		if (thePoolPanel != null)
		{
			thePoolPanel.setPC(getPc());
			thePoolPanel.setCategory(theCategory);
			thePoolPanel.showRemainingAbilityPoints();
		}
		setNeedsUpdate(false);
	}

	private void updateSelectedModel()
	{
		if (theSelectedPane != null)
		{
			theSelectedPane.setPC(getPc());
			theSelectedPane.update();
		}
	}

	private void chkViewAllActionPerformed()
	{
		updateSelectedModel();
	}

	/**
	 * @see pcgen.gui.tabs.BaseCharacterInfoTab#getTab()
	 */
	@Override
	protected Tab getTab()
	{
		return tab;
	}

	/**
	 * @see pcgen.gui.tabs.BaseCharacterInfoTab#getTabName()
	 */
	@Override
	public String getTabName()
	{
		return theCategory.getDisplayLocation().toString();
	}

	/**
	 * @see pcgen.gui.tabs.ability.IAbilitySelectionListener#abilitySelected(pcgen.core.Ability)
	 */
	public void abilitySelected(final Ability anAbility)
	{
		theInfoPanel.setAbility(anAbility);
	}

	/**
	 * Adds an ability to the character if it is legal for the character to
	 * take that ability.
	 * 
	 * @see pcgen.gui.tabs.ability.IAbilitySelectionListener#addAbility(pcgen.core.Ability)
	 */
	public boolean addAbility(final Ability anAbility)
	{
		// TODO - There has to be a better way to do this.
		final int aq = checkAbilityQualify(anAbility);

		switch (aq)
		{
			case ABILITY_NOT_QUALIFIED:
				ShowMessageDelegate.showMessageDialog(NO_QUALIFY_MESSAGE,
					Constants.APPLICATION_NAME, MessageType.INFORMATION);

				return false;

			case ABILITY_DUPLICATE:
				ShowMessageDelegate.showMessageDialog(DUPLICATE_MESSAGE,
					Constants.APPLICATION_NAME, MessageType.INFORMATION);

				return false;

			case ABILITY_FULL:
				ShowMessageDelegate.showMessageDialog(POOL_FULL_MESSAGE,
					Constants.APPLICATION_NAME, MessageType.INFORMATION);

				return false;

			case ABILITY_OK:

				// Feat is OK, so do nothing
				break;

			default:
				Logging
					.debugPrint(theCategory.getDisplayName()
						+ " " + anAbility.getDisplayName() + " " + PropertyFactory.getString("in_iayIsSomehowInState") + " " + aq //$NON-NLS-1$ //$NON-NLS-2$
						+ " " + PropertyFactory.getString("in_iayWhichIsNotHandeledInIAaddAb")); //$NON-NLS-1$

				break;
		}

		// we can only be here if the PC can add the ability
		try
		{
			getPc().setDirty(true);

			if (theCategory == AbilityCategory.FEAT)
			{
				AbilityUtilities.modAbility(getPc(), anAbility, null, AbilityCategory.FEAT);
			}
			else
			{
				addPCAbility(anAbility);
			}
		}
		catch (Exception exc)
		{
			Logging.errorPrint("Failed to add ability due to ", exc);
			ShowMessageDelegate.showMessageDialog(PropertyFactory
				.getFormattedString("in_iayAddAbility", exc.getMessage()),
				Constants.APPLICATION_NAME, MessageType.ERROR);
		}


		// Recalc the innate spell list
		getPc().getSpellList();

		// update the skills tab, as feats could effect totals
		CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoSkills());
		pane.setPaneForUpdate(pane.infoInventory());
		pane.setPaneForUpdate(pane.infoSpells());
		pane.setPaneForUpdate(pane.infoSummary());
		pane.refresh();

		getPc().aggregateFeatList();
		updateAvailableModel();
		updateSelectedModel();

		// TODO - Make sure this is handled elsewhere.
		//		setAddEnabled(false);

		getPc().calcActiveBonuses();

		thePoolPanel.showRemainingAbilityPoints();
		return true;
	}

	private void addPCAbility(final Ability anAbility)
	{
		PlayerCharacter pc = getPc();
		if (!pc.isImporting())
		{
			pc.getSpellList();
		}
		
		Ability pcAbility = pc.addAbilityNeedCheck(theCategory, anAbility);

		if (pcAbility != anAbility) //yes != not !.equals
		{
			pc.selectTemplates(pcAbility, pc.isImporting());
		}

		AbilityUtilities.finaliseAbility(pcAbility, Constants.EMPTY_STRING,
				pc, theCategory);
	}

	/**
	 * @see pcgen.gui.tabs.ability.IAbilitySelectionListener#removeAbility(pcgen.core.Ability)
	 */
	public boolean removeAbility(final Ability anAbility)
	{
		try
		{
			getPc().setDirty(true);
			PlayerCharacter aPC = getPc();
			if (!aPC.isImporting())
			{
				aPC.getSpellList();
			}

			Ability pcAbility = aPC.getMatchingAbility(theCategory, anAbility,
					Nature.NORMAL);

			if (pcAbility != null)
			{
				// how many sub-choices to make
				double abilityCount = (aPC.getSelectCorrectedAssociationCount(pcAbility) * pcAbility.getSafe(ObjectKey.SELECTION_COST).doubleValue());
				
				boolean adjustedAbilityPool = false;
				
				// adjust the associated List
				if (pcAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
				{
					if ("".equals(null) || null == null)
					{
						// Get modChoices to adjust the associated list and Feat Pool
						adjustedAbilityPool = ChooserUtilities.modChoices(
						pcAbility,
						new ArrayList(),
						new ArrayList(),
						true,
						aPC,
						false,
						theCategory);
					}
					else
					{
						aPC.removeAssociation(pcAbility, null);
					}
				}

				// if no sub choices made (i.e. all of them removed in Chooser box),
				// then remove the Feat
				boolean removed = false;
				boolean result  = pcAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED) ? aPC.hasAssociations(pcAbility) : false ; 
				
				if (! result)
				{
					removed = aPC.removeRealAbility(theCategory, pcAbility);
					aPC.removeTemplatesFrom(pcAbility);
					CDOMObjectUtilities.removeAdds(pcAbility, aPC);
					CDOMObjectUtilities.restoreRemovals(pcAbility, aPC);
				}
				
				if (!adjustedAbilityPool && (theCategory == AbilityCategory.FEAT))
				{
					AbilityUtilities.adjustPool(pcAbility, aPC, false, abilityCount, removed);
				}
				
				aPC.adjustMoveRates();
			}
		}
		catch (Exception exc)
		{
			Logging.errorPrintLocalised("in_iayFailedToRemoveAbility", exc);
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_iayRemoveAbility") + ": "
				+ exc.getMessage(), Constants.APPLICATION_NAME, MessageType.ERROR);
			return false;
		}

		// update the skills tab, as feats could effect totals
		CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoSkills());
		pane.setPaneForUpdate(pane.infoInventory());
		pane.setPaneForUpdate(pane.infoSpells());
		pane.setPaneForUpdate(pane.infoSummary());
		pane.refresh();

		// Called for side effects
		getPc().aggregateFeatList();

		updateAvailableModel();
		updateSelectedModel();

		getPc().calcActiveBonuses();
		thePoolPanel.showRemainingAbilityPoints();
		// TODO - Make sure this is handled by the removal
		//		setRemoveEnabled(false);
		return true;
	}
	
	public void setCurrentActivityCategory(AbilityCategory cat)
	{
		theCategory = cat;
		forceRefresh();
	}

	public void abilityCategorySelected(AbilityCategory anAbilityCat)
	{
		if (theCategory != anAbilityCat)
		{
			setCurrentActivityCategory(anAbilityCat);
		}
	}
}
