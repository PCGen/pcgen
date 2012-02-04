/*
 * Created on 21-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

/**
 * @author Valued Customer
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreSpellCastMemorizeTester extends AbstractPrerequisiteTest
		implements PrerequisiteTest
{

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	public String kindHandled()
	{
		return "spellcast.memorize"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.prereq.Prerequisite, pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
	{

		final int requiredNumber = Integer.parseInt(prereq.getOperand());
		final boolean prereqMemorized =
				prereq.getKey().toUpperCase().startsWith("Y"); //$NON-NLS-1$
		int runningTotal = 0;

		for (PCClass aClass : character.getClassSet())
		{
			if (aClass.getSafe(ObjectKey.MEMORIZE_SPELLS) == prereqMemorized)
			{
				runningTotal++;
			}
		}

		runningTotal =
				prereq.getOperator().compare(runningTotal, requiredNumber);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		final boolean prereqMemorized =
				prereq.getKey().toUpperCase().startsWith("Y"); //$NON-NLS-1$

		if (prereqMemorized)
		{
			return LanguageBundle
				.getFormattedString(
					"PreSpellCastMemorize.toHtml_does_memorise", prereq.getOperator().toDisplayString()); //$NON-NLS-1$
		}
		return LanguageBundle
			.getFormattedString(
				"PreSpellCastMemorize.toHtml_does_not_memorise", prereq.getOperator().toDisplayString()); //$NON-NLS-1$
	}

}
