"""
Tests for DA2 identifier extraction.

IMPORTANT:
You only need to implement ONE parsing approach (DOM, SAX, or XPath).
Delete the test classes for the approaches you did NOT implement:
- If you implemented DOM (ElementTree): Keep TestDOM, delete TestSAX and TestXPath
- If you implemented SAX: Keep TestSAX, delete TestDOM and TestXPath  
- If you implemented XPath (lxml): Keep TestXPath, delete TestDOM and TestSAX

The TestCommon class contains tests that work regardless of your parser choice.
"""
import pytest
from src import da1_identifiers


XML_SAMPLE = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<unit xmlns="http://www.srcML.org/srcML/src" revision="1.0.0" language="Python">
  <decl_stmt><decl><type>int</type><name>GLOBAL_CONST</name></decl></decl_stmt>
  <class>
    <name>DataMiner</name>
  </class>
  <function>
    <type>void</type>
    <name>buildReport</name>
    <parameter_list>
      <parameter><decl><type>int</type><name>max_items</name></decl></parameter>
    </parameter_list>
    <block>
      <decl_stmt><decl><type>int</type><name>localCount</name></decl></decl_stmt>
      <decl_stmt><decl><type>str</type><name>tmp_value</name></decl></decl_stmt>
    </block>
  </function>
</unit>"""


def _by_name(rows):
    return {row["name"]: row for row in rows}


# ============================================================================
# Common tests - these work regardless of which parser you implemented
# ============================================================================

class TestCommon:
    """Tests for parser-agnostic functions (aggregate_*, build_file_identifier_dataset, etc)."""
    
    def test_aggregate_identifier_features_empty(self):
        """Aggregate should handle empty list."""
        agg = da1_identifiers.aggregate_identifier_features([])
        assert agg["n_identifiers"] == 0
        assert agg["vocab_size"] == 0
        assert agg["vocab_diversity"] == 0.0
    
    def test_build_file_identifier_dataset_bad_parser(self):
        """Dataset builder should reject invalid parser names."""
        try:
            da1_identifiers.build_file_identifier_dataset({"a.py": XML_SAMPLE}, parser="invalid")
        except ValueError:
            return
        assert False, "Expected ValueError for unsupported parser"


# ============================================================================
# DOM (ElementTree) tests - DELETE THIS CLASS if you implemented SAX or XPath
# ============================================================================

class TestDOM:
    """Tests for DOM (ElementTree) implementation.
    
    DELETE THIS ENTIRE CLASS if you did NOT implement extract_identifiers_dom().
    """
    
    def test_extract_identifiers_dom_basic(self):
        """DOM should extract all identifiers correctly."""
        rows = da1_identifiers.extract_identifiers_dom(XML_SAMPLE)
        names = _by_name(rows)

        assert "buildReport" in names
        assert names["buildReport"]["kind"] == "function"
        assert names["buildReport"]["convention"] == "camelCase"

        assert "DataMiner" in names
        assert names["DataMiner"]["kind"] == "class"
        assert names["DataMiner"]["convention"] == "PascalCase"

        assert "max_items" in names
        assert names["max_items"]["kind"] == "parameter"
        assert names["max_items"]["scope"] == "parameter"
        assert names["max_items"]["convention"] == "snake_case"

        assert "localCount" in names
        assert names["localCount"]["kind"] == "variable"
        assert names["localCount"]["scope"] == "local"

        assert "GLOBAL_CONST" in names
        assert names["GLOBAL_CONST"]["kind"] == "variable"
        assert names["GLOBAL_CONST"]["scope"] == "global"
        assert names["GLOBAL_CONST"]["convention"] == "SCREAMING_SNAKE"
    
    def test_aggregate_identifier_features_nonempty_dom(self):
        """Aggregate metrics should be computed correctly from DOM extraction."""
        rows = da1_identifiers.extract_identifiers_dom(XML_SAMPLE)
        agg = da1_identifiers.aggregate_identifier_features(rows)

        assert agg["n_identifiers"] == len(rows)
        assert agg["avg_identifier_length"] > 0
        assert agg["avg_tokens_per_identifier"] > 0
        assert agg["vocab_size"] > 0
        assert 0 <= agg["vocab_diversity"] <= 1
        assert 0 <= agg["pct_snake_case"] <= 1
        assert 0 <= agg["pct_camel_case"] <= 1
        assert 0 <= agg["pct_pascal_case"] <= 1
    
    def test_build_file_identifier_dataset_dom(self):
        """Dataset builder should work with parser='dom'."""
        dataset = da1_identifiers.build_file_identifier_dataset(
            {
                "a.py": XML_SAMPLE,
                "b.py": "<unit xmlns=\"http://www.srcML.org/srcML/src\" revision=\"1.0.0\" language=\"Python\"><function><name>process_data</name></function></unit>",
            },
            parser="dom",
        )

        assert len(dataset) == 2
        paths = [row["file_path"] for row in dataset]
        assert paths == ["a.py", "b.py"]
        assert all("n_identifiers" in row for row in dataset)