package pcgen.base.formula.base;

import pcgen.base.util.MappedDeque;
import pcgen.base.util.TypedKey;

public class EvaluationManager extends MappedDeque
{

	public static final TypedKey<FormulaManager> FMANAGER =
			new TypedKey<FormulaManager>();

	public static final TypedKey<ScopeInstance> INSTANCE =
			new TypedKey<ScopeInstance>();

	public static final TypedKey<Class<?>> ASSERTED = new TypedKey<Class<?>>();

	public static final TypedKey<Object> INPUT = new TypedKey<Object>();

	public static EvaluationManager generate(FormulaManager formulaManager,
		ScopeInstance globalScopeInst, Class<Number> assertedFormat)
	{
		EvaluationManager manager = new EvaluationManager();
		manager.set(FMANAGER, formulaManager);
		manager.set(INSTANCE, globalScopeInst);
		manager.set(ASSERTED, assertedFormat);
		return manager;
	}

	public static EvaluationManager generate(FormulaManager formulaManager,
		VariableID<?> varID)
	{
		EvaluationManager manager = new EvaluationManager();
		manager.set(FMANAGER, formulaManager);
		manager.set(INSTANCE, varID.getScope());
		manager.set(ASSERTED, varID.getVariableFormat());
		return manager;
	}

}
