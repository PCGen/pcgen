package pcgen.gui2.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.BodyStructure;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.character.EquipSet;
import pcgen.core.character.EquipSlot;
import pcgen.facade.util.ListFacade;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class {@code EquipmentSetFacadeImplTest} is a test class for
 * EquipmentSetFacadeImpl. 
 *
 * <br/>
 * 
 */
public class EquipmentSetFacadeImplTest extends AbstractCharacterTestCase
{
	/* Equipment names constants */
	private static final String QUARTERSTAFF = "Quarterstaff";
	private static final String SATCHEL = "Satchel";
	private static final String BOOK = "Book";
	private static final String BEDROLL = "Bedroll";
	
	private static final String LOC_HANDS = "HANDS";
	private static final String LOC_BOTH_HANDS = "Both Hands";
	private static final String LOC_PRIMARY = "Primary Hand";
	private static final String SLOT_WEAPON = "Weapon";
	private static final String SLOT_RING = "Ring";
	private static final int NUM_BASE_NODES = 9;
	private static final String LOC_BODY = "Body";

	private MockDataSetFacade dataset;
	private MockUIDelegate uiDelegate;
	private TodoManager todoManager;
	private EquipmentListFacadeImpl equipmentList; 
	
	
	/**
	 * Check that EquipmentSetFacadeImpl can be initialised with an empty dataset.  
	 */
	@Test
	public void testEmptyInit()
	{
		EquipSet es = new EquipSet("0.1", "Unit Test Equip");
		EquipmentSetFacadeImpl esfi = 
				new EquipmentSetFacadeImpl(uiDelegate, getCharacter(), es,
					dataset, equipmentList, todoManager, null);
		ListFacade<EquipNode> nodeList = esfi.getNodes();
		assertFalse(nodeList.isEmpty(), "Expected a non empty node set");
		assertEquals(Constants.EQUIP_LOCATION_EQUIPPED, nodeList.getElementAt(0).toString(), "Incorrect name of base node");
		assertEquals(NUM_BASE_NODES, nodeList.getSize(), "Incorrect nunber of nodes found");
	}
	
	/**
	 * Check that EquipmentSetFacadeImpl can be initialised with a dataset 
	 * containing equipment.  
	 */
	@Test
	public void testInitWithEquipment()
	{
		PlayerCharacter pc = getCharacter();
		EquipSet es = new EquipSet("0.1", "Unit Test Equip");
		Equipment item = new Equipment();
		item.setName(SATCHEL);
		Equipment item2 = new Equipment();
		item2.setName(BOOK);
		Equipment item3 = new Equipment();
		item3.setName(QUARTERSTAFF);
		item3.put(IntegerKey.SLOTS, 2);
		
		EquipSet satchelEs = addEquipToEquipSet(pc, es, item, 1.0f);
		addEquipToEquipSet(pc, satchelEs, item2, 1.0f);
		addEquipToEquipSet(pc, es, item3, 1.0f, LOC_BOTH_HANDS);
		int adjustedBaseNodes = NUM_BASE_NODES -4;
		EquipmentSetFacadeImpl esfi =
				new EquipmentSetFacadeImpl(uiDelegate, pc, es, dataset,
					equipmentList, todoManager, null);
		ListFacade<EquipNode> nodeList = esfi.getNodes();
		assertFalse(nodeList.isEmpty(), "Expected a non empty path set");
		EquipNode node = nodeList.getElementAt(0);
		assertEquals(Constants.EQUIP_LOCATION_EQUIPPED, node.toString(), "Incorrect body struct name");
		assertEquals(EquipNode.NodeType.BODY_SLOT, node.getNodeType(), "Incorrect body struct type");
		assertEquals("00", node.getSortKey(), "Incorrect sort key");
		assertNull(node.getParent(), "Incorrect parent");
		node = nodeList.getElementAt(adjustedBaseNodes);
		assertEquals(item.getName(), node.toString(), "Incorrect container name");
		assertEquals(EquipNode.NodeType.EQUIPMENT, node.getNodeType(), "Incorrect container type");
		assertEquals("00|"+item.getName(), node.getSortKey(), "Incorrect sort key");
		assertEquals(nodeList.getElementAt(0), node.getParent(), "Incorrect parent");
		node = nodeList.getElementAt(adjustedBaseNodes+2);
		assertEquals(item2.getName(), node.toString(), "Incorrect item name");
		assertEquals(EquipNode.NodeType.EQUIPMENT, node.getNodeType(), "Incorrect item type");
		assertEquals("00|"+item.getName()+"|"+item2.getName(), node.getSortKey(), "Incorrect sort key");
		assertEquals(nodeList.getElementAt(adjustedBaseNodes), node.getParent(), "Incorrect parent");
		node = nodeList.getElementAt(adjustedBaseNodes+1);
		assertEquals(item3.getName(), node.toString(), "Incorrect item name");
		assertEquals(EquipNode.NodeType.EQUIPMENT, node.getNodeType(), "Incorrect item type");
		assertEquals("01|"+item3.getName(), node.getSortKey(), "Incorrect sort key");
		assertEquals(LOC_HANDS, node.getParent().toString(), "Incorrect parent");
		node = nodeList.getElementAt(adjustedBaseNodes+2);
		EquipNode parent = node.getParent();
		assertEquals(Constants.EQUIP_LOCATION_EQUIPPED, parent.getParent().toString(), "Root incorrect");
		assertEquals(item.getName(), parent.toString(), "Leaf incorrect");
		assertEquals(adjustedBaseNodes+3, nodeList.getSize(), "Incorrect number of paths found");
	}
	
