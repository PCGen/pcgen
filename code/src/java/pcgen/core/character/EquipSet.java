/*
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
package pcgen.core.character;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.EquipmentLocation;
import pcgen.core.BodyStructure;
import pcgen.core.BonusManager;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.BonusObj;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.gui2.facade.EquipNode;
import pcgen.util.Logging;

/**
 * Every item of equipment that is contained in an EquipSet has an ID with the following
 * structure.  X.Y, where X is the parent ID (and may also be a period separated list)
 * and Y is this equipment's id.  All EquipSets have 0 as their ultimate parent.
 *
 * This means that the ID for a "root" EquipSet looks like: 0.1
 * 0 is the parent ID,
 * 1 the equipset ID
 *
 * Each piece of equipment that is part of this EquipSet has a parent ID of 0.1
 *
 * The ID path for an Equipset that is the root of an Equipset tree
 * id_path for a "root" EquipSet looks like: 0.1
 * where
 * 0 == my parent (none)
 * 1 == my Id
 *
 * a Child id_path looks like this: 0.1.3
 * where
 * 0 == root
 * 1 == my parent
 * 3 == my Id
 */

/*
 * the Structure of each EQUIPSET is as follows.
 *
 * EQUIPSET: id_path : name : value : item
 *
 * id_path = a . delimited string that denotes parent/child relationship
 * name = name of EquipSet or item this represents
 (and is used to define uniquiness for compareTo)
 * value = Name of the Equipment stored in this item
 * item = Equipment item stored (optional)
 * qty = number of items this equipset contains (all same item)
 */

/**
 * {@code EquipSet.java}
 */
public final class EquipSet implements Comparable<EquipSet>, Cloneable
{
	/** The root path of the default equipment set. */
	public static final String DEFAULT_SET_PATH = "0.1";

	private Equipment eq_item;
	private Float qty = 1.0f;
	private Map<BonusObj, BonusManager.TempBonusInfo> tempBonusBySource = new IdentityHashMap<>();

	private String id_path = Constants.EMPTY_STRING;
	private String name = Constants.EMPTY_STRING;
	private String note = Constants.EMPTY_STRING;
	private String value = Constants.EMPTY_STRING;
	private boolean useTempBonuses = true;

	/** This list of equipment nodes to be displayed on the equipped tree. */
	private DefaultListFacade<EquipNode> nodeList;

	private Map<EquipSlot, EquipNode> equipSlotNodeMap;

	private Map<String, EquipNode> naturalWeaponNodes;

	/**
	 * Retrieve the id from a path, that is the last number in the sequence.
	 * e.g. The id of the path 0.1.17 is 17.  
	 * @param path The path to be interpreted.
	 * @return The numeric id
	 */
	public static int getIdFromPath(String path)
	{
		int id = 0;

		try
		{
			final StringTokenizer aTok = new StringTokenizer(path, Constants.EQUIP_SET_PATH_SEPARATOR, false);

			while (aTok.hasMoreTokens())
			{
				id = Integer.parseInt(aTok.nextToken());
			}
		}
		catch (NullPointerException e)
		{
			Logging.errorPrint("Error in EquipSet.getId " + path, e);
		}

		return id;
	}

	/**
	 * Retrieve the parent path of this path, that is the sequence without 
	 * the last number.
	 * e.g. The parent of the path 0.1.17 is 0.1  
	 * @param path The path to be interpreted.
	 * @return The parent path.
	 */
	public static String getParentPath(String path)
	{
		int idx = path.lastIndexOf(Constants.EQUIP_SET_PATH_SEPARATOR);
		if (idx < 0)
		{
			return "";
		}

		return path.substring(0, idx);
	}

	/**
	 * Retrieve the depth from a path, that is the number of numbers in the sequence.
	 * e.g. The depth of the path 0.1.17 is 3.  
	 * @param path The path to be interpreted.
	 * @return The numeric depth
	 */
	public static int getPathDepth(String path)
	{
		try
		{
			final StringTokenizer aTok = new StringTokenizer(path, Constants.EQUIP_SET_PATH_SEPARATOR, false);
			return aTok.countTokens();
		}
		catch (NullPointerException e)
		{
			Logging.errorPrint("Error in EquipSet.getPathDepth", e);
		}

		return 0;
	}

