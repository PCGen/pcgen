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
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
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

	public static PCStat createStat(String name, String abb)
	{
		PCStat stat = new PCStat();
		stat.setName(name);
		stat.setKeyName(abb);
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
		fd.setName("*" + factname);
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
		fd.setName("*" + factsetname);
		fd.setFactSetName(factsetname);
		fd.setFormatManager(new StringManager());
		context.getReferenceContext().importObject(fd);
		return fd;
	}

}
