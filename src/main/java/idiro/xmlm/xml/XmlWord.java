package idiro.xmlm.xml;



import idiro.xmlm.xml.validator.AlwaysTrueValidator;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Abstract class to register a xml word in the dictionary of the programm.
 *
 * To Create a new word, a class has to extends XmlWord,
 * the class name in lower case is the actual xml field.
 * It may be changed in the constructor or by a set.
 * 
 * 
 * In the initParser, the developer has to instantiate 
 * child(ren) with the methods initChildWord. If the
 * children is a leaf you can instantiate a XmlLeaf
 * within it.
 * 
 * The children are parsed automatically in a HashMap
 * of their parents @see idiro.xmlm.xml.XmlContainer.
 * 
 * Options:
 * 
 * If you do not want to parse the XmlWord in the hashmap
 * but another object, you have to implement getWordValue()
 * which returns by default 'this'
 * 
 * If you are not happy with the hashMap you can parse manually
 * You have to create an Interface in the children implemented in
 * the parent, then implement the method in callWordInterfaceMethods()
 * in the child object. You may also in the parent want to remove
 * the children from the map, it is the method delete
 * 
 * You have at the moment of writing an example in 
 * idiro.xmlm.xml.dictionary.utils.Dictionary 
 * 
 * If a child is optional but you want to give him a default
 * value if it is not here, you can set the defaultValue, if there
 * is no default value it will returns null and not add anything to
 * the map
 * 
 * At the end of each parsing you check if everything is ok with
 * endParsing, if the parent has special needs and need to add tests
 * You can set a validator to any children in the initParser method.
 * It is set by default to 'AlwaysTrueValidator'. 
 *
 */
@SuppressWarnings("deprecation")
public abstract class XmlWord extends XmlContainer{
	
	/**
	 * Extensions of the xml word
	 */
	private Map<String,XmlContextExt> extensions = new LinkedHashMap<String,XmlContextExt>();
	
	private XmlValidator validator = new AlwaysTrueValidator();

	/**
	 * Default Value of a word if not parsed
	 */
	private Object defaultValue = null;
	
	
	public XmlWord(){
		super();
	}
	
	public XmlWord(String nameXml){
		super(nameXml);
	}
	
	public XmlWord(XmlValidator validator){
		super();
		if(validator != null){
			this.validator = validator;
		}
	}
	
	public XmlWord(String nameXml, XmlValidator validator){
		super(nameXml);
		if(validator != null){
			this.validator = validator;
		}
	}
	
	
	/**
	 * Get a description of the word
	 * @return a description
	 */
	public abstract String getDescription();

	/**
	 * Methods called at the end of the parsing
	 * in order to call methods from the wordParent 
	 * instance,by default disable
	 */
	public boolean callWordInterfaceMethods(){
		return true;
	}
	
	/**
	 * Initialize and add an extension
	 * @param ext the extension to initialize
	 * @return
	 */
	@Deprecated
	protected boolean addExtension(XmlContextExt ext){
		ext.setWord(this);
		ext.initParser();
		if(extensions.get(ext.getExtensionName()) == null ){
			extensions.put(ext.getExtensionName(),ext);
			return true;
		}
		return false;
	}

	/**
	 * Check if the parser is correct (there is no fields that appears two times)
	 * @return true if ok
	 */
	protected boolean checkParserInit(){
		boolean ok = true;
		logger.debug("check the parser in "+getNameXml());
		
		LinkedList<String> children = new LinkedList<String>(getChildrenProperties().keySet());
		Iterator<XmlContextExt> itExt = extensions.values().iterator();
		while(ok && itExt.hasNext()){
			Iterator<String> extChIt = itExt.next().getChildrenProperties().keySet().iterator();
			while(ok && extChIt.hasNext()){
				String ch = extChIt.next();
				if(children.contains(ch)){
					logger.error("Development error: "+ch + "appears several time in "+getNameXml());
					ok = false;
				}else{
					children.add(ch);
				}
			}
		}
		
		return ok;
	}
	
	/**
	 * Get the extension from the name of a child
	 * @param nodeName
	 * @return
	 */
	private String getExtNameFromChild(String nodeName){
		boolean found = false;
		String extName = null;
		Iterator<XmlContextExt> itExt = extensions.values().iterator();
		while(!found && itExt.hasNext()){
			XmlContextExt ext = itExt.next();
			if(ext.getChildrenProperties().get(nodeName) != null){
				found = true;
				extName = ext.getNameXml();
			}
		}
		logger.debug("In "+getNameXml()+" search for the extension of "+nodeName+": "+extName);
		return extName;
	}
	
