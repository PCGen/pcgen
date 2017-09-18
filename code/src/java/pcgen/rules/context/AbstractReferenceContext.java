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

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.ObjectDatabase;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.CategorizedClassIdentity;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.format.table.ColumnFormatFactory;
import pcgen.cdom.format.table.TableFormatFactory;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.UnconstructedValidator;
import pcgen.cdom.util.IntegerKeyComparator;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.SubClass;
import pcgen.util.Logging;
import pcgen.util.StringPClassUtil;

public abstract class AbstractReferenceContext implements ObjectDatabase
{

	@SuppressWarnings("rawtypes")
	private static final Class<Categorized> CATEGORIZED_CLASS = Categorized.class;
	private static final Class<DomainSpellList> DOMAINSPELLLIST_CLASS = DomainSpellList.class;
	private static final Class<ClassSkillList> CLASSSKILLLIST_CLASS = ClassSkillList.class;
	private static final Class<ClassSpellList> CLASSSPELLLIST_CLASS = ClassSpellList.class;
	private static final Class<SubClass> SUBCLASS_CLASS = SubClass.class;

	private DoubleKeyMap<Class<?>, Object, WeakReference<List<?>>> sortedMap =
            new DoubleKeyMap<>();

	private final Map<CDOMObject, CDOMSingleRef<?>> directRefCache = new HashMap<>();

	private URI sourceURI;
	
	private URI extractURI;
	
	private final SimpleFormatManagerLibrary fmtLibrary = new SimpleFormatManagerLibrary();

	public AbstractReferenceContext()
	{
		FormatUtilities.loadDefaultFormats(fmtLibrary);
		fmtLibrary.addFormatManagerBuilder(new ColumnFormatFactory(this));
		fmtLibrary.addFormatManagerBuilder(new TableFormatFactory(this));
	}

	public abstract <T extends Loadable> ReferenceManufacturer<T> getManufacturer(
		Class<T> cl);
	
	public abstract <T extends Loadable> boolean hasManufacturer(Class<T> cl);
	
	protected abstract <T extends Categorized<T>> boolean hasManufacturer(
		Class<T> cl, Category<T> cat);

	protected final <T extends Loadable> ReferenceManufacturer<T> getNewReferenceManufacturer(
		Class<T> cl)
	{
		return constructReferenceManufacturer(cl);
	}

	protected abstract <T extends Loadable> ReferenceManufacturer<T> constructReferenceManufacturer(
		Class<T> cl);

	/**
	 * Retrieve the Reference manufacturer that handles this class and category. Note that
	 * even though abilities are categorized, the category may not be know initially, so
	 * null cat values are legal.
	 *
	 * @return The reference manufacturer
	 */
	public abstract Collection<? extends ReferenceManufacturer<?>> getAllManufacturers();

	public boolean validate(UnconstructedValidator validator)
	{
		boolean returnGood = true;
		for (ReferenceManufacturer<?> ref : getAllManufacturers())
		{
			returnGood &= ref.validate(validator);
		}
		return returnGood;
	}

	public <T extends Loadable> CDOMGroupRef<T> getCDOMAllReference(Class<T> c)
	{
		return getManufacturer(c).getAllReference();
	}

	public <T extends Categorized<T>> CDOMGroupRef<T> getCDOMAllReference(
		Class<T> c, Category<T> cat)
	{
		return getManufacturer(c, cat).getAllReference();
	}

	public <T extends Loadable> CDOMGroupRef<T> getCDOMTypeReference(
			Class<T> c, String... val)
	{
		return getManufacturer(c).getTypeReference(val);
	}

	public <T extends Categorized<T>> CDOMGroupRef<T> getCDOMTypeReference(
			Class<T> c, Category<T> cat, String... val)
	{
		return getManufacturer(c, cat).getTypeReference(val);
	}

	public <T extends Loadable> T constructCDOMObject(Class<T> c, String val)
	{
		T obj;
		if (CATEGORIZED_CLASS.isAssignableFrom(c))
		{
			Class cl = c;
			obj = (T) getManufacturer(cl, null).constructObject(val);
		}
		else
		{
			obj = getManufacturer(c).constructObject(val);
		}
		obj.setSourceURI(sourceURI);
		return obj;
	}

	public <T extends Loadable> void constructIfNecessary(Class<T> cl,
			String value)
	{
		getManufacturer(cl).constructIfNecessary(value);
	}

	public <T extends Loadable> CDOMSingleRef<T> getCDOMReference(Class<T> c,
			String val)
	{
		return getManufacturer(c).getReference(val);
	}

