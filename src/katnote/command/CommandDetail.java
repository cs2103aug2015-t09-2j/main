package katnote.command;

import java.util.Date;
import java.util.HashMap;

public class CommandDetail {
	private CommandType commandType;
	private HashMap<String, Object> commandData;
	
	/*
	 * 
	 */
	public CommandDetail(CommandType commandType){
		this.commandType = commandType;
		commandData = new HashMap<String, Object>();
	}
	
	/*
	 * Return type of command
	 */
	public CommandType getCommandType(){
		return commandType;
	}
	
	/*
	 * Return value of property with given key
	 * @param key 
	 */
	public Object getProperty(String key){
		return commandData.get(key);
	}
	
	/*
     * Return value of string property with given key
     * @param key 
     */
    public String getString(String key){
        return (String) commandData.get(key);
    }
    
    /*
     * Return value of Date property with given key
     * @param key 
     */
    public Date getDate(String key){
        return (Date) commandData.get(key);
    }
	
	/*
	 * 
	 */
	public void setProperty(String key, Object value){
		commandData.put(key, value);
	}

}
