package plugin.lsttokens.testsupport;

import pcgen.base.format.StringManager;
import pcgen.base.util.BasicIndirect;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.content.factset.FactSetDefinition;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;

public final class BuildUtilities
{
	private static final StringManager STR_MGR = new StringManager();

	private BuildUtilities()
	{
	}

	public static PCAlignment createAlignment(final String longName,
		final String shortName)
	{
		final PCAlignment align = new PCAlignment();
		align.setName(longName);
		align.setKeyName(shortName);
		align.put(StringKey.SORT_KEY, shortName);
		return align;
	}

	public static SizeAdjustment createSize(String name, int order)
	{
		final String abb  = name.substring(0, 1);
	
		final SizeAdjustment sa = new SizeAdjustment();
	
		sa.setName(name);
		sa.setKeyName(abb);
		sa.put(IntegerKey.SIZEORDER, order);
	
		Globals.getContext().getReferenceContext().importObject(sa);
		return sa;
	}

	/**
	 * Create a Stat for the test system with a Sortkey. This will be provided a default
	 * sort key.
	 * 
	 * @param name
	 *            The name of the stat to be created
	 * @param abb
	 *            The abbreviation of the stat to be created
	 * @return The new PCStat
	 */
	public static PCStat createStat(String name, String abb)
	{
		return createStat(name, abb, "ZZ");
	}

	/**
	 * Create a Stat for the test system with a Sortkey.
	 * 
	 * @param name
	 *            The name of the stat to be created
	 * @param abb
	 *            The abbreviation of the stat to be created
	 * @param sortKey
	 *            The sort key of the stat to be created
	 * @return The new PCStat
	 */
	public static PCStat createStat(String name, String abb, String sortKey)
	{
		PCStat stat = new PCStat();
		stat.setName(name);
		stat.setKeyName(abb);
		stat.put(StringKey.SORT_KEY, sortKey);
		stat.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		stat.put(VariableKey.getConstant("MAXLEVELSTAT=" + stat.getKeyName()),
				FormulaFactory.getFormulaFor(stat.getKeyName() + "SCORE-10"));
		return stat;
	}
	
	public static void setFact(CDOMObject cdo, String factname, String value)
	{
		FactKey<String> fk = FactKey.getConstant(factname, STR_MGR);
		cdo.put(fk, new BasicIndirect<>(STR_MGR, value));
	}
	
	/**
	 * Add a new value to a fact set.
	 * 
	 * @param cdo The object to be updated.
	 * @param factsetname The name of the fact set (must be a string set).
	 * @param value The value to be added.
	 */
	public static void addToFactSet(CDOMObject cdo, String factsetname, String value)
	{
		FactSetKey<String> fk = FactSetKey.getConstant(factsetname, STR_MGR);
		FormatManager<String> tm = new StringManager();
		Indirect<String> indirect = tm.convertIndirect(value);
		cdo.addToSetFor(fk, indirect);
	}

	public static FactDefinition<?, String> createFact(LoadContext context,
		String factname, Class<? extends Loadable> cls)
	{
		FactDefinition<?, String> fd = new FactDefinition<>();
		fd.setUsableLocation(cls);
		fd.setName("TS_" + factname);
		fd.setFactName(factname);
		fd.setFormatManager(new StringManager());
		context.getReferenceContext().importObject(fd);
		return fd;
	}

	/**
	 * Define a new FACTSET to hold a set of strings for a type of object. 
	 * @param context The context in which the data is being loaded 
	 * @param factsetname The name of the new FACTSET
	 * @param cls The object type the set will apply to.
	 * @return The full definition, already loaded into the context. 
	 */
	public static FactSetDefinition<?, String> createFactSet(LoadContext context,
		String factsetname, Class<? extends Loadable> cls)
	{
		FactSetDefinition<?, String> fd = new FactSetDefinition<>();
		fd.setUsableLocation(cls);
		fd.setName("TS_" + factsetname);
		fd.setFactSetName(factsetname);
		fd.setFormatManager(new StringManager());
		context.getReferenceContext().importObject(fd);
		return fd;
	}

	/**
	 * Build a FEAT in the given LoadContext with the given name.
	 * 
	 * @param context
	 *            The LoadContext in which the FEAT should be built
	 * @param name
	 *            The name for the FEAT
	 * @return The Ability (Feat)
	 */
	public static Ability buildFeat(LoadContext context, String name)
	{
		return buildAbility(context, BuildUtilities.getFeatCat(), name);
	}

	/**
	 * Build an Ability in the given LoadContext with the given Category and name.
	 * 
	 * @param context
	 *            The LoadContext in which the Ability should be built
	 * @param cat
	 *            The Category for the Ability
	 * @param name
	 *            The name for the Ability
	 * @return The Ability
	 */
	public static Ability buildAbility(LoadContext context, AbilityCategory cat, String name)
	{
		Ability a = cat.newInstance();
		a.setName(name);
		context.getReferenceContext().importObject(a);
		return a;
	}

	/**
	 * Build the unselected Race for unit tests.
	 * 
	 * @param context
	 *            The LoadContext in which the Race should be built
	 */
	public static void buildUnselectedRace(LoadContext context)
	{
		Race r = context.getReferenceContext().constructCDOMObject(Race.class, "Unselected");
		r.addToListFor(ListKey.GROUP, "UNSELECTED");
		r.addToListFor(ListKey.TYPE, Type.valueOf("Humanoid"));
	}
	
	/**
	 * Get the FEAT AbilityCategory
	 */
	public static AbilityCategory getFeatCat()
	{
		return AbilityCategory.FEAT;
	}
}
