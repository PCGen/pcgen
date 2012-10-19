/*
 * CompanionSupportFacadeImpl.java
 * Copyright 2012 (C) Connor Petty <cpmeister@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Mar 18, 2012, 11:38:13 PM
 */
package pcgen.gui2.facade;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.list.CompanionList;
import pcgen.core.FollowerOption;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.character.Follower;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.CompanionFacade;
import pcgen.core.facade.CompanionStubFacade;
import pcgen.core.facade.CompanionSupportFacade;
import pcgen.core.facade.PartyFacade;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.TodoFacade.CharacterTab;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.DefaultMapFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.MapFacade;
import pcgen.system.CharacterManager;
import pcgen.util.Logging;

/**
 * This class implements the basic CompanionSupportFacade
 * for a given
 * <code>PlayerCharacter</code> and is
 * used to help implement companion support for the
 * CharacterFacade.
 * @see pcgen.gui2.facade.CharacterFacadeImpl
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CompanionSupportFacadeImpl implements CompanionSupportFacade, ListListener<CharacterFacade>
{

	private DefaultListFacade<CompanionFacadeDelegate> companionList;
	private PlayerCharacter theCharacter;
	private DefaultListFacade<CompanionStubFacade> availCompList;
	private DefaultMapFacade<String, Integer> maxCompanionsMap;
	private Map<String, CompanionList> keyToCompanionListMap;
	private final TodoManager todoManager;

	/**
	 * Create a new instance of CompanionSupportFacadeImpl
	 * @param theCharacter The character to be represented.
	 * @param todoManager The user tasks tracker.
	 * @param nameRef The reference to the character's name. 
	 * @param fileRef The reference to the character's file. 
	 */
	public CompanionSupportFacadeImpl(PlayerCharacter theCharacter,
		TodoManager todoManager, ReferenceFacade<String> nameRef,
		ReferenceFacade<File> fileRef)
	{
		this.theCharacter = theCharacter;
		this.todoManager = todoManager;
		this.companionList = new DefaultListFacade<CompanionFacadeDelegate>();
		this.availCompList = new DefaultListFacade<CompanionStubFacade>();
		this.maxCompanionsMap = new DefaultMapFacade<String, Integer>();
		this.keyToCompanionListMap = new HashMap<String, CompanionList>();
		initCompData(true);
		CharacterManager.getCharacters().addListListener(this);
		addMasterListeners(nameRef, fileRef);
	}

	/**
	 * Add listeners to the master name and file that will update the master 
	 * information of all companions when the master changes.
	 * @param nameRef The reference to the character's name. 
	 * @param fileRef The reference to the character's file.
	 */
	private void addMasterListeners(ReferenceFacade<String> nameRef,
		ReferenceFacade<File> fileRef)
	{
		nameRef.addReferenceListener(new ReferenceListener<String>()
		{

			@Override
			public void referenceChanged(ReferenceEvent<String> e)
			{
				String newName = e.getNewReference();
				for (CompanionFacadeDelegate delegate : companionList)
				{
					CharacterFacade companion =
							CharacterManager.getCharacterMatching(delegate);
					if (companion != null)
					{
						CharacterFacadeImpl compFacadeImpl =
								(CharacterFacadeImpl) companion;
						Follower follower =
								compFacadeImpl.getTheCharacter().getMaster();
						follower.setName(newName);
					}
				}

			}

		});
		fileRef.addReferenceListener(new ReferenceListener<File>()
		{

			@Override
			public void referenceChanged(ReferenceEvent<File> e)
			{
				File newFile = e.getNewReference();
				for (CompanionFacadeDelegate delegate : companionList)
				{
					CharacterFacade companion =
							CharacterManager.getCharacterMatching(delegate);
					if (companion != null)
					{
						CharacterFacadeImpl compFacadeImpl =
								(CharacterFacadeImpl) companion;
						Follower follower =
								compFacadeImpl.getTheCharacter().getMaster();
						follower.setFileName(newFile.getAbsolutePath());
					}
				}

			}

		});
	}

	/**
	 * Refresh the character's companion information, reflecting any changes in 
	 * the character's qualification for companions.   
	 */
	void refreshCompanionData()
	{
		initCompData(false);
		for (CompanionFacadeDelegate delegate : companionList)
		{
			CompanionFacade compFacade = delegate.getDelegate();
			if (compFacade instanceof CharacterFacadeImpl)
			{
				CharacterFacadeImpl compFacadeImpl = (CharacterFacadeImpl) compFacade;
				compFacadeImpl.refreshClassLevelModel();
				compFacadeImpl.postLevellingUpdates();
			}
		}
	}
	
	/**
	 * Initialisation of the character's companion data.
	 * @param rebuildCompanionList Should the list of the character;s companions be rebuilt?
	 */
	private void initCompData(boolean rebuildCompanionList)
	{
		List<CompanionStub> companions = new ArrayList<CompanionStub>();
		for (CompanionList compList : Globals.getContext().ref
				.getConstructedCDOMObjects(CompanionList.class))
		{
			keyToCompanionListMap.put(compList.getKeyName(), compList);
			Map<FollowerOption, CDOMObject> fMap = theCharacter.getAvailableFollowers(compList.getKeyName(), null);
			for (FollowerOption followerOpt : fMap.keySet())
			{
				if (followerOpt.getRace() != Globals.s_EMPTYRACE && followerOpt.qualifies(theCharacter, null))
				{
					companions.add(new CompanionStub(followerOpt.getRace(), compList.getKeyName()));
				}
			}
			int maxVal = theCharacter.getMaxFollowers(compList);
			if (maxVal == 0)
			{
				maxCompanionsMap.removeKey(compList.toString());
			}
			else
			{
				maxCompanionsMap.putValue(compList.toString(), maxVal);
			}
		}
		availCompList.updateContents(companions);
		//Logging.debugPrint("Available comps " + availCompList);
		//Logging.debugPrint("Max comps " + maxCompanionsMap);
		
		if (rebuildCompanionList)
		{
			for (Follower follower : theCharacter.getFollowerList())
			{
				CompanionFacade comp =
						new CompanionNotLoaded(follower.getName(), new File(
							follower.getFileName()), follower.getRace(), follower
							.getType().toString());
				CompanionFacadeDelegate delegate = new CompanionFacadeDelegate();
				delegate.setCompanionFacade(comp);
				companionList.addElement(delegate);
			}
		}
		//Logging.debugPrint("Companion list " + companionList);
		for (CompanionList compList : Globals.getContext().ref
				.getConstructedCDOMObjects(CompanionList.class))
		{
			updateCompanionTodo(compList.toString());
		}
	}


	private void updateCompanionTodo(String companionType)
	{
		Integer max = maxCompanionsMap.getValue(companionType);
		int maxCompanions = max == null ? 0 : max;
		int numCompanions = 0;
		for (CompanionFacadeDelegate cfd : companionList)
		{
			if (cfd.getCompanionType().equals(companionType))
			{
				numCompanions++;
			}
		}		

		if (maxCompanions > -1 && maxCompanions < numCompanions)
		{
			todoManager.addTodo(new TodoFacadeImpl(CharacterTab.CompanionsTab,
				companionType, "in_companionTodoTooMany", companionType, 1)); //$NON-NLS-1$
			todoManager.removeTodo("in_companionTodoRemain", companionType); //$NON-NLS-1$
		}
		else if (maxCompanions > -1 && maxCompanions > numCompanions)
		{
			todoManager.addTodo(new TodoFacadeImpl(CharacterTab.CompanionsTab,
				companionType, "in_companionTodoRemain", companionType, 1)); //$NON-NLS-1$
			todoManager.removeTodo("in_companionTodoTooMany", companionType); //$NON-NLS-1$
		}
		else
		{
			todoManager.removeTodo("in_companionTodoRemain", companionType); //$NON-NLS-1$
			todoManager.removeTodo("in_companionTodoTooMany", companionType); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addCompanion(CharacterFacade companion, String companionType)
	{
		if (companion == null || !(companion instanceof CharacterFacadeImpl))
		{
			return;
		}

		CharacterFacadeImpl compFacadeImpl = (CharacterFacadeImpl) companion;
		CompanionList compList = keyToCompanionListMap.get(companionType);
		Race compRace = (Race) compFacadeImpl.getRaceRef().getReference();
		FollowerOption followerOpt = getFollowerOpt(compList, compRace);
		if (followerOpt == null)
		{
			Logging.errorPrint("Unable to find follower option for companion " //$NON-NLS-1$
				+ companion + " of race " + compRace); //$NON-NLS-1$
			return;
		}

		if (!followerOpt.qualifies(theCharacter, null))
		{
			Logging.errorPrint("Not qualified to take companion " //$NON-NLS-1$
				+ companion + " of race " + compRace); //$NON-NLS-1$
			return;
		}
		
		// Update the companion with the master details
		Logging.log(Logging.INFO,
			"Setting master to " + theCharacter.getName() //$NON-NLS-1$
				+ " for character " + compFacadeImpl); //$NON-NLS-1$
		final Follower newMaster =
				new Follower(theCharacter.getFileName(), theCharacter.getName(), compList);
		newMaster.setAdjustment(followerOpt.getAdjustment());
		compFacadeImpl.getTheCharacter().setMaster(newMaster);
		compFacadeImpl.refreshClassLevelModel();
		compFacadeImpl.postLevellingUpdates();
		
		// Update the master with the new companion
		File compFile = compFacadeImpl.getFileRef().getReference();
		String compFilename = StringUtils.isEmpty(compFile.getPath()) ? "" : compFile.getAbsolutePath();
		Follower follower =
				new Follower(compFilename, compFacadeImpl.getNameRef()
					.getReference(), compList);
		follower.setRace(compRace);
		theCharacter.addFollower(follower);

		CompanionFacadeDelegate delegate = new CompanionFacadeDelegate();
		delegate.setCompanionFacade(companion);
		companionList.addElement(delegate);

		// Watch companion file name and character name to update follower record
		companion.getFileRef().addReferenceListener(new DelegateFileListener(follower));
		companion.getNameRef().addReferenceListener(new DelegateNameListener(follower));

		updateCompanionTodo(companionType);
	}

	private FollowerOption getFollowerOpt(CompanionList compList, Race compRace)
	{
		FollowerOption followerOpt = null; 
		Map<FollowerOption, CDOMObject> fMap = theCharacter.getAvailableFollowers(compList.getKeyName(), null);
		for (FollowerOption fOpt : fMap.keySet())
		{
			if (compRace == fOpt.getRace())
			{
				followerOpt = fOpt;
				break;
			}
		}
		return followerOpt;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeCompanion(CompanionFacade companion)
	{
		if (!(companion instanceof CompanionFacadeDelegate))
		{
			return;
		}
		
		File compFile = companion.getFileRef().getReference();
		for (Follower follower : theCharacter.getFollowerList())
		{
			File followerFile = new File(follower.getFileName());
			if (followerFile.equals(compFile))
			{
				theCharacter.delFollower(follower);
				break;
			}
		}
		companionList.removeElement((CompanionFacadeDelegate) companion);

		updateCompanionTodo(companion.getCompanionType());
	}

	/**
	 * Adds a newly opened character into the existing
	 * companion framework. This character will replace
	 * the dummy CompanionFacade that has the same
	 * file name. This should typically be called
	 * when a character is opened from one of the follower stubs
	 * @param character the character to link
	 */
	private void linkCompanion(CharacterFacade character)
	{
		for (CompanionFacadeDelegate delegate : companionList)
		{
			File file = delegate.getFileRef().getReference();
			String name = delegate.getNameRef().getReference();
			if (file.equals(character.getFileRef().getReference())
				&& name.equals(character.getNameRef().getReference()))
			{
				String companionType = delegate.getCompanionType();
				delegate.setCompanionFacade(character);
				
				// Check for a companion being loaded that is not properly linked to the master.
				// Note: When creating a companion we leave the linking to the create code.  
				if (character.getMaster() == null
					&& character.getRaceRef().getReference() != null
					&& !Constants.NONESELECTED.equals(character.getRaceRef()
						.getReference().getKeyName()))
				{
					CompanionList compList = keyToCompanionListMap.get(companionType);
					final Follower newMaster =
							new Follower(theCharacter.getFileName(), theCharacter.getName(), compList);
					FollowerOption followerOpt =
							getFollowerOpt(compList, (Race) character
								.getRaceRef().getReference());
					if (followerOpt != null)
					{
						newMaster.setAdjustment(followerOpt.getAdjustment());
					}
					else
					{
						Logging.log(Logging.WARNING,
							"Failed to find FollowerOption for complist "
								+ compList + " and race "
								+ character.getRaceRef().getReference());
					}
					((CharacterFacadeImpl)character).getTheCharacter().setMaster(newMaster);
				}
				return;
			}
		}
	}

	/**
	 * Removes a character from the companion framework.
	 * This will replace the specified character with a dummy
	 * CompanionFacade.
	 * This should be called after the specified character has been closed
	 * or is closing.
	 * If this method is not called after closing a companion character
	 * the underlying CharacterFacade would not be able to be garbage collected.
	 * @param character the character to unlink
	 */
	private void unlinkCompanion(CharacterFacade character)
	{
		for (CompanionFacadeDelegate delegate : companionList)
		{
			File file = delegate.getFileRef().getReference();
			if (file.equals(character.getFileRef().getReference()))
			{
				CompanionFacade comp =
						new CompanionNotLoaded(character.getNameRef()
							.getReference(), character.getFileRef()
							.getReference(), character.getRaceRef()
							.getReference(), delegate.getCompanionType());
				delegate.setCompanionFacade(comp);
				return;
			}
		}
	}

	@Override
	public ListFacade<CompanionStubFacade> getAvailableCompanions()
	{
		return availCompList;
	}

	@Override
	public MapFacade<String, Integer> getMaxCompanionsMap()
	{
		return maxCompanionsMap;
	}

	@Override
	public ListFacade<? extends CompanionFacade> getCompanions()
	{
		return companionList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementAdded(ListEvent<CharacterFacade> e)
	{
		linkCompanion(e.getElement());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementRemoved(ListEvent<CharacterFacade> e)
	{
		unlinkCompanion(e.getElement());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementsChanged(ListEvent<CharacterFacade> e)
	{
		PartyFacade characters = CharacterManager.getCharacters();
		for (CharacterFacade characterFacade : characters)
		{
			linkCompanion(characterFacade);
		}
		// TODO: Unlink characters no longer open 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementModified(ListEvent<CharacterFacade> e)
	{
		// Ignored.
	}

	/**
	 * The Class <code>DelegateFileListener</code> tracks the file name of a companion and 
	 * keeps the associated Follower record up to date.
	 */
	private class DelegateFileListener implements ReferenceListener<File> 
	{
		private final Follower follower;

		public DelegateFileListener(Follower followerIn)
		{
			this.follower = followerIn;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void referenceChanged(ReferenceEvent<File> e)
		{
			follower.setFileName(e.getNewReference().getAbsolutePath());
		}
	}

	/**
	 * The Class <code>DelegateNameListener</code> tracks the name of a companion and 
	 * keeps the associated Follower record up to date.
	 */
	private class DelegateNameListener implements ReferenceListener<String> 
	{
		private final Follower follower;

		public DelegateNameListener(Follower followerIn)
		{
			this.follower = followerIn;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void referenceChanged(ReferenceEvent<String> e)
		{
			follower.setName(e.getNewReference());
		}
	}
}
