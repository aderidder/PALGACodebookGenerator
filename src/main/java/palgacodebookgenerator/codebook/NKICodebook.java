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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * codebook used by the NKI
 * the idea is that using the OptionsInSheets Excel will be augmented with translations and ontology information
 * and that this file can then be parsed and transformed to xml (by another program) which can then be loaded
 * into art-decor
 */
class NKICodebook  extends DefaultCodebook {
    /**
     * returns whether certain paths should actually be in the output file
     * we used this to prevent "temp" variables from being added. However, it caused
     * some issues, so for now, just add everyting
     * @param path path to check
     * @return always false at the moment
     */
    private static boolean skipPath(String path){
//        return path.startsWith("temp.") || path.equalsIgnoreCase("temp");
        return false;
    }

    /**
     * constructor
     * @param protocol             the protocol which should be written to the codebook
     * @param captionOverwriter    caption overwriting and tracking of conflicting captions
     */
    NKICodebook(Protocol protocol, CaptionOverwriter captionOverwriter, boolean writeInSeparateSheets){
        super(protocol, captionOverwriter, "NKI", writeInSeparateSheets);
    }

    /**
     * write codebook to Excel, everything in a single worksheet
     * @param outputDir    where the file should be written
     */
    @Override
    public void writeToExcelSingleSheet(String outputDir){
        List<String> mainHeaderNames = Arrays.asList("path","caption","input_type","data_type", "options");

        Workbook workbook = ExcelUtils.createXLSXWorkbook();
        Sheet mainsheet = addMainWorksheet(workbook, mainHeaderNames);

        for(List<CodebookItem> codebookItems:codebookItemMap.values()){
            for(CodebookItem codebookItem:codebookItems){
                String path = codebookItem.getPath();
                if(!skipPath(path)) {
                    ExcelUtils.writeValues(mainsheet,
                            path,
                            codebookItem.getCaption(),
                            codebookItem.get_name(),
                            codebookItem.getData_type(),
                            codebookItem.getOptionsString());
                }
            }
        }
        ExcelUtils.writeXLSXWorkBook(workbook, getCodebookOutputName(outputDir));
    }

    /**
     * write codebook to Excel, options for concepts in separate sheets
     * @param outputDir    where the file should be written
     */
    @Override
    public void writeToExcelOptionsInSheets(String outputDir){
        List<String> mainHeaderNames = Arrays.asList("id", "description_nl", "description_en", "codesystem","code", "description_code", "codelist_ref","data_type", "input_type", "properties");
        List<String> sheetHeaderList = Arrays.asList("value_nl", "description_nl", "value_en", "description_en", "codesystem", "code", "description_code");

        Workbook workbook = ExcelUtils.createXLSXWorkbook();
        Sheet infosheet = ExcelUtils.createSheetWithoutHeader(workbook, "INFO");
        addInfoSheetData(infosheet);

        Sheet mainsheet = addMainWorksheet(workbook, mainHeaderNames);

        for(List<CodebookItem> codebookItems:codebookItemMap.values()){
            for(CodebookItem codebookItem:codebookItems){
                String path = codebookItem.getPath();
                if(!skipPath(path)) {
                    String property = "{PALGA_COLNAME=" + path + "}";
                    List<String> optionsList = codebookItem.getOptions();
                    if (optionsList.size() == 0) {
                        ExcelUtils.writeValues(mainsheet, path, codebookItem.getCaption(), "", "", "", "", "", codebookItem.getData_type(), codebookItem.get_name(), property);
                    } else {
                        ExcelUtils.writeValues(mainsheet, path, codebookItem.getCaption(), "", "", "", "", path, codebookItem.getData_type(), codebookItem.get_name(), property);
                        addOptionsWorksheet(workbook, codebookItem.getPathAsRef(), sheetHeaderList, optionsList);
                    }
                }
            }
        }
        ExcelUtils.writeXLSXWorkBook(workbook, getCodebookOutputName(outputDir));
    }

