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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pcgen.core.GameMode;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.PToolBar;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.Filterable;
import pcgen.gui.filter.PObjectFilter;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

/**
 * <code>InfoInventory</code><br>
 * @author Thomas Behr 16-09-02
 * @version $Revision$
 */
public final class InfoInventory extends JTabbedPane implements Filterable,
		CharacterInfoTab
{
	static final long serialVersionUID = -4186874622211290063L;

	private static final Tab tab = Tab.INVENTORY;

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
		setName(tab.toString());

		initComponents();
		initActionListeners();

		InfoGear.setNeedsUpdate(true);
	}

	public void setPc(PlayerCharacter pc)
	{
		if (this.pc != pc || pc.getSerial() > serial)
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
		return SettingsHandler.getPCGenOption(".Panel.Inventory.Order", tab
			.ordinal());
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Inventory.Order", order);
	}

	public String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(tab);
	}

	public boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(tab);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List<String> getToDos()
	{
		List<String> toDoList = new ArrayList<String>();
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
		if (pc.getSerial() > serial)
		{
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public void forceRefresh()
	{
		if (readyForRefresh)
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
	public List<PObjectFilter> getAvailableFilters()
	{
		return gear.getAvailableFilters();
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @param mode
	 **/
	public void setFilterMode(int mode)
	{
		if (getSelectedIndex() == Tab.GEAR.index())
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
		if (getSelectedIndex() == Tab.GEAR.index())
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
		if (getSelectedIndex() == Tab.GEAR.index())
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
		if (getSelectedIndex() == Tab.GEAR.index())
		{
			return gear.isNegateEnabled();
		}

		return false;
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @return removed filters
	 **/
	public List<PObjectFilter> getRemovedFilters()
	{
		return gear.getRemovedFilters();
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @return selected filters
	 **/
	public List<PObjectFilter> getSelectedFilters()
	{
		return gear.getSelectedFilters();
	}

	/**
	 * delegates filter related stuff to gear tab
	 * @return selection mode
	 **/
	public int getSelectionMode()
	{
		if (getSelectedIndex() == Tab.GEAR.index())
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
		gear.addComponentListener(PToolBar.getCurrentInstance()
			.getComponentListener());
		equipment.addComponentListener(PToolBar.getCurrentInstance()
			.getComponentListener());
		resources.addComponentListener(PToolBar.getCurrentInstance()
			.getComponentListener());
		tempmod.addComponentListener(PToolBar.getCurrentInstance()
			.getComponentListener());

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				// As the selected child panel (gear, equipped etc) will not be
				// sending a message saying it is visible, we need to send one for it.
				ComponentEvent childEvent =
						new ComponentEvent(getSelectedComponent(),
							ComponentEvent.COMPONENT_SHOWN);
				PToolBar.getCurrentInstance().getComponentListener()
					.componentShown(childEvent);
				refresh();
			}
		});

		addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				if (getSelectedIndex() == Tab.GEAR.index())
				{
					InfoGear.setNeedsUpdate(true);
				}
				else if (getSelectedIndex() == Tab.EQUIPPING.index())
				{
					InfoEquipping.setNeedsUpdate(true);
				}
				else if (getSelectedIndex() == Tab.RESOURCES.index())
				{
					InfoResources.setNeedsUpdate(true);
				}
				else if (getSelectedIndex() == Tab.TEMPBONUS.index())
				{
					InfoTempMod.setNeedsUpdate(true);
				}
			}
		});
	}

	private void initComponents()
	{
		readyForRefresh = true;
		add(gear, Tab.GEAR.index());
		setTitleAt(Tab.GEAR.index(), LanguageBundle.getString("in_Info"
			+ gear.getName()));
		add(equipment, Tab.EQUIPPING.index());
		setTitleAt(Tab.EQUIPPING.index(), LanguageBundle.getString("in_Info"
			+ equipment.getName()));
		add(resources, Tab.RESOURCES.index());
		setTitleAt(Tab.RESOURCES.index(), LanguageBundle.getString("in_Info"
			+ resources.getName()));
		add(tempmod, Tab.TEMPBONUS.index());
		setTitleAt(Tab.TEMPBONUS.index(), LanguageBundle.getString("in_Info"
			+ tempmod.getName()));

		if (SettingsHandler.showNaturalWeaponTab())
		{
			naturalWeapons = new InfoNaturalWeapons(pc);
			add(naturalWeapons, Tab.NATWEAPONS.index());
			setTitleAt(Tab.NATWEAPONS.index(), LanguageBundle
				.getString("in_Info" + naturalWeapons.getName()));
		}

		addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
				refresh();
			}
		});
	}

	/**
	 * @see pcgen.gui.filter.Filterable#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		final int ind = getSelectedIndex();
		if (ind == Tab.GEAR.index())
		{
			return gear.accept(aPC, pObject);
		}
		else if (ind == Tab.EQUIPPING.index())
		{
			return equipment.accept(aPC, pObject);
		}
		else if (ind == Tab.RESOURCES.index())
		{
			return resources.accept(aPC, pObject);
		}
		else if (ind == Tab.TEMPBONUS.index())
		{
			return tempmod.accept(aPC, pObject);
		}

		return false;
	}

}
