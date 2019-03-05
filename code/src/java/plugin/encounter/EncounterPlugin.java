/*
 * Copyright 2003 (C) Devon Jones
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
package plugin.encounter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.HandsFacet;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.util.CControl;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.character.EquipSet;
import pcgen.core.character.EquipSlot;
import pcgen.core.display.CharacterDisplay;
import pcgen.gui2.tools.Utility;
import pcgen.pluginmgr.InteractivePlugin;
import pcgen.pluginmgr.PCGenMessage;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.messages.FocusOrStateChangeOccurredMessage;
import pcgen.pluginmgr.messages.TransmitInitiativeValuesBetweenComponentsMessage;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import plugin.encounter.gui.EncounterView;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.io.ReadXML;
import gmgen.io.VectorTable;
import gmgen.plugin.InitHolderList;
import gmgen.plugin.PcgCombatant;
import gmgen.plugin.dice.Dice;
import gmgen.pluginmgr.messages.AddMenuItemToGMGenToolsMenuMessage;
import gmgen.pluginmgr.messages.RequestAddTabToGMGenMessage;

/**
 * This class controls the various classes that are
 * involved in the functionality of the Encounter Generator.  This {@code class
 * } is a plugin for the {@code GMGenSystem}, is called by the
 * {@code PluginLoader} and will create a model and a view for this plugin.
 */
public class EncounterPlugin extends MouseAdapter implements InteractivePlugin, ActionListener, ItemListener
{
	/** Directory where Data for this plug-in is expected to be. */
	private static final String DIR_ENCOUNTER = "encounter_tables"; //$NON-NLS-1$

	/** Name of the log */
	public static final String LOG_NAME = "Encounter"; //$NON-NLS-1$

	/** The model that holds all the data for generating encounters. */
	private EncounterModel theModel;

	/** The user interface for the encounter generator. */
	private EncounterView theView;

	/**
	 * The environment that can be selected for the encounter to take place in,
	 */
	private EnvironmentModel theEnvironments;

	/** The list of combatants in the game. */
	private InitHolderList theList;

	/** The plugin menu item in the tools menu. */
	private JMenuItem encounterToolsItem = new JMenuItem();

	/** The list of races that are available for creatures. */
	private RaceModel theRaces;

	/** The English name of the plugin. */
	private static final String NAME = "Encounter"; //$NON-NLS-1$
	/** Key of plugin tab. */
	private static final String IN_NAME = "in_plugin_encounter_name"; //$NON-NLS-1$
	/** Mnemonic in menu for {@link #IN_NAME} */
	private static final String IN_NAME_MN = "in_mn_plugin_encounter_name"; //$NON-NLS-1$

	private PCGenMessageHandler messageHandler;

	/**
	 * Starts the plugin, registering itself with the {@code TabAddMessage}.
	 */
	@Override
	public void start(PCGenMessageHandler mh)
	{
		messageHandler = mh;
		theModel = new EncounterModel();
		theView = new EncounterView();
		theRaces = new RaceModel();
		theList = new InitHolderList();
		createView();

		messageHandler.handleMessage(new RequestAddTabToGMGenMessage(this, getLocalizedName(), getView()));
		initMenus();
	}

	@Override
	public void stop()
	{
		messageHandler = null;
	}

	@Override
	public int getPriority()
	{
		return SettingsHandler.getGMGenOption(LOG_NAME + ".LoadOrder", 30);
	}

	/**
	 * Sets the instance of the model for the encounter generator.
	 * @param theModel the {@code EncounterModel}.
	 */
	public void setModel(EncounterModel theModel)
	{
		this.theModel = theModel;
	}

	/**
	 * Gets the model that holds the data for the encounter generator.
	 * @return the {@code EncounterModel}.
	 */
	public EncounterModel getModel()
	{
		return theModel;
	}

	/**
	 * Accessor for name
	 * @return name
	 */
	@Override
	public String getPluginName()
	{
		return NAME;
	}

	private static String getLocalizedName()
	{
		return LanguageBundle.getString(IN_NAME);
	}

	/**
	 * Gets the list of races for creatures.
	 * @return the list of races.
	 */
	public RaceModel getRaces()
	{
		return theRaces;
	}

