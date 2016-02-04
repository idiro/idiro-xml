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

package com.idiro.xmlm.xml.validator.string;

import com.idiro.check.FileChecker;
import com.idiro.xmlm.xml.LeafValidator;
import com.idiro.xmlm.xml.XmlLeaf;


/**
 * Check if the file exists and is readable
 * @author etienne
 *
 */
public class InputFileValidator extends LeafValidator<String>{
	
	@Override
	public String description() {
		return "file exists and is readable";
	}

	@Override
	public boolean validate(XmlLeaf<String> leaf) {
		boolean ok = false;
		FileChecker fCh = new FileChecker(leaf.getWordValue());
		if(fCh.isInitialized()){
			if(fCh.exists()){
				if(fCh.canRead()){
					ok = true;
				}else{
					logger.warn("The file "+fCh.getFilename()+ " is not readable");
				}
			}else{
				logger.warn("The file "+fCh.getFilename()+ " does not exist");
			}
		}else{
			logger.warn(leaf.getWordValue()+" does not represent a file path");
		}
		return ok;
	}

}
