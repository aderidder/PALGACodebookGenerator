/*
 * Copyright 2017 NKI/AvL; VUmc 2018/2019/2020
 *
 * This file is part of PALGA Protocol Codebook Generator.
 *
 * PALGA Protocol Codebook Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PALGA Protocol Codebook Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PALGA Protocol Codebook Generator. If not, see <http://www.gnu.org/licenses/>
 *
 */

package palgacodebookgenerator.codebook;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * class for overwriting the existing palga captions with custom ones
 */
public class CaptionOverwriter {
    private static final Logger logger = LogManager.getLogger(CaptionOverwriter.class.getName());
    private final String protocolName;
    private final Map<String, String> overwriteMap = new HashMap<>();
    private final Map<String, List<String>> missingOverwritesMap = new HashMap<>();
    private boolean validOverwriteFileProvided = false;

    /**
     * create caption overwriter for a protocol
     * @param protocolName    name of the protocol
     */
    public CaptionOverwriter(String protocolName){
        this.protocolName = protocolName;
    }

    /**
     * add line of data from the overwrite file to the overwrite map
     * the datafile contains a concept and a caption per line
     * @param line    line of data
     */
    private void addLine(String line){
        String [] splitLine = line.split("\t");
        if(!overwriteMap.containsKey(splitLine[0])){
            overwriteMap.put(splitLine[0], splitLine[1]);
        }
        else{
            logger.log(Level.INFO, "Map file contains duplicates for "+splitLine[0]+". First instance found will be used.");
        }
    }

    /**
     * read a file to fill the caption overwriter
     * if no file exists, the object will still be filled with missing captions later
     * @param fileName    name of the caption overwriter file to read
     */
    public void readCaptionOverwriteFile(String fileName){
        if(fileName==null || fileName.equalsIgnoreCase("")){
            validOverwriteFileProvided = false;
        }
        else {
            String line;

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(fileName), StandardCharsets.ISO_8859_1))) {
                // read the first line of the data, which contains the header, and add it to our input data
                line = br.readLine();
                if (!correctProtocolFile(line)) {
                    throw new Exception("The first line did not match #"+protocolName+"\nPlease add this line or select the " +
                            "proper protocol overwrite file.");
                }
                // add other lines
                while ((line = br.readLine()) != null) {
                    addLine(line);
                }
                validOverwriteFileProvided = true;
            } catch (Exception e) {
                throw new RuntimeException("A problem occured reading the caption overwrite file: "+e.getMessage());
            }
        }
    }

    /**
     * returns whether a valid overwrite file was provided
     * @return true/false
     */
    boolean isValidOverwriteFileProvided(){
        return validOverwriteFileProvided;
    }

    /**
     * add a conflicting caption to the missingOverwrites map
     * @param path       the variable name
     * @param caption    the caption that was found
     */
    void addConflictingCaption(String path, String caption){
        if(!missingOverwritesMap.containsKey(path)){
            missingOverwritesMap.put(path, new ArrayList<>());
        }
        if(caption.equalsIgnoreCase("")){
            caption = "<NO CAPTION>";
        }

        // retrieve the list of captions for the path and add the new one
        List<String> captionList = missingOverwritesMap.get(path);
        if(!captionList.contains(caption)){
            captionList.add(caption);
        }
    }

    /**
     * get a string representation of all the conflicting captions
     * @return string representation of all the conflicting captions
     */
    private String getConflictingCaptions(){
        List<String> outputList = new ArrayList<>();
        for(Map.Entry<String, List<String>> entrySet:missingOverwritesMap.entrySet()){
            String path = entrySet.getKey();
            String conflicts = entrySet.getValue().stream().collect(Collectors.joining("\t"));
            outputList.add(path+"\t"+conflicts);
        }
        return outputList.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * verifies whether the first line in the captionsOverwrite File starts with a # and the protocolName
     * @param line    the first line
     * @return true/false
     */
    private boolean correctProtocolFile(String line){
        return line.trim().equalsIgnoreCase("#" + protocolName);
    }

    /**
     * returns whether the path exists in the overwrites map
     * @param path    the variable name
     * @return true/false
     */
    boolean containsPath(String path){
        return overwriteMap.containsKey(path);
    }

    /**
     * returns the caption-overwrite as specified in the file
     * @param path    the variable name
     * @return the overwrite caption
     */
    String getOverwrite(String path){
        return overwriteMap.get(path);
    }

    /**
     * write the conflicting captions to a file
     * @param outDir    directory whence to write the file
     */
    public void writeConflictingCaptions(String outDir){
        if(missingOverwritesMap.size()>0) {
            String conflictsFile = outDir + protocolName + "_ConflictingCaptions.txt";
            logger.log(Level.INFO, "Found conflicting Captions that were not solved in the Conflicts overwrite file. Please see "+conflictsFile);
            String output = getConflictingCaptions();
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(conflictsFile))) {
                bufferedWriter.write(output);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            logger.log(Level.INFO, "No conflicting captions were found");
        }
    }
}
