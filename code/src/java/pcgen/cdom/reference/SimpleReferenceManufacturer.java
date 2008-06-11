/**
 * 
 */
package pcgen.cdom.reference;

import pcgen.cdom.base.CDOMObject;

public class SimpleReferenceManufacturer<T extends CDOMObject>
		extends
		AbstractReferenceManufacturer<T, CDOMSimpleSingleRef<T>, CDOMTypeRef<T>, CDOMAllRef<T>>
		implements ReferenceManufacturer<T, CDOMSimpleSingleRef<T>>
{
	public SimpleReferenceManufacturer(Class<T> cl)
	{
		super(cl);
	}

	@Override
	protected CDOMSimpleSingleRef<T> getLocalReference(String val)
	{
		return new CDOMSimpleSingleRef<T>(getCDOMClass(), val);
	}

	@Override
	protected CDOMTypeRef<T> getLocalTypeReference(String[] val)
	{
		return new CDOMTypeRef<T>(getCDOMClass(), val);
	}

	@Override
	protected CDOMAllRef<T> getLocalAllReference()
	{
		return new CDOMAllRef<T>(getCDOMClass());
	}
}