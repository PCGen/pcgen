/*
 * EqBuilder.java
 * @(#) $Id$
 *
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on November 23, 2001, 7:04 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.core.SpecialProperty;
import pcgen.core.analysis.EqModSpellInfo;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.spell.Spell;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JTableEx;
import pcgen.gui.utils.TableSorter;
import pcgen.gui.utils.Utility;
import pcgen.system.LanguageBundle;
import pcgen.util.Delta;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;

/**
 * Item customizer main panel. Allows a user to customize an item and
 * then either save or purchase the item.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
final class EqBuilder extends JPanel
{
	/** EQTYPE_NONE = -1 */
	public static final int EQTYPE_NONE = -1;
	/** EQTYPE_POTION = -1 */
	public static final int EQTYPE_POTION = 0;
	/** EQTYPE_SCROLL = -1 */
	public static final int EQTYPE_SCROLL = 1;
	/** EQTYPE_WAND = -1 */
	public static final int EQTYPE_WAND = 2;
	/** EQTYPE_RING = -1 */
	public static final int EQTYPE_RING = 3;
	static final long serialVersionUID = -369105812700996734L;
	private static TableSorter sorter;
	/** The types of equipment that are valid for creation based on a spell. */
	static Type[] validEqTypes = { Type.POTION, Type.SCROLL, Type.WAND, Type.RING };
	private DefaultListModel listModel1;
	private DefaultListModel listModel2;
	private Equipment aNewEq = null;
	private Equipment baseEquipment = null;
	private EquipmentModModel dataModel;
	private EQFrame parentFrame = null;
	private FlippingSplitPane jSplitPane2;
	private FlippingSplitPane jSplitPane3;
	private JButton jButtonAdd1;
	private JButton jButtonAdd2;
	private JButton jButtonCancel;
	private JButton jButtonCost;
	private JButton jButtonName;
	private JButton jButtonOk;
	private JButton jButtonPurchase;
	private JButton jButtonRemove1;
	private JButton jButtonRemove2;
	private JButton jButtonSProp;
	private JButton jButtonWeight;
	private JButton jButtonDamage;
	private JComboBoxEx jComboBoxSize;
	private JLabel jLabelSize;
	private JList jListSelected1;
	private JList jListSelected2;
	private JPanel jPanel1;
	private JPanel jPanel2;
	private JPanel jPanel20;
	private JPanel jPanel21;
	private JPanel jPanel22;
	private JPanel jPanel23;
	private JPanel jPanel3;
	private JPanel jPanel5;
	private JPanel jPanelAvailables;
	private JPanel jPanelButtons1;
	private JPanel jPanelButtons2;
	private JPanel jPanelModifiers;
	private JPanel jPanelOkCancel;
	private JPanel jPanelSelected1;
	private JPanel jPanelSelected2;
	private JPanel jPanelSelections;
	private JScrollPane jScrollPane2;
	private JScrollPane jScroll_ListAvailable;
	private JScrollPane jScroll_ListSelected1;
	private JScrollPane jScroll_ListSelected2;
	private JTableEx jListAvailable;
	private JTextPane jItemDesc;
	private String customName = "";
	private List[] newTypeList = { null, null };
	private int eqType = EQTYPE_NONE;
	private int iListCount = 0;
	private PlayerCharacter aPC;

	/**
	 * Creates new form EqBuilder
	 * @param apc
	 */
	EqBuilder(PlayerCharacter apc)
	{
		this.aPC = apc;
		initComponents();
	}

	/**
	 * @param aFrame
	 * *********************************************************
	 */

	public void setParentWindow(EQFrame aFrame)
	{
		parentFrame = aFrame;
		//Globals.setCurrentFrame(parentFrame);
	}

	/**
	 * Set the equipment
	 * @param aEq
	 * @return TRUE if OK
	 */
	public boolean setEquipment(Equipment aEq)
	{
		if (aEq.isWeapon() && aEq.isDouble()) {
			dataModel = new EquipmentModModelDouble();
		}
		else {
			dataModel = new EquipmentModModelSingle();
		}
		sorter.setModel(dataModel);
		return setEquipment(aEq, false);
	}

	/**
	 * toFront
	 */
	public void toFront()
	{
		switch (eqType)
		{
			case EQTYPE_POTION:
			case EQTYPE_WAND:
			case EQTYPE_SCROLL:

				for (int idx = 0; idx < dataModel.getDisplayModifiers().size(); idx++)
				{
					final EquipmentModifier eqMod = dataModel.getDisplayModifiers().get(idx);

					if (eqMod.getKeyName().startsWith("SE_") && (eqMod.getSafeSizeOfListFor(ListKey.TYPE) == 1))
					{
						idx = sorter.translateRow(idx);
						jListAvailable.setRowSelectionInterval(idx, idx);
						jButtonAdd1ActionPerformed();

						break;
					}
				}

				break;

			default:
				break;
		}
	}

	private static void setGuiTextInfo(Object obj, String in_String)
	{
		Utility.setGuiTextInfo(obj, "in_EqBuilder_" + in_String);
	}

	private boolean setEquipment(Equipment aEq, boolean bReloading)
	{
		listModel1.clear();
		listModel2.clear();

		final String sBaseKey = aEq.getBaseItemName();

		if (!bReloading)
		{
			customName = "";

			//
			// If item has a base item name that differs
			// from its item name, then get it for the base
			//
			if (!sBaseKey.equals(aEq.getName()))
			{
				baseEquipment = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
						Equipment.class, sBaseKey);
			}
			else
			{
				baseEquipment = aEq;
			}
		}

		// Translate string into numerical type
		//
		eqType = EQTYPE_NONE;

		for (int idx = 0; idx < validEqTypes.length; ++idx)
		{
			if (aEq.isType(validEqTypes[idx].toString()))
			{
				eqType = idx;

				break;
			}
		}

		// If there are no modifiers attached, make sure
		// the item has no types assigned by any modifiers
		//
		if ((aEq.getEqModifierList(true).size() == 0) && (aEq.getEqModifierList(false).size() == 0))
		{
			for (EquipmentModifier eqMod : Globals.getContext().ref.getConstructedCDOMObjects(EquipmentModifier.class))
			{
				if (!eqMod.getDisplayName().startsWith("EXCLUDEEQ"))
				{
					continue;
				}

				for (String type : eqMod.getSafeListFor(ListKey.ITEM_TYPES))
				{
					if (aEq.isEitherType(type.toUpperCase()))
					{
						errorDialog("This item already has type: " +
										type +
										". Select the base item and modify it instead.");
						return false;
					}
				}
			}
		}

		// Bail out if couldn't find base item
		//
		if (baseEquipment == null)
		{
			Logging.errorPrint("No base equipment found: " + aEq.getName() + ", aborting EqBuilder:" + sBaseKey);

			return false;
		}

		if (bReloading)
		{
			if (aEq.isWeapon() && aEq.isDouble())
			{
				iListCount = 2;
			}
			else
			{
				iListCount = 1;
			}
		}
		else
		{
			if (baseEquipment.isWeapon() && baseEquipment.isDouble())
			{
				iListCount = 2;
			}
			else
			{
				iListCount = 1;
			}
		}

		jListSelected1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jListSelected1.setModel(listModel1);

		jPanelButtons1.setVisible(true);

		// Setup modifier filter based on the item's properties
		if (bReloading)
		{
			dataModel.setFilter(aEq, iListCount);
		}
		else
		{
			dataModel.setFilter(baseEquipment, iListCount);
		}

		// Only show size combo if there is a resizing method
		boolean showSize = Globals.canResizeHaveEffect(aPC, aEq, aEq.typeList());
		jLabelSize.setVisible(showSize);
		jComboBoxSize.setVisible(showSize);

		jButtonDamage.setVisible(aEq.isWeapon());

		jComboBoxSize.setSelectedItem(aEq.getSafe(ObjectKey.SIZE));

		if (!bReloading)
		{
			// Start with a clean copy
			aNewEq = aEq.clone();
		}

		// If not a two-headed weapon, then hide the controls
		// pertaining to the second head
		//
		jPanelSelected2.setVisible(iListCount > 1);

		loadScreenInfo();

		updateDisplay(true); // update primary list
		updateDisplay(false); // update secondary list

		return true;
	}

	private SizeAdjustment getItemSize()
	{
		final int idx = jComboBoxSize.getSelectedIndex();

		if (idx >= 0)
		{
			return (SizeAdjustment) jComboBoxSize.getItemAt(idx);
		}

		return null;
	}

	/**
	 * Process the user clicking on one of the add buttons. Two buttons may
	 * be present if the item has two sets of enhancements, eg double
	 * headed weapons. If only one is shown, then it is the primary.
	 *
	 * @param bPrimary Was the button pressed the one associated with the
	 * primary selected enhancement list.
	 */
	private void addButton(boolean bPrimary)
	{
		jButtonAdd1.setEnabled(false);
		jButtonAdd2.setEnabled(false);

		//Globals.setCurrentFrame(parentFrame);

		//
		// Trash the cost modifications
		//
		aNewEq.setCostMod("0");

		final ListSelectionModel lsm = jListAvailable.getSelectionModel();
		int iSelected = lsm.getMinSelectionIndex();

		if (iSelected >= 0)
		{
			iSelected = sorter.getRowTranslated(iSelected);

			if (iSelected >= 0)
			{
				//
				// Add to equipment object
				//
				final EquipmentModifier eqMod = dataModel.getDisplayModifiers().get(iSelected);

				if (eqMod.getSafe(StringKey.CHOICE_STRING).startsWith("EQBUILDER.SPELL"))
				{
					jButtonSpellActionPerformed(eqMod, eqMod.getSafe(StringKey.CHOICE_STRING).substring(15));

					return;
				}

				aNewEq.addEqModifier(eqMod, bPrimary, aPC);
				setEquipment(aNewEq, true);
				updateDisplay(bPrimary);

				if (aNewEq.isDouble() && eqMod.getSafe(ObjectKey.ASSIGN_TO_ALL))
				{
					aNewEq.addEqModifier(eqMod, !bPrimary, aPC);
					updateDisplay(!bPrimary);
				}

				//getRootPane().getParent().requestFocus();
				((EQFrame)getRootPane().getParent()).setVisible(true);
				((EQFrame)getRootPane().getParent()).toFront();
				Globals.getCurrentFrame().requestFocus();
			}
		}
	}

	private void doCleanUp()
	{
		aNewEq = null;
		newTypeList[0] = null;
		newTypeList[1] = null;
		saveScreenInfo();
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		listModel1 = new DefaultListModel();
		listModel2 = new DefaultListModel();
		jPanel1 = new JPanel();
		jPanelOkCancel = new JPanel();
		jPanel3 = new JPanel();
		JPanel attribButtonPanel = new JPanel();
		jLabelSize = new JLabel( /*"Size"*/);
		jButtonName = new JButton( /*"Name"*/);
		jButtonSProp = new JButton( /*"SProp"*/);
		jButtonCost = new JButton( /*"Cost"*/);
		jButtonWeight = new JButton( /*"Weight"*/);
		jButtonDamage = new JButton( /*"Damage"*/);
		jComboBoxSize = new JComboBoxEx();
		JPanel actionButtonPanel = new JPanel();
		jButtonCancel = new JButton( /*"Cancel"*/);
		jButtonOk = new JButton( /*"Ok"*/);
		jButtonPurchase = new JButton( /*"Purchase"*/);
		jSplitPane2 = new FlippingSplitPane();
		jPanel2 = new JPanel();
		jScrollPane2 = new JScrollPane();
		jItemDesc = new JTextPane();
		jPanelModifiers = new JPanel();
		jSplitPane3 = new FlippingSplitPane();
		jPanelAvailables = new JPanel();
		jScroll_ListAvailable = new JScrollPane();
		JPanel sizePanel = new JPanel();

		setGuiTextInfo(jLabelSize, "Size");

		sorter = new TableSorter();
		jListAvailable = new JTableEx(sorter);
		jListAvailable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		sorter.addMouseListenerToHeaderInTable(jListAvailable);
		jPanel5 = new JPanel();
		jPanelSelections = new JPanel();
		jPanelSelected1 = new JPanel();
		jPanel20 = new JPanel();
		jPanelButtons1 = new JPanel();
		jButtonAdd1 = new JButton( /*"Add"*/
			);
		jButtonRemove1 = new JButton( /*"Remove"*/
			);
		jPanel21 = new JPanel();
		jScroll_ListSelected1 = new JScrollPane();
		jListSelected1 = new JList(listModel1);
		jPanelSelected2 = new JPanel();
		jPanel22 = new JPanel();
		jPanelButtons2 = new JPanel();
		jButtonAdd2 = new JButton( /*"Add"*/
			);
		jButtonRemove2 = new JButton( /*"Remove"*/
			);
		jPanel23 = new JPanel();
		jScroll_ListSelected2 = new JScrollPane();
		jListSelected2 = new JList(listModel2);

		setLayout(new BorderLayout());

		setPreferredSize(new Dimension(640, 480));
		jPanel1.setLayout(new BorderLayout());

		jPanel1.setPreferredSize(new Dimension(640, 480));
		jPanelOkCancel.setLayout(new BoxLayout(jPanelOkCancel, BoxLayout.X_AXIS));

		jPanel3.setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints1;

		GridBagLayout gridbag = new GridBagLayout();
		attribButtonPanel.setLayout(gridbag);

		setGuiTextInfo(jButtonName, "Name");
		jButtonName.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					jButtonNameActionPerformed();
				}
			});
		jButtonName.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jButtonNameActionPerformed();
				}
			});
		attribButtonPanel.add(jButtonName);

		setGuiTextInfo(jButtonSProp, "SProp");
		jButtonSProp.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					jButtonSPropActionPerformed();
				}
			});
		jButtonSProp.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jButtonSPropActionPerformed();
				}
			});
		attribButtonPanel.add(jButtonSProp);

		setGuiTextInfo(jButtonCost, "Cost");
		jButtonCost.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					jButtonCostActionPerformed();
				}
			});
		jButtonCost.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jButtonCostActionPerformed();
				}
			});
		attribButtonPanel.add(jButtonCost);

		setGuiTextInfo(jButtonWeight, "Weight");
		jButtonWeight.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					jButtonWeightActionPerformed();
				}
			});
		jButtonWeight.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jButtonWeightActionPerformed();
				}
			});
		attribButtonPanel.add(jButtonWeight);

		setGuiTextInfo(jButtonDamage, "Damage");
		jButtonDamage.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					jButtonDamageActionPerformed();
				}
			});
		jButtonDamage.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jButtonDamageActionPerformed();
				}
			});
		attribButtonPanel.add(jButtonDamage);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		jPanel3.add(attribButtonPanel, gridBagConstraints1);

		jPanelOkCancel.add(jPanel3);

		actionButtonPanel.setLayout(new BoxLayout(actionButtonPanel, BoxLayout.X_AXIS));

		setGuiTextInfo(jButtonOk, "Ok"); //$NON-NLS-1$
		jButtonOk.setPreferredSize(new Dimension(81, 27));
		jButtonOk.setMaximumSize(new Dimension(81, 27));
		jButtonOk.setMinimumSize(new Dimension(81, 27));
		jButtonOk.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					jButtonOkActionPerformed(false);
				}
			});
		jButtonOk.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jButtonOkActionPerformed(false);
				}
			});

		actionButtonPanel.add(jButtonOk);

		setGuiTextInfo(jButtonPurchase, "Purchase"); //$NON-NLS-1$
		jButtonPurchase.setPreferredSize(new Dimension(81, 27));
		jButtonPurchase.setMaximumSize(new Dimension(81, 27));
		jButtonPurchase.setMinimumSize(new Dimension(81, 27));
		jButtonPurchase.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					jButtonOkActionPerformed(true);
				}
			});
		jButtonPurchase.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jButtonOkActionPerformed(true);
				}
			});

		actionButtonPanel.add(jButtonPurchase);

		jPanelOkCancel.add(actionButtonPanel);

		setGuiTextInfo(jButtonCancel, "Cancel"); //$NON-NLS-1$
		jButtonCancel.setPreferredSize(new Dimension(81, 27));
		jButtonCancel.setMaximumSize(new Dimension(81, 27));
		jButtonCancel.setMinimumSize(new Dimension(81, 27));
		jButtonCancel.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					jButtonCancelActionPerformed();
				}
			});
		jButtonCancel.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jButtonCancelActionPerformed();
				}
			});

		actionButtonPanel.add(jButtonCancel);

		jPanel1.add(jPanelOkCancel, BorderLayout.SOUTH);

		jSplitPane2.setDividerSize(5);
		jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		jPanel2.setLayout(new BorderLayout());

		jPanel2.setPreferredSize(new Dimension(44, 70));
		jItemDesc.setEditable(false);
		jItemDesc.setBackground(jPanel1.getBackground());
		jScrollPane2.setViewportView(jItemDesc);

		jPanel2.add(jScrollPane2, BorderLayout.CENTER);

		jSplitPane2.setLeftComponent(jPanel2);

		jPanelModifiers.setLayout(new BoxLayout(jPanelModifiers, BoxLayout.X_AXIS));

		jPanelModifiers.setPreferredSize(new Dimension(640, 300));
		jSplitPane3.setDividerSize(5);
		jSplitPane3.setPreferredSize(new Dimension(640, 407));
		jPanelAvailables.setLayout(new BorderLayout());

		jPanelAvailables.setPreferredSize(new Dimension(340, 403));
		jListAvailable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jListAvailable.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jListAvailableMouseClicked(evt);
				}
			});
		jScroll_ListAvailable.setViewportView(jListAvailable);

		jPanelAvailables.add(jScroll_ListAvailable, BorderLayout.CENTER);

		jSplitPane3.setLeftComponent(jPanelAvailables);

		jPanel5.setLayout(new BoxLayout(jPanel5, BoxLayout.Y_AXIS));

		jPanel5.setPreferredSize(new Dimension(200, 400));
		jPanelSelections.setLayout(new BoxLayout(jPanelSelections, BoxLayout.Y_AXIS));

		jPanelSelected1.setLayout(new BoxLayout(jPanelSelected1, BoxLayout.X_AXIS));

		jPanelButtons1.setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints2;

		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.anchor = GridBagConstraints.NORTHWEST;
		JLabel fillerTemplate = new JLabel(" ");
		jPanelButtons1.add(
			new Box.Filler(
				fillerTemplate.getMinimumSize(),
				fillerTemplate.getPreferredSize(),
				fillerTemplate.getMaximumSize()),
			gridBagConstraints2);

		setGuiTextInfo(jButtonAdd1, "Add1");
		jButtonAdd1.setPreferredSize(new Dimension(81, 27));
		jButtonAdd1.setMaximumSize(new Dimension(81, 27));
		jButtonAdd1.setMinimumSize(new Dimension(81, 27));
		jButtonAdd1.setEnabled(false);
		jButtonAdd1.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					jButtonAdd1ActionPerformed();
				}
			});
		jButtonAdd1.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jButtonAdd1ActionPerformed();
				}
			});

		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.anchor = GridBagConstraints.NORTHWEST;
		jPanelButtons1.add(jButtonAdd1, gridBagConstraints2);

		setGuiTextInfo(jButtonRemove1, "Remove1");
		jButtonRemove1.setPreferredSize(new Dimension(81, 27));
		jButtonRemove1.setEnabled(false);
		jButtonRemove1.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					jButtonRemove1ActionPerformed();
				}
			});
		jButtonRemove1.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jButtonRemove1ActionPerformed();
				}
			});

		gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		jPanelButtons1.add(jButtonRemove1, gridBagConstraints2);
		jPanelButtons1.setAlignmentY(Component.CENTER_ALIGNMENT);

		jPanel20.add(jPanelButtons1);

		jPanelSelected1.add(jPanel20);

		sizePanel.add(jLabelSize);
		jComboBoxSize.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent evt)
				{
					jComboBoxSizeActionPerformed();
				}
			});
		sizePanel.add(jComboBoxSize);
		jPanelSelections.add(sizePanel);


		jPanel21.setLayout(new BorderLayout());


		jListSelected1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jListSelected1.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jListSelected1MouseClicked(evt);
				}
			});
		jListSelected1.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				if (jListSelected1.getSelectedIndex() >= 0)
				{
					jListSelected1.ensureIndexIsVisible(jListSelected1
						.getSelectedIndex());
				}
			}
		});
		jScroll_ListSelected1.setViewportView(jListSelected1);

		jPanel21.add(
			new JLabel(
				LanguageBundle.getString("in_EqBuilder_Sel1"),
				SwingConstants.CENTER),
			BorderLayout.NORTH);
		jPanel21.add(jScroll_ListSelected1, BorderLayout.CENTER);

		jPanelSelected1.add(jPanel21);

		jPanelSelections.add(jPanelSelected1);

		jPanelSelected2.setLayout(new BoxLayout(jPanelSelected2, BoxLayout.X_AXIS));

		jPanelButtons2.setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints3;

		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints3.gridy = 0;
		jPanelButtons2.add(
			new Box.Filler(
				fillerTemplate.getMinimumSize(),
				fillerTemplate.getPreferredSize(),
				fillerTemplate.getMaximumSize()),
			gridBagConstraints3);

		setGuiTextInfo(jButtonAdd2, "Add2");
		jButtonAdd2.setPreferredSize(new Dimension(81, 27));
		jButtonAdd2.setMaximumSize(new Dimension(81, 27));
		jButtonAdd2.setMinimumSize(new Dimension(81, 27));
		jButtonAdd2.setEnabled(false);
		jButtonAdd2.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					jButtonAdd2ActionPerformed();
				}
			});
		jButtonAdd2.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jButtonAdd2ActionPerformed();
				}
			});

		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints3.gridy = 1;
		jPanelButtons2.add(jButtonAdd2, gridBagConstraints3);

		setGuiTextInfo(jButtonRemove2, "Remove2");
		jButtonRemove2.setPreferredSize(new Dimension(81, 27));
		jButtonRemove2.setEnabled(false);
		jButtonRemove2.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					jButtonRemove2ActionPerformed();
				}
			});
		jButtonRemove2.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jButtonRemove2ActionPerformed();
				}
			});

		gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridy = 2;
		jPanelButtons2.add(jButtonRemove2, gridBagConstraints3);

		jPanel22.add(jPanelButtons2);

		jPanelSelected2.add(jPanel22);

		jPanel23.setLayout(new BorderLayout());

		jListSelected2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jListSelected2.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					jListSelected2MouseClicked(evt);
				}
			});
		jListSelected2.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				if (jListSelected2.getSelectedIndex() >= 0)
				{
					jListSelected2.ensureIndexIsVisible(jListSelected2
						.getSelectedIndex());
				}
			}
		});
		jScroll_ListSelected2.setViewportView(jListSelected2);

		jPanel23.add(
			new JLabel(
				LanguageBundle.getString("in_EqBuilder_Sel2"),
				SwingConstants.CENTER),
			BorderLayout.NORTH);
		jPanel23.add(jScroll_ListSelected2, BorderLayout.CENTER);

		jPanelSelected2.add(jPanel23);

		jPanelSelections.add(jPanelSelected2);

		jPanel5.add(jPanelSelections);

		jSplitPane3.setRightComponent(jPanel5);

		jPanelModifiers.add(jSplitPane3);

		jSplitPane2.setRightComponent(jPanelModifiers);

		jPanel1.add(jSplitPane2, BorderLayout.CENTER);

		add(jPanel1, BorderLayout.CENTER);

		//
		// Set up the size combo's contents
		//
		jComboBoxSize.setModel(new DefaultComboBoxModel(populateSizeModel()));

		jSplitPane2.setDividerLocation(SettingsHandler.getCustomizerSplit1());
		jSplitPane3.setDividerLocation(SettingsHandler.getCustomizerSplit2());

		//
		// Want to resize the width of the last column to fill the empty space (if any) created when
		// this is resized
		jScroll_ListAvailable.addComponentListener(new ComponentAdapter()
			{
				public void componentResized(ComponentEvent e)
				{
					final Dimension dimMax = jScroll_ListAvailable.getSize();
					Dimension dimCur = jListAvailable.getSize();

					if (dimCur.getWidth() < dimMax.getWidth())
					{
						jListAvailable.setSize((int) dimMax.getWidth(), (int) dimCur.getHeight());

						final int[] cols = { jListAvailable.getColumnCount() - 1 };
						jListAvailable.setOptimalColumnWidths(cols);
					}
				}
			});
	}

	private static Object[] populateSizeModel()
	{
		return Globals.getContext().ref.getOrderSortedCDOMObjects(SizeAdjustment.class).toArray();
	}

	private void jButtonAdd1ActionPerformed()
	{
		addButton(true);
	}

	private void jButtonAdd2ActionPerformed()
	{
		addButton(false);
	}

	private void jButtonCancelActionPerformed()
	{
		doCleanUp();
		((EQFrame) getRootPane().getParent()).exitItem_actionPerformed(true, null, false);
	}

	private void jButtonCostActionPerformed()
	{
		InputInterface ii = InputFactory.getInputInstance();
		Object selectedValue = ii.showInputDialog(null,
									"Enter Item's New Cost",
									Constants.APPLICATION_NAME,
									MessageType.INFORMATION,
									null,
									aNewEq.getCost(aPC).toString());

		if (selectedValue != null)
		{
			String aString = ((String) selectedValue).trim();

			try
			{
				BigDecimal newCost = new BigDecimal(aString);

				if (newCost.doubleValue() < 0)
				{
					errorDialog("Cost cannot be negative!");
					return;
				}

				aNewEq.setCostMod("0");
				aNewEq.setCostMod(newCost.subtract(aNewEq.getCost(aPC)));
				showItemInfo(aPC);
			}
			catch (Exception e)
			{
				errorDialog("Invalid number!");
			}
		}
	}

	private void jButtonNameActionPerformed()
	{
		String defaultName = customName;
		if (defaultName.length() == 0)
		{
			defaultName = aNewEq.getItemNameFromModifiers();
		}
		InputInterface ii = InputFactory.getInputInstance();
		Object selectedValue = ii.showInputDialog(
			null,
			"Enter the new name", //$NON-NLS-1$
			Constants.APPLICATION_NAME,
			MessageType.INFORMATION,
			null,
			defaultName);

		if (selectedValue != null)
		{
			String aString = ((String) selectedValue).trim();

			if ((aString.indexOf('|') >= 0)
				|| (aString.indexOf(':') >= 0)
				|| (aString.indexOf(';') >= 0))
			{
				errorDialog(
					"Invalid character in string! You cannot use '|', ':' or ';' in this entry");
			}
			else
			{
				customName = aString;

				StringBuffer oldName =
					new StringBuffer("(").append(aNewEq.getItemNameFromModifiers()).append(")");

				//
				// Replace illegal characters in old name
				//
				for (int i = 0; i < oldName.length(); i++)
				{
					switch (oldName.charAt(i))
					{
						case ';':
						case ':':
						case '|':
							oldName.setCharAt(i, '@');

							break;

						default:
							break;
					}
				}

				if (!oldName.toString().toUpperCase().startsWith(Constants.GENERIC_ITEM))
				{
					aNewEq.addToListFor(
						ListKey.SPECIAL_PROPERTIES,
						SpecialProperty.createFromLst(oldName.toString()));
				}

				aNewEq.resizeItem(aPC, getItemSize());
				showItemInfo(aPC);
			}
		}
	}

	private void jButtonOkActionPerformed(boolean bPurchase)
	{
		String sName = aNewEq.getKeyName();

		if (customName.length() != 0)
		{
			sName = customName;
		}

		if (aNewEq.isWeapon() && !aNewEq.isMelee() && !aNewEq.isRanged())
		{
			informationDialog("Weapons must either be Melee or Ranged");
			return;
		}

		if (sName.toUpperCase().startsWith(Constants.GENERIC_ITEM))
		{
			informationDialog("You must rename this item!");
			return;
		}

		if (Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				Equipment.class, sName) != null)
		{
			informationDialog("There is already an existing item: " + sName);
			return;
		}

		aNewEq.setName(sName);
		aNewEq.put(StringKey.OUTPUT_NAME, sName);
		aNewEq.removeType(Type.AUTO_GEN);
		aNewEq.removeType(Type.STANDARD);
		if (!aNewEq.isType(Constants.TYPE_CUSTOM)) {
			aNewEq.addType(Type.CUSTOM);
		}


		if (aNewEq.getSafe(ObjectKey.MOD_CONTROL).getModifiersRequired() &&
				(aNewEq.getEqModifierList(true).size() == 0) &&
				(aNewEq.getEqModifierList(false).size() == 0))
			{
				errorDialog("You must add at least 1 modifier to this item first.");
				return;
			}

		//
		// Need to change this so that we can customize it again
		//
		if (aNewEq.getBaseItemName().toUpperCase().startsWith(Constants.GENERIC_ITEM))
		{
			aNewEq.remove(ObjectKey.BASE_ITEM);
		}

		Globals.getContext().ref.importObject(aNewEq);
		//PCGen_Frame1.getInst().eqList_Changed(aNewEq, bPurchase);

		((EQFrame) getRootPane().getParent()).exitItem_actionPerformed(false, aNewEq, bPurchase);
		doCleanUp();
	}

	private void jButtonRemove1ActionPerformed()
	{
		jButtonRemove1.setEnabled(false);
		removeButton(listModel1, jListSelected1.getSelectedIndex(), true);
	}

	private void jButtonRemove2ActionPerformed()
	{
		jButtonRemove2.setEnabled(false);
		removeButton(listModel2, jListSelected2.getSelectedIndex(), false);
	}

	private void jButtonSPropActionPerformed()
	{
		InputInterface ii = InputFactory.getInputInstance();
		Object selectedValue = ii.showInputDialog(null,
									"Enter Special Property",
									Constants.APPLICATION_NAME,
									MessageType.INFORMATION,
									null,
									aNewEq.getRawSpecialProperties());

		if (selectedValue == null)
		{
			selectedValue = new String("");
		}

		String aString = ((String) selectedValue).trim();

		if ((aString.indexOf('|') >= 0) || (aString.indexOf(':') >= 0) || (aString.indexOf(';') >= 0))
		{
			errorDialog("Invalid character in string! You cannot use '|', ':' or ';' in this entry");
		}
		else
		{
			aNewEq.removeListFor(ListKey.SPECIAL_PROPERTIES);
			if (!aString.equals(""))
			{
				aNewEq.addToListFor(ListKey.SPECIAL_PROPERTIES, SpecialProperty.createFromLst(aString));
			}

			aNewEq.resizeItem(aPC, getItemSize());
			showItemInfo(aPC);
		}
	}

	private void jButtonSpellActionPerformed(EquipmentModifier eqMod, String extraInfo)
	{
		List<String> classList = null;
		List<String> levelList = null;
		boolean metaAllowed = true;
		int spellBooks = 0;

		if (extraInfo.length() != 0)
		{
			//
			// CLASS=Wizard|CLASS=Sorcerer|Metamagic=0|LEVEL=1|LEVEL=2|SPELLBOOKS=Y
			final StringTokenizer aTok = new StringTokenizer(extraInfo, "|", false);

			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();

				if (aString.startsWith("CLASS="))
				{
					if (classList == null)
					{
						classList = new ArrayList<String>();
					}

					classList.add(aString.substring(6));
				}
				else if (aString.startsWith("LEVEL="))
				{
					if (levelList == null)
					{
						levelList = new ArrayList<String>();
					}

					levelList.add(aString.substring(6));
				}
				else if (aString.startsWith("SPELLBOOKS="))
				{
					switch (aString.charAt(11))
					{
						case 'Y':
							spellBooks = 1;

							break;

						case 'N':
							spellBooks = -1;

							break;

						default:
							spellBooks = 0;

							break;
					}
				}
				else if (aString.equals("METAMAGIC=N"))
				{
					metaAllowed = false;
				}
			}
		}

		ChooseSpellDialog csd = new ChooseSpellDialog(
									(JFrame) Utility.getParentNamed(getParent(), EQFrame.class.getName()),
									aPC,
									eqType,
									metaAllowed, classList, levelList, spellBooks, eqMod.getSafe(StringKey.CHOICE_STRING));
		csd.setVisible(true);

		if (!csd.getWasCancelled())
		{
			Object castingClass = csd.getCastingClass();
			Spell theSpell = csd.getSpell();
			String variant = csd.getVariant();
			String spellType = csd.getSpellType();
			int baseSpellLevel = csd.getBaseSpellLevel();
			int casterLevel = csd.getCasterLevel();
			Object[] metamagicFeats = csd.getMetamagicFeats();

			int charges = -1;

			Integer min = eqMod.get(IntegerKey.MIN_CHARGES);
			if (min != null && min > 0)
			{
				Integer max = eqMod.get(IntegerKey.MAX_CHARGES);
				for (;;)
				{
					InputInterface ii = InputFactory.getInputInstance();

					String toPrint    = "Enter Number of Charges (" +
										Integer.toString(min) + "-" +
										Integer.toString(max) + ")";

					Object selectedValue = ii.showInputDialog(null,
											toPrint,
											Constants.APPLICATION_NAME,
											MessageType.INFORMATION,
											null,
											Integer.toString(max));

					if (selectedValue != null)
					{
						try
						{
							final String aString = ((String) selectedValue).trim();
							charges = Integer.parseInt(aString);

							if (charges < min)
							{
								continue;
							}

							if (charges > max)
							{
								continue;
							}

							break;
						}
						catch (Exception exc)
						{
							//TODO: Should we really ignore this?
						}
					}
				}
			}

			EquipmentModifier existingEqMod = aNewEq.getEqModifierKeyed(eqMod.getKeyName(), true);

			if (existingEqMod == null)
			{
				aNewEq.addEqModifier(eqMod, true, aPC);
			}
			existingEqMod = aNewEq.getEqModifierKeyed(eqMod.getKeyName(), true);
			
			EqModSpellInfo.setSpellInfo(aNewEq, existingEqMod,
					(PObject) castingClass, theSpell, variant, spellType,
					baseSpellLevel, casterLevel, metamagicFeats, charges);

			updateDisplay(true);
		}
	}

	private void jButtonWeightActionPerformed()
	{
		InputInterface ii = InputFactory.getInputInstance();
		Object selectedValue = ii.showInputDialog(null,
									"Enter Item's New Weight",
									Constants.APPLICATION_NAME,
									MessageType.INFORMATION,
									null,
									aNewEq.getWeight(aPC).toString());

		if (selectedValue != null)
		{
			String aString = ((String) selectedValue).trim();

			try
			{
				BigDecimal newWeight =
						new BigDecimal(Globals.getGameModeUnitSet().convertWeightFromUnitSet(
												(new Double(aString)).doubleValue()));

				if (newWeight.doubleValue() < 0)
				{
					errorDialog("Weight cannot be negative!");
					return;
				}

				aNewEq.put(ObjectKey.WEIGHT_MOD, BigDecimal.ZERO);
				aNewEq.put(ObjectKey.WEIGHT_MOD,
						newWeight.subtract(new BigDecimal(aNewEq
								.getWeightAsDouble(aPC))));
				showItemInfo(aPC);
			}
			catch (Exception e)
			{
				errorDialog("Invalid number!");
			}
		}
	}

	/**
	 * Change the Damage of this weapon
	 **/
	private void jButtonDamageActionPerformed()
	{
		InputInterface ii = InputFactory.getInputInstance();
		Object selectedValue = ii.showInputDialog(null,
									"Select the new damage for this weapon",
									Constants.APPLICATION_NAME,
									MessageType.INFORMATION,
									null,
									aNewEq.getDamage(aPC));

		if (selectedValue != null)
		{
			String aString = ((String) selectedValue).trim();
			aNewEq.put(StringKey.DAMAGE_OVERRIDE, aString);
			showItemInfo(aPC);
		}
	}

	private void jComboBoxSizeActionPerformed()
	{
		if (jComboBoxSize.getSelectedIndex() >= 0)
		{
			if (aNewEq != null)
			{
				aNewEq.resizeItem(aPC, getItemSize());
				showItemInfo(aPC);
			}
		}
	}

	/**
	 * User has clicked on the "available" list. Enable/disable
	 * buttons if qualified/not qualified.
	 * If double click then send to AddButton if only 1 enabled.
	 *
	 * @param evt
	 */
	private void jListAvailableMouseClicked(MouseEvent evt)
	{
		final ListSelectionModel lsm = jListAvailable.getSelectionModel();
		final int iSelected = lsm.getMinSelectionIndex();

		if (iSelected >= 0)
		{
			jButtonAdd1.setEnabled(sorter.getValueAt(iSelected, 0).equals("Y"));
			jButtonAdd2.setEnabled(sorter.getValueAt(iSelected, 1).equals("Y"));

			if (evt.getClickCount() == 2)
			{
				if (jButtonAdd1.isEnabled() && !jButtonAdd2.isEnabled())
				{
					jButtonAdd1ActionPerformed();
				}
				else if (!jButtonAdd1.isEnabled() && jButtonAdd2.isEnabled())
				{
					jButtonAdd2ActionPerformed();
				}
			}
		}
	}

	/**
	 * User has clicked/double clicked on the "selected 1" list
	 * Enable the "Remove1" button on 1st click
	 * If double click send to RemoveButton1
	 *
	 * @param evt
	 */
	private void jListSelected1MouseClicked(MouseEvent evt)
	{
		if (jListSelected1.getSelectedIndex() != -1)
		{
			jButtonRemove1.setEnabled(true);

			if (evt.getClickCount() == 2)
			{
				jButtonRemove1ActionPerformed();
			}
		}
	}

	/**
	 * User has clicked/double clicked on the "selected 2" list
	 * Enable the "Remove2" button on 1st click
	 * If double click send to RemoveButton2
	 *
	 * @param evt
	 */
	private void jListSelected2MouseClicked(MouseEvent evt)
	{
		if (jListSelected2.getSelectedIndex() != -1)
		{
			jButtonRemove2.setEnabled(true);

			if (evt.getClickCount() == 2)
			{
				jButtonRemove2ActionPerformed();
			}
		}
	}

	private void loadScreenInfo()
	{
		String tbl = "EqBuilder";
		if (dataModel instanceof EquipmentModModelDouble) {
			tbl = tbl + "A";
		}
		else {
			tbl = tbl + "C";
		}
		int i = jListAvailable.getColumnCount();
		int width = 0;

		for (; i > 0; i--)
		{
			final TableColumn col = jListAvailable.getColumnModel().getColumn(i - 1);
			width = Globals.getCustColumnWidth(tbl, i - 1);

			if (width == 0)
			{
				break;
			}

			col.setPreferredWidth(width);
		}

		if (width == 0)
		{
			//
			// Resize the columns for optimal display
			//
			i = jListAvailable.getColumnCount();

			int[] cols = new int[i];

			for (; i > 0; i--)
			{
				cols[i - 1] = i - 1;
			}

			jListAvailable.setOptimalColumnWidths(cols);
		}
	}

	private boolean needRebuild(boolean bPrimary)
	{
		boolean bRebuild = false;
		final int idx = bPrimary ? 0 : 1;
		List<String> newTypes = null;
		List<String> oldTypes = newTypeList[idx];

		final EquipmentModifier aEqMod = aNewEq.getEqModifierKeyed("ADDTYPE", bPrimary);

		if (aEqMod != null)
		{
			newTypes = aNewEq.getAssociationList(aEqMod);
		}

		if (((oldTypes == null) && (aEqMod != null)) || ((oldTypes != null) && (aEqMod == null)))
		{
			bRebuild = true;
		}
		else if ((oldTypes != null) && (newTypes != null))
		{
			if (oldTypes.size() != newTypes.size())
			{
				bRebuild = true;
			}
			else
			{
				for (int i = 0; i < newTypes.size(); i++)
				{
					if (!oldTypes.contains(newTypes.get(i)))
					{
						bRebuild = true;

						break;
					}
				}
			}
		}

		newTypeList[idx] = newTypes;

		return bRebuild;
	}

	private void removeAddedType(String addedType)
	{
		final EquipmentModifier eqMod = aNewEq.getEqModifierKeyed("ADDTYPE", true);

		if (eqMod != null)
		{
			aNewEq.removeAssociation(eqMod, addedType);
			if (!aNewEq.hasAssociations(eqMod))
			{
				aNewEq.removeEqModifier(eqMod, true, aPC);
			}
		}
	}

	private void removeButton(DefaultListModel lm, int idx, boolean bPrimary)
	{
		removeElement(lm, idx, bPrimary);
	}

	private void removeElement(DefaultListModel lm, int idx, boolean bPrimary)
	{
		if ((idx >= 0) && (idx < lm.size()))
		{
			EquipmentModifier eqMod = (EquipmentModifier) lm.elementAt(idx);

			if (baseEquipment.getEqModifierList(bPrimary).contains(eqMod))
			{
				errorDialog("That modifier is part of the base item. You cannot remove it.");
				return;
			}

			//
			// Trash the cost modifications
			//
			aNewEq.setCostMod("0");

			aNewEq.removeEqModifier(eqMod, bPrimary, aPC);
			setEquipment(aNewEq, true);
			updateDisplay(bPrimary);

			if (aNewEq.isDouble() && eqMod.getSafe(ObjectKey.ASSIGN_TO_ALL))
			{
				aNewEq.removeEqModifier(eqMod, !bPrimary, aPC);
				updateDisplay(!bPrimary);
			}

			// Get focus in case the chooser popped up
			//getRootPane().getParent().requestFocus();
			((EQFrame)getRootPane().getParent()).setVisible(true);
			((EQFrame)getRootPane().getParent()).toFront();
		}
	}

	private void saveScreenInfo()
	{
		String tbl = "EqBuilder";
		if (dataModel instanceof EquipmentModModelDouble) {
			tbl = tbl + "A";
		}
		else {
			tbl = tbl + "C";
		}

		SettingsHandler.setCustomizerSplit1(jSplitPane2.getDividerLocation());
		SettingsHandler.setCustomizerSplit2(jSplitPane3.getDividerLocation());

		int i = jListAvailable.getColumnCount();

		for (; i > 0; i--)
		{
			final TableColumn col = jListAvailable.getColumnModel().getColumn(i - 1);
			Globals.setCustColumnWidth(tbl, i - 1, col.getWidth());
		}
	}

	private void showItemInfo(PlayerCharacter aPlayerCharacter)
	{
		//
		// Show base item name
		//
		StringBuffer aInfo = new StringBuffer(140);
		aInfo.append("Base Item: ").append(baseEquipment.getName()).append(Constants.LINE_SEPARATOR);

		if (customName.length() != 0)
		{
			aInfo.append("Name: ").append(customName).append(Constants.LINE_SEPARATOR);
		}

		String sprop = "";

		// If we've got types added, check to see if they've changed.
		// If they have, we need to rebuild the filtered list
		if (needRebuild(true) || needRebuild(false))
		{
			if (aNewEq.isContainer())
			{
				//TODO: What is this? XXX
			}

			if (aNewEq.isWeapon())
			{
				if (aNewEq.getDamage(aPlayerCharacter).length() == 0)
				{
					NewWeaponInfoDialog nwid = new NewWeaponInfoDialog(
												(JFrame) Utility.getParentNamed(getParent(), EQFrame.class.getName()));
					nwid.setVisible(true);

					if (!nwid.getWasCancelled())
					{
						StringBuffer modString = new StringBuffer(Constants.INTERNAL_EQMOD_WEAPON);
						modString.append("|DAMAGE=").append(nwid.getDamage());
						modString.append("|CRITRANGE=").append(nwid.getCritRange());
						modString.append("|CRITMULT=").append(nwid.getCritMultiplier());
						aNewEq.addEqModifiers(modString.toString(), true);
					}
					else
					{
						removeAddedType("WEAPON");
					}
				}
			}

			final String modString = Constants.INTERNAL_EQMOD_WEAPON + "|RANGE=";

			if (aNewEq.isWeapon() && aNewEq.isRanged())
			{
				while (aNewEq.getRange(aPlayerCharacter).intValue() == 0)
				{
					InputInterface ii = InputFactory.getInputInstance();
					Object selectedValue = ii.showInputDialog(null,
											"Enter the range",
											Constants.APPLICATION_NAME,
											MessageType.INFORMATION,
											null,
											null);

					if (selectedValue != null)
					{
						final String aString = ((String) selectedValue).trim();

						if (Delta.decode(aString).intValue() > 0)
						{
							aNewEq.removeEqModifiers(modString, true, aPC);
							aNewEq.addEqModifiers(modString + aString, true);
						}
					}
					else
					{
						removeAddedType("RANGED");

						break;
					}
				}
			}

			if (!aNewEq.isWeapon() || !aNewEq.isRanged())
			{
				removeAddedType("RANGED");
				aNewEq.removeEqModifiers(modString, true, aPC);
			}

			if (aNewEq.isArmor())
			{
				if (aNewEq.getMaxDex(aPlayerCharacter).intValue() == Constants.MAX_MAXDEX)
				{
					//TODO: What is this?
				}
			}

			//
			// Need to change this so that we can customize it again
			//
			if (aNewEq.getBaseItemName().toUpperCase().startsWith(Constants.GENERIC_ITEM))
			{
				aNewEq.remove(ObjectKey.BASE_ITEM);
			}

			setEquipment(aNewEq, true);

			return;
		}

		try
		{
			if (aNewEq != null)
			{
				final int itemPluses = aNewEq.calcPlusForCosting();

				aInfo.append("New Item: ");
				aInfo.append(aNewEq.nameItemFromModifiers(aPlayerCharacter));
				aInfo.append(Constants.LINE_SEPARATOR);

				aInfo.append("Cost: ");
				aInfo.append(aNewEq.getCost(aPlayerCharacter).toString());

				if (itemPluses != 0)
				{
					aInfo.append(" (plus: ").append(itemPluses).append(')');
				}

				double weight = aNewEq.getWeight(aPlayerCharacter).doubleValue();

				aInfo.append(", Weight: ");
				aInfo.append(Globals.getGameModeUnitSet().displayWeightInUnitSet(weight));
				aInfo.append(Globals.getGameModeUnitSet().getWeightUnit());

				if (aNewEq.isArmor() || aNewEq.isShield())
				{
					aInfo.append(", AC: ").append(aNewEq.getACMod(aPlayerCharacter).toString());
					aInfo.append(", ACCheck: ").append(aNewEq.acCheck(aPlayerCharacter).toString());
					aInfo.append(", Fail: ").append(aNewEq.spellFailure(aPlayerCharacter).toString());
					aInfo.append(", Max Dex: ").append(aNewEq.getMaxDex(aPlayerCharacter).toString());
				}

				if (aNewEq.isWeapon())
				{
					aInfo.append(", Damage: ").append(aNewEq.getDamage(aPlayerCharacter));

					int i = aNewEq.getBonusToDamage(aPlayerCharacter, true);

					if (i != 0)
					{
						aInfo.append(Delta.toString(i));
					}

					i = aNewEq.getBonusToHit(aPlayerCharacter, true);

					if (i != 0)
					{
						aInfo.append(" (").append(Delta.toString(i)).append(" to hit)");
					}

					if (aNewEq.isDouble())
					{
						String altDamage = aNewEq.getAltDamage(aPlayerCharacter);

						if (altDamage.length() != 0)
						{
							aInfo.append('/').append(altDamage);
							i = aNewEq.getBonusToDamage(aPlayerCharacter, false);

							if (i != 0)
							{
								aInfo.append(Delta.toString(i));
							}

							i = aNewEq.getBonusToHit(aPlayerCharacter, false);

							if (i != 0)
							{
								aInfo.append(" (").append(Delta.toString(i)).append(" to hit)");
							}
						}
					}

					final int critRange = 21 - aPlayerCharacter.getCritRange(aNewEq, true);
					aInfo.append(" (").append(String.valueOf(critRange));

					if (critRange < 20)
					{
						aInfo.append("-20");
					}

					aInfo.append(' ').append(aNewEq.getCritMult());

					if (aNewEq.isDouble())
					{
						aInfo.append('/');

						int altCritRange = 21 - aPlayerCharacter.getCritRange(aNewEq, false);

						if (altCritRange != critRange)
						{
							aInfo.append(String.valueOf(altCritRange));

							if (altCritRange < 20)
							{
								aInfo.append("-20");
							}

							aInfo.append(' ');
						}

						aInfo.append(aNewEq.getAltCritMult());
					}

					aInfo.append(')');

					if (aNewEq.isRanged())
					{
						aInfo.append(", Range: ").append(aNewEq.getRange(aPlayerCharacter).toString());
					}
				}

				sprop = aNewEq.getSpecialProperties(aPlayerCharacter);

				if (sprop.length() != 0)
				{
					aInfo.append(Constants.LINE_SEPARATOR).append("SPROP: ").append(sprop);
				}
			}

			jItemDesc.setText(aInfo.toString());
		}
		catch (Exception e)
		{
			errorDialog("ERROR: Exception type:" + e.getClass().getName() +
							Constants.LINE_SEPARATOR + "Message:" + e.getMessage());
			Logging.errorPrint(aInfo.toString(), e);
		}
	}

	private void updateDisplay(boolean bPrimary)
	{
		updateDisplay(bPrimary, true);
	}

	private void updateDisplay(boolean bPrimary, boolean bRedraw)
	{
		//
		// Get list of modifiers and update the listbox
		//
		List<EquipmentModifier> eqModList = aNewEq.getEqModifierList(bPrimary);
		DefaultListModel lm;

		if (bPrimary)
		{
			lm = listModel1;
		}
		else
		{
			lm = listModel2;
		}

		lm.clear();

		for (EquipmentModifier eqMod : eqModList)
		{
			lm.addElement(eqMod);
		}

		if (bRedraw)
		{
			dataModel.fireTableDataChanged();
			showItemInfo(aPC);
		}
		//getRootPane().getParent().requestFocus();
		//((EQFrame)getRootPane().getParent()).setVisible(true);
		//((EQFrame)getRootPane().getParent()).toFront();
	}

	/**
	 * Show an error Dialogue
	 *
	 * @param toPrint The error message to print in the dialogue
	 */

	private void errorDialog(String toPrint)
	{
		ShowMessageDelegate.showMessageDialog(toPrint, Constants.APPLICATION_NAME, MessageType.ERROR, this);
	}

	/**
	 * Show a Dialogue (information)
	 *
	 * @param toPrint The information to print in the dialogue
	 */

	private void informationDialog(String toPrint)
	{
		ShowMessageDelegate.showMessageDialog(toPrint, Constants.APPLICATION_NAME, MessageType.INFORMATION, this);
	}




	/**
	 * This internal class is provides the equipment table
	 * with the data it needs to operate
	 * It has column header names, column widths,
	 * the row count, the and the column count
	 * For the actual data, this class relies on the global
	 * equipment modifier list from <code>Globals</code>.
	 */
	private abstract class EquipmentModModel extends AbstractTableModel
	{
		static final long serialVersionUID = -369105812700996734L;
		private Object[] lastColValue = new Object[6];
		private int lastRow = -1;
		private List<EquipmentModifier > displayModifiers = new ArrayList<EquipmentModifier >();


		/**
		 * Return the current number of rows in the table
		 * based on the value from the global equipment list.
		 *
		 * @return the number of rows
		 */
		public int getRowCount()
		{
			return displayModifiers.size();
		}

		/**
		 * Change the value of a grid cell.
		 * @param row
		 * @param column
		 * @return value
		 */
		public Object getValueAt(int row, int column)
		{
			if ((column < 0) || (column >= getColumnCount()))
			{
				return "Out of Bounds";
			}

			if (row >= getRowCount())
			{
				return null;
			}

			if (row != lastRow)
			{
				// if we are not looking at the same row as last time
				// then create a new array of strings to hold the cached row data
				lastColValue = new String[getColumnCount()];
				lastRow = row;
			}
			else if (lastColValue[column] != null)
			{
				// If we are looking at the same row as last time then if
				// we have a cached value for the column return the cached value.
				return lastColValue[column];
			}

			Object sRet;
			sRet = getEqModTableValueAt(aPC, row, column);

			try
			{
				// Set the cached value of the column
				lastColValue[column] = sRet;
			}
			catch (Exception exc)
			{
				// We do not care if we have a problem caching the value
				// because we can just get it again next time around
			}

			return sRet;
		}

		protected abstract Object getEqModTableValueAt(PlayerCharacter aPlayerCharacter, int row, int column);

		/**
		 * @param e
		 * @return Object
		 */
		protected Object getSourceValue(EquipmentModifier e) {
			Object sRet;
			sRet = SourceFormat.getFormattedString(e,
			Globals.getSourceDisplay(), true);
			return sRet;
		}

		/**
		 * @param e
		 * @return Object
		 */
		protected Object getSaValue(EquipmentModifier e) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (SpecialProperty sp : e.getSafeListFor(ListKey.SPECIAL_PROPERTIES))
			{
				if (!first)
				{
					sb.append(", ");
				}
				first = false;
				sb.append(sp.getDisplayName());
			}
			return sb.toString();
		}

		/**
		 * @param e
		 * @return Object
		 */
		protected Object getCostValue(EquipmentModifier e) {
			Object sRet;
			int iPlus = e.getSafe(IntegerKey.PLUS);
			StringBuffer eCost = new StringBuffer(20);

			if (iPlus != 0)
			{
				eCost.append("Plus:").append(iPlus);
			}

			Formula baseCost = e.getSafe(FormulaKey.BASECOST);

			if (!"0".equals(baseCost.toString()))
			{
				if (eCost.length() != 0)
				{
					eCost.append(", ");
				}

				eCost.append("Precost:").append(baseCost);
			}

			Formula cost = e.getSafe(FormulaKey.BASECOST);

			if (!"0".equals(cost.toString()))
			{
				if (eCost.length() != 0)
				{
					eCost.append(", ");
				}

				eCost.append("Cost:").append(cost);
			}

			sRet = eCost.toString();
			return sRet;
		}

		/**
		 * @param aPlayerCharacter
		 * @param e
		 * @return Object
		 */
		protected Object getPrereqValue(PlayerCharacter aPlayerCharacter, EquipmentModifier e) {
			Object sRet;
			sRet = PrerequisiteUtilities.preReqHTMLStringsForList(
					aPlayerCharacter, aNewEq, e.getPrerequisiteList(), true);
			return sRet;
		}

		/**
		 * @param e
		 * @return Object
		 */
		protected Object getNameValue(EquipmentModifier e) {
			Object sRet;
			sRet = e.getDisplayName();

			if (e.isType("BaseMaterial"))
			{
				sRet = "*" + sRet;
			}
			return sRet;
		}

		/**
		 * @param e
		 * @return Object
		 */
		protected Object getHead2QualifiesValue(EquipmentModifier e) {
			Object sRet;
			if ((aNewEq != null) && aNewEq.canAddModifier(e, false))
			{
				sRet = "Y";
			}
			else
			{
				sRet = "N";
			}
			return sRet;
		}

		/**
		 * @param e
		 * @return Object
		 */
		protected Object getHead1QualifiesValue(EquipmentModifier e) {
			Object sRet;
			if ((aNewEq != null) && aNewEq.canAddModifier(e, true))
			{
				sRet = "Y";
			}
			else
			{
				sRet = "N";
			}
			return sRet;
		}

		private void setFilter(Equipment anEq, int listCount)
		{
			List<String> aFilter = anEq.typeList();
			int currentRowCount = getRowCount();
			displayModifiers.clear();

			if (currentRowCount > 0)
			{
				fireTableRowsDeleted(0, currentRowCount - 1);
			}

			for (EquipmentModifier aEqMod : Globals.getContext().ref.getConstructedCDOMObjects(EquipmentModifier.class))
			{
				if (anEq.isVisible(aEqMod))
				{
					if (aEqMod.isType("ALL"))
					{
						displayModifiers.add(aEqMod);
					}
					else
					{
						for (String aType : aFilter)
						{
							if (aEqMod.isType(aType))
							{
								displayModifiers.add(aEqMod);

								break;
							}
						}
					}
				}
			}

			Globals.sortPObjectListByName(displayModifiers);
			lastRow = -1;

			fireTableStructureChanged();
		}

		/**
		 * Get the display modfiers
		 * @return display modfiers
		 */
		public List<EquipmentModifier> getDisplayModifiers() 
		{
			return displayModifiers;
		}
	}
	/**
	 */
	public class EquipmentModModelSingle extends EquipmentModModel {
		private static final int COL_Q1 = 0;
		private static final int COL_NAME = 1;
		private static final int COL_SA = 2;
		private static final int COL_COST = 3;
		private static final int COL_PREREQ = 4;
		private static final int COL_SOURCE = 5;
		private static final int COL_COUNT = 6;

		protected Object getEqModTableValueAt(PlayerCharacter aPlayerCharacter, int row, int column)
		{
			EquipmentModifier e;

			try	{
				e = getDisplayModifiers().get(row);
			}
			catch (Exception exc) {
				return null;
			}

			switch (column)
			{
				case COL_Q1:
					return getHead1QualifiesValue(e);
				case COL_NAME:
					return getNameValue(e);
				case COL_PREREQ:
					return getPrereqValue(aPlayerCharacter, e);
				case COL_COST:
					return getCostValue(e);
				case COL_SA:
					return getSaValue(e);
				case COL_SOURCE:
					return getSourceValue(e);
				default:
					Logging.errorPrint("In EqBuilder.getEqModTableValueAt the column " + column + " is not handled.");
					return "";
			}
		}
		public Class<?> getColumnClass(int column)
		{
			return String.class;
		}


		/**
		 * Return the column name.
		 * @param column
		 *
		 * @return the name of the column
		 */
		public String getColumnName(int column)
		{
			switch(column) {
			case COL_Q1: return "Q";
			case COL_NAME: return "Name";
			case COL_PREREQ: return "Prereqs";
			case COL_COST: return "Cost";
			case COL_SA: return "Special Properties";
			case COL_SOURCE: return "Source";
			default:
				return "";
			}
		}
		public int getColumnCount() {
			return COL_COUNT;
		}
		@Override
		public String toString() {
			return "EquipmentModModelSingle";
		}
   }
	/**
	 */
	public class EquipmentModModelDouble extends EquipmentModModel {
		private static final int COL_Q1 = 0;
		private static final int COL_Q2 = 1;
		private static final int COL_NAME = 2;
		private static final int COL_SA = 3;
		private static final int COL_COST = 4;
		private static final int COL_PREREQ = 5;
		private static final int COL_SOURCE = 6;
		private static final int COL_COUNT = 7;

		protected Object getEqModTableValueAt(PlayerCharacter aPlayerCharacter, int row, int column)
		{
			EquipmentModifier e;

			try	{
				e = getDisplayModifiers().get(row);
			}
			catch (Exception exc) {
				return null;
			}

			switch (column)
			{
				case COL_Q1:
					return getHead1QualifiesValue(e);
				case COL_Q2:
					return getHead2QualifiesValue(e);
				case COL_NAME:
					return getNameValue(e);
				case COL_PREREQ:
					return getPrereqValue(aPlayerCharacter, e);
				case COL_COST:
					return getCostValue(e);
				case COL_SA:
					return getSaValue(e);
				case COL_SOURCE:
					return getSourceValue(e);
				default:
					Logging.errorPrint("In EqBuilder.getEqModTableValueAt the column " + column + " is not handled.");
					return "";
			}
		}
		/**
		 * @param column
		 * @return Class
		 *
		 */
		public Class<?> getColumnClass(int column)
		{
			return String.class;
		}

		/**
		 * Return the number of columns in the table.
		 *
		 * @return the number of columns
		 */
		public int getColumnCount()
		{
			return COL_COUNT;
		}

		/**
		 * Return the column name.
		 * @param column
		 *
		 * @return the name of the column
		 */
		public String getColumnName(int column)
		{
			switch(column) {
			case COL_Q1: return "Q1";
			case COL_Q2: return "Q2";
			case COL_NAME: return "Name";
			case COL_PREREQ: return "Prereqs";
			case COL_COST: return "Cost";
			case COL_SA: return "Special Properties";
			case COL_SOURCE: return "Source";
			default:
				return "";
			}
		}
	}
}
