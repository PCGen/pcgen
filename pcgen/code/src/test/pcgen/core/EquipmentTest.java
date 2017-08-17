/*
 *
 * Copyright 2005 (C) Andrew Wilson <nuance@sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.BaseDice;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.GenericLoader;

/**
 * Equipment Test
 */
@SuppressWarnings("nls")
public class EquipmentTest extends AbstractCharacterTestCase
{

	private Equipment eq = null;
	private Equipment eqDouble = null;
	private static final String OriginalKey = "OrigKey";
	private CampaignSourceEntry source;

	/**
	 * Main
	 * @param args
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(EquipmentTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(EquipmentTest.class);
	}

	/**
	 * Constructs a new <code>EquipmentTest</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public EquipmentTest()
	{
		// Constructor
	}

	/**
	 * Constructs a new <code>EquipmentTest</code> with the given
	 * <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public EquipmentTest(final String name)
	{
		super(name);
	}

	@Override
	public void additionalSetUp() throws PersistenceLayerException
	{
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}

		GenericLoader<Equipment> eqLoader =
				new GenericLoader<>(Equipment.class);
		eq = eqLoader.parseLine(Globals.getContext(), null,
			"Dummy	SIZE:M 	KEY:OrigKey	TYPE:Weapon", source);
		eq = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
				Equipment.class, OriginalKey);

		eqDouble = eqLoader.parseLine(Globals.getContext(), null,
			"Double	SIZE:M 	KEY:DoubleKey	TYPE:Weapon.Double", source);

		eqDouble = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
				Equipment.class, "DoubleKey");
		
		GenericLoader<EquipmentModifier> loader =
				new GenericLoader<>(EquipmentModifier.class);
		loader
			.parseLine(
				Globals.getContext(),
				null,
				"+1 (Enhancement to Weapon or Ammunition)	KEY:PLUS1W	FORMATCAT:MIDDLE	NAMEOPT:TEXT=+1	TYPE:Ammunition.Weapon	PLUS:1	VISIBLE:QUALIFY	ITYPE:Masterwork.Enhancement.Magic.Plus1	SOURCEPAGE:RSRD SpecialMaterials.rtf	BONUS:WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement	ASSIGNTOALL:NO",
				source);
		loader
			.parseLine(
				Globals.getContext(),
				null,
				"Masterwork		KEY:MWORKW	FORMATCAT:FRONT	NAMEOPT:NORMAL	TYPE:MasterworkQuality.Ammunition.Weapon	COST:0	VISIBLE:QUALIFY	ITYPE:Masterwork	SOURCEPAGE:SRDEquipmentI.rtf	BONUS:ITEMCOST|TYPE=Ammunition|6	BONUS:ITEMCOST|TYPE=Weapon|300	BONUS:WEAPON|TOHIT|1|TYPE=Enhancement	ASSIGNTOALL:YES",
				source);

		SettingsHandler.getGame().addPlusCalculation(
			"WEAPON|(2000*PLUS*PLUS)+(2000*ALTPLUS*ALTPLUS)");

	}

	/*****************************************************************************
	 * createKeyForAutoResize Tests
	 ****************************************************************************/

	// Original Key was what I expected
	public void testcreateKeyForAutoResize001()
	{
		is(this.eq.getKeyName(), strEq(OriginalKey));
	}

	/** 
	 * Try lower case letter for size
	 */
	public void testcreateKeyForAutoResize002()
	{
		SizeAdjustment newSize = small;
		
		final String expectedKey =
				Constants.AUTO_RESIZE_PREFIX + newSize.getKeyName().toUpperCase()
					+ OriginalKey;

		is(this.eq.createKeyForAutoResize(newSize), strEq(expectedKey));
	}

	/** 
	 * Try upper case word for size
	 */
	public void testcreateKeyForAutoResize003()
	{
		SizeAdjustment newSize = colossal;

		final String expectedKey =
				Constants.AUTO_RESIZE_PREFIX + newSize.getKeyName().toUpperCase().substring(0, 1)
					+ OriginalKey;

		is(this.eq.createKeyForAutoResize(newSize), strEq(expectedKey));
	}

