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

package com.idiro.xmlm;

/*
 * Copyright 2009 by Idiro Technologies. 
 * All rights reserved
 */


import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.idiro.BlockManager;
import com.idiro.Log;
import com.idiro.xmlm.process.XMLFieldProcess;
import com.idiro.xmlm.process.XMLFileProcess;


/**
 * Xml process manager.
 * 
 * Manages idiro xml processes (reflection),
 * it launches each xml one after the other,
 * it calls a help functionality.
 * 
 * Xml process manager is a singleton class.
 * 
 * @see com.idiro.xmlm.process.XMLFileProcess
 * 
 * 
 * @author etienne
 * 
 */
public class XMLProcessManager extends BlockManager{

	/**
	 * The logger.
	 */
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * The single instance of process runner.
	 */
	private static XMLProcessManager runner = new XMLProcessManager();

	/**
	 * True if initialised
	 */
	private boolean init = false;

	/**
	 * Constructor.
	 * 
	 */
	protected XMLProcessManager() {

	}

	/**
	 * Get the only instance that exists.
	 * 
	 * Get the instance of the object.
	 * Initialise the instance if it is not done.
	 * 
	 * 
	 * @return Returns the single allowed instance of ProcessRunner
	 */
	public static XMLProcessManager getInstance() {
		if(!runner.init){
			runner.init = true;
			// Loads in the log settings.
			Log.init();
		}
		return runner;
	}

	/**
	 * Runs a process manager
	 * 
	 * @param args
	 *            The args: -h as first argument is accepted
	 *            list of xml files for the rest
	 */
	private boolean run(final String[] args){

		boolean ok = true;

		if (args.length == 0) {
			logger.error("You must either pass in a parameter or and an xml file");
			ok = false;
		}else{

			String[] values;
			if (args[0].equals("-h") || args[0].startsWith("--help")) {
				runHelp(args);
				ok = true;
			} else {
				values = args;

				int i = 0;
				while(i < values.length && ok){
					if (values[i].endsWith(".xml")) {
						String[] xml = new String[1];
						xml[0] = values[i];
						logger.info("Running " + values[i]);                 
						XMLFileProcess p = new XMLFileProcess();
						ok &= p.execute(xml);
					} else {
						logger.error(values[i]+ " is not a xml file");
						ok = false;
					}
					++i;
				}
			}
		}
		if(!ok){
			help();
		}
		return ok;
	}

	/**
	 * Gives the help into an error log message. 
	 * 
	 * The help returns the list of the xml field accepted.
	 * @see com.idiro.xmlm.process.XMLFieldProcess
	 * 
	 * @return true if it has been written successfully
	 */
	public boolean help(){
		boolean ok = true;
		String rootPackageWithPoint = this.getRootPackage()+".";
		List<String> classeNames = getNonAbstractClassesFromSuperClass(XMLFieldProcess.class.getCanonicalName());
		StringBuilder builder = new StringBuilder();
		builder.append("The field processes present in this package are:\n");
		Collections.sort(classeNames);
		String lastPackage = "";

		for (String className : classeNames) {

			Class<?> c = null;
			try{

				// This is the class to load
				c = Class.forName(className);
				XMLFieldProcess b = (XMLFieldProcess) c.newInstance();
				String packageName = className.substring(0, className.lastIndexOf("."));
				packageName = packageName.replaceFirst(rootPackageWithPoint, "");
				packageName = packageName.replaceAll("[.]", " ");
				if (!packageName.equals(lastPackage)) {
					builder.append("\n\n" + packageName + "\n");
				}
				builder.append("\n       " + b.getNameXml() + getSpaces(40 - b.getNameXml().length()) + b.getDescription());
				lastPackage = packageName;
			}catch(Exception e){
				logger.error("The XMLFieldProcess "+c.getCanonicalName()+" newInstance return an exception: the default constructor is it implemented ?");
				ok = false;
			}
		}
		builder.append("\n\n\nFor more information on a process please type -h <name of the process>\n");
		builder.append("Program usage: program <xmlfile> [<xmlfile> <xmlfile>...]\n\n");
		logger.error(builder.toString());

		return ok;
	}

	/**
	 * Runs help for specific xml field process(es).
	 * 
	 * Returns in a log form a template xml file for
	 * the specified field(s).
	 * 
	 * @see com.idiro.xmlm.process.XMLFieldProcess
	 * @param args -h/--help then the list of xml field name 
	 */
	private void runHelp(String[] args){

		XMLFileProcess fProcess = new XMLFileProcess();
		String help = "";
		if(args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))){
			help();
		}else if( args.length == 2){
			if(args[1].toLowerCase().equals("all")){
				help = fProcess.help();
			}else{
				help = fProcess.help(args[1]);
			}
		}else if(args.length > 2){
			String[] values = new String[args.length -1];
			for(int i = 1; i < args.length;++i){
				values[i-1] = args[i];
			}
			help = fProcess.help(values);
		}else{
			help();
		}
		logger.info("\n"+help);
	}

	/**
	 * Gets the command for running the specified task.
	 * 
	 * @param taskClass
	 *            The class
	 * @return the command
	 */
	public static String getProcessCommand(final Class<?> taskClass) {
		String text = taskClass.getCanonicalName();
		text = text.replaceFirst(getInstance().getRootPackage().toLowerCase()+".", "");
		text = text.toLowerCase();
		return text;
	}

	/**
	 * @return the init
	 */
	public boolean isInit() {
		return init;
	}


	/**
	 * Runs a task Manager.
	 * 
	 * @param args
	 */
	public static boolean runTaskManager(String[] args){
		boolean ok = false;
		XMLProcessManager runner = XMLProcessManager.getInstance();
		if(ok = runner.isInit()){
			String currentDir = (new File("")).getAbsolutePath();

			String arguments = new String();
			for(int i=0; i < args.length;++i){
				arguments += args[i] + " ";
			}

			runner.logger.info("Starting. "+runner.getClass().getPackage().toString().split(" ")[1].toLowerCase()+" " + arguments + " from " + currentDir);
			ok = runner.run(args);

			runner.logger.info("Ending. "+ runner.getClass().getPackage().toString().split(" ")[1].toLowerCase()+" "+ arguments + " from " + currentDir);

		}
		return ok;
	}

	/**
	 * Change of process manager. Helpful to change package search.
	 * 
	 * By default the runner is set to be a XMLProcessManager instance.
	 * So only XMLFieldProcess belonging to com.idiro.xmlm.* packages 
	 * will be found.
	 * @param runner the runner to set
	 */
	public static final void setRunner(XMLProcessManager runner) {
		XMLProcessManager.runner = runner;
	}


}

