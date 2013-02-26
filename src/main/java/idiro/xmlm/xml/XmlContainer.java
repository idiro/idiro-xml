package idiro.xmlm.xml;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

/**
 * Way of storing an XML structure in memory, 
 * before and after parsing.
 * 
 * This class allow to store xml on the form of a
 * tree.
 * 
 * In this framework it is plan that the
 * xml is parsed recursively.
 * 
 * The results of xml contained are automatically
 * saved in a HashMap, the result is under the name
 * of the children field. You access them by the 
 * methods get*().
 * 
 * If your need are more complex there is other
 * options that can be set in XmlWord.
 * 
 * @author etienne
 *
 */
public abstract class XmlContainer{

	/**
	 * Interface to implements for each children,
	 * by default the interface is the parent but
	 * the developer can change it for example in
	 * the initParser
	 */   
	public interface Interface{

	}

	protected Logger logger = Logger.getLogger(getClass());


	/**
	 * Name of the xmlWord:
	 * By default is set as the class name,
	 * it can be change in the class constructor,
	 * it is the string to parse in the xml
	 */
	private String nameXml;


	/**
	 * The word Parent of the object.
	 * 
	 * If you have the xml '<idiro><bla>...</bla></idiro>'
	 * the parent of bla will be idiro, and the parent of idiro is null
	 * 
	 */
	private XmlWord wordParent = null;

	/**
	 * The interface object where the parsed children go
	 * For XmlWord children the interface is the parent
	 * For a XmlContextExt the interface it the XmlContextExt
	 */
	protected XmlContainer wordInterface = null;

	/**
	 * Associate each nodename of a XmlWord to a property object
	 * Each children of the node is associate to a property,
	 * that allow to do quick check. 
	 * 
	 * The developer has to specify, the number of time the word
	 * can occur
	 */
	private Map<String,XmlProperty> childrenProperties = new LinkedHashMap<String,XmlProperty>();


	/**
	 * Associate each children to a value
	 */
	private Map<String,Object> children = new LinkedHashMap<String,Object>();



	public XmlContainer(){
		String word = getClass().getName();
		setNameXml(word.split("\\.")[word.split("\\.").length - 1].toLowerCase());
	}

	public XmlContainer(String nameXml){
		setNameXml(nameXml);
	}


	/**
	 * Methods to initialize the parser
	 * @return
	 */
	protected abstract boolean initParser();

	/**
	 * The user may check if the inter-related variables
	 * are fine
	 * @return
	 */
	public abstract boolean checkParsing();

	/**
	 * Initialise a child word
	 * @param name name of the node
	 * @param single single node type are multiple
	 * @param optional optional node or required
	 */
	protected boolean initChildWord(XmlWord word, String comment, int min, int max){
		if(word == null || childrenProperties.get(word.getNameXml()) != null ) return false;

		childrenProperties.put(word.getNameXml(), 
				new XmlProperty(word.getClass().getCanonicalName(),min, max, comment, 
						word.getValidator(), word.getWordDefaultValue()));
		return true;
	}

	/**
	 * Check if everything that have to be parsed is parsed
	 * Gives default value for optional children not parsed
	 * @param nodeNameNumber
	 * @return
	 */
	protected boolean checkParsingFinal(Map<String,Integer> nodeNameNumber){
		boolean ok = true;
		Iterator<String> NodesIt = childrenProperties.keySet().iterator();
		while(NodesIt.hasNext()){
			String nodeName = NodesIt.next();
			if(nodeNameNumber.get(nodeName) != null){
				if( !childrenProperties.get(nodeName).
						isIn(nodeNameNumber.get(nodeName))){
					ok = false;
					logger.error("The node "+nodeName+" has not been parsed correctly");
					logger.error("Occurence of word (min, max, value) = ("+
							childrenProperties.get(nodeName).getMin()+", "+
							childrenProperties.get(nodeName).getMax()+", "+
							nodeNameNumber.get(nodeName)+")");
				}
			}else{
				if(!childrenProperties.get(nodeName).isIn(0)){
					ok = false;
					logger.error("The node "+nodeName+" has not been parsed correctly");
					logger.error("Occurence of word (min, max, value) = ("+
							childrenProperties.get(nodeName).getMin()+", "+
							childrenProperties.get(nodeName).getMax()+", "+
							nodeNameNumber.get(nodeName)+")");
				}else{
					Object o = childrenProperties.get(nodeName).getDefaultValue();
					if(o != null){
						if(childrenProperties.get(nodeName).getMax() == 1){
							put(nodeName,o);
						}else{
							putNewList(nodeName,o);
						}
					}
				}
			}
		}
		return ok;
	}


