/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.context;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.UnconstructedValidator;

public interface ReferenceContext
{
	public <T extends Loadable> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl);

	public <T extends Loadable> ReferenceManufacturer<T> getManufacturer(
		ClassIdentity<T> cl);

	public <T extends Loadable & CategorizedCDOMObject<T>> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl, Class<? extends Category<T>> catClass, String category);

	public <T extends Loadable & CategorizedCDOMObject<T>> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl, Category<T> cat);

	public <T extends Loadable> ReferenceManufacturer<T> getManufacturer(
			ManufacturableFactory<T> factory);

	public Collection<? extends ReferenceManufacturer<?>> getAllManufacturers();

	public <T extends Loadable> T constructCDOMObject(Class<T> c, String val);

	public <T extends Loadable> boolean containsConstructedCDOMObject(
			Class<T> c, String s);

	public <T extends Loadable> T constructNowIfNecessary(Class<T> cl,
			String name);

	public <T extends Loadable> void constructIfNecessary(Class<T> cl,
			String value);

	public <T extends Loadable> void importObject(T orig);

	public <T extends Loadable> CDOMSingleRef<T> getCDOMReference(Class<T> c,
			String val);

	public <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMSingleRef<T> getCDOMReference(
			Class<T> c, Category<T> cat, String val);

	public <T extends Loadable> CDOMGroupRef<T> getCDOMTypeReference(
			Class<T> c, String... val);

	public <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMGroupRef<T> getCDOMTypeReference(
			Class<T> c, Category<T> cat, String... val);

	public <T extends Loadable> CDOMGroupRef<T> getCDOMAllReference(Class<T> c);

	public <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMGroupRef<T> getCDOMAllReference(
			Class<T> c, Category<T> cat);

	public <T extends Loadable> T silentlyGetConstructedCDOMObject(
			Class<T> c, String val);

	public <T extends CDOMObject & CategorizedCDOMObject<T>> T silentlyGetConstructedCDOMObject(
			Class<T> c, Category<T> cat, String val);

	public <T extends CDOMObject & CategorizedCDOMObject<T>> void reassociateCategory(
			Category<T> cat, T obj);

	public <T extends Loadable> void reassociateKey(String key, T obj);

	public <T extends Loadable> boolean forget(T obj);

	public <T extends Loadable> Collection<T> getConstructedCDOMObjects(
			Class<T> c);

	public <T extends CDOMObject> List<T> getOrderSortedCDOMObjects(Class<T> c);

	public Set<Object> getAllConstructedObjects();

	public <T extends CDOMObject> CDOMSingleRef<T> getCDOMDirectReference(T obj);

	public void registerAbbreviation(CDOMObject obj, String value);

	public String getAbbreviation(CDOMObject obj);

	public <T> T getAbbreviatedObject(Class<T> cl, String value);

	public URI getExtractURI();

	public void setExtractURI(URI extractURI);

	public URI getSourceURI();

	public void setSourceURI(URI sourceURI);

	public boolean resolveReferences(UnconstructedValidator validator);

	public void buildDerivedObjects();

	public void buildDeferredObjects();

	public boolean validate(UnconstructedValidator validator);

	public <T extends CDOMObject> T performCopy(T obj, String copyName);

	public <T extends CDOMObject> T performMod(T obj);

	public <T extends Loadable> int getConstructedObjectCount(Class<T> class1);

	public void copyAbbreviationsFrom(AbstractReferenceContext rc);

	public <T extends Loadable> T getItemInOrder(Class<T> cl, int item);
}