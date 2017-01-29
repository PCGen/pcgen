package pcgen.gui2.facade;

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
import pcgen.facade.core.EquipmentSetFacade.EquipNode;
import pcgen.facade.core.EquipmentSetFacade.EquipNode.NodeType;
import pcgen.facade.util.ListFacade;
import pcgen.gui2.facade.EquipmentSetFacadeImpl.EquipNodeImpl;
import pcgen.util.TestHelper;

import org.junit.Assert;

/**
 * The Class <code>EquipmentSetFacadeImplTest</code> is a test class for
 * EquipmentSetFacadeImpl. 
 *
 * <br/>
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
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
	public void testEmptyInit()
	{
		EquipSet es = new EquipSet("0.1", "Unit Test Equip");
		EquipmentSetFacadeImpl esfi = 
				new EquipmentSetFacadeImpl(uiDelegate, getCharacter(), es,
					dataset, equipmentList, todoManager, null);
		ListFacade<EquipNode> nodeList = esfi.getNodes();
		Assert.assertFalse("Expected a non empty node set", nodeList.isEmpty());
		Assert.assertEquals("Incorrect name of base node", Constants.EQUIP_LOCATION_EQUIPPED,
			nodeList.getElementAt(0).toString());
		Assert.assertEquals("Incorrect nunber of nodes found", NUM_BASE_NODES, nodeList.getSize());
	}
	
	/**
	 * Check that EquipmentSetFacadeImpl can be initialised with a dataset 
	 * containing equipment.  
	 */
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
		addEquipToEquipSet (pc, es, item3, 1.0f, LOC_BOTH_HANDS);
		int adjustedBaseNodes = NUM_BASE_NODES -4;
		EquipmentSetFacadeImpl esfi =
				new EquipmentSetFacadeImpl(uiDelegate, pc, es, dataset,
					equipmentList, todoManager, null);
		ListFacade<EquipNode> nodeList = esfi.getNodes();
		Assert.assertFalse("Expected a non empty path set", nodeList.isEmpty());
		EquipNodeImpl node = (EquipNodeImpl) nodeList.getElementAt(0);
		Assert.assertEquals("Incorrect body struct name", Constants.EQUIP_LOCATION_EQUIPPED, node.toString());
		Assert.assertEquals("Incorrect body struct type", NodeType.BODY_SLOT, node.getNodeType());
		Assert.assertEquals("Incorrect sort key", "00", node.getSortKey());
		Assert.assertEquals("Incorrect parent", null, node.getParent());
		node = (EquipNodeImpl) nodeList.getElementAt(adjustedBaseNodes);
		Assert.assertEquals("Incorrect container name", item.getName(), node.toString());
		Assert.assertEquals("Incorrect container type", NodeType.EQUIPMENT, node.getNodeType());
		Assert.assertEquals("Incorrect sort key", "00|"+item.getName(), node.getSortKey());
		Assert.assertEquals("Incorrect parent", nodeList.getElementAt(0), node.getParent());
		node = (EquipNodeImpl) nodeList.getElementAt(adjustedBaseNodes+2);
		Assert.assertEquals("Incorrect item name", item2.getName(), node.toString());
		Assert.assertEquals("Incorrect item type", NodeType.EQUIPMENT, node.getNodeType());
		Assert.assertEquals("Incorrect sort key", "00|"+item.getName()+"|"+item2.getName(), node.getSortKey());
		Assert.assertEquals("Incorrect parent", nodeList.getElementAt(adjustedBaseNodes), node.getParent());
		node = (EquipNodeImpl) nodeList.getElementAt(adjustedBaseNodes+1);
		Assert.assertEquals("Incorrect item name", item3.getName(), node.toString());
		Assert.assertEquals("Incorrect item type", NodeType.EQUIPMENT, node.getNodeType());
		Assert.assertEquals("Incorrect sort key", "01|"+item3.getName(), node.getSortKey());
		Assert.assertEquals("Incorrect parent", LOC_HANDS, node.getParent().toString());
		node = (EquipNodeImpl) nodeList.getElementAt(adjustedBaseNodes+2);
		EquipNode parent = node.getParent();
		Assert.assertEquals("Root incorrect", Constants.EQUIP_LOCATION_EQUIPPED, parent.getParent().toString());
		Assert.assertEquals("Leaf incorrect", item.getName(), parent.toString());
		Assert.assertEquals("Incorrect nuber of paths found", adjustedBaseNodes+3, nodeList.getSize());
	}
	
	/**
	 * Check that EquipmentSetFacadeImpl when initialised with a dataset 
	 * containing equipment hides and shows the correct weapon slots.  
	 */
	public void testSlotManagementOnInitWithEquipment()
	{
		PlayerCharacter pc = getCharacter();
		EquipSet es = new EquipSet("0.1", "Unit Test Equip");
		Equipment weapon = new Equipment();
		weapon.setName("Morningstar");
		
		addEquipToEquipSet (pc, es, weapon, 1.0f, LOC_PRIMARY);

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
		Assert.assertNotNull("Morningstar should be present", testNode);
		Assert.assertEquals("Morningstar type", EquipNode.NodeType.EQUIPMENT, testNode.getNodeType());
		Assert.assertEquals("Morningstar location", LOC_PRIMARY, esfi.getLocation(testNode));

		// Test for removed slots
		String[] removedSlots = {"Primary Hand", "Double Weapon", "Both Hands"};
		for (String slotName : removedSlots)
		{
			testNode = nodeMap.get(slotName);
			Assert.assertNull(slotName + " should not be present", testNode);
		}

		// Test for still present slots
		String[] retainedSlots = {"Secondary Hand", "Ring"};
		for (String slotName : retainedSlots)
		{
			testNode = nodeMap.get(slotName);
			Assert.assertNotNull(slotName + " should be present", testNode);
		}
		
	}
	
	/**
	 * Check that EquipmentSetFacadeImpl can manage addition and removal of equipment.  
	 */
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
		Assert.assertEquals("Initial num carried", 0, item.getCarried(), 0.01);
		Assert.assertEquals("Initial num equipped", 0, item.getNumberEquipped());
		Assert.assertEquals("Initial node list size", NUM_BASE_NODES, esfi.getNodes().getSize());

		Equipment addedEquip = (Equipment) esfi.addEquipment(root, item, 2);
		Assert.assertEquals("First add num carried", 2, addedEquip.getCarried(), 0.01);
		Assert.assertEquals("First add num equipped", 2, addedEquip.getNumberEquipped());
		Assert.assertEquals("Should be no sideeffects to num carried", 0, item.getCarried(), 0.01);
		Assert.assertEquals("Should be no sideeffects to equipped", 0, item.getNumberEquipped());
		Assert.assertEquals("First add node list size", NUM_BASE_NODES+1, esfi.getNodes().getSize());
		Assert.assertEquals("generated equip set id", "0.1.01", ((EquipNodeImpl)esfi.getNodes().getElementAt(NUM_BASE_NODES)).getIdPath());

		Equipment secondEquip = (Equipment) esfi.addEquipment(root, item, 1);
		Assert.assertEquals("Second add num carried", 3, secondEquip.getCarried(), 0.01);
		Assert.assertEquals("Second add num equipped", 3, secondEquip.getNumberEquipped());
		Assert.assertEquals("Should be no sideeffects to num carried", 0, item.getCarried(), 0.01);
		Assert.assertEquals("Should be no sideeffects to equipped", 0, item.getNumberEquipped());
		Assert.assertTrue("Same equipment item should have been used", addedEquip == secondEquip);
		Assert.assertEquals("First add node list size", NUM_BASE_NODES+1, esfi.getNodes().getSize());

		EquipNode target =  esfi.getNodes().getElementAt(NUM_BASE_NODES);
		Equipment removedEquip = (Equipment) esfi.removeEquipment(target, 2);
		Assert.assertEquals("First add num carried", 1, removedEquip.getCarried(), 0.01);
		Assert.assertEquals("First add num equipped", 1, removedEquip.getNumberEquipped());
		Assert.assertTrue("Same equipment item should have been used", addedEquip == removedEquip);
		Assert.assertEquals("Should be no sideeffects to num carried", 0, item.getCarried(), 0.01);
		Assert.assertEquals("Should be no sideeffects to equipped", 0, item.getNumberEquipped());
		Assert.assertEquals("First add node list size", NUM_BASE_NODES+1, esfi.getNodes().getSize());

		esfi.removeEquipment(target, 1);
		Assert.assertEquals("First add num carried", 0, addedEquip.getCarried(), 0.01);
		Assert.assertEquals("First add num equipped", 0, addedEquip.getNumberEquipped());
		Assert.assertEquals("Should be no sideeffects to num carried", 0, item.getCarried(), 0.01);
		Assert.assertEquals("Should be no sideeffects to equipped", 0, item.getNumberEquipped());
		Assert.assertEquals("First add node list size", NUM_BASE_NODES, esfi.getNodes().getSize());
	}

	/**
	 * Test the creation of phantom slots, looking at types and quantities particularly.  
	 */
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
		Assert.assertNotNull("Primary Hand should be present", testNode);
		Assert.assertEquals("Primary Hand type", EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType());
		Assert.assertEquals("Primary Hand count", 1, esfi.getQuantity(testNode));

		testNode = nodeMap.get("Secondary Hand");
		Assert.assertNotNull("Secondary Hand should be present", testNode);
		Assert.assertEquals("Secondary Hand type", EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType());
		Assert.assertEquals("Secondary Hand count", 1, esfi.getQuantity(testNode));

		testNode = nodeMap.get(Constants.EQUIP_LOCATION_SECONDARY + " 1");
		Assert.assertNull(Constants.EQUIP_LOCATION_SECONDARY + " 1 should not be present", testNode);

		testNode = nodeMap.get(Constants.EQUIP_LOCATION_SECONDARY + " 2");
		Assert.assertNull(Constants.EQUIP_LOCATION_SECONDARY + " 2 should not be present", testNode);

		testNode = nodeMap.get("Double Weapon");
		Assert.assertNotNull("Double Weapon should be present", testNode);
		Assert.assertEquals("Double Weapon type", EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType());
		Assert.assertEquals("Double Weapon count", 1, esfi.getQuantity(testNode));

		testNode = nodeMap.get("Both Hands");
		Assert.assertNotNull("Both Hands should be present", testNode);
		Assert.assertEquals("Both Hands type", EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType());
		Assert.assertEquals("Both Hands count", 1, esfi.getQuantity(testNode));

		testNode = nodeMap.get("Unarmed");
		Assert.assertNotNull("Unarmed should be present", testNode);
		Assert.assertEquals("Unarmed type", EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType());
		Assert.assertEquals("Unarmed count", 1, esfi.getQuantity(testNode));

		testNode = nodeMap.get("Ring");
		Assert.assertNotNull("Ring should be present", testNode);
		Assert.assertEquals("Ring type", EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType());
		Assert.assertEquals("Ring count", 2, esfi.getQuantity(testNode));
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
		Assert.assertNotNull("Ring should be present", testNode);
		Assert.assertEquals("Ring type", EquipNode.NodeType.PHANTOM_SLOT, testNode.getNodeType());
		Assert.assertEquals("Ring count", 2, esfi.getQuantity(testNode));
		
		PCTemplate template = TestHelper.makeTemplate("RingBonus");
		Globals.getContext().unconditionallyProcess(template, "BONUS",
				"SLOTS|Ring|1");
		pc.addTemplate(template);
		Assert.assertEquals("Ring count", 3, esfi.getQuantity(testNode));
	}
	
	/**
	 * Verify the getRequiredLoc method. 
	 */
	public void testGetRequiredLoc()
	{
		EquipSet es = new EquipSet("0.1", "Unit Test Equip");
		EquipmentSetFacadeImpl esfi =
				new EquipmentSetFacadeImpl(uiDelegate, getCharacter(), es,
					dataset, equipmentList, todoManager, null);

		Assert.assertNull("Null equipment should give null location", esfi.getNaturalWeaponLoc(null));
		
		Equipment eq = new Equipment();
		eq.addType(Type.MELEE);
		eq.addType(Type.WEAPON);
		EquipNode requiredLoc = esfi.getNaturalWeaponLoc(eq);
		Assert.assertNull("Melee weapon should not have required location.", requiredLoc);
		
		eq.addType(Type.NATURAL);
		eq.put(IntegerKey.SLOTS, 0);
		eq.setName("Sting");
		requiredLoc = esfi.getNaturalWeaponLoc(eq);
		Assert.assertNotNull("Natural weapon should have required location.", requiredLoc);
		Assert.assertEquals("Incorrect name for secondary natural weapon", "Natural-Secondary", requiredLoc.toString());
		Assert.assertEquals("Natural weapom should replace hands.", "HANDS", requiredLoc.getBodyStructure().toString());

		eq.setModifiedName("Natural/Primary");
		requiredLoc = esfi.getNaturalWeaponLoc(eq);
		Assert.assertNotNull("Natural weapon should have required location.", requiredLoc);
		Assert.assertEquals("Incorrect name for primary natural weapon", "Natural-Primary", requiredLoc.toString());
		Assert.assertEquals("Natural weapom should replace hands.", "HANDS", requiredLoc.getBodyStructure().toString());
	}
	
	
	/**
	 * Check that EquipmentSetFacadeImpl can move an equipment item up the list.
	 */
	public void testMoveEquipmentUp()
	{
		EquipmentSetFacadeImpl esfi = prepareEquipmentSet();
		ListFacade<EquipNode> nodeList = esfi.getNodes();
		Assert.assertFalse("Expected a non empty path set", nodeList.isEmpty());
		EquipNodeImpl quarterstaffNode = getEquipNodeByName(nodeList, QUARTERSTAFF);
		//assertEquals("Incorrect item name", item3.getName(), quarterstaffNode.toString());
		Assert.assertEquals("Incorrect item type", NodeType.EQUIPMENT, quarterstaffNode.getNodeType());
		Assert.assertEquals("Incorrect parent", Constants.EQUIP_LOCATION_EQUIPPED, quarterstaffNode.getParent().toString());
		Assert.assertEquals("Incorrect path", "0.1.02", quarterstaffNode.getIdPath());
		
		EquipNodeImpl bookNode = getEquipNodeByName(nodeList, BOOK);
		Assert.assertEquals("Incorrect path", "0.1.01.01", bookNode.getIdPath());
		EquipNodeImpl satchelNode = getEquipNodeByName(nodeList, SATCHEL);
		Assert.assertEquals("Incorrect path", "0.1.01", satchelNode.getIdPath());
		
		Assert.assertTrue("Move up failed unexpectedly", esfi.moveEquipment(quarterstaffNode, -1));
		Assert.assertEquals("Incorrect quarterstaff path", "0.1.01", quarterstaffNode.getIdPath());
		Assert.assertEquals("Incorrect satchel path", "0.1.02", satchelNode.getIdPath());
		Assert.assertEquals("Incorrect book path", "0.1.02.01", bookNode.getIdPath());
	}
	
	/**
	 * Check that EquipmentSetFacadeImpl can move an equipment item down the list.
	 */
	public void testMoveEquipmentDown()
	{
		EquipmentSetFacadeImpl esfi = prepareEquipmentSet();
		ListFacade<EquipNode> nodeList = esfi.getNodes();
		Assert.assertFalse("Expected a non empty path set", nodeList.isEmpty());
		EquipNodeImpl quarterstaffNode = getEquipNodeByName(nodeList, QUARTERSTAFF);
		//assertEquals("Incorrect item name", item3.getName(), quarterstaffNode.toString());
		Assert.assertEquals("Incorrect item type", NodeType.EQUIPMENT, quarterstaffNode.getNodeType());
		Assert.assertEquals("Incorrect parent", Constants.EQUIP_LOCATION_EQUIPPED, quarterstaffNode.getParent().toString());
		Assert.assertEquals("Incorrect path", "0.1.02", quarterstaffNode.getIdPath());
		
		EquipNodeImpl bookNode = getEquipNodeByName(nodeList, BOOK);
		Assert.assertEquals("Incorrect path", "0.1.01.01", bookNode.getIdPath());
		EquipNodeImpl satchelNode = getEquipNodeByName(nodeList, SATCHEL);
		Assert.assertEquals("Incorrect path", "0.1.01", satchelNode.getIdPath());
		EquipNodeImpl bedrollNode = getEquipNodeByName(nodeList, BEDROLL);
		Assert.assertEquals("Incorrect path", "0.1.03", bedrollNode.getIdPath());
		
		Assert.assertTrue("Move down failed unexpectedly",
			esfi.moveEquipment(satchelNode, 1));
		Assert.assertEquals("Incorrect quarterstaff path after move down.", "0.1.02",
			quarterstaffNode.getIdPath());
		Assert.assertEquals("Incorrect satchel path after move down.", "0.1.03",
			satchelNode.getIdPath());
		Assert.assertEquals("Incorrect book path after move down.", "0.1.03.01",
			bookNode.getIdPath());
		Assert.assertEquals("Incorrect bedroll path after move down.", "0.1.04",
			bedrollNode.getIdPath());
		
		Assert.assertTrue("Move to bottom failed unexpectedly",
			esfi.moveEquipment(satchelNode, 1));
		Assert.assertEquals("Incorrect quarterstaff path after move to bottom.", "0.1.02",
			quarterstaffNode.getIdPath());
		Assert.assertEquals("Incorrect satchel path after move to bottom.", "0.1.05",
			satchelNode.getIdPath());
		Assert.assertEquals("Incorrect book path after move to bottom.", "0.1.05.01",
			bookNode.getIdPath());
		Assert.assertEquals("Incorrect bedroll path after move to bottom.", "0.1.04",
			bedrollNode.getIdPath());
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
	
	private EquipNodeImpl getEquipNodeByName(ListFacade<EquipNode> nodeList,
		String name)
	{
		for (EquipNode equipNode : nodeList)
		{
			if (name.equals(equipNode.toString()))
			{
				return (EquipNodeImpl) equipNode;
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
	private EquipSet addEquipToEquipSet(PlayerCharacter pc, EquipSet es,
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
	private EquipSet addEquipToEquipSet(PlayerCharacter pc, EquipSet es,
		Equipment item, float qty, String locName)
	{
		String id = EquipmentSetFacadeImpl.getNewIdPath(pc.getDisplay(), es);
		EquipSet newSet = new EquipSet(id, locName, item.getName(), item);
		item.setQty(qty);
		newSet.setQty(1.0f);
		pc.addEquipSet(newSet);
		return newSet;
	}

	/* (non-Javadoc)
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		dataset = new MockDataSetFacade(SettingsHandler.getGame());
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
			SystemCollections.addToEquipSlotsList(equipSlot, SettingsHandler.getGame().getName());
			Globals.setEquipSlotTypeCount("HANDS", "2");

			equipSlot = new EquipSlot();
			equipSlot.setSlotName(SLOT_RING);
			equipSlot.addContainedType("Ring");
			equipSlot.setContainNum(2);
			equipSlot.setSlotNumType("BODY");
			SystemCollections.addToEquipSlotsList(equipSlot, SettingsHandler.getGame().getName());
			Globals.setEquipSlotTypeCount("BODY", "1");
		}
		uiDelegate = new MockUIDelegate();
		todoManager = new TodoManager();
		equipmentList = new EquipmentListFacadeImpl();
	}
}
