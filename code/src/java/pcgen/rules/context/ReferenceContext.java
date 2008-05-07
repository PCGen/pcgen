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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pcgen.base.util.OneToOneMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSimpleSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;

public class ReferenceContext
{

	private SimpleReferenceContext simple = new SimpleReferenceContext();
//	private CategorizedReferenceContext categorized = new CategorizedReferenceContext();
	private Map<Class<?>, OneToOneMap<CDOMObject, String>> abbMap = new HashMap<Class<?>, OneToOneMap<CDOMObject, String>>();

	public Class<?> getClassFor(String key)
	{
		return null; // return StringPClassUtil.getCDOMClassFor(key);
	}

	public void clear()
	{
		simple.clear();
//		categorized.clear();
	}

	public boolean validate()
	{
		return simple.validate();
		//&& categorized.validate();
	}

//	public <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMGroupRef<T> getCDOMAllReference(
//			Class<T> c, Category<T> cat)
//	{
//		return categorized.getManufacturer(c, cat).getAllReference();
//	}

//	public <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMGroupRef<T> getCDOMTypeReference(
//			Class<T> c, Category<T> cat, String... val)
//	{
//		return categorized.getManufacturer(c, cat).getTypeReference(val);
//	}

	public <T extends CDOMObject> CDOMGroupRef<T> getCDOMAllReference(Class<T> c)
	{
		return simple.getManufacturer(c).getAllReference();
	}

	public <T extends CDOMObject> CDOMGroupRef<T> getCDOMTypeReference(
			Class<T> c, String... val)
	{
		return simple.getManufacturer(c).getTypeReference(val);
	}

	public <T extends CDOMObject> T constructCDOMObject(Class<T> c, String val)
	{
		T obj;
//		if (CategorizedCDOMObject.class.isAssignableFrom(c))
//		{
//			obj = (T) categorized.constructCDOMObject((Class) c, null, val);
//		}
//		else
//		{
			obj =  simple.constructCDOMObject(c, val);
//		}
		obj.put(ObjectKey.SOURCE_URI, sourceURI);
		return obj;
	}

	public <T extends CDOMObject> void constructIfNecessary(Class<T> cl,
			String value)
	{
		simple.constructIfNecessary(cl, value);
	}

	public <T extends CDOMObject> CDOMSingleRef<T> getCDOMReference(Class<T> c,
			String val)
	{
		return simple.getCDOMReference(c, val);
	}

	public <T extends CDOMObject> void reassociateKey(String value, T obj)
	{
//		if (CategorizedCDOMObject.class.isAssignableFrom(obj.getClass()))
//		{
//			categorized.reassociateKey(obj, value);
//		}
//		else
//		{
			simple.reassociateKey(obj, value);
//		}
	}

	public <T extends CDOMObject> T silentlyGetConstructedCDOMObject(
			Class<T> c, String val)
	{
		return simple.silentlyGetConstructedCDOMObject(c, val);
	}

//	public <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMSingleRef<T> getCDOMReference(
//			Class<T> c, Category<T> cat, String val)
//	{
//		return categorized.getCDOMReference(c, cat, val);
//	}
//
//	public <T extends CDOMObject & CategorizedCDOMObject<T>> void reassociateCategory(
//			Category<T> cat, T obj)
//	{
//		categorized.reassociateCategory(cat, obj);
//	}

	// public <T extends CDOMObject> T cloneConstructedCDOMObject(T orig,
	// String newKey)
	// {
	// Class cl = (Class) orig.getClass();
	// if (CategorizedCDOMObject.class.isAssignableFrom(cl))
	// {
	// return (T) cloneCategorized(cl, ((CategorizedCDOMObject) orig)
	// .getCDOMCategory(), orig, newKey);
	// }
	// else
	// {
	// return (T) simple.cloneConstructedCDOMObject(cl, orig, newKey);
	// }
	// }

	public <T extends CDOMObject> void importObject(T orig)
	{
//		if (CategorizedCDOMObject.class.isAssignableFrom(orig.getClass()))
//		{
//			throw new IllegalArgumentException();
//		}
//		else
//		{
			simple.importObject(orig);
//		}
	}

	// public <T extends CDOMObject & CategorizedCDOMObject<T>> T
	// cloneCategorized(
	// Class<T> cl, Category<T> cat, Object o, String newKey)
	// {
	// return categorized.cloneConstructedCDOMObject(cl, cat, (T) o, newKey);
	// }

	public <T extends CDOMObject> ReferenceManufacturer<T, CDOMSimpleSingleRef<T>> getReferenceManufacturer(
			Class<T> c)
	{
		return simple.getManufacturer(c);
	}

//	public <T extends CDOMObject & CategorizedCDOMObject<T>> ReferenceManufacturer<T, CDOMCategorizedSingleRef<T>> getReferenceManufacturer(
//			Class<T> c, Category<T> cat)
//	{
//		return categorized.getManufacturer(c, cat);
//	}

