/*
 * PCGenMenuBar.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Aug 16, 2008, 3:19:16 PM
 */
package pcgen.gui2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.EquipmentSetFacade;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.SourceSelectionFacade;
import pcgen.core.facade.TempBonusFacade;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.SortedListFacade;
import pcgen.gui2.tools.CharacterSelectionListener;
import pcgen.gui2.util.AbstractListMenu;
import pcgen.gui2.util.AbstractRadioListMenu;
import pcgen.system.CharacterManager;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;
import pcgen.util.Comparators;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class PCGenMenuBar extends JMenuBar implements CharacterSelectionListener
{
	
	private final PCGenFrame frame;
	private final PCGenActionMap actionMap;
	private final EquipmentSetMenu equipmentMenu;
	private final TempBonusMenu tempMenu;
	private CharacterFacade character;
	
	public PCGenMenuBar(PCGenFrame frame)
	{
		this.frame = frame;
		this.actionMap = frame.getActionMap();
		this.equipmentMenu = new EquipmentSetMenu();
		this.tempMenu = new TempBonusMenu();
		initComponents();
	}
	
	private void initComponents()
	{
		add(new FileMenu());
		add(createEditMenu());
		add(createSourcesMenu());
		add(createToolsMenu());
		add(createHelpMenu());
	}
	
	private JMenu createEditMenu()
	{
		JMenu menu = new JMenu(actionMap.get(PCGenActionMap.EDIT_COMMAND));
		menu.add(actionMap.get(PCGenActionMap.UNDO_COMMAND));
		menu.add(actionMap.get(PCGenActionMap.REDO_COMMAND));
		menu.addSeparator();
		menu.add(actionMap.get(PCGenActionMap.ADD_KIT_COMMAND));
		menu.add(actionMap.get(PCGenActionMap.GENERATE_COMMAND));
		menu.addSeparator();
		menu.add(equipmentMenu);
		menu.add(tempMenu);
		return menu;
	}
	
	private JMenu createSourcesMenu()
	{
		JMenu menu = new JMenu(actionMap.get(PCGenActionMap.SOURCES_COMMAND));
		menu.add(new JMenuItem(actionMap.get(PCGenActionMap.SOURCES_LOAD_SELECT_COMMAND)));
		menu.addSeparator();
		menu.add(new QuickSourceMenu());
		menu.addSeparator();
		menu.add(actionMap.get(PCGenActionMap.INSTALL_DATA_COMMAND));
		
		return menu;
	}
	
	private JMenu createToolsMenu()
	{
		JMenu menu = new JMenu(actionMap.get(PCGenActionMap.TOOLS_COMMAND));
		
		JMenu filtersMenu = new JMenu(actionMap.get(PCGenActionMap.FILTERS_COMMAND));
		filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.KIT_FILTERS_COMMAND)));
		filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.RACE_FILTERS_COMMAND)));
		filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.CLASS_FILTERS_COMMAND)));
		filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.ABILITY_FILTERS_COMMAND)));
		filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SKILL_FILTERS_COMMAND)));
		filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.EQUIPMENT_FILTERS_COMMAND)));
		filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SPELL_FILTERS_COMMAND)));
		filtersMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.TEMPLATE_FILTERS_COMMAND)));
		menu.add(filtersMenu);
		
		JMenu generatorsMenu = new JMenu(actionMap.get(PCGenActionMap.GENERATORS_COMMAND));
		generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.TREASURE_GENERATORS_COMMAND)));
		generatorsMenu.addSeparator();
		generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.STAT_GENERATORS_COMMAND)));
		generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.RACE_GENERATORS_COMMAND)));
		generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.CLASS_GENERATORS_COMMAND)));
		generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.ABILITY_GENERATORS_COMMAND)));
		generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SKILL_GENERATORS_COMMAND)));
		generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.EQUIPMENT_GENERATORS_COMMAND)));
		generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.SPELL_GENERATORS_COMMAND)));
		generatorsMenu.add(new JMenuItem(actionMap.get(PCGenActionMap.TEMPLATE_GENERATORS_COMMAND)));
		menu.add(generatorsMenu);
		menu.addSeparator();
		menu.add(actionMap.get(PCGenActionMap.PREFERENCES_COMMAND));
		menu.addSeparator();
		menu.add(actionMap.get(PCGenActionMap.GMGEN_COMMAND));
		menu.addSeparator();
		menu.add(actionMap.get(PCGenActionMap.CONSOLE_COMMAND));
		//menu.add(new ComboListMenu<File>(actionMap.get(PCGenActionMap.CSHEET_COMMAND),
		//		frame.getCharacterSheets()));
		return menu;
	}
	
	private JMenu createHelpMenu()
	{
		JMenu menu = new JMenu(actionMap.get(PCGenActionMap.HELP_COMMAND));
		menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_CONTEXT_COMMAND)));
		menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_DOCS_COMMAND)));
		menu.addSeparator();
		menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_OGL_COMMAND)));
		menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_SPONSORS_COMMAND)));
		menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_TIPOFTHEDAY_COMMAND)));
		menu.addSeparator();
		menu.add(new JMenuItem(actionMap.get(PCGenActionMap.HELP_ABOUT_COMMAND)));
		return menu;
	}
	
	@Override
	public void setCharacter(CharacterFacade character)
	{
		this.character = character;
		equipmentMenu.setListModel(character.getEquipmentSets());
		tempMenu.setListModel(character.getAvailableTempBonuses());
	}
	
	private class FileMenu extends AbstractListMenu<File>
			implements ActionListener
	{
		
		public FileMenu()
		{
			super(actionMap.get(PCGenActionMap.FILE_COMMAND));
			add(new JMenuItem(actionMap.get(PCGenActionMap.NEW_COMMAND)));
			add(new JMenuItem(actionMap.get(PCGenActionMap.OPEN_COMMAND)));
			addSeparator();
			add(new JMenuItem(actionMap.get(PCGenActionMap.CLOSE_COMMAND)));
			add(new JMenuItem(actionMap.get(PCGenActionMap.CLOSEALL_COMMAND)));
			addSeparator();
			
			add(new JMenuItem(actionMap.get(PCGenActionMap.SAVE_COMMAND)));
			add(new JMenuItem(actionMap.get(PCGenActionMap.SAVEAS_COMMAND)));
			add(new JMenuItem(actionMap.get(PCGenActionMap.SAVEALL_COMMAND)));
			add(new JMenuItem(actionMap.get(PCGenActionMap.REVERT_COMMAND)));
			addSeparator();
			add(new PartyMenu());
			addSeparator();
			
			add(new JMenuItem(actionMap.get(PCGenActionMap.PRINT_COMMAND)));
			add(new JMenuItem(actionMap.get(PCGenActionMap.EXPORT_COMMAND)));
			addSeparator();
			setOffset(16);
			setListModel( CharacterManager.getRecentCharacters());
			addSeparator();

			
			add(new JMenuItem(actionMap.get(PCGenActionMap.EXIT_COMMAND)));
		}
		
		@Override
		protected JMenuItem createMenuItem(File item, int index)
		{
			JMenuItem menuItem = new JMenuItem();
			menuItem.setText((index+1) + " " + item.getName()); //$NON-NLS-1$
			menuItem.setToolTipText(LanguageBundle.getFormattedString(
				"in_OpenRecentCharTip", item.getAbsolutePath())); //$NON-NLS-1$
			menuItem.setActionCommand(item.getAbsolutePath());
			menuItem.setMnemonic(String.valueOf(index+1).charAt(0));
			menuItem.addActionListener(this);
			return menuItem;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.loadCharacterFromFile(new File(e.getActionCommand()));
		}
		
	}
	
	private class PartyMenu extends AbstractListMenu<File>
			implements ActionListener
	{
		
		public PartyMenu()
		{
			super(actionMap.get(PCGenActionMap.PARTY_COMMAND));
			add(new JMenuItem(actionMap.get(PCGenActionMap.OPEN_PARTY_COMMAND)));
			add(new JMenuItem(actionMap.get(PCGenActionMap.CLOSE_PARTY_COMMAND)));
			addSeparator();
			
			add(new JMenuItem(actionMap.get(PCGenActionMap.SAVE_PARTY_COMMAND)));
			add(new JMenuItem(actionMap.get(PCGenActionMap.SAVEAS_PARTY_COMMAND)));
			addSeparator();
			setOffset(6);
			setListModel( CharacterManager.getRecentParties());
		}
		
		@Override
		protected JMenuItem createMenuItem(File item, int index)
		{
			JMenuItem menuItem = new JMenuItem();
			menuItem.setText((index+1) + " " + item.getName()); //$NON-NLS-1$
			menuItem.setToolTipText(item.getAbsolutePath());
			menuItem.setActionCommand(item.getAbsolutePath());
			menuItem.setMnemonic(String.valueOf(index+1).charAt(0));
			menuItem.addActionListener(this);
			return menuItem;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.loadPartyFromFile(new File(e.getActionCommand()));
		}
		
	}
	
	private class QuickSourceMenu extends AbstractRadioListMenu<SourceSelectionFacade>
			implements ReferenceListener<SourceSelectionFacade>
	{
		
		public QuickSourceMenu()
		{
			super(actionMap.get(PCGenActionMap.SOURCES_LOAD_COMMAND));
			ReferenceFacade<SourceSelectionFacade> ref = frame.getCurrentSourceSelectionRef();
			setSelectedItem(ref.getReference());
			ListFacade<SourceSelectionFacade> sources = FacadeFactory.getDisplayedSourceSelections();
			setListModel(new SortedListFacade<SourceSelectionFacade>(Comparators.toStringIgnoreCaseCollator(), sources));
			ref.addReferenceListener(this);
		}
		
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				Object item = e.getItemSelectable().getSelectedObjects()[0];
				if (frame.loadSourceSelection((SourceSelectionFacade) item))
				{
					setSelectedItem(frame.getCurrentSourceSelectionRef().getReference());
				}
			}
		}
		
		@Override
		public void referenceChanged(ReferenceEvent<SourceSelectionFacade> e)
		{
			setSelectedItem(e.getNewReference());
		}
		
	}
	
	private class EquipmentSetMenu extends AbstractRadioListMenu<EquipmentSetFacade>
	{
		
		public EquipmentSetMenu()
		{
			super(actionMap.get(PCGenActionMap.EQUIPMENTSET_COMMAND));
		}
		
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}
		
	}
	
	private class TempBonusMenu extends AbstractListMenu<TempBonusFacade>
			implements ItemListener
	{
		
		public TempBonusMenu()
		{
			super(actionMap.get(PCGenActionMap.TEMP_BONUS_COMMAND));
		}
		
		@Override
		protected JMenuItem createMenuItem(TempBonusFacade item, int index)
		{
			return new CheckBoxMenuItem(item, character.getTempBonuses().containsElement(item),
										this);
		}
		
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			TempBonusFacade bonus = (TempBonusFacade) e.getItemSelectable().getSelectedObjects()[0];
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				character.addTempBonus(bonus);
			}
			else
			{
				character.removeTempBonus(bonus);
			}
		}
		
	}
	
}
