package plugin.lsttokens.pcclass;

import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ADDDOMAINS Token
 */
public class AdddomainsToken extends AbstractToken implements
		CDOMPrimaryToken<PCClass>
{

	private static final Class<Domain> DOMAIN_CLASS = Domain.class;

	@Override
	public String getTokenName()
	{
		return "ADDDOMAINS";
	}

	public boolean parse(LoadContext context, PCClass po, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('.', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.DOT);
		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			Prerequisite prereq = null; // Do not initialize, null is
			// significant!
			String domainKey;

			// Note: May contain PRExxx
			int openBracketLoc = tokString.indexOf('[');
			if (openBracketLoc == -1)
			{
				if (tokString.indexOf(']') != -1)
				{
					Logging.errorPrint("Invalid " + getTokenName()
							+ " must have '[' if it contains a PREREQ tag");
					return false;
				}
				domainKey = tokString;
			}
			else
			{
				if (tokString.indexOf(']') != tokString.length() - 1)
				{
					Logging.errorPrint("Invalid " + getTokenName()
							+ " must end with ']' if it contains a PREREQ tag");
					return false;
				}
				domainKey = tokString.substring(0, openBracketLoc);
				String prereqString = tokString.substring(openBracketLoc + 1,
						tokString.length() - 1);
				if (prereqString.length() == 0)
				{
					Logging.errorPrint(getTokenName()
							+ " cannot have empty prerequisite : " + value);
					return false;
				}
				prereq = getPrerequisite(prereqString);
			}
			AssociatedPrereqObject apo = context.getListContext().addToList(
					getTokenName(), po, PCClass.ALLOWED_DOMAINS,
					context.ref.getCDOMReference(DOMAIN_CLASS, domainKey));
			if (prereq != null)
			{
				apo.addPrerequisite(prereq);
			}
		}

		return true;
	}

	public String[] unparse(LoadContext context, PCClass po)
	{
		AssociatedChanges<CDOMReference<Domain>> changes = context
				.getListContext().getChangesInList(getTokenName(), po,
						PCClass.ALLOWED_DOMAINS);
		Collection<CDOMReference<Domain>> removedItems = changes.getRemoved();
		if (removedItems != null && !removedItems.isEmpty()
				|| changes.includesGlobalClear())
		{
			context
					.addWriteMessage(getTokenName()
							+ " does not support .CLEAR");
			return null;
		}
		MapToList<CDOMReference<Domain>, AssociatedPrereqObject> mtl = changes
				.getAddedAssociations();
		if (mtl == null || mtl.isEmpty())
		{
			return null;
		}
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (CDOMReference<Domain> domain : mtl.getKeySet())
		{
			for (AssociatedPrereqObject assoc : mtl.getListFor(domain))
			{
				if (!first)
				{
					sb.append('.');
				}
				first = false;
				sb.append(domain.getLSTformat());
				List<Prerequisite> prereqs = assoc.getPrerequisiteList();
				Prerequisite prereq;
				if (prereqs == null || prereqs.size() == 0)
				{
					prereq = null;
				}
				else if (prereqs.size() == 1)
				{
					prereq = prereqs.get(0);
				}
				else
				{
					context.addWriteMessage("Added Domain from "
							+ getTokenName() + " had more than one "
							+ "Prerequisite: " + prereqs.size());
					return null;
				}
				if (prereq != null)
				{
					sb.append('[');
					StringWriter swriter = new StringWriter();
					try
					{
						prereqWriter.write(swriter, prereq);
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage("Error writing Prerequisite: "
								+ e);
						return null;
					}
					sb.append(swriter.toString());
					sb.append(']');
				}
			}
		}
		return new String[] { sb.toString() };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
