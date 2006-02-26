/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreDeityAlign  extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
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
			final GameMode gm = SettingsHandler.getGame();
			final String[] aligns = gm.getAlignmentListStrings(false);

			String deityAlign = ""; //$NON-NLS-1$
			if (character.getDeity() != null)
			{
				try
				{
					final int align = Integer.parseInt(character.getDeity().getAlignment());
					deityAlign = aligns[align];
				}
				catch (NumberFormatException e)
				{
					// If it isn't a number, we expect the exception 
					deityAlign = character.getDeity().getAlignment();
				}
			}

			String desiredAlign = prereq.getOperand();
			try
			{
				final int align = Integer.parseInt(prereq.getOperand());
				desiredAlign = aligns[align];
			}
			catch (NumberFormatException e)
			{
				// If it isn't a number, we expect the exception 
			}

			if (desiredAlign.equalsIgnoreCase(deityAlign))
			{
				runningTotal = 1;
			}
		}

		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "DEITYALIGN"; //$NON-NLS-1$
	}


	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	public String toHtmlString(final Prerequisite prereq) {
		return PropertyFactory.getFormattedString("PreDeityAlign.toHtml", prereq.getOperator().toDisplayString(), SettingsHandler.getGame().getShortAlignmentAtIndex(Integer.parseInt(prereq.getKey()) )); //$NON-NLS-1$
	}

}
