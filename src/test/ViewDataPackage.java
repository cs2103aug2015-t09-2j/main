package test;

import java.util.ArrayList;

public class ViewDataPackage {

    public static class TaskGroupPackage {
        private String groupHeader;
        private String[] taskDescription;
        private String[] dateString;
        private String[] indexString;
        
        public TaskGroupPackage(String groupHeader, String[] taskDescription, String[] dateString, String[] indexString){
            this.groupHeader = groupHeader;
            this.taskDescription = taskDescription;
            this.dateString = dateString;
            this.indexString = indexString;
        }
        
        public String getGroupHeader(){
            return groupHeader;
        }
        public String[] getTaskDesciptions(){
            return taskDescription;
        }
        public String[] getDateString(){
            return dateString;
        }
        public String[] getIndexString(){
            return indexString;
        }        
    }   
    
    
    String response = null;
    TaskGroupPackage[] viewList = null;

    public ViewDataPackage(ArrayList<TaskGroupPackage> viewList, String response) {
        this.response = response;
        this.viewList = new TaskGroupPackage[viewList.size()];
        viewList.toArray(this.viewList);
    }

    public String getResponse() {
        return response;
    }

    public TaskGroupPackage[] getViewList() {
        return viewList;
    }
}
