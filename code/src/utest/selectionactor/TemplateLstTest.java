/*
 * 
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package selectionactor;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.test.InequalityTester;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.facet.AbstractStorageFacet;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstSystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.ReferenceContext;
import plugin.lsttokens.AutoLst;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.TemplateLst;
import plugin.lsttokens.TypeLst;
import plugin.lsttokens.ability.MultToken;
import plugin.lsttokens.ability.VisibleToken;
import plugin.lsttokens.auto.LangToken;
import plugin.lsttokens.equipment.ProficiencyToken;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.primitive.language.LangBonusToken;
import selectionactor.testsupport.InequalityTesterInst;

public class TemplateLstTest extends TestCase
{

	static TemplateLst csa = new TemplateLst();

	@Test
	public void testEmpty()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	public ChooseSelectionActor<PCTemplate> getActor()
	{
		return csa;
	}

	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Test
	public void testAddRemoveSimple() throws PersistenceLayerException
	{
		setUpPC();
		finishLoad(Globals.getContext());
		InequalityTester it = InequalityTesterInst.getInstance();
		PCClass owner = new PCClass();
		PCTemplate obj1 = construct("Templ");
		PCTemplate obj2 = construct("Templ2");
		PlayerCharacter pc1 = new PlayerCharacter();
		PlayerCharacter pc2 = new PlayerCharacter();
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().applyChoice(owner, obj1, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().applyChoice(owner, obj1, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().removeChoice(owner, obj1, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().removeChoice(owner, obj1, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().applyChoice(owner, obj1, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().applyChoice(owner, obj1, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().removeChoice(owner, obj1, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().removeChoice(owner, obj1, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().applyChoice(owner, obj1, pc1);
		getActor().applyChoice(owner, obj2, pc1);
		getActor().removeChoice(owner, obj1, pc1);
		getActor().applyChoice(owner, obj2, pc2);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
	}

	@Test
	public void testAddRemoveHasChild() throws PersistenceLayerException
	{
		setUpPC();
		PCTemplate obj1 = construct("Templ");
		PCTemplate obj2 = construct("Templ2");
		Globals.getContext().unconditionallyProcess(obj1, "AUTO", "LANG|Universal");
		Globals.getContext().unconditionallyProcess(obj2, "AUTO", "LANG|Other");
		finishLoad(Globals.getContext());
		InequalityTester it = InequalityTesterInst.getInstance();
		PCClass owner = new PCClass();
		PlayerCharacter pc1 = new PlayerCharacter();
		PlayerCharacter pc2 = new PlayerCharacter();
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().applyChoice(owner, obj1, pc2);
		assertTrue(pc2.hasLanguage(universal));
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().applyChoice(owner, obj1, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().removeChoice(owner, obj1, pc2);
		assertFalse(pc2.hasLanguage(universal));
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().removeChoice(owner, obj1, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().applyChoice(owner, obj1, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().applyChoice(owner, obj1, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().removeChoice(owner, obj1, pc2);
		assertFalse(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().removeChoice(owner, obj1, pc1);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
		getActor().applyChoice(owner, obj1, pc1);
		getActor().applyChoice(owner, obj2, pc1);
		getActor().removeChoice(owner, obj1, pc1);
		getActor().applyChoice(owner, obj2, pc2);
		assertTrue(AbstractStorageFacet.areEqualCache(pc1.getCharID(), pc2.getCharID(), it));
	}

	private PCTemplate construct(String name)
	{
		return Globals.getContext().ref.constructCDOMObject(getCDOMClass(), name);
	}

	protected PCStat str;
	protected PCStat cha;
	protected PCStat dex;
	protected PCStat wis;
	protected PCStat intel;
	protected PCAlignment lg;
	protected PCAlignment ln;
	protected PCAlignment le;
	protected PCAlignment ng;
	protected PCAlignment tn;
	protected PCAlignment ne;
	protected PCAlignment cg;
	protected PCAlignment cn;
	protected PCAlignment ce;
	protected SizeAdjustment colossal;
	protected SizeAdjustment gargantuan;
	protected SizeAdjustment huge;
	protected SizeAdjustment large;
	protected SizeAdjustment medium;
	protected SizeAdjustment small;
	protected SizeAdjustment tiny;
	protected SizeAdjustment diminutive;
	protected SizeAdjustment fine;
	protected Language universal;
	protected Language other;

	private static final MultToken ABILITY_MULT_TOKEN = new plugin.lsttokens.ability.MultToken();
	private static final plugin.lsttokens.choose.LangToken CHOOSE_LANG_TOKEN = new plugin.lsttokens.choose.LangToken();
	private static final VisibleToken ABILITY_VISIBLE_TOKEN = new plugin.lsttokens.ability.VisibleToken();
	private static final ChooseLst CHOOSE_TOKEN = new plugin.lsttokens.ChooseLst();
	private static final AutoLst AUTO_TOKEN = new plugin.lsttokens.AutoLst();
	private static final LangToken AUTO_LANG_TOKEN = new plugin.lsttokens.auto.LangToken();
	private static final ProficiencyToken EQUIP_PROFICIENCY_TOKEN = new plugin.lsttokens.equipment.ProficiencyToken();
	private static final TypeLst EQUIP_TYPE_TOKEN = new plugin.lsttokens.TypeLst();
	private static final LangBonusToken LANGBONUS_PRIM = new plugin.primitive.language.LangBonusToken();
	private static final plugin.qualifier.language.PCToken PC_QUAL = new plugin.qualifier.language.PCToken();

	protected void finishLoad(LoadContext context)
	{
		context.ref.buildDeferredObjects();
		context.ref.buildDerivedObjects();
		context.resolveDeferredTokens();
		assertTrue(context.ref.resolveReferences(null));
		context.resolvePostDeferredTokens();
	}

	protected void setUpPC() throws PersistenceLayerException
	{
		TokenRegistration.register(AUTO_LANG_TOKEN);
		TokenRegistration.register(ABILITY_VISIBLE_TOKEN);
		TokenRegistration.register(AUTO_TOKEN);
		TokenRegistration.register(CHOOSE_TOKEN);
		TokenRegistration.register(CHOOSE_LANG_TOKEN);
		TokenRegistration.register(ABILITY_MULT_TOKEN);
		TokenRegistration.register(EQUIP_TYPE_TOKEN);
		TokenRegistration.register(EQUIP_PROFICIENCY_TOKEN);
		TokenRegistration.register(LANGBONUS_PRIM);
		TokenRegistration.register(PC_QUAL);

		Globals.createEmptyRace();
		Globals.setUseGUI(false);
		Globals.emptyLists();
		GameMode gamemode = SettingsHandler.getGame();

		str = createStat("Strength", "STR");
		str.put(VariableKey.getConstant("LOADSCORE"), FormulaFactory
			.getFormulaFor("STRSCORE"));
		str.put(VariableKey.getConstant("OFFHANDLIGHTBONUS"), FormulaFactory
			.getFormulaFor(2));
		dex = createStat("Dexterity", "DEX");
		PCStat con = createStat("Constitution", "CON");
		intel = createStat("Intelligence", "INT");
		wis = createStat("Wisdom", "WIS");
		cha = createStat("Charisma", "CHA");

		ReferenceContext ref = Globals.getContext().ref;
		lg = createAlignment("Lawful Good", "LG");
		ref.importObject(lg);
		ln = createAlignment("Lawful Neutral", "LN");
		ref.importObject(ln);
		le = createAlignment("Lawful Evil", "LE");
		ref.importObject(le);
		ng = createAlignment("Neutral Good", "NG");
		ref.importObject(ng);
		tn = createAlignment("True Neutral", "TN");
		ref.importObject(tn);
		ne = createAlignment("Neutral Evil", "NE");
		ref.importObject(ne);
		cg = createAlignment("Chaotic Good", "CG");
		ref.importObject(cg);
		cn = createAlignment("Chaotic Neutral", "CN");
		ref.importObject(cn);
		ce = createAlignment("Chaotic Evil", "CE");
		ref.importObject(ce);
		ref.importObject(createAlignment("None", "NONE"));
		ref.importObject(createAlignment("Deity's", "Deity"));

		gamemode.setBonusFeatLevels("3|3");

		SettingsHandler.setGame("3.5");

		ref.importObject(str);
		ref.importObject(dex);
		ref.importObject(con);
		ref.importObject(intel);
		ref.importObject(wis);
		ref.importObject(cha);

		fine = createSize("Fine");
		diminutive = createSize("Diminutive");
		tiny = createSize("Tiny");
		small = createSize("Small");
		medium = createSize("Medium");
		medium.put(ObjectKey.IS_DEFAULT_SIZE, true);
		large = createSize("Large");
		huge = createSize("Huge");
		gargantuan = createSize("Gargantuan");
		colossal = createSize("Colossal");

		for (PCStat stat : ref.getOrderSortedCDOMObjects(PCStat.class))
		{
			ref.registerAbbreviation(stat, stat.getAbb());
		}
		for (PCAlignment al : ref.getOrderSortedCDOMObjects(PCAlignment.class))
		{
			ref.registerAbbreviation(al, al.getAbb());
		}
		universal = ref.constructCDOMObject(Language.class, "Universal");
		other = ref.constructCDOMObject(Language.class, "Other");
		LstSystemLoader.createLangBonusObject(Globals.getContext());
	}

	private PCStat createStat(String name, String abb)
	{
		PCStat stat = new PCStat();
		stat.setName(name);
		stat.put(StringKey.ABB, abb);
		stat.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		stat.put(VariableKey.getConstant("MAXLEVELSTAT=" + stat.getAbb()),
				FormulaFactory.getFormulaFor(stat.getAbb() + "SCORE-10"));
		return stat;
	}

	private SizeAdjustment createSize(String name)
	{
		final String abb  = name.substring(0, 1);

		final SizeAdjustment sa = new SizeAdjustment();

		sa.setName(name);
		sa.put(StringKey.ABB, abb);

		Globals.getContext().ref.importObject(sa);
		Globals.getContext().ref.registerAbbreviation(sa, sa.getAbbreviation());
		return sa;
	}

	public static PCAlignment createAlignment(final String longName,
		final String shortName)
	{
		final PCAlignment align = new PCAlignment();
		align.setName(longName);
		align.put(StringKey.ABB, shortName);
		return align;
	}

}
