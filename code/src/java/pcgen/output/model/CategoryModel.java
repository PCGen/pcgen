/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Objects;

import pcgen.cdom.base.Category;

import freemarker.template.TemplateScalarModel;

/**
 * A CategoryModel wraps a Category object into a TemplateScalarModel
 */
public class CategoryModel implements TemplateScalarModel
{
    /**
     * The underlying Category object
     */
    private final Category<?> category;

    /**
     * Constructs a new CategoryModel with the given underlying Category
     *
     * @param cat The Category this CategoryModel wraps
     */
    public CategoryModel(Category<?> cat)
    {
        Objects.requireNonNull(cat, "Category cannot be null");
        this.category = cat;
    }

    @Override
    public String getAsString()
    {
        return category.getDisplayName();
    }

}
