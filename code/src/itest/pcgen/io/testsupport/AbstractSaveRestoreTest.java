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
import java.util.Collections;

import compare.InequalityTesterInst;
import junit.framework.TestCase;
import pcgen.base.solver.Modifier;
import pcgen.base.test.InequalityTester;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.enumeration.NumericPCAttribute;
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
import pcgen.cdom.facet.model.DeityFacet;
import pcgen.cdom.facet.model.DomainFacet;
import pcgen.cdom.facet.model.ExpandedCampaignFacet;
import pcgen.cdom.facet.model.LanguageFacet;
import pcgen.cdom.facet.model.SizeFacet;
import pcgen.cdom.facet.model.SkillFacet;
import pcgen.cdom.facet.model.StatFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.cdom.inst.CodeControl;
import pcgen.cdom.util.CControl;
import pcgen.core.AbilityCategory;
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
import pcgen.output.channel.ChannelUtilities;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SourceFileLoader;
import pcgen.persistence.lst.LevelLoader;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.ModifierFactory;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.RandomChooser;
import plugin.bonustokens.Feat;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;
import util.TestURI;

public abstract class AbstractSaveRestoreTest extends TestCase
{

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
	private static final plugin.modifier.cdom.SetModifierFactory SMF =
			new plugin.modifier.cdom.SetModifierFactory();

	protected LoadContext context;
	protected PlayerCharacter pc;
	protected PlayerCharacter reloadedPC;
	protected CharID id;
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
			mode.setBonusFeatLevels("3|3");
			LevelLoader
				.parseLine(
					mode,
					"LEVEL:LEVEL	MINXP:(LEVEL*LEVEL-LEVEL)*500		CSKILLMAX:LEVEL+ClassSkillMax+3	CCSKILLMAX:(LEVEL+CrossClassSkillMax+3)/2",
					0, TestURI.getURI(), "Default");
			mode.setAlignmentText("Alignment");
		}
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		setUpBeforeClass();
		setUpContext();
	}

	
	@Override
	protected void tearDown() throws Exception
	{
		ChooserFactory.popChooserClassname();
		super.tearDown();
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
		ChooserFactory.pushChooserClassname(RandomChooser.class.getName());
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

		Globals.setUseGUI(false);
		Globals.emptyLists();

		GameMode gamemode = SettingsHandler.getGame();
		gamemode.clearLoadContext();
		BuildUtilities.buildUnselectedRace(Globals.getContext());
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

		AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
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
		ref.importObject(BuildUtilities.createAlignment("None", "NONE"));
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

		context = Globals.getContext();
		create(Language.class, "Common");
		human = create(Race.class, "Human");
		BuildUtilities.createFact(context, "ClassType", PCClass.class);
		FactDefinition<?, String> fd =
				BuildUtilities.createFact(context, "SpellType", PCClass.class);
		fd.setSelectable(true);
		context.getReferenceContext().importObject(AbilityCategory.FEAT);
		SourceFileLoader.createLangBonusObject(Globals.getContext());
		ChooserFactory.setDelegate(new MockUIDelegate());
		FormatManager<?> fmtManager = ref.getFormatManager("ALIGNMENT");
		proc(fmtManager);
		setAlignmentInputCodeControl(context, fmtManager, ref);
	}

	private void setAlignmentInputCodeControl(LoadContext context,
		FormatManager<?> fmtManager, AbstractReferenceContext ref)
	{
		CodeControl ai = ref.constructCDOMObject(CodeControl.class, "Controller");
		String channelName = ChannelUtilities.createVarName("AlignmentInput");
		context.getVariableContext().assertLegalVariableID(
			context.getActiveScope(), fmtManager,
			channelName);
		String controlName = '*' + CControl.ALIGNMENTINPUT.getName();
		ai.put(ObjectKey.getKeyFor(String.class, controlName), "AlignmentInput");
	}

	private <T> void proc(FormatManager<T> fmtManager)
	{
		Class<T> cl = fmtManager.getManagedClass();
		ModifierFactory<T> m = TokenLibrary.getModifier(cl, "SET");
		Modifier<T> defaultModifier = m.getFixedModifier(fmtManager, "NONE");
		context.getVariableContext().addDefault(cl, defaultModifier);
	}

	protected void runRoundRobin(Runnable preEqualityCleanup)
	{
		runRoundRobin(preEqualityCleanup, false);
	}

	protected void runRoundRobin(Runnable preEqualityCleanup, boolean dump)
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
		GameMode mode = SettingsHandler.getGame();
		String pcgString =
				(new PCGVer2Creator(pc, mode, null)).createPCGString();
		if (dump)
		{
			System.err.println(pcgString);
		}
		InputStream is = new ByteArrayInputStream(pcgString.getBytes());
		PCGIOHandler ioh = new PCGIOHandler();
		ioh.read(reloadedPC, is, true);
		assertEquals(ioh.getErrors().toString(), 0, ioh.getErrors().size());
		assertEquals(ioh.getWarnings().toString(), 0, ioh.getWarnings().size());
	}
	
	protected void dumpPC(PlayerCharacter plchar)
	{
		GameMode mode = SettingsHandler.getGame();
		String pcgString =
				(new PCGVer2Creator(plchar, mode, null)).createPCGString();
		System.err.println(pcgString);
	}

	private void setBoilerplate()
	{
		pc.setRace(human);
		pc.setHeight(0);
		pc.setPCAttribute(NumericPCAttribute.WEIGHT, 0);
		pc.setAllowDebt(false);
		pc.setHanded(Handed.Right);
		pc.setGender(Gender.Male);
		pc.setIgnoreCost(false);
		pc.setPCAttribute(NumericPCAttribute.AGE, 0);
		pc.setGold(BigDecimal.ZERO);
		pc.setXP(0);
		pc.setRegion(Region.getConstant(Constants.NONE));

		pc.setStringFor(PCStringKey.INTERESTS, "");
		pc.setStringFor(PCStringKey.MAGIC, "");
		pc.setStringFor(PCStringKey.PORTRAIT_PATH, "");
		pc.setStringFor(PCStringKey.BIRTHDAY, "");
		pc.setStringFor(PCStringKey.DESCRIPTION, "");
		pc.setStringFor(PCStringKey.RESIDENCE, "");
		pc.setStringFor(PCStringKey.PERSONALITY1, "");
		pc.setStringFor(PCStringKey.EYECOLOR, "");
		pc.setStringFor(PCStringKey.PLAYERSNAME, "");
		pc.setStringFor(PCStringKey.HAIRSTYLE, "");
		pc.setStringFor(PCStringKey.PHOBIAS, "");
		pc.setStringFor(PCStringKey.LOCATION, "");
		pc.setStringFor(PCStringKey.NAME, "");
		pc.setStringFor(PCStringKey.COMPANIONS, "");
		pc.setStringFor(PCStringKey.SKINCOLOR, "");
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