	protected XmlWord initParserChild(String xmlField){
		XmlWord temp = null;
		boolean ok = true;
		try {
			logger.trace("In '"+getNameXml()+"' create the new word: '"+xmlField+"'...");
			temp = (XmlWord) Class.forName(getChildrenProperties().get(xmlField).getCanonicalName()).newInstance();
			temp.setNameXml(xmlField);
			temp.setValidator(getChildrenProperties().get(xmlField).getValidator());
			temp.setWordDefaultValue(getChildrenProperties().get(xmlField).getDefaultValue());
			logger.trace("In '"+getNameXml()+"' set the parents and container of "+xmlField+"...");
			temp.setWordParent(getWord());
			try{
				temp.wordInterface = this;
			}catch(Exception e){
				logger.debug("'"+temp.getNameXml()+ "' is not parsable to the xml container "+this.getClass().getName());
			}
			//Init Parsing
			logger.trace("In '"+getNameXml()+"' init the parser of '"+xmlField+"'...");
			temp.initParser();

			logger.trace("In '"+getNameXml()+"' check the parser of '"+xmlField+"'...");
			ok = temp.checkParserInit();
		} catch (Exception e) {
			ok = false;
			logger.error("Instantiation exception "+e.getCause()+" "+e.getMessage());
		}

		if(!ok){
			temp = null;
			logger.error("Fail to init '"+xmlField+"' in '"+getNameXml()+"'");
		}else{
			logger.debug("New word '"+getNameXml()+"' created with the fields "+getChildrenProperties().keySet());
		}
		return temp;
	}
	/**
	 * Initialise and parse a child from the xmlname
	 * @param cur
	 * @param child
	 * @return
	 */
	protected boolean initAndParseWordChild(String xmlField,Node child){
		boolean ok = true;
		try {

			XmlWord temp = initParserChild(xmlField);
			ok = temp != null;
			//Parse
			if(ok){
				logger.trace("In '"+getNameXml()+"' parse children...");
				ok = temp.parse(child);
			}else{
				logger.error("The parser checker returns false in '"+getNameXml()+"'");
			}

			if(ok){
				ok = temp.getValidator().validateWithLog(temp);
			}

			if(ok){
				//Call interface method
				logger.trace("field '"+temp.getNameXml()+ "' parse to parent...");
				put(xmlField, temp.getWordValue());
				ok = temp.callWordInterfaceMethods();
			}

		} catch (Exception e) {
			ok = false;
			logger.error("Instantiation exception "+e.getCause()+" "+e.getMessage());
		}
		return ok;
	}

	/**
	 * Get the word container of this object
	 * @return
	 */
	public abstract XmlWord getWord();

	/**
	 * Generate a help for the word
	 * 
	 * @param space the number of spaces to generate
	 * @return a help
	 */
	public abstract String help(int space, int spacePerLevel);



