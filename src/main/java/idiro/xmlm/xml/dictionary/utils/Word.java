package idiro.xmlm.xml.dictionary.utils;

import idiro.xmlm.xml.XmlLeaf;
import idiro.xmlm.xml.XmlWord;

/**
 * Child of Dictionary.
 * @see idiro.xmlm.xml.dictionary.utils.Dictionary
 * @author etienne
 *
 */
public class Word extends XmlWord {
	
	public static final String key_s_key = "key";
	public static final String key_s_value = "value";
	
	public interface Interface extends XmlWord.Interface{
		boolean add(Word f);
	}
	
	public boolean initParser(){
		initChildWord(new XmlLeaf<String>(key_s_key,""), "key, ie the word", 1,1);
		initChildWord(new XmlLeaf<String>(key_s_value,""), "value, ie the definition", 1,1);
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
	
}
