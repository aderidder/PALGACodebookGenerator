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

import palgacodebookgenerator.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * class for codebookitems. These contain the aggregated information from the PALGA nodes for easy use by
 * the codebooks
 */
public class CodebookItem {
    private String path;
    private String caption;
    private String _name;
    private String data_type;
    private List<String> options;
    private List<String> validationRules;
    private List<String> partialRules;

    private String log;
    private String ntype;

    private String net;
    private String id;

    /**
     * constructor for new codebook item
     * @param path               the path variable
     * @param caption            the caption variable
     * @param data_type          the data_type variable
     * @param _name              the _name variable
     * @param options            a list with the options for this path
     * @param validationRules    a list with the validation rules for this path
     */
    public CodebookItem(String path, String caption, String data_type, String _name, List<String> options, List<String> validationRules){
        this.path = cleanPath(path);
        this.caption = caption.trim();
        this.data_type = data_type;
        this._name = _name;
        this.options = options;
        this.validationRules = validationRules;
    }

    /**
     * Sqa$(temp.genesetnummer)RedenAanvraag -->
     * 1: Sqa$(
     * 2: )RedenAanvraag
     * This we then turn into  Sqa$(Var)RedenAanvraag
     * @param path the path which has to be cleaned
     * @return the cleaned path
     */

    private static String cleanPath(String path){
        Pattern pattern = Pattern.compile("(.*\\$\\().*(\\).*)");
        Matcher matcher = pattern.matcher(path);
        if(matcher.matches()){
            return matcher.group(1)+"Var"+matcher.group(2);
        }
        return path.trim();
    }

    /**
     * sets the log variable for this item
     * @param log    the log variable
     */
    public void setLog(String log){
        this.log = log;
    }

    /**
     * sets the node type variable for this item
     * @param ntype    node type
     */
    public void setNtype(String ntype){
        this.ntype = ntype;
    }

    /**
     * returns the log variable
     * @return the log variable
     */
    public String getLog(){
        return log;
    }

    /**
     * returns the node type
     * @return the node type
     */
    String getNtype(){
        return ntype;
    }

    /**
     * return to which net this codebookitem belongs
     * @return the net to which this codebookitem belongs
     */
    String getNet() {
        return net;
    }

    /**
     * set the net to which this codebookitem belongs
     * @param net    the net to which this codebookitem belongs
     */
    public void setNet(String net) {
        this.net = net;
    }

    /**
     * returns the id of this codebookitem
     * @return the id of this codebookitem
     */
    public String getId() {
        return id;
    }

    /**
     * set the id of this codebookitem
     * @param id    the id of this codebookitem
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * returns the caption of this codebookitem
     * @return the caption of this codebookitem
     */
    String getCaption() {
        return caption;
    }

    /**
     * returns the _name of this codebookitem
     * @return the _name of this codebookitem
     */
    String get_name() {
        return _name;
    }

    /**
     * returns the data_type of this codebookitem
     * @return the data_type of this codebookitem
     */
    String getData_type() {
        return data_type;
    }

    /**
     * returns the partialRules list
     * @return the partialRules list
     */
    List<String> getPartialRules() {
        return partialRules;
    }

    /**
     * returns the partialRules list as a String
     * @return the partialRules list as a String
     */
    String getPartialRulesString() {
        return partialRules.stream().collect(Collectors.joining());
    }

    /**
     * sets the partialRules list based on a String
     * it first splits the string into chunks which can fit in an Excel cell
     * @param partialRules    the partialRules String
     */
    public void setPartialRules(String partialRules) {
        this.partialRules = StringUtils.splitString(partialRules);
    }

    /**
     * returns the number of cell the partial rules will require
     * @return the size of the partialrules list
     */
    int getPartialRulesLength(){
        if(partialRules==null){
            return 0;
        }
        return partialRules.size();
    }

    /**
     * returns the path variable
     * @return the path variable
     */
    public String getPath(){
        return path;
    }

    /**
     * sets the caption variable
     * @param caption the caption variable
     */
    void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * sets the _name variable
     * @param _name    the _name variable
     */
    public void set_name(String _name) {
        this._name = _name;
    }

    /**
     * returns the options list as a String, separated by ;
     * @return a string which represents the options for this codebookitem
     */
    String getOptionsString(){
        return options.stream().collect(Collectors.joining("; "));
    }

    /**
     * returns the validationRules list as a String, separated by ;
     * @return a string which represents the validation rules for this codebookitem
     */
    String getValidationRule(){
        return validationRules.stream().collect(Collectors.joining("; "));
    }

    /**
     * returns the options list
     * @return the options list
     */
    List<String> getOptions(){
        return options;
    }

    /**
     * returns whether this codebookItem has options
     * @return true/false
     */
    boolean hasOptions(){
        return options.size()>0;
    }

    /**
     * merge the optionlists of this codebookItem with that of another codebookItem with the same path via a union
     * @param codebookItem    another codebookItem for the same path
     */
    void mergeOptions(CodebookItem codebookItem){
        options = union(options, codebookItem.getOptions());
        codebookItem.options = options;
    }

    /**
     * Excel worksheet names have a max of 32 characters, so we take a substring of the path
     * @return the path or a substring with a maximum of 32 characters
     */
    public String getPathAsRef(){
        if(path.length()>31){
            return path.substring(0,31);
        }
        return path;
    }

    /**
     * union of two Lists
     * @param list1    first list
     * @param list2    second list
     * @return new list with union of the lists
     */
    private static List<String> union(List<String> list1, List<String> list2) {
        Set<String> set = new LinkedHashSet<>();
        set.addAll(list1);
        set.addAll(list2);
        return new ArrayList<>(set);
    }

    /**
     * set the data_type
     * @param data_type the data_type
     */
    void setData_type(String data_type) {
        this.data_type = data_type;
    }
}
