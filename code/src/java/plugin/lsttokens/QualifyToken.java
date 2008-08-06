package plugin.lsttokens;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.Qualifier;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.reference.CategorizedCDOMReference;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;
import pcgen.core.spell.Spell;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.StringPClassUtil;

/**
 * Deals with the QUALIFY token for Abilities
 */
public class QualifyToken extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "QUALIFY";
	}

	public List<Class<? extends CDOMObject>> getLegalTypes()
	{
		return Arrays.asList(PCClassLevel.class, Ability.class, Deity.class,
			Domain.class, Equipment.class, PCClass.class, Race.class,
			Skill.class, Spell.class, PCTemplate.class, WeaponProf.class);
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (!getLegalTypes().contains(obj.getClass()))
		{
			Logging.errorPrint("Cannot use QUALIFY on a " + obj.getClass());
			return false;
		}
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		if (value.indexOf("|") == -1)
		{
			Logging.errorPrint(getTokenName()
				+ " requires at least two arguments, QualifyType and Key: "
				+ value);
			return false;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		String firstToken = st.nextToken();
		int equalLoc = firstToken.indexOf('=');
		String className;
		String categoryName;
		if (equalLoc != firstToken.lastIndexOf('='))
		{
			Logging.errorPrint("  Error encountered parsing " + getTokenName());
			Logging.errorPrint("  Found second = in QualifyType=Category");
			Logging
				.errorPrint("  Format is: QualifyType[=Category]|Key[|Key] value was: "
					+ value);
			Logging.errorPrint("  Valid QualifyTypes are: "
				+ StringPClassUtil.getValidStrings());
			return false;
		}
		else if (equalLoc == -1)
		{
			className = firstToken;
			categoryName = null;
		}
		else
		{
			className = firstToken.substring(0, equalLoc);
			categoryName = firstToken.substring(equalLoc + 1);
		}
		Class<? extends CDOMObject> c = StringPClassUtil.getClassFor(className);
		ReferenceManufacturer<? extends CDOMObject, ?> rm;
		if (CategorizedCDOMObject.class.isAssignableFrom(c))
		{
			if (categoryName == null)
			{
				Logging.errorPrint("  Error encountered parsing "
					+ getTokenName());
				Logging
					.errorPrint("  Found Categorized Type without =Category");
				Logging
					.errorPrint("  Format is: QualifyType[=Category]|Key[|Key] value was: "
						+ value);
				Logging.errorPrint("  Valid QualifyTypes are: "
					+ StringPClassUtil.getValidStrings());
				return false;
			}
			rm = getReferenceManufacturer(context, (Class) c, categoryName);
			if (rm == null)
			{
				Logging.errorPrint("  Error encountered parsing "
						+ getTokenName());
				Logging.errorPrint("  " + className + " Category: "
						+ categoryName + " not found");
				return false;
			}
		}
		else
		{
			if (categoryName != null)
			{
				Logging.errorPrint("  Error encountered parsing "
					+ getTokenName());
				Logging
					.errorPrint("  Found Non-Categorized Type with =Category");
				Logging
					.errorPrint("  Format is: QualifyType[=Category]|Key[|Key] value was: "
						+ value);
				Logging.errorPrint("  Valid QualifyTypes are: "
					+ StringPClassUtil.getValidStrings());
				return false;
			}
			rm = context.ref.getManufacturer(c);
		}

		while (st.hasMoreTokens())
		{
			CDOMReference<? extends CDOMObject> ref =
					rm.getReference(st.nextToken());
			context.obj.addToList(obj, ListKey.QUALIFY, new Qualifier(rm
				.getReferenceClass(), ref));
		}

		return true;
	}

	private <T extends CDOMObject & CategorizedCDOMObject<T>> ReferenceManufacturer<? extends CDOMObject, ?> getReferenceManufacturer(
		LoadContext context, Class<T> c, String categoryName)
	{
		Category<T> cat = StringPClassUtil.getCategoryFor(c, categoryName);
		if (cat == null)
		{
			return null;
		}
		return context.ref.getManufacturer(c, cat);
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<Qualifier> changes =
				context.getObjectContext().getListChanges(obj, ListKey.QUALIFY);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Collection<Qualifier> quals = changes.getAdded();
		HashMapToList<String, CDOMReference<?>> map =
				new HashMapToList<String, CDOMReference<?>>();
		for (Qualifier qual : quals)
		{
			Class<? extends CDOMObject> cl = qual.getQualifiedClass();
			String s = StringPClassUtil.getStringFor(cl);
			CDOMReference<?> ref = qual.getQualifiedReference();
			String key = s;
			if (ref instanceof CategorizedCDOMReference)
			{
				Category<?> cat =
						((CategorizedCDOMReference<?>) ref).getCDOMCategory();
				key += '=' + cat.toString();
			}
			map.addToListFor(key, ref);
		}
		Set<CDOMReference<?>> set =
				new TreeSet<CDOMReference<?>>(ReferenceUtilities.REFERENCE_SORTER);
		Set<String> returnSet = new TreeSet<String>();
		for (String key : map.getKeySet())
		{
			set.clear();
			set.addAll(map.getListFor(key));
			StringBuilder sb = new StringBuilder();
			sb.append(key).append(Constants.PIPE).append(
				ReferenceUtilities.joinLstFormat(set, Constants.PIPE));
			returnSet.add(sb.toString());
		}
		return returnSet.toArray(new String[returnSet.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
