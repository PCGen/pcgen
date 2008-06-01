package plugin.lsttokens.pcclass;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.AttackCycle;
import pcgen.core.PCClass;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.AttackType;

/**
 * Class deals with ATTACKCYCLE Token
 */
public class AttackcycleToken extends AbstractToken implements
		CDOMPrimaryToken<PCClass>
{

	@Override
	public String getTokenName()
	{
		return "ATTACKCYCLE";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
		if (aTok.countTokens() % 2 == 1)
		{
			Logging.errorPrint(getTokenName()
					+ " must have an even number of argumetns.");
			return false;
		}

		while (aTok.hasMoreTokens())
		{
			AttackType at = AttackType.getInstance(aTok.nextToken());
			if (AttackType.GRAPPLE.equals(at))
			{
				Logging.errorPrint("Error: Cannot Set Attack Cycle "
						+ "for GRAPPLE Attack Type");
				return false;
			}
			String cycle = aTok.nextToken();
			try
			{
				Integer i = Integer.parseInt(cycle);
				context.getObjectContext().addToList(pcc, ListKey.ATTACK_CYCLE,
						new AttackCycle(at, i));
				/*
				 * This is a bit of a hack - it is designed to account for the
				 * fact that the BAB tag in ATTACKCYCLE actually impacts both
				 * ATTACK.MELEE and ATTACK.GRAPPLE ... therefore, one method of
				 * handing this (which is done here) is to actually allow the
				 * pcgen.core code to keep the 4 attack type view (MELEE,
				 * RANGED, UNARMED, GRAPPLE) by simply loading the attackCycle
				 * for MELEE into GRAPPLE. This is done in the hope that this is
				 * a more flexible solution for potential future requirements
				 * for other attack types (rather than treating GRAPPLE as a
				 * special case throughout the core code) - thpr Nov 1, 2006
				 */
				if (at.equals(AttackType.MELEE))
				{
					context.getObjectContext().addToList(pcc,
							ListKey.ATTACK_CYCLE,
							new AttackCycle(AttackType.GRAPPLE, i));
				}
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Invalid " + getTokenName() + ": " + value
						+ " Cycle " + cycle + " must be an integer.");
				return false;
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Changes<AttackCycle> changes = context.getObjectContext()
				.getListChanges(pcc, ListKey.ATTACK_CYCLE);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		Integer grappleValue = null;
		Integer meleeValue = null;
		for (AttackCycle ac : changes.getAdded())
		{
			AttackType attackType = ac.getAttackType();
			if (attackType.equals(AttackType.GRAPPLE))
			{
				grappleValue = ac.getValue();
			}
			else
			{
				if (attackType.equals(AttackType.MELEE))
				{
					meleeValue = ac.getValue();
				}
				set.add(new StringBuilder().append(attackType.getIdentifier())
						.append(Constants.PIPE).append(ac.getValue())
						.toString());
			}
		}
		if (grappleValue != null)
		{
			// Validate same as MELEE
			if (!grappleValue.equals(meleeValue))
			{
				context.addWriteMessage("Grapple Attack Cycle (" + grappleValue
						+ ") MUST be equal to " + "Melee Attack Cycle ("
						+ meleeValue + ") because it is not stored");
				return null;
			}
		}
		return new String[] { StringUtil.join(set, Constants.PIPE) };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
