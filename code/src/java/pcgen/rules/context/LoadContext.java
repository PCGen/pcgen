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
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.ObjectCache;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Campaign;
import pcgen.core.WeaponProf;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.TokenSupport;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

public abstract class LoadContext
{

	private static final Class<String> STRING_CLASS = String.class;

	public final ListContext list;

	public final ObjectContext obj;

	public final AbstractReferenceContext ref;
	
	private List<Campaign> campaignList = new ArrayList<Campaign>();

	public LoadContext(AbstractReferenceContext rc, ListContext lc, ObjectContext oc)
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

//	public <T extends PrereqObject> CDOMGroupRef<T> groupChildNodesOfClass(
//			PrereqObject parent, Class<T> child)
//	{
//		/*
//		 * Create a new Group in the graph and then (defer to end of build)
//		 * create edges between the new Group and all of the children of the
//		 * given parent.
//		 */
//		// TODO FIXME
//		return null;
//	}

	private int writeMessageCount = 0;

	public void addWriteMessage(String string)
	{
		Logging.errorPrint("!!" + string);
		// TODO FIXME Silently consume for now - these are message generated
		// during LST write...
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

	public ListContext getGraphContext()
	{
		return list;
	}

	public ObjectContext getObjectContext()
	{
		return obj;
	}

	public ListContext getListContext()
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

	public void resolveReferences()
	{
		ref.resolveReferences();
	}

	public void resolveDeferredTokens()
	{
		for (DeferredToken<? extends CDOMObject> token : TokenLibrary
				.getDeferredTokens())
		{
			processRes(token);
		}
	}

	private <T extends CDOMObject> void processRes(DeferredToken<T> token)
	{
		Class<T> cl = token.getDeferredTokenClass();
		Collection<? extends ReferenceManufacturer> mfgs = ref
				.getAllManufacturers();
		for (ReferenceManufacturer<? extends T, ?> rm : mfgs)
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

//	public <T extends CDOMObject> PrimitiveChoiceSet<T> getChoiceSet(
//			Class<T> poClass, String value)
//	{
//		return support.getChoiceSet(this, poClass, value);
//	}
//
//	public <T extends CDOMObject> PrimitiveChoiceFilter<T> getPrimitiveChoiceFilter(
//			Class<T> cl, String key)
//	{
//		return support.getPrimitive(this, cl, key);
//	}

	public <T> boolean processSubToken(T cdo, String tokenName,
			String key, String value) throws PersistenceLayerException
	{
		return support.processSubToken(this, cdo, tokenName, key, value);
	}

	public <T extends CDOMObject> boolean processToken(T derivative,
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

	public <T> String[] unparse(T cdo, String tokenName)
	{
		return support.unparse(this, cdo, tokenName);
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

	public Prerequisite getPrerequisite(String string, String value)
			throws PersistenceLayerException
	{
		return support.getPrerequisite(this, string, value);
	}

	public <T extends CDOMObject> T cloneConstructedCDOMObject(T cdo, String newName)
	{
		T newObj = obj.cloneConstructedCDOMObject(cdo, newName);
		ref.importObject(newObj);
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

	CDOMObject stateful;

	public void clearStatefulInformation()
	{
		stateful = null;
	}

	public boolean addStatefulToken(String s) throws PersistenceLayerException
	{
		int colonLoc = s.indexOf(':');
		if (colonLoc == -1)
		{
			//TODO error
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
			stateful.overlayCDOMObject(target);
		}
	}

	public void setLoaded(List<Campaign> selectedCampaignsList)
	{
		campaignList.clear();
		campaignList.addAll(selectedCampaignsList);
	}

	public boolean isTypeHidden(Class<?> cl, String type)
	{
		for (Campaign c : campaignList)
		{
			List<String> hiddentypes = c.getListFor(ListKey.getKeyFor(
					STRING_CLASS, "HIDDEN_" + cl.getSimpleName()));
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
}
