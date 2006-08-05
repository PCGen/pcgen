/*
 * InfoSummary.java
 * Copyright 2002 (C) James Dempsey
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
 * Created on June 22, 2002, 4:00 PM
 *
 * $Id$
 */
package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import pcgen.core.Constants;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.RollingMethods;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.StatList;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfo;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.NameGui;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.pcGenGUI;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.utils.FormattedCellEditor;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.JTableEx;
import pcgen.gui.utils.RegexFormatter;
import pcgen.gui.utils.ResizeColumnListener;
import pcgen.gui.utils.TableSorter;
import pcgen.gui.utils.Utility;
import pcgen.gui.utils.WholeNumberField;
import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Tab;

/**
 * <code>InfoSummary</code> is a panel which allows the user to enter
 * basic data about a character.
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public final class InfoSummary extends FilterAdapterPanel implements CharacterInfoTab
{
	private static final Tab tab = Tab.SUMMARY;
	
	private static final String NONABILITY = PropertyFactory.getString("in_sumCannotModifyANonAbility"); //$NON-NLS-1$
	private static final String STAT_UPPER_LIMIT = PropertyFactory.getString("in_sumCannotRaiseStatAbove"); // intentionally a fragment //$NON-NLS-1$
	private static final String IN_PURCHASE_MODE = PropertyFactory.getString("in_sumInPurchaseMode"); // intentionally a fragment //$NON-NLS-1$
	private static final String STAT_LOWER_LIMIT = PropertyFactory.getString("in_sumCannotLowerStatBelow"); // intentionally a fragment //$NON-NLS-1$
	private static final int STAT_COLUMN = 0;
	private static final int BASE_COLUMN = 1;
	private static final int RACE_COLUMN = 2;
	private static final int OTHER_COLUMN = 3;
	private static final int TOTAL_COLUMN = 4;
	private static final int MOD_COLUMN = 5;
	private static final int INC_COLUMN = 6;
	private static final int DEC_COLUMN = 7;
	private static boolean needsUpdate = true;
	private static final int COL_PCLEVEL = 0;
	private static final int COL_CLASSNAME = 1;
	private static final int COL_SRC = 2;
	private ClassComboModel classComboModel = null; // Model for the race combo box.
	private ClassModel pcClassTreeModel = null; // Model for the pcClassTable.
	private InfoSpecialAbilities infoSpecialAbilities;
	private JButton jButtonHP = null;
	private JButton lvlDownButton = new JButton("-"); //$NON-NLS-1$
	private JButton lvlUpButton = new JButton("+"); //$NON-NLS-1$
	private JComboBoxEx alignmentComboBox = new JComboBoxEx();
	private JComboBoxEx classComboBox = new JComboBoxEx();
	private JComboBoxEx raceComboBox = new JComboBoxEx();
	private JFrame abilitiesFrame = new JFrame(PropertyFactory.getString("in_specialabilities")); //$NON-NLS-1$
	private JLabel labelAlignment = null;
	private JLabel labelClass = null;
	private JLabel labelHPName = null;
	private JLabel labelHP = new JLabel();
	private JLabel labelName = null;
	private JLabel labelRace = null;
	private JLabel poolLabel = new JLabel(PropertyFactory.getString("in_sumStatCost")); //$NON-NLS-1$
	private JLabel poolText = new JLabel();
	private JLabel poolPointLabel = null;
	private JLabel poolPointText  = null;
	private JPanel levelPanel = new JPanel();
	private JPanel northPanel = new JPanel();
	private JPanel poolPanel = new JPanel();
	private JTableEx pcClassTable; // Contains the PC's current classes and levels
	private JTextField pcNameText = new JTextField(""); //$NON-NLS-1$
	private JTextField playerNameText = new JTextField(""); //$NON-NLS-1$
	private JTextField tabNameText = new JTextField(""); //$NON-NLS-1$
	private RaceComboModel raceComboModel = null; // Model for the race combo box.
	private WholeNumberField levelText = new WholeNumberField(1, 3);
	private String[] alignmentStrings;
	private boolean abilitiesFrameHasBeenSized = false;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	/**
	 * The listener for when the PC abilities have been changed so the
	 * PC can be updated.
	 */
	private ActionListener abilitiesListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (!abilitiesFrameHasBeenSized)
				{
					Dimension screenSize = PCGen_Frame1.getCharacterPane().getParent().getParent().getSize();
					int screenHeight = screenSize.height;
					int screenWidth = screenSize.width;

					abilitiesFrame.setSize(screenWidth, screenHeight);
					abilitiesFrameHasBeenSized = true;
				}

				abilitiesFrame.setVisible(true);
			}
		};

	/**
	 * The listener for when the PC alignment has been changed so the
	 * PC can be updated.
	 */
	private ActionListener alignmentListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (alignmentComboBox.getSelectedItem() != null)
				{
					alignmentChanged();
				}
			}
		};

	/**
	 * The listener for when the PC classes has been changed so the
	 * PC can be updated.
	 */
	private ActionListener classListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (classComboBox.getSelectedItem() != null)
				{
					final PCClass pcClass = (PCClass) classComboBox.getSelectedItem();
					setInfoLabelText(pcClass);

					if (pcClass.isQualified(pc))
					{
						labelClass.setForeground(new Color(SettingsHandler.getPrereqQualifyColor()));
					}
					else
					{
						labelClass.setForeground(new Color(SettingsHandler.getPrereqFailColor()));
					}
				}
			}
		};

	/**
	 * The listener for when a level is added to or removed from the
	 * PC controlling whether it can be updated.
	 */
	private ActionListener levelCmdListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int numLevels = levelText.getValue();

				if (numLevels <= 0)
				{
					ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumNumberOfLevelsMustBePositive"), Constants.s_APPNAME, MessageType.ERROR);

					return;
				}

				PCClass pcClass = (PCClass) classComboBox.getSelectedItem();

				if (pcClass.getDisplayName().equals(Constants.s_NONESELECTED))
				{
					ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumYouMustSelectAClass"), Constants.s_APPNAME, MessageType.ERROR);

					return;
				}
				if (pcClass == null)
				{
					ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumYouMustSelectAClass"), Constants.s_APPNAME, MessageType.ERROR);

					return;
				}

				if (!pcClass.isQualified(pc))
				{
					ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumYouAreNotQualifiedToTakeTheClass") + pcClass.getDisplayName() + ".",
						Constants.s_APPNAME, MessageType.ERROR);

					return;
				}

				// Now we make it negative to remove levels
				if (evt.getSource() == lvlDownButton)
				{
					numLevels *= -1;
				}

				addClass(pcClass, numLevels);
				pcClassTreeModel.fireTableDataChanged();
			}
		};

	/**
	 * The listener for when the user moves through the race list
	 * so the description text can be updated.
	 */
	private ActionListener raceListener = new ActionListener()
		{
			/**
			 *  Update the info label when the user changes the race that is
			 * selected in the combo box. Setting the character's race is
			 * handled on a lost focus event now.
			 *
			 * @param  evt  The ActionEvent
			 */
			public void actionPerformed(ActionEvent evt)
			{
				final Race race = (Race) raceComboBox.getSelectedItem();

				if (race.getDisplayName().equals(Constants.s_NONESELECTED)) {
					enableClassControls(false);
				}
				else if (race != null){
					setInfoLabelText(race);
					enableClassControls(true);
				}
			}
		};

	/**
	 * The listener for when the random button is pressed to generate
	 * a random name.
	 */
	private ActionListener randNameListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (nameFrame == null)
				{
					nameFrame = new NameGui(pc);
				}
				else
				{
					nameFrame.setPc(pc);
				}

				nameFrame.setVisible(true);
			}
		};


	/**
	 * The listener for when the PC name has been changed so the
	 * PC can be updated.
	 */
	private InputVerifier pcNameInputVerify = new InputVerifier()
		{
			public boolean shouldYieldFocus(JComponent input)
			{
				boolean valueOk = verify(input);
				final String entry = pcNameText.getText();

				if ((entry != null) && (!entry.equals(pc.getName())))
				{
					pc.setName(entry);
					PCGen_Frame1.forceUpdate_PlayerTabs();
				}
				return valueOk;
			}
			
			public boolean verify(JComponent input)
			{
				return true;
			}
		};

	/**
	 * The listener for when the player name has been changed so the
	 * PC can be updated.
	 */
	private InputVerifier playerNameInputVerify = new InputVerifier()
		{
			public boolean shouldYieldFocus(JComponent input)
			{
				boolean valueOk = verify(input);
				String entry = playerNameText.getText();

				if ((entry != null) && (!entry.equals(pc.getPlayersName())))
				{
					pc.setPlayersName(entry);
				}
				return valueOk;
			}
			
			public boolean verify(JComponent input)
			{
				return true;
			}
		};

	/**
	 * The listener for when the PC race has been changed so the
	 * PC can be updated.
	 */
	private FocusAdapter raceFocusListener = new FocusAdapter()
		{
			/**
			 *  Update character's race when the user moves away from
			 * the race combo box.
			 *
			 * @param  evt  The FocusEvent
			 */
			public void focusLost(FocusEvent evt)
			{
				// Temporary focus lost means something like the drop-down has
				// got focus
				if (evt.isTemporary())
				{
					return;
				}

				// Focus was really lost; update the race
				///////////////////////////////
				// If user needs to select a hitpoint value from the popup list, then
				// when running Java 1.3 racecombo doesn't loose focus. This causes
				// the race to revert to the previously selected value upon return to
				// the summary tab. Running updateRace in a thread appears to fix this...
				//
				// Byngl - November 19, 2002
				//
				final Runnable doUpdate = new Runnable()
					{
						public void run()
						{
							updateRace();
						}
					};

				SwingUtilities.invokeLater(doUpdate);

				///////////////////////////////
			}
		};

	/**
	 * The listener for when the PC tab name has been changed so the
	 * PC can be updated.
	 */
	private InputVerifier tabNameInputVerify = new InputVerifier()
		{
			public boolean shouldYieldFocus(JComponent input)
			{
				boolean valueOk = verify(input);
				String entry = tabNameText.getText();

				if ((entry != null) && (!entry.equals(pc.getTabName())))
				{
					pc.setTabName(entry);
				}
				return valueOk;
			}
			
			public boolean verify(JComponent input)
			{
				return true;
			}
		};

	private JButton abilitiesButton;
	private JButton btnAddHD = new JButton("+"); //$NON-NLS-1$
	private JButton btnRemoveHD = new JButton("-"); //$NON-NLS-1$
	private JButton btnAddKit = new JButton("Create Monster/Add Kit"); //$NON-NLS-1$
	private JButton randName;
	private JButton rollStatsButton;
	private JLabel lblHDModify = new JLabel();
	private JLabel lblMonsterlHD = new JLabel();
	private JLabel txtMonsterlHD = new JLabel();
	private JLabelPane infoPane = new JLabelPane();
	private JLabelPane statPane = new JLabelPane();
	private JLabelPane todoPane = new JLabelPane();
	private JPanel pnlHD = new JPanel();
	private JTableEx statTable;
	private NameGui nameFrame = null;
	private RendererEditor plusMinusRenderer = new RendererEditor();
	private StatTableModel statTableModel = new StatTableModel();
	private WholeNumberField txtHD = new WholeNumberField(1, 3);

	/**
	 * InfoSummary default constructor.
	 * @param pc PlayerCharacter to display summary for.
	 */
	public InfoSummary(PlayerCharacter pc)
	{
		this.pc = pc;

		infoSpecialAbilities = new InfoSpecialAbilities(pc);
		// do not remove this
		// we will use the component's name to save component specific settings
		setName(tab.toString());

		// Build the GUI components
		initComponents();

		// Restore filter settings.  Note that this will register listeners.
		FilterFactory.restoreFilterSettings(this);
	}

	public void setPc(PlayerCharacter pc)
	{
		if(this.pc != pc || pc.getSerial() > serial)
		{
			this.pc = pc;
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public PlayerCharacter getPc()
	{
		return pc;
	}

	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(".Panel.Summary.Order", tab.ordinal());
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Summary.Order", order);
	}

	public String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(tab);
	}

	public boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(tab);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List<String> getToDos()
	{
		List<String> toDoList = new ArrayList<String>();
		if (isNewCharName(pc.getName()))
		{
			toDoList.add(PropertyFactory.getString("in_sumTodoName")); //$NON-NLS-1$
		}
		if (Globals.getGameModeAlignmentText().length() != 0 && pc.getAlignment() == 9)
		{
			toDoList.add(PropertyFactory.getString("in_sumTodoAlign")); //$NON-NLS-1$
		}
		if (pc.getTotalLevels() == 0)
		{
			toDoList.add(PropertyFactory.getString("in_sumTodoStats")); //$NON-NLS-1$
		}
		if (!SettingsHandler.isAbilitiesShownAsATab())
		{
			toDoList.addAll(infoSpecialAbilities.getToDos());
		}
		return toDoList;
	}

	/**
	 * Identify if the supplied name is a default one generated by the system
	 * e.g. New1 or New2
	 * @param name The name to be checked.
	 * @return True if the name is a default.
	 */
	private boolean isNewCharName(String name)
	{
		if (name == null)
		{
			return true;
		}

		if (!name.startsWith("New") || name.length() < 4)
		{
			return false;
		}

		try
		{
			// Integer.parseInt(name.substring(3));
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	public void refresh()
	{
		if(pc.getSerial() > serial)
		{
			serial = pc.getSerial();
			forceRefresh();
		}

		//
		// If user has changed character creation method in preferences dialog after starting a new character...
		//
		if (rollStatsButton != null)
		{
			if ((pc.getTotalPlayerLevels() == 0) && (SettingsHandler.getGame().getRollMethod() == Constants.CHARACTERSTATMETHOD_ROLLED))
			{
				rollStatsButton.setEnabled(true);
			}
			else
			{
				rollStatsButton.setEnabled(false);
			}
		}
	}

	public void forceRefresh()
	{
		if(readyForRefresh)
		{
			needsUpdate = true;
			updateCharacterInfo();
			infoSpecialAbilities.setPc(pc);
		}
		else
		{
			serial = 0;
		}
	}

	public JComponent getView()
	{
		return this;
	}

	/**
	 * specifies whether the "match any" option should be available
	 * @return true
	 */
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
	 * @return true
	 */
	public boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * Get the pc name JTextField
	 * @return the pc name JTextField
	 */
	public JTextField getPcNameText()
	{
		return pcNameText;
	}

	/**
	 * specifies the filter selection mode
	 * @return FilterConstants.MULTI_MULTI_MODE = 2
	 */
	public int getSelectionMode()
	{
		return FilterConstants.MULTI_MULTI_MODE;
	}

	/**
	 * Add a Monster HD
	 * @param direction Specifies if we are adding or removing HD.
	 */
	public void addMonsterHD(int direction)
	{
		int numHD = txtHD.getValue();

		if (numHD <= 0)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumNumberOfHitDiceMustBePositive"), Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		numHD *= direction;

		//
		// Race needs to have a MONSTERCLASS:<class>,<levels> tag
		//
		final String monsterClass = pc.getRace().getMonsterClass(pc, false);

		if (monsterClass != null)
		{
			//
			// Class must exist in Global list
			//
			final PCClass aClass = Globals.getClassKeyed(monsterClass);

			if (aClass != null)
			{
				//
				// Can't allow HD to drop below racial minimum
				//
				if (numHD < 0)
				{
					final int minHD = pc.getRace().getMonsterClassLevels(pc) + pc.getRace().hitDice(pc);
					final PCClass pcClass = pc.getClassKeyed(monsterClass);
					int currentHD = pc.getRace().hitDice(pc);

					if (pcClass != null)
					{
						currentHD += pcClass.getLevel();
					}

					//
					// Don't allow a number so big it causes us to drop below minimum level
					//
					Logging.errorPrint("minHD=" + minHD + "  currentHD=" + currentHD + "  numHD=" + numHD); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

					if ((currentHD + numHD) < minHD)
					{
						numHD = minHD - currentHD;
						Logging.errorPrint("numHD modified to: " + numHD); //$NON-NLS-1$
					}

					if ((pcClass == null) || (numHD == 0) || ((currentHD + numHD) < minHD))
					{
						ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumCannotLowerHitDiceAnyMore"), Constants.s_APPNAME, MessageType.ERROR);

						return;
					}
				}

				addClass(aClass, numHD);
			}
		}
	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		this.registerFilter(FilterFactory.createPCClassFilter());
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSizeFilters(this);
		FilterFactory.registerAllRaceFilters(this);
		FilterFactory.registerAllClassFilters(this);
		FilterFactory.registerAllPrereqAlignmentFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 */
	public synchronized void refreshFiltering()
	{
		stopListeners();

		if (raceComboModel != null)
		{
			raceComboModel.updateModel();
		}

		if (classComboModel != null)
		{
			classComboModel.updateModel();
		}

		startListeners();
	}

	/**
	 * This method updates the display label containing the current HP.
	 */
	public void updateHP()
	{
		labelHP.setText(String.valueOf(pc.hitPoints()));
	}

	/**
	 * This method gets the number of stat points used in the pool
	 * @param pc The PlayerCharacter to get used stat pool for
	 * @return used stat pool
	 */
	private static int getUsedStatPool(PlayerCharacter pc)
	{
		int i = 0;

		for (int stat = 0; stat < SettingsHandler.getGame().s_ATTRIBLONG.length; ++stat)
		{
			final PCStat aStat = (PCStat) pc.getStatList().getStatAt(stat);

			if (!aStat.isRolled())
			{
				continue;
			}

			final int statValue = pc.getStatList().getBaseStatFor(SettingsHandler.getGame().s_ATTRIBSHORT[stat]);
			i += getPurchaseCostForStat(pc, statValue);
		}
		i += (int) pc.getTotalBonusTo("POINTBUY", "SPENT");
		return i;
	}

	/**
	 * This method sets the text in the race description field based on the race
	 * selected.
	 * @param aRace Race to display info for
	 */
	private void setInfoLabelText(Race aRace)
	{
		StringBuffer b = new StringBuffer();
		b.append("<html>"); //$NON-NLS-1$

		if ((aRace != null) && !aRace.getDisplayName().startsWith("<none")) //$NON-NLS-1$
		{
			b.append("<b>").append(PropertyFactory.getString("in_sumRace")).append(aRace.getDisplayName()).append("</b>"); //$NON-NLS-1$ //$NON-NLS-3$ //$NON-NLS-2$
			b.append(" &nbsp;<b>").append(PropertyFactory.getString("in_sumTYPE")).append("</b>:").append(aRace.getType()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			final String cString = aRace.preReqHTMLStrings(pc, false);

			if (cString.length() > 0)
			{
				b.append(" &nbsp;<b>").append(PropertyFactory.getString("in_sumRequirements")); //$NON-NLS-1$ //$NON-NLS-2$
				b.append("</b>:").append(cString); //$NON-NLS-1$
			}

			String bString = aRace.getSourceEntry().toString();

			if (bString.length() > 0)
			{
				b.append(" &nbsp;<b>").append(PropertyFactory.getString("in_sumSource1")); //$NON-NLS-1$ //$NON-NLS-2$
				b.append("</b>:").append(bString); //$NON-NLS-1$
			}

			final StringBuffer aString = new StringBuffer();

			for (int i = 0; i < SettingsHandler.getGame().s_ATTRIBSHORT.length; ++i)
			{
				if (aRace.isNonAbility(i))
				{
					if (aString.length() > 0)
					{
						aString.append(' ');
					}

					aString.append(SettingsHandler.getGame().s_ATTRIBSHORT[i]).append(PropertyFactory.getString("in_SumNonability")); //$NON-NLS-1$
				}
				else
				{
					if (aRace.getStatMod(i, pc) != 0)
					{
						if (aString.length() > 0)
						{
							aString.append(' ');
						}

						aString.append(SettingsHandler.getGame().s_ATTRIBSHORT[i]).append(':').append(aRace.getStatMod(i, pc));
					}
				}
			}

			if (aString.length() > 0)
			{
				b.append(" &nbsp;<b>").append(PropertyFactory.getString("in_sumStatAdj")).append("</b>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				b.append(String.valueOf(aString));
			}

			b.append(" &nbsp;<b>").append(PropertyFactory.getString("in_sumSize1")).append("</b>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			b.append(aRace.getSize());

			if (aRace.getMovement() != null) {
				final String movelabel = aRace.getMovement().toString();
				if (movelabel.length() > 0)
				{
					b.append(" &nbsp;<b>").append(PropertyFactory.getString("in_sumMove")).append("</b>").append(String.valueOf(movelabel)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}

			b.append(" &nbsp;<b>").append(PropertyFactory.getString("in_sumVision")).append("</b>").append(aRace.getDisplayVision(pc)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			if (aRace.getFavoredClass().length() != 0)
			{
				final String favClassKey = aRace.getFavoredClass();
				String favClassName = PropertyFactory.getString("in_sumVarious");
				PCClass favClass = Globals.getClassKeyed(favClassKey);
				if (favClass != null)
				{
					favClassName = favClass.getDisplayName();
				}
				b.append(" &nbsp;<b>").append("FAVORED CLASS:").append("</b>").append(favClassName); //$NON-NLS-1$
			}

			if (aRace.getLevelAdjustment(pc) > 0)
			{
				b.append(" &nbsp;<b>").append(PropertyFactory.getString("in_sumLevelAdj")).append("</b>:").append(String.valueOf(aRace.getLevelAdjustment(pc))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}

		b.append("</html>"); //$NON-NLS-1$
		infoPane.setText(b.toString());
	}

	/**
	 * This method sets the text in the class description field based on the class
	 * selected.
	 * @param aClass Class to display info for.
	 */
	private void setInfoLabelText(PCClass aClass)
	{
		if (aClass != null)
		{
			StringBuffer b = new StringBuffer();
			b.append("<html><b>").append(PropertyFactory.getString("in_sumClass")).append(aClass.getDisplayName()).append("</b>"); //$NON-NLS-3$ //$NON-NLS-1$ //$NON-NLS-2$
			b.append(" &nbsp;<b>").append(PropertyFactory.getString("in_sumTYPE")).append("</b>:").append(aClass.getType());

			final String cString = aClass.preReqHTMLStrings(pc, false);

			if (cString.length() > 0)
			{
				b.append(" &nbsp;<b>").append(PropertyFactory.getString("in_sumRequirements")).append("</b>:").append(cString); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

			String bString = aClass.getSourceEntry().toString();

			if (bString.length() > 0)
			{
				b.append(" <b>").append(PropertyFactory.getString("in_sumSource")).append("</b>:").append(bString); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

//			b.append(" <b>").append(PropertyFactory.getString("in_sumBAB1")).append("</b>").append(aClass.getAttackBonusType()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			b.append(" <b>").append(PropertyFactory.getString("in_sumHD")).append("</b>1D").append(aClass.getBaseHitDie() + ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			if (Globals.getGameModeShowSpellTab())
			{
				b.append(" <b>").append(PropertyFactory.getString("in_sumSpelltype")).append("</b>").append(aClass.getSpellType()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				b.append(" <b>").append(PropertyFactory.getString("in_sumBaseStat")).append("</b>").append(aClass.getSpellBaseStat()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}

			b.append("</html>"); //$NON-NLS-1$
			infoPane.setText(b.toString());
		}
	}

	/**
	 * This method sets the HTML text used to display calculated stats such
	 * as AC, BAB, saves, etc.
	 */
	private void setStatLabelText()
	{
		int bonus;
		StringBuffer statBuf = new StringBuffer();

		statBuf.append("<html>"); //$NON-NLS-1$

		if (pc != null)
		{
			if (Globals.getGameModeACText().length() != 0)
			{
				statBuf.append("<b>").append(Globals.getGameModeACAbbrev()).append("</b> "); //$NON-NLS-1$ //$NON-NLS-2$
				statBuf.append("<i>").append(PropertyFactory.getString("in_sumTotal")).append("</i>: ").append(pc.getACTotal()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (SettingsHandler.getGame().isValidACType("Flatfooted"))
				{
					statBuf.append(" <i>").append(PropertyFactory.getString("in_sumFlatfooted")).append("</i>: ").append(pc.flatfootedAC()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				if (SettingsHandler.getGame().isValidACType("Touch"))
				{
					statBuf.append(" <i>").append(PropertyFactory.getString("in_sumTouch")).append("</i>: ").append(pc.touchAC()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				statBuf.append("<br>"); //$NON-NLS-1$
			}
			else
			{
				statBuf.append("<b>").append("Total AC:").append("</b> ").append((int) pc.getTotalBonusTo(PropertyFactory.getString("in_sumCombat"), PropertyFactory.getString("in_sumAC"))); //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-1$ //$NON-NLS-4$ //$NON-NLS-5$
				statBuf.append("<br>"); //$NON-NLS-1$
			}

			final int initMod = pc.initiativeMod();
			statBuf.append("<b>").append(PropertyFactory.getString("in_sumInit")).append("</b>: ").append(Delta.toString(initMod)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			// BAB
			String aString = SettingsHandler.getGame().getBabAbbrev();
			if (aString == null)
			{
				aString = PropertyFactory.getString("in_sumBAB");
			}
			if ((aString != null) && (aString.length() != 0))
			{
				bonus = pc.baseAttackBonus();
				statBuf.append(" <b>").append(aString).append("</b>: ").append(bonus); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				// Include Epic Attack Bonus if there is one.
				int epicBonus = (int) pc.getBonusDueToType("COMBAT", "TOHIT", "EPIC");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (epicBonus > 0)
				{
					statBuf.append('+');//$NON-NLS-1$
					statBuf.append(epicBonus);
					statBuf.append('=');//$NON-NLS-1$
					statBuf.append(bonus + epicBonus);
				}
			}
			statBuf.append(" <b>").append(Globals.getGameModeHPAbbrev()).append("</b>: ").append(pc.hitPoints()); //$NON-NLS-1$ //$NON-NLS-2$

			if (Globals.getGameModeAltHPText().length() != 0)
			{
				statBuf.append(" <b>").append(Globals.getGameModeAltHPAbbrev()).append("</b>: ").append(pc.altHP()); //$NON-NLS-1$ //$NON-NLS-2$
			}

			for (int z = 0; z < SettingsHandler.getGame().getUnmodifiableCheckList().size(); ++z)
			{
				//
				// If the current game mode has no 'saves', then we will omit the header as we will never get here...
				//
				if (z == 0)
				{
					statBuf.append("<br>"); //$NON-NLS-1$
					statBuf.append("<b>").append(PropertyFactory.getString("in_sumSaves")).append("</b>: "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				bonus = (int) pc.getBonus(z + 1, true);
				statBuf.append(" <i>").append(SettingsHandler.getGame().getUnmodifiableCheckList().get(z).toString()) //$NON-NLS-1$
				.append("</i>: ").append(Delta.toString(bonus)); //$NON-NLS-1$
			}

			//
			// Show character's current size
			//
			statBuf.append("<br><b>").append(PropertyFactory.getString("in_sumSize")).append("</b>: ").append(pc.getSize()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		statBuf.append("</html>"); //$NON-NLS-1$
		statPane.setText(statBuf.toString());
	}

	/**
	 * This method is called to add 1+ levels to a character.
	 * @param theClass PCClass to add to the character
	 * @param levels int number of levels of the class to add
	 */
	private void addClass(PCClass theClass, int levels)
	{
		if (Globals.getGameModeAlignmentText().length() != 0)
		{
			if ((levels > 0) && (pc.getAlignment() == SettingsHandler.getGame().getIndexOfAlignment(Constants.s_NONE)))
			{
				ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumYouMustSelectAnAlignmentBeforeAddingClasses"), Constants.s_APPNAME, //$NON-NLS-1$
					MessageType.ERROR);

				return;
			}
		}

		if ((theClass == null) || !theClass.isQualified(pc))
		{
			return;
		}

		if ((levels > 0) && !pc.canLevelUp())
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_Enforce_rejectLevelUp"), Constants.s_APPNAME, MessageType.ERROR);
			return;
		}

		if ((levels > 1) && SettingsHandler.getEnforceSpendingBeforeLevelUp())
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_Enforce_oneLevelOnly"), Constants.s_APPNAME, MessageType.INFORMATION);
			levels = 1;
		}

		final PCClass aClass = pc.getClassKeyed(theClass.getKeyName());

		if (!Globals.checkRule(RuleConstants.LEVELCAP) //$NON-NLS-1$
			&& ((levels > theClass.getMaxLevel())
			|| ((aClass != null) && ((aClass.getLevel() + levels) > aClass.getMaxLevel()))))
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumMaximumLevelIs") + theClass.getMaxLevel(), Constants.s_APPNAME, MessageType.INFORMATION);

			return;
		}

		// Check with the user on their first level up
		if ((pc.getTotalLevels() == 0) && (levels > 0))
		{
			if (SettingsHandler.getGame().isPurchaseStatMode()
				&& (pc.getPointBuyPoints() > getUsedStatPool(pc)))
			{
				int proceed = JOptionPane.showConfirmDialog(this, PropertyFactory.getString("in_sumPoolWarning"), //$NON-NLS-1$
						PropertyFactory.getString("in_sumLevelWarnTitle"), JOptionPane.YES_NO_OPTION, //$NON-NLS-1$
						JOptionPane.WARNING_MESSAGE);

				if (proceed != JOptionPane.YES_OPTION)
				{
					return;
				}
			}
			else if (SettingsHandler.isShowWarningAtFirstLevelUp())
			{
				final JCheckBox shouldDisplay = new JCheckBox(PropertyFactory.getString(
							"in_sumAbilitiesWarningCheckBox"), true); //$NON-NLS-1$
				shouldDisplay.addItemListener(new ItemListener()
					{
						public void itemStateChanged(ItemEvent evt)
						{
							SettingsHandler.setShowWarningAtFirstLevelUp(shouldDisplay.isSelected());
						}
					});

				JPanel msgPanel = buildMessageLabelPanel(PropertyFactory.getString("in_sumAbilitiesWarning"), //$NON-NLS-1$
						shouldDisplay);

				int proceed = JOptionPane.showConfirmDialog(this, msgPanel,
						PropertyFactory.getString("in_sumLevelWarnTitle"), JOptionPane.YES_NO_OPTION, //$NON-NLS-1$
						JOptionPane.WARNING_MESSAGE);

				if (proceed != JOptionPane.YES_OPTION)
				{
					return;
				}
			}
		}

		pc.incrementClassLevel(levels, theClass);

		PCGen_Frame1.forceUpdate_PlayerTabs();
		CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoClasses());
		pane.setPaneForUpdate(pane.infoSkills());
		pane.setPaneForUpdate(pane.infoFeats());
		pane.setPaneForUpdate(pane.infoDomain());
		pane.setPaneForUpdate(pane.infoSpells());
		pane.setPaneForUpdate(pane.infoInventory());
		pane.refresh();

		infoSpecialAbilities.refresh();
		pane.refreshToDosAsync();

		statTable.invalidate();
		statTable.updateUI();
		setStatLabelText();

		//
		// If we've just added the first non-monster level,
		// ask to choose free item of clothing if haven't already
		//
		if (levels > 0)
		{
			TabUtils.selectClothes(pc);
		}

		forceRefresh();
	}

	/**
	 * Builds a JPanel containing the supplied message, split at each new
	 * line and an optional checkbox, suitable for use in a showMessageDialog
	 * call. This is generally useful for showing messges which can turned
	 * off either in preferences or when they are displayed.
	 *
	 * @param message The message to be displayed.
	 * @param checkbox The optional checkbox to be added - may be null.
	 * @return JPanel A panel containing the message and the checkbox.
	 */
	public static JPanel buildMessageLabelPanel(String message, JCheckBox checkbox)
	{
		JPanel panel = new JPanel();
		JLabel label;
		String part;

		panel.setLayout(new GridBagLayout());

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridx = cons.gridy = 0;
		cons.gridwidth = GridBagConstraints.REMAINDER;
		cons.gridheight = 1;
		cons.anchor = GridBagConstraints.WEST;
		cons.insets = new Insets(0, 0, 3, 0);
		cons.weightx = 1;
		cons.weighty = 0;
		cons.fill = GridBagConstraints.NONE;

		int start = 0;
		int sepPos = -1;

		do
		{
			sepPos = message.indexOf("\n", start);

			if (sepPos >= 0)
			{
				part = message.substring(start, sepPos);
				start = sepPos + 1;
			}
			else
			{
				part = message.substring(start);
				start = -1;
			}

			label = new JLabel(part, SwingConstants.LEADING);

			panel.add(label, cons);
			cons.gridy++;
		}
		while (start >= 0);

		if (checkbox != null)
		{
			label = new JLabel("", SwingConstants.LEADING);
			panel.add(label, cons);
			cons.gridy++;
			panel.add(checkbox, cons);
			cons.gridy++;
		}

		return panel;
	}
		/**
	 * This method is called when a character's alignment is changed to validate
	 * the alignment matches those allowed for the character's classes
	 */
	private void alignmentChanged()
	{
		final int newAlignment = alignmentComboBox.getSelectedIndex();
		final int oldAlignment = pc.getAlignment();

		if (newAlignment == oldAlignment)
		{
			return;
		}

		//
		// Get a list of classes that will become unqualified (and have an ex-class)
		//
		StringBuffer unqualified = new StringBuffer();
		List<PCClass> classList = pc.getClassList();
		List<PCClass> exclassList = new ArrayList<PCClass>();

		for (PCClass aClass : classList)
		{

			pc.setAlignment(oldAlignment, false, true);

			if (aClass.isQualified(pc))
			{
				pc.setAlignment(newAlignment, false, true);

				if (!aClass.isQualified(pc) && (aClass.getExClass().length() != 0))
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

		//
		// Give the user a chance to bail
		//
		if (unqualified.length() > 0)
		{
			if (JOptionPane.showConfirmDialog(null,
					PropertyFactory.getString("in_sumExClassesWarning") + Constants.s_LINE_SEP + unqualified, //$NON-NLS-1$
					Constants.s_APPNAME, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION)
			{
				pc.setAlignment(oldAlignment, false, true);
				alignmentComboBox.setSelectedIndex(oldAlignment);

				return;
			}
		}

		//
		// Convert the class(es)
		//
		for (PCClass aClass : exclassList)
		{
			pc.makeIntoExClass(aClass);
		}

		pc.setAlignment(newAlignment, false, true);
		forceRefresh();
		enableRaceControls(newAlignment != SettingsHandler.getGame().getIndexOfAlignment(Constants.s_NONE));
		PCGen_Frame1.getCharacterPane().refreshToDosAsync();
	}

	/**
	 * This method creates or refreshes the model for the PC Class menu
	 */
	private void createClassComboModel()
	{
		if (classComboModel == null)
		{
			classComboModel = new ClassComboModel();
		}
		else
		{
			classComboModel.updateModel();
		}
	}

	/**
	 * This method creates or refreshes the model for the PC Class tree
	 */
	private void createClassTreeModel()
	{
		if (pcClassTreeModel == null)
		{
			pcClassTreeModel = new ClassModel();
		}
		else
		{
			pcClassTreeModel.resetModel();
		}
	}

	/**
	 * Creates or refreshes the Models that will be used
	 */
	private void createModels()
	{
		createClassTreeModel();
		createRaceComboModel();
		createClassComboModel();
	}

	/**
	 * This method creates or refreshes the model for the PC race menu
	 */
	private void createRaceComboModel()
	{
		if (raceComboModel == null)
		{
			raceComboModel = new RaceComboModel();
		}
		else
		{
			raceComboModel.updateModel();
		}
	}

	/**
	 * This method creates the table containing the PC Class list/tree
	 */
	private void createTreeTables()
	{
		pcClassTable = new JTableEx(new TableSorter(pcClassTreeModel));
		pcClassTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pcClassTable.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					final String aString = pc.getLevelInfoClassKeyName(pcClassTable.getSelectedRow());
					final PCClass aClass = Globals.getClassKeyed(aString);

					if (aClass != null)
					{
						classComboBox.setSelectedItem(aClass);
					}
				}
			});
	}

	/**
	 * This method enables the race controls that make changes to the character
	 * @param enable true if controls should be enabled.
	 */
	private void enableRaceControls(boolean enable)
	{
		raceComboBox.setEnabled(enable);
		// tests if the race selection is valid and enables the class controls
		// TODO if (raceComboBox.getSelectedItem().equals(Race.NONE))
		if (!(((Race)raceComboBox.getSelectedItem()).getDisplayName().equals(Constants.s_NONESELECTED))) {
			enableClassControls(true);
		}
		else {
			enableClassControls(false);
		}
	}

	/**
	 * This method enables the class controls that make changes to the character
	 * @param enable true if controls should be enabled
	 */
	private void enableClassControls(boolean enable)
	{
		classComboBox.setEnabled(enable);
		levelText.setEnabled(enable);
		lvlUpButton.setEnabled(enable);
		lvlDownButton.setEnabled(enable);
	}

	/**
	 * This is called when the tab is hidden.
	 */
	private void formComponentHidden()
	{
		updateRace();
	}

	/**
	 * This is called when the tab is shown.
	 */
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.setMessageAreaTextWithoutSaving(""); //$NON-NLS-1$
		refresh();
	}

	private TableColumn setStatTableColumnPrefs(final int columnIndex, final int preferredWidth, final int align)
	{
		TableColumn col = statTable.getColumnModel().getColumn(columnIndex);
		int width = Globals.getCustColumnWidth("AbilitiesS", columnIndex); //$NON-NLS-1$

		boolean tabVisible = SettingsHandler.getGame().getSummaryTabStatColumnVisible(columnIndex);
		if (!tabVisible)
		{
			col.setMinWidth(0);
			col.setWidth(0);
			col.setMaxWidth(0);
			col.setResizable(false);
			width = 0;
		}
		else if (width == 0)
		{
			width = preferredWidth;
		}
		col.setPreferredWidth(width);

		col.addPropertyChangeListener(new ResizeColumnListener(statTable, "AbilitiesS", columnIndex)); //$NON-NLS-1$
		statTable.setColAlign(columnIndex, align);

		if (!tabVisible)
		{
			col = null;
		}
		return col;
	}

	/**
	 * This method initializes the GUI components.
	 */
	private void initComponents()
	{
		readyForRefresh = true;
		// Layout the stats table
		JScrollPane statScrollPane = new JScrollPane();
		statTable = new JTableEx();
		statTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		statTable.setModel(statTableModel);
		statTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		statTable.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					statTableMouseClicked(evt);
				}
			});

		setStatTableColumnPrefs(STAT_COLUMN, 40, SwingConstants.LEFT);

		TableColumn col = setStatTableColumnPrefs(BASE_COLUMN, 30, SwingConstants.CENTER);
		if (col != null)
		{
			//Using a formatted text field here to validate for integers.
			//FormattedCellEditor sets the focus to the component which
			//selects the entered data for over-writing.
			JFormattedTextField field = new JFormattedTextField(
					new RegexFormatter("\\d{1,4}|\\*")); //$NON-NLS-1$
			field.setHorizontalAlignment(SwingConstants.CENTER);
			col.setCellEditor(new FormattedCellEditor(field));
		}

		setStatTableColumnPrefs(RACE_COLUMN, 20, SwingConstants.CENTER);

		setStatTableColumnPrefs(OTHER_COLUMN, 20, SwingConstants.CENTER);

		setStatTableColumnPrefs(TOTAL_COLUMN, 20, SwingConstants.CENTER);

		setStatTableColumnPrefs(MOD_COLUMN, 20, SwingConstants.CENTER);

		col = statTable.getColumnModel().getColumn(INC_COLUMN);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(30);
		col.setMinWidth(30);
		col = statTable.getColumnModel().getColumn(DEC_COLUMN);
		col.setCellRenderer(plusMinusRenderer);
		col.setMaxWidth(30);
		col.setMinWidth(30);

		statScrollPane.setViewportView(statTable);

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		northPanel.setLayout(gridbag);
		c.insets = new Insets(2, 2, 2, 2);

		createModels();
		createTreeTables();

		// Layout the first column
		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelName = new JLabel(PropertyFactory.getString("in_sumCharString") + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		gridbag.setConstraints(labelName, c);
		northPanel.add(labelName);

		Utility.buildConstraints(c, 1, 0, 1, 1, 3, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(pcNameText, c);
		northPanel.add(pcNameText);

		//CoreUtility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		//c.fill = GridBagConstraints.NONE;
		//c.anchor = GridBagConstraints.EAST;
		Utility.buildConstraints(c, 1, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		randName = new JButton(PropertyFactory.getString("in_sumRandomNameString")); //$NON-NLS-1$
		gridbag.setConstraints(randName, c);
		northPanel.add(randName);
		Utility.setDescription(randName, PropertyFactory.getString("in_randNameTipString")); //$NON-NLS-1$

		Utility.buildConstraints(c, 0, 2, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;

		JLabel tabLabel = new JLabel(PropertyFactory.getString("in_tabString") + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		gridbag.setConstraints(tabLabel, c);
		northPanel.add(tabLabel);

		Utility.buildConstraints(c, 1, 2, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tabNameText, c);
		northPanel.add(tabNameText);
		tabNameText.setInputVerifier(tabNameInputVerify);

		Utility.buildConstraints(c, 0, 3, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;

		JLabel label = new JLabel(PropertyFactory.getString("in_sumPlayerString") + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 1, 3, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(playerNameText, c);
		northPanel.add(playerNameText);
		playerNameText.setInputVerifier(playerNameInputVerify);

		// Layout the second column
		Utility.buildConstraints(c, 3, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelAlignment = new JLabel(PropertyFactory.getString("in_alignString") + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		gridbag.setConstraints(labelAlignment, c);
		northPanel.add(labelAlignment);

		Utility.buildConstraints(c, 4, 0, 1, 1, 2, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(alignmentComboBox, c);
		northPanel.add(alignmentComboBox);

		alignmentComboBox.setModel(new DefaultComboBoxModel(populateAlignmentStrings()));

		Utility.buildConstraints(c, 3, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelRace = new JLabel(PropertyFactory.getString("in_raceString") + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		gridbag.setConstraints(labelRace, c);
		northPanel.add(labelRace);

		Utility.buildConstraints(c, 4, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(raceComboBox, c);
		northPanel.add(raceComboBox);

		raceComboModel = new RaceComboModel();
		raceComboBox.setModel(raceComboModel);

		Utility.buildConstraints(c, 3, 2, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		labelClass = new JLabel(PropertyFactory.getString("in_classString") + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		gridbag.setConstraints(labelClass, c);
		northPanel.add(labelClass);

		Utility.buildConstraints(c, 4, 2, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(classComboBox, c);
		northPanel.add(classComboBox);

		ClassComboBoxRenderer renderer = new ClassComboBoxRenderer();
		renderer.setPreferredSize(new Dimension(200, 17));
		classComboBox.setRenderer(renderer);

		classComboModel = new ClassComboModel();
		classComboBox.setModel(classComboModel);

		// Layout for the level panel
		Utility.buildConstraints(c, 3, 3, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel(PropertyFactory.getString("in_levelString") + ": "); //$NON-NLS-1$ //$NON-NLS-2$
		gridbag.setConstraints(label, c);
		northPanel.add(label);

		Utility.buildConstraints(c, 4, 3, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;

		//label = new JLabel(PropertyFactory.getString("in_levelString") + ": ");
		Utility.setDescription(lvlDownButton, PropertyFactory.getString("in_levelDownButtonTooltip")); //$NON-NLS-1$
		Utility.setDescription(lvlUpButton, PropertyFactory.getString("in_levelUpButtonTooltip")); //$NON-NLS-1$
		Utility.setDescription(levelText, PropertyFactory.getString("in_levelTextTooltip")); //$NON-NLS-1$

		//levelPanel.add(label);
		levelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		levelPanel.add(levelText);
		levelPanel.add(lvlUpButton);
		levelPanel.add(lvlDownButton);
		levelPanel.add(btnAddKit);
		gridbag.setConstraints(levelPanel, c);
		northPanel.add(levelPanel);

/////////////////////////
		Utility.buildConstraints(c, 0, 4, 1, 1, 0, 0);
		lblMonsterlHD.setText(PropertyFactory.getString("in_sumMonsterHitDice")); //$NON-NLS-1$
		c.anchor = GridBagConstraints.EAST;
		northPanel.add(lblMonsterlHD, c);

		Utility.buildConstraints(c, 1, 4, 1, 1, 0, 0);
		txtMonsterlHD.setText("0"); //$NON-NLS-1$
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		northPanel.add(txtMonsterlHD, c);

		Utility.buildConstraints(c, 3, 4, 1, 1, 0, 0);
		lblHDModify.setText(PropertyFactory.getString("in_sumHDToAddRem")); //$NON-NLS-1$
		c.anchor = GridBagConstraints.EAST;
		northPanel.add(lblHDModify, c);

		pnlHD.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlHD.add(txtHD);
		pnlHD.add(btnAddHD);
		pnlHD.add(btnRemoveHD);

		Utility.buildConstraints(c, 4, 4, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		northPanel.add(pnlHD, c);

		//pnlHD.add(lblHD);
		btnAddHD.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					addMonsterHD(1);
				}
			});
		btnRemoveHD.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					addMonsterHD(-1);
				}
			});

		btnAddKit.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						addKit();
					}
				});

/////////////////////////
		// Layout the Stats table
		Utility.buildConstraints(c, 0, 5, 5, 2, 0, 18);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;

		JPanel statPanel = new JPanel();
		statPanel.setLayout(new BorderLayout());
		statPanel.add(statScrollPane, BorderLayout.CENTER);

		poolPanel.add(poolLabel);
		poolText.setPreferredSize(new Dimension(60, 20));
		poolPanel.add(poolText);

		if (Globals.getGameModeHasPointPool())
		{
			if (poolPointLabel == null)
			{
				poolPointLabel = new JLabel(Globals.getGameModePointPoolName() + ": ");
				poolPanel.add(poolPointLabel);

				poolPointText = new JLabel();
				poolPointText.setPreferredSize(new Dimension(60, 20));
				poolPanel.add(poolPointText);

				showPointPool();
			}
		}

		if (Globals.getGameModeHPFormula().length() == 0)
		{
			jButtonHP = new JButton();
			jButtonHP.setText(Globals.getGameModeHPAbbrev());
			jButtonHP.setAlignmentY(0.0F);
			jButtonHP.setHorizontalAlignment(SwingConstants.LEFT);
			jButtonHP.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						pcGenGUI.showHpFrame(pc);
					}
				});
			poolPanel.add(jButtonHP);
		}
		else
		{
			labelHPName = new JLabel(Globals.getGameModeHPAbbrev());
			poolPanel.add(labelHPName);
		}
		labelHP.setText(""); //$NON-NLS-1$
		labelHP.setHorizontalAlignment(SwingConstants.TRAILING);
		poolPanel.add(labelHP);
		statPanel.add(poolPanel, BorderLayout.SOUTH);
		gridbag.setConstraints(statPanel, c);

		northPanel.add(statPanel);

		// Layout the to do pane
		todoPane.setBackground(northPanel.getBackground());
		todoPane.setContentType("text/html"); //$NON-NLS-1$
		StringBuffer todoText = new StringBuffer("<html><body></body></html>"); //$NON-NLS-1$
		todoPane.setText(todoText.toString());
		todoPane.setEditable(false);

		JScrollPane scroll = new JScrollPane();
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setViewportView(todoPane);
		scroll.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
		scroll.setBackground(new Color(255, 255, 255));

		JPanel pane1 = new JPanel();
		pane1.add(scroll);

		TitledBorder title1 = BorderFactory.createTitledBorder(PropertyFactory.getString("in_tipsString")); //$NON-NLS-1$
		title1.setTitleJustification(TitledBorder.CENTER);
		pane1.setBorder(title1);
		pane1.setLayout(new BoxLayout(pane1, BoxLayout.Y_AXIS));

		Utility.buildConstraints(c, 0, 7, 5, 1, 0, 18);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pane1, c);
		northPanel.add(pane1);

		// Layout the info pane
		infoPane.setBackground(northPanel.getBackground());
		infoPane.setContentType("text/html"); //$NON-NLS-1$
		infoPane.setText(""); //$NON-NLS-1$
		infoPane.setEditable(false);

		JScrollPane scrol2 = new JScrollPane();
		scrol2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrol2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrol2.setViewportView(infoPane);
		scrol2.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
		scrol2.setBackground(northPanel.getBackground());
		Utility.setDescription(infoPane, PropertyFactory.getString("in_sumAny_requirements_you_don__t_meet_are_in_italics._137")); //$NON-NLS-1$

		JPanel pane2 = new JPanel();
		pane2.add(scrol2);

		TitledBorder title2 = BorderFactory.createTitledBorder(PropertyFactory.getString("in_infoString")); //$NON-NLS-1$
		title2.setTitleJustification(TitledBorder.CENTER);
		pane2.setBorder(title2);
		pane2.setLayout(new BoxLayout(pane2, BoxLayout.X_AXIS));

		Utility.buildConstraints(c, 5, 0, 1, 5, 9, 0);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pane2, c);
		northPanel.add(pane2);

		// Layout for the Classes table
		col = pcClassTable.getColumnModel().getColumn(COL_PCLEVEL);
		col.setPreferredWidth(15);
		pcClassTable.setColAlign(COL_PCLEVEL, SwingConstants.CENTER);

		Utility.buildConstraints(c, 5, 5, 1, 1, 0, 18);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;

		JScrollPane scrollPane = new JScrollPane(pcClassTable);
		JPanel pane3 = new JPanel();
		pane3.add(scrollPane);

		TitledBorder title3 = BorderFactory.createTitledBorder(PropertyFactory.getString("in_classesString")); //$NON-NLS-1$
		title3.setTitleJustification(TitledBorder.CENTER);
		pane3.setBorder(title3);
		pane3.setLayout(new BoxLayout(pane3, BoxLayout.X_AXIS));
		gridbag.setConstraints(pane3, c);
		northPanel.add(pane3);

		// Abilities button
		if (!SettingsHandler.isAbilitiesShownAsATab())
		{
			Utility.buildConstraints(c, 5, 6, 1, 1, 0, 0);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.CENTER;
			abilitiesButton = new JButton(PropertyFactory.getString("in_specialabilities")); //$NON-NLS-1$
			gridbag.setConstraints(abilitiesButton, c);
			northPanel.add(abilitiesButton);

			abilitiesFrame.getContentPane().setLayout(new BorderLayout());
			abilitiesFrame.getContentPane().add(infoSpecialAbilities, BorderLayout.CENTER);

			JPanel cPanel = new JPanel();
			cPanel.setLayout(new FlowLayout());

			JButton closeButton = new JButton(PropertyFactory.getString("in_close")); //$NON-NLS-1$
			closeButton.setMnemonic(PropertyFactory.getMnemonic("in_mn_close")); //$NON-NLS-1$
			closeButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						abilitiesFrame.setVisible(false);
						PCGen_Frame1.getCharacterPane().refreshToDosAsync();
					}
				});
			cPanel.add(closeButton);
			abilitiesFrame.getContentPane().add(cPanel, BorderLayout.SOUTH);

			ClassLoader loader = getClass().getClassLoader();
			Toolkit kit = Toolkit.getDefaultToolkit();

			// according to the API, the following should *ALWAYS* use '/'
			Image img = kit.getImage(loader.getResource("pcgen/gui/resource/PcgenIcon.gif")); //$NON-NLS-1$
			loader = null;
			abilitiesFrame.setIconImage(img);
		}

		// Layout the stats pane
		statPane.setBackground(northPanel.getBackground());
		statPane.setContentType("text/html"); //$NON-NLS-1$
		statPane.setText(""); //$NON-NLS-1$
		statPane.setEditable(false);

		JScrollPane statsScroll = new JScrollPane();
		statsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		statsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		statsScroll.setViewportView(statPane);
		statsScroll.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
		statsScroll.setBackground(new Color(255, 255, 255));

		JPanel pane4 = new JPanel();
		pane4.add(statsScroll);

		TitledBorder title4 = BorderFactory.createTitledBorder(PropertyFactory.getString("in_statsString")); //$NON-NLS-1$
		title4.setTitleJustification(TitledBorder.CENTER);
		pane4.setBorder(title4);
		pane4.setLayout(new BoxLayout(pane4, BoxLayout.Y_AXIS));
		Utility.buildConstraints(c, 5, 7, 1, 1, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(pane4, c);
		northPanel.add(pane4);

		this.setLayout(new BorderLayout());
		this.add(northPanel, BorderLayout.CENTER);

		addComponentListener(new ComponentAdapter()
			{
				public void componentShown(ComponentEvent evt)
				{
					formComponentShown();
				}

				public void componentHidden(ComponentEvent evt)
				{
					formComponentHidden();
				}
			});

		addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent evt)
				{
					refresh();
				}
			});
	}

	private void addKit()
	{
		PCGen_Frame1.getInst().addKit_actionPerformed();
	}

	/**
	 * This method converts the global alignment list into an array of Strings
	 * to be used on the alignment menu.
	 * @return String[] containing the names of the alignments
	 */
	private String[] populateAlignmentStrings()
	{
		return alignmentStrings = SettingsHandler.getGame().getAlignmentListStrings(true);
	}

	/**
	 * This method refreshes the display and everything shown in it.
	 */
	private synchronized void refreshDisplay()
	{
		if (pc == null)
		{
			return;
		}

		stopListeners();

		pcNameText.setText(pc.getName());
		tabNameText.setText(pc.getTabName());
		playerNameText.setText(pc.getPlayersName());

		boolean rebuild = false;

		if (alignmentStrings.length != SettingsHandler.getGame().getUnmodifiableAlignmentList().size()) // - 1 DRH
		{
			rebuild = true;
		}
		else
		{
			String[] al = SettingsHandler.getGame().getAlignmentListStrings(true);

			for (int i = 0; i < Math.min(alignmentStrings.length, al.length); ++i)
			{
				if (!alignmentStrings[i].equals(al[i]))
				{
					rebuild = true;
				}
			}
		}

		if (rebuild)
		{
			alignmentComboBox.setModel(new DefaultComboBoxModel(populateAlignmentStrings()));
		}

		final int align = pc.getAlignment();

		if ((align > -1) && (align < alignmentStrings.length))
		{
			alignmentComboBox.setSelectedIndex(align);
		}

		final Race pcRace = pc.getRace();
		raceComboModel.setSelectedItem(pcRace);

		if ( PrereqHandler.passesAll( pcRace.getPreReqList(), pc, pcRace))
		{
			labelRace.setForeground(new Color(SettingsHandler.getPrereqQualifyColor()));
		}
		else
		{
			labelRace.setForeground(new Color(SettingsHandler.getPrereqFailColor()));
		}

		setInfoLabelText(pcRace);

		labelClass.setForeground(Color.black);

		//
		// select the last class levelled
		//
		if ((pc.getTotalLevels() == 0)) // new PC?
		{
			classComboBox.setSelectedItem(null);
		}
		else if (pc.getLevelInfoSize() != 0)
		{
			final Object lastSelection = classComboBox.getSelectedItem();

			for (int idx = pc.getLevelInfoSize() - 1; idx >= 0; --idx)
			{
				final PCClass pcClass = pc.getClassKeyed(pc.getLevelInfoClassKeyName(idx));

				if (pcClass != null)
				{
					classComboBox.setSelectedItem(Globals.getClassKeyed(pcClass.getKeyName()));

					if (classComboBox.getSelectedIndex() >= 0)
					{
						break;
					}
				}
			}

			//
			// If couldn't find a selection, then default back to the previous choice
			//
			if ((classComboBox.getSelectedIndex() < 0) && (lastSelection != null))
			{
				classComboBox.setSelectedItem(lastSelection);
			}
		}
		else if (pc.getRace().getMonsterClass(pc, false) != null)
		{
			String monsterClass = pc.getRace().getMonsterClass(pc, false);
			classComboBox.setSelectedItem( Globals.getClassKeyed(monsterClass) );
		}
		else
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumClassKindErrMsg"), Constants.s_APPNAME, //$NON-NLS-1$
				MessageType.ERROR);
		}

		final PCClass pcSelectedClass = (PCClass) classComboBox.getSelectedItem();

		if ((pcSelectedClass != null) && !pcSelectedClass.isQualified(pc))
		{
			labelClass.setForeground(new Color(SettingsHandler.getPrereqFailColor()));
		}

		createModels();
		statTableModel.fireTableDataChanged();
		statTable.invalidate();
		statTable.updateUI();
		try
		{
			pcClassTable.updateUI();
		}
		catch(Exception e) {
			// TODO Handle this?
		}

		updatePool(false);

		setStatLabelText();

		enableRaceControls(!alignmentComboBox.isVisible() || (align != SettingsHandler.getGame().getIndexOfAlignment(Constants.s_NONE)));
		startListeners();
	}

	/**
	 * This method adds the listeners to the components on the tab.
	 */
	private void startListeners()
	{
		pcNameText.setInputVerifier(pcNameInputVerify);
		randName.addActionListener(randNameListener);
		alignmentComboBox.addActionListener(alignmentListener);
		raceComboBox.addActionListener(raceListener);
		raceComboBox.addFocusListener(raceFocusListener);
		classComboBox.addActionListener(classListener);
		tabNameText.setInputVerifier(tabNameInputVerify);
		playerNameText.setInputVerifier(playerNameInputVerify);
		lvlDownButton.addActionListener(levelCmdListener);
		lvlUpButton.addActionListener(levelCmdListener);

		if (!SettingsHandler.isAbilitiesShownAsATab() && (abilitiesButton != null))
		{
			abilitiesButton.addActionListener(abilitiesListener);
		}
	}

	/**
	 * This method is invoked when the mouse is clicked on the stat table
	 * If the requested change is valid based on the rules mode selected,
	 * it performs the update on the character stat and forces the rest of
	 * the connected items to update.
	 * @param evt The MouseEvent we are processing
	 **/
	private void statTableMouseClicked(MouseEvent evt)
	{
		final int selectedStat = statTable.getSelectedRow();

		if ((selectedStat < 0) || (selectedStat >= SettingsHandler.getGame().s_ATTRIBSHORT.length))
		{
			// Ignore invalid row selection
			return;
		}

		int stat = pc.getStatList().getBaseStatFor(SettingsHandler.getGame().s_ATTRIBSHORT[selectedStat]);
		boolean makeChange = false;
//		boolean checkPurchase = false;
		int increment = 0;
		int poolMod = 0;

		final PCStat aStat = pc.getStatList().getStatAt(selectedStat);

		final int column = statTable.columnAtPoint(evt.getPoint());

		switch (column)
		{
			case STAT_COLUMN:
			case BASE_COLUMN:
			case RACE_COLUMN:
			case OTHER_COLUMN:
			case TOTAL_COLUMN:
			case MOD_COLUMN:
				break;

			case INC_COLUMN:
			{
				increment = 1;

				final int pcTotalLevels = pc.getTotalLevels();
				final int pcPlayerLevels = pc.getTotalPlayerLevels();
				final boolean isPurchaseMode = SettingsHandler.getGame().isPurchaseStatMode();

				if (pc.isNonAbility(selectedStat))
				{
					if (!SettingsHandler.isExpertGUI())
					{
						ShowMessageDelegate.showMessageDialog(NONABILITY, Constants.s_APPNAME, MessageType.ERROR);
					}
				}
				else if (stat >= aStat.getMaxValue())
				{
					if (!SettingsHandler.isExpertGUI())
					{
						ShowMessageDelegate.showMessageDialog(STAT_UPPER_LIMIT + Integer.toString(aStat.getMaxValue()), Constants.s_APPNAME,
							MessageType.ERROR);
					}
				}
				else if ((pcPlayerLevels < 2) && (stat >= SettingsHandler.getGame().getPurchaseScoreMax(pc)) && isPurchaseMode)
				{
					if (!SettingsHandler.isExpertGUI())
					{
						ShowMessageDelegate.showMessageDialog(STAT_UPPER_LIMIT + SettingsHandler.getGame().getStatDisplayText(SettingsHandler.getGame().getPurchaseScoreMax(pc)) + IN_PURCHASE_MODE,
							Constants.s_APPNAME,
							MessageType.ERROR);
					}
				}
				else
				{
					if (poolPointText != null)
					{
						//
						// If have class levels, then cost for stat increases come out of point pool
						//
						if (pcTotalLevels > 0)
						{
							poolMod = getPurchaseCostForStat(pc, stat + increment) - getPurchaseCostForStat(pc, stat);
							break;
						}
					}


					makeChange = true;

					if (isPurchaseMode && (pcTotalLevels == 0))
					{
//						checkPurchase = true;
					}
					else if (!isPurchaseMode || (pcTotalLevels > 0))
					{
						pc.setPoolAmount(Math.max(pc.getPoolAmount() - 1, 0));
					}
				}
			}

			break;

			case DEC_COLUMN:
			{
				increment = -1;

				//final int minPurchaseScore = SettingsHandler.getPurchaseModeBaseStatScore();
				final int minPurchaseScore = SettingsHandler.getGame().getPurchaseScoreMin(pc);
				final int pcTotalLevels = pc.getTotalLevels();
				final int pcPlayerLevels = pc.getTotalPlayerLevels();
				final boolean isPurchaseMode = SettingsHandler.getGame().isPurchaseStatMode();

				if (stat <= aStat.getMinValue())
				{
					if (!SettingsHandler.isExpertGUI())
					{
						ShowMessageDelegate.showMessageDialog(STAT_LOWER_LIMIT + Integer.toString(aStat.getMinValue()), Constants.s_APPNAME,
							MessageType.ERROR);
					}
				}
				else if (pc.isNonAbility(selectedStat))
				{
					if (!SettingsHandler.isExpertGUI())
					{
						ShowMessageDelegate.showMessageDialog(NONABILITY, Constants.s_APPNAME, MessageType.ERROR);
					}
				}
				else if ((pcPlayerLevels < 2) && (stat <= minPurchaseScore) && isPurchaseMode)
				{
					if (!SettingsHandler.isExpertGUI())
					{
						ShowMessageDelegate.showMessageDialog(STAT_LOWER_LIMIT + SettingsHandler.getGame().getStatDisplayText(minPurchaseScore) + IN_PURCHASE_MODE, Constants.s_APPNAME,
							MessageType.ERROR);
					}
				}
				else
				{
					//
					// If have class levels, then cost for stat decreases go back into point pool
					//
					if (poolPointText != null)
					{
						if (pcTotalLevels > 0)
						{
							poolMod = getPurchaseCostForStat(pc, stat + increment) - getPurchaseCostForStat(pc, stat);
							break;
						}
					}

					makeChange = true;

					if (!isPurchaseMode || (pcTotalLevels > 0))
					{
						pc.setPoolAmount(pc.getPoolAmount() + 1);
					}
				}
			}

			break;

			default:
				Logging.errorPrint("In InfoSummary.statTableMouseClicked the column " + column + " is not handled."); //$NON-NLS-1$ //$NON-NLS-2$

				break;
		}

		if (poolMod != 0)
		{
			//
			// Make sure there are enough pool points to raise stat
			//
			if (poolMod > 0)
			{
				if (poolMod > pc.getSkillPoints())
				{
					ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumStatPoolEmpty") + Globals.getGameModePointPoolName() + ".", Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
				}
				else
				{
					makeChange = true;
				}
			}
			else
			{
				if (pc.getStatIncrease(SettingsHandler.getGame().s_ATTRIBSHORT[selectedStat], true) <= 0)
				{
					ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumNoAddThisLevel"), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
				}
				else
				{
					makeChange = true;
				}
			}
			//
			// If we can modify the stat, then we need to pay the piper
			//
			if (makeChange)
			{
				pc.adjustFeats(- poolMod);
				showPointPool();
			}
		}

		if (makeChange)
		{
			final int preIncHpMod = (int) pc.getStatBonusTo("HP", "BONUS"); //$NON-NLS-1$ //$NON-NLS-2$

			aStat.setBaseScore(stat + increment);
			pc.saveStatIncrease(aStat.getAbb(), increment, false);

			updatePool(increment > 0);
			statTableModel.fireTableRowsUpdated(selectedStat, selectedStat);
			pc.calcActiveBonuses();
			setStatLabelText();

			final PCGen_Frame1 rootFrame = PCGen_Frame1.getInst();

			if ((int) pc.getStatBonusTo("HP", "BONUS") != preIncHpMod) //$NON-NLS-1$ //$NON-NLS-2$
			{
				rootFrame.hpTotal_Changed();
			}

			CharacterInfo pane = PCGen_Frame1.getCharacterPane();
			pane.setPaneForUpdate(pane.infoSkills());
			pane.setPaneForUpdate(pane.infoSpells());
			pane.refresh();

			// We should be updating here too.
			setStatLabelText();
		}
	}

	/**
	 * This method removes the listeners from the components on the tab.
	 */
	private void stopListeners()
	{
		pcNameText.setInputVerifier(null);
		randName.removeActionListener(randNameListener);
		alignmentComboBox.removeActionListener(alignmentListener);
		raceComboBox.removeActionListener(raceListener);
		raceComboBox.removeFocusListener(raceFocusListener);
		classComboBox.removeActionListener(classListener);
		tabNameText.setInputVerifier(null);
		playerNameText.setInputVerifier(null);
		lvlDownButton.removeActionListener(levelCmdListener);
		lvlUpButton.removeActionListener(levelCmdListener);

		if (!SettingsHandler.isAbilitiesShownAsATab() && (abilitiesButton != null))
		{
			abilitiesButton.removeActionListener(abilitiesListener);
		}
	}

	/**
	 * This method updates the local reference to the currently selected
	 * character and updates the displayed information.
	 */
	private void updateCharacterInfo()
	{
		lblMonsterlHD.setVisible(SettingsHandler.hideMonsterClasses());
		txtMonsterlHD.setVisible(SettingsHandler.hideMonsterClasses());
		lblHDModify.setVisible(SettingsHandler.hideMonsterClasses());
		pnlHD.setVisible(SettingsHandler.hideMonsterClasses());

		if ((pc == null) || !needsUpdate)
		{
			return;
		}
		infoSpecialAbilities.refresh();

		final Race race = pc.getRace();
		if (race != null)
		{
			raceComboBox.setSelectedItem(race.getDisplayName());
		}
		showPointPool();
		updateHP();

		if (Globals.getGameModeAlignmentText().length() == 0)
		{
			labelAlignment.setVisible(false);
			alignmentComboBox.setVisible(false);
		}
		else
		{
			labelAlignment.setVisible(true);
			alignmentComboBox.setVisible(true);
		}

		if (pnlHD.isVisible())
		{
			updateHD();
			txtHD.setValue(1);
		}

		levelText.setValue(1);
		needsUpdate = false;

		refreshDisplay();
	}

	private void updateHD()
	{
		int monsterHD = -1;
		int minLevel = 0;

		if (pc != null)
		{
			final String monsterClass = pc.getRace().getMonsterClass(pc, false);

			if (monsterClass != null)
			{
				monsterHD = pc.getRace().hitDice(pc);
				minLevel = pc.getRace().hitDice(pc) + pc.getRace().getMonsterClassLevels(pc);

				final PCClass aClass = pc.getClassKeyed(monsterClass);

				if (aClass != null)
				{
					monsterHD += aClass.getLevel();
				}
			}
		}

		btnAddHD.setEnabled(pc.getRace().hasAdvancement() && (monsterHD >= 0));
		btnRemoveHD.setEnabled(monsterHD > minLevel);

		if (monsterHD < 0)
		{
			monsterHD = 0;
		}

		txtMonsterlHD.setText(Integer.toString(monsterHD));
		txtHD.setEnabled(btnAddHD.isEnabled() | btnRemoveHD.isEnabled());
	}

	/**
	 * This method updates the purchase point pool.
	 * @param checkPurchasePoints boolean true if the pool should be checked
	 * for available points before doing the update.
	 */
	private void updatePool(boolean checkPurchasePoints)
	{
		int usedStatPool = getUsedStatPool(pc);

		// This is a problem for races with non-0 level
		// adjustment so only count PC & NPC levels, not
		// monster levels XXX
		int pcPlayerLevels = pc.getTotalPlayerLevels();

		int maxDiddleLevel;
		if (poolPointText != null)
		{
			maxDiddleLevel = 0;
		}
		else
		{
			maxDiddleLevel = 1;
		}


		// Let them dink on stats at 0th or 1st PC levels
		if (pcPlayerLevels <= maxDiddleLevel)
		{
			pc.setCostPool(usedStatPool);
			pc.setPoolAmount(usedStatPool);
		}

		// Handle purchase mode for stats
		if (SettingsHandler.getGame().isPurchaseStatMode())
		{
			final String bString = Integer.toString(pc.getCostPool());
//			int availablePool = SettingsHandler.getPurchaseModeMethodPool();
			int availablePool = pc.getPointBuyPoints();
			if (availablePool < 0)
			{
				availablePool = RollingMethods.roll(SettingsHandler.getGame().getPurchaseModeMethodPoolFormula());
				pc.setPointBuyPoints(availablePool);
			}

			if (availablePool != 0)
			{
				poolLabel.setText(PropertyFactory.getString("in_sumStatCost")); //$NON-NLS-1$
				poolText.setText(bString + " / " + availablePool); //$NON-NLS-1$
			}

			if (checkPurchasePoints && (availablePool != 0))
			{
				//
				// Let the user know that they've exceded their goal, but allow them to keep going if they want...
				// Only do this at 1st level or lower
				//
				if ((pcPlayerLevels <= maxDiddleLevel) && (availablePool > 0) && (usedStatPool > availablePool))
				{
					ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumYouHaveExcededTheMaximumPointsOf") + availablePool + PropertyFactory.getString("in_sumAsSpecifiedByTheMethod") //$NON-NLS-1$ //$NON-NLS-2$
					+ SettingsHandler.getGame().getPurchaseModeMethodName() + "\"",
						Constants.s_APPNAME, MessageType.INFORMATION);
				}
			}
		}

		//
		// Create the button if there are any defined rolling methods, but only enable it if
		// the current character generation method selecteded is 'rolled' and user hasn't
		// added any classes
		//
		if (rollStatsButton != null)
		{
			if ((pcPlayerLevels == 0) && (SettingsHandler.getGame().getRollMethod() == Constants.CHARACTERSTATMETHOD_ROLLED))
			{
				rollStatsButton.setEnabled(true);
			}
			else
			{
				rollStatsButton.setEnabled(false);
			}
		}
		else if (SettingsHandler.getGame().getRollingMethod(0) != null)
		{
			rollStatsButton = new JButton(PropertyFactory.getString("in_demAgeRoll"));
			rollStatsButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						pc.rollStats(Constants.CHARACTERSTATMETHOD_ROLLED);

						statTableModel.fireTableRowsUpdated(0, SettingsHandler.getGame().s_ATTRIBLONG.length);
						pc.calcActiveBonuses();

						updatePool(false);

						final CharacterInfo pane = PCGen_Frame1.getCharacterPane();
						pane.setPaneForUpdate(pane.infoSkills());
						pane.setPaneForUpdate(pane.infoSpells());
						pane.refresh();

						setStatLabelText();
					}
				});
			poolPanel.add(rollStatsButton);
		}

		// Non-purchase mode for stats
		if (!SettingsHandler.getGame().isPurchaseStatMode() || (pc.getPointBuyPoints() == 0))
		{
			poolLabel.setText(PropertyFactory.getString("in_sumStatTotal")); //$NON-NLS-1$

			int statTotal = 0;
			int modTotal = 0;
			final StatList statList = pc.getStatList();

			for (int i = 0; i < SettingsHandler.getGame().s_ATTRIBLONG.length; ++i)
			{
				final PCStat aStat = pc.getStatList().getStatAt(i);

				if (pc.isNonAbility(i) || !aStat.isRolled())
				{
					continue;
				}

				final int currentStat = statList.getBaseStatFor(SettingsHandler.getGame().s_ATTRIBSHORT[i]);
				final int currentMod = statList.getStatModFor(SettingsHandler.getGame().s_ATTRIBSHORT[i]);

				statTotal += currentStat;
				modTotal += currentMod;
			}

			poolLabel.setText(PropertyFactory.getString("in_sumStatTotal") + Integer.toString(statTotal) + PropertyFactory.getString("in_sumModifierTotal") //$NON-NLS-1$ //$NON-NLS-2$
				+ Integer.toString(modTotal));
			poolText.setText(""); //$NON-NLS-1$
		}
	}

	/**
	 * This method is called by the listeners on the race combo box
	 * to handle a change.  It will only force updates to the GUI if
	 * a change was actually made to the character's race.
	 * author dhibbs aka Sage Sam
	 * @since 28 Oct 2002
	 */
	private void updateRace()
	{
		if (raceComboBox.getSelectedItem() != null)
		{
			final Race r = (Race) raceComboBox.getSelectedItem();
			final Race oldRace = pc.getRace();

			if (!r.equals(oldRace))
			{
				//
				// Remove any monster class levels associated with old race (in excess of freebies)
				//
				if (pnlHD.isVisible())
				{
					final String monsterClass = oldRace.getMonsterClass(pc, false);

					if (monsterClass != null)
					{
						final PCClass aClass = pc.getClassKeyed(monsterClass);

						if (aClass != null)
						{
							final int numLevels = aClass.getLevel() - oldRace.getMonsterClassLevels(pc);

							if (numLevels > 0)
							{
								addClass(aClass, -numLevels);
							}
						}
					}
				}

				pc.setRace(r);

				if (pnlHD.isVisible())
				{
					updateHD();
				}

				PCGen_Frame1.forceUpdate_PlayerTabs();
				CharacterInfo pane = PCGen_Frame1.getCharacterPane();
				pane.setPaneForUpdate(pane.infoRace());
				pane.setPaneForUpdate(pane.infoFeats());
				pane.setPaneForUpdate(pane.infoSkills());
				pane.setPaneForUpdate(pane.infoSpells());
				infoSpecialAbilities.refresh();
				pane.refreshToDosAsync();

				if (pc.getRace().hitDice(pc) != 0)
				{
					pc.getRace().rollHP(pc);
				}

				showPointPool();
				updateHP();
				refreshDisplay();
			}
		}
	}

	private final class ClassModel extends AbstractTableModel
	{
		public Class<?> getColumnClass(int column)
		{
			return String.class;
		}

		public int getColumnCount()
		{
			return 3;
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case COL_PCLEVEL:
					return PropertyFactory.getString("in_sumLevel"); //$NON-NLS-1$

				case COL_CLASSNAME:
					return PropertyFactory.getString("in_sumClassLvl"); //$NON-NLS-1$

				case COL_SRC:
					return PropertyFactory.getString("in_sumSource"); //$NON-NLS-1$

				default:
					return PropertyFactory.getString("in_sumOut_of_Bounds"); //$NON-NLS-1$
			}
		}

		/** <code>getRowCount()
		 * returns the number of rows. Gets the number of stats from Globals.s_ATTRIBLONG
		 * @return row count
		 */
		public int getRowCount()
		{
			int iCount = 0;

			if (pc != null)
			{
				PCClass aClass;

				for (int idx = 0; idx < pc.getLevelInfoSize(); ++idx)
				{
					aClass = pc.getClassKeyed(pc.getLevelInfoClassKeyName(idx));

					if ((aClass != null) && !shouldDisplayThis(aClass))
					{
						continue;
					}

					++iCount;
				}
			}

			return iCount;
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			String retStr = ""; //$NON-NLS-1$

			switch (columnIndex)
			{
				case COL_PCLEVEL:
					retStr = Integer.toString(rowIndex + 1);

					break;

				case COL_CLASSNAME:

					if (pc != null)
					{
						PCClass aClass = null;
						int lvl = 0;

						for (int idx = 0; idx < pc.getLevelInfoSize(); ++idx)
						{
							final String classKeyName = pc.getLevelInfoClassKeyName(idx);
							aClass = pc.getClassKeyed(classKeyName);

							if ((aClass == null) || !shouldDisplayThis(aClass))
							{
								continue;
							}

							if (rowIndex-- == 0)
							{
								retStr = aClass.getDisplayName();

								final String subClass = aClass.getDisplayClassName();

								if (!retStr.equals(subClass))
								{
									retStr = retStr + "/" + subClass; //$NON-NLS-1$
								}

								lvl = pc.getLevelInfoClassLevel(idx);

								break;
							}
						}

						if ((aClass == null) || (aClass.getLevel() == lvl))
						{
							retStr += (" (" + Integer.toString(lvl) + ')'); //$NON-NLS-1$
						}
					}

					break;

				case COL_SRC:

					if (pc != null)
					{
						PCClass aClass = null;

						for (int idx = 0; idx < pc.getLevelInfoSize(); ++idx)
						{
							final String classKey = pc.getLevelInfoClassKeyName(idx);
							aClass = pc.getClassKeyed(classKey);

							if ((aClass != null) && !shouldDisplayThis(aClass))
							{
								continue;
							}

							if (rowIndex-- == 0)
							{
								break;
							}
						}

						if (aClass != null)
						{
							retStr = aClass.getSourceEntry().toString();
						}
					}

					break;

				default:
					retStr = PropertyFactory.getString("in_sumOut_of_Bounds"); //$NON-NLS-1$
			}

			return retStr;
		}

		private void resetModel()
		{
			fireTableDataChanged();
		}

		/**
		 * return a boolean to indicate if the item should be included in the list.
		 * Only Weapon, Armor and Shield type items should be checked for proficiency.
		 * @param aClass The Class we are testing
		 * @return true if it should be displayed
		 */
		private boolean shouldDisplayThis(final PCClass aClass)
		{
			if (SettingsHandler.hideMonsterClasses() && aClass.isMonster())
			{
				return false;
			}

			return true;
		}
	}

	/**
	 * ComboBox model to manage the list of classes. This model supports
	 * filtering in addition to the usual combo box things.
	 */
	private final class ClassComboModel extends DefaultComboBoxModel
	{
		private ClassComboModel()
		{
			updateModel();
		}

		private void updateModel()
		{
			final Object pcClass = getSelectedItem();
			removeAllElements();

			for (PCClass aClass : Globals.getClassList())
			{
				if (SettingsHandler.hideMonsterClasses() && aClass.isMonster())
				{
					continue;
				}

				if (aClass.isVisible() && accept(pc, aClass))
				{
					addElement(aClass);
				}
			}

			// Make sure empty class is in all lists
			PCClass aNullClass = new PCClass();
			aNullClass.setName(Constants.s_NONESELECTED);
			insertElementAt(aNullClass, 0);

			if (pcClass != null) {
				setSelectedItem(pcClass);
			}
			else {
				setSelectedItem(aNullClass);
			}
		}
	}

	/**
	 * ComboBox model to manage the list of races. This model supports
	 * filtering in addition to the usual combo box things.
	 */
	private final class RaceComboModel extends DefaultComboBoxModel
	{
		private RaceComboModel()
		{
			updateModel();
		}

		private void updateModel()
		{
			final Object pcRace = getSelectedItem();
			removeAllElements();

			for (Iterator<Race> it = Globals.getRaceMap().values().iterator(); it.hasNext();)
			{
				final Race aRace = (Race) it.next();

				if (accept(pc, aRace))
				{
					addElement(aRace);
				}
			}

			// Make sure empty race is in all lists
			if (getIndexOf(Globals.s_EMPTYRACE) < 0)
			{
				insertElementAt(Globals.s_EMPTYRACE, 0);
			}

			// Make sure the currently selected race is still available.
			// This is done to ensure that filtering doesn't change the PC's race.
			if ((pcRace != null) && (getIndexOf(pcRace) < 0))
			{
				insertElementAt(pcRace, 1);
			}

			setSelectedItem(pcRace);
		}
	}

	private final class RendererEditor implements TableCellRenderer
	{
		private DefaultTableCellRenderer def = new DefaultTableCellRenderer();
		private JButton plusButton = new JButton("+"); //$NON-NLS-1$

		private RendererEditor()
		{
			def.setBackground(InfoSummary.this.getBackground());
			def.setAlignmentX(Component.CENTER_ALIGNMENT);
			def.setHorizontalAlignment(SwingConstants.CENTER);
			plusButton.setPreferredSize(new Dimension(30, 24));
			plusButton.setMinimumSize(new Dimension(30, 24));
			plusButton.setMaximumSize(new Dimension(30, 24));
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column)
		{
			if (column == INC_COLUMN)
			{
				def.setText("+"); //$NON-NLS-1$
				def.setBorder(BorderFactory.createEtchedBorder());

				return def;
			}
			else if (column == DEC_COLUMN)
			{
				def.setText("-"); //$NON-NLS-1$
				def.setBorder(BorderFactory.createEtchedBorder());

				return def;
			}

			return null;
		}
	}

	/**
	 * This class is the model for the stat table
	 **/
	private final class StatTableModel extends AbstractTableModel
	{
		public boolean isCellEditable(int row, int col)
		{
			return (col == 1);
		}

		public Class<?> getColumnClass(int columnIndex)
		{
			return getValueAt(0, columnIndex).getClass();
		}

		public int getColumnCount()
		{
			return (DEC_COLUMN + 1);
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case STAT_COLUMN:
					return PropertyFactory.getString("in_sumStat"); //$NON-NLS-1$

				case BASE_COLUMN:
					return PropertyFactory.getString("in_sumScoreEditable"); //$NON-NLS-1$

				case RACE_COLUMN:
					return PropertyFactory.getString("in_sumRaceAdj"); //$NON-NLS-1$

				case OTHER_COLUMN:
					return PropertyFactory.getString("in_sumOtherAdj"); //$NON-NLS-1$

				case TOTAL_COLUMN:
					return PropertyFactory.getString("in_sumTotal"); //$NON-NLS-1$

				case MOD_COLUMN:
					return PropertyFactory.getString("in_sumMod"); //$NON-NLS-1$

				case INC_COLUMN:
					return "+"; //$NON-NLS-1$

				case DEC_COLUMN:
					return "-"; //$NON-NLS-1$

				default:
					return PropertyFactory.getString("in_sumOut_of_Bounds"); //$NON-NLS-1$
			}
		}

		/**
		 * <code>getRowCount()</code>
		 * returns the number of rows
		 * Gets the number of stats from Globals.s_ATTRIBLONG
		 * @return row count
		 **/
		public int getRowCount()
		{
			if (pc != null)
			{
				return SettingsHandler.getGame().s_ATTRIBLONG.length;
			}
			return 0;
		}

		public void setValueAt(Object obj, int rowIndex, int columnIndex)
		{
			if ((rowIndex >= 0) && (rowIndex < pc.getStatList().size()) && (columnIndex == 1))
			{
				if (obj == null)
				{
					return;
				}

				final int statVal;
				if (!"*".equals(obj.toString())) //$NON-NLS-1$
				{
					statVal = Delta.parseInt(obj.toString());
				}
				else
				{
					statVal = 10;
				}
				final int pcPlayerLevels = pc.getTotalPlayerLevels();

				final PCStat aStat = pc.getStatList().getStatAt(rowIndex);

				if (pc.isNonAbility(rowIndex))
				{
					ShowMessageDelegate.showMessageDialog(NONABILITY, Constants.s_APPNAME, MessageType.ERROR);

					return;
				}
				else if (statVal < aStat.getMinValue())
				{
					ShowMessageDelegate.showMessageDialog(STAT_LOWER_LIMIT + SettingsHandler.getGame().getStatDisplayText(aStat.getMinValue()), Constants.s_APPNAME,
						MessageType.ERROR);

					return;
				}
				else if (statVal > aStat.getMaxValue())
				{
					ShowMessageDelegate.showMessageDialog(STAT_UPPER_LIMIT + SettingsHandler.getGame().getStatDisplayText(aStat.getMaxValue()), Constants.s_APPNAME,
						MessageType.ERROR);

					return;
				}
				else if ((pcPlayerLevels < 2) && SettingsHandler.getGame().isPurchaseStatMode())
				{
					final int maxPurchaseScore = SettingsHandler.getGame().getPurchaseScoreMax(pc);

					if (statVal > maxPurchaseScore)
					{
						ShowMessageDelegate.showMessageDialog(STAT_UPPER_LIMIT + SettingsHandler.getGame().getStatDisplayText(maxPurchaseScore) + IN_PURCHASE_MODE, Constants.s_APPNAME,
							MessageType.ERROR);

						return;
					}

					final int minPurchaseScore = SettingsHandler.getGame().getPurchaseModeBaseStatScore(pc);

					if (statVal < minPurchaseScore)
					{
						ShowMessageDelegate.showMessageDialog(STAT_LOWER_LIMIT + SettingsHandler.getGame().getStatDisplayText(minPurchaseScore) + IN_PURCHASE_MODE, Constants.s_APPNAME,
							MessageType.ERROR);

						return;
					}
				}

				final int baseScore = aStat.getBaseScore();
				if (poolPointText != null)
				{
					if (pcPlayerLevels > 0)
					{
						int poolMod = getPurchaseCostForStat(pc, statVal) - getPurchaseCostForStat(pc, baseScore);
						//
						// Adding to stat
						//
						if (poolMod > 0 )
						{
							if (poolMod > pc.getSkillPoints())
							{
								ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumStatPoolEmpty") + Globals.getGameModePointPoolName() + ".", Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
								return;
							}
						}
						else if (poolMod < 0)
						{
							if (pc.getStatIncrease(aStat.getAbb(), true) < Math.abs(statVal - baseScore))
							{
								ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_sumStatStartedHigher"), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
								return;
							}
						}

						pc.adjustFeats( - poolMod);
						showPointPool();
					}
				}

				aStat.setBaseScore(statVal);
				pc.saveStatIncrease(aStat.getAbb(), statVal - baseScore, false);
				setStatLabelText();

				statTableModel.fireTableRowsUpdated(rowIndex, rowIndex);
				pc.calcActiveBonuses();

				updatePool(true);
				if (rowIndex == BASE_COLUMN)
				{
					// TODO This if switch currently does nothing?
				}
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (columnIndex == 0)
			{
				if ((rowIndex >= 0) && (rowIndex < SettingsHandler.getGame().s_ATTRIBLONG.length))
				{
					return SettingsHandler.getGame().s_ATTRIBLONG[rowIndex];
				}
				return PropertyFactory.getString("in_sumOut_of_Bounds"); //$NON-NLS-1$
			}

			final String aStat = SettingsHandler.getGame().s_ATTRIBSHORT[rowIndex];

			switch (columnIndex)
			{
				case BASE_COLUMN:

					if (pc.isNonAbility(rowIndex))
					{
						return "*"; //$NON-NLS-1$
					}

					return new Integer(pc.getStatList().getBaseStatFor(aStat));

				case RACE_COLUMN:

					if (pc.isNonAbility(rowIndex))
					{
						return "*"; //$NON-NLS-1$
					}

					//return new Integer(currentPC.getStatList().getTotalStatFor(aStat) - currentPC.getStatList().getBaseStatFor(aStat));
					int rBonus = (int) pc.getRaceBonusTo("STAT", aStat); //$NON-NLS-1$

					return new Integer(rBonus);

				case OTHER_COLUMN:

					if (pc.isNonAbility(rowIndex))
					{
						return "*"; //$NON-NLS-1$
					}

					int iRace = (int) pc.getRaceBonusTo("STAT", aStat); //$NON-NLS-1$

					return new Integer(pc.getStatList().getTotalStatFor(aStat)
						- pc.getStatList().getBaseStatFor(aStat) - iRace);

				case TOTAL_COLUMN:

					if (pc.isNonAbility(rowIndex))
					{
						return "*"; //$NON-NLS-1$
					}

//					return new Integer(pc.getStatList().getTotalStatFor(aStat));
					return SettingsHandler.getGame().getStatDisplayText(pc.getStatList().getTotalStatFor(aStat));

				case MOD_COLUMN:

					if (pc.isNonAbility(rowIndex))
					{
						return new Integer(0);
					}

					return new Integer(pc.getStatList().getStatModFor(aStat));

				case INC_COLUMN:

					return "+"; //$NON-NLS-1$

				case DEC_COLUMN:

					return "-"; //$NON-NLS-1$

				default:
					return PropertyFactory.getString("in_sumOut_of_Bounds"); //$NON-NLS-1$
			}
		}
	}

	private void showPointPool()
	{
		if (poolPointText == null)
		{
			return;
		}

		int poolPointsTotal = 0;

		for (PCLevelInfo pcl : pc.getLevelInfo())
		{
			poolPointsTotal += pcl.getSkillPointsGained();
		}

		int poolPointsUsed = poolPointsTotal - pc.getSkillPoints();

		poolPointText.setText(Integer.toString(poolPointsUsed) + " / " + Integer.toString(poolPointsTotal));
	}

	private static int getPurchaseCostForStat(final PlayerCharacter aPC, int statValue)
	{
		final int iMax = SettingsHandler.getGame().getPurchaseScoreMax(aPC);
		final int iMin = SettingsHandler.getGame().getPurchaseScoreMin(aPC);

		if (statValue > iMax)
		{
			statValue = iMax;
		}

		if (statValue >= iMin)
		{
			return SettingsHandler.getGame().getAbilityScoreCost(statValue - iMin);
		}
		return 0;
	}

	class ClassComboBoxRenderer extends JLabel implements ListCellRenderer
	{

		/**
		 * Constructor
		 */
		public ClassComboBoxRenderer()
		{
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			if (value != null) {
				final PCClass aClass = (PCClass)value;
				setText(aClass.getDisplayName());
				if (isSelected)
				{
					if (aClass.isQualified(pc)) {
						setBackground(list.getSelectionBackground());
						setForeground(list.getSelectionForeground());
					}
					else {
						setBackground(Color.RED);
						setForeground(list.getSelectionForeground());
					}
				}
				else
				{
					if (aClass.isQualified(pc)) {
						setBackground(list.getBackground());
						setForeground(list.getForeground());
					}
					else {
						setBackground(list.getBackground());
						setForeground(Color.RED);
					}
				}
			}

			return this;
		}
	}

	/**
	 * Update the displayed list of tasks to be done to complete the
	 * current character.
	 * @param todoList The list of TODOs we are adding to
	 */
	public void setToDoList(List<String> todoList)
	{
		StringBuffer todoText = new StringBuffer("<html><body>"); //$NON-NLS-1$

		int i=1;
		for (String task : todoList)
		{
			todoText.append(i++).append(". ").append(task).append("<br>"); //$NON-NLS-1$
		}

		todoText.append("</body></html>"); //$NON-NLS-1$
		todoPane.setText(todoText.toString());
		todoPane.setEditable(false);

	}
}
