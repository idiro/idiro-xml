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


import java.io.File;

import com.idiro.xmlm.xml.LeafValidator;
import com.idiro.xmlm.xml.XmlLeaf;
import com.idiro.xmlm.xml.XmlWord;

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
