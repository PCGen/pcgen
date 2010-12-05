/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.reference;

import pcgen.cdom.base.Loadable;

/**
 * A SimpleReferenceManufacturer is a ReferenceManufacturer that will construct
 * or reference non-categorized CDOMObjects.
 * 
 * @see pcgen.cdom.reference.ReferenceManufacturer
 * 
 * @param <T>
 *            The Class of object this SimpleReferenceManufacturer can
 *            manufacture
 */
public class SimpleReferenceManufacturer<T extends Loadable>
		extends
		AbstractReferenceManufacturer<T, CDOMSimpleSingleRef<T>, CDOMTypeRef<T>, CDOMAllRef<T>>
		implements ReferenceManufacturer<T>
{
	/**
	 * Constructs a new SimpleReferenceManufacturer that will construct or
	 * reference non-categorized CDOMObjects of the given Class.
	 * 
	 * @param objClass
	 *            The Class of object this AbstractReferenceManufacturer will
	 *            construct and reference.
	 */
	public SimpleReferenceManufacturer(Class<T> objClass)
	{
		super(objClass);
	}

	/**
	 * Returns a CDOMSimpleSingleRef for the given identifier as defined by the
	 * Class provided when this SimpleReferenceManufacturer was constructed.
	 * This is designed to be used ONLY by the AbstractReferenceManufacturer
	 * template Class and should not be called by other objects.
	 * 
	 * @param key
	 *            The identifier for which a SimpleReferenceManufacturer should
	 *            be returned.
	 * @return a CDOMSimpleSingleRef for the given identifier as defined by the
	 *         Class provided when this SimpleReferenceManufacturer was
	 *         constructed.
	 */
	@Override
	protected CDOMSimpleSingleRef<T> getLocalReference(String key)
	{
		return new CDOMSimpleSingleRef<T>(getReferenceClass(), key);
	}

	/**
	 * Returns a CDOMTypeRef for the given types as defined by the Class
	 * provided when this SimpleReferenceManufacturer was constructed. This is
	 * designed to be used ONLY by the AbstractReferenceManufacturer template
	 * Class and should not be called by other objects.
	 * 
	 * @param types
	 *            An array of the types of objects to which the returned
	 *            CDOMReference will refer.
	 * @return A CDOMTypeRef for the given types as defined by the Class
	 *         provided when this SimpleReferenceManufacturer was constructed.
	 */
	@Override
	protected CDOMTypeRef<T> getLocalTypeReference(String[] types)
	{
		return new CDOMTypeRef<T>(getReferenceClass(), types);
	}

	/**
	 * Returns a CDOMAllRef for all objects of the Class provided when this
	 * SimpleReferenceManufacturer was constructed. This is designed to be used
	 * ONLY by the AbstractReferenceManufacturer template Class and should not
	 * be called by other objects.
	 * 
	 * @return A CDOMAllRef for all objects of the Class provided when this
	 *         SimpleReferenceManufacturer was constructed.
	 */
	@Override
	protected CDOMAllRef<T> getLocalAllReference()
	{
		return new CDOMAllRef<T>(getReferenceClass());
	}

	/**
	 * Returns a description of the type of Class this
	 * SimpleReferenceManufacturer constructs or references. This is designed to
	 * be used ONLY by the AbstractReferenceManufacturer template Class and
	 * should not be called by other objects.
	 * 
	 * @return A String description of the Class that this
	 *         SimpleReferenceManufacturer constructs or references.
	 */
	@Override
	protected String getReferenceDescription()
	{
		return getReferenceClass().getSimpleName();
	}

	/**
	 * Returns true if the given String (an identifier) is permitted by the
	 * given UnconstructedValidator. Will always return false if the
	 * UnconstructedValidator is null.
	 * 
	 * @param validator
	 *            The UnconstructedValidator to use to determine if the given
	 *            String (an identifier) should be permitted as an unconstructed
	 *            reference.
	 * @param key
	 *            The identifier to be checked to see if the
	 *            UnconstructedValidator will permit it as an unconstructed
	 *            reference.
	 * @return true if the given String (an identifier) is permitted by the
	 *         given UnconstructedValidator; false otherwise.
	 */
	@Override
	protected boolean validate(UnconstructedValidator validator, String key)
	{
		return validator != null && validator.allow(getReferenceClass(), key);
	}
}