package pcgen.util;

import org.nfunk.jep.function.PostfixMathCommand;

public abstract class PCGenCommand extends PostfixMathCommand
{
	protected Object parent;
	protected String variableSource;

	public abstract String getFunctionName();

	public boolean updateVariables(PJEP jep)
	{
		return true;
	}

	public void setParent(Object parent)
	{
		this.parent = parent;
	}

	protected void setVariableSource(String variableSource)
	{
		this.variableSource = variableSource;
	}

	/**
	 * Is this command cacheable?
	 * @return true if cacheable, false if not.
	 */
	public boolean getCachable()
	{
		return true;
	}
}
