package pcgen.cdom.reference;

import pcgen.cdom.base.PrereqObject;

public interface TransparentReference<T extends PrereqObject>
{
	public void resolve(ReferenceManufacturer<T, ?> rm);
}
