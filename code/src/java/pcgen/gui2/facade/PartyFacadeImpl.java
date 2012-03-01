/*
 * PartyFacadeImpl.java
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Nov 6, 2011, 7:51:52 PM
 */
package pcgen.gui2.facade;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import pcgen.core.PlayerCharacter;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.DefaultReferenceFacade;
import pcgen.core.facade.PartyFacade;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.io.ExportHandler;
import pcgen.io.PCGIOHandler;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class PartyFacadeImpl extends DefaultListFacade<CharacterFacade> implements PartyFacade
{

	private DefaultReferenceFacade<File> fileRef = new DefaultReferenceFacade<File>();

	@Override
	public void export(ExportHandler theHandler, BufferedWriter buf)
	{
		Collection<PlayerCharacter> characters = new ArrayList<PlayerCharacter>();
		for (CharacterFacade character : this)
		{
			if (character instanceof CharacterFacadeImpl)
			{
				characters.add(((CharacterFacadeImpl) character).getTheCharacter());
			}
		}
		theHandler.write(characters, buf);
	}

	@Override
	public ReferenceFacade<File> getFileRef()
	{
		return fileRef;
	}

	@Override
	public void setFile(File file)
	{
		fileRef.setReference(file);
	}

	public void save()
	{
		File partyFile = fileRef.getReference();
		List<File> characterFiles = new ArrayList<File>();
		for (CharacterFacade character : this)
		{
			characterFiles.add(character.getFileRef().getReference());
		}
		(new PCGIOHandler()).write(partyFile, characterFiles);
	}

}
