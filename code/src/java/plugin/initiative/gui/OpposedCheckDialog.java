/*
 * sandbox Copyright (C) 2004 Ross M. Lodge
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
 *
 * Created on May 4, 2004, 1:32:24 PM
 *
 */

package plugin.initiative.gui;

import gmgen.GMGenSystem;
import gmgen.plugin.PcgCombatant;
import pcgen.core.Globals;
import pcgen.core.Skill;
import plugin.initiative.OpposedSkillBasicModel;
import plugin.initiative.OpposedSkillModel;
import plugin.initiative.OpposedSkillTypeModel;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.ArrayList;

/**
 * <p>
 * This dialog class presents three lists of the <code>PcgCombatant</code>s:
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
 *
 * <p>
 * Current Ver: $Revision$
 * </p>
 * <p>
 * Last Editor: $Author$
 * </p>
 * <p>
 * Last Edited: $Date$
 * </p>
 *
 * @author LodgeR
 */
public class OpposedCheckDialog extends JDialog
{

	/**
	 * <p>
	 * A transfer handler to manage drag-and-drop between the tables.
	 * It interprets all drags as moves and won't allow drops on the initiating
	 * table.  It is designed to be shared by all the tables.
	 * </p>
	 */
	private static class CombatantTransferHandler extends TransferHandler
	{

		/**
		 * <p>
		 * A transferrable class that saves a list of combatants being transferred.
		 * </p>
		 */
		class CombatantTransferable implements Transferable
		{

			/**
			 * A list of combatants that are being transferred.
			 */
			private List<PcgCombatant> items = null;

			/**
			 * <p>
			 * Constructor.  The JTable us used to get the selected rows and store
			 * them in the <code>items</code> member.
			 * </p>
			 *
			 * @param table The source JTable
			 */
			public CombatantTransferable(JTable table)
			{
				int[] rows = table.getSelectedRows();

				if (rows != null && rows.length > 0)
				{
					OpposedSkillBasicModel model =
							(OpposedSkillBasicModel) table.getModel();
					items = new ArrayList<PcgCombatant>();
					for (int i = 0; i < rows.length; i++)
					{
						items.add(model.getCombatant(rows[i]));
					}
				}

			}

			/* (non-Javadoc)
			 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
			 */
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

			/* (non-Javadoc)
			 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
			 */
            @Override
			public DataFlavor[] getTransferDataFlavors()
			{
				return new DataFlavor[]{combatantFlavor};
			}

			/* (non-Javadoc)
			 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
			 */
            @Override
			public boolean isDataFlavorSupported(DataFlavor flavor)
			{
				return combatantFlavor.equals(flavor);
			}
		}

		/** A data flavor for use in the transfer */
		private DataFlavor combatantFlavor = null;

