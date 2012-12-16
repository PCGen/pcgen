/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
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
package tokenmodel.testsupport;

import junit.framework.TestCase;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.facet.DirectAbilityFacet;
import pcgen.cdom.facet.FacetLibrary;
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
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.SizeFacet;
import pcgen.cdom.facet.model.SkillFacet;
import pcgen.cdom.facet.model.StatFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.cdom.facet.model.WeaponProfFacet;
import pcgen.core.AbilityCategory;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SourceFileLoader;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.ReferenceContext;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.RandomChooser;
import plugin.lsttokens.AutoLst;
import plugin.lsttokens.TypeLst;
import plugin.lsttokens.ability.MultToken;
import plugin.lsttokens.ability.VisibleToken;
import plugin.lsttokens.auto.LangToken;
import plugin.lsttokens.equipment.ProficiencyToken;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.primitive.language.LangBonusToken;

public abstract class AbstractTokenModelTest extends TestCase
{

	protected LoadContext context;
	protected PlayerCharacter pc;
	protected CharID id;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		setUpContext();
	}

	protected <T extends CDOMObject> T create(Class<T> cl, String key)
	{
		return context.ref.constructCDOMObject(cl, key);
	}

	private static final MultToken ABILITY_MULT_TOKEN =
			new plugin.lsttokens.ability.MultToken();
	protected static final plugin.lsttokens.ChooseLst CHOOSE_TOKEN =
			new plugin.lsttokens.ChooseLst();
	private static final plugin.lsttokens.choose.LangToken CHOOSE_LANG_TOKEN =
			new plugin.lsttokens.choose.LangToken();
	private static final VisibleToken ABILITY_VISIBLE_TOKEN =
			new plugin.lsttokens.ability.VisibleToken();
	private static final AutoLst AUTO_TOKEN = new plugin.lsttokens.AutoLst();
	protected static final LangToken AUTO_LANG_TOKEN =
			new plugin.lsttokens.auto.LangToken();
	private static final ProficiencyToken EQUIP_PROFICIENCY_TOKEN =
			new plugin.lsttokens.equipment.ProficiencyToken();
	private static final TypeLst EQUIP_TYPE_TOKEN =
			new plugin.lsttokens.TypeLst();
	private static final LangBonusToken LANGBONUS_PRIM =
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
		id = pc.getCharID();
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
	protected RaceFacet raceFacet;
	protected SizeFacet sizeFacet;
	protected SkillFacet skillFacet;
	protected StatFacet statFacet;
	protected TemplateFacet templateFacet;
	protected WeaponProfFacet weaponProfFacet;

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
		TokenRegistration.register(getToken());

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
		raceFacet = FacetLibrary.getFacet(RaceFacet.class);
		sizeFacet = FacetLibrary.getFacet(SizeFacet.class);
		skillFacet = FacetLibrary.getFacet(SkillFacet.class);
		statFacet = FacetLibrary.getFacet(StatFacet.class);
		templateFacet = FacetLibrary.getFacet(TemplateFacet.class);
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
		context = Globals.getContext();
		create(Language.class, "Common");
		context.ref.importObject(AbilityCategory.FEAT);
		SourceFileLoader.createLangBonusObject(Globals.getContext());
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
	
	public abstract CDOMToken<?> getToken();
}
