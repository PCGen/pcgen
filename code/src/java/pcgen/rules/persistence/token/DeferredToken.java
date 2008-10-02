package pcgen.rules.persistence.token;

import pcgen.cdom.base.CDOMObject;
import pcgen.rules.context.LoadContext;

public interface DeferredToken<T extends CDOMObject>
{
	public boolean process(LoadContext context, T obj);
	
	public Class<T> getTokenClass();
}
