/*
 * Copyright James Dempsey, 2013
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
 */
package pcgen.gui2.equip;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.cdom.base.Constants;
import pcgen.core.EquipmentModifier;
import pcgen.core.SizeAdjustment;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.EquipmentBuilderFacade;
import pcgen.facade.core.EquipmentBuilderFacade.EquipmentHead;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilteredListFacade;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.models.CharacterComboBoxModel;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.FontManipulation;
import pcgen.gui2.util.TreeColumnCellRenderer;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code EquipCustomPanel} displays an available/selected table
 * pair to allow the creation of a custom piece of equipment.. 
 *
 * 
 */
public final class EquipCustomPanel extends FlippingSplitPane
{

	private final FilteredTreeViewTable<Object, EquipmentModifier> availableTable;
	private final FilteredTreeViewTable<Object, EquipmentModifier> selectedTable;
	private final JButton nameButton;
	private final JButton spropButton;
	private final JButton costButton;
	private final JButton weightButton;
	private final JButton damageButton;
	private final JComboBox<EquipmentHead> headCombo;
	private final JComboBox<SizeAdjustment> sizeCombo;
	private final JButton addButton;
	private final JButton removeButton;
	private final InfoPane equipModInfoPane;
	private final InfoPane equipInfoPane;
	private final CharacterFacade character;
	private final TreeColumnCellRenderer renderer;
	private final NameAction nameAction;
	private final SPropAction spropAction;
	private final CostAction costAction;
	private final WeightAction weightAction;
	private final DamageAction damageAction;
	private final AddEqmodAction addAction;
	private final RemoveEqmodAction removeAction;
	private final EquipmentBuilderFacade builder;
	private EquipInfoHandler equipInfoHandler;

	private final ListFacade<EquipmentHead> validHeads;
	private HeadBoxModel headBoxModel;
	private SizeBoxModel sizeBoxModel;
	private EquipmentHead currentHead = EquipmentHead.PRIMARY;
	private Map<EquipmentHead, EquipModTreeViewModel> availEqmodModelMap;
	private Map<EquipmentHead, EquipModTreeViewModel> selectedEqmodModelMap;

	/**
	 * Create a new instance of EquipCustomPanel for a character.
	 * @param character The character being displayed.
	 * @param builder The equipment builder to be used for creating the item.
	 */
	public EquipCustomPanel(CharacterFacade character, EquipmentBuilderFacade builder)
	{
		this.character = character;
		this.builder = builder;
		validHeads = new DefaultListFacade<>(builder.getEquipmentHeads());

		this.availableTable = new FilteredTreeViewTable<>();
		this.selectedTable = new FilteredTreeViewTable<>();
		this.nameButton = new JButton();
		this.spropButton = new JButton();
		this.costButton = new JButton();
		this.weightButton = new JButton();
		this.damageButton = new JButton();
		this.headCombo = new JComboBox<>();
		this.sizeCombo = new JComboBox<>();
		this.addButton = new JButton();
		this.removeButton = new JButton();
		this.equipModInfoPane = new InfoPane(LanguageBundle.getString("in_igEqModInfo")); //$NON-NLS-1$
		this.equipInfoPane = new InfoPane(LanguageBundle.getString("in_igEqInfo")); //$NON-NLS-1$
		this.renderer = new EquipQualifiedTreeCellRenderer(character, builder.getEquipment());

		this.nameAction = new NameAction();
		this.spropAction = new SPropAction();
		this.costAction = new CostAction();
		this.weightAction = new WeightAction();
		this.damageAction = new DamageAction();
		this.addAction = new AddEqmodAction();
		this.removeAction = new RemoveEqmodAction();

		initHeadMaps();
		initComponents();
		initDefaults();
	}

	/**
	 * Setup any data related to multiple equipment heads. 
	 */
	private void initHeadMaps()
	{
		availEqmodModelMap = new HashMap<>();
		selectedEqmodModelMap = new HashMap<>();

		for (EquipmentHead head : validHeads)
		{
			availEqmodModelMap.put(head, new EquipModTreeViewModel(builder, head, true));
			selectedEqmodModelMap.put(head, new EquipModTreeViewModel(builder, head, false));
		}
	}

