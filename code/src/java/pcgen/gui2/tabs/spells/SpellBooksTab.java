/*
 * SpellBooksTab.java
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
 * Created on Oct 1, 2011, 10:09:50 PM
 */
package pcgen.gui2.tabs.spells;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.StringUtils;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.SpellSupportFacade.RootNode;
import pcgen.core.facade.SpellSupportFacade.SpellNode;
import pcgen.core.facade.SpellSupportFacade.SuperNode;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.tabs.TabTitle;
import pcgen.gui2.tabs.models.CharacterComboBoxModel;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.JTreeViewTable;
import pcgen.system.LanguageBundle;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class SpellBooksTab extends FlippingSplitPane
{

	private final TabTitle tabTitle = new TabTitle("in_InfoSpellbooks"); //$NON-NLS-1$
	private final JTreeViewTable<SuperNode> availableTable;
	private final JTreeViewTable<SuperNode> selectedTable;
	private final JButton addButton;
	private final JButton removeButton;
	private final InfoPane spellsPane;
	private final InfoPane classPane;
	private final JComboBox defaultBookCombo;

	public SpellBooksTab()
	{
		super("SpellBooks");
		this.availableTable = new JTreeViewTable<SuperNode>();
		this.selectedTable = new JTreeViewTable<SuperNode>();
		this.addButton = new JButton();
		this.removeButton = new JButton();
		this.spellsPane = new InfoPane(LanguageBundle.getString("InfoSpells.spell.info"));
		this.classPane = new InfoPane(LanguageBundle.getString("InfoSpells.class.info"));
		this.defaultBookCombo = new JComboBox();
		initComponents();
	}

	private void initComponents()
	{
		FlippingSplitPane upperPane = new FlippingSplitPane("SpellBooksTop");
		Box box = Box.createVerticalBox();
		JScrollPane pane = new JScrollPane(availableTable);
		pane.setPreferredSize(new Dimension(250, 300));
		box.add(pane);
		box.add(Box.createVerticalStrut(5));
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(new JLabel(LanguageBundle.getString("InfoSpells.set.auto.book")));
			hbox.add(Box.createHorizontalGlue());
			box.add(hbox);
		}
		box.add(Box.createVerticalStrut(5));
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(defaultBookCombo);
			hbox.add(Box.createHorizontalGlue());
			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(addButton);
			hbox.add(Box.createHorizontalStrut(5));
			box.add(hbox);
		}
		box.add(Box.createVerticalStrut(5));
		upperPane.setLeftComponent(box);

		box = Box.createVerticalBox();
		box.add(new JScrollPane(selectedTable));
		box.add(Box.createVerticalStrut(5));
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(removeButton);
			hbox.add(Box.createHorizontalGlue());
			box.add(hbox);
		}
		box.add(Box.createVerticalStrut(5));
		upperPane.setRightComponent(box);
		upperPane.setResizeWeight(0);
		setTopComponent(upperPane);

		FlippingSplitPane bottomPane = new FlippingSplitPane("SpellBooksBottom");
		bottomPane.setLeftComponent(spellsPane);
		bottomPane.setRightComponent(classPane);
		setBottomComponent(bottomPane);
		setOrientation(VERTICAL_SPLIT);
	}

	private enum Models
	{
		DefaultSpellBookModel
	}

	public Hashtable<Object, Object> createModels(final CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(TreeViewModelHandler.class, new TreeViewModelHandler(character));
		state.put(AddSpellAction.class, new AddSpellAction(character));
		state.put(RemoveSpellAction.class, new RemoveSpellAction(character));
		state.put(SpellInfoHandler.class, new SpellInfoHandler(character, availableTable,
															   selectedTable, spellsPane));
		state.put(ClassInfoHandler.class, new ClassInfoHandler(character, availableTable,
															   selectedTable, classPane));
		final CharacterComboBoxModel<String> defaultSpellBookModel;
		defaultSpellBookModel = new CharacterComboBoxModel<String>()
		{

			@Override
			public void setSelectedItem(Object anItem)
			{
				character.getSpellSupport().setDefaultSpellBook((String) anItem);
			}

		};
		defaultSpellBookModel.setListFacade(character.getSpellSupport().getSpellbooks());
		defaultSpellBookModel.setReference(character.getSpellSupport().getDefaultSpellBookRef());
		state.put(Models.DefaultSpellBookModel, defaultSpellBookModel);
		state.put(QualifiedSpellTreeCellRenderer.class,
			new QualifiedSpellTreeCellRenderer(character));

		return state;
	}

	public void restoreModels(Hashtable<?, ?> state)
	{
		((TreeViewModelHandler) state.get(TreeViewModelHandler.class)).install();
		((SpellInfoHandler) state.get(SpellInfoHandler.class)).install();
		((ClassInfoHandler) state.get(ClassInfoHandler.class)).install();
		((AddSpellAction) state.get(AddSpellAction.class)).install();
		((RemoveSpellAction) state.get(RemoveSpellAction.class)).install();
		addButton.setAction((AddSpellAction) state.get(AddSpellAction.class));
		removeButton.setAction((RemoveSpellAction) state.get(RemoveSpellAction.class));
		defaultBookCombo.setModel((ComboBoxModel) state.get(Models.DefaultSpellBookModel));
		availableTable
			.setTreeCellRenderer((QualifiedSpellTreeCellRenderer) state
				.get(QualifiedSpellTreeCellRenderer.class));
		selectedTable
			.setTreeCellRenderer((QualifiedSpellTreeCellRenderer) state
				.get(QualifiedSpellTreeCellRenderer.class));
	}

	public void storeModels(Hashtable<Object, Object> state)
	{
		((SpellInfoHandler) state.get(SpellInfoHandler.class)).uninstall();
		((ClassInfoHandler) state.get(ClassInfoHandler.class)).uninstall();
		((AddSpellAction) state.get(AddSpellAction.class)).uninstall();
		((RemoveSpellAction) state.get(RemoveSpellAction.class)).uninstall();
	}

	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	/**
	 * Identify the current spell book, being the spell book that spells should 
	 * be added to. If no books exist then return an empty string.
	 * 
	 * @param character The character we are checking for.
	 * @return The name of the 'current' spell book, or empty string if none exist.
	 */
	String getCurrentSpellBookName(CharacterFacade character)
	{
		String spellList = "";
		Object selectedObject = selectedTable.getSelectedObject();
		if (selectedObject != null)
		{
			if (selectedObject instanceof SpellNode)
			{
				spellList =
						((SpellNode) selectedObject).getRootNode()
							.getName();
			}
			else if (selectedObject instanceof RootNode)
			{
				spellList = ((RootNode) selectedObject).getName();
			}
			else
			{
				JTree tree = selectedTable.getTree();
				TreePath path = tree.getSelectionPath();
				while (path.getParentPath() != null && (path.getParentPath().getParentPath() != null))
				{
					path = path.getParentPath();
				}
				spellList = path.getLastPathComponent().toString();
			}
		}
		if (StringUtils.isEmpty(spellList))
		{
			ListFacade<?> data = selectedTable.getTreeViewModel().getDataModel();
			if (!data.isEmpty())
			{
				Object firstElem = data.getElementAt(0);
				if (firstElem instanceof SpellNode)
				{
					spellList = ((SpellNode) firstElem).getRootNode().getName();
				}
			}
		}
		return spellList;
	}

	private class AddSpellAction extends AbstractAction
	{

		private CharacterFacade character;

		public AddSpellAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<?> data = availableTable.getSelectedData();
			String bookname = getCurrentSpellBookName(character);
			for (Object object : data)
			{
				if (object instanceof SpellNode)
				{
					character.getSpellSupport().addToSpellBook(
						(SpellNode) object, bookname);
				}
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

	private class RemoveSpellAction extends AbstractAction
	{

		private CharacterFacade character;

		public RemoveSpellAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, Icons.Back16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<?> data = selectedTable.getSelectedData();
			for (Object object : data)
			{
				if (object instanceof SpellNode)
				{
					SpellNode node = (SpellNode) object;
					character.getSpellSupport().removeFromSpellBook(node,
						node.getRootNode().getName());
				}
			}
		}
		
		public void install()
		{
			selectedTable.addActionListener(this);
		}
		
		public void uninstall()
		{
			selectedTable.removeActionListener(this);
		}

	}

	private class TreeViewModelHandler
	{

		private SpellTreeViewModel availableModel;
		private SpellTreeViewModel selectedModel;

		public TreeViewModelHandler(CharacterFacade character)
		{
			availableModel = new SpellTreeViewModel(character.getSpellSupport().getKnownSpellNodes(), false);
			selectedModel = new SpellTreeViewModel(character.getSpellSupport().getBookSpellNodes(), true);
		}

		public void install()
		{
			availableTable.setTreeViewModel(availableModel);
			selectedTable.setTreeViewModel(selectedModel);
		}

	}

}