	/** Try empty new size */
	public void testcreateKeyForAutoResize004()
	{
		is(this.eq.createKeyForAutoResize(null), strEq(OriginalKey));
	}

	/** Ensure that second customisation will work correctly */
	public void testcreateKeyForAutoResize005()
	{
		SizeAdjustment newSize = fine;

		String expectedKey =
				Constants.AUTO_RESIZE_PREFIX + newSize.getKeyName().toUpperCase().substring(0, 1)
					+ OriginalKey;

		is(this.eq.createKeyForAutoResize(newSize), strEq(expectedKey));

		newSize = diminutive;

		expectedKey =
				Constants.AUTO_RESIZE_PREFIX + newSize.getKeyName().toUpperCase().substring(0, 1)
					+ OriginalKey;

		is(this.eq.createKeyForAutoResize(newSize), strEq(expectedKey));
	}

	/** Try nonsense abbreviation for Size */
	public void testcreateKeyForAutoResize006()
	{
		String unExpectedKey = Constants.AUTO_RESIZE_PREFIX + "X" + OriginalKey;

		is(this.eq.createKeyForAutoResize(null), not(strEq(unExpectedKey)));
		is(this.eq.createKeyForAutoResize(null), strEq(OriginalKey));
	}

	/*****************************************************************************
	 * createNameForAutoResize tests
	 ****************************************************************************/

	/** Test with Size that exists and is formatted correctly */
	public void testcreateNameForAutoResize002()
	{
		is(this.eq.createNameForAutoResize(large), strEq("Dummy (Large)"));
	}

	/** Test with Abbreviation for Size that exists */
	public void testcreateNameForAutoResize003()
	{
		is(this.eq.createNameForAutoResize(fine), strEq("Dummy (Fine)"));
	}

	/** Test with Nonexistant size */
	public void testcreateNameForAutoResize004()
	{
		is(this.eq.createNameForAutoResize(null), strEq("Dummy"));
	}

	/** Test that size is replaced correctly */
	public void testcreateNameForAutoResize005()
	{
		String newKey = eq.createKeyForAutoResize(large);
		CDOMDirectSingleRef<SizeAdjustment> largeRef = CDOMDirectSingleRef.getRef(large);
		eq.put(ObjectKey.SIZE, largeRef);
		eq.setName("Pointy Stick (Large)");
		eq.put(StringKey.KEY_NAME, newKey);

		String expectedKey = Constants.AUTO_RESIZE_PREFIX + "L" + OriginalKey;

		// confirm test set up
		is(eq.getKeyName(), strEq(expectedKey));
		is(eq.getName(), strEq("Pointy Stick (Large)"));
		is(eq.getSize(), strEq("L"));

		// Now check that new name is generated Correctly
		is(this.eq.createNameForAutoResize(diminutive),
			strEq("Pointy Stick (Diminutive)"));

	}

	/** Test that size is replaced correctly */
	public void testcreateNameForAutoResize006()
	{
		String newKey = eq.createKeyForAutoResize(large);
		CDOMDirectSingleRef<SizeAdjustment> largeRef = CDOMDirectSingleRef.getRef(large);
		eq.put(ObjectKey.SIZE, largeRef);
		eq.put(ObjectKey.BASESIZE, largeRef);
		eq.setName("Pointy Stick (+1/Large)");
		eq.put(StringKey.KEY_NAME, newKey);

		String expectedKey = Constants.AUTO_RESIZE_PREFIX + "L" + OriginalKey;

		// confirm test set up
		is(eq.getKeyName(), strEq(expectedKey));
		is(eq.getName(), strEq("Pointy Stick (+1/Large)"));
		is(eq.getSize(), strEq("L"));

		// Now check that new name is generated Correctly
		is(this.eq.createNameForAutoResize(gargantuan),
			strEq("Pointy Stick (+1/Gargantuan)"));

	}

