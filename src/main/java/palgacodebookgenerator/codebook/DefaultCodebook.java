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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import palgacodebookgenerator.utils.ExcelUtils;
import palgacodebookgenerator.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * default functionality for codebooks
 */
abstract class DefaultCodebook implements Codebook{
    Map<String, List<CodebookItem>> codebookItemMap;
    Protocol protocol;
    private int maxPartialRulesLength=0;
    private final boolean writeInSeparateSheets;
    private final String codebookType;

    DefaultCodebook(Protocol protocol, CaptionOverwriter captionOverwriter, String codebookType, boolean writeInSeparateSheets){
        this.writeInSeparateSheets = writeInSeparateSheets;
        this.codebookType = codebookType;
        this.protocol = protocol;
        addData(captionOverwriter);
    }

    /**
     * write codebook to Excel.
     * @param outputDir directory which will contain the created codebook
     */
    @Override
    public final void writeToExcel(String outputDir) {
        if(writeInSeparateSheets) {
            writeToExcelOptionsInSheets(outputDir);
        }
        else{
            writeToExcelSingleSheet(outputDir);
        }
    }

    /**
     * generate the codebook item map and try to merge codebook items
     * @param captionOverwriter    holds caption overwrites and tricks conflicting captions
     */
    private void addData(CaptionOverwriter captionOverwriter){
        codebookItemMap = protocol.getCodebookItems();
        for(List<CodebookItem> codebookItems:codebookItemMap.values()) {
            overwriteCaption(codebookItems, captionOverwriter);
            tryMerge(codebookItems, captionOverwriter);
        }
    }

    /**
     * merge functionality for a list of codebook items
     * @param codebookItems        list of codebook items found in the protocol
     * @param captionOverwriter    holds caption overwrites and tricks conflicting captions
     */
    private void tryMerge(List<CodebookItem> codebookItems, CaptionOverwriter captionOverwriter){
        for(int i=0; i<codebookItems.size(); i++){
            CodebookItem codebookItem1 = codebookItems.get(i);
            for(int j=i+1; j<codebookItems.size(); j++){
                CodebookItem codebookItem2 = codebookItems.get(j);
                codebookItem1.mergeOptions(codebookItem2);
                if(mayMerge(codebookItem1, codebookItem2, captionOverwriter)){
                    codebookItems.remove(j);
                    j--;
                }
            }
            checkPartialRulesLength(codebookItem1);
        }
    }

    /**
     * attempts to overwrite the captions using the caption overwrite file
     * @param codebookItemList     list with codebookitems
     * @param captionOverwriter    caption overwriting and tracking of conflicting captions
     */
    private void overwriteCaption(List<CodebookItem> codebookItemList, CaptionOverwriter captionOverwriter){
        for(CodebookItem codebookItem:codebookItemList) {
            String path = codebookItem.getPath();
            if (captionOverwriter.isValidOverwriteFileProvided() && captionOverwriter.containsPath(path)) {
                // if so, use the specified overwrite
                String newCaption = captionOverwriter.getOverwrite(path);
                codebookItem.setCaption(newCaption);
            }
        }
    }

    /**
     * keep track of the maximum partial rules length. we use this to create the header
     * as the maximum decides how many cells the partial rules span
     * @param codebookItem    the current codebookItem
     */
    private void checkPartialRulesLength(CodebookItem codebookItem){
        int partialRulesLength = codebookItem.getPartialRulesLength();
        if(partialRulesLength>maxPartialRulesLength){
            maxPartialRulesLength = partialRulesLength;
        }
    }

    /**
     * get the full filename for the output
     * @param outputDir    directory where file will be written
     * @return full filename
     */
    String getCodebookOutputName(String outputDir){
        String inSeparateSheets = writeInSeparateSheets ?"_sep":"";
        return outputDir+protocol.getProtocolName()+"_codebook_"+protocol.getSmallVersion()+"_"+codebookType+inSeparateSheets+".xlsx";
    }