	/**
	 * Returns the context of the current word
	 * 
	 * It returns the nameXml of a parent
	 * With a number > 0, it returns the parent starting by the oldest
	 * With a number < 0, it returns the parent starting by the youngest
	 * With a number = 0, return null
	 * @param number
	 * @return
	 */
	public String getContext(final int number) {
		String context = null;
		XmlContainer cur = (XmlContainer) wordParent;
		if(number == 0){
			logger.warn("Context does not take the value '0' as input");
		}else if(number > 0){
			LinkedList<XmlContainer> list = new LinkedList<XmlContainer>();
			logger.trace("In "+getClass()+" do a list of context");
			if(cur == null)logger.trace("Element has no parent");
			while(cur != null){
				logger.trace("Parent: '"+cur.nameXml+"'");
				list.addFirst((XmlContainer) cur);
				cur = (XmlContainer) cur.wordParent;
			}
			if(number <= list.size()){
				context =  list.get(number - 1).nameXml;
			}

		}else{
			int index = number +1;
			while(index < 0 && cur != null){
				cur = (XmlContainer) cur.wordParent;
				index++;
			}
			if(cur != null){
				context = cur.nameXml; 
			}
		}
		if(context == null){
			logger.debug("In '"+getNameXml()+"' get context "+number+" returns null");
		}else{
			logger.debug("In '"+getNameXml()+"' get context "+number+" returns "+context);
		}
		return context;
	}

	public List<String> getContext(){
		XmlContainer cur = wordParent;
		LinkedList<String> list = new LinkedList<String>();
		logger.trace("In "+getClass()+" do a list of context");
		if(cur == null)logger.trace("Element has no parent");
		while(cur != null){
			logger.trace("Parent: "+cur.nameXml);
			list.addFirst(cur.getNameXml());
			cur = cur.wordParent;
		}
		return list;
	}

	/**
	 * Returns the parent named parentName.
	 * 
	 * Returns the first object where parentName = nameXml
	 * @param parentName
	 * @return
	 */
	public XmlContainer getParent(String parentName){
		boolean found = false;
		XmlContainer cur = wordParent;
		while(!found && cur != null){
			if(cur.nameXml.equals(parentName)){
				found = true;
			}else{
				cur = cur.wordParent;
			}
		}
		return cur;
	}

	/**
	 * Save in the children map a value
	 * @param nameXml name of the children
	 * @param value value to save
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean put(String nameXml, Object value){

		if(nameXml == null || value == null){
			logger.error("In '"+nameXml+"' the method 'put' does not take null values");
			return false;
		}

		XmlProperty prop = null;
		if( (  prop = childrenProperties.get(nameXml)) == null){
			logger.error(nameXml+" is not a child of "+getNameXml());
			return false;
		}
		if(prop.getMax() == 1){
			children.put(nameXml,value);
		}else{
			//Save a list
			List<?> l = getL(nameXml);
			if(l == null){
				putNewList(nameXml, value);
				l =  getL(nameXml);
			}
			if(value instanceof Boolean){
				((List<Boolean>) l).add((Boolean)value);
			}else if(value instanceof Integer){
				((List<Integer>) l).add((Integer)value);
			}else if(value instanceof Double){
				((List<Double>) l).add((Double)value);
			}else if(value instanceof String){
				((List<String>) l).add((String)value);
			}else if(value instanceof XmlWord){
				((List<XmlWord>) l).add((XmlWord)value);
			}else{
				((List<Object>) l).add((Object)value);
			}
		}
		return true;
	}

	private boolean putNewList(String nameXml, Object value){
		boolean ok = true;

		if(nameXml == null || value == null){
			logger.error("In '"+nameXml+"' the method 'put' does not take null values");
			return false;
		}
		List<?> l = getL(nameXml);
		if(l == null){
			if(value instanceof Boolean){
				l = new LinkedList<Boolean>();
			}else if(value instanceof Integer){
				l = new LinkedList<Integer>();
			}else if(value instanceof Double){
				l = new LinkedList<Double>();
			}else if(value instanceof String){
				l = new LinkedList<String>();
			}else if(value instanceof XmlWord){
				l = new LinkedList<XmlWord>();
			}else{
				//logger.warn("In "+getNameXml()+" creates a object in the children Map, it shouldn't happened");
				l = new LinkedList<Object>();
			}
			children.put(nameXml, l);
		}else{
			ok = false;
		}

		return ok;
	}

	/**
	 * Delete a value in the children map
	 * @param nameXml
	 * @return
	 */
	public boolean delete(String nameXml){
		return children.remove(nameXml) != null;
	}


