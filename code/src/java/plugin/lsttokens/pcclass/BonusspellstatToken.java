package plugin.lsttokens.pcclass;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with BONUSSPELLSTAT Token
 */
public class BonusspellstatToken implements CDOMPrimaryToken<PCClass>
{

	private static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

	public String getTokenName()
	{
		return "BONUSSPELLSTAT";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (Constants.LST_NONE.equals(value))
		{
			context.getObjectContext().put(pcc, ObjectKey.HAS_BONUS_SPELL_STAT,
					Boolean.FALSE);
			return true;
		}
		context.getObjectContext().put(pcc, ObjectKey.HAS_BONUS_SPELL_STAT,
				Boolean.TRUE);
		/*
		 * TODO Does this consume DEFAULT in some way, so that it can set
		 * HAS_BONUS_SPELL_STAT to true, but not trigger the creation of
		 * BONUS_SPELL_STAT?
		 */
		PCStat pcs = context.ref.getAbbreviatedObject(PCSTAT_CLASS, value);
		if (pcs == null)
		{
			Logging.errorPrint("Invalid Stat Abbreviation in " + getTokenName()
					+ ": " + value);
			return false;
		}
		context.getObjectContext().put(pcc, ObjectKey.BONUS_SPELL_STAT, pcs);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Boolean bss = context.getObjectContext().getObject(pcc,
				ObjectKey.HAS_BONUS_SPELL_STAT);
		PCStat pcs = context.getObjectContext().getObject(pcc,
				ObjectKey.BONUS_SPELL_STAT);
		if (bss == null)
		{
			if (pcs != null)
			{
				context
						.addWriteMessage(getTokenName()
								+ " expected HAS_BONUS_SPELL_STAT to exist if BONUS_SPELL_STAT was defined");
			}
			return null;
		}
		if (bss.booleanValue())
		{
			if (pcs == null)
			{
				context
						.addWriteMessage(getTokenName()
								+ " expected BONUS_SPELL_STAT to exist since HAS_BONUS_SPELL_STAT was false");
				return null;
			}
			return new String[] { pcs.getLSTformat() };
		}
		else
		{
			return new String[] { "NONE" };
		}
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
