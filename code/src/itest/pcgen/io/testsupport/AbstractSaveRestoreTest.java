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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;

import junit.framework.TestCase;
import pcgen.base.test.InequalityTester;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.facet.DirectAbilityFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.facet.model.ActiveEqModFacet;
import pcgen.cdom.facet.model.AlignmentFacet;
import pcgen.cdom.facet.model.BioSetFacet;
import pcgen.cdom.facet.model.CheckFacet;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.ClassLevelFacet;
import pcgen.cdom.facet.model.CompanionModFacet;
import pcgen.cdom.facet.model.DeityFacet;
import pcgen.cdom.facet.model.DomainFacet;
import pcgen.cdom.facet.model.ExpandedCampaignFacet;
import pcgen.cdom.facet.model.LanguageFacet;
import pcgen.cdom.facet.model.RaceSelectionFacet;
import pcgen.cdom.facet.model.SizeFacet;
import pcgen.cdom.facet.model.SkillFacet;
import pcgen.cdom.facet.model.StatFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.cdom.facet.model.TemplateSelectionFacet;
import pcgen.cdom.facet.model.WeaponProfFacet;
import pcgen.core.AbilityCategory;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.gui2.facade.MockUIDelegate;
import pcgen.io.PCGIOHandler;
import pcgen.io.PCGVer2Creator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.persistence.SourceFileLoader;
import pcgen.persistence.lst.LevelLoader;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.ReferenceContext;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.RandomChooser;
import plugin.lsttokens.testsupport.TokenRegistration;

import compare.InequalityTesterInst;

public abstract class AbstractSaveRestoreTest extends TestCase
{

	protected LoadContext context;
	protected PlayerCharacter pc;
	protected PlayerCharacter reloadedPC;
	protected CharID id;
	private static URI URI;
	private static boolean setup = false;

	public static void setUpBeforeClass() throws Exception
	{
		if (!setup)
		{
			setup = true;
			TokenRegistration.register(new plugin.lsttokens.level.LevelToken());
			TokenRegistration.register(new plugin.lsttokens.level.MinxpToken());
			TokenRegistration.register(new plugin.lsttokens.level.CskillmaxToken());
			TokenRegistration.register(new plugin.lsttokens.level.CcskillmaxToken());
			SettingsHandler.setGame("3.5");
			GameMode mode = SettingsHandler.getGame();
			LevelLoader
				.parseLine(
					mode,
					"LEVEL:LEVEL	MINXP:(LEVEL*LEVEL-LEVEL)*500		CSKILLMAX:LEVEL+ClassSkillMax+3	CCSKILLMAX:(LEVEL+CrossClassSkillMax+3)/2",
					0, URI, "Default");
			mode.setAlignmentText("Alignment");
		}
	}

	@Override
	protected void setUp() throws Exception
	{
		URI = new URI("file:/Test%20Case");
		super.setUp();
		setUpBeforeClass();
		setUpContext();
	}

	protected <T extends Loadable> T create(Class<T> cl, String key)
	{
		return context.ref.constructCDOMObject(cl, key);
	}

	private static final plugin.lsttokens.ability.MultToken ABILITY_MULT_TOKEN =
			new plugin.lsttokens.ability.MultToken();
	protected static final plugin.lsttokens.ChooseLst CHOOSE_TOKEN =
			new plugin.lsttokens.ChooseLst();
	private static final plugin.lsttokens.choose.LangToken CHOOSE_LANG_TOKEN =
			new plugin.lsttokens.choose.LangToken();
	private static final plugin.lsttokens.ability.VisibleToken ABILITY_VISIBLE_TOKEN =
			new plugin.lsttokens.ability.VisibleToken();
	private static final plugin.lsttokens.AutoLst AUTO_TOKEN =
			new plugin.lsttokens.AutoLst();
	protected static final plugin.lsttokens.auto.LangToken AUTO_LANG_TOKEN =
			new plugin.lsttokens.auto.LangToken();
	private static final plugin.lsttokens.equipment.ProficiencyToken EQUIP_PROFICIENCY_TOKEN =
			new plugin.lsttokens.equipment.ProficiencyToken();
	private static final plugin.lsttokens.TypeLst EQUIP_TYPE_TOKEN =
			new plugin.lsttokens.TypeLst();
	private static final plugin.primitive.language.LangBonusToken LANGBONUS_PRIM =
			new plugin.primitive.language.LangBonusToken();
	private static final plugin.qualifier.language.PCToken PC_QUAL =
			new plugin.qualifier.language.PCToken();

	protected void finishLoad()
	{
		context.commit();
		context.ref.buildDeferredObjects();
		context.ref.buildDerivedObjects();
		context.resolveDeferredTokens();
		assertTrue(context.ref.resolveReferences(null));
		context.resolvePostDeferredTokens();
		pc = new PlayerCharacter();
		setBoilerplate();
		reloadedPC =
				new PlayerCharacter(true, PersistenceManager.getInstance()
					.getLoadedCampaigns());
		id = pc.getCharID();
	}

	protected PCStat str;
	protected PCStat cha;
	protected PCStat dex;
	protected PCStat wis;
	protected PCStat con;
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

	protected DirectAbilityFacet directAbilityFacet;
	protected ActiveEqModFacet activeEqModFacet;
	protected AlignmentFacet alignmentFacet;
	protected BioSetFacet bioSetFacet;
	protected CheckFacet checkFacet;
	protected ClassFacet classFacet;
	protected ClassLevelFacet classLevelFacet;
	protected CompanionModFacet companionModFacet;
	protected DeityFacet deityFacet;
	protected DomainFacet domainFacet;
	protected ExpandedCampaignFacet expandedCampaignFacet;
	protected LanguageFacet languageFacet;
	protected RaceSelectionFacet raceFacet;
	protected SizeFacet sizeFacet;
	protected SkillFacet skillFacet;
	protected StatFacet statFacet;
	protected TemplateFacet templateConsolidationFacet;
	protected TemplateSelectionFacet templateFacet;
	protected WeaponProfFacet weaponProfFacet;
	protected Race human;

