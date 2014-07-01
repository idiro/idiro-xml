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
