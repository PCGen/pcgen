/*
 * EquipmentSetFacade.java
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
 * Created on Aug 18, 2008, 2:00:52 PM
 */
package pcgen.core.facade;

import java.util.EventListener;
import java.util.EventObject;
import pcgen.core.facade.util.ListFacade;

/**
 * <code>EquipmentSetFacade</code>defines the interface layer between the UI 
 * and the pcgen core for managing how equipment is worn or placed (t.e. 
 * equipped). There can be multiple instance of this facade per character,
 * each representing a configuration of equipped gear (e.g. dungeon, boat, 
 * camp).
 *
 * <br/>
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *  
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 * @version $Revision: $
 */
public interface EquipmentSetFacade
{

	public boolean isContainer(EquipmentFacade equipment);

	/**
	 * This list contains the equipment currently equipped and how many of them 
	 * are equipped. The quantity for each item in the list reflects the number 
	 * of such items that are equipped on the character as a whole, meaning that 
	 * even if that item is equipped in multiple places on the character the 
	 * list should show a value in quantity that is the sum of these locations 
	 * and quantities at these locations.
	 *  
	 * @return The list of equipped items. 
	 */
	public EquipmentListFacade getEquippedItems();

	public EquipmentFacade addEquipment(EquipNode node, EquipmentFacade equipment, int quantity);

	public EquipmentFacade removeEquipment(EquipNode node, int quantity);

	public void removeAllEquipment();

	public ListFacade<EquipNode> getNodes();

	public int getQuantity(EquipNode node);

	public String getLocation(EquipNode node);

	/**
	 * This method test whether a piece of equipment can be equipped
	 * at a particular equipment path. This is not meant to check whether
	 * the character fulfills the prerequisites of this item, but just
	 * simply if equipping of this item would violate equipment slot limitations
	 * or if the item is suited to be put in this path. The method must also take
	 * into account that some containers accept only certain equipment
	 * (ie. crossbows only accept bolts) 
	 * @param node the node to the container
	 * @param equipment the equipment that we want to check
	 * @return true if the equipment can be placed in the location.
	 */
	public boolean canEquip(EquipNode node, EquipmentFacade equipment);
	
	public ReferenceFacade<String> getNameRef();

	public void setName(String name);

	public void addEquipmentTreeListener(EquipmentTreeListener listener);

	public void removeEquipmentTreeListener(EquipmentTreeListener listener);

	/**
	 * Identify the preferred location to place the item
	 * @param equipment The item to be checked
	 * @return The name of the location
	 */
	public String getPreferredLoc(EquipmentFacade equipment);

	/**
	 * Retrieve the name of a location in which the item of equipment
	 * is either equipped or could be equipped. A currently equipped 
	 * location is returned by preference. For items in multiple locations 
	 * the first encountered location is reported.
	 * @param equip The item of equipment.
	 * @return The name of the location
	 */
	public String getLocation(EquipmentFacade equip);

	public static interface EquipmentTreeListener extends EventListener
	{

		public void quantityChanged(EquipmentTreeEvent e);

	}

	public static class EquipmentTreeEvent extends EventObject
	{

		private EquipNode node;

		public EquipmentTreeEvent(Object source, EquipNode node)
		{
			super(source);
			this.node = node;
		}

		public EquipNode getNode()
		{
			return node;
		}

	}

	public static interface EquipNode extends Comparable<EquipNode>
	{

		public enum NodeType
		{

			BODY_SLOT, PHANTOM_SLOT, EQUIPMENT;
		}

		public NodeType getNodeType();

		public EquipNode getParent();

		public EquipmentFacade getEquipment();

		public BodyStructureFacade getBodyStructure();

	}

}
