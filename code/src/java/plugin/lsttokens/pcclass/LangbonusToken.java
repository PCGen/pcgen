package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with LANGBONUS Token
 */
public class LangbonusToken extends AbstractToken implements
		CDOMPrimaryToken<PCClass>
{

	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	@Override
	public String getTokenName()
	{
		return "LANGBONUS";
	}

	public boolean parse(LoadContext context, PCClass cl, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);
		boolean foundAny = false;
		boolean foundOther = false;
		boolean firstToken = true;

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				if (!firstToken)
				{
					Logging.errorPrint("Non-sensical situation was "
							+ "encountered while parsing " + getTokenName()
							+ ": When used, .CLEAR must be the first argument");
					return false;
				}
				context.getListContext().removeAllFromList(getTokenName(), cl,
						Language.STARTING_LIST);
			}
			else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				CDOMReference<Language> lang;
				String clearText = tokText.substring(7);
				if (Constants.LST_ALL.equals(clearText))
				{
					lang = context.ref.getCDOMAllReference(LANGUAGE_CLASS);
				}
				else
				{
					lang = TokenUtilities.getTypeOrPrimitive(context,
							LANGUAGE_CLASS, clearText);
				}
				if (lang == null)
				{
					Logging
							.errorPrint("  Error was encountered while parsing "
									+ getTokenName()
									+ ": "
									+ value
									+ " had an invalid .CLEAR. reference: "
									+ clearText);
					return false;
				}
				context.getListContext().removeFromList(getTokenName(), cl,
						Language.STARTING_LIST, lang);
			}
			else
			{
				/*
				 * Note this is done one-by-one, because .CLEAR. token type
				 * needs to be able to perform the unlink. That could be
				 * changed, but the increase in complexity isn't worth it.
				 * (Changing it to a grouping object that didn't place links in
				 * the graph would also make it harder to trace the source of
				 * class skills, etc.)
				 */
				CDOMReference<Language> lang;
				if (Constants.LST_ALL.equals(tokText))
				{
					foundAny = true;
					lang = context.ref.getCDOMAllReference(LANGUAGE_CLASS);
				}
				else
				{
					foundOther = true;
					lang = TokenUtilities.getTypeOrPrimitive(context,
							LANGUAGE_CLASS, tokText);
				}
				if (lang == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
							+ getTokenName() + ": " + value
							+ " had an invalid reference: " + tokText);
					return false;
				}
				context.getListContext().addToList(getTokenName(), cl,
						Language.STARTING_LIST, lang);
			}
			firstToken = false;
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcl)
	{
		AssociatedChanges<CDOMReference<Language>> changes = context
				.getListContext().getChangesInList(getTokenName(), pcl,
						Language.STARTING_LIST);
		List<String> list = new ArrayList<String>();
		Collection<CDOMReference<Language>> removedItems = changes.getRemoved();
		if (removedItems != null && !removedItems.isEmpty())
		{
			if (changes.includesGlobalClear())
			{
				context.addWriteMessage("Non-sensical relationship in "
						+ getTokenName()
						+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			list.add(Constants.LST_DOT_CLEAR_DOT
					+ ReferenceUtilities
							.joinLstFormat(removedItems, ",.CLEAR."));
		}
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEAR);
		}
		Collection<CDOMReference<Language>> addedItems = changes.getAdded();
		if (addedItems != null && !addedItems.isEmpty())
		{
			list.add(ReferenceUtilities.joinLstFormat(addedItems,
					Constants.COMMA));
		}
		if (list.isEmpty())
		{
			// Zero indicates no add or global clear
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
