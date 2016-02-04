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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.idiro.xmlm.xml.XmlWord;

/**
 * Dictionary: xml words to store a Map.
 * 
 * @author etienne
 *
 */
public class Dictionary extends XmlWord
implements Word.Interface{
	
	public static final String key_lw_word = "word";
	
	Map<String,String> dictionary = new LinkedHashMap<String,String>();
	List<String> words = new LinkedList<String>();
	
	public Dictionary(){}

	public boolean initParser(){
		initChildWord(new Word(),
					"Words contained in the dictionary", 1,Integer.MAX_VALUE);
		return true;
	}
	
	@Override
	public String getDescription() {
		return "A dictionary records words, a key can be recorded only once";
	}

	@Override
	public boolean add(Word w) {
		if(dictionary.get(w.getS(Word.key_s_key)) != null){
			logger.error("Dictionary already contains "+w.getS(Word.key_s_key));
			return false;
		}

		dictionary.put(w.getS(Word.key_s_key), w.getS(Word.key_s_value));
		words.add(w.getS(Word.key_s_key));
		return true;
	}

	/**
	 * @return the dictionary
	 */
	public Map<String, String> getDictionary() {
		return dictionary;
	}

	@Override
	public boolean checkParsing() {
		return true;
	}

	/**
	 * @return the words
	 */
	public final List<String> getWords() {
		return words;
	}
	
}
