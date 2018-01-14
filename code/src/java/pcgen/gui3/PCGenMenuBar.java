/*
 * PCGenMenuBar.java
 * Copyright 2016 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Aug 26, 2016, 1:29:09 PM
 */
package pcgen.gui3;

import java.util.List;
import java.util.function.Function;

import pcgen.gui3.util.MappedList;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PCGenMenuBar extends MenuBar
{

	private final PCGenActions actions;

	public PCGenMenuBar(PCGenActions actions)
	{

		this.actions = actions;

		Menu fileMenu = new FileMenu();
//        fileMenu.setAccelerator(KeyCombination.keyCombination("F"));

		Menu editMenu = new Menu("Edit");
		Menu sourcesMenu = new Menu("Sources");
		Menu toolsMenu = new Menu("Tools");
		Menu helpMenu = new Menu("Help");

		this.getMenus().addAll(fileMenu, editMenu, sourcesMenu, toolsMenu, helpMenu);
	}

	private Menu fileMenu(){
		Menu menu = new Menu();
		
		return menu;
	}
//	private JMenu createSourcesMenu()
	//	{
	//		PCGMenu menu = new PCGMenu(actionMap.get(PCGenActionMap.SOURCES_COMMAND));
	//		menu.add(new PCGMenuItem(actionMap.get(PCGenActionMap.SOURCES_LOAD_SELECT_COMMAND)));
	//		menu.addSeparator();
	//		menu.add(new QuickSourceMenu());
	//		menu.addSeparator();
	//		menu.add(new PCGMenuItem(actionMap.get(PCGenActionMap.SOURCES_RELOAD_COMMAND)));
	//		menu.add(new PCGMenuItem(actionMap.get(PCGenActionMap.SOURCES_UNLOAD_COMMAND)));
	//		menu.addSeparator();
	//		menu.add(actionMap.get(PCGenActionMap.INSTALL_DATA_COMMAND));
	//		
	//		return menu;
	//	}
	private class FileMenu extends Menu
	{

		public FileMenu()
		{
			super("File");

//			add(PCGenActions.NewAction.class);
//			add(PCGenActions.OpenAction.class);
//			addSeparator();
//			add(PCGenActions.CloseAction.class);


//            add("Close", null);
//            add("Close All", null);
//            addSeparator();
//            
//            add("Save", null);

//            super(actionMap.get(PCGenActionMap.FILE_COMMAND));
//			add(new PCGMenuItem(actionMap.get(PCGenActionMap.NEW_COMMAND)));
//			add(new PCGMenuItem(actionMap.get(PCGenActionMap.OPEN_COMMAND)));
//			addSeparator();
//			add(new PCGMenuItem(actionMap.get(PCGenActionMap.CLOSE_COMMAND)));
//			add(new PCGMenuItem(actionMap.get(PCGenActionMap.CLOSEALL_COMMAND)));
//			addSeparator();
//			
//			add(new PCGMenuItem(actionMap.get(PCGenActionMap.SAVE_COMMAND)));
//			add(new PCGMenuItem(actionMap.get(PCGenActionMap.SAVEAS_COMMAND)));
//			add(new PCGMenuItem(actionMap.get(PCGenActionMap.SAVEALL_COMMAND)));
//			add(new PCGMenuItem(actionMap.get(PCGenActionMap.REVERT_COMMAND)));
//			addSeparator();
//			add(new PartyMenu());
//			addSeparator();
//			
//			add(new PCGMenuItem(actionMap.get(PCGenActionMap.PRINT_COMMAND)));
//			add(new PCGMenuItem(actionMap.get(PCGenActionMap.EXPORT_COMMAND)));
//			addSeparator();
//			setOffset(16);
//			setListModel( CharacterManager.getRecentCharacters());
//			addSeparator();
//
//			
//			add(new PCGMenuItem(actionMap.get(PCGenActionMap.EXIT_COMMAND)));
		}

	}

	private void add(Menu menu, Class<? extends PCGenAction> actionClass)
	{
		PCGenAction action = actions.getAction(actionClass);
		MenuItem item = new MenuItem();
		item.acceleratorProperty().bind(action.acceleratorProperty());
		item.textProperty().bind(action.textProperty());
		item.graphicProperty().bind(action.graphicProperty());
		item.disableProperty().bind(action.disabledProperty());
		item.setOnAction(action);
		menu.getItems().add(item);
	}

	private static void addSeparator(Menu menu)
	{
		menu.getItems().add(new SeparatorMenuItem());
	}

	public static <T> void addMenuItems(Menu menu, ObservableList<T> list, Function<T, MenuItem> mapper)
	{
		List<MenuItem> items = menu.getItems();
		int size = items.size();
		Bindings.bindContent(items.subList(size, size), new MappedList<MenuItem, T>(list, mapper));
	}

}
