/*
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

/**
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreSpellCastMemorizeTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String kindHandled()
    {
        return "spellcast.memorize"; //$NON-NLS-1$
    }

    @Override
    public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
    {

        final int requiredNumber = Integer.parseInt(prereq.getOperand());
        final boolean prereqMemorized = prereq.getKey().toUpperCase().startsWith("Y"); //$NON-NLS-1$
        int runningTotal = 0;

        for (PCClass aClass : display.getClassSet())
        {
            if (aClass.getSafe(ObjectKey.MEMORIZE_SPELLS) == prereqMemorized)
            {
                runningTotal++;
            }
        }

        runningTotal = prereq.getOperator().compare(runningTotal, requiredNumber);
        return countedTotal(prereq, runningTotal);
    }

    @Override
    public String toHtmlString(final Prerequisite prereq)
    {
        final boolean prereqMemorized = prereq.getKey().toUpperCase().startsWith("Y"); //$NON-NLS-1$
        final Object[] args = {prereq.getOperator().toDisplayString(), prereq.getOperand()};

        if (prereqMemorized)
        {
            return LanguageBundle.getFormattedString("PreSpellCastMemorize.toHtml_does_memorise", args); //$NON-NLS-1$
        }
        return LanguageBundle.getFormattedString("PreSpellCastMemorize.toHtml_does_not_memorise", args); //$NON-NLS-1$
    }

}
