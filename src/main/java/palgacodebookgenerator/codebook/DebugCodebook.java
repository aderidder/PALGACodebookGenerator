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
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import palgacodebookgenerator.utils.ExcelUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Codebook used for debugging. Contains several extra columns in the output, such as the net name
 */
class DebugCodebook extends DefaultCodebook {
    /**
     * constructor
     * @param protocol             the protocol which should be written to the codebook
     * @param captionOverwriter    caption overwriting and tracking of conflicting captions
     */
    DebugCodebook(Protocol protocol, CaptionOverwriter captionOverwriter, boolean writeInSeparateSheets){
        super(protocol, captionOverwriter, "DEBUG", writeInSeparateSheets);
    }

    /**
     * write codebook to Excel, everything in a single worksheet
     * @param outputDir    where the file should be written
     */
    @Override
    public void writeToExcelSingleSheet(String outputDir){
        Workbook workbook = ExcelUtils.createXLSXWorkbook();
        Sheet mainsheet = addMainWorksheet(workbook, getWriteToExcelMainHeader());

        for(List<CodebookItem> codebookItems:codebookItemMap.values()){
            for(CodebookItem codebookItem:codebookItems){
                ExcelUtils.writeValues(mainsheet, getWriteToExcelValues(codebookItem));
            }
        }
        ExcelUtils.writeXLSXWorkBook(workbook, getCodebookOutputName(outputDir));
    }

    /**
     * get a list with the headernames for the main sheet
     * @return list with the headernames for the main sheet
     */
    private List<String> getWriteToExcelMainHeader(){
        List<String> mainHeaderNames = new ArrayList<>(Arrays.asList("netname","id","ntype","log","path","caption","input_type","data_type","options","field_validation"));
        // the field_entered_when can span multiple columns due to Excel cell limitations
        mainHeaderNames.addAll(getFieldEnteredWhenHeader());
        return mainHeaderNames;
    }

    /**
     * get a list with the values that will be written to the main sheet
     * @param codebookItem    the codebook item which has the values that are to be written
     * @return list with values
     */
    private List<String> getWriteToExcelValues(CodebookItem codebookItem){
        List<String> argumentsList = new ArrayList<>(Arrays.asList(codebookItem.getNet(),codebookItem.getId(),codebookItem.getNtype(),codebookItem.getLog(),codebookItem.getPath(),codebookItem.getCaption(),codebookItem.get_name(),codebookItem.getData_type(),codebookItem.getOptionsString(),codebookItem.getValidationRule()));
        argumentsList.addAll(codebookItem.getPartialRules());
        return argumentsList;
    }

    /**
     * write codebook to Excel, options for concepts in separate sheets
     * @param outputDir    where the file should be written
     */
    @Override
    public void writeToExcelOptionsInSheets(String outputDir){
        List<String> sheetHeaderList = Arrays.asList("PALGA_VALUE", "PALGA_DESCRIPTION", "CODESYSTEM");

        Workbook workbook = ExcelUtils.createXLSXWorkbook();
        Sheet mainsheet = addMainWorksheet(workbook, getWriteToExcelOptionsMainHeader());

        for(List<CodebookItem> codebookItems:codebookItemMap.values()){
            for(CodebookItem codebookItem:codebookItems){
                String path = codebookItem.getPath();
                if(codebookItem.hasOptions()) {
                    ExcelUtils.writeValues(mainsheet, getWriteToExcelOptionsValuesOptionsRef(codebookItem));
                    addOptionsWorksheet(workbook, codebookItem.getPathAsRef(), sheetHeaderList, codebookItem.getOptions());
                }
                else {
                    ExcelUtils.writeValues(mainsheet, getWriteToExcelOptionsValuesNoOptionsRef(codebookItem));
                }
            }
        }
        ExcelUtils.writeXLSXWorkBook(workbook, getCodebookOutputName(outputDir));
    }

    /**
     * get a list with the headernames for the main sheet
     * @return list with the headernames for the main sheet
     */
    private List<String> getWriteToExcelOptionsMainHeader(){
        List<String> mainHeaderNames = new ArrayList<>(Arrays.asList("netname","id","ntype", "log", "path","caption","input_type","data_type", "codelist_ref","field_validation"));
        mainHeaderNames.addAll(getFieldEnteredWhenHeader());
        return mainHeaderNames;
    }

    /**
     * get a list with the values to be written when the codebook item has no options
     * @param codebookItem    the codebook item which has the values that are to be written
     * @return list with values
     */
    private List<String> getWriteToExcelOptionsValuesNoOptionsRef(CodebookItem codebookItem){
        List<String> argumentsList = new ArrayList<>(Arrays.asList(codebookItem.getNet(),codebookItem.getId(),codebookItem.getNtype(),codebookItem.getLog(),codebookItem.getPath(),codebookItem.getCaption(),codebookItem.get_name(),codebookItem.getData_type(),"",codebookItem.getValidationRule()));
        argumentsList.addAll(codebookItem.getPartialRules());
        return argumentsList;
    }

    /**
     * get a list with the values to be written when the codebook item does have options
     * @param codebookItem    the codebook item which has the values that are to be written
     * @return list with values
     */
    private List<String> getWriteToExcelOptionsValuesOptionsRef(CodebookItem codebookItem){
        List<String> argumentsList = new ArrayList<>(Arrays.asList(codebookItem.getNet(),codebookItem.getId(),codebookItem.getNtype(),codebookItem.getLog(),codebookItem.getPath(),codebookItem.getCaption(),codebookItem.get_name(),codebookItem.getData_type(),codebookItem.getPathAsRef(),codebookItem.getValidationRule()));
        argumentsList.addAll(codebookItem.getPartialRules());
        return argumentsList;
    }


    /**
     * checks whether two codebookitems can be merged into a single codebookitem
     * @param codebookItem1        first codebook item
     * @param codebookItem2        second codebook item
     * @param captionOverwriter    caption overwriting and tracking of conflicting captions
     * @return true/false
     */
    @Override
    boolean mayMergeForCodebook(CodebookItem codebookItem1, CodebookItem codebookItem2, CaptionOverwriter captionOverwriter) {
        boolean canMerge=true;

        if(!codebookItem1.getData_type().equalsIgnoreCase(codebookItem2.getData_type())){
            canMerge = false;
        }
        else if(!codebookItem1.getValidationRule().equalsIgnoreCase(codebookItem2.getValidationRule())){
            canMerge = false;
        }
        else if(!codebookItem1.getPartialRulesString().equalsIgnoreCase(codebookItem2.getPartialRulesString())){
            canMerge = false;
        }

        return canMerge;
    }
}
