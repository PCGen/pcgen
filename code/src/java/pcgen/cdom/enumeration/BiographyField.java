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
    NAME("in_nameLabel"), PLAYERNAME("in_player"),
    GENDER("in_gender"), HANDED("in_handString"), ALIGNMENT("in_alignString"),
    DEITY("in_deity"), AGE("in_age"), SKIN_TONE("in_appSkintoneColor"),
    HAIR_COLOR("in_appHairColor"), HAIR_STYLE("in_style"),
    EYE_COLOR("in_appEyeColor"), HEIGHT("in_height"), WEIGHT("in_weight"),
    SPEECH_PATTERN("in_speech"), BIRTHDAY("in_birthday"),
    LOCATION("in_location"), CITY("in_home"), REGION("in_region"),
    BIRTHPLACE("in_birthplace"),
    PERSONALITY_TRAIT_1("in_personality1"),
    PERSONALITY_TRAIT_2("in_personality2"), PHOBIAS("in_phobias"),
    INTERESTS("in_interest"), CATCH_PHRASE("in_phrase");

    private final String il8nKey;

    BiographyField(final String key)
    {
        il8nKey = key;
    }

    /**
     * @return the il8nKey
     */
    public String getIl8nKey()
    {
        return il8nKey;
    }
}
