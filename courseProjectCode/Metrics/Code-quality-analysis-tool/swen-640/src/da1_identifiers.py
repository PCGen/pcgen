import re
from typing import Any, Dict, List
import xml.etree.ElementTree as ET

from src.db_utils import exec_commit


def extract_identifiers_dom(xml_str: str) -> List[Dict[str, Any]]:
    """Extract identifier rows using a DOM-style approach (ElementTree/XPath-style finds).

    Parameters:
    - xml_str: srcML XML document as string

    Returns:
    - List of identifier dicts, each with keys:
      - name (str): identifier text
      - kind (str): one of 'function', 'parameter', 'variable', 'class'
      - convention (str): naming convention detected
      - length (int): character count
      - n_tokens (int): token count after splitting
      - scope (str): one of 'global', 'local', 'parameter'

    Behavior:
    - Parse the entire XML tree with ElementTree
    - Use `.iter()` or `.findall()` to locate function/class/parameter/decl nodes
    - For each <name> node, classify its context (function name vs parameter name vs variable)
    - Return one row per identifier found

    Examples:
    >>> xml = '<unit><function><name>process</name></function></unit>'
    >>> ids = extract_identifiers_dom(xml)
    >>> ids[0]['name']
    'process'
    >>> ids[0]['kind']
    'function'

    Implementation hints:
    - Use `ET.fromstring(xml_str)` to parse
    - Namespace-aware: strip namespace prefixes with helper (e.g., tag.rsplit('}', 1)[1])
    - Iterate over functions first, then parameters within, then local variables
    - Check parent/ancestor tags to determine context (function vs class vs global)
    """
    root = ET.fromstring(xml_str)

    #  strip namespace prefixes
    for item in root.iter():
        if "}" in item.tag:
            item.tag = item.tag.rsplit('}', 1)[1]

    dictList = []

    # Example XPath-style searches (ElementTree subset)
    for fn in root.findall('.//function'):
        name_node = fn.find('./name')
        if name_node is not None and (name_node.text or '').strip():
            fn_name = name_node.text.strip()
            newDict = {'name': fn_name, 'kind': "function"}
            dictList.append(newDict)

            for param in fn.findall('.//parameter//decl'):
                name_node = param.find('./name')
                if name_node is not None and (name_node.text or '').strip():
                    param_name = name_node.text.strip()
                    newDict = {'name': param_name, 'kind': "parameter", 'scope': "parameter"}
                    dictList.append(newDict)

            for local in fn.findall('.//decl_stmt//decl'):
                name_node = local.find('./name')
                if name_node is not None and (name_node.text or '').strip():
                    var_name = name_node.text.strip()
                    newDict = {'name': var_name, 'kind': "variable", 'scope': "local"}
                    dictList.append(newDict)

    for clas in root.findall('.//class'):
        name_node = clas.find('./name')
        if name_node is not None and (name_node.text or '').strip():
            class_name = name_node.text.strip()
            newDict = {'name': class_name, 'kind': "class"}
            dictList.append(newDict)

    for globa in root.findall('./decl_stmt//decl'):
        name_node = globa.find('./name')
        if name_node is not None and (name_node.text or '').strip():
            gvar_name = name_node.text.strip()
            newDict = {'name': gvar_name, 'kind': "variable", 'scope': "global"}
            dictList.append(newDict)

    for objectDict in dictList:
        # regex my despised

        # snake case checker
        if re.fullmatch(r"^[a-z0-9]*(?:_[a-z0-9]+)*$", objectDict["name"]):
            objectDict["convention"] = "snake_case"

        # pascal case checker
        elif re.fullmatch(r"^(?:[A-Z][a-z0-9]*)*$", objectDict["name"]):
            objectDict["convention"] = "PascalCase"

        # camel case checker
        elif re.fullmatch(r"^[a-z0-9]*(?:[A-Z][a-z0-9]*)*$", objectDict["name"]):
            objectDict["convention"] = "camelCase"

        # screaming snake checker
        elif re.fullmatch(r"^[A-Z0-9]*(?:_[A-Z0-9]+)*$", objectDict["name"]):
            objectDict["convention"] = "SCREAMING_SNAKE"

        else:
            objectDict["convention"] = "other"

        if objectDict["convention"] == "snake_case" or objectDict["convention"] == "SCREAMING_SNAKE":
            tokens = objectDict["name"].split("_")
            objectDict["n_tokens"] = len(tokens)
            objectDict["tokens"] = tokens
        elif objectDict["convention"] == "other":
            objectDict["n_tokens"] = 1  # if we cant figure out the pattern, it's getting called a single token.
            objectDict["tokens"] = objectDict["name"]
        else:
            tokens = list(filter(None, re.split(r'(?=[A-Z])', objectDict["name"])))
            objectDict["n_tokens"] = len(tokens)
            objectDict["tokens"] = tokens

        objectDict["length"] = len(objectDict["name"])

    return dictList


