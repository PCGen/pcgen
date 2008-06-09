package plugin.lsttokens.ability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Ability;
import pcgen.core.Description;
import pcgen.io.EntityEncoder;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * This class deals with the BENEFIT Token
 */
public class BenefitToken extends AbstractToken implements
		CDOMPrimaryToken<Ability>
{

	@Override
	public String getTokenName()
	{
		return "BENEFIT";
	}

	public boolean parse(LoadContext context, Ability ability, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			context.getObjectContext().removeList(ability, ListKey.BENEFIT);
			return true;
		}
		if (value.startsWith(Constants.LST_DOT_CLEAR_DOT))
		{
			context.getObjectContext().removePatternFromList(ability,
					ListKey.BENEFIT, value.substring(7));
			return true;
		}

		context.getObjectContext().addToList(ability, ListKey.BENEFIT,
				parseBenefit(value));
		return true;
	}

	public String[] unparse(LoadContext context, Ability ability)
	{
		Changes<Description> changes = context.getObjectContext()
				.getListChanges(ability, ListKey.BENEFIT);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		List<String> list = new ArrayList<String>();
		Collection<Description> removedItems = changes.getRemoved();
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
			for (Description d : removedItems)
			{
				list.add(Constants.LST_DOT_CLEAR_DOT + d);
			}
		}
		/*
		 * TODO .CLEAR. is not properly round-robin capable
		 */
		Collection<Description> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			for (Description d : added)
			{
				list.add(EntityEncoder.encode(d.getPCCText()));
			}
		}
		if (list.isEmpty())
		{
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Parses the BENEFIT tag into a Description object.
	 * 
	 * @param aDesc
	 *            The LST tag
	 * @return A <tt>Description</tt> object
	 */
	public Description parseBenefit(final String aDesc)
	{
		final StringTokenizer tok = new StringTokenizer(aDesc, Constants.PIPE);

		final Description desc = new Description(EntityEncoder.decode(tok
				.nextToken()));

		boolean isPre = false;
		while (tok.hasMoreTokens())
		{
			final String token = tok.nextToken();
			if (PreParserFactory.isPreReqString(token))
			{
				desc.addPrerequisite(getPrerequisite(token));
				isPre = true;
			}
			else
			{
				if (isPre)
				{
					Logging.errorPrint("Invalid " + getTokenName() + ": "
							+ aDesc);
					Logging
							.errorPrint("  PRExxx must be at the END of the Token");
					isPre = false;
				}
				desc.addVariable(token);
			}
		}

		return desc;
	}

	public Class<Ability> getTokenClass()
	{
		return Ability.class;
	}

}
