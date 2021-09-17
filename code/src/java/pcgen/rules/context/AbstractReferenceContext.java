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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.format.dice.DiceFormat;
import pcgen.base.formatmanager.ArrayFormatFactory;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.SortKeyRequired;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.format.table.ColumnFormatFactory;
import pcgen.cdom.format.table.DataTable;
import pcgen.cdom.format.table.TableColumn;
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
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.SubClass;
import pcgen.output.channel.compat.HandedCompat;
import pcgen.util.Logging;

/**
 * An AbstractReferenceContext is responsible for dealing with References during load of a
 * PCGen dataset from LST files.
 * 
 * Most of the function relates to 3 areas: (1) Managing the ReferenceManagers that
 * actually hold the references to CDOMObjects (Skills, Languages, etc.) (2) Managing the
 * overall loading process of the managers (e.g. resolving those references once load is
 * complete) (3) Managing Formats available in the data
 * 
 * References are necessary because we need to be able to parse data that, for example,
 * provides: AUTO:LANGUAGE|Draconic ... and we may or may not have loaded Language files.
 * It would be impossible to load everything in order (there will be circular references).
 * The result is that the loading system always refers to objects indirectly when they are
 * referenced by a Token like AUTO:LANGUAGE. This indirect reference is a "CDOMReference"
 * (and may refer to one or more CDOMObjects - groups are allowed). Once the load is
 * complete, the references can be resolved to their actual underlying objects and any
 * references that were made where an object of that name does not exist can be
 * identified.
 */
public abstract class AbstractReferenceContext
{

	@SuppressWarnings("rawtypes")
	private static final Class<Categorized> CATEGORIZED_CLASS = Categorized.class;
	private static final Class<DomainSpellList> DOMAINSPELLLIST_CLASS = DomainSpellList.class;
	private static final Class<ClassSkillList> CLASSSKILLLIST_CLASS = ClassSkillList.class;
	private static final Class<ClassSpellList> CLASSSPELLLIST_CLASS = ClassSpellList.class;
	private static final Class<DataTable> DATA_TABLE_CLASS = DataTable.class;
	private static final Class<TableColumn> TABLE_COLUMN_CLASS = TableColumn.class;

	private final DoubleKeyMap<Class<?>, Object, WeakReference<List<?>>> sortedMap = new DoubleKeyMap<>();

	private URI sourceURI;

	private URI extractURI;

	private final SimpleFormatManagerLibrary fmtLibrary = new SimpleFormatManagerLibrary();

	public void initialize()
	{
		FormatUtilities.loadDefaultFormats(fmtLibrary);
		fmtLibrary.addFormatManagerBuilder(new ArrayFormatFactory('\n', ','));
		fmtLibrary.addFormatManager(new DiceFormat());
		fmtLibrary.addFormatManager(HandedCompat.HANDED_MANAGER);
		fmtLibrary.addFormatManagerBuilder(
			new ColumnFormatFactory(this.getManufacturer(AbstractReferenceContext.TABLE_COLUMN_CLASS)));
		fmtLibrary.addFormatManagerBuilder(
			new TableFormatFactory(this.getManufacturer(AbstractReferenceContext.DATA_TABLE_CLASS)));
	}

	public abstract <T extends Loadable> ReferenceManufacturer<T> getManufacturer(Class<T> cl);

	/**
	 * Returns true if this AbstractReferenceContext has a Manufacturer for the given
	 * ClassIdentity.
	 * 
	 * @param classIdentity
	 *            The ClassIdentity to be checked to see if this AbstractReferenceContext
	 *            has a Manufacturer for the ClassIdentity
	 * @return true if this AbstractReferenceContext has a Manufacturer for the given
	 *         ClassIdentity; false otherwise
	 */
	public abstract <T extends Loadable> boolean hasManufacturer(ClassIdentity<T> classIdentity);

	protected abstract <T extends Loadable> ReferenceManufacturer<T> constructReferenceManufacturer(
		ClassIdentity<T> identity);

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

	public <T extends Loadable> CDOMGroupRef<T> getCDOMTypeReference(Class<T> c, String... val)
	{
		return getManufacturer(c).getTypeReference(val);
	}