	public <T extends Categorized<T>> CDOMSingleRef<T> getCDOMReference(
			Class<T> c, Category<T> cat, String val)
	{
		return getManufacturer(c, cat).getReference(val);
	}

	public <T extends Loadable> void reassociateKey(String key, T obj)
	{
		if (CATEGORIZED_CLASS.isAssignableFrom(obj.getClass()))
		{
			Class cl = obj.getClass();
			reassociateCategorizedKey(key, obj, cl);
		}
		else
		{
			getManufacturer((Class<T>) obj.getClass()).renameObject(key, obj);
		}
	}

	private <T extends Categorized<T>> void reassociateCategorizedKey(
			String key, Loadable orig, Class<T> cl)
	{
		T obj = (T) orig;
		getManufacturer(cl, obj.getCDOMCategory()).renameObject(key, obj);
	}

	@Override
	public <T extends Loadable> T get(Class<T> c, String val)
	{
		return silentlyGetConstructedCDOMObject(c, val);
	}

	@Override
	public <T extends Loadable> Indirect<T> getIndirect(Class<T> c, String val)
	{
		return getCDOMReference(c, val);
	}

	public <T extends Loadable> T silentlyGetConstructedCDOMObject(Class<T> c,
		String val)
	{
		return getManufacturer(c).getActiveObject(val);
	}

	public <T extends Categorized<T>> T silentlyGetConstructedCDOMObject(
		Class<T> c, Category<T> cat, String val)
	{
		return getManufacturer(c, cat).getActiveObject(val);
	}

	public <T extends Categorized<T>> void reassociateCategory(Category<T> cat,
		T obj)
	{
		Category<T> oldCat = obj.getCDOMCategory();
		if (oldCat == null && cat == null || oldCat != null
				&& oldCat.equals(cat))
		{
			Logging.errorPrint("Worthless Category change encountered: "
					+ obj.getDisplayName() + " " + oldCat);
		}
		reassociateCategory(getGenericClass(obj), obj, oldCat, cat);
	}

	@SuppressWarnings("unchecked")
	protected <T> Class<T> getGenericClass(T obj)
	{
		return (Class<T>) obj.getClass();
	}

	private <T extends Categorized<T>> void reassociateCategory(
			Class<T> cl, T obj, Category<T> oldCat, Category<T> cat)
	{
		getManufacturer(cl, oldCat).forgetObject(obj);
		obj.setCDOMCategory(cat);
		getManufacturer(cl, cat).addObject(obj, obj.getKeyName());
	}

	public <T extends Loadable> void importObject(T orig)
	{
		if (CATEGORIZED_CLASS.isAssignableFrom(orig.getClass()))
		{
			Class cl = orig.getClass();
			importCategorized(orig, cl);
		}
		else
		{
			getManufacturer((Class<T>) orig.getClass()).addObject(orig,
					orig.getKeyName());
		}
	}

	private <T extends Categorized<T>> void importCategorized(Loadable orig,
		Class<T> cl)
	{
		T obj = (T) orig;
		getManufacturer(cl, obj.getCDOMCategory()).addObject(obj,
				obj.getKeyName());
	}

	public <T extends Loadable> boolean forget(T obj)
	{
		if (CATEGORIZED_CLASS.isAssignableFrom(obj.getClass()))
		{
			Class cl = obj.getClass();
			Categorized cdo = (Categorized) obj;
			if (hasManufacturer(cl, cdo.getCDOMCategory()))
			{
                // Work around a bug in the Eclipse 3.7.0/1 compiler by explicitly extracting a Category<?>
                return getManufacturer(cl, (Category<?>) cdo.getCDOMCategory()).forgetObject(obj);
			}
		}
		else
		{
			if (hasManufacturer(obj.getClass()))
			{
				return getManufacturer((Class<T>) obj.getClass()).forgetObject(
						obj);
			}
		}
		return false;
	}

	public <T extends Loadable> Collection<T> getConstructedCDOMObjects(
			Class<T> c)
	{
		// if (CategorizedCDOMObject.class.isAssignableFrom(c))
		// {
		// return categorized.getAllConstructedCDOMObjects((Class) c);
		// }
		// else
		// {
		return getManufacturer(c).getAllObjects();
		// }
	}

	public <T extends Loadable> List<T> getOrderSortedCDOMObjects(Class<T> c)
	{
		return getManufacturer(c).getOrderSortedObjects();
	}

	public Set<Object> getAllConstructedObjects()
	{
		Set<Object> set = new HashSet<>();
		for (ReferenceManufacturer<?> ref : getAllManufacturers())
		{
			set.addAll(ref.getAllObjects());
		}
		// Collection otherSet = categorized.getAllConstructedCDOMObjects();
		// set.addAll(otherSet);
		return set;
	}

