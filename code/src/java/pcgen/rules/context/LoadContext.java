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
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.facet.DataSetInitializationFacet;
import pcgen.cdom.facet.FacetInitialization;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.inst.ObjectCache;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.core.Campaign;
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

public abstract class LoadContext
{

	private static final PrerequisiteWriter PREREQ_WRITER =
			new PrerequisiteWriter();

	static
	{
		FacetInitialization.initialize();
	}

	private final DataSetID datasetID = DataSetID.getID();

	private final AbstractListContext list;

	private final AbstractObjectContext obj;

	private final AbstractReferenceContext ref;
	
	private final List<Campaign> campaignList = new ArrayList<Campaign>();

	private int writeMessageCount = 0;

	private final TokenSupport support = new TokenSupport();

	private List<Object> dontForget = new ArrayList<Object>();

	//Per file
	private URI sourceURI;

	//Per file
	private CDOMObject stateful;

	public LoadContext(AbstractReferenceContext rc, AbstractListContext lc, AbstractObjectContext oc)
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
		getReferenceContext().setExtractURI(extractURI);
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
		getReferenceContext().setSourceURI(sourceURI);
		getListContext().setSourceURI(sourceURI);
		clearStatefulInformation();
		Logging.debugPrint("Starting Load of " + sourceURI);
	}

	public URI getSourceURI()
	{
		return sourceURI;
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
		Collection<? extends ReferenceManufacturer> mfgs = getReferenceContext()
				.getAllManufacturers();
		for (ReferenceManufacturer<? extends T> rm : mfgs)
		{
			if (cl.isAssignableFrom(rm.getReferenceClass()))
			{
				for (T po : rm.getAllObjects())
				{
					token.process(this, po);
				}
				for (T po : rm.getDerivativeObjects())
				{
					token.process(this, po);
				}
			}
		}
	}

	public void resolvePostDeferredTokens()
	{
		Collection<? extends ReferenceManufacturer> mfgs = getReferenceContext()
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
					this.setSourceURI(po.getSourceURI());
					token.process(this, po);
				}
			}
		}
	}

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
	
	public <T extends Loadable> void unconditionallyProcess(T cdo, String key, String value)
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

	public <T extends CDOMObject> T cloneConstructedCDOMObject(T cdo, String newName)
	{
		T newObj = getObjectContext().cloneConstructedCDOMObject(cdo, newName);
		getReferenceContext().importObject(newObj);
		return newObj;
	}

	/**
	 * Create a copy of a CDOMObject duplicating any references to the old 
	 * object. (e.g. Spell, Domain etc)
	 * 
	 * Package protected rather than private for testing only
	 *  
	 * @param cdo The original object being copied. 
	 * @param newName The name that should be given to the new object.
	 * @return The newly created CDOMObject.
	 */
	@SuppressWarnings("unchecked")
	<T extends CDOMObject> T cloneInMasterLists(T cdo, String newName)
	{
		T newObj;
		try
		{
			newObj = (T) cdo.clone();
			newObj.setName(newName);
			getListContext().cloneInMasterLists(cdo, newObj);
		}
		catch (CloneNotSupportedException e)
		{
			Logging.errorPrint("Failed to clone " + cdo, e);
			e.printStackTrace();
			return null;
		}
		return newObj;
	}

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

	public CampaignSourceEntry getCampaignSourceEntry(Campaign source, String value)
	{
		return CampaignSourceEntry.getNewCSE(source, sourceURI, value);
	}

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
		return processToken(stateful, s.substring(0, colonLoc).intern(),
				s.substring(colonLoc + 1).intern());
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

	public abstract boolean consolidate();

	public DataSetID getDataSetID()
	{
		return datasetID;
	}

	public void loadCampaignFacets()
	{
		FacetLibrary.getFacet(DataSetInitializationFacet.class).initialize(this);
	}

	public void forgetMeNot(CDOMReference<?> cdr)
	{
		dontForget.add(cdr);
	}

	public AbstractReferenceContext getReferenceContext()
	{
		return ref;
	}
	
	public List<Campaign> getLoadedCampaigns()
	{
		return Collections.unmodifiableList(campaignList);
	}

	public ReferenceManufacturer<? extends Loadable> getManufacturer(
		String firstToken)
	{
		return ReferenceContextUtilities.getManufacturer(getReferenceContext(),
			firstToken);
	}

}
