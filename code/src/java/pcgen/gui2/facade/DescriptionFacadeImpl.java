/**
 * DescriptionFacadeImpl.java
 * Copyright James Dempsey, 2011
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
 * Created on 06/10/2011 7:59:35 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.ChronicleEntry;
import pcgen.core.NoteItem;
import pcgen.core.PlayerCharacter;
import pcgen.core.facade.ChronicleEntryFacade;
import pcgen.core.facade.DescriptionFacade;
import pcgen.core.facade.NoteFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>DescriptionFacadeImpl</code> is an implementation of 
 * the DescriptionFacade interface for the new user interface. It is 
 * intended to provide a full implementation of the new ui/core 
 * interaction layer.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class DescriptionFacadeImpl implements DescriptionFacade
{
	/** Name of the Biography node. */
	private static final String NOTE_NAME_BIO = LanguageBundle
		.getString("in_bio"); //$NON-NLS-1$
	/** Name of the Description node. */
	private static final String NOTE_NAME_DESCRIP = LanguageBundle
		.getString("in_descrip"); //$NON-NLS-1$
	/** Name of the Companions notes node. */
	private static final String NOTE_NAME_COMPANION = LanguageBundle
		.getString("in_companions"); //$NON-NLS-1$
	/** Name of the Other Assets notes node. */
	private static final String NOTE_NAME_OTHER_ASSETS = LanguageBundle
		.getString("in_otherAssets"); //$NON-NLS-1$
	/** Name of the Magic Item notes node. */
	private static final String NOTE_NAME_MAGIC_ITEMS = LanguageBundle
		.getString("in_magicItems"); //$NON-NLS-1$
	/** Name of the DM Notes node. */
	private static final String NOTE_NAME_DM_NOTES = LanguageBundle
		.getString("in_dmNotes"); //$NON-NLS-1$

	private PlayerCharacter theCharacter;
	private DefaultListFacade<ChronicleEntryFacade> chronicleEntries;
	private DefaultListFacade<NoteFacade> notes;
	

	/**
	 * Create a new DescriptionFacadeImpl instance for the character.
	 * @param pc The character.
	 */
	public DescriptionFacadeImpl(PlayerCharacter pc)
	{
		theCharacter = pc;
		chronicleEntries = new DefaultListFacade<ChronicleEntryFacade>();
		for (ChronicleEntryFacade entry : theCharacter.getChronicleEntries())
		{
			chronicleEntries.addElement(entry);
		}
		
		notes = new DefaultListFacade<NoteFacade>();
		addDefaultNotes();
		
		for (NoteItem item : theCharacter.getNotesList())
		{
			notes.addElement(item);
		}
	}

	private void addDefaultNotes()
	{
		notes.addElement(createDefaultNote(NOTE_NAME_BIO, theCharacter
			.getDisplay().getBio()));
		notes.addElement(createDefaultNote(NOTE_NAME_DESCRIP, theCharacter
			.getDisplay().getDescription()));
		notes.addElement(createDefaultNote(NOTE_NAME_COMPANION,
			theCharacter.getSafeStringFor(StringKey.MISC_COMPANIONS)));
		notes.addElement(createDefaultNote(NOTE_NAME_OTHER_ASSETS,
			theCharacter.getSafeStringFor(StringKey.MISC_ASSETS)));
		notes.addElement(createDefaultNote(NOTE_NAME_MAGIC_ITEMS,
			theCharacter.getSafeStringFor(StringKey.MISC_MAGIC)));
		notes.addElement(createDefaultNote(NOTE_NAME_DM_NOTES,
			theCharacter.getSafeStringFor(StringKey.MISC_DM)));
	}

	/**
	 * @param noteNameBio
	 * @param bio
	 * @return
	 */
	private NoteFacade createDefaultNote(String noteName, String value)
	{
		NoteItem note = new NoteItem(0, -1, noteName, value);
		note.setRequired(true);
		return note;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DescriptionFacade#createChronicleEntry()
	 */
	@Override
	public ChronicleEntryFacade createChronicleEntry()
	{
		ChronicleEntry chronicleEntry = new ChronicleEntry();
		theCharacter.addChronicleEntry(chronicleEntry);
		chronicleEntries.addElement(chronicleEntry);
		return chronicleEntry;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DescriptionFacade#removeChronicleEntry(pcgen.core.ChronicleEntry)
	 */
	@Override
	public void removeChronicleEntry(ChronicleEntryFacade chronicleEntry)
	{
		if (chronicleEntry instanceof ChronicleEntry)
		{
			theCharacter.removeChronicleEntry((ChronicleEntry) chronicleEntry);
		}
		chronicleEntries.removeElement(chronicleEntry);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.DescriptionFacade#getChronicleEntries()
	 */
	@Override
	public ListFacade<ChronicleEntryFacade> getChronicleEntries()
	{
		return chronicleEntries;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListFacade<NoteFacade> getNotes()
	{
		return notes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNote(NoteFacade note, String text)
	{
		if (note == null || !(note instanceof NoteItem))
		{
			return;
		}
		
		NoteItem noteItem = (NoteItem) note;
		noteItem.setValue(text);
		if (noteItem.isRequired())
		{
			String noteName = noteItem.getName(); 
			if (NOTE_NAME_BIO.equals(noteName))
			{
				theCharacter.setBio(text);
			}
			else if (NOTE_NAME_DESCRIP.equals(noteName))
			{
				theCharacter.setBio(text);
			}
			else if (NOTE_NAME_COMPANION.equals(noteName))
			{
				theCharacter.setStringFor(StringKey.MISC_COMPANIONS, text);
			}
			else if (NOTE_NAME_OTHER_ASSETS.equals(noteName))
			{
				theCharacter.setStringFor(StringKey.MISC_ASSETS, text);
			}
			else if (NOTE_NAME_MAGIC_ITEMS.equals(noteName))
			{
				theCharacter.setStringFor(StringKey.MISC_MAGIC, text);
			}
			else if (NOTE_NAME_DM_NOTES.equals(noteName))
			{
				theCharacter.setStringFor(StringKey.MISC_DM, text);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void renameNote(NoteFacade note, String newName)
	{
		if (note == null || !(note instanceof NoteItem) || note.isRequired())
		{
			return;
		}
		
		NoteItem noteItem = (NoteItem) note;
		noteItem.setName(newName);
		notes.modifyElement(noteItem);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteNote(NoteFacade note)
	{
		if (note == null || !(note instanceof NoteItem) || note.isRequired())
		{
			return;
		}
		
		theCharacter.getNotesList().remove(note);
		notes.removeElement(note);
	}

	public void addNewNote()
	{
		int parentId = -1;
		int newNodeId = 0;

		for (NoteItem currItem : theCharacter.getNotesList())
		{
			if (currItem.getId() > newNodeId)
			{
				newNodeId = currItem.getId();
			}
		}

		++newNodeId;
		
		Set<String> names = new HashSet<String>();
		for (NoteFacade note : notes)
		{
			names.add(note.getName());
		}

		String baseName = LanguageBundle.getString("in_newItem"); //$NON-NLS-1$
		String name = baseName;
		int num = 0;
		while (names.contains(name))
		{
			num++;
			name = baseName + " " + num; //$NON-NLS-1$
		}
		NoteItem note =
				new NoteItem(newNodeId, parentId, name, LanguageBundle
					.getString("in_newValue")); //$NON-NLS-1$
		theCharacter.addNotesItem(note);
		notes.addElement(note);
	}
}
