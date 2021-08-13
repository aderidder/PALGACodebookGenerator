# The PALGA Protocol Codebook Generator
## Introduction
PALGA is the national pathology registry of the Netherlands. A large portion of their data is collected using the PALGA protocols. Usage of these protocols ensures that data is collected in a standardised way, thereby ensuring high quality data. Researchers can request a dataset from PALGA for their research. Unfortunately, the data is available only in Dutch. 

The VUmc, NKI and PALGA worked together in a pilot project to make design a process to make PALGA data available for international research. The process has two phases:
* Phase 1: 
    * Generate an Excel codebook for a PALGA protocol using the PALGA Protocol Codebook Generator
    * Translate the codebook / map it to a medical thesaurus
    * Convert the Excel codebook to an ART-DECOR XML using the PALGA Protocol Codebook to XML
    * Make the codebook available online in ART-DECOR 
* Phase 2:
    * Researcher with PALGA Protocol data translates his/her data using the PALGA Protocol Data Translator

The software you are currently looking at is the PALGA Protocol Codebook Generator, which can create a codebook for a PALGA Protocol. It does so by parsing data from a protocol database. As these databases are not public, this program can only be used by PALGA.<br>

## Creating an executable jar
You can use maven to create an executable jar file, using mvn package. The jar is placed in the target directory and can be run using java -jar <generated_jar_file>

## Generating codebooks
The program has three requirements: a workspace file and an output directory. Optionally, an overwrite file can be specified.
 
### The Workspace File
This is a database with the PALGA Protocols

### The Output Directory
Directory where the output will be written

### The Overwrite File
Contains the identifier of a concept, a tab, and a label for the concept. This basically allows you to overwrite the labels found in the PALGA Protocol.

## About
The PALGA Protocol Codebook Generator is a collaboration between NKI / AvL, VUmc and PALGA.<br>
The program was designed and created by **Sander de Ridder** (NKI 2017; VUmc 2018/2019/2020) and Jeroen Belien (VUmc)<br>
Testers & Consultants: Rinus Voorham (PALGA), Rick Spaan (PALGA)<br>
This project was sponsored by MLDS project OPSLAG and KWF project TraIT2Health-RI (WP: Registry-in-a-Box)<br>

The PALGA Protocol Codebook Generator is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

The PALGA Protocol Codebook Generator is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
