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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Excel helper functions
 */
public class ExcelUtils {

    /**
     * create a new workbook
     * @return the workbook
     */
    public static Workbook createXLSXWorkbook(){
        return new XSSFWorkbook();
    }

    /**
     * write a workbook to file
     * @param workbook the workbook to write
     * @param fileName the filename of the output file
     */
    public static void writeXLSXWorkBook(Workbook workbook, String fileName){
        try (FileOutputStream fileOut = new FileOutputStream(fileName)){
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            logger.error("Problem creating {}. The file has NOT been created.", fileName);
        }
    }

    /**
     * create a style with bold text and blue background
     * @param workbook     the workbook in which the style should be available
     * @return the cellstyle
     */
    public static CellStyle createHeaderStyle(Workbook workbook){
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.index);
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        return headerStyle;
    }

    /**
     * create a sheet in a workbook with header texts, which follow a certain cellstyle
     * @param workbook       the workbook in which the sheet must be created
     * @param sheetName      the name of the worksheet
     * @param headerNames    list of the headernames
     * @param headerStyle    the style which to apply to the headers
     * @return the newly created sheet
     */
    public static Sheet createSheetWithHeader(Workbook workbook, String sheetName, List <String> headerNames, CellStyle headerStyle){
        Sheet sheet  = workbook.createSheet(sheetName);
        Row row = sheet.createRow(0);

        for(int i=0; i<headerNames.size(); i++){
            row.getCell(i, Row.CREATE_NULL_AS_BLANK).setCellValue(headerNames.get(i));
            row.getCell(i).setCellStyle(headerStyle);
        }
        return sheet;
    }

    public static Sheet createSheetWithoutHeader(Workbook workbook, String sheetName){
        return workbook.createSheet(StringUtils.cleanString(sheetName));
    }

    /**
     * write values to a sheet
     * @param sheet     the sheet to which the values will be written
     * @param values    one or more string values which will be written
     */
    public static void writeValues(Sheet sheet, String ... values){
        Row row;
        if(sheet.getPhysicalNumberOfRows()==0){
            row = sheet.createRow(sheet.getLastRowNum());
        }
        else {
            row = sheet.createRow(sheet.getLastRowNum() + 1);
        }
        for(int i=0; i<values.length; i++){
            String value = StringUtils.cleanString(values[i]);
            row.getCell(i, Row.CREATE_NULL_AS_BLANK).setCellValue(value);
        }
    }

    /**
     * write values to a sheet
     * @param sheet     the sheet to which the values will be written
     * @param values    a list with values which will be written
     */
    public static void writeValues(Sheet sheet, List<String> values){
        writeValues(sheet, values.toArray(new String[values.size()]));
    }

    private static final Logger logger = LogManager.getLogger(ExcelUtils.class.getName());
}