	/**
	 * Check that EquipmentSetFacadeImpl when initialised with a dataset 
	 * containing equipment hides and shows the correct weapon slots.  
	 */
	@Test
	public void testSlotManagementOnInitWithEquipment()
	{
		PlayerCharacter pc = getCharacter();
		EquipSet es = new EquipSet("0.1", "Unit Test Equip");
		Equipment weapon = new Equipment();
		weapon.setName("Morningstar");
		
		addEquipToEquipSet(pc, es, weapon, 1.0f, LOC_PRIMARY);

		EquipmentSetFacadeImpl esfi =
				new EquipmentSetFacadeImpl(uiDelegate, pc, es, dataset,
					equipmentList, todoManager, null);
		ListFacade<EquipNode> nodes = esfi.getNodes();
		Map<String, EquipNode> nodeMap = new HashMap<>();
		for (EquipNode equipNode : nodes)
		{
			nodeMap.put(equipNode.toString(), equipNode);
		}

		EquipNode testNode = nodeMap.get("Morningstar");
		assertNotNull(testNode, "Morningstar should be present");
		assertEquals(EquipNode.NodeType.EQUIPMENT, testNode.getNodeType(), "Morningstar type");
		assertEquals(LOC_PRIMARY, esfi.getLocation(testNode), "Morningstar location");

		// Test for removed slots
		String[] removedSlots = {"Primary Hand", "Double Weapon", "Both Hands"};
		for (String slotName : removedSlots)
		{
			testNode = nodeMap.get(slotName);
			assertNull(testNode, slotName + " should not be present");
		}

		// Test for still present slots
		String[] retainedSlots = {"Secondary Hand", "Ring"};
		for (String slotName : retainedSlots)
		{
			testNode = nodeMap.get(slotName);
			assertNotNull(testNode, slotName + " should be present");
		}
		
	}
	
