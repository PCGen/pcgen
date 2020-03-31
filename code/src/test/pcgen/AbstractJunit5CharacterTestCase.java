/*
 * Copyright 2003 (C) frugal@purplewombat.co.uk
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen;

import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.util.CControl;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.LevelInfo;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.core.SystemCollections;
import pcgen.core.system.LoadInfo;
import pcgen.persistence.GameModeFileLoader;
import pcgen.persistence.SourceFileLoader;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.util.TestHelper;

import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import util.FormatSupport;
import util.GameModeSupport;

/**
 * This is an abstract TestClass designed to be able to create a PlayerCharacter
 * Object.
 */
@SuppressWarnings("nls")
public abstract class AbstractJunit5CharacterTestCase
{
	private PlayerCharacter character = null;
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

	/**
	 * Sets up the absolute minimum amount of data to create a PlayerCharacter
	 * Object.
	 * @throws Exception PersistenceLayerException
	 */
	@BeforeEach
	public void setUp() throws Exception
	{
		TestHelper.loadPlugins();
		final GameMode gamemode = new GameMode("3.5");
		gamemode.setBonusFeatLevels("3|3");
		ControlTestSupport.enableFeature(gamemode.getModeContext(), CControl.ALIGNMENTFEATURE);
		final LevelInfo levelInfo = new LevelInfo();
		levelInfo.setLevelString("LEVEL");
		levelInfo.setMaxClassSkillString("LEVEL+3");
		levelInfo.setMaxCrossClassSkillString("(LEVEL+3)/2");
		gamemode.addLevelInfo("Normal", levelInfo);
		gamemode.addXPTableName("Normal");
		gamemode.setDefaultXPTableName("Normal");
		LoadInfo loadable =
				gamemode.getModeContext().getReferenceContext().constructNowIfNecessary(
					LoadInfo.class, gamemode.getName());
		loadable.addLoadScoreValue(0, BigDecimal.ONE);
		GameModeFileLoader.addDefaultTabInfo(gamemode);
		SystemCollections.addToGameModeList(gamemode);
		SettingsHandler.setGame("3.5");

		Globals.setUseGUI(false);
		Globals.emptyLists();

		str = BuildUtilities.createStat("Strength", "STR", "A");
		str.put(VariableKey.getConstant("LOADSCORE"),
				FormulaFactory.getFormulaFor("STRSCORE"));
		str.put(VariableKey.getConstant("OFFHANDLIGHTBONUS"),
				FormulaFactory.getFormulaFor(2));
		str.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		str.put(VariableKey.getConstant("MAXLEVELSTAT=" + str.getKeyName()),
				FormulaFactory.getFormulaFor(str.getKeyName() + "SCORE-10"));

		dex = BuildUtilities.createStat("Dexterity", "DEX", "B");
		dex.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		dex.put(VariableKey.getConstant("MAXLEVELSTAT=" + dex.getKeyName()),
				FormulaFactory.getFormulaFor(dex.getKeyName() + "SCORE-10"));

		PCStat con = BuildUtilities.createStat("Constitution", "CON", "C");
		con.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		con.put(VariableKey.getConstant("MAXLEVELSTAT=" + con.getKeyName()),
				FormulaFactory.getFormulaFor(con.getKeyName() + "SCORE-10"));

		intel = BuildUtilities.createStat("Intelligence", "INT", "D");
		intel.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		intel.put(VariableKey.getConstant("MAXLEVELSTAT=" + intel.getKeyName()),
				FormulaFactory.getFormulaFor(intel.getKeyName() + "SCORE-10"));

		wis = BuildUtilities.createStat("Wisdom", "WIS", "E");
		wis.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		wis.put(VariableKey.getConstant("MAXLEVELSTAT=" + wis.getKeyName()),
				FormulaFactory.getFormulaFor(wis.getKeyName() + "SCORE-10"));

		cha = BuildUtilities.createStat("Charisma", "CHA", "F");
		cha.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		cha.put(VariableKey.getConstant("MAXLEVELSTAT=" + cha.getKeyName()),
				FormulaFactory.getFormulaFor(cha.getKeyName() + "SCORE-10"));

		gamemode.setBonusFeatLevels("3|3");
		SettingsHandler.setGame("3.5");

		LoadContext context = Globals.getContext();
		BuildUtilities.buildUnselectedRace(context);
		AbstractReferenceContext ref = context.getReferenceContext();
		ref.importObject(BuildUtilities.createAlignment("None", "NONE"));

		FormatSupport.addBasicDefaults(context);
		FormatSupport.addNoneAsDefault(context,
			context.getReferenceContext().getManufacturer(PCAlignment.class));
		SourceFileLoader.defineBuiltinVariables(context);
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

		GameModeSupport.addDefaultWieldCategories(context);

		ref.importObject(str);
		ref.importObject(dex);
		ref.importObject(con);
		ref.importObject(intel);
		ref.importObject(wis);
		ref.importObject(cha);

		ref.constructCDOMObject(Language.class, "All Language For Test");

		BuildUtilities.createFact(context, "ClassType", PCClass.class);
		FactDefinition<?, String> fd =
				BuildUtilities.createFact(context, "SpellType", PCClass.class);
		fd.setSelectable(true);

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

		SourceFileLoader.createLangBonusObject(context);

		GameModeFileLoader.addDefaultUnitSet(SettingsHandler.getGameAsProperty().get());
		SettingsHandler.getGameAsProperty().get().selectDefaultUnitSet();
		ref.importObject(BuildUtilities.getFeatCat());
		SourceFileLoader.processFactDefinitions(context);
		additionalSetUp();
		if (!ref.resolveReferences(null))
		{
			fail("Unconstructed References");
		}
		context.resolvePostValidationTokens();
		context.resolvePostDeferredTokens();
		context.loadCampaignFacets();

		character = new PlayerCharacter();
	}

	protected void additionalSetUp() throws Exception
	{
		//override to provide info
	}

	/**
	 * Constructs a new {@code AbstractCharacterTestCase}.
	 */
	public AbstractJunit5CharacterTestCase()
	{
		super();
	}

	@AfterEach
	public void tearDown() throws Exception
	{
		character = null;
	}

	/**
	 * @return Returns the character.
	 */
	public PlayerCharacter getCharacter()
	{
		return character;
	}

	/**
	 * @param aCharacter The character to set.
	 */
	public void setCharacter(final PlayerCharacter aCharacter)
	{
		this.character = aCharacter;
	}

	/**
	 * Set the value of the stat for the character.
	 *
	 * @param pc The Player Character
	 * @param stat The name of the stat to be set (eg DEX)
	 * @param value The value to be set (eg 18)
	 */
	public void setPCStat(final PlayerCharacter pc, final PCStat stat,
			final int value)
	{
		pc.setStat(stat,  value);
	}

	public static CNAbility applyAbility(PlayerCharacter character,
		AbilityCategory cat, Ability a, String assoc)
	{
		if (a.getCDOMCategory() == null)
		{
			fail("Attempt to apply an Ability " + a.getKeyName()
				+ " that never received a Category");
		}
		CNAbility cna = CNAbilityFactory.getCNAbility(cat, Nature.NORMAL, a);
		CNAbilitySelection cnas = new CNAbilitySelection(cna, assoc);
		character.addAbility(cnas, UserSelection.getInstance(),
			UserSelection.getInstance());
		return cna;
	}

	protected void addAbility(AbilityCategory cat, Ability a)
	{
		if (a.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			fail("addAbility takes Mult:NO Abilities");
		}
		applyAbility(character, cat, a, null);
	}

}