	/**
	 * Constructor
	 * @param id
	 * @param aName
	 */
	public EquipSet(final String id, final String aName)
	{
		id_path = id;
		name = aName;
	}

	/**
	 * Constructor
	 * @param id
	 * @param aName
	 * @param aValue
	 * @param item
	 */
	public EquipSet(final String id, final String aName, final String aValue, final Equipment item)
	{
		id_path = id;
		name = aName;
		value = aValue;
		eq_item = item;
	}

	/**
	 * our Id is the last number on the id_path
	 * if id_path is "0.2.8.15", our id is 15
	 * @return id
	 **/
	public int getId()
	{
		return EquipSet.getIdFromPath(id_path);
	}

	/**
	 * Set ID Path
	 * @param x
	 */
	public void setIdPath(final String x)
	{
		id_path = x;
	}

	/**
	 * Get Id Path
	 * @return id_path
	 */
	public String getIdPath()
	{
		return id_path;
	}

	/**
	 * Set Item
	 * @param item
	 */
	public void setItem(final Equipment item)
	{
		eq_item = item;
	}

	/**
	 * Get item
	 * @return eq_item
	 */
	public Equipment getItem()
	{
		return eq_item;
	}

	/**
	 * Set name
	 * @param x
	 */
	public void setName(final String x)
	{
		name = x;
	}

	/**
	 * name is our EquipSet name if we are a root node
	 * or it is the name of the location for the equipment we are holding
	 * @return name
	 **/
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the player added note to aString
	 * @param aString
	 **/
	public void setNote(final String aString)
	{
		note = aString;
	}

	/**
	 * Get note
	 * @return note
	 */
	public String getNote()
	{
		return note;
	}

	/**
	 * the Parent Id Path is everything except our Id
	 * if id_path is "0.2.8.15", our Parent Id is "0.2.8"
	 * @return parent id path
	 **/
	public String getParentIdPath()
	{
		final StringBuilder buf = new StringBuilder(50);

		// get all tokens and include the delimiter
		try
		{
			final StringTokenizer aTok = new StringTokenizer(id_path, Constants.EQUIP_SET_PATH_SEPARATOR, true);

			// get all tokens (and delimiters) except last two
			for (int i = aTok.countTokens() - 2; i > 0; i--)
			{
				buf.append(aTok.nextToken());
			}
		}
		catch (NullPointerException e)
		{
			Logging.errorPrint("Error in EquipSet.getParentIdPath", e);
		}

		return buf.toString();
	}

	/**
	 * Set's the number of items in this equipset
	 * @param x
	 **/
	public void setQty(final Float x)
	{
		qty = x;
	}

	/**
	 * Get quantity
	 * @return quantity
	 */
	public Float getQty()
	{
		return qty;
	}

	/**
	 * return the root id of the EquipSet
	 * If our id_path is "0.2.8.15", the root would be "0.2"
	 * @return root id path
	 **/
	public String getRootIdPath()
	{
		final StringBuilder buf = new StringBuilder(50);
		final StringTokenizer aTok = new StringTokenizer(id_path, Constants.EQUIP_SET_PATH_SEPARATOR, false);
		final String result;

		if (aTok.countTokens() < 2)
		{
			result = Constants.EMPTY_STRING;
		}
		else
		{
			// get first two tokens and delimiter
			buf.append(aTok.nextToken());
			buf.append('.');
			buf.append(aTok.nextToken());

			result = buf.toString();
		}

		return result;
	}

	/**
	 * Set temp bonus list
	 * @param aList
	 */
	public void setTempBonusList(final Map<BonusObj, BonusManager.TempBonusInfo> aList)
	{
		tempBonusBySource = aList;
	}

	/**
	 * a List of BonusObj's
	 * @return temp bonus list
	 **/
	public Map<BonusObj, BonusManager.TempBonusInfo> getTempBonusMap()
	{
		return tempBonusBySource;
	}

