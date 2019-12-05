/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.persistence;

/**
 * Exception thrown by the Persistance Layer
 */
public final class PersistenceLayerException extends Exception
{

    /**
     * Constructs an instance of {@code PersistenceLayerException} with the specified detail message.
     *
     * @param msg the detail message.
     */
    public PersistenceLayerException(String msg)
    {
        super(msg);
    }

    /**
     * @param rootCause the root cause of the exception.
     */
    public PersistenceLayerException(Throwable rootCause)
    {
        super(rootCause);
    }

    /**
     * Constructs an instance of {@code PersistenceLayerException} with the specified {@link Throwable rootCause}
     * and the specified detail message.
     *
     * @param msg       the detail message.
     * @param rootCause the root cause of the exception.
     */
    public PersistenceLayerException(String msg, Throwable rootCause)
    {
        super(msg, rootCause);
    }
}