	/**
	 * Get an object from the children map
	 * @param nameXml
	 * @return
	 */
	public Object get(String nameXml){
		Object o = null;
		if( childrenProperties.get(nameXml) == null){
			logger.error(nameXml+" is not a child of "+getNameXml());
		}else{
			o = children.get(nameXml);
			if(o == null){
				logger.debug("In "+getNameXml()+" the object "+nameXml+" does not exist");
			}
		}
		return o;
	}

	/**
	 * Get a boolean from the children map
	 * @param leafName
	 * @return
	 */
	public Boolean getB(String leafName){

		Boolean res = null;
		Object o = get(leafName);
		if(o != null)
			if( o instanceof Boolean){
				res = (Boolean) o;
			}else{
				logger.warn("In '"+getNameXml()+"' the leaf '"+
						leafName+"' is not a Boolean but a "+ 
							o.getClass().getCanonicalName());
			}
		return res;
	}

	/**
	 * Get an integer from the children map
	 * @param leafName
	 * @return
	 */
	public Integer getI(String leafName){

		Integer res = null;
		Object o = get(leafName);
		if(o != null)
			if( o instanceof Integer){
				res = (Integer) o;
			}else{
				logger.warn("In "+getNameXml()+" the leaf "+
						leafName+" is not a Integer but a "+ 
							o.getClass().getCanonicalName());
			}
		return res;
	}

	/**
	 * Get a double from the children map
	 * @param leafName
	 * @return
	 */
	public Double getD(String leafName){

		Double res = null;
		Object o = get(leafName);
		if(o != null)
			if( o instanceof Double){
				res = (Double) o;
			}else{
				logger.warn("In "+getNameXml()+" the leaf "+
						leafName+" is not a Double but a "+ 
							o.getClass().getCanonicalName());
			}
		return res;
	}

	/**
	 * Get a string from the children map
	 * @param leafName
	 * @return
	 */
	public String getS(String leafName){

		String res = null;
		Object o = get(leafName);
		if(o != null)
			if( o instanceof String){
				res = (String) o;
			}else{
				logger.warn("In "+getNameXml()+" the leaf "+
						leafName+" is not a String but a "+ 
							o.getClass().getCanonicalName());
			}
		return res;
	}

	/**
	 * Get a xmlWord from the children map
	 * @param nameXml
	 * @return
	 */
	public XmlWord getW(String nameXml){

		XmlWord res = null;
		Object o = get(nameXml);
		if(o != null)
			if( o instanceof XmlWord){
				res = (XmlWord) o;
			}else{
				logger.warn("In "+getNameXml()+" the leaf "+
						nameXml+" is not a XmlWord but a "+ 
							o.getClass().getCanonicalName());
			}
		return res;
	}

	/**
	 * Get a List from the children map
	 * @param nameXml
	 * @return
	 */
	public List<?> getL(String nameXml){

		List<?> res = null;
		Object o = get(nameXml);
		if(o != null)
			if( o instanceof List<?>){
				res = (List<?>) o;
			}else{
				logger.warn("In "+getNameXml()+" the leaf "+nameXml+" is not a List");
			}
		return res;
	}

