/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.testsupport;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.formula.PluginFunctionLibrary;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.PrerequisiteTestFactory;
import pcgen.gui2.converter.TokenConverter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.ModifierFactory;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.util.PJEP;

public final class TokenRegistration
{

    public static final Set<String> PPI_SET = new HashSet<>();
    public static final Set<String> PW_SET = new HashSet<>();
    public static final Set<LstToken> TOKEN_SET = new HashSet<>();
    public static final Set<Token> EXPORT_SET = new HashSet<>();
    public static final Set<ModifierFactory<?>> M_SET = new HashSet<>();

    private TokenRegistration()
    {
    }

    public static void register(PrerequisiteParserInterface ppi)
            throws PersistenceLayerException
    {
        String s = Arrays.asList(ppi.kindsHandled()).toString();
        if (!PPI_SET.contains(s))
        {
//			try {
            PreParserFactory.getInstance().register(ppi);
            PPI_SET.add(s);
            TokenLibrary.addToTokenMap(ppi);
//			} catch (PersistenceLayerException e) {
//				Logging.log(Logging.WARNING,
//						"Ignoring error while registering parser for test", e);
//			}
        }
    }

    public static void register(LstToken token)
    {
        if (!TOKEN_SET.contains(token))
        {
            TokenLibrary.addToTokenMap(token);
            TokenStore.inst().addToTokenMap(token);
            TOKEN_SET.add(token);
            if (token instanceof QualifierToken)
            {
                TokenLibrary.addToQualifierMap((QualifierToken<?>) token);
            }
            if (token instanceof PrimitiveToken)
            {
                TokenLibrary.addToPrimitiveMap((PrimitiveToken<?>) token);
            }
        }
    }

    public static void register(Token token)
    {
        if (!EXPORT_SET.contains(token))
        {
            EXPORT_SET.add(token);
            ExportHandler.addToTokenMap(token);
        }
    }

    public static void clearTokens()
    {
        TokenLibrary.reset();
        TokenStore.reset();
        TOKEN_SET.clear();
        PPI_SET.clear();
        PW_SET.clear();
        EXPORT_SET.clear();
        M_SET.clear();
        PreParserFactory.clear();
        PrerequisiteTestFactory.clear();
        PrerequisiteWriterFactory.clear();
        PJEP.clear();
        ExportHandler.clear();
        TokenConverter.clear();
        TokenConverter.clearConstants();
        PluginFunctionLibrary.clear();
    }

    public static void register(PrerequisiteWriterInterface writer)
            throws PersistenceLayerException
    {
        String s = writer.kindHandled();
        if (!PW_SET.contains(s))
        {
            PrerequisiteWriterFactory.getInstance().register(writer);
            PW_SET.add(s);
        }
    }

    public static void register(Class<? extends BonusObj> cl)
    {
        try
        {
            TokenLibrary.addBonusClass(cl);
        } catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public static void register(ModifierFactory<?> m)
    {
        if (!M_SET.contains(m))
        {
            TokenLibrary.addToModifierMap(m);
            M_SET.add(m);
        }
    }
}
