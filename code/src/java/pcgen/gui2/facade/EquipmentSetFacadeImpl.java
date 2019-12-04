/*
 * Copyright James Dempsey, 2010
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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.Constants;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.HandsFacet;
import pcgen.cdom.util.CControl;
import pcgen.core.BodyStructure;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SystemCollections;
import pcgen.core.character.EquipSet;
import pcgen.core.character.EquipSlot;
import pcgen.core.display.CharacterDisplay;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.EquipmentListFacade;
import pcgen.facade.core.EquipmentListFacade.EquipmentListEvent;
import pcgen.facade.core.EquipmentListFacade.EquipmentListListener;
import pcgen.facade.core.EquipmentSetFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;

/**
 * The Class {@code EquipmentSetFacadeImpl} is an implementation of
 * the EquipmentSetFacade interface for the new user interface. It handles 
 * the interaction with the UI and the character with respect a grouping 
 * of the character's gear. This covers what is carried, what is not and 
 * where each item is located. As a result it also manages what items are 
 * deemed active.
 */
public class EquipmentSetFacadeImpl implements EquipmentSetFacade, EquipmentListListener, ListListener<EquipmentFacade>
{
	private final PlayerCharacter theCharacter;
	private final CharacterDisplay charDisplay;
	private EquipSet eqSet;
	private final UIDelegate delegate;
	private final TodoManager todoManager;
	private final DataSetFacade dataSet;
	private final EquipmentListFacadeImpl purchasedList;

	private final Collection<EquipmentTreeListener> listeners = new ArrayList<>();
	private DefaultReferenceFacade<String> name;
	private EquipmentListFacadeImpl equippedItemsList;
	private double totalWeight = 0;
	/** List of phantom nodes which are currently both empty and not able to contain equipment */
	private Set<EquipNode> hiddenPhantomNodes;
	private final CharacterFacadeImpl characterFacadeImpl;

	/**
	 * Create a new Equipment Set Facade implementation for an existing 
	 * equipset.
	 * 
	 * @param delegate The user interface delegate for notifying the user. 
	 * @param pc The character the set belongs to.
	 * @param eqSet The set.
	 * @param dataSet The datasets that the character is using.
	 * @param purchasedList The list of the charcter's purchased equipment.
	 * @param todoManager The user tasks tracker.
	 * @param characterFacadeImpl The UI facade for the character.
	 */
	public EquipmentSetFacadeImpl(UIDelegate delegate, PlayerCharacter pc, EquipSet eqSet, DataSetFacade dataSet,
		EquipmentListFacadeImpl purchasedList, TodoManager todoManager, CharacterFacadeImpl characterFacadeImpl)
	{
		this.delegate = delegate;
		this.theCharacter = pc;
		this.todoManager = todoManager;
		this.characterFacadeImpl = characterFacadeImpl;
		this.charDisplay = pc.getDisplay();
		this.dataSet = dataSet;
		this.purchasedList = purchasedList;
		initForEquipSet(eqSet);

		purchasedList.addEquipmentListListener(this);
		purchasedList.addListListener(this);
	}

	private void initForEquipSet(EquipSet equipSet)
	{
		this.eqSet = equipSet;
		name = new DefaultReferenceFacade<>(equipSet.getName());
		equippedItemsList = new EquipmentListFacadeImpl();
		setNaturalWeaponNodes(new HashMap<>());
		hiddenPhantomNodes = new HashSet<>();

		buildNodeList();

		List<EquipSet> equipList = new ArrayList<>(charDisplay.getEquipSet());
		Collections.sort(equipList);
		createNaturalWeaponSlots();
		updateNaturalWeaponSlots();
		updatePhantomSlots();
		addChildrenToPath(equipSet.getIdPath(), equipList, null);
	}

	private void buildNodeList()
	{
		setNodeList(new DefaultListFacade<>());
		setEquipSlotNodeMap(new LinkedHashMap<>());
		int index = 0;
		for (BodyStructure bodyStruct : dataSet.getEquipmentLocations())
		{
			String structString = bodyStruct.toString();
			EquipNode node = new EquipNode(bodyStruct, index++);
			getNodeList().addElement(node);

			// Add locations for this body structure
			for (EquipSlot slot : SystemCollections.getUnmodifiableEquipSlotList())
			{
				String bodyStructureName = slot.getBodyStructureName();
				final String hands = "HANDS";
				if ("Ring".equalsIgnoreCase(slot.getSlotName()) || "Fingers".equalsIgnoreCase(slot.getSlotName()))
				{
					bodyStructureName = hands;
				}
				if (bodyStructureName.equalsIgnoreCase(structString))
				{
					if (slot.canContainType("WEAPON"))
					{
						// Add phantom nodes for the various weapon slots
						if (getPCHands() > 0)
						{
							addEquipNodeForEquipSlot(node,
								createWeaponEquipSlot(slot, Constants.EQUIP_LOCATION_PRIMARY), true);
						}
						for (int i = 1; i < getPCHands(); ++i)
						{
							if (i > 1)
							{
								addEquipNodeForEquipSlot(node,
									createWeaponEquipSlot(slot, Constants.EQUIP_LOCATION_SECONDARY + " " + i), true);
							}
							else
							{
								addEquipNodeForEquipSlot(node,
									createWeaponEquipSlot(slot, Constants.EQUIP_LOCATION_SECONDARY), true);
							}
						}

						addEquipNodeForEquipSlot(node, createWeaponEquipSlot(slot, Constants.EQUIP_LOCATION_DOUBLE),
							true);
						addEquipNodeForEquipSlot(node, createWeaponEquipSlot(slot, Constants.EQUIP_LOCATION_BOTH),
							true);
						addEquipNodeForEquipSlot(node, createWeaponEquipSlot(slot, Constants.EQUIP_LOCATION_UNARMED),
							true);
					}
					else
					{
						addEquipNodeForEquipSlot(node, slot, false);
					}
				}
			}
		}
	}

	private int getPCHands()
	{
		String solverValue = theCharacter.getControl(CControl.CREATUREHANDS);
		if (solverValue == null)
		{
			return FacetLibrary.getFacet(HandsFacet.class).getHands(theCharacter.getCharID());
		}
		else
		{
			Object val = theCharacter.getGlobal(solverValue);
			return ((Number) val).intValue();
		}
	}

	private EquipSlot createWeaponEquipSlot(EquipSlot slot, String slotName)
	{
		EquipSlot wpnSlot = slot.clone();
		wpnSlot.setSlotName(slotName);
		return wpnSlot;
	}

