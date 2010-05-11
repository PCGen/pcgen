package pcgen.rules.persistence.token;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.LoadContext;

public interface QualifierToken<T extends CDOMObject> extends LstToken, PrimitiveChoiceSet<T>
{
	public boolean initialize(LoadContext context, SelectionCreator<T> cl,
			String condition, String value, boolean negated);

	public String getTokenName();
}
