/*
 * Copyright (c) 2013 Tom Parker <thpr@users.sourceforge.net>
 * 
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
package pcgen.io.testsupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

import pcgen.ControlTestSupport;
import pcgen.base.test.InequalityTester;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.facet.DirectAbilityFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.RaceSelectionFacet;
import pcgen.cdom.facet.TemplateSelectionFacet;
import pcgen.cdom.facet.WeaponProfFacet;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.facet.model.ActiveEqModFacet;
import pcgen.cdom.facet.model.BioSetFacet;
import pcgen.cdom.facet.model.CheckFacet;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.ClassLevelFacet;
import pcgen.cdom.facet.model.CompanionModFacet;
import pcgen.cdom.facet.model.DomainFacet;
import pcgen.cdom.facet.model.ExpandedCampaignFacet;
import pcgen.cdom.facet.model.LanguageFacet;
import pcgen.cdom.facet.model.SizeFacet;
import pcgen.cdom.facet.model.SkillFacet;
import pcgen.cdom.facet.model.StatFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.cdom.util.CControl;
import pcgen.core.Deity;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.gui2.facade.MockUIDelegate;
import pcgen.io.PCGIOHandler;
import pcgen.io.PCGVer2Creator;
import pcgen.output.channel.compat.AgeCompat;
import pcgen.output.channel.compat.HandedCompat;
import pcgen.persistence.SourceFileLoader;
import pcgen.persistence.lst.LevelLoader;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.util.chooser.ChooserFactory;

import plugin.bonustokens.Feat;
import plugin.lsttokens.AutoLst;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.TypeLst;
import plugin.lsttokens.ability.MultToken;
import plugin.lsttokens.ability.VisibleToken;
import plugin.lsttokens.auto.LangToken;
import plugin.lsttokens.equipment.ProficiencyToken;
import plugin.lsttokens.level.CcskillmaxToken;
import plugin.lsttokens.level.CskillmaxToken;
import plugin.lsttokens.level.LevelToken;
import plugin.lsttokens.level.MinxpToken;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.modifier.cdom.SetModifierFactory;
import plugin.primitive.language.LangBonusToken;
import plugin.qualifier.language.PCToken;

import compare.InequalityTesterInst;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import util.FormatSupport;
import util.TestURI;

public abstract class AbstractSaveRestoreTest
{

	private static final MultToken ABILITY_MULT_TOKEN =
			new MultToken();
	private static final ChooseLst CHOOSE_TOKEN =
			new ChooseLst();
	private static final plugin.lsttokens.choose.LangToken CHOOSE_LANG_TOKEN =
			new plugin.lsttokens.choose.LangToken();
	private static final VisibleToken ABILITY_VISIBLE_TOKEN =
			new VisibleToken();
	private static final AutoLst AUTO_TOKEN =
			new AutoLst();
	private static final LangToken AUTO_LANG_TOKEN =
			new LangToken();
	private static final ProficiencyToken EQUIP_PROFICIENCY_TOKEN =
			new ProficiencyToken();
	private static final TypeLst EQUIP_TYPE_TOKEN =
			new TypeLst();
	private static final LangBonusToken LANGBONUS_PRIM =
			new LangBonusToken();
	private static final PCToken PC_QUAL =
			new PCToken();
	private static final SetModifierFactory SMF =
			new SetModifierFactory();

	protected LoadContext context;
	protected PlayerCharacter pc;
	protected PlayerCharacter reloadedPC;
	protected CharID id;

	@BeforeAll
	public static void classSetUp()
	{
		TokenRegistration.register(new LevelToken());
		TokenRegistration.register(new MinxpToken());
		TokenRegistration.register(new CskillmaxToken());
		TokenRegistration.register(new CcskillmaxToken());
		SettingsHandler.setGame("3.5");
		final GameMode gameMode = SettingsHandler.getGameAsProperty().get();
		gameMode.setBonusFeatLevels("3|3");
		LevelLoader.parseLine(gameMode,
			"LEVEL:LEVEL	MINXP:(LEVEL*LEVEL-LEVEL)*500		"
		+ "CSKILLMAX:LEVEL+ClassSkillMax+3	CCSKILLMAX:(LEVEL+CrossClassSkillMax+3)/2",
			0, TestURI.getURI(), "Default");
		ControlTestSupport.enableFeature(gameMode.getModeContext(), CControl.ALIGNMENTFEATURE);
	}

	protected <T extends Loadable> T create(Class<T> cl, String key)
	{
		return context.getReferenceContext().constructCDOMObject(cl, key);
	}

	protected void finishLoad()
	{
		context.commit();
		SourceFileLoader.processFactDefinitions(context);
		context.getReferenceContext().buildDeferredObjects();
		context.getReferenceContext().buildDerivedObjects();
		context.resolveDeferredTokens();
		assertTrue(context.getReferenceContext().resolveReferences(null));
		context.resolvePostValidationTokens();
		context.resolvePostDeferredTokens();
		context.loadCampaignFacets();
		pc = new PlayerCharacter();
		setBoilerplate();
		reloadedPC = new PlayerCharacter(Collections.emptyList());
		id = pc.getCharID();
	}

	protected PCStat str;
	protected PCStat cha;
	protected PCStat dex;
	protected PCStat wis;
	protected PCStat con;
	protected PCStat intel;
	private PCAlignment lg;
	private PCAlignment ln;
	protected PCAlignment le;
	private PCAlignment ng;
	private PCAlignment tn;
	private PCAlignment ne;
	private PCAlignment cg;
	private PCAlignment cn;
	private PCAlignment ce;
	private SizeAdjustment colossal;
	private SizeAdjustment gargantuan;
	private SizeAdjustment huge;
	private SizeAdjustment large;
	private SizeAdjustment medium;
	private SizeAdjustment small;
	private SizeAdjustment tiny;
	private SizeAdjustment diminutive;
	private SizeAdjustment fine;

	private DirectAbilityFacet directAbilityFacet;
	private ActiveEqModFacet activeEqModFacet;
	private BioSetFacet bioSetFacet;
	private CheckFacet checkFacet;
	protected ClassFacet classFacet;
	private ClassLevelFacet classLevelFacet;
	private CompanionModFacet companionModFacet;
	private DomainFacet domainFacet;
	private ExpandedCampaignFacet expandedCampaignFacet;
	private LanguageFacet languageFacet;
	protected RaceSelectionFacet raceFacet;
	private SizeFacet sizeFacet;
	private SkillFacet skillFacet;
	private StatFacet statFacet;
	private TemplateFacet templateConsolidationFacet;
	private TemplateSelectionFacet templateFacet;
	private WeaponProfFacet weaponProfFacet;
	private Race human;

	@BeforeEach
	void setUpContext()
	{
		ChooserFactory.useRandomChooser();
		TokenRegistration.clearTokens();
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
		TokenRegistration.register(SMF);
		TokenRegistration.register(Feat.class);

		directAbilityFacet = FacetLibrary.getFacet(DirectAbilityFacet.class);
		activeEqModFacet = FacetLibrary.getFacet(ActiveEqModFacet.class);
		bioSetFacet = FacetLibrary.getFacet(BioSetFacet.class);
		checkFacet = FacetLibrary.getFacet(CheckFacet.class);
		classFacet = FacetLibrary.getFacet(ClassFacet.class);
		classLevelFacet = FacetLibrary.getFacet(ClassLevelFacet.class);
		companionModFacet = FacetLibrary.getFacet(CompanionModFacet.class);
		domainFacet = FacetLibrary.getFacet(DomainFacet.class);
		expandedCampaignFacet =
				FacetLibrary.getFacet(ExpandedCampaignFacet.class);
		languageFacet = FacetLibrary.getFacet(LanguageFacet.class);
		raceFacet = FacetLibrary.getFacet(RaceSelectionFacet.class);
		sizeFacet = FacetLibrary.getFacet(SizeFacet.class);
		skillFacet = FacetLibrary.getFacet(SkillFacet.class);
		statFacet = FacetLibrary.getFacet(StatFacet.class);
		templateFacet = FacetLibrary.getFacet(TemplateSelectionFacet.class);
		templateConsolidationFacet = FacetLibrary.getFacet(TemplateFacet.class);
		weaponProfFacet = FacetLibrary.getFacet(WeaponProfFacet.class);

		Globals.setUseGUI(false);
		Globals.emptyLists();

		final GameMode gameMode = SettingsHandler.getGameAsProperty().get();
		gameMode.clearLoadContext();
		BuildUtilities.buildUnselectedRace(Globals.getContext());

		context = Globals.getContext();
		AbstractReferenceContext ref = context.getReferenceContext();
		ref.importObject(BuildUtilities.createAlignment("None", "NONE"));

		Deity none = new Deity();
		none.setName("None");
		ref.importObject(none);

		ControlTestSupport.enableFeature(context, CControl.DOMAINFEATURE);

		FormatSupport.addNoneAsDefault(context,
			ref.getManufacturer(Deity.class));

		FormatSupport.addBasicDefaults(context);
		FormatSupport.addNoneAsDefault(context,
			context.getReferenceContext().getManufacturer(PCAlignment.class));
		SourceFileLoader.defineBuiltinVariables(context);

		str = BuildUtilities.createStat("Strength", "STR", "A");
		str.put(VariableKey.getConstant("LOADSCORE"),
			FormulaFactory.getFormulaFor("STRSCORE"));
		str.put(VariableKey.getConstant("OFFHANDLIGHTBONUS"),
			FormulaFactory.getFormulaFor(2));
		dex = BuildUtilities.createStat("Dexterity", "DEX", "B");
		con = BuildUtilities.createStat("Constitution", "CON", "C");
		intel = BuildUtilities.createStat("Intelligence", "INT", "D");
		wis = BuildUtilities.createStat("Wisdom", "WIS", "E");
		cha = BuildUtilities.createStat("Charisma", "CHA", "F");

		lg = BuildUtilities.createAlignment("Lawful Good", "LG");
		ref.importObject(lg);
		ln = BuildUtilities.createAlignment("Lawful Neutral", "LN");
		ref.importObject(ln);
		le = BuildUtilities.createAlignment("Lawful Evil", "LE");
		ref.importObject(le);
		ng = BuildUtilities.createAlignment("Neutral Good", "NG");
		ref.importObject(ng);
		tn = BuildUtilities.createAlignment("True Neutral", "TN");
		ref.importObject(tn);
		ne = BuildUtilities.createAlignment("Neutral Evil", "NE");
		ref.importObject(ne);
		cg = BuildUtilities.createAlignment("Chaotic Good", "CG");
		ref.importObject(cg);
		cn = BuildUtilities.createAlignment("Chaotic Neutral", "CN");
		ref.importObject(cn);
		ce = BuildUtilities.createAlignment("Chaotic Evil", "CE");
		ref.importObject(ce);
		ref.importObject(BuildUtilities.createAlignment("Deity's", "Deity"));

		ref.importObject(str);
		ref.importObject(dex);
		ref.importObject(con);
		ref.importObject(intel);
		ref.importObject(wis);
		ref.importObject(cha);

		fine = BuildUtilities.createSize("Fine", 0);
		diminutive = BuildUtilities.createSize("Diminutive", 1);
		tiny = BuildUtilities.createSize("Tiny", 2);
		small = BuildUtilities.createSize("Small", 3);
		medium = BuildUtilities.createSize("Medium", 4);
		medium.put(ObjectKey.IS_DEFAULT_SIZE, true);
		large = BuildUtilities.createSize("Large", 5);
		huge = BuildUtilities.createSize("Huge", 6);
		gargantuan = BuildUtilities.createSize("Gargantuan", 7);
		colossal = BuildUtilities.createSize("Colossal", 8);

		create(Language.class, "Common");
		human = create(Race.class, "Human");
		BuildUtilities.createFact(context, "ClassType", PCClass.class);
		FactDefinition<?, String> fd =
				BuildUtilities.createFact(context, "SpellType", PCClass.class);
		fd.setSelectable(true);
		context.getReferenceContext().importObject(BuildUtilities.getFeatCat());
		SourceFileLoader.createLangBonusObject(Globals.getContext());
		ChooserFactory.setDelegate(new MockUIDelegate());
	}

	@AfterAll
	static void classTearDown()
	{
		TokenRegistration.clearTokens();
	}
	
	@AfterEach
	void tearDown()
	{
		TokenRegistration.clearTokens();
		context = null;
		pc = null;
		reloadedPC = null;
		id = null;
	}

	protected void runRoundRobin(Runnable preEqualityCleanup)
	{
		runRoundRobin(preEqualityCleanup, false);
	}

	private void runRoundRobin(Runnable preEqualityCleanup, boolean dump)
	{
		runWriteRead(dump);
		if (preEqualityCleanup != null)
		{
			preEqualityCleanup.run();
		}
		checkEquality();
	}

	protected void checkEquality()
	{
		InequalityTester it = InequalityTesterInst.getInstance();
		assertTrue(AbstractStorageFacet.areEqualCache(pc.getCharID(),
			reloadedPC.getCharID(), it));
	}

	protected void runWriteRead(boolean dump)
	{
		final GameMode gameMode = SettingsHandler.getGameAsProperty().get();
		String pcgString =
				(new PCGVer2Creator(pc, gameMode, null)).createPCGString();
		if (dump)
		{
			System.err.println(pcgString);
		}
		InputStream is = new ByteArrayInputStream(pcgString.getBytes());
		PCGIOHandler ioh = new PCGIOHandler();
		ioh.read(reloadedPC, is, true);
		assertEquals(0, ioh.getErrors().size(), ioh.getErrors().toString());
		assertEquals(0, ioh.getWarnings().size(), ioh.getWarnings().toString());
	}
	
	protected void dumpPC(PlayerCharacter plchar)
	{
		final GameMode gameMode = SettingsHandler.getGameAsProperty().get();
		String pcgString =
				(new PCGVer2Creator(plchar, gameMode, null)).createPCGString();
		System.err.println(pcgString);
	}

	private void setBoilerplate()
	{
		pc.setRace(human);
		pc.setHeight(0);
		pc.setWeight(0);
		pc.setAllowDebt(false);
		HandedCompat.setCurrentHandedness(pc.getCharID(), Handed.Right);
		pc.setGender(Gender.Male);
		pc.setIgnoreCost(false);
		AgeCompat.setCurrentAge(pc.getCharID(), 0);
		pc.setXP(0);
		pc.setRegion(Region.getConstant(Constants.NONE));

		pc.setStringFor(PCStringKey.INTERESTS, "");
		pc.setStringFor(PCStringKey.MAGIC, "");
		pc.setStringFor(PCStringKey.PORTRAIT_PATH, "");
		pc.setStringFor(PCStringKey.BIRTHDAY, "");
		pc.setStringFor(PCStringKey.DESCRIPTION, "");
		pc.setStringFor(PCStringKey.CITY, "");
		pc.setStringFor(PCStringKey.PERSONALITY1, "");
		pc.setStringFor(PCStringKey.EYECOLOR, "");
		pc.setStringFor(PCStringKey.PLAYERSNAME, "");
		pc.setStringFor(PCStringKey.PHOBIAS, "");
		pc.setStringFor(PCStringKey.LOCATION, "");
		pc.setStringFor(PCStringKey.NAME, "");
		pc.setStringFor(PCStringKey.COMPANIONS, "");
		pc.setStringFor(PCStringKey.CATCHPHRASE, "");
		pc.setStringFor(PCStringKey.BIO, "");
		pc.setStringFor(PCStringKey.GMNOTES, "");
		pc.setStringFor(PCStringKey.BIRTHPLACE, "");
		pc.setStringFor(PCStringKey.ASSETS, "");
		pc.setStringFor(PCStringKey.SPEECHTENDENCY, "");
		pc.setStringFor(PCStringKey.HAIRCOLOR, "");
		pc.setStringFor(PCStringKey.PERSONALITY2, "");
		pc.setStringFor(PCStringKey.TABNAME, "");
	}
}