	/** Test that size is replaced correctly */
	public void testcreateNameForAutoResize007()
	{
		String newKey = eq.createKeyForAutoResize(large);
		CDOMDirectSingleRef<SizeAdjustment> largeRef = CDOMDirectSingleRef.getRef(large);
		eq.put(ObjectKey.SIZE, largeRef);
		eq.setName("Pointy Stick (+1/Large/Speed)");
		eq.put(StringKey.KEY_NAME, newKey);

		String expectedKey = Constants.AUTO_RESIZE_PREFIX + "L" + OriginalKey;

		// confirm test set up
		is(eq.getKeyName(), strEq(expectedKey));
		is(eq.getName(), strEq("Pointy Stick (+1/Large/Speed)"));
		is(eq.getSize(), strEq("L"));

		// Now check that new name is generated Correctly
		is(this.eq.createNameForAutoResize(colossal),
			strEq("Pointy Stick (+1/Colossal/Speed)"));
	}

	/** Test that size is replaced correctly */
	public void testcreateNameForAutoResize008()
	{
		String newKey = eq.createKeyForAutoResize(large);
		CDOMDirectSingleRef<SizeAdjustment> largeRef = CDOMDirectSingleRef.getRef(large);
		eq.put(ObjectKey.SIZE, largeRef);
		eq.setName("Pointy Stick (+1/Speed)");
		eq.put(StringKey.KEY_NAME, newKey);

		String expectedKey = Constants.AUTO_RESIZE_PREFIX + "L" + OriginalKey;

		// confirm test set up
		is(eq.getKeyName(), strEq(expectedKey));
		is(eq.getName(), strEq("Pointy Stick (+1/Speed)"));
		is(eq.getSize(), strEq("L"));

		// Now check that new name is generated Correctly
		is(this.eq.createNameForAutoResize(colossal),
			strEq("Pointy Stick (+1/Speed) (Colossal)"));
	}

	public void testResizeItem()
	{
		// Make it a weapon
		eq.getEquipmentHead(1).put(StringKey.DAMAGE, "1d6");
		eq.addToListFor(ListKey.TYPE, Type.WEAPON);

		// Create a base item
		Equipment custEq = eq.clone();
		custEq.put(StringKey.KEY_NAME, "Custom");
		custEq.put(ObjectKey.BASE_ITEM, CDOMDirectSingleRef.getRef(eq));
		Globals.getContext().getReferenceContext().importObject(custEq);
		Globals.getContext().getReferenceContext().importObject(eq);

		GameMode gameMode = SettingsHandler.getGame();
		is(Globals.getContext().getReferenceContext()
				.getConstructedObjectCount(SizeAdjustment.class), gt(0),
				"size list initialised");
		BaseDice d6 = gameMode.getModeContext().getReferenceContext().constructCDOMObject(BaseDice.class, "1d6");
		d6.addToDownList(new RollInfo("1d4"));
		d6.addToDownList(new RollInfo("1d3"));
		d6.addToDownList(new RollInfo("1d2"));
		d6.addToDownList(new RollInfo("1"));
		d6.addToUpList(new RollInfo("1d8"));
		d6.addToUpList(new RollInfo("2d6"));
		d6.addToUpList(new RollInfo("3d6"));
		d6.addToUpList(new RollInfo("4d6"));
		d6.addToUpList(new RollInfo("6d6"));
		d6.addToUpList(new RollInfo("8d6"));
		d6.addToUpList(new RollInfo("12d6"));
		Globals.getContext().getReferenceContext().importObject(d6);

		is(custEq.getSize(), eq("M"), "starting size");
		is(custEq.getDamage(getCharacter()), eq("1d6"), "starting size");

		// Drop the size
		custEq.resizeItem(getCharacter(), small);
		is(custEq.getSize(), eq("S"), "reduce size size");
		is(custEq.getDamage(getCharacter()), eq("1d4"), "reduce size damage");

		// Increase the size
		custEq.resizeItem(getCharacter(), large);
		is(custEq.getSize(), eq("L"), "reduce size size");
		is(custEq.getDamage(getCharacter()), eq("1d8"), "reduce size damage");
	}
	
