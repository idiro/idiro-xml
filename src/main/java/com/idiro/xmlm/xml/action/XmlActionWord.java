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
