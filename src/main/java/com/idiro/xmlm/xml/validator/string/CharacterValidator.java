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

package com.idiro.xmlm.xml.validator.string;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.idiro.xmlm.xml.LeafValidator;
import com.idiro.xmlm.xml.XmlLeaf;

/**
 * Validate a character within a string.
 * 
 * Warning: The character validator change the
 * string as well as validate it. The value of
 * the leaf is a string of length one if the leaf
 * has been validate (the method validate returns true).
 * 
 * 
 * @author etienne
 *
 */
public class CharacterValidator extends LeafValidator<String>{

	Character[] chToCheck = {'\'','"'};
	List<Character>  charAccepted = new LinkedList<Character>();
	
	public CharacterValidator(){}
	
	public CharacterValidator(char[] chAccepted){
		for(int i= 0; i < chAccepted.length;++i)
			charAccepted.add(chAccepted[i]);
		
	}
	
	@Override
	public boolean validate(XmlLeaf<String> leaf) {
		boolean ok = true;
		
		Character c = getCharFromOctal(removeQuotes(leaf.getWordValue()));
		if(c == null){
			ok = false;
			logger.error("Leaf '"+leaf.getWordValue()+"' is not a valid character");
		}else{
			leaf.setValue(
					new String(new char[]{c})
					);
		}
		
		
		if(ok && !charAccepted.isEmpty()){
			Iterator<Character> it = charAccepted.iterator();
			boolean found = false;
			while(it.hasNext() && !found){
				found = it.next() == leaf.getWordValue().charAt(0);
			}
			if(!found){
				logger.error("The character '"+c+"' is not acceptable");
			}
			ok = found;
			
		}
		
		return ok;
	}
	
	public String removeQuotes(String value){
		String ans = value;
		if(value.length() > 2 &&
			value.charAt(0) == value.charAt(value.length()-1)){
			int i = 0;
			boolean found = false;
			
			while(i < chToCheck.length && !found){
				if(value.charAt(0) == chToCheck[i]){
					found = true;
					ans = value.substring(1,value.length()-1);
				}
				++i;
			}
		}
		return ans;
	}
	
	public Character getCharFromOctal(String charValue){
		Character ans = null;
		if(charValue.charAt(0) == '#' &&
				charValue.length() > 1){
			try{
				int intAns = Integer.parseInt(charValue.substring(1));
				if(intAns < 0 || intAns > 127){
					logger.error("'"+charValue.substring(1)+"' is not between 0 and 127");
				}else{
					ans = (char) intAns;
				}
			}catch(Exception e){
				logger.error("Cannot not convert the value to an integer");
			}
			
		}else if(charValue.length() == 1){
			ans = charValue.charAt(0);
		}
		return (char)ans;
	}

	@Override
	public String description() {
		if(charAccepted.isEmpty())
			return "check if it is a character, you may parse the character as 'c', \"c\", #124 or \"#124\"";
		
		List<Integer> transInt = new ArrayList<Integer>(charAccepted.size());
		Iterator<Character> it = charAccepted.iterator();
		while(it.hasNext()){
			transInt.add((int)it.next());
		}
		return "check if it is a character within"+transInt+", you may parse the character as 'c', \"c\", #124 or \"#124\"";
	}

}
