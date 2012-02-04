/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.AlignmentConverter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

/**
 * Prerequisite test that the character has a deity with the correct alignment.
 */
public class PreDeityAlignTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
	{

		//
		// If game mode doesn't support alignment, then pass the prereq
		//
		int runningTotal = 0;

		if (Globals.getGameModeAlignmentText().length() == 0)
		{
			runningTotal = 1;
		}
		else
		{
			PCAlignment deityAlign = null; //$NON-NLS-1$
			if (character.getDeity() != null)
			{
				deityAlign = character.getDeity().get(ObjectKey.ALIGNMENT);
			}
			if (deityAlign != null)
			{
				String desiredAlignIdentifier = prereq.getOperand();
				PCAlignment desiredAlign = getPCAlignment(desiredAlignIdentifier);

				if (desiredAlign.equals(deityAlign))
				{
					runningTotal = 1;
				}
			}
		}

		return countedTotal(prereq, runningTotal);
	}

	private PCAlignment getPCAlignment(String desiredAlignIdentifier)
	{
		return AlignmentConverter.getPCAlignment(desiredAlignIdentifier);
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	public String kindHandled()
	{
		return "DEITYALIGN"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		return LanguageBundle
			.getFormattedString(
				"PreDeityAlign.toHtml", prereq.getOperator().toDisplayString(), getPCAlignment(prereq.getOperand()).getAbb()); //$NON-NLS-1$
	}

}
