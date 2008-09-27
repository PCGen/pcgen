package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with LEVELSPERFEAT Token
 */
public class LevelsperfeatToken implements CDOMPrimaryToken<PCClass>
{

	public String getTokenName()
	{
		return "LEVELSPERFEAT";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		final StringTokenizer token = new StringTokenizer(value, "|");
		if (token.countTokens() < 1 || token.countTokens() > 2)
		{
			Logging.log(Logging.LST_ERROR, getTokenName() + " must be of the form: "
				+ getTokenName() + ":<int> or " + getTokenName()
				+ ":<int>|LEVELTYPE=<string>" + " Got " + getTokenName() + ":"
				+ value);
			return false;
		}

		String numLevels = token.nextToken();
		try
		{
			Integer in = Integer.valueOf(numLevels);
			if (in.intValue() < 0)
			{
				Logging.log(Logging.LST_ERROR, getTokenName() + " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(pcc, IntegerKey.LEVELS_PER_FEAT, in);
		}
		catch (NumberFormatException nfe)
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int> or " + getTokenName() + ":<int>|LEVELTYPE=<string>");
			return false;
		}
		
		if (token.hasMoreTokens())
		{
			String levelTypeTag = token.nextToken();
			final StringTokenizer levelTypeToken = new StringTokenizer(levelTypeTag, "=");
			if (levelTypeToken.countTokens() != 2)
			{
				Logging.log(Logging.LST_ERROR, getTokenName() + " must be of the form: "
					+ getTokenName() + ":<int> or " + getTokenName()
					+ ":<int>|LEVELTYPE=<string>" + " Got " + getTokenName() + ":"
					+ value);
				return false;
			}
			String tag = levelTypeToken.nextToken();
			if (!"LEVELTYPE".equals(tag))
			{
				Logging.log(Logging.LST_ERROR, getTokenName() + " must be of the form: "
					+ getTokenName() + ":<int> or " + getTokenName()
					+ ":<int>|LEVELTYPE=<string>" + " Got " + getTokenName() + ":"
					+ value);
				return false;
			}
			String levelType = levelTypeToken.nextToken();
			context.getObjectContext().put(pcc, StringKey.LEVEL_TYPE, levelType);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see pcgen.rules.persistence.token.CDOMPrimaryToken#unparse(pcgen.rules.context.LoadContext, java.lang.Object)
	 */
	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Integer lpf = context.getObjectContext().getInteger(pcc,
				IntegerKey.LEVELS_PER_FEAT);
		if (lpf == null)
		{
			return null;
		}
		if (lpf.intValue() < 0)
		{
			context
					.addWriteMessage(getTokenName()
							+ " must be an integer >= 0");
			return null;
		}
		StringBuffer result = new StringBuffer();
		result.append(lpf);
		String levelType =  context.getObjectContext().getString(pcc,
			StringKey.LEVEL_TYPE);
		if (levelType != null && levelType.length() > 0)
		{
			result.append("|LEVELTYPE=");
			result.append(levelType);
		}
		return new String[] { result.toString() };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
