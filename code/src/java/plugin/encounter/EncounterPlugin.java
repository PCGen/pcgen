package plugin.encounter;

import gmgen.GMGenSystem;
import gmgen.GMGenSystemView;
import gmgen.io.ReadXML;
import gmgen.io.VectorTable;
import gmgen.plugin.Dice;
import gmgen.plugin.InitHolderList;
import gmgen.plugin.PcgCombatant;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBPlugin;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.InitHolderListSendMessage;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.ToolMenuItemAddMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.filechooser.FileFilter;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.character.EquipSet;
import pcgen.core.character.EquipSlot;
import pcgen.gui.utils.TabbedPaneUtilities;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserRadio;
import plugin.encounter.gui.EncounterView;

/**
 * The <code>EncounterPlugin</code> controlls the various classes that are
 * involved in the functionality of the Encounter Generator.  This <code>class
 * </code> is a plugin for the <code>GMGenSystem</code>, is called by the
 * <code>PluginLoader</code> and will create a model and a view for this plugin.
 * @author Expires 2003
 * @version 2.10
 */
public class EncounterPlugin extends GMBPlugin implements ActionListener,
		ItemListener, MouseListener
{
	/** Name of the log */
	public static final String LOG_NAME = "Encounter";

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
	private String name = "Encounter";

	/** The version number of the plugin. */
	private String version = "01.00.99.01.00";

	/**
	 * Creates an instance of this class creating a new <code>InitHolderList
	 * </code>.
	 */
	public EncounterPlugin()
	{
		super();
	}

	@Override
	public FileFilter[] getFileTypes()
	{
		return null;
	}

	/**
	 * Starts the plugin, registering itself with the <code>TabAddMessage</code>.
	 */
	@Override
	public void start()
	{
		theModel = new EncounterModel(getDataDir());
		theView = new EncounterView();
		theRaces = new RaceModel();
		theList = new InitHolderList();
		createView();

		GMBus.send(new TabAddMessage(this, name, getView(), getPluginSystem()));
		initMenus();
	}

	@Override
	public String getPluginSystem()
	{
		return SettingsHandler.getGMGenOption(LOG_NAME + ".System",
			Constants.SYSTEM_GMGEN);
	}

	@Override
	public int getPluginLoadOrder()
	{
		return SettingsHandler.getGMGenOption(LOG_NAME + ".LoadOrder", 30);
	}

	/**
	 * Sets the instance of the model for the encounter generator.
	 * @param theModel the <code>EncounterModel</code>.
	 */
	public void setModel(EncounterModel theModel)
	{
		this.theModel = theModel;
	}

	/**
	 * Gets the model that holds the data for the encounter generator.
	 * @return the <code>EncounterModel</code>.
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
	public String getName()
	{
		return name;
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
	 * Accessor for version
	 * @return version
	 */
	@Override
	public String getVersion()
	{
		return version;
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
	 * Gets the <code>JPanel</code> view associated for this class.
	 * @return the view.
	 */
	public JPanel getView()
	{
		return theView;
	}

	/**
	 * Calls the appropriate methods depending on the source of the event.
	 * @param e the source of the event from the GUI.
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
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
			Logging.errorPrint("Unhandled ActionEvent: "
				+ e.getSource().toString());
		}

		updateUI();
	}

	/**
	 * Handles the <b>Generate Encounter</b> button.
	 * @param m the encounter model.
	 */
	public void handleGenerateEncounter(EncounterModel m)
	{
		File f =
				new File(getDataDir() + File.separator + "encounter_tables"
					+ File.separator + "environments.xml");
		ReadXML xml;

		if (f.exists())
		{
			xml = new ReadXML(f);
		}
		else
		{
			Logging.errorPrint("handleGenerateEncounter:");
			Logging.errorPrint(f.toString());

			return;
		}

		VectorTable environments = xml.getTable();

		theModel.clear();

		if (theView.getEnvironment().getSelectedIndex() == 0)
		{
			generateXofYEL(theView.getNumberOfCreatures().getText(), theView
				.getTargetEL());
		}
		else
		{
			generateXfromY(environments.crossReference(
				theView.getEnvironment().getSelectedItem().toString(), "File")
				.toString());
		}

		updateUI();
	}

	/**
	 * Handles the <b>Add Creature</b> button.
	 */
	public void handleAddCreature()
	{
		if (!theView.getLibraryCreatures().isSelectionEmpty())
		{
			Object[] values = theView.getLibraryCreatures().getSelectedValues();

			for (int i = 0; i < values.length; i++)
			{
				theModel.addElement(values[i]);
			}

			updateUI();
		}
	}

	/**
	 * listens to messages from the GMGen system, and handles them as needed
	 * @param message the source of the event from the system
	 * @see gmgen.pluginmgr.GMBPlugin#handleMessage(GMBMessage)
	 */
	@Override
	public void handleMessage(GMBMessage message)
	{
		if (message instanceof StateChangedMessage)
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
		JTabbedPane tp = TabbedPaneUtilities.getTabbedPaneFor(theView);
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
			Object[] values =
					theView.getEncounterCreatures().getSelectedValues();

			for (int i = 0; i < values.length; i++)
			{
				theModel.removeElement(values[i]);
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

				LevelCommandFactory lcf = aPC.getRace().get(ObjectKey.MONSTER_CLASS);

				if (lcf != null)
				{
					handleMonster(aPC, lcf);
				}
				else
				{
					handleNonMonster(aPC);
				}

				handleEquipment(aPC);
				aPC.setPlayersName("Enemy");
				theList.add(new PcgCombatant(aPC, "Enemy"));
			}

			JOptionPane
				.showMessageDialog(
					null,
					"You will now be returned to PCGen so that you can finalise your selected combatants.\nOnce they are finalised, return to the GMGen Initiative tab to begin the combat!",
					"Combatant Setup Complete", JOptionPane.INFORMATION_MESSAGE);

			GMBus.send(new InitHolderListSendMessage(this, theList));
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
		encounterToolsItem.setMnemonic('n');
		encounterToolsItem.setText("Encounter Generator");
		encounterToolsItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				toolMenuItem(evt);
			}
		});
		GMBus.send(new ToolMenuItemAddMessage(this, encounterToolsItem));
	}

	/**
	 * Enables or diabales items on the GUI depending on the state of the
	 * model.
	 * @see ItemListener#itemStateChanged(ItemEvent)
	 */
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

	public void mouseClicked(MouseEvent evt)
	{
		if (evt.getSource() == theView.getLibraryCreatures())
		{
			if (evt.getClickCount() == 2)
			{
				int index =
						theView.getLibraryCreatures().locationToIndex(
							evt.getPoint());
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
					int index =
							theView.getEncounterCreatures().locationToIndex(
								evt.getPoint());
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

	public void mouseEntered(MouseEvent e)
	{
		// TODO:  Method doesn't do anything?
	}

	public void mouseExited(MouseEvent e)
	{
		// TODO:  Method doesn't do anything?
	}

	public void mousePressed(MouseEvent e)
	{
		// TODO:  Method doesn't do anything?
	}

	public void mouseReleased(MouseEvent e)
	{
		// TODO:  Method doesn't do anything?
	}

	private void createView()
	{
		theEnvironments = new EnvironmentModel(getDataDir());

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
	public void toolMenuItem(ActionEvent evt)
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
	public void updateUI()
	{
		int sel;

		if ((sel = theView.getEnvironment().getSelectedIndex()) == -1)
		{
			sel = 0;
		}

		// Get any currently selected items in the Races list
		ArrayList<Object> selected = new ArrayList<Object>();

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
				theModel.removeElement(obj);
		}

		theView.getEnvironment().setSelectedIndex(sel);
		theView.setTotalEncounterLevel(Integer.toString(theModel.getCR()));

		//	If there are no races in the the races model, make sure we cannot accidentally
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
		ArrayList<Integer> stillSelected = new ArrayList<Integer>();

		for (Object obj : selected)
		{
			if (theRaces.contains(obj))
			{
				stillSelected.add(theRaces.indexOf(obj));
			}
		}

		//	convert the ArrayList to an integer array - needed
		//	to select multiple indices
		if (stillSelected.size() > 0)
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
	 * @throws FileNotFoundException an exception if there is a non-existant
	 *         file.
	 */
	private Vector<?> getMonsterFromTable(String table)
		throws FileNotFoundException
	{
		String tablePath;
		String tableEntry;
		String numMonsters;

		Random roll = new Random(System.currentTimeMillis());

		if (table.startsWith("["))
		{
			tablePath =
					getDataDir() + File.separator + "encounter_tables"
						+ File.separator
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
		tableEntry =
				monsterTable.getTable().crossReference(percent, "Monster")
					.toString();

		/*get amount of items*/
		numMonsters =
				monsterTable.getTable().crossReference(percent, "Number")
					.toString();

		/*create items and add to list*/
		if (tableEntry.startsWith("["))
		{
			return getMonsterFromTable(tableEntry.substring(1, tableEntry
				.length() - 1));
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
			num = Integer.valueOf(0);

			for (int x = 0; x < Integer.parseInt(dice[0]); x++)
			{
				num =
						Integer.valueOf(num.intValue()
							+ roll.nextInt(Integer.parseInt(dice[1])) + 1);
			}
		}

		Vector<Object> toReturn = new Vector<Object>();
		toReturn.addElement(num);
		toReturn.addElement(Globals.getContext().ref.silentlyGetConstructedCDOMObject(Race.class, tableEntry));

		return toReturn;
	}

	private String getNewIdPath(PlayerCharacter aPC, EquipSet eSet)
	{
		String pid = "0";
		int newID = 0;

		if (eSet != null)
		{
			pid = eSet.getIdPath();
		}

		for (EquipSet es : aPC.getEquipSet())
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
	private static List<String> getWeaponLocationChoices(int hands,
		String multiHand)
	{
		ArrayList<String> result = new ArrayList<String>(hands + 2);

		if (hands > 0)
		{
			result.add(Constants.EQUIP_LOCATION_PRIMARY);

			for (int i = 1; i < hands; ++i)
			{
				if (i > 1)
				{
					result.add(Constants.EQUIP_LOCATION_SECONDARY + " " + i);
				}
				else
				{
					result.add(Constants.EQUIP_LOCATION_SECONDARY);
				}
			}

			if (multiHand.length() > 0)
			{
				result.add(multiHand);
			}
		}

		return result;
	}

	private void addAllToEquipSet(PlayerCharacter aPC, EquipSet eqSet)
	{
		for (Equipment eq : aPC.getEquipmentSet())
		{
			addEquipToTarget(aPC, eqSet, "", eq.clone(), new Float(1));
		}
	}

	private EquipSet createDefaultEquipset(PlayerCharacter aPC)
	{
		EquipSet eSet;

		if (!aPC.hasEquipSet())
		{
			String id = getNewIdPath(aPC, null);
			String defaultEquipSet = "Default Set";
			eSet = new EquipSet(id, defaultEquipSet);
			aPC.addEquipSet(eSet);
			Logging.debugPrint("Adding EquipSet: " + defaultEquipSet);
		}
		else
		{
			eSet = aPC.getEquipSetByIdPath("0.1");
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

		try
		{
			critters = getMonsterFromTable(Environment);
		}
		catch (FileNotFoundException e)
		{
			Logging.errorPrint(e.getMessage(), e);

			return;
		}

		//	If we don't find anything just return.
		if (critters.size() < 1)
		{
			// TODO: Maybe we need a message here to inform the user that nothing was found
			// in the currently selected environment that matches the EL criteria
			Logging
				.debugPrint("EncounterPlugin - generateXfromY found no matches");
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
		File f =
				new File(getDataDir() + File.separator + "encounter_tables"
					+ File.separator + "4_1.xml");
		ReadXML xml;
		VectorTable table41;
		Random roll = new Random(System.currentTimeMillis());
		List<Race> critters = new ArrayList<Race>();

		if (!f.exists())
		{
			Logging.errorPrint("ACK! No FILE! " + f.toString());

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
			Logging.errorPrint("Tables do not match the given parameters ("
				+ totalEL + ", " + size + ")");

			return;
		}

		Formula crFormula = FormulaFactory.getFormulaFor(crs);
		if (!crFormula.isValid())
		{
			Logging.errorPrint("CR Formula " + crs
					+ " was not valid: " + crFormula.toString());
		}
		ChallengeRating cr = new ChallengeRating(crFormula);

		// populate critters with a list of matching monsters with the right CR.
		for (final Race race : Globals.getContext().ref.getConstructedCDOMObjects(Race.class))
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

	private void handleMonster(PlayerCharacter aPC, LevelCommandFactory lcf)
	{
		PCClass cl = lcf.getPCClass();
		int levels = lcf.getLevelCount().resolve(aPC, "").intValue();
		Logging.debugPrint("Monster Class: " + cl.getDisplayName()
				+ " Level: " + levels);
		PCClass pcClass = aPC.getClassKeyed(cl.getKeyName());

		int currentLevels = 0;
		if (pcClass != null)
		{
			currentLevels = aPC.getLevel(pcClass);
		}
		if (currentLevels < levels)
		{
			aPC.incrementClassLevel(levels - currentLevels, cl);
		}
	}

	private void handleNonMonster(PlayerCharacter aPC)
	{
		PCClass mclass = Globals.getContext().ref.silentlyGetConstructedCDOMObject(PCClass.class, "Warrior");

		if (mclass != null)
		{
			Logging.debugPrint("Class: " + mclass.getDisplayName()
				+ " Level: 1");
			aPC.incrementClassLevel(1, mclass);
			rollHP(aPC);
		}
	}

	private boolean handleRace(PlayerCharacter aPC, int number)
	{
		Race race =
				Globals.getContext().ref.silentlyGetConstructedCDOMObject(Race.class, (String) theModel.getElementAt(number));

		if (race == null)
		{
			return false;
		}

		aPC.setRace(race);
		aPC.setName(race.toString());

		return true;
	}

	private final List<String> locationChoices(PlayerCharacter pc, Equipment eqI)
	{
		// Some Equipment locations are based on the number of hands
		int hands = 0;
		if (pc != null)
		{
			hands = pc.getHands();
		}

		List<String> aList = new ArrayList<String>();

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

			if (locName.length() != 0)
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
		List<EquipSlot> eqSlotList =
				SystemCollections.getUnmodifiableEquipSlotList();

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

	private String getEquipLocation(PlayerCharacter pc, EquipSet eSet,
		String locName, Equipment eqI)
	{
		String location = locName;

		if ("".equals(location) || (location.length() == 0))
		{
			// get the possible locations for this item
			List<String> aList = locationChoices(pc, eqI);
			location = getSingleLocation(pc, eqI);

			if (!((location.length() != 0) && canAddEquip(pc, eSet, location,
				eqI)))
			{
				// let them choose where to put the item
				ChooserRadio c = ChooserFactory.getRadioInstance();
				c.setAvailableList(aList);
				c.setVisible(false);
				c.setTitle(eqI.getName());
				c.setMessageText("Select a location for this item");
				c.setVisible(true);
				aList = c.getSelectedList();

				if (aList.size() > 0)
				{
					location = aList.get(0);
				}
			}
		}

		if ("".equals(location) || (location.length() == 0))
		{
			return null;
		}

		// make sure we can add item to that slot in this EquipSet
		if (!canAddEquip(pc, eSet, location, eqI))
		{
			JOptionPane.showMessageDialog(null, "Can not equip "
				+ eqI.getName() + " to " + location, "GMGen",
				JOptionPane.ERROR_MESSAGE);

			return null;
		}

		return location;
	}

	private static boolean canAddEquip(PlayerCharacter pc, EquipSet eSet,
		String locName, Equipment eqI)
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
		HashMap<String, String> slotMap = new HashMap<String, String>();

		for (EquipSet eqSet : pc.getEquipSet())
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

		for (EquipSet eqSet : pc.getEquipSet())
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
				if ((locName.equals(Constants.EQUIP_LOCATION_BOTH)
					|| locName.equals(Constants.EQUIP_LOCATION_DOUBLE) || locName
					.equals(Constants.EQUIP_LOCATION_TWOWEAPONS))
					&& (eqSet.getName().equals(Constants.EQUIP_LOCATION_PRIMARY)
						|| eqSet.getName().equals(Constants.EQUIP_LOCATION_SECONDARY)
						|| eqSet.getName().equals(Constants.EQUIP_LOCATION_BOTH)
						|| eqSet.getName().equals(Constants.EQUIP_LOCATION_DOUBLE)
						|| eqSet.getName().equals(Constants.EQUIP_LOCATION_TWOWEAPONS)))
				{
					return false;
				}

				// inverse of above case
				if ((locName.equals(Constants.EQUIP_LOCATION_PRIMARY) || locName
					.equals(Constants.EQUIP_LOCATION_SECONDARY))
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
						if (existNum > (eSlot.getSlotCount() + (int) pc.getTotalBonusTo(
							"SLOTS", slotType)))
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

	private EquipSet addEquipToTarget(PlayerCharacter aPC, EquipSet eSet,
		String locName, Equipment eqI, Float newQty)
	{
		String location = getEquipLocation(aPC, eSet, locName, eqI);

		// construct the new IdPath
		// new id is one larger than any other id at this path level
		String id = getNewIdPath(aPC, eSet);

		Logging.debugPrint("--addEB-- IdPath:" + id + "  Parent:"
			+ eSet.getIdPath() + " Location:" + location + " eqName:"
			+ eqI.getName() + "  eSet:" + eSet.getName());

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

	private void rollHP(PlayerCharacter aPC)
	{
		for (PCClass pcClass : aPC.getClassSet())
		{
			for (int j = 0; j < aPC.getLevel(pcClass); j++)
			{
				int bonus =
						(int) aPC.getTotalBonusTo("HD", "MIN")
							+ (int) aPC.getTotalBonusTo("HD", "MIN;CLASS."
								+ pcClass.getKeyName());
				int size = aPC.getLevelHitDie(pcClass, j + 1).getDie();
				PCClassLevel classLevel = aPC.getActiveClassLevel(pcClass, j);
				aPC.setHP(classLevel,
					Integer.valueOf(new Dice(1, size, bonus).roll()));
			}
		}

		aPC.setCurrentHP(aPC.hitPoints());
	}
}
