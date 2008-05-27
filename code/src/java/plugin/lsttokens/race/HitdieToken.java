package plugin.lsttokens.race;

import pcgen.base.formula.AddingFormula;
import pcgen.base.formula.DividingFormula;
import pcgen.base.formula.MultiplyingFormula;
import pcgen.base.formula.SubtractingFormula;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.AbstractHitDieModifier;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.Modifier;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.modifier.ContextModifier;
import pcgen.cdom.modifier.HitDieFormula;
import pcgen.cdom.modifier.HitDieLock;
import pcgen.cdom.modifier.HitDieStep;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with HITDIE Token
 */
public class HitdieToken extends AbstractToken implements
		CDOMPrimaryToken<Race>
{

	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;

	@Override
	public String getTokenName()
	{
		return "HITDIE";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		try
		{
			String lock = value;
			int pipeLoc = lock.indexOf(Constants.PIPE);
			if (pipeLoc != lock.lastIndexOf(Constants.PIPE))
			{
				Logging.errorPrint(getTokenName() + " has more than one pipe, "
						+ "is not of format: <int>[|<prereq>]");
				return false;
			}
			// Do not initialize, null is significant
			CDOMReference<PCClass> owner = null;
			if (pipeLoc != -1)
			{
				// Has a limitation
				String lockPre = lock.substring(pipeLoc + 1);
				if (lockPre.startsWith("CLASS.TYPE="))
				{
					String substring = lock.substring(pipeLoc + 12);
					if (substring.length() == 0)
					{
						Logging
								.errorPrint("Cannot have Empty Type Limitation in "
										+ getTokenName() + ": " + value);
						return false;
					}
					if (hasIllegalSeparator('.', substring))
					{
						return false;
					}
					owner = context.ref.getCDOMTypeReference(PCCLASS_CLASS,
							substring.split("\\."));
				}
				else if (lockPre.startsWith("CLASS="))
				{
					String substring = lock.substring(pipeLoc + 7);
					if (substring.length() == 0)
					{
						Logging
								.errorPrint("Cannot have Empty Class Limitation in "
										+ getTokenName() + ": " + value);
						return false;
					}
					owner = context.ref.getCDOMReference(PCCLASS_CLASS,
							substring);
				}
				else
				{
					Logging.errorPrint("Invalid Limitation in HITDIE: "
							+ lockPre);
					return false;
				}
				lock = lock.substring(0, pipeLoc);
			}

			AbstractHitDieModifier hdm;
			if (lock.startsWith("%/"))
			{
				// HITDIE:%/num --- divides the classes hit die by num.
				int denom = Integer.parseInt(lock.substring(2));
				if (denom <= 0)
				{
					Logging.errorPrint(getTokenName()
							+ " was expecting a Positive Integer "
							+ "for dividing Lock, was : " + lock.substring(2));
					return false;
				}
				hdm = new HitDieFormula(new DividingFormula(denom));
			}
			else if (lock.startsWith("%*"))
			{
				// HITDIE:%*num --- multiplies the classes hit die by num.
				int mult = Integer.parseInt(lock.substring(2));
				if (mult <= 0)
				{
					Logging.errorPrint(getTokenName()
							+ " was expecting a Positive "
							+ "Integer for multiplying Lock, was : "
							+ lock.substring(2));
					return false;
				}
				hdm = new HitDieFormula(new MultiplyingFormula(mult));
			}
			else if (lock.startsWith("%+"))
			{
				// possibly redundant with BONUS:HD MAX|num
				// HITDIE:%+num --- adds num to the classes hit die.
				int add = Integer.parseInt(lock.substring(2));
				if (add <= 0)
				{
					Logging.errorPrint(getTokenName()
							+ " was expecting a Positive "
							+ "Integer for adding Lock, was : "
							+ lock.substring(2));
					return false;
				}
				hdm = new HitDieFormula(new AddingFormula(add));
			}
			else if (lock.startsWith("%-"))
			{
				// HITDIE:%-num --- subtracts num from the classes hit die.
				// possibly redundant with BONUS:HD MAX|num if that will
				// take negative numbers.
				int sub = Integer.parseInt(lock.substring(2));
				if (sub <= 0)
				{
					Logging.errorPrint(getTokenName()
							+ " was expecting a Positive "
							+ "Integer for subtracting Lock, was : "
							+ lock.substring(2));
					return false;
				}
				hdm = new HitDieFormula(new SubtractingFormula(sub));
			}
			else if (lock.startsWith("%up"))
			{
				// HITDIE:%upnum --- moves the hit die num steps up the die size
				// list d4,d6,d8,d10,d12. Stops at d12.

				int steps = Integer.parseInt(lock.substring(3));
				if (steps <= 0)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
							+ getTokenName() + " up (must be positive)");
					return false;
				}
				if (steps >= 5)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
							+ getTokenName() + " up (too large)");
					return false;
				}

				hdm = new HitDieStep(steps, new HitDie(12));
			}
			else if (lock.startsWith("%Hup"))
			{
				// HITDIE:%upnum --- moves the hit die num steps up the die size
				// list d4,d6,d8,d10,d12. Stops at d12.

				int steps = Integer.parseInt(lock.substring(4));
				if (steps <= 0)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
							+ getTokenName());
					return false;
				}
				hdm = new HitDieStep(steps, null);
			}
			else if (lock.startsWith("%down"))
			{
				// HITDIE:%downnum --- moves the hit die num steps down the die
				// size
				// list d4,d6,d8,d10,d12. Stops at d4.

				int steps = Integer.parseInt(lock.substring(5));
				if (steps <= 0)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
							+ getTokenName() + " down (must be positive)");
					return false;
				}
				if (steps >= 5)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
							+ getTokenName() + " down (too large)");
					return false;
				}

				hdm = new HitDieStep(-steps, new HitDie(4));
			}
			else if (lock.startsWith("%Hdown"))
			{
				// HITDIE:%downnum --- moves the hit die num steps down the die
				// size
				// list. No limit.
				int steps = Integer.parseInt(lock.substring(6));
				if (steps <= 0)
				{
					Logging.errorPrint("Invalid Step Count: " + steps + " in "
							+ getTokenName());
					return false;
				}
				hdm = new HitDieStep(-steps, null);
			}
			else
			{
				int i = Integer.parseInt(lock);
				if (i <= 0)
				{
					Logging.errorPrint("Invalid HitDie: " + i + " in "
							+ getTokenName());
					return false;
				}
				// HITDIE:num --- sets the hit die to num regardless of class.
				hdm = new HitDieLock(new HitDie(i));
			}

			Modifier<HitDie> mod = owner == null ? hdm
					: new ContextModifier<HitDie, PCClass>(hdm, owner);
			context.getObjectContext().put(race, ObjectKey.HITDIE, mod);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid Number in " + getTokenName() + ": "
					+ nfe.getLocalizedMessage());
			Logging.errorPrint("  Must be an Integer");
			return false;
		}
	}

	public String[] unparse(LoadContext context, Race pcl)
	{
		Modifier<HitDie> hdcf = context.getObjectContext().getObject(pcl,
				ObjectKey.HITDIE);
		if (hdcf == null)
		{
			return null;
		}
		return new String[] { hdcf.getLSTformat() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}
