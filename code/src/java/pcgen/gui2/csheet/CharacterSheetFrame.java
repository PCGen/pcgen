/*
 * CharacterSheetFrame.java
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
 * Created on Feb 14, 2011, 11:13:03 AM
 */
package pcgen.gui2.csheet;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.EquipmentSetFacade;
import pcgen.core.facade.TempBonusFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.gui2.PCGenFrame;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.PCGenAction;
import pcgen.gui2.util.AbstractListMenu;
import pcgen.gui2.util.AbstractRadioListMenu;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CharacterSheetFrame extends JFrame
{

	private final CharacterSheetPanel sheetPanel;
	private CharacterFacade character;
	private EquipmentSetMenu equipMenu;
	private TempBonusMenu bonusMenu;

	public CharacterSheetFrame(PCGenFrame frame, CharacterFacade character)
	{
		this.character = character;
		this.sheetPanel = new CharacterSheetPanel();
		sheetPanel.setCharacter(character);
		initComponents();
		pack();
	}

	private void initComponents()
	{
		setJMenuBar(createMenuBar());
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createToolBar(), BorderLayout.NORTH);
		getContentPane().add(sheetPanel, BorderLayout.CENTER);
	}

	private JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar();
		return toolBar;
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar bar = new JMenuBar();
		bar.add(createFileMenu());

		equipMenu = new EquipmentSetMenu();
		bar.add(equipMenu);

		bonusMenu = new TempBonusMenu();
		bar.add(bonusMenu);

		return bar;
	}

	private JMenu createFileMenu()
	{
		JMenu menu = new JMenu("File");
		menu.add(new JMenuItem(new SaveAction()));
		menu.addSeparator();
		menu.add(new JMenuItem(new CloseAction()));
		return menu;
	}

	private void close()
	{
		equipMenu.dispose();
		bonusMenu.dispose();
	}

	private class SaveAction extends PCGenAction
	{

		public SaveAction()
		{
			super("mnuFileSave", null, "shortcut S", Icons.Save16);
		}

	}

	private class CloseAction extends PCGenAction
	{

		public CloseAction()
		{
			super("mnuFileClose", null, "shortcut W", Icons.Close16);
		}

	}

	private class EquipmentSetMenu extends AbstractRadioListMenu<EquipmentSetFacade>
			implements ReferenceListener<EquipmentSetFacade>
	{

		public EquipmentSetMenu()
		{
			super(new PCGenAction("mnuEditEquipmentSet"));
			this.setListModel(character.getEquipmentSets());
			character.getEquipmentSetRef().addReferenceListener(this);
		}

		public void dispose()
		{
			character.getEquipmentSetRef().removeReferenceListener(this);
		}

		@Override
		public void itemStateChanged(ItemEvent e)
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				character.setEquipmentSet((EquipmentSetFacade) e.getItem());
			}
		}

		@Override
		public void referenceChanged(ReferenceEvent<EquipmentSetFacade> e)
		{
			setSelectedItem(e.getNewReference());
		}

	}

	private class TempBonusMenu extends AbstractListMenu<TempBonusFacade>
			implements ItemListener
	{

		public TempBonusMenu()
		{
			super(new PCGenAction("mnuEditTempBonus"));
			setListModel(character.getAvailableTempBonuses());
		}

		public void dispose()
		{
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