	/**
	 * Check that EquipmentSetFacadeImpl can manage addition and removal of equipment.
	 */
	@Test
	public void testAddRemove()
	{
		EquipSet es = new EquipSet("0.1", "Unit Test Equip");
		CharacterFacadeImpl charFacade =
				new CharacterFacadeImpl(getCharacter(), uiDelegate, dataset);
		EquipmentSetFacadeImpl esfi =
				new EquipmentSetFacadeImpl(uiDelegate, getCharacter(), es,
					dataset, equipmentList, todoManager, charFacade);
		EquipNode root = esfi.getNodes().getElementAt(0);
		Equipment item = new Equipment();
		item.setName("Dart");
		assertEquals(0, item.getCarried(), 0.01, "Initial num carried");
		assertEquals(0, item.getNumberEquipped(), "Initial num equipped");
		assertEquals(NUM_BASE_NODES, esfi.getNodes().getSize(), "Initial node list size");

		Equipment addedEquip = (Equipment) esfi.addEquipment(root, item, 2);
		assertEquals(2, addedEquip.getCarried(), 0.01, "First add num carried");
		assertEquals(2, addedEquip.getNumberEquipped(), "First add num equipped");
		assertEquals(0, item.getCarried(), 0.01, "Should be no sideeffects to num carried");
		assertEquals(0, item.getNumberEquipped(), "Should be no sideeffects to equipped");
		assertEquals(NUM_BASE_NODES+1, esfi.getNodes().getSize(), "First add node list size");
		assertEquals("0.1.01", esfi.getNodes().getElementAt(NUM_BASE_NODES).getIdPath(), "generated equip set id");

		Equipment secondEquip = (Equipment) esfi.addEquipment(root, item, 1);
		assertEquals(3, secondEquip.getCarried(), 0.01, "Second add num carried");
		assertEquals(3, secondEquip.getNumberEquipped(), "Second add num equipped");
		assertEquals(0, item.getCarried(), 0.01, "Should be no sideeffects to num carried");
		assertEquals(0, item.getNumberEquipped(), "Should be no sideeffects to equipped");
		assertSame(addedEquip, secondEquip, "Same equipment item should have been used");
		assertEquals(NUM_BASE_NODES+1, esfi.getNodes().getSize(), "First add node list size");

		EquipNode target =  esfi.getNodes().getElementAt(NUM_BASE_NODES);
		Equipment removedEquip = (Equipment) esfi.removeEquipment(target, 2);
		assertEquals(1, removedEquip.getCarried(), 0.01, "First add num carried");
		assertEquals(1, removedEquip.getNumberEquipped(), "First add num equipped");
		assertSame(addedEquip, removedEquip, "Same equipment item should have been used");
		assertEquals(0, item.getCarried(), 0.01, "Should be no sideeffects to num carried");
		assertEquals(0, item.getNumberEquipped(), "Should be no sideeffects to equipped");
		assertEquals(NUM_BASE_NODES+1, esfi.getNodes().getSize(), "First add node list size");

		esfi.removeEquipment(target, 1);
		assertEquals(0, addedEquip.getCarried(), 0.01, "First add num carried");
		assertEquals(0, addedEquip.getNumberEquipped(), "First add num equipped");
		assertEquals(0, item.getCarried(), 0.01, "Should be no sideeffects to num carried");
		assertEquals(0, item.getNumberEquipped(), "Should be no sideeffects to equipped");
		assertEquals(NUM_BASE_NODES, esfi.getNodes().getSize(), "First add node list size");
	}

	/**
	 * Test the creation of phantom slots, looking at types and quantities particularly.  
	 */
	@Test
	public void testSlotCreation()
	{
		EquipSet es = new EquipSet("0.1", "Unit Test Equip");
		EquipmentSetFacadeImpl esfi =
				new EquipmentSetFacadeImpl(uiDelegate, getCharacter(), es,
					dataset, equipmentList, todoManager, null);
		ListFacade<EquipNode> nodes = esfi.getNodes();
		Map<String, EquipNode> nodeMap = new HashMap<>();
		for (EquipNode equipNode : nodes)
		{
			nodeMap.put(equipNode.toString(), equipNode);
		}

		EquipNode testNode = nodeMap.get("Primary Hand");
		assertNotNull(testNode, "Primary Hand should be present");
		assertEquals(EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType(), "Primary Hand type");
		assertEquals(1, esfi.getQuantity(testNode), "Primary Hand count");

		testNode = nodeMap.get("Secondary Hand");
		assertNotNull(testNode, "Secondary Hand should be present");
		assertEquals(EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType(), "Secondary Hand type");
		assertEquals(1, esfi.getQuantity(testNode), "Secondary Hand count");

		testNode = nodeMap.get(Constants.EQUIP_LOCATION_SECONDARY + " 1");
		assertNull(testNode, Constants.EQUIP_LOCATION_SECONDARY + " 1 should not be present");

		testNode = nodeMap.get(Constants.EQUIP_LOCATION_SECONDARY + " 2");
		assertNull(testNode, Constants.EQUIP_LOCATION_SECONDARY + " 2 should not be present");

		testNode = nodeMap.get("Double Weapon");
		assertNotNull(testNode, "Double Weapon should be present");
		assertEquals(EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType(), "Double Weapon type");
		assertEquals(1, esfi.getQuantity(testNode), "Double Weapon count");

		testNode = nodeMap.get("Both Hands");
		assertNotNull(testNode, "Both Hands should be present");
		assertEquals(EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType(), "Both Hands type");
		assertEquals(1, esfi.getQuantity(testNode), "Both Hands count");

		testNode = nodeMap.get("Unarmed");
		assertNotNull(testNode, "Unarmed should be present");
		assertEquals(EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType(), "Unarmed type");
		assertEquals(1, esfi.getQuantity(testNode), "Unarmed count");

		testNode = nodeMap.get("Ring");
		assertNotNull(testNode, "Ring should be present");
		assertEquals(EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType(), "Ring type");
		assertEquals(2, esfi.getQuantity(testNode), "Ring count");
	}