	/**
	 * Test the loading a output of customised equipment. This time without a set of the base 
	 * item included, so a limited representation of the object is expected to be output. 
	 */
	public void testCustomEquipRoundRobin()
	{
		EquipmentModifier eqMod = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
			EquipmentModifier.class, "PLUS1W");
		assertNotNull("Eqmod should be present", eqMod);

		Equipment aEquip = eq.clone();
		String customProperties = "NAME=Falchion +1 (Small)$SIZE=S$EQMOD=PLUS1W";
		PlayerCharacter thePC = getCharacter();
		aEquip.load(customProperties, "$", "=", thePC); //$NON-NLS-1$//$NON-NLS-2$
		aEquip.setToCustomSize(thePC);
		assertEquals("Equip name", "Falchion +1 (Small)", aEquip.getDisplayName());
		assertEquals("Equip size", "S", aEquip.getSize());
		assertEquals("Equip eqmod", "PLUS1W", aEquip.getEqModifierList(true).get(0).getKeyName());
		assertEquals("Output", "Falchion +1 (Small)$EQMOD=PLUS1W", aEquip.formatSaveLine('$', '=').trim());
	}
	
	/**
	 * Test the loading a output of customised equipment. This time with a set of the base 
	 * item included, so an exact replica of the object is expected to be output. 
	 */
	public void testCustomEquipRoundRobinWithBase()
	{
		EquipmentModifier eqMod = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
			EquipmentModifier.class, "PLUS1W");
		assertNotNull("Eqmod should be present", eqMod);

		Equipment aEquip = eq.clone();
		aEquip.setBase();
		String customProperties = "NAME=Falchion +1 (Small)$SIZE=S$EQMOD=PLUS1W";
		PlayerCharacter thePC = getCharacter();
		aEquip.load(customProperties, "$", "=", thePC); //$NON-NLS-1$//$NON-NLS-2$
		aEquip.setToCustomSize(thePC);
		assertEquals("Equip name", "Falchion +1 (Small)", aEquip.getDisplayName());
		assertEquals("Equip size", "S", aEquip.getSize());
		assertEquals("Equip eqmod", "PLUS1W", aEquip.getEqModifierList(true).get(0).getKeyName());
		assertEquals("Output", "OrigKey$"+customProperties, aEquip.formatSaveLine('$', '=').trim());
		
	}
	
	/**
	 * Validate naming items using the +1 modifier 
	 */
	public void testGetItemNameFromModifiersPlus1()
	{
		EquipmentModifier eqMod = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
			EquipmentModifier.class, "PLUS1W");
		assertNotNull("Eqmod should be present", eqMod);

		Equipment aEquip = eq.clone();
		assertEquals("Name before modifier added", "Dummy", aEquip
			.getItemNameFromModifiers());
		aEquip.addEqModifier(eqMod, true, getCharacter());
		assertEquals("Name after modifier added", "Dummy +1", aEquip
			.getItemNameFromModifiers());
		
	}
	
	/**
	 * Validate naming items using the masterwork equip modifier
	 */
	public void testGetItemNameFromModifiersMasterwork()
	{
		EquipmentModifier eqMod = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
			EquipmentModifier.class, "MWORKW");
		assertNotNull("Eqmod should be present", eqMod);

		Equipment aEquip = eq.clone();
		assertEquals("Name before modifier added", "Dummy", aEquip
			.getItemNameFromModifiers());
		aEquip.addEqModifier(eqMod, true, getCharacter());
		assertEquals("Name after modifier added", "Masterwork Dummy", aEquip
			.getItemNameFromModifiers());
		
	}
	
	/**
	 * Validate naming items using the NOTHING name option 
	 * @throws Exception 
	 */
	public void testGetItemNameFromModifiersNothing() throws Exception
	{
		GenericLoader<EquipmentModifier> loader =
				new GenericLoader<>(EquipmentModifier.class);
		loader
			.parseLine(
				Globals.getContext(),
				null,
				"Hidden Mod	KEY:HIDDENMOD	FORMATCAT:PARENS	NAMEOPT:TEXT=Foo	TYPE:Ammunition.Weapon	VISIBLE:QUALIFY	ITYPE:Masterwork.Enhancement.Magic.Plus1",
				source);
		EquipmentModifier eqMod =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
					EquipmentModifier.class, "HIDDENMOD");
		assertNotNull("Eqmod should be present", eqMod);
		loader
		.parseLine(
			Globals.getContext(),
			null,
			"Hidden Mod 2	KEY:HIDDENMOD2	FORMATCAT:PARENS	NAMEOPT:NOTHING	TYPE:Ammunition.Weapon	VISIBLE:QUALIFY	ITYPE:Masterwork.Enhancement.Magic.Plus1",
			source);
	EquipmentModifier eqMod2 =
			Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
				EquipmentModifier.class, "HIDDENMOD2");
	assertNotNull("Eqmod should be present", eqMod);
	assertNotNull("Eqmod should be present", eqMod);
	loader
	.parseLine(
		Globals.getContext(),
		null,
		"Hidden Mod 2	KEY:HIDDENMOD3	FORMATCAT:PARENS	NAMEOPT:NOTHING	TYPE:Ammunition.Weapon	VISIBLE:QUALIFY	ITYPE:Masterwork.Enhancement.Magic.Plus1",
		source);
