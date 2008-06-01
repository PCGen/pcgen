/**
 * 
 */
package pcgen.rules.persistence.token;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;

public class ClassWrappedToken implements CDOMCompatibilityToken<PCClassLevel>
{

	private static int wrapIndex = Integer.MIN_VALUE;

	private static final Integer ONE = Integer.valueOf(1);

	private CDOMToken<PCClass> wrappedToken;

	private int priority = wrapIndex++;

	public Class<PCClassLevel> getTokenClass()
	{
		return PCClassLevel.class;
	}

	public ClassWrappedToken(CDOMToken<PCClass> tok)
	{
		wrappedToken = tok;
	}

	public boolean parse(LoadContext context, PCClassLevel obj, String value)
			throws PersistenceLayerException
	{
		if (ONE.equals(obj.get(IntegerKey.LEVEL)))
		{
			PCClass parent = (PCClass) obj.get(ObjectKey.PARENT);
			return wrappedToken.parse(context, parent, value);
		}
		return false;
	}

	public String getTokenName()
	{
		return wrappedToken.getTokenName();
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return priority;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

}