	/**
	 * Sets the instance of the view being used for this class.
	 * @param theView the GUI interface.
	 */
	public void setView(EncounterView theView)
	{
		this.theView = theView;
	}

	/**
	 * Gets the {@code JPanel} view associated for this class.
	 * @return the view.
	 */
	public JPanel getView()
	{
		return theView;
	}

	/**
	 * Calls the appropriate methods depending on the source of the event.
	 * @param e the source of the event from the GUI.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == theView.getAddCreature())
		{
			handleAddCreature();
		}
		else if (e.getSource() == theView.getRemoveCreature())
		{
			handleRemoveCreature();
		}
		else if (e.getSource() == theView.getTransferToTracker())
		{
			handleTransferToTracker();
		}
		else if (e.getSource() == theView.getGenerateEncounter())
		{
			handleGenerateEncounter(theModel);
		}
		else
		{
			Logging.errorPrintLocalised("in_plugin_encounter_error_unhandled", //$NON-NLS-1$
				e.getSource());
		}

		updateUI();
	}

	/**
	 * Handles the <b>Generate Encounter</b> button.
	 * @param m the encounter model.
	 */
	public void handleGenerateEncounter(EncounterModel m)
	{
		File f = new File(getDataDirectory() + File.separator + DIR_ENCOUNTER + File.separator + "environments.xml");
		ReadXML xml;

		if (f.exists())
		{
			xml = new ReadXML(f);
		}
		else
		{
			Logging.errorPrintLocalised("in_plugin_encounter_error_missing", f); //$NON-NLS-1$

			return;
		}

		VectorTable environments = xml.getTable();

		theModel.clear();

		if (theView.getEnvironment().getSelectedIndex() == 0)
		{
			generateXofYEL(theView.getNumberOfCreatures().getText(), theView.getTargetEL());
		}
		else
		{
			generateXfromY(
				environments.crossReference(theView.getEnvironment().getSelectedItem().toString(), "File").toString());
		}

		updateUI();
	}

	/**
	 * Handles the <b>Add Creature</b> button.
	 */
	private void handleAddCreature()
	{
		if (!theView.getLibraryCreatures().isSelectionEmpty())
		{
			List<Object> values = theView.getLibraryCreatures().getSelectedValuesList();
			values.forEach(theModel::addElement);

			updateUI();
		}
	}

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 */
	@Override
	public void handleMessage(PCGenMessage message)
	{
		if (message instanceof FocusOrStateChangeOccurredMessage)
		{
			if (isActive())
			{
				updateUI();
				encounterToolsItem.setEnabled(false);
			}
			else
			{
				encounterToolsItem.setEnabled(true);
			}
		}
	}

	/**
	 * True if active
	 * @return True if active
	 */
	public boolean isActive()
	{
		JTabbedPane tp = Utility.getTabbedPaneFor(theView);
		return tp != null && JOptionPane.getFrameForComponent(tp).isFocused()
			&& tp.getSelectedComponent().equals(theView);
	}

	/**
	 * Handles the <b>Remove Creature</b> button.
	 */
	public void handleRemoveCreature()
	{
		if (!theView.getEncounterCreatures().isSelectionEmpty())
		{
			List values = theView.getEncounterCreatures().getSelectedValuesList();
			for (Object value : values)
			{
				theModel.removeElement(value);
			}

			updateUI();
		}
	}

