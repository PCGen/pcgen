package pcgen.cdom.base;

import static org.junit.Assert.assertEquals;

import pcgen.base.format.ArrayFormatManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.formula.inst.ScopeManagerInst;
import pcgen.base.solver.FormulaSetupFactory;
import pcgen.cdom.formula.scope.GlobalPCScope;
import pcgen.cdom.formula.scope.PCGenScope;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FormulaFactoryTest
{

    private ArrayFormatManager<Number> arrayManager;
    private PCGenScope varScope;
    private FormulaManager formulaManager;

    @BeforeEach
    void setUp()
    {
        arrayManager = new ArrayFormatManager<>(FormatUtilities.NUMBER_MANAGER,
                '\n', ',');
        varScope = new GlobalPCScope();
        FormulaSetupFactory formulaSetupFactory = new FormulaSetupFactory();
        ScopeManagerInst legalScopeManager = new ScopeManagerInst();
        formulaSetupFactory
                .setLegalScopeManagerSupplier(() -> legalScopeManager);
        formulaManager = formulaSetupFactory.generate();
        legalScopeManager.registerScope(varScope);
    }

    @AfterEach
    void tearDown()
    {
        arrayManager = null;
        formulaManager = null;
        varScope = null;
    }

    @Test
    void testNumberFormula()
    {
        NEPFormula<Number> formula1 =
                FormulaFactory.getValidFormula("3", new ManagerFactory()
                {
                }, formulaManager, varScope, FormatUtilities.NUMBER_MANAGER);
        NEPFormula<Number> formula2 =
                FormulaFactory.getValidFormula("3", new ManagerFactory()
                {
                }, formulaManager, varScope, FormatUtilities.NUMBER_MANAGER);
        assertEquals(formula1, formula2);
    }

    @Test
    void testComplexNumberFormula()
    {
        formulaManager.getFactory().assertLegalVariableID("Arm", varScope,
                FormatUtilities.NUMBER_MANAGER);
        NEPFormula<Number> formula1 =
                FormulaFactory.getValidFormula("3+Arm", new ManagerFactory()
                {
                }, formulaManager, varScope, FormatUtilities.NUMBER_MANAGER);
        NEPFormula<Number> formula2 =
                FormulaFactory.getValidFormula("3+Arm", new ManagerFactory()
                {
                }, formulaManager, varScope, FormatUtilities.NUMBER_MANAGER);
        assertEquals(formula1, formula2);
    }

    @Test
    void testArrayFormula()
    {
        NEPFormula<Number[]> formula1 =
                FormulaFactory.getValidFormula("3,4,5", new ManagerFactory()
                {
                }, formulaManager, varScope, arrayManager);
        NEPFormula<Number[]> formula2 =
                FormulaFactory.getValidFormula("3,4,5", new ManagerFactory()
                {
                }, formulaManager, varScope, arrayManager);
        assertEquals(formula1, formula2);
    }

}