    /**
     * add a new Options worksheet, add a header and the values
     * @param workbook       the workbook in which the sheet will be created
     * @param sheetName      the name of the new sheet
     * @param headerList     headers for the sheet
     * @param optionsList    the values that need to be added
     */
    void addOptionsWorksheet(Workbook workbook, String sheetName, List<String> headerList, List<String> optionsList){
        sheetName = StringUtils.cleanString(sheetName);
        Sheet sheet = workbook.getSheet(sheetName);
        if(sheet==null){
            CellStyle headerStyle = ExcelUtils.createHeaderStyle(workbook);
            sheet = ExcelUtils.createSheetWithHeader(workbook, sheetName, headerList, headerStyle);
            for(String anOption:optionsList){
                ExcelUtils.writeValues(sheet, anOption, anOption);
            }
        }
    }

    /**
     * creates the CODEBOOK sheet
     * @param workbook        workbook in which to create the sheet
     * @param mainHeaderNames headers to add
     * @return the newly created sheet
     */
    Sheet addMainWorksheet(Workbook workbook, List<String> mainHeaderNames){
        CellStyle headerStyle = ExcelUtils.createHeaderStyle(workbook);
        String sheetName = "CODEBOOK";
        return ExcelUtils.createSheetWithHeader(workbook, sheetName, mainHeaderNames, headerStyle);
    }

    /**
     * get a list for the partial rules variable: the field_entered_when header
     * as this variable can span multiple cells, the field_entered_when get an extension
     * based on the maximum number of columns the variable spans
     * @return list with field_entered_when column names
     */
    List<String> getFieldEnteredWhenHeader(){
        String stringPart = "field_entered_when";
        List<String> headerPartList = new ArrayList<>();
        for(int i=1; i<=maxPartialRulesLength; i++){
            headerPartList.add(stringPart+"_"+i);
        }
        return headerPartList;
    }

    /**
     * checks whether two codebook items can be merged
     * @param codebookItem1        first codebook item
     * @param codebookItem2        second codebook item
     * @param captionOverwriter    caption overwriting and tracking of conflicting captions
     * @return true/false
     */
    private boolean mayMerge(CodebookItem codebookItem1, CodebookItem codebookItem2, CaptionOverwriter captionOverwriter){
        boolean canMerge = updateCaption(codebookItem1, codebookItem2, captionOverwriter);
        return canMerge && mayMergeForCodebook(codebookItem1, codebookItem2, captionOverwriter);
    }

    /**
     * general part of the merge check, which attempts to overwrite the caption of the codebookitems
     * using the captionoverwriter
     * as discussed with PALGA, overwrites will always be used if available, even if there is no conflict
     * reason for this is that it will allow us to specify better labels for the codebook if desired
     *
     * @param codebookItem1        first codebook item
     * @param codebookItem2        second codebook item
     * @param captionOverwriter    caption overwriting and tracking of conflicting captions
     * @return true/false
     */
    private boolean updateCaption(CodebookItem codebookItem1, CodebookItem codebookItem2, CaptionOverwriter captionOverwriter) {
        boolean canMerge=true;

        String path = codebookItem1.getPath();
        // check whether the caption is conflicting between the two codebook items
        if(!codebookItem1.getCaption().equalsIgnoreCase(codebookItem2.getCaption())){
            // if conflicting, add the conflict to the captionOverwriter and return that the items cannot
            // be merged
            captionOverwriter.addConflictingCaption(path, codebookItem1.getCaption());
            captionOverwriter.addConflictingCaption(path, codebookItem2.getCaption());
            canMerge = false;
        }

        return canMerge;
    }

    /**
     * abstract method which every codebook must implement. The method checks whether two codebook items can be merged.
     * as the fields collected differ per codebook, merging must be handled by the individual codebooks.
     * @param codebookItem1        first codebook item
     * @param codebookItem2        second codebook item
     * @param captionOverwriter    caption overwriting and tracking of conflicting captions
     * @return true/false
     */
    abstract boolean mayMergeForCodebook(CodebookItem codebookItem1, CodebookItem codebookItem2, CaptionOverwriter captionOverwriter);

    /**
     * write a codebook to Excel
     * @param outputDir    where the file should be written
     */
    abstract void writeToExcelSingleSheet(String outputDir);

    /**
     * write a codebook to Excel with options per sheet
     * @param outputDir    where the file should be written
     */
    abstract void writeToExcelOptionsInSheets(String outputDir);

}
