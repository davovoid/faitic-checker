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

import java.awt.Color;

public class ScheduleEvent {

	private String iEventName;
	private int iMinuteStart, iMinuteEnd, iDay;
	private Color iColor=Color.WHITE;
	private String iAssocSubject=null;
	
	public final int MONDAY=0, TUESDAY=1, WEDNESDAY=2,
			THURSDAY=3, FRIDAY=4, SATURDAY=5, SUNDAY=6;
	
	public ScheduleEvent(String eventName, int minuteStart, int minuteEnd, int day, Color color, String assocSubject) {

		modify(eventName,minuteStart,minuteEnd,day,color,assocSubject);
		
	}
	
	public ScheduleEvent(String eventName, int minuteStart, int minuteEnd, int day) {

		modify(eventName,minuteStart,minuteEnd,day,getColor(),getAssocSubject());
		
	}
	
	public void modify(String eventName, int minuteStart, int minuteEnd, int day, Color color, String assocSubject){
		
		iEventName=eventName;
		iMinuteStart=minuteStart; iMinuteEnd=minuteEnd; iDay=day;
		iColor=color;
		iAssocSubject=assocSubject;
		
	}
	
	public void modify(String eventName, int minuteStart, int minuteEnd, int day){
		
		modify(eventName,minuteStart,minuteEnd,day,getColor(),getAssocSubject());
		
	}
	
	
	public int getHour(int minutes){
		
		return (int) Math.floor((float)minutes/60.0);
		
	}
	

	public int getMinute(int minutes){
		
		return minutes % 60;
		
	}
	
	public int getMinuteStart(){
		
		return iMinuteStart;
		
	}
	
	public int getMinuteEnd(){
		
		return iMinuteEnd;
		
	}
	
	public int getDay(){
		
		return iDay;
		
	}
	
	public String getEventName(){
		
		return iEventName;
		
	}
	
	public Color getColor(){
		
		return iColor;
		
	}
	
	public String getAssocSubject(){
		
		return iAssocSubject;
		
	}
	
	

}
