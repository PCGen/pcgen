/*
 * SpellsKnownTab.java
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
 * Created on Sep 19, 2011, 5:26:29 PM
 */
package pcgen.gui2.tabs.spells;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.SpellSupportFacade.SpellNode;
import pcgen.core.facade.SpellSupportFacade.SuperNode;
import pcgen.gui2.tabs.CharacterInfoTab;
import pcgen.gui2.tabs.TabTitle;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.JTreeViewTable;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.enumeration.Tab;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
@SuppressWarnings("serial")
public class SpellsKnownTab extends FlippingSplitPane implements CharacterInfoTab
{

	private final TabTitle tabTitle = new TabTitle(Tab.KNOWN_SPELLS);
	private final JTreeViewTable<SuperNode> availableTable;
	private final JTreeViewTable<SuperNode> selectedTable;
	private final QualifiedSpellTreeCellRenderer spellRenderer;
	private final JButton addButton;
	private final JButton removeButton;
	private final JCheckBox autoKnownBox;
	private final JCheckBox slotsBox;
	private final JTextField spellSheetField;
	private final InfoPane spellsPane;
	private final InfoPane classPane;
	private JFileChooser fileChooser = null;
	private JButton previewSpellsButton;
	private JButton exportSpellsButton;

	public SpellsKnownTab()
	{
		super("SpellsKnown");
		this.availableTable = new JTreeViewTable<SuperNode>();
		this.selectedTable = new JTreeViewTable<SuperNode>();
		this.spellRenderer = new QualifiedSpellTreeCellRenderer();
		this.addButton = new JButton();
		this.removeButton = new JButton();
		this.autoKnownBox = new JCheckBox();
		this.slotsBox = new JCheckBox();
		this.spellSheetField = new JTextField();
		this.spellsPane = new InfoPane(LanguageBundle.getString("InfoSpells.spell.info"));
		this.classPane = new InfoPane(LanguageBundle.getString("InfoSpells.class.info"));
		initComponents();
	}

