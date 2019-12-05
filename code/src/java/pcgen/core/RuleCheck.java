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
package pcgen.core;

import java.net.URI;

import pcgen.cdom.base.Loadable;
import pcgen.cdom.reference.CDOMSingleRef;

public final class RuleCheck implements Loadable
{
    private String ruleName;
    private String ruleKey;
    private String ruleDescription = "";
    private String parm = "";
    private String var = "";
    private CDOMSingleRef<RuleCheck> excludeKey;
    private boolean isEnabledByDefault = false;
    private URI sourceURI;

    @Override
    public URI getSourceURI()
    {
        return sourceURI;
    }

    @Override
    public void setSourceURI(URI source)
    {
        sourceURI = source;
    }

    @Override
    public boolean isInternal()
    {
        return false;
    }

    @Override
    public boolean isType(String type)
    {
        return false;
    }

    public void setDefault(Boolean set)
    {
        isEnabledByDefault = set;
    }

    public boolean getDefault()
    {
        return isEnabledByDefault;
    }

    public void setDesc(String description)
    {
        ruleDescription = description;
    }

    public String getDesc()
    {
        return ruleDescription;
    }

    public void setExclude(CDOMSingleRef<RuleCheck> ref)
    {
        excludeKey = ref;
    }

    public boolean isExclude()
    {
        return (excludeKey != null);
    }

    public CDOMSingleRef<RuleCheck> getExclude()
    {
        return excludeKey;
    }

    /**
     * Returns the unique key for this Rule
     *
     * @return key
     */
    @Override
    public String getKeyName()
    {
        return ruleKey;
    }

    /**
     * Sets the Name (and key if not already set)
     *
     * @param name set name to
     */
    @Override
    public void setName(String name)
    {
        ruleName = name;

        if (ruleKey == null)
        {
            ruleKey = name;
        }
    }

    @Override
    public String getDisplayName()
    {
        return ruleName;
    }

    public String getName()
    {
        return ruleName;
    }

    /**
     * @param aString set parm, key and var to
     */
    public void setParameter(final String aString)
    {
        parm = aString;

        if (var.length() <= 0)
        {
            var = aString;
        }
    }

    public String getParameter()
    {
        return parm;
    }

    /**
     * @param aString set key and var to
     */
    public void setVariable(final String aString)
    {
        var = aString;
    }

    public String getVariable()
    {
        return var;
    }

    public void setKeyName(String key)
    {
        ruleKey = key;
    }
}
