/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import pcgen.core.PlayerCharacter;
import pcgen.util.PropertyFactory;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreDeity  extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character) throws PrerequisiteException
	{
		int runningTotal;

		final String charDeity = character.getDeity()!=null ? character.getDeity().getName() : ""; //$NON-NLS-1$

		if (prereq.getOperator().equals( PrerequisiteOperator.EQ ))
		{
			runningTotal = (charDeity.equalsIgnoreCase(prereq.getOperand())) ? 1 : 0;
		}
		else if (prereq.getOperator().equals( PrerequisiteOperator.NEQ ))
		{
			runningTotal = (charDeity.equalsIgnoreCase(prereq.getOperand())) ? 0 : 1;
		}
		else
		{
			throw new PrerequisiteException(PropertyFactory.getFormattedString("PreDeity.error.bad_coparator", prereq.toString())); //$NON-NLS-1$
		}

		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "DEITY"; //$NON-NLS-1$
	}

}