		/** The mime type used by the data flavor.  Not really accurate, since
		 * the transferrable class really returns a List.
		 */
		private String mimeType =
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
		public CombatantTransferHandler()
		{
			try
			{
				combatantFlavor = new DataFlavor(mimeType);
			}
			catch (ClassNotFoundException e)
			{
				//Intentionally left blank
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
		 */
        @Override
		public boolean canImport(JComponent c, DataFlavor[] flavors)
		{
			if (sourceTable == null || c == null
				|| sourceTable.getName().equals(c.getName()))
			{
				return false;
			}
			for (int i = 0; i < flavors.length; i++)
			{
				if (combatantFlavor.equals(flavors[i]))
				{
					return true;
				}
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
		 */
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

		/* (non-Javadoc)
		 * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
		 */
        @Override
		protected void exportDone(JComponent c, Transferable data, int action)
		{
			if (action == MOVE)
			{
				try
				{
					List items = (List) data.getTransferData(combatantFlavor);
					for (Iterator i = items.iterator(); i.hasNext();)
					{
						sourceModel.removeCombatant(((PcgCombatant) i.next())
							.getName());
					}
				}
				catch (UnsupportedFlavorException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			sourceModel = null;
		}

		/* (non-Javadoc)
		 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
		 */
        @Override
		public int getSourceActions(JComponent c)
		{
			return MOVE;
		}

		/* (non-Javadoc)
		 * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
		 */
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
					List items = (List) t.getTransferData(combatantFlavor);
					for (Iterator i = items.iterator(); i.hasNext();)
					{
						model.addCombatant((PcgCombatant) i.next());
					}
					return true;
				}
				catch (UnsupportedFlavorException ufe)
				{
					//Nothing
				}
				catch (IOException ioe)
				{
					//Nothing
				}
			}
			return false;
		}
	}

	/** The shared <code>TransferHandler</code> for all tables */
	private CombatantTransferHandler transferHandler =
			new CombatantTransferHandler();
	/** Label for the available table */
	private javax.swing.JLabel availableLabel = null; //
	/** Scroll pane for the available table */
	private javax.swing.JScrollPane availableScrollPane = null; //
	/** JTable that holds the available combatants */
	private javax.swing.JTable availableTable = null; //
	/** Data model for the available table */
	private OpposedSkillTypeModel ivjAvailableModel = null;
	/** Data model for the opposed table */
	private OpposedSkillModel ivjOpposedSkillModel = null;
	/** Data model for the rolling table */
	private OpposedSkillModel ivjRollingSkillModel = null;
	/** The main panel; functions as a content pane for the dialog. */
	private javax.swing.JPanel jContentPane = null;
	/** Ok button */
	private javax.swing.JButton okButton = null; //
	/** Combo box lising skills for the opposing group */
	private javax.swing.JComboBox opposingComboBox = null; //
	/** Label for the opposing group */
	private javax.swing.JLabel opposingGroupLabel = null; //
	/** Scroll pane for the opposing group table */
	private javax.swing.JScrollPane opposingGroupScrollPane = null; //
	/** Table for the opposing group of combatants */
	private javax.swing.JTable opposingGroupTable = null; //
	/** Button for rolling skill checks */
	private javax.swing.JButton rollButton = null; //
	/** Combo box listing skills for the rolling group */
	private javax.swing.JComboBox rollingComboBox = null; //
	/** Label for the rolling group */
	private javax.swing.JLabel rollingGroupLabel = null; //
	/** Scroll pane for rollingGroupTable */
	private javax.swing.JScrollPane rollingGroupScrollPane = null; //
	/** Table that holds the main (rolling) group of combatants */
	private javax.swing.JTable rollingGroupTable = null; //
	/** Sorted list of skill names */
	private TreeSet<String> skillNames = new TreeSet<String>();

	/**
	 * This is the default constructor
	 */
	public OpposedCheckDialog()
	{
		super();
		skillNames.add("Hide");
		skillNames.add("Move Silently");
		skillNames.add("Listen");
		skillNames.add("Spot");
		initialize();
	}

	/**
	 * <p>Constructor</p>
	 * @param owner
	 * @param rollingGroup
	 * @param availableGroup
	 * @throws java.awt.HeadlessException
	 */
	public OpposedCheckDialog(Dialog owner, List rollingGroup,
		List availableGroup) throws HeadlessException
	{
		super(owner);
		initializeLists(rollingGroup, availableGroup);
		initialize();
	}

	/**
	 * <p>Constructor</p>
	 * @param owner
	 * @param modal
	 * @param rollingGroup
	 * @param availableGroup
	 * @throws java.awt.HeadlessException
	 */
	public OpposedCheckDialog(Dialog owner, boolean modal, List rollingGroup,
		List availableGroup) throws HeadlessException
	{
		super(owner, modal);
		initializeLists(rollingGroup, availableGroup);
		initialize();
	}

	/**
	 * <p>Constructor</p>
	 * @param owner
	 * @param title
	 * @param rollingGroup
	 * @param availableGroup
	 * @throws java.awt.HeadlessException
	 */
	public OpposedCheckDialog(Dialog owner, String title, List rollingGroup,
		List availableGroup) throws HeadlessException
	{
		super(owner, title);
		initializeLists(rollingGroup, availableGroup);
		initialize();
	}

	/**
	 * <p>Constructor</p>
	 * @param owner
	 * @param title
	 * @param modal
	 * @param rollingGroup   A list comprising the main (rolling) group of combatants
	 * @param availableGroup A list comprising the other combatants
	 * @throws java.awt.HeadlessException
	 */
	public OpposedCheckDialog(Dialog owner, String title, boolean modal,
		List rollingGroup, List availableGroup) throws HeadlessException
	{
		super(owner, title, modal);
		initializeLists(rollingGroup, availableGroup);
		initialize();
	}

	/**
	 * <p>Constructor</p>
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 * @param rollingGroup   A list comprising the main (rolling) group of combatants
	 * @param availableGroup A list comprising the other combatants
	 * @throws java.awt.HeadlessException
	 */
	public OpposedCheckDialog(Dialog owner, String title, boolean modal,
		GraphicsConfiguration gc, List rollingGroup, List availableGroup)
		throws HeadlessException
	{
		super(owner, title, modal, gc);
		initializeLists(rollingGroup, availableGroup);
		initialize();
	}

	/**
	 * <p>Constructor</p>
	 * @param owner
	 * @param rollingGroup   A list comprising the main (rolling) group of combatants
	 * @param availableGroup A list comprising the other combatants
	 * @throws java.awt.HeadlessException
	 */
	public OpposedCheckDialog(Frame owner, List rollingGroup,
		List availableGroup) throws HeadlessException
	{
		super(owner);
		initializeLists(rollingGroup, availableGroup);
		initialize();
	}

	/**
	 * <p>Constructor</p>
	 * @param owner
	 * @param modal
	 * @param rollingGroup   A list comprising the main (rolling) group of combatants
	 * @param availableGroup A list comprising the other combatants
	 * @throws java.awt.HeadlessException
	 */
	public OpposedCheckDialog(Frame owner, boolean modal, List rollingGroup,
		List availableGroup) throws HeadlessException
	{
		super(owner, modal);
		initializeLists(rollingGroup, availableGroup);
		initialize();
	}

	/**
	 * <p>Constructor</p>
	 * @param owner
	 * @param title
	 * @param rollingGroup   A list comprising the main (rolling) group of combatants
	 * @param availableGroup A list comprising the other combatants
	 * @throws java.awt.HeadlessException
	 */
	public OpposedCheckDialog(Frame owner, String title, List rollingGroup,
		List availableGroup) throws HeadlessException
	{
		super(owner, title);
		initializeLists(rollingGroup, availableGroup);
		initialize();
	}

	/**
	 * <p>Constructor</p>
	 * @param owner
	 * @param title
	 * @param modal
	 * @param rollingGroup   A list comprising the main (rolling) group of combatants
	 * @param availableGroup A list comprising the other combatants
	 * @throws java.awt.HeadlessException
	 */
	public OpposedCheckDialog(Frame owner, String title, boolean modal,
		List rollingGroup, List availableGroup) throws HeadlessException
	{
		super(owner, title, modal);
		initializeLists(rollingGroup, availableGroup);
		initialize();
	}

	/**
	 * <p>Constructor</p>
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 * @param rollingGroup   A list comprising the main (rolling) group of combatants
	 * @param availableGroup A list comprising the other combatants
	 */
	public OpposedCheckDialog(Frame owner, String title, boolean modal,
		GraphicsConfiguration gc, List rollingGroup, List availableGroup)
	{
		super(owner, title, modal, gc);
		initializeLists(rollingGroup, availableGroup);
		initialize();
	}

	/**
	 * <p>Constructor</p>
	 * @param rollingGroup   A list comprising the main (rolling) group of combatants
	 * @param availableGroup A list comprising the other combatants
	 */
	public OpposedCheckDialog(List rollingGroup, List availableGroup)
	{
		super();
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
	private void initializeLists(List rollingGroup, List availableGroup)
	{
		for (Skill skill : Globals.getContext().ref.getConstructedCDOMObjects(Skill.class))
		{
			skillNames.add(skill.toString());
		}
		ivjAvailableModel = new OpposedSkillTypeModel(availableGroup);
		ivjRollingSkillModel = new OpposedSkillModel(rollingGroup);
		ivjOpposedSkillModel = new OpposedSkillModel();
	}

	/**
	 *
	 * This method initializes availableLabel
	 *
	 * @return javax.swing.JLabel
	 *
	 */
	private javax.swing.JLabel getAvailableLabel()
	{
		if (availableLabel == null)
		{
			availableLabel = new javax.swing.JLabel();
			availableLabel.setText("Available Combatants");
		}
		return availableLabel;
	}

	/**
	 *
	 * This method initializes availableScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 *
	 */
	private javax.swing.JScrollPane getAvailableScrollPane()
	{
		if (availableScrollPane == null)
		{
			availableScrollPane = new javax.swing.JScrollPane();
			availableScrollPane.setViewportView(getAvailableTable());
			availableScrollPane.setPreferredSize(new java.awt.Dimension(300,
				100));
		}
		return availableScrollPane;
	}

	/**
	 *
	 * This method initializes availableTable
	 *
	 * @return javax.swing.JTable
	 *
	 */
	private javax.swing.JTable getAvailableTable()
	{
		if (availableTable == null)
		{
			availableTable = new javax.swing.JTable();
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
	 *
	 */
	private OpposedSkillBasicModel getIvjAvailableModel()
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
	 *
	 */
	private OpposedSkillModel getIvjOpposedSkillModel()
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
	 *
	 */
	private OpposedSkillModel getIvjRollingSkillModel()
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
	private javax.swing.JPanel getJContentPane()
	{
		if (jContentPane == null)
		{
			java.awt.GridBagConstraints consAvailableLabel =
					new java.awt.GridBagConstraints();

			java.awt.GridBagConstraints consOpposingGroupLabel =
					new java.awt.GridBagConstraints();

			java.awt.GridBagConstraints consRollingGroupLabel =
					new java.awt.GridBagConstraints();

			java.awt.GridBagConstraints consOkButton =
					new java.awt.GridBagConstraints();

			java.awt.GridBagConstraints consRollButton =
					new java.awt.GridBagConstraints();

			java.awt.GridBagConstraints consRollingComboBox =
					new java.awt.GridBagConstraints();

			java.awt.GridBagConstraints consOpposingGroupPane =
					new java.awt.GridBagConstraints();

			java.awt.GridBagConstraints consOpposingComboBox =
					new java.awt.GridBagConstraints();

			java.awt.GridBagConstraints consRollingGroupPane =
					new java.awt.GridBagConstraints();

			java.awt.GridBagConstraints consAvailableTable =
					new java.awt.GridBagConstraints();

			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.GridBagLayout());
			consAvailableTable.gridx = 0;
			consAvailableTable.gridy = 1;
			consAvailableTable.weightx = 1.0;
			consAvailableTable.weighty = 1.0;
			consAvailableTable.fill = java.awt.GridBagConstraints.BOTH;
			consAvailableTable.gridwidth = 2;
			consRollingGroupPane.weightx = 1.0;
			consRollingGroupPane.weighty = 1.0;
			consRollingGroupPane.fill = java.awt.GridBagConstraints.BOTH;
			consRollingGroupPane.gridx = 0;
			consRollingGroupPane.gridy = 4;
			consOpposingComboBox.weightx = 1.0;
			consOpposingComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consOpposingComboBox.gridx = 1;
			consOpposingComboBox.gridy = 3;
			consOpposingGroupPane.weightx = 1.0;
			consOpposingGroupPane.weighty = 1.0;
			consOpposingGroupPane.fill = java.awt.GridBagConstraints.BOTH;
			consOpposingGroupPane.gridx = 1;
			consOpposingGroupPane.gridy = 4;
			consRollingComboBox.weightx = 1.0;
			consRollingComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consRollingComboBox.gridx = 0;
			consRollingComboBox.gridy = 3;
			consRollButton.gridx = 0;
			consRollButton.gridy = 5;
			consOkButton.gridx = 1;
			consOkButton.gridy = 5;
			jContentPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(
				5, 5, 5, 5));
			consRollingGroupLabel.gridx = 0;
			consRollingGroupLabel.gridy = 2;
			consOpposingGroupLabel.gridx = 1;
			consOpposingGroupLabel.gridy = 2;
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
	 *
	 */
	private javax.swing.JButton getOkButton()
	{
		if (okButton == null)
		{
			okButton = new javax.swing.JButton();
			okButton.setText("Ok");
			okButton.addActionListener(new java.awt.event.ActionListener()
			{

                @Override
				public void actionPerformed(java.awt.event.ActionEvent e)
				{

					okButtonActionPerformed(e);

				}
			});

		}
		return okButton;
	}

	/**
	 *
	 * This method initializes opposingComboBox
	 *
	 * @return javax.swing.JComboBox
	 *
	 */
	private javax.swing.JComboBox getOpposingComboBox()
	{
		if (opposingComboBox == null)
		{
			opposingComboBox = new javax.swing.JComboBox(skillNames.toArray());
			opposingComboBox.setSelectedIndex(-1);
			opposingComboBox
				.addActionListener(new java.awt.event.ActionListener()
				{

                @Override
					public void actionPerformed(java.awt.event.ActionEvent e)
					{

						opposingComboBoxActionPerformed(e);

					}
				});

		}
		return opposingComboBox;
	}

	/**
	 *
	 * This method initializes opposingGroupLabel
	 *
	 * @return javax.swing.JLabel
	 *
	 */
	private javax.swing.JLabel getOpposingGroupLabel()
	{
		if (opposingGroupLabel == null)
		{
			opposingGroupLabel = new javax.swing.JLabel();
			opposingGroupLabel.setText("Opposing Group");
		}
		return opposingGroupLabel;
	}

	/**
	 *
	 * This method initializes opposingGroupScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 *
	 */
	private javax.swing.JScrollPane getOpposingGroupScrollPane()
	{
		if (opposingGroupScrollPane == null)
		{
			opposingGroupScrollPane = new javax.swing.JScrollPane();
			opposingGroupScrollPane.setViewportView(getOpposingGroupTable());
			opposingGroupScrollPane.setPreferredSize(new java.awt.Dimension(
				300, 100));
		}
		return opposingGroupScrollPane;
	}

	/**
	 *
	 * This method initializes opposingGroupTable
	 *
	 * @return javax.swing.JTable
	 *
	 */
	private javax.swing.JTable getOpposingGroupTable()
	{
		if (opposingGroupTable == null)
		{
			opposingGroupTable = new javax.swing.JTable();
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
	 *
	 */
	private javax.swing.JButton getRollButton()
	{
		if (rollButton == null)
		{
			rollButton = new javax.swing.JButton();
			rollButton.setText("Roll");
			rollButton.addActionListener(new java.awt.event.ActionListener()
			{

                @Override
				public void actionPerformed(java.awt.event.ActionEvent e)
				{

					rollButtonActionPerformed(e);

				}
			});

		}
		return rollButton;
	}

	/**
	 *
	 * This method initializes rollingComboBox
	 *
	 * @return javax.swing.JComboBox
	 *
	 */
	private javax.swing.JComboBox getRollingComboBox()
	{
		if (rollingComboBox == null)
		{
			rollingComboBox = new javax.swing.JComboBox(skillNames.toArray());
			rollingComboBox.setSelectedIndex(-1);
			rollingComboBox
				.addActionListener(new java.awt.event.ActionListener()
				{

                @Override
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
						rollingComboBoxActionPerformed(e);
					}
				});

		}
		return rollingComboBox;
	}

	/**
	 *
	 * This method initializes rollingGroupLabel
	 *
	 * @return javax.swing.JLabel
	 *
	 */
	private javax.swing.JLabel getRollingGroupLabel()
	{
		if (rollingGroupLabel == null)
		{
			rollingGroupLabel = new javax.swing.JLabel();
			rollingGroupLabel.setText("Rolling Group");
		}
		return rollingGroupLabel;
	}

	/**
	 *
	 * This method initializes rollingGroupScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 *
	 */
	private javax.swing.JScrollPane getRollingGroupScrollPane()
	{
		if (rollingGroupScrollPane == null)
		{
			rollingGroupScrollPane = new javax.swing.JScrollPane();
			rollingGroupScrollPane.setViewportView(getRollingGroupTable());
			rollingGroupScrollPane.setPreferredSize(new java.awt.Dimension(300,
				100));
		}
		return rollingGroupScrollPane;
	}

	/**
	 *
	 * This method initializes rollingGroupTable
	 *
	 * @return javax.swing.JTable
	 *
	 */
	private javax.swing.JTable getRollingGroupTable()
	{
		if (rollingGroupTable == null)
		{
			rollingGroupTable = new javax.swing.JTable();
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
		this.setContentPane(getJContentPane());
		pack();
		setLocationRelativeTo(GMGenSystem.inst);
	}

	/**
	 * <p>
	 * Hides the dialog when the OK button is pressed.
	 * </p>
	 *
	 * @param e
	 */
	protected void okButtonActionPerformed(ActionEvent e)
	{
		setVisible(false);
	}

	/**
	 * <p>
	 * Sets the skill for ivjOpposedSkillModel when a skill is selected.
	 * </p>
	 *
	 * @param e
	 */
	protected void opposingComboBoxActionPerformed(ActionEvent e)
	{
		ivjOpposedSkillModel.setSkill(opposingComboBox.getSelectedItem()
			.toString());
	}

	/**
	 * <p>
	 * Rolls the skill checks in both skill tables.
	 * </p>
	 *
	 * @param e
	 */
	protected void rollButtonActionPerformed(ActionEvent e)
	{
		ivjOpposedSkillModel.rollAll();
		ivjRollingSkillModel.rollAll();
	}

	/**
	 * <p>
	 * Sets the skill for ivjRollingSkillModel.
	 * </p>
	 *
	 * @param e
	 */
	protected void rollingComboBoxActionPerformed(ActionEvent e)
	{
		ivjRollingSkillModel.setSkill(rollingComboBox.getSelectedItem()
			.toString());
	}
}
