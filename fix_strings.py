#! /usr/bin/env python3
# Import the modules
import os
import xml.etree.ElementTree as ET
import pathlib

# Define the directory path
dir_path = pathlib.Path(__file__).parent / "app" / "src" / "main" / "res"

# Loop through the subdirectories starting with "values-"
for subdir in dir_path.iterdir():
    if subdir.name.startswith("values-") and not subdir.name.endswith("night") and not subdir.name[-1].isdigit():
        # Get the full path of the strings.xml file
        file_path = subdir / "strings.xml"
        # Parse the XML file
        tree = ET.parse(file_path)
        root = tree.getroot()
        # Loop through the <string> tags under <resources>
        for string in root.findall("string"):
            # Check if the tag is empty
            if not string.text:
                # Remove the tag from the tree
                root.remove(string)
        # Write the modified tree back to the file
        tree.write(file_path, encoding="unicode")

