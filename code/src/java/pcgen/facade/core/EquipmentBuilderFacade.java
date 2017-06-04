/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.facade.core;

import pcgen.facade.util.ReferenceFacade;

import java.util.EnumSet;

import pcgen.facade.util.ListFacade;

/**
 * EquipmentBuilderFacade defines the methods that can be used to build up a 
 * piece of custom equipment.
 * 
 * 
 */
public interface EquipmentBuilderFacade
{
	/**
	 * EquipmentHead marks the ends of a weapon that may be customized. Most 
	 * weapons only have one head but quarterstaves or the like have two.
	 */
	public enum EquipmentHead
	{
		PRIMARY, SECONDARY;

		/**
		 * @return
		 */
		public boolean isPrimary()
		{
			return this == PRIMARY;
		}
	}
	
	public boolean addModToEquipment(EquipModFacade modifier, EquipmentHead head);
	
	public boolean removeModFromEquipment(EquipModFacade modifier, EquipmentHead head);
	
	public boolean setName(String name);
	
	public boolean setSProp(String sprop);
	
	public boolean setCost(String newCost);
	
	public boolean setWeight(String newWeight);
	
	public ListFacade<EquipModFacade> getAvailList(EquipmentHead head);
	
	public ListFacade<EquipModFacade> getSelectedList(EquipmentHead head);
	
	public EquipmentFacade getEquipment();

	/**
	 * Is the modifier able to be added to the item of equipment?
	 * @param eqModFacade The equipment modifier to be checked.
	 * @param head The equipment head that is being modified.
	 * @return True if it can be added, false if not.
	 */
	public boolean canAddModifier(EquipModFacade eqModFacade, EquipmentHead head);

	/**
	 * Can this item of equipment be resized?
	 * @return true if the item can be resized
	 */
	public boolean isResizable();

	/**
	 * @param newSize The new size for the equipment.
	 */
	public void setSize(SizeAdjustmentFacade newSize);

	/**
	 * @return A reference to the equipment's current size.
	 */
	public ReferenceFacade<SizeAdjustmentFacade> getSizeRef();
	
	/**
	 * @return The equipment heads which can be customized on this item of equipment.
	 */
	public EnumSet<EquipmentHead> getEquipmentHeads();

	/**
	 * @param newValue
	 * @return
	 */
	boolean setDamage(String newValue);

	/**
	 * @return
	 */
	public String getBaseItemName();

	/**
	 * @return
	 */
	public boolean isWeapon();

	/**
	 * @return
	 */
	public String getDamage();

}
