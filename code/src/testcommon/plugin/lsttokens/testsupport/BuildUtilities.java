package plugin.lsttokens.testsupport;

import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.SizeAdjustment;

public class BuildUtilities
{

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

}