	public <T extends CDOMObject> Collection<T> getConstructedCDOMObjects(
			Class<T> c)
	{
//		if (CategorizedCDOMObject.class.isAssignableFrom(c))
//		{
//			return categorized.getAllConstructedCDOMObjects((Class) c);
//		}
//		else
//		{
			return simple.getConstructedCDOMObjects(c);
//		}
	}

//	public <T extends CDOMObject & CategorizedCDOMObject<T>> Collection<T> getConstructedCDOMObjects(
//			Class<T> c, Category<T> cat)
//	{
//		return categorized.getConstructedCDOMObjects(c, cat);
//	}

	public Set<CDOMObject> getAllConstructedObjects()
	{
		Set<CDOMObject> set = new HashSet<CDOMObject>();
		set.addAll(simple.getAllConstructedCDOMObjects());
//		Collection otherSet = categorized.getAllConstructedCDOMObjects();
//		set.addAll(otherSet);
		return set;
	}

	public <T extends CDOMObject> boolean containsConstructedCDOMObject(
			Class<T> c, String s)
	{
		return simple.containsConstructedCDOMObject(c, s);
	}

	public void buildDerivedObjects()
	{
//		Collection<CDOMDomain> domains = simple
//				.getConstructedCDOMObjects(CDOMDomain.class);
//		for (CDOMDomain d : domains)
//		{
//			simple.constructCDOMObject(DOMAINSPELLLIST_CLASS, d.getKeyName());
//		}
//		Collection<CDOMPCClass> classes = simple
//				.getConstructedCDOMObjects(CDOMPCClass.class);
//		for (CDOMPCClass pcc : classes)
//		{
//			String key = pcc.getKeyName();
//			simple.constructCDOMObject(CLASSSKILLLIST_CLASS, key);
//			// TODO Need to limit which are built to only spellcasters...
//			simple.constructCDOMObject(CLASSSPELLLIST_CLASS, key);
//			simple.constructCDOMObject(SPELLPROGRESSION_CLASS, key);
//			Collection<CDOMSubClass> subclasses = categorized
//					.getConstructedCDOMObjects(SUBCLASS_CLASS, SubClassCategory
//							.getConstant(key));
//			for (CDOMSubClass subcl : subclasses)
//			{
//				String subKey = subcl.getKeyName();
//				simple.constructCDOMObject(CLASSSKILLLIST_CLASS, subKey);
//				// TODO Need to limit which are built to only
//				// spellcasters...
//				simple.constructCDOMObject(CLASSSPELLLIST_CLASS, subKey);
//				simple.constructCDOMObject(SPELLPROGRESSION_CLASS, subKey);
//			}
//		}
	}

//	public <T extends CDOMObject> CDOMAddressedSingleRef<T> getCDOMAddressedReference(
//			CDOMObject obj, Class<T> name, String string)
//	{
//		return simple.getAddressedReference(obj, name, string);
//	}

	private HashMap<CDOMObject, CDOMSingleRef<?>> directRef = new HashMap<CDOMObject, CDOMSingleRef<?>>();

	public <T extends CDOMObject> CDOMSingleRef<T> getCDOMDirectReference(T obj)
	{
		CDOMSingleRef<?> ref = directRef.get(obj);
		if (ref == null)
		{
			ref = new CDOMDirectSingleRef<T>(obj);
		}
		return (CDOMSingleRef<T>) ref;
	}

	public void registerAbbreviation(CDOMObject obj, String value)
	{
		OneToOneMap<CDOMObject, String> map = abbMap.get(obj.getClass());
		if (map == null)
		{
			map = new OneToOneMap<CDOMObject, String>();
			abbMap.put(obj.getClass(), map);
		}
		map.put(obj, value);
		obj.put(StringKey.ABB, value);
	}

	public String getAbbreviation(CDOMObject obj)
	{
		OneToOneMap<CDOMObject, String> map = abbMap.get(obj.getClass());
		return map == null ? null : map.get(obj);
	}

	public <T> T getAbbreviatedObject(Class<T> cl, String value)
	{
		OneToOneMap<T, String> map = (OneToOneMap<T, String>) abbMap.get(cl);
		return map == null ? null : map.getKeyFor(value);
	}
	
	private URI sourceURI;

	private URI extractURI;

	public URI getExtractURI()
	{
		return extractURI;
	}

	public void setExtractURI(URI extractURI)
	{
		this.extractURI = extractURI;
	}

	public URI getSourceURI()
	{
		return sourceURI;
	}

	public void setSourceURI(URI sourceURI)
	{
		this.sourceURI = sourceURI;
	}
}
