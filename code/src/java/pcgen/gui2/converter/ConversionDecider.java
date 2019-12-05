/*
 * Copyright 2009 (C) James Dempsey
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
package pcgen.gui2.converter;

import java.util.List;

/**
 * The Interface {@code ConversionDecider} defines a callback for
 * the LSTConverter to ask the user for a decision on ambiguous syntax
 * that needs to be converted.
 */
public interface ConversionDecider
{

    /**
     * Gets the conversion decision. The user will presented with the choice
     * of conversion options. The choiceTokenResults entry for the user's choice
     * will be returned. It is assumed that choiceDescriptions and choiceTokenResults
     * are the same length.
     *
     * @param overallDescription A user readable description of the decision required
     * @param choiceDescriptions A user readable description of each choice
     * @param choiceTokenResults The syntax equivalent to each choice.
     * @param defaultChoice      The index of the initially selected option.
     * @return the conversion decision
     */
    String getConversionDecision(String overallDescription, List<String> choiceDescriptions,
            List<String> choiceTokenResults, int defaultChoice);

    String getConversionInput(String overallDescription);
}
