/*
 * EquipInfoTab.java
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jul 6, 2010, 5:08:49 PM
 */
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.EquipmentFacade;
import pcgen.core.facade.EquipmentSetFacade;
import pcgen.core.facade.EquipmentSetFacade.EquipNode;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.filter.DisplayableFilter;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.equip.EquipmentModel;
import pcgen.gui2.tabs.equip.EquipmentModels;
import pcgen.gui2.tabs.equip.EquipmentModels.EquipView;
import pcgen.gui2.tabs.equip.EquipmentSelection;
import static pcgen.gui2.tabs.equip.EquipmentSelection.equipmentArrayFlavor;
import pcgen.gui2.tabs.models.CharacterComboBoxModel;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.JDynamicTable;
import pcgen.gui2.util.JTreeTable;
import pcgen.gui2.util.SortMode;
import pcgen.gui2.util.SortingPriority;
import pcgen.gui2.util.table.DefaultDynamicTableColumnModel;
import pcgen.gui2.util.table.DynamicTableColumnModel;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class EquipInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	private static final DataFlavor equipNodeArrayFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
			+ ";class=\"" + EquipNode[].class.getName() + "\"", null);
	private static final Font labelFont = new Font("Verdana", Font.BOLD, 12);
	private static final Font textFont = new Font("Verdana", Font.PLAIN, 12);
	private static final Font smallFont = new Font("Verdana", Font.PLAIN, 10);
	private final JDynamicTable equipmentTable;
	private final JComboBox equipViewBox;
	private final JTreeTable equipmentSetTable;
	private final InfoPane infoPane;
	private final JButton unequipButton;
	private final JButton unequipAllButton;
	private final JButton equipButton;
	private final JComboBox equipSetBox;
	private final JButton newSetButton;
	private final JButton removeSetButton;
	private final JButton exportTemplateButton;
	private final JButton viewBrowserButton;
	private final JButton exportFileButton;
	private final JButton setNoteButton;
	private final JLabel weightLabel;
	private final JLabel loadLabel;
	private final JLabel limitLabel;
	private DisplayableFilter tableFilter;

	public EquipInfoTab()
	{
		this.equipmentTable = new JDynamicTable();
		this.equipViewBox = new JComboBox(EquipView.values());
		this.infoPane = new InfoPane();
		this.equipmentSetTable = new JTreeTable()
		{

			@Override
			protected void configureEnclosingScrollPane()
			{
				//We do nothing so the table is displayed without a header
			}

		};
		this.equipButton = new JButton();
		this.unequipButton = new JButton();
		this.unequipAllButton = new JButton();
		this.equipSetBox = new JComboBox();
		this.newSetButton = new JButton();
		this.removeSetButton = new JButton();
		this.exportTemplateButton = new JButton();
		this.viewBrowserButton = new JButton();
		this.exportFileButton = new JButton();
		this.setNoteButton = new JButton();
		this.weightLabel = new JLabel();
		this.loadLabel = new JLabel();
		this.limitLabel = new JLabel();
		initComponents();
	}

	private void initComponents()
	{
		newSetButton.setFont(smallFont);
		newSetButton.setMargin(new Insets(0, 0, 0, 0));
		removeSetButton.setFont(smallFont);
		removeSetButton.setMargin(new Insets(0, 0, 0, 0));

		exportTemplateButton.setText("Export Template");
		viewBrowserButton.setText("View in Browser");
		exportFileButton.setText("Export to File");
		setNoteButton.setText("Set Note");


		setOrientation(HORIZONTAL_SPLIT);
		FlippingSplitPane splitPane = new FlippingSplitPane(VERTICAL_SPLIT);

		JPanel panel = new JPanel(new BorderLayout());

		Box bar = Box.createHorizontalBox();
		bar.add(Box.createHorizontalStrut(5));
		bar.add(new JLabel("Equip View"));
		bar.add(Box.createHorizontalStrut(5));
		bar.add(equipViewBox);
		bar.add(Box.createHorizontalStrut(5));
		tableFilter = new SearchFilterPanel();
		bar.add(tableFilter.getFilterComponent());
		bar.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
		panel.add(bar, BorderLayout.NORTH);

		equipmentTable.setAutoCreateColumnsFromModel(false);
		equipmentTable.setColumnModel(createEquipmentColumnModel());
		equipmentTable.setSortingPriority(Collections.singletonList(new SortingPriority(0, SortMode.ASCENDING)));
		equipmentTable.sortModel();
		panel.add(new JScrollPane(equipmentTable), BorderLayout.CENTER);

		Box buttonsBox = Box.createHorizontalBox();
		buttonsBox.add(unequipButton);
		buttonsBox.add(Box.createHorizontalStrut(3));
		buttonsBox.add(unequipAllButton);
		buttonsBox.add(Box.createHorizontalGlue());
		equipButton.setHorizontalTextPosition(SwingConstants.LEADING);
		buttonsBox.add(equipButton);
		buttonsBox.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		panel.add(buttonsBox, BorderLayout.SOUTH);

		splitPane.setTopComponent(panel);
		splitPane.setBottomComponent(infoPane);

		setLeftComponent(splitPane);

		panel = new JPanel(new BorderLayout());

		Box equipPane = Box.createVerticalBox();
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel("Equip Set:"));
		box.add(Box.createHorizontalStrut(3));
		box.add(equipSetBox);
		box.add(Box.createHorizontalStrut(3));
		box.add(newSetButton);
		box.add(Box.createHorizontalStrut(3));
		box.add(removeSetButton);
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel("Weight:"));
		box.add(Box.createHorizontalStrut(5));
		box.add(weightLabel);
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel("Load:"));
		box.add(Box.createHorizontalStrut(5));
		box.add(loadLabel);
