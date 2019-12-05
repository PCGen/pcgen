/*
 * Copyright 2003 (C) Devon Jones
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.doomsdaybook;

import java.util.ArrayList;
import java.util.List;

import gmgen.plugin.dice.Dice;

/**
 * This class deals with RuleSets for Random name generation
 */
public class RuleSet extends ArrayList<String> implements DataElement
{
    private final List<DataValue> retList = new ArrayList<>();
    private Rule retRule;
    private String id;
    private String title;
    private String usage = "private";
    private final VariableHashMap allVars;
    private int weight;

    /**
     * Constructor
     *
     * @param allVars
     * @param title
     * @param id
     * @param weight
     */
    public RuleSet(VariableHashMap allVars, String title, String id, int weight)
    {
        this.allVars = allVars;
        this.title = title;
        this.id = id;
        this.weight = weight;
    }

    /**
     * Constructor
     *
     * @param allVars
     * @param title
     * @param id
     * @param usage
     */
    public RuleSet(VariableHashMap allVars, String title, String id, String usage)
    {
        this(allVars, title, id, 1, usage);
    }

    /**
     * Constructor
     *
     * @param allVars
     * @param title
     * @param id
     * @param weight
     * @param usage
     */
    private RuleSet(VariableHashMap allVars, String title, String id, int weight, String usage)
    {
        this.allVars = allVars;
        this.title = title;
        this.id = id;
        this.weight = weight;
        this.usage = usage;
    }

    /**
     * Get the data
     *
     * @return A list of data
     * @throws Exception
     */
    @Override
    public List<DataValue> getData() throws Exception
    {
        retList.clear();

        int rangeTop = getRange();
        int modifier;

        try
        {
            modifier = Integer.parseInt(allVars.getVal(getId() + "modifier"));
        } catch (Exception e)
        {
            modifier = 0;
        }

        // Determine which entry to choose
        Dice die = new Dice(1, rangeTop, 0);
        int choice = die.roll();
        choice += modifier;
        choice = (choice < 0) ? rangeTop : choice;

        //select the detail to return
        int aWeight = 0;

        // Iterate through the list of choices until the weights (from each DataValue)
        // are greater the num chosen as the 'choice'
        for (String key : this)
        {
            DataElement chkValue = allVars.getDataElement(key);
            int valueWeight = chkValue.getWeight();

            if (valueWeight > 0)
            {
                aWeight += valueWeight;

                if (aWeight >= choice)
                {
                    retList.addAll(chkValue.getData());

                    break;
                }
            }
        }

        return retList;
    }

    /**
     * Set the id
     *
     * @param id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Get the id
     *
     * @return id
     */
    @Override
    public String getId()
    {
        return id;
    }

    /**
     * Get the last data
     *
     * @return the last list of data
     */
    @Override
    public List<DataValue> getLastData()
    {
        return retList;
    }

    /**
     * Get the last rule
     *
     * @return last rule
     */
    public Rule getLastRule()
    {
        return retRule;
    }

    /**
     * Get the range.
     *
     * @return range
     * @throws Exception When no entry exists for the supplied key.
     */
    private int getRange() throws Exception
    {
        int rangeTop = 0;

        for (String key : this)
        {
            DataElement value = allVars.getDataElement(key);
            rangeTop += value.getWeight();
        }

        if (rangeTop <= 0)
        { //the die will nullpointer if it is not at least 1
            rangeTop = 1;
        }

        return rangeTop;
    }

    /**
     * Get the rule.
     *
     * @return rule
     * @throws Exception When no entry exists for the supplied key.
     */
    public Rule getRule() throws Exception
    {
        int rangeTop = getRange();
        int modifier;

        try
        {
            modifier = Integer.parseInt(allVars.getVal(getId() + "modifier"));
        } catch (Exception e)
        {
            modifier = 0;
        }

        // Determine which entry to choose
        Dice die = new Dice(1, rangeTop, 0);
        int choice = die.roll();
        choice += modifier;

        choice = (choice < 0) ? rangeTop : choice;

        //select the detail to return
        int aWeight = 0;

        // Iterate through the list of choices until the weights (from each DataValue)
        // are greater the num chosen as the 'choice'
        for (String key : this)
        {
            Rule chkValue = (Rule) allVars.getDataElement(key);
            int valueWeight = chkValue.getWeight();

            if (valueWeight > 0)
            {
                aWeight += valueWeight;

                if (aWeight >= choice)
                {
                    retRule = chkValue;

                    return chkValue;
                }
            }
        }

        return retRule;
    }

    /**
     * Get the rule given a choice
     *
     * @param choice
     * @return Rule
     * @throws Exception When no entry exists for the supplied key.
     */
    public Rule getRule(int choice) throws Exception
    {
        //select the detail to return
        int aWeight = 0;

        // Iterate through the list of choices until the weights (from each DataValue)
        // are greater the num chosen as the 'choice'
        for (String key : this)
        {
            Rule chkValue = (Rule) allVars.getDataElement(key);
            int valueWeight = chkValue.getWeight();

            if (valueWeight > 0)
            {
                aWeight += valueWeight;

                if (aWeight >= choice)
                {
                    retRule = chkValue;

                    break;
                }
            }
        }

        return retRule;
    }

    /**
     * Set the title
     *
     * @param title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Get the title
     *
     * @return title
     */
    @Override
    public String getTitle()
    {
        return title;
    }

    /**
     * Set the usage
     *
     * @param usage
     */
    public void setUsage(String usage)
    {
        this.usage = usage;
    }

    /**
     * Get the usage
     *
     * @return usage
     */
    public String getUsage()
    {
        return usage;
    }

    /**
     * Set the weight
     *
     * @param weight
     */
    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    /**
     * Get the weight
     *
     * @return weight
     */
    @Override
    public int getWeight()
    {
        return weight;
    }

    @Override
    public String toString()
    {
        return getTitle();
    }
}
