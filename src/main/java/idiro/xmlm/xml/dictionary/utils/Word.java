package idiro.xmlm.xml.dictionary.utils;

import idiro.xmlm.xml.LeafValidator;
import idiro.xmlm.xml.XmlLeaf;
import idiro.xmlm.xml.XmlWord;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Child of Dictionary.
 * @see idiro.xmlm.xml.dictionary.utils.Dictionary
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
