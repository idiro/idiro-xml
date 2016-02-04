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

package com.idiro.xmlm.xml;

import org.apache.log4j.Logger;

/**
 * Validate a XmlWord after parsing it
 * @author etienne
 *
 * A validator check if what you have parsed
 * is correct or not, it may also change the
 * value of the word to make it correct
 */
public abstract class XmlValidator{

	protected Logger logger = Logger.getLogger(getClass());
	
	public boolean validateWithLog(XmlWord word){
		boolean ok = validate(word);
		if(!ok){
			logger.error("'"+word.getNameXml()+"' validation failed ("+word.getContext()+")");
			logger.error("Value of '"+word.getNameXml()+"' "+word.getWordValue());
			logger.error("Validation description: "+description());
		}else{
			logger.debug("'"+word.getNameXml()+"' validation succeed");
		}
		return ok;
	}
	
	public abstract String description();
	
	public abstract boolean validate(XmlWord word);
	
}