	/**
	 * Handles the <b>Begin Combat</b> button.
	 */
	public void handleTransferToTracker()
	{
		int i;
		PlayerCharacter aPC;
		JFrame oldRoot = Globals.getRootFrame();
		Globals.setRootFrame(GMGenSystem.inst);
		theModel.setPCs(theModel.size());

		try
		{
			for (i = 0; i < theModel.size(); i++)
			{
				aPC = theModel.getPCs()[i];
				aPC.setImporting(false);

				if (!handleRace(aPC, i))
				{
					continue;
				}

				LevelCommandFactory lcf = aPC.getDisplay().getRace().get(ObjectKey.MONSTER_CLASS);

				if (lcf != null)
				{
					handleMonster(aPC, lcf);
				}
				else
				{
					handleNonMonster(aPC);
				}

				handleEquipment(aPC);
				aPC.setPCAttribute(PCStringKey.PLAYERSNAME, "Enemy");
				theList.add(new PcgCombatant(aPC, "Enemy", messageHandler));
			}

			JOptionPane.showMessageDialog(null,
				"You will now be returned to PCGen so that you can finalise your selected combatants.\n"
				+ "Once they are finalised, return to the GMGen Initiative tab to begin the combat!",
				"Combatant Setup Complete", JOptionPane.INFORMATION_MESSAGE);

			messageHandler.handleMessage(new TransmitInitiativeValuesBetweenComponentsMessage(this, theList));
			removeAll();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

		Globals.setRootFrame(oldRoot);
	}

	/**
	 * Initiliase the menus
	 */
	public void initMenus()
	{
		encounterToolsItem.setMnemonic(LanguageBundle.getMnemonic(IN_NAME_MN));
		encounterToolsItem.setText(getLocalizedName());
		encounterToolsItem.addActionListener(EncounterPlugin::toolMenuItem);
		messageHandler.handleMessage(new AddMenuItemToGMGenToolsMenuMessage(this, encounterToolsItem));
	}

	/**
	 * Enables or disables items on the GUI depending on the state of the
	 * model.
	 */
	@Override
	public void itemStateChanged(ItemEvent e)
	{
		if (theView.getEnvironment().getSelectedIndex() == 0)
		{
			theView.getNumberOfCreatures().setEnabled(true);
			theView.getTargetEncounterLevel().setEnabled(true);
			theView.getNumberLabel().setEnabled(true);
			theView.getTargetLabel().setEnabled(true);
		}
		else
		{
			theView.getNumberOfCreatures().setEnabled(false);
			theView.getTargetEncounterLevel().setEnabled(false);
			theView.getNumberLabel().setEnabled(false);
			theView.getTargetLabel().setEnabled(false);
		}
	}

	@Override
	public void mouseClicked(MouseEvent evt)
	{
		if (evt.getSource() == theView.getLibraryCreatures())
		{
			if (evt.getClickCount() == 2)
			{
				int index = theView.getLibraryCreatures().locationToIndex(evt.getPoint());
				ListModel dlm = theView.getLibraryCreatures().getModel();
				Object item = dlm.getElementAt(index);
				theView.getLibraryCreatures().ensureIndexIsVisible(index);
				theModel.addElement(item);
				updateUI();
			}
		}
		else if (evt.getSource() == theView.getEncounterCreatures())
		{
			try
			{
				if (evt.getClickCount() == 2)
				{
					int index = theView.getEncounterCreatures().locationToIndex(evt.getPoint());
					ListModel dlm = theView.getEncounterCreatures().getModel();
					Object item = dlm.getElementAt(index);
					theView.getEncounterCreatures().ensureIndexIsVisible(index);
					theModel.removeElement(item);
					updateUI();
				}
			}
			catch (Exception e)
			{
				//TODO: Should this really be ignored?
			}
		}
	}

	private void createView()
	{
		theEnvironments = new EnvironmentModel(getDataDirectory() + File.separator + DIR_ENCOUNTER);

		theView.getLibraryCreatures().setModel(theRaces);
		theView.getEncounterCreatures().setModel(theModel);
		theView.getEnvironment().setModel(theEnvironments);

		theView.getLibraryCreatures().addMouseListener(this);
		theView.getEncounterCreatures().addMouseListener(this);

		theView.getAddCreature().addActionListener(this);
		theView.getRemoveCreature().addActionListener(this);
		theView.getTransferToTracker().addActionListener(this);
		theView.getGenerateEncounter().addActionListener(this);
		theView.getEnvironment().addItemListener(this);
	}

	/**
	 * Tool item menu
	 * @param evt
	 */
	private static void toolMenuItem(ActionEvent evt)
	{
		JTabbedPane tp = GMGenSystemView.getTabPane();

		for (int i = 0; i < tp.getTabCount(); i++)
		{
			if (tp.getComponentAt(i) instanceof EncounterView)
			{
				tp.setSelectedIndex(i);
			}
		}
	}

	/**
	 * Updates all necessary items if there has been a change.
	 */
	private void updateUI()
	{
		int sel;

		if ((sel = theView.getEnvironment().getSelectedIndex()) == -1)
		{
			sel = 0;
		}

		// Get any currently selected items in the Races list
		List<Object> selected = new ArrayList<>();

		for (int index : theView.getLibraryCreatures().getSelectedIndices())
		{
			selected.add(theRaces.elementAt(index));
		}

		theRaces.update();
		theEnvironments.update();

		//	We need to check that the items in the encounter model still exist in the
		//	Races list - this might be a problem if the loaded sources are changed
		//  IF it is not in the races model then remove it from the encounter model
		//	TODO: This is only a quick fix to clear the encounter list if the 
		//	the sources are changed - it will only remove the items when focus is
		//	returned to this control, 
		for (Object obj : theModel.toArray())
		{
			if (!theRaces.contains(obj))
			{
				theModel.removeElement(obj);
			}
		}

		theView.getEnvironment().setSelectedIndex(sel);
		theView.setTotalEncounterLevel(Integer.toString(theModel.getCR()));

		//	If there are no races in the races model, make sure we cannot accidentally
		//	generate an encounter
		if (theRaces.getSize() > 1)
		{
			theView.getGenerateEncounter().setEnabled(true);
		}
		else
		{
			theView.getGenerateEncounter().setEnabled(false);
		}

		if (theModel.getSize() < 1)
		{
			theView.getTransferToTracker().setEnabled(false);
		}
		else
		{
			theView.getTransferToTracker().setEnabled(true);
		}

		// re-select the selected creatures only if they still exist in 
		//	the Races list - may not if sources have been changed
		List<Integer> stillSelected = new ArrayList<>();

		for (Object obj : selected)
		{
			if (theRaces.contains(obj))
			{
				stillSelected.add(theRaces.indexOf(obj));
			}
		}

		//	convert the ArrayList to an integer array - needed
		//	to select multiple indices
		if (!stillSelected.isEmpty())
		{
			int[] ints = new int[stillSelected.size()];
			for (int i = 0; i < ints.length; i++)
			{
				ints[i] = (stillSelected.get(i)).intValue();
			}

			theView.getLibraryCreatures().setSelectedIndices(ints);
		}
	}

	/**
	 * Gets a monster from the table specified.
	 * @param table the table that the creature will come from.
	 * @return the creature(s).
	 */
	private Vector<?> getMonsterFromTable(String table)
	{
		String tablePath;
		String tableEntry;
		String numMonsters;

		Random roll = new Random(System.currentTimeMillis());

		if (table.startsWith("["))
		{
			tablePath = getDataDirectory() + File.separator + DIR_ENCOUNTER + File.separator
				+ table.substring(1, table.length() - 1);
			Logging.errorPrint("subfile " + tablePath);
		}
		else
		{
			tablePath = table;
		}

		tablePath = tablePath.concat(".xml");

		/*open file*/
		File monsterFile = new File(tablePath);

		if (!monsterFile.exists())
		{
			Logging.errorPrint("could not open " + tablePath);

			return null;
		}

		ReadXML monsterTable = new ReadXML(monsterFile);
		String percent = monsterTable.findPercentageEntry(roll.nextInt(99) + 1);

		/*get item type*/
		tableEntry = monsterTable.getTable().crossReference(percent, "Monster").toString();

		/*get amount of items*/
		numMonsters = monsterTable.getTable().crossReference(percent, "Number").toString();

		/*create items and add to list*/
		if (tableEntry.startsWith("["))
		{
			return getMonsterFromTable(tableEntry.substring(1, tableEntry.length() - 1));
		}

		//TODO This calculation should be done as int and convert to Integer at the end - better speed. thpr 10/19/06
		Integer num;

		try
		{
			num = Integer.valueOf(numMonsters);
		}
		catch (NumberFormatException e)
		{
			String[] dice = numMonsters.split("d");
			num = 0;

			for (int x = 0; x < Integer.parseInt(dice[0]); x++)
			{
				num += roll.nextInt(Integer.parseInt(dice[1])) + 1;
			}
		}

		Vector<Object> toReturn = new Vector<>();
		toReturn.addElement(num);
		toReturn.addElement(
			Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, tableEntry));

		return toReturn;
	}

