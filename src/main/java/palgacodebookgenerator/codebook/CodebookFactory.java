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

import palgacodebookgenerator.data.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * create codebook
 */
public class CodebookFactory {
    /**
     * create codebook of a certain type. The type determines which columns the codebook contains
     * @param codebookType          NKI/PALGA/DEBUG
     * @param protocol              the protocol object
     * @param captionOverwriter     captionOverwrite object
     * @param storeInSeparateSheets whether for the selected codebook the value lists should be stored in separate worksheets
     * @return a list with the codebook, which can be written to Excel
     */
    public static List<Codebook> createCodebook(String codebookType, Protocol protocol, CaptionOverwriter captionOverwriter, boolean storeInSeparateSheets){
        List<Codebook> codebookList = new ArrayList<>();
        if(codebookType.equalsIgnoreCase("NKI")){
            codebookList.add(new NKICodebook(protocol, captionOverwriter, storeInSeparateSheets));
        }
        else if(codebookType.equalsIgnoreCase("PALGA")){
            codebookList.add(new PalgaCodebook(protocol, captionOverwriter, storeInSeparateSheets));
        }
        else if(codebookType.equalsIgnoreCase("PALGAWEB")){
            codebookList.add(new PalgaCodebookWeb(protocol, captionOverwriter, storeInSeparateSheets));
        }
        else if(codebookType.equalsIgnoreCase("DEBUG")){
            codebookList.add(new DebugCodebook(protocol, captionOverwriter, storeInSeparateSheets));
        }
        else if(codebookType.equalsIgnoreCase("PALGA & NKI")){
            codebookList.add(new NKICodebook(protocol, captionOverwriter, true));
            codebookList.add(new PalgaCodebook(protocol, captionOverwriter, false));
            codebookList.add(new PalgaCodebook(protocol, captionOverwriter, true));
            codebookList.add(new PalgaCodebookWeb(protocol, captionOverwriter, false));
            codebookList.add(new PalgaCodebookWeb(protocol, captionOverwriter, true));
        }
        else{
            throw new RuntimeException("Unknown codebooktype: "+codebookType+". Valid options: {PALGA, PALGAWEB, NKI, DEBUG, PALGA & NKI}");
        }
        return codebookList;
    }
}