	public void testBonusSlot()
	{
		EquipSet es = new EquipSet("0.1", "Unit Test Equip");
		PlayerCharacter pc = getCharacter();
		EquipmentSetFacadeImpl esfi =
				new EquipmentSetFacadeImpl(uiDelegate, pc, es,
					dataset, equipmentList, todoManager, null);
		ListFacade<EquipNode> nodes = esfi.getNodes();
		Map<String, EquipNode> nodeMap = new HashMap<>();
		for (EquipNode equipNode : nodes)
		{
			nodeMap.put(equipNode.toString(), equipNode);
		}

		EquipNode testNode = nodeMap.get("Ring");
		assertNotNull(testNode, "Ring should be present");
		assertEquals(EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType(), "Ring type");
		assertEquals(2, esfi.getQuantity(testNode), "Ring count");
		
		PCTemplate template = TestHelper.makeTemplate("RingBonus");
		Globals.getContext().unconditionallyProcess(template, "BONUS",
				"SLOTS|Ring|1");
		pc.addTemplate(template);
		assertEquals(3, esfi.getQuantity(testNode), "Ring count");
	}
	
	/**
	 * Verify the getRequiredLoc method. 
	 */
	@Test
	public void testGetRequiredLoc()
	{
		EquipSet es = new EquipSet("0.1", "Unit Test Equip");
		EquipmentSetFacadeImpl esfi =
				new EquipmentSetFacadeImpl(uiDelegate, getCharacter(), es,
					dataset, equipmentList, todoManager, null);

		assertNull(esfi.getNaturalWeaponLoc(null), "Null equipment should give null location");
		
		Equipment eq = new Equipment();
		eq.addType(Type.MELEE);
		eq.addType(Type.WEAPON);
		EquipNode requiredLoc = esfi.getNaturalWeaponLoc(eq);
		assertNull(requiredLoc, "Melee weapon should not have required location.");
		
		eq.addType(Type.NATURAL);
		eq.put(IntegerKey.SLOTS, 0);
		eq.setName("Sting");
		requiredLoc = esfi.getNaturalWeaponLoc(eq);
		assertNotNull(requiredLoc, "Natural weapon should have required location.");
		assertEquals("Natural-Secondary", requiredLoc.toString(), "Incorrect name for secondary natural weapon");
		assertEquals("HANDS", requiredLoc.getBodyStructure().toString(), "Natural weapon should replace hands.");

		eq.setModifiedName("Natural/Primary");
		requiredLoc = esfi.getNaturalWeaponLoc(eq);
		assertNotNull(requiredLoc, "Natural weapon should have required location.");
		assertEquals("Natural-Primary", requiredLoc.toString(), "Incorrect name for primary natural weapon");
		assertEquals("HANDS", requiredLoc.getBodyStructure().toString(), "Natural weapon should replace hands.");
	}
	
	
	/**
	 * Check that EquipmentSetFacadeImpl can move an equipment item up the list.
	 */
	@Test
	public void testMoveEquipmentUp()
	{
		EquipmentSetFacadeImpl esfi = prepareEquipmentSet();
		ListFacade<EquipNode> nodeList = esfi.getNodes();
		assertFalse(nodeList.isEmpty(), "Expected a non empty path set");
		EquipNode quarterstaffNode = getEquipNodeByName(nodeList, QUARTERSTAFF);
		//assertEquals(item3.getName(), quarterstaffNode.toString(), "Incorrect item name");
		assertEquals(EquipNode.NodeType.EQUIPMENT, quarterstaffNode.getNodeType(), "Incorrect item type");
		assertEquals(Constants.EQUIP_LOCATION_EQUIPPED, quarterstaffNode.getParent().toString(), "Incorrect parent");
		assertEquals("0.1.02", quarterstaffNode.getIdPath(), "Incorrect path");
		
		EquipNode bookNode = getEquipNodeByName(nodeList, BOOK);
		assertEquals("0.1.01.01", bookNode.getIdPath(), "Incorrect path");
		EquipNode satchelNode = getEquipNodeByName(nodeList, SATCHEL);
		assertEquals("0.1.01", satchelNode.getIdPath(), "Incorrect path");
		
		assertTrue(esfi.moveEquipment(quarterstaffNode, -1), "Move up failed unexpectedly");
		assertEquals("0.1.01", quarterstaffNode.getIdPath(), "Incorrect quarterstaff path");
		assertEquals("0.1.02", satchelNode.getIdPath(), "Incorrect satchel path");
		assertEquals("0.1.02.01", bookNode.getIdPath(), "Incorrect book path");
	}
	
