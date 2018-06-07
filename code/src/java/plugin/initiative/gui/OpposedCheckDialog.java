/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2004 Ross M. Lodge
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * OpposedCheckDialog.java
 */

package plugin.initiative.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.TableModel;

import gmgen.GMGenSystem;
import gmgen.plugin.InitHolder;
import gmgen.plugin.PcgCombatant;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.Skill;

import plugin.initiative.OpposedSkillBasicModel;
import plugin.initiative.OpposedSkillModel;
import plugin.initiative.OpposedSkillTypeModel;

/**
 * <p>
 * This dialog class presents three lists of the {@code PcgCombatant}s:
 * a list of available combatants, a list of combatants rolling a skill check,
 * and a list of combatants opposing the skill check.  A droplist of skills
 * is presented for both lists.  Because pcgen doesn't contain any information
 * about which skills oppose which, no choices are automatically selected --
 * the DM must select both skill names.
 * </p>
 * <p>
 * When a skill is selected, or when the "roll" button is pressed, skill checks are
 * rolled for both groups.  Combatants without the skill roll if the skill can be used
 * untrained, but their roll remains blank if the skill cannot be used untrained.
 * No particular other processing is performed -- that is, the results are not interpreted
 * or compared in any way, the DM must judge the results by eye.
 * </p>
 * <p>
 * The rolling and opposing tables each have a "fudge" column where the user can enter
 * a fudge value to affect the outcome of the roll.
 * </p>
 * <p>
 * Combatants can be dragged from one table to another.  The user must select the
 * rows in the source table, release the mouse button, then click-and-drag the rows
 * to a new table.
 * </p>
 * <ul>
 *    <li>TODO: Add comparison/re-sorting support to models?</li>
 *    <li>TODO: Add logging support to the initiative tracker log</li>
 * </ul>
 */
class OpposedCheckDialog extends JDialog
{

	/** The shared {@code TransferHandler} for all tables */
	private final TransferHandler transferHandler =
			new CombatantTransferHandler();
	/** Label for the available table */
	private JLabel availableLabel = null; //
	/** Scroll pane for the available table */
	private JScrollPane availableScrollPane = null; //
	/** JTable that holds the available combatants */
	private JTable availableTable = null; //
	/** Data model for the available table */
	private OpposedSkillTypeModel ivjAvailableModel = null;
	/** Data model for the opposed table */
	private OpposedSkillModel ivjOpposedSkillModel = null;
	/** Data model for the rolling table */
	private OpposedSkillModel ivjRollingSkillModel = null;
	/** The main panel; functions as a content pane for the dialog. */
	private JPanel jContentPane = null;
	/** Ok button */
	private JButton okButton = null; //
	/** Combo box lising skills for the opposing group */
	private JComboBox<Object> opposingComboBox = null; //
	/** Label for the opposing group */
	private JLabel opposingGroupLabel = null; //
	/** Scroll pane for the opposing group table */
	private JScrollPane opposingGroupScrollPane = null; //
	/** Table for the opposing group of combatants */
	private JTable opposingGroupTable = null; //
	/** Button for rolling skill checks */
	private JButton rollButton = null; //
	/** Combo box listing skills for the rolling group */
	private JComboBox<Object> rollingComboBox = null; //
	/** Label for the rolling group */
	private JLabel rollingGroupLabel = null; //
	/** Scroll pane for rollingGroupTable */
	private JScrollPane rollingGroupScrollPane = null; //
	/** Table that holds the main (rolling) group of combatants */
	private JTable rollingGroupTable = null; //
	/** Sorted list of skill names */
	private final TreeSet<String> skillNames = new TreeSet<>();

	/**
	 * <p>Constructor</p>
	 * @param owner
	 * @param rollingGroup   A list comprising the main (rolling) group of combatants
	 * @param availableGroup A list comprising the other combatants
	 * @throws HeadlessException if running without a gui
	 */
	OpposedCheckDialog(Frame owner, List<InitHolder> rollingGroup,
	                   List<InitHolder> availableGroup) throws HeadlessException
	{
		super(owner);
		initializeLists(rollingGroup, availableGroup);
		initialize();
	}

