package idiro.xmlm.xml.dictionary.utils;

import idiro.xmlm.xml.XmlWord;

import java.util.LinkedHashMap;
import java.util.Map;

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

	public boolean initParser(){
		initChildWord(new Word(),"Words contained in the dictionary", 1,Integer.MAX_VALUE);
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
	
	@Override
	public Object getWordValue(){
		return dictionary;
	}
	
	@Override
	public Object getWordDefaultValue(){
		return new LinkedHashMap<String,String>();
	}
	
}