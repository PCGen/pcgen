/*
 * Created on 01-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreCheckTester extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/** Constructor */
	public PreCheckTester() {
		super();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindHandled()
	 */
	public String kindHandled() {
		return "CHECK"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.prereq.Prerequisite, pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character) {
		int runningTotal=0;

		final String checkName = prereq.getKey();
		final int operand = character.getVariableValue(prereq.getOperand(), "").intValue(); //$NON-NLS-1$
		final int characterCheckVal = SettingsHandler.getGame().getIndexOfCheck(checkName);
		if (characterCheckVal>=0) {
			final int characterCheckBonus = (int) character.getBonus(characterCheckVal + 1, true);
			runningTotal = prereq.getOperator().compare(characterCheckBonus, operand);
		}
		return countedTotal(prereq, runningTotal);
	}

}
