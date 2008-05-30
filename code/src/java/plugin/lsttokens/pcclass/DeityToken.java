package plugin.lsttokens.pcclass;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Deity;
import pcgen.core.PCClass;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with DEITY Token
 */
public class DeityToken extends AbstractToken implements
		CDOMPrimaryToken<PCClass>
{

	private static final Class<Deity> DEITY_CLASS = Deity.class;

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "DEITY";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		context.getObjectContext().removeList(pcc, ListKey.DEITY);

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			CDOMReference<Deity> deity = context.ref.getCDOMReference(
					DEITY_CLASS, tokText);
			context.getObjectContext().addToList(pcc, ListKey.DEITY, deity);
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Changes<CDOMReference<Deity>> changes = context.getObjectContext()
				.getListChanges(pcc, ListKey.DEITY);
		Collection<CDOMReference<Deity>> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		return new String[] { ReferenceUtilities.joinLstFormat(added,
				Constants.PIPE) };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
