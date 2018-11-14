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

import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.ControlTestSupport;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.facet.DirectAbilityFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.input.RaceInputFacet;
import pcgen.cdom.facet.input.TemplateInputFacet;
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
import pcgen.cdom.facet.model.WeaponProfModelFacet;
import pcgen.cdom.util.CControl;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.SourceFileLoader;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.util.chooser.ChooserFactory;
import plugin.lsttokens.AutoLst;
import plugin.lsttokens.TypeLst;
import plugin.lsttokens.ability.MultToken;
import plugin.lsttokens.ability.VisibleToken;
import plugin.lsttokens.auto.LangToken;
import plugin.lsttokens.equipment.ProficiencyToken;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.primitive.language.LangBonusToken;

import org.junit.jupiter.api.BeforeEach;
import util.FormatSupport;

public abstract class AbstractTokenModelTest
{

	protected static final MultToken ABILITY_MULT_TOKEN = new MultToken();
	protected static final plugin.lsttokens.ChooseLst CHOOSE_TOKEN =
			new plugin.lsttokens.ChooseLst();
	protected static final plugin.lsttokens.choose.LangToken CHOOSE_LANG_TOKEN =
			new plugin.lsttokens.choose.LangToken();
	private static final VisibleToken ABILITY_VISIBLE_TOKEN = new VisibleToken();
	private static final AutoLst AUTO_TOKEN = new plugin.lsttokens.AutoLst();
	protected static final LangToken AUTO_LANG_TOKEN = new LangToken();
	private static final ProficiencyToken EQUIP_PROFICIENCY_TOKEN =
			new ProficiencyToken();
	private static final TypeLst EQUIP_TYPE_TOKEN = new TypeLst();
	private static final LangBonusToken LANGBONUS_PRIM = new LangBonusToken();
	private static final plugin.qualifier.language.PCToken PC_QUAL =
			new plugin.qualifier.language.PCToken();
	private static final plugin.modifier.cdom.SetModifierFactory SMF =
			new plugin.modifier.cdom.SetModifierFactory();

	protected LoadContext context;
	protected PlayerCharacter pc;
	protected CharID id;

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
	protected BioSetFacet bioSetFacet;
	protected CheckFacet checkFacet;
	protected ClassFacet classFacet;
	protected ClassLevelFacet classLevelFacet;
	protected CompanionModFacet companionModFacet;
	protected DomainFacet domainFacet;
	protected ExpandedCampaignFacet expandedCampaignFacet;
	protected LanguageFacet languageFacet;
	protected RaceInputFacet raceFacet;
	protected SizeFacet sizeFacet;
	protected SkillFacet skillFacet;
	protected StatFacet statFacet;
	protected TemplateFacet templateConsolidationFacet;
	protected TemplateInputFacet templateInputFacet;
	protected WeaponProfModelFacet weaponProfModelFacet;

	@BeforeEach
	protected void setUp() throws Exception
	{
		setUpContext();
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
		id = pc.getCharID();
	}

	protected void setUpContext()
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
		TokenRegistration.register(getToken());
		TokenRegistration.register(plugin.bonustokens.Feat.class);

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
		raceFacet = FacetLibrary.getFacet(RaceInputFacet.class);
		sizeFacet = FacetLibrary.getFacet(SizeFacet.class);
		skillFacet = FacetLibrary.getFacet(SkillFacet.class);
		statFacet = FacetLibrary.getFacet(StatFacet.class);
		templateInputFacet = FacetLibrary.getFacet(TemplateInputFacet.class);
		templateConsolidationFacet = FacetLibrary.getFacet(TemplateFacet.class);
		weaponProfModelFacet = FacetLibrary.getFacet(WeaponProfModelFacet.class);

		Globals.setUseGUI(false);
		Globals.emptyLists();
		final GameMode gameMode = SettingsHandler.getGameAsProperty().get();
		gameMode.clearLoadContext();

		context = Globals.getContext();
		AbstractReferenceContext ref = context.getReferenceContext();
		ControlTestSupport.enableFeature(context, CControl.ALIGNMENTFEATURE);

		BuildUtilities.buildUnselectedRace(context);
		ref.importObject(BuildUtilities.createAlignment("None", "NONE"));
		
		FormatSupport.addNoneAsDefault(context,
			ref.getManufacturer(PCAlignment.class));
		FormatSupport.addBasicDefaults(context);
		SourceFileLoader.defineBuiltinVariables(context);

		str = BuildUtilities.createStat("Strength", "STR", "A");
		str.put(VariableKey.getConstant("LOADSCORE"),
			FormulaFactory.getFormulaFor("STRSCORE"));
		str.put(VariableKey.getConstant("OFFHANDLIGHTBONUS"),
			FormulaFactory.getFormulaFor(2));
		dex = BuildUtilities.createStat("Dexterity", "DEX", "B");
		PCStat con = BuildUtilities.createStat("Constitution", "CON", "C");
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

		gameMode.setBonusFeatLevels("3|3");

		SettingsHandler.setGame("3.5");

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
		BuildUtilities.createFact(context, "ClassType", PCClass.class);
		FactDefinition<?, String> fd =
				BuildUtilities.createFact(context, "SpellType", PCClass.class);
		fd.setSelectable(true);
		context.getReferenceContext().importObject(BuildUtilities.getFeatCat());
		SourceFileLoader.createLangBonusObject(Globals.getContext());
	}

	public abstract CDOMToken<?> getToken();

	protected Object getAssoc()
	{
		return null;
	}
}
