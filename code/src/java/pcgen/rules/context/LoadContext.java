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

import pcgen.base.formula.inst.NEPFormula;
import pcgen.base.proxy.DeferredMethodController;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.GroupDefinition;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.cdom.grouping.GroupingCollection;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.core.Campaign;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.persistence.token.ParseResult;

public interface LoadContext
{

	/*
	 * Source Info
	 */
    void setExtractURI(URI extractURI);

	void setSourceURI(URI sourceURI);

	URI getSourceURI();

	/*
	 * Context info
	 */
    DataSetID getDataSetID();

	AbstractReferenceContext getReferenceContext();

	AbstractObjectContext getObjectContext();

	AbstractListContext getListContext();

	boolean consolidate();

	/*
	 * Token Processing (State Machine)
	 */
    void commit();

	void rollback();

	/*
	 * Token Content
	 */
    void resolveDeferredTokens();

	void resolvePostDeferredTokens();

	void resolvePostValidationTokens();

	<T extends CDOMObject> PrimitiveCollection<T> getChoiceSet(SelectionCreator<T> sc, String value);

	<T extends CDOMObject> PrimitiveCollection<T> getPrimitiveChoiceFilter(SelectionCreator<T> sc, String key);

	String getPrerequisiteString(Collection<Prerequisite> prereqs);

	ReferenceManufacturer<? extends Loadable> getManufacturer(String firstToken);

	void forgetMeNot(CDOMReference<?> cdr);

	/*
	 * Loader Content
	 */
    <T extends CDOMObject> T cloneConstructedCDOMObject(T cdo, String newName);

	CampaignSourceEntry getCampaignSourceEntry(Campaign source, String value);

	void clearStatefulInformation();

	boolean addStatefulToken(String s) throws PersistenceLayerException;

	void addStatefulInformation(CDOMObject target);

	void setLoaded(List<Campaign> campaigns);

	List<Campaign> getLoadedCampaigns();

	void loadCampaignFacets();

	<T extends CDOMObject> T performCopy(T object, String copyName);

	/*
	 * Token Processing (direct)
	 */
    <T> ParseResult processSubToken(T cdo, String tokenName, String key, String value);

	<T extends Loadable> boolean processToken(T derivative, String typeStr, String argument)
            ;

	<T extends Loadable> void unconditionallyProcess(T cdo, String key, String value);

	<T> String[] unparseSubtoken(T cdo, String tokenName);

	/**
	 * Unparses the given Object into a Collection of String objects, for each token that
	 * would be on the object.
	 * 
	 * @param loadable
	 *            The loadable object to be unparsed into the tokens used to persist the
	 *            object
	 * @return A Collection of Strings representing the tokens that are part of the
	 *         persistent format of the given Loadable
	 */
    <T extends Loadable> Collection<String> unparse(T loadable);

	/*
	 * Output Messages
	 */
    void addWriteMessage(String string);

	int getWriteMessageCount();

	/**
	 * Loads a token "local" to this LoadContext (meaning it is local to the
	 * data currently loaded and does not apply to all future data that may be
	 * loaded [with a different LoadContext since a LoadContext is not
	 * reusable]).
	 * 
	 * This is used for dynamic tokens, such as those produced from a FACTDEF or
	 * FACTSETDEF
	 * 
	 * @param token
	 *            The "local" token to be loaded into this LoadContext
	 */
    void loadLocalToken(Object token);

	/**
	 * Returns a GroupDefinition<T> based on the given Class and Group instructions
	 * 
	 * Note: This is used to convert dynamic groups (produced by FACT/FACTSET) into primitives that 
	 * can be used in tokens like CHOOSE.
	 * 
	 * @param cl The class of object used for the GroupDefinition
	 * @param instructions The instructions used to determine the contents of the GroupDefinition
	 * @return A GroupDefinition<T> based on the given Class and Group instructions
	 */
    <T> GroupDefinition<T> getGroup(Class<T> cl, String instructions);

	LoadContext dropIntoContext(String scope);

	/**
	 * Returns the VariableContext for this LoadContext.
	 * 
	 * @return The VariableContext for this LoadContext
	 */
	VariableContext getVariableContext();

	/**
	 * Returns the currently active PCGenScope.
	 * 
	 * @return The currently active PCGenScope
	 */
    PCGenScope getActiveScope();

	/**
	 * Directly returns a valid formula with the Format of the given FormatManager.
	 * 
	 * @param formatManager
	 *            The FormatManager to indicate the format of the NEPFormula to be
	 *            returned
	 * @param instructions
	 *            The instructions for the NEPFormula to process when it is solved
	 * @return A NEPFormula that implements the given Format and instructions
	 */
    <T> NEPFormula<T> getValidFormula(FormatManager<T> formatManager, String instructions);

	/**
	 * Adds a DeferredMethodController to this LoadContext. This DeferredMethodController
	 * will be run if commit() is called or forgotten with no action if rollback() is
	 * called.
	 * 
	 * @param controller
	 *            The DeferredMethodController to be added to this LoadContext
	 */
    void addDeferredMethodController(DeferredMethodController<?> controller);

	/**
	 * Returns a GroupingCollection based on the given scope name and grouping name.
	 * 
	 * Note: This is used to for new Grouping items (MODIFY) and is the strategic
	 * direction for grouping in LoadContext.
	 * 
	 * @param scope
	 *            The scope used to determine the contents of the Grouping
	 * @param instructions
	 *            The instructions used to determine the contents of the Grouping
	 * @return A GroupingCollection based on the given scope name and Group
	 *         instructions
	 */
    GroupingCollection<?> getGrouping(PCGenScope scope, String instructions);
}