	private static String getNewIdPath(PlayerCharacter aPC, EquipSet eSet)
	{
		String pid = "0";
		int newID = 0;

		if (eSet != null)
		{
			pid = eSet.getIdPath();
		}

		for (EquipSet es : aPC.getDisplay().getEquipSet())
		{
			if (es.getParentIdPath().equals(pid) && (es.getId() > newID))
			{
				newID = es.getId();
			}
		}

		++newID;

		return pid + '.' + newID;
	}

	/**
	 * This method gets a list of locations for a weapon
	 * @param hands
	 * @param multiHand
	 * @return weapon location choices
	 **/
	private static List<String> getWeaponLocationChoices(int hands, String multiHand)
	{
		List<String> result = new ArrayList<>(hands + 2);

		if (hands > 0)
		{
			result.add(Constants.EQUIP_LOCATION_PRIMARY);

			for (int i = 1; i < hands; ++i)
			{
				if (i > 1)
				{
					result.add(Constants.EQUIP_LOCATION_SECONDARY + ' ' + i);
				}
				else
				{
					result.add(Constants.EQUIP_LOCATION_SECONDARY);
				}
			}

			if (!multiHand.isEmpty())
			{
				result.add(multiHand);
			}
		}

		return result;
	}

	private void addAllToEquipSet(PlayerCharacter aPC, EquipSet eqSet)
	{
		for (Equipment eq : aPC.getDisplay().getEquipmentSet())
		{
			addEquipToTarget(aPC, eqSet, "", eq.clone(), 1.0f);
		}
	}

