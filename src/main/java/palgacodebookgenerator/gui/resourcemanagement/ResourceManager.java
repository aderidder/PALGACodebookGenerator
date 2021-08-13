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

package palgacodebookgenerator.gui.resourcemanagement;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.net.URL;

/**
 * class for frontend resource management
 */
public class ResourceManager {
    private static final String cssLocation = "/css/";
    private static final String imagesLocation = "/images/";

    /**
     * returns the requested resource as an inputstream
     * @param resource    the requested resource
     * @return the resource as an inputstream
     */
    private static InputStream getResourceAsStream(String resource) {
        return ResourceManager.class.getResourceAsStream(resource);
    }

    /**
     * returns the requested resource as a URL
     * @param resource    the requested resource
     * @return the resource as a URL
     */
    private static URL getResource(String resource){
        return ResourceManager.class.getResource(resource);
    }

    /**
     * returns the requested image as an Image. Looks in resources/images/ for the requested image
     * @param imageName    the requested image's name
     * @return the image as an Image
     */
    public Image getResourceImage(String imageName){
        return new Image(getResourceAsStream(imagesLocation+imageName));
    }

    /**
     * returns the requested stylesheet. Looks in resources/css/ for the requested stylesheet
     * @param styleSheetName    the requested stylesheet's name
     * @return a string representation of the stylesheet's location
     */
    public String getResourceStyleSheet(String styleSheetName){
        return getResource(cssLocation+styleSheetName).toExternalForm();
    }

}
