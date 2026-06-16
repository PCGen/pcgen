package pcgen.cdom.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.base.format.ArrayFormatManager;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.cdom.formula.scope.GlobalPCScope;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;

import util.FormatSupport;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FormulaFactoryTest
{

	private ArrayFormatManager<Number> arrayManager;
	private PCGenScope varScope;
	private LoadContext context;
	private ManagerFactory managerFactory;

	@BeforeEach
	void setUp() throws Exception
	{
		arrayManager = new ArrayFormatManager<>(FormatUtilities.NUMBER_MANAGER,
			'\n', ',');
		context = new RuntimeLoadContext(
			RuntimeReferenceContext.createRuntimeReferenceContext(),
			new ConsolidatedListCommitStrategy());
		FormatSupport.addBasicDefaults(context);
		varScope = context.getVariableContext().getScope(GlobalPCScope.GLOBAL_SCOPE_NAME);
		managerFactory = context.getVariableContext().getManagerFactory();
	}

	@AfterEach
	void tearDown() throws Exception
	{
		arrayManager = null;
		context = null;
		varScope = null;
		managerFactory = null;
	}

	@Test
	void testNumberFormula()
	{
		NEPFormula<Number> formula1 = FormulaFactory.getValidFormula("3",
			managerFactory, varScope, FormatUtilities.NUMBER_MANAGER);
		NEPFormula<Number> formula2 = FormulaFactory.getValidFormula("3",
			managerFactory, varScope, FormatUtilities.NUMBER_MANAGER);
		assertEquals(formula1, formula2);
	}

	@Test
	void testComplexNumberFormula()
	{
		context.getVariableContext().assertLegalVariableID("Arm", varScope,
			FormatUtilities.NUMBER_MANAGER);
		NEPFormula<Number> formula1 = FormulaFactory.getValidFormula("3+Arm",
			managerFactory, varScope, FormatUtilities.NUMBER_MANAGER);
		NEPFormula<Number> formula2 = FormulaFactory.getValidFormula("3+Arm",
			managerFactory, varScope, FormatUtilities.NUMBER_MANAGER);
		assertEquals(formula1, formula2);
	}

	@Test
	void testArrayFormula()
	{
		NEPFormula<Number[]> formula1 = FormulaFactory.getValidFormula("3,4,5",
			managerFactory, varScope, arrayManager);
		NEPFormula<Number[]> formula2 = FormulaFactory.getValidFormula("3,4,5",
			managerFactory, varScope, arrayManager);
		assertEquals(formula1, formula2);
	}

}
