/*
 * (c) Copyright 2019 Thomas Parker thpr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package util;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.solver.Modifier;
import pcgen.base.util.FormatManager;

public class SimpleSetModifier<T> implements Modifier<T>
{

    private T object;
    private FormatManager<T> formatManager;

    public SimpleSetModifier(FormatManager<T> formatManager, T object)
    {
        this.formatManager = formatManager;
        this.object = object;
    }

    @Override
    public T process(EvaluationManager manager)
    {
        return object;
    }

    @Override
    public void getDependencies(DependencyManager fdm)
    {
    }

    @Override
    public long getPriority()
    {
        return 0;
    }

    @Override
    public FormatManager<T> getVariableFormat()
    {
        return formatManager;
    }

    @Override
    public String getIdentification()
    {
        return "SET";
    }

    @Override
    public String getInstructions()
    {
        return formatManager.unconvert(object);
    }

}
