package idiro.xmlm.process;

import idiro.BlockInt;
import idiro.check.FileChecker;
import idiro.xmlm.XMLProcessManager;
import idiro.xmlm.xml.XmlWord;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Runs all the XML processes contained in a file.
 * 
 * XMLFileProcess takes an xml file as input.
 * From this xml file it will runs the processes,
 * the children of the "process" tag.
 * 
 * XMLFileProcess extends XmlWord, it is the root
 * field of the xml called currently "process"
 * 
 * @author etienne
 *
 */
public final class XMLFileProcess extends XmlWord 
implements BlockInt,XMLFieldProcess.Interface{

	/**
	 * xml file to read from
	 */
	private File xmlFile;

	/**
	 * List of process to launch
	 */
	List<XMLFieldProcess> listXMLFieldProcess;

	public XMLFileProcess(){
		listXMLFieldProcess = new LinkedList<XMLFieldProcess>();
		setNameXml("process");
	}

	@Override
	public boolean execute(String[] args) {
		boolean ok = true;
		if(args.length != 1){
			logger.error("Takes as input only one parameter: the xmlFile");
			ok = false;
		}else{
			xmlFile = new File(args[0]);
			FileChecker fch = new FileChecker(xmlFile);
			if(!fch.canRead()){
				logger.error("The file "+fch.getFilename()+" is not readable");
				ok = false;
			}
		}
		if(ok)
			ok = init();

		if(ok)
			ok = run();

		return ok;
	}

	/**
	 * Check if has been execute correctly.
	 * Nothing to check here
	 */
	protected boolean finalCheck() {
		return true;
	}

	/**
	 * Description of the class
	 */
	public String getDescription() {
		return "Reads the xml first layer, parse the features and launch the processes";
	}


	/**
	 * Initialise XMLFileProcess
	 * 
	 * Parse all the argument to each process contained in the xml
	 */
	public boolean init() {
		boolean ok = true;

		ok = initParser();

		//Register the class in the xml and parse the features
		Document document = getDocument(xmlFile);
		if (document == null) {
			return false;
		}

		Element root = document.getDocumentElement();
		root.normalize();

		if(ok){
			ok = this.parse(root);
		}
		return ok;
	}

	/**
	 * Runs all the process contained in the xml file given in the constructor
	 * one per one
	 * 
	 * @return true if everything runs successfully
	 */
	protected boolean run() {
		boolean ok = true;
		Iterator<XMLFieldProcess> it = listXMLFieldProcess.iterator();
		XMLFieldProcess cur = null;
		while(it.hasNext() && ok){
			cur = it.next();
			ok = cur.execute();	
		}
		if(!ok){
			logger.error("Fail to execute "+cur.getClass());
		}else{
			logger.debug("Process launched successfully");
		}
		return ok;
	}


	public boolean initParser(){
		boolean ok = true;
		//List the processes available
		Iterator<String> it = 
				XMLProcessManager.getInstance().getNonAbstractClassesFromSuperClass(
						XMLFieldProcess.class.getCanonicalName()).iterator();
		while(it.hasNext()){
			String className = it.next();
			try{
				XMLFieldProcess newProc = (XMLFieldProcess) Class.forName(className).newInstance();
				initChildWord(newProc, newProc.getDescription(), 0, Integer.MAX_VALUE);
				logger.debug(newProc.getNameXml()+" added to "+getNameXml());
			}catch(Exception e){
				logger.error("Fail to instanciate "+ className);
				logger.error(e.getMessage());
				ok = false;
			}
		}
		return ok;
	}

	/**
	 * Get help for the process with a description of the xml parameter
	 * 
	 * @return a string help
	 */
	public String help(){
		StringBuffer builder = new StringBuffer("Help for the the process "+getNameXml()+"\n\n");
		builder.append(getDescription()+"\n\n");

		builder.append("For running the process, the xml is under the form:\n\n");

		builder.append(this.help(0,4));
		return builder.toString();
	}

	/**
	 * Get help on only several Fields
	 */
	public String help(String[] fields){

		initParser();
		StringBuffer builder = new StringBuffer();

		builder.append("Help for the the processes: ");
		for(int i=0;i<fields.length;++i)
			builder.append(fields[i]+",\n");
		builder.append("\n");
		builder.append("For running these processes, the xml is under the form:\n\n");
		XMLFieldProcess process;

		builder.append("<"+getNameXml()+">\n");
		builder.append("<!--"+getDescription()+"-->\n\n");
		for(int i = 0; i < fields.length; ++i){
			if(getChildrenProperties().get(fields[i]) == null){
				logger.error(fields[i]+" is an unknown process");
			}else{
				try {
					process = (XMLFieldProcess) Class.forName(getChildrenProperties().get(fields[i]).getCanonicalName()).newInstance();
					builder.append(process.help(4, 4));

				} catch (Exception e) {
					logger.error("Error for instancing: "+getChildrenProperties().get(fields[i]).getCanonicalName()+" ("+fields[i]+")");
				}
			}
		}
		builder.append("</"+getNameXml()+">\n");

		return builder.toString();
	}

	/** 
	 * Get help only on a process 
	 */
	public String help(String field){
		initParser();
		if(getChildrenProperties().get(field) == null){
			logger.error("field "+field+" unknown");
			return "";
		}

		XMLFieldProcess process;
		StringBuffer builder = new StringBuffer();
		try {
			process = (XMLFieldProcess) Class.forName(getChildrenProperties().get(field).getCanonicalName()).newInstance();

			builder.append("Help for the the process "+process.getNameXml()+"\n\n");

			builder.append("For running the process, the xml is under the form:\n\n");

			builder.append("<"+getNameXml()+">\n");
			builder.append("<!--"+getDescription()+"-->\n\n");

			builder.append(process.help(4, 4));
			builder.append("</"+getNameXml()+">\n");

		} catch (Exception e) {
			logger.error("Error for instancing: "+getChildrenProperties().get(field).getCanonicalName()+" ("+field+")");
		}
		return builder.toString();
	}

	/**
	 * Get the name associate to each of the XMLFieldProcess contained in the program
	 * @param classNames List of the class XMLFieldProcess of the program
	 * @return
	 */
	public Map<String,String> getProcessesName(){
		Map<String,String> res = new LinkedHashMap<String,String>();
		Iterator<String> it = getChildrenProperties().keySet().iterator();
		while(it.hasNext()){
			String field = it.next();
			res.put(field, getChildrenProperties().get(field).getCanonicalName());
		}
		return res;
	}

	/**
	 * Returns a document for parsing based on the file.
	 * 
	 * @param file
	 *            to parse
	 * @return document
	 */
	public final Document getDocument(final File file) {
		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse (file);
			return doc;
		} catch (Exception e) {
			logger.error(e.getCause()+" "+e.getMessage());
			return null;
		}
	}

	@Override
	public boolean add(XMLFieldProcess process) {
		listXMLFieldProcess.add(process);
		return true;
	}

	@Override
	public boolean checkParsing() {
		return true;
	}
}
