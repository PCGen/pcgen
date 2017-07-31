package pcgen.base.formula.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VariableList
{

	private List<VariableID<?>> vars;

	/**
	 * Returns a non-null list of VariableID objects that identify the list of
	 * dependencies of the Formula this VariableDependencyManager represents.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. The contents
	 * of the List will not be modified as a result of the VariableDependencyManager
	 * maintaining or otherwise transferring a reference to the List to another object
	 * (and the VariableDependencyManager cannot be modified if the returned list is
	 * modified).
	 * 
	 * @return A non-null list of VariableID objects that identify the list of
	 *         dependencies of the Formula this VariableDependencyManager represents
	 */
	public List<VariableID<?>> getVariables()
	{
		if (vars == null)
		{
			vars = Collections.emptyList();
		}
		else
		{
			vars = new ArrayList<>(vars);
		}
		return vars;
	}

	public void add(VariableID<?> varID)
	{
		if (vars == null)
		{
			vars = new ArrayList<>();
		}
		vars.add(varID);
	}

}
