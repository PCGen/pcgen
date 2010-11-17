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
import pcgen.cdom.base.Identified;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.CategorizedManufacturer;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.UnconstructedValidator;

public interface ReferenceContext
{
	public <T extends Identified> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl);

	public <T extends CDOMObject & CategorizedCDOMObject<T>> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl, String category);

	public <T extends CDOMObject & CategorizedCDOMObject<T>> CategorizedManufacturer<T> getManufacturer(
			Class<T> cl, Category<T> cat);

	public Collection<? extends ReferenceManufacturer<?>> getAllManufacturers();

	public <T extends CDOMObject> T constructCDOMObject(Class<T> c, String val);

	public <T extends CDOMObject> boolean containsConstructedCDOMObject(
			Class<T> c, String s);

	public <T extends Identified> T constructNowIfNecessary(Class<T> cl,
			String name);

	public <T extends CDOMObject> void constructIfNecessary(Class<T> cl,
			String value);

	public <T extends CDOMObject> void importObject(T orig);

	public <T extends Identified> CDOMSingleRef<T> getCDOMReference(Class<T> c,
			String val);

	public <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMSingleRef<T> getCDOMReference(
			Class<T> c, Category<T> cat, String val);

	public <T extends CDOMObject> CDOMGroupRef<T> getCDOMTypeReference(
			Class<T> c, String... val);

	public <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMGroupRef<T> getCDOMTypeReference(
			Class<T> c, Category<T> cat, String... val);

	public <T extends Identified> CDOMGroupRef<T> getCDOMAllReference(Class<T> c);

	public <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMGroupRef<T> getCDOMAllReference(
			Class<T> c, Category<T> cat);

	public <T extends Identified> T silentlyGetConstructedCDOMObject(
			Class<T> c, String val);

	public <T extends CDOMObject & CategorizedCDOMObject<T>> T silentlyGetConstructedCDOMObject(
			Class<T> c, Category<T> cat, String val);

	public <T extends CDOMObject & CategorizedCDOMObject<T>> void reassociateCategory(
			Category<T> cat, T obj);

	public <T extends CDOMObject> void reassociateKey(String key, T obj);

	public <T extends CDOMObject> boolean forget(T obj);

	public <T extends CDOMObject> Collection<T> getConstructedCDOMObjects(
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

	public void resolveReferences();

	public void buildDerivedObjects();

	public void buildDeferredObjects();

	public boolean validate(UnconstructedValidator validator);

	public <T extends CDOMObject & CategorizedCDOMObject<T>> Category<T> getCategoryFor(Class<T> cl, String string);

	public <T extends CDOMObject> T performCopy(T obj, String copyName);

	public <T extends CDOMObject> T performMod(T obj);

	public <T extends CDOMObject> int getConstructedObjectCount(Class<T> class1);

	public void copyAbbreviationsFrom(AbstractReferenceContext rc);

	public <T extends CDOMObject> T getItemInOrder(Class<T> cl, int item);
}