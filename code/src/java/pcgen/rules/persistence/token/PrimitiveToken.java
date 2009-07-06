package pcgen.rules.persistence.token;

import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.rules.context.LoadContext;

public interface PrimitiveToken<T> extends PrimitiveChoiceFilter<T>
{
	public boolean initialize(LoadContext context, String value, String args);

	public String getTokenName();
}
