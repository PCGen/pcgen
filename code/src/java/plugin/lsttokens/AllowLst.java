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

/**
 * Processes the ALLOW token, which is the "new formula token" for Prerequisites.
 * This is designed to control ONLY situations at a user selection - it does not do
 * ongoing enforcement. For ongoing enforcement, ENABLE is used.
 */
public class AllowLst extends AbstractNonEmptyToken<CDOMObject>
		implements CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "ALLOW";
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
		NEPFormula<Boolean> formula =
				context.getValidFormula(FormatUtilities.BOOLEAN_MANAGER, value);
		obj.addToListFor(ListKey.ALLOW, formula);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<NEPFormula<Boolean>> changes =
				context.getObjectContext().getListChanges(obj, ListKey.ALLOW);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		//This is correct - NEPFormula unparses to its instructions with toString()
		return new String[]{StringUtil.join(changes.getAdded(), Constants.PIPE)};
	}
}
