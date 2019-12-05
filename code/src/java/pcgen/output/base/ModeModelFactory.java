/*
 * Copyright (c) Thomas Parker, 2015.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.output.base;

import pcgen.core.GameMode;

import freemarker.template.TemplateModel;

/**
 * A ModeModelFactory is a class that can generate TemplateModel objects for the
 * current Game Mode.
 */
public interface ModeModelFactory
{
    /**
     * Generates a TemplateModel based on the current Game Mode.
     *
     * @param mode The GameMode TemplateModel should be produced by this
     *             ModeModelFactory
     * @return A TemplateModel produced by this ModeModelFactory
     */
    TemplateModel generate(GameMode mode);
}
