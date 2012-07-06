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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.ObjectCache;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.cdom.reference.UnconstructedValidator;
import pcgen.core.Campaign;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.WeaponProf;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.ParsingSeparator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.rules.persistence.ChoiceSetLoadUtilities;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.TokenSupport;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.rules.persistence.token.PostDeferredToken;
import pcgen.util.Logging;
import pcgen.util.StringPClassUtil;

public abstract class LoadContext
{

	private static final Class<String> STRING_CLASS = String.class;

	public final AbstractListContext list;

	public final AbstractObjectContext obj;

	public final ReferenceContext ref;
	
	private final List<Campaign> campaignList = new ArrayList<Campaign>();

	public LoadContext(ReferenceContext rc, AbstractListContext lc, AbstractObjectContext oc)
	{
		if (rc == null)
		{
			throw new IllegalArgumentException("ReferenceContext cannot be null");
		}
		if (lc == null)
		{
			throw new IllegalArgumentException("ListContext cannot be null");
		}
		if (oc == null)
		{
			throw new IllegalArgumentException("ObjectContext cannot be null");
		}
		ref = rc;
		list = lc;
		obj = oc;
	}

	private int writeMessageCount = 0;

	public void addWriteMessage(String string)
	{
		Logging.errorPrint("!!" + string);
		/*
		 * TODO Need to find a better solution for what happens during write...
		 */
		writeMessageCount++;
	}

	public int getWriteMessageCount()
	{
		return writeMessageCount;
	}

	/**
	 * Sets the extract URI. This is a shortcut for setting the URI on both the
	 * graph and obj members.
	 * 
	 * @param extractURI
	 */
	public void setExtractURI(URI extractURI)
	{
		getObjectContext().setExtractURI(extractURI);
		ref.setExtractURI(extractURI);
		getListContext().setExtractURI(extractURI);
	}

	/**
	 * Sets the source URI. This is a shortcut for setting the URI on both the
	 * graph and obj members.
	 * 
	 * @param sourceURI
	 */
	public void setSourceURI(URI sourceURI)
	{
		this.sourceURI = sourceURI;
		getObjectContext().setSourceURI(sourceURI);
		ref.setSourceURI(sourceURI);
		getListContext().setSourceURI(sourceURI);
		clearStatefulInformation();
	}

	/*
	 * Get the type of context we're running in (either Editor or Runtime)
	 */
	public abstract String getContextType();

	public AbstractObjectContext getObjectContext()
	{
		return obj;
	}

	public AbstractListContext getListContext()
	{
		return list;
	}

	public void commit()
	{
		getListContext().commit();
		getObjectContext().commit();
	}

	public void rollback()
	{
		getListContext().rollback();
		getObjectContext().rollback();
	}

	public void resolveReferences(UnconstructedValidator validator)
	{
		ref.resolveReferences(validator);
	}

	public void resolveDeferredTokens()
	{
		for (DeferredToken<? extends Loadable> token : TokenLibrary
				.getDeferredTokens())
		{
			processRes(token);
		}
		commit();
	}

	private <T extends Loadable> void processRes(DeferredToken<T> token)
	{
		Class<T> cl = token.getDeferredTokenClass();
		Collection<? extends ReferenceManufacturer> mfgs = ref
				.getAllManufacturers();
		for (ReferenceManufacturer<? extends T> rm : mfgs)
		{
			if (cl.isAssignableFrom(rm.getReferenceClass()))
			{
				for (T po : rm.getAllObjects())
				{
					token.process(this, po);
				}
			}
		}
	}

	public void resolvePostDeferredTokens()
	{
		Collection<? extends ReferenceManufacturer> mfgs = ref
				.getAllManufacturers();
		for (PostDeferredToken<? extends Loadable> token : TokenLibrary
				.getPostDeferredTokens())
		{
			processPostRes(token, mfgs);
		}
	}

	private <T extends Loadable> void processPostRes(PostDeferredToken<T> token,
			Collection<? extends ReferenceManufacturer> mfgs)
	{
		Class<T> cl = token.getDeferredTokenClass();
		for (ReferenceManufacturer<? extends T> rm : mfgs)
		{
			if (cl.isAssignableFrom(rm.getReferenceClass()))
			{
				for (T po : rm.getAllObjects())
				{
					token.process(this, po);
				}
			}
		}
	}

