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
import java.util.List;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.list.CompanionList;
import pcgen.core.FollowerOption;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.CompanionFacade;
import pcgen.core.facade.CompanionStubFacade;
import pcgen.core.facade.CompanionSupportFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.MapFacade;

/**
 * This class implements the basic CompanionSupportFacade
 * for a given
 * <code>PlayerCharacter</code> and is
 * used to help implement companion support for the
 * CharacterFacade.
 * @see pcgen.gui2.facade.CharacterFacadeImpl
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CompanionSupportFacadeImpl implements CompanionSupportFacade
{

	private DefaultListFacade<CompanionFacadeDelegate> companionList;
	private PlayerCharacter theCharacter;
	private DefaultListFacade<CompanionStubFacade> availCompList;

	/**
	 * Create a new instance of CompanionSupportFacadeImpl
	 * @param theCharacter The character to be represented.
	 */
	public CompanionSupportFacadeImpl(PlayerCharacter theCharacter)
	{
		this.theCharacter = theCharacter;
		this.companionList = new DefaultListFacade<CompanionFacadeDelegate>();
		this.availCompList = new DefaultListFacade<CompanionStubFacade>();
		initCompData();
	}

	/**
	 * Initialisation of the character's companion data. 
	 */
	private void initCompData()
	{
		List<CompanionStub> companions = new ArrayList<CompanionStub>();
		for (CompanionList compList : Globals.getContext().ref
				.getConstructedCDOMObjects(CompanionList.class))
		{
			Map<FollowerOption, CDOMObject> fMap = theCharacter.getAvailableFollowers(compList.getKeyName(), null);
			for (FollowerOption followerOpt : fMap.keySet())
			{
				companions.add(new CompanionStub(followerOpt.getRace(), compList.getKeyName()));
			}
		}
		availCompList.setContents(companions);
		//Logging.errorPrint("Available comps" + availCompList);
	}

	@Override
	public void addCompanion(CharacterFacade companion)
	{
		CompanionFacadeDelegate delegate = new CompanionFacadeDelegate();
		delegate.setCompanionFacade(companion);
		companionList.addElement(delegate);
	}

	@Override
	public void removeCompanion(CompanionFacade companion)
	{
		if (!(companion instanceof CompanionFacadeDelegate))
		{
			return;
		}
		companionList.removeElement((CompanionFacadeDelegate) companion);
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
			if (file.equals(character.getFileRef().getReference()))
			{
				delegate.setCompanionFacade(character);
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
				CompanionFacade dummy = null;
				//TODO: create dummy CompanionFacade containing follower info from character
				delegate.setCompanionFacade(dummy);
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
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ListFacade<? extends CompanionFacade> getCompanions()
	{
		return companionList;
	}

}
