/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.function.testsupport;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.ref.WeakReference;
import java.util.Optional;

import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.WriteableFunctionLibrary;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.LoadContextFacet;
import pcgen.cdom.formula.ManagerKey;
import pcgen.cdom.formula.MonitorableVariableStore;
import pcgen.cdom.formula.scope.GlobalPCScope;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;

import util.FormatSupport;

public abstract class AbstractFormulaTestCase
{

    protected FormatManager<Number> numberManager = new NumberManager();
    protected FormatManager<String> stringManager = new StringManager();

    protected LoadContext context;
    private FormulaManager formulaManager;

    public void setUp() throws Exception
    {
        context = new RuntimeLoadContext(
                RuntimeReferenceContext.createRuntimeReferenceContext(),
                new ConsolidatedListCommitStrategy());
        FacetLibrary.getFacet(LoadContextFacet.class).set(context.getDataSetID(),
                new WeakReference<>(context));
        FormatSupport.addBasicDefaults(context);
    }

    /**
     * Force a given formula to be valid.
     *
     * @param node           The root node for processing the formula
     * @param formatManager  The FormatManager indicating the format of the result of the formula
     * @param assertedFormat The asserted format
     */
    protected void isValid(SimpleNode node, FormatManager<?> formatManager,
            FormatManager<?> assertedFormat)
    {
        SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
        FormulaSemantics semantics = generateFormulaSemantics(assertedFormat);
        FormatManager<?> resultFormat =
                (FormatManager<?>) semanticsVisitor.visit(node, semantics);
        if (!formatManager.equals(resultFormat))
        {
            fail(() -> "Expected Formula to return: " + formatManager.getIdentifierType()
                    + " but it returned: " + resultFormat.getIdentifierType());
        }
    }

    protected void isStatic(String formula, SimpleNode node, boolean b)
    {
        StaticVisitor staticVisitor =
                new StaticVisitor(getFormulaManager().get(FormulaManager.FUNCTION));
        boolean isStat = (Boolean) staticVisitor.visit(node, null);
        if (isStat != b)
        {
            fail(() -> "Expected Static (" + b + ") Formula: " + formula);
        }
    }

    protected void evaluatesTo(String formula, SimpleNode node, Object valueOf)
    {
        EvaluationManager manager = generateManager();
        Object result = new EvaluateVisitor().visit(node, manager);
        if (result.equals(valueOf))
        {
            return;
        }
        //Try ints as double as well just in case (temporary)
        if (valueOf instanceof Integer)
        {
            if (result.equals(valueOf))
            {
                return;
            }
        }
        //Give Doubles a bit of fuzz
        else if (valueOf instanceof Double)
        {
            if (TestUtilities.doubleEqual((Double) valueOf,
                    ((Number) result).doubleValue(), TestUtilities.SMALL_ERROR))
            {
                return;
            }
        }
        fail(() -> "Expected " + valueOf.getClass().getSimpleName() + " (" + valueOf
                + ") for Formula: " + formula + ", was " + result + " ("
                + result.getClass().getSimpleName() + ")");
    }

    protected void isNotValid(String formula, SimpleNode node)
    {
        SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
        FormulaSemantics semantics = generateFormulaSemantics(null);
        try
        {
            semanticsVisitor.visit(node, semantics);
            fail(() -> "Expected Invalid Formula: " + formula + " but was valid");
        } catch (SemanticsFailureException e)
        {
            //Expected
        }
    }

    protected WriteableFunctionLibrary getFunctionLibrary()
    {
        return (WriteableFunctionLibrary) getFormulaManager()
                .get(FormulaManager.FUNCTION);
    }

    protected OperatorLibrary getOperatorLibrary()
    {
        return getFormulaManager().getOperatorLibrary();
    }

    protected VariableLibrary getVariableLibrary()
    {
        return getFormulaManager().getFactory();
    }

    protected MonitorableVariableStore getVariableStore()
    {
        return (MonitorableVariableStore) getFormulaManager().get(FormulaManager.RESULTS);
    }

    protected LegalScope getGlobalScope()
    {
        return getGlobalScopeInst().getLegalScope();
    }

    protected ScopeInstance getGlobalScopeInst()
    {
        return getFormulaManager().getScopeInstanceFactory()
                .getGlobalInstance(GlobalPCScope.GLOBAL_SCOPE_NAME);
    }

    protected FormulaManager getFormulaManager()
    {
        if (formulaManager == null)
        {
            formulaManager = context.getVariableContext().getFormulaManager()
                    .getWith(FormulaManager.RESULTS, new MonitorableVariableStore());
        }
        return formulaManager;
    }

    protected FormulaSemantics generateFormulaSemantics(FormatManager<?> assertedFormat)
    {
        Optional<FormatManager<?>> format =
                (assertedFormat == null) ? Optional.empty() : Optional.of(assertedFormat);
        return new FormulaSemantics()
                .getWith(FormulaSemantics.FMANAGER, formulaManager)
                .getWith(FormulaSemantics.SCOPE, getGlobalScope())
                .getWith(FormulaSemantics.ASSERTED, format)
                .getWith(ManagerKey.CONTEXT, context);
    }

    public EvaluationManager generateManager()
    {
        return new EvaluationManager()
                .getWith(EvaluationManager.FMANAGER, getFormulaManager())
                .getWith(EvaluationManager.INSTANCE, getGlobalScopeInst())
                .getWith(EvaluationManager.ASSERTED, Optional.of(FormatUtilities.NUMBER_MANAGER))
                .getWith(ManagerKey.CONTEXT, context);
    }
}
