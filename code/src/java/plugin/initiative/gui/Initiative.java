/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  Initiative.java
 *
 *  Created on September 18, 2002, 4:36 PM
 */
package plugin.initiative.gui;

import gmgen.GMGenSystem;
import gmgen.io.SimpleFileFilter;
import gmgen.plugin.Combatant;
import gmgen.plugin.Dice;
import gmgen.plugin.Event;
import gmgen.plugin.InfoCharacterDetails;
import gmgen.plugin.InitHolder;
import gmgen.plugin.InitHolderList;
import gmgen.plugin.PcgCombatant;
import gmgen.plugin.Spell;
import gmgen.plugin.SystemHP;
import gmgen.plugin.SystemInitiative;
import gmgen.pluginmgr.GMBComponent;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.CombatantUpdatedMessage;
import gmgen.util.LogUtilities;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.NumberFormatter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import org.jdom.output.Format;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.analysis.StatAnalysis;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.util.Logging;
import plugin.initiative.AttackModel;
import plugin.initiative.CheckModel;
import plugin.initiative.DiceRollModel;
import plugin.initiative.InitOutputter;
import plugin.initiative.InitiativePlugin;
import plugin.initiative.PObjectModel;
import plugin.initiative.SaveModel;
import plugin.initiative.SpellModel;
import plugin.initiative.XMLCombatant;

/**
 *@author     Devon Jones
 *@since    March 20, 2003
 */
public class Initiative extends javax.swing.JPanel
{
	//** End Dynamic Components **
	//** Other Variables **

	/**  List that contains the list of Combatants.  Kept sorted. */
	public InitHolderList initList = new InitHolderList();
	private Combatant copyCombatant;
	private javax.swing.JButton bAddCombatant;
	private JButton bCast = new JButton();
	private JButton bOpposedSkill = new JButton();
	private javax.swing.JButton bCombatantReRoll;
	private JButton bDamage = new JButton();
	private javax.swing.JButton bDelete;
	private JButton bEvent = new JButton();
	private JButton bHeal = new JButton();
	private JButton bKill = new JButton();
	private javax.swing.JButton bNextInit;
	private JButton bRaise = new JButton();
	private javax.swing.JButton bRefocus;
	private javax.swing.JButton bRoll;
	private JButton bRefresh = new JButton();
	private javax.swing.JButton bDuplicateCombatant = new JButton();

	// End of variables declaration                   
	//** Dynamic Components **
	private JButton bSave = new JButton();
	private JButton bStabilize = new JButton();
	private JCheckBox showDead = new JCheckBox();
	private JCheckBox showEvents = new JCheckBox();
	private javax.swing.JCheckBoxMenuItem tablePopupCBDuration;
	private javax.swing.JCheckBoxMenuItem tablePopupCBHP;
	private javax.swing.JCheckBoxMenuItem tablePopupCBHPMax;
	private javax.swing.JCheckBoxMenuItem tablePopupCBInitiative;
	private javax.swing.JCheckBoxMenuItem tablePopupCBName;
	private javax.swing.JCheckBoxMenuItem tablePopupCBNumber;
	private javax.swing.JCheckBoxMenuItem tablePopupCBPlayer;
	private javax.swing.JCheckBoxMenuItem tablePopupCBPlus;

	//** End Copy & Paste Functions **
	// Variables declaration - do not modify                     
	private javax.swing.JCheckBoxMenuItem tablePopupCBStatus;
	private javax.swing.JCheckBoxMenuItem tablePopupCBType;
	private javax.swing.JLabel lCounter;
	private javax.swing.JPanel buttonPanelTop;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPopupMenu tablePopup;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollEvents;
	private FlippingSplitPane jSplitPane1;
	private javax.swing.JTabbedPane tpaneInfo;
	private javax.swing.JTable combatantTable;
	private javax.swing.JTextArea tpCombatInfo;
	private javax.swing.JToolBar topToolbar;
	private javax.swing.JToolBar bottomToolbar;
	private List columnList = new ArrayList();
	private LogUtilities log;
	private int currentCombat = 1;
	private int currentInit = -1;
	private int round = 0;

	/*
	 *  History:
	 *  March 20, 2003: Cleanup for Version 1.0
	 */

	/**  Creates new form Initiative */
	public Initiative()
	{
		initComponents();
		initDynamicComponents();
		initPrefs();
		initTable();
		initLast();
		addTableListener();
		jSplitPane1.setOneTouchExpandable(true);
	}

	/**
	 * Sets the active Initiative to be the passed in value
	 *
	 * @param init - The new Active Initiative value
	 */
	public void setCurrentInit(int init)
	{
		currentInit = init;

		if (currentInit > 0)
		{
			lCounter.setText(round + " (" + init + ")");
		}
		else
		{
			lCounter.setText("");
		}

		refreshTable();
	}