	private final TokenSupport support = new TokenSupport();

	public <T extends CDOMObject> PrimitiveCollection<T> getChoiceSet(
			SelectionCreator<T> sc, String value)
	{
		try
		{
			return ChoiceSetLoadUtilities.getChoiceSet(this, sc, value);
		}
		catch (ParsingSeparator.GroupingMismatchException e)
		{
			Logging.errorPrint("Group Mismatch in getting ChoiceSet: "
					+ e.getMessage());
			return null;
		}
	}

	public <T extends CDOMObject> PrimitiveCollection<T> getPrimitiveChoiceFilter(
			SelectionCreator<T> sc, String key)
	{
		return ChoiceSetLoadUtilities.getPrimitive(this, sc, key);
	}
			

	public <T> ParseResult processSubToken(T cdo, String tokenName,
			String key, String value)
	{
		return support.processSubToken(this, cdo, tokenName, key, value);
	}

	public <T extends Loadable> boolean processToken(T derivative,
			String typeStr, String argument) throws PersistenceLayerException
	{
		return support.processToken(this, derivative, typeStr, argument);
	}
	
	public <T extends CDOMObject> void unconditionallyProcess(T cdo, String key, String value)
	{
		try
		{
			if (processToken(cdo, key, value))
			{
				commit();
			}
			else
			{
				rollback();
				Logging.replayParsedMessages();
			}
			Logging.clearParseMessages();
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint("Error in token parse: "
					+ e.getLocalizedMessage());
		}
	}

	/**
	 * Produce the LST code for any occurrences of the token. An attempt to 
	 * unparse an invalid or non-existent token will result in an 
	 * IllegalArgumentError.
	 *  
	 * @param <T> The type of object to be processed, generally a CDOMObject.
	 * @param cdo The object to be partially unparsed
	 * @param tokenName The name of the token to be extracted, must be a primary token.
	 * @return An array of LST code 'fields' being each occurrence of the token for the target object.
	 */
	public <T> String[] unparseToken(T cdo, String tokenName)
	{
		return support.unparseToken(this, cdo, tokenName);
	}

	/**
	 * Produce the LST code for any occurrences of subtokens of the parent token.
	 *  
	 * @param <T> The type of object to be processed, generally a CDOMObject.
	 * @param cdo The object to be partially unparsed
	 * @param tokenName The name of the parent token
	 * @return An array of LST code 'fields' all of which are subtokens of the parent token.
	 */
	public <T> String[] unparseSubtoken(T cdo, String tokenName)
	{
		return support.unparseSubtoken(this, cdo, tokenName);
	}

	public <T> Collection<String> unparse(T cdo)
	{
		return support.unparse(this, cdo);
	}

//	public <T extends CDOMObject> PrimitiveChoiceSet<?> getChoiceSet(
//			CDOMObject cdo, String key, String val)
//			throws PersistenceLayerException
//	{
//		return support.getChoiceSet(this, cdo, key, val);
//	}

	public <T extends CDOMObject> T cloneConstructedCDOMObject(T cdo, String newName)
	{
		T newObj = obj.cloneConstructedCDOMObject(cdo, newName);
		ref.importObject(newObj);
		return newObj;
	}

	/**
	 * Create a copy of a CDOMObject duplicating any references to the old 
	 * object. (e.g. Spell, Domain etc)
	 *  
	 * @param cdo The original object being copied. 
	 * @param newName The name that should be given to the new object.
	 * @return The newly created CDOMObject.
	 */
	@SuppressWarnings("unchecked")
	public <T extends CDOMObject> T cloneInMasterLists(T cdo, String newName)
	{
		T newObj;
		try
		{
			newObj = (T) cdo.clone();
			newObj.setName(newName);
			list.cloneInMasterLists(cdo, newObj);
		}
		catch (CloneNotSupportedException e)
		{
			Logging.errorPrint("Failed to clone " + cdo, e);
			e.printStackTrace();
			return null;
		}
		return newObj;
	}
	
	private static final PrerequisiteWriter PREREQ_WRITER =
			new PrerequisiteWriter();

