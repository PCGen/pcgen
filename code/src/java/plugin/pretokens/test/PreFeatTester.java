/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.AbilityCategory;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class PreFeatTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final Equipment equipment,
		final PlayerCharacter aPC) throws PrerequisiteException
	{
		if (aPC == null)
		{
			return 0;
		}
		return passes(prereq, aPC, equipment);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(
		final Prerequisite prereq,
		final PlayerCharacter character,
		CDOMObject source)
		throws PrerequisiteException
	{
		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString(
				"PreFeat.error", prereq.toString())); //$NON-NLS-1$
		}

		int runningTotal = PrerequisiteUtilities.passesAbilityTest(
			prereq,
			character,
			number,
			AbilityCategory.FEAT.getKeyName());

		return countedTotal(prereq, runningTotal);
	}

	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		String aString = prereq.getKey();
		if ((prereq.getSubKey() != null) && !prereq.getSubKey().equals(""))
		{
			aString = aString + " ( " + prereq.getSubKey() + " )";
		}

		if (aString.startsWith("TYPE="))
		{
			// {0} {1} {2}(s) of type {3}
			return PropertyFactory.getFormattedString("PreFeat.type.toHtml",
				new Object[]{prereq.getOperator().toDisplayString(),
					prereq.getOperand(),
					AbilityCategory.FEAT.getDisplayName().toLowerCase(),
					aString.substring(5)});
		}
		// {2} {3} {1} {0}
		return PropertyFactory.getFormattedString("PreFeat.toHtml",
			new Object[]{AbilityCategory.FEAT.getDisplayName().toLowerCase(),
				aString, prereq.getOperator().toDisplayString(),
				prereq.getOperand()}); //$NON-NLS-1$
	}

	/*
	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	public String kindHandled()
	{
		return "FEAT"; //$NON-NLS-1$
	}

}
