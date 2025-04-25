/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.gui2.tabs;

import static pcgen.gui2.tabs.equip.EquipmentSelection.EQUIPMENT_ARRAY_FLAVOR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import pcgen.cdom.base.Constants;
import pcgen.core.Equipment;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.EquipmentListFacade;
import pcgen.facade.core.EquipmentListFacade.EquipmentListEvent;
import pcgen.facade.core.EquipmentListFacade.EquipmentListListener;
import pcgen.facade.core.GearBuySellFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilterButton;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.equip.EquipmentSelection;
import pcgen.gui2.tabs.models.BigDecimalFieldHandler;
import pcgen.gui2.tabs.models.CharacterComboBoxModel;
import pcgen.gui2.tabs.models.CharacterTreeCellRenderer;
import pcgen.gui2.tabs.models.CharacterTreeCellRenderer.Handler;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.SignIcon;
import pcgen.gui2.util.SignIcon.Sign;
import pcgen.gui2.util.event.PopupMouseAdapter;
import pcgen.gui2.util.treeview.CachedDataView;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.gui3.utilty.ColorUtilty;
import pcgen.system.CharacterManager;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

/**
 * A character tab providing the user with the ability to buy and sell
 * equipment.
 */
@SuppressWarnings("serial")
public class PurchaseInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	private static final Set<String> PRIMARY_TYPES = new HashSet<>();
	private final FilteredTreeViewTable<CharacterFacade, EquipmentFacade> availableTable;
	private final FilteredTreeViewTable<CharacterFacade, EquipmentFacade> purchasedTable;
	private final EquipmentRenderer equipmentRenderer;
	private final JCheckBox autoResizeBox;
	private final JButton addCustomButton;
	private final JButton addEquipmentButton;
	private final JButton sellEquipmentButton;
	private final JButton removeEquipmentButton;
	private final InfoPane infoPane;
	private final JFormattedTextField wealthLabel;
	private final JFormattedTextField goldField;
	private final JFormattedTextField goldModField;
	private final JComboBox buySellRateBox;
	private final JButton fundsAddButton;
	private final JButton fundsSubtractButton;
	private final JCheckBox allowDebt;
	private final List<JLabel> currencyLabels;

	/**
	 * Create a new instance of PurchaseInfoTab
	 */
	public PurchaseInfoTab()
	{
		this.availableTable = new FilteredTreeViewTable<>();
		this.purchasedTable = new FilteredTreeViewTable<>();
		this.equipmentRenderer = new EquipmentRenderer();
		this.autoResizeBox = new JCheckBox();
		this.addCustomButton = new JButton();
		this.addEquipmentButton = new JButton();
		this.sellEquipmentButton = new JButton();
		this.removeEquipmentButton = new JButton();
		this.infoPane = new InfoPane();
		this.wealthLabel = new JFormattedTextField(NumberFormat.getNumberInstance());
		this.goldField = new JFormattedTextField(NumberFormat.getNumberInstance());
		this.goldModField = new JFormattedTextField(NumberFormat.getNumberInstance());
		this.buySellRateBox = new JComboBox<>();
		this.fundsAddButton = new JButton();
		this.fundsSubtractButton = new JButton();
		this.allowDebt = new JCheckBox();
		this.currencyLabels = new ArrayList<>();

		initComponents();
	}

	private void initComponents()
	{
		setOrientation(VERTICAL_SPLIT);
		FlippingSplitPane splitPane = new FlippingSplitPane(); //$NON-NLS-1$
		splitPane.setOrientation(HORIZONTAL_SPLIT);
		{ // Top Left panel
			FilterBar<CharacterFacade, EquipmentFacade> filterBar = new FilterBar<>();
			{ // Filters
				filterBar.addDisplayableFilter(new SearchFilterPanel());
				FilterButton<CharacterFacade, EquipmentFacade> premadeFilter =
						new FilterButton<>("EqQualified"); //$NON-NLS-1$
				premadeFilter.setText(LanguageBundle.getString("in_igQualFilter")); //$NON-NLS-1$
				premadeFilter.setFilter(CharacterFacade::isQualifiedFor);
				FilterButton<CharacterFacade, EquipmentFacade> customFilter =
						new FilterButton<>("EqAffordable"); //$NON-NLS-1$
				customFilter.setText(LanguageBundle.getString("in_igAffordFilter")); //$NON-NLS-1$
				customFilter.setFilter((context, element) ->
						context.getInfoFactory().getCost(element) <= context.getFundsRef().get().floatValue());
				filterBar.addDisplayableFilter(premadeFilter);
				filterBar.addDisplayableFilter(customFilter);
			}
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(filterBar, BorderLayout.NORTH);

			availableTable.setTreeCellRenderer(equipmentRenderer);
			availableTable.setDisplayableFilter(filterBar);
			panel.add(new JScrollPane(availableTable), BorderLayout.CENTER);

			Box box = Box.createHorizontalBox();
			box.add(Box.createHorizontalStrut(5));
			box.add(autoResizeBox);
			box.add(Box.createHorizontalGlue());
			addCustomButton.setHorizontalTextPosition(SwingConstants.LEADING);
			box.add(addCustomButton);
			box.add(Box.createHorizontalStrut(5));
			addEquipmentButton.setHorizontalTextPosition(SwingConstants.LEADING);
			box.add(addEquipmentButton);
			box.add(Box.createHorizontalStrut(5));
			box.setBorder(new EmptyBorder(0, 0, 5, 0));
			panel.add(box, BorderLayout.SOUTH);
			splitPane.setLeftComponent(panel);
		}
		{// Top Right panel
			FilterBar<CharacterFacade, EquipmentFacade> filterBar = new FilterBar<>();
			filterBar.addDisplayableFilter(new SearchFilterPanel());

			JPanel panel = new JPanel(new BorderLayout());
			panel.add(filterBar, BorderLayout.NORTH);

			purchasedTable.setDisplayableFilter(filterBar);
			purchasedTable.setTreeCellRenderer(equipmentRenderer);
			panel.add(new JScrollPane(purchasedTable), BorderLayout.CENTER);

			Box box = Box.createHorizontalBox();
			box.add(Box.createHorizontalStrut(5));
			sellEquipmentButton.setToolTipText(LanguageBundle.getString("in_ieSellEq_Tooltip"));
			box.add(sellEquipmentButton);
			removeEquipmentButton.setToolTipText(LanguageBundle.getString("in_ieRemEq_Tooltip"));
			box.add(removeEquipmentButton);
			box.add(Box.createHorizontalGlue());
			box.setBorder(new EmptyBorder(0, 0, 5, 0));
			panel.add(box, BorderLayout.SOUTH);
			splitPane.setRightComponent(panel);
		}
		setTopComponent(splitPane);
		splitPane = new FlippingSplitPane(); //$NON-NLS-1$
		splitPane.setOrientation(HORIZONTAL_SPLIT);
		{// Bottom Left Panel
			JPanel panel = new JPanel();
			initMoneyPanel(panel);
			splitPane.setLeftComponent(panel);
		}
		{// Bottom Right Panel
			infoPane.setTitle(LanguageBundle.getString("in_igEqInfo")); //$NON-NLS-1$
			splitPane.setRightComponent(infoPane);
		}
		splitPane.setResizeWeight(0.25);
		setResizeWeight(1);
		setBottomComponent(splitPane);
	}

	/**
	 * Create the money panel laying out all components.
	 *
	 * @param panel The panel to be populated.
	 */
	private void initMoneyPanel(JPanel panel)
	{
		panel.setLayout(new GridBagLayout());

		GridBagConstraints leftGbc = new GridBagConstraints();
		Insets panelInsets = panel.getInsets();
		leftGbc.insets = new Insets(2, panelInsets.left, 2, 10);
		leftGbc.gridwidth = 2;
		leftGbc.fill = GridBagConstraints.HORIZONTAL;
		leftGbc.anchor = GridBagConstraints.LINE_START;

		GridBagConstraints middleGbc = new GridBagConstraints();
		middleGbc.insets = new Insets(2, 5, 2, 5);
		middleGbc.gridwidth = 1;
		middleGbc.fill = GridBagConstraints.HORIZONTAL;
		middleGbc.anchor = GridBagConstraints.LINE_END;

		GridBagConstraints rightGbc = new GridBagConstraints();
		rightGbc.insets = new Insets(2, 10, 2, panelInsets.right);
		rightGbc.gridwidth = GridBagConstraints.REMAINDER;
		rightGbc.fill = GridBagConstraints.HORIZONTAL;

		GridBagConstraints fullLineGbc = new GridBagConstraints();
		fullLineGbc.insets = new Insets(2, panelInsets.left, 2, panelInsets.right);
		fullLineGbc.gridwidth = GridBagConstraints.REMAINDER;
		fullLineGbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel label = new JLabel(LanguageBundle.getString("in_igValueLabel")); //$NON-NLS-1$
		panel.add(label, leftGbc);
		wealthLabel.setEditable(false);
		wealthLabel.setColumns(10);
		wealthLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(wealthLabel, middleGbc);
		panel.add(createCurrencyLabel(), rightGbc);

		label = new JLabel(LanguageBundle.getString("in_igFundsLabel")); //$NON-NLS-1$
		panel.add(label, leftGbc);
		goldField.setHorizontalAlignment(SwingConstants.RIGHT);
		goldField.setColumns(10);
		goldField.setMinimumSize(new Dimension(50, goldField.getPreferredSize().height));
		panel.add(goldField, middleGbc);
		panel.add(createCurrencyLabel(), rightGbc);

		label = new JLabel(LanguageBundle.getString("in_igAddSubFundsLabel")); //$NON-NLS-1$
		panel.add(label, fullLineGbc);

		JPanel expmodPanel = new JPanel();
		{
			GridBagConstraints gbc2 = new GridBagConstraints();
			gbc2.fill = GridBagConstraints.HORIZONTAL;
			gbc2.weightx = 1.0;
			gbc2.insets = new Insets(0, 1, 0, 1);
			expmodPanel.add(fundsAddButton, gbc2);
			expmodPanel.add(fundsSubtractButton, gbc2);
		}
		panel.add(expmodPanel, leftGbc);
		goldModField.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(goldModField, middleGbc);
		panel.add(createCurrencyLabel(), rightGbc);

		label = new JLabel(LanguageBundle.getString("in_igBuySellRateLabel")); //$NON-NLS-1$
		fullLineGbc.insets = new Insets(10, 2, 2, 2);
		panel.add(label, fullLineGbc);
		buySellRateBox.setPrototypeDisplayValue("QuiteLongPrototypeDisplayValue"); //$NON-NLS-1$
		fullLineGbc.insets = new Insets(2, 2, 2, 2);
		panel.add(buySellRateBox, fullLineGbc);

		panel.add(allowDebt, fullLineGbc);

		fullLineGbc.weighty = 1.0f;
		panel.add(new JLabel(), fullLineGbc);
	}

	/**
	 * Create a new label with the currency abbreviation.
	 *
	 * @return A new label
	 */
	private JLabel createCurrencyLabel()
	{
		JLabel label;
		label = new JLabel(""); //$NON-NLS-1$
		currencyLabels.add(label);
		return label;
	}

	@Override
	public ModelMap createModels(final CharacterFacade character)
	{
		ModelMap models = new ModelMap();
		models.put(AvailableTreeViewModel.class, new AvailableTreeViewModel(character));
		models.put(PurchasedTreeViewModel.class, new PurchasedTreeViewModel(character));
		models.put(UseAutoResizeAction.class, new UseAutoResizeAction(character));
		models.put(AddCustomAction.class, new AddCustomAction(character));
		models.put(AddAction.class, new AddAction(character));
		models.put(SellAction.class, new SellAction(character));
		models.put(RemoveAction.class, new RemoveAction(character));
		models.put(DeleteCustomAction.class, new DeleteCustomAction(character));
		models.put(FundsAddAction.class, new FundsAddAction(character));
		models.put(FundsSubtractAction.class, new FundsSubtractAction(character));
		models.put(AllowDebtAction.class, new AllowDebtAction(character));
		models.put(EquipInfoHandler.class, new EquipInfoHandler(character));
		models.put(Handler.class, equipmentRenderer.createHandler(character));
		models.put(EquipmentFilterHandler.class, new EquipmentFilterHandler(character));
		models.put(EquipmentTransferHandler.class, new EquipmentTransferHandler(character));
		models.put(CurrencyLabelHandler.class, new CurrencyLabelHandler(character));
		models.put(BuyPopupMenuHandler.class, new BuyPopupMenuHandler(character));
		models.put(SellPopupMenuHandler.class, new SellPopupMenuHandler(character));

		models.put(CurrencyFieldHandler.class, new CurrencyFieldHandler(character));

		CharacterComboBoxModel<GearBuySellFacade> buySellModel = new CharacterComboBoxModel<>(
				character.getDataSet().getGearBuySellSchemes(), character.getGearBuySellRef())
		{

			@Override
			public void setSelectedItem(Object anItem)
			{
				character.setGearBuySellRef((GearBuySellFacade) anItem);
			}

		};
		models.put(CharacterComboBoxModel.class, buySellModel);

		return models;
	}

	@Override
	public void restoreModels(ModelMap models)
	{
		models.get(EquipmentFilterHandler.class).install();
		models.get(Handler.class).install();
		models.get(AvailableTreeViewModel.class).install();
		purchasedTable.setTreeViewModel(models.get(PurchasedTreeViewModel.class));
		autoResizeBox.setAction(models.get(UseAutoResizeAction.class));
		addCustomButton.setAction(models.get(AddCustomAction.class));
		addEquipmentButton.setAction(models.get(AddAction.class));
		sellEquipmentButton.setAction(models.get(SellAction.class));
		removeEquipmentButton.setAction(models.get(RemoveAction.class));
		fundsAddButton.setAction(models.get(FundsAddAction.class));
		fundsSubtractButton.setAction(models.get(FundsSubtractAction.class));
		buySellRateBox.setModel(models.get(CharacterComboBoxModel.class));
		allowDebt.setAction(models.get(AllowDebtAction.class));

		models.get(EquipInfoHandler.class).install();
		models.get(EquipmentTransferHandler.class).install();
		models.get(UseAutoResizeAction.class).install();
		models.get(AddAction.class).install();
		models.get(SellAction.class).install();
		models.get(RemoveAction.class).install();
		models.get(CurrencyFieldHandler.class).install();
		models.get(CurrencyLabelHandler.class).install();
		models.get(BuyPopupMenuHandler.class).install();
		models.get(SellPopupMenuHandler.class).install();
		models.get(AllowDebtAction.class).install();
	}

	@Override
	public void storeModels(ModelMap models)
	{
		models.get(EquipInfoHandler.class).uninstall();
		models.get(AddAction.class).uninstall();
		models.get(SellAction.class).uninstall();
		models.get(RemoveAction.class).uninstall();
		models.get(CurrencyFieldHandler.class).uninstall();
		models.get(BuyPopupMenuHandler.class).uninstall();
		models.get(SellPopupMenuHandler.class).uninstall();
		models.get(Handler.class).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return new TabTitle(Tab.PURCHASE);
	}

	private static List<EquipmentFacade> getMenuTargets(JTable table, MouseEvent e)
	{
		int row = table.rowAtPoint(e.getPoint());
		if (!table.isRowSelected(row))
		{
			if ((row >= 0) && (table.getRowCount() > row))
			{
				table.setRowSelectionInterval(row, row);
			}
		}
		return Arrays.stream(table.getSelectedRows())
		             .mapToObj(selRow -> table.getModel().getValueAt(selRow, 0))
		             .filter(value -> value instanceof EquipmentFacade)
		             .map(value -> (EquipmentFacade) value)
		             .collect(Collectors.toList());
	}

	private class CurrencyFieldHandler
	{

		private final BigDecimalFieldHandler fundsHandler;
		private final BigDecimalFieldHandler wealthHandler;

		public CurrencyFieldHandler(final CharacterFacade character)
		{
			/**
			 * Handler for the Current Funds field. This listens for and
			 * processes both changes to the value from the character and
			 * modifications to the field made by the user.
			 */
			fundsHandler = new BigDecimalFieldHandler(goldField, character.getFundsRef())
			{

				@Override
				protected void valueChanged(BigDecimal value)
				{
					character.setFunds(value);
					availableTable.refilter();
				}

			};
			/**
			 * Handler for theTotal Wealth field. This listens for and processes
			 * changes to the value from the character.
			 */
			wealthHandler = new BigDecimalFieldHandler(wealthLabel, character.getWealthRef())
			{

				@Override
				protected void valueChanged(BigDecimal value)
				{
					// Ignored for this read-only field
				}

			};
		}

		public void install()
		{
			fundsHandler.install();
			wealthHandler.install();
		}

		public void uninstall()
		{
			fundsHandler.uninstall();
			wealthHandler.uninstall();
		}
	}

	private class AddAction extends AbstractAction
	{

		private final CharacterFacade character;

		public AddAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_ieAddEq")); //$NON-NLS-1$
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<?> data = availableTable.getSelectedData();
			if (data != null)
			{
				for (Object object : data)
				{
					if (object instanceof EquipmentFacade equip)
					{
						if (character.isAutoResize())
						{
							equip = character.getEquipmentSizedForCharacter(equip);
						}
						character.addPurchasedEquipment(equip, 1, false, false);
					}
				}
				availableTable.refilter();
			}
		}

		public void install()
		{
			availableTable.addActionListener(this);
		}

		public void uninstall()
		{
			availableTable.removeActionListener(this);
		}

	}

	private class AddCustomAction extends AbstractAction
	{

		private final CharacterFacade character;

		public AddCustomAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_igAddCustom")); //$NON-NLS-1$
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<?> data = availableTable.getSelectedData();
			if (data != null)
			{
				for (Object object : data)
				{
					if (object instanceof EquipmentFacade equip)
					{
						if (character.isAutoResize())
						{
							equip = character.getEquipmentSizedForCharacter(equip);
						}
						character.addPurchasedEquipment(equip, 1, true, false);
					}
				}
			}
		}

	}

	/**
	 * The Class {@code DeleteCustomAction} defines an action to delete a
	 * custom equipment item.
	 */
	private class DeleteCustomAction extends AbstractAction
	{

		private final CharacterFacade character;

		public DeleteCustomAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_igDeleteCustom")); //$NON-NLS-1$
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<?> data = availableTable.getSelectedData();
			if (data != null)
			{
				for (Object object : data)
				{
					if (object instanceof EquipmentFacade equip)
					{
						character.deleteCustomEquipment(equip);
					}
				}
			}
		}

	}

	private class UseAutoResizeAction extends AbstractAction
	{

		private final CharacterFacade character;

		public UseAutoResizeAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_igAutoResize")); //$NON-NLS-1$
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.setAutoResize(autoResizeBox.isSelected());
		}

		public void install()
		{
			autoResizeBox.setSelected(character.isAutoResize());
		}

	}

	private final class SellAction extends AbstractAction
	{

		private final CharacterFacade character;

		private SellAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_ieSellEq"));
			putValue(SMALL_ICON, Icons.Back16.getImageIcon());
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<?> data = purchasedTable.getSelectedData();
			if (data != null)
			{
				for (Object object : data)
				{
					character.removePurchasedEquipment((EquipmentFacade) object, 1, false);
				}
				availableTable.refilter();
			}
		}

		public void install()
		{
			purchasedTable.addActionListener(this);
		}

		public void uninstall()
		{
			purchasedTable.removeActionListener(this);
		}
	}

	private final class RemoveAction extends AbstractAction
	{

		private final CharacterFacade character;

		private RemoveAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_ieRemEq"));
			putValue(SMALL_ICON, Icons.Back16.getImageIcon());
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<?> data = purchasedTable.getSelectedData();
			if (data != null)
			{
				data.forEach(object -> character.removePurchasedEquipment((EquipmentFacade) object, 1, true));
				availableTable.refilter();
			}
		}

		public void install()
		{
			purchasedTable.addActionListener(this);
		}

		public void uninstall()
		{
			purchasedTable.removeActionListener(this);
		}
	}

	/**
	 * Handler for actions from the add funds button. Also defines the
	 * appearance of the button.
	 */
	private class FundsAddAction extends AbstractAction
	{

		private final CharacterFacade character;

		public FundsAddAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, new SignIcon(Sign.Plus));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object value = goldModField.getValue();
			if (value == null)
			{
				return;
			}
			BigDecimal modVal = BigDecimal.valueOf(((Number) value).doubleValue());
			character.adjustFunds(modVal);
		}

	}

	/**
	 * Handler for actions from the subtract funds button. Also defines the
	 * appearance of the button.
	 */
	private class FundsSubtractAction extends AbstractAction
	{

		private final CharacterFacade character;

		public FundsSubtractAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, new SignIcon(Sign.Minus));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object value = goldModField.getValue();
			if (value == null)
			{
				return;
			}
			BigDecimal modVal = BigDecimal.valueOf(((Number) value).doubleValue() * -1);
			character.adjustFunds(modVal);
		}

	}

	/**
	 * The Class {@code AllowDebtAction} links the allow debt checkbox to a
	 * character.
	 */
	private class AllowDebtAction extends AbstractAction
	{

		private final CharacterFacade character;

		public AllowDebtAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_igAllowDebt")); //$NON-NLS-1$
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.setAllowDebt(allowDebt.isSelected());
		}

		public void install()
		{
			allowDebt.setSelected(character.isAllowDebt());
		}

	}

	private class EquipInfoHandler implements ListSelectionListener
	{

		private final CharacterFacade character;
		private String text;
		private List<EquipmentFacade> oldList;

		public EquipInfoHandler(CharacterFacade character)
		{
			this.character = character;
			this.text = ""; //$NON-NLS-1$
			oldList = null;
		}

		public void install()
		{
			availableTable.getSelectionModel().addListSelectionListener(this);
			purchasedTable.getSelectionModel().addListSelectionListener(this);
			infoPane.setText(text);
		}

		public void uninstall()
		{
			availableTable.getSelectionModel().removeListSelectionListener(this);
			purchasedTable.getSelectionModel().removeListSelectionListener(this);
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			JTable target = availableTable;
			if (purchasedTable.getSelectionModel().equals(e.getSource()))
			{
				target = purchasedTable;
			}
			if (!e.getValueIsAdjusting())
			{
				int[] selectedRows = target.getSelectedRows();
				List<EquipmentFacade> newList = new ArrayList<>(selectedRows.length);
				for (int row : selectedRows)
				{
					if (row != -1)
					{
						Object value = target.getModel().getValueAt(row, 0);
						if (value instanceof EquipmentFacade)
						{
							newList.add((EquipmentFacade) value);
						}
					}
				}
				if (newList.isEmpty() || newList.equals(oldList))
				{
					return;
				}
				oldList = newList;
				StringBuilder sb = new StringBuilder(2000);
				for (EquipmentFacade equip : newList)
				{
					sb.append(character.getInfoFactory().getHTMLInfo(equip));
				}
				text = "<html>" + sb + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$
				infoPane.setText(text);
			}
		}

	}

	/**
	 * The Class {@code CurrencyLabelHandler} manages the currently
	 * displayed currency.
	 */
	private class CurrencyLabelHandler
	{

		private final CharacterFacade character;

		public CurrencyLabelHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			String currencyDisplay = character.getDataSet().getGameMode().getCurrencyDisplay();
			for (JLabel label : currencyLabels)
			{
				label.setText(currencyDisplay);
			}
		}

	}

	private class AvailableTreeViewModel extends CachedDataView<EquipmentFacade>
			implements TreeViewModel<EquipmentFacade>, DataView<EquipmentFacade>
	{

		private final ListFacade<? extends TreeView<EquipmentFacade>> treeviews =
				new DefaultListFacade<>(Arrays.asList(EquipmentTreeView.values()));
		private final List<DefaultDataViewColumn> columns =
				Arrays.asList(new DefaultDataViewColumn("in_igEqModelColCost", Float.class, true), //$NON-NLS-1$
					new DefaultDataViewColumn("in_igEqModelColWeight", Float.class, true), //$NON-NLS-1$
					new DefaultDataViewColumn("in_descrip", String.class, false), //$NON-NLS-1$
					new DefaultDataViewColumn("in_igEqModelColSource", String.class, false)); //$NON-NLS-1$
		private final CharacterFacade character;
		private final ListFacade<EquipmentFacade> equipmentList;

		public AvailableTreeViewModel(CharacterFacade character)
		{
			this.character = character;
			this.equipmentList = character.getDataSet().getEquipment();

			if (PRIMARY_TYPES.isEmpty())
			{
				for (int i = 0; i < equipmentList.getSize(); i++)
				{
					EquipmentFacade eq = equipmentList.getElementAt(i);
					List<String> types = eq.getTypesForDisplay();
					if (!types.isEmpty())
					{
						PRIMARY_TYPES.add(types.get(0));
					}
				}
			}
		}

		@Override
		public ListFacade<? extends TreeView<EquipmentFacade>> getTreeViews()
		{
			return treeviews;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 2;
		}

		@Override
		public DataView<EquipmentFacade> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<EquipmentFacade> getDataModel()
		{
			return equipmentList;
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}

		@Override
		public String getPrefsKey()
		{
			return "PurchaseAvail"; //$NON-NLS-1$
		}

		@Override
		public Object getDataInternal(EquipmentFacade obj, int column)
		{
			return switch (column)
					{
						case 0 -> character.getInfoFactory().getCost(obj);
						case 1 -> character.getInfoFactory().getWeight(obj);
						case 2 -> character.getInfoFactory().getDescription(obj);
						case 3 -> obj.getSource();
						default -> null;
					};
		}

		@Override
		public void setData(Object value, EquipmentFacade element, int column)
		{
		}

		public void install()
		{
			availableTable.setTreeViewModel(this);
		}
	}

	private class PurchasedTreeViewModel
			implements TreeViewModel<EquipmentFacade>, DataView<EquipmentFacade>, EquipmentListListener
	{

		private final ListFacade<? extends TreeView<EquipmentFacade>> treeviews =
				new DefaultListFacade<>(Arrays.asList(EquipmentTreeView.values()));
		private final List<DefaultDataViewColumn> columns =
				Arrays.asList(new DefaultDataViewColumn("in_igEqModelColCost", Float.class, true), //$NON-NLS-1$
					new DefaultDataViewColumn("in_igEqModelColWeight", Float.class, false), //$NON-NLS-1$
					new DefaultDataViewColumn("in_igEqModelColQty", Integer.class, true), //$NON-NLS-1$
					new DefaultDataViewColumn("in_descrip", String.class, false)); //$NON-NLS-1$
		private final CharacterFacade character;
		private final EquipmentListFacade equipmentList;

		public PurchasedTreeViewModel(CharacterFacade character)
		{
			this.character = character;
			this.equipmentList = character.getPurchasedEquipment();
			this.equipmentList.addEquipmentListListener(this);
		}

		@Override
		public ListFacade<? extends TreeView<EquipmentFacade>> getTreeViews()
		{
			return treeviews;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		@Override
		public DataView<EquipmentFacade> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<EquipmentFacade> getDataModel()
		{
			return equipmentList;
		}

		@Override
		public Object getData(EquipmentFacade obj, int column)
		{
			return switch (column)
					{
						case 0 -> character.getInfoFactory().getCost(obj);
						case 1 -> character.getInfoFactory().getWeight(obj);
						case 2 -> equipmentList.getQuantity(obj);
						case 3 -> character.getInfoFactory().getDescription(obj);
						default -> null;
					};
		}

		@Override
		public void setData(Object value, EquipmentFacade element, int column)
		{
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}

		@Override
		public void quantityChanged(EquipmentListEvent equipment)
		{
			purchasedTable.refreshModelData();
		}

		@Override
		public String getPrefsKey()
		{
			return "Purchased"; //$NON-NLS-1$
		}

	}

	private enum EquipmentTreeView implements TreeView<EquipmentFacade>
	{

		NAME(LanguageBundle.getString("in_nameLabel")), //$NON-NLS-1$
		TYPE_NAME(LanguageBundle.getString("in_typeName")), //$NON-NLS-1$
		TYPE_SUBTYPE_NAME(LanguageBundle.getString("in_typeSubtypeName")), //$NON-NLS-1$
		SOURCE_NAME(LanguageBundle.getString("in_sourceName")); //$NON-NLS-1$
		private final String name;

		private EquipmentTreeView(String name)
		{
			this.name = name;
		}

		@Override
		public String getViewName()
		{
			return name;
		}

		@Override
		public List<TreeViewPath<EquipmentFacade>> getPaths(EquipmentFacade pobj)
		{
			switch (this)
			{
				case TYPE_SUBTYPE_NAME:
					List<String> types = pobj.getTypesForDisplay();
					if (types != null && types.size() > 1)
					{
						List<TreeViewPath<EquipmentFacade>> paths = new ArrayList<>();
						for (String type : types)
						{
							if (PRIMARY_TYPES.contains(type))
							{
								for (String subType : types)
								{
									if (!type.equals(subType))
									{
										paths.add(new TreeViewPath<>(pobj, type, subType));
									}
								}
							}
						}
						return paths;
					}
					// Less then two types, fall through to treat it as a type tree.
				case TYPE_NAME:
					types = pobj.getTypesForDisplay();
					if (types != null && !types.isEmpty())
					{
						List<TreeViewPath<EquipmentFacade>> paths = new ArrayList<>(types.size());
						for (String type : types)
						{
							if (PRIMARY_TYPES.contains(type))
							{
								paths.add(new TreeViewPath<>(pobj, type));
							}
						}
						return paths;
					}
					// No types, fall through and treat it as just a name.
				case NAME:
					return Collections.singletonList(new TreeViewPath<>(pobj));
				case SOURCE_NAME:
					return Collections.singletonList(new TreeViewPath<>(pobj, pobj.getSourceForNodeDisplay()));
				default:
					throw new InternalError();
			}

		}

	}

	/**
	 * The Class {@code EquipmentRenderer} displays the tree cells of the
	 * available and purchased equipment tables.
	 */
	private static class EquipmentRenderer extends CharacterTreeCellRenderer
	{

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean focus)
		{

			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
			Object equipObj = ((DefaultMutableTreeNode) value).getUserObject();
			if (equipObj instanceof EquipmentFacade && !character.isQualifiedFor((EquipmentFacade) equipObj))
			{
				setForeground(ColorUtilty.colorToAWTColor(UIPropertyContext.getNotQualifiedColor()));
			}
			return this;
		}

	}

	private class EquipmentFilterHandler
	{

		private final CharacterFacade character;

		public EquipmentFilterHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			availableTable.setContext(character);
		}

	}

	private class EquipmentTransferHandler extends TransferHandler
	{

		private final CharacterFacade character;

		public EquipmentTransferHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			availableTable.setDragEnabled(true);
			availableTable.setDropMode(DropMode.ON);
			availableTable.setTransferHandler(this);

			purchasedTable.setDragEnabled(true);
			purchasedTable.setDropMode(DropMode.ON);
			purchasedTable.setTransferHandler(this);
		}

		@Override
		public int getSourceActions(JComponent c)
		{
			return MOVE;
		}

		@Override
		protected Transferable createTransferable(JComponent c)
		{
			List<?> data = null;
			if (c == availableTable)
			{
				data = availableTable.getSelectedData();
			}
			else if (c == purchasedTable)
			{
				data = purchasedTable.getSelectedData();
			}
			if (data == null)
			{
				return null;
			}
			data.removeIf(o -> !(o instanceof EquipmentFacade));
			if (data.isEmpty())
			{
				return null;
			}

			EquipmentFacade[] equipArray = data.toArray(new EquipmentFacade[0]);
			return new EquipmentSelection(equipArray);
		}

		@Override
		public boolean canImport(TransferSupport support)
		{
			if (!support.isDataFlavorSupported(EQUIPMENT_ARRAY_FLAVOR))
			{
				return false;
			}
			support.setShowDropLocation(false);
			return true;
		}

		private EquipmentFacade[] getEquipmentArray(TransferSupport support)
		{
			EquipmentFacade[] equipmentArray = null;
			try
			{
				equipmentArray = (EquipmentFacade[]) support.getTransferable().getTransferData(EQUIPMENT_ARRAY_FLAVOR);
			}
			catch (UnsupportedFlavorException | IOException ex)
			{
			}
			return equipmentArray;
		}

		@Override
		public boolean importData(TransferSupport support)
		{
			if (!canImport(support))
			{
				return false;
			}
			if (!support.isDrop())
			{
				return false;
			}
			EquipmentFacade[] equipmentArray = getEquipmentArray(support);
			if (support.getComponent() == availableTable)
			{
				for (EquipmentFacade equipmentFacade : equipmentArray)
				{
					character.removePurchasedEquipment(equipmentFacade, 1, false);
				}
			}
			else if (support.getComponent() == purchasedTable)
			{
				for (EquipmentFacade equipmentFacade : equipmentArray)
				{
					EquipmentFacade equip = character.getEquipmentSizedForCharacter(equipmentFacade);
					character.addPurchasedEquipment(equip, 1, false, false);
				}
			}
			availableTable.refilter();
			return true;
		}

	}

	private class BuyPopupMenuHandler extends PopupMouseAdapter
	{

		private final CharacterFacade character;

		BuyPopupMenuHandler(CharacterFacade character)
		{
			this.character = character;
		}

		@Override
		public void showPopup(MouseEvent e)
		{
			List<EquipmentFacade> targets = getMenuTargets(availableTable, e);
			if (targets.isEmpty())
			{
				return;
			}

			JPopupMenu popupMenu = new JPopupMenu();
			popupMenu.add(new BuyNumMenuItem(character, targets, 1));
			JMenu buyMenu = new JMenu(LanguageBundle.getString("in_igBuyQuantity")); //$NON-NLS-1$
			buyMenu.add(new BuyNumMenuItem(character, targets, 2));
			buyMenu.add(new BuyNumMenuItem(character, targets, 5));
			buyMenu.add(new BuyNumMenuItem(character, targets, 10));
			buyMenu.add(new BuyNumMenuItem(character, targets, 15));
			buyMenu.add(new BuyNumMenuItem(character, targets, 20));
			buyMenu.add(new BuyNumMenuItem(character, targets, 50));
			popupMenu.add(buyMenu);
			popupMenu.add(new BuyNumMenuItem(character, targets, 0));
			popupMenu.addSeparator();
			popupMenu.add(new AddCustomAction(character));
			popupMenu.add(new DeleteCustomAction(character));
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}

		public void install()
		{
			availableTable.addMouseListener(this);
		}

		public void uninstall()
		{
			availableTable.removeMouseListener(this);
		}

	}

	/**
	 * Menu item for buying a quanity of an item at the current rate.
	 */
	private class BuyNumMenuItem extends JMenuItem implements ActionListener
	{

		private final int quantity;
		private final CharacterFacade character;
		private final List<EquipmentFacade> targets;

		BuyNumMenuItem(CharacterFacade character, List<EquipmentFacade> targets, int quantity)
		{
			super(LanguageBundle.getFormattedString(
				quantity > 0 ? "in_igBuyMenuCommand" : "in_igBuyN", quantity)); //$NON-NLS-1$ //$NON-NLS-2$
			this.character = character;
			this.targets = targets;
			this.quantity = quantity;
			if (quantity > 0)
			{
				setToolTipText(LanguageBundle.getFormattedString("in_igBuyMenuDesc", quantity)); //$NON-NLS-1$
			}
			else
			{
				setToolTipText(LanguageBundle.getString("in_igBuyNMenuDesc")); //$NON-NLS-1$
			}
			setIcon(Icons.Add16.getImageIcon());

			addActionListener(this);
		}

		/**
		 * Action to buy a certain number of items.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			int num = quantity;
			if (num == 0)
			{
				String selectedValue =
						JOptionPane.showInputDialog(
							null, LanguageBundle.getString("in_igBuyEnterQuantity"), //$NON-NLS-1$
							Constants.APPLICATION_NAME, JOptionPane.QUESTION_MESSAGE);
				if (selectedValue != null)
				{
					try
					{
						num = (int) Float.parseFloat(selectedValue);
					}
					catch (NumberFormatException ex)
					{
						//Ignored
					}
				}
			}
			if (num > 0)
			{
				for (EquipmentFacade equip : targets)
				{
					if (character.isAutoResize())
					{
						equip = character.getEquipmentSizedForCharacter(equip);
					}
					character.addPurchasedEquipment(equip, num, false, false);
					availableTable.refilter();
				}
			}
		}

	}

	private class SellPopupMenuHandler extends PopupMouseAdapter
	{

		private final CharacterFacade character;

		SellPopupMenuHandler(CharacterFacade character)
		{
			this.character = character;
		}

		@Override
		public void showPopup(MouseEvent e)
		{
			List<EquipmentFacade> targets = getMenuTargets(purchasedTable, e);
			if (targets.isEmpty())
			{
				return;
			}

			JPopupMenu popupMenu = new JPopupMenu();
			popupMenu.add(new SellNumMenuItem(character, targets, 1));
			JMenu sellMenu = new JMenu(LanguageBundle.getString("in_igSellQuantity")); //$NON-NLS-1$
			sellMenu.add(new SellNumMenuItem(character, targets, 2));
			sellMenu.add(new SellNumMenuItem(character, targets, 5));
			sellMenu.add(new SellNumMenuItem(character, targets, 10));
			sellMenu.add(new SellNumMenuItem(character, targets, 15));
			sellMenu.add(new SellNumMenuItem(character, targets, 20));
			sellMenu.add(new SellNumMenuItem(character, targets, 50));
			popupMenu.add(sellMenu);
			popupMenu.add(new SellNumMenuItem(character, targets, 0));
			popupMenu.add(new SellNumMenuItem(character, targets, SellNumMenuItem.SELL_ALL_QUANTITY));
			popupMenu.addSeparator();
			JMenu moveMenu = new JMenu(LanguageBundle.getString("in_igMoveItemMenuTitle")); //$NON-NLS-1$
			moveMenu.setEnabled(false);
			for (CharacterFacade dest : CharacterManager.getCharacters())
			{
				if (dest != character)
				{
					moveMenu.add(new MoveItemMenuItem(character, dest, targets));
					moveMenu.setEnabled(true);
				}
			}
			popupMenu.add(moveMenu);
			JMenu copyMenu = new JMenu(LanguageBundle.getString("in_igCopyItemMenuTitle")); //$NON-NLS-1$
			copyMenu.setEnabled(false);
			for (CharacterFacade dest : CharacterManager.getCharacters())
			{
				if (dest != character)
				{
					copyMenu.add(new CopyItemMenuItem(dest, targets));
					copyMenu.setEnabled(true);
				}
			}
			popupMenu.add(copyMenu);
			popupMenu.addSeparator();
			popupMenu.add(new ModifyChargesMenuItem(character, targets));
			popupMenu.add(new AddNoteMenuItem(character, targets));
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}

		public void install()
		{
			purchasedTable.addMouseListener(this);
		}

		public void uninstall()
		{
			purchasedTable.removeMouseListener(this);
		}

	}

	/**
	 * Menu item for selling a quantity of an item at the current rate.
	 */
	private class SellNumMenuItem extends JMenuItem implements ActionListener
	{

		public static final int SELL_ALL_QUANTITY = -5;
		private final int quantity;
		private final CharacterFacade character;
		private final List<EquipmentFacade> targets;

		SellNumMenuItem(CharacterFacade character, List<EquipmentFacade> targets, int quantity)
		{
			super(
				LanguageBundle.getFormattedString(
					quantity > 0 ? "in_igSellMenuCommand" : (quantity == SELL_ALL_QUANTITY //$NON-NLS-1$
					? "in_igSellAll" : "in_igSellN"), quantity)); //$NON-NLS-1$ //$NON-NLS-2$
			this.character = character;
			this.targets = targets;
			this.quantity = quantity;
			if (quantity > 0)
			{
				setToolTipText(LanguageBundle.getFormattedString("in_igSellMenuDesc", quantity)); //$NON-NLS-1$
			}
			else if (quantity == SELL_ALL_QUANTITY)
			{
				setToolTipText(LanguageBundle.getString("in_igSellAllMenuDesc")); //$NON-NLS-1$
			}
			else
			{
				setToolTipText(LanguageBundle.getString("in_igSellNMenuDesc")); //$NON-NLS-1$
			}
			setIcon(Icons.Remove16.getImageIcon());

			addActionListener(this);
		}

		/**
		 * Action to buy a certain number of items.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			boolean sellAll = quantity == SELL_ALL_QUANTITY;
			int num = quantity;
			if (num == 0)
			{
				String selectedValue =
						JOptionPane.showInputDialog(
							null, LanguageBundle.getString("in_igBuyEnterQuantity"), //$NON-NLS-1$
							Constants.APPLICATION_NAME, JOptionPane.QUESTION_MESSAGE);
				if (selectedValue != null)
				{
					try
					{
						num = (int) Float.parseFloat(selectedValue);
					}
					catch (NumberFormatException ex)
					{
						//Ignored
					}
				}
			}
			if (num > 0 || sellAll)
			{
				for (EquipmentFacade equip : targets)
				{
					if (sellAll)
					{
						num = character.getPurchasedEquipment().getQuantity(equip);
					}
					character.removePurchasedEquipment(equip, num, false);
				}
				availableTable.refilter();
			}
		}

	}

	private static class ModifyChargesMenuItem extends JMenuItem implements ActionListener
	{

		private final CharacterFacade character;
		private final List<EquipmentFacade> targets;

		ModifyChargesMenuItem(CharacterFacade character, List<EquipmentFacade> targets)
		{
			super(LanguageBundle.getString("in_igModifyCharges")); //$NON-NLS-1$
			this.character = character;
			this.targets = targets;
			// Set enabled only if there are items with charges
			boolean hasItemWithCharges = false;
			for (EquipmentFacade equipment : targets)
			{
				if (equipment instanceof Equipment && ((Equipment) equipment).getMaxCharges() > 0)
				{
					hasItemWithCharges = true;
					break;
				}
			}
			setEnabled(hasItemWithCharges);
			addActionListener(this);
		}

		/**
		 * Action to modify the number of charges on the items.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.modifyCharges(targets);
		}

	}

	private static class AddNoteMenuItem extends JMenuItem implements ActionListener
	{

		private final CharacterFacade character;
		private final List<EquipmentFacade> targets;

		AddNoteMenuItem(CharacterFacade character, List<EquipmentFacade> targets)
		{
			super(LanguageBundle.getString("in_igAddNote")); //$NON-NLS-1$
			this.character = character;
			this.targets = targets;

			addActionListener(this);
		}

		/**
		 * Action to modify the number of charges on the items.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.addNote(targets);
		}

	}

	private static class MoveItemMenuItem extends JMenuItem implements ActionListener
	{

		private final CharacterFacade character;
		private final CharacterFacade destination;
		private final List<EquipmentFacade> targets;

		MoveItemMenuItem(CharacterFacade character, CharacterFacade destination, List<EquipmentFacade> targets)
		{
			super(destination.getNameRef().get());
			this.character = character;
			this.destination = destination;
			this.targets = targets;

			addActionListener(this);
		}

		/**
		 * Action to move the items between characters.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			for (EquipmentFacade item : targets)
			{
				character.removePurchasedEquipment(item, 1, true);
				destination.addPurchasedEquipment(item, 1, false, true);
			}
		}

	}

	private static class CopyItemMenuItem extends JMenuItem implements ActionListener
	{

		private final CharacterFacade destination;
		private final List<EquipmentFacade> targets;

		CopyItemMenuItem(CharacterFacade destination, List<EquipmentFacade> targets)
		{
			super(destination.getNameRef().get());
			this.destination = destination;
			this.targets = targets;

			addActionListener(this);
		}

		/**
		 * Action to copy the items to another character.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			for (EquipmentFacade item : targets)
			{
				destination.addPurchasedEquipment(item, 1, false, true);
			}
		}

	}

}
