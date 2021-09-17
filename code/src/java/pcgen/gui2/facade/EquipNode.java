/*
 * Extracted from code that was
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
 */
package pcgen.gui2.facade;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.BodyStructure;
import pcgen.core.Equipment;
import pcgen.core.character.EquipSlot;
import pcgen.facade.core.EquipmentFacade;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code EquipNodeImpl} represents a node in the equipping
 * tree. It may be an item of equipment or a slot that may be filled.
 * EquipNodeImpl objects are immutable.
 */
public class EquipNode implements Comparable<EquipNode>
{
	public static enum NodeType
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
		EQUIPMENT
	}

	private static final NumberFormat FMT = new DecimalFormat("00");
	private final NodeType nodeType;
	private final BodyStructure bodyStructure;
	private Equipment equipment;
	EquipNode parent;
	private final String name;
	private EquipSlot slot;
	String idPath;
	private String order;
	boolean singleOnly = false;

	/**
	 * Create a new EquipNodeImpl instance representing a body structure.
	 *
	 * @param bodyStructure The part of the body.
	 * @param order The order of the body structure
	 */
	public EquipNode(BodyStructure bodyStructure, int order)
	{
		this.nodeType = NodeType.BODY_SLOT;
		this.bodyStructure = bodyStructure;
		this.name = bodyStructure.toString();
		this.order = FMT.format(order);
	}

	/**
	 * Create a new EquipNodeImpl instance representing an equipment slot.
	 * These are called phantom slots as they represent a location yet to
	 * be filled.
	 *
	 * @param parent The parent body structure node
	 * @param slot The equipment slot
	 * @param singleOnly Can the slot only ever have a single entry. e.g. weapon slots
	 */
	public EquipNode(EquipNode parent, EquipSlot slot, boolean singleOnly)
	{
		this.nodeType = NodeType.PHANTOM_SLOT;
		this.bodyStructure = parent.bodyStructure;
		this.slot = slot;
		this.name = slot.getSlotName();
		this.parent = parent;
		this.singleOnly = singleOnly;
	}

	/**
	 * Create a new EquipNodeImpl instance representing an item of equipment.
	 *
	 * @param parent The parent node, may be null for a body structure.
	 * @param slot The equipment slot the item is equipped to.
	 * @param equipment The equipment item, may be null.
	 * @param idPath The id of the path as used by the core.
	 */
	public EquipNode(EquipNode parent, EquipSlot slot, Equipment equipment, String idPath)
	{
		this.nodeType = NodeType.EQUIPMENT;
		this.bodyStructure = parent.bodyStructure;
		this.slot = slot;
		this.equipment = equipment;
		this.idPath = idPath;
		this.parent = parent;
		this.name = equipment.getDisplayName();
	}

	public BodyStructure getBodyStructure()
	{
		return bodyStructure;
	}

	public EquipmentFacade getEquipment()
	{
		return equipment;
	}

	public NodeType getNodeType()
	{
		return nodeType;
	}

	public EquipNode getParent()
	{
		return parent;
	}

	/**
	 * @return the slot
	 */
	public EquipSlot getSlot()
	{
		return slot;
	}

	/**
	 * @return the idPath
	 */
	String getIdPath()
	{
		return idPath;
	}

	/**
	 * @param idPath the idPath to set
	 */
	void setIdPath(String idPath)
	{
		this.idPath = idPath;
	}

	/**
	 * @return The key to be used for sorting EquipNodes
	 */
	String getSortKey()
	{
		StringBuilder sortKey = new StringBuilder(50);
		if (parent != null)
		{
			sortKey.append(parent.getSortKey());
		}
		switch (nodeType)
		{
			case BODY_SLOT -> sortKey.append(order);
			case PHANTOM_SLOT -> {
				sortKey.append("|");
				sortKey.append(slot.getSlotName());
			}
			case EQUIPMENT -> {
				sortKey.append("|");
				String objKey = equipment.get(StringKey.SORT_KEY);
				if (objKey == null)
				{
					objKey = equipment.getDisplayName();
				}
				sortKey.append(objKey);
			}
			default -> {
			}
			//Case not caught, should this cause an error?
		}
		return sortKey.toString();
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public int compareTo(EquipNode other)
	{
		String orderThis = getOrder(this);
		String orderOther = getOrder(other);

		if (!orderThis.equals(orderOther))
		{
			return orderThis.compareTo(orderOther);
		}
		if ((getIdPath() != null) && (other.getIdPath() != null))
		{
			return idPath.compareTo(other.idPath);
		}
		return getSortKey().compareTo(other.getSortKey());
	}

	/**
	 * Retrieve the order of the node, that is the top level 
	 * order of the body structures. 
	 * @param equipNodeImpl The node to be examined.
	 * @return The order applicable to the node.
	 */
	private String getOrder(EquipNode equipNodeImpl)
	{
		if (StringUtils.isNotBlank(equipNodeImpl.order))
		{
			return equipNodeImpl.order;
		}
		EquipNode enParent = equipNodeImpl.parent;
		if (enParent != null)
		{
			return getOrder(enParent);
		}
		return "";
	}

}
