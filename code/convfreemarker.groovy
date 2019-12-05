/*
 * Copyright 2014 (C) James Dempsey
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

/**
 * This script converts a PCGen output sheet to FreeMarker. Note it is expected
 * that manual tidy-up will be required afterwards.
 *
 * Usage: groovy convfreemarker.groovy sheetname [outputtemplatename]
 */
import java.util.regex.Matcher

// Check parameters
def usage = "Usage: groovy convfreemarker.groovy sheetname [outputtemplatename]"
if (args.length != 1) {
    println "Provide exactly one argument"
    println usage
    System.exit(1)
}

// Set up our file names, default output name is inputname with .ftl appended.
def source = args[0]
def target = source + '.ftl'
if (args.length > 1) {
    target = args[1]
}

def pcgenExportFile = new File(source)

def freemarkerFile = new File(target)
freemarkerFile.write('<#ftl encoding="UTF-8" strip_whitespace=true >\r\n')

// Process each line in the inout file, writing the updated line to output
int linenum = 0;
int numChanged = 0;
int numSkipped = 0;
int numWarn = 0
pcgenExportFile.eachLine {
    linenum++

    def line = it
    def origLine = it


    // FOR loops - static values
    // |FOR,%range,0,5,1,0|
    // becomes <@loop from=0 to=5 ; range , range_has_next >
    line = line.replaceAll("\\|FOR,\\%([a-zA-Z0-9]+),([0-9]+),([0-9]+),1,0\\|", /<@loop from=$2 to=$3 ; $1 , $1_has_next>/)

    // FOR loops - dynamic values
    // |FOR,%spellbook,1,1,1,1|
    line = line.replaceAll('\\|FOR,\\%([a-zA-Z0-9]+),1,1,1,1\\|', '<#assign $1 = 1 /> <#-- TODO: Matching </@loop> will need to be manually removed -->')
    // |FOR,%class,0,0,1,1|
    line = line.replaceAll('\\|FOR,\\%([a-zA-Z0-9]+),0,0,1,1\\|', '<#assign $1 = 0 /> <#-- TODO: Matching </@loop> will need to be manually removed -->')
    // |FOR,%class,0,COUNT[CLASSES]-1,1,0|
    // becomes <@loop from=0 to=pcvar('COUNT[CLASSES]-1') ; class , class_has_next >
    line = line.replaceAll('\\|FOR,\\%([a-zA-Z0-9]+),([0-9]+),([-_+=.,% A-Za-z0-9"()\\[\\]]+),1,0\\|', /<@loop from=$2 to=pcvar('$3') ; $1 , $1_has_next>/)
    line = line.replaceAll('\\|FOR,\\%([a-zA-Z0-9]+),([0-9]+),([-_+=.,% A-Za-z0-9"()\\[\\]]+),1,([12])\\|', /<@loop from=$2 to=pcvar('$3') ; $1 , $1_has_next> <#-- TODO: Loop was of early exit type $4 -->/)
    // |FOR,%spelllevelcount,COUNT[SPELLSINBOOK.%class.%spellbook.%level],COUNT[SPELLSINBOOK.%class.%spellbook.%level],1,0|
    line = line.replaceAll('\\|FOR,\\%([a-zA-Z0-9]+),([-_+=.% A-Za-z0-9"()\\[\\]]+),([-_+=.,% A-Za-z0-9"()\\[\\]]+),1,0\\|', /<@loop from=pcvar('$2') to=pcvar('$3') ; $1 , $1_has_next>/)
    line = line.replaceAll('\\|FOR,\\%([a-zA-Z0-9]+),([-_+=.% A-Za-z0-9"()\\[\\]]+),([-_+=.,% A-Za-z0-9"()\\[\\]]+),1,([12])\\|', /<@loop from=pcvar('$2') to=pcvar('$3') ; $1 , $1_has_next> <#-- TODO: Loop was of early exit type $4 -->/)
    line = line.replaceAll("\\|ENDFOR\\|", '</@loop>')

    // If tests
    def lhsIdenitfierChars = 'A-Za-z0-9%.;=_ "\\[\\]\\(\\)-'
    // |IIF(%spelllevelcount:0)|
    line = line.replaceAll("\\|IIF\\(%([A-Za-z0-9]+):([0-9]+)\\)\\|", '<#if ($1 = $2)>')
    // |IIF(WEAPON.%weap.CATEGORY:BOTH)|
    line = line.replaceAll("\\|IIF\\(([" + lhsIdenitfierChars + "]+):([A-Za-z0-9 -]+)\\)\\|", '<#if (pcstring("$1") = "$2")>')
    // |IIF(countdistinct("ABILITIES";"NAME=Turn Undead")>0)|
    line = line.replaceAll('\\|IIF\\(([' + lhsIdenitfierChars + ']+)>([0-9]+)\\)\\|', /<#if (pcvar('$1') > $2)>/)
    // |IIF(countdistinct("ABILITIES";"NAME=Turn Undead")==0)|
    line = line.replaceAll('\\|IIF\\(([' + lhsIdenitfierChars + ']+)==([0-9]+)\\)\\|', /<#if (pcvar('$1') = $2)>/)
    line = line.replaceAll("\\|ELSE\\|", "<#else>")
    line = line.replaceAll("\\|ENDIF\\|", '</#if>')

    // |OIF(EVEN:%spell,<tr class="nobrkg">,<tr class="nobrkw">)|
    line = line.replaceAll('\\|OIF\\(EVEN:(%[A-Za-z0-9]+),([^,]+),([^,]+)\\)\\|', '<#if ($1 % 2 = 0)>$2<#else>$3</#if>')

    // Equipsets
    // |EQSET.START|
    line = line.replaceAll("^\\|EQSET\\.START\\|", '<@equipsetloop>')
    // |EQSET.START|
    line = line.replaceAll("^\\|EQSET\\.END\\|", '</@equipsetloop>')

    // Filter tags - uggh
    // |%VAR.RageTimes.GTEQ.1|
    line = line.replaceAll("^\\|%([^\\|]+)\\.GTEQ\\.([0-9]+)\\|", '<#if (pcvar("$1") >= $2) >')
    // |%BARBARIAN=1|
    line = line.replaceAll("^\\|%([^\\|]+)=([0-9]+)\\|", '<#if (pcvar("$1") >= $2) >')
    // |%COUNT[SA]|
    line = line.replaceAll("^\\|%([^\\|]+)\\|", '<#if (pcvar("$1") > 0) >')
    line = line.replaceAll("^\\|%\\|", '</#if>')

    // Raw variables
    // |%level|
    replaceStr = Matcher.quoteReplacement('${') + "\$1}";
    line = line.replaceAll("\\|%([A-Za-z0-9]+)\\|", replaceStr)

    // Formatting tags
    line = line.replaceAll("\\|MANUALWHITESPACE\\|", '<@compress single_line=true><#-- TODO: Add <#t> at the end of each following line with output to ensure no extra spaces are output. -->')
    line = line.replaceAll("\\|ENDMANUALWHITESPACE\\|", '</@compress>')
    replaceStr = Matcher.quoteReplacement('${') + "' '}";
    line = line.replaceAll("\\|SPACE\\|", replaceStr)

    // |IIF(%class<%class!MAX)|
    line = line.replaceAll("\\|IIF\\(%([a-zA-Z0-9+-]+)<%([a-zA-Z0-9+-]+)\\!MAX\\)\\|", "<#if (\$1_has_next)>")
    line = line.replaceAll("\\|IIF\\(%([a-zA-Z0-9+-]+):%([a-zA-Z0-9+-]+)\\!MAX\\)\\|", "<#if (\$1_has_next)>")

    // General tags
    def replaceStr = Matcher.quoteReplacement('${pcstring(') + "'\$1')}";
    line = line.replaceAll('\\|(?!FOR)(?!IIF)([_ A-Za-z0-9.=%/*\\(\\)",-]+)\\|', replaceStr)
    replaceStr = Matcher.quoteReplacement('${pcvar(') + "'\$1')}";
    line = line.replaceAll('\\|(?!FOR)(?!IIF)([_ A-Za-z0-9.=%/*\\(\\)",+-]+)\\|', replaceStr)

    // Loop variables
    replaceStr = Matcher.quoteReplacement('${') + "\$1}";
    line = line.replaceAll("%([a-zA-Z0-9+-]+)", replaceStr)

    if (line ==~ ".*\\|.*") {
        // If we couldn't conert everything report the line and revert back to the original content
        println "Unable to convert line ${linenum}: ${origLine} got to ${line}"
        line = origLine
        numSkipped++
    }
    if (line ==~ ".*<#-- TODO.*") {
        numWarn++
    }
    if (line != origLine) {
        numChanged++
    }

    // Output the revised file
    freemarkerFile.append(line + "\r\n")
}

println ""
println "Scanned ${linenum} lines, ${numSkipped} were skipped and ${numChanged} were migrated. ${numWarn} warnings left as TODOs."
println "Output new template to ${freemarkerFile}"
