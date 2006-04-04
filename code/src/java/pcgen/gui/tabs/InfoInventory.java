/*
 * InfoInventory.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on September 16, 2002, 3:30 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui.tabs;

import pcgen.core.Constants;
import pcgen.core.GameMode;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.PToolBar;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.Filterable;
import pcgen.util.PropertyFactory;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>InfoInventory</code><br>
 * @author Thomas Behr 16-09-02
 * @version $Revision$
 */
public final class InfoInventory extends JTabbedPane implements Filterable, CharacterInfoTab
{
	static final long serialVersionUID = -4186874622211290063L;
	private static final int GEAR_INDEX = 0;
	private static final int EQUIPPING_INDEX = 1;
	private static final int RESOURCES_INDEX = 2;
	private static final int TEMPMOD_INDEX = 3;
	private static final int NATURAL_INDEX = 4;
	private InfoEquipping equipment;
	private InfoGear gear;
	private InfoNaturalWeapons naturalWeapons;
	private InfoResources resources;
	private InfoTempMod tempmod;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	/**
	 * Constructor
	 * @param pc
	 */
	public InfoInventory(PlayerCharacter pc)
	{
		this.pc = pc;
		equipment = new InfoEquipping(pc);
		gear = new InfoGear(pc);
		resources = new InfoResources(pc);
		tempmod = new InfoTempMod(pc);
		// do not remove this
		// we will use the component's name to save component specific settings
		setName(Constants.tabNames[Constants.TAB_INVENTORY]);

		initComponents();
		initActionListeners();

		InfoGear.setNeedsUpdate(true);
	}

	public void setPc(PlayerCharacter pc)
	{
		if(this.pc != pc || pc.getSerial() > serial)
		{
			this.pc = pc;
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public PlayerCharacter getPc()
	{
		return pc;
	}

	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(".Panel.Inventory.Order", Constants.TAB_INVENTORY);
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Inventory.Order", order);
	}

	public String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(Constants.TAB_INVENTORY);
	}

	public boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(Constants.TAB_INVENTORY);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List getToDos()
	{
		List toDoList = new ArrayList();
		toDoList.addAll(gear.getToDos());
		toDoList.addAll(equipment.getToDos());
		toDoList.addAll(resources.getToDos());
		toDoList.addAll(tempmod.getToDos());
		if (SettingsHandler.showNaturalWeaponTab())
		{
			toDoList.addAll(naturalWeapons.getToDos());
		}
		return toDoList;
	}

