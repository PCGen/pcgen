/**
 * 
 */
package pcgen.cdom.reference;

import pcgen.cdom.base.CDOMObject;

public class TransparentReferenceManufacturer<T extends CDOMObject>
		extends
		AbstractReferenceManufacturer<T, CDOMTransparentSingleRef<T>, CDOMTransparentTypeRef<T>, CDOMTransparentAllRef<T>>
		implements ReferenceManufacturer<T, CDOMTransparentSingleRef<T>>,
		Cloneable
{
	public TransparentReferenceManufacturer(Class<T> cl)
	{
		super(cl);
	}

	@Override
	protected CDOMTransparentSingleRef<T> getLocalReference(String val)
	{
		return new CDOMTransparentSingleRef<T>(getCDOMClass(), val);
	}

	@Override
	protected CDOMTransparentTypeRef<T> getLocalTypeReference(String[] val)
	{
		return new CDOMTransparentTypeRef<T>(getCDOMClass(), val);
	}

	@Override
	protected CDOMTransparentAllRef<T> getLocalAllReference()
	{
		return new CDOMTransparentAllRef<T>(getCDOMClass());
	}

	public void resolveUsing(ReferenceManufacturer<T, ?> rm)
	{
		CDOMTransparentAllRef<T> all = getAllRef();
		if (all != null)
		{
			all.resolve(rm);
		}
		for (CDOMTransparentTypeRef<T> ref : getTypeReferences())
		{
			ref.resolve(rm);
		}
		for (CDOMTransparentSingleRef<T> ref : getReferenced())
		{
			ref.resolve(rm);
		}
		injectConstructed(rm);
	}
}