EquipmentModifier eqMod3 =
		Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
			EquipmentModifier.class, "HIDDENMOD3");
assertNotNull("Eqmod should be present", eqMod);

		Equipment aEquip = eq.clone();
		assertEquals("Name before modifier added", "Dummy", aEquip
			.getItemNameFromModifiers());
		aEquip.addEqModifier(eqMod, true, getCharacter());
		assertEquals("Name after modifier added", "Dummy (Foo)", aEquip
			.getItemNameFromModifiers());
		aEquip.addEqModifier(eqMod2, true, getCharacter());
		assertEquals("Name after modifier added", "Dummy (Foo)", aEquip
			.getItemNameFromModifiers());
		aEquip.addEqModifier(eqMod3, true, getCharacter());
		assertEquals("Name after modifier added", "Dummy (Foo)", aEquip
			.getItemNameFromModifiers());

	}
	
	/**
	 * Validate the processing of the getCost function. 
	 */
	public void testGetCost()
	{
		EquipmentModifier eqMod = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
			EquipmentModifier.class, "MWORKW");
		assertNotNull("Eqmod MWORKW should be present", eqMod);

		EquipmentModifier eqModPlus = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
			EquipmentModifier.class, "PLUS1W");
		assertNotNull("Eqmod PLUS1W should be present", eqModPlus);

		Equipment aEquip = eq.clone();
		assertEquals("Default cost of item", BigDecimal.ZERO, aEquip.getCost(getCharacter()));
		
		aEquip.addEqModifier(eqMod, true, getCharacter());
		assertEquals(
			"Invalid cost when adding an eqmod with a bonus to ITEMCOST", 300,
			aEquip.getCost(getCharacter()).floatValue(), 0.01);
		
		aEquip.addEqModifier(eqModPlus, true, getCharacter());
		assertEquals("Invalid cost when adding an eqmod with a plus", 2300,
			aEquip.getCost(getCharacter()).floatValue(), 0.01);
	}
	
	/**
	 * Test the use of HEADPLUSTOTAL in COST and BONUS:ITEMCOST formulas on 
	 * both primary and alternate heads.
	 * @throws Exception
	 */
	public void testGetCostWithHeadPlus() throws Exception
	{
		GenericLoader<EquipmentModifier> loader =
				new GenericLoader<>(EquipmentModifier.class);
		loader
			.parseLine(
				Globals.getContext(),
				null,
				"HeadPlusTest		KEY:HEADPT	FORMATCAT:FRONT	NAMEOPT:NORMAL	TYPE:MasterworkQuality.Weapon	COST:HEADPLUSTOTAL*20	BONUS:ITEMCOST|TYPE=Weapon|HEADPLUSTOTAL*5	VISIBLE:YES",
				source);
		EquipmentModifier eqMod =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
					EquipmentModifier.class, "HEADPT");
		assertNotNull("Eqmod HEADPT should be present", eqMod);

		loader
			.parseLine(
				Globals.getContext(),
				null,
				"HeadPlusTest		KEY:HEADPT2	FORMATCAT:FRONT	NAMEOPT:NORMAL	TYPE:MasterworkQuality.Weapon	COST:HEADPLUSTOTAL*21	BONUS:ITEMCOST|TYPE=Weapon|HEADPLUSTOTAL*7	VISIBLE:YES",
				source);
		EquipmentModifier eqMod2 =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
					EquipmentModifier.class, "HEADPT2");

	
		EquipmentModifier eqModPlus = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
			EquipmentModifier.class, "PLUS1W");
		assertNotNull("Eqmod PLUS1W should be present", eqModPlus);

		Equipment aEquip = eqDouble.clone();
		assertEquals("Default cost of item", BigDecimal.ZERO, aEquip.getCost(getCharacter()));
		
		aEquip.addEqModifier(eqMod, true, getCharacter());
		assertEquals("Invalid cost when adding an eqmod with no plus", 0,
			aEquip.getCost(getCharacter()).floatValue(), 0.01);
		
		aEquip.addEqModifier(eqModPlus, true, getCharacter());
		assertEquals("Invalid cost when adding an eqmod with a plus", 2025,
			aEquip.getCost(getCharacter()).floatValue(), 0.01);

		aEquip.addEqModifier(eqMod2, false, getCharacter());
		assertEquals("Invalid cost when adding an eqmod to alt head with no plus", 2025,
			aEquip.getCost(getCharacter()).floatValue(), 0.01);
		
		aEquip.addEqModifier(eqModPlus, false, getCharacter());
		assertEquals("Invalid cost when adding an eqmod to alt head with a plus", 4053,
			aEquip.getCost(getCharacter()).floatValue(), 0.01);
		
	}
	
	/**
	 * Test the nameItemFromModifiers method, specifically for the
	 * use of a null character.
	 */
	public void testNameItemFromModifiers()
	{
		String name = eq.nameItemFromModifiers(null);
		assertEquals("Dummy", name);
		name = eq.nameItemFromModifiers(getCharacter());
		assertEquals("Dummy", name);
		
		name = eqDouble.nameItemFromModifiers(getCharacter());
		assertEquals("Double", name);
		Equipment item = eqDouble.clone();
		EquipmentModifier eqModPlus = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
			EquipmentModifier.class, "PLUS1W");
		item.addEqModifier(eqModPlus, true, getCharacter());
		item.put(ObjectKey.BASE_ITEM, CDOMDirectSingleRef.getRef(eqDouble));
		name = item.nameItemFromModifiers(getCharacter());
		assertEquals("Double +1_-", name);
	}
	
	/**
	 * Verify that isPreType is working correctly for eqmods.
	 */
	public void testIsPreTypeEqMod()
	{
		EquipmentModifier eqMod = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
			EquipmentModifier.class, "PLUS1W");
		assertNotNull("Eqmod should be present", eqMod);

		Equipment aEquip = eq.clone();
		assertFalse("Does not have eqmod yet", aEquip.isPreType("EQMOD=PLUS1W"));
		aEquip.addEqModifier(eqMod, true, getCharacter());
		assertTrue("Should have eqmod now", aEquip.isPreType("EQMOD=PLUS1W"));
		assertFalse("Should not have eqmod with choice", aEquip.isPreType("EQMOD=PLUS1W(Choice)"));
		aEquip.addAssociation(eqMod, "ChoicE");
		assertTrue("Should have eqmod with choice now", aEquip.isPreType("EQMOD=PLUS1W(Choice)"));
		assertFalse("Should not have choice Bad", aEquip.isPreType("EQMOD=PLUS1W(Bad)"));
		
	}
}