	/**
	 * Check that EquipmentSetFacadeImpl can move an equipment item down the list.
	 */
	@Test
	public void testMoveEquipmentDown()
	{
		EquipmentSetFacadeImpl esfi = prepareEquipmentSet();
		ListFacade<EquipNode> nodeList = esfi.getNodes();
		assertFalse(nodeList.isEmpty(), "Expected a non empty path set");
		EquipNode quarterstaffNode = getEquipNodeByName(nodeList, QUARTERSTAFF);
		//assertEquals(item3.getName(), quarterstaffNode.toString(), "Incorrect item name");
		assertEquals(EquipNode.NodeType.EQUIPMENT, quarterstaffNode.getNodeType(), "Incorrect item type");
		assertEquals(Constants.EQUIP_LOCATION_EQUIPPED, quarterstaffNode.getParent().toString(), "Incorrect parent");
		assertEquals("0.1.02", quarterstaffNode.getIdPath(), "Incorrect path");
		
		EquipNode bookNode = getEquipNodeByName(nodeList, BOOK);
		assertEquals("0.1.01.01", bookNode.getIdPath(), "Incorrect path");
		EquipNode satchelNode = getEquipNodeByName(nodeList, SATCHEL);
		assertEquals("0.1.01", satchelNode.getIdPath(), "Incorrect path");
		EquipNode bedrollNode = getEquipNodeByName(nodeList, BEDROLL);
		assertEquals("0.1.03", bedrollNode.getIdPath(), "Incorrect path");
		
		assertTrue(esfi.moveEquipment(satchelNode, 1), "Move down failed unexpectedly");
		assertEquals("0.1.02", quarterstaffNode.getIdPath(), "Incorrect quarterstaff path after move down.");
		assertEquals("0.1.03", satchelNode.getIdPath(), "Incorrect satchel path after move down.");
		assertEquals("0.1.03.01", bookNode.getIdPath(), "Incorrect book path after move down.");
		assertEquals("0.1.04", bedrollNode.getIdPath(), "Incorrect bedroll path after move down.");
		
		assertTrue(esfi.moveEquipment(satchelNode, 1), "Move to bottom failed unexpectedly");
		assertEquals("0.1.02", quarterstaffNode.getIdPath(), "Incorrect quarterstaff path after move to bottom.");
		assertEquals("0.1.05", satchelNode.getIdPath(), "Incorrect satchel path after move to bottom.");
		assertEquals("0.1.05.01", bookNode.getIdPath(), "Incorrect book path after move to bottom.");
		assertEquals("0.1.04", bedrollNode.getIdPath(), "Incorrect bedroll path after move to bottom.");
	}
	

