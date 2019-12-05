/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.context;

import java.lang.ref.WeakReference;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.cdom.formula.ManagerKey;
import pcgen.cdom.helper.ReferenceDependency;

/**
 * PCGenManagerFactory is a ManagerFactory responsible for ensuring the Managers are
 * constructed with sufficient support to work with the formula functions provided in
 * PCGen.
 */
public class PCGenManagerFactory implements ManagerFactory
{
    private final WeakReference<LoadContext> context;

    /**
     * Constructs a new PCGenManagerFactory with the provided LoadContext to be included
     * in each Manager that is returned.
     *
     * @param context The LoadContext for this ManagerFactory to include in each Manager that
     *                is returned
     */
    public PCGenManagerFactory(LoadContext context)
    {
        this.context = new WeakReference<>(context);
    }

    @Override
    public FormulaSemantics generateFormulaSemantics(FormulaManager manager, LegalScope legalScope)
    {
        FormulaSemantics semantics = ManagerFactory.super.generateFormulaSemantics(manager, legalScope);
        return semantics.getWith(ManagerKey.CONTEXT, context.get());
    }

    @Override
    public EvaluationManager generateEvaluationManager(FormulaManager formulaManager)
    {
        EvaluationManager evalManager = ManagerFactory.super.generateEvaluationManager(formulaManager);
        return evalManager.getWith(ManagerKey.CONTEXT, context.get());
    }

    @Override
    public DependencyManager generateDependencyManager(FormulaManager formulaManager, ScopeInstance scopeInst)
    {
        DependencyManager depManager = ManagerFactory.super.generateDependencyManager(formulaManager, scopeInst);
        return depManager.getWith(ManagerKey.CONTEXT, context.get()).getWith(ManagerKey.REFERENCES,
                new ReferenceDependency());
    }

}
