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

package com.idiro.xmlm.xml.action;

import com.idiro.BlockInt;
import com.idiro.utils.Time;
import com.idiro.xmlm.xml.XmlWord;


/**
 * XmlWord which can be executed.
 * 
 * Methods close to a XMLFieldProcess however
 * the word has to be in a XMLFieldProcess.
 * 
 * Use the 'execute' method to execute a 
 * XmlActionWord.
 * 
 * @author etienne
 *
 */
public abstract class XmlActionWord  extends XmlWord implements BlockInt{

	public interface Interface{
		boolean add(XmlActionWord o);
	}
	
	/**
	 * The execution start time
	 */
	protected long startTime;
	
	protected boolean parsed = false;
	
	public XmlActionWord() {
	}
	
	public XmlActionWord(String xmlName) {
		super(xmlName);
	}

	@Override
	public boolean execute(String[] args) {
		return execute();
	}
	
	public boolean execute(){
		startTime = System.currentTimeMillis();

		if(!parsed){
			logger.error("Arguments has not been parsed, or not correctly for "+this.getClass());
			return false;
		}


		logger.debug("Start to init "+this.getClass()+" at "+ ((double)System.currentTimeMillis())/1000);
		if(!init()){
			logger.error("The class "+this.getClass()+" has not been initialised correctly");
			return false;
		}
		logger.debug("Start to run "+this.getClass()+" at "+ ((double)System.currentTimeMillis())/1000);
		if(!run()){
			logger.error("The class "+this.getClass()+" has not run correctly");
			return false;
		}
		logger.debug("Start to check "+this.getClass()+" at "+ ((double)System.currentTimeMillis())/1000);
		if(!finalCheck()){
			logger.error("The class "+this.getClass()+" has not the output expected");
			return false;
		}

		long endTime = System.currentTimeMillis();
		long[] time = Time.getHMSm(endTime - startTime);
		logger.info(this.getClass()+" complete in " + time[Time.DAYS] + " days " + time[Time.HOURS] + " hours "
				+ time[Time.MINUTES] + " mins " + time[Time.SECONDS] + " seconds " + time[Time.MILLIS] + " ms ");

		return true;
	}
	
	@Override
	/**
	 * Add the XMLFieldProcess to a XMLFileProcess
	 * to be executed
	 */
	public boolean callWordInterfaceMethods() {
		parsed = true;
		try{
			return ((Interface) wordInterface).add(this);
		}catch(Exception e){
			logger.debug("Cannot cast "+wordInterface.getClass().getCanonicalName());
		}
		return true;
	}
	
	protected abstract boolean run();

	protected abstract boolean finalCheck();

}
