package plugin.lsttokens;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class RequirementLst extends AbstractNonEmptyToken<CDOMObject>
		implements CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "REQUIREMENT";
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj,
		String value)
	{
		if (true)
		{
			return new ParseResult.Fail("Not supported since it is not monitored in an ongoing fashion");
		}
		NEPFormula<Boolean> formula =
				context.getValidFormula(FormatUtilities.BOOLEAN_MANAGER, value);
		obj.addToListFor(ListKey.REQUIREMENT, formula);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<NEPFormula<Boolean>> changes =
				context.getObjectContext().getListChanges(obj, ListKey.REQUIREMENT);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[]{StringUtil.join(changes.getAdded(), Constants.PIPE)};
	}
}
