package katnote.parser;

import java.util.Arrays;
import java.util.HashSet;

public class CommandKeywords {
	// keywords for command types
	public static final String KW_ADD = "add";
	public static final String KW_EDIT = "edit";
	public static final String KW_DELETE = "delete";
	public static final String KW_FIND = "find";
	public static final String KW_VIEW = "view";
	
	// keywords for time properties
	public static final String KW_BY = "by";
	public static final String KW_FROM = "from";
	public static final String KW_TO = "to";
	public static final String KW_ON = "on";
	public static final String KW_REPEAT = "repeat";
	public static final String KW_UNTIL = "until";
	
	// keywords for view, add properties
	public static final String KW_SET = "set";
	public static final String KW_MARK = "mark";
	
	private static final String[] ALL_KEYWORDS_LIST = new String[]{
			KW_ADD, KW_EDIT, KW_DELETE, KW_FIND, KW_VIEW,
			KW_BY, KW_FROM, KW_TO, KW_ON, KW_REPEAT, KW_UNTIL,
			KW_MARK, KW_SET,
	};
	
	private static final HashSet<String> ALL_KEYWORDS_SET = new HashSet<String>(Arrays.asList(ALL_KEYWORDS_LIST));
	
	/*
	 * Check a token whether it is a keyword or not
	 */
	public static final boolean isKeyword(String token){
		return ALL_KEYWORDS_SET.contains(token.toLowerCase());
	}
}
