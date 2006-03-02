/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import pcgen.core.*;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.spell.Spell;
import pcgen.util.PropertyFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class PreFeatTester extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final Equipment equipment, final PlayerCharacter aPC)
		throws PrerequisiteException
	{
		if (aPC == null)
		{
			return 0;
		}
		return passes(prereq, aPC);
	}

    /*
     * (non-Javadoc)
     *
     * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
     */
    public int passes(final Prerequisite prereq, final PlayerCharacter character) throws PrerequisiteException
    {

        final boolean countMults = prereq.isCountMultiples();

        final int number;
        try
        {
            number = Integer.parseInt(prereq.getOperand());
        }
        catch (NumberFormatException exceptn)
        {
            throw new PrerequisiteException(PropertyFactory.getFormattedString("PreFeat.error", prereq.toString())); //$NON-NLS-1$
        }

        String key = prereq.getKey();
        String subKey = prereq.getSubKey();
        final boolean keyIsType = key.startsWith("TYPE=") || key.startsWith("TYPE."); //$NON-NLS-1$ //$NON-NLS-2$
        final boolean subKeyIsType = subKey != null && (subKey.startsWith("TYPE=") || subKey.startsWith("TYPE.")); //$NON-NLS-1$ //$NON-NLS-2$
        if (keyIsType)
        {
            key = key.substring(5);
        }
        if (subKeyIsType)
        {
            subKey = subKey.substring(5);
        }

        int runningTotal = 0;
        final List aFeatList = character != null ? character.aggregateFeatList() : null;
        if ((aFeatList != null) && !aFeatList.isEmpty())
        {
            for (Iterator e1 = aFeatList.iterator(); e1.hasNext();)
            {
                final Ability aFeat = (Ability) e1.next();
                final String featName = aFeat.getName();
                if ((!keyIsType && featName.equalsIgnoreCase(key)) || (keyIsType && aFeat.isType(key)))
                {
                    // either this feat has matched on the name, or the type

                    if (subKey != null)
                    {
                        final String cType = subKey;
                        final List availableList = new ArrayList();
                        final List selectedList = new ArrayList();
                        final String aChoiceString = aFeat.getChoiceString();
                        PObjectUtilities.modChoices(aFeat, availableList, selectedList, false, character, true);
                        availableList.clear();

                        if (subKeyIsType) // TYPE syntax
                        {
                            if (aChoiceString.startsWith("SKILL")) //$NON-NLS-1$
                            {
                                runningTotal = subKeySkill(countMults, runningTotal, cType, selectedList);
                            }
                            else if (aChoiceString.startsWith("WEAPONPROFS")) //$NON-NLS-1$
                            {
                                runningTotal = subKeyWeaponProf(countMults, runningTotal, cType, selectedList);
                            }
                            else if (aChoiceString.startsWith("DOMAIN")) //$NON-NLS-1$
                            {
                                runningTotal = subKeyDomain(countMults, runningTotal, cType, selectedList);
                            }
                            else if (aChoiceString.startsWith("SPELL")) //$NON-NLS-1$
                            {
                                runningTotal = subKeySpell(countMults, runningTotal, cType, selectedList);
                            }
                            // End. subKeyIsType
                        }
                        else {
                            if (featName.equalsIgnoreCase(key) && aFeat.containsAssociated(subKey))
                            {
                                runningTotal++;
                                if (aFeat.isMultiples() && countMults)
                                {
                                    runningTotal += (aFeat.getAssociatedCount() - 1);
                                }
                            }
                            else
                            {
                                final int wildCardPos = subKey.indexOf('%');

                                if (wildCardPos > -1)
                                {
                                    for (int k = 0; k < aFeat.getAssociatedCount(); ++k)
                                    {

                                        final String fString = aFeat.getAssociated(k).toUpperCase();
                                        if (wildCardPos == 0 || fString.startsWith(subKey.substring(0, wildCardPos - 1).toUpperCase()))
                                        {
                                            runningTotal++;
                                            if (!countMults)
                                            {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        // Subkey == null

                        runningTotal++;
                        if (aFeat.isMultiples() && countMults)
                        {
                            runningTotal += (aFeat.getAssociatedCount() - 1);
                        }
                    }
                }
                else
                {
                    if (subKey != null)
                    {
                        final String s1 = key + " (" + subKey + ")";
                        final String s2 = key + "(" + subKey + ")";
                        if (featName.equalsIgnoreCase(s1) || aFeat.getName().equalsIgnoreCase(s2))
                        {
                            runningTotal++;
                            if (!countMults)
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }

        runningTotal = prereq.getOperator().compare(runningTotal, number);
        return countedTotal(prereq, runningTotal);
    }

    /**
     * @param countMults
     * @param runningTotal
     * @param cType
     * @param selectedList
     * @return int
     */
    private int subKeySpell(final boolean countMults, int runningTotal, final String cType, final List selectedList) {
        for (Iterator e = selectedList.iterator(); e.hasNext();) {
            final Object aObj = e.next();
            final Spell sp;
            sp = Globals.getSpellNamed(aObj.toString());
            if (sp == null) {
                continue;
            }
            if (sp.isType(cType)) {
                runningTotal++;
                if (!countMults) {
                    break;
                }
            }
        }
        return runningTotal;
    }

    /**
     * @param countMults
     * @param runningTotal
     * @param cType
     * @param selectedList
     * @return int
     */
    private int subKeyDomain(final boolean countMults, int runningTotal, final String cType, final List selectedList) {
        for (Iterator e = selectedList.iterator(); e.hasNext();) {
            final Object aObj = e.next();
            final Domain dom;
            dom = Globals.getDomainKeyed(aObj.toString());
            if (dom == null) {
                continue;
            }
            if (dom.isType(cType)) {
                runningTotal++;
                if (!countMults) {
                    break;
                }
            }
        }
        return runningTotal;
    }

    /**
     * @param countMults
     * @param runningTotal
     * @param cType
     * @param selectedList
     * @return int
     */
    private int subKeyWeaponProf(final boolean countMults, int runningTotal, final String cType, final List selectedList) {
        for (Iterator e = selectedList.iterator(); e.hasNext();) {
            final Object aObj = e.next();
            final WeaponProf wp;
            wp = Globals.getWeaponProfNamed(aObj.toString());
            if (wp == null) {
                continue;
            }
            final Equipment eq;
            eq = EquipmentList.getEquipmentKeyed(wp.getKeyName());
            if (eq == null) {
                continue;
            }
            if (eq.isType(cType)) {
                runningTotal++;
                if (!countMults) {
                    break;
                }
            }
        }
        return runningTotal;
    }

    /**
     * @param countMults
     * @param runningTotal
     * @param cType
     * @param selectedList
     * @return int
     */
    private int subKeySkill(final boolean countMults, int runningTotal, final String cType, final List selectedList) {
        for (Iterator e = selectedList.iterator(); e.hasNext();) {
            final Object aObj = e.next();
            final Skill sk;
            sk = Globals.getSkillNamed(aObj.toString());
            if (sk == null) {
                continue;
            }
            if (sk.isType(cType)) {
                runningTotal++;
                if (!countMults) {
                    break;
                }
            }
        }
        return runningTotal;
    }

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
	        return PropertyFactory.getFormattedString(
                        "PreFeat.type.toHtml", new Object[] {
                        		prereq.getOperator().toDisplayString(),
                        		prereq.getOperand(),
                        		SettingsHandler.getGame().getSingularTabName(Constants.TAB_ABILITIES).toLowerCase(),
                        		aString.substring(5)
                        	});
		}
		// {2} {3} {1} {0}
        return PropertyFactory.getFormattedString(
                        "PreFeat.toHtml", new Object[] {
                        		SettingsHandler.getGame().getSingularTabName(Constants.TAB_ABILITIES).toLowerCase(),
                                aString,
                                prereq.getOperator().toDisplayString(),
                                prereq.getOperand() }); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     *
     * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
     */
    public String kindHandled() {
        return "FEAT"; //$NON-NLS-1$
    }

}
