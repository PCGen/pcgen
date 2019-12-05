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

import pcgen.util.Logging;

public class Rule extends ArrayList<String> implements DataElement
{
    private final List<DataValue> retList = new ArrayList<>();
    private String id;
    private String title;
    private final VariableHashMap allVars;
    private int weight;

    public Rule(VariableHashMap allVars, String title, String id, int weight)
    {
        this.allVars = allVars;
        this.title = title;
        this.id = id;
        this.weight = weight;
    }

    @Override
    public List<DataValue> getData() throws Exception
    {
        retList.clear();

        for (String key : this)
        {
            DataElement ele = allVars.getDataElement(key);
            retList.addAll(ele.getData());
        }

        return retList;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public List<DataValue> getLastData() throws Exception
    {
        retList.clear();

        for (String key : this)
        {
            DataElement ele = allVars.getDataElement(key);
            retList.addAll(ele.getLastData());
        }

        return retList;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    @Override
    public int getWeight()
    {
        return weight;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (String key : this)
        {
            try
            {
                DataElement ele = allVars.getDataElement(key);

                if (ele.getTitle() != null)
                {
                    sb.append("[").append(ele.getTitle()).append("] ");
                }
            } catch (Exception e)
            {
                Logging.errorPrint(e.getMessage(), e);
            }
        }

        return sb.toString();
    }
}
