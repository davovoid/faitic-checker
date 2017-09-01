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
import java.io.File;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Schedule {

	protected static ArrayList<ScheduleEvent> eventList=new ArrayList<ScheduleEvent>();
	
	private static String iUsername;
	
	public Schedule(String username) {
		
		// Variables
		
		iUsername=username;
		eventList.clear();
		
	}
	

	public static boolean scheduleExists(){
		
		return new File(ClassicRoutines.cpath(ClassicRoutines.getUserDataPath(true) + "/schedule-" + iUsername + ".json")).exists();
		
	}
	
	
	public static void readEvents(int scheduleIndex){
		
		eventList.clear(); // Clears events before everything
		
		JSONParser jsonParser=new JSONParser();	// Initializes the JSONParser
		
		String scheduleFile=ClassicRoutines.cpath(ClassicRoutines.getUserDataPath(true) + "/schedule-" + iUsername + ".json");
		
		if(new File(scheduleFile).exists()){ // if the file exists
			
			try {
				
				// Read the schedule json
				JSONObject schedulejson=(JSONObject) jsonParser.parse(ClassicRoutines.readFile(scheduleFile));
				
				if(!schedulejson.containsKey("schedules")) return;
				
				// Checks all schedules
				JSONArray schedules=(JSONArray) schedulejson.get("schedules");
				
				if(scheduleIndex>=schedules.size() || scheduleIndex<0) return;
				
				JSONObject scheduleentry=(JSONObject) schedules.get(scheduleIndex); // Get the schedule at pos scheduleIndex
				
				if(scheduleentry.containsKey("events")){
					
					JSONArray events=(JSONArray) scheduleentry.get("events");
					
					for(int i=0; i<events.size(); i++){
						
						try{

							// For each event

							JSONObject eventjson=(JSONObject) events.get(i);

							String eventname="untitled";
							int minutestart=0, minuteend=0, day=0;
							Color color=Color.WHITE;
							String assocsubject=null;

							if(eventjson.containsKey("eventname")) 		eventname 		= (String) 	eventjson.get("eventname");
							if(eventjson.containsKey("assocsubject")) 	assocsubject 	= (String) 	eventjson.get("assocsubject");
							if(eventjson.containsKey("minutestart"))	minutestart 	= (int)(long) 	eventjson.get("minutestart");
							if(eventjson.containsKey("minuteend"))		minuteend 		= (int)(long) 	eventjson.get("minuteend");
							if(eventjson.containsKey("day"))			day 			= (int)(long) 	eventjson.get("day");

							if(eventjson.containsKey("colorr") &&
							   eventjson.containsKey("colorg") &&
							   eventjson.containsKey("colorb") &&
							   eventjson.containsKey("colora"))			color			= new Color((int)(long) eventjson.get("colorr"),
																									(int)(long) eventjson.get("colorg"),
																									(int)(long) eventjson.get("colorb"),
																									(int)(long) eventjson.get("colora"));

							ScheduleEvent event=new ScheduleEvent(eventname, minutestart, minuteend, day, color, assocsubject);

							eventList.add(event);

						} catch(Exception e2){
							
							e2.printStackTrace();
							
						}
						
					}
					
				}
				
				
				
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
		}
			
		
		
		
	}

	public static String[] getScheduleNames(){

		JSONParser jsonParser=new JSONParser();	// Initializes the JSONParser

		String scheduleFile=ClassicRoutines.cpath(ClassicRoutines.getUserDataPath(true) + "/schedule-" + iUsername + ".json");

		if(new File(scheduleFile).exists()){

			try {

				// Read the schedule json
				JSONObject schedulejson=(JSONObject) jsonParser.parse(ClassicRoutines.readFile(scheduleFile));

				if(!schedulejson.containsKey("schedules")) return new String[0];

				// Checks all schedules
				JSONArray schedules=(JSONArray) schedulejson.get("schedules");

				String[] schedulenames=new String[schedules.size()]; // To be returned when finished

				for(int j=0; j<schedules.size(); j++){

					JSONObject scheduleentry=(JSONObject) schedules.get(j);

					if(scheduleentry.containsKey("schedulename")){

						String scheduleentryname=(String) scheduleentry.get("schedulename");

						schedulenames[j]=scheduleentryname; // New entry

					} else{
						
						schedulenames[j]="Untitled"; // Left empty

					}

				}
				
				return schedulenames;

			} catch (Exception e) {

				e.printStackTrace();
				
			}

		}

		return new String[0];
		

	}

	public static void saveSchedule(String schedulename, int schedulepos, boolean newEntry){

		// STEP 1: PREVIOUS SCHEDULES
		
		JSONArray schedules=null;

		JSONParser jsonParser=new JSONParser();	// Initializes the JSONParser

		String scheduleFile=ClassicRoutines.cpath(ClassicRoutines.getUserDataPath(true) + "/schedule-" + iUsername + ".json");

		if(new File(scheduleFile).exists()){ 

			try {

				// Read the schedule json
				JSONObject schedulejson=(JSONObject) jsonParser.parse(ClassicRoutines.readFile(scheduleFile));

				if(schedulejson.containsKey("schedules")){
					
					// Reads all schedules
					schedules=(JSONArray) schedulejson.get("schedules");

				}


			} catch (Exception e) {

				e.printStackTrace();
				
			}

		}
		
		// Are there schedules?
		if(schedules==null){
			
			// No, let's clean them
		
			schedules=new JSONArray();
			
		}
		
		
		// STEP 2: GENERATE SCHEDULE
		
		JSONObject newschedule=new JSONObject();
		
		newschedule.put("schedulename", schedulename); // Name
		
		JSONArray events=new JSONArray();
		
		for(ScheduleEvent event : eventList){ // For each event
			
			JSONObject eventjson=new JSONObject();

			String eventname=event.getEventName();
			int minutestart=event.getMinuteStart(), minuteend=event.getMinuteEnd(), day=event.getDay();
			Color color=event.getColor();
			String assocsubject=event.getAssocSubject();

			eventjson.put("eventname", eventname);
			eventjson.put("minutestart", minutestart);
			eventjson.put("minuteend", minuteend);
			eventjson.put("day", day);
			eventjson.put("colorr", color.getRed());
			eventjson.put("colorg", color.getGreen());
			eventjson.put("colorb", color.getBlue());
			eventjson.put("colora", color.getAlpha());
			
			if(assocsubject!=null) eventjson.put("assocsubject", assocsubject);
			
			events.add(eventjson); // Add event to list
			
		}
		
		newschedule.put("events", events); // Add events to the new entry
		
		
		// STEP 3: MERGE THE NEW ENTRY
		
		JSONArray newschedules=new JSONArray();
		
		for(int i=0; i<schedulepos; i++){ // Before
			
			if(i<schedules.size() && i>=0)
				newschedules.add(schedules.get(i));
			
		}
		
		// Now add the new one
		
		newschedules.add(newschedule);
		
		for(int i=(newEntry ? schedulepos : schedulepos+1); i<schedules.size(); i++){ // After

			if(i>=0) newschedules.add(schedules.get(i));
			
		}
		
		// STEP 4: SAVE IT
		
		JSONObject schedulejsonout=new JSONObject();
		schedulejsonout.put("schedules", newschedules);
		
		ClassicRoutines.writeFile(scheduleFile,schedulejsonout.toJSONString());
		
		
	}
	
	

	public static void removeSchedule(int schedulepos){

		// STEP 1: PREVIOUS SCHEDULES
		
		JSONArray schedules=null;

		JSONParser jsonParser=new JSONParser();	// Initializes the JSONParser

		String scheduleFile=ClassicRoutines.cpath(ClassicRoutines.getUserDataPath(true) + "/schedule-" + iUsername + ".json");

		if(new File(scheduleFile).exists()){ 

			try {

				// Read the schedule json
				JSONObject schedulejson=(JSONObject) jsonParser.parse(ClassicRoutines.readFile(scheduleFile));

				if(schedulejson.containsKey("schedules")){
					
					// Reads all schedules
					schedules=(JSONArray) schedulejson.get("schedules");

				}


			} catch (Exception e) {

				e.printStackTrace();
				
			}

		}
		
		// Are there schedules?
		if(schedules==null){
			
			// No, let's clean them
		
			schedules=new JSONArray();
			
		}
		
		// Skip step 2. No entry to generate
		
		// STEP 3: MERGE THE NEW ENTRY
		
		JSONArray newschedules=new JSONArray();
		
		for(int i=0; i<schedulepos; i++){ // Before
			
			if(i<schedules.size() && i>=0)
				newschedules.add(schedules.get(i));
			
		}
		
		for(int i=schedulepos+1; i<schedules.size(); i++){ // After. Discarding schedulepos

			if(i>=0) newschedules.add(schedules.get(i));
			
		}
		
		// STEP 4: SAVE IT
		
		JSONObject schedulejsonout=new JSONObject();
		schedulejsonout.put("schedules", newschedules);
		
		ClassicRoutines.writeFile(scheduleFile,schedulejsonout.toJSONString());
		
		
	}
	

}
