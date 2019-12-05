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
        return em.getWith(EvaluationManager.INPUT, input);
    }
}
