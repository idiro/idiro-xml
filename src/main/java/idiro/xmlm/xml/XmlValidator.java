package idiro.xmlm.xml;

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
