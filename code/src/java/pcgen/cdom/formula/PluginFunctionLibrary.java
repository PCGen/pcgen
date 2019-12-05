/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.formula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcgen.base.formula.base.FormulaFunction;
import pcgen.system.PluginLoader;
import pcgen.util.Logging;

public final class PluginFunctionLibrary implements PluginLoader
{

    private static PluginFunctionLibrary instance = null;

    private ArrayList<FormulaFunction> list = new ArrayList<>();

    private PluginFunctionLibrary()
    {
        // Don't instantiate utility class
    }

    public static PluginFunctionLibrary getInstance()
    {
        if (instance == null)
        {
            instance = new PluginFunctionLibrary();
        }
        return instance;
    }

    @Override
    public void loadPlugin(Class<?> clazz) throws Exception
    {
        Object token = clazz.newInstance();
        if (token instanceof FormulaFunction)
        {
            FormulaFunction tok = (FormulaFunction) token;
            FormulaFunction existing = existingFunction(tok.getFunctionName());
            if (existing != null)
            {
                Logging.errorPrint("Duplicate Function " + tok.getFunctionName() + " found. Classes were "
                        + existing.getClass().getName() + " and " + tok.getClass().getName());
            } else
            {
                list.add(tok);
            }
        }
    }

    private FormulaFunction existingFunction(String name)
    {
        for (FormulaFunction f : list)
        {
            if (f.getFunctionName().equalsIgnoreCase(name))
            {
                return f;
            }
        }
        return null;
    }

    @Override
    public Class<?>[] getPluginClasses()
    {
        return new Class[]{FormulaFunction.class};
    }

    public List<FormulaFunction> getFunctions()
    {
        return Collections.unmodifiableList(list);
    }

    public static void clear()
    {
        if (instance != null)
        {
            instance.list.clear();
        }
    }

}