//		box.add(Box.createHorizontalGlue());
//		box.add(new JLabel("Limit:"));
		box.add(Box.createHorizontalStrut(5));
		box.add(limitLabel);
		box.add(Box.createHorizontalGlue());

		equipPane.add(Box.createVerticalStrut(3));
		equipPane.add(box);
		equipPane.add(Box.createVerticalStrut(3));

		box = Box.createHorizontalBox();
		box.add(exportTemplateButton);
		box.add(Box.createHorizontalStrut(3));
		box.add(viewBrowserButton);
		box.add(Box.createHorizontalStrut(3));
		box.add(exportFileButton);
		box.add(Box.createHorizontalStrut(3));
		box.add(setNoteButton);
		equipPane.add(box);
		equipPane.add(Box.createVerticalStrut(3));

		panel.add(equipPane, BorderLayout.NORTH);

		EquipmentModel.initializeTreeTable(equipmentSetTable);
		panel.add(new JScrollPane(equipmentSetTable), BorderLayout.CENTER);
		setRightComponent(panel);
	}

	private DynamicTableColumnModel createEquipmentColumnModel()
	{
		DefaultDynamicTableColumnModel model = new DefaultDynamicTableColumnModel(1);
		TableColumn column = new TableColumn(0);
		column.setHeaderValue("Name");
		model.addColumn(column);
		column = new TableColumn(1);
		column.setHeaderValue("Type");
		model.addColumn(column);
		model.setVisible(column, true);
		column = new TableColumn(2);
		column.setHeaderValue("Loc");
		model.addColumn(column);
		model.setVisible(column, true);
		column = new TableColumn(3);
		column.setHeaderValue("Qty");
		model.addColumn(column);
		model.setVisible(column, true);
		column = new TableColumn(4);
		column.setHeaderValue("Wgt");
		model.addColumn(column);
		model.setVisible(column, true);
		return model;
	}

	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(EquipmentModel.class, new EquipmentModel(character));
		state.put(EquipmentModels.class, new EquipmentModels(character));
		state.put(UnequipAllAction.class, new UnequipAllAction(character));
		state.put(EquipSetBoxModel.class, new EquipSetBoxModel(character));
		state.put(AddSetAction.class, new AddSetAction(character));
		state.put(RemoveSetAction.class, new RemoveSetAction(character));
		state.put(LabelsUpdater.class, new LabelsUpdater(character));
		state.put(EquipInfoHandler.class, new EquipInfoHandler(character));
		state.put(EquipmentRenderer.class, new EquipmentRenderer(character));
		state.put(EquipmentTransferHandler.class, new EquipmentTransferHandler(character));
		state.put(EquipmentSetTransferHandler.class, new EquipmentSetTransferHandler(character));
		return state;
	}

	public void restoreModels(Hashtable<?, ?> state)
	{
		((EquipmentModel) state.get(EquipmentModel.class)).install(equipmentSetTable);
		((EquipmentModels) state.get(EquipmentModels.class)).install(equipViewBox, equipmentTable,
																	 tableFilter, equipmentSetTable,
																	 equipButton, unequipButton);
		((LabelsUpdater) state.get(LabelsUpdater.class)).install();
		((EquipInfoHandler) state.get(EquipInfoHandler.class)).install();
		((EquipmentRenderer) state.get(EquipmentRenderer.class)).install();
		((EquipmentTransferHandler) state.get(EquipmentTransferHandler.class)).install();
		((EquipmentSetTransferHandler) state.get(EquipmentSetTransferHandler.class)).install();
		unequipAllButton.setAction((UnequipAllAction) state.get(UnequipAllAction.class));
		newSetButton.setAction((AddSetAction) state.get(AddSetAction.class));
		removeSetButton.setAction((RemoveSetAction) state.get(RemoveSetAction.class));
		equipSetBox.setModel((EquipSetBoxModel) state.get(EquipSetBoxModel.class));
	}

	public void storeModels(Hashtable<Object, Object> state)
	{
		((LabelsUpdater) state.get(LabelsUpdater.class)).uninstall();
		((EquipmentModel) state.get(EquipmentModel.class)).uninstall();
		((EquipmentModels) state.get(EquipmentModels.class)).uninstall();
		((EquipInfoHandler) state.get(EquipInfoHandler.class)).uninstall();
	}

	public TabTitle getTabTitle()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private class AddSetAction extends AbstractAction
	{

		private final CharacterFacade character;

		public AddSetAction(CharacterFacade character)
		{
			super("New");
			this.character = character;
		}

		public void actionPerformed(ActionEvent e)
		{
			String name = JOptionPane.showInputDialog(JOptionPane.getFrameForComponent(EquipInfoTab.this), "Name of new set");
			if (StringUtils.isNotEmpty(name))
			{
				character.setEquipmentSet(character.createEquipmentSet(name));

			}
		}

	}

	private class RemoveSetAction extends AbstractAction
	{

		private final CharacterFacade character;

		public RemoveSetAction(CharacterFacade character)
		{
			super("Remove");
			this.character = character;
		}

		public void actionPerformed(ActionEvent e)
		{
			character.deleteEquipmentSet(character.getEquipmentSetRef().getReference());
		}

	}

	private class EquipSetBoxModel extends CharacterComboBoxModel<EquipmentSetFacade>
	{

		private final CharacterFacade character;

		public EquipSetBoxModel(CharacterFacade character)
		{
			this.character = character;
			setListFacade(character.getEquipmentSets());
			setReference(character.getEquipmentSetRef());
		}

		public void setSelectedItem(Object anItem)
		{
			character.setEquipmentSet((EquipmentSetFacade) anItem);
		}

	}

	private class UnequipAllAction extends AbstractAction
	{

		private final CharacterFacade character;

		public UnequipAllAction(CharacterFacade character)
		{
			super("Unequip All");
			this.character = character;
		}

		public void actionPerformed(ActionEvent e)
		{

			int ret = JOptionPane.showConfirmDialog(EquipInfoTab.this,
													"All items will be removed from your character",
													"Are you sure?", JOptionPane.YES_NO_OPTION);
			if (ret == JOptionPane.YES_OPTION)
			{
				character.getEquipmentSetRef().getReference().removeAllEquipment();
			}
		}

	}

	private class LabelsUpdater implements ReferenceListener<String>
	{

		private final ReferenceFacade<String> weightRef;
		private final ReferenceFacade<String> loadRef;
		private final ReferenceFacade<String> limitRef;

		public LabelsUpdater(CharacterFacade character)
		{
			weightRef = character.getCarriedWeightRef();
			loadRef = character.getLoadRef();
			limitRef = character.getWeightLimitRef();
		}

		public void install()
		{
			weightLabel.setText(weightRef.getReference());
			loadLabel.setText(loadRef.getReference());
			limitLabel.setText(limitRef.getReference());

			weightRef.addReferenceListener(this);
			loadRef.addReferenceListener(this);
			limitRef.addReferenceListener(this);
		}

		public void uninstall()
		{
			weightRef.removeReferenceListener(this);
			loadRef.removeReferenceListener(this);
			limitRef.removeReferenceListener(this);
		}

		public void referenceChanged(ReferenceEvent<String> e)
		{
			Object source = e.getSource();
			if (source == weightRef)
			{
				weightLabel.setText(e.getNewReference());
			}
			else if (source == loadRef)
			{
				loadLabel.setText(e.getNewReference());
			}
			else
			{
				limitLabel.setText(e.getNewReference());
			}
		}

	}

	private class EquipInfoHandler implements ListSelectionListener
	{

		private CharacterFacade character;

		public EquipInfoHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			equipmentTable.getSelectionModel().addListSelectionListener(this);
		}

		public void uninstall()
		{
			equipmentTable.getSelectionModel().removeListSelectionListener(this);
		}

		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				int selectedRows[] = equipmentTable.getSelectedRows();
				StringBuilder sb = new StringBuilder(2000);
				for (int row : selectedRows)
				{
					EquipmentFacade equip = null;
					if (row != -1)
					{
						Object value = equipmentTable.getModel().getValueAt(row, 0);
						if (value instanceof EquipmentFacade)
						{
							equip = (EquipmentFacade) value;
						}
					}
					if (equip != null)
					{
						sb.append(character.getInfoFactory().getHTMLInfo(equip));
					}
				}
				infoPane.setText("<html>" + sb.toString() + "</html>");
			}
		}

	}

	private class EquipmentRenderer extends DefaultTableCellRenderer
	{

		private CharacterFacade character;

		public EquipmentRenderer(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			equipmentTable.setDefaultRenderer(Object.class, this);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value instanceof EquipmentFacade
					&& !character.isQualifiedFor((EquipmentFacade) value))
			{
				setForeground(UIPropertyContext.getNotQualifiedColor());
			}
			else if (!isSelected)
			{
				setForeground(UIPropertyContext.getQualifiedColor());
			}
			return this;
		}

	}

	private static class EquipNodeSelection implements Transferable
	{

		private static DataFlavor[] FLAVORS = new DataFlavor[]
		{
			equipNodeArrayFlavor,
			equipmentArrayFlavor
		};
		private EquipNode[] nodeArray;

		public EquipNodeSelection(EquipNode[] nodeArray)
		{
			this.nodeArray = nodeArray;
		}

		public DataFlavor[] getTransferDataFlavors()
		{
			return FLAVORS;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor)
		{
			return flavor == FLAVORS[0] || flavor == FLAVORS[1];
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
		{
			if (flavor == equipNodeArrayFlavor)
			{
				return nodeArray;
			}
			if (flavor == equipmentArrayFlavor)
			{
				EquipmentFacade[] equipArray = new EquipmentFacade[nodeArray.length];
				for (int i = 0; i < equipArray.length; i++)
				{
					equipArray[i] = nodeArray[i].getEquipment();
				}
				return equipArray;
			}
			throw new UnsupportedFlavorException(flavor);
		}

	}

	private class EquipmentTransferHandler extends TransferHandler
	{

		private CharacterFacade character;

		public EquipmentTransferHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			equipmentTable.setDragEnabled(true);
			equipmentTable.setDropMode(DropMode.ON);
			equipmentTable.setTransferHandler(this);
		}

		@Override
		public int getSourceActions(JComponent c)
		{
			return MOVE;
		}

		@Override
		protected Transferable createTransferable(JComponent c)
		{
			if (c == equipmentTable)
			{
				int[] rows = equipmentTable.getSelectedRows();
				if (ArrayUtils.isEmpty(rows))
				{
					return null;
				}
				EquipmentFacade[] equipArray = new EquipmentFacade[rows.length];
				for (int i = 0; i < equipArray.length; i++)
				{
					equipArray[i] = (EquipmentFacade) equipmentTable.getModel().getValueAt(rows[i], 0);
				}
				return new EquipmentSelection(equipArray);
			}
			return super.createTransferable(c);
		}

		@Override
		public boolean canImport(TransferSupport support)
		{
			if (!support.isDataFlavorSupported(equipNodeArrayFlavor))
			{
				return false;
			}
			support.setShowDropLocation(false);
			return true;
		}

		private EquipNode[] getEquipNodeArray(TransferSupport support)
		{
			EquipNode[] equipNodeArray = null;
			try
			{
				equipNodeArray = (EquipNode[]) support.getTransferable().getTransferData(equipNodeArrayFlavor);
			}
			catch (UnsupportedFlavorException ex)
			{
				Logger.getLogger(EquipInfoTab.class.getName()).log(Level.SEVERE, null, ex);
			}
			catch (IOException ex)
			{
				Logger.getLogger(EquipInfoTab.class.getName()).log(Level.SEVERE, null, ex);
			}
			return equipNodeArray;
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
			EquipNode[] nodes = getEquipNodeArray(support);
			if (nodes == null)
			{
				return false;
			}
			EquipmentSetFacade equipSet = character.getEquipmentSetRef().getReference();
			for (EquipNode equipNode : nodes)
			{
				equipSet.removeEquipment(equipNode, 1);
			}
			return true;
		}

	}

	private class EquipmentSetTransferHandler extends TransferHandler
	{

		private CharacterFacade character;

		public EquipmentSetTransferHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			equipmentSetTable.setTransferHandler(this);
			equipmentSetTable.setDragEnabled(true);
			equipmentSetTable.setDropMode(DropMode.ON_OR_INSERT_ROWS);
		}

		@Override
		public int getSourceActions(JComponent c)
		{
			return MOVE;
		}

		@Override
		protected Transferable createTransferable(JComponent c)
		{
			if (c == equipmentSetTable)
			{
				int[] rows = equipmentSetTable.getSelectedRows();
				if (ArrayUtils.isEmpty(rows))
				{
					return null;
				}
				EquipNode[] nodeArray = new EquipNode[rows.length];
				for (int i = 0; i < nodeArray.length; i++)
				{
					nodeArray[i] = (EquipNode) equipmentSetTable.getModel().getValueAt(rows[i], 0);
				}
				return new EquipNodeSelection(nodeArray);
			}
			return super.createTransferable(c);
		}

		private EquipmentFacade[] getEquipmentArray(TransferSupport support)
		{
			EquipmentFacade[] equipmentArray = null;
			try
			{
				equipmentArray = (EquipmentFacade[]) support.getTransferable().getTransferData(equipmentArrayFlavor);
			}
			catch (UnsupportedFlavorException ex)
			{
				Logger.getLogger(EquipInfoTab.class.getName()).log(Level.SEVERE, null, ex);
			}
			catch (IOException ex)
			{
				Logger.getLogger(EquipInfoTab.class.getName()).log(Level.SEVERE, null, ex);
			}
			return equipmentArray;
		}

		private EquipNode[] getEquipNodeArray(TransferSupport support)
		{
			EquipNode[] equipNodeArray = null;
			try
			{
				equipNodeArray = (EquipNode[]) support.getTransferable().getTransferData(equipNodeArrayFlavor);
			}
			catch (UnsupportedFlavorException ex)
			{
				Logger.getLogger(EquipInfoTab.class.getName()).log(Level.SEVERE, null, ex);
			}
			catch (IOException ex)
			{
				Logger.getLogger(EquipInfoTab.class.getName()).log(Level.SEVERE, null, ex);
			}
			return equipNodeArray;
		}

		@Override
		public boolean canImport(TransferSupport support)
		{
			JTable.DropLocation location = (JTable.DropLocation) support.getDropLocation();
			int row = location.getRow();
			EquipNode node = (EquipNode) equipmentSetTable.getValueAt(row, 0);
			if (location.isInsertRow())
			{
				node = node.getParent();
			}
			EquipmentSetFacade equipSet = character.getEquipmentSetRef().getReference();

			if (support.isDataFlavorSupported(equipNodeArrayFlavor))
			{
				EquipNode[] equipNodeArray = getEquipNodeArray(support);
				if (equipNodeArray == null)
				{
					return false;
				}
				for (EquipNode equipNode : equipNodeArray)
				{
					if (!equipSet.canEquip(node, equipNode.getEquipment()))
					{
						return false;
					}
				}
				return true;
			}
			else if (support.isDataFlavorSupported(equipmentArrayFlavor))
			{
				EquipmentFacade[] equipmentArray = getEquipmentArray(support);
				if (equipmentArray == null)
				{
					return false;
				}
				for (EquipmentFacade equipmentFacade : equipmentArray)
				{
					if (!equipSet.canEquip(node, equipmentFacade))
					{
						return false;
					}
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean importData(TransferSupport support)
		{
			if (!canImport(support) || !support.isDrop())
			{
				return false;
			}

			JTable.DropLocation location = (JTable.DropLocation) support.getDropLocation();
			int row = location.getRow();
			EquipNode node = (EquipNode) equipmentSetTable.getValueAt(row, 0);
			if (location.isInsertRow())
			{
				node = node.getParent();
			}
			EquipmentSetFacade equipSet = character.getEquipmentSetRef().getReference();

			if (support.isDataFlavorSupported(equipNodeArrayFlavor))
			{
				EquipNode[] equipNodeArray = getEquipNodeArray(support);
				if (equipNodeArray == null)
				{
					return false;
				}
				for (EquipNode equipNode : equipNodeArray)
				{
					equipSet.removeEquipment(equipNode, 1);
					equipSet.addEquipment(node, equipNode.getEquipment(), 1);
				}
			}
			else if (support.isDataFlavorSupported(equipmentArrayFlavor))
			{
				EquipmentFacade[] equipmentArray = getEquipmentArray(support);
				if (equipmentArray == null)
				{
					return false;
				}
				for (EquipmentFacade equipmentFacade : equipmentArray)
				{
					equipSet.addEquipment(node, equipmentFacade, 1);
				}
			}
			return true;
		}

	}

}
