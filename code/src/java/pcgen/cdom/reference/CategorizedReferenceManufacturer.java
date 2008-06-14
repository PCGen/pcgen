/**
 * 
 */
package pcgen.cdom.reference;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;

public class CategorizedReferenceManufacturer<T extends CDOMObject & CategorizedCDOMObject<T>>
		extends
		AbstractReferenceManufacturer<T, CDOMCategorizedSingleRef<T>, CDOMTypeRef<T>, CDOMAllRef<T>>
		implements ReferenceManufacturer<T, CDOMCategorizedSingleRef<T>>
{

	private final Category<T> category;

	public CategorizedReferenceManufacturer(Class<T> cl, Category<T> cat)
	{
		super(cl);
		category = cat;
	}

	@Override
	protected CDOMCategorizedSingleRef<T> getLocalReference(String val)
	{
		return new CDOMCategorizedSingleRef<T>(getCDOMClass(), category, val);
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

	@Override
	public boolean validate()
	{
		return super.validate();
	}
	
	
}