/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  Initiative.java
 */
package plugin.initiative.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.NumberFormatter;

import gmgen.GMGenSystem;
import gmgen.plugin.Combatant;
import gmgen.plugin.Event;
import gmgen.plugin.InfoCharacterDetails;
import gmgen.plugin.InitHolder;
import gmgen.plugin.InitHolderList;
import gmgen.plugin.PcgCombatant;
import gmgen.plugin.Spell;
import gmgen.plugin.State;
import gmgen.plugin.SystemHP;
import gmgen.plugin.SystemInitiative;
import gmgen.plugin.dice.Dice;
import gmgen.pluginmgr.messages.CombatantHasBeenUpdatedMessage;
import gmgen.util.LogUtilities;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui2.util.event.PopupMouseAdapter;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.PluginManager;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;
import plugin.initiative.AttackModel;
import plugin.initiative.CheckModel;
import plugin.initiative.DiceRollModel;
import plugin.initiative.InitiativePlugin;
import plugin.initiative.PObjectModel;
import plugin.initiative.SaveModel;
import plugin.initiative.SpellModel;
import plugin.initiative.XMLCombatant;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

@SuppressWarnings({"UseOfObsoleteCollectionType", "PMD.ReplaceVectorWithList", "PMD.UseArrayListInsteadOfVector"})
public class Initiative extends javax.swing.JPanel
{
	//** End Dynamic Components **
	//** Other Variables **

	/**  List that contains the list of Combatants.  Kept sorted. */
	public final InitHolderList initList = new InitHolderList();
	private final JButton bCast = new JButton();
	private final JButton bOpposedSkill = new JButton();
	private JButton bCombatantReRoll;
	private final JButton bDamage = new JButton();
	private JButton bDelete;
	private final JButton bEvent = new JButton();
	private final JButton bHeal = new JButton();
	private final JButton bKill = new JButton();
	private JButton bNextInit;
	private final JButton bRaise = new JButton();
	private JButton bRoll;
	private final JButton bRefresh = new JButton();
	private final JButton bDuplicateCombatant = new JButton();

	// End of variables declaration                   
	//** Dynamic Components **
	private final JButton bSave = new JButton();
	private final JButton bStabilize = new JButton();
	private final JCheckBox showDead = new JCheckBox();
	private final JCheckBox showEvents = new JCheckBox();
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
	private javax.swing.JPopupMenu tablePopup;
	private JSplitPane jSplitPane1;
	private javax.swing.JTabbedPane tpaneInfo;
	private javax.swing.JTable combatantTable;
	private javax.swing.JTextArea tpCombatInfo;
	private javax.swing.JToolBar bottomToolbar;
	private List columnList = new ArrayList();
	private LogUtilities log;
	private int currentCombat = 1;
	private int currentInit = -1;
	private int round = 0;

	private final PCGenMessageHandler messageHandler;