	public String getPrerequisiteString(Collection<Prerequisite> prereqs)
	{
		try
		{
			return PREREQ_WRITER.getPrerequisiteString(prereqs);
		}
		catch (PersistenceLayerException e)
		{
			addWriteMessage("Error writing Prerequisite: " + e);
			return null;
		}
	}

	public Map<Class<?>, Set<String>> typeMap = new HashMap<Class<?>, Set<String>>();
	
	public void buildTypeLists()
	{
		Set<String> typeSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		typeMap.put(WeaponProf.class, typeSet);
		for (WeaponProf wp : ref.getConstructedCDOMObjects(WeaponProf.class))
		{
			for (Type t : wp.getTrueTypeList(false))
			{
				typeSet.add(t.toString());
			}
		}
		typeSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		typeMap.put(Equipment.class, typeSet);
		for (Equipment e : ref.getConstructedCDOMObjects(Equipment.class))
		{
			for (Type t : e.getTrueTypeList(false))
			{
				typeSet.add(t.toString());
			}
		}
	}
	
	public Collection<String> getTypes(Class<?> cl)
	{
		Set<String> returnSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		Set<String> set = typeMap.get(cl);
		if (set != null)
		{
			returnSet.addAll(set);
		}
		return returnSet;
	}
	
	public boolean containsType(Class<?> cl, String type)
	{
		Set<String> set = typeMap.get(cl);
		return set != null && set.contains(type);
	}

	private URI sourceURI;

	public CampaignSourceEntry getCampaignSourceEntry(Campaign source, String value)
	{
		return CampaignSourceEntry.getNewCSE(source, sourceURI, value);
	}

	private CDOMObject stateful;

	public void clearStatefulInformation()
	{
		stateful = null;
	}

	public boolean addStatefulToken(String s) throws PersistenceLayerException
	{
		int colonLoc = s.indexOf(':');
		if (colonLoc == -1)
		{
			Logging.errorPrint("Found invalid stateful token: " + s);
			return false;
		}
		if (stateful == null)
		{
			stateful = new ObjectCache();
		}
		return processToken(stateful, s.substring(0, colonLoc), s
			.substring(colonLoc + 1));
	}

	public void addStatefulInformation(CDOMObject target)
	{
		if (stateful != null)
		{
			target.overlayCDOMObject(stateful);
		}
	}

	public void setLoaded(List<Campaign> campaigns)
	{
		campaignList.clear();
		campaignList.addAll(campaigns);
	}

