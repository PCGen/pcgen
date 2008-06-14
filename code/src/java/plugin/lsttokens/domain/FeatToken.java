package plugin.lsttokens.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CategorizedCDOMReference;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Domain;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deal with FEAT token
 */
public class FeatToken extends AbstractToken implements
		CDOMPrimaryToken<Domain>
{
	public static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	public boolean parse(LoadContext context, Domain domain, String value)
	{
		return parseFeat(context, domain, value);
	}

	public boolean parseFeat(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		boolean first = true;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				if (!first)
				{
					Logging.errorPrint("  Non-sensical " + getTokenName()
							+ ": .CLEAR was not the first list item: " + value);
					return false;
				}
				context.getObjectContext().removeList(obj, ListKey.FEAT);
			}
			else
			{
				CDOMReference<Ability> ability = TokenUtilities
						.getTypeOrPrimitive(context, ABILITY_CLASS,
								AbilityCategory.FEAT, token);
				context.obj.addToList(obj, ListKey.FEAT, ability);
			}
			first = false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Domain domain)
	{
		Changes<CDOMReference<Ability>> changes = context.obj.getListChanges(
				domain, ListKey.FEAT);
		List<String> list = new ArrayList<String>();
		Collection<CDOMReference<Ability>> added = changes.getAdded();
		Collection<CDOMReference<Ability>> removedItems = changes.getRemoved();
		if (changes.includesGlobalClear())
		{
			if (removedItems != null && !removedItems.isEmpty())
			{
				context.addWriteMessage("Non-sensical relationship in "
						+ getTokenName()
						+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			list.add(Constants.LST_DOT_CLEAR);
		}
		else if (removedItems != null && !removedItems.isEmpty())
		{
			context.addWriteMessage(getTokenName() + " does not support "
					+ Constants.LST_DOT_CLEAR_DOT);
			return null;
		}
		if (added != null && !added.isEmpty())
		{
			Set<String> set = new TreeSet<String>();
			for (CDOMReference<Ability> ab : added)
			{
				if (!AbilityCategory.FEAT
						.equals(((CategorizedCDOMReference<Ability>) ab)
								.getCDOMCategory()))
				{
					context.addWriteMessage("Abilities awarded by "
							+ getTokenName() + " must be of CATEGORY FEAT");
					return null;
				}
				set.add(ab.getLSTformat());
			}
			list.addAll(set);
		}
		return new String[] { StringUtil.join(list, Constants.PIPE) };
	}

	public Class<Domain> getTokenClass()
	{
		return Domain.class;
	}
}
