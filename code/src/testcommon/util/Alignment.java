/*
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
package util;

import pcgen.core.Globals;
import pcgen.rules.context.AbstractReferenceContext;
import plugin.lsttokens.testsupport.BuildUtilities;

public final class Alignment
{
    private Alignment()
    {
    }

    public static void createAllAlignments()
    {
        AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
        ref.importObject(BuildUtilities.createAlignment("Lawful Good", "LG"));
        ref.importObject(BuildUtilities.createAlignment("Lawful Neutral", "LN"));
        ref.importObject(BuildUtilities.createAlignment("Lawful Evil", "LE"));
        ref.importObject(BuildUtilities.createAlignment("Neutral Good", "NG"));
        ref.importObject(BuildUtilities.createAlignment("True Neutral", "TN"));
        ref.importObject(BuildUtilities.createAlignment("Neutral Evil", "NE"));
        ref.importObject(BuildUtilities.createAlignment("Chaotic Good", "CG"));
        ref.importObject(BuildUtilities.createAlignment("Chaotic Neutral", "CN"));
        ref.importObject(BuildUtilities.createAlignment("Chaotic Evil", "CE"));
        ref.importObject(BuildUtilities.createAlignment("None", "NONE"));
        ref.importObject(BuildUtilities.createAlignment("Deity's", "Deity"));
    }
}