	public boolean isTypeHidden(Class<?> cl, String type)
	{
		for (Campaign c : campaignList)
		{
			List<String> hiddentypes = getCampaignHiddenTypes(cl, c);
			if (hiddentypes != null)
			{
				for (String s : hiddentypes)
				{
					if (s.equalsIgnoreCase(type))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	private List<String> getCampaignHiddenTypes(Class<?> cl, Campaign c)
	{
		List<String> hiddentypes = c.getSafeListFor(ListKey.getKeyFor(
				STRING_CLASS, "HIDDEN_" + cl.getSimpleName()));
		for (Campaign subCamp : c.getSubCampaigns())
		{
			hiddentypes.addAll(getCampaignHiddenTypes(cl, subCamp));
		}
		return hiddentypes;
	}

	public abstract boolean consolidate();

	public ReferenceManufacturer<? extends Loadable> getManufacturer(String firstToken)
	{
		int equalLoc = firstToken.indexOf('=');
		String className;
		String categoryName;
		if (equalLoc != firstToken.lastIndexOf('='))
		{
			Logging
					.log(Logging.LST_ERROR,
							"  Error encountered: Found second = in ObjectType=Category");
			Logging.log(Logging.LST_ERROR,
					"  Format is: ObjectType[=Category]|Key[|Key] value was: "
							+ firstToken);
			Logging.log(Logging.LST_ERROR, "  Valid ObjectTypes are: "
					+ StringPClassUtil.getValidStrings());
			return null;
		}
		else if ("FEAT".equals(firstToken))
		{
			className = "ABILITY";
			categoryName = "FEAT";
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
		Class<? extends Loadable> c = StringPClassUtil.getClassFor(className);
		Class catClass = StringPClassUtil.getCategoryClassFor(className);
		if (c == null)
		{
			Logging.log(Logging.LST_ERROR, "Unrecognized ObjectType: "
					+ className);
			return null;
		}
		ReferenceManufacturer<? extends Loadable> rm;
		if (CategorizedCDOMObject.class.isAssignableFrom(c))
		{
			if (categoryName == null)
			{
				Logging
						.log(Logging.LST_ERROR,
								"  Error encountered: Found Categorized Type without =Category");
				Logging.log(Logging.LST_ERROR,
						"  Format is: ObjectType[=Category]|Key[|Key] value was: "
								+ firstToken);
				Logging.log(Logging.LST_ERROR, "  Valid ObjectTypes are: "
						+ StringPClassUtil.getValidStrings());
				return null;
			}
			
			rm = ref.getManufacturer(((Class) c), catClass, categoryName);
			if (rm == null)
			{
				Logging.log(Logging.LST_ERROR, "  Error encountered: "
						+ className + " Category: " + categoryName
						+ " not found");
				return null;
			}
		}
		else
		{
			if (categoryName != null)
			{
				Logging
						.log(Logging.LST_ERROR,
								"  Error encountered: Found Non-Categorized Type with =Category");
				Logging.log(Logging.LST_ERROR,
						"  Format is: ObjectType[=Category]|Key[|Key] value was: "
								+ firstToken);
				Logging.log(Logging.LST_ERROR, "  Valid ObjectTypes are: "
						+ StringPClassUtil.getValidStrings());
				return null;
			}
			rm = ref.getManufacturer(c);
		}
		return rm;
	}

	public void performDeferredProcessing(CDOMObject cdo)
	{
		for (DeferredToken<? extends Loadable> token : TokenLibrary
				.getDeferredTokens())
		{
			if (token.getDeferredTokenClass().isAssignableFrom(cdo.getClass()))
			{
				processDeferred(cdo, token);
			}
		}
	}

	private <T extends Loadable> void processDeferred(CDOMObject cdo,
			DeferredToken<T> token)
	{
		token.process(this, ((T) cdo));
	}

	public <T extends PObject> void addTypesToList(T cdo)
	{
		Set<String> typeSet = typeMap.get(cdo.getClass());
		for (Type t : cdo.getTrueTypeList(false))
		{
			typeSet.add(t.toString());
		}
	}

	public void validateAssociations()
	{
		for (ReferenceManufacturer<?> rm : ref.getAllManufacturers())
		{
			for (CDOMSingleRef<?> singleRef : rm.getReferenced())
			{
				String choice = singleRef.getChoice();
				if (choice != null)
				{
					CDOMObject cdo = (CDOMObject) singleRef.resolvesTo();
					ChooseInformation<?> ci = cdo.get(ObjectKey.CHOOSE_INFO);
					if (ci == null)
					{
						Logging.errorPrint("Found "
							+ rm.getReferenceDescription() + " "
							+ cdo.getKeyName() + " " + " that had association: "
							+ choice + " but was not an object with CHOOSE");
						rm.fireUnconstuctedEvent(singleRef);
						continue;
					}
					ClassIdentity<?> clIdentity = ci.getClassIdentity();
					if (choice.indexOf("%") > -1)
					{
						//patterns or %LIST are OK
						//See CollectionToAbilitySelection.ExpandingConverter
						continue;
					}
					Class<?> cl = clIdentity.getChoiceClass();
					if (Loadable.class.isAssignableFrom(cl))
					{
						ReferenceManufacturer<? extends Loadable> mfg =
								ref.getManufacturer((ClassIdentity<? extends Loadable>) clIdentity);
						if (!mfg.containsObject(choice)
							&& (ref.getAbbreviatedObject(
								clIdentity.getChoiceClass(), choice) == null)
							&& (TokenLibrary.getPrimitive(cl, choice) == null))
						{
							Logging.errorPrint("Found "
								+ rm.getReferenceDescription() + " "
								+ cdo.getKeyName() + " "
								+ " that had association: " + choice
								+ " but no such "
								+ mfg.getReferenceDescription()
								+ " was ever defined");
							rm.fireUnconstuctedEvent(singleRef);
							continue;
						}
					}
				}
			}
		}
	}
}
