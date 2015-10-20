package katnote.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import katnote.ViewState;
import katnote.task.Task;

public class TaskViewFormatter {

    private static final String GROUP_TITLE_TODAY = "Today";
    private static final String GROUP_TITLE_TOMORROW = "Tomorrow";
    private static final String GROUP_TITLE_THE_REST = "The Rest";
    private static final String GROUP_TITLE_FLOATING_TASKS = "Task to do";
    private static final int DISPLAY_LIMIT = 6;

    private ArrayList<TaskViewGroup> _viewList = new ArrayList<TaskViewGroup>();
    private ArrayList<Task> _viewOrderedTaskList = new ArrayList<Task>();
    private boolean _isGUIFormat;
    private int index = 1;

    public TaskViewFormatter(ViewState viewState, boolean isGUIFormat) {
        _isGUIFormat = isGUIFormat;
        ArrayList<Task> floatingTasks = viewState.getFloatingTasks();
        if(floatingTasks.size() != 0){
            processFloatingTask(floatingTasks);
        }        
        processNormalTasks(viewState.getNormalTasks());
    }

    /**
     * Retrieves the ordered task list that is in the same order as the task
     * displayed
     * 
     * @return the list of task in a view-dependent order
     */
    public ArrayList<Task> getOrderedTaskList() {
        return _viewOrderedTaskList;
    }

    /**
     * Retrieves the formatted list for GUI
     * 
     * @return the list of formatted UI components
     */
    public ArrayList<TaskViewGroup> getFormattedViewGroupList() {
        return _viewList;
    }

    /**
     * Retrieves the type of format whether it is GUI or Text based
     * 
     * @return true - if formatted for GUI, false - if formatted for Command
     *         Line
     */
    public boolean isGUIFormat() {
        return _isGUIFormat;
    }

    private void processNormalTasks(ArrayList<Task> normalTasks) {
        if (normalTasks == null) {
            return;
        }
        Queue<Task> normalTasksQueue = copyTasksIntoLinkedList(normalTasks);
        if (_isGUIFormat) {
            processTaskDueToday(normalTasksQueue);
            processTaskDueTomorrow(normalTasksQueue);
            processRemainingTasks(normalTasksQueue);
        }

    }

    private void processRemainingTasks(Queue<Task> normalTasksQueue) {
        ArrayList<Task> remainingList = new ArrayList<Task>(normalTasksQueue);
        if(remainingList.size() == 0){
            return;
        }
        _viewList.add(createTaskRemainingGroup(remainingList));
    }

    private void processTaskDueTomorrow(Queue<Task> normalTasksQueue) {
        ArrayList<Task> tomorrowList = extractTaskDueTomorrow(normalTasksQueue);
        if(tomorrowList.size() == 0){
            return;
        }
        TaskViewGroup taskGroupForTomorrow = createTaskTomorrowGroup(tomorrowList);
        _viewList.add(taskGroupForTomorrow);        
    }

    private void processTaskDueToday(Queue<Task> normalTasksQueue) {
        ArrayList<Task> todayList = extractTasksDueToday(normalTasksQueue);
        if(todayList.size() == 0){
            return;
        }
        TaskViewGroup taskGroupForToday = createTaskTodayGroup(todayList);
        _viewList.add(taskGroupForToday);
    }

    private TaskViewGroup createTaskTodayGroup(ArrayList<Task> todayList) {        
        TaskViewGroup todayGroup = createTaskGroupDetailed(GROUP_TITLE_TODAY, todayList);
        return todayGroup;
    }

    private ArrayList<Task> extractTasksDueToday(Queue<Task> taskQueue) {
        ArrayList<Task> todayList = new ArrayList<Task>();
        Task task = taskQueue.peek();
        LocalDate date;
        LocalDate dateToday = LocalDate.now();

        while (task != null) {
            date = task.getEndDate().toLocalDate();
            if (date.isEqual(dateToday)) {
                todayList.add(taskQueue.remove());
            } else {
                break;
            }
            task = taskQueue.peek();
        }
        return todayList;
    }

    private TaskViewGroup createTaskTomorrowGroup(ArrayList<Task> tomorrowList) {
        TaskViewGroup tomorrowGroup = createTaskGroupDetailed(GROUP_TITLE_TOMORROW, tomorrowList);
        return tomorrowGroup;
    }

    private ArrayList<Task> extractTaskDueTomorrow(Queue<Task> taskQueue) {
        ArrayList<Task> tomorrowList = new ArrayList<Task>();

        Task task = taskQueue.peek();
        LocalDate date;
        LocalDate dateTomorrow = LocalDate.now().plusDays(1);

        while (task != null) {
            date = task.getEndDate().toLocalDate();
            if (date.isEqual(dateTomorrow)) {
                tomorrowList.add(taskQueue.remove());
            } else {
                break;
            }
            task = taskQueue.peek();
        }
        return tomorrowList;
    }

    private TaskViewGroup createTaskRemainingGroup(ArrayList<Task> list) {
        TaskViewGroup remainingGroup = createTaskGroupDetailed(GROUP_TITLE_THE_REST, list);
        return remainingGroup;
    }

    private LinkedList<Task> copyTasksIntoLinkedList(ArrayList<Task> list) {
        LinkedList<Task> linkedList = new LinkedList<Task>(list);
        return linkedList;
    }

    private void processFloatingTask(ArrayList<Task> floatingTasks) {
        if (floatingTasks == null) {
            return;
        }
        if (_isGUIFormat) {
            _viewList.add(createFloatingTaskGroup(floatingTasks));
        }
    }

    private TaskViewGroup createFloatingTaskGroup(ArrayList<Task> floatingTasks) {
        TaskViewGroup floatingGroup = createTaskGroup(GROUP_TITLE_FLOATING_TASKS, floatingTasks);
        return floatingGroup;
    }

    private TaskViewGroup createTaskGroupDetailed(String groupTitle, ArrayList<Task> list) {
        TaskViewGroup viewGroup = new TaskViewGroup(groupTitle);

        for (int i = 0; i < list.size(); i++) {
            Task t = list.get(i);
            TaskDetailedRow row = new TaskDetailedRow(t, index);
            viewGroup.addDetialedTaskRow(row);
            _viewOrderedTaskList.add(t);
            index++;
        }

        return viewGroup;
    }

    private TaskViewGroup createTaskGroup(String groupTitle, ArrayList<Task> list) {
        TaskViewGroup viewGroup = new TaskViewGroup(groupTitle);

        for (int i = 0; i < list.size(); i++) {
            Task t = list.get(i);
            TaskRow row = new TaskRow(index + ". " + t.getTitle(), t.isCompleted());
            viewGroup.addTaskRow(row);
            _viewOrderedTaskList.add(t);
            index++;
        }

        return viewGroup;
    }

}