	private static EquipSet createDefaultEquipset(PlayerCharacter aPC)
	{
		EquipSet eSet;

		if (aPC.getDisplay().hasEquipSet())
		{
			eSet = aPC.getDisplay().getEquipSetByIdPath(EquipSet.DEFAULT_SET_PATH);
		}
		else
		{
			String id = getNewIdPath(aPC, null);
			String defaultEquipSet = "Default Set";
			eSet = new EquipSet(id, defaultEquipSet);
			aPC.addEquipSet(eSet);
			Logging.debugPrint("Adding EquipSet: " + defaultEquipSet);
		}

		return eSet;
	}

	/**
	 * Generates a creature for an encounter given a specified environment.
	 * @param Environment the environment setting that the encounter will
	 *        take place in.
	 */
	private void generateXfromY(String Environment)
	{
		Vector<?> critters;

		critters = getMonsterFromTable(Environment);

		//	If we don't find anything just return.
		if (critters.isEmpty())
		{
			// TODO: Maybe we need a message here to inform the user that nothing was found
			// in the currently selected environment that matches the EL criteria
			Logging.debugPrint("EncounterPlugin - generateXfromY found no matches");
			return;
		}

		for (int x = 0; x < ((Integer) critters.firstElement()).intValue(); x++)
		{
			theModel.addElement(critters.lastElement().toString());
		}
	}

	/**
	 * Generates creatures for an encounter based on a specified Encounter
	 * Level and number of creatures.
	 * @param size the number of creatures needed for encounter.
	 * @param totalEL total experience level.
	 */
	private void generateXofYEL(String size, String totalEL)
	{
		File f = new File(getDataDirectory() + File.separator + DIR_ENCOUNTER + File.separator + "4_1.xml");
		ReadXML xml;
		VectorTable table41;
		Random roll = new Random(System.currentTimeMillis());
		List<Race> critters = new ArrayList<>();

		if (!f.exists())
		{
			Logging.errorPrintLocalised("in_plugin_encounter_error_missing", f); //$NON-NLS-1$

			return;
		}

		xml = new ReadXML(f);

		if ((table41 = xml.getTable()) == null)
		{
			Logging.errorPrint("ACK! error getting table41! " + f.toString());

			return;
		}

		xml = null;
		f = null;

		// verrify values on the table.
		String crs = (String) table41.crossReference(totalEL, size);

		table41 = null;
		if (crs == null)
		{
			Logging.errorPrint("Tables do not match the given parameters (" + totalEL + ", " + size + ')');

			return;
		}

		Formula crFormula = FormulaFactory.getFormulaFor(crs);
		if (!crFormula.isValid())
		{
			Logging.errorPrint("CR Formula " + crs + " was not valid: " + crFormula.toString());
		}
		ChallengeRating cr = new ChallengeRating(crFormula);

		// populate critters with a list of matching monsters with the right CR.
		for (final Race race : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Race.class))
		{
			if (cr.equals(race.get(ObjectKey.CHALLENGE_RATING)))
			{
				critters.add(race);
			}
		}

		int i = roll.nextInt(critters.size());

