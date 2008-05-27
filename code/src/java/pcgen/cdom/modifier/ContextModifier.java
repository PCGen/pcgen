package pcgen.cdom.modifier;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.content.Modifier;
import pcgen.util.StringPClassUtil;

public class ContextModifier<T, R extends PrereqObject> implements Modifier<T>
{
	private final Modifier<T> modifier;
	private final CDOMReference<R> contextItems;

	public ContextModifier(Modifier<T> mod, CDOMReference<R> context)
	{
		if (mod == null)
		{
			throw new IllegalArgumentException(
					"Modifier in ContextModifier cannot be null");
		}
		if (context == null)
		{
			throw new IllegalArgumentException(
					"Context in ContextModifier cannot be null");
		}
		modifier = mod;
		contextItems = context;
	}

	public T applyModifier(T obj, Object context)
	{
		return (context instanceof PrereqObject && contextItems
				.contains((R) context)) ? modifier.applyModifier(obj, context)
				: obj;
	}

	public String getLSTformat()
	{
		String cf = contextItems.getLSTformat();
		StringBuilder sb = new StringBuilder();
		sb.append(modifier.getLSTformat()).append('|');
		sb.append(StringPClassUtil.getStringFor(contextItems
				.getReferenceClass()));
		sb.append(cf.indexOf('=') == -1 ? '=' : '.');
		sb.append(cf);
		return sb.toString();
	}

	public Class<T> getModifiedClass()
	{
		return modifier.getModifiedClass();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ContextModifier)
		{
			ContextModifier<?, ?> other = (ContextModifier<?, ?>) obj;
			return modifier.equals(other.modifier)
					&& contextItems.equals(other.contextItems);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return modifier.hashCode() * 31 - contextItems.hashCode();
	}

}
