/*
 * KitPanel.java
 * Copyright James Dempsey, 2012
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
 * Created on 01/03/2012 8:01:51 AM
 *
 * $Id$
 */
package pcgen.gui2.kits;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreeCellRenderer;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.KitFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilteredListFacade;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.models.QualifiedTreeCellRenderer;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>KitPanel</code> displays an available/selected table pair to 
 * allow the allocation of kit to the currently selected character. 
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class KitPanel extends FlippingSplitPane
{

	private final FilteredTreeViewTable<Object, KitFacade> availableTable;
	private final FilteredTreeViewTable<Object, KitFacade> selectedTable;
	private final JButton addButton;
	private final InfoPane infoPane;
	private final CharacterFacade character;
	private TreeCellRenderer renderer;
	private AddAction addAction;

	/**
	 * Create a new instance of KitPanel for a character.
	 * @param character The character being displayed.
	 */
	public KitPanel(CharacterFacade character)
	{
		this.character = character;
		this.availableTable = new FilteredTreeViewTable<Object, KitFacade>();
		this.selectedTable = new FilteredTreeViewTable<Object, KitFacade>();
		this.addButton = new JButton();
		this.infoPane = new InfoPane(LanguageBundle.getString("in_kitInfo")); //$NON-NLS-1$
		this.renderer = new QualifiedTreeCellRenderer(character);
		this.addAction = new AddAction(character);

		initComponents();
		initDefaults();
	}

	private void initComponents()
	{
		FlippingSplitPane topPane = new FlippingSplitPane();
		setTopComponent(topPane);
		setOrientation(VERTICAL_SPLIT);

		JPanel availPanel = new JPanel(new BorderLayout());
		FilterBar<Object, KitFacade> bar = new FilterBar<Object, KitFacade>();
		bar.addDisplayableFilter(new SearchFilterPanel());
		availPanel.add(bar, BorderLayout.NORTH);

		availableTable.setDisplayableFilter(bar);
		availableTable.setTreeViewModel(new KitTreeViewModel(character, true));
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
		FilterBar<Object, KitFacade> filterBar = new FilterBar<Object, KitFacade>();
		filterBar.addDisplayableFilter(new SearchFilterPanel());

		selectedTable.setDisplayableFilter(filterBar);
		selectedTable.setTreeViewModel(new KitTreeViewModel(character, false));
		selectedTable.setTreeCellRenderer(renderer);
		selPanel.add(new JScrollPane(selectedTable), BorderLayout.CENTER);

		topPane.setRightComponent(selPanel);
		setBottomComponent(infoPane);
		setResizeWeight(.75);
	}

	/**
	 * 
	 */
	private void initDefaults()
	{
		InfoHandler infoHandler = new InfoHandler(character);
		availableTable.getSelectionModel().addListSelectionListener(infoHandler);
		selectedTable.getSelectionModel().addListSelectionListener(infoHandler);

		availableTable.addActionListener(addAction);
	}

	private class InfoHandler implements ListSelectionListener
	{

		private CharacterFacade character;

		public InfoHandler(CharacterFacade character)
		{
			this.character = character;
		}

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
				if (obj instanceof KitFacade)
				{
					infoPane.setText(character.getInfoFactory().getHTMLInfo((KitFacade) obj));
				}
			}
		}

	}

	private class AddAction extends AbstractAction
	{

		private CharacterFacade character;

		public AddAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_kitApply")); //$NON-NLS-1$
			this.character = character;
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		public void actionPerformed(ActionEvent e)
		{
			List<Object> data = availableTable.getSelectedData();
			for (Object kit : data)
			{
				if (kit instanceof KitFacade)
				{
					character.addKit((KitFacade) kit);
					return;
				}
			}
		}

	}

	private static class KitTreeViewModel
			implements TreeViewModel<KitFacade>, DataView<KitFacade>,
			Filter<CharacterFacade, KitFacade>, ListListener<KitFacade>
	{

		private static final DefaultListFacade<? extends TreeView<KitFacade>> treeViews =
				new DefaultListFacade<TreeView<KitFacade>>(Arrays.asList(TemplateTreeView.values()));
		private final List<DefaultDataViewColumn> columns;
		private final CharacterFacade character;
		private final boolean isAvailModel;
		private FilteredListFacade<CharacterFacade, KitFacade> kits;

		public KitTreeViewModel(CharacterFacade character, boolean isAvailModel)
		{
			this.character = character;
			this.isAvailModel = isAvailModel;
			if (isAvailModel)
			{
				kits = new FilteredListFacade<CharacterFacade, KitFacade>();
				kits.setContext(character);
				kits.setFilter(this);
				ListFacade<KitFacade> kitList = new DefaultListFacade<KitFacade>(character.getAvailableKits());
				kits.setDelegate(kitList);
				character.getKits().addListListener(this);
				columns = Arrays.asList(new DefaultDataViewColumn("in_source", String.class, false)); //$NON-NLS-1$
			}
			else
			{
				kits = null;
				columns = Arrays.asList(new DefaultDataViewColumn("in_source", String.class, false)); //$NON-NLS-1$
			}
		}

		public ListFacade<? extends TreeView<KitFacade>> getTreeViews()
		{
			return treeViews;
		}

		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		public DataView<KitFacade> getDataView()
		{
			return this;
		}

		public ListFacade<KitFacade> getDataModel()
		{
			if (isAvailModel)
			{
				return kits;
			}
			else
			{
				return character.getKits();
			}
		}

		public List<?> getData(KitFacade obj)
		{
			return Arrays.asList(obj.getSource());
		}

		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}

		public void elementAdded(ListEvent<KitFacade> e)
		{
			kits.refilter();
		}

		public void elementRemoved(ListEvent<KitFacade> e)
		{
			kits.refilter();
		}

		public void elementsChanged(ListEvent<KitFacade> e)
		{
			kits.refilter();
		}

		@Override
		public void elementModified(ListEvent<KitFacade> e)
		{
			kits.refilter();
		}

		public boolean accept(CharacterFacade context, KitFacade element)
		{
			return !context.getKits().containsElement(element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPrefsKey()
		{
			return isAvailModel ? "KitTreeAvail" : "KitTreeSelected";  //$NON-NLS-1$//$NON-NLS-2$
		}

	}

	private enum TemplateTreeView implements TreeView<KitFacade>
	{

		NAME(LanguageBundle.getString("in_nameLabel")), //$NON-NLS-1$
		TYPE_NAME(LanguageBundle.getString("in_typeName")), //$NON-NLS-1$
		SOURCE_NAME(LanguageBundle.getString("in_sourceName")); //$NON-NLS-1$
		private String name;

		private TemplateTreeView(String name)
		{
			this.name = name;
		}

		public String getViewName()
		{
			return name;
		}

		@SuppressWarnings("unchecked")
		public List<TreeViewPath<KitFacade>> getPaths(KitFacade pobj)
		{
			switch (this)
			{
				case NAME:
					return Collections.singletonList(new TreeViewPath<KitFacade>(pobj));
				case TYPE_NAME:
					TreeViewPath<KitFacade> path =
							createTreeViewPath(pobj, (Object[]) pobj
								.getDisplayType().split("\\.")); //$NON-NLS-1$
					return Arrays.asList(path);
				case SOURCE_NAME:
					return Collections.singletonList(new TreeViewPath<KitFacade>(pobj, pobj.getSource()));
				default:
					throw new InternalError();
			}
		}

		/**
		 * Create a TreeViewPath for the kit and paths. 
		 * @param pobj The skill
		 * @param path The paths under which the kit should be shown.
		 * @return The TreeViewPath.
		 */
		protected static TreeViewPath<KitFacade> createTreeViewPath(KitFacade pobj,
																	Object... path)
		{
			if (path.length == 0)
			{
				return new TreeViewPath<KitFacade>(pobj);
			}
			return new TreeViewPath<KitFacade>(pobj, path);
		}

	}

}