	private EquipmentSetFacadeImpl prepareEquipmentSet()
	{
		PlayerCharacter pc = getCharacter();
		EquipSet es = new EquipSet("0.1", "Unit Test Equip");
		pc.addEquipSet(es);
		Equipment item = new Equipment();
		item.setName(SATCHEL);
		Equipment item2 = new Equipment();
		item2.setName(BOOK);
		Equipment item3 = new Equipment();
		item3.setName(QUARTERSTAFF);
		Equipment item4 = new Equipment();
		item4.setName(BEDROLL);

		EquipSet satchelEs = addEquipToEquipSet(pc, es, item, 1.0f);
		addEquipToEquipSet(pc, satchelEs, item2, 1.0f);
		addEquipToEquipSet(pc, es, item3, 1.0f);
		addEquipToEquipSet(pc, es, item4, 1.0f);

		return new EquipmentSetFacadeImpl(uiDelegate, getCharacter(), es,
			dataset, equipmentList, todoManager, null);
	}
	
	private static EquipNode getEquipNodeByName(ListFacade<EquipNode> nodeList,
	                                            String name)
	{
		for (EquipNode equipNode : nodeList)
		{
			if (name.equals(equipNode.toString()))
			{
				return equipNode;
			}
		}
		
		return null;
	}
	
	/**
	 * Add the equipment item to the equipset.
	 * 
	 * @param pc The character owning the set
	 * @param es The set to add the item to
	 * @param item The item of equipment
	 * @param qty The number to be placed in the location.
	 * @return The new EquipSet object for the item.
	 */
	private static EquipSet addEquipToEquipSet(PlayerCharacter pc, EquipSet es,
	                                           Equipment item, float qty)
	{
		return addEquipToEquipSet(pc, es, item, qty, Constants.EQUIP_LOCATION_EQUIPPED);
	}
	/**
	 * Add the equipment item to the equipset.
	 * 
	 * @param pc The character owning the set
	 * @param es The set to add the item to
	 * @param item The item of equipment
	 * @param qty The number to be placed in the location.
	 * @return The new EquipSet object for the item.
	 */
	private static EquipSet addEquipToEquipSet(PlayerCharacter pc, EquipSet es,
	                                           Equipment item, float qty, String locName)
	{
		String id = EquipmentSetFacadeImpl.getNewIdPath(pc.getDisplay(), es);
		EquipSet newSet = new EquipSet(id, locName, item.getName(), item);
		item.setQty(qty);
		newSet.setQty(1.0f);
		pc.addEquipSet(newSet);
		return newSet;
	}

	@BeforeEach
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		dataset = new MockDataSetFacade(SettingsHandler.getGameAsProperty().get());
		dataset.addEquipmentLocation(new BodyStructure(Constants.EQUIP_LOCATION_EQUIPPED, true));
		dataset.addEquipmentLocation(new BodyStructure(LOC_HANDS, false));
		dataset.addEquipmentLocation(new BodyStructure(LOC_BODY, false));
		if (SystemCollections.getUnmodifiableEquipSlotList().isEmpty())
		{
			EquipSlot equipSlot = new EquipSlot();
			equipSlot.setSlotName(SLOT_WEAPON);
			equipSlot.addContainedType("Weapon");
			equipSlot.setContainNum(1);
			equipSlot.setSlotNumType("HANDS");
			SystemCollections.addToEquipSlotsList(equipSlot, SettingsHandler.getGameAsProperty().get().getName());
			Globals.setEquipSlotTypeCount("HANDS", 2);

			equipSlot = new EquipSlot();
			equipSlot.setSlotName(SLOT_RING);
			equipSlot.addContainedType("Ring");
			equipSlot.setContainNum(2);
			equipSlot.setSlotNumType("BODY");
			SystemCollections.addToEquipSlotsList(equipSlot, SettingsHandler.getGameAsProperty().get().getName());
			Globals.setEquipSlotTypeCount("BODY", 1);
		}
		uiDelegate = new MockUIDelegate();
		todoManager = new TodoManager();
		equipmentList = new EquipmentListFacadeImpl();
	}
}
