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
import java.util.Arrays;
import java.util.List;

/**
 * Codebook for PALGA web, tailored to their wishes
 */
class PalgaCodebookWeb extends DefaultPalgaCodebook {
    /**
     * constructor
     * @param protocol             the protocol which should be written to the codebook
     * @param captionOverwriter    caption overwriting and tracking of conflicting captions
     */
    PalgaCodebookWeb(Protocol protocol, CaptionOverwriter captionOverwriter, boolean writeInSeparateSheets){
        super(protocol, captionOverwriter, "PALGAWEB", writeInSeparateSheets);
    }


    /**
     * get a list with the headernames for the main sheet
     * @return list with the headernames for the main sheet
     */
    List<String> getWriteToExcelMainHeader(){
        return new ArrayList<>(Arrays.asList("path","caption","input_type","data_type", "options", "field_validation"));
    }

    /**
     * get a list with the values that will be written to the main sheet
     * @param codebookItem    the codebook item which has the values that are to be written
     * @return list with values
     */
    List<String> getWriteToExcelValuesSingleSheet(CodebookItem codebookItem){
        return new ArrayList<>(Arrays.asList(codebookItem.getPath(), codebookItem.getCaption(), codebookItem.get_name(), codebookItem.getData_type(),  codebookItem.getOptionsString(), codebookItem.getValidationRule()));
    }

    /**
     * get a list with the values to be written when the codebook item has no options
     * @param codebookItem    the codebook item which has the values that are to be written
     * @return list with values
     */
    List<String> getWriteToExcelOptionsValuesNoOptionsRef(CodebookItem codebookItem){
        return new ArrayList<>(Arrays.asList(codebookItem.getPath(), codebookItem.getCaption(), codebookItem.get_name(), codebookItem.getData_type(), "", codebookItem.getValidationRule()));
    }

    /**
     * get a list with the values to be written when the codebook item does have options
     * @param codebookItem    the codebook item which has the values that are to be written
     * @return list with values
     */
    List<String> getWriteToExcelOptionsValuesOptionsRef(CodebookItem codebookItem){
        return new ArrayList<>(Arrays.asList(codebookItem.getPath(), codebookItem.getCaption(), codebookItem.get_name(), codebookItem.getData_type(), codebookItem.getPathAsRef(), codebookItem.getValidationRule()));
    }

}
