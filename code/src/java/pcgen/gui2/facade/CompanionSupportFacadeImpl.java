/*
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
 */
package pcgen.gui2.facade;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.list.CompanionList;
import pcgen.core.FollowerOption;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.character.Follower;
import pcgen.core.display.CharacterDisplay;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CompanionFacade;
import pcgen.facade.core.CompanionStubFacade;
import pcgen.facade.core.CompanionSupportFacade;
import pcgen.facade.core.PartyFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultMapFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.MapFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.system.CharacterManager;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;

import org.apache.commons.lang3.StringUtils;

/**
 * This class implements the basic CompanionSupportFacade
 * for a given
 * {@code PlayerCharacter} and is
 * used to help implement companion support for the
 * CharacterFacade.
 * @see pcgen.gui2.facade.CharacterFacadeImpl
 */
public class CompanionSupportFacadeImpl implements CompanionSupportFacade, ListListener<CharacterFacade>
{

	private final DefaultListFacade<CompanionFacadeDelegate> companionList;
	private final PlayerCharacter theCharacter;
	private final CharacterDisplay charDisplay;
	private final DefaultListFacade<CompanionStubFacade> availCompList;
	private final DefaultMapFacade<String, Integer> maxCompanionsMap;
	private final Map<String, CompanionList> keyToCompanionListMap;
	private final TodoManager todoManager;
	private final CharacterFacadeImpl pcFacade;

