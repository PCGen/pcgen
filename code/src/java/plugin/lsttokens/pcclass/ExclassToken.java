package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with EXCLASS Token
 */
public class ExclassToken implements CDOMPrimaryToken<PCClass>
{

	public String getTokenName()
	{
		return "EXCLASS";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		CDOMSingleRef<PCClass> cl = context.ref.getCDOMReference(PCClass.class,
				value);
		context.getObjectContext().put(pcc, ObjectKey.EX_CLASS, cl);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		CDOMSingleRef<PCClass> cl = context.getObjectContext().getObject(pcc,
				ObjectKey.EX_CLASS);
		if (cl == null)
		{
			return null;
		}
		return new String[] { cl.getLSTformat() };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
