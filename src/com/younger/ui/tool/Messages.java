/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    Messages.java
 *    Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 *
 */

package com.younger.ui.tool;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Messages.
 *
 * @author apple yyyang@tju.edu.cn
 * @version $Revision: 7059 $
 */
public class Messages {	
  private static Messages instance;
  private static Locale locale = Locale.getDefault();
  private static String packageLocation = Messages.class.getPackage().getName();
  private static String DEFAULT_FILE_NAME = ".messages";

  /**
   * getInstance.
   * @return 
   */
  public static Messages getInstance() {
    if (instance == null) {
      instance = new Messages();
    }
    return instance;
  }
	
  /**
   * getString.
   * @param key
   * @return
   */
  public  String getString(String key) {
    try {
//      return ResourceBundle.getBundle(packageLocation + DEFAULT_FILE_NAME + "_" + locale.getLanguage()).getString(key);
    	 return ResourceBundle.getBundle("i18n" + DEFAULT_FILE_NAME + "_" + locale.getLanguage()).getString(key);
    } catch (MissingResourceException e) {
      try {
        return ResourceBundle.getBundle(packageLocation + DEFAULT_FILE_NAME).getString(key);
      } catch (MissingResourceException missingResourceException) {
        return null;
      }
    }
  }
  
  /**
   * getString.
   * @param key
   * @return
   */
  public  String getString(Locale someLocale, String key) {
    try {
//      return ResourceBundle.getBundle(packageLocation + DEFAULT_FILE_NAME + "_" + locale.getLanguage()).getString(key);
    	 return ResourceBundle.getBundle("i18n" + DEFAULT_FILE_NAME + "_" + someLocale.getLanguage()).getString(key);
    } catch (MissingResourceException e) {
      try {
        return ResourceBundle.getBundle(packageLocation + DEFAULT_FILE_NAME).getString(key);
      } catch (MissingResourceException missingResourceException) {
        return null;
      }
    }
  }
	
	
  /**
   * getString.
   * @param key
   * @param locale
   * @return
   */
  public  String getString(String key, Locale locale) {
    try {
    	 return ResourceBundle.getBundle("i18n" + DEFAULT_FILE_NAME + "_" + locale.getLanguage()).getString(key);
//      return ResourceBundle.getBundle(packageLocation + DEFAULT_FILE_NAME + "_" + locale.getLanguage()).getString(key);
    } catch (MissingResourceException e) {
      try {
        return ResourceBundle.getBundle(packageLocation + DEFAULT_FILE_NAME).getString(key);
      } catch (MissingResourceException missingResourceException) {
        return null;
      }
    }
  }
}