def aggregate_identifier_features(identifiers: List[Dict[str, Any]]) -> Dict[str, Any]:
    """Compute file-level aggregate metrics from identifier rows.

    Parameters:
    - identifiers: list of identifier dicts from extract_identifiers_dom/sax

    Returns:
    - dict with keys:
      - n_identifiers (int): total count
      - avg_identifier_length (float): mean character length
      - avg_tokens_per_identifier (float): mean tokens per name
      - vocab_size (int): unique normalized tokens
      - vocab_diversity (float): unique tokens / total tokens (0.0 to 1.0)
      - pct_snake_case (float): fraction using snake_case
      - pct_camel_case (float): fraction using camelCase
      - pct_pascal_case (float): fraction using PascalCase

    Behavior:
    - Return all metrics as 0/0.0 if identifiers list is empty
    - Compute means with simple arithmetic (sum / count)
    - Vocabulary = set of all unique tokens (after lowercasing and splitting)
    - Diversity = len(vocab) / total_token_count (avoid division by zero)

    Examples:
    >>> ids = [{'name': 'getUser', 'convention': 'camelCase', 'length': 7, 'tokens': ['get', 'user']}]
    >>> agg = aggregate_identifier_features(ids)
    >>> agg['n_identifiers']
    1
    >>> agg['pct_camel_case']
    1.0

    Implementation hints:
    - Use sum() and len() for averages
    - Build vocab with set comprehension: {token for row in identifiers for token in row['tokens']}
    - Count conventions with list comprehension and sum(1 for ...)
    """
    size = len(identifiers)
    length = 0
    tokens = 0
    uniqueTokens = []
    snake = 0
    camel = 0
    pascal = 0
    outputDict = {"n_identifiers": 0, "avg_identifier_length": 0.0,
                  "avg_tokens_per_identifier": 0.0,
                  "vocab_size": 0, "vocab_diversity": 0.0, "pct_snake_case": 0.0,
                  "pct_camel_case": 0.0, "pct_pascal_case": 0.0}

    for identifiedDict in identifiers:
        length += identifiedDict["length"]
        tokens += identifiedDict["n_tokens"]

        for token in identifiedDict["tokens"]:
            if token not in uniqueTokens:
                uniqueTokens.append(token)

        if identifiedDict["convention"] == "camelCase":
            camel += 1
        elif identifiedDict["convention"] == "snake_case" or identifiedDict["convention"] == "SCREAMING_SNAKE":
            snake += 1
        elif identifiedDict["convention"] == "PascalCase":
            pascal += 1

        uniqueSize = len(uniqueTokens)
        outputDict = {"n_identifiers": size, "avg_identifier_length": length / size,
                      "avg_tokens_per_identifier": tokens / size,
                      "vocab_size": uniqueSize, "vocab_diversity": uniqueSize / tokens, "pct_snake_case": snake / size,
                      "pct_camel_case": camel / size, "pct_pascal_case": pascal / size}
    return outputDict


def build_file_identifier_dataset(xml_by_file: Dict[str, str], parser: str = "dom") -> List[Dict[str, Any]]:
    """Build file-level dataset rows from {file_path: xml_str}.

    Parameters:
    - xml_by_file: dict mapping file paths to srcML XML strings
    - parser: either 'dom' or 'sax' (default 'dom')

    Returns:
    - List of dicts, one per file, with keys:
      - file_path (str)
      - n_identifiers (int)
      - avg_identifier_length (float)
      - ... (all metrics from aggregate_identifier_features)

    Behavior:
    - Raise ValueError if parser is not 'dom' or 'sax'
    - Process files in sorted order (for reproducibility)
    - For each file: extract identifiers → aggregate → append to output

    Examples:
    >>> xml_map = {'a.py': '<unit>...</unit>', 'b.py': '<unit>...</unit>'}
    >>> dataset = build_file_identifier_dataset(xml_map, parser='dom')
    >>> len(dataset)
    2
    >>> dataset[0]['file_path']
    'a.py'

    Implementation hints:
    - Normalize parser string: parser.lower().strip()
    - Use sorted(xml_by_file.keys()) for deterministic iteration
    - Call extract_identifiers_dom or extract_identifiers_sax based on parser choice
    - Merge file_path with aggregate dict: {'file_path': path, **agg}
    """

    #  I cut out sax too as it's irrelevant here as Im not implementing it.
    if parser != "dom":
        raise ValueError("parser must be 'dom'")

    fileData = []
    for key in sorted(xml_by_file.keys()):

        line_count = xml_by_file[key].count("\n")  # number of lines for file, relevant to some project pieces

        extractedData = extract_identifiers_dom(xml_by_file[key])
        aggData = aggregate_identifier_features(extractedData)

        function_count = sum(1 for i in extractedData if i['kind'] == 'function')  # sum function count from extractedData

        exec_commit("""INSERT INTO file_data (file_path, file_line_count, function_count)
                       VALUES (%(file_path)s, %(line_count)s, %(function_count)s) ON CONFLICT (file_path) DO
        UPDATE SET
            file_line_count = EXCLUDED.file_line_count,
            function_count = EXCLUDED.function_count""",
                    {"file_path": key, "line_count": line_count, "function_count": function_count})

        fileData.append({"file_path": key, **aggData})

    return fileData
