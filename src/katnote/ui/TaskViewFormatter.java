package katnote.ui;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import katnote.ViewState;
import katnote.task.Task;

public class TaskViewFormatter {

    private static final int NUMBER_OF_DAYS_A_WEEK = 7;
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
        processFloatingTask(floatingTasks);
        processNormalTasksAndEvents(viewState.getNormalTasks(), viewState.getEventTasks());
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

    private void processNormalTasksAndEvents(ArrayList<Task> normalTasks, ArrayList<Task> events) {
        if (normalTasks == null || normalTasks.isEmpty()) {
            return;
        }
        Queue<Task> normalTasksQueue = copyTasksIntoLinkedList(normalTasks);
        if (_isGUIFormat) {
            processForToday(normalTasksQueue, events);
            processForTomorrow(normalTasksQueue, events);
            processForTheWeek(normalTasksQueue, events);
            processRemainingTasks(normalTasksQueue);
        }
    }

    private void processForTheWeek(Queue<Task> normalTasksQueue, ArrayList<Task> eventList) {
        ArrayList<Task> remainingList;
        ArrayList<Task> eventRemainingWeekList;

        for (int i = 2; i < NUMBER_OF_DAYS_A_WEEK; i++) {
            LocalDate dayDue = LocalDate.now().plusDays(i);
            remainingList = extractTaskDueThisDate(normalTasksQueue, dayDue);
            eventRemainingWeekList = extractEventsHappeningThisDate(eventList, dayDue);
            if (remainingList.isEmpty() && eventRemainingWeekList.isEmpty()) {
                continue;
            }
            ArrayList<Task> combinedList = combineNormalAndEventsOrdered(remainingList, eventRemainingWeekList);
            _viewList.add(createTaskGroupDetailed(getDayString(dayDue.getDayOfWeek()), combinedList, false));
        }
    }

    private void processRemainingTasks(Queue<Task> normalTasksQueue) {
        if (normalTasksQueue.isEmpty()) {
            return;
        }
        ArrayList<Task> remainingList = new ArrayList<Task>(normalTasksQueue);
        _viewList.add(createTaskRemainingGroup(remainingList));
    }

    private void processForTomorrow(Queue<Task> normalTasksQueue, ArrayList<Task> eventList) {
        ArrayList<Task> tomorrowList = extractTaskDueTomorrow(normalTasksQueue);
        ArrayList<Task> eventTomorrowList = extractEventsHappeningTomorrow(eventList);
        if (tomorrowList.isEmpty() && eventTomorrowList.isEmpty()) {
            return;
        }
        ArrayList<Task> combinedList = combineNormalAndEventsOrdered(tomorrowList, eventTomorrowList);
        TaskViewGroup taskGroupForTomorrow = createTaskTomorrowGroup(combinedList);
        _viewList.add(taskGroupForTomorrow);
    }

    private void processForToday(Queue<Task> normalTasksQueue, ArrayList<Task> eventList) {
        ArrayList<Task> todayList = extractTasksDueToday(normalTasksQueue);
        ArrayList<Task> eventTodayList = extractEventsHappeningToday(eventList);
        if (todayList.isEmpty() && eventTodayList.isEmpty()) {
            return;
        }
        ArrayList<Task> combinedList = combineNormalAndEventsOrdered(todayList, eventTodayList);
        TaskViewGroup taskGroupForToday = createTaskTodayGroup(combinedList);
        _viewList.add(taskGroupForToday);
    }

    private ArrayList<Task> combineNormalAndEventsOrdered(ArrayList<Task> normalList, ArrayList<Task> eventList) {
        ArrayList<Task> combinedList = new ArrayList<Task>();

        while (!normalList.isEmpty()|| !eventList.isEmpty()) {
            Task earlierToDo;
            if (normalList.isEmpty()) {
                earlierToDo = eventList.remove(0);
            } else if (eventList.isEmpty()) {
                earlierToDo = normalList.remove(0);
            } else {
                earlierToDo = extractTheEarlierTasks(normalList, eventList);
            }
            combinedList.add(earlierToDo);
        }
        return combinedList;
    }

    private Task extractTheEarlierTasks(ArrayList<Task> normalList, ArrayList<Task> eventList) {
        Task normalTask = normalList.get(0);
        Task event = eventList.get(0);
        LocalDate eventStartDate = event.getStartDate().toLocalDate();
        LocalTime eventStartTime = event.getStartDate().toLocalTime();
        LocalDate eventEndDate = event.getEndDate().toLocalDate();
        LocalTime eventEndTime = event.getEndDate().toLocalTime();
        LocalDate dueDate = normalTask.getEndDate().toLocalDate();
        LocalTime dueTime = normalTask.getEndDate().toLocalTime();

        if (eventStartDate.isBefore(dueDate)) {
            return eventList.remove(0);
        } else if (eventStartDate.isEqual(dueDate)) {
            if (eventStartTime.isBefore(dueTime)) {
                return eventList.remove(0);
            } else {
                return normalList.remove(0);
            }
        } else if (eventStartDate.isAfter(dueDate)) {
            return normalList.remove(0);
        } else if (dueDate.isBefore(eventEndDate)) {
            return normalList.remove(0);
        } else if (dueDate.isEqual(eventEndDate)) {
            if (dueTime.isBefore(eventEndTime)) {
                return normalList.remove(0);
            } else {
                return eventList.remove(0);
            }
        } else {
            return eventList.remove(0);
        }
    }