	/**
	 * Should apply temporary bonuses to this equipset?
	 * @param aBool
	 **/
	public void setUseTempMods(final boolean aBool)
	{
		useTempBonuses = aBool;
	}

	/**
	 * Return TRUE if using temp mods
	 * @return TRUE if using temp mods
	 */
	public boolean getUseTempMods()
	{
		return useTempBonuses;
	}

	/**
	 * Set value
	 * @param x
	 */
	public void setValue(final String x)
	{
		value = x;
	}

	/**
	 * value is null for root nodes or
	 * it is the name of the piece of equipment we are holding
	 * @return value
	 **/
	public String getValue()
	{
		return value;
	}

	/**
	 * Clear the temp bonus list
	 */
	public void clearTempBonusList()
	{
		tempBonusBySource.clear();
	}

	/**
	 * Creates a duplicate of this equip set. Note that this is
	 * a deep clone - all equipment associated with this EquipSet
	 * will also be cloned.
	 *
	 * @return A new equip set, identical to this one.
	 */
	@Override
	public EquipSet clone()
	{
		EquipSet eqSet = null;

		try
		{
			eqSet = (EquipSet) super.clone();

			if (eq_item != null)
			{
				eqSet.eq_item = eq_item.clone();
			}

			if (qty != null)
			{
				eqSet.qty = qty;
			}
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.APPLICATION_NAME, MessageType.ERROR);
		}

