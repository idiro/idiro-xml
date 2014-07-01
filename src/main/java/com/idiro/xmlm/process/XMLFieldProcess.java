package com.idiro.xmlm.process;


import org.apache.log4j.Logger;

import com.idiro.BlockInt;
import com.idiro.utils.Time;
import com.idiro.xmlm.xml.XmlWord;

/**
 * Abstract class to create an independent xml process.
 * 
 * 
 * Each XMLFieldProcess extends XmlWord, it uses the 
 * parse mechanism of XmlWord before being execute by the
 * "execute" command in the class XMLFileProcess.
 * 
 *  
 * @author etienne
 *
 */
public abstract class XMLFieldProcess extends XmlWord implements BlockInt{

	/**
	 * The execution start time
	 */
	protected long startTime;
	
	/**
	 * The logger
	 */
	private static Logger XMLFieldProcessLogger = Logger.getLogger(XMLFieldProcess.class);

	
	/**
	 * Logger of the task
	 */
	protected Logger logger = Logger.getLogger(this.getClass());

	
	/**
	 * Register if the process has been parsed or not
	 */
	private boolean parsed = false;

	public interface Interface extends XmlWord.Interface{
		boolean add(XMLFieldProcess process);
	}
	
	/**
	 * Execute a XMLFieldProcess
	 * 
	 * @return true if the execution has been successful
	 */
	public boolean execute(){

		startTime = System.currentTimeMillis();

		if(!parsed){
			XMLFieldProcessLogger.error("Arguments has not been parsed, or not correctly for "+this.getClass());
			return false;
		}


		XMLFieldProcessLogger.debug("Start to init "+this.getClass()+" at "+ ((double)System.currentTimeMillis())/1000);
		if(!init()){
			XMLFieldProcessLogger.error("The class "+this.getClass()+" has not been initialised correctly");
			return false;
		}
		XMLFieldProcessLogger.debug("Start to run "+this.getClass()+" at "+ ((double)System.currentTimeMillis())/1000);
		if(!run()){
			XMLFieldProcessLogger.error("The class "+this.getClass()+" has not run correctly");
			return false;
		}
		XMLFieldProcessLogger.debug("Start to check "+this.getClass()+" at "+ ((double)System.currentTimeMillis())/1000);
		if(!finalCheck()){
			XMLFieldProcessLogger.error("The class "+this.getClass()+" has not the output expected");
			return false;
		}

		long endTime = System.currentTimeMillis();
		long[] time = Time.getHMSm(endTime - startTime);
		XMLFieldProcessLogger.info(this.getClass()+" complete in " + time[Time.DAYS] + " days " + time[Time.HOURS] + " hours "
				+ time[Time.MINUTES] + " mins " + time[Time.SECONDS] + " seconds " + time[Time.MILLIS] + " ms ");

		return true;
	}
	
	@Override
	public boolean execute(String[] argv){
		return execute();
	}
	
	
	@Override
	/**
	 * Add the XMLFieldProcess to a XMLFileProcess
	 * to be executed
	 */
	public boolean callWordInterfaceMethods() {
		parsed = true;
		return ((Interface)wordInterface).add(this);
	}
	
	/**
	 * No need of checkParsing since the init method has the same role
	 */
	@Override
	public boolean checkParsing() {
		return true;
	}

	/**
	 * This runs the XMLFieldProcess
	 * 
	 * @return true if runs ok
	 */
	protected abstract boolean run();

	/**
	 * This check the output of the XMLFieldProcess
	 * 
	 * @return true if checks ok 
	 */
	protected abstract boolean finalCheck();
	
}
