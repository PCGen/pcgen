/*
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Globals;
import pcgen.core.PCCheck;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;

/**
 * Prerequisite test that the character has a non-zero value for a given check.
 */
public class PreCheckTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

    /**
     * Constructor.
     */
    public PreCheckTester()
    {
        super();
    }

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String kindHandled()
    {
        return "CHECK"; //$NON-NLS-1$
    }

    @Override
    public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
    {
        int runningTotal = 0;

        final String checkName = prereq.getKey();
        final int operand = character.getVariableValue(prereq.getOperand(), "").intValue(); //$NON-NLS-1$
        PCCheck check =
                Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCCheck.class, checkName);
        if (check != null)
        {
            final int characterCheckBonus = character.getTotalCheck(check);
            runningTotal = prereq.getOperator().compare(characterCheckBonus, operand) > 0 ? 1 : 0;
        }
        return countedTotal(prereq, runningTotal);
    }

}
