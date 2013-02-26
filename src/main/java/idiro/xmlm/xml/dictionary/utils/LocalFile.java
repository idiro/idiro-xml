package idiro.xmlm.xml.dictionary.utils;

import idiro.xmlm.xml.LeafValidator;
import idiro.xmlm.xml.XmlLeaf;
import idiro.xmlm.xml.XmlWord;

import java.io.File;

public class LocalFile extends XmlWord {

	public static final String key_s_file = "file";
	public static final String key_b_rm = "removeafterprocess";
	
	LeafValidator<String> fileValidator = null;
	
	
	public LocalFile(){}
	
	public LocalFile(LeafValidator<String> fileValidator){
		super();
		this.fileValidator = fileValidator;
	}
	
	public boolean initParser(){
		if(fileValidator != null){
			initChildWord(new XmlLeaf<String>(key_s_file,fileValidator,""), 
				"Path of a file in the local file system", 1,1);
		}else{
			initChildWord(new XmlLeaf<String>(key_s_file,""), 
					"Path of a file in the local file system", 1,1);
		}
		initChildWord(new XmlLeaf<Boolean>(key_b_rm,false), 
				"Remove the file after process", 1,1);
		return true;
	}
	
	@Override
	public String getDescription() {
		return "Parse arguments for a local file definition, with a path, and if it has to be removed";
	}

	@Override
	public boolean checkParsing() {
		return true;
	}

	public File getFile(){
		return new File(getS(key_s_file));
	}
}
