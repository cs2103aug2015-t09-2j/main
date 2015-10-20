/* This program contains the service class UIFeedback and its methods */

package katnote;

import java.util.ArrayList;

public class UIFeedback {
    private boolean isError_; // true if UIFeedback contains an error reponseMessage
    private ViewState viewState_;
    private String responseMessage_;
    
    /*-- Constructors --*/
    
    public UIFeedback() { 
        viewState_ = new ViewState();
    }
    
    public UIFeedback(boolean isError, ViewState vs, String responseMessage) {
        isError_ = isError;
        viewState_ = vs;
        responseMessage_ = responseMessage;
    }
     
    /*-- Public Methods --*/
    
    public void setError(boolean isError) {
        isError_ = isError;
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