	public void refresh()
	{
		if(pc.getSerial() > serial)
		{
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public void forceRefresh()
	{
		if(readyForRefresh)
		{
			setNeedsUpdate(true);

			gear.setPc(pc);
			equipment.setPc(pc);
			resources.setPc(pc);
			tempmod.setPc(pc);

			if (SettingsHandler.showNaturalWeaponTab())
			{
				naturalWeapons.forceRefresh();
			}
		}
		else
		{
			serial = 0;
		}
	}

	public JComponent getView()
	{
		return this;
	}

	/**
	 * Return the current temporary modifier pane
	 * @return The temp mod pane, if any
	 */
	InfoTempMod getTempModPane()
	{
		return tempmod;
	}
	
	/**
	 * delegates filter related stuff to gear tab
	 * @return available filters
	 **/
	public List getAvailableFilters()
	{
		return gear.getAvailableFilters();
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @param mode
	 **/
	public void setFilterMode(int mode)
	{
		if (getSelectedIndex() == GEAR_INDEX)
		{
			gear.setFilterMode(mode);
		}
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @return filter mode
	 **/
	public int getFilterMode()
	{
		if (getSelectedIndex() == GEAR_INDEX)
		{
			return gear.getFilterMode();
		}

		return FilterConstants.MATCH_ALL;
	}

	/**
	 * Selector
	 * @return InfoGear
	 **/
	public InfoGear getInfoGear()
	{
		return gear;
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @return true if any match is enabled
	 **/
	public boolean isMatchAnyEnabled()
	{
		if (getSelectedIndex() == GEAR_INDEX)
		{
			return gear.isMatchAnyEnabled();
		}

		return false;
	}

	/**
	 * Set the needs update flag for several other panels
	 * (Gear, Equipping, Resources and Tmp Mod)
	 * @param b
	 */
	public static void setNeedsUpdate(boolean b)
	{
		InfoGear.setNeedsUpdate(b);
		InfoEquipping.setNeedsUpdate(b);
		InfoResources.setNeedsUpdate(b);
		InfoTempMod.setNeedsUpdate(b);

		if (SettingsHandler.showNaturalWeaponTab())
		{
			InfoNaturalWeapons.setNeedsUpdate(b);
		}
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @return true if negate is enabled
	 **/
	public boolean isNegateEnabled()
	{
		if (getSelectedIndex() == GEAR_INDEX)
		{
			return gear.isNegateEnabled();
		}

		return false;
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @return removed filters
	 **/
	public List getRemovedFilters()
	{
		return gear.getRemovedFilters();
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @return selected filters
	 **/
	public List getSelectedFilters()
	{
		return gear.getSelectedFilters();
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @return selection mode
	 **/
	public int getSelectionMode()
	{
		if (getSelectedIndex() == GEAR_INDEX)
		{
			return gear.getSelectionMode();
		}

		return FilterConstants.DISABLED_MODE;
	}

	/**
	 * delegates filter related stuff to gear tab
	 **/
	public void initializeFilters()
	{
		gear.initializeFilters();

		gear.setKitFilter("GEAR");
	}

	/**
	 * delegates filter related stuff to gear tab
	 **/
	public void refreshFiltering()
	{
		gear.refreshFiltering();
	}

	private void initActionListeners()
	{
		gear.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		equipment.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		resources.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		tempmod.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());

		addComponentListener(new ComponentAdapter()
			{
				public void componentShown(ComponentEvent evt)
				{
					// As the selected child panel (gear, equipped etc) will not be
					// sending a message saying it is visible, we need to send one for it.
					ComponentEvent childEvent = new ComponentEvent(getSelectedComponent(),
								ComponentEvent.COMPONENT_SHOWN);
					PToolBar.getCurrentInstance().getComponentListener().componentShown(childEvent);
					refresh();
				}
			});

		addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
					if (getSelectedIndex() == GEAR_INDEX)
					{
						InfoGear.setNeedsUpdate(true);
					}
					else if (getSelectedIndex() == EQUIPPING_INDEX)
					{
					    InfoEquipping.setNeedsUpdate(true);
					}
					else if (getSelectedIndex() == RESOURCES_INDEX)
					{
					    InfoResources.setNeedsUpdate(true);
					}
					else if (getSelectedIndex() == TEMPMOD_INDEX)
					{
					    InfoTempMod.setNeedsUpdate(true);
					}
				}
			});
	}

	private void initComponents()
	{
		readyForRefresh = true;
		add(gear, GEAR_INDEX);
		setTitleAt(GEAR_INDEX, PropertyFactory.getString("in_Info" + gear.getName()));
		add(equipment, EQUIPPING_INDEX);
		setTitleAt(EQUIPPING_INDEX, PropertyFactory.getString("in_Info" + equipment.getName()));
		add(resources, RESOURCES_INDEX);
		setTitleAt(RESOURCES_INDEX, PropertyFactory.getString("in_Info" + resources.getName()));
		add(tempmod, TEMPMOD_INDEX);
		setTitleAt(TEMPMOD_INDEX, PropertyFactory.getString("in_Info" + tempmod.getName()));

		if (SettingsHandler.showNaturalWeaponTab())
		{
			naturalWeapons = new InfoNaturalWeapons(pc);
			add(naturalWeapons, NATURAL_INDEX);
			setTitleAt(NATURAL_INDEX, PropertyFactory.getString("in_Info" + naturalWeapons.getName()));
		}

		addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent evt)
				{
					refresh();
				}
			});
	}

}
