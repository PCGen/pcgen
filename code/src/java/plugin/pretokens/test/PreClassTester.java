/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import pcgen.core.Equipment;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreClassTester extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final Equipment equipment, PlayerCharacter aPC)
	{
		Logging.errorPrint("PreClass on equipment: "+equipment.getName()+"  pre: "+toHtmlString(prereq));
		return 0;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		int runningTotal = 0;

		final boolean sumLevels = prereq.isTotalValues();
		final String aString = prereq.getKey().toUpperCase();
		final int preClass = Integer.parseInt(prereq.getOperand());


		if ("SPELLCASTER".equals(aString)) //$NON-NLS-1$
		{
			if (character.isSpellCaster(preClass, sumLevels))
			{
				runningTotal = preClass;
			}
		}
		else if (aString.startsWith("SPELLCASTER.")) //$NON-NLS-1$
		{
			if (character.isSpellCaster(aString.substring(12), preClass, sumLevels))
			{
				runningTotal = preClass;
			}
		}
		else
		{
			final PCClass aClass = character.getClassNamed(aString);
			if (aClass != null)
			{
				runningTotal += aClass.getLevel();
			}
		}
		runningTotal = prereq.getOperator().compare(runningTotal, preClass);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "CLASS"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	public String toHtmlString(final Prerequisite prereq) {
		final String level = prereq.getOperand();
		final String operator = prereq.getOperator().toDisplayString();

		return PropertyFactory.getFormattedString("PreClass.toHtml",prereq.getKey(), operator, level ); //$NON-NLS-1$
	}

}
