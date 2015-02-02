package plugin.lsttokens.testsupport;

import pcgen.base.util.BasicIndirect;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import plugin.format.StringManager;

public class BuildUtilities
{
	private static final StringManager STR_MGR = new StringManager();

	public static PCAlignment createAlignment(final String longName,
		final String shortName)
	{
		final PCAlignment align = new PCAlignment();
		align.setName(longName);
		align.setKeyName(shortName);
		return align;
	}

	public static SizeAdjustment createSize(String name)
	{
		final String abb  = name.substring(0, 1);
	
		final SizeAdjustment sa = new SizeAdjustment();
	
		sa.setName(name);
		sa.setKeyName(abb);
	
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
		cdo.put(fk, new BasicIndirect<String>(STR_MGR, value));
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

}
