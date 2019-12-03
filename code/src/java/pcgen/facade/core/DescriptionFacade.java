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
package pcgen.facade.core;

import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.core.ChronicleEntry;
import pcgen.core.NoteItem;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;

/**
 * The Class {@code DescriptionFacade} tracks descriptive entries about the character,
 * interfacing between the user interface and the core.
 *
 * 
 */
public interface DescriptionFacade
{

	/**
	 * Remove a chronicle entry.
	 * @param chronicleEntry The entry to be removed.
	 */
    void removeChronicleEntry(ChronicleEntry chronicleEntry);

	/**
	 * Retrieve the set of the character's chronicle entries.
	 * @return The character's chronicle entries.
	 */
    ListFacade<ChronicleEntry> getChronicleEntries();

	/**
	 * Create a new Chronicle Entry and add it to the character's list.
	 * @return The new ChronicleEntry.
	 */
    ChronicleEntry createChronicleEntry();

	/**
	 * Retrieve the set of notes defined for the character.
	 * @return The character's notes.
	 */
    ListFacade<NoteItem> getNotes();

	/**
	 * Update the name of a note.
	 * @param note The note to be renamed.
	 * @param newName The new name.
	 */
    void renameNote(NoteItem note, String newName);

	/**
	 * Remove a note from a character. 
	 * @param note The note to be removed.
	 */
    void deleteNote(NoteItem note);

	/**
	 * Add a new custom note to the character.
	 */
    void addNewNote();

	/**
	 * Set the contents of a note.
	 * @param note The note to be updated.
	 * @param text The new contents of the note.
	 */
    void setNote(NoteItem note, String text);

	/**
	 * Retrieve the value for a text only biography field.
	 * @param field The field to be queried. 
	 * @return the value of the field.
	 */
    ReferenceFacade<String> getBiographyField(BiographyField field);

	/**
	 * Update the value of a text only biography field.
	 * @param field The biography field to be updated.
	 * @param attribute The attribute to be updated. 
	 * @param newValue The new value of the field.
	 */
    void setBiographyField(BiographyField field, PCStringKey attribute, String newValue);

}