	private void initComponents()
	{
		availableTable.setTreeCellRenderer(spellRenderer);
		selectedTable.setTreeCellRenderer(spellRenderer);
		FlippingSplitPane upperPane = new FlippingSplitPane("SpellsKnownTop");
		Box box = Box.createVerticalBox();
		JScrollPane pane = new JScrollPane(availableTable);
		pane.setPreferredSize(new Dimension(250, 300));
		box.add(pane);
		box.add(Box.createVerticalStrut(2));
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(autoKnownBox);
			hbox.add(Box.createHorizontalGlue());
			box.add(hbox);
		}
		//box.add(Box.createVerticalStrut(2));
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(slotsBox);
			hbox.add(Box.createHorizontalGlue());
			hbox.add(Box.createHorizontalStrut(10));
			hbox.add(addButton);
			hbox.add(Box.createHorizontalStrut(5));
			box.add(hbox);
		}
		box.add(Box.createVerticalStrut(5));
		upperPane.setLeftComponent(box);

		box = Box.createVerticalBox();
		box.add(new JScrollPane(selectedTable));
		box.add(Box.createVerticalStrut(4));
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(removeButton);
			hbox.add(Box.createHorizontalStrut(10));

			JButton spellSheetButton = new JButton(LanguageBundle.getString("InfoSpells.select.spellsheet"));
			spellSheetButton.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					selectSpellSheetButton();
				}

			});
			hbox.add(spellSheetButton);
			hbox.add(Box.createHorizontalStrut(3));

			String text = PCGenSettings.getSelectedSpellSheet();
			if (text != null)
			{
				text = new File(text).getName();
			}
			spellSheetField.setEditable(false);
			spellSheetField.setText(text);
			spellSheetField.setToolTipText(text);
			hbox.add(spellSheetField);
			hbox.add(Box.createHorizontalStrut(3));
			previewSpellsButton = new JButton(Icons.PrintPreview16.getImageIcon());
			hbox.add(previewSpellsButton);
			hbox.add(Box.createHorizontalStrut(3));
			exportSpellsButton = new JButton(Icons.Print16.getImageIcon());
			hbox.add(exportSpellsButton);
			hbox.add(Box.createHorizontalStrut(5));
			box.add(hbox);
		}
		box.add(Box.createVerticalStrut(5));
		upperPane.setRightComponent(box);
		upperPane.setResizeWeight(0);
		setTopComponent(upperPane);

		FlippingSplitPane bottomPane = new FlippingSplitPane("SpellsKnownBottom");
		bottomPane.setLeftComponent(spellsPane);
		bottomPane.setRightComponent(classPane);
		setBottomComponent(bottomPane);
		setOrientation(VERTICAL_SPLIT);
	}

	@Override
	public ModelMap createModels(CharacterFacade character)
	{
		ModelMap models = new ModelMap();
		models.put(TreeViewModelHandler.class, new TreeViewModelHandler(character));
		models.put(AddSpellAction.class, new AddSpellAction(character));
		models.put(RemoveSpellAction.class, new RemoveSpellAction(character));
		models.put(AutoAddSpellsAction.class, new AutoAddSpellsAction(character));
		models.put(UseHigherSlotsAction.class, new UseHigherSlotsAction(character));
		models.put(PreviewSpellsAction.class, new PreviewSpellsAction(character));
		models.put(ExportSpellsAction.class, new ExportSpellsAction(character));
		models.put(SpellInfoHandler.class, new SpellInfoHandler(character, availableTable,
				selectedTable, spellsPane));
		models.put(ClassInfoHandler.class, new ClassInfoHandler(character, availableTable,
				selectedTable, classPane));
		return models;
	}

	@Override
	public void restoreModels(ModelMap models)
	{
		models.get(TreeViewModelHandler.class).install();
		models.get(SpellInfoHandler.class).install();
		models.get(ClassInfoHandler.class).install();
		models.get(AddSpellAction.class).install();
		models.get(RemoveSpellAction.class).install();
		models.get(AutoAddSpellsAction.class).install();
		models.get(UseHigherSlotsAction.class).install();
		previewSpellsButton.setAction(models.get(PreviewSpellsAction.class));
		exportSpellsButton.setAction(models.get(ExportSpellsAction.class));
	}

	@Override
	public void storeModels(ModelMap models)
	{
		models.get(SpellInfoHandler.class).uninstall();
		models.get(ClassInfoHandler.class).uninstall();
		models.get(AddSpellAction.class).uninstall();
		models.get(RemoveSpellAction.class).uninstall();
		models.get(TreeViewModelHandler.class).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	/**
	 * Select a spell output sheet
	 */
	private void selectSpellSheetButton()
	{
		if (fileChooser == null)
		{
			fileChooser = new JFileChooser();
		}

		fileChooser.setDialogTitle(LanguageBundle.getString("InfoSpells.select.output.sheet")); //$NON-NLS-1$
		fileChooser.setCurrentDirectory(new File(ConfigurationSettings.getOutputSheetsDir()));
		if (PCGenSettings.getSelectedSpellSheet() != null)
		{
			fileChooser.setSelectedFile(new File(PCGenSettings.getSelectedSpellSheet()));
		}
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			PCGenSettings.getInstance().setProperty(PCGenSettings.SELECTED_SPELL_SHEET_PATH,
					fileChooser.getSelectedFile().getAbsolutePath());
			spellSheetField.setText(fileChooser.getSelectedFile().getName());
			spellSheetField.setToolTipText(fileChooser.getSelectedFile().getName());
		}
	}

	private class AddSpellAction extends AbstractAction
	{

		private final CharacterFacade character;

		public AddSpellAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<?> data = availableTable.getSelectedData();
			for (Object object : data)
			{
				if (object instanceof SpellNode)
				{
					character.getSpellSupport().addKnownSpell((SpellNode) object);
				}
			}
		}

		public void install()
		{
			availableTable.addActionListener(this);
			addButton.setAction(this);
		}

		public void uninstall()
		{
			availableTable.removeActionListener(this);
		}

	}

	private class RemoveSpellAction extends AbstractAction
	{

		private final CharacterFacade character;

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
					character.getSpellSupport().removeKnownSpell((SpellNode) object);
				}
			}
		}

		public void install()
		{
			selectedTable.addActionListener(this);
			removeButton.setAction(this);
		}

		public void uninstall()
		{
			selectedTable.removeActionListener(this);
		}

	}

	private class AutoAddSpellsAction extends AbstractAction
	{

		private final CharacterFacade character;

		public AutoAddSpellsAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("InfoSpells.autoload"));
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			autoKnownBox.setAction(this);
			character.getSpellSupport().setAutoSpells(autoKnownBox.isSelected());
		}

		public void install()
		{
			autoKnownBox.setSelected(character.getSpellSupport().isAutoSpells());
		}

	}

	private class UseHigherSlotsAction extends AbstractAction
	{

		private final CharacterFacade character;

		public UseHigherSlotsAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("InfoKnownSpells.canUseHigherSlots"));
			this.character = character;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.getSpellSupport().setUseHigherKnownSlots(slotsBox.isSelected());
		}

		public void install()
		{
			slotsBox.setAction(this);
			slotsBox.setSelected(character.getSpellSupport().isUseHigherKnownSlots());
		}

	}

	private class PreviewSpellsAction extends AbstractAction
	{

		private final CharacterFacade character;

		public PreviewSpellsAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, Icons.PrintPreview16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.getSpellSupport().previewSpells();
		}

	}

	private class ExportSpellsAction extends AbstractAction
	{

		private final CharacterFacade character;

		public ExportSpellsAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, Icons.Print16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.getSpellSupport().exportSpells();
		}

	}

	private class TreeViewModelHandler
	{

		private final SpellTreeViewModel availableModel;
		private final SpellTreeViewModel selectedModel;
		private final CharacterFacade character;

		public TreeViewModelHandler(CharacterFacade character)
		{
			this.character = character;
			availableModel = new SpellTreeViewModel(character.getSpellSupport().getAvailableSpellNodes(), false, "SpellsKnownAva");
			selectedModel = new SpellTreeViewModel(character.getSpellSupport().getAllKnownSpellNodes(), true, "SpellsKnownSel");
		}

		public void install()
		{
			spellRenderer.setCharacter(character);
			availableTable.setTreeViewModel(availableModel);
			selectedTable.setTreeViewModel(selectedModel);
		}

		public void uninstall()
		{
			spellRenderer.setCharacter(null);
		}

	}

}