	private void initComponents()
	{
		JPanel upperPanel = new JPanel(new BorderLayout());
		setTopComponent(upperPanel);
		setOrientation(VERTICAL_SPLIT);

		Box bannerBox = Box.createHorizontalBox();
		bannerBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		bannerBox.add(Box.createHorizontalGlue());
		JLabel baseItemLabel = new JLabel(LanguageBundle.getString("in_EqBuilder_BaseItem"));
		FontManipulation.large(baseItemLabel);
		bannerBox.add(baseItemLabel);
		bannerBox.add(Box.createHorizontalStrut(5));
		JLabel baseItemName = new JLabel(builder.getBaseItemName());
		FontManipulation.large(baseItemName);
		FontManipulation.title(baseItemName);
		bannerBox.add(baseItemName);
		if (validHeads.getSize() > 1)
		{
			bannerBox.add(Box.createHorizontalStrut(45));
			JLabel headLabel = new JLabel(LanguageBundle.getString("in_EqBuilder_Head"));
			FontManipulation.large(headLabel);
			bannerBox.add(headLabel);
			bannerBox.add(Box.createHorizontalStrut(5));
			Dimension prefDim = headCombo.getPreferredSize();
			prefDim.width += 15;
			headCombo.setMaximumSize(prefDim);
			bannerBox.add(headCombo);
		}
		bannerBox.add(Box.createHorizontalGlue());
		upperPanel.add(bannerBox, BorderLayout.NORTH);

		FlippingSplitPane topPane = new FlippingSplitPane();
		upperPanel.add(topPane, BorderLayout.CENTER);

		JPanel availPanel = new JPanel(new BorderLayout());
		FilterBar<Object, EquipmentModifier> bar = new FilterBar<>();
		bar.addDisplayableFilter(new SearchFilterPanel());
		availPanel.add(bar, BorderLayout.NORTH);

		availableTable.setDisplayableFilter(bar);
		availableTable.setTreeViewModel(availEqmodModelMap.get(currentHead));
		availableTable.setTreeCellRenderer(renderer);

		availPanel.add(new JScrollPane(availableTable), BorderLayout.CENTER);

		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		addButton.setHorizontalTextPosition(SwingConstants.LEADING);
		addButton.setAction(addAction);
		box.add(addButton);
		box.add(Box.createHorizontalStrut(5));
		box.setBorder(new EmptyBorder(0, 0, 5, 0));
		availPanel.add(box, BorderLayout.SOUTH);

		topPane.setLeftComponent(availPanel);

		JPanel selPanel = new JPanel(new BorderLayout());

		Box equipButtonBox = Box.createHorizontalBox();
		equipButtonBox.add(Box.createHorizontalGlue());
		nameButton.setHorizontalTextPosition(SwingConstants.LEADING);
		nameButton.setAction(nameAction);
		equipButtonBox.add(nameButton);
		equipButtonBox.add(Box.createHorizontalStrut(5));
		spropButton.setHorizontalTextPosition(SwingConstants.LEADING);
		spropButton.setAction(spropAction);
		equipButtonBox.add(spropButton);
		equipButtonBox.add(Box.createHorizontalStrut(5));
		costButton.setHorizontalTextPosition(SwingConstants.LEADING);
		costButton.setAction(costAction);
		equipButtonBox.add(costButton);
		equipButtonBox.add(Box.createHorizontalStrut(5));
		weightButton.setHorizontalTextPosition(SwingConstants.LEADING);
		weightButton.setAction(weightAction);
		equipButtonBox.add(weightButton);
		if (builder.isWeapon())
		{
			equipButtonBox.add(Box.createHorizontalStrut(5));
			damageButton.setHorizontalTextPosition(SwingConstants.LEADING);
			damageButton.setAction(damageAction);
			equipButtonBox.add(damageButton);
		}
		// Only show size if it can be used
		if (builder.isResizable())
		{
			JPanel sizePanel = new JPanel();
			JLabel sizeLabel = new JLabel(LanguageBundle.getString("in_EqBuilder_Size"));
			sizePanel.add(sizeLabel);
			sizePanel.add(sizeCombo);
			equipButtonBox.add(Box.createHorizontalStrut(5));
			equipButtonBox.add(sizePanel);
		}
		equipButtonBox.add(Box.createHorizontalGlue());
		equipButtonBox.setBorder(new EmptyBorder(5, 0, 0, 0));
		selPanel.add(equipButtonBox, BorderLayout.NORTH);

		selectedTable.setTreeViewModel(selectedEqmodModelMap.get(currentHead));
		selectedTable.setTreeCellRenderer(renderer);
		selPanel.add(new JScrollPane(selectedTable), BorderLayout.CENTER);

		box = Box.createHorizontalBox();
		removeButton.setHorizontalTextPosition(SwingConstants.TRAILING);
		removeButton.setAction(removeAction);
		box.add(Box.createHorizontalStrut(5));
		box.add(removeButton);
		box.add(Box.createHorizontalGlue());
		box.setBorder(new EmptyBorder(0, 0, 5, 0));
		selPanel.add(box, BorderLayout.SOUTH);

		topPane.setRightComponent(selPanel);
		FlippingSplitPane bottomPane = new FlippingSplitPane();
		bottomPane.setLeftComponent(equipModInfoPane);
		bottomPane.setRightComponent(equipInfoPane);
		setBottomComponent(bottomPane);
		setResizeWeight(0.75);
	}