	/**
	 * Create a new EquipNode for the slot and add it the node list.  
	 * @param bodyStructNode The parent body structure node
	 * @param slot The equipment slot
	 * @param singleOnly Can the slot only ever have a single entry. e.g. weapon slots
	 */
	private void addEquipNodeForEquipSlot(EquipNode bodyStructNode, EquipSlot slot, boolean singleOnly)
	{
		EquipNode slotNode = new EquipNode(bodyStructNode, slot, singleOnly);
		getNodeList().addElement(slotNode);
		getEquipSlotNodeMap().put(slot, slotNode);
	}

	/**
	 * Recursive method to build up a tree of EquipNodes. It finds from the 
	 * equipment list those children of the item specified by the idPath and
	 * adds them to the paths list. It will then do the same for each child 
	 * that was found. If the parent is null then all direct children will be 
	 * attached to the relevant body structure nodes. 
	 *  
	 * @param idPath The equipset id of the parent record.
	 * @param equipList The list of equipsets to be added.
	 * @param parent The parent node 
	 */
	private void addChildrenToPath(String idPath, List<EquipSet> equipList, EquipNode parent)
	{
		List<EquipNode> children = new ArrayList<>();

		// process all EquipNode Items
		for (int iSet = 0; iSet < equipList.size(); ++iSet)
		{
			EquipSet es = equipList.get(iSet);

			if (es.getParentIdPath().equals(idPath))
			{
				EquipSlot slot = Globals.getEquipSlotByName(es.getName());
				EquipNode slotNode = getEquipSlotNodeMap().get(slot);
				EquipNode parentNode = parent;
				if (parentNode == null)
				{
					if (slotNode != null)
					{
						parentNode = slotNode.getParent();
					}
					else
					{
						for (EquipNode scanNode : getNodeList())
						{
							if ((scanNode.getNodeType() == EquipNode.NodeType.BODY_SLOT)
								&& scanNode.toString().equals(es.getName()))
							{
								parentNode = scanNode;
								break;
							}
							if ((scanNode.getNodeType() == EquipNode.NodeType.PHANTOM_SLOT)
								&& scanNode.toString().equals(es.getName()))
							{
								parentNode = scanNode.getParent();
								slotNode = scanNode;
								slot = scanNode.getSlot();
								break;
							}
						}
					}
				}

				if (parentNode != null)
				{
					EquipNode node = new EquipNode(parentNode, slot, es.getItem(), es.getIdPath());
					getNodeList().addElement(node);
					if ((slotNode != null) && (slotNode.getNodeType() == EquipNode.NodeType.PHANTOM_SLOT)
						&& (getNumFreeSlots(slotNode) <= 0))
					{
						getNodeList().removeElement(slotNode);
						for (EquipNode inompatNode : getIncompatibleWeaponSlots(slotNode))
						{
							getNodeList().removeElement(inompatNode);
						}
					}

					updateTotalQuantity(es.getItem(), es.getItem().getQty().intValue());

					// add to list for recursive calls
					children.add(node);
				}
				else
				{
					Logging.errorPrint("Could not find parent for " + es.getName() + " for item " + es.getItem()
						+ " at path " + es.getIdPath());
				}

				// and remove from tempSetList so
				// it won't get processed again
				equipList.remove(es);
				--iSet;
			}
		}

		// Now process the children
		for (final EquipNode EquipNode : children)
		{
			addChildrenToPath(EquipNode.getIdPath(), equipList, EquipNode);
		}
	}

	/**
	 * Make this equipment set the active one.
	 */
	void activateEquipSet()
	{
		theCharacter.setCalcEquipSetId(eqSet.getIdPath());
		theCharacter.setCalcEquipmentList();
		updateOutputOrder();
		theCharacter.setUseTempMods(eqSet.getUseTempMods());
		theCharacter.calcActiveBonuses();
		theCharacter.setDirty(true);
	}

	/**
	 * Add the weight for the item to the total for the set.
	 *
	 * @param equip The equipment item being added
	 * @param quantity The number being added (or negative if being removed)
	 * @param root The BodyStructure in which the item is being placed or removed
	 */
	private void updateTotalWeight(Equipment equip, float quantity, BodyStructure root)
	{
		if (!Constants.EQUIP_LOCATION_NOTCARRIED.equals(root.toString()))
		{
			equip.setNumberCarried(equip.getCarried() + quantity);
			if (!Constants.EQUIP_LOCATION_CARRIED.equals(root.toString()))
			{
				equip.setNumberEquipped((int) (equip.getNumberEquipped() + quantity));
			}
		}

		theCharacter.setCalcEquipmentList();
		if (!Constants.EQUIP_LOCATION_NOTCARRIED.equals(root.toString()))
		{
			totalWeight = charDisplay.totalWeight();
		}
	}

	/**
	 * returns new id_Path with the last id one higher than the current
	 * highest id for EquipSets with the same ParentIdPath.
	 * TODO: This needs to be moved to the core.
	 * @param display The display interface for the character owning the equipset.
	 * @param parentSet The parent of the equipset that is being created, null if it a root set.
	 * @return new id path
	 **/
	static String getNewIdPath(CharacterDisplay display, EquipSet parentSet)
	{
		String pid = "0";
		int newID = 0;

		if (parentSet != null)
		{
			pid = parentSet.getIdPath();
		}

		for (EquipSet es : display.getEquipSet())
		{
			if (es.getParentIdPath().equals(pid) && (es.getId() > newID))
			{
				newID = es.getId();
			}
		}

		++newID;

		NumberFormat format = (parentSet != null) ? new DecimalFormat("00") : new DecimalFormat("0");
		return pid + '.' + format.format(newID);
	}

	/**
	 * Shift the equipment sets down to make room for an item to be inserted.
	 * @param parentSet The equipment set the item is being inserted into.
	 * @param startingNode The first equipment node to be moved down.
	 * @param origPathToNode A map of the equipment nodes to their original paths.
	 * @param origPathToEquipSet A map of the equipment sets to their original paths.
	 * @return The path which has been available.
	 */
	private String shiftEquipSetsDown(EquipSet parentSet, EquipNode startingNode,
		Map<String, EquipNode> origPathToNode, Map<String, EquipSet> origPathToEquipSet)
	{
		String pid = "0";
		NumberFormat format = (parentSet != null) ? new DecimalFormat("00") : new DecimalFormat("0");

		if (parentSet != null)
		{
			pid = parentSet.getIdPath();
		}
		//Logging.errorPrint("Moving children of " + parentSet + " down, starting with " + startingNode + " .");

		String startingPath = startingNode.idPath;
		int startingId = EquipSet.getIdFromPath(startingPath);
		for (Map.Entry<String, EquipNode> entry : origPathToNode.entrySet())
		{
			String origPath = entry.getKey();
			EquipNode node = entry.getValue();
			EquipSet es = origPathToEquipSet.get(origPath);

			int esId = es.getId();
			if (es.getParentIdPath().equals(pid))
			{
				if (esId >= startingId)
				{
					esId++;
				}
				String newPath = pid + '.' + format.format(esId);
				es.setIdPath(newPath);
				updateContainerPath(origPath, newPath, origPathToNode, origPathToEquipSet);
				node.setIdPath(newPath);
				getNodeList().modifyElement(node);
			}
		}

		String newPath = pid + '.' + format.format(startingId);
		return newPath;
	}

