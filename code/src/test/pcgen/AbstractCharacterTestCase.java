/*
 * Created on 23-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen;

import java.util.Collection;

import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.GameModeFileLoader;
import pcgen.persistence.SourceFileLoader;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

/**
 * This is an abstract TestClass designed to be able to create a PlayerCharacter
 * Object.
 *
 * @author frugal@purplewombat.co.uk
 */
@SuppressWarnings("nls")
abstract public class AbstractCharacterTestCase extends PCGenTestCase
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
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TestHelper.loadPlugins();

		Globals.setUseGUI(false);
		Globals.emptyLists();
		final GameMode gamemode = SettingsHandler.getGame();
		
		str = BuildUtilities.createStat("Strength", "STR");
		str.put(VariableKey.getConstant("LOADSCORE"),
				FormulaFactory.getFormulaFor("STRSCORE"));
		str.put(VariableKey.getConstant("OFFHANDLIGHTBONUS"),
				FormulaFactory.getFormulaFor(2));
		str.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		str.put(VariableKey.getConstant("MAXLEVELSTAT=" + str.getKeyName()),
				FormulaFactory.getFormulaFor(str.getKeyName() + "SCORE-10"));

		dex = BuildUtilities.createStat("Dexterity", "DEX");
		dex.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		dex.put(VariableKey.getConstant("MAXLEVELSTAT=" + dex.getKeyName()),
				FormulaFactory.getFormulaFor(dex.getKeyName() + "SCORE-10"));

		PCStat con = BuildUtilities.createStat("Constitution", "CON");
		con.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		con.put(VariableKey.getConstant("MAXLEVELSTAT=" + con.getKeyName()),
				FormulaFactory.getFormulaFor(con.getKeyName() + "SCORE-10"));

		intel = BuildUtilities.createStat("Intelligence", "INT");
		intel.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		intel.put(VariableKey.getConstant("MAXLEVELSTAT=" + intel.getKeyName()),
				FormulaFactory.getFormulaFor(intel.getKeyName() + "SCORE-10"));

		wis = BuildUtilities.createStat("Wisdom", "WIS");
		wis.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		wis.put(VariableKey.getConstant("MAXLEVELSTAT=" + wis.getKeyName()),
				FormulaFactory.getFormulaFor(wis.getKeyName() + "SCORE-10"));

		cha = BuildUtilities.createStat("Charisma", "CHA");
		cha.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		cha.put(VariableKey.getConstant("MAXLEVELSTAT=" + cha.getKeyName()),
				FormulaFactory.getFormulaFor(cha.getKeyName() + "SCORE-10"));

		gamemode.setBonusFeatLevels("3|3");
		SettingsHandler.setGame("3.5");

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

		GameModeFileLoader.addDefaultWieldCategories(Globals.getContext());
		
		ref.importObject(str);
		ref.importObject(dex);
		ref.importObject(con);
		ref.importObject(intel);
		ref.importObject(wis);
		ref.importObject(cha);

		ref.constructCDOMObject(Language.class, "All Language For Test");

		fine = BuildUtilities.createSize("Fine");
		diminutive = BuildUtilities.createSize("Diminutive");
		tiny = BuildUtilities.createSize("Tiny");
		small = BuildUtilities.createSize("Small");
		medium = BuildUtilities.createSize("Medium");
		medium.put(ObjectKey.IS_DEFAULT_SIZE, true);
		large = BuildUtilities.createSize("Large");
		huge = BuildUtilities.createSize("Huge");
		gargantuan = BuildUtilities.createSize("Gargantuan");
		colossal = BuildUtilities.createSize("Colossal");

		SourceFileLoader.createLangBonusObject(Globals.getContext());
		GameModeFileLoader.addDefaultUnitSet(SettingsHandler.getGame());
		SettingsHandler.getGame().selectDefaultUnitSet();
		ref.importObject(AbilityCategory.FEAT);
		additionalSetUp();
		assertTrue(ref.resolveReferences(null));

		character = new PlayerCharacter();
	}

	protected void additionalSetUp() throws Exception
	{
		//override to provide info
	}

	/**
	 * Constructs a new <code>AbstractCharacterTestCase</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public AbstractCharacterTestCase()
	{
		super();
	}

	/**
	 * Constructs a new <code>AbstractCharacterTestCase</code> with the given
	 * <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase(String)
	 */
	public AbstractCharacterTestCase(final String name)
	{
		super(name);
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		character = null;
		super.tearDown();
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
	 * @param statName The name of the stat to be set (eg DEX)
	 * @param value The value to be set (eg 18)
	 */
	public void setPCStat(final PlayerCharacter pc, final PCStat stat,
			final int value)
	{
		pc.setStat(stat,  value);
	}


	/**
	 * Checks if the character has the specified ability.
	 * 
	 * <p>
	 * If <tt>aCategory</tt> is <tt>null</tt> then all categories that have
	 * the same innate ability category will be checked.
	 * <p>
	 * If <tt>anAbilityType</tt> is <tt>ANY</tt> then all Natures will be
	 * checked for the ability.
	 * 
	 * @param aCategory
	 *            An <tt>AbilityCategory</tt> or <tt>null</tt>
	 * @param anAbilityType
	 *            A <tt>Nature</tt>.
	 * @param anAbility
	 *            The <tt>Ability</tt> to check for.
	 * 
	 * @return <tt>true</tt> if the character has the ability with the
	 *         criteria specified.
	 */
	public boolean hasAbility(PlayerCharacter pc,
		final AbilityCategory aCategory, final Nature anAbilityType,
		final Ability anAbility)
	{
		Collection<CNAbility> cnabilities = pc.getCNAbilities(aCategory, anAbilityType);
		for (CNAbility cna : cnabilities)
		{
			Ability a = cna.getAbility();
			if (a.getKeyName().equals(anAbility.getKeyName()))
			{
				return true;
			}
		}
		return false;
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

	protected void removeAbility(AbilityCategory cat, Ability a)
	{
		if (a.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			fail("addAbility takes Mult:NO Abilities");
		}
		CNAbility cna = CNAbilityFactory.getCNAbility(cat, Nature.NORMAL, a);
		character.removeAbility(new CNAbilitySelection(cna, null),
			UserSelection.getInstance(), UserSelection.getInstance());
	}

	protected Ability addMultYesAbility(AbilityCategory cat, Ability a)
	{
		return a;
	}
	
	protected CNAbility finalize(Ability a, String string,
		PlayerCharacter pc, AbilityCategory cat)
	{
		return applyAbility(pc, cat, a, string);
	}
}