	/**
	 *
	 * <p>
	 * Iniitializes the list of skills, and the main data models for the tables,
	 * based on the rolling and available groups.
	 * </p>
	 *
	 * @param rollingGroup   A list comprising the main (rolling) group of combatants
	 * @param availableGroup A list comprising the other combatants
	 */
	private void initializeLists(List<InitHolder> rollingGroup, List<InitHolder> availableGroup)
	{
		skillNames.addAll(Globals.getContext()
				                  .getReferenceContext()
				                  .getConstructedCDOMObjects(Skill.class)
				                  .stream()
				                  .map(PObject::toString)
				                  .collect(Collectors.toList()));
		ivjAvailableModel = new OpposedSkillTypeModel(availableGroup);
		ivjRollingSkillModel = new OpposedSkillModel(rollingGroup);
		ivjOpposedSkillModel = new OpposedSkillModel();
	}

	/**
	 *
	 * This method initializes availableLabel
	 *
	 * @return javax.swing.JLabel
	 */
	private Component getAvailableLabel()
	{
		if (availableLabel == null)
		{
			availableLabel = new JLabel();
			availableLabel.setText("Available Combatants");
		}
		return availableLabel;
	}

	/**
	 *
	 * This method initializes availableScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private Component getAvailableScrollPane()
	{
		if (availableScrollPane == null)
		{
			availableScrollPane = new JScrollPane();
			availableScrollPane.setViewportView(getAvailableTable());
			availableScrollPane.setPreferredSize(new Dimension(300,
				100));
		}
		return availableScrollPane;
	}

	/**
	 *
	 * This method initializes availableTable
	 *
	 * @return javax.swing.JTable
	 */
	private Component getAvailableTable()
	{
		if (availableTable == null)
		{
			availableTable = new JTable();
			availableTable.setModel(getIvjAvailableModel());
			availableTable.setDragEnabled(true);
			availableTable.setTransferHandler(transferHandler);
			availableTable.setName("availableTable");
		}
		return availableTable;
	}

	/**
	 *
	 * This method initializes ivjAvailableModel
	 *
	 * @return OpposedSkillAvailableModel
	 */
	private TableModel getIvjAvailableModel()
	{
		if (ivjAvailableModel == null)
		{
			ivjAvailableModel = new OpposedSkillTypeModel();
		}
		return ivjAvailableModel;
	}

	/**
	 *
	 * This method initializes ivjOpposedSkillModel
	 *
	 * @return OpposedSkillModel
	 */
	private TableModel getIvjOpposedSkillModel()
	{
		if (ivjOpposedSkillModel == null)
		{
			ivjOpposedSkillModel = new OpposedSkillModel();
		}
		return ivjOpposedSkillModel;
	}