	/**
	 * Create a new instance of CompanionSupportFacadeImpl
	 * @param theCharacter The character to be represented.
	 * @param todoManager The user tasks tracker.
	 * @param nameRef The reference to the character's name. 
	 * @param fileRef The reference to the character's file. 
	 * @param pcFacade The UI facade for the master.
	 */
	public CompanionSupportFacadeImpl(PlayerCharacter theCharacter, TodoManager todoManager,
		ReferenceFacade<String> nameRef, ReferenceFacade<File> fileRef, CharacterFacadeImpl pcFacade)
	{
		this.theCharacter = theCharacter;
		this.pcFacade = pcFacade;
		this.charDisplay = theCharacter.getDisplay();
		this.todoManager = todoManager;
		this.companionList = new DefaultListFacade<>();
		this.availCompList = new DefaultListFacade<>();
		this.maxCompanionsMap = new DefaultMapFacade<>();
		this.keyToCompanionListMap = new HashMap<>();
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
	private void addMasterListeners(ReferenceFacade<String> nameRef, ReferenceFacade<File> fileRef)
	{
		nameRef.addReferenceListener(e -> {
            String newName = e.getNewReference();
            for (CompanionFacadeDelegate delegate : companionList)
            {
                CharacterFacade companion = CharacterManager.getCharacterMatching(delegate);
                if (companion != null)
                {
                    CharacterFacadeImpl compFacadeImpl = (CharacterFacadeImpl) companion;
                    Follower follower = compFacadeImpl.getTheCharacter().getDisplay().getMaster();
                    follower.setName(newName);
                }
            }

        });
		fileRef.addReferenceListener(e -> {
            File newFile = e.getNewReference();
            for (CompanionFacadeDelegate delegate : companionList)
            {
                CharacterFacade companion = CharacterManager.getCharacterMatching(delegate);
                if (companion != null)
                {
                    CharacterFacadeImpl compFacadeImpl = (CharacterFacadeImpl) companion;
                    Follower follower = compFacadeImpl.getTheCharacter().getDisplay().getMaster();
                    follower.setFileName(newFile.getAbsolutePath());
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
				PlayerCharacter pc = compFacadeImpl.getTheCharacter();
				pc.setMaster(pc.getDisplay().getMaster());
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
		List<CompanionStub> companions = new ArrayList<>();
		for (CompanionList compList : Globals.getContext().getReferenceContext()
			.getConstructedCDOMObjects(CompanionList.class))
		{
			keyToCompanionListMap.put(compList.getKeyName(), compList);
			Map<FollowerOption, CDOMObject> fMap = charDisplay.getAvailableFollowers(compList.getKeyName(), null);
			for (FollowerOption followerOpt : fMap.keySet())
			{
				if (!followerOpt.getRace().isUnselected() && followerOpt.qualifies(theCharacter, null))
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
			for (Follower follower : charDisplay.getFollowerList())
			{
				CompanionFacade comp = new CompanionNotLoaded(follower.getName(), new File(follower.getFileName()),
					follower.getRace(), follower.getType().toString());
				CompanionFacadeDelegate delegate = new CompanionFacadeDelegate();
				delegate.setCompanionFacade(comp);
				companionList.addElement(delegate);
			}
		}
		//Logging.debugPrint("Companion list " + companionList);
		for (CompanionList compList : Globals.getContext().getReferenceContext()
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
			todoManager.addTodo(
				new TodoFacadeImpl(
					Tab.COMPANIONS, companionType, "in_companionTodoTooMany", companionType, 1)); //$NON-NLS-1$
			todoManager.removeTodo("in_companionTodoRemain", companionType); //$NON-NLS-1$
		}
		else if (maxCompanions > -1 && maxCompanions > numCompanions)
		{
			todoManager.addTodo(
				new TodoFacadeImpl(
					Tab.COMPANIONS, companionType, "in_companionTodoRemain", companionType, 1)); //$NON-NLS-1$
			todoManager.removeTodo("in_companionTodoTooMany", companionType); //$NON-NLS-1$
		}
		else
		{
			todoManager.removeTodo("in_companionTodoRemain", companionType); //$NON-NLS-1$
			todoManager.removeTodo("in_companionTodoTooMany", companionType); //$NON-NLS-1$
		}
	}

	@Override
	public void addCompanion(CharacterFacade companion, String companionType)
	{
		if (companion == null || !(companion instanceof CharacterFacadeImpl))
		{
			return;
		}

		CharacterFacadeImpl compFacadeImpl = (CharacterFacadeImpl) companion;
		CompanionList compList = keyToCompanionListMap.get(companionType);
		Race compRace = compFacadeImpl.getRaceRef().get();
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
		Logging.log(Logging.INFO, "Setting master to " + charDisplay.getName() //$NON-NLS-1$
			+ " for character " + compFacadeImpl); //$NON-NLS-1$
		final Follower newMaster = new Follower(charDisplay.getFileName(), charDisplay.getName(), compList);
		newMaster.setAdjustment(followerOpt.getAdjustment());
		compFacadeImpl.getTheCharacter().setMaster(newMaster);
		compFacadeImpl.refreshClassLevelModel();
		compFacadeImpl.postLevellingUpdates();

		// Update the master with the new companion
		File compFile = compFacadeImpl.getFileRef().get();
		String compFilename = StringUtils.isEmpty(compFile.getPath()) ? "" : compFile.getAbsolutePath();
		Follower follower = new Follower(compFilename, compFacadeImpl.getNameRef().get(), compList);
		follower.setRace(compRace);
		theCharacter.addFollower(follower);
		theCharacter.setCalcFollowerBonus();
		theCharacter.calcActiveBonuses();
		pcFacade.postLevellingUpdates();

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
		Map<FollowerOption, CDOMObject> fMap = charDisplay.getAvailableFollowers(compList.getKeyName(), null);
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

	@Override
	public void removeCompanion(CompanionFacade companion)
	{
		if (!(companion instanceof CompanionFacadeDelegate))
		{
			return;
		}

		File compFile = companion.getFileRef().get();
		for (Follower follower : charDisplay.getFollowerList())
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
			File file = delegate.getFileRef().get();
			String name = delegate.getNameRef().get();
			Race race = delegate.getRaceRef().get();
			if (file.equals(character.getFileRef().get()) && name.equals(character.getNameRef().get())
				&& (race == null || race.equals(character.getRaceRef().get())))
			{
				String companionType = delegate.getCompanionType();
				delegate.setCompanionFacade(character);

				// Check for a companion being loaded that is not properly linked to the master.
				// Note: When creating a companion we leave the linking to the create code.  
				if (character.getMaster() == null && character.getRaceRef().get() != null
					&& !character.getRaceRef().get().isUnselected())
				{
					CompanionList compList = keyToCompanionListMap.get(companionType);
					final Follower newMaster = new Follower(charDisplay.getFileName(), charDisplay.getName(), compList);
					FollowerOption followerOpt = getFollowerOpt(compList, character.getRaceRef().get());
					if (followerOpt != null)
					{
						newMaster.setAdjustment(followerOpt.getAdjustment());
					}
					else
					{
						Logging.log(Logging.WARNING, "Failed to find FollowerOption for complist " + compList
							+ " and race " + character.getRaceRef().get());
					}
					((CharacterFacadeImpl) character).getTheCharacter().setMaster(newMaster);
				}
				return;
			}
		}
	}

	/**
	 * Remove ourselves from the global characters list so that
	 * the current character can be garbage collected.
	 */
	void closeCharacter()
	{
		CharacterManager.getCharacters().removeListListener(this);
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
			File file = delegate.getFileRef().get();
			if (file.equals(character.getFileRef().get()))
			{
				CompanionFacade comp = new CompanionNotLoaded(character.getNameRef().get(),
					character.getFileRef().get(), character.getRaceRef().get(), delegate.getCompanionType());
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

	@Override
	public void elementAdded(ListEvent<CharacterFacade> e)
	{
		linkCompanion(e.getElement());
	}

	@Override
	public void elementRemoved(ListEvent<CharacterFacade> e)
	{
		unlinkCompanion(e.getElement());
	}

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

	@Override
	public void elementModified(ListEvent<CharacterFacade> e)
	{
		// Ignored.
	}

	/**
	 * The Class {@code DelegateFileListener} tracks the file name of a companion and
	 * keeps the associated Follower record up to date.
	 */
	private static class DelegateFileListener implements ReferenceListener<File>
	{
		private final Follower follower;

		public DelegateFileListener(Follower followerIn)
		{
			this.follower = followerIn;
		}

		@Override
		public void referenceChanged(ReferenceEvent<File> e)
		{
			follower.setFileName(e.getNewReference().getAbsolutePath());
		}
	}

	/**
	 * The Class {@code DelegateNameListener} tracks the name of a companion and
	 * keeps the associated Follower record up to date.
	 */
	private static class DelegateNameListener implements ReferenceListener<String>
	{
		private final Follower follower;

		public DelegateNameListener(Follower followerIn)
		{
			this.follower = followerIn;
		}

		@Override
		public void referenceChanged(ReferenceEvent<String> e)
		{
			follower.setName(e.getNewReference());
		}
	}
}
