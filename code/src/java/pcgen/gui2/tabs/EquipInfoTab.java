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

import static pcgen.gui2.tabs.equip.EquipmentSelection.equipmentArrayFlavor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import pcgen.core.BodyStructure;
import pcgen.facade.core.BodyStructureFacade;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.EquipmentSetFacade;
import pcgen.facade.core.EquipmentSetFacade.EquipNode;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.filter.DisplayableFilter;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.equip.EquipmentModel;
import pcgen.gui2.tabs.equip.EquipmentModels;
import pcgen.gui2.tabs.equip.EquipmentModels.EquipView;
import pcgen.gui2.tabs.equip.EquipmentSelection;
import pcgen.gui2.tabs.models.CharacterComboBoxModel;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.tools.PrefTableColumnModel;
import pcgen.gui2.util.FontManipulation;
import pcgen.gui2.util.JDynamicTable;
import pcgen.gui2.util.JTreeTable;
import pcgen.gui2.util.event.PopupMouseAdapter;
import pcgen.gui2.util.table.DynamicTableColumnModel;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Tab;

/**
 * EquipInfoTab is a character tab for managing where gear is distributed for a
 * character. Each set of distribution information is called an EquipSet.
 * Multiple EquipSets can be managed to reflect different configurations.
 *
 * <br>
 *
 * @author Connor Petty &lt;cpmeister@users.sourceforge.net&gt;
 */
@SuppressWarnings("serial")
public class EquipInfoTab extends FlippingSplitPane implements CharacterInfoTab, TodoHandler
{

	private static final DataFlavor equipNodeArrayFlavor = new DataFlavor(
			DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" //$NON-NLS-1$
			+ EquipNode[].class.getName() + "\"", null); //$NON-NLS-1$
	private final JDynamicTable equipmentTable;
	private final JComboBox equipViewBox;
	private final JTreeTable equipmentSetTable;
	private final InfoPane infoPane;
	private final JButton unequipButton;
	private final JButton unequipAllButton;
	private final JButton moveUpButton;
	private final JButton moveDownButton;
	private final JButton equipButton;
	private final JComboBox equipSetBox;
	private final JButton newSetButton;
	private final JButton removeSetButton;
	private final JButton exportTemplateButton;
	private final JButton viewBrowserButton;
	private final JButton exportFileButton;
	private final JButton setNoteButton;
	private final JButton expandAllButton;
	private final JButton collapseAllButton;
	private final JLabel weightLabel;
	private final JLabel loadLabel;
	private final JLabel limitLabel;
	private DisplayableFilter<Object, Object> tableFilter;

	public EquipInfoTab()
	{
		super("Equip");
		//TODO: remove this when optimized sorting is implemented
		this.equipmentTable = new JDynamicTable()
		{

			@Override
			public void setModel(TableModel dataModel)
			{
				RowSorter<? extends TableModel> oldRowSorter = getRowSorter();
				super.setModel(dataModel);
				RowSorter<? extends TableModel> newRowSorter = getRowSorter();
				if (newRowSorter != null && oldRowSorter != null)
				{
					newRowSorter.setSortKeys(oldRowSorter.getSortKeys());
				}
			}

		};
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
		this.moveUpButton = new JButton();
		this.moveDownButton = new JButton();
		this.equipSetBox = new JComboBox();
		this.newSetButton = new JButton();
		this.removeSetButton = new JButton();
		this.exportTemplateButton = new JButton();
		this.viewBrowserButton = new JButton();
		this.exportFileButton = new JButton();
		this.setNoteButton = new JButton();
		this.expandAllButton = new JButton();
		this.collapseAllButton = new JButton();
		this.weightLabel = new JLabel();
		this.loadLabel = new JLabel();
		this.limitLabel = new JLabel();
		initComponents();
	}