		return eqSet;
	}

	/**
	 * Compares the path ids of each object to determine relative order.
	 * 
	 * @param obj The EquipSet to compare with.
	 *  
	 * @return a negative integer, zero, or a positive integer as this EquipSet 
	 * is less than, equal to, or greater than the specified EquipSet.
	 */
	@Override
	public int compareTo(final EquipSet obj)
	{
		return id_path.compareToIgnoreCase(obj.id_path);
	}

	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * true if temp bonus list is not empty.
	 * @return true if temp bonus list is not empty
	 */
	public boolean useTempBonusList()
	{
		return !tempBonusBySource.isEmpty();
	}

	/**
	 * Apply this EquipSet to a PlayerCharacter object.
	 * @param aPC the PC to equip the item on
	 */
	public void equipItem(PlayerCharacter aPC)
	{
		final StringTokenizer aTok = new StringTokenizer(getIdPath(), Constants.EQUIP_SET_PATH_SEPARATOR);

		// if the eSet.getIdPath() is longer than 3
		// it's inside a container, don't try to equip
		if (aTok.countTokens() > Constants.ID_PATH_LENGTH_FOR_NON_CONTAINED)
		{
			// Get back to carried/equipped/not carried to determine correct location
			StringBuilder rootPath = new StringBuilder(40);
			for (int i = 0; i < Constants.ID_PATH_LENGTH_FOR_NON_CONTAINED; i++)
			{
				if (i > 0)
				{
					rootPath.append(".");
				}
				rootPath.append(aTok.nextToken());
			}
			EquipSet rootSet = aPC.getEquipSetByIdPath(rootPath.toString());
			if (rootSet != null && rootSet.name.startsWith(Constants.EQUIP_LOCATION_CARRIED))
			{
				eq_item.addEquipmentToLocation(qty, EquipmentLocation.CARRIED_NEITHER, false, aPC);
			}
			else if (rootSet != null && rootSet.name.startsWith(Constants.EQUIP_LOCATION_NOTCARRIED))
			{
				eq_item.addEquipmentToLocation(qty, EquipmentLocation.NOT_CARRIED, false, aPC);
			}
			else if (rootSet != null && rootSet.name.startsWith(Constants.EQUIP_LOCATION_EQUIPPED))
			{
				eq_item.addEquipmentToLocation(qty, EquipmentLocation.EQUIPPED_NEITHER, false, aPC);
			}
			else
			{
				eq_item.addEquipmentToLocation(qty, EquipmentLocation.CONTAINED, false, aPC);
			}
		}
		else if (name.startsWith(Constants.EQUIP_LOCATION_CARRIED))
		{
			eq_item.addEquipmentToLocation(qty, EquipmentLocation.CARRIED_NEITHER, false, aPC);
		}
		else if (name.startsWith(Constants.EQUIP_LOCATION_NOTCARRIED))
		{
			eq_item.addEquipmentToLocation(qty, EquipmentLocation.NOT_CARRIED, false, aPC);
		}
		else if (eq_item.isWeapon())
		{
			if (name.equals(Constants.EQUIP_LOCATION_PRIMARY) || name.equals(Constants.EQUIP_LOCATION_NATURAL_PRIMARY))
			{
				eq_item.addWeaponToLocation(qty, EquipmentLocation.EQUIPPED_PRIMARY, aPC);
			}
			else if (name.startsWith(Constants.EQUIP_LOCATION_SECONDARY)
				|| name.equals(Constants.EQUIP_LOCATION_NATURAL_SECONDARY))
			{
				eq_item.addWeaponToLocation(qty, EquipmentLocation.EQUIPPED_SECONDARY, aPC);
			}
			else if (name.equals(Constants.EQUIP_LOCATION_BOTH))
			{
				eq_item.addWeaponToLocation(qty, EquipmentLocation.EQUIPPED_BOTH, aPC);
			}
			else if (name.equals(Constants.EQUIP_LOCATION_DOUBLE))
			{
				eq_item.addWeaponToLocation(qty, EquipmentLocation.EQUIPPED_TWO_HANDS, aPC);
			}
			else if (name.equals(Constants.EQUIP_LOCATION_UNARMED))
			{
				eq_item.addWeaponToLocation(qty, EquipmentLocation.EQUIPPED_NEITHER, aPC);
			}
			else if (name.equals(Constants.EQUIP_LOCATION_TWOWEAPONS))
			{
				Float quantity = (qty.doubleValue() < 2.0) ? 2.0f : qty;

				setQty(quantity);
				eq_item.addWeaponToLocation(quantity, EquipmentLocation.EQUIPPED_TWO_HANDS, aPC);
			}
			else if (name.equals(Constants.EQUIP_LOCATION_SHIELD))
			{
				eq_item.addWeaponToLocation(qty, EquipmentLocation.EQUIPPED_NEITHER, aPC);
			}
		}
		else
		{
			eq_item.addEquipmentToLocation(qty, EquipmentLocation.EQUIPPED_NEITHER, true, aPC);
		}
	}

	/**
	 * If there is a note set in this Equipment set, then add it to the contained equipment.
	 */
	public void addNoteToItem()
	{
		final String aNote = getNote();

		if ((aNote != null) && (!aNote.isEmpty()))
		{
			getItem().setNote(aNote);
		}
	}

	/**
	 * Is this EquipSet part of the Equipment set located at rootId.
	 *
	 * @param rootId The id to test
	 * @return true if eqSet is a child of rootId
	 */
	public boolean isPartOf(String rootId)
	{
		// rootId = 0.1.
		// parentIdPath = 0.10.
		// OR
		// rootId = 0.10.
		// parentIdPath = 0.1.
		final String abCalcId = rootId + Constants.EQUIP_SET_PATH_SEPARATOR;
		final String abParentId = getParentIdPath() + Constants.EQUIP_SET_PATH_SEPARATOR;

		return abParentId.startsWith(abCalcId);
	}

	public DefaultListFacade<EquipNode> getNodeList()
	{
		return nodeList;
	}

	public void setNodeList(DefaultListFacade<EquipNode> nodeList)
	{
		this.nodeList = nodeList;
	}

	public Map<EquipSlot, EquipNode> getEquipSlotNodeMap()
	{
		return equipSlotNodeMap;
	}

	public void setEquipSlotNodeMap(Map<EquipSlot, EquipNode> equipSlotNodeMap)
	{
		this.equipSlotNodeMap = equipSlotNodeMap;
	}

	public Map<String, EquipNode> getNaturalWeaponNodes()
	{
		return naturalWeaponNodes;
	}

	public void setNaturalWeaponNodes(Map<String, EquipNode> naturalWeaponNodes)
	{
		this.naturalWeaponNodes = naturalWeaponNodes;
	}

	/**
	 * Retrieve the preferred location for a natural weapon. Will return null
	 * for non natural weapon equipment items.
	 *
	 * @param pc which PlayerCharacter has the item
	 * @param item The equipment item to be checked.
	 * @return The preferred natural equip node, or null if not applicable.
	 */
	public EquipNode getNatWeaponLoc(PlayerCharacter pc, Equipment item)
	{
		String locName = pc.getNaturalWeaponLocation(item);
		if (locName != null)
		{
			return naturalWeaponNodes.get(locName);
		}
		return null;
	}

	/**
	 * Check if the node is a valid location for the natural weapon to be equipped to.
	 * This allows primary natural weapons to be equipped to primary or secondary
	 * slots, but secondary weapons only too the secondary slot.
	 *
	 * @param node The node to be tested.
	 * @param equipment The natural weapon
	 * @param naturalLoc The natural weapon;s preferred slot.
	 * @return true if the node can take the natural weapon, false otherwise.
	 */
	public boolean validLocationForNaturalWeapon(EquipNode node, Equipment equipment, EquipNode naturalLoc)
	{
		if (equipment.isPrimaryNaturalWeapon())
		{
			return getNaturalWeaponNodes().containsValue(node);
		}
		return node.equals(naturalLoc);
	}

	public boolean canEquip(PlayerCharacter theCharacter, EquipNode node,
		Equipment item)
	{
		// Check for a required location (i.e. you can't carry a natural weapon)
		EquipNode requiredLoc = getNatWeaponLoc(theCharacter, item);
		if (requiredLoc != null)
		{
			return validLocationForNaturalWeapon(node, item, requiredLoc);
		}

		// Is this a container? Then check if the object can fit in
		if (node.getNodeType() == EquipNode.NodeType.EQUIPMENT)
		{
			EquipmentFacade parent = node.getEquipment();
			if ((parent instanceof Equipment) && ((Equipment) parent).isContainer())
			{
				// Check if it fits
				if (((Equipment) parent).canContain(theCharacter, item) == 1)
				{
					return true;
				}
			}
		}

		if (node.getNodeType() == EquipNode.NodeType.PHANTOM_SLOT)
		{
			// Check first for an already full or taken slot
			if (!getNodeList().containsElement(node))
			{
				return false;
			}
			EquipSlot slot = node.getSlot();
			if (slot.canContainType(item.getType()))
			{
				if (item.isWeapon())
				{
					final String slotName = slot.getSlotName();

					if (item.isUnarmed() && slotName.equals(Constants.EQUIP_LOCATION_UNARMED))
					{
						return true;
					}
					if (item.isShield() && slotName.equals(Constants.EQUIP_LOCATION_SHIELD))
					{
						return true;
					}

					// If it is outsized, they can't equip it to a weapon slot
					if (item.isWeaponOutsizedForPC(theCharacter))
					{
						return false;
					}

					if (slotName.startsWith(Constants.EQUIP_LOCATION_BOTH))
					{
						return true;
					}
					if (item.isMelee() && item.isDouble() && slotName.equals(Constants.EQUIP_LOCATION_DOUBLE))
					{
						return true;
					}
					if (item.isWeaponOneHanded(theCharacter))
					{
						if (slotName.equals(Constants.EQUIP_LOCATION_PRIMARY)
							|| slotName.startsWith(Constants.EQUIP_LOCATION_SECONDARY))
						{
							return true;
						}
					}

				}
				else
				{
					return true;
				}
			}
		}

		// Is this a body structure? Then check if the object be placed there
		if (node.getNodeType() == EquipNode.NodeType.BODY_SLOT)
		{
			BodyStructure root = node.getBodyStructure();
			if (root.isHoldsAnyType())
			{
				return !root.isForbidden(item.getTrueTypeList(false));
			}
		}

		// This item can't be equipped in this location
		return false;
	}

}
