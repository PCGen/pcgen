package pcgen.base.formula.inst;

import java.util.Objects;

import pcgen.base.formula.base.VarScoped;

public class GlobalVarScoped implements VarScoped
{

	private final String name;

	public GlobalVarScoped(String name)
	{
		this.name = Objects.requireNonNull(name);
	}

	@Override
	public String getKeyName()
	{
		return name;
	}

	@Override
	public String getLocalScopeName()
	{
		//null to indicate Global
		return null;
	}

	@Override
	public VarScoped getVariableParent()
	{
		//Null to indicate Global
		return null;
	}

}
