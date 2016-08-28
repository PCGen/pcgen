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
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.core.ChronicleEntry;
import pcgen.core.NoteItem;
import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.facade.core.ChronicleEntryFacade;
import pcgen.facade.core.DescriptionFacade;
import pcgen.facade.core.NoteFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>DescriptionFacadeImpl</code> is an implementation of 
 * the DescriptionFacade interface for the new user interface. It is 
 * intended to provide a full implementation of the new ui/core 
 * interaction layer.
 *
 * <br>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
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

	private final PlayerCharacter theCharacter;
	private final CharacterDisplay charDisplay;
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
		charDisplay = pc.getDisplay();
		chronicleEntries = new DefaultListFacade<>();
		for (ChronicleEntryFacade entry : charDisplay.getChronicleEntries())
		{
			chronicleEntries.addElement(entry);
		}
		
		notes = new DefaultListFacade<>();
		addDefaultNotes();
		
		for (NoteItem item : charDisplay.getNotesList())
		{
			notes.addElement(item);
		}

		birthday = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.BIRTHDAY));
		location = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.LOCATION));
		city = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.RESIDENCE));
		region = new DefaultReferenceFacade<>(charDisplay.getRegionString());
		birthplace = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.BIRTHPLACE));
		personalityTrait1 = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.PERSONALITY1));
		personalityTrait2 = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.PERSONALITY2));
		phobias = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.PHOBIAS));
		interests = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.INTERESTS));
		catchPhrase = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.CATCHPHRASE));
		hairStyle = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.HAIRSTYLE));
		speechPattern = new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.SPEECHTENDENCY));
		customBiographyFields = new DefaultListFacade<>();
		addCharacterCustomFields();
	}

	private void addDefaultNotes()
	{
		notes.addElement(createDefaultNote(NOTE_NAME_BIO, charDisplay.getBio()));
		notes.addElement(createDefaultNote(NOTE_NAME_DESCRIP, charDisplay.getSafeStringFor(PCStringKey.DESCRIPTION)));
		notes.addElement(createDefaultNote(NOTE_NAME_COMPANION,
			charDisplay.getSafeStringFor(PCStringKey.COMPANIONS)));
		notes.addElement(createDefaultNote(NOTE_NAME_OTHER_ASSETS,
			charDisplay.getSafeStringFor(PCStringKey.ASSETS)));
		notes.addElement(createDefaultNote(NOTE_NAME_MAGIC_ITEMS,
			charDisplay.getSafeStringFor(PCStringKey.MAGIC)));
		notes.addElement(createDefaultNote(NOTE_NAME_GM_NOTES,
			charDisplay.getSafeStringFor(PCStringKey.GMNOTES)));
	}

	/**
	 * @param noteNameBio
	 * @param bio
	 * @return
	 */
	private static NoteFacade createDefaultNote(String noteName, String value)
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
			if (StringUtils.isNotEmpty(getBiographyField(field).get()))
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
				theCharacter.setStringFor(PCStringKey.COMPANIONS, text);
			}
			else if (NOTE_NAME_OTHER_ASSETS.equals(noteName))
			{
				theCharacter.setStringFor(PCStringKey.ASSETS, text);
			}
			else if (NOTE_NAME_MAGIC_ITEMS.equals(noteName))
			{
				theCharacter.setStringFor(PCStringKey.MAGIC, text);
			}
			else if (NOTE_NAME_GM_NOTES.equals(noteName))
			{
				theCharacter.setStringFor(PCStringKey.GMNOTES, text);
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
		
		theCharacter.removeNote((NoteItem) note);
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

		for (NoteItem currItem : charDisplay.getNotesList())
		{
			if (currItem.getId() > newNodeId)
			{
				newNodeId = currItem.getId();
			}
		}

		++newNodeId;
		
		Set<String> names = new HashSet<>();
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
				speechPattern.set(newValue);
				theCharacter.setSpeechTendency(newValue);
				break;

			case BIRTHDAY:
				birthday.set(newValue);
				theCharacter.setBirthday(newValue);
				break;

			case LOCATION:
				location.set(newValue);
				theCharacter.setLocation(newValue);
				break;

			case CITY:
				city.set(newValue);
				theCharacter.setResidence(newValue);
				break;

			case BIRTHPLACE:
				birthplace.set(newValue);
				theCharacter.setBirthplace(newValue);
				break;

			case PERSONALITY_TRAIT_1:
				personalityTrait1.set(newValue);
				theCharacter.setTrait1(newValue);
				break;

			case PERSONALITY_TRAIT_2:
				personalityTrait2.set(newValue);
				theCharacter.setTrait2(newValue);
				break;

			case PHOBIAS:
				phobias.set(newValue);
				theCharacter.setPhobias(newValue);
				break;

			case INTERESTS:
				interests.set(newValue);
				theCharacter.setInterests(newValue);
				break;

			case CATCH_PHRASE:
				catchPhrase.set(newValue);
				theCharacter.setCatchPhrase(newValue);
				break;

			case HAIR_STYLE:
				hairStyle.set(newValue);
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