	private void initComponents()
	{
		FontManipulation.small(newSetButton);
		newSetButton.setMargin(new Insets(0, 0, 0, 0));
		FontManipulation.small(removeSetButton);
		removeSetButton.setMargin(new Insets(0, 0, 0, 0));

		exportTemplateButton.setText(LanguageBundle.getString("in_equipExportTemplate")); //$NON-NLS-1$
		viewBrowserButton.setText(LanguageBundle.getString("in_equipViewBrowser")); //$NON-NLS-1$
		exportFileButton.setText(LanguageBundle.getString("in_equipExportFile")); //$NON-NLS-1$
		setNoteButton.setText(LanguageBundle.getString("in_equipSetNote")); //$NON-NLS-1$

		setOrientation(HORIZONTAL_SPLIT);
		FlippingSplitPane splitPane = new FlippingSplitPane(VERTICAL_SPLIT, "EquipMain");

		JPanel panel = new JPanel(new BorderLayout());

		Box bar = Box.createHorizontalBox();
		bar.add(Box.createHorizontalStrut(5));
		bar.add(new JLabel(LanguageBundle.getString("in_equipView"))); //$NON-NLS-1$
		bar.add(Box.createHorizontalStrut(5));
		bar.add(equipViewBox);
		bar.add(Box.createHorizontalStrut(5));
		tableFilter = new SearchFilterPanel();
		bar.add(tableFilter.getFilterComponent());
		bar.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
		panel.add(bar, BorderLayout.NORTH);

		equipmentTable.setAutoCreateColumnsFromModel(false);
		equipmentTable.setColumnModel(createEquipmentColumnModel());
		equipmentTable.setAutoCreateRowSorter(true);
		panel.add(new JScrollPane(equipmentTable), BorderLayout.CENTER);

		Box buttonsBox = Box.createHorizontalBox();
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
		box.add(new JLabel(LanguageBundle.getString("in_equipSetLabel"))); //$NON-NLS-1$
		box.add(Box.createHorizontalStrut(3));
		box.add(equipSetBox);
		box.add(Box.createHorizontalStrut(3));
		box.add(newSetButton);
		box.add(Box.createHorizontalStrut(3));
		box.add(removeSetButton);
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel(LanguageBundle.getString("in_equipWeightLabel"))); //$NON-NLS-1$
		box.add(Box.createHorizontalStrut(5));
		box.add(weightLabel);
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel(LanguageBundle.getString("in_equipLoadLabel"))); //$NON-NLS-1$
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
		exportTemplateButton.setEnabled(false);
		box.add(Box.createHorizontalStrut(3));
		box.add(viewBrowserButton);
		viewBrowserButton.setEnabled(false);
		box.add(Box.createHorizontalStrut(3));
		box.add(exportFileButton);
		exportFileButton.setEnabled(false);
		box.add(Box.createHorizontalStrut(3));
		box.add(setNoteButton);
		setNoteButton.setEnabled(false);
		box.add(Box.createHorizontalStrut(3));
		box.add(expandAllButton);
		box.add(Box.createHorizontalStrut(3));
		box.add(collapseAllButton);
		equipPane.add(box);
		equipPane.add(Box.createVerticalStrut(3));

		panel.add(equipPane, BorderLayout.NORTH);

		EquipmentModel.initializeTreeTable(equipmentSetTable);
		panel.add(new JScrollPane(equipmentSetTable), BorderLayout.CENTER);

		Box selPanelbuttonsBox = Box.createHorizontalBox();
		selPanelbuttonsBox.add(Box.createHorizontalStrut(3));
		selPanelbuttonsBox.add(unequipButton);
		selPanelbuttonsBox.add(Box.createHorizontalStrut(3));
		selPanelbuttonsBox.add(unequipAllButton);
		selPanelbuttonsBox.add(Box.createHorizontalStrut(3));
		selPanelbuttonsBox.add(moveUpButton);
		selPanelbuttonsBox.add(Box.createHorizontalStrut(3));
		selPanelbuttonsBox.add(moveDownButton);
		selPanelbuttonsBox.add(Box.createHorizontalGlue());
		selPanelbuttonsBox.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		panel.add(selPanelbuttonsBox, BorderLayout.SOUTH);
		setRightComponent(panel);
	}

	private DynamicTableColumnModel createEquipmentColumnModel()
	{
		PrefTableColumnModel model = new PrefTableColumnModel("EquipList", 1);

		TableColumn column = new TableColumn(0);
		column.setHeaderValue(LanguageBundle.getString("in_nameLabel")); //$NON-NLS-1$
		model.addColumn(column, true, 150);
		column = new TableColumn(1);
		column.setHeaderValue(LanguageBundle.getString("in_type")); //$NON-NLS-1$
		model.addColumn(column, true, 75);
		column = new TableColumn(2);
		column.setHeaderValue(LanguageBundle.getString("in_equipLocationAbbrev")); //$NON-NLS-1$
		model.addColumn(column, true, 75);
		column = new TableColumn(3);
		column.setHeaderValue(LanguageBundle.getString("in_equipQuantityAbbrev")); //$NON-NLS-1$
		model.addColumn(column, true, 75);
		column = new TableColumn(4);
		column.setHeaderValue(LanguageBundle.getString("in_equipWeightAbbrev")); //$NON-NLS-1$
		model.addColumn(column, true, 75);
		column = new TableColumn(5);
		column.setHeaderValue(LanguageBundle.getString("in_descrip")); //$NON-NLS-1$
		model.addColumn(column, false, 75);
		return model;
	}

	@Override
	public ModelMap createModels(CharacterFacade character)
	{
		ModelMap models = new ModelMap();
		models.put(EquipmentModel.class, new EquipmentModel(character, equipmentSetTable));
		models.put(EquipmentModels.class, new EquipmentModels(character,
				equipViewBox, equipmentTable, tableFilter, equipmentSetTable,
				equipButton, unequipButton, moveUpButton, moveDownButton));
		models.put(UnequipAllAction.class, new UnequipAllAction(character));
		models.put(EquipSetBoxModel.class, new EquipSetBoxModel(character));
		models.put(AddSetAction.class, new AddSetAction(character));
		models.put(RemoveSetAction.class, new RemoveSetAction(character));
		models.put(LabelsUpdater.class, new LabelsUpdater(character));
		models.put(EquipInfoHandler.class, new EquipInfoHandler(character));
		models.put(EquipmentRenderer.class, new EquipmentRenderer(character));
		models.put(EquipmentTransferHandler.class, new EquipmentTransferHandler(character));
		models.put(EquipmentSetTransferHandler.class, new EquipmentSetTransferHandler(character));
		models.put(OrderPopupMenuHandler.class, new OrderPopupMenuHandler(character));
		models.put(ExpandAllAction.class, new ExpandAllAction());
		models.put(CollapseAllAction.class, new CollapseAllAction());
		return models;
	}

	@Override
	public void restoreModels(ModelMap models)
	{
		models.get(EquipmentModel.class).install();
		models.get(EquipmentModels.class).install();
		models.get(LabelsUpdater.class).install();
		models.get(EquipInfoHandler.class).install();
		models.get(EquipmentRenderer.class).install();
		models.get(EquipmentTransferHandler.class).install();
		models.get(EquipmentSetTransferHandler.class).install();
		models.get(OrderPopupMenuHandler.class).install();
		unequipAllButton.setAction(models.get(UnequipAllAction.class));
		newSetButton.setAction(models.get(AddSetAction.class));
		removeSetButton.setAction(models.get(RemoveSetAction.class));
		expandAllButton.setAction(models.get(ExpandAllAction.class));
		collapseAllButton.setAction(models.get(CollapseAllAction.class));
		equipSetBox.setModel(models.get(EquipSetBoxModel.class));
	}

	@Override
	public void storeModels(ModelMap models)
	{
		models.get(LabelsUpdater.class).uninstall();
		models.get(EquipmentModel.class).uninstall();
		models.get(EquipmentModels.class).uninstall();
		models.get(EquipInfoHandler.class).uninstall();
		models.get(OrderPopupMenuHandler.class).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return new TabTitle(Tab.EQUIPPING);
	}

	private List<Integer> getMenuTargets(JTable table, MouseEvent e)
	{
		int row = table.rowAtPoint(e.getPoint());
		if (!table.isRowSelected(row))
		{
			table.setRowSelectionInterval(row, row);
		}
		List<Integer> targets = new ArrayList<>();
		for (int selRow : table.getSelectedRows())
		{
			targets.add(selRow);
		}
		return targets;
	}

	/**
	 */
	public void setLoadLabel(String text)
	{
		// bold / highlight text based on encumbrance value
		Font font = loadLabel.getFont();
		Color color = UIPropertyContext.getQualifiedColor();
		Load encumbrance = Load.getLoadType(text);

		switch (encumbrance)
		{
			case MEDIUM:
				font = FontManipulation.bold(font);
				color = UIPropertyContext.getAutomaticColor();
				break;
			case HEAVY:
				font = FontManipulation.bold_italic(font);
				color = UIPropertyContext.getVirtualColor();
				break;
			case OVERLOAD:
				font = FontManipulation.bold_italic(font);
				color = UIPropertyContext.getNotQualifiedColor();
				break;

			default:
				font = FontManipulation.plain(font);
		}

		loadLabel.setText(text);
		loadLabel.setFont(font);
		loadLabel.setForeground(color);
	}

	@Override
	public void adviseTodo(String fieldName)
	{
		// We don't provide further advice at this time. 
	}

	private class AddSetAction extends AbstractAction
	{

		private final CharacterFacade character;

		public AddSetAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_new")); //$NON-NLS-1$
			this.character = character;
		}

		@Override
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
			super(LanguageBundle.getString("in_remove")); //$NON-NLS-1$
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.deleteEquipmentSet(character.getEquipmentSetRef().get());
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

		@Override
		public void setSelectedItem(Object anItem)
		{
			character.setEquipmentSet((EquipmentSetFacade) anItem);
		}

	}

	private class ExpandAllAction extends AbstractAction
	{

		public ExpandAllAction()
		{
			super("+"); //$NON-NLS-1$
			putValue(SHORT_DESCRIPTION, LanguageBundle.getString("in_expandAllTip")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			JTree tree = equipmentSetTable.getTree();
			for (int i = 0; i < tree.getRowCount(); i++)
			{
				tree.expandRow(i);
			}
		}

	}

	private class CollapseAllAction extends AbstractAction
	{

		public CollapseAllAction()
		{
			super("-"); //$NON-NLS-1$
			putValue(SHORT_DESCRIPTION, LanguageBundle.getString("in_collapseAllTip")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			JTree tree = equipmentSetTable.getTree();
			for (int i = tree.getRowCount() - 1; i >= 0; i--)
			{
				tree.collapseRow(i);
			}
		}

	}

	private class UnequipAllAction extends AbstractAction
	{

		private final CharacterFacade character;

		public UnequipAllAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_equipUnequipAll")); //$NON-NLS-1$
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{

			int ret
					= JOptionPane.showConfirmDialog(EquipInfoTab.this,
							LanguageBundle.getString("in_equipUnequipConfirm"), //$NON-NLS-1$
							LanguageBundle.getString("in_areYouSure"), //$NON-NLS-1$
							JOptionPane.YES_NO_OPTION);
			if (ret == JOptionPane.YES_OPTION)
			{
				character.getEquipmentSetRef().get().removeAllEquipment();
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
			weightLabel.setText(weightRef.get());
			setLoadLabel(loadRef.get());
			limitLabel.setText(limitRef.get());

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

		@Override
		public void referenceChanged(ReferenceEvent<String> e)
		{
			Object source = e.getSource();
			if (source == weightRef)
			{
				weightLabel.setText(e.getNewReference());
			}
			else if (source == loadRef)
			{
				setLoadLabel(e.getNewReference());
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
		private String text;

		public EquipInfoHandler(CharacterFacade character)
		{
			this.character = character;
			this.text = ""; //$NON-NLS-1$
		}

		public void install()
		{
			equipmentTable.getSelectionModel().addListSelectionListener(this);
			equipmentSetTable.getSelectionModel().addListSelectionListener(this);
			infoPane.setText(text);
		}

		public void uninstall()
		{
			equipmentTable.getSelectionModel().removeListSelectionListener(this);
			equipmentSetTable.getSelectionModel().removeListSelectionListener(this);
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			JTable target = equipmentTable;
			if (equipmentSetTable.getSelectionModel().equals(e.getSource()))
			{
				target = equipmentSetTable;
			}
			if (!e.getValueIsAdjusting())
			{
				int selectedRows[] = target.getSelectedRows();
				StringBuilder sb = new StringBuilder(2000);
				for (int row : selectedRows)
				{
					EquipmentFacade equip = null;
					if (row != -1)
					{
						Object value = target.getModel().getValueAt(row, 0);
						if (value instanceof EquipmentFacade)
						{
							equip = (EquipmentFacade) value;
						}
						else if (value instanceof EquipNode)
						{
							equip = ((EquipNode) value).getEquipment();
						}
					}
					if (equip != null)
					{
						sb.append(character.getInfoFactory().getHTMLInfo(equip));
					}
				}
				text = "<html>" + sb.toString() + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$
				infoPane.setText(text);
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

		@Override
		public DataFlavor[] getTransferDataFlavors()
		{
			return FLAVORS;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor)
		{
			return flavor == FLAVORS[0] || flavor == FLAVORS[1];
		}

		@Override
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
			catch (UnsupportedFlavorException | IOException ex)
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
			EquipmentSetFacade equipSet = character.getEquipmentSetRef().get();
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
			catch (UnsupportedFlavorException | IOException ex)
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
			catch (UnsupportedFlavorException | IOException ex)
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
			if (node == null)
			{
				return false;
			}
			if (location.isInsertRow())
			{
				node = node.getParent();
			}
			EquipmentSetFacade equipSet = character.getEquipmentSetRef().get();

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
			EquipNode beforeNode = null;
			if (location.isInsertRow())
			{
				beforeNode = node;
				node = node.getParent();
			}
			EquipmentSetFacade equipSet = character.getEquipmentSetRef().get();

			if (support.isDataFlavorSupported(equipNodeArrayFlavor))
			{
				EquipNode[] equipNodeArray = getEquipNodeArray(support);
				if (equipNodeArray == null)
				{
					return false;
				}
				for (EquipNode equipNode : equipNodeArray)
				{
					int quantity = equipSet.getQuantity(equipNode);
					equipSet.removeEquipment(equipNode, quantity);
					equipSet.addEquipment(node, equipNode.getEquipment(), quantity, beforeNode);
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
					equipSet.addEquipment(node, equipmentFacade, 1, beforeNode);
				}
			}
			return true;
		}

	}

	private class OrderPopupMenuHandler extends PopupMouseAdapter
	{

		private final CharacterFacade character;

		OrderPopupMenuHandler(CharacterFacade character)
		{
			this.character = character;
		}

		@Override
		public void showPopup(MouseEvent e)
		{
			List<Integer> targets = getMenuTargets(equipmentSetTable, e);
			if (targets.isEmpty())
			{
				return;
			}

			List<EquipNode> upTargets = new ArrayList<>();
			List<EquipNode> downTargets = new ArrayList<>();
			List<EquipNode> sortTargets = new ArrayList<>();
			EquipNode[] relativeNodes = filterTargets(targets, upTargets, downTargets, sortTargets);

			JPopupMenu popupMenu = new JPopupMenu();
			if (!upTargets.isEmpty() || !downTargets.isEmpty())
			{
				popupMenu.add(new MoveEquipUpMenuItem(character, upTargets, relativeNodes[0]));
				popupMenu.add(new MoveEquipDownMenuItem(character, downTargets, relativeNodes[1]));
			}
			if (!sortTargets.isEmpty())
			{
				if (!upTargets.isEmpty() || !downTargets.isEmpty())
				{
					popupMenu.addSeparator();

				}
				popupMenu.add(new SortEquipMenuItem(character, sortTargets));
			}
			if (popupMenu.getComponents().length > 0)
			{
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		private EquipNode[] filterTargets(List<Integer> targetRows,
				List<EquipNode> upTargets, List<EquipNode> downTargets,
				List<EquipNode> sortTargets)
		{
			EquipmentSetFacade equipSet = character.getEquipmentSetRef().get();
			TableModel equipSetModel = equipmentSetTable.getModel();
			int beforeRow = equipSetModel.getRowCount();
			int afterRow = 0;
			for (Integer selRow : targetRows)
			{
				Object value = equipSetModel.getValueAt(selRow, 0);
				if (!(value instanceof EquipNode))
				{
					continue;
				}
				EquipNode equipNode = (EquipNode) value;
				if (!isInGeneralBodyStructure(equipNode))
				{
					continue;
				}

				switch (equipNode.getNodeType())
				{
					case BODY_SLOT:
						sortTargets.add(equipNode);
						break;

					case EQUIPMENT:
						// Check node is not the top item in the parent
						upTargets.add(equipNode);
						downTargets.add(equipNode);
						// Check item is a container
						if (equipSet.isContainer(equipNode.getEquipment()))
						{
							sortTargets.add(equipNode);
						}
						break;

					default:
						break;
				}
			}

			// Set the before and after nodes
			EquipNode[] relativeNodes = new EquipNode[]
			{
				null, null
			};
			if (beforeRow >= 0)
			{
				relativeNodes[0]
						= (EquipNode) equipSetModel.getValueAt(beforeRow, 0);
			}
			if (afterRow < equipSetModel.getRowCount())
			{
				relativeNodes[1]
						= (EquipNode) equipSetModel.getValueAt(afterRow, 0);
			}
			return relativeNodes;
		}

		/**
		 * Identify if the EquipNode is within one of the general body
		 * structures (equipped, carried and not carried)
		 *
		 * @param equipNode The node to check.
		 * @return true if it is in a general body structure.
		 */
		private boolean isInGeneralBodyStructure(EquipNode equipNode)
		{
			BodyStructureFacade bodyStructure
					= equipNode.getBodyStructure();
			return (bodyStructure instanceof BodyStructure
					&& ((BodyStructure) bodyStructure).getEquipSlots()
					.isEmpty());
		}

		public void install()
		{
			equipmentSetTable.addMouseListener(this);
		}

		public void uninstall()
		{
			equipmentSetTable.removeMouseListener(this);
		}

	}

	/**
	 * Menu item for moving the selected equipment up a step in their container.
	 */
	private class MoveEquipUpMenuItem extends JMenuItem implements ActionListener
	{

		private final CharacterFacade character;
		private final List<EquipNode> targets;
		private final EquipNode beforeNode;

		MoveEquipUpMenuItem(CharacterFacade character, List<EquipNode> targets, EquipNode beforeNode)
		{
			super(LanguageBundle.getString("in_equipMoveUpMenuCommand")); //$NON-NLS-1$ 
			this.character = character;
			this.targets = targets;
			this.beforeNode = beforeNode;
			setToolTipText(LanguageBundle.getString("in_equipMoveUpMenuDesc")); //$NON-NLS-1$
			setIcon(Icons.Up16.getImageIcon());
			setEnabled(!targets.isEmpty());

			addActionListener(this);
		}

		/**
		 * Action to move the current item up in the current container
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			EquipmentSetFacade equipSet
					= character.getEquipmentSetRef().get();
			for (EquipNode equipNode : targets)
			{
				equipSet.moveEquipment(equipNode, -1);
			}
		}

	}

	/**
	 * Menu item for moving the selected equipment up a step in their container.
	 */
	private class MoveEquipDownMenuItem extends JMenuItem implements ActionListener
	{

		private final CharacterFacade character;
		private final List<EquipNode> targets;
		private final EquipNode afterNode;

		MoveEquipDownMenuItem(CharacterFacade character, List<EquipNode> targets, EquipNode beforeNode)
		{
			super(LanguageBundle.getString("in_equipMoveDownMenuCommand")); //$NON-NLS-1$ 
			this.character = character;
			this.targets = targets;
			this.afterNode = beforeNode;
			setToolTipText(LanguageBundle.getString("in_equipMoveDownMenuDesc")); //$NON-NLS-1$
			setIcon(Icons.Down16.getImageIcon());
			setEnabled(!targets.isEmpty());

			addActionListener(this);
		}

		/**
		 * Action to move the current item up in the current container
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			EquipmentSetFacade equipSet
					= character.getEquipmentSetRef().get();
			for (EquipNode equipNode : targets)
			{
				equipSet.moveEquipment(equipNode, 1);
			}
		}

	}

	/**
	 * Menu item for moving the selected equipment up a step in their container.
	 */
	private class SortEquipMenuItem extends JMenuItem implements ActionListener
	{

		private final CharacterFacade character;
		private final List<EquipNode> targets;

		SortEquipMenuItem(CharacterFacade character, List<EquipNode> targets)
		{
			super(LanguageBundle.getString("in_equipSortAscMenuCommand")); //$NON-NLS-1$ 
			this.character = character;
			this.targets = targets;
			setToolTipText(LanguageBundle.getString("in_equipSortAscMenuDesc")); //$NON-NLS-1$
			setIcon(Icons.FForward16.getImageIcon());
			setEnabled(!targets.isEmpty());

			addActionListener(this);
		}

		/**
		 * Action to move the current item up in the current container
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			EquipmentSetFacade equipSet
					= character.getEquipmentSetRef().get();
			for (EquipNode equipNode : targets)
			{
				equipSet.sortEquipment(equipNode);
			}
		}

	}

}
