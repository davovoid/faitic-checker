/*
 * 	Faicheck - A NON OFFICIAL application to manage the Faitic Platform
 * 	Copyright (C) 2016, 2017 David Ricardo Araújo Piñeiro
 * 	
 * 	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


package daraujo.faiticchecker;

import java.util.Locale;
import java.util.ResourceBundle;

public class TextData {

	private ResourceBundle objectLocale;
	private final String localePath="daraujo.faiticchecker.locale";
	
	public TextData(Locale locale){
		
		objectLocale=ResourceBundle.getBundle(localePath,locale);
		
		
	}
	
	public String getKey(String key, String... args){
		
		String output=objectLocale.getString(key);
		
		int i=0;
		for(String arg : args){
			
			output=output.replace("%" + i, arg);
			i++;
			
		}
		
		return output;

	}
	
}