	public <T extends Loadable> boolean containsConstructedCDOMObject(
			Class<T> c, String s)
	{
		return getManufacturer(c).containsObject(s);
	}

	public void buildDerivedObjects()
	{
		Collection<Domain> domains = getConstructedCDOMObjects(Domain.class);
		for (Domain d : domains)
		{
			DomainSpellList dsl = constructCDOMObject(DOMAINSPELLLIST_CLASS, d.getKeyName());
			dsl.addType(Type.DIVINE);
			d.put(ObjectKey.DOMAIN_SPELLLIST, dsl);
		}
		Collection<PCClass> classes = getConstructedCDOMObjects(PCClass.class);
		for (PCClass pcc : classes)
		{
			String key = pcc.getKeyName();
			ClassSkillList skl = constructCDOMObject(CLASSSKILLLIST_CLASS, key);
			boolean isMonster = pcc.isMonster();
			if (isMonster)
			{
				skl.addType(Type.MONSTER);
			}
			pcc.put(ObjectKey.CLASS_SKILLLIST, skl);
			/*
			 * TODO Need to limit which are built to only spellcasters... If you
			 * do that, please see TO-DO in SpellListFacet
			 */
			ClassSpellList csl = constructCDOMObject(CLASSSPELLLIST_CLASS, key);
			FactKey<String> fk = FactKey.valueOf("SpellType");
			String spelltype = pcc.getResolved(fk);
			if (spelltype != null)
			{
				csl.addType(Type.getConstant(spelltype));
			}
			pcc.put(ObjectKey.CLASS_SPELLLIST, csl);
			// simple.constructCDOMObject(SPELLPROGRESSION_CLASS, key);
			// Collection<CDOMSubClass> subclasses = categorized
			// .getConstructedCDOMObjects(SUBCLASS_CLASS, SubClassCategory
			// .getConstant(key));
			// for (CDOMSubClass subcl : subclasses)
			if (pcc.containsListFor(ListKey.SUB_CLASS))
			{
				SubClassCategory cat = SubClassCategory.getConstant(key);
				boolean needSelf = pcc.getSafe(ObjectKey.ALLOWBASECLASS).booleanValue();
				for (SubClass subcl : pcc.getListFor(ListKey.SUB_CLASS))
				{
					String subKey = subcl.getKeyName();
					if (subKey.equalsIgnoreCase(key))
					{
						//Now an error to explicitly create this match, see CODE-1928
						Logging.errorPrint("Cannot explicitly create a SUBCLASS that matches the parent class.  "
								+ "Use ALLOWBASECLASS.  "
								+ "Tokens on the offending SUBCLASS line will be ignored");
						pcc.removeFromListFor(ListKey.SUB_CLASS, subcl);
						continue;
					}
					skl = constructCDOMObject(CLASSSKILLLIST_CLASS, subKey);
					if (isMonster)
					{
						skl.addType(Type.MONSTER);
					}
					subcl.put(ObjectKey.CLASS_SKILLLIST, skl);
					// TODO Need to limit which are built to only
					// spellcasters...
					csl = constructCDOMObject(CLASSSPELLLIST_CLASS, subKey);
					if (spelltype != null)
					{
						csl.addType(Type.getConstant(spelltype));
					}
					subcl.put(ObjectKey.CLASS_SPELLLIST, csl);
					// constructCDOMObject(SPELLPROGRESSION_CLASS, subKey);
					/*
					 * CONSIDER For right now, this is easiest to do here, though
					 * doing this 'live' may be more appropriate in the end.
					 */
					subcl.setCDOMCategory(cat);
					importObject(subcl);
				}
				if (needSelf)
				{
					SubClass self = constructCDOMObject(SUBCLASS_CLASS, key);
					reassociateCategory(SUBCLASS_CLASS, self, null, cat);
				}
			}
		}
	}

	public <T extends CDOMObject> CDOMSingleRef<T> getCDOMDirectReference(T obj)
	{
		@SuppressWarnings("unchecked")
		CDOMSingleRef<T> ref = (CDOMSingleRef<T>) directRefCache.get(obj);
		if (ref == null)
		{
			ref = new CDOMDirectSingleRef<>(obj);
		}
		return ref;
	}

	URI getExtractURI()
	{
		return extractURI;
	}

	void setExtractURI(URI extractURI)
	{
		this.extractURI = extractURI;
	}

	URI getSourceURI()
	{
		return sourceURI;
	}

	void setSourceURI(URI sourceURI)
	{
		this.sourceURI = sourceURI;
	}