	/**  Creates new form Initiative */
	public Initiative()
	{
		messageHandler = PluginManager.getInstance().getPostbox();
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
	private void setCurrentInit(int init)
	{
		currentInit = init;

		if (currentInit > 0)
		{
			lCounter.setText(
				LanguageBundle.getFormattedString("in_plugin_initiative_round", round, init)); //$NON-NLS-1$
		}
		else
		{
			lCounter.setText(""); //$NON-NLS-1$
		}

		refreshTable();
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
	 *  Looks at each line in the table, and returns an ArrayList of lines that are Selected.
	 *
	 *@return    An ArrayList of currently selected InitHolders
	 */
	private List<InitHolder> getSelected()
	{
		final List<InitHolder> retList = new ArrayList<>();

		int j = -1;

		for (int i = 0; i < combatantTable.getRowCount(); i++)
		{
			j++;

			InitHolder iH = initList.get(j);

			if ((iH.getStatus() == State.Dead) && !showDead.isSelected())
			{
				i--;

				continue;
			}

			if ((iH instanceof Event) && !showEvents.isSelected())
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
	private List<InitHolder> getUnSelected()
	{
		final List<InitHolder> retList = new ArrayList<>();

		int j = -1;

		for (int i = 0; i < combatantTable.getRowCount(); i++)
		{
			j++;

			InitHolder iH = initList.get(j);

			if ((iH.getStatus() == State.Dead) && !showDead.isSelected())
			{
				i--;

				continue;
			}

			if ((iH instanceof Event) && !showEvents.isSelected())
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

	/**  Calls up the AddCombatant dialog for adding a new combatant */
	private void addCombatant()
	{
		final JDialog dialog = new AddCombatant(JOptionPane.getFrameForComponent(this), true, this);
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
		String name = initList.getUniqueName(pc.getDisplay().getName());

		//Changed from != to .equals 10/21/06 thpr
		if (!name.equals(pc.getDisplay().getName()))
		{
			//Means this one is already loaded, so it should be considered a new pc.
			pc.setName(name);
			//TODO:  Is this necessary?  Exactly why?
			pc.setFileName("");
		}

		final PcgCombatant pcgcbt = new PcgCombatant(pc, type, messageHandler);
		initList.add(pcgcbt);
		addTab(pcgcbt);
	}

	/**
	 * <p>
	 * Adds a tab to the {@code tpaneInfo} member. All methods adding
	 * character tabs to {@code tpaneInfo} should call this method to do
	 * so, as it provides a standard setup for the text panes and installs
	 * hyperlink listeners.
	 * </p>
	 *
	 * @param cbt Combatant to add.
	 */
	void addTab(final Combatant cbt)
	{
		javax.swing.JTextPane lp = new javax.swing.JTextPane();
		lp.setContentType("text/html");
		InfoCharacterDetails ic = new InfoCharacterDetails(cbt, lp);
		tpaneInfo.addTab(cbt.getName(), ic.getScrollPane());
		lp.setEditable(false);
		lp.addHyperlinkListener(new HyperlinkListener()
		{
			private final Combatant combatant = cbt;

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				hyperLinkSelected(e, combatant);
			}
		});
	}

	//** End Table CoreUtility Functions **
	//** Preferences Functions **

	/**  Applys preference dialog selects as new preferences, and implements those selections */
	void applyPrefs()
	{
		//Combat Prefs
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doCombat", true)
			&& !bottomToolbar.isAncestorOf(bSave))
		{
			bottomToolbar.add(bSave);
		}

		if (!SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doCombat", true)
			&& bottomToolbar.isAncestorOf(bSave))
		{
			bottomToolbar.remove(bSave);
		}

		//Spell Prefs
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doSpells", true)
			&& !bottomToolbar.isAncestorOf(bCast))
		{
			bottomToolbar.add(bCast);
			bottomToolbar.add(bEvent);
		}

		if (!SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doSpells", true)
			&& bottomToolbar.isAncestorOf(bCast))
		{
			bottomToolbar.remove(bCast);
			bottomToolbar.remove(bEvent);
			bottomToolbar.add(showEvents);
		}

		checkAndFixColumns(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doSpells", true), "Dur");

		//Mixed Prefs
		checkAndFixColumns(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doSpells", true)
			|| SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doDeath", true), "Status");

		//HP Prefs
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doHP", true)
			&& !bottomToolbar.isAncestorOf(bDamage))
		{
			bottomToolbar.add(bDamage);
			bottomToolbar.add(bHeal);
			bottomToolbar.add(bStabilize);
		}

		if (!SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doHP", true)
			&& bottomToolbar.isAncestorOf(bDamage))
		{
			bottomToolbar.remove(bDamage);
			bottomToolbar.remove(bHeal);
			bottomToolbar.remove(bStabilize);
		}

		checkAndFixColumns(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doHP", true), "HP");
		checkAndFixColumns(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doHP", true), "HP Max");
		checkAndFixColumns(SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doNumber", true), "#");

		//Death Prefs
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doDeath", true)
			&& !bottomToolbar.isAncestorOf(bKill))
		{
			bottomToolbar.add(bKill);
			bottomToolbar.add(showDead);
		}

		if (!SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doDeath", true)
			&& bottomToolbar.isAncestorOf(bKill))
		{
			bottomToolbar.remove(bKill);
			bottomToolbar.remove(showDead);
		}

		initTable();
		refreshTable();
		repaint();
	}

	/**  Calls up the CastSpell dialog, passing in the data for the first selected combatant, if there is one
	 *   sets the name of the spell as requested.
	 */
	private void castSpell()
	{
		final List<InitHolder> selectedList = getSelected();

		if (!selectedList.isEmpty())
		{
			final InitHolder iH = selectedList.remove(0);
			castSpell("", iH, null);

			return;
		}

		initList.sort();
		refreshTable();
		castSpell("", null, null);
	}

	/**
	 * <p>Calls up the CastSpell dialog, passing the data for the indicated combatant (which
	 * may be null), and sets the name to the indicated value.  If SpellModel is present,
	 * it sets the dialog's spell model as well.</p>
	 *
	 * @param name The spell's name; may be empty string.
	 * @param iH {@code InitHolder} instance, may be null
	 * @param model {@code SpellModel} instance, may be null
	 */
	private void castSpell(String name, InitHolder iH, SpellModel model)
	{
		CastSpell dialog;

		if (iH == null)
		{
			dialog = new CastSpell(JOptionPane.getFrameForComponent(this), true, this);
		}
		else
		{
			dialog = new CastSpell(JOptionPane.getFrameForComponent(this), true, this, iH.getPlayer(),
				iH.getInitiative().getCurrentInitiative());
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
	private void checkDeadTabs()
	{
		initList.stream().filter(anInitList -> anInitList.getStatus() == State.Dead).forEach(anInitList -> {
			if (showDead.isSelected() && (anInitList instanceof Combatant)
				&& (tpaneInfo.indexOfTab(anInitList.getName()) == -1))
			{
				Combatant cbt = (Combatant) anInitList;
				addTab(cbt);
			}
			else
			{
				removeTab(anInitList);
			}
		});
	}

	/**
	 * Set the current initiative holder to dead
	 * @param deadIH
	 */
	private void combatantDied(InitHolder deadIH)
	{
		writeToCombatTabWithRound(deadIH.getName() + " (" + deadIH.getPlayer() + ") Killed");

		for (InitHolder anInitList : initList)
		{
			String cbtType = "";

			if (anInitList instanceof Combatant)
			{
				Combatant cbt = (Combatant) anInitList;
				cbtType = cbt.getCombatantType();
			}

			if (cbtType.equals("Enemy") && (anInitList.getStatus() != State.Dead))
			{
				return;
			}
		}

		writeToCombatTabWithRound("Combat finished, all enemies killed");
		checkDeadTabs();
	}

	//** End Functions called by dialogs **
	//** Copy & Paste Functions **

	/**  Damages the selected combatants */
	private void damageCombatant()
	{
		int subdualType = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".Damage.Subdual",
			PreferencesDamagePanel.DAMAGE_SUBDUAL);

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
	private void deleteCombatant()
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
	private void doDamage(int damage)
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
	private void doDamage(int damage, InitHolder iH)
	{
		if (iH instanceof Combatant)
		{
			Combatant cbt = (Combatant) iH;
			State oldStatus = cbt.getStatus();
			cbt.damage(damage);

			State newStatus = cbt.getStatus();
			writeToCombatTabWithRound(cbt.getName() + " (" + cbt.getPlayer() + ") Took " + damage + " Damage: "
				+ cbt.getHP().getCurrent() + '/' + cbt.getHP().getMax());
			doMassiveDamage(cbt, damage);

			if ((oldStatus != newStatus) && (newStatus == State.Dead))
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
	private void doHeal(int heal)
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
				writeToCombatTabWithRound(cbt.getName() + " (" + cbt.getPlayer() + ") Gained " + heal + " Healing: "
					+ cbt.getHP().getCurrent() + '/' + cbt.getHP().getMax());
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
	private void doMassiveDamage(Combatant cbt, int damage)
	{
		int massiveType = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".Damage.Massive.Type",
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
			SavingThrowDialog dialog =
					new SavingThrowDialog(GMGenSystem.inst, true, cbt, 15, SavingThrowDialog.FORT_SAVE);
			dialog.setVisible(true);
			dialog.dispose();

			//Show the dialog and get it's results
			int returnVal = dialog.getReturnValue();
			int roll = dialog.getRoll();
			int total = dialog.getTotal();
			int dc = dialog.getDC();

			//Create a message out with the results
			StringBuilder sb = new StringBuilder();
			sb.append(dialog.getSaveAbbrev(dialog.getSaveType()));
			sb.append(" save DC " + dc);

			if (roll > 0)
			{
				sb.append(" with a roll of " + (roll + total));
				sb.append(" (" + total + " + Roll: " + roll + ')');
			}

			//write out the results to the combat tab
			if (returnVal == SavingThrowDialog.PASS_OPTION)
			{
				writeToCombatTabWithRound(
					cbt.getName() + " (" + cbt.getPlayer() + ") Passed a " + sb + " to avoid massive damage effects");
			}
			else if (returnVal == SavingThrowDialog.FAIL_OPTION)
			{
				writeToCombatTabWithRound(
					cbt.getName() + " (" + cbt.getPlayer() + ") Failed a " + sb + " to avoid massive damage effects");

				//Failure
				int massiveEffect = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".Damage.Massive.Effect",
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
	private void doNonLethal(int damage)
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
	private void doNonLethal(int damage, InitHolder iH)
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

				PCStat stat = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCStat.class,
					"CON");
				if (damage > pc.getTotalStatFor(stat))
				{
					isEnough = true;
				}
			}

			if (isEnough)
			{
				SavingThrowDialog dialog =
						new SavingThrowDialog(GMGenSystem.inst, true, cbt, 15, SavingThrowDialog.FORT_SAVE);
				dialog.setVisible(true);
				dialog.dispose();

				//Show the dialog and get it's results
				int returnVal = dialog.getReturnValue();
				int roll = dialog.getRoll();
				int total = dialog.getTotal();
				int dc = dialog.getDC();

				//Create a message out with the results
				StringBuilder sb = new StringBuilder();
				sb.append(dialog.getSaveAbbrev(dialog.getSaveType()));
				sb.append(" save DC " + dc);

				if (roll > 0)
				{
					sb.append(" with a roll of " + (roll + total));
					sb.append(" (" + total + " + Roll: " + roll + ')');
				}

				if (returnVal == SavingThrowDialog.PASS_OPTION)
				{
					writeToCombatTabWithRound(
						cbt.getName() + " (" + cbt.getPlayer() + ") Passed a " + sb + " to avoid unconsiousness");
					cbt.nonLethalDamage(false);
				}
				else if (returnVal == SavingThrowDialog.FAIL_OPTION)
				{
					writeToCombatTabWithRound(
						cbt.getName() + " (" + cbt.getPlayer() + ") Failed a " + sb + " to avoid unconsiousness");
					cbt.nonLethalDamage(true);
				}
			}
			combatantUpdated(cbt);
		}
	}

	/**
	 *  Do an amount of subdual damage to the selected combatants
	 *
	 *@param  damage  The amount of damage to do
	 */
	private void doSubdual(int damage)
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
	private void doSubdual(int damage, InitHolder iH)
	{
		if (iH instanceof Combatant)
		{
			Combatant cbt = (Combatant) iH;
			cbt.subdualDamage(damage);
			combatantUpdated(cbt);

			writeToCombatTabWithRound(cbt.getName() + " (" + cbt.getPlayer() + ") Took " + damage + " Subdual Damage: "
				+ cbt.getHP().getCurrent() + '(' + cbt.getHP().getSubdual() + "s)/" + cbt.getHP().getMax());
		}
	}

	//** End Combat Tab Functions **
	//** Toolbar & Button CoreUtility Functions **

	/**  Focuses the GUI on the Next Init button */
	void focusNextInit()
	{
		bNextInit.grabFocus();
	}

	/**  Focuses the GUI on the Roll button */
	void focusRoll()
	{
		bRoll.grabFocus();
	}

	/**  Heals the selected combatants */
	private void healCombatant()
	{
		String inputValue = JOptionPane.showInputDialog(this, "Heal", Integer.toString(1));

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
	private void initLast()
	{
		//Spell Tracking On
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doSpells", true))
		{
			bottomToolbar.add(bCast);
			bottomToolbar.add(bEvent);
			bottomToolbar.add(showEvents);
		}

		//Combat Tracking On
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doCombat", true))
		{
			bottomToolbar.add(bSave);
		}

		//HP Tracking On
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doHP", true))
		{
			bottomToolbar.add(bDamage);
			bottomToolbar.add(bHeal);
			bottomToolbar.add(bStabilize);
		}

		//Death Tracking On
		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doDeath", true))
		{
			bottomToolbar.add(bKill);
			bottomToolbar.add(bRaise);
			bottomToolbar.add(showDead);
		}

		boolean bShowDead = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".ShowDead", false);
		showDead.setSelected(bShowDead);

		boolean bShowEvents = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".ShowEvents", true);
		showEvents.setSelected(bShowEvents);
	}

	/**
	 *  Initialization of the bulk of preferences.
	 */
	private void initPrefs()
	{
		boolean prefsSet = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".arePrefsSet", false);

		if (!prefsSet)
		{
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".arePrefsSet", true);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".doSpells", true);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".doDeath", true);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".doHP", true);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".doMaxHP", 100);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".doMaxNum", 20);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".doNum", 20);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnName.0", "#");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnWidth.0", 25);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnName.1", "Name");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnWidth.1", 100);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnName.2", "Player");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnWidth.2", 100);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnName.3", "Status");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnWidth.3", 75);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnName.4", "+");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnWidth.4", 25);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnName.5", "Init");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnWidth.5", 25);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnName.6", "Dur");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnWidth.6", 25);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnName.7", "HP");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnWidth.7", 25);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnName.8", "HP Max");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnWidth.8", 50);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnName.9", "Type");
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnWidth.9", 50);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".NumberOfColumns", 10);
		}
	}

	/**  Kills the selected combatants */
	private void killCombatant()
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
	private void loadFromDocument(Document character, PCGenMessageHandler comp)
	{
		if (character.getRootElement().getName().equals("Party"))
		{
			Element party = character.getRootElement();
			List xmlList = party.getChildren("Character");

			for (Object aXmlList : xmlList)
			{
				Element eCharacter = (Element) aXmlList;
				InitHolder combatant = new XMLCombatant(eCharacter);
				initList.add(combatant);
			}

			List pcgList = party.getChildren("PcgCombatant");

			for (Object aPcgList : pcgList)
			{
				Element eCharacter = (Element) aPcgList;
				final PcgCombatant combatant = new PcgCombatant(eCharacter, comp, messageHandler);
				initList.add(combatant);
				addTab(combatant);
			}

			List eventList = party.getChildren("Event");

			for (Object anEventList : eventList)
			{
				Element eCharacter = (Element) anEventList;
				InitHolder combatant = new Event(eCharacter);
				initList.add(combatant);
			}

			List spellList = party.getChildren("Spell");

			for (Object aSpellList : spellList)
			{
				Element eCharacter = (Element) aSpellList;
				InitHolder combatant = new Spell(eCharacter);
				initList.add(combatant);
			}

			initList.calculateNumberField();
		}
		else if (character.getRootElement().getName().equals("Character"))
		{
			Element eCharacter = character.getRootElement();
			InitHolder combatant = new XMLCombatant(eCharacter);
			initList.add(combatant);
		}
	}

	/**
	 * Perform initial loading
	 * @param initFile
	 * @param comp
	 */
	public void loadINIT(File initFile, PCGenMessageHandler comp)
	{
		try
		{
			SAXBuilder builder = new SAXBuilder();
			Document character = builder.build(initFile);
			loadFromDocument(character, comp);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(this),
				"File load error: " + initFile.getName());
			Logging.errorPrint("File Load Error" + initFile.getName());
			Logging.errorPrint(e.getMessage(), e);
		}
	}

	/**  Moves to the next active initiative */
	private void nextInit()
	{
		int oldInit = currentInit;
		setCurrentInit(currentInit - 1);

		int bleedingTime = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".Damage.Dying",
			PreferencesDamagePanel.DAMAGE_DYING_END);

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
						writeToCombatTabWithRound(e.getPlayer() + "'s " + e.getName() + " ended");

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

			for (InitHolder anInitList : initList)
			{

				if (bleedingTime == PreferencesDamagePanel.DAMAGE_DYING_END)
				{
					if (anInitList instanceof Combatant)
					{
						bleed((Combatant) anInitList);
					}
				}

				anInitList.endRound();
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
	private void pasteNew(InitHolder toPaste)
	{
		if (toPaste instanceof XMLCombatant)
		{

			XMLCombatant cb = (XMLCombatant) toPaste;
			SystemInitiative init = cb.getInitiative();
			SystemHP hitPoints = cb.getHP();
			String name = initList.getUniqueName(cb.getName());
			InitHolder newCbt = new XMLCombatant(name, toPaste.getPlayer(), init.getAttribute().getValue(),
				hitPoints.getAttribute().getValue(), hitPoints.getMax(), hitPoints.getCurrent(), hitPoints.getSubdual(),
				init.getBonus(), cb.getCombatantType(), cb.getCR());
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
	private void pasteNew(InitHolder toPaste, int num)
	{
		for (int i = 0; i < num; i++)
		{
			pasteNew(toPaste);
		}
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
	private void performAttack(AttackModel attack, InitHolder combatant)
	{
		Vector combatants = new Vector(initList.size());

		combatants
			.addAll(
				initList.stream()
					.filter(anInitList -> (anInitList instanceof PcgCombatant) && (anInitList != combatant)
						&& ((anInitList.getStatus() != State.Dead) || showDead.isSelected()))
					.collect(Collectors.toList()));

		AttackDialog dlg = new AttackDialog(attack, combatants);
		dlg.setModal(true);
		dlg.setVisible(true);

		final List<Integer> dmgList = dlg.getDamageList();
		final List targetList = dlg.getDamagedCombatants();

		if ((dmgList != null) && (targetList != null) && (!dmgList.isEmpty()) && (!targetList.isEmpty()))
		{
			writeToCombatTabWithRound(combatant.getName() + " successfully attacks using " + attack);

			for (int i = 0; (i < dmgList.size()) && (i < targetList.size()); i++)
			{
				if (dlg.isSubdual())
				{
					int subdualType = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".Damage.Subdual",
						PreferencesDamagePanel.DAMAGE_SUBDUAL);

					if (subdualType == PreferencesDamagePanel.DAMAGE_SUBDUAL)
					{
						doSubdual(dmgList.get(i), (InitHolder) targetList.get(i));
					}
					else if (subdualType == PreferencesDamagePanel.DAMAGE_NON_LETHAL)
					{
						doNonLethal(dmgList.get(i), (InitHolder) targetList.get(i));
					}
				}
				else
				{
					doDamage(dmgList.get(i), (InitHolder) targetList.get(i));
				}
			}

			initList.sort();
			refreshTable();
		}
		else if ((dmgList != null) && (!dmgList.isEmpty()))
		{
			writeToCombatTabWithRound(combatant.getName() + " successfully attacks using " + attack);
		}
		else
		{
			writeToCombatTabWithRound(combatant.getName() + " fails with attack using " + attack);
		}

		dlg.dispose();
	}

	/**  Raises the selected combatants from the dead */
	private void raiseCombatant()
	{
		final List selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = (InitHolder) selectedList.remove(0);

			if (iH instanceof Combatant)
			{
				Combatant cbt = (Combatant) iH;
				writeToCombatTabWithRound(iH.getName() + " (" + cbt.getPlayer() + ") Raised");
				cbt.raise();
				combatantUpdated(cbt);
			}
		}

		initList.sort();
		refreshTable();
	}

	/**  Refocuses the selected combatants */
	private void refocusCombatant()
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
				writeToCombatTabWithRound(cbt.getName() + " (" + cbt.getPlayer() + ") Refocused");
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

			if (((c.getStatus() != State.Dead) || showDead.isSelected())
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
		initList.stream().filter(anInitList -> ((anInitList.getStatus() != State.Dead) || showDead.isSelected())
			&& (anInitList instanceof Combatant)).forEach(anInitList -> {
				Combatant cbt = (Combatant) anInitList;

				removeTab(cbt);
				addTab(cbt);

			});
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
	private void removeTab(InitHolder iH)
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
	private void removeTab(String name)
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
	private void rerollCombatant()
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);

			if (iH instanceof Combatant)
			{
				Combatant cbt = (Combatant) iH;
				cbt.init.check(0);
				writeToCombatTabWithRound(cbt.getName() + " (" + cbt.getPlayer() + ") Rerolled");
				combatantUpdated(cbt);
			}
		}

		initList.sort();
		refreshTable();
	}

	//** End Toolbar & Button CoreUtility Functions **
	//** Functions implementing button calls for top toolbar **

	/**  Starts a new combat, and rolls a new initiative for all combatants */
	private void roll()
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
	private void rollSave()
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

		SavingThrowDialog dialog = new SavingThrowDialog(GMGenSystem.inst, true, cbt, model);
		dialog.setVisible(true);
		dialog.dispose();

		int returnVal = dialog.getReturnValue();
		int roll = dialog.getRoll();
		int total = dialog.getTotal();
		model = dialog.getSaveModel();

		StringBuilder sb = new StringBuilder();
		sb.append(dialog.getSaveAbbrev(dialog.getSaveType()));
		sb.append(" save DC " + model.getDc());

		if (roll > 0)
		{
			sb.append(" with a roll of " + (roll + total));
			sb.append(" (" + total + " + Roll: " + roll + ')');
		}

		if (returnVal == SavingThrowDialog.PASS_OPTION)
		{
			writeToCombatTabWithRound(iH.getName() + " (" + iH.getPlayer() + ") Passed a " + sb);
		}
		else if (returnVal == SavingThrowDialog.FAIL_OPTION)
		{
			writeToCombatTabWithRound(iH.getName() + " (" + iH.getPlayer() + ") Failed a " + sb);
		}
		return model;
	}

	/**
	 *  Saves the current combatants out to an XML file
	 *
	 *@param  xml            The File to save to
	 *@exception  IOException  XML and file IO exceptions
	 */
	private void saveToDocument(File xml) throws IOException
	{
		Element party = new Element("Party");
		party.setAttribute("filever", "1.0");
		party.setAttribute("filetype", "initsave");

		/*if(currentInit > -1) {
		 party.setAttribute("current_init", Integer.toString(currentInit));
		 }*/
		initList.forEach((InitHolder anInitList) -> party.addContent(anInitList.getSaveElement()));

		XMLOutputter xmlOut = new XMLOutputter();
		xmlOut.setFormat(Format.getRawFormat().setEncoding("US-ASCII"));

		try (Writer fr = new FileWriter(xml, StandardCharsets.UTF_8))
		{
			Document saveDocument = new Document(party);
			xmlOut.output(saveDocument, fr);
			fr.flush();
		}
	}

	//** End Preferences Functions **
	//** IO Functions **

	/**  Calls up a file save dialog, and if a file is selected/created, will then save the combatants out to disk. */
	public void saveToFile()
	{
		FileChooser fileChooser = new FileChooser();
		File defaultFile = new File(PCGenSettings.getPcgDir());
		if (defaultFile.exists())
		{
			fileChooser.setInitialFileName(String.valueOf(defaultFile));
		}
		FileChooser.ExtensionFilter fileFilter =
				new FileChooser.ExtensionFilter("GMGen Initiative/Encounter Export", "gmi", "init");
		fileChooser.getExtensionFilters().add(fileFilter);
		fileChooser.setSelectedExtensionFilter(fileFilter);

		// todo: fix root window
		File xml = fileChooser.showSaveDialog(null);
		if (xml != null)
		{
			try
			{
				if (xml.exists())
				{
					Dialog<ButtonType> alert = new Alert(Alert.AlertType.CONFIRMATION);
					// todo: i18n
					alert.setTitle("File Exists");
					alert.setHeaderText("File Exists, Overwrite?");
					alert.setContentText(xml.toString() + " exists");
					Optional<ButtonType> confirmChoice = alert.showAndWait();
					if (confirmChoice.isPresent() && confirmChoice.get() == ButtonType.OK)
					{
						xml.getParentFile().mkdirs();
						saveToDocument(xml);
					}
				}
				else
				{
					xml.getParentFile().mkdirs();
					saveToDocument(xml);
				}
			}
			catch (IOException e)
			{
				Dialog<ButtonType> errorDialog = new Alert(Alert.AlertType.ERROR);
				errorDialog.setTitle("Error Writing File");
				// todo: make a specific "exception" or "scrolling" dialog
				errorDialog.setContentText(e.getMessage());
				errorDialog.showAndWait();
				Logging.errorPrint("Error Writing File");
				Logging.errorPrint(e.getMessage(), e);
			}
		}
	}

	/**  Stabilizes the selected combatants */
	private void stabilizeCombatant()
	{
		final List<InitHolder> selectedList = getSelected();

		while (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);

			if (iH instanceof Combatant)
			{
				Combatant cbt = (Combatant) iH;
				writeToCombatTabWithRound(iH.getName() + " (" + cbt.getPlayer() + ") Stabilized");
				cbt.stabilize();
				combatantUpdated(cbt);
			}
		}

		initList.sort();
		refreshTable();
	}

	/**  Calls up the CastSpell dialog, passing in the data for the first selected combatant, if there is one*/
	private void startEvent()
	{
		final List<InitHolder> selectedList = getSelected();

		if (!selectedList.isEmpty())
		{
			InitHolder iH = selectedList.remove(0);
			StartEvent dialog = new StartEvent(JOptionPane.getFrameForComponent(this), true, this, iH.getPlayer(),
				iH.getInitiative().getCurrentInitiative());
			dialog.setVisible(true);
			refreshTable();

			return;
		}

		initList.sort();
		refreshTable();

		StartEvent dialog = new StartEvent(JOptionPane.getFrameForComponent(this), true, this);
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
	private void writeToCombatTab(String message)
	{
		log.logMessage(InitiativePlugin.LOG_NAME, message);
	}

	/**
	 *  Writes out a message to the Combat window, and includes the initiative that the message was written at
	 *
	 *@param  message  Message to write to the Data window
	 */
	void writeToCombatTabWithRound(String message)
	{
		writeToCombatTab(" (Round: " + round + ", Init: " + currentInit + "): " + message);
	}

	/**
	 * <p>Called when a hyperlink is selected in one of the text panes in {@code tpaneInfo}.
	 * Used to generate attack/skill, etc. dialogs.</p>
	 *
	 * @param e {@code HyperLinkEvent} that called this method.
	 * @param cbt {@code PcgCombatant} to perform action for.
	 */
	private void hyperLinkSelected(HyperlinkEvent e, InitHolder cbt)
	{
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
		{
			PObjectModel model = PObjectModel.Factory(e.getDescription());

			if (model != null)
			{
				if ((model instanceof AttackModel) && (cbt instanceof PcgCombatant))
				{
					InitHolder pcgcbt = cbt;
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
				else if ((model instanceof DiceRollModel) && (cbt instanceof PcgCombatant))
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
	 * Fired when the selection in the {@code combatantTable} changes;
	 * if any rows are selected, it synchronizes the tab view with the first
	 * selected row.
	 * </p>
	 *
	 * @param e
	 *            {@code ListSelectionEvent} which fired this method
	 */
	private void listSelectionChanged(ListSelectionEvent e)
	{
		final int row = combatantTable.getSelectedRow();

		if (row >= 0)
		{
			final String name =
					(String) combatantTable.getValueAt(row, combatantTable.getColumnModel().getColumnIndex("Name"));

			if ((name != null) && !name.isEmpty() && (tpaneInfo.indexOfTab(name) >= 0))
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

	private void TablePopupActionPerformed(ActionEvent evt)
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
		DefaultTableModel tabModel = (DefaultTableModel) combatantTable.getModel();
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
		colModel.addColumnModelListener(new javax.swing.event.TableColumnModelListener()
		{
			@Override
			public void columnAdded(javax.swing.event.TableColumnModelEvent e)
			{
				colModAdded(e);
			}

			@Override
			public void columnMarginChanged(javax.swing.event.ChangeEvent e)
			{
				colModMarginChanged(e);
			}

			@Override
			public void columnMoved(javax.swing.event.TableColumnModelEvent e)
			{
				colModMoved(e);
			}

			@Override
			public void columnRemoved(javax.swing.event.TableColumnModelEvent e)
			{
				colModRemoved(e);
			}

			@Override
			public void columnSelectionChanged(ListSelectionEvent e)
			{
				colModSelectionChanged(e);
			}
		});
	}

	private void bAddCombatantActionPerformed(ActionEvent evt)
	{

		addCombatant();
	}

	private void bCastActionPerformed(ActionEvent evt)
	{
		castSpell();
		focusNextInit();
	}

	private void bCombatantReRollActionPerformed(ActionEvent evt)
	{

		rerollCombatant();
		focusNextInit();
	}

	private void bDamageActionPerformed(ActionEvent evt)
	{
		damageCombatant();
		focusNextInit();
	}

	private void bDeleteActionPerformed(ActionEvent evt)
	{

		deleteCombatant();
		focusNextInit();
	}

	private void bEventActionPerformed(ActionEvent evt)
	{
		startEvent();
		focusNextInit();
	}

	private void bHealActionPerformed(ActionEvent evt)
	{
		healCombatant();
		focusNextInit();
	}

	private void bKillActionPerformed(ActionEvent evt)
	{
		killCombatant();
		focusNextInit();
	}

	private void bNextInitActionPerformed(ActionEvent evt)
	{

		nextInit();
	}

	private void bRaiseActionPerformed(ActionEvent evt)
	{
		raiseCombatant();
		refreshTable();
		focusNextInit();
	}

	private void bRefocusActionPerformed(ActionEvent evt)
	{

		refocusCombatant();
		focusNextInit();
	}

	private void bRollActionPerformed(ActionEvent evt)
	{

		roll();
		focusNextInit();
	}

	private void bSaveActionPerformed(ActionEvent evt)
	{
		rollSave();
	}

	private void bStabilizeActionPerformed(ActionEvent evt)
	{
		stabilizeCombatant();
		refreshTable();
		focusNextInit();
	}

	private void bleed(Combatant cbt)
	{
		if (cbt.getStatus() == State.Bleeding)
		{
			int stableType = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".Damage.Stable",
				PreferencesDamagePanel.DAMAGE_STABLE_PERCENT);

			if (stableType == PreferencesDamagePanel.DAMAGE_STABLE_PERCENT)
			{
				int roll = new Dice(1, 100).roll();

				if (roll <= 10)
				{
					cbt.stabilize();
					writeToCombatTabWithRound(
						cbt.getName() + " (" + cbt.getPlayer() + ") auto-stabilized  (" + roll + "%)");
				}
				else
				{
					writeToCombatTabWithRound(
						cbt.getName() + " (" + cbt.getPlayer() + ") failed to auto-stabilize (" + roll + "%)");
				}
			}
			else if (stableType == PreferencesDamagePanel.DAMAGE_STABLE_SAVE)
			{
				SavingThrowDialog dialog =
						new SavingThrowDialog(GMGenSystem.inst, true, cbt, 20, SavingThrowDialog.FORT_SAVE);
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
				StringBuilder sb = new StringBuilder();
				sb.append(dialog.getSaveAbbrev(dialog.getSaveType()));
				sb.append(" save DC " + dc);

				if (roll > 0)
				{
					sb.append(" with a roll of " + (roll + total));
					sb.append(" (" + total + " + Roll: " + roll + ')');
				}

				//write out the results to the combat tab
				if (returnVal == SavingThrowDialog.PASS_OPTION)
				{
					writeToCombatTabWithRound(
						cbt.getName() + " (" + cbt.getPlayer() + ") Passed a " + sb + " to auto-stabilize");
				}
				else if (returnVal == SavingThrowDialog.FAIL_OPTION)
				{
					writeToCombatTabWithRound(
						cbt.getName() + " (" + cbt.getPlayer() + ") Failed a " + sb + " to auto-stabilize");
				}
			}

			State oldStatus = cbt.getStatus();
			cbt.bleed();
			combatantUpdated(cbt);
			State newStatus = cbt.getStatus();

			if ((oldStatus != newStatus) && (newStatus == State.Dead))
			{
				combatantDied(cbt);
			}
		}
	}

	/**
	 * <p>Casts a spell based on the specified spell model.</p>
	 *
	 * @param model A {@code SpellModel} instance
	 * @param combatant Combatant who is casting the spell.
	 */
	private void castSpell(SpellModel model, InitHolder combatant)
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

	private void colModSelectionChanged(ListSelectionEvent evt)
	{
		// TODO:  Method does nothing?
	}

	private void combatantTablePropertyChange(java.beans.PropertyChangeEvent evt)
	{

		editTableRow();
		refreshTable();
	}

	// TODO Change the Status to be a drop down list rather than a text field.
	private void editTable(int row, int column)
	{
		// Figure out which row is the active row
		// Karianna - Commented out this section to fix bug 
		/*
		int activeRow = 0;
		for (int i = 0; i < initList.size(); i++)
		{
			InitHolder c = initList.get(i);
			// IF the InitHolder status is not Dead 
			//     or showDead is selected (e.g. InitHolder is alive or we're showing the dead)
			// AND InitHolder is not an Event or we're shoeing events
			// THEN update the active row
			if ((c.getStatus() != Status.Dead || showDead.isSelected())
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
		if (!iH.getName().equals(oldName) && (iH instanceof Combatant))
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
		final javax.swing.JToolBar topToolbar = new javax.swing.JToolBar();
		final javax.swing.JPanel buttonPanelTop = new javax.swing.JPanel();
		bRoll = new JButton();
		final JButton bAddCombatant = new JButton();
		bNextInit = new JButton();
		final JButton bRefocus = new JButton();
		bCombatantReRoll = new JButton();
		bDelete = new JButton();
		final javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
		lCounter = new javax.swing.JLabel();
		jSplitPane1 = new JSplitPane();
		final javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
		final javax.swing.JScrollPane jScrollEvents = new javax.swing.JScrollPane();
		combatantTable = new javax.swing.JTable();
		tpaneInfo = new javax.swing.JTabbedPane();
		tpCombatInfo = new javax.swing.JTextArea();
		tpCombatInfo.setName("Events");
		bottomToolbar = new javax.swing.JToolBar();

		tablePopupCBNumber.setText("#");
		tablePopupCBNumber.addActionListener(this::TablePopupActionPerformed);

		tablePopup.add(tablePopupCBNumber);
		tablePopupCBName.setText("Name");
		tablePopupCBName.addActionListener(this::TablePopupActionPerformed);

		tablePopup.add(tablePopupCBName);
		tablePopupCBPlayer.setText("Player");
		tablePopupCBPlayer.addActionListener(this::TablePopupActionPerformed);

		tablePopup.add(tablePopupCBPlayer);
		tablePopupCBStatus.setText("Status");
		tablePopupCBStatus.addActionListener(this::TablePopupActionPerformed);

		tablePopup.add(tablePopupCBStatus);
		tablePopupCBPlus.setText("Plus");
		tablePopupCBPlus.addActionListener(this::TablePopupActionPerformed);

		tablePopup.add(tablePopupCBPlus);
		tablePopupCBInitiative.setText("Initiative");
		tablePopupCBInitiative.addActionListener(this::TablePopupActionPerformed);

		tablePopup.add(tablePopupCBInitiative);
		tablePopupCBDuration.setText("Duration");
		tablePopupCBDuration.addActionListener(this::TablePopupActionPerformed);

		tablePopup.add(tablePopupCBDuration);
		tablePopupCBHP.setText("HP");
		tablePopupCBHP.setEnabled(false);
		tablePopupCBHP.addActionListener(this::TablePopupActionPerformed);

		tablePopup.add(tablePopupCBHP);
		tablePopupCBHPMax.setText("HP Max");
		tablePopupCBHPMax.setEnabled(false);
		tablePopupCBHPMax.addActionListener(this::TablePopupActionPerformed);

		tablePopup.add(tablePopupCBHPMax);
		tablePopupCBType.setText("Type");
		tablePopupCBType.addActionListener(this::TablePopupActionPerformed);

		tablePopup.add(tablePopupCBType);

		setLayout(new java.awt.BorderLayout());

		setPreferredSize(new java.awt.Dimension(700, 600));
		buttonPanelTop.setLayout(new javax.swing.BoxLayout(buttonPanelTop, javax.swing.BoxLayout.X_AXIS));

		bAddCombatant.setText("Add Combatant");
		bAddCombatant.addActionListener(this::bAddCombatantActionPerformed);

		buttonPanelTop.add(bAddCombatant);

		bDuplicateCombatant.setText("Duplicate");
		bDuplicateCombatant.setEnabled(false);
		bDuplicateCombatant.addActionListener(this::bDuplicateCombatantActionPerformed);

		bDelete.setText("Delete");
		bDelete.addActionListener(this::bDeleteActionPerformed);

		buttonPanelTop.add(bDelete);

		buttonPanelTop.add(bDuplicateCombatant);

		buttonPanelTop.add(new JSeparator());

		bRoll.setText("Start Combat");
		bRoll.addActionListener(this::bRollActionPerformed);

		buttonPanelTop.add(bRoll);

		bCombatantReRoll.setText("Roll");
		bCombatantReRoll.addActionListener(this::bCombatantReRollActionPerformed);

		buttonPanelTop.add(bCombatantReRoll);

		bNextInit.setText("Next Initiative");
		bNextInit.addActionListener(this::bNextInitActionPerformed);

		buttonPanelTop.add(bNextInit);

		buttonPanelTop.add(new JSeparator());

		bRefocus.setText("Refocus");
		bRefocus.addActionListener(this::bRefocusActionPerformed);

		buttonPanelTop.add(bRefocus);

		buttonPanelTop.add(new JSeparator());

		bRefresh.setText("Refresh Tabs");
		bRefresh.addActionListener(evt -> {
			refreshTable();
			refreshTabs();
		});
		buttonPanelTop.add(bRefresh);

		topToolbar.add(buttonPanelTop);

		topToolbar.add(jPanel2);

		topToolbar.add(lCounter);

		add(topToolbar, java.awt.BorderLayout.NORTH);

		jSplitPane1.setDividerLocation(400);
		jSplitPane1.setOneTouchExpandable(true);
		jSplitPane1.setPreferredSize(new java.awt.Dimension(800, 405));
		combatantTable.addMouseListener(new PopupMouseAdapter()
		{
			@Override
			public void showPopup(final MouseEvent evt)
			{
				initTablePopup();
				tablePopup.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		});

		combatantTable.addPropertyChangeListener(this::combatantTablePropertyChange);
		combatantTable.getSelectionModel().addListSelectionListener(e -> {
			boolean hasSelection = combatantTable.getSelectedRow() > -1;
			bDuplicateCombatant.setEnabled(hasSelection);
			bDelete.setEnabled(hasSelection);
			bCombatantReRoll.setEnabled(hasSelection);
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
	 * @param evt
	 */
	private void bDuplicateCombatantActionPerformed(ActionEvent evt)
	{
		//TODO: This only works for saved pcgen files and xml combatants.
		//For pcgen files, it reloads the file, since there's no good way
		//curently to clone a PlayerCharacter.
		DefaultFormatter formatter = new NumberFormatter();
		formatter.setAllowsInvalid(false);
		formatter.setCommitsOnValidEdit(true);
		formatter.setValueClass(Integer.class);
		JFormattedTextField field = new JFormattedTextField(formatter);
		field.setValue(1);
		int choice = JOptionPane.showConfirmDialog(GMGenSystem.inst, field, "How many copies?",
			JOptionPane.OK_CANCEL_OPTION);
		if (choice == JOptionPane.CANCEL_OPTION)
		{
			return;
		}
		int count = ((Number) field.getValue()).intValue();
		for (InitHolder holderToCopy : getSelected())
		{
			if ((holderToCopy instanceof XMLCombatant) || (holderToCopy instanceof PcgCombatant))
			{
				if (holderToCopy instanceof PcgCombatant)
				{
					if ((((PcgCombatant) holderToCopy).getPC().getFileName() != null)
						&& (!((PcgCombatant) holderToCopy).getPC().getFileName().isEmpty()))
					{
						pasteNew(holderToCopy, count);
					}
					else
					{
						JOptionPane.showMessageDialog(GMGenSystem.inst,
							"Combatant " + holderToCopy.getName()
								+ " cannot be duplicated because it has not been saved to a valid .pcg file.",
							"Cannot Duplicate", JOptionPane.WARNING_MESSAGE);
					}
				}
				else
				{
					pasteNew(holderToCopy, count);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(GMGenSystem.inst,
					"Combatant " + holderToCopy.getName()
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
		bSave.addActionListener(this::bSaveActionPerformed);

		bCast.setText("Cast Spell");
		bCast.addActionListener(this::bCastActionPerformed);

		bEvent.setText("Start Event");
		bEvent.addActionListener(this::bEventActionPerformed);

		bKill.setText("Kill");
		bKill.addActionListener(this::bKillActionPerformed);

		bDamage.setText("Damage");
		bDamage.addActionListener(this::bDamageActionPerformed);

		bHeal.setText("Heal");
		bHeal.addActionListener(this::bHealActionPerformed);

		bStabilize.setText("Stabilize");
		bStabilize.addActionListener(this::bStabilizeActionPerformed);

		bRaise.setText("Raise");
		bRaise.addActionListener(this::bRaiseActionPerformed);

		showDead.setSelected(true);
		showDead.setText("Show Dead");
		showDead.addActionListener(this::showDeadActionPerformed);

		showEvents.setSelected(true);
		showEvents.setText("Show Events");
		showEvents.addActionListener(this::showEventsActionPerformed);
		bOpposedSkill.setText("Mass Skill Check");
		bOpposedSkill.addActionListener(this::opposedSkillActionPerformed);
	}

	/**
	 * <p>
	 * Handles button press for bOpposedSkill.  Opens the opposed check dialog.
	 * </p>
	 * @param e
	 */
	private void opposedSkillActionPerformed(ActionEvent e)
	{
		List<InitHolder> selected = getSelected();
		List notSelected = getUnSelected();
		OpposedCheckDialog dlg = new OpposedCheckDialog(GMGenSystem.inst, selected, notSelected);
		dlg.setModal(true);
		dlg.setVisible(true);
		dlg.dispose();
	}

	private void initTable()
	{
		initTableColumns();

		JTableHeader header = combatantTable.getTableHeader();
		header.addMouseListener(new PopupMouseAdapter()
		{
			@Override
			public void showPopup(final MouseEvent evt)
			{
				initTablePopup();
				tablePopup.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		});
		columnList = getColumnOrder();
		combatantTable.getSelectionModel().addListSelectionListener(this::listSelectionChanged);

		TableColumn typeColumn = combatantTable.getColumn("Type");
		// These are the combobox values
		String[] values = {"PC", "Enemy", "Ally", "Non Combatant"};

		// Set the combobox editor on the 1st visible column
		//int vColIndex = 0;
		//TableColumn col = table.getColumnModel().getColumn(vColIndex);
		typeColumn.setCellEditor(new TypeEditor(values));
		//typeColumn.setCellRenderer(new TypeRenderer(values));
	}

	private void initTableColumns()
	{
		DefaultTableModel tabModel = (DefaultTableModel) combatantTable.getModel();
		tabModel.setColumnCount(0);

		int colNo = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".NumberOfColumns", 0);
		int[] widths = new int[colNo];

		for (int i = 0; i < colNo; i++)
		{
			String name = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnName." + i, "");
			int width = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnWidth." + i, 100);
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

			switch (name)
			{
				case "Name":
					tablePopupCBName.setSelected(true);
					break;
				case "Player":
					tablePopupCBPlayer.setSelected(true);
					break;
				case "Status":
					tablePopupCBStatus.setSelected(true);
					break;
				case "+":
					tablePopupCBPlus.setSelected(true);
					break;
				case "Init":
					tablePopupCBInitiative.setSelected(true);
					break;
				case "Dur":
					tablePopupCBDuration.setSelected(true);
					break;
				case "HP":
					tablePopupCBHP.setSelected(true);
					break;
				case "HP Max":
					tablePopupCBHPMax.setSelected(true);
					break;
				case "#":
					tablePopupCBNumber.setSelected(true);
					break;
				case "Type":
					tablePopupCBType.setSelected(true);
					break;
				default:
					//Case not caught, should this cause an error?
					break;
			}
		}

		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doSpells", true)
			|| SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doDeath", true)
			|| SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doHP", true))
		{
			tablePopupCBDuration.setEnabled(true);
		}
		else
		{
			tablePopupCBDuration.setEnabled(false);
		}

		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doHP", true))
		{
			tablePopupCBHP.setEnabled(true);
			tablePopupCBHPMax.setEnabled(true);
		}
		else
		{
			tablePopupCBHP.setEnabled(false);
			tablePopupCBHPMax.setEnabled(false);
		}

		if (SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".doNumber", true))
		{
			tablePopupCBNumber.setEnabled(true);
		}
	}

	private void refreshEventTab()
	{
		tpCombatInfo.setText("");

		for (final InitHolder anInitList : initList)
		{
			StringBuilder sb = new StringBuilder();

			if (anInitList instanceof Event)
			{
				Event evt = (Event) anInitList;
				sb.append(evt.getName() + " (" + evt.getPlayer() + ")\n");
				sb.append("Duration: " + evt.getDuration() + '\n');

				if (evt.getEffect().isEmpty())
				{
					sb.append('\n');
				}
				else
				{
					sb.append(evt.getEffect()).append("\n\n");
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

	private void showDeadActionPerformed(ActionEvent evt)
	{
		checkDeadTabs();
		refreshTable();
		focusNextInit();
	}

	private void showEventsActionPerformed(ActionEvent evt)
	{
		refreshTable();
		focusNextInit();
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ShowEvents", showEvents.isSelected());
	}

	private void trackTable()
	{
		TableColumnModel colModel = combatantTable.getColumnModel();
		int numCols = colModel.getColumnCount();
		SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".NumberOfColumns", numCols);

		for (int i = 0; i < numCols; i++)
		{
			TableColumn col = colModel.getColumn(i);
			String name = col.getIdentifier().toString();
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnName." + i, name);
			SettingsHandler.setGMGenOption(InitiativePlugin.LOG_NAME + ".ColumnWidth." + i, col.getWidth());
		}
	}

	/**
	 * Update the initiative holder
	 * @param iH
	 */
	private void initHolderUpdated(InitHolder iH)
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
	private void combatantUpdated(Combatant cbt)
	{
		messageHandler.handleMessage(new CombatantHasBeenUpdatedMessage(GMGenSystem.inst));
	}

	//** End Other Variables **

	/**
	 * A cell editor
	 */
	private static final class TypeEditor extends DefaultCellEditor
	{

		/**
		 * Constructor
		 * @param items
		 */
		private TypeEditor(String[] items)
		{
			super(new JComboBox(items));
		}
	}

	/**
	 * A table cell renderer
	 */
	@SuppressWarnings("ALL")
	public static class TypeRenderer extends JComboBox implements TableCellRenderer
	{

		/**
		 * Constructor
		 * @param items
		 */
		public TypeRenderer(String[] items)
		{
			super(items);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
		{
			if (isSelected)
			{
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
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
