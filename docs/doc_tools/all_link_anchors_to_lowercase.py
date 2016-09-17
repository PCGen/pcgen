# -*- coding: utf-8 -*-
# Python 2 script

# This is a generalised script for crawling over all the HTML files and making changes en-masse.
# In this particular case, it's changing all of the following to be lowercase:
#   - <span id="..."> attributes
#   - <span name="..."> attributes
#   - <h1 id="..."> and <h1 name="..."> attributes, and so on for h2, h3...
#   - <a href="./xyz.html#FOO"> - lowercase the name `FOO` to `foo`.
#
# I'm doing this because `pandoc` automatically generates anchors from headings,
# i.e. `# WIDGETS` becomes `<h1 id="widgets">WIDGETS</h1>`. But the `id`
# attribute is always generated in lowercase.

import os
import codecs

import bs4 # Requires package `beautifulsoup4` for HTML parsing

def list_all_html_files():
    html_files = list()
    for root, dirs, files in os.walk("../"):
        # html_files.extend(filter(lambda x: x.endswith(".html"), files))
        html_files.extend( [os.path.join(root, f) for f in files if f.endswith(".html")] )
    return html_files

def process_one_file(html_filename):
    def lowercase_id_name(t):
        if t.has_attr("id"):
            t["id"] = t["id"].lower()
        if t.has_attr("name"):
            t["name"] = t["name"].lower()
        if t.has_attr("href"):
            href = t["href"]
            if (not href.startswith("http://")) and ("#" in href):
                #print href
                # Find relative links within the pcgen docs.
                # Find relative links that refer to a specific anchor ("#")
                href_position = href.find("#")
                t["href"] = href[:href_position + 1] + href[href_position + 1:].lower()
                #print tag["href"]

    def delete_w3c_validation_stickers(t):
        # Deleting things like the below - because it's the future and we are using XHTML now, not HTML 4.01 Strict.
        # <p>
        #  <a href="http://validator.w3.org/check?uri=referer">
        #   <img alt="Valid HTML 4.01 Strict" src="../../images/system/valid-html401.png"/>
        #  </a>
        # </p>
        if t.has_attr("href") and t["href"] == "http://validator.w3.org/check?uri=referer":
            t.extract() # delete tag

    with open(html_filename, "r") as fh:
        soup = bs4.BeautifulSoup(fh)#, from_encoding="utf-8")

        for tag in soup.find_all(True):
            lowercase_id_name(tag)
            delete_w3c_validation_stickers(tag)

        # print soup.prettify()

    with codecs.open(html_filename, encoding="utf-8", mode="w") as fh:
        output_html = soup.prettify()
        fh.write(output_html)



# print list_all_html_files()

# process_one_file("../listfilepages\\globalfilestagpages\\globalfilesprexxx.html")

for f in list_all_html_files():
    process_one_file(f)
