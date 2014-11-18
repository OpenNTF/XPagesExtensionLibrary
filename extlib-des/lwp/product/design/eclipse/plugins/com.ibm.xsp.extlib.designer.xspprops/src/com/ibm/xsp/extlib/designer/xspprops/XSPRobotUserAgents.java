/*
 * © Copyright IBM Corp. 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */


package com.ibm.xsp.extlib.designer.xspprops;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Gary Marjoram
 *
 */
public class XSPRobotUserAgents {
    public static final String ROBOT_UA_EMPTY = "<empty>"; // $NON-NLS-1$
    public static final String ROBOT_UA_AUTO  = "<auto>"; // $NON-NLS-1$
    
    private boolean auto;
    private boolean empty;
    private ArrayList<String> list = new ArrayList<String>();
    
    public XSPRobotUserAgents(String input) {
        // Create the user defined agent list
        set(input);
    }
    
    // Given a comma separated list of agents parse them
    public void set(String input) {
        reset();
        for(String agent:input.split(",")) {
            add(agent);
        }        
    }    
    
    // Get the comma separated list of agents
    public String get() {
        String userBot;
        boolean addComma = false;
        String agents = "";
        
        // Add the specials
        if(empty) {
            agents += ROBOT_UA_EMPTY;
            addComma = true;
        }
        
        if(auto) {
            if(addComma) {
                agents += ",";
            }
            agents += ROBOT_UA_AUTO;
            addComma = true;
        }
        
        // Add the userlist
        Iterator<String> bot = list.iterator();
        while (bot.hasNext()) {        
            userBot = bot.next().toString();
            if(userBot.length() > 0) {
                if(addComma) {
                    agents += ",";
                }
                agents += userBot;
                addComma = true;                
            }
        }
        
        return agents;
    }
    
    // Get the array representation if the user defined agent list
    public String[] getUserDefinedArray() {        
        return list.toArray(new String[list.size()]);
    }
    
    // Get the user defined agent list size
    public int getUserDefinedCount() {
        return list.size();
    }
        
    public boolean getAuto() {
        return auto;
    }
    
    public boolean getEmpty() {
        return empty;
    }
    
    public boolean isDefault() {
        return(!(auto || empty || (getUserDefinedCount() > 0)));
    }
    
    // Add an agent
    public void add(String input) {
        input = input.trim();
        
        if(input.length() > 0) {
            // Check for specials
            if(input.equalsIgnoreCase(ROBOT_UA_AUTO)) {
                auto = true;
                empty = false;
            } else if(input.equalsIgnoreCase(ROBOT_UA_EMPTY)) {
                // Empty must appear on it's own
                list.clear();
                auto = false;
                empty = true;
            } else {
                // User Item
                list.add(input);
                empty = false;
            }
        }
    }
    
    // Remove an agent at a particular pos
    public void remove(int index) {
        if(index < list.size()) {
            list.remove(index);
            if((list.size() == 0) && (!auto)) {
                empty = true;
            }
        }
    }

    // Remove an agent
    public void remove(String input) {
        input = input.trim();

        if(input.equalsIgnoreCase(ROBOT_UA_AUTO)) {
            auto = false;
            if(list.size() == 0) {
                empty = true;
            }
        } else if(input.equalsIgnoreCase(ROBOT_UA_EMPTY)) {
            empty = false;
        } else {            
            // Search the userlist
            Iterator<String> bot = list.iterator();
            int i=0;
            while (bot.hasNext()) {        
                if(input.equalsIgnoreCase(bot.next().toString())) {
                    // Found it remove
                    remove(i);
                    return;
                }
                i++;
            }            
        }
    }
    
    // Set the agent at a particular pos
    public void set(int index, String input) {
        input = input.trim();

        if(index < list.size()) {
            if(input.equalsIgnoreCase(ROBOT_UA_AUTO)) {
                auto = true;
                list.remove(index);
            } else if(input.equalsIgnoreCase(ROBOT_UA_EMPTY)) {
                // Empty must appear on it's own
                list.clear();
                auto = false;
                empty = true;
            } else {            
                if(input.length() > 0) {
                    list.set(index, input);
                } else {
                    // Empty String - remove
                    remove(index);
                }
            }
        }
    }
        
    // Default the object
    public void reset() {
        list.clear();
        auto = false;
        empty = false;        
    }   
}