	private void initDefaults()
	{
		equipInfoHandler = new EquipInfoHandler(character, builder);
		selectedTable.getSelectionModel().addListSelectionListener(equipInfoHandler);

		EquipModInfoHandler eqModInfoHandler = new EquipModInfoHandler(character, builder);
		availableTable.getSelectionModel().addListSelectionListener(eqModInfoHandler);
		selectedTable.getSelectionModel().addListSelectionListener(eqModInfoHandler);

		availableTable.addActionListener(addAction);
		sizeBoxModel = new SizeBoxModel();
		sizeCombo.setModel(sizeBoxModel);
		headBoxModel = new HeadBoxModel();
		headCombo.setModel(headBoxModel);
	}

	private class EquipInfoHandler implements ListSelectionListener
	{

		private final CharacterFacade character;
		private final EquipmentBuilderFacade builder2;

		public EquipInfoHandler(CharacterFacade character, EquipmentBuilderFacade builder)
		{
			this.character = character;
			builder2 = builder;
			refreshInfo();
		}

		private void refreshInfo()
		{
			EquipmentFacade equip = builder2.getEquipment();
			equipInfoPane.setText(character.getInfoFactory().getHTMLInfo(equip));
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				refreshInfo();
			}
		}

	}

	private class EquipModInfoHandler implements ListSelectionListener
	{

		private final CharacterFacade character;
		private final EquipmentBuilderFacade builder;
		private EquipmentModifier currObj;

		public EquipModInfoHandler(CharacterFacade character, EquipmentBuilderFacade builder)
		{
			this.character = character;
			this.builder = builder;
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				Object obj = null;
				if (e.getSource() == availableTable.getSelectionModel())
				{
					int selectedRow = availableTable.getSelectedRow();
					if (selectedRow != -1)
					{
						obj = availableTable.getModel().getValueAt(selectedRow, 0);
					}
				}
				else
				{
					int selectedRow = selectedTable.getSelectedRow();
					if (selectedRow != -1)
					{
						obj = selectedTable.getModel().getValueAt(selectedRow, 0);
					}
				}
				if (obj instanceof EquipmentModifier && obj != currObj)
				{
					currObj = (EquipmentModifier) obj;
					equipModInfoPane
						.setText(character.getInfoFactory().getHTMLInfo((EquipmentModifier) obj, builder.getEquipment()));
				}
			}
		}

	}

	private class AddEqmodAction extends AbstractAction
	{
		public AddEqmodAction()
		{
			super(LanguageBundle.getString("in_eqCust_AddPrimary")); //$NON-NLS-1$
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<Object> data = availableTable.getSelectedData();
			for (Object eqMod : data)
			{
				if (eqMod instanceof EquipmentModifier)
				{
					builder.addModToEquipment((EquipmentModifier) eqMod, currentHead);
				}
			}
			equipInfoHandler.refreshInfo();
			availableTable.refilter();
		}

	}

	private class RemoveEqmodAction extends AbstractAction
	{
		public RemoveEqmodAction()
		{
			super(LanguageBundle.getString("in_eqCust_RemovePrimary")); //$NON-NLS-1$
			putValue(SMALL_ICON, Icons.Back16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<Object> data = selectedTable.getSelectedData();
			for (Object eqMod : data)
			{
				if (eqMod instanceof EquipmentModifier)
				{
					builder.removeModFromEquipment((EquipmentModifier) eqMod, currentHead);
				}
			}
			equipInfoHandler.refreshInfo();
			availableTable.refilter();
		}

	}

	private class NameAction extends AbstractAction
	{
		public NameAction()
		{
			super(LanguageBundle.getString("in_nameLabel")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object result = JOptionPane.showInputDialog(EquipCustomPanel.this,
				LanguageBundle.getString("in_eqCust_NewName"), Constants.APPLICATION_NAME, JOptionPane.QUESTION_MESSAGE,
				null, null, builder.getEquipment().toString());
			String selectedValue = result == null ? "" : result.toString();
			builder.setName(selectedValue);
			equipInfoHandler.refreshInfo();
		}
	}

	private class SPropAction extends AbstractAction
	{
		public SPropAction()
		{
			super(LanguageBundle.getString("in_eqCust_SProp")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object result = JOptionPane.showInputDialog(EquipCustomPanel.this,
				LanguageBundle.getString("in_eqCust_NewSProp"), Constants.APPLICATION_NAME,
				JOptionPane.QUESTION_MESSAGE, null, null, builder.getEquipment().getRawSpecialProperties());
			String selectedValue = result == null ? "" : result.toString();
			builder.setSProp(selectedValue);
			equipInfoHandler.refreshInfo();
		}
	}

	private class CostAction extends AbstractAction
	{
		public CostAction()
		{
			super(LanguageBundle.getString("in_igEqModelColCost")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object result = JOptionPane.showInputDialog(EquipCustomPanel.this,
				LanguageBundle.getString("in_eqCust_NewCost"), Constants.APPLICATION_NAME, JOptionPane.QUESTION_MESSAGE,
				null, null, character.getInfoFactory().getCost(builder.getEquipment()));
			String selectedValue = result == null ? "" : result.toString();
			builder.setCost(selectedValue);
			equipInfoHandler.refreshInfo();
		}
	}

	private class WeightAction extends AbstractAction
	{
		public WeightAction()
		{
			super(LanguageBundle.getString("in_igEqModelColWeight")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object result = JOptionPane.showInputDialog(EquipCustomPanel.this,
				LanguageBundle.getString("in_eqCust_NewWeight"), Constants.APPLICATION_NAME,
				JOptionPane.QUESTION_MESSAGE, null, null, character.getInfoFactory().getWeight(builder.getEquipment()));
			String selectedValue = result == null ? "" : result.toString();
			builder.setWeight(selectedValue);
			equipInfoHandler.refreshInfo();
		}
	}

	private class DamageAction extends AbstractAction
	{
		public DamageAction()
		{
			super(LanguageBundle.getString("in_igInfoLabelTextDamage")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object result =
					JOptionPane.showInputDialog(EquipCustomPanel.this, LanguageBundle.getString("in_eqCust_NewDamage"),
						Constants.APPLICATION_NAME, JOptionPane.QUESTION_MESSAGE, null, null, builder.getDamage());
			String selectedValue = result == null ? "" : result.toString();
			builder.setDamage(selectedValue);
			equipInfoHandler.refreshInfo();
		}
	}

	private static final class EquipModTreeViewModel implements TreeViewModel<EquipmentModifier>, DataView<EquipmentModifier>,
			Filter<EquipmentBuilderFacade, EquipmentModifier>, ListListener<EquipmentModifier>
	{

		private static final DefaultListFacade<? extends TreeView<EquipmentModifier>> TREE_VIEWS =
				new DefaultListFacade<>(Arrays.asList(EquipModTreeView.values()));
		private final List<DefaultDataViewColumn> columns;
		private final boolean isAvailModel;
		private final FilteredListFacade<EquipmentBuilderFacade, EquipmentModifier> equipMods;
		private final EquipmentBuilderFacade builder;
		private final EquipmentHead head;

		private EquipModTreeViewModel(EquipmentBuilderFacade builder, EquipmentHead head,
		                              boolean isAvailModel)
		{
			this.builder = builder;
			this.head = head;
			this.isAvailModel = isAvailModel;
			equipMods = new FilteredListFacade<>();
			equipMods.setContext(builder);
			equipMods.setFilter(this);
			//$NON-NLS-1$
			if (isAvailModel)
			{
				ListFacade<EquipmentModifier> eqModList = builder.getAvailList(head);
				equipMods.setDelegate(eqModList);
				builder.getAvailList(head).addListListener(this);
			}
			columns = Collections.singletonList(new DefaultDataViewColumn("in_source", String.class, false)); //$NON-NLS-1$
		}

		@Override
		public ListFacade<? extends TreeView<EquipmentModifier>> getTreeViews()
		{
			return TREE_VIEWS;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		@Override
		public DataView<EquipmentModifier> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<EquipmentModifier> getDataModel()
		{
			if (isAvailModel)
			{
				return equipMods;
			}

			return builder.getSelectedList(head);
		}

		@Override
		public Object getData(EquipmentModifier element, int column)
		{
            if (column == 0)
            {
                return element.getSource();
            }
            return null;
        }

		@Override
		public void setData(Object value, EquipmentModifier element, int column)
		{
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}

		@Override
		public void elementAdded(ListEvent<EquipmentModifier> e)
		{
			//equipMods.elementAdded(e);
		}

		@Override
		public void elementRemoved(ListEvent<EquipmentModifier> e)
		{
			//equipMods.elementRemoved(e);
		}

		@Override
		public void elementsChanged(ListEvent<EquipmentModifier> e)
		{
			//equipMods.refilter();
		}

		@Override
		public void elementModified(ListEvent<EquipmentModifier> e)
		{
			//equipMods.refilter();
		}

		@Override
		public boolean accept(EquipmentBuilderFacade context, EquipmentModifier element)
		{
			return true;
		}

		@Override
		public String getPrefsKey()
		{
			return isAvailModel ? "EqModTreeAvail" : "EqModTreeSelected"; //$NON-NLS-1$//$NON-NLS-2$
		}

	}

	private enum EquipModTreeView implements TreeView<EquipmentModifier>
	{
		NAME(LanguageBundle.getString("in_nameLabel")), //$NON-NLS-1$
		TYPE_NAME(LanguageBundle.getString("in_typeName")), //$NON-NLS-1$
		SOURCE_NAME(LanguageBundle.getString("in_sourceName")); //$NON-NLS-1$
		private final String name;

		private EquipModTreeView(String name)
		{
			this.name = name;
		}

		@Override
		public String getViewName()
		{
			return name;
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<TreeViewPath<EquipmentModifier>> getPaths(EquipmentModifier pobj)
		{
			switch (this)
			{
				case NAME:
					return Collections.singletonList(new TreeViewPath<>(pobj));
				case TYPE_NAME:
					TreeViewPath<EquipmentModifier> path =
							createTreeViewPath(pobj, (Object[]) pobj.getDisplayType().split("\\.")); //$NON-NLS-1$
					return Collections.singletonList(path);
				case SOURCE_NAME:
					return Collections.singletonList(new TreeViewPath<>(pobj, pobj.getSourceForNodeDisplay()));
				default:
					throw new InternalError();
			}
		}

		/**
		 * Create a TreeViewPath for the equipment modifier and paths. 
		 * @param pobj The equipment modifier
		 * @param path The paths under which the equipment modifier should be shown.
		 * @return The TreeViewPath.
		 */
		private static TreeViewPath<EquipmentModifier> createTreeViewPath(EquipmentModifier pobj, Object... path)
		{
			if (path.length == 0)
			{
				return new TreeViewPath<>(pobj);
			}
			if (path.length > 2)
			{
				return new TreeViewPath<>(pobj, path[0], path[1]);
			}
			return new TreeViewPath<>(pobj, path);
		}

	}

	private class HeadBoxModel extends CharacterComboBoxModel<EquipmentHead>
	{

		private final DefaultReferenceFacade<EquipmentHead> headRef;

		public HeadBoxModel()
		{
			setListFacade(validHeads);
			headRef = new DefaultReferenceFacade<>(currentHead);
			setReference(headRef);
		}

		@Override
		public void setSelectedItem(Object anItem)
		{
			EquipmentHead head = (EquipmentHead) anItem;
			currentHead = head;
			headRef.set(head);
			availableTable.setTreeViewModel(availEqmodModelMap.get(currentHead));
			selectedTable.setTreeViewModel(selectedEqmodModelMap.get(currentHead));
		}

		@Override
		public void referenceChanged(ReferenceEvent<EquipmentHead> e)
		{
			super.referenceChanged(e);
		}

	}

	private class SizeBoxModel extends CharacterComboBoxModel<SizeAdjustment>
	{

		public SizeBoxModel()
		{
			setListFacade(character.getDataSet().getSizes());
			setReference(builder.getSizeRef());
		}

		@Override
		public void setSelectedItem(Object anItem)
		{
			builder.setSize((SizeAdjustment) anItem);
		}

		@Override
		public void referenceChanged(ReferenceEvent<SizeAdjustment> e)
		{
			super.referenceChanged(e);
			equipInfoHandler.refreshInfo();
		}

	}

}