	public <T extends Loadable> T constructCDOMObject(Class<T> c, String val)
	{
		T obj;
		if (CATEGORIZED_CLASS.isAssignableFrom(c))
		{
			throw new UnsupportedOperationException("Categorized can't be built directly with null category");
		}
		else
		{
			obj = getManufacturer(c).constructObject(val);
		}
		obj.setSourceURI(sourceURI);
		return obj;
	}

	public <T extends Loadable> void constructIfNecessary(Class<T> cl, String value)
	{
		getManufacturer(cl).constructIfNecessary(value);
	}

	public <T extends Loadable> CDOMSingleRef<T> getCDOMReference(Class<T> c, String val)
	{
		return getManufacturer(c).getReference(val);
	}

	public <T extends Loadable> void reassociateKey(String key, T obj)
	{
		@SuppressWarnings("unchecked")
		ClassIdentity<T> identity = (ClassIdentity<T>) obj.getClassIdentity();
		getManufacturerId(identity).renameObject(key, obj);
	}

	public <T extends Loadable> T get(Class<T> c, String val)
	{
		return silentlyGetConstructedCDOMObject(c, val);
	}

	public <T extends Loadable> Indirect<T> getIndirect(Class<T> c, String val)
	{
		return getCDOMReference(c, val);
	}

	public <T extends Loadable> T silentlyGetConstructedCDOMObject(Class<T> c, String val)
	{
		return getManufacturer(c).getActiveObject(val);
	}

	@SuppressWarnings("unchecked")
	protected <T> Class<T> getGenericClass(T obj)
	{
		return (Class<T>) obj.getClass();
	}

	public <T extends Loadable> void importObject(T orig)
	{
		/*
		 * Assume a class will behave well and return its own identity. This is made to
		 * avoid having to have Loadable<T>
		 */
		@SuppressWarnings("unchecked")
		ClassIdentity<T> identity = (ClassIdentity<T>) orig.getClassIdentity();
		ReferenceManufacturer<T> mfg = getManufacturerId(identity);
		mfg.addObject(orig, orig.getKeyName());
	}

	public <T extends Loadable> boolean forget(T obj)
	{
		@SuppressWarnings("unchecked")
		ClassIdentity<T> identity = (ClassIdentity<T>) obj.getClassIdentity();
		if (hasManufacturer(identity))
		{
			return getManufacturerId(identity).forgetObject(obj);
		}
		return false;
	}

	public <T extends Loadable> Collection<T> getConstructedCDOMObjects(Class<T> c)
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

