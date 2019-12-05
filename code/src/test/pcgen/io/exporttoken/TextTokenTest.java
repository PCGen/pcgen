/*
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.io.exporttoken;

import static org.junit.Assert.assertEquals;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.NumericPCAttribute;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;

import plugin.exporttokens.TextToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code TextTokenTest} tests the functioning of the TEXT
 * token processing code.
 */
public class TextTokenTest extends AbstractCharacterTestCase
{
    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        PlayerCharacter character = getCharacter();
        character.setName("The Vitamins are in my Fresh Brussels Sprouts");
        character.setPCAttribute(PCStringKey.INTERESTS, "one potatoe. two potatoe. mORe");
    }

    /**
     * Test the output for positive numbers with fractions.
     */
    @Test
    public void testTextFormatting()
    {
        TextToken tok = new TextToken();
        ExportHandler eh = ExportHandler.createExportHandler(null);
        PlayerCharacter character = getCharacter();

        assertEquals("TEXT.LOWER.NAME",
                "the vitamins are in my fresh brussels sprouts", tok.getToken(
                        "TEXT.LOWER.NAME", character, eh));
        assertEquals("TEXT.UPPER.NAME",
                "THE VITAMINS ARE IN MY FRESH BRUSSELS SPROUTS", tok.getToken(
                        "TEXT.UPPER.NAME", character, eh));
        assertEquals("TEXT.SENTENCE.NAME",
                "The vitamins are in my fresh brussels sprouts", tok.getToken(
                        "TEXT.SENTENCE.NAME", character, eh));
        assertEquals("TEXT.SENTENCE.NAME",
                "The vitamins are in my fresh brussels sprouts", tok.getToken(
                        "TEXT.SENTENCE.NAME", character, eh));
        character.setPCAttribute(PCStringKey.NAME, "The Vitamins are in my Fresh Brussels Sprouts");
        assertEquals("TEXT.SENTENCE.INTERESTS",
                "One potatoe. Two potatoe. More", tok.getToken(
                        "TEXT.SENTENCE.INTERESTS", character, eh));
        assertEquals("TEXT.TITLE.NAME",
                "The Vitamins Are In My Fresh Brussels Sprouts", tok.getToken(
                        "TEXT.TITLE.NAME", getCharacter(), eh));
        assertEquals("TEXT.TITLE.NAME", "One Potatoe. Two Potatoe. More", tok
                .getToken("TEXT.TITLE.INTERESTS", character, eh));
    }

    /**
     * Test the output for negative numbers with fractions.
     */
    @Test
    public void testNumSuffix()
    {
        TextToken tok = new TextToken();
        ExportHandler eh = ExportHandler.createExportHandler(null);
        PlayerCharacter character = getCharacter();

        character.setPCAttribute(NumericPCAttribute.AGE, 1);
        assertEquals("Suffix 1", "st", tok.getToken("TEXT.NUMSUFFIX.AGE",
                getCharacter(), eh));
        character.setPCAttribute(NumericPCAttribute.AGE, 2);
        assertEquals("Suffix 2", "nd", tok.getToken("TEXT.NUMSUFFIX.AGE",
                getCharacter(), eh));
        character.setPCAttribute(NumericPCAttribute.AGE, 3);
        assertEquals("Suffix 3", "rd", tok.getToken("TEXT.NUMSUFFIX.AGE",
                getCharacter(), eh));
        character.setPCAttribute(NumericPCAttribute.AGE, 4);
        assertEquals("Suffix 4", "th", tok.getToken("TEXT.NUMSUFFIX.AGE",
                getCharacter(), eh));
        character.setPCAttribute(NumericPCAttribute.AGE, 11);
        assertEquals("Suffix 11", "th", tok.getToken("TEXT.NUMSUFFIX.AGE",
                getCharacter(), eh));
        character.setPCAttribute(NumericPCAttribute.AGE, 12);
        assertEquals("Suffix 12", "th", tok.getToken("TEXT.NUMSUFFIX.AGE",
                getCharacter(), eh));
        character.setPCAttribute(NumericPCAttribute.AGE, 13);
        assertEquals("Suffix 13", "th", tok.getToken("TEXT.NUMSUFFIX.AGE",
                getCharacter(), eh));
        character.setPCAttribute(NumericPCAttribute.AGE, 14);
        assertEquals("Suffix 14", "th", tok.getToken("TEXT.NUMSUFFIX.AGE",
                getCharacter(), eh));
        character.setPCAttribute(NumericPCAttribute.AGE, 21);
        assertEquals("Suffix 21", "st", tok.getToken("TEXT.NUMSUFFIX.AGE",
                getCharacter(), eh));
        character.setPCAttribute(NumericPCAttribute.AGE, 22);
        assertEquals("Suffix 22", "nd", tok.getToken("TEXT.NUMSUFFIX.AGE",
                getCharacter(), eh));
        character.setPCAttribute(NumericPCAttribute.AGE, 23);
        assertEquals("Suffix 23", "rd", tok.getToken("TEXT.NUMSUFFIX.AGE",
                getCharacter(), eh));
        character.setPCAttribute(NumericPCAttribute.AGE, 24);
        assertEquals("Suffix 24", "th", tok.getToken("TEXT.NUMSUFFIX.AGE",
                getCharacter(), eh));
        character.setPCAttribute(NumericPCAttribute.AGE, 133);
        assertEquals("Suffix 133", "rd", tok.getToken("TEXT.NUMSUFFIX.AGE",
                getCharacter(), eh));
    }

    /**
     * Test the output for negative numbers with fractions.
     */
    @Test
    public void testNumSuffixDirect()
    {
        TextToken tok = new TextToken();
        ExportHandler eh = ExportHandler.createExportHandler(null);

        assertEquals("Suffix 1", "st", tok.getToken("TEXT.NUMSUFFIX.1",
                getCharacter(), eh));
        assertEquals("Suffix 2", "nd", tok.getToken("TEXT.NUMSUFFIX.2",
                getCharacter(), eh));
        assertEquals("Suffix 3", "rd", tok.getToken("TEXT.NUMSUFFIX.3",
                getCharacter(), eh));
        assertEquals("Suffix 4", "th", tok.getToken("TEXT.NUMSUFFIX.4",
                getCharacter(), eh));
        assertEquals("Suffix 12", "th", tok.getToken("TEXT.NUMSUFFIX.12",
                getCharacter(), eh));
        assertEquals("Suffix 133", "rd", tok.getToken("TEXT.NUMSUFFIX.133",
                getCharacter(), eh));
        assertEquals("Suffix 133", "rd", tok.getToken("TEXT.NUMSUFFIX.133.0",
                getCharacter(), eh));
    }

}
