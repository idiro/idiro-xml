package idiro.xmlm.xml;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

/**
 * Way to add functionalities to a XmlWord
 * in function of the context.
 * 
 * Adds an extension to a XmlWord.
 * For a user perspective it is invisible.
 * However to separate complex case with a 
 * strong context it is handy
 * 
 * It is not really used because it is preferable
 * to extend a word.
 * 
 * @author etienne
 *
 */
@Deprecated
public abstract class XmlContextExt extends XmlContainer{

	protected Logger logger = Logger.getLogger(getClass());

	/**
	 * The xmlword extended
	 */
	private XmlWord word;

	private String extensionName;

	
	public XmlContextExt(){
		super();
		setExtensionName(getNameXml());
	}

	
	/**
	 * Parse a nodeList with the children of the extension
	 * @param nodeList
	 * @return
	 */
	public boolean parse(List<Node> nodeList){
		boolean ok = true;

		Map<String,Integer> nodeNameNumber = new LinkedHashMap<String,Integer>();
		Iterator<Node> itNode = nodeList.iterator();
		//Create a XMLWord for each name found
		while( itNode.hasNext() && ok){
			ok = parseChild(itNode.next(), nodeNameNumber);
		}

		//Check if everything is all right
		if(ok){
			ok = checkParsingFinal(nodeNameNumber);
		}

		if(ok){
			ok = checkParsing();
		}
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
	protected boolean parseChild(final Node child,Map<String,Integer> nodeNameNumber){
		boolean ok = true;
		Integer count;
		String chName = child.getNodeName();
		XmlProperty childProperty = null;
		if((childProperty= getChildrenProperties().get(chName) )== null){

			if(!chName.equals("#text") &&
					!chName.equals("#comment")){
				//if it is a comments or a line jump is fine
				logger.error("The node "+chName+" does not exists");
				ok = false;
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

				if((getChildrenProperties().get(chName)) != null){
					logger.debug("Word child found: "+chName);
					if(! initAndParseWordChild(chName, child)){
						logger.error("Error parsing the node "+chName);
						ok = false;
					}

				}else{
					logger.error(chName+" is not a leaf of "+getNameXml());
					ok = false;
				}
			}else{
				logger.error("The field "+chName+" should appear less than "+childProperty.getMax()+1+" in "+getNameXml());
				ok = false;
			}
		}
		return ok;
	}


	@Override
	public String help(int space, int spacePerLevel){

		initParser();
		StringBuffer builder = new StringBuffer();

		Iterator<String> it = getChildrenProperties().keySet().iterator();
		while(it.hasNext()){
			String cur = it.next();
			builder.append(getSpaces(space)+"<!--"+
			"REPEATED BETWEEN "+getChildrenProperties().get(cur).getMin()+
			" AND "+getChildrenProperties().get(cur).getMax()+" TIMES -->\n");
			
			if( getChildrenProperties().get(cur) != null){
				try {
					XmlWord temp = initParserChild(cur);
					if(temp != null){
						builder.append(temp.help(space, spacePerLevel));
					}
					
				} catch (Exception e) {
					logger.error("Instantiation exception "+e.getCause()+" "+e.getMessage());
				}
			}else{
				logger.error("In "+getNameXml()+" child "+cur+" unknown");
			}
			
		}
		return builder.toString();
	}

	/**
	 * @return the word
	 */
	public XmlWord getWord() {
		return word;
	}

	/**
	 * @param word the word to set
	 */
	protected void setWord(XmlWord word) {
		this.word = word;
		setWordParent(word.getWordParent());
		setNameXml(word.getNameXml());
	}

	/**
	 * @return the extensionName
	 */
	public String getExtensionName() {
		return extensionName;
	}

	/**
	 * @param extensionName the extensionName to set
	 */
	protected void setExtensionName(String extensionName) {
		this.extensionName = extensionName;
	}
	
}
