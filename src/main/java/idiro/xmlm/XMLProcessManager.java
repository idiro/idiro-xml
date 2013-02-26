package idiro.xmlm;

/*
 * Copyright 2009 by Idiro Technologies. 
 * All rights reserved
 */

import idiro.BlockManager;
import idiro.Log;
import idiro.xmlm.process.XMLFieldProcess;
import idiro.xmlm.process.XMLFileProcess;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Xml process manager.
 * 
 * Manages idiro xml processes (reflection),
 * it launches each xml one after the other,
 * it calls a help functionality.
 * 
 * Xml process manager is a singleton class.
 * 
 * @see idiro.xmlm.process.XMLFileProcess
 * 
 * 
 * @author etienne
 * 
 */
public final class XMLProcessManager extends BlockManager{

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
	private XMLProcessManager() {

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
	 * @see idiro.xmlm.process.XMLFieldProcess
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
	 * @see idiro.xmlm.process.XMLFieldProcess
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


}