	/**
	 * Update the path of any items contained within an equipment item being moved.
	 *
	 * @param parentOrigPath The original path of the container.
	 * @param parentNewPath The new path of the container.
	 * @param origPathToNode The map of the equipment nodes by path.
	 * @param origPathToEquipSet The map of the equipment sets by path.
	 */
	private void updateContainerPath(String parentOrigPath, String parentNewPath,
		Map<String, EquipNode> origPathToNode, Map<String, EquipSet> origPathToEquipSet)
	{
		for (final Map.Entry<String, EquipSet> entry : origPathToEquipSet.entrySet())
		{
			String origItemPath = entry.getKey();
			EquipSet itemEs = entry.getValue();

			if (origItemPath.startsWith(parentOrigPath) && !origItemPath.equals(parentOrigPath))
			{
				String newItemPath = origItemPath.replace(parentOrigPath, parentNewPath);
				itemEs.setIdPath(newItemPath);
				EquipNode node = origPathToNode.get(origItemPath);
				if (node != null)
				{
					node.setIdPath(newItemPath);
					getNodeList().modifyElement(node);
				}
			}
		}
	}

	/**
	 * Create a map of the character's equipment nodes keyed on their current
	 * id paths.
	 * @return A map of id paths and the matching equipment nodes.
	 */
	private Map<String, EquipNode> buildPathNodeMap()
	{
		Map<String, EquipNode> pathMap = new HashMap<>();
		for (EquipNode node : getNodeList())
		{
			if (node.getIdPath() != null)
			{
				EquipNode eni = node;
				pathMap.put(eni.idPath, eni);
			}
		}
		return pathMap;
	}

	/**
	 * Create a map of the character's equipment sets keyed on their current
	 * id paths.
	 * @return A map of id paths and the matching equipment sets.
	 */
	private Map<String, EquipSet> buildPathEquipSetMap()
	{
		Map<String, EquipSet> esMap = new HashMap<>();
		for (EquipSet es : charDisplay.getEquipSet())
		{
			esMap.put(es.getIdPath(), es);
		}
		return esMap;
	}

	@Override
	public EquipmentFacade addEquipment(EquipNode node, EquipmentFacade equipment, int quantity)
	{
		return addEquipment(node, equipment, quantity, null);
	}