		for (int x = 0; x < Integer.parseInt(size); x++)
		{
			theModel.addElement(critters.get(i).toString());
		}
	}

	private void handleEquipment(PlayerCharacter aPC)
	{
		EquipSet eqSet = createDefaultEquipset(aPC);
		addAllToEquipSet(aPC, eqSet);
		aPC.setCalcEquipSetId(eqSet.getIdPath());
		aPC.setCalcEquipmentList();
	}

	private static void handleMonster(PlayerCharacter aPC, LevelCommandFactory lcf)
	{
		PCClass cl = lcf.getPCClass();
		int levels = lcf.getLevelCount().resolve(aPC, "").intValue();
		Logging.debugPrint("Monster Class: " + cl.getDisplayName() + " Level: " + levels);
		PCClass pcClass = aPC.getClassKeyed(cl.getKeyName());

		int currentLevels = 0;
		if (pcClass != null)
		{
			currentLevels = aPC.getDisplay().getLevel(pcClass);
		}
		if (currentLevels < levels)
		{
			aPC.incrementClassLevel(levels - currentLevels, cl);
		}
	}

	private static void handleNonMonster(PlayerCharacter aPC)
	{
		PCClass mclass =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class, "Warrior");

		if (mclass != null)
		{
			Logging.debugPrint("Class: " + mclass.getDisplayName() + " Level: 1");
			aPC.incrementClassLevel(1, mclass);
			rollHP(aPC);
		}
	}

	private boolean handleRace(PlayerCharacter aPC, int number)
	{
		Race race = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Race.class,
			(String) theModel.getElementAt(number));

		if (race == null)
		{
			return false;
		}

		aPC.setRace(race);
		aPC.setName(race.toString());

		return true;
	}

	private static List<String> locationChoices(PlayerCharacter pc, Equipment eqI)
	{
		// Some Equipment locations are based on the number of hands
		int hands = 0;
		if (pc != null)
		{
			hands = getHands(pc);
		}

		List<String> aList = new ArrayList<>();

		if (eqI.isWeapon())
		{
			if (eqI.isUnarmed())
			{
				aList.add(Constants.EQUIP_LOCATION_UNARMED);
			}
			else if (eqI.isWeaponLightForPC(pc))
			{
				aList = getWeaponLocationChoices(hands, "");

				if (eqI.isRanged() && !eqI.isThrown())
				{
					aList.add(Constants.EQUIP_LOCATION_BOTH);
				}

				if (eqI.isMelee())
				{
					aList.add(Constants.EQUIP_LOCATION_TWOWEAPONS);
				}
			}
			else
			{
				if (eqI.isWeaponOneHanded(pc))
				{
					aList = getWeaponLocationChoices(hands, Constants.EQUIP_LOCATION_BOTH);

					if (eqI.isMelee())
					{
						if (eqI.isDouble())
						{
							aList.add(Constants.EQUIP_LOCATION_DOUBLE);
						}
						else
						{
							aList.add(Constants.EQUIP_LOCATION_TWOWEAPONS);
						}
					}
				}
				else
				{
					aList.add(Constants.EQUIP_LOCATION_BOTH);

					if (eqI.isMelee() && eqI.isDouble())
					{
						aList.add(Constants.EQUIP_LOCATION_DOUBLE);
					}
				}
			}
		}
		else
		{
			String locName = getSingleLocation(pc, eqI);

			if (!locName.isEmpty())
			{
				aList.add(locName);
			}
			else
			{
				aList.add(Constants.EQUIP_LOCATION_EQUIPPED);
			}
		}

		if (!eqI.isUnarmed())
		{
			aList.add(Constants.EQUIP_LOCATION_CARRIED);
			aList.add(Constants.EQUIP_LOCATION_NOTCARRIED);
		}

		return aList;
	}

	private static int getHands(PlayerCharacter pc)
	{
		String solverValue = pc.getControl(CControl.CREATUREHANDS);
		if (solverValue == null)
		{
			return FacetLibrary.getFacet(HandsFacet.class).getHands(pc.getCharID());
		}
		else
		{
			Object val = pc.getGlobal(solverValue);
			return ((Number) val).intValue();
		}
	}

	/**
	 * If an item can only go in one location, return the name of that
	 * location to add to an EquipSet
	 * @param pc
	 * @param eqI
	 * @return single location
	 **/
	private static String getSingleLocation(PlayerCharacter pc, Equipment eqI)
	{
		// Handle natural weapons
		if (eqI.isNatural())
		{
			if (eqI.getSlots(pc) == 0)
			{
				if (eqI.modifiedName().endsWith("Primary"))
				{
					return Constants.EQUIP_LOCATION_NATURAL_PRIMARY;
				}
				return Constants.EQUIP_LOCATION_NATURAL_SECONDARY;
			}
		}

		// Always force weapons to go through the chooser dialog
		List<EquipSlot> eqSlotList = SystemCollections.getUnmodifiableEquipSlotList();

		if ((eqSlotList == null) || eqSlotList.isEmpty())
		{
			return "";
		}

		for (EquipSlot es : eqSlotList)
		{
			// see if this EquipSlot can contain this item TYPE
			if (es.canContainType(eqI.getType()))
			{
				return es.getSlotName();
			}
		}

		return "";
	}

	private String getEquipLocation(PlayerCharacter pc, EquipSet eSet, String locName, Equipment eqI)
	{
		String location = locName;

		if ("".equals(location) || (location.isEmpty()))
		{
			// get the possible locations for this item
			List<String> aList = locationChoices(pc, eqI);
			location = getSingleLocation(pc, eqI);

			if (!((!location.isEmpty()) && canAddEquip(pc, eSet, location, eqI)))
			{
				// let them choose where to put the item
				List<String> selectedList = new ArrayList<>();
				selectedList = Globals.getChoiceFromList("Select a location for " + eqI.getName(), aList, selectedList,
					1, false, true, pc);
				if (!selectedList.isEmpty())
				{
					location = selectedList.get(0);
				}
			}
		}

		if ("".equals(location) || (location.isEmpty()))
		{
			return null;
		}

		// make sure we can add item to that slot in this EquipSet
		if (!canAddEquip(pc, eSet, location, eqI))
		{
			JOptionPane.showMessageDialog(null, "Can not equip " + eqI.getName() + " to " + location, "GMGen",
				JOptionPane.ERROR_MESSAGE);

			return null;
		}

		return location;
	}

	private static boolean canAddEquip(PlayerCharacter pc, EquipSet eSet, String locName, Equipment eqI)
	{
		String idPath = eSet.getIdPath();

		// If Carried/Equipped/Not Carried slot
		// allow as many as they would like
		if (locName.startsWith(Constants.EQUIP_LOCATION_CARRIED)
			|| locName.startsWith(Constants.EQUIP_LOCATION_EQUIPPED)
			|| locName.startsWith(Constants.EQUIP_LOCATION_NOTCARRIED))
		{
			return true;
		}

		// allow as many unarmed items as you'd like
		if (eqI.isUnarmed())
		{
			return true;
		}

		// allow many Secondary Natural weapons
		if (locName.equals(Constants.EQUIP_LOCATION_NATURAL_SECONDARY))
		{
			return true;
		}

		// Don't allow weapons that are too large for PC
		if (eqI.isWeaponTooLargeForPC(pc))
		{
			return false;
		}

		// make a HashMap to keep track of the number of each
		// item that is already equipped to a slot
		Map<String, String> slotMap = new HashMap<>();

		for (EquipSet eqSet : pc.getDisplay().getEquipSet())
		{
			if (!eqSet.getParentIdPath().startsWith(idPath))
			{
				continue;
			}

			// check to see if we already have
			// an item in that particular location
			if (eqSet.getName().equals(locName))
			{
				Equipment eItem = eqSet.getItem();
				String nString = slotMap.get(locName);
				int existNum = 0;

				if (nString != null)
				{
					existNum = Integer.parseInt(nString);
				}

				if (eItem != null)
				{
					existNum += eItem.getSlots(pc);
				}

				slotMap.put(locName, String.valueOf(existNum));
			}
		}

		for (EquipSet eqSet : pc.getDisplay().getEquipSet())
		{
			if (!eqSet.getParentIdPath().startsWith(idPath))
			{
				continue;
			}

			// if it's a weapon we have to do some
			// checks for hands already in use
			if (eqI.isWeapon())
			{
				// weapons can never occupy the same slot
				if (eqSet.getName().equals(locName))
				{
					return false;
				}

				// if Double Weapon or Both Hands, then no
				// other weapon slots can be occupied
				if ((locName.equals(Constants.EQUIP_LOCATION_BOTH) || locName.equals(Constants.EQUIP_LOCATION_DOUBLE)
					|| locName.equals(Constants.EQUIP_LOCATION_TWOWEAPONS))
					&& (eqSet.getName().equals(Constants.EQUIP_LOCATION_PRIMARY)
						|| eqSet.getName().equals(Constants.EQUIP_LOCATION_SECONDARY)
						|| eqSet.getName().equals(Constants.EQUIP_LOCATION_BOTH)
						|| eqSet.getName().equals(Constants.EQUIP_LOCATION_DOUBLE)
						|| eqSet.getName().equals(Constants.EQUIP_LOCATION_TWOWEAPONS)))
				{
					return false;
				}

				// inverse of above case
				if ((locName.equals(Constants.EQUIP_LOCATION_PRIMARY)
					|| locName.equals(Constants.EQUIP_LOCATION_SECONDARY))
					&& (eqSet.getName().equals(Constants.EQUIP_LOCATION_BOTH)
						|| eqSet.getName().equals(Constants.EQUIP_LOCATION_DOUBLE)
						|| eqSet.getName().equals(Constants.EQUIP_LOCATION_TWOWEAPONS)))
				{
					return false;
				}
			}

			// If we already have an item in that location
			// check to see how many are allowed in that slot
			if (eqSet.getName().equals(locName))
			{
				final String nString = slotMap.get(locName);
				int existNum = 0;

				if (nString != null)
				{
					existNum = Integer.parseInt(nString);
				}

				existNum += eqI.getSlots(pc);

				EquipSlot eSlot = Globals.getEquipSlotByName(locName);

				if (eSlot == null)
				{
					return true;
				}

				for (String slotType : eSlot.getContainType())
				{
					if (eqI.isType(slotType))
					{
						// if the item takes more slots, return false
						if (existNum > (eSlot.getSlotCount() + (int) pc.getTotalBonusTo("SLOTS", slotType)))
						{
							return false;
						}
					}
				}

				return true;
			}
		}

		return true;
	}

	private EquipSet addEquipToTarget(PlayerCharacter aPC, EquipSet eSet, String locName, Equipment eqI, Float newQty)
	{
		String location = getEquipLocation(aPC, eSet, locName, eqI);

		// construct the new IdPath
		// new id is one larger than any other id at this path level
		String id = getNewIdPath(aPC, eSet);

		Logging.debugPrint("--addEB-- IdPath:" + id + "  Parent:" + eSet.getIdPath() + " Location:" + location
			+ " eqName:" + eqI.getName() + "  eSet:" + eSet.getName());

		// now create a new EquipSet to add this Equipment item to
		EquipSet newSet = new EquipSet(id, location, eqI.getName(), eqI);

		// set the Quantity of equipment
		eqI.setQty(newQty);
		newSet.setQty(newQty);

		aPC.addEquipSet(newSet);
		aPC.setCurrentEquipSetName(eSet.getName());

		return newSet;
	}

	private void removeAll()
	{
		theModel.removeAllElements();
		theList = new InitHolderList();
		updateUI();
	}

	private static void rollHP(PlayerCharacter aPC)
	{
		CharacterDisplay display = aPC.getDisplay();
		for (PCClass pcClass : display.getClassSet())
		{
			for (int j = 0; j < display.getLevel(pcClass); j++)
			{
				int bonus = (int) aPC.getTotalBonusTo("HD", "MIN")
					+ (int) aPC.getTotalBonusTo("HD", "MIN;CLASS." + pcClass.getKeyName());
				int size = display.getLevelHitDie(pcClass, j + 1).getDie();
				PCClassLevel classLevel = display.getActiveClassLevel(pcClass, j);
				aPC.setHP(classLevel, new Dice(1, size, bonus).roll());
			}
		}

		aPC.setDirty(true);
	}

	/**
	 *  Gets the name of the data directory for Plugin object
	 *
	 *@return    The data directory name
	 */
	@Override
	public File getDataDirectory()
	{
		File dataDir = new File(SettingsHandler.getGmgenPluginDir(), getPluginName());
		return dataDir;
	}
}