    private TaskViewGroup createTaskTodayGroup(ArrayList<Task> todayList) {
        TaskViewGroup todayGroup = createTaskGroupDetailed(GROUP_TITLE_TODAY, todayList, true);
        return todayGroup;
    }

    private ArrayList<Task> extractEventsHappeningToday(ArrayList<Task> eventList) {
        ArrayList<Task> todayList = new ArrayList<Task>();
        LocalDate startDate;
        LocalDate endDate;
        LocalDate dateToday = LocalDate.now();

        for (int i = 0; i < eventList.size(); i++) {
            Task task = eventList.get(i);
            startDate = task.getStartDate().toLocalDate();
            endDate = task.getEndDate().toLocalDate();
            if ((dateToday.isEqual(startDate) || dateToday.isAfter(startDate)) && (dateToday.isBefore(endDate)
                    || dateToday.isEqual(endDate))) {
                todayList.add(task);
            }
        }
        return todayList;
    }

    private ArrayList<Task> extractEventsHappeningTomorrow(ArrayList<Task> eventList) {
        ArrayList<Task> tomorrowList = new ArrayList<Task>();
        LocalDate startDate;
        LocalDate endDate;
        LocalDate dateTomorrow = LocalDate.now().plusDays(1);

        for (int i = 0; i < eventList.size(); i++) {
            Task task = eventList.get(i);
            startDate = task.getStartDate().toLocalDate();
            endDate = task.getEndDate().toLocalDate();
            if ((dateTomorrow.isEqual(startDate) || dateTomorrow.isAfter(startDate)) && (dateTomorrow.isBefore(endDate)
                    || dateTomorrow.isEqual(endDate))) {
                tomorrowList.add(task);
            }
        }
        return tomorrowList;
    }

    private ArrayList<Task> extractEventsHappeningThisDate(ArrayList<Task> eventList, LocalDate eventDate) {
        ArrayList<Task> newEventList = new ArrayList<Task>();
        LocalDate startDate;
        LocalDate endDate;

        for (int i = 0; i < eventList.size(); i++) {
            Task task = eventList.get(i);
            startDate = task.getStartDate().toLocalDate();
            endDate = task.getEndDate().toLocalDate();
            if ((eventDate.isEqual(startDate) || eventDate.isAfter(startDate)) && (eventDate.isBefore(endDate)
                    || eventDate.isEqual(endDate))) {
                newEventList.add(task);
            }
        }

        return newEventList;
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
        TaskViewGroup tomorrowGroup = createTaskGroupDetailed(GROUP_TITLE_TOMORROW, tomorrowList, true);
        return tomorrowGroup;
    }

    private String getDayString(DayOfWeek day) {
        return day.getDisplayName(TextStyle.FULL, Locale.US);
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

    private ArrayList<Task> extractTaskDueThisDate(Queue<Task> taskQueue, LocalDate dueDate) {
        ArrayList<Task> dueList = new ArrayList<Task>();

        Task task = taskQueue.peek();
        LocalDate date;

        while (task != null) {
            date = task.getEndDate().toLocalDate();
            if (date.isEqual(dueDate)) {
                dueList.add(taskQueue.remove());
            } else {
                break;
            }
            task = taskQueue.peek();
        }
        return dueList;
    }

    private TaskViewGroup createTaskRemainingGroup(ArrayList<Task> list) {
        TaskViewGroup remainingGroup = createTaskGroupDetailed(GROUP_TITLE_THE_REST, list, false);
        return remainingGroup;
    }

    private LinkedList<Task> copyTasksIntoLinkedList(ArrayList<Task> list) {
        LinkedList<Task> linkedList = new LinkedList<Task>(list);
        return linkedList;
    }

    private void processFloatingTask(ArrayList<Task> floatingTasks) {
        if (floatingTasks == null || floatingTasks.isEmpty()) {
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

    private TaskViewGroup createTaskGroupDetailed(String groupTitle, ArrayList<Task> list, boolean isDateHidden) {
        TaskViewGroup viewGroup = new TaskViewGroup(groupTitle);

        for (int i = 0; i < list.size(); i++) {
            Task t = list.get(i);
            TaskDetailedRow row = new TaskDetailedRow(t, index, isDateHidden);
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
