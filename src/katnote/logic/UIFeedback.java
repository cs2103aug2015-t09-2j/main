//@@author A0131003J

/* This program contains the service class UIFeedback and its methods */

package katnote.logic;

public class UIFeedback {
    private boolean isError_; // true if UIFeedback contains an error reponseMessage
    private boolean isExit_;
    private boolean isSearch_;
    private ViewState viewState_;
    private String responseMessage_;
    
    /*-- Constructors --*/
    
    public UIFeedback() { 
        viewState_ = new ViewState();
        isExit_ = false;
        isError_ = false;
        isSearch_ = false;
    }
    
    public UIFeedback(boolean isError, ViewState vs, String responseMessage) {
        isError_ = isError;
        viewState_ = vs;
        responseMessage_ = responseMessage;
        isExit_ = false;
        isSearch_ = false;
    }
     
    /*-- Public Methods --*/
    
    public void setError(boolean isError) {
        isError_ = isError;
    }
    
    public void setExit(boolean isExit) {
        isExit_ = isExit;
    }
    
    public void setSearch(boolean isSearch) {
        isSearch_ = isSearch;
    }
    
    public void setViewState(ViewState vs) {
        viewState_ = vs;
    }
    
    public void setResponse(String responseMessage) {
        responseMessage_ = responseMessage;
    }
     
    public boolean isAnError() {
        return isError_;
    }
    
    public boolean isAnExit() {
        return isExit_;
    }
    
    public boolean isASearch() {
        return isSearch_;
    }
    
    /**
     * 
     * @return Returns the ViewState attribute of UIFeedback object
     */
    public ViewState getViewState() {
        return viewState_;
    }
    
    /**
     * 
     * @return Returns the String containing the response message of the UIFeedback object.
     */
    public String getMessage() {
        return responseMessage_;
    }
       
}