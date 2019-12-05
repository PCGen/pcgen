/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.output.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import freemarker.template.TemplateDateModel;

/**
 * A DateModel wraps a Date object into a TemplateScalarModel
 */
public class DateModel implements TemplateDateModel
{
    /**
     * The underlying Date object
     */
    private final LocalDateTime date;

    /**
     * Constructs a new DateModel with the given underlying Date
     *
     * @param d The Date this DateModel wraps
     */
    public DateModel(LocalDateTime d)
    {
        this.date = d;
    }

    @Override
    public Date getAsDate()
    {
        return Date.from(date.toInstant(ZoneOffset.ofHours(0)));
    }

    @Override
    public int getDateType()
    {
        return TemplateDateModel.UNKNOWN;
    }

}