	@Override
	public EquipmentFacade addEquipment(EquipNode node, EquipmentFacade equipment, int quantity, EquipNode beforeNode)
	{
		if (!(equipment instanceof Equipment))
		{
			return null;
		}

		Equipment item = (Equipment) equipment;
		EquipNode targetNode = node;

		// Validate the item can go into the location.
		if (!canEquip(targetNode, equipment))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME, LanguageBundle
				.getFormattedString("in_equipCannotEquipToLocation", item.toString(), targetNode.toString()));
			return null;
		}

		EquipNode parent;
		EquipSet parentEs;
		EquipSlot equipSlot;
		String locName;
		switch (targetNode.getNodeType())
		{
			case BODY_SLOT:
				parent = targetNode;
				parentEs = eqSet;
				equipSlot = null;
				locName = parent.toString();
				break;

			case PHANTOM_SLOT:
				parent = targetNode.getParent();
				parentEs = eqSet;
				equipSlot = targetNode.getSlot();
				locName = equipSlot.toString();
				break;

			case EQUIPMENT:
				parent = targetNode;
				parentEs = charDisplay.getEquipSetByIdPath(parent.getIdPath());
				equipSlot = targetNode.getSlot();
				locName = parent.toString();
				break;

			default:
				// Should never occur
				return null;
		}

		// Check for adding more instances to an existing item, but don't merge containers
		if (!item.isContainer())
		{
			for (EquipNode existing : getNodeList())
			{
				if (parent.equals(existing.getParent()) && (existing.getNodeType() == EquipNode.NodeType.EQUIPMENT))
				{
					EquipNode existingImpl = existing;
					if ((equipSlot != null) && !equipSlot.equals(existingImpl.getSlot()))
					{
						continue;
					}

					Equipment existingItem = (Equipment) existing.getEquipment();
					if (existingItem.equals(item))
					{
						int totalQuantity = (int) (existingItem.getQty() + quantity);
						existingItem.setQty(totalQuantity);
						EquipSet es = charDisplay.getEquipSetByIdPath(existing.getIdPath());
						es.setQty(es.getQty() + quantity);
						updateTotalWeight(existingItem, quantity, parent.getBodyStructure());
						fireQuantityChanged(existing);
						updateTotalQuantity(existingItem, quantity);
						updateNaturalWeaponSlots();
						updatePhantomSlots();
						return existingItem;
					}
				}
			}
		}

		// Create equip set for the item
		String id;
		if (beforeNode != null)
		{
			id = shiftEquipSetsDown(parentEs, beforeNode, buildPathNodeMap(), buildPathEquipSetMap());
		}
		else
		{
			id = EquipmentSetFacadeImpl.getNewIdPath(charDisplay, parentEs);
		}
		Equipment newItem = item.clone();
		EquipSet newSet = new EquipSet(id, locName, newItem.getName(), newItem);
		newItem.setQty(quantity);
		newSet.setQty((float) quantity);
		theCharacter.addEquipSet(newSet);
		Equipment eqTarget = parentEs.getItem();
		if ((eqTarget != null) && eqTarget.isContainer())
		{
			eqTarget.insertChild(theCharacter, newItem);
			newItem.setParent(eqTarget);
		}

		// Create EquipNode for the item
		EquipNode itemNode = new EquipNode(parent, equipSlot, newItem, id);
		getNodeList().addElement(itemNode);
		if ((targetNode.getNodeType() == EquipNode.NodeType.PHANTOM_SLOT) && (getNumFreeSlots(targetNode) <= 0))
		{
			getNodeList().removeElement(targetNode);
			for (EquipNode inompatNode : getIncompatibleWeaponSlots(targetNode))
			{
				getNodeList().removeElement(inompatNode);
			}
		}

		updateTotalWeight(newItem, quantity, parent.getBodyStructure());
		updateTotalQuantity(newItem, quantity);
		updateNaturalWeaponSlots();
		updateOutputOrder();
		theCharacter.calcActiveBonuses();
		updatePhantomSlots();
		characterFacadeImpl.postEquippingUpdates();

		return newItem;
	}

	@Override
	public boolean moveEquipment(EquipNode node, int numRowsToMove)
	{
		// Confirm our assumptions
		if ((node.getBodyStructure() == null)
			|| (node.getNodeType() != EquipNode.NodeType.EQUIPMENT) || (node.getParent() == null))
		{
			return false;
		}
		if (numRowsToMove == 0)
		{
			return true;
		}
		BodyStructure bodyStruct = node.getBodyStructure();
		if (!bodyStruct.isHoldsAnyType())
		{
			return false;
		}
		EquipNode equipNode = node;

		List<EquipNode> orderedEquipNodes = new ArrayList<>(getNodeList().getContents());
		Collections.sort(orderedEquipNodes);

		// Get current location
		int currLoc = orderedEquipNodes.indexOf(node);
		if (currLoc < 0)
		{
			return false;
		}

		// Calculate new location
		EquipNode beforeNode;
		boolean addAsLastChildOfParent = false;
		if (numRowsToMove < 0)
		{
			beforeNode = scanBackForNewLoc(equipNode, orderedEquipNodes, numRowsToMove * -1, currLoc);
		}
		else
		{
			beforeNode = scanForwardForNewLoc(equipNode, orderedEquipNodes, numRowsToMove, currLoc);
			addAsLastChildOfParent = beforeNode == null;
		}

		// Move the equipment item
		Map<String, EquipNode> origPathToNode = buildPathNodeMap();
		Map<String, EquipSet> origPathToEquipSet = buildPathEquipSetMap();
		getNodeList().removeElement(equipNode);
		String origIdPath = equipNode.getIdPath();
		EquipSet parentEs = charDisplay.getEquipSetByIdPath(EquipSet.getParentPath(origIdPath));
		EquipSet nodeEs = charDisplay.getEquipSetByIdPath(origIdPath);
		String newIdPath;
		if (addAsLastChildOfParent)
		{
			newIdPath = EquipmentSetFacadeImpl.getNewIdPath(charDisplay, parentEs);
		}
		else
		{
			newIdPath = shiftEquipSetsDown(parentEs, beforeNode, origPathToNode, origPathToEquipSet);
		}
		nodeEs.setIdPath(newIdPath);
		equipNode.setIdPath(newIdPath);
		getNodeList().addElement(equipNode);

		// Update children of the item
		updateContainerPath(origIdPath, newIdPath, origPathToNode, origPathToEquipSet);

		return true;
	}

	@Override
	public boolean sortEquipment(EquipNode parentNode)
	{
		// Confirm our assumptions
		if ((parentNode.getBodyStructure() == null)
			|| ((parentNode.getNodeType() != EquipNode.NodeType.EQUIPMENT) && (parentNode.getNodeType() != EquipNode.NodeType.BODY_SLOT)))
		{
			return false;
		}
		BodyStructure bodyStruct = parentNode.getBodyStructure();
		if (!bodyStruct.isHoldsAnyType())
		{
			return false;
		}

		String pid = parentNode.idPath;
		boolean isBodyStructure = parentNode.getBodyStructure() != null;
		List<EquipNode> childList = new ArrayList<>();
		Map<String, EquipNode> origPathToNode = buildPathNodeMap();
		Map<String, EquipSet> origPathToEquipSet = buildPathEquipSetMap();
		for (Map.Entry<String, EquipNode> entry : origPathToNode.entrySet())
		{
			final String origPath = entry.getKey();
			final EquipNode node = entry.getValue();
			EquipSet es = origPathToEquipSet.get(origPath);

			if (node.parent == parentNode)
			{
				childList.add(node);
				if (pid == null)
				{
					pid = es.getParentIdPath();
				}
			}
		}

		// Sort child list
		childList.sort(new EquipNameComparator());

		// Renumber paths
		// need to start from a unique id if only sorting some nodes at a level
		int id = isBodyStructure ? theCharacter.getNewChildId(pid) : 1;
		NumberFormat format = new DecimalFormat("00");
		for (EquipNode childNode : childList)
		{
			String origPath = childNode.idPath;
			String newPath = pid + '.' + format.format(id);
			getNodeList().removeElement(childNode);
			EquipSet es = origPathToEquipSet.get(origPath);
			es.setIdPath(newPath);
			updateContainerPath(origPath, newPath, origPathToNode, origPathToEquipSet);
			childNode.setIdPath(newPath);
			getNodeList().addElement(childNode);
			id++;
		}
		return true;
	}

	/**
	 * Scan back in the node list to find the row that is a certain number of
	 * steps above the starting row. This will skip past the contents of any
	 * containers which do not contain the start row.
	 *
	 * @param equipNode The node being moved.
	 * @param orderedEquipNodes The sorted list of all equipment nodes for this equipment set.
	 * @param numRowsToMove The positive number of rows to move back
	 * @param startRow The row at which the node is currently located.
	 * @return The node currently at the new lcoation.
	 */
	private EquipNode scanBackForNewLoc(EquipNode equipNode, List<EquipNode> orderedEquipNodes,
		int numRowsToMove, int startRow)
	{
		int currIndex = startRow;
		int numRowsMoved = 0;
		String lastIdPath = equipNode.getIdPath();
		EquipNode lastRowNode = equipNode;
		while ((currIndex > 0) && (numRowsMoved < numRowsToMove))
		{
			currIndex--;
			int lastDepth = EquipSet.getPathDepth(lastIdPath);
			EquipNode currRowNode = orderedEquipNodes.get(currIndex);
			int currRowDepth = (currRowNode.getIdPath() == null) ? 0 : EquipSet.getPathDepth(currRowNode.getIdPath());

			if (lastDepth < currRowDepth)
			{
				// Ignore this child of a higher container
			}
			else if ((equipNode.getBodyStructure() != currRowNode.getBodyStructure())
				|| (equipNode.getParent() != currRowNode.getParent()))
			{
				// We've gone too far (outside the target body structure or
				// past where the item can be equipped), so return the last item
				// we could equip to
				return lastRowNode;
			}
			else
			{
				// Valid target - parent or sibling of prev row
				numRowsMoved++;
				lastIdPath = currRowNode.getIdPath();
				lastRowNode = currRowNode;
			}
		}
		return lastRowNode;
	}

	/**
	 * Scan forward in the node list to find the row that is a certain number of
	 * steps below the starting row. This will skip past the contents of any
	 * containers which do not contain the start row.
	 *
	 * @param equipNode The node being moved.
	 * @param orderedEquipNodes The sorted list of all equipment nodes for this equipment set.
	 * @param numRowsToMove The positive number of rows to move forward
	 * @param startRow The row at which the node is currently located.
	 * @return The node currently at the new lcoation.
	 */
	private EquipNode scanForwardForNewLoc(EquipNode equipNode, List<EquipNode> orderedEquipNodes,
		int numRowsToMove, int startRow)
	{
		int currIndex = startRow;
		int numRowsMoved = 0;
		String lastIdPath = equipNode.getIdPath();
		EquipNode lastRowNode = equipNode;
		while ((currIndex < orderedEquipNodes.size()) && (numRowsMoved <= numRowsToMove))
		{
			currIndex++;
			if (currIndex == orderedEquipNodes.size())
			{
				return null;
			}
			int lastDepth = EquipSet.getPathDepth(lastIdPath);
			EquipNode currRowNode = orderedEquipNodes.get(currIndex);
			if (currRowNode.getIdPath() == null)
			{
				return null;
			}
			int currRowDepth = EquipSet.getPathDepth(currRowNode.getIdPath());

			if (lastDepth < currRowDepth)
			{
				// Ignore this child of a lower container
			}
			else if ((equipNode.getBodyStructure() != currRowNode.getBodyStructure())
				|| (equipNode.getParent() != currRowNode.getParent()))
			{
				// We've gone too far (outside the target body structure or
				// past where the item can be equipped), so return the last item
				// we could equip to
				return null;
			}
			else
			{
				// Valid target - sibling of prev row or sibling of prev row's parent.
				numRowsMoved++;
				lastIdPath = currRowNode.getIdPath();
				lastRowNode = currRowNode;
			}
		}
		return lastRowNode;
	}

	/**
	 * Reorder the equipment for output to cater for any changes in the
	 * equipment list. Note this assumes this equipment set is the active one
	 * as it updates the character's master equipment list.
	 */
	private void updateOutputOrder()
	{
		List<EquipNode> orderedEquipNodes = new ArrayList<>(getNodeList().getContents());
		Collections.sort(orderedEquipNodes);
		List<Equipment> processed = new ArrayList<>(orderedEquipNodes.size());

		int outputIndex = 1;
		for (EquipNode equipNode : orderedEquipNodes)
		{
			if (equipNode.getEquipment() != null)
			{
				Equipment equip = theCharacter.getEquipmentNamed(equipNode.getEquipment().toString());
				// If an item is split in multiple places, don't overwrite its order
				if ((equip != null) && !processed.contains(equip))
				{
					equip.setOutputIndex(outputIndex++);
					processed.add(equip);
				}
			}
		}

	}

	@Override
	public EquipmentFacade removeEquipment(EquipNode node, int quantity)
	{
		if (node.getNodeType() != EquipNode.NodeType.EQUIPMENT)
		{
			return null;
		}

		EquipNode targetNode = node;
		EquipNode parentNode = node.getParent();
		EquipSet eSet = charDisplay.getEquipSetByIdPath(targetNode.getIdPath());
		if (eSet == null)
		{
			Logging.errorPrint("No equipset found for node " + targetNode + " path " + targetNode.getIdPath());
			return null;
		}
		int newQty = (int) (eSet.getQty() - quantity);

		Equipment eqI = eSet.getItem();

		if (newQty <= 0)
		{
			// If it was a container, remove all children
			removeChildren(node);

			// remove Equipment (via EquipSet) from the PC
			theCharacter.delEquipSet(eSet);
			getNodeList().removeElement(targetNode);

			// if it was inside a container, make sure to update
			// the container Equipment Object
			if (parentNode.getNodeType() == EquipNode.NodeType.EQUIPMENT)
			{
				Equipment eqP = eqI.getParent();

				if (eqP != null)
				{
					eqP.removeChild(theCharacter, eqI);
				}
			}
			else if (targetNode.getSlot() != null)
			{
				final EquipNode restoredNode = getEquipSlotNodeMap().get(targetNode.getSlot());
				if ((restoredNode != null) && !getNodeList().containsElement(restoredNode))
				{
					getNodeList().addElement(0, restoredNode);
					addCompatWeaponSlots(restoredNode);
				}
			}
		}
		else
		{
			eSet.setQty((float) newQty);
			fireQuantityChanged(targetNode);
		}
		updateTotalWeight(eqI, quantity * -1, targetNode.getBodyStructure());
		updateTotalQuantity(eqI, quantity * -1);
		updateNaturalWeaponSlots();
		theCharacter.calcActiveBonuses();
		updatePhantomSlots();

		return eqI;
	}

	/**
	 * Remove all equipment items held within a supplied container
	 * @param parentNode The container node to be emptied
	 */
	private void removeChildren(EquipNode parentNode)
	{
		if (parentNode.getNodeType() != EquipNode.NodeType.EQUIPMENT)
		{
			return;
		}

		List<EquipNode> equipToBeRemoved = new ArrayList<>(getNodeList().getSize());
		for (EquipNode node : getNodeList())
		{
			// Only select top level equipment, anything in a container will be removed along with the container.
			if ((node.getNodeType() == EquipNode.NodeType.EQUIPMENT) && (node.getParent() == parentNode))
			{
				equipToBeRemoved.add(node);
			}
		}

		for (EquipNode node : equipToBeRemoved)
		{
			removeEquipment(node, getQuantity(node));
		}
	}

	/**
	 * Calculate a list of weapon slots that are not compatible with the
	 * supplied slot. These can then be removed from the list to be displayed
	 * when the slot is filled and added back in when the slot is empty.
	 *
	 * @param targetNode The node to be check.
	 * @return The list of incompatible nodes, empty if the target is not a weapon slot.
	 */
	private List<EquipNode> getIncompatibleWeaponSlots(EquipNode targetNode)
	{
		List<EquipNode> wpnList = new ArrayList<>();
		if (targetNode.getNodeType() != EquipNode.NodeType.PHANTOM_SLOT)
		{
			return wpnList;
		}

		String[] incompatLocNames = {};
		final String slotName = targetNode.getSlot().toString();
		if (Constants.EQUIP_LOCATION_PRIMARY.equals(slotName))
		{
			incompatLocNames = new String[]{Constants.EQUIP_LOCATION_BOTH, Constants.EQUIP_LOCATION_DOUBLE};
		}
		else if (Constants.EQUIP_LOCATION_SECONDARY.equals(slotName))
		{
			incompatLocNames = new String[]{Constants.EQUIP_LOCATION_BOTH, Constants.EQUIP_LOCATION_DOUBLE,
				Constants.EQUIP_LOCATION_SHIELD};
		}
		else if (Constants.EQUIP_LOCATION_SHIELD.equals(slotName))
		{
			incompatLocNames = new String[]{Constants.EQUIP_LOCATION_BOTH, Constants.EQUIP_LOCATION_DOUBLE,
				Constants.EQUIP_LOCATION_SECONDARY};
		}
		else if (Constants.EQUIP_LOCATION_BOTH.equals(slotName))
		{
			incompatLocNames = new String[]{Constants.EQUIP_LOCATION_PRIMARY, Constants.EQUIP_LOCATION_DOUBLE,
				Constants.EQUIP_LOCATION_SECONDARY, Constants.EQUIP_LOCATION_SHIELD};
		}
		else if (Constants.EQUIP_LOCATION_DOUBLE.equals(slotName))
		{
			incompatLocNames = new String[]{Constants.EQUIP_LOCATION_PRIMARY, Constants.EQUIP_LOCATION_BOTH,
				Constants.EQUIP_LOCATION_SECONDARY, Constants.EQUIP_LOCATION_SHIELD};
		}
		//TODO: Extra secondary locations for more than 2 arms

		List<String> namesList = Arrays.asList(incompatLocNames);
		for (EquipSlot slot : getEquipSlotNodeMap().keySet())
		{
			if (namesList.contains(slot.toString()))
			{
				wpnList.add(getEquipSlotNodeMap().get(slot));
			}
		}
		return wpnList;
	}

	/**
	 * Add back to the list any weapon slots that are now valid again. Called
	 * after a weapon is removed from a character and the weapon;s node has
	 * been added back to the node list.
	 *
	 * @param restoredNode The weapon equip node being restored.
	 */
	private void addCompatWeaponSlots(final EquipNode restoredNode)
	{
		List<EquipNode> weaponSlots = getIncompatibleWeaponSlots(restoredNode);
		Set<EquipNode> incompatNodes = new HashSet<>();
		for (EquipNode equipNode : getNodeList())
		{
			if ((equipNode.getNodeType() == EquipNode.NodeType.EQUIPMENT) && affectsWeaponSlots(equipNode))
			{
				EquipSlot slot = equipNode.getSlot();
				if ((slot != null) && (getEquipSlotNodeMap().get(slot) != null))
				{
					incompatNodes.addAll(getIncompatibleWeaponSlots(getEquipSlotNodeMap().get(slot)));
				}
			}
		}
		weaponSlots.removeAll(incompatNodes);
		for (EquipNode node : weaponSlots)
		{
			getNodeList().addElement(0, node);
		}
	}

	private boolean affectsWeaponSlots(EquipNode equipNode)
	{
		List<String> typeList = Arrays.asList(equipNode.getEquipment().getTypes());
		return typeList.contains("WEAPON") || typeList.contains("SHIELD");
	}

	/**
	 * Update the total quantity held of an item as recorded in the
	 * equipmentList. This will add or remove the item from the list as
	 * required. Items with the same name are aggregated to give a total
	 * amount held.
	 *
	 * @param item The item to be updated
	 * @param amtChanged The amount by which the quantity has changed.
	 */
	private void updateTotalQuantity(Equipment item, int amtChanged)
	{
		for (EquipmentFacade equip : equippedItemsList)
		{
			if (item.equals(equip))
			{
				int newQty = equippedItemsList.getQuantity(equip) + amtChanged;
				if (newQty > 0)
				{
					equippedItemsList.setQuantity(equip, newQty);
				}
				else
				{
					equippedItemsList.removeElement(equip);
				}
				return;
			}
		}

		// Item is new so add it
		equippedItemsList.addElement(item, amtChanged);
	}

	/**
	 * Update the available natural weapon slots that are displayed. Will hide the
	 * natural weapon slots unless there are unequipped natural weapons.
	 */
	private void updateNaturalWeaponSlots()
	{
		if (pcHasUnequippedNaturalWeapons())
		{
			// Ensure natural weapon locations are visible
			getNaturalWeaponNodes().values().stream().filter(natWpnEquipNode -> !getNodeList().containsElement(natWpnEquipNode))
				.forEach(natWpnEquipNode -> getNodeList().addElement(natWpnEquipNode));
		}
		else
		{
			// Ensure natural weapon locations are not visible
			getNaturalWeaponNodes().values().stream().filter(natWpnEquipNode -> getNodeList().containsElement(natWpnEquipNode))
				.forEach(natWpnEquipNode -> getNodeList().removeElement(natWpnEquipNode));
		}
	}

	/**
	 * Examine each phantom slot and ensure its display status matches the
	 * current free capacity. This may remove phantom slots from the node list
	 * (where they are full), add them to the node list (where they have spare
	 * capacity), or flag a todo (where they are over capacity. This is done to
	 * react to bonus slots changing in the character.
	 */
	private void updatePhantomSlots()
	{
		// Check for phantom slots which are no longer usable and slots over capacity
		Set<EquipNode> nodesToBeRemoved = new HashSet<>();
		Set<EquipNode> presentPNs = new HashSet<>();
		Set<EquipNode> neededPNs = new HashSet<>();
		for (EquipNode node : getNodeList())
		{
			EquipNode nodeImpl = node;
			switch (node.getNodeType())
			{
				case PHANTOM_SLOT:
					if (getNumFreeSlots(node) <= 0)
					{
						nodesToBeRemoved.add(node);
					}
					else
					{
						presentPNs.add(nodeImpl);
					}
					break;

				case EQUIPMENT:
					if (nodeImpl.getSlot() != null)
					{
						final EquipNode parentNode = getEquipSlotNodeMap().get(nodeImpl.getSlot());
						if ((parentNode != null) && (parentNode.getNodeType() == EquipNode.NodeType.PHANTOM_SLOT))
						{
							int numFreeSlots = getNumFreeSlots(parentNode);
							if (numFreeSlots < 0)
							{
								todoManager.addTodo(new TodoFacadeImpl(Tab.INVENTORY, parentNode.toString(),
									"in_equipNodeOverfull", Tab.EQUIPPING.name(), 9)); //$NON-NLS-1$
							}
							else
							{
								todoManager.removeTodo("in_equipNodeOverfull", parentNode.toString()); //$NON-NLS-1$
								if (numFreeSlots > 0)
								{
									neededPNs.add(parentNode);
								}
							}
						}
					}
					break;

				default:
					break;
			}
		}

		// Add hiddenPNs to neededPNs if they now have spare capacity
		for (EquipNode node : hiddenPhantomNodes)
		{
			if (getNumFreeSlots(node) > 0)
			{
				neededPNs.add(node);
			}
		}

		// Remove the phantom nodes flagged, add to hiddenPNs as needed
		for (EquipNode node : nodesToBeRemoved)
		{
			getNodeList().removeElement(node);
			if (getQuantity(node) <= 0)
			{
				hiddenPhantomNodes.add(node);
			}
		}

		// Add any now needed phantom nodes to the visible list
		neededPNs.removeAll(presentPNs);
		for (EquipNode restoredNode : neededPNs)
		{
			getNodeList().addElement(0, restoredNode);
			addCompatWeaponSlots(restoredNode);
			hiddenPhantomNodes.remove(restoredNode);
		}
	}

	@Override
	public boolean isContainer(EquipmentFacade equipment)
	{
		if (!(equipment instanceof Equipment))
		{
			return false;
		}

		Equipment item = (Equipment) equipment;
		return item.isContainer();
	}

	@Override
	public void setName(String name)
	{
		this.name.set(name);
		eqSet.setName(name);
	}

	@Override
	public void addEquipmentTreeListener(EquipmentTreeListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void removeEquipmentTreeListener(EquipmentTreeListener listener)
	{
		listeners.remove(listener);
	}

	@Override
	public ReferenceFacade<String> getNameRef()
	{
		return name;
	}

	@Override
	public EquipmentListFacade getEquippedItems()
	{
		return equippedItemsList;
	}

	@Override
	public boolean canEquip(EquipNode node, EquipmentFacade equipment)
	{
		if ((equipment == null) || (node == null))
		{
			return false;
		}
		return eqSet.canEquip(theCharacter, node, (Equipment) equipment);
	}

	@Override
	public String getPreferredLoc(EquipmentFacade equipment)
	{
		EquipNode reqNode = getNaturalWeaponLoc(equipment);
		if (reqNode != null)
		{
			return reqNode.toString();
		}
		for (EquipNode node : getEquipSlotNodeMap().values())
		{
			if (node.getNodeType() == EquipNode.NodeType.PHANTOM_SLOT)
			{
				if (canEquip(node, equipment))
				{
					return node.toString();
				}
			}
		}

		return LanguageBundle.getString("in_other"); //$NON-NLS-1$
	}

	/**
	 * Retrieve the preferred location for a natural weapon. Will return null
	 * for non natural weapon equipment items.
	 *
	 * @param equipment The equipment item to be checked.
	 * @return The preferred natural equip node, or null if not applicable.
	 */
	protected EquipNode getNaturalWeaponLoc(EquipmentFacade equipment)
	{
		if (!(equipment instanceof Equipment))
		{
			return null;
		}
		return eqSet.getNatWeaponLoc(theCharacter, (Equipment) equipment);
	}

	private void createNaturalWeaponSlots()
	{
		for (EquipSlot slot : SystemCollections.getUnmodifiableEquipSlotList())
		{
			if (slot.canContainType("WEAPON")) //$NON-NLS-1$
			{
				for (EquipNode node : getNodeList())
				{
					if ((node.getNodeType() == EquipNode.NodeType.BODY_SLOT)
						&& slot.getBodyStructureName().equalsIgnoreCase(node.getBodyStructure().toString()))
					{
						createNaturalWeaponSlot(slot, node, Constants.EQUIP_LOCATION_NATURAL_PRIMARY);
						createNaturalWeaponSlot(slot, node, Constants.EQUIP_LOCATION_NATURAL_SECONDARY);
						return;
					}
				}
			}
		}

	}

	private void createNaturalWeaponSlot(EquipSlot slot, EquipNode node, String locName)
	{
		EquipSlot natWpnEquipSlot = createWeaponEquipSlot(slot, locName);
		EquipNode slotNode = new EquipNode(node, natWpnEquipSlot, true);
		getNaturalWeaponNodes().put(locName, slotNode);
	}

	/**
	 * Calculate the number of free instances of the slot there are in the
	 * equipment set.
	 *
	 * @param slot The slot to be checked, must be a PHANTOM_SLOT.
	 * @return The number of slots free
	 */
	private int getNumFreeSlots(EquipNode slot)
	{
		if (slot.getNodeType() != EquipNode.NodeType.PHANTOM_SLOT)
		{
			return 0;
		}

		EquipNode node = slot;
		int numPossible = getQuantity(node);

		// Scan for items
		int numUsed = 0;
		for (EquipNode item : getNodeList())
		{
			if ((item.getNodeType() == EquipNode.NodeType.EQUIPMENT) && (item.getSlot() == node.getSlot()))
			{
				Equipment equip = (Equipment) item.getEquipment();
				numUsed += equip.getSlots(theCharacter);
			}
		}

		return numPossible - numUsed;
	}

	@Override
	public void removeAllEquipment()
	{
		List<EquipNode> equipToBeRemoved = new ArrayList<>(getNodeList().getSize());
		for (EquipNode node : getNodeList())
		{
			// Only select top level equipment, anything in a container will be removed along with the container.
			if ((node.getNodeType() == EquipNode.NodeType.EQUIPMENT) && (node.getParent().getNodeType() != EquipNode.NodeType.EQUIPMENT))
			{
				equipToBeRemoved.add(node);
			}
		}

		for (EquipNode node : equipToBeRemoved)
		{
			removeEquipment(node, getQuantity(node));
		}
	}

	@Override
	public String toString()
	{
		return name.get();
	}

	/**
	 * Notify any listeners that the quantity of a node has changed.
	 *
	 * @param node The node that has changed quantity.
	 */
	private void fireQuantityChanged(EquipNode node)
	{
		EquipmentTreeEvent event = null;
		for (EquipmentTreeListener equipmentTreeListener : listeners)
		{
			if (event == null)
			{
				event = new EquipmentTreeEvent(this, node);
			}
			equipmentTreeListener.quantityChanged(event);
		}
	}

	/**
	 * @return The core EquipSet that this facade represents.
	 */
	EquipSet getEquipSet()
	{
		return eqSet;
	}

	@Override
	public ListFacade<EquipNode> getNodes()
	{
		return getNodeList();
	}

	@Override
	public int getQuantity(EquipNode equipNode)
	{
		EquipNode node = equipNode;
		switch (node.getNodeType())
		{
			case BODY_SLOT:
				return 1;

			case PHANTOM_SLOT:
				final String slotName = node.getSlot().toString();
				if (Constants.EQUIP_LOCATION_BOTH.equals(slotName))
				{
					return 1; // Set to 1, we should only have 1 object here. - Andrew
				}
				if (Constants.EQUIP_LOCATION_DOUBLE.equals(slotName))
				{
					return 1; // Set to 1, we should only have 1 object here. - Andrew
				}
				return node.singleOnly ? 1 : (node.getSlot().getSlotCount()
					+ (int) theCharacter.getTotalBonusTo("SLOTS", node.getSlot().getSlotName()));

			default:
				EquipSet parentEs = charDisplay.getEquipSetByIdPath(node.getIdPath());
				return (parentEs == null) ? 0 : parentEs.getQty().intValue();
		}
	}

	@Override
	public String getLocation(EquipNode equipNode)
	{
		EquipNode node = equipNode;
        if (node.getNodeType() == EquipNode.NodeType.BODY_SLOT) {
            return node.toString();
        }
        if (node.getSlot() != null) {
            return node.getSlot().toString();
        }
        return node.getBodyStructure().toString();
    }

	@Override
	public String getLocation(EquipmentFacade equip)
	{
		for (EquipNode node : getNodeList())
		{
			if (equip.equals(node.getEquipment()))
			{
				switch (node.getNodeType())
				{
					case EQUIPMENT:
					case PHANTOM_SLOT:
						return getLocation(node);
					default:
						return node.getParent().toString();
				}
			}
		}

		return getPreferredLoc(equip);
	}

	/**
	 * Identify if the character has any natural weapons that have not been
	 * equipped yet.
	 * @return true if there are unequipped natural attacks, false if not.
	 */
	private boolean pcHasUnequippedNaturalWeapons()
	{
		for (Equipment equipItem : theCharacter.getEquipmentMasterList())
		{
			if (equipItem.isNatural() && !equippedItemsList.containsElement(equipItem))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void elementAdded(ListEvent<EquipmentFacade> e)
	{
		// We don't care about new equipment being added.
	}

	@Override
	public void elementRemoved(ListEvent<EquipmentFacade> e)
	{
		EquipmentFacade equipmentFacade = e.getElement();
		if (equippedItemsList.containsElement(equipmentFacade))
		{
			if (Logging.isDebugMode())
			{
				Logging.debugPrint("Currently equipped item " + equipmentFacade + " is being removed.");
			}
		}
		List<EquipNode> affectedList = findEquipmentNodes(equipmentFacade);
		for (EquipNode equipNode : affectedList)
		{
			EquipSet eSet = charDisplay.getEquipSetByIdPath(equipNode.getIdPath());
			if (eSet != null)
			{
				removeEquipment(equipNode, eSet.getQty().intValue());
			}
		}
	}

	private List<EquipNode> findEquipmentNodes(EquipmentFacade equipmentFacade)
	{
		List<EquipNode> affectedList = new ArrayList<>();
		for (EquipNode node : getNodeList())
		{
			if (equipmentFacade.equals(node.getEquipment()))
			{
				affectedList.add(node);
			}
		}
		return affectedList;
	}

	@Override
	public void elementsChanged(ListEvent<EquipmentFacade> e)
	{
		// We expect a refresh of the equipment set to follow an equipment changed 
		// event, so we can ignore these.
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Equip elementsChanged " + e);
		}
	}

	@Override
	public void elementModified(ListEvent<EquipmentFacade> e)
	{
		// We don't care about new equipment being modified.
	}

	@Override
	public void quantityChanged(EquipmentListEvent e)
	{
		EquipmentFacade equipmentFacade = e.getEquipment();
		if (equippedItemsList.containsElement(equipmentFacade))
		{
			int quantity = purchasedList.getQuantity(equipmentFacade) - equippedItemsList.getQuantity(equipmentFacade);
			if (quantity < 0)
			{
				if (Logging.isDebugMode())
				{
					Logging.debugPrint("Currently equipped item " + equipmentFacade + " is being partially removed "
						+ quantity + " from " + equippedItemsList.getQuantity(equipmentFacade));
				}

				int numStillToRemove = -1 * quantity;
				List<EquipNode> affectedList = findEquipmentNodes(equipmentFacade);
				affectedList.sort(new EquipLocImportantComparator()); // TODO: Custom sort order
				for (EquipNode equipNode : affectedList)
				{
					EquipSet eSet = charDisplay.getEquipSetByIdPath(equipNode.getIdPath());
					if (eSet != null)
					{
						int numToRemove = Math.min(eSet.getQty().intValue(), numStillToRemove);
						removeEquipment(equipNode, numToRemove);
						numStillToRemove -= numToRemove;
					}

					if (numStillToRemove <= 0)
					{
						return;
					}
				}

			}
		}
	}

	/**
	 * The Class {@code EquipLocImportantComparator} compares EquipNodes based
	 * on their 'importance' to the character. The predefined order of the slots is 
	 * not carried, carried, equipped, all others in alpha order.
	 */

	private static class EquipLocImportantComparator implements Comparator<EquipNode>, Serializable
	{

		@Override
		public int compare(EquipNode o1, EquipNode o2)
		{
			BodyStructure bodyStruct1 = o1.getBodyStructure();
			BodyStructure bodyStruct2 = o2.getBodyStructure();

			if (bodyStruct1 != bodyStruct2)
			{
				final String[] locOrder = {Constants.EQUIP_LOCATION_NOTCARRIED, Constants.EQUIP_LOCATION_CARRIED,
					Constants.EQUIP_LOCATION_EQUIPPED};
				for (String locName : locOrder)
				{
					if (locName.equals(bodyStruct1.toString()))
					{
						return -1;
					}
					if (locName.equals(bodyStruct2.toString()))
					{
						return 1;
					}
				}

				return bodyStruct1.toString().compareTo(bodyStruct2.toString());
			}

			return o1.compareTo(o2);
		}

	}

	/**
	 * The Class {@code EquipNameComparator} compares EquipNodes based
	 * on alpha order by sort key (if defined) or name.
	 */

	private static class EquipNameComparator implements Comparator<EquipNode>, Serializable
	{

		@Override
		public int compare(final EquipNode o1, final EquipNode o2)
		{
			return o1.getSortKey().compareTo(o2.getSortKey());
		}

	}

	@Override
	public boolean isRoot()
	{
		return eqSet.getParentIdPath().equals("0");
	}

	private DefaultListFacade<EquipNode> getNodeList()
	{
		return eqSet.getNodeList();
	}

	private void setNodeList(DefaultListFacade<EquipNode> nodeList)
	{
		eqSet.setNodeList(nodeList);
	}

	private Map<EquipSlot, EquipNode> getEquipSlotNodeMap()
	{
		return eqSet.getEquipSlotNodeMap();
	}

	private void setEquipSlotNodeMap(Map<EquipSlot, EquipNode> equipSlotNodeMap)
	{
		eqSet.setEquipSlotNodeMap(equipSlotNodeMap);
	}

	public Map<String, EquipNode> getNaturalWeaponNodes()
	{
		return eqSet.getNaturalWeaponNodes();
	}

	private void setNaturalWeaponNodes(Map<String, EquipNode> naturalWeaponNodes)
	{
		eqSet.setNaturalWeaponNodes(naturalWeaponNodes);
	}
}