	/**
	 * parses the arguments from a leaf of the xml file
	 *
	 * @param n the xml file leaf
	 * @return true if it has been parsed correctly
	 */
	public boolean parse(final Node n){
		boolean ok = true;

		if (!n.getNodeName().equals(getNameXml())) {
			logger.error("Node '"+getNameXml()+"' expected and not '"+n.getNodeName()+"'.");
			return false;
		}

		
		Map<String,Integer> nodeNameNumber = new LinkedHashMap<String,Integer>();
		Map<String, LinkedList<Node> > extensionNodes = new LinkedHashMap< String,LinkedList<Node> >();
		Iterator<XmlContextExt> itExt = extensions.values().iterator();
		while(itExt.hasNext()){
			extensionNodes.put(itExt.next().getNameXml(), new LinkedList<Node>());
		}

		NodeList allNodes = n.getChildNodes();
		//Create a XMLWord for each name found
		logger.trace("field '"+this.getNameXml()+ "' parse words...");
		int i = 0;
		while(i < allNodes.getLength() && ok){
			ok = parseChild(allNodes.item(i), nodeNameNumber,extensionNodes);
			++i;
		}
		
		//Parse the extensions
		logger.trace("field '"+this.getNameXml()+ "' parse extension...");
		itExt = extensions.values().iterator();
		while(itExt.hasNext()){
			XmlContextExt ext = itExt.next();
			ok = ext.parse(extensionNodes.get(ext.getNameXml()));
		}

		//Check if everything is all right (only the default, each extension is treated independently)
		logger.trace("field '"+this.getNameXml()+ "' check...");
		if(ok){
			ok = checkParsingFinal(nodeNameNumber);
		}
		
		if(ok){
			ok = checkParsing();
		}

		logger.debug("Finish to parse '"+getNameXml()+"' ("+getContext()+")");
		return ok;
	}
	
	/**
	 * Parse one node , child
	 * We keep in track the number of time a child is parsed.
	 * We parse all the extension nodes in another step
	 * 
	 * @param child
	 * @param nodeNameNumber
	 * @param extensionNodes
	 * @return
	 */
	protected boolean parseChild(final Node child,Map<String,Integer> nodeNameNumber,
			Map<String, LinkedList<Node> > extensionNodes){
		boolean ok = true;
		Integer count;
		String chName = child.getNodeName();
		XmlProperty childProperty = null;
		if((childProperty= getChildrenProperties().get(chName) )== null){

			if(!chName.equals("#text") &&
					!chName.equals("#comment")){
				String extName = null;
				if( (extName = getExtNameFromChild(chName)) != null){
					extensionNodes.get(extName).add(child);
				}else{
					//if it is a comments or a line jump is fine
					logger.error("The node '"+chName+"' does not exists");
					ok = false;
				}
			}
		}else{
			//We count the element name
			if( (count = nodeNameNumber.get(chName)) != null){
				nodeNameNumber.put(chName, count + 1);
			}else{
				nodeNameNumber.put(chName, 1);
			}
			count = nodeNameNumber.get(chName);
			if(childProperty.isBellow(count)){
				//Search the Node in the list XmlWord


				if( getChildrenProperties().get(chName) != null){
					logger.debug("Word child found: '"+chName+"'");
					if(! initAndParseWordChild(chName, child)){
						logger.error("Error parsing the node '"+chName+"'");
						ok = false;
					}

				}else {
					ok = false;
					logger.error("'"+chName+"' is not a leaf of '"+getNameXml()+"'");
					
				}
			}else{
				logger.error("The field '"+chName+"' should appear less than "+
			childProperty.getMax()+1+" in '"+getNameXml()+"'");
				ok = false;
			}
		}
		return ok;
	}

	/**
	 * Generate a help for the word
	 * 
	 * @param space the number of spaces to generate
	 * @return a help
	 */
	public String help(int space, int spacePerLevel){
		
		initParser();
		StringBuffer builder = new StringBuffer();
		builder.append(getSpaces(space)+"<"+getNameXml()+">\n");
		builder.append(getSpaces(space+spacePerLevel)+"<!--"+getDescription()+"-->\n\n");

		Iterator<String> it = getChildrenProperties().keySet().iterator();
		while(it.hasNext()){
			String cur = it.next();
			builder.append(getSpaces(space+spacePerLevel)+"<!--"+
					"Repeated between "+getChildrenProperties().get(cur).getMin()+
					" and "+getChildrenProperties().get(cur).getMax()+" times: "+
					getChildrenProperties().get(cur).getComment()+"-->\n");
					
			
			if( getChildrenProperties().get(cur) != null){
				try {
					XmlWord temp = initParserChild(cur);
					if(temp != null){
						builder.append(temp.help(space+spacePerLevel, spacePerLevel));
					}
					
				} catch (Exception e) {
					logger.error("Instantiation exception "+e.getCause()+" "+e.getMessage());
				}
			}else{
				logger.error("In "+getNameXml()+" child "+cur+" unknown");
			}
		}
		Iterator<XmlContextExt> extIt = getExtensions().values().iterator();
		while(extIt.hasNext()){
			builder.append(extIt.next().help(space+spacePerLevel, spacePerLevel));
		}
		builder.append(getSpaces(space)+"</"+getNameXml()+">\n\n");
		return builder.toString();
	}
	

	/**
	 * @return the extensions
	 */
	public Map<String,XmlContextExt> getExtensions() {
		return extensions;
	}
	
	public XmlContextExt getExtension(String extName) {
		return extensions.get(extName);
	}
	
	/**
	 * Get the word container
	 * @return
	 */
	public XmlWord getWord(){
		return this;
	}

	/**
	 * @return the validator
	 */
	public XmlValidator getValidator() {
		return validator;
	}

	/**
	 * @param validator the validator to set
	 */
	public void setValidator(XmlValidator validator) {
		this.validator = validator;
	}
	

	/**
	 * Get the value to insert into the Parent
	 * By default it is itself
	 * @return
	 */
	public Object getWordValue(){
		return this;
	}

	/**
	 * Get the default value to insert into the Parent
	 * If the field does not appear
	 * By default it is itself
	 * @return
	 */
	public Object getWordDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	protected void setWordDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
}


