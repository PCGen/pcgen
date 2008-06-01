package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.KnownSpellIdentifier;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.spell.Spell;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with KNOWNSPELLS Token
 */
public class KnownspellsToken extends AbstractToken implements
		CDOMPrimaryToken<PCClass>
{

	private static final Class<Spell> SPELL_CLASS = Spell.class;

	@Override
	public String getTokenName()
	{
		return "KNOWNSPELLS";
	}

	public boolean parse(LoadContext context, PCClass po, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
		boolean firstToken = true;

		while (pipeTok.hasMoreTokens())
		{
			String totalFilter = pipeTok.nextToken();
			if (Constants.LST_DOT_CLEARALL.equals(totalFilter))
			{
				if (!firstToken)
				{
					Logging
							.errorPrint("Non-sensical situation was "
									+ "encountered while parsing "
									+ getTokenName()
									+ ": When used, .CLEARALL must be the first argument");
					return false;
				}
				context.getObjectContext().removeList(po, ListKey.KNOWN_SPELLS);
				continue;
			}
			if (hasIllegalSeparator(',', totalFilter))
			{
				return false;
			}

			StringTokenizer commaTok = new StringTokenizer(totalFilter,
					Constants.COMMA);

			/*
			 * This is a rather interesting situation - this takes items that
			 * are ALLOWED and converts them to GRANTS. Therefore, this must be
			 * done as a post-manufacturing run on the Graph.
			 * 
			 * As there is no guarantee when the factory is added that the list
			 * is complete, this resolution of known MUST be performed as a
			 * query against the PC, not stored in the graph as Grants edges.
			 */

			// must satisfy all elements in a comma delimited list
			Integer levelLim = null;
			CDOMReference<Spell> sp = null;
			while (commaTok.hasMoreTokens())
			{
				String filterString = commaTok.nextToken();

				if (filterString.startsWith("LEVEL="))
				{
					if (levelLim != null)
					{
						Logging
								.errorPrint("Cannot have more than one Level limit in "
										+ getTokenName() + ": " + value);
						return false;
					}
					// if the argument starts with LEVEL=, compare the level to
					// the desired spellLevel
					try
					{
						levelLim = Integer.valueOf(filterString.substring(6));
					}
					catch (NumberFormatException e)
					{
						Logging.errorPrint("Invalid Number in "
								+ getTokenName() + ": " + value);
						Logging.errorPrint("  Level must be an integer");
						return false;
					}
				}
				else
				{
					if (sp != null)
					{
						Logging
								.errorPrint("Cannot have more than one Type/Spell limit in "
										+ getTokenName() + ": " + value);
						return false;
					}
					sp = TokenUtilities.getTypeOrPrimitive(context,
							SPELL_CLASS, filterString);
					if (sp == null)
					{
						Logging.errorPrint("  encountered Invalid limit in "
								+ getTokenName() + ": " + value);
						return false;
					}
				}
				firstToken = false;
			}
			if (sp == null)
			{
				/*
				 * There is no need to check for an invalid construction here
				 * (meaning levelLim is null as well) as that was implicitly
				 * checked by ensuring || did not occur.
				 */
				sp = context.ref.getCDOMAllReference(SPELL_CLASS);
			}
			KnownSpellIdentifier ksi = new KnownSpellIdentifier(sp, levelLim);
			context.getObjectContext().addToList(po, ListKey.KNOWN_SPELLS, ksi);
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClass po)
	{
		Changes<KnownSpellIdentifier> changes = context.getObjectContext()
				.getListChanges(po, ListKey.KNOWN_SPELLS);
		List<String> list = new ArrayList<String>();
		if (changes.includesGlobalClear())
		{
			list.add(Constants.LST_DOT_CLEARALL);
		}
		Collection<KnownSpellIdentifier> removedItems = changes.getRemoved();
		if (removedItems != null && !removedItems.isEmpty())
		{
			context.addWriteMessage(getTokenName()
					+ " does not support .CLEAR.");
			return null;
		}
		Collection<KnownSpellIdentifier> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			Map<CDOMReference<?>, Integer> map = new TreeMap<CDOMReference<?>, Integer>(
					TokenUtilities.REFERENCE_SORTER);
			for (LSTWriteable lstw : added)
			{
				KnownSpellIdentifier ksi = (KnownSpellIdentifier) lstw;
				CDOMReference<Spell> ref = ksi.getLimit();
				Integer i = ksi.getSpellLevel();
				map.put(ref, i);
			}
			for (Entry<CDOMReference<?>, Integer> me : map.entrySet())
			{
				StringBuilder sb = new StringBuilder();
				boolean needComma = false;
				CDOMReference<?> ref = me.getKey();
				String refString = ref.getLSTformat();
				if (!Constants.ALLREF_LST.equals(refString))
				{
					sb.append(refString);
					needComma = true;
				}
				Integer i = me.getValue();
				if (i != null)
				{
					if (needComma)
					{
						sb.append(',');
					}
					sb.append("LEVEL=").append(i);
				}
				list.add(sb.toString());
			}
		}
		if (list.isEmpty())
		{
			return null;
		}
		return new String[] { StringUtil.join(list, Constants.PIPE) };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
