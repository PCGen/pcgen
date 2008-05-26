package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with SPECIALTYKNOWN Token
 */
public class SpecialtyknownToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "SPECIALTYKNOWN";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		StringTokenizer st = new StringTokenizer(value, ",");
		List<Formula> list = new ArrayList<Formula>();
		while (st.hasMoreTokens())
		{
			list.add(FormulaFactory.getFormulaFor(st.nextToken()));
		}

		pcclass.addSpecialtyKnown(level, list);
		return true;
	}
}
