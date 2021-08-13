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

package palgacodebookgenerator.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * String helper functions
 */
public class StringUtils {
    /**
     * accepts a string and splits it into ~32767 size chunks, which is the maximum Excel accepts in a cell
     * @param value    string to split
     * @return the string as a list of max 32767 characters
     */
    public static List<String> splitString(String value){
        List<String> splitString = new ArrayList<>();
        while(value.length()>32767) {
            String tmpString = value.substring(0, 32767);
            int indexLastSpace = tmpString.lastIndexOf(" ");
            splitString.add(value.substring(0, indexLastSpace));
            value = value.substring(indexLastSpace);
        }
        splitString.add(value);
        return splitString;
    }

    /**
     * removes the " from the beginning and ending of a string
     * @param value    a string
     * @return cleaned string
     */
    public static String cleanString(String value){
        if(value.startsWith("\"")){
            value = value.substring(1, value.length()-1);
        }
        return value;
    }
}