	/**
	 * Get a list of boolean from the children map
	 * @param leafName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Boolean> getLB(String leafName){

		boolean ok = true;
		List<Boolean> res = null;
		List<?> l = getL(leafName);
		if(l != null){
			if(l.isEmpty()){
				logger.debug("list "+leafName+" empty cannot check if it is a boolean list");
			}else{
				if(!(l.get(0) instanceof Boolean)){
					logger.warn("In "+getNameXml()+" the leaf "+
							leafName+" is not a List<Boolean> but a list of "+
							l.get(0).getClass().getCanonicalName());
					ok = false;
				}
			}
			if(ok){
				res = (List<Boolean>) l;
			}
		}

		return res;
	}

	/**
	 * Get a list of integer from the children map
	 * @param leafName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getLI(String leafName){

		boolean ok = true;
		List<Integer> res = null;
		List<?> l = getL(leafName);
		if(l != null){
			if(l.isEmpty()){
				logger.debug("list "+leafName+" empty cannot check if it is a integer list");
			}else{
				if(!(l.get(0) instanceof Integer)){
					logger.warn("In "+getNameXml()+" the leaf "+
							leafName+" is not a List<Integer> but a list of "+
							l.get(0).getClass().getCanonicalName());
					ok = false;
				}
			}
			if(ok){
				res = (List<Integer>) l;
			}
		}

		return res;
	}

	/**
	 * Get a list of double from the children map
	 * @param leafName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Double> getLD(String leafName){

		boolean ok = true;
		List<Double> res = null;
		List<?> l = getL(leafName);
		if(l != null){
			if(l.isEmpty()){
				logger.debug("list "+leafName+" empty cannot check if it is a double list");
			}else{
				if(!(l.get(0) instanceof Double)){
					logger.warn("In "+getNameXml()+" the leaf "+
							leafName+" is not a List<Double> but a list of "+
							l.get(0).getClass().getCanonicalName());
					ok = false;
				}
			}
			if(ok){
				res = (List<Double>) l;
			}
		}

		return res;
	}

	/**
	 * Get a list of string from the children map
	 * @param leafName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getLS(String leafName){

		boolean ok = true;
		List<String> res = null;
		List<?> l = getL(leafName);
		if(l != null){
			if(l.isEmpty()){
				logger.debug("list "+leafName+" empty cannot check if it is a string list");
			}else{
				if(!(l.get(0) instanceof String)){
					logger.warn("In "+getNameXml()+" the leaf "+leafName+
							" is not a List<String> but a list of "+
							l.get(0).getClass().getCanonicalName());
					ok = false;
				}
			}
			if(ok){
				res = (List<String>) l;
			}
		}

		return res;
	}

	/**
	 * Get a list of xml word from the children map
	 * @param leafName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<XmlWord> getLW(String leafName){

		boolean ok = true;
		List<XmlWord> res = null;
		List<?> l = getL(leafName);
		if(l != null){
			if(l.isEmpty()){
				logger.debug("list "+leafName+" empty cannot check if it is a boolean list");
			}else{
				if(!(l.get(0) instanceof XmlWord)){
					logger.warn("In "+getNameXml()+" the leaf "+
							leafName+" is not a List<XmlWord> but a list of "+
							l.get(0).getClass().getCanonicalName());
					ok = false;
				}
			}
			if(ok){
				res = (List<XmlWord>) l;
			}
		}

		return res;
	}

	/**
	 * 
	 * @param count
	 *            The number of spaces
	 * @return That number of spaces.
	 */
	protected String getSpaces(final int count) {
		StringBuilder spaces = new StringBuilder();
		for (int i = 0; i < count; i++) {
			spaces.append(" ");
		}
		return spaces.toString();
	}

	/**
	 * @param nameXml the nameXml to set
	 */
	protected void setNameXml(String nameXml) {
		this.nameXml = nameXml.toLowerCase();
	}

	/**
	 * @return the nameXml
	 */
	public String getNameXml() {
		return nameXml;
	}

	/**
	 * @return the childrenProperties
	 */
	public Map<String, XmlProperty> getChildrenProperties() {
		return childrenProperties;
	}

	/**
	 * @return the wordParent
	 */
	protected XmlWord getWordParent() {
		return wordParent;
	}


	/**
	 * @param wordParent the wordParent to set
	 */
	protected void setWordParent(XmlWord wordParent) {
		this.wordParent = wordParent;
	}

	/**
	 * @return the children
	 */
	public Map<String, Object> getChildren() {
		return children;
	}

}
