/*
 * Created on 23-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen;

import gmgen.pluginmgr.PluginLoader;
import pcgen.core.Constants;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.util.TestHelper;

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

	/**
	 * Sets up the absolute minimum amount of data to create a PlayerCharacter
	 * Object.
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		Globals.setUseGUI(false);
		Globals.emptyLists();
		final GameMode gamemode = SettingsHandler.getGame();

		gamemode.setAttribLong(new String[]{"Strength", "Dexterity",
			"Constitution", "Intelligence", "Wisdom", "Charisma"});
		gamemode.setAttribShort(new String[]{"STR", "DEX", "CON", "INT", "WIS",
			"CHA"});

		final PCStat str = new PCStat();
		str.setName("Strength");
		str.setAbb("STR");
		gamemode.addToStatList(str);

		final PCStat dex = new PCStat();
		dex.setName("Dexterity");
		dex.setAbb("DEX");
		gamemode.addToStatList(dex);

		final PCStat con = new PCStat();
		con.setName("Constitution");
		con.setAbb("CON");
		gamemode.addToStatList(con);

		final PCStat intel = new PCStat();
		intel.setName("Intelligence");
		intel.setAbb("INT");
		gamemode.addToStatList(intel);

		final PCStat wis = new PCStat();
		wis.setName("Wisdom");
		wis.setAbb("WIS");
		gamemode.addToStatList(wis);

		final PCStat cha = new PCStat();
		cha.setName("Charisma");
		cha.setAbb("CHA");
		gamemode.addToStatList(cha);

		TestHelper.makeSizeAdjustments();

		gamemode.addToAlignmentList(createAlignment("Lawful Good", "LG"));
		gamemode.addToAlignmentList(createAlignment("Lawful Neutral", "LN"));
		gamemode.addToAlignmentList(createAlignment("Lawful Evil", "LE"));
		gamemode.addToAlignmentList(createAlignment("Neutral Good", "NG"));
		gamemode.addToAlignmentList(createAlignment("True Neutral", "TN"));
		gamemode.addToAlignmentList(createAlignment("Neutral Evil", "NE"));
		gamemode.addToAlignmentList(createAlignment("Chaotic Good", "CG"));
		gamemode.addToAlignmentList(createAlignment("Chaotic Neutral", "CN"));
		gamemode.addToAlignmentList(createAlignment("Chaotic Evil", "CE"));
		gamemode.addToAlignmentList(createAlignment("None", "NONE"));
		gamemode.addToAlignmentList(createAlignment("Deity's", "Deity"));

		gamemode.setBonusFeatLevels("3|3");

		SettingsHandler.setGame("3.5");
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);

		character = new PlayerCharacter();
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

	public static PCAlignment createAlignment(final String longName,
		final String shortName)
	{
		final PCAlignment align = new PCAlignment();
		align.setName(longName);
		align.setKeyName(shortName);
		return align;
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
	public void setPCStat(final PlayerCharacter pc, final String statName,
		final int value)
	{
		final int index = SettingsHandler.getGame().getStatFromAbbrev(statName);

		if ((index > -1))
		{
			final PCStat stat = pc.getStatList().getStatAt(index);
			stat.setBaseScore(value);
			stat.setStatMod("floor(SCORE/2)-5");
			stat.addVariable(-9, "MAXLEVELSTAT=" + statName, statName
				+ "SCORE-10");
		}
	}
}
