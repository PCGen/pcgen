package pcgen.io;

import java.util.List;

import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Compatibility
{

    private Compatibility()
    {
    }

    @Nullable
    static PCTemplate getTemplateFor(PCTemplate template, String templateKey, String feat)
    {
        if (templateKey.charAt(0) == 'L')
        {
            int level = Integer.parseInt(templateKey.substring(1));
            List<PCTemplate> levelTemplates = template.getListFor(ListKey.LEVEL_TEMPLATES);
            for (final PCTemplate templ : levelTemplates)
            {
                if (level == templ.get(IntegerKey.LEVEL))
                {
                    return templ;
                }
            }
        } else
        {
            // Assume 'H'
            int minhd;
            int maxhd;
            String hdString = templateKey.substring(1);
            int minusLoc = hdString.indexOf('-');
            if (minusLoc == -1)
            {
                if (hdString.indexOf('+') == (hdString.length() - 1))
                {
                    minhd = Integer.parseInt(hdString.substring(0, hdString.length() - 1));
                    maxhd = Integer.MAX_VALUE;
                } else
                {
                    minhd = Integer.parseInt(hdString);
                    maxhd = minhd;
                }
            } else
            {
                minhd = Integer.parseInt(hdString.substring(0, minusLoc));
                maxhd = Integer.parseInt(hdString.substring(minusLoc + 1));
            }
            List<PCTemplate> levelTemplates = template.getListFor(ListKey.HD_TEMPLATES);
            for (final PCTemplate templ : levelTemplates)
            {
                if ((minhd == templ.get(IntegerKey.HD_MIN)) && (maxhd == templ.get(IntegerKey.HD_MAX)))
                {
                    return templ;
                }
            }
        }
        Logging.errorPrint(
                "Unable to find appropriate Template for " + templateKey + ":" + feat + " in " + template.getDisplayName());
        return null;
    }

    @NotNull
    public static String getKeyFor(PCTemplate pct)
    {
        Integer level = pct.get(IntegerKey.LEVEL);
        StringBuilder hd = new StringBuilder();
        if (level == null)
        {
            hd.append('H');
            Integer min = pct.get(IntegerKey.HD_MIN);
            Integer max = pct.get(IntegerKey.HD_MAX);
            hd.append(min);
            if (max == Integer.MAX_VALUE)
            {
                hd.append('+');
            } else if (!max.equals(min))
            {
                hd.append('-').append(max);
            }
        } else
        {
            hd.append('L');
            hd.append(level);
        }
        return hd.toString();
    }

    @Nullable
    public static PersistentTransitionChoice<?> processOldAdd(LoadContext context, String first)
    {
        int openParenLoc = first.indexOf('(');
        if (openParenLoc == -1)
        {
            Logging.errorPrint("Expected to have a ( : " + first);
            return null;
        }
        int closeParenLoc = first.lastIndexOf(')');
        if (closeParenLoc == -1)
        {
            Logging.errorPrint("Expected to have a ) : " + first);
            return null;
        }
        String key = first.substring(7, openParenLoc);
        String choices = first.substring(openParenLoc + 1, closeParenLoc);
        String count = "";
        if (closeParenLoc != (first.length() - 1))
        {
            count = first.substring(closeParenLoc + 1) + '|';
        }
        PCClass applied = new PCClass();
        ParseResult pr = context.processSubToken(applied, "ADD", key, count + choices);
        pr.printMessages(context.getSourceURI());
        if (!pr.passed())
        {
            return null;
        }
        context.commit();
        return applied.getListFor(ListKey.ADD).get(0);
    }

}