	/**  Sets current situation for some objects as the default locations, as the program exits */
	public void setExitPrefs()
	{
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ShowDead",
			showDead.isSelected());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
			+ ".ShowEvents", showEvents.isSelected());
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
			+ ".DividerLocation", jSplitPane1.getDividerLocation());
	}

	/**
	 * Set the log
	 * @param log
	 */
	public void setLog(LogUtilities log)
	{
		this.log = log;

		if (tpaneInfo.getTitleAt(0).equals("Combat"))
		{
			tpaneInfo.remove(0);
		}
	}

	/**
	 *  Determines if there is a combatant that has been copied that is ready for pasting
	 *
	 *@return    if there is a combatant ready for pasting
	 */
	public boolean isPastable()
	{
		if (copyCombatant == null)
		{
			return false;
		}

		return true;
	}

	/**
	 *  Looks at each line in the table, and returns an ArrayList of lines that are Selected.
	 *
	 *@return    An ArrayList of currently selected InitHolders
	 */
	public List<InitHolder> getSelected()
	{
		final List<InitHolder> retList = new ArrayList<InitHolder>();

		int j = -1;

		for (int i = 0; i < combatantTable.getRowCount(); i++)
		{
			j++;

			InitHolder iH = initList.get(j);

			if (iH.getStatus().equals("Dead") && !showDead.isSelected())
			{
				i--;

				continue;
			}

			if (iH instanceof Event && !showEvents.isSelected())
			{
				i--;

				continue;
			}

			if (combatantTable.isRowSelected(i))
			{
				retList.add(iH);
			}
		}

		return retList;
	}

	/**
	 *  Looks at each line in the table, and returns an ArrayList of lines that are not Selected.
	 *
	 *@return    An ArrayList of currently selected InitHolders
	 */
	public List<InitHolder> getUnSelected()
	{
		final List<InitHolder> retList = new ArrayList<InitHolder>();

		int j = -1;

		for (int i = 0; i < combatantTable.getRowCount(); i++)
		{
			j++;

			InitHolder iH = initList.get(j);

			if (iH.getStatus().equals("Dead") && !showDead.isSelected())
			{
				i--;

				continue;
			}

			if (iH instanceof Event && !showEvents.isSelected())
			{
				i--;

				continue;
			}

			if (!combatantTable.isRowSelected(i))
			{
				retList.add(iH);
			}
		}

		return retList;
	}

	/**
	 *  Returns a count of the number of lines that are selected in the table.
	 *
	 *@return    Count of the selected lines
	 */
	public int getSelectedCount()
	{
		return getSelected().size();
	}

	/**
	 *  Checks to see if the first current selection is an XMLCombatant
	 *
	 *@return    if the selected line is an XML Combatant
	 */
	public boolean isXMLCombatantSelected()
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);

			return (iH instanceof XMLCombatant);
		}

		return false;
	}

	/**  Calls up the AddCombatant dialog for adding a new combatant */
	public void addCombatant()
	{
		AddCombatant dialog =
				new AddCombatant(JOptionPane.getFrameForComponent(this), true,
					this);
		dialog.setVisible(true);
		refreshTable();
	}

	/**
	 * Add an initiative holder (a combatant)
	 * @param iH
	 */
	public void addInitHolder(InitHolder iH)
	{
		if (iH instanceof Combatant)
		{
			Combatant cbt = (Combatant) iH;

			if (!initList.isUniqueName(cbt.getName()))
			{
				cbt.setName(initList.getUniqueName(cbt.getName()));
			}
			addTab(cbt);
		}

		initList.add(iH);
	}

	/**
	 * Add a new pcg combatant
	 * @param pc
	 * @param type
	 */
	public void addPcgCombatant(PlayerCharacter pc, String type)
	{
		String name = initList.getUniqueName(pc.getName());

		//Changed from != to .equals 10/21/06 thpr
		if (!name.equals(pc.getName()))
		{
			//Means this one is already loaded, so it should be considered a new pc.
			pc.setName(name);
			//TODO:  Is this necessary?  Exactly why?
			pc.setFileName("");
		}

		final PcgCombatant pcgcbt = new PcgCombatant(pc, type);
		initList.add(pcgcbt);
		addTab(pcgcbt);
	}

	/**
	 * <p>
	 * Adds a tab to the <code>tpaneInfo</code> member. All methods adding
	 * character tabs to <code>tpaneInfo</code> should call this method to do
	 * so, as it provides a standard setup for the text panes and installs
	 * hyperlink listeners.
	 * </p>
	 *
	 * @param cbt Combatant to add.
	 */
	public void addTab(final Combatant cbt)
	{
		javax.swing.JTextPane lp = new javax.swing.JTextPane();
		InfoCharacterDetails ic = new InfoCharacterDetails(cbt, lp);
		tpaneInfo.addTab(cbt.getName(), ic.getScrollPane());
		lp.setEditable(false);
		lp.addHyperlinkListener(new HyperlinkListener()
		{
			private Combatant combatant = cbt;

			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				hyperLinkSelected(e, combatant);
			}
		});
	}

	//** End Table CoreUtility Functions **
	//** Preferences Functions **

	/**  Applys preference dialog selects as new preferences, and implements those selections */
	public void applyPrefs()
	{
		//Combat Prefs
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".doCombat", true)
			&& !bottomToolbar.isAncestorOf(bSave))
		{
			bottomToolbar.add(bSave);
		}

		if (!SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".doCombat", true)
			&& bottomToolbar.isAncestorOf(bSave))
		{
			bottomToolbar.remove(bSave);
		}

		//Spell Prefs
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".doSpells", true)
			&& !bottomToolbar.isAncestorOf(bCast))
		{
			bottomToolbar.add(bCast);
			bottomToolbar.add(bEvent);
		}

		if (!SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".doSpells", true)
			&& bottomToolbar.isAncestorOf(bCast))
		{
			bottomToolbar.remove(bCast);
			bottomToolbar.remove(bEvent);
			bottomToolbar.add(showEvents);
		}

		checkAndFixColumns(SettingsHandler.getGMGenOption(
			InitiativePlugin.LOG_NAME + ".doSpells", true), "Dur");

		//Mixed Prefs
		checkAndFixColumns(SettingsHandler.getGMGenOption(
			InitiativePlugin.LOG_NAME + ".doSpells", true)
			|| SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
				+ ".doDeath", true), "Status");

		//HP Prefs
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doHP",
			true)
			&& !bottomToolbar.isAncestorOf(bDamage))
		{
			bottomToolbar.add(bDamage);
			bottomToolbar.add(bHeal);
			bottomToolbar.add(bStabilize);
		}

		if (!SettingsHandler.getGMGenOption(
			InitiativePlugin.LOG_NAME + ".doHP", true)
			&& bottomToolbar.isAncestorOf(bDamage))
		{
			bottomToolbar.remove(bDamage);
			bottomToolbar.remove(bHeal);
			bottomToolbar.remove(bStabilize);
		}

		checkAndFixColumns(SettingsHandler.getGMGenOption(
			InitiativePlugin.LOG_NAME + ".doHP", true), "HP");
		checkAndFixColumns(SettingsHandler.getGMGenOption(
			InitiativePlugin.LOG_NAME + ".doHP", true), "HP Max");
		checkAndFixColumns(SettingsHandler.getGMGenOption(
			InitiativePlugin.LOG_NAME + ".doNumber", true), "#");

		//Death Prefs
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".doDeath", true)
			&& !bottomToolbar.isAncestorOf(bKill))
		{
			bottomToolbar.add(bKill);
			bottomToolbar.add(showDead);
		}

		if (!SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".doDeath", true)
			&& bottomToolbar.isAncestorOf(bKill))
		{
			bottomToolbar.remove(bKill);
			bottomToolbar.remove(showDead);
		}

		initTable();
		refreshTable();
		this.repaint();
	}

	/**  Calls up the CastSpell dialog, passing in the data for the first selected combatant, if there is one*/
	public void castSpell()
	{
		castSpell("");
	}

	/**  Calls up the CastSpell dialog, passing in the data for the first selected combatant, if there is one
	 *   sets the name of the spell as requested.
	 * @param name
	 */
	public void castSpell(String name)
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);
			castSpell(name, iH, null);

			return;
		}

		initList.sort();
		refreshTable();
		castSpell(name, null, null);
	}

	/**
	 * <p>Calls up the CastSpell dialog, passing the data for the indicated combatant (which
	 * may be null), and sets the name to the indicated value.  If SpellModel is present,
	 * it sets the dialog's spell model as well.</p>
	 *
	 * @param name The spell's name; may be empty string.
	 * @param iH <code>InitHolder</code> instance, may be null
	 * @param model <code>SpellModel</code> instance, may be null
	 */
	public void castSpell(String name, InitHolder iH, SpellModel model)
	{
		CastSpell dialog;

		if (iH == null)
		{
			dialog =
					new CastSpell(JOptionPane.getFrameForComponent(this), true,
						this);
		}
		else
		{
			dialog =
					new CastSpell(JOptionPane.getFrameForComponent(this), true,
						this, iH.getPlayer(), iH.getInitiative()
							.getCurrentInitiative());
		}

		if (name != null)
		{
			dialog.setSpellName(name);
		}

		if (model != null)
		{
			dialog.setSpellModel(model);
		}

		dialog.setVisible(true);
		refreshTable();
	}

	/**
	 * Check for dead combatants
	 */
	public void checkDeadTabs()
	{
		for (int i = 0; i < initList.size(); i++)
		{
			InitHolder iH = initList.get(i);

			if (iH.getStatus().equals("Dead"))
			{
				if (showDead.isSelected() && iH instanceof Combatant
					&& (tpaneInfo.indexOfTab(iH.getName()) == -1))
				{
					Combatant cbt = (Combatant) iH;
					addTab(cbt);
				}
				else
				{
					removeTab(iH);
				}
			}
		}
	}

	/**
	 * Set the current initiative holder to dead
	 * @param deadIH
	 */
	public void combatantDied(InitHolder deadIH)
	{
		writeToCombatTabWithRound(deadIH.getName() + " (" + deadIH.getPlayer()
			+ ") Killed");

		for (int i = 0; i < initList.size(); i++)
		{
			InitHolder iH = initList.get(i);
			String cbtType = "";

			if (iH instanceof Combatant)
			{
				Combatant cbt = (Combatant) iH;
				cbtType = cbt.getCombatantType();
			}

			if (cbtType.equals("Enemy") && !iH.getStatus().equals("Dead"))
			{
				return;
			}
		}

		writeToCombatTabWithRound("Combat finished, all enemies killed");
		checkDeadTabs();
	}

	//** End Functions called by dialogs **
	//** Copy & Paste Functions **

	/**  Copys the highlighted combatant by putting a pointer to it in copyCombatant */
	public void copy()
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);

			if (iH instanceof Combatant)
			{
				copyCombatant = (Combatant) iH;
			}
		}

		initList.sort();
		refreshTable();
	}

	/**  Damages the selected combatants */
	public void damageCombatant()
	{
		int subdualType =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
					+ ".Damage.Subdual", PreferencesDamagePanel.DAMAGE_SUBDUAL);

		DamageDialog dialog = new DamageDialog(GMGenSystem.inst, true);
		dialog.setVisible(true);
		dialog.dispose();

		int returnVal = dialog.getReturn();

		if (returnVal == DamageDialog.OK_VALUE)
		{
			int damage = dialog.getDamage();

			if (dialog.getSubdual())
			{
				if (subdualType == PreferencesDamagePanel.DAMAGE_SUBDUAL)
				{
					doSubdual(damage);
				}
				else if (subdualType == PreferencesDamagePanel.DAMAGE_NON_LETHAL)
				{
					doNonLethal(damage);
				}
			}
			else
			{
				doDamage(damage);
			}
		}
	}

	/**  Deletes the selected combatants from the Init List */
	public void deleteCombatant()
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			try
			{
				InitHolder iH = selectedList.remove(0);
				initList.remove(iH);
				removeTab(iH);
			}
			catch (Exception e)
			{
				// TODO:  Exception Needs to be handled
			}
		}

		initList.sort();
		refreshTable();
	}

	/**
	 *  Do an amount of damage to the selected combatants
	 *
	 *@param  damage  The amount of damage to do
	 */
	public void doDamage(int damage)
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			doDamage(damage, selectedList.remove(0));
		}

		initList.sort();
		refreshTable();
	}

	/**
	 * <p>Damages specified combatant.  This allows other methods to damage
	 * combatants who are not necessarily selected at the time.</p>
	 *
	 * @param damage
	 *             Points of damage to do.
	 * @param iH
	 *             InitHolder to damage.
	 */
	public void doDamage(int damage, InitHolder iH)
	{
		if (iH instanceof Combatant)
		{
			Combatant cbt = (Combatant) iH;
			String oldStatus = cbt.getStatus();
			cbt.damage(damage);

			String newStatus = cbt.getStatus();
			writeToCombatTabWithRound(cbt.getName() + " (" + cbt.getPlayer()
				+ ") Took " + damage + " Damage: " + cbt.getHP().getCurrent()
				+ "/" + cbt.getHP().getMax());
			doMassiveDamage(cbt, damage);

			if (!oldStatus.equals(newStatus) && newStatus.equals("Dead"))
			{
				combatantDied(cbt);
			}
		}
	}

	//** End Functions implementing button calls for the bottom toolbar **
	//** Functions called by dialogs **

	/**
	 *  Do an amount of healing to the selected combatants
	 *
	 *@param  heal  The amount of healing to do
	 */
	public void doHeal(int heal)
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);

			if (iH instanceof Combatant)
			{
				Combatant cbt = (Combatant) iH;
				cbt.heal(heal);
				combatantUpdated(cbt);
				writeToCombatTabWithRound(cbt.getName() + " ("
					+ cbt.getPlayer() + ") Gained " + heal + " Healing: "
					+ cbt.getHP().getCurrent() + "/" + cbt.getHP().getMax());
			}
		}

		initList.sort();
		refreshTable();
	}

	/**
	 * Do Massive damage
	 * @param cbt
	 * @param damage
	 */
	public void doMassiveDamage(Combatant cbt, int damage)
	{
		int massiveType =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
					+ ".Damage.Massive.Type",
					PreferencesMassiveDamagePanel.MASSIVE_DND);
		boolean isMassive = false;

		if (massiveType == PreferencesMassiveDamagePanel.MASSIVE_DND)
		{
			isMassive = SystemHP.isDndMassive(cbt, damage);
		}
		else if (massiveType == PreferencesMassiveDamagePanel.MASSIVE_D20_MODERN)
		{
			isMassive = SystemHP.isD20ModernMassive(cbt, damage);
		}
		else if (massiveType == PreferencesMassiveDamagePanel.MASSIVE_HOUSE_HALF)
		{
			isMassive = SystemHP.isHouseHalfMassive(cbt, damage);
		}

		if (isMassive)
		{
			StringBuffer sb = new StringBuffer();
			SavingThrowDialog dialog =
					new SavingThrowDialog(GMGenSystem.inst, true, cbt, 15,
						SavingThrowDialog.FORT_SAVE);
			dialog.setVisible(true);
			dialog.dispose();

			//Show the dialog and get it's results
			int returnVal = dialog.getReturnValue();
			int roll = dialog.getRoll();
			int total = dialog.getTotal();
			int dc = dialog.getDC();

			//Create a message out with the results
			sb.append(dialog.getSaveAbbrev(dialog.getSaveType()));
			sb.append(" save DC " + dc);

			if (roll > 0)
			{
				sb.append(" with a roll of " + (roll + total));
				sb.append(" (" + total + " + Roll: " + roll + ")");
			}

			//write out the results to the combat tab
			if (returnVal == SavingThrowDialog.PASS_OPTION)
			{
				writeToCombatTabWithRound(cbt.getName() + " ("
					+ cbt.getPlayer() + ") Passed a " + sb
					+ " to avoid massive damage effects");
			}
			else if (returnVal == SavingThrowDialog.FAIL_OPTION)
			{
				writeToCombatTabWithRound(cbt.getName() + " ("
					+ cbt.getPlayer() + ") Failed a " + sb
					+ " to avoid massive damage effects");

				//Failure
				int massiveEffect =
						SettingsHandler.getGMGenOption(
							InitiativePlugin.LOG_NAME
								+ ".Damage.Massive.Effect",
							PreferencesMassiveDamagePanel.MASSIVE_EFFECT_KILL);

				if (massiveEffect == PreferencesMassiveDamagePanel.MASSIVE_EFFECT_KILL)
				{
					cbt.kill();
					combatantDied(cbt);
				}
				else if (massiveEffect == PreferencesMassiveDamagePanel.MASSIVE_EFFECT_NEGATIVE)
				{
					SystemHP hp = cbt.getHP();
					int current = hp.getCurrent();
					cbt.damage(current + 1);
				}
				else if (massiveEffect == PreferencesMassiveDamagePanel.MASSIVE_EFFECT_HALF_TOTAL)
				{
					SystemHP hp = cbt.getHP();
					int max = hp.getMax();
					cbt.damage(max / 2);
				}
				else if (massiveEffect == PreferencesMassiveDamagePanel.MASSIVE_EFFECT_HALF_CURRENT)
				{
					SystemHP hp = cbt.getHP();
					int current = hp.getCurrent();
					cbt.damage(current / 2);
				}
			}
		}
		combatantUpdated(cbt);
	}

	/**
	 *  Do an amount of non-lethal damage to the selected combatants
	 *
	 *@param  damage  The amount of damage to do
	 */
	public void doNonLethal(int damage)
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);
			doNonLethal(damage, iH);
		}
	}

	/**
	 * <p>Applies non-lethal combatant.  This allows other methods to damage
	 * combatants who are not necessarily selected at the time.</p>
	 *
	 * @param damage
	 *             Points of damage to do.
	 * @param iH
	 *             InitHolder to damage.
	 */
	public void doNonLethal(int damage, InitHolder iH)
	{
		if (iH instanceof Combatant)
		{
			Combatant cbt = (Combatant) iH;

			boolean isEnough = false;

			if (cbt instanceof XMLCombatant)
			{
				XMLCombatant xmlcbt = (XMLCombatant) cbt;

				if (damage > xmlcbt.getHP().getAttribute().getValue())
				{
					isEnough = true;
				}
			}

			if (cbt instanceof PcgCombatant)
			{
				PcgCombatant pcgcbt = (PcgCombatant) cbt;
				PlayerCharacter pc = pcgcbt.getPC();

				PCStat stat = Globals.getContext().ref
						.getAbbreviatedObject(PCStat.class, "CON");
				if (damage > StatAnalysis.getTotalStatFor(pc, stat))
				{
					isEnough = true;
				}
			}

			if (isEnough)
			{
				StringBuffer sb = new StringBuffer();
				SavingThrowDialog dialog =
						new SavingThrowDialog(GMGenSystem.inst, true, cbt, 15,
							SavingThrowDialog.FORT_SAVE);
				dialog.setVisible(true);
				dialog.dispose();

				//Show the dialog and get it's results
				int returnVal = dialog.getReturnValue();
				int roll = dialog.getRoll();
				int total = dialog.getTotal();
				int dc = dialog.getDC();

				//Create a message out with the results
				sb.append(dialog.getSaveAbbrev(dialog.getSaveType()));
				sb.append(" save DC " + dc);

				if (roll > 0)
				{
					sb.append(" with a roll of " + (roll + total));
					sb.append(" (" + total + " + Roll: " + roll + ")");
				}

				if (returnVal == SavingThrowDialog.PASS_OPTION)
				{
					writeToCombatTabWithRound(cbt.getName() + " ("
						+ cbt.getPlayer() + ") Passed a " + sb
						+ " to avoid unconsiousness");
					cbt.nonLethalDamage(false);
				}
				else if (returnVal == SavingThrowDialog.FAIL_OPTION)
				{
					writeToCombatTabWithRound(cbt.getName() + " ("
						+ cbt.getPlayer() + ") Failed a " + sb
						+ " to avoid unconsiousness");
					cbt.nonLethalDamage(true);
				}
			}
			combatantUpdated(cbt);
		}
	}

	/**
	 * Set the combatant type
	 * @param comType
	 */
	public void doSetCombatantType(String comType)
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);

			if (iH instanceof Combatant)
			{
				Combatant cbt = (Combatant) iH;
				cbt.setCombatantType(comType);
				combatantUpdated(cbt);
			}
		}

		refreshTable();
	}

	/**
	 *  Do an amount of subdual damage to the selected combatants
	 *
	 *@param  damage  The amount of damage to do
	 */
	public void doSubdual(int damage)
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);
			doSubdual(damage, iH);
		}

		initList.sort();
		refreshTable();
	}

	/**
	 * <p>Applies subdual damage to combatant.  This allows other methods to damage
	 * combatants who are not necessarily selected at the time.</p>
	 *
	 * @param damage
	 *             Points of damage to do.
	 * @param iH
	 *             InitHolder to damage.
	 */
	public void doSubdual(int damage, InitHolder iH)
	{
		if (iH instanceof Combatant)
		{
			Combatant cbt = (Combatant) iH;
			cbt.subdualDamage(damage);
			combatantUpdated(cbt);

			writeToCombatTabWithRound(cbt.getName() + " (" + cbt.getPlayer()
				+ ") Took " + damage + " Subdual Damage: "
				+ cbt.getHP().getCurrent() + "(" + cbt.getHP().getSubdual()
				+ "s)/" + cbt.getHP().getMax());
		}
	}

	//** End Combat Tab Functions **
	//** Toolbar & Button CoreUtility Functions **

	/**  Focuses the GUI on the Next Init button */
	public void focusNextInit()
	{
		bNextInit.grabFocus();
	}

	/**  Focuses the GUI on the Roll button */
	public void focusRoll()
	{
		bRoll.grabFocus();
	}

	/**  Heals the selected combatants */
	public void healCombatant()
	{
		String inputValue =
				JOptionPane.showInputDialog(this, "Heal", Integer.toString(1));

		if (inputValue != null)
		{
			try
			{
				doHeal(Integer.parseInt(inputValue));
			}
			catch (NumberFormatException e)
			{
				healCombatant();
			}
		}
	}

	/**  Finishes the initialization, by implementing the last portions of the user's preferences */
	public void initLast()
	{
		int iDividerLocation =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
					+ ".DividerLocation", 400);
		jSplitPane1.setDividerLocation(iDividerLocation);

		//Spell Tracking On
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".doSpells", true))
		{
			bottomToolbar.add(bCast);
			bottomToolbar.add(bEvent);
			bottomToolbar.add(showEvents);
		}

		//Combat Tracking On
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".doCombat", true))
		{
			bottomToolbar.add(bSave);
		}

		//HP Tracking On
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doHP",
			true))
		{
			bottomToolbar.add(bDamage);
			bottomToolbar.add(bHeal);
			bottomToolbar.add(bStabilize);
		}

		//Death Tracking On
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".doDeath", true))
		{
			bottomToolbar.add(bKill);
			bottomToolbar.add(bRaise);
			bottomToolbar.add(showDead);
		}

		boolean bShowDead =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
					+ ".ShowDead", false);
		showDead.setSelected(bShowDead);

		boolean bShowEvents =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
					+ ".ShowEvents", true);
		showEvents.setSelected(bShowEvents);
	}

	/**
	 *  Initialization of the bulk of preferences.  sets the defaults
	 *  if this is the first time you have used this version
	 */
	public void initPrefs()
	{
		boolean prefsSet =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
					+ ".arePrefsSet", false);

		if (!prefsSet)
		{
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".arePrefsSet", true);
		}

		Double version =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
					+ ".Version", 0.0);

		if ((version.doubleValue() < 1.0) || !prefsSet)
		{
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".doSpells", true);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".doDeath", true);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".doHP",
				true);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".doMaxHP", 100);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".doMaxNum", 20);
			SettingsHandler.setGMGenOption(
				InitiativePlugin.LOG_NAME + ".doNum", 20);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnName.0", "#");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnWidth.0", 25);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnName.1", "Name");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnWidth.1", 100);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnName.2", "Player");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnWidth.2", 100);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnName.3", "Status");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnWidth.3", 75);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnName.4", "+");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnWidth.4", 25);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnName.5", "Init");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnWidth.5", 25);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnName.6", "Dur");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnWidth.6", 25);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnName.7", "HP");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnWidth.7", 25);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnName.8", "HP Max");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnWidth.8", 50);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnName.9", "Type");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnWidth.9", 50);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".NumberOfColumns", 10);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".DividerLocation", 450);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".SubVersion", 1.0);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".Version", 1.0);
		}
	}

	/**  Kills the selected combatants */
	public void killCombatant()
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);

			if (iH instanceof Combatant)
			{
				Combatant cbt = (Combatant) iH;
				cbt.kill();
				combatantDied(cbt);
				combatantUpdated(cbt);
			}
		}

		initList.sort();
		refreshTable();
	}

	/**
	 *  Loads a character or party from an XML document
	 *
	 *@param  character  XML document containing a character or a party
	 * @param comp
	 */
	public void loadFromDocument(Document character, GMBComponent comp)
	{
		if (character.getRootElement().getName().equals("Party"))
		{
			Element party = character.getRootElement();
			List xmlList = party.getChildren("Character");

			for (int i = 0; i < xmlList.size(); i++)
			{
				Element eCharacter = (Element) xmlList.get(i);
				XMLCombatant combatant = new XMLCombatant(eCharacter);
				initList.add(combatant);
			}

			List pcgList = party.getChildren("PcgCombatant");

			for (int i = 0; i < pcgList.size(); i++)
			{
				Element eCharacter = (Element) pcgList.get(i);
				final PcgCombatant combatant =
						new PcgCombatant(eCharacter, comp);
				initList.add(combatant);
				addTab(combatant);
			}

			List eventList = party.getChildren("Event");

			for (int i = 0; i < eventList.size(); i++)
			{
				Element eCharacter = (Element) eventList.get(i);
				Event combatant = new Event(eCharacter);
				initList.add(combatant);
			}

			List spellList = party.getChildren("Spell");

			for (int i = 0; i < spellList.size(); i++)
			{
				Element eCharacter = (Element) spellList.get(i);
				Spell combatant = new Spell(eCharacter);
				initList.add(combatant);
			}

			initList.calculateNumberField();
		}
		else if (character.getRootElement().getName().equals("Character"))
		{
			Element eCharacter = character.getRootElement();
			XMLCombatant combatant = new XMLCombatant(eCharacter);
			initList.add(combatant);
		}
	}

	/**
	 * Perform initial loading
	 * @param initFile
	 * @param comp
	 */
	public void loadINIT(File initFile, GMBComponent comp)
	{
		try
		{
			SAXBuilder builder = new SAXBuilder();
			Document character = builder.build(initFile);
			loadFromDocument(character, comp);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(JOptionPane
				.getFrameForComponent(this), "File load error: "
				+ initFile.getName());
			Logging.errorPrint("File Load Error" + initFile.getName());
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	/**  Moves to the next active initiative */
	public void nextInit()
	{
		int oldInit = currentInit;
		setCurrentInit(currentInit - 1);

		int bleedingTime =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
					+ ".Damage.Dying", PreferencesDamagePanel.DAMAGE_DYING_END);

		for (int i = 0; i < initList.size(); i++)
		{
			InitHolder iH = initList.get(i);

			if (iH instanceof Event)
			{
				Event e = (Event) iH;
				int eInit = e.getInitiative().getCurrentInitiative();

				if (oldInit == eInit)
				{
					int duration = e.decDuration();

					if (duration < 0)
					{
						writeToCombatTabWithRound(e.getPlayer() + "'s "
							+ e.getName() + " ended");

						if (e.isAlert())
						{
							JOptionPane.showMessageDialog(this, e.getEndText());
						}

						initList.remove(i);
					}
				}
			}
			else if (iH instanceof Combatant)
			{
				Combatant cbt = (Combatant) iH;
				int cInit = cbt.getInitiative().getCurrentInitiative();

				if (oldInit == cInit)
				{
					cbt.decDuration();

					if (bleedingTime == PreferencesDamagePanel.DAMAGE_DYING_INITIATIVE)
					{
						bleed((Combatant) iH);
					}
				}
			}
		}

		if (currentInit <= 0)
		{
			int maxInit = initList.getMaxInit();
			setCurrentInit(maxInit);

			for (int i = 0; i < initList.size(); i++)
			{
				InitHolder iH = initList.get(i);

				if (bleedingTime == PreferencesDamagePanel.DAMAGE_DYING_END)
				{
					if (iH instanceof Combatant)
					{
						bleed((Combatant) iH);
					}
				}

				iH.endRound();
			}

			round++;
			writeToCombatTab("Round " + round);
			setCurrentInit(maxInit);
		}
		else if (!initList.initValid(currentInit))
		{
			nextInit();
		}

		refreshTable();
	}

	/**  pastes the copied combatant
	 * @param toPaste
	 */
	public void pasteNew(Combatant toPaste)
	{
		if (toPaste instanceof XMLCombatant)
		{
			XMLCombatant newCbt;

			XMLCombatant cb = (XMLCombatant) toPaste;
			SystemInitiative init = cb.getInitiative();
			SystemHP hitPoints = cb.getHP();
			String name = initList.getUniqueName(cb.getName());
			newCbt =
					new XMLCombatant(name, toPaste.getPlayer(), init
						.getAttribute().getValue(), hitPoints.getAttribute()
						.getValue(), hitPoints.getMax(),
						hitPoints.getCurrent(), hitPoints.getSubdual(), init
							.getBonus(), cb.getCombatantType(), cb.getCR());
			initList.add(newCbt);
		}

		if (toPaste instanceof PcgCombatant)
		{
//			PcgCombatant cb = (PcgCombatant) toPaste;
//			PCGen_Frame1.getInst().loadPCFromFile(
//				new File(cb.getPC().getFileName()), false, true);
			// As character exists in pcgen it is automatically added in to the init list
		}

		refreshTable();
	}

	/**
	 *  Pastes num copies of the copied combatant
	 * @param toPaste
	 *
	 *@param  num  number to paste
	 */
	public void pasteNew(Combatant toPaste, int num)
	{
		for (int i = 0; i < num; i++)
		{
			pasteNew(toPaste);
		}
	}

	/**
	 *  Pastes num copies of the copied combatant
	 *
	 *@param  num  number to paste
	 */
	public void pasteNew(int num)
	{
		pasteNew(copyCombatant, num);
	}

	/**
	 *  Pastes num copies of the copied combatant
	 */
	public void pasteNew()
	{
		pasteNew(copyCombatant);
	}

	/**
	 * <p>Performs an attack action for the specified combatant.  This method
	 * constructs an AttackModel from the specified string and displays an attack
	 * dialog.</p>
	 * <p>If other combatants are present, this method passes the attack dialog
	 * a list of such combatants, and the user can choose to damage one or more of them.
	 * Deceased combatants are removed from this list, as is the current combatant.</p>
	 *
	 * @param attack
	 * @param combatant
	 */
	public void performAttack(AttackModel attack, PcgCombatant combatant)
	{
		Vector combatants = new Vector(initList.size());

		for (int i = 0; i < initList.size(); i++)
		{
			if (initList.get(i) instanceof PcgCombatant
				&& (initList.get(i) != combatant)
				&& (!(initList.get(i)).getStatus().equals("Dead") || showDead
					.isSelected()))
			{
				combatants.add(initList.get(i));
			}
		}

		AttackDialog dlg = new AttackDialog(attack, combatants);
		dlg.setModal(true);
		dlg.setVisible(true);

		final List<Integer> dmgList = dlg.getDamageList();
		final List targetList = dlg.getDamagedCombatants();

		if ((dmgList != null) && (targetList != null) && (dmgList.size() > 0)
			&& (targetList.size() > 0))
		{
			writeToCombatTabWithRound(combatant.getName()
				+ " successfully attacks using " + attack);

			for (int i = 0; (i < dmgList.size()) && (i < targetList.size()); i++)
			{
				if (dlg.isSubdual())
				{
					int subdualType =
							SettingsHandler.getGMGenOption(
								InitiativePlugin.LOG_NAME + ".Damage.Subdual",
								PreferencesDamagePanel.DAMAGE_SUBDUAL);

					if (subdualType == PreferencesDamagePanel.DAMAGE_SUBDUAL)
					{
						doSubdual((dmgList.get(i)).intValue(),
							(PcgCombatant) targetList.get(i));
					}
					else if (subdualType == PreferencesDamagePanel.DAMAGE_NON_LETHAL)
					{
						doNonLethal((dmgList.get(i)).intValue(),
							(PcgCombatant) targetList.get(i));
					}
				}
				else
				{
					doDamage((dmgList.get(i)).intValue(),
						(PcgCombatant) targetList.get(i));
				}
			}

			initList.sort();
			refreshTable();
		}
		else if ((dmgList != null) && (dmgList.size() > 0))
		{
			writeToCombatTabWithRound(combatant.getName()
				+ " successfully attacks using " + attack);
		}
		else
		{
			writeToCombatTabWithRound(combatant.getName()
				+ " fails with attack using " + attack);
		}

		dlg.dispose();
	}

	/**  Raises the selected combatants from the dead */
	public void raiseCombatant()
	{
		final List selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = (InitHolder) selectedList.remove(0);

			if (iH instanceof Combatant)
			{
				Combatant cbt = (Combatant) iH;
				writeToCombatTabWithRound(iH.getName() + " (" + cbt.getPlayer()
					+ ") Raised");
				cbt.raise();
				combatantUpdated(cbt);
			}
		}

		initList.sort();
		refreshTable();
	}

	/**  Refocuses the selected combatants */
	public void refocusCombatant()
	{
		final List selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = (InitHolder) selectedList.remove(0);

			if (iH instanceof Combatant)
			{
				Combatant cbt = (Combatant) iH;
				cbt.init.refocus();
				combatantUpdated(cbt);
				writeToCombatTabWithRound(cbt.getName() + " ("
					+ cbt.getPlayer() + ") Refocused");
			}
		}

		initList.sort();
		refreshTable();
	}

	//** Table CoreUtility Functions **

	/**  Refreshes the main table to reflect the current data in memory */
	public void refreshTable()
	{
		combatantTable.clearSelection();

		DefaultTableModel model = (DefaultTableModel) combatantTable.getModel();
		model.setNumRows(0);

		int startSelect = -1;
		int rowNum = 0;

		for (int i = 0; i < initList.size(); i++)
		{
			InitHolder c = initList.get(i);

			if ((!c.getStatus().equals("Dead") || showDead.isSelected())
				&& (!(c instanceof Event) || showEvents.isSelected()))
			{
				Vector rowVector = initList.getRowVector(i, columnList);
				model.addRow(rowVector);

				int cInit = c.getInitiative().getCurrentInitiative();

				if (cInit == currentInit)
				{
					if (startSelect == -1)
					{
						startSelect = rowNum;
					}

					combatantTable.setRowSelectionInterval(startSelect, rowNum);
				}

				rowNum++;
			}
		}

		refreshEventTab();
	}

	//** End Initialization Functions **

	/**
	 * Refresh the tabs
	 */
	public void refreshTabs()
	{
		for (int i = 0; i < initList.size(); i++)
		{
			InitHolder iH = initList.get(i);

			if ((!iH.getStatus().equals("Dead") || showDead.isSelected())
				&& iH instanceof Combatant)
			{
				Combatant cbt = (Combatant) iH;

				//if (pcgcbt.getPC().isDisplayUpdate())
				//{
				//pcgcbt.getPC().setDisplayUpdate(false);
				removeTab(cbt);
				addTab(cbt);
				//}
			}
		}
	}

	/**
	 * Remove the pcg combatant
	 * @param pc
	 */
	public void removePcgCombatant(PlayerCharacter pc)
	{
		for (int i = 0; i < initList.size(); i++)
		{
			InitHolder iH = initList.get(i);

			if (iH instanceof PcgCombatant)
			{
				PcgCombatant c = (PcgCombatant) iH;

				if (c.getPC() == pc)
				{
					initList.remove(iH);
					removeTab(iH);
				}
			}
		}
	}

	/**
	 * Remove the tab
	 * @param iH
	 */
	public void removeTab(InitHolder iH)
	{
		try
		{
			tpaneInfo.removeTabAt(tpaneInfo.indexOfTab(iH.getName()));
		}
		catch (Exception e)
		{
			// TODO:  Exception Needs to be handled
		}
	}

	/**
	 * Remove the named tab
	 * @param name The name of the tab to remove
	 */
	public void removeTab(String name)
	{
		try
		{
			tpaneInfo.removeTabAt(tpaneInfo.indexOfTab(name));
		}
		catch (IndexOutOfBoundsException e)
		{
			// Ignore - means the name was not recognised
		}
	}

	/**  Re-rolls the selected combatant's initiatives */
	public void rerollCombatant()
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);

			if (iH instanceof Combatant)
			{
				Combatant cbt = (Combatant) iH;
				cbt.init.check();
				writeToCombatTabWithRound(cbt.getName() + " ("
					+ cbt.getPlayer() + ") Rerolled");
				combatantUpdated(cbt);
			}
		}

		initList.sort();
		refreshTable();
	}

	//** End Toolbar & Button CoreUtility Functions **
	//** Functions implementing button calls for top toolbar **

	/**  Starts a new combat, and rolls a new initiative for all combatants */
	public void roll()
	{
		round = 0;
		initList.check();

		int maxInit = initList.getMaxInit();
		writeToCombatTab("Combat Number " + currentCombat + ": ");
		writeToCombatTab("Round 1");
		currentCombat++;
		round = 1;
		setCurrentInit(maxInit);
	}

	//** End Functions implementing button calls for top toolbar **

	//** Functions implementing button calls for the bottom toolbar **

	/**
	 * Save the initiative roll
	 */
	public void rollSave()
	{
		final List<InitHolder> selectedList = getSelected();
		//int dc = 0;
		//int type = SavingThrowDialog.NULL_SAVE;
		SaveModel model = new SaveModel();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);

			if (iH instanceof Combatant)
			{
				model = performSave(model, iH);
			}
		}

		refreshTable();
	}

	private SaveModel performSave(SaveModel model, InitHolder iH)
	{
		Combatant cbt = (Combatant) iH;

		SavingThrowDialog dialog =
				new SavingThrowDialog(GMGenSystem.inst, true, cbt, model);
		dialog.setVisible(true);
		dialog.dispose();

		int returnVal = dialog.getReturnValue();
		int roll = dialog.getRoll();
		int total = dialog.getTotal();
		model = dialog.getSaveModel();

		StringBuffer sb = new StringBuffer();
		sb.append(dialog.getSaveAbbrev(dialog.getSaveType()));
		sb.append(" save DC " + model.getDc());

		if (roll > 0)
		{
			sb.append(" with a roll of " + (roll + total));
			sb.append(" (" + total + " + Roll: " + roll + ")");
		}

		if (returnVal == SavingThrowDialog.PASS_OPTION)
		{
			writeToCombatTabWithRound(iH.getName() + " (" + iH.getPlayer()
				+ ") Passed a " + sb);
		}
		else if (returnVal == SavingThrowDialog.FAIL_OPTION)
		{
			writeToCombatTabWithRound(iH.getName() + " (" + iH.getPlayer()
				+ ") Failed a " + sb);
		}
		return model;
	}

	/**
	 *  Saves the current combatants out to an XML file
	 *
	 *@param  xml            The File to save to
	 *@exception  Exception  XML and file IO exceptions
	 */
	public void saveToDocument(File xml) throws Exception
	{
		Element party = new Element("Party");
		party.setAttribute("filever", "1.0");
		party.setAttribute("filetype", "initsave");

		/*if(currentInit > -1) {
		 party.setAttribute("current_init", Integer.toString(currentInit));
		 }*/
		for (int i = 0; i < initList.size(); i++)
		{
			InitHolder iH = initList.get(i);
			party.addContent(iH.getSaveElement());
		}

		Document saveDocument = new Document(party);
		InitOutputter xmlOut = new InitOutputter();
		xmlOut.setFormat(Format.getRawFormat().setEncoding("US-ASCII"));

		FileWriter fr = new FileWriter(xml);
		xmlOut.output(saveDocument, fr);
		fr.flush();
		fr.close();
	}

	//** End Preferences Functions **
	//** IO Functions **

	/**  Calls up a file save dialog, and if a file is selected/created, will then save the combatants out to disk. */
	public void saveToFile()
	{
		JFileChooser fLoad = new JFileChooser();
		File defaultFile = SettingsHandler.getPcgPath();

		if (defaultFile.exists())
		{
			fLoad.setCurrentDirectory(defaultFile);
		}

		String[] fileExt = new String[]{"gmi", "init"};
		SimpleFileFilter ff =
				new SimpleFileFilter(fileExt,
					"GMGen Initiative/Encounter Export");
		fLoad.addChoosableFileFilter(ff);
		fLoad.setFileFilter(ff);

		int returnVal = fLoad.showSaveDialog(this);

		try
		{
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				String fileName = fLoad.getSelectedFile().getName();
				String ext = "";

				if (!fileName.endsWith(".gmi"))
				{
					ext = ".gmi";
				}

				File xml =
						new File(fLoad.getSelectedFile().getParent()
							+ File.separator + fileName + ext);

				if (xml.exists())
				{
					int choice =
							JOptionPane.showConfirmDialog(this,
								"File Exists, Overwrite?", "File Exists",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);

					if (choice == JOptionPane.YES_OPTION)
					{
						SettingsHandler.ensurePathExists(xml.getParentFile());
						saveToDocument(xml);
					}
				}
				else
				{
					SettingsHandler.ensurePathExists(xml.getParentFile());
					saveToDocument(xml);
				}
			}
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, "Error Writing File");
			Logging.errorPrint("Error Writing File");
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	/**  Shows the preferences dialog */
	public void showPreferences()
	{
		// TODO:  Method does nothing?
	}

	/**  Stabilizes the selected combatants */
	public void stabilizeCombatant()
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);

			if (iH instanceof Combatant)
			{
				Combatant cbt = (Combatant) iH;
				writeToCombatTabWithRound(iH.getName() + " (" + cbt.getPlayer()
					+ ") Stabilized");
				cbt.stabilize();
				combatantUpdated(cbt);
			}
		}

		initList.sort();
		refreshTable();
	}

	/**  Calls up the CastSpell dialog, passing in the data for the first selected combatant, if there is one*/
	public void startEvent()
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);
			StartEvent dialog =
					new StartEvent(JOptionPane.getFrameForComponent(this),
						true, this, iH.getPlayer(), iH.getInitiative()
							.getCurrentInitiative());
			dialog.setVisible(true);
			refreshTable();

			return;
		}

		initList.sort();
		refreshTable();

		StartEvent dialog =
				new StartEvent(JOptionPane.getFrameForComponent(this), true,
					this);
		dialog.setVisible(true);
		refreshTable();
	}

	//** End IO Functions **
	//** Combat Tab Functions **

	/**
	 *  Writes out a message to the Combat window
	 *
	 *@param  message  Message to write to the Data window
	 */
	public void writeToCombatTab(String message)
	{
		log.logMessage(InitiativePlugin.LOG_NAME, message);
	}

	/**
	 *  Writes out a message to the Combat window, and includes the initiative that the message was written at
	 *
	 *@param  message  Message to write to the Data window
	 */
	public void writeToCombatTabWithRound(String message)
	{
		writeToCombatTab(" (Round: " + round + ", Init: " + currentInit + "): "
			+ message);
	}

	/**
	 * <p>Called when a hyperlink is selected in one of the text panes in <code>tpaneInfo</code>.
	 * Used to generate attack/skill, etc. dialogs.</p>
	 *
	 * @param e <code>HyperLinkEvent</code> that called this method.
	 * @param cbt <code>PcgCombatant</code> to perform action for.
	 */
	protected void hyperLinkSelected(HyperlinkEvent e, Combatant cbt)
	{
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
		{
			PObjectModel model = PObjectModel.Factory(e.getDescription());

			if (model != null)
			{
				if (model instanceof AttackModel && cbt instanceof PcgCombatant)
				{
					PcgCombatant pcgcbt = (PcgCombatant) cbt;
					performAttack((AttackModel) model, pcgcbt);
				}
				else if (model instanceof CheckModel)
				{
					performCheck((CheckModel) model);
				}
				else if (model instanceof SpellModel)
				{
					castSpell((SpellModel) model, cbt);
				}
				else if (model instanceof SaveModel)
				{
					performSave((SaveModel) model, cbt);
				}
				else if (model instanceof DiceRollModel
					&& cbt instanceof PcgCombatant)
				{
					performDiceRoll((DiceRollModel) model);
				}
			}
		}
	}

	/**
	 * <p>
	 * Performs the specified dice roll by raising a dice roll dialog.
	 * </p>
	 *
	 * @param model The dice roll model
	 */
	private void performDiceRoll(DiceRollModel model)
	{
		DiceRollDialog dlg = new DiceRollDialog(model);
		dlg.setModal(true);
		dlg.setVisible(true);
		dlg.dispose();
	}

	/**
	 * <p>
	 * Fired when the selection in the <code>combatantTable</code> changes;
	 * if any rows are selected, it synchronizes the tab view with the first
	 * selected row.
	 * </p>
	 *
	 * @param e
	 *            <code>ListSelectionEvent</code> which fired this method
	 */
	protected void listSelectionChaned(ListSelectionEvent e)
	{
		final int row = combatantTable.getSelectedRow();

		if (row >= 0)
		{
			final String name =
					(String) combatantTable.getValueAt(row, combatantTable
						.getColumnModel().getColumnIndex("Name"));

			if ((name != null) && (name.length() > 0)
				&& (tpaneInfo.indexOfTab(name) >= 0))
			{
				tpaneInfo.setSelectedIndex(tpaneInfo.indexOfTab(name));
			}
		}
	}

	private List getColumnOrder()
	{
		TableColumnModel colModel = combatantTable.getColumnModel();
		final List colOrder = new ArrayList();

		for (int i = 0; i < colModel.getColumnCount(); i++)
		{
			colOrder.add(colModel.getColumn(i).getHeaderValue());
		}

		return colOrder;
	}

	private void setColumnWidths(int[] widths)
	{
		TableColumnModel colModel = combatantTable.getColumnModel();

		for (int i = 0; i < widths.length; i++)
		{
			TableColumn col = colModel.getColumn(i);
			col.setPreferredWidth(widths[i]);
		}
	}

	                                           
	private void TablePopupActionPerformed(java.awt.event.ActionEvent evt)
	{
		                                           
		checkAndFixColumns(tablePopupCBName.getState(), "Name");
		checkAndFixColumns(tablePopupCBPlayer.getState(), "Player");
		checkAndFixColumns(tablePopupCBStatus.getState(), "Status");
		checkAndFixColumns(tablePopupCBPlus.getState(), "+");
		checkAndFixColumns(tablePopupCBInitiative.getState(), "Init");
		checkAndFixColumns(tablePopupCBDuration.getState(), "Dur");
		checkAndFixColumns(tablePopupCBHP.getState(), "HP");
		checkAndFixColumns(tablePopupCBHPMax.getState(), "HP Max");
		checkAndFixColumns(tablePopupCBNumber.getState(), "#");
		checkAndFixColumns(tablePopupCBType.getState(), "Type");
		refreshTable();
	}

	private void addColumn(String name, int width)
	{
		DefaultTableModel tabModel =
				(DefaultTableModel) combatantTable.getModel();
		TableColumnModel colModel = combatantTable.getColumnModel();
		tabModel.addColumn(name);

		TableColumn column = colModel.getColumn(colModel.getColumnCount() - 1);
		column.setPreferredWidth(width);
		column.setWidth(width);
		column.setIdentifier(name);
		columnList = getColumnOrder();
	}

	private void addTableListener()
	{
		TableColumnModel colModel = combatantTable.getColumnModel();
		colModel
			.addColumnModelListener(new javax.swing.event.TableColumnModelListener()
			{
				public void columnAdded(
					javax.swing.event.TableColumnModelEvent evt)
				{
					colModAdded(evt);
				}

				public void columnMarginChanged(
					javax.swing.event.ChangeEvent evt)
				{
					colModMarginChanged(evt);
				}

				public void columnMoved(
					javax.swing.event.TableColumnModelEvent evt)
				{
					colModMoved(evt);
				}

				public void columnRemoved(
					javax.swing.event.TableColumnModelEvent evt)
				{
					colModRemoved(evt);
				}

				public void columnSelectionChanged(
					javax.swing.event.ListSelectionEvent evt)
				{
					colModSelectionChanged(evt);
				}
			});
	}

	                                         
	private void bAddCombatantActionPerformed(java.awt.event.ActionEvent evt)
	{
		                                              
		addCombatant();
	}

	private void bCastActionPerformed(java.awt.event.ActionEvent evt)
	{
		castSpell();
		focusNextInit();
	}

	                                       
	private void bCombatantReRollActionPerformed(java.awt.event.ActionEvent evt)
	{
		                                                 
		rerollCombatant();
		focusNextInit();
	}

	private void bDamageActionPerformed(java.awt.event.ActionEvent evt)
	{
		damageCombatant();
		focusNextInit();
	}

	                                            
	private void bDeleteActionPerformed(java.awt.event.ActionEvent evt)
	{
		                                        
		deleteCombatant();
		focusNextInit();
	}

	private void bEventActionPerformed(java.awt.event.ActionEvent evt)
	{
		startEvent();
		focusNextInit();
	}

	private void bHealActionPerformed(java.awt.event.ActionEvent evt)
	{
		healCombatant();
		focusNextInit();
	}

	private void bKillActionPerformed(java.awt.event.ActionEvent evt)
	{
		killCombatant();
		focusNextInit();
	}

	                                        
	private void bNextInitActionPerformed(java.awt.event.ActionEvent evt)
	{
		                                          
		nextInit();
	}

	private void bRaiseActionPerformed(java.awt.event.ActionEvent evt)
	{
		raiseCombatant();
		refreshTable();
		focusNextInit();
	}

	                                                
	private void bRefocusActionPerformed(java.awt.event.ActionEvent evt)
	{
		                                         
		refocusCombatant();
		focusNextInit();
	}

	                                             
	private void bRollActionPerformed(java.awt.event.ActionEvent evt)
	{
		                                      
		roll();
		focusNextInit();
	}

	                                          
	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)
	{
		rollSave();
	}

	private void bStabilizeActionPerformed(java.awt.event.ActionEvent evt)
	{
		stabilizeCombatant();
		refreshTable();
		focusNextInit();
	}

	private void bleed(Combatant cbt)
	{
		if (cbt.getStatus().equals("Bleeding"))
		{
			int stableType =
					SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
						+ ".Damage.Stable",
						PreferencesDamagePanel.DAMAGE_STABLE_PERCENT);

			if (stableType == PreferencesDamagePanel.DAMAGE_STABLE_PERCENT)
			{
				int roll = new Dice(1, 100).roll();

				if (roll <= 10)
				{
					cbt.stabilize();
					writeToCombatTabWithRound(cbt.getName() + " ("
						+ cbt.getPlayer() + ") auto-stabilized  (" + roll
						+ "%)");
				}
				else
				{
					writeToCombatTabWithRound(cbt.getName() + " ("
						+ cbt.getPlayer() + ") failed to auto-stabilize ("
						+ roll + "%)");
				}
			}
			else if (stableType == PreferencesDamagePanel.DAMAGE_STABLE_SAVE)
			{
				SavingThrowDialog dialog =
						new SavingThrowDialog(GMGenSystem.inst, true, cbt, 20,
							SavingThrowDialog.FORT_SAVE);
				dialog.setVisible(true);
				dialog.dispose();

				//Show the dialog and get it's results
				int returnVal = dialog.getReturnValue();
				int roll = dialog.getRoll();
				int total = dialog.getTotal();
				int dc = dialog.getDC();

				//stabilize if the combatant passes the save
				if (dialog.getReturnValue() == SavingThrowDialog.PASS_OPTION)
				{
					cbt.stabilize();
				}

				//Create a message out with the results
				StringBuffer sb = new StringBuffer();
				sb.append(dialog.getSaveAbbrev(dialog.getSaveType()));
				sb.append(" save DC " + dc);

				if (roll > 0)
				{
					sb.append(" with a roll of " + (roll + total));
					sb.append(" (" + total + " + Roll: " + roll + ")");
				}

				//write out the results to the combat tab
				if (returnVal == SavingThrowDialog.PASS_OPTION)
				{
					writeToCombatTabWithRound(cbt.getName() + " ("
						+ cbt.getPlayer() + ") Passed a " + sb
						+ " to auto-stabilize");
				}
				else if (returnVal == SavingThrowDialog.FAIL_OPTION)
				{
					writeToCombatTabWithRound(cbt.getName() + " ("
						+ cbt.getPlayer() + ") Failed a " + sb
						+ " to auto-stabilize");
				}
			}

			String oldStatus = cbt.getStatus();
			cbt.bleed();
			combatantUpdated(cbt);
			String newStatus = cbt.getStatus();

			if (!oldStatus.equals(newStatus) && newStatus.equals("Dead"))
			{
				combatantDied(cbt);
			}
		}
	}

	/**
	 * <p>Casts a spell based on the specified spell model.</p>
	 *
	 * @param model A <code>SpellModel</code> instance
	 * @param combatant Combatant who is casting the spell.
	 */
	private void castSpell(SpellModel model, Combatant combatant)
	{
		castSpell(model.getName(), combatant, model);
	}

	private void checkAndFixColumns(boolean shouldExist, String colName)
	{
		if (shouldExist && !getColumnOrder().contains(colName))
		{
			addColumn(colName, 100);
			trackTable();
		}
		else if (!shouldExist && getColumnOrder().contains(colName))
		{
			removeColumn(colName);
		}
	}

	/**
	 * <p>
	 * Generates a skill check dialog for a skill-check hyper link event.
	 * </p>
	 *
	 * @param model
	 *             The skill model to roll with.
	 */
	private void performCheck(CheckModel model)
	{
		CheckDialog dlg = new CheckDialog(model);
		dlg.setModal(true);
		dlg.setVisible(true);
		dlg.dispose();
	}

	private void colModAdded(javax.swing.event.TableColumnModelEvent evt)
	{
		// TODO:  Method does nothing?
	}

	private void colModMarginChanged(javax.swing.event.ChangeEvent evt)
	{
		trackTable();
	}

	private void colModMoved(javax.swing.event.TableColumnModelEvent evt)
	{
		trackTable();
	}

	private void colModRemoved(javax.swing.event.TableColumnModelEvent evt)
	{
		// TODO:  Method does nothing?
	}

	private void colModSelectionChanged(javax.swing.event.ListSelectionEvent evt)
	{
		// TODO:  Method does nothing?
	}

	                                     
	private void combatantTableMousePressed(java.awt.event.MouseEvent evt)
	{
		                                            
		if (evt.isPopupTrigger())
		{
			initTablePopup();
			tablePopup.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	                                             
	private void combatantTableMouseReleased(java.awt.event.MouseEvent evt)
	{
		                                             
		if (evt.isPopupTrigger())
		{
			initTablePopup();
			tablePopup.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	private void combatantTablePropertyChange(java.beans.PropertyChangeEvent evt)
	{
		                                              
		editTableRow();
		refreshTable();
	}

	private void editTable(int row, int column)
	{
		// Figure out which row is the active row
		// Karianna - Commented out this section to fix bug 
		/*
		int activeRow = 0;
		for (int i = 0; i < initList.size(); i++)
		{
			InitHolder c = initList.get(i);
			// IF the InitHolder status is not Dead or showDead is selected (e.g. InitHolder is alive or we're showeing the dead)
			// AND InitHolder is not an Event or we're shoeing events
			// THEN update the active row
			if ((!c.getStatus().equals("Dead") || showDead.isSelected())
				&& (!(c instanceof Event) || showEvents.isSelected()))
			{
				activeRow++;
			}
		}
		*/
		// Look up the active row (-1 as arrays are indexed starting at 0)
		//InitHolder iH = initList.get(activeRow - 1);
		InitHolder iH = initList.get(row);
		String oldName = iH.getName();
		Object data = combatantTable.getValueAt(row, column);
		boolean atTop = (currentInit == initList.getMaxInit());
		iH.editRow(columnList, column, data);
		if (!iH.getName().equals(oldName) && iH instanceof Combatant)
		{
			removeTab(oldName);
			addTab((Combatant) iH);
		}
		initHolderUpdated(iH);
		initList.sort();
		if (atTop)
		{
			setCurrentInit(initList.getMaxInit());
		}
		refreshTable();
	}

	private void editTableRow()
	{
		int row = combatantTable.getEditingRow();
		int column = combatantTable.getEditingColumn();

		if ((row > -1) && (column > -1))
		{
			editTable(row, column);
		}
	}

	/**
	 *  This method is called from within the constructor to initialize the form.
	 *  WARNING: Do NOT modify this code. The content of this method is always
	 *  regenerated by the Form Editor.
	 */
	private void initComponents()
	{//GEN-BEGIN:initComponents
		tablePopup = new javax.swing.JPopupMenu();
		tablePopupCBNumber = new javax.swing.JCheckBoxMenuItem();
		tablePopupCBName = new javax.swing.JCheckBoxMenuItem();
		tablePopupCBPlayer = new javax.swing.JCheckBoxMenuItem();
		tablePopupCBStatus = new javax.swing.JCheckBoxMenuItem();
		tablePopupCBPlus = new javax.swing.JCheckBoxMenuItem();
		tablePopupCBInitiative = new javax.swing.JCheckBoxMenuItem();
		tablePopupCBDuration = new javax.swing.JCheckBoxMenuItem();
		tablePopupCBHP = new javax.swing.JCheckBoxMenuItem();
		tablePopupCBHPMax = new javax.swing.JCheckBoxMenuItem();
		tablePopupCBType = new javax.swing.JCheckBoxMenuItem();
		topToolbar = new javax.swing.JToolBar();
		buttonPanelTop = new javax.swing.JPanel();
		bRoll = new javax.swing.JButton();
		bAddCombatant = new javax.swing.JButton();
		bNextInit = new javax.swing.JButton();
		bRefocus = new javax.swing.JButton();
		bCombatantReRoll = new javax.swing.JButton();
		bDelete = new javax.swing.JButton();
		jPanel2 = new javax.swing.JPanel();
		lCounter = new javax.swing.JLabel();
		jSplitPane1 = new FlippingSplitPane();
		jScrollPane1 = new javax.swing.JScrollPane();
		jScrollEvents = new javax.swing.JScrollPane();
		combatantTable = new javax.swing.JTable();
		tpaneInfo = new javax.swing.JTabbedPane();
		tpCombatInfo = new javax.swing.JTextArea();
		tpCombatInfo.setName("Events");
		bottomToolbar = new javax.swing.JToolBar();

		tablePopupCBNumber.setText("#");
		tablePopupCBNumber
			.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					TablePopupActionPerformed(evt);
				}
			});

		tablePopup.add(tablePopupCBNumber);
		tablePopupCBName.setText("Name");
		tablePopupCBName.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				TablePopupActionPerformed(evt);
			}
		});

		tablePopup.add(tablePopupCBName);
		tablePopupCBPlayer.setText("Player");
		tablePopupCBPlayer
			.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					TablePopupActionPerformed(evt);
				}
			});

		tablePopup.add(tablePopupCBPlayer);
		tablePopupCBStatus.setText("Status");
		tablePopupCBStatus
			.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					TablePopupActionPerformed(evt);
				}
			});

		tablePopup.add(tablePopupCBStatus);
		tablePopupCBPlus.setText("Plus");
		tablePopupCBPlus.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				TablePopupActionPerformed(evt);
			}
		});

		tablePopup.add(tablePopupCBPlus);
		tablePopupCBInitiative.setText("Initiative");
		tablePopupCBInitiative
			.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					TablePopupActionPerformed(evt);
				}
			});

		tablePopup.add(tablePopupCBInitiative);
		tablePopupCBDuration.setText("Duration");
		tablePopupCBDuration
			.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					TablePopupActionPerformed(evt);
				}
			});

		tablePopup.add(tablePopupCBDuration);
		tablePopupCBHP.setText("HP");
		tablePopupCBHP.setEnabled(false);
		tablePopupCBHP.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				TablePopupActionPerformed(evt);
			}
		});

		tablePopup.add(tablePopupCBHP);
		tablePopupCBHPMax.setText("HP Max");
		tablePopupCBHPMax.setEnabled(false);
		tablePopupCBHPMax.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				TablePopupActionPerformed(evt);
			}
		});

		tablePopup.add(tablePopupCBHPMax);
		tablePopupCBType.setText("Type");
		tablePopupCBType.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				TablePopupActionPerformed(evt);
			}
		});

		tablePopup.add(tablePopupCBType);

		setLayout(new java.awt.BorderLayout());

		setPreferredSize(new java.awt.Dimension(700, 600));
		buttonPanelTop.setLayout(new javax.swing.BoxLayout(buttonPanelTop,
			javax.swing.BoxLayout.X_AXIS));

		bRoll.setText("Start Combat");
		bRoll.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bRollActionPerformed(evt);
			}
		});

		buttonPanelTop.add(bRoll);

		bAddCombatant.setText("Add Combatant");
		bAddCombatant.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bAddCombatantActionPerformed(evt);
			}
		});

		buttonPanelTop.add(bAddCombatant);

		//		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".ShowDuplicateButton", false))
		//		{
		bDuplicateCombatant.setText("Duplicate");
		bDuplicateCombatant
			.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					bDuplicateCombatantActionPerformed(evt);
				}
			});

		buttonPanelTop.add(bDuplicateCombatant);
		//		}

		bNextInit.setText("Next Initiative");
		bNextInit.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bNextInitActionPerformed(evt);
			}
		});

		buttonPanelTop.add(bNextInit);

		bRefocus.setText("Refocus");
		bRefocus.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bRefocusActionPerformed(evt);
			}
		});

		buttonPanelTop.add(bRefocus);

		bCombatantReRoll.setText("Roll");
		bCombatantReRoll.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bCombatantReRollActionPerformed(evt);
			}
		});

		buttonPanelTop.add(bCombatantReRoll);

		bDelete.setText("Delete");
		bDelete.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bDeleteActionPerformed(evt);
			}
		});

		buttonPanelTop.add(bDelete);

		bRefresh.setText("Refresh Tabs");
		bRefresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				refreshTable();
				refreshTabs();
			}
		});
		buttonPanelTop.add(bRefresh);

		topToolbar.add(buttonPanelTop);

		topToolbar.add(jPanel2);

		lCounter.setFont(new java.awt.Font("Dialog", 0, 18));
		topToolbar.add(lCounter);

		add(topToolbar, java.awt.BorderLayout.NORTH);

		jSplitPane1.setDividerLocation(400);
		jSplitPane1.setOneTouchExpandable(true);
		jSplitPane1.setPreferredSize(new java.awt.Dimension(800, 405));
		combatantTable.addMouseListener(new java.awt.event.MouseAdapter()
		{
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt)
			{
				combatantTableMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt)
			{
				combatantTableMouseReleased(evt);
			}
		});

		combatantTable
			.addPropertyChangeListener(new java.beans.PropertyChangeListener()
			{
				public void propertyChange(java.beans.PropertyChangeEvent evt)
				{
					combatantTablePropertyChange(evt);
				}
			});

		jScrollPane1.setViewportView(combatantTable);
		jScrollEvents.setViewportView(tpCombatInfo);
		
		jSplitPane1.setLeftComponent(jScrollPane1);

		tpaneInfo.addTab("Events", jScrollEvents);

		jSplitPane1.setRightComponent(tpaneInfo);

		add(jSplitPane1, java.awt.BorderLayout.CENTER);

		add(bottomToolbar, java.awt.BorderLayout.SOUTH);
		bottomToolbar.add(bOpposedSkill);
	}

	/**
	 * <p></p>
	 * @param evt
	 */
	protected void bDuplicateCombatantActionPerformed(ActionEvent evt)
	{
		//TODO: This only works for saved pcgen files and xml combatants.
		//For pcgen files, it reloads the file, since there's no good way
		//curently to clone a PlayerCharacter.
		DefaultFormatter formatter = new NumberFormatter();
		formatter.setAllowsInvalid(false);
		formatter.setCommitsOnValidEdit(true);
		formatter.setValueClass(Integer.class);
		JFormattedTextField field = new JFormattedTextField(formatter);
		field.setValue(Integer.valueOf(1));
		int choice =
				JOptionPane.showConfirmDialog(GMGenSystem.inst, field,
					"How many copies?", JOptionPane.OK_CANCEL_OPTION);
		if (choice == JOptionPane.CANCEL_OPTION)
		{
			return;
		}
		int count = ((Number) field.getValue()).intValue();
		for (InitHolder holderToCopy : getSelected())
		{
			if (holderToCopy instanceof XMLCombatant
				|| holderToCopy instanceof PcgCombatant)
			{
				if (holderToCopy instanceof PcgCombatant)
				{
					if (((PcgCombatant) holderToCopy).getPC().getFileName() != null
						&& ((PcgCombatant) holderToCopy).getPC().getFileName()
							.length() > 0)
					{
						pasteNew((Combatant) holderToCopy, count);
					}
					else
					{
						JOptionPane
							.showMessageDialog(
								GMGenSystem.inst,
								"Combatant "
									+ holderToCopy.getName()
									+ " cannot be duplicated because it has not been saved to a valid .pcg file.",
								"Cannot Duplicate", JOptionPane.WARNING_MESSAGE);
					}
				}
				else
				{
					pasteNew((Combatant) holderToCopy, count);
				}
			}
			else
			{
				JOptionPane
					.showMessageDialog(
						GMGenSystem.inst,
						"Combatant "
							+ holderToCopy.getName()
							+ " cannot be duplicated because it is not a PCGen or XML combatant.",
						"Cannot Duplicate", JOptionPane.WARNING_MESSAGE);
			}

		}
	}

	//GEN-END:initComponents

	//** Initialization Functions **
	private void initDynamicComponents()
	{
		bSave.setText("Roll Save");
		bSave.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bSaveActionPerformed(evt);
			}
		});

		bCast.setText("Cast Spell");
		bCast.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bCastActionPerformed(evt);
			}
		});

		bEvent.setText("Start Event");
		bEvent.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bEventActionPerformed(evt);
			}
		});

		bKill.setText("Kill");
		bKill.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bKillActionPerformed(evt);
			}
		});

		bDamage.setText("Damage");
		bDamage.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bDamageActionPerformed(evt);
			}
		});

		bHeal.setText("Heal");
		bHeal.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bHealActionPerformed(evt);
			}
		});

		bStabilize.setText("Stabilize");
		bStabilize.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bStabilizeActionPerformed(evt);
			}
		});

		bRaise.setText("Raise");
		bRaise.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				bRaiseActionPerformed(evt);
			}
		});

		showDead.setSelected(true);
		showDead.setText("Show Dead");
		showDead.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				showDeadActionPerformed(evt);
			}
		});

		showEvents.setSelected(true);
		showEvents.setText("Show Events");
		showEvents.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				showEventsActionPerformed(evt);
			}
		});
		bOpposedSkill.setText("Mass Skill Check");
		bOpposedSkill.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				opposedSkillActionPerformed(e);
			}
		});
	}

	/**
	 * <p>
	 * Handles button press for bOpposedSkill.  Opens the opposed check dialog.
	 * </p>
	 * @param e
	 */
	protected void opposedSkillActionPerformed(ActionEvent e)
	{
		List<InitHolder> selected = getSelected();
		List notSelected = getUnSelected();
		OpposedCheckDialog dlg =
				new OpposedCheckDialog(GMGenSystem.inst, selected, notSelected);
		dlg.setModal(true);
		dlg.setVisible(true);
		dlg.dispose();
	}

	private void initTable()
	{
		initTableColumns();

		JTableHeader header = combatantTable.getTableHeader();
		header.addMouseListener(new java.awt.event.MouseAdapter()
		{
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt)
			{
				combatantTableMousePressed(evt);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt)
			{
				combatantTableMouseReleased(evt);
			}
		});
		columnList = getColumnOrder();
		combatantTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					listSelectionChaned(e);
				}
			});

		TableColumn typeColumn = combatantTable.getColumn("Type");
		// These are the combobox values
		String[] values = new String[]{"PC", "Enemy", "Ally", "Non Combatant"};

		// Set the combobox editor on the 1st visible column
		//int vColIndex = 0;
		//TableColumn col = table.getColumnModel().getColumn(vColIndex);
		typeColumn.setCellEditor(new TypeEditor(values));
		//typeColumn.setCellRenderer(new TypeRenderer(values));
	}

	private void initTableColumns()
	{
		DefaultTableModel tabModel =
				(DefaultTableModel) combatantTable.getModel();
		tabModel.setColumnCount(0);

		int colNo =
				SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
					+ ".NumberOfColumns", 0);
		int[] widths = new int[colNo];

		for (int i = 0; i < colNo; i++)
		{
			String name =
					SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
						+ ".ColumnName." + i, "");
			int width =
					SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
						+ ".ColumnWidth." + i, 100);
			addColumn(name, width);
			widths[i] = width;
		}

		setColumnWidths(widths);
	}

	private void initTablePopup()
	{
		TableColumnModel colModel = combatantTable.getColumnModel();
		int numCols = colModel.getColumnCount();
		tablePopupCBName.setSelected(false);
		tablePopupCBPlayer.setSelected(false);
		tablePopupCBStatus.setSelected(false);
		tablePopupCBPlus.setSelected(false);
		tablePopupCBInitiative.setSelected(false);
		tablePopupCBDuration.setSelected(false);
		tablePopupCBHP.setSelected(false);
		tablePopupCBHPMax.setSelected(false);
		tablePopupCBNumber.setSelected(false);
		tablePopupCBType.setSelected(false);

		for (int i = 0; i < numCols; i++)
		{
			TableColumn col = colModel.getColumn(i);
			String name = col.getIdentifier().toString();

			if (name.equals("Name"))
			{
				tablePopupCBName.setSelected(true);
			}
			else if (name.equals("Player"))
			{
				tablePopupCBPlayer.setSelected(true);
			}
			else if (name.equals("Status"))
			{
				tablePopupCBStatus.setSelected(true);
			}
			else if (name.equals("+"))
			{
				tablePopupCBPlus.setSelected(true);
			}
			else if (name.equals("Init"))
			{
				tablePopupCBInitiative.setSelected(true);
			}
			else if (name.equals("Dur"))
			{
				tablePopupCBDuration.setSelected(true);
			}
			else if (name.equals("HP"))
			{
				tablePopupCBHP.setSelected(true);
			}
			else if (name.equals("HP Max"))
			{
				tablePopupCBHPMax.setSelected(true);
			}
			else if (name.equals("#"))
			{
				tablePopupCBNumber.setSelected(true);
			}
			else if (name.equals("Type"))
			{
				tablePopupCBType.setSelected(true);
			}
		}

		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".doSpells", true)
			|| SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
				+ ".doDeath", true)
			|| SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
				+ ".doHP", true))
		{
			tablePopupCBDuration.setEnabled(true);
		}
		else
		{
			tablePopupCBDuration.setEnabled(false);
		}

		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doHP",
			true))
		{
			tablePopupCBHP.setEnabled(true);
			tablePopupCBHPMax.setEnabled(true);
		}
		else
		{
			tablePopupCBHP.setEnabled(false);
			tablePopupCBHPMax.setEnabled(false);
		}

		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME
			+ ".doNumber", true))
		{
			tablePopupCBNumber.setEnabled(true);
		}
	}

	private void refreshEventTab()
	{
		tpCombatInfo.setText("");

		for (int i = 0; i < initList.size(); i++)
		{
			InitHolder iH = initList.get(i);
			StringBuffer sb = new StringBuffer();

			if (iH instanceof Event)
			{
				Event evt = (Event) iH;
				sb.append(evt.getName() + " (" + evt.getPlayer() + ")\n");
				sb.append("Duration: " + evt.getDuration() + "\n");

				if (evt.getEffect().length() > 0)
				{
					sb.append(evt.getEffect() + "\n\n");
				}
				else
				{
					sb.append("\n");
				}
			}

			tpCombatInfo.setText(tpCombatInfo.getText() + sb);
		}
	}

	private void removeColumn(String name)
	{
		TableColumnModel colModel = combatantTable.getColumnModel();

		for (int i = 0; i < colModel.getColumnCount(); i++)
		{
			TableColumn col = colModel.getColumn(i);

			if (col.getHeaderValue().toString().equals(name))
			{
				colModel.removeColumn(col);
			}
		}

		trackTable();
		initTable();
		columnList = getColumnOrder();
	}

	private void showDeadActionPerformed(java.awt.event.ActionEvent evt)
	{
		checkDeadTabs();
		refreshTable();
		focusNextInit();
	}

	private void showEventsActionPerformed(java.awt.event.ActionEvent evt)
	{
		refreshTable();
		focusNextInit();
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
			+ ".ShowEvents", showEvents.isSelected());
	}

	private void trackTable()
	{
		TableColumnModel colModel = combatantTable.getColumnModel();
		int numCols = colModel.getColumnCount();
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
			+ ".NumberOfColumns", numCols);

		for (int i = 0; i < numCols; i++)
		{
			TableColumn col = colModel.getColumn(i);
			String name = col.getIdentifier().toString();
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnName." + i, name);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME
				+ ".ColumnWidth." + i, col.getWidth());
		}
	}

	/**
	 * Update the initiative holder
	 * @param iH
	 */
	public static void initHolderUpdated(InitHolder iH)
	{
		if (iH instanceof Combatant)
		{
			combatantUpdated((Combatant) iH);
		}
	}

	/**
	 * Send a message stating that the combatant has been updated
	 * @param cbt
	 */
	public static void combatantUpdated(Combatant cbt)
	{
		GMBus.send(new CombatantUpdatedMessage(GMGenSystem.inst, cbt));
	}

	//** End Other Variables **

	/**
	 * A cell editor
	 */
	public static class TypeEditor extends DefaultCellEditor
	{

		/**
		 * Constructor
		 * @param items
		 */
		public TypeEditor(String[] items)
		{
			super(new JComboBox(items));
		}
	}

	/**
	 * A table cell renderer
	 */
	public static class TypeRenderer extends JComboBox implements
			TableCellRenderer
	{

		/**
		 * Constructor
		 * @param items
		 */
		public TypeRenderer(String[] items)
		{
			super(items);
		}

		public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column)
		{
			if (isSelected)
			{
				setForeground(table.getSelectionForeground());
				super.setBackground(table.getSelectionBackground());
			}
			else
			{
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}

			// Select the current value
			setSelectedItem(value);
			return this;
		}
	}
}
