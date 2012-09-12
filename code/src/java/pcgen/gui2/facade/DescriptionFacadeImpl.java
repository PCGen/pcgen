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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.ChronicleEntry;
import pcgen.core.NoteItem;
import pcgen.core.PlayerCharacter;
import pcgen.core.facade.ChronicleEntryFacade;
import pcgen.core.facade.DefaultReferenceFacade;
import pcgen.core.facade.DescriptionFacade;
import pcgen.core.facade.NoteFacade;
import pcgen.core.facade.ReferenceFacade;
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
	private static final String NOTE_NAME_GM_NOTES = LanguageBundle
		.getString("in_gmNotes"); //$NON-NLS-1$

	private PlayerCharacter theCharacter;
	private DefaultListFacade<ChronicleEntryFacade> chronicleEntries;
	private DefaultListFacade<NoteFacade> notes;

	private DefaultReferenceFacade<String> birthday;
	private DefaultReferenceFacade<String> location;
	private DefaultReferenceFacade<String> city;
	private DefaultReferenceFacade<String> region;
	private DefaultReferenceFacade<String> birthplace;
	private DefaultReferenceFacade<String> personalityTrait1;
	private DefaultReferenceFacade<String> personalityTrait2;
	private DefaultReferenceFacade<String> phobias;
	private DefaultReferenceFacade<String> interests;
	private DefaultReferenceFacade<String> catchPhrase;
	private DefaultReferenceFacade<String> hairStyle;
	private DefaultReferenceFacade<String> speechPattern;
	private DefaultListFacade<BiographyField> customBiographyFields; 	

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

		birthday = new DefaultReferenceFacade<String>(theCharacter.getSafeStringFor(StringKey.BIRTHDAY));
		location = new DefaultReferenceFacade<String>(theCharacter.getSafeStringFor(StringKey.LOCATION));
		city = new DefaultReferenceFacade<String>(theCharacter.getResidence());
		region = new DefaultReferenceFacade<String>(theCharacter.getRegionString());
		birthplace = new DefaultReferenceFacade<String>(theCharacter.getBirthplace());
		personalityTrait1 = new DefaultReferenceFacade<String>(theCharacter.getSafeStringFor(StringKey.TRAIT1));
		personalityTrait2 = new DefaultReferenceFacade<String>(theCharacter.getSafeStringFor(StringKey.TRAIT2));
		phobias = new DefaultReferenceFacade<String>(theCharacter.getSafeStringFor(StringKey.PHOBIAS));
		interests = new DefaultReferenceFacade<String>(theCharacter.getSafeStringFor(StringKey.INTERESTS));
		catchPhrase = new DefaultReferenceFacade<String>(theCharacter.getSafeStringFor(StringKey.CATCH_PHRASE));
		hairStyle = new DefaultReferenceFacade<String>(theCharacter.getSafeStringFor(StringKey.HAIR_STYLE));
		speechPattern = new DefaultReferenceFacade<String>(theCharacter.getSafeStringFor(StringKey.SPEECH_TENDENCY));
		customBiographyFields = new DefaultListFacade<BiographyField>();
		addCharacterCustomFields();
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
		notes.addElement(createDefaultNote(NOTE_NAME_GM_NOTES,
			theCharacter.getSafeStringFor(StringKey.MISC_GM)));
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

	/**
	 * Add any custom biography fields already registered for the character.  
	 */
	private void addCharacterCustomFields()
	{
		for (BiographyField field : EnumSet.range(BiographyField.SPEECH_PATTERN, BiographyField.CATCH_PHRASE))
		{
			if (StringUtils.isNotEmpty(getBiographyField(field).getReference()))
			{
				customBiographyFields.addElement(field);
			}
		}
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
				theCharacter.setDescription(text);
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
			else if (NOTE_NAME_GM_NOTES.equals(noteName))
			{
				theCharacter.setStringFor(StringKey.MISC_GM, text);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReferenceFacade<String> getBiographyField(BiographyField field)
	{
		switch (field)
		{
			case SPEECH_PATTERN:
				return speechPattern;

			case BIRTHDAY:
				return birthday;

			case LOCATION:
				return location;

			case CITY:
				return city;

			case REGION:
				return region;

			case BIRTHPLACE:
				return birthplace;

			case PERSONALITY_TRAIT_1:
				return personalityTrait1;

			case PERSONALITY_TRAIT_2:
				return personalityTrait2;

			case PHOBIAS:
				return phobias;

			case INTERESTS:
				return interests;

			case CATCH_PHRASE:
				return catchPhrase;

			case HAIR_STYLE:
				return hairStyle;
				
			default:
				throw new UnsupportedOperationException("The field " + field //$NON-NLS-1$
					+ " must use a dedicated getter."); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBiographyField(BiographyField field, String newValue)
	{
		switch (field)
		{
			case SPEECH_PATTERN:
				speechPattern.setReference(newValue);
				theCharacter.setSpeechTendency(newValue);
				break;

			case BIRTHDAY:
				birthday.setReference(newValue);
				theCharacter.setBirthday(newValue);
				break;

			case LOCATION:
				location.setReference(newValue);
				theCharacter.setLocation(newValue);
				break;

			case CITY:
				city.setReference(newValue);
				theCharacter.setResidence(newValue);
				break;

			case BIRTHPLACE:
				birthplace.setReference(newValue);
				theCharacter.setBirthplace(newValue);
				break;

			case PERSONALITY_TRAIT_1:
				personalityTrait1.setReference(newValue);
				theCharacter.setTrait1(newValue);
				break;

			case PERSONALITY_TRAIT_2:
				personalityTrait2.setReference(newValue);
				theCharacter.setTrait2(newValue);
				break;

			case PHOBIAS:
				phobias.setReference(newValue);
				theCharacter.setPhobias(newValue);
				break;

			case INTERESTS:
				interests.setReference(newValue);
				theCharacter.setInterests(newValue);
				break;

			case CATCH_PHRASE:
				catchPhrase.setReference(newValue);
				theCharacter.setCatchPhrase(newValue);
				break;

			case HAIR_STYLE:
				hairStyle.setReference(newValue);
				theCharacter.setHairStyle(newValue);
				break;
				
			case REGION:
				throw new UnsupportedOperationException("The field " + field //$NON-NLS-1$
					+ " cannot be set from the UI."); //$NON-NLS-1$

			default:
				throw new UnsupportedOperationException("The field " + field //$NON-NLS-1$
					+ " must use a dedicated setter."); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListFacade<BiographyField> getCustomBiographyFields()
	{
		return customBiographyFields;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addCustomBiographyField(BiographyField field)
	{
		customBiographyFields.addElement(field);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeCustomBiographyField(BiographyField field)
	{
		customBiographyFields.removeElement(field);
	}
}