    /**
     * add sheet with metadata
     * @param sheet the sheet to which to add the metadata
     */
    private void addInfoSheetData(Sheet sheet){
        String version = protocol.getSmallVersion();
        String protocolName = protocol.getProtocolName();
        String effectiveDate= new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        ExcelUtils.writeValues(sheet, "Version", version);
        ExcelUtils.writeValues(sheet, "DatasetName_nl", "PALGA Protocol: "+ protocolName);
        ExcelUtils.writeValues(sheet, "DatasetDescription_nl", "PALGA Protocol: "+ protocolName +" versie "+version);
        ExcelUtils.writeValues(sheet, "DatasetName_en", "PALGA Protocol: "+ protocolName);
        ExcelUtils.writeValues(sheet, "DatasetDescription_en", "PALGA Protocol: "+ protocolName + " version "+version);
        ExcelUtils.writeValues(sheet, "Effectivedate", effectiveDate);

    }

    /**
     * attempts to fix some issues which may prevent items from being merged
     * @param codebookItem1 first codebook item
     * @param codebookItem2 second codebook item
     */
    private void solveConflicts(CodebookItem codebookItem1, CodebookItem codebookItem2){
        fixNumber(codebookItem1);
        fixNumber(codebookItem2);
        fixTextNumber(codebookItem1, codebookItem2);
        fixTextNumber(codebookItem2, codebookItem1);

        fixRadio(codebookItem1, codebookItem2);
        fixRadio(codebookItem2, codebookItem1);
        fixTextFormat(codebookItem1, codebookItem2);
        fixTextFormat(codebookItem2, codebookItem1);

    }

    private boolean fixRadio(CodebookItem codebookItem1, CodebookItem codebookItem2){
        if(codebookItem1.get_name().equalsIgnoreCase("radio") && !codebookItem2.get_name().equalsIgnoreCase("radio")){
            codebookItem2.set_name("radio");
            codebookItem2.setData_type(codebookItem1.getData_type());
            return true;
        }
        return false;
    }

    private boolean fixTextFormat(CodebookItem codebookItem1, CodebookItem codebookItem2){
        if(codebookItem1.get_name().equalsIgnoreCase("text_input") && codebookItem2.get_name().equalsIgnoreCase("format_variable")){
            codebookItem2.set_name("text_input");
            return true;
        }
        return false;
    }

    private void fixNumber(CodebookItem codebookItem){
        if(codebookItem.getData_type().equalsIgnoreCase("number")){
            codebookItem.setData_type("numeric");
        }
    }

    private void fixTextNumber(CodebookItem codebookItem1, CodebookItem codebookItem2){
        if(codebookItem1.getData_type().equalsIgnoreCase("text") && codebookItem2.getData_type().equalsIgnoreCase("numeric")){
            codebookItem1.setData_type("numeric");
        }
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
        boolean canMerge = true;

        solveConflicts(codebookItem1, codebookItem2);
//        if(!codebookItem1.getCaption().equalsIgnoreCase(codebookItem2.getCaption())){
//            // check some map for the variable and if it's there, set both codebookItem captions to that value
//            // otherwise provide some feedback and set canMerge to false;
//            String path = codebookItem1.getPath();
//            if(captionOverwriter.isValidOverwriteFileProvided() && captionOverwriter.containsPath(path)){
//                String newCaption = captionOverwriter.getOverwrite(path);
//                codebookItem1.setCaption(newCaption);
//                codebookItem2.setCaption(newCaption);
//            }
//            else {
//                captionOverwriter.addConflictingCaption(path, codebookItem1.getCaption());
//                captionOverwriter.addConflictingCaption(path, codebookItem2.getCaption());
//                canMerge = false;
//            }
//        }

//        if((codebookItem1.getData_type().equalsIgnoreCase("number") && codebookItem2.getData_type().equalsIgnoreCase("numeric"))||
//           (codebookItem1.getData_type().equalsIgnoreCase("numeric") && codebookItem2.getData_type().equalsIgnoreCase("number"))){
//            return true;
//        }
        if(!codebookItem1.getData_type().equalsIgnoreCase(codebookItem2.getData_type())){
            canMerge = false;
        }
        return canMerge;
    }

}