	public boolean resolveReferences(UnconstructedValidator validator)
	{
		boolean returnGood = true;
		for (ReferenceManufacturer<?> rs : getAllManufacturers())
		{
			returnGood &= processResolution(validator, rs);
		}
		return returnGood;
	}

	private <T extends Loadable> boolean processResolution(
			UnconstructedValidator validator, ReferenceManufacturer<T> rs)
	{
		ManufacturableFactory<T> factory = rs.getFactory();
		ManufacturableFactory<T> parent = factory.getParent();
		ReferenceManufacturer<T> manufacturer = (parent == null) ? null
				: getManufacturer(parent);
		return factory.populate(manufacturer, rs, validator)
				&& rs.resolveReferences(validator);
	}

	public void buildDeferredObjects()
	{
		for (ReferenceManufacturer<?> rs : getAllManufacturers())
		{
			rs.buildDeferredObjects();
		}
	}

	public <T extends Loadable> T constructNowIfNecessary(Class<T> cl, String name)
	{
		return getManufacturer(cl).constructNowIfNecessary(name);
	}

	public <T extends Loadable> int getConstructedObjectCount(Class<T> c)
	{
		return getManufacturer(c).getConstructedObjectCount();
	}

	public <T extends Loadable> T getItemInOrder(Class<T> cl, int item)
	{
		return getManufacturer(cl).getItemInOrder(item);
	}

	public <T extends Loadable> ReferenceManufacturer<T> getManufacturer(
			ClassIdentity<T> identity)
	{
		Class cl = identity.getChoiceClass();
		if (Categorized.class.isAssignableFrom(cl))
		{
			//Do categorized.
			Category category = ((CategorizedClassIdentity) identity).getCategory();
			return getManufacturer(cl, category);
		}
		else
		{
			return getManufacturer(cl);
		}
	}

	public <T extends CDOMObject> List<T> getSortedList(Class<T> cl,
		IntegerKey key)
	{
		List<T> returnList;
		WeakReference<List<?>> wr = sortedMap.get(cl, key);
		if ((wr == null) || ((returnList = (List<T>) wr.get()) == null))
		{
			returnList = generateList(cl, new IntegerKeyComparator(key));
			sortedMap.put(cl, key, new WeakReference<>(returnList));
		}
		return Collections.unmodifiableList(returnList);
	}

	public <T extends CDOMObject> List<T> getSortOrderedList(Class<T> cl)
	{
		List<T> returnList;
		Comparator<CDOMObject> comp = Globals.pObjectNameComp;
		//We arbitrarily use the sort order comparator as the second key
		WeakReference<List<?>> wr = sortedMap.get(cl, comp);
		if ((wr == null) || ((returnList = (List<T>) wr.get()) == null))
		{
			returnList = generateList(cl, comp);
			sortedMap.put(cl, comp, new WeakReference<>(returnList));
		}
		return Collections.unmodifiableList(returnList);
	}

	private <T extends CDOMObject> List<T> generateList(Class<T> cl,
		Comparator<? super T> comp)
	{
		Set<T> tm = new TreeSet<>(comp);
		tm.addAll(getConstructedCDOMObjects(cl));
		return new ArrayList<>(tm);
	}

	public abstract <T extends Categorized<T>> ReferenceManufacturer<T> getManufacturer(
		Class<T> cl, Category<T> cat);

	public abstract <T extends Loadable> ReferenceManufacturer<T> getManufacturer(
		ManufacturableFactory<T> factory);

	public abstract <T extends Categorized<T>> ReferenceManufacturer<T> getManufacturer(
		Class<T> cl, Class<? extends Category<T>> catClass, String category);

	abstract <T extends CDOMObject> T performCopy(T object, String copyName);

	public abstract <T extends CDOMObject> T performMod(T obj);

	public FormatManager<?> getFormatManager(String clName)
	{
		if ((!fmtLibrary.hasFormatManager(clName))
			&& (StringPClassUtil.getClassForBasic(clName) != null))
		{
			importCDOMToFormat(clName);
		}
		return fmtLibrary.getFormatManager(clName);
	}

	private void importCDOMToFormat(String name)
	{
		Class<? extends Loadable> cl = StringPClassUtil.getClassForBasic(name);
		if (cl == null)
		{
			throw new IllegalArgumentException(
				"Invalid Data Definition Location (no class): " + name);
		}
		ReferenceManufacturer<? extends Loadable> mgr = getManufacturer(cl);
		if (!name.equalsIgnoreCase(mgr.getIdentifierType()))
		{
			throw new IllegalArgumentException("Invalid Data: " + name
				+ " did not return a matching manufacturer: " + mgr.getIdentifierType());
		}
		fmtLibrary.addFormatManager(mgr);
	}
}