	/**
	 *
	 * This method initializes ivjRollingSkillModel
	 *
	 * @return OpposedSkillModel
	 */
	private TableModel getIvjRollingSkillModel()
	{
		if (ivjRollingSkillModel == null)
		{
			ivjRollingSkillModel = new OpposedSkillModel();
		}
		return ivjRollingSkillModel;
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private Container getJContentPane()
	{
		if (jContentPane == null)
		{

			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			GridBagConstraints consAvailableTable = new GridBagConstraints();
			consAvailableTable.gridx = 0;
			consAvailableTable.gridy = 1;
			consAvailableTable.weightx = 1.0;
			consAvailableTable.weighty = 1.0;
			consAvailableTable.fill = GridBagConstraints.BOTH;
			consAvailableTable.gridwidth = 2;
			GridBagConstraints consRollingGroupPane = new GridBagConstraints();
			consRollingGroupPane.weightx = 1.0;
			consRollingGroupPane.weighty = 1.0;
			consRollingGroupPane.fill = GridBagConstraints.BOTH;
			consRollingGroupPane.gridx = 0;
			consRollingGroupPane.gridy = 4;
			GridBagConstraints consOpposingComboBox = new GridBagConstraints();
			consOpposingComboBox.weightx = 1.0;
			consOpposingComboBox.fill = GridBagConstraints.HORIZONTAL;
			consOpposingComboBox.gridx = 1;
			consOpposingComboBox.gridy = 3;
			GridBagConstraints consOpposingGroupPane = new GridBagConstraints();
			consOpposingGroupPane.weightx = 1.0;
			consOpposingGroupPane.weighty = 1.0;
			consOpposingGroupPane.fill = GridBagConstraints.BOTH;
			consOpposingGroupPane.gridx = 1;
			consOpposingGroupPane.gridy = 4;
			GridBagConstraints consRollingComboBox = new GridBagConstraints();
			consRollingComboBox.weightx = 1.0;
			consRollingComboBox.fill = GridBagConstraints.HORIZONTAL;
			consRollingComboBox.gridx = 0;
			consRollingComboBox.gridy = 3;
			GridBagConstraints consRollButton = new GridBagConstraints();
			consRollButton.gridx = 0;
			consRollButton.gridy = 5;
			GridBagConstraints consOkButton = new GridBagConstraints();
			consOkButton.gridx = 1;
			consOkButton.gridy = 5;
			jContentPane.setBorder(BorderFactory.createEmptyBorder(
				5, 5, 5, 5));
			GridBagConstraints consRollingGroupLabel = new GridBagConstraints();
			consRollingGroupLabel.gridx = 0;
			consRollingGroupLabel.gridy = 2;
			GridBagConstraints consOpposingGroupLabel = new GridBagConstraints();
			consOpposingGroupLabel.gridx = 1;
			consOpposingGroupLabel.gridy = 2;
			GridBagConstraints consAvailableLabel = new GridBagConstraints();
			consAvailableLabel.gridx = 0;
			consAvailableLabel.gridy = 0;
			consAvailableLabel.gridwidth = 2;
			jContentPane.add(getAvailableScrollPane(), consAvailableTable);
			jContentPane.add(getRollingGroupScrollPane(), consRollingGroupPane);
			jContentPane.add(getOpposingComboBox(), consOpposingComboBox);
			jContentPane.add(getOpposingGroupScrollPane(),
				consOpposingGroupPane);
			jContentPane.add(getRollingComboBox(), consRollingComboBox);
			jContentPane.add(getRollButton(), consRollButton);
			jContentPane.add(getOkButton(), consOkButton);
			jContentPane.add(getRollingGroupLabel(), consRollingGroupLabel);
			jContentPane.add(getOpposingGroupLabel(), consOpposingGroupLabel);
			jContentPane.add(getAvailableLabel(), consAvailableLabel);
		}
		return jContentPane;
	}

	/**
	 *
	 * This method initializes okButton
	 *
	 * @return javax.swing.JButton
	 */
	private Component getOkButton()
	{
		if (okButton == null)
		{
			okButton = new JButton();
			okButton.setText("Ok");
			okButton.addActionListener(this::okButtonActionPerformed);

		}
		return okButton;
	}

	/**
	 *
	 * This method initializes opposingComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private Component getOpposingComboBox()
	{
		if (opposingComboBox == null)
		{
			opposingComboBox = new JComboBox<>(skillNames.toArray());
			opposingComboBox.setSelectedIndex(-1);
			opposingComboBox
				.addActionListener(this::opposingComboBoxActionPerformed);

		}
		return opposingComboBox;
	}

	/**
	 *
	 * This method initializes opposingGroupLabel
	 *
	 * @return javax.swing.JLabel
	 */
	private Component getOpposingGroupLabel()
	{
		if (opposingGroupLabel == null)
		{
			opposingGroupLabel = new JLabel();
			opposingGroupLabel.setText("Opposing Group");
		}
		return opposingGroupLabel;
	}

	/**
	 *
	 * This method initializes opposingGroupScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private Component getOpposingGroupScrollPane()
	{
		if (opposingGroupScrollPane == null)
		{
			opposingGroupScrollPane = new JScrollPane();
			opposingGroupScrollPane.setViewportView(getOpposingGroupTable());
			opposingGroupScrollPane.setPreferredSize(new Dimension(
				300, 100));
		}
		return opposingGroupScrollPane;
	}

	/**
	 *
	 * This method initializes opposingGroupTable
	 *
	 * @return javax.swing.JTable
	 */
	private Component getOpposingGroupTable()
	{
		if (opposingGroupTable == null)
		{
			opposingGroupTable = new JTable();
			opposingGroupTable.setModel(getIvjOpposedSkillModel());
			opposingGroupTable.setDragEnabled(true);
			opposingGroupTable.setTransferHandler(transferHandler);
			opposingGroupTable.setName("opposingGroupTable");
		}
		return opposingGroupTable;
	}

	/**
	 *
	 * This method initializes rollButton
	 *
	 * @return javax.swing.JButton
	 */
	private Component getRollButton()
	{
		if (rollButton == null)
		{
			rollButton = new JButton();
			rollButton.setText("Roll");
			rollButton.addActionListener(this::rollButtonActionPerformed);

		}
		return rollButton;
	}

	/**
	 *
	 * This method initializes rollingComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private Component getRollingComboBox()
	{
		if (rollingComboBox == null)
		{
			rollingComboBox = new JComboBox(skillNames.toArray());
			rollingComboBox.setSelectedIndex(-1);
			rollingComboBox
				.addActionListener(this::rollingComboBoxActionPerformed);

		}
		return rollingComboBox;
	}

	/**
	 *
	 * This method initializes rollingGroupLabel
	 *
	 * @return javax.swing.JLabel
	 */
	private Component getRollingGroupLabel()
	{
		if (rollingGroupLabel == null)
		{
			rollingGroupLabel = new JLabel();
			rollingGroupLabel.setText("Rolling Group");
		}
		return rollingGroupLabel;
	}

	/**
	 *
	 * This method initializes rollingGroupScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private Component getRollingGroupScrollPane()
	{
		if (rollingGroupScrollPane == null)
		{
			rollingGroupScrollPane = new JScrollPane();
			rollingGroupScrollPane.setViewportView(getRollingGroupTable());
			rollingGroupScrollPane.setPreferredSize(new Dimension(300, 100));
		}
		return rollingGroupScrollPane;
	}

	/**
	 *
	 * This method initializes rollingGroupTable
	 *
	 * @return javax.swing.JTable
	 */
	private Component getRollingGroupTable()
	{
		if (rollingGroupTable == null)
		{
			rollingGroupTable = new JTable();
			rollingGroupTable.setModel(getIvjRollingSkillModel());
			rollingGroupTable.setDragEnabled(true);
			rollingGroupTable.setTransferHandler(transferHandler);
			rollingGroupTable.setName("rollingGroupTable");
		}
		return rollingGroupTable;
	}

	/**
	 * This method initializes this dialog, packs it, and sets its screen location.
	 */
	private void initialize()
	{
		setContentPane(getJContentPane());
		pack();
		setLocationRelativeTo(GMGenSystem.inst);
	}

	/**
	 * <p>
	 * Hides the dialog when the OK button is pressed.
	 * </p>
	 *
	 * @param e unused
	 */
	private void okButtonActionPerformed(ActionEvent e)
	{
		setVisible(false);
	}

	/**
	 * <p>
	 * Sets the skill for ivjOpposedSkillModel when a skill is selected.
	 * </p>
	 *
	 * @param e unused
	 */
	private void opposingComboBoxActionPerformed(ActionEvent e)
	{
		ivjOpposedSkillModel.setSkill(opposingComboBox.getSelectedItem()
			.toString());
	}

	/**
	 * <p>
	 * Rolls the skill checks in both skill tables.
	 * </p>
	 *
	 * @param e unused
	 */
	private void rollButtonActionPerformed(ActionEvent e)
	{
		ivjOpposedSkillModel.rollAll();
		ivjRollingSkillModel.rollAll();
	}

	/**
	 * <p>
	 * Sets the skill for ivjRollingSkillModel.
	 * </p>
	 *
	 * @param e unused
	 */
	private void rollingComboBoxActionPerformed(ActionEvent e)
	{
		ivjRollingSkillModel.setSkill(rollingComboBox.getSelectedItem()
			.toString());
	}

	/**
	 * <p>
	 * A transfer handler to manage drag-and-drop between the tables.
	 * It interprets all drags as moves and won't allow drops on the initiating
	 * table.  It is designed to be shared by all the tables.
	 * </p>
	 */
	private static final class CombatantTransferHandler extends TransferHandler
	{

		/** A data flavor for use in the transfer */
		private DataFlavor combatantFlavor = null;

		/** The mime type used by the data flavor.  Not really accurate, since
		 * the transferrable class really returns a List.
		 */
		private final String mimeType =
				DataFlavor.javaJVMLocalObjectMimeType
					+ ";class=gmgen.plugin.PcgCombatant";
		/** The source data model for the transfer. */
		private OpposedSkillBasicModel sourceModel = null;
		/** The source table for the transfer. */
		private JTable sourceTable = null;

		/**
		 * <p>
		 * Default constructor -- initializes the data flavor.
		 * </p>
		 */
		private CombatantTransferHandler()
		{
			try
			{
				combatantFlavor = new DataFlavor(mimeType);
			}
			catch (final ClassNotFoundException e)
			{
				//Intentionally left blank
			}
		}

        @Override
		public boolean canImport(JComponent c, DataFlavor[] flavors)
		{
			if ((sourceTable == null) || (c == null)
					|| sourceTable.getName()
					.equals(c.getName()))
			{
				return false;
			}
			for (final DataFlavor flavor : flavors)
			{
				if (combatantFlavor.equals(flavor))
				{
					return true;
				}
			}
			return false;
		}

        @Override
		protected Transferable createTransferable(JComponent c)
		{
			if (c instanceof JTable)
			{
				sourceModel = (OpposedSkillBasicModel) ((JTable) c).getModel();
				sourceTable = (JTable) c;
				return new CombatantTransferable((JTable) c);
			}
			return null;
		}

        @Override
		protected void exportDone(JComponent c, Transferable data, int action)
		{
			if (action == TransferHandler.MOVE)
			{
				try
				{
					Iterable<PcgCombatant> items = (Iterable<PcgCombatant>) data.getTransferData(combatantFlavor);
					items.forEach(item -> sourceModel.removeCombatant(item.getName()));
				}
				catch (final UnsupportedFlavorException | IOException e)
				{
					e.printStackTrace();
				}
			}
			sourceModel = null;
		}

        @Override
		public int getSourceActions(JComponent c)
		{
			return TransferHandler.MOVE;
		}

        @Override
		public boolean importData(JComponent c, Transferable t)
		{
			if (canImport(c, t.getTransferDataFlavors()))
			{
				JTable table = (JTable) c;
				OpposedSkillBasicModel model =
						(OpposedSkillBasicModel) table.getModel();
				try
				{
					Iterable<PcgCombatant> items = (Iterable<PcgCombatant>) t.getTransferData(combatantFlavor);
					items.forEach(model::addCombatant);
					return true;
				}
				catch (final UnsupportedFlavorException | IOException ufe)
				{
					//Nothing
				}
			}
			return false;
		}

		/**
		 * <p>
		 * A transferrable class that saves a list of combatants being transferred.
		 * </p>
		 */
		private final class CombatantTransferable implements Transferable
		{

			/**
			 * A list of combatants that are being transferred.
			 */
			private List<PcgCombatant> items = null;

			/**
			 * <p>
			 * Constructor.  The JTable us used to get the selected rows and store
			 * them in the {@code items} member.
			 * </p>
			 *
			 * @param table The source JTable
			 */
			private CombatantTransferable(JTable table)
			{
				int[] rows = table.getSelectedRows();

				if ((rows != null) && (rows.length > 0))
				{
					OpposedSkillBasicModel model =
							(OpposedSkillBasicModel) table.getModel();
					items = new ArrayList<>();
					Arrays.stream(rows).forEach(model::getCombatant);
				}

			}

            @Override
			public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException
			{
				if (!isDataFlavorSupported(flavor))
				{
					throw new UnsupportedFlavorException(flavor);
				}
				return items;
			}

            @Override
			public DataFlavor[] getTransferDataFlavors()
			{
				return new DataFlavor[]{combatantFlavor};
			}

            @Override
			public boolean isDataFlavorSupported(DataFlavor flavor)
			{
				return combatantFlavor.equals(flavor);
			}
		}
	}
}
