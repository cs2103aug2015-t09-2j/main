package katnote.ui;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import katnote.task.Task;
import katnote.task.TaskType;

public class TaskViewFormatter {

    private static final String GROUP_TITLE_TODAY = "Today";
    private static final String GROUP_TITLE_TOMORROW = "Tomorrow";
    private static final String GROUP_TITLE_THE_REST = "The Rest";
    private static final String GROUP_TITLE_FLOATING_TASKS = "Floating Tasks";

    private ArrayList<TaskViewGroup> _viewList = new ArrayList<TaskViewGroup>();
    private boolean _isGUIFormat;
    private int index = 1;

    public TaskViewFormatter(ArrayList<Task> list, boolean isGUIFormat) {
        _isGUIFormat = isGUIFormat;
        ArrayList<Task> listCopy = new ArrayList<Task>(list);
        ArrayList<Task> floatingTasks = extractFloatingTask(listCopy);
        processFloatingTask(floatingTasks);
        processNormalTasks(listCopy);
    }

    private ArrayList<Task> extractFloatingTask(ArrayList<Task> list) {
        ArrayList<Task> newList = new ArrayList<Task>();
        for (int i = 0; i < list.size(); i++) {
            Task t = list.get(i);
            if (t.getTaskType() == TaskType.FLOATING) {
                newList.add(list.remove(i));
                i--;
            }
        }
        return newList;
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
            // date order from now to the future is assumed
            // (?) should ignore empty groups? "Nothing here" line ?
            _viewList.add(createTaskTodayGroup(normalTasksQueue));
            _viewList.add(createTaskTomorrowGroup(normalTasksQueue));
            _viewList.add(createTaskRemainingGroup(normalTasksQueue));
        }

    }

    private TaskViewGroup createTaskTodayGroup(Queue<Task> taskQueue) {
        ArrayList<Task> todayList = new ArrayList<Task>();

        Task task = taskQueue.peek();
        LocalDate date;
        LocalDate dateToday = LocalDate.now();

        while (task != null) {
            date = task.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (date.isEqual(dateToday)) {
                todayList.add(taskQueue.remove());
            } else {
                break;
            }
            task = taskQueue.peek();
        }

        TaskViewGroup todayGroup = createTaskGroupDetailed(GROUP_TITLE_TODAY, todayList);

        return todayGroup;
    }

    private TaskViewGroup createTaskTomorrowGroup(Queue<Task> taskQueue) {
        ArrayList<Task> tomorrowList = new ArrayList<Task>();

        Task task = taskQueue.peek();
        LocalDate date;
        LocalDate dateTomorrow = LocalDate.now().plusDays(1);

        while (task != null) {
            date = task.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (date.isEqual(dateTomorrow)) {
                tomorrowList.add(taskQueue.remove());
            } else {
                break;
            }
            task = taskQueue.peek();
        }

        TaskViewGroup tomorrowGroup = createTaskGroupDetailed(GROUP_TITLE_TOMORROW, tomorrowList);

        return tomorrowGroup;
    }

    private TaskViewGroup createTaskRemainingGroup(Queue<Task> taskQueue) {
        ArrayList<Task> remainingList = new ArrayList<Task>(taskQueue);

        TaskViewGroup remainingGroup = createTaskGroupDetailed(GROUP_TITLE_THE_REST, remainingList);

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
            index++;
        }

        return viewGroup;
    }

}
