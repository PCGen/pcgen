/*
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
 */
package pcgen.facade.core;

import java.util.EventListener;
import java.util.EventObject;

import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;

/**
 * {@code EquipmentSetFacade}defines the interface layer between the UI
 * and the pcgen core for managing how equipment is worn or placed (t.e. 
 * equipped). There can be multiple instance of this facade per character,
 * each representing a configuration of equipped gear (e.g. dungeon, boat, 
 * camp).
 *
 *  
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

	/**
	 * Insert an item of equipment into a specific location in the equipment set. 
	 * @param node The parent node to which the equipment will be added.
	 * @param equipment The item of equipment to be added.
	 * @param quantity The number of instances of the item to be added.
	 * @param beforeNode The node above which the equipment should be inserted.
	 * @return The item of equipment that was actually added. 
	 */
	public EquipmentFacade addEquipment(EquipNode node, EquipmentFacade equipment, int quantity, EquipNode beforeNode);

	public EquipmentFacade removeEquipment(EquipNode node, int quantity);

	public void removeAllEquipment();

	/**
	 * Retrieve the list of nodes to be displayed in the tree. Nodes could be
	 * <ul>
	 * <li>empty slots (phantom slots)</li>
	 * <li>a part of the body (body slot)</li>
	 * <li>an item of equipment (equipment slot)</li>
	 * </ul>
	 * 
	 * @return The list of equipment nodes to be displayed on the equipped tree. 
	 */
	public ListFacade<EquipNode> getNodes();

	public int getQuantity(EquipNode node);

	public String getLocation(EquipNode node);

	/**
	 * This method tests whether a piece of equipment can be equipped
	 * at a particular equipment path. This is not meant to check whether
	 * the character fulfills the prerequisites of this item, but just
	 * simply if equipping of this item would violate equipment slot limitations
	 * or if the item is suited to be put in this path. The method must also take
	 * into account that some containers accept only certain equipment
	 * (i.e. crossbows only accept bolts) 
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

	@SuppressWarnings("serial")
	public static class EquipmentTreeEvent extends EventObject
	{

		private final EquipNode node;

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
			/** This is a body location which can contain other slots rather 
			 * than equipment. */
			BODY_SLOT,

			/** This is a node which shows an empty slot that takes a specific 
			 * equipment type (e.g. Ring). It is removed from the tree when the 
			 * slot it represents is full and added back in when there is space 
			 * in the slot. */
			PHANTOM_SLOT,

			/** This is a node representing an equipped item of equipment. */
			EQUIPMENT;
		}

		public NodeType getNodeType();

		public EquipNode getParent();

		public EquipmentFacade getEquipment();

		public BodyStructureFacade getBodyStructure();

	}

	/**
	 * Move the equipment a certain number of slots up (negative) or down 
	 * (positive) in the tree.
	 * 
	 * @param node The equipment node to be moved.
	 * @param numRowsToMove The number of rows to move.
	 * @return true if the move was successful, false if it could not be made.
	 */
	public boolean moveEquipment(EquipNode node, int numRowsToMove);

	/**
	 * Sort the contents of the supplied node in alphabetical order.
	 * @param parentNode The node, may be a container or a general 
	 * body slot.
	 * @return true if the sort was successful, false if it could not be done.
	 */
	public boolean sortEquipment(EquipNode parentNode);

}
