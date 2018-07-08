/*
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.enumeration;

import java.util.stream.Stream;

import pcgen.system.LanguageBundle;

public enum NotePCAttribute implements StringPCAttribute
{
	ASSETS(PCStringKey.ASSETS, "in_otherAssets"), BIO(PCStringKey.BIO, "in_bio"), COMPANIONS(PCStringKey.COMPANIONS,
			"in_companions"), DESCRIPTION(PCStringKey.DESCRIPTION, "in_descrip"), MAGIC(PCStringKey.MAGIC,
					"in_magicItems"), GMNOTES(PCStringKey.GMNOTES, "in_gmNotes");

	private final PCStringKey stringKey;
	private final String noteName;

	@Override
	public PCStringKey getStringKey()
	{
		return stringKey;
	}

	public static NotePCAttribute getByNoteName(String noteName)
	{
		return Stream.of(NotePCAttribute.values()).filter(x -> x.noteName.equals(noteName)).findFirst().get();
	}

	NotePCAttribute(final PCStringKey stringKey, final String noteNameLocation)
	{
		this.stringKey = stringKey;
		this.noteName = LanguageBundle.getString(noteNameLocation);
	}

	@Override
	public String toString()
	{
		return "NotePCAttribute{" + "stringKey=" + stringKey + ", noteName='" + noteName + '\'' + '}';
	}
}
