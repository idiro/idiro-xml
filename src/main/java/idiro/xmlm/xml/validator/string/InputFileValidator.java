package idiro.xmlm.xml.validator.string;

import idiro.check.FileChecker;
import idiro.xmlm.xml.LeafValidator;
import idiro.xmlm.xml.XmlLeaf;

/**
 * Check if the file exists and is readable
 * @author etienne
 *
 */
public class InputFileValidator extends LeafValidator<String>{
	
	@Override
	public String description() {
		return "file exists and is readable";
	}

	@Override
	public boolean validate(XmlLeaf<String> leaf) {
		boolean ok = false;
		FileChecker fCh = new FileChecker(leaf.getWordValue());
		if(fCh.isInitialized()){
			if(fCh.exists()){
				if(fCh.canRead()){
					ok = true;
				}else{
					logger.warn("The file "+fCh.getFilename()+ " is not readable");
				}
			}else{
				logger.warn("The file "+fCh.getFilename()+ " does not exist");
			}
		}else{
			logger.warn(leaf.getWordValue()+" does not represent a file path");
		}
		return ok;
	}

}
