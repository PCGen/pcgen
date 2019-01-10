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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

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

import org.hamcrest.Matchers;

/**
 * Equipment Test
 */
@SuppressWarnings("nls")
public class EquipmentTest extends AbstractCharacterTestCase
{

	private Equipment eq = null;
	private Equipment eqDouble = null;
	private static final String ORIGINAL_KEY = "OrigKey";
	private CampaignSourceEntry source;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
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
				Equipment.class, ORIGINAL_KEY);

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
				"+1 (Enhancement to Weapon or Ammunition)	KEY:PLUS1W	FORMATCAT:MIDDLE	NAMEOPT:TEXT=+1	"
				+ "TYPE:Ammunition.Weapon	PLUS:1	VISIBLE:QUALIFY	ITYPE:Masterwork.Enhancement.Magic.Plus1	"
				+ "SOURCEPAGE:RSRD SpecialMaterials.rtf	BONUS:WEAPON|DAMAGE,TOHIT|1|TYPE=Enhancement	ASSIGNTOALL:NO",
				source);
		loader
			.parseLine(
				Globals.getContext(),
				null,
				"Masterwork		KEY:MWORKW	FORMATCAT:FRONT	NAMEOPT:NORMAL	TYPE:MasterworkQuality.Ammunition.Weapon"
				+ "	COST:0	VISIBLE:QUALIFY	ITYPE:Masterwork	SOURCEPAGE:SRDEquipmentI.rtf	"
				+ "BONUS:ITEMCOST|TYPE=Ammunition|6	BONUS:ITEMCOST|TYPE=Weapon|300	"
				+ "BONUS:WEAPON|TOHIT|1|TYPE=Enhancement	ASSIGNTOALL:YES",
				source);

		SettingsHandler.getGame().addPlusCalculation(
			"WEAPON|(2000*PLUS*PLUS)+(2000*ALTPLUS*ALTPLUS)");
		
		finishLoad();
	}

	/*****************************************************************************
	 * createKeyForAutoResize Tests
	 ****************************************************************************/

	public void testcreateKeyForAutoResize001()
	{
		assertThat(this.eq.getKeyName(), Matchers.is(equalTo(ORIGINAL_KEY)));
	}

	/** 
	 * Try lower case letter for size
	 */
	public void testcreateKeyForAutoResize002()
	{
		SizeAdjustment newSize = small;
		
		final String expectedKey =
				Constants.AUTO_RESIZE_PREFIX + newSize.getKeyName().toUpperCase()
					+ ORIGINAL_KEY;

		assertThat(this.eq.createKeyForAutoResize(newSize), Matchers.is(equalTo((expectedKey))));
	}

	/**
	 * Try upper case word for size
	 */
	public void testcreateKeyForAutoResize003()
	{
		SizeAdjustment newSize = colossal;

		final String expectedKey =
				Constants.AUTO_RESIZE_PREFIX + newSize.getKeyName().toUpperCase().substring(0, 1)
					+ ORIGINAL_KEY;

		assertThat(this.eq.createKeyForAutoResize(newSize), Matchers.is(equalTo((expectedKey))));
	}

	/** Try empty new size */
	public void testcreateKeyForAutoResize004()
	{
		assertThat(this.eq.createKeyForAutoResize(null), Matchers.is(equalTo((ORIGINAL_KEY))));
	}

	/** Ensure that second customisation will work correctly */
	public void testcreateKeyForAutoResize005()
	{
		SizeAdjustment newSize = fine;

		String expectedKey =
				Constants.AUTO_RESIZE_PREFIX + newSize.getKeyName().toUpperCase().substring(0, 1)
					+ ORIGINAL_KEY;

		assertThat(this.eq.createKeyForAutoResize(newSize), Matchers.is(equalTo((expectedKey))));

		newSize = diminutive;

		expectedKey =
				Constants.AUTO_RESIZE_PREFIX + newSize.getKeyName().toUpperCase().substring(0, 1)
					+ ORIGINAL_KEY;

		assertThat(this.eq.createKeyForAutoResize(newSize), Matchers.is(equalTo((expectedKey))));
	}

	/** Try nonsense abbreviation for Size */
	public void testcreateKeyForAutoResize006()
	{
		String unExpectedKey = Constants.AUTO_RESIZE_PREFIX + "X" + ORIGINAL_KEY;

		assertThat(unExpectedKey, not(Matchers.is(this.eq.createKeyForAutoResize(null))));
		assertThat(this.eq.createKeyForAutoResize(null), Matchers.is(equalTo((ORIGINAL_KEY))));
	}

	/*****************************************************************************
	 * createNameForAutoResize tests
	 ****************************************************************************/

	/** Test with Size that exists and is formatted correctly */
	public void testcreateNameForAutoResize002()
	{
		assertThat(this.eq.createNameForAutoResize(large), Matchers.is(equalTo(("Dummy (Large)"))));
	}

	/** Test with Abbreviation for Size that exists */
	public void testcreateNameForAutoResize003()
	{
		assertThat(this.eq.createNameForAutoResize(fine), Matchers.is(equalTo(("Dummy (Fine)"))));
	}

	/** Test with Nonexistant size */
	public void testcreateNameForAutoResize004()
	{
		assertThat(this.eq.createNameForAutoResize(null), Matchers.is(equalTo(("Dummy"))));
	}

	/** Test that size is replaced correctly */
	public void testcreateNameForAutoResize005()
	{
		String newKey = eq.createKeyForAutoResize(large);
		CDOMDirectSingleRef<SizeAdjustment> largeRef = CDOMDirectSingleRef.getRef(large);
		eq.put(ObjectKey.SIZE, largeRef);
		eq.setName("Pointy Stick (Large)");
		eq.put(StringKey.KEY_NAME, newKey);

		String expectedKey = Constants.AUTO_RESIZE_PREFIX + "L" + ORIGINAL_KEY;

		// confirm test set up
		assertThat(eq.getKeyName(), Matchers.is(equalTo((expectedKey))));
		assertThat(eq.getName(), Matchers.is(equalTo(("Pointy Stick (Large)"))));
		assertThat(eq.getSize(), Matchers.is(equalTo(("L"))));

		// Now check that new name is generated Correctly
		assertThat(this.eq.createNameForAutoResize(diminutive),
			Matchers.is(equalTo(("Pointy Stick (Diminutive)"))));

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

		String expectedKey = Constants.AUTO_RESIZE_PREFIX + "L" + ORIGINAL_KEY;

		// confirm test set up
		assertThat(eq.getKeyName(), Matchers.is(equalTo((expectedKey))));
		assertThat(eq.getName(), Matchers.is(equalTo(("Pointy Stick (+1/Large)"))));
		assertThat(eq.getSize(), Matchers.is(equalTo(("L"))));

		// Now check that new name is generated Correctly
		assertThat(this.eq.createNameForAutoResize(gargantuan),
			Matchers.is(equalTo(("Pointy Stick (+1/Gargantuan)"))));

	}

	/** Test that size is replaced correctly */
	public void testcreateNameForAutoResize007()
	{
		String newKey = eq.createKeyForAutoResize(large);
		CDOMDirectSingleRef<SizeAdjustment> largeRef = CDOMDirectSingleRef.getRef(large);
		eq.put(ObjectKey.SIZE, largeRef);
		eq.setName("Pointy Stick (+1/Large/Speed)");
		eq.put(StringKey.KEY_NAME, newKey);

		String expectedKey = Constants.AUTO_RESIZE_PREFIX + "L" + ORIGINAL_KEY;

		// confirm test set up
		assertThat(eq.getKeyName(), Matchers.is(equalTo((expectedKey))));
		assertThat(eq.getName(), Matchers.is(equalTo(("Pointy Stick (+1/Large/Speed)"))));
		assertThat(eq.getSize(), Matchers.is(equalTo(("L"))));

		// Now check that new name is generated Correctly
		assertThat(this.eq.createNameForAutoResize(colossal),
			Matchers.is(equalTo(("Pointy Stick (+1/Colossal/Speed)"))));
	}

	/** Test that size is replaced correctly */
	public void testcreateNameForAutoResize008()
	{
		String newKey = eq.createKeyForAutoResize(large);
		CDOMDirectSingleRef<SizeAdjustment> largeRef = CDOMDirectSingleRef.getRef(large);
		eq.put(ObjectKey.SIZE, largeRef);
		eq.setName("Pointy Stick (+1/Speed)");
		eq.put(StringKey.KEY_NAME, newKey);

		String expectedKey = Constants.AUTO_RESIZE_PREFIX + "L" + ORIGINAL_KEY;

		// confirm test set up
		assertThat(eq.getKeyName(), Matchers.is(equalTo((expectedKey))));
		assertThat(eq.getName(), Matchers.is(equalTo(("Pointy Stick (+1/Speed)"))));
		assertThat(eq.getSize(), Matchers.is(equalTo(("L"))));

		// Now check that new name is generated Correctly
		assertThat(this.eq.createNameForAutoResize(colossal),
			Matchers.is(equalTo(("Pointy Stick (+1/Speed) (Colossal)"))));
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
		assertThat("size list initialised",
				Globals.getContext().getReferenceContext().getConstructedObjectCount(SizeAdjustment.class),
			Matchers.is(greaterThan(0)));
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

		assertThat("starting size", custEq.getSize(), Matchers.is("M"));
		assertThat("starting size", custEq.getDamage(getCharacter()), Matchers.is("1d6"));

		// Drop the size
		custEq.resizeItem(getCharacter(), small);
		assertThat("reduce size", custEq.getSize(), Matchers.is("S"));
		assertThat("reduce size", custEq.getDamage(getCharacter()), Matchers.is("1d4"));

		// Increase the size
		custEq.resizeItem(getCharacter(), large);
		assertThat("increase size", custEq.getSize(), Matchers.is("L"));
		assertThat("increase size", custEq.getDamage(getCharacter()), Matchers.is("1d8"));
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
				"Hidden Mod	KEY:HIDDENMOD	FORMATCAT:PARENS	NAMEOPT:TEXT=Foo	TYPE:Ammunition.Weapon	"
				+ "VISIBLE:QUALIFY	ITYPE:Masterwork.Enhancement.Magic.Plus1",
				source);
		EquipmentModifier eqMod =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
					EquipmentModifier.class, "HIDDENMOD");
		assertNotNull("Eqmod should be present", eqMod);
		loader
		.parseLine(
			Globals.getContext(),
			null,
			"Hidden Mod 2	KEY:HIDDENMOD2	FORMATCAT:PARENS	NAMEOPT:NOTHING	TYPE:Ammunition.Weapon	"
			+ "VISIBLE:QUALIFY	ITYPE:Masterwork.Enhancement.Magic.Plus1",
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
		"Hidden Mod 2	KEY:HIDDENMOD3	FORMATCAT:PARENS	NAMEOPT:NOTHING	TYPE:Ammunition.Weapon	"
		+ "VISIBLE:QUALIFY	ITYPE:Masterwork.Enhancement.Magic.Plus1",
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
	 * 
	 * @throws PersistenceLayerException  if there is a problem with the LST syntax
	 */
	public void testGetCostWithHeadPlus() throws PersistenceLayerException
	{
		GenericLoader<EquipmentModifier> loader =
				new GenericLoader<>(EquipmentModifier.class);
		loader
			.parseLine(
				Globals.getContext(),
				null,
				"HeadPlusTest		KEY:HEADPT	FORMATCAT:FRONT	NAMEOPT:NORMAL	TYPE:MasterworkQuality.Weapon	"
				+ "COST:HEADPLUSTOTAL*20	BONUS:ITEMCOST|TYPE=Weapon|HEADPLUSTOTAL*5	VISIBLE:YES",
				source);
		EquipmentModifier eqMod =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
					EquipmentModifier.class, "HEADPT");
		assertNotNull("Eqmod HEADPT should be present", eqMod);

		loader
			.parseLine(
				Globals.getContext(),
				null,
				"HeadPlusTest		KEY:HEADPT2	FORMATCAT:FRONT	NAMEOPT:NORMAL	TYPE:MasterworkQuality.Weapon	"
				+ "COST:HEADPLUSTOTAL*21	BONUS:ITEMCOST|TYPE=Weapon|HEADPLUSTOTAL*7	VISIBLE:YES",
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

	/**
	 * EquipmentModifiers must have a parent in order to be rendered to an output sheet
	 */
	public void testAddEqModifierSetsEquipmentAsParentOfTheModifier()
	{
		EquipmentModifier eqMod = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
				EquipmentModifier.class, "PLUS1W");
		assertNotNull("Eqmod should be present", eqMod);
		assertTrue("Eqmod parent should be null at beginning", eqMod.getVariableParent().isEmpty());

		Equipment aEquip = eq.clone();
		aEquip.addEqModifier(eqMod, true, null);
		assertSame("Eqmod parent should be the equipment", aEquip, eqMod.getVariableParent().get());
	}

	@Override
	protected void defaultSetupEnd()
	{
		//Nothing, we will trigger ourselves
	}
}
