/** 
 *  Copyright © 2016 Red Sqirl, Ltd. All rights reserved.
 *  Red Sqirl, Clarendon House, 34 Clarendon St., Dublin 2. Ireland
 *
 *  This file is part of Xml parser utility
 *
 *  User agrees that use of this software is governed by: 
 *  (1) the applicable user limitations and specified terms and conditions of 
 *      the license agreement which has been entered into with Red Sqirl; and 
 *  (2) the proprietary and restricted rights notices included in this software.
 *  
 *  WARNING: THE PROPRIETARY INFORMATION OF Xml parser utility IS PROTECTED BY IRISH AND 
 *  INTERNATIONAL LAW.  UNAUTHORISED REPRODUCTION, DISTRIBUTION OR ANY PORTION
 *  OF IT, MAY RESULT IN CIVIL AND/OR CRIMINAL PENALTIES.
 *  
 *  If you have received this software in error please contact Red Sqirl at 
 *  support@redsqirl.com
 */

package com.idiro.xmlm.xml.dictionary.utils;


import java.util.LinkedHashMap;
import java.util.Map;

import com.idiro.xmlm.xml.LeafValidator;
import com.idiro.xmlm.xml.XmlLeaf;
import com.idiro.xmlm.xml.XmlWord;

/**
 * Child of Dictionary.
 * @see com.idiro.xmlm.xml.dictionary.utils.Dictionary
 * @author etienne
 *
 */
public class Word extends XmlWord {
	
	public static final String key_s_key = "key";
	public static final String key_s_value = "value";
	
	private static Map<String,LeafValidator<String>> keyValidators = 
			new LinkedHashMap<String,LeafValidator<String>>();
	
	private static Map<String,LeafValidator<String>> valueValidators = 
			new LinkedHashMap<String,LeafValidator<String>>();
	
	public interface Interface extends XmlWord.Interface{
		boolean add(Word f);
	}
	
	public Word(){}
	
	public boolean initParser(){
		logger.debug(getPath()+": "+valueValidators.containsKey(getPath()));
		
		initChildWord(new XmlLeaf<String>(key_s_key,
				keyValidators.get(getPath()),""), 
				"key, ie the word", 1,1);
		initChildWord(new XmlLeaf<String>(key_s_value,
				  valueValidators.get(getPath()),""), 
				  "value, ie the definition", 1,1);
		return true;
	}
	
	@Override
	public String getDescription() {
		return "Parse arguments for a word which records a key and a value.";
	}
	
	@Override
	public boolean callWordInterfaceMethods() {
		return ( (Interface)wordInterface).add(this);
	}

	@Override
	public boolean checkParsing() {
		return true;
	}

	/**
	 * @return the keyValidators
	 */
	public static Map<String,LeafValidator<String>> getKeyValidators() {
		return keyValidators;
	}

	/**
	 * @return the valueValidators
	 */
	public static Map<String,LeafValidator<String>> getValueValidators() {
		return valueValidators;
	}
	
}