	public <T extends Loadable> boolean containsConstructedCDOMObject(Class<T> c, String s)
	{
		return getManufacturer(c).containsObjectKeyed(s);
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
				boolean needSelf = pcc.getSafe(ObjectKey.ALLOWBASECLASS);
				for (SubClass subcl : pcc.getListFor(ListKey.SUB_CLASS))
				{
					String subKey = subcl.getKeyName();
					if (subKey.equalsIgnoreCase(key))
					{
						//Now an error to explicitly create this match, see CODE-1928
						Logging.errorPrint("Cannot explicitly create a SUBCLASS that matches the parent class.  "
							+ "Use ALLOWBASECLASS.  " + "Tokens on the offending SUBCLASS line will be ignored");
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
					SubClass self = cat.newInstance();
					self.setKeyName(key);
					importObject(self);
				}
			}
		}
	}

	/**
	 * Returns a CDOMSingleRef for the given Object.
	 * 
	 * If possible an internal reference to the object will be returned; otherwise a
	 * direct reference may be returned. This possible use of a direct reference allows
	 * this method to be used before OR after reference resolution.
	 * 
	 * @param obj
	 *            The object for which a CDOMSingleRef should be returned
	 * @return A CDOMSingleRef for the given Object
	 */
	public <T extends Loadable> CDOMSingleRef<T> getCDOMDirectReference(T obj)
	{
		return new CDOMDirectSingleRef<>(obj);
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

	private <T extends Loadable> boolean processResolution(UnconstructedValidator validator,
		ReferenceManufacturer<T> rs)
	{
		ManufacturableFactory<T> factory = rs.getFactory();
		ManufacturableFactory<T> parent = factory.getParent();
		ReferenceManufacturer<T> manufacturer = (parent == null) ? null : getManufacturerFac(parent);
		return factory.populate(manufacturer, rs, validator) && rs.resolveReferences(validator);
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

	/**
	 * Returns the ReferenceManufacturer for the given ClassIdentity.
	 * 
	 * @param classIdentity
	 *            The ClassIdentity for which the ReferenceManufacturer should be returned
	 * @return The ReferenceManufacturer for the given ClassIdentity
	 */
	public abstract <T extends Loadable> ReferenceManufacturer<T> getManufacturerId(ClassIdentity<T> classIdentity);

	public <T extends CDOMObject> List<T> getSortedList(Class<T> cl, IntegerKey key)
	{
		List<T> returnList;
		WeakReference<List<?>> wr = sortedMap.get(cl, key);
		if ((wr == null) || ((returnList = (List<T>) wr.get()) == null))
		{
			returnList = generateList(cl, Comparator.comparing(o -> o.getSafe(key)));
			sortedMap.put(cl, key, new WeakReference<>(returnList));
		}
		return Collections.unmodifiableList(returnList);
	}

	public <T extends CDOMObject> List<T> getSortOrderedList(Class<T> cl)
	{
		List<T> returnList;
		Comparator<CDOMObject> comp = CDOMObject.P_OBJECT_NAME_COMP;
		//We arbitrarily use the sort order comparator as the second key
		WeakReference<List<?>> wr = sortedMap.get(cl, comp);
		if ((wr == null) || ((returnList = (List<T>) wr.get()) == null))
		{
			returnList = generateList(cl, comp);
			sortedMap.put(cl, comp, new WeakReference<>(returnList));
		}
		return Collections.unmodifiableList(returnList);
	}

	private <T extends CDOMObject> List<T> generateList(Class<T> cl, Comparator<? super T> comp)
	{
		Set<T> tm = new TreeSet<>(comp);
		tm.addAll(getConstructedCDOMObjects(cl));
		return new ArrayList<>(tm);
	}

	/**
	 * Returns the ReferenceManufacturer for the given ManufacturableFactory.
	 * 
	 * Note: Use of this method should be avoided if possible. getManufacturerId is
	 * preferred; this method is only present for current backward compatibility to how
	 * parent/child ability categories function, and will be removed when it is practical
	 * to do so.
	 * 
	 * @param factory
	 *            The ManufacturableFactory for which the ReferenceManufacturer should be
	 *            returned
	 * @return The ReferenceManufacturer for the given ManufacturableFactory
	 */
	public abstract <T extends Loadable> ReferenceManufacturer<T> getManufacturerFac(ManufacturableFactory<T> factory);

	abstract <T extends CDOMObject> T performCopy(T object, String copyName);

	public abstract <T extends CDOMObject> T performMod(T obj);

	public FormatManager<?> getFormatManager(String clName)
	{
		return fmtLibrary.getFormatManager(clName);
	}

	void importCDOMToFormat(Class<? extends Loadable> cl)
	{
		fmtLibrary.addFormatManager(getManufacturer(cl));
	}

	/**
	 * Returns a sorted list of items, as sorted by the sort key.
	 * 
	 * @param cl
	 *            The Class of object to return
	 * @return The List of items, sorted by their sort key
	 */
	public <T extends SortKeyRequired & Loadable> List<T> getSortkeySortedCDOMObjects(Class<T> cl)
	{
		List<T> items = new ArrayList<>(getConstructedCDOMObjects(cl));
		items.sort(Comparator.comparing(SortKeyRequired::getSortKey));
		return items;
	}

	/**
	 * Returns the ReferenceManufacturer for a given Format name and class.
	 * 
	 * @param formatName
	 *            The (persistent) name of the format for which the ReferenceManufacturer
	 *            should be returned
	 * @param cl
	 *            The class, indicating additional information about the
	 *            ReferenceManufacturer to be returned
	 * @return The ReferenceManufacturer for a given Format name and class
	 */
	public abstract <T extends Loadable> ReferenceManufacturer<T> getManufacturerByFormatName(String formatName,
		Class<T> cl);
}
