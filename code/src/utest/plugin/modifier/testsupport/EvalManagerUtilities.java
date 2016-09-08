package plugin.modifier.testsupport;

import pcgen.base.formula.base.EvaluationManager;

public final class EvalManagerUtilities
{

	private EvalManagerUtilities()
	{
	}

	public static EvaluationManager getInputEM(Object input)
	{
		EvaluationManager em = new EvaluationManager();
		em.push(EvaluationManager.INPUT, input);
		return em;
	}
}
