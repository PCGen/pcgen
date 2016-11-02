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

import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.inst.SimpleScopeInstance;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.GroupDefinition;
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
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.rules.persistence.ChoiceSetLoadUtilities;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.TokenSupport;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.rules.persistence.token.PostDeferredToken;
import pcgen.rules.persistence.token.PostValidationToken;
import pcgen.util.Logging;

abstract class LoadContextInst implements LoadContext
{

	private static final PrerequisiteWriter PREREQ_WRITER =
			new PrerequisiteWriter();

	private final DataSetID datasetID = DataSetID.getID();

	private final AbstractListContext list;

	private final AbstractObjectContext obj;

	private final AbstractReferenceContext ref;
	
	private final VariableContext var;

	private final List<Campaign> campaignList = new ArrayList<>();

	private int writeMessageCount = 0;

	private final TokenSupport support = new TokenSupport();

	private final List<Object> dontForget = new ArrayList<>();

	//Per file
	private URI sourceURI;

	//Per file
	private CDOMObject stateful;
	
	private ScopeInstance scopeInst = null;

	static
	{
		FacetInitialization.initialize();
	}

	public LoadContextInst(AbstractReferenceContext rc, AbstractListContext lc, AbstractObjectContext oc)
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
		var = new VariableContext();
	}

	@Override
	public void addWriteMessage(String string)
	{
		Logging.errorPrint("!!" + string);
		/*
		 * TODO Need to find a better solution for what happens during write...
		 */
		writeMessageCount++;
	}

	@Override
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
	@Override
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
	@Override
	public void setSourceURI(URI sourceURI)
	{
		this.sourceURI = sourceURI;
		getObjectContext().setSourceURI(sourceURI);
		getReferenceContext().setSourceURI(sourceURI);
		getListContext().setSourceURI(sourceURI);
		clearStatefulInformation();
		Logging.debugPrint("Starting Load of " + sourceURI);
	}

	@Override
	public URI getSourceURI()
	{
		return sourceURI;
	}

	/*
	 * Get the type of context we're running in (either Editor or Runtime)
	 */
	public abstract String getContextType();

	@Override
	public AbstractObjectContext getObjectContext()
	{
		return obj;
	}

	@Override
	public AbstractListContext getListContext()
	{
		return list;
	}

	@Override
	public VariableContext getVariableContext()
	{
		return var;
	}
	
	@Override
	public void commit()
	{
		getListContext().commit();
		getObjectContext().commit();
	}

	@Override
	public void rollback()
	{
		getListContext().rollback();
		getObjectContext().rollback();
	}

	@Override
	public void resolveDeferredTokens()
	{
		for (DeferredToken<? extends Loadable> token : support.
				getDeferredTokens())
		{
			processRes(token);
		}
		commit();
	}

	private <T extends Loadable> void processRes(DeferredToken<T> token)
	{
		Class<T> cl = token.getDeferredTokenClass();
		Collection<? extends ReferenceManufacturer<?>> mfgs = getReferenceContext()
				.getAllManufacturers();
		for (ReferenceManufacturer<?> rm : mfgs)
		{
			if (cl.isAssignableFrom(rm.getReferenceClass()))
			{
				@SuppressWarnings("unchecked")
				ReferenceManufacturer<? extends T> trm =
						(ReferenceManufacturer<? extends T>) rm;
				for (T po : trm.getAllObjects())
				{
					token.process(this, po);
				}
				for (T po : trm.getDerivativeObjects())
				{
					token.process(this, po);
				}
			}
		}
	}

	@Override
	public void resolvePostDeferredTokens()
	{
		Collection<? extends ReferenceManufacturer<?>> mfgs = getReferenceContext()
				.getAllManufacturers();
		for (PostDeferredToken<? extends Loadable> token : TokenLibrary.getPostDeferredTokens())
		{
			processPostRes(token, mfgs);
		}
	}

	private <T extends Loadable> void processPostRes(PostDeferredToken<T> token,
			Collection<? extends ReferenceManufacturer<?>> mfgs)
	{
		Class<T> cl = token.getDeferredTokenClass();
		for (ReferenceManufacturer<?> rm : mfgs)
		{
			if (cl.isAssignableFrom(rm.getReferenceClass()))
			{
				@SuppressWarnings("unchecked")
				ReferenceManufacturer<? extends T> trm =
						(ReferenceManufacturer<? extends T>) rm;
				for (T po : trm.getAllObjects())
				{
					this.setSourceURI(po.getSourceURI());
					token.process(this, po);
				}
			}
		}
	}

	@Override
	public void resolvePostValidationTokens()
	{
		Collection<? extends ReferenceManufacturer> mfgs = getReferenceContext()
				.getAllManufacturers();
		for (PostValidationToken<? extends Loadable> token : TokenLibrary.getPostValidationTokens())
		{
			processPostVal(token, mfgs);
		}
	}

	private <T extends Loadable> void processPostVal(PostValidationToken<T> token,
			Collection<? extends ReferenceManufacturer> mfgs)
	{
		Class<T> cl = token.getValidationTokenClass();
		for (ReferenceManufacturer<? extends T> rm : mfgs)
		{
			if (cl.isAssignableFrom(rm.getReferenceClass()))
			{
				setSourceURI(null);
				token.process(this, rm.getAllObjects());
			}
		}
	}

	@Override
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

	@Override
	public <T extends CDOMObject> PrimitiveCollection<T> getPrimitiveChoiceFilter(
			SelectionCreator<T> sc, String key)
	{
		return ChoiceSetLoadUtilities.getPrimitive(this, sc, key);
	}

	@Override
	public <T> ParseResult processSubToken(T cdo, String tokenName,
			String key, String value)
	{
		return support.processSubToken(this, cdo, tokenName, key, value);
	}

	@Override
	public <T extends Loadable> boolean processToken(T derivative,
			String typeStr, String argument) throws PersistenceLayerException
	{
		return support.processToken(this, derivative, typeStr, argument);
	}
	
	@Override
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
	@Override
	public <T> String[] unparseSubtoken(T cdo, String tokenName)
	{
		return support.unparseSubtoken(this, cdo, tokenName);
	}

	@Override
	public <T> Collection<String> unparse(T cdo)
	{
		return support.unparse(this, cdo);
	}

	@Override
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
			return null;
		}
		return newObj;
	}

	@Override
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

	@Override
	public CampaignSourceEntry getCampaignSourceEntry(Campaign source, String value)
	{
		return CampaignSourceEntry.getNewCSE(source, sourceURI, value);
	}

	@Override
	public void clearStatefulInformation()
	{
		stateful = null;
	}

	@Override
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

	@Override
	public void addStatefulInformation(CDOMObject target)
	{
		if (stateful != null)
		{
			target.overlayCDOMObject(stateful);
		}
	}

	@Override
	public void setLoaded(List<Campaign> campaigns)
	{
		campaignList.clear();
		campaignList.addAll(campaigns);
	}

	@Override
	public abstract boolean consolidate();

	@Override
	public DataSetID getDataSetID()
	{
		return datasetID;
	}

	@Override
	public void loadCampaignFacets()
	{
		FacetLibrary.getFacet(DataSetInitializationFacet.class).initialize(this);
	}

	@Override
	public void forgetMeNot(CDOMReference<?> cdr)
	{
		dontForget.add(cdr);
	}

	@Override
	public AbstractReferenceContext getReferenceContext()
	{
		return ref;
	}
	
	@Override
	public List<Campaign> getLoadedCampaigns()
	{
		return Collections.unmodifiableList(campaignList);
	}

	@Override
	public ReferenceManufacturer<? extends Loadable> getManufacturer(
		String firstToken)
	{
		return ReferenceContextUtilities.getManufacturer(getReferenceContext(),
			firstToken);
	}

	@Override
	public <T extends CDOMObject> T performCopy(T object, String copyName)
	{
		T copy = ref.performCopy(object, copyName);
		list.cloneInMasterLists(object, copy);
		return copy;
	}

	@Override
	public void loadLocalToken(Object token)
	{
		support.loadLocalToken(token);
	}

	@Override
	public <T> GroupDefinition<T> getGroup(Class<T> cl, String s)
	{
		return support.getGroup(cl, s);
	}

	@Override
	public ScopeInstance getActiveScope()
	{
		if (scopeInst == null)
		{
			LegalScope legalScope = var.getScope("Global");
			scopeInst = new SimpleScopeInstance(null, legalScope, "Global");
		}
		return scopeInst;
	}

	@Override
	public LoadContext dropIntoContext(String scope)
	{
		LegalScope legalScope = var.getScope(scope);
		if (legalScope == null)
		{
			throw new IllegalArgumentException("LegalVariableScope " + scope
				+ " does not exist");
		}
		return dropIntoContext(legalScope);
	}

	private LoadContext dropIntoContext(LegalScope lvs)
	{
		LegalScope parent = lvs.getParentScope();
		if (parent == null)
		{
			//is Global
			return this;
		}
		LoadContext parentLC = dropIntoContext(parent);
		SimpleScopeInstance localInst = new SimpleScopeInstance(parentLC.getActiveScope(),
			lvs, "Context (during Load)");
		return new DerivedLoadContext(parentLC, localInst);
	}

	private class DerivedLoadContext implements LoadContext
	{

		private final LoadContext parent;
		private final ScopeInstance scopeInst;

		public DerivedLoadContext(LoadContext parent, ScopeInstance lvs)
		{
			this.scopeInst = lvs;
			this.parent = parent;
		}

		@Override
		public void setExtractURI(URI extractURI)
		{
			parent.setExtractURI(extractURI);
		}

		@Override
		public void setSourceURI(URI sourceURI)
		{
			parent.setSourceURI(sourceURI);
		}

		@Override
		public URI getSourceURI()
		{
			return parent.getSourceURI();
		}

		@Override
		public DataSetID getDataSetID()
		{
			return parent.getDataSetID();
		}

		@Override
		public AbstractReferenceContext getReferenceContext()
		{
			return parent.getReferenceContext();
		}

		@Override
		public AbstractObjectContext getObjectContext()
		{
			return parent.getObjectContext();
		}

		@Override
		public AbstractListContext getListContext()
		{
			return parent.getListContext();
		}

		@Override
		public boolean consolidate()
		{
			return parent.consolidate();
		}

		@Override
		public VariableContext getVariableContext()
		{
			return parent.getVariableContext();
		}

		@Override
		public void commit()
		{
			parent.commit();
		}

		@Override
		public void rollback()
		{
			parent.rollback();
		}

		@Override
		public void resolveDeferredTokens()
		{
			parent.resolveDeferredTokens();
		}

		@Override
		public void resolvePostDeferredTokens()
		{
			parent.resolvePostDeferredTokens();
		}

		@Override
		public <T extends CDOMObject> PrimitiveCollection<T> getChoiceSet(
			SelectionCreator<T> sc, String value)
		{
			return parent.getChoiceSet(sc, value);
		}

		@Override
		public <T extends CDOMObject> PrimitiveCollection<T> getPrimitiveChoiceFilter(
			SelectionCreator<T> sc, String key)
		{
			return parent.getPrimitiveChoiceFilter(sc, key);
		}

		@Override
		public String getPrerequisiteString(Collection<Prerequisite> prereqs)
		{
			return parent.getPrerequisiteString(prereqs);
		}

		@Override
		public ReferenceManufacturer<? extends Loadable> getManufacturer(
			String firstToken)
		{
			return parent.getManufacturer(firstToken);
		}

		@Override
		public void forgetMeNot(CDOMReference<?> cdr)
		{
			parent.forgetMeNot(cdr);
		}

		@Override
		public <T extends CDOMObject> T cloneConstructedCDOMObject(T cdo,
			String newName)
		{
			return parent.cloneConstructedCDOMObject(cdo, newName);
		}

		@Override
		public CampaignSourceEntry getCampaignSourceEntry(Campaign source,
			String value)
		{
			return parent.getCampaignSourceEntry(source, value);
		}

		@Override
		public void clearStatefulInformation()
		{
			parent.clearStatefulInformation();
		}

		@Override
		public boolean addStatefulToken(String s)
			throws PersistenceLayerException
		{
			return parent.addStatefulToken(s);
		}

		@Override
		public void addStatefulInformation(CDOMObject target)
		{
			parent.addStatefulInformation(target);
		}

		@Override
		public void setLoaded(List<Campaign> campaigns)
		{
			parent.setLoaded(campaigns);
		}

		@Override
		public List<Campaign> getLoadedCampaigns()
		{
			return parent.getLoadedCampaigns();
		}

		@Override
		public void loadCampaignFacets()
		{
			parent.loadCampaignFacets();
		}

		@Override
		public <T extends CDOMObject> T performCopy(T object, String copyName)
		{
			return parent.performCopy(object, copyName);
		}

		@Override
		public <T> ParseResult processSubToken(T cdo, String tokenName,
			String key, String value)
		{
			return support.processSubToken(this, cdo, tokenName, key, value);
		}

		@Override
		public <T extends Loadable> boolean processToken(T derivative,
			String typeStr, String argument) throws PersistenceLayerException
		{
			return support.processToken(this, derivative, typeStr, argument);
		}

		@Override
		public <T extends Loadable> void unconditionallyProcess(T cdo,
			String key, String value)
		{
			parent.unconditionallyProcess(cdo, key, value);
		}

		@Override
		public <T> String[] unparseSubtoken(T cdo, String tokenName)
		{
			return support.unparseSubtoken(this, cdo, tokenName);
		}

		@Override
		public <T> Collection<String> unparse(T cdo)
		{
			return support.unparse(this, cdo);
		}

		@Override
		public void addWriteMessage(String string)
		{
			parent.addWriteMessage(string);
		}

		@Override
		public int getWriteMessageCount()
		{
			return parent.getWriteMessageCount();
		}

		@Override
		public ScopeInstance getActiveScope()
		{
			return scopeInst;
		}

		@Override
		public LoadContext dropIntoContext(String scope)
		{
			LegalScope toScope = var.getScope(scope);
			if (toScope == null)
			{
				throw new IllegalArgumentException("LegalVariableScope "
					+ scope + " does not exist");
			}
			LegalScope currentScope = scopeInst.getLegalScope();
			if (currentScope.equals(toScope))
			{
				return this;
			}
			else if (currentScope.getParentScope().equals(toScope))
			{
				//Jump up from here
				return parent;
			}
			else if (toScope.getParentScope().equals(currentScope))
			{
				//Direct drop from this
				SimpleScopeInstance localInst = new SimpleScopeInstance(scopeInst,
					toScope, "Context (during Load)");
				return new DerivedLoadContext(this, localInst);
			}
			//Random jump to somewhere else...
			return LoadContextInst.this.dropIntoContext(toScope);
		}

		@Override
		public void loadLocalToken(Object token)
		{
			parent.loadLocalToken(token);
		}

		@Override
		public <T> GroupDefinition<T> getGroup(Class<T> cl, String s)
		{
			return parent.getGroup(cl, s);
		}

		@Override
		public void resolvePostValidationTokens()
		{
			parent.resolvePostValidationTokens();
		}
	}
}
