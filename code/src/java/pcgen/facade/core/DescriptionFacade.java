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
	public void removeChronicleEntry(ChronicleEntryFacade chronicleEntry);

	/**
	 * Retrieve the set of the character's chronicle entries.
	 * @return The character's chronicle entries.
	 */
	public ListFacade<ChronicleEntryFacade> getChronicleEntries();

	/**
	 * Create a new Chronicle Entry and add it to the character's list.
	 * @return The new ChronicleEntry.
	 */
	public ChronicleEntryFacade createChronicleEntry();

	/**
	 * Retrieve the set of notes defined for the character.
	 * @return The character's notes.
	 */
	public ListFacade<NoteFacade> getNotes();

	/**
	 * Update the name of a note.
	 * @param note The note to be renamed.
	 * @param newName The new name.
	 */
	public void renameNote(NoteFacade note, String newName);

	/**
	 * Remove a note from a character. 
	 * @param note The note to be removed.
	 */
	public void deleteNote(NoteFacade note);

	/**
	 * Add a new custom note to the character.
	 */
	public void addNewNote();

	/**
	 * Set the contents of a note.
	 * @param note The note to be updated.
	 * @param text The new contents of the note.
	 */
	public void setNote(NoteFacade note, String text);

	/**
	 * Retrieve the value for a text only biography field.
	 * @param field The field to be queried. 
	 * @return the value of the field.
	 */
	public ReferenceFacade<String> getBiographyField(BiographyField field);

	/**
	 * Update the value of a text only biography field.
	 * @param field The field to be updated. 
	 * @param newValue The new value of the field.
	 */
	public void setBiographyField(BiographyField field, String newValue);

	/**
	 * @return The custom BiographyFields held for this character.
	 */
	public ListFacade<BiographyField> getCustomBiographyFields();

	/**
	 * Add a new field to the list of fields that will be displayed for 
	 * this character.
	 * @param field The BiographyField to be displayed.
	 */
	public void addCustomBiographyField(BiographyField field);

	/**
	 * Remove a new field from the list of fields that will be displayed for 
	 * this character.
	 * @param field The BiographyField to be hidden.
	 */
	public void removeCustomBiographyField(BiographyField field);
}
