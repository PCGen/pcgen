package plugin.lsttokens.deity;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Deity;
import pcgen.core.WeaponProf;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with DEITYWEAP Token
 */
public class DeityweapToken extends AbstractToken implements CDOMPrimaryToken<Deity>
{

	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

	@Override
	public String getTokenName()
	{
		return "DEITYWEAP";
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<WeaponProf> ref;
			if (Constants.LST_ALL.equalsIgnoreCase(token)
					|| Constants.LST_ANY.equalsIgnoreCase(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(WEAPONPROF_CLASS);
			}
			else
			{
				foundOther = true;
				ref = context.ref.getCDOMReference(WEAPONPROF_CLASS, token);
			}
			context.getObjectContext().addToList(deity, ListKey.DEITYWEAPON,
					ref);
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		Changes<CDOMReference<WeaponProf>> changes = context
				.getObjectContext().getListChanges(deity, ListKey.DEITYWEAPON);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[] { ReferenceUtilities.joinLstFormat(changes
				.getAdded(), Constants.PIPE) };
	}

	public Class<Deity> getTokenClass()
	{
		return Deity.class;
	}
}
