package plugin.lsttokens.deity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.list.DomainList;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with DOMAINS Token
 */
public class DomainsToken extends AbstractToken implements CDOMPrimaryToken<Deity>
{

	private static final Class<Domain> DOMAIN_CLASS = Domain.class;

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "DOMAINS";
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
		StringTokenizer commaTok = new StringTokenizer(pipeTok.nextToken(),
				Constants.COMMA);
		CDOMReference<DomainList> dl = Deity.DOMAINLIST;
		ArrayList<AssociatedPrereqObject> proList = new ArrayList<AssociatedPrereqObject>();

		boolean first = true;
		boolean foundAll = false;
		boolean foundOther = false;

		while (commaTok.hasMoreTokens())
		{
			String tokString = commaTok.nextToken();
			if (tokString.startsWith("PRE") || tokString.startsWith("!PRE"))
			{
				Logging.errorPrint("Invalid " + getTokenName()
						+ ": PRExxx was comma delimited : " + value);
				return false;
			}
			if (Constants.LST_DOT_CLEAR.equals(tokString))
			{
				if (!first)
				{
					Logging.errorPrint("  Non-sensical " + getTokenName()
							+ ": .CLEAR was not the first list item: " + value);
					return false;
				}
				context.getListContext().removeAllFromList(getTokenName(),
						deity, dl);
			}
			else if (tokString.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				CDOMReference<Domain> ref;
				String clearText = tokString.substring(7);
				if (Constants.LST_ALL.equals(clearText)
						|| Constants.LST_ANY.equals(clearText))
				{
					ref = context.ref.getCDOMAllReference(DOMAIN_CLASS);
				}
				else
				{
					ref = context.ref.getCDOMReference(DOMAIN_CLASS, clearText);
				}
				context.getListContext().removeFromList(getTokenName(), deity,
						dl, ref);
			}
			else if (Constants.LST_ALL.equals(tokString)
					|| Constants.LST_ANY.equals(tokString))
			{
				CDOMGroupRef<Domain> ref = context.ref
						.getCDOMAllReference(DOMAIN_CLASS);
				proList.add(context.getListContext().addToList(getTokenName(),
						deity, dl, ref));
				foundAll = true;
			}
			else
			{
				CDOMSingleRef<Domain> ref = context.ref.getCDOMReference(
						DOMAIN_CLASS, tokString);
				proList.add(context.getListContext().addToList(getTokenName(),
						deity, dl, ref));
				foundOther = true;
			}
			first = false;
		}

		if (foundAll && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ALL and a specific reference: " + value);
			return false;
		}

		while (pipeTok.hasMoreTokens())
		{
			String tokString = pipeTok.nextToken();
			Prerequisite prereq = getPrerequisite(tokString);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put items after the "
						+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			for (AssociatedPrereqObject ao : proList)
			{
				ao.addAllPrerequisites(prereq);
			}
		}

		return true;
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		CDOMReference<DomainList> dl = Deity.DOMAINLIST;
		AssociatedChanges<CDOMReference<Domain>> changes = context
				.getListContext().getChangesInList(getTokenName(), deity, dl);
		List<String> list = new ArrayList<String>();
		Collection<CDOMReference<Domain>> removedItems = changes.getRemoved();
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
			list.add(Constants.LST_DOT_CLEAR_DOT
					+ ReferenceUtilities.joinLstFormat(removedItems,
							",.CLEAR."));
		}
		MapToList<CDOMReference<Domain>, AssociatedPrereqObject> mtl = changes
				.getAddedAssociations();
		if (mtl != null && !mtl.isEmpty())
		{
			MapToList<Set<Prerequisite>, LSTWriteable> m = new HashMapToList<Set<Prerequisite>, LSTWriteable>();
			for (CDOMReference<Domain> ab : mtl.getKeySet())
			{
				for (AssociatedPrereqObject assoc : mtl.getListFor(ab))
				{
					m.addToListFor(new HashSet<Prerequisite>(assoc
							.getPrerequisiteList()), ab);
				}
			}
			Set<String> set = new TreeSet<String>();
			for (Set<Prerequisite> prereqs : m.getKeySet())
			{
				Set<LSTWriteable> domainSet = new TreeSet<LSTWriteable>(
						TokenUtilities.WRITEABLE_SORTER);
				domainSet.addAll(m.getListFor(prereqs));
				StringBuilder sb = new StringBuilder(ReferenceUtilities
						.joinLstFormat(domainSet, Constants.COMMA));
				if (prereqs != null && !prereqs.isEmpty())
				{
					sb.append(Constants.PIPE);
					sb.append(getPrerequisiteString(context, prereqs));
				}
				set.add(sb.toString());
			}
			list.addAll(set);
		}
		if (list.isEmpty())
		{
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<Deity> getTokenClass()
	{
		return Deity.class;
	}
}
