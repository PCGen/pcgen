/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Language;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class LangautoLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	@Override
	public String getTokenName()
	{
		return "LANGAUTO";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		boolean firstToken = true;
		boolean foundAny = false;
		boolean foundOther = false;

		final StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);

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
				context.getObjectContext().removeList(obj,
						ListKey.AUTO_LANGUAGES);
			}
			else
			{
				CDOMReference<Language> ref;
				if (Constants.LST_ALL.equals(tokText))
				{
					foundAny = true;
					ref = context.ref.getCDOMAllReference(LANGUAGE_CLASS);
				}
				else
				{
					foundOther = true;
					ref = TokenUtilities.getTypeOrPrimitive(context,
							LANGUAGE_CLASS, tokText);
				}
				if (ref == null)
				{
					Logging.errorPrint("  Error was encountered while parsing "
							+ getTokenName());
					return false;
				}
				context.getObjectContext().addToList(obj,
						ListKey.AUTO_LANGUAGES, ref);
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

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<CDOMReference<Language>> changes = context.getObjectContext()
				.getListChanges(obj, ListKey.AUTO_LANGUAGES);
		Collection<CDOMReference<Language>> added = changes.getAdded();
		StringBuilder sb = new StringBuilder();
		boolean needComma = false;
		if (changes.includesGlobalClear())
		{
			sb.append(Constants.LST_DOT_CLEAR);
			needComma = true;
		}
		else if (added == null || added.isEmpty())
		{
			// Zero indicates no Token (and no global clear, so nothing to do)
			return null;
		}
		if (added != null)
		{
			for (LSTWriteable lw : added)
			{
				if (needComma)
				{
					sb.append(Constants.COMMA);
				}
				needComma = true;
				sb.append(lw.getLSTformat());
			}
		}
		return new String[] { sb.toString() };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