	protected void setUpContext() throws PersistenceLayerException
	{
		ChooserFactory.setInterfaceClassname(RandomChooser.class.getName());
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

		directAbilityFacet = FacetLibrary.getFacet(DirectAbilityFacet.class);
		activeEqModFacet = FacetLibrary.getFacet(ActiveEqModFacet.class);
		alignmentFacet = FacetLibrary.getFacet(AlignmentFacet.class);
		bioSetFacet = FacetLibrary.getFacet(BioSetFacet.class);
		checkFacet = FacetLibrary.getFacet(CheckFacet.class);
		classFacet = FacetLibrary.getFacet(ClassFacet.class);
		classLevelFacet = FacetLibrary.getFacet(ClassLevelFacet.class);
		companionModFacet = FacetLibrary.getFacet(CompanionModFacet.class);
		deityFacet = FacetLibrary.getFacet(DeityFacet.class);
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

		Globals.createEmptyRace();
		Globals.setUseGUI(false);
		Globals.emptyLists();

		GameMode gamemode = SettingsHandler.getGame();
		gamemode.clearLoadContext();
		str = createStat("Strength", "STR");
		str.put(VariableKey.getConstant("LOADSCORE"),
			FormulaFactory.getFormulaFor("STRSCORE"));
		str.put(VariableKey.getConstant("OFFHANDLIGHTBONUS"),
			FormulaFactory.getFormulaFor(2));
		dex = createStat("Dexterity", "DEX");
		con = createStat("Constitution", "CON");
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
		context = Globals.getContext();
		create(Language.class, "Common");
		human = create(Race.class, "Human");
		context.ref.importObject(AbilityCategory.FEAT);
		SourceFileLoader.createLangBonusObject(Globals.getContext());
		ChooserFactory.setDelegate(new MockUIDelegate());
	}

	private PCStat createStat(String name, String abb)
	{
		PCStat stat = new PCStat();
		stat.setName(name);
		stat.put(StringKey.ABB, abb);
		stat.put(FormulaKey.STAT_MOD,
			FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		stat.put(VariableKey.getConstant("MAXLEVELSTAT=" + stat.getAbb()),
			FormulaFactory.getFormulaFor(stat.getAbb() + "SCORE-10"));
		return stat;
	}

	private SizeAdjustment createSize(String name)
	{
		final String abb = name.substring(0, 1);

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

	protected void runRoundRobin()
	{
		runWriteRead();
		checkEquality();
	}

	protected void checkEquality()
	{
		InequalityTester it = InequalityTesterInst.getInstance();
		assertTrue(AbstractStorageFacet.areEqualCache(pc.getCharID(),
			reloadedPC.getCharID(), it));
	}

	protected void runWriteRead()
	{
		GameMode mode = SettingsHandler.getGame();
		String pcgString =
				(new PCGVer2Creator(pc, mode, null)).createPCGString();
		InputStream is = new ByteArrayInputStream(pcgString.getBytes());
		PCGIOHandler ioh = new PCGIOHandler();
		ioh.read(reloadedPC, is, true);
		assertEquals(ioh.getErrors().toString(), 0, ioh.getErrors().size());
		assertEquals(ioh.getWarnings().toString(), 0, ioh.getWarnings().size());
	}

	protected void setBoilerplate()
	{
		pc.setRace(human);
		pc.setHeight(0);
		pc.setWeight(0);
		pc.setAllowDebt(false);
		pc.setHanded(Handed.Right);
		pc.setGender(Gender.Male);
		pc.setIgnoreCost(false);
		pc.setAge(0);
		pc.setGold(BigDecimal.ZERO);
		pc.setXP(0);
		pc.setRegion(Region.getConstant(Constants.NONE));

		pc.setStringFor(StringKey.INTERESTS, "");
		pc.setStringFor(StringKey.MISC_MAGIC, "");
		pc.setStringFor(StringKey.PORTRAIT_PATH, "");
		pc.setStringFor(StringKey.BIRTHDAY, "");
		pc.setStringFor(StringKey.DESCRIPTION, "");
		pc.setStringFor(StringKey.RESIDENCE, "");
		pc.setStringFor(StringKey.TRAIT1, "");
		pc.setStringFor(StringKey.EYE_COLOR, "");
		pc.setStringFor(StringKey.PLAYERS_NAME, "");
		pc.setStringFor(StringKey.HAIR_STYLE, "");
		pc.setStringFor(StringKey.PHOBIAS, "");
		pc.setStringFor(StringKey.LOCATION, "");
		pc.setStringFor(StringKey.NAME, "");
		pc.setStringFor(StringKey.MISC_COMPANIONS, "");
		pc.setStringFor(StringKey.SKIN_COLOR, "");
		pc.setStringFor(StringKey.CATCH_PHRASE, "");
		pc.setStringFor(StringKey.BIO, "");
		pc.setStringFor(StringKey.MISC_GM, "");
		pc.setStringFor(StringKey.BIRTHPLACE, "");
		pc.setStringFor(StringKey.MISC_ASSETS, "");
		pc.setStringFor(StringKey.SPEECH_TENDENCY, "");
		pc.setStringFor(StringKey.HAIR_COLOR, "");
		pc.setStringFor(StringKey.TRAIT2, "");
		pc.setStringFor(StringKey.TAB_NAME, "");
	}

}
