/**
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
 */
package pcgen.gui2.facade;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.core.ChronicleEntry;
import pcgen.core.NoteItem;
import pcgen.cdom.enumeration.NotePCAttribute;
import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.facade.core.ChronicleEntryFacade;
import pcgen.facade.core.DescriptionFacade;
import pcgen.facade.core.NoteFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.WriteableReferenceFacade;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code DescriptionFacadeImpl} is an implementation of
 * the DescriptionFacade interface for the new user interface. It is 
 * intended to provide a full implementation of the new ui/core 
 * interaction layer.
 */
class DescriptionFacadeImpl implements DescriptionFacade
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
	private final DefaultListFacade<ChronicleEntryFacade> chronicleEntries;
	private final DefaultListFacade<NoteFacade> notes;

	private final Map<BiographyField, WriteableReferenceFacade<String>> bioData = new EnumMap<>(BiographyField.class);

	private final DefaultListFacade<BiographyField> customBiographyFields;

	private static DefaultReferenceFacade<String> newDefaultBioFieldFor(final PlayerCharacter pc, final PCStringKey key)
	{
		return new DefaultReferenceFacade<>(pc.getDisplay().getSafeStringFor(key));
	}

	/**
	 * Create a new DescriptionFacadeImpl instance for the character.
	 * @param pc The character.
	 */
	DescriptionFacadeImpl(PlayerCharacter pc)
	{
		theCharacter = pc;
		charDisplay = pc.getDisplay();
		chronicleEntries = new DefaultListFacade<>();
		charDisplay.getChronicleEntries().forEach(chronicleEntries::addElement);

		notes = new DefaultListFacade<>();
		addDefaultNotes();

		charDisplay.getNotesList().forEach(item ->
				notes.addElement(item));

		bioData.put(BiographyField.BIRTHDAY, DescriptionFacadeImpl.newDefaultBioFieldFor(pc, PCStringKey.BIRTHDAY));
		bioData.put(BiographyField.BIRTHPLACE, new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.BIRTHPLACE)));
		bioData.put(BiographyField.LOCATION, new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.LOCATION)));
		bioData.put(BiographyField.CITY, new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.RESIDENCE)));
		bioData.put(BiographyField.REGION, new DefaultReferenceFacade<>(charDisplay.getRegionString()));
		bioData.put(BiographyField.PERSONALITY_TRAIT_1, new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.PERSONALITY1)));
		bioData.put(BiographyField.PERSONALITY_TRAIT_2, new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.PERSONALITY2)));
		bioData.put(BiographyField.PHOBIAS, new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.PHOBIAS)));
		bioData.put(BiographyField.INTERESTS, new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.INTERESTS)));
		bioData.put(BiographyField.CATCH_PHRASE, new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.CATCHPHRASE)));
		bioData.put(BiographyField.HAIR_STYLE, new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.HAIRSTYLE)));
		bioData.put(BiographyField.SPEECH_PATTERN, new DefaultReferenceFacade<>(charDisplay.getSafeStringFor(PCStringKey.SPEECHTENDENCY)));
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
	 * @param noteName
	 * @param value
	 * @return note
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
			if (StringUtils.isNotEmpty(getBiographyField(field).get()))
			{
				customBiographyFields.addElement(field);
			}
		}
	}

	/**
	 * @see pcgen.facade.core.DescriptionFacade#createChronicleEntry()
	 */
	@Override
	public ChronicleEntryFacade createChronicleEntry()
	{
		ChronicleEntry chronicleEntry = new ChronicleEntry();
		theCharacter.addChronicleEntry(chronicleEntry);
		chronicleEntries.addElement(chronicleEntry);
		return chronicleEntry;
	}

	/**
	 * @see pcgen.facade.core.DescriptionFacade#removeChronicleEntry(ChronicleEntryFacade)
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

	/**
	 * @see pcgen.facade.core.DescriptionFacade#getChronicleEntries()
	 */
	@Override
	public ListFacade<ChronicleEntryFacade> getChronicleEntries()
	{
		return chronicleEntries;
	}

	@Override
	public ListFacade<NoteFacade> getNotes()
	{
		return notes;
	}

	@Override
	public void setNote(NoteFacade note, String text)
	{
		if (!(note instanceof NoteItem))
		{
			return;
		}

		NoteItem noteItem = (NoteItem) note;
		noteItem.setValue(text);
		if (noteItem.isRequired())
		{
			String noteName = noteItem.getName();
			NotePCAttribute whichAttr = NotePCAttribute.getByNoteName(noteName);
			theCharacter.setPCAttribute(whichAttr, text);
		}
	}

	@Override
	public void renameNote(NoteFacade note, String newName)
	{
		if (!(note instanceof NoteItem) || note.isRequired())
		{
			return;
		}

		NoteItem noteItem = (NoteItem) note;
		noteItem.setName(newName);
		notes.modifyElement(noteItem);
	}

	@Override
	public void deleteNote(NoteFacade note)
	{
		if (!(note instanceof NoteItem) || note.isRequired())
		{
			return;
		}

		theCharacter.removeNote((NoteItem) note);
		notes.removeElement(note);
	}

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

	@Override
	public ReferenceFacade<String> getBiographyField(final BiographyField field)
	{
		if (bioData.containsKey(field))
		{
			return bioData.get(field);
		}
		throw new UnsupportedOperationException("The field " + field //$NON-NLS-1$
				+ " must use a dedicated getter."); //$NON-NLS-1$
	}

	@Override
	public void setBiographyField(final BiographyField field, final String newValue)
	{
		// TODO: generify this
		Set<BiographyField> canBeDirectlySet = EnumSet.of(
				BiographyField.LOCATION,
				BiographyField.SPEECH_PATTERN,
				BiographyField.INTERESTS,
				BiographyField.BIRTHPLACE,
				BiographyField.PERSONALITY_TRAIT_1,
				BiographyField.PERSONALITY_TRAIT_2,
				BiographyField.CITY,
				BiographyField.PHOBIAS,
				BiographyField.CATCH_PHRASE,
				BiographyField.HAIR_STYLE,
				BiographyField.BIRTHDAY
		);

		if (canBeDirectlySet.contains(field))
		{
			bioData.get(field).set(newValue);
			theCharacter.setPCAttribute(field.getPcattr(), newValue);
		}
		else if (field == BiographyField.REGION)
		{
			throw new UnsupportedOperationException("The field " + field //$NON-NLS-1$
					+ " cannot be set from the UI."); //$NON-NLS-1$
		}
		else
		{
			throw new UnsupportedOperationException("The field " + field //$NON-NLS-1$
					+ " must use a dedicated setter."); //$NON-NLS-1$
		}
	}

	@Override
	public ListFacade<BiographyField> getCustomBiographyFields()
	{
		return customBiographyFields;
	}

	@Override
	public void addCustomBiographyField(BiographyField field)
	{
		customBiographyFields.addElement(field);
	}

	@Override
	public void removeCustomBiographyField(BiographyField field)
	{
		customBiographyFields.removeElement(field);
	}
}
