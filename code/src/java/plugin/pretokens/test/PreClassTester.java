/*
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Equipment;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreClassTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

    @Override
    public int passes(final Prerequisite prereq, final Equipment equipment, PlayerCharacter aPC)
    {
        Logging.debugPrint("PreClass on equipment: " + equipment.getName() + "  pre: " + toHtmlString(prereq));
        if (aPC == null)
        {
            return 0;
        }
        return passes(prereq, aPC, equipment);
    }

    @Override
    public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
    {
        CharacterDisplay display = character.getDisplay();
        int runningTotal = 0;
        int countedTotal = 0;

        final boolean sumLevels = prereq.isTotalValues();
        final String aString = prereq.getKey().toUpperCase();
        final int preClass = Integer.parseInt(prereq.getOperand());

        if ("SPELLCASTER".equals(aString)) //$NON-NLS-1$
        {
            int spellCaster = character.isSpellCaster(preClass, sumLevels);
            if (spellCaster > 0)
            {
                if (prereq.isCountMultiples())
                {
                    countedTotal = spellCaster;
                } else
                {
                    runningTotal = preClass;
                }
            }
        } else if (aString.startsWith("SPELLCASTER.")) //$NON-NLS-1$
        {
            int spellCaster = character.isSpellCaster(aString.substring(12), preClass, sumLevels);
            if (spellCaster > 0)
            {
                if (prereq.isCountMultiples())
                {
                    countedTotal = spellCaster;
                } else
                {
                    runningTotal = preClass;
                }
            }
        } else if (aString.equals("ANY"))
        {
            for (PCClass cl : display.getClassSet())
            {
                if (prereq.isCountMultiples())
                {
                    if (display.getLevel(cl) >= preClass)
                    {
                        countedTotal++;
                    }
                } else
                {
                    runningTotal = Math.max(runningTotal, display.getLevel(cl));
                }
            }
        } else if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
        {
            String typeString = aString.substring(5);
            for (PCClass cl : display.getClassSet())
            {
                if (cl.isType(typeString))
                {
                    if (prereq.isCountMultiples())
                    {
                        if (display.getLevel(cl) >= preClass)
                        {
                            countedTotal++;
                        }
                    } else
                    {
                        runningTotal = Math.max(runningTotal, display.getLevel(cl));
                    }
                } else
                {
                    for (CDOMReference<PCClass> ref : cl.getSafeListFor(ListKey.SERVES_AS_CLASS))
                    {
                        for (PCClass fakeClass : ref.getContainedObjects())
                        {
                            if (fakeClass.isType(typeString))
                            {
                                if (prereq.isCountMultiples())
                                {
                                    if (display.getLevel(cl) >= preClass)
                                    {
                                        countedTotal++;
                                    }
                                } else
                                {
                                    runningTotal += display.getLevel(cl);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } else
        {
            PCClass aClass = character.getClassKeyed(aString);
            if (aClass != null)
            {
                if (prereq.isCountMultiples())
                {
                    if (display.getLevel(aClass) >= preClass)
                    {
                        countedTotal++;
                    }
                } else
                {
                    runningTotal += display.getLevel(aClass);
                }
            } else
            {
                CLASSLIST:
                for (PCClass theClass : display.getClassSet())
                {
                    for (CDOMReference<PCClass> ref : theClass.getSafeListFor(ListKey.SERVES_AS_CLASS))
                    {
                        for (PCClass fakeClass : ref.getContainedObjects())
                        {
                            if (fakeClass.getKeyName().equalsIgnoreCase(aString))
                            {
                                if (prereq.isCountMultiples())
                                {
                                    if (display.getLevel(theClass) >= preClass)
                                    {
                                        countedTotal++;
                                    }
                                } else
                                {
                                    runningTotal += display.getLevel(theClass);
                                }
                                break CLASSLIST;
                            }
                        }
                    }
                }
            }
        }
        runningTotal = prereq.getOperator().compare(runningTotal, preClass);
        return countedTotal(prereq, prereq.isCountMultiples() ? countedTotal : runningTotal);
    }

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String kindHandled()
    {
        return "CLASS"; //$NON-NLS-1$
    }

    @Override
    public String toHtmlString(final Prerequisite prereq)
    {
        final String level = prereq.getOperand();
        final String operator = prereq.getOperator().toDisplayString();

        return LanguageBundle.getFormattedString("PreClass.toHtml", prereq.getKey(), operator, level); //$NON-NLS-1$
    }

}
