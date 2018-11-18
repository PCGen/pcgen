/*
 * Copyright James Dempsey, 2012
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
package pcgen.cdom.enumeration;

/**
 * {@code BiographyField} lists the possible biographical fields which may
 * be edited or suppressed from export.
 */
@SuppressWarnings("nls")
public enum BiographyField
{
	NAME("in_nameLabel", PCAttribute.NAME), PLAYERNAME("in_player", PCAttribute.PLAYERSNAME),
	GENDER("in_gender", null), HANDED("in_handString", null), ALIGNMENT("in_alignString", null),
	DEITY("in_deity", null), AGE("in_age", null), SKIN_TONE("in_appSkintoneColor", null),
	HAIR_COLOR("in_appHairColor", PCAttribute.HAIRCOLOR), HAIR_STYLE("in_style", PCAttribute.HAIRSTYLE),
	EYE_COLOR("in_appEyeColor", PCAttribute.EYECOLOR), HEIGHT("in_height", null), WEIGHT("in_weight", null),
	SPEECH_PATTERN("in_speech", PCAttribute.SPEECHTENDENCY), BIRTHDAY("in_birthday", PCAttribute.BIRTHDAY),
	LOCATION("in_location", PCAttribute.LOCATION), CITY("in_home", null), REGION("in_region", null),
	BIRTHPLACE("in_birthplace", PCAttribute.BIRTHPLACE),
	PERSONALITY_TRAIT_1("in_personality1", PCAttribute.PERSONALITY1),
	PERSONALITY_TRAIT_2("in_personality2", PCAttribute.PERSONALITY2), PHOBIAS("in_phobias", PCAttribute.PHOBIAS),
	INTERESTS("in_interest", PCAttribute.INTERESTS), CATCH_PHRASE("in_phrase", PCAttribute.CATCHPHRASE);

	private final String il8nKey;
	private final PCAttribute pcattr;

	BiographyField(final String key, final PCAttribute pcattr)
	{
		il8nKey = key;
		this.pcattr = pcattr;
	}

	/**
	 * @return the il8nKey
	 */
	public String getIl8nKey()
	{
		return il8nKey;
	}

	public PCAttribute getPcattr()
	{
		return pcattr;
	}
}
