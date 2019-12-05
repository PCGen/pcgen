/*
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 */
package pcgen.io;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * {@code EntityEncoder}<br>
 * Encodes reserved characters and escape sequences as entities<br>
 * Decodes entities as reserved characters and escape sequences
 */
public final class EntityEncoder
{
    private static final String ENCODE = "\\\n\r\f:|[]&";
    private static final EntityMap ENTITIES;
    private static final String ENCODE_LIGHT = "\\\n\r\f|&";
    private static final EntityMap ENTITIES_LIGHT;

    static
    {
        ENTITIES = new EntityMap();
        ENTITIES.put("\n", "&nl;");
        ENTITIES.put("\r", "&cr;");
        ENTITIES.put("\f", "&lf;");
        ENTITIES.put(":", "&colon;");
        ENTITIES.put("|", "&pipe;");
        ENTITIES.put("[", "&lbracket;");
        ENTITIES.put("]", "&rbracket;");
        ENTITIES.put("&", "&amp;");

        ENTITIES_LIGHT = new EntityMap();
        ENTITIES_LIGHT.put("\n", "&nl;");
        ENTITIES_LIGHT.put("\r", "&cr;");
        ENTITIES_LIGHT.put("\f", "&lf;");
        ENTITIES_LIGHT.put("|", "&pipe;");
        ENTITIES_LIGHT.put("&", "&amp;");
    }

    /**
     * Private constructor to disable instantiation.
     */
    private EntityEncoder()
    {
    }

    /**
     * decode characters.
     * {@literal
     * "\n" <- "&nl;"
     * "\r" <- "&cr;"
     * "\f" <- "&lf;"
     * ":" <- "&colon;"
     * "|" <- "&pipe;"
     * "[" <- "&lbracket;"
     * "]" <- "&rbracket;"
     * "&" <- "&amp;"
     * }
     * <br>author: Thomas Behr 09-09-02
     *
     * @param s the String to decode
     * @return the decoded String
     */
    public static String decode(String s)
    {
        final StringBuilder buffer = new StringBuilder();
        final StringTokenizer tokens = new StringTokenizer(s, "&;", true);

        while (tokens.hasMoreTokens())
        {
            String cToken = tokens.nextToken();

            if ("&".equals(cToken))
            {
                String tok1 = null;
                String tok2 = null;

                try
                {
                    tok1 = tokens.nextToken();
                    tok2 = tokens.nextToken();
                    buffer.append(ENTITIES.get(cToken + tok1 + tok2));
                } catch (NoSuchElementException exc)
                {
                    buffer.append(cToken);

                    if (tok1 != null)
                    {
                        buffer.append(tok1);

                        if (tok2 != null)
                        {
                            buffer.append(tok2);
                        }
                    }
                }
            } else
            {
                buffer.append(cToken);
            }
        }

        return buffer.toString();
    }

    /**
     * encode characters.
     * {@literal
     * "\n" -> "&nl;"
     * "\r" -> "&cr;"
     * "\f" -> "&lf;"
     * ":" -> "&colon;"
     * "|" -> "&pipe;"
     * "[" -> "&lbracket;"
     * "]" -> "&rbracket;"
     * "&" -> "&amp;"
     * }
     * <br>author: Thomas Behr 09-09-02
     *
     * @param s the String to encode
     * @return the encoded String
     */
    public static String encode(String s)
    {
        final StringBuilder buffer = new StringBuilder();
        if (s != null)
        {
            final StringTokenizer tokens = new StringTokenizer(s, ENCODE, true);

            while (tokens.hasMoreTokens())
            {
                buffer.append(ENTITIES.get(tokens.nextToken()));
            }
        }
        return buffer.toString();
    }

    /**
     * Encode the characters.
     * {@literal
     * "\n" -> "&nl;"
     * "\r" -> "&cr;"
     * "\f" -> "&lf;"
     * "|" -> "&pipe;"
     * "&" -> "&amp;"
     * }
     * Note that this must be a subset of the encode function as
     * the same decode function is used to decode these values.
     *
     * @param s the String to encode
     * @return the encoded String
     */
    public static String encodeLight(String s)
    {
        final StringBuilder buffer = new StringBuilder();
        if (s != null)
        {
            final StringTokenizer tokens = new StringTokenizer(s, ENCODE_LIGHT, true);

            while (tokens.hasMoreTokens())
            {
                buffer.append(ENTITIES_LIGHT.get(tokens.nextToken()));
            }
        }
        return buffer.toString();
    }
}

final class EntityMap
{
    private final Map<String, String> map = new HashMap<>();

    /**
     * Get value
     *
     * @param key
     * @return value
     */
    public String get(String key)
    {
        final String value = map.get(key);

        return (value == null) ? key : value;
    }

    /**
     * Put value in map
     *
     * @param key
     * @param value
     */
    public void put(String key, String value)
    {
        map.put(key, value);
        map.put(value, key);
    }
}
