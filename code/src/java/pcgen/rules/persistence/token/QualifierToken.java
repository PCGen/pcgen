package pcgen.rules.persistence.token;

import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.LoadContext;

public interface QualifierToken<T> extends LstToken, PrimitiveChoiceSet<T>
{
	public boolean initialize(LoadContext context, Class<T> cl,
			String condition, String value, boolean negated);

	public String getTokenName();
}
