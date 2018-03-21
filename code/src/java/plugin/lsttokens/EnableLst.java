package plugin.lsttokens;

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

/**
 * Processes the ENABLE token, which is the "new formula token" for Requirements. This is
 * designed to control ONLY ongoing enforcement. It does not do enforcement at user
 * selection. For enforcement at user selection, ALLOW is used.
 */
public class EnableLst extends AbstractNonEmptyToken<CDOMObject>
		implements CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "ENABLE";
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
		return new ParseResult.Fail("Not supported since it is not monitored in an ongoing fashion");
//		NEPFormula<Boolean> formula =
//				context.getValidFormula(FormatUtilities.BOOLEAN_MANAGER, value);
//		obj.addToListFor(ListKey.ENABLE, formula);
//		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<NEPFormula<Boolean>> changes =
				context.getObjectContext().getListChanges(obj, ListKey.ENABLE);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[]{StringUtil.join(changes.getAdded(), Constants.PIPE)};
	}
}
