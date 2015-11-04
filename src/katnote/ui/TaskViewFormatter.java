//@@author A0125447E
package katnote.ui;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import katnote.logic.ViewState;
import katnote.task.Task;

public class TaskViewFormatter {

    private static final int FRONT_INDEX = 0;
    private static final int NUMBER_OF_DAYS_A_WEEK = 7;
    private static final String GROUP_TITLE_OVERDUE = "OVERDUE";
    private static final String GROUP_TITLE_TODAY = "Today";
    private static final String GROUP_TITLE_TOMORROW = "Tomorrow";
    private static final String GROUP_TITLE_SEARCH_RESULT = "Search Result";
    private static final String GROUP_TITLE_FLOATING_TASKS = "Task to do";
    private static final String DATE_PATTERN = "dd MMM yy";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private ArrayList<TaskViewGroup> _viewList = new ArrayList<TaskViewGroup>();
    private ArrayList<Task> _viewOrderedTaskList = new ArrayList<Task>();
    private int index = 1;

    public TaskViewFormatter(ViewState viewState) {
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

    private void processSearchListing(ArrayList<Task> normalTasksList, ArrayList<Task> eventList) {
        ArrayList<Task> combinedList = combineNormalAndEventsOrdered(normalTasksList, eventList);
        if (combinedList.isEmpty()) {
            return;
        }
        _viewList.add(createTaskGroupDetailed(GROUP_TITLE_SEARCH_RESULT, combinedList, false));
    }

    private void processForOverdue(Queue<Task> normalTasksQueue) {
        ArrayList<Task> overdueList = extractTaskDueBeforeThisDate(normalTasksQueue, LocalDate.now());
        if (overdueList.isEmpty()) {
            return;
        }
        TaskViewGroup taskGroupOverDue = createTaskOverdueGroup(overdueList);
        _viewList.add(taskGroupOverDue);

    }

    private void processForTheWeek(Queue<Task> normalTasksQueue, ArrayList<Task> eventList) {
        ArrayList<Task> remainingList;
        ArrayList<Task> eventRemainingWeekList;

        for (int i = 0; i <= NUMBER_OF_DAYS_A_WEEK; i++) {
            LocalDate dayDue = LocalDate.now().plusDays(i);
            remainingList = extractTaskDueThisDate(normalTasksQueue, dayDue);
            eventRemainingWeekList = extractEventsHappeningThisDate(eventList, dayDue);
            if (remainingList.isEmpty() && eventRemainingWeekList.isEmpty()) {
                continue;
            }

            ArrayList<Task> combinedList = combineNormalAndEventsOrdered(remainingList, eventRemainingWeekList);

            if (i == 0) {
                TaskViewGroup taskGroupForToday = createTaskTodayGroup(combinedList);
                _viewList.add(taskGroupForToday);
            } else if (i == 1) {
                TaskViewGroup taskGroupForTomorrow = createTaskTomorrowGroup(combinedList);
                _viewList.add(taskGroupForTomorrow);
            } else {
                String dayOfWeekString = getDayString(dayDue.getDayOfWeek());
                dayOfWeekString += "      " + dayDue.format(DATE_FORMAT);
                _viewList.add(createTaskGroupDetailed(dayOfWeekString, combinedList, true));
            }
        }
    }

    private void processRemainingTask(Queue<Task> normalTasksQueue, ArrayList<Task> eventCopy) {
        LocalDate date = LocalDate.now();
        while (!normalTasksQueue.isEmpty()) {
            Task normalTask = normalTasksQueue.peek();
            date = normalTask.getEndDate().toLocalDate();
            ArrayList<Task> normalTaskListThisDate = extractTaskDueThisDate(normalTasksQueue, date);
            ArrayList<Task> eventListThisDate = extractEventsHappeningThisDate(eventCopy, date);
            ArrayList<Task> combinedList = combineNormalAndEventsOrdered(normalTaskListThisDate, eventListThisDate);

            if (date.equals(LocalDate.now())) {
                TaskViewGroup taskGroupForToday = createTaskTodayGroup(combinedList);
                _viewList.add(taskGroupForToday);
            } else if (date.equals(LocalDate.now().plusDays(1))) {
                TaskViewGroup taskGroupForTomorrow = createTaskTomorrowGroup(combinedList);
                _viewList.add(taskGroupForTomorrow);
            } else {
                String dayOfWeekString = getDayString(date.getDayOfWeek());
                dayOfWeekString += "      " + date.format(DATE_FORMAT);

                _viewList.add(createTaskGroupDetailed(dayOfWeekString, combinedList, true));
            }
        }
        Queue<Task> eventQueue = copyTasksIntoLinkedList(eventCopy);
        while (!eventQueue.isEmpty()) {
            Task event = eventQueue.peek();
            LocalDate eventStartDate = event.getStartDate().toLocalDate();
            if (eventStartDate.isBefore(date) || eventStartDate.isEqual(date)) {
                eventQueue.poll();
                continue;
            } else {
                break;
            }
        }
        eventCopy = new ArrayList<Task>(eventQueue);
        if (eventCopy.isEmpty()) {
            return;
        }
        _viewList.add(createTaskGroupDetailed("Events", eventCopy, false));
    }

    private void processFloatingTask(ArrayList<Task> floatingTasks) {
        if (floatingTasks == null || floatingTasks.isEmpty()) {
            return;
        }
        _viewList.add(createFloatingTaskGroup(floatingTasks));
    }

    private void processNormalTasksAndEvents(ArrayList<Task> normalTasksList, ArrayList<Task> eventList) {
        if (normalTasksList.isEmpty() && eventList.isEmpty()) {
            return;
        }
        Queue<Task> normalTasksQueue = copyTasksIntoLinkedList(normalTasksList);
        ArrayList<Task> eventCopy = new ArrayList<Task>(eventList);
        processForOverdue(normalTasksQueue);
        processForTheWeek(normalTasksQueue, eventCopy);
        processRemainingTask(normalTasksQueue, eventCopy);
    }

    private ArrayList<Task> combineNormalAndEventsOrdered(ArrayList<Task> normalList, ArrayList<Task> eventList) {
        ArrayList<Task> combinedList = new ArrayList<Task>();

        while (!normalList.isEmpty() || !eventList.isEmpty()) {
            Task earlierToDo;
            if (normalList.isEmpty()) {
                earlierToDo = eventList.remove(FRONT_INDEX);
            } else if (eventList.isEmpty()) {
                earlierToDo = normalList.remove(FRONT_INDEX);
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
            return eventList.remove(FRONT_INDEX);
        } else if (eventStartDate.isEqual(dueDate)) {
            if (eventStartTime.isBefore(dueTime)) {
                return eventList.remove(FRONT_INDEX);
            } else {
                return normalList.remove(FRONT_INDEX);
            }
        } else if (eventStartDate.isAfter(dueDate)) {
            return normalList.remove(FRONT_INDEX);
        } else if (dueDate.isBefore(eventEndDate)) {
            return normalList.remove(FRONT_INDEX);
        } else if (dueDate.isEqual(eventEndDate)) {
            if (dueTime.isBefore(eventEndTime)) {
                return normalList.remove(FRONT_INDEX);
            } else {
                return eventList.remove(FRONT_INDEX);
            }
        } else {
            return eventList.remove(FRONT_INDEX);
        }
    }

    private ArrayList<Task> extractEventsHappeningThisDate(ArrayList<Task> eventList, LocalDate eventDate) {
        ArrayList<Task> newEventList = new ArrayList<Task>();
        LocalDate startDate;
        LocalDate endDate;

        for (int i = 0; i < eventList.size(); i++) {
            Task task = eventList.get(i);
            startDate = task.getStartDate().toLocalDate();
            endDate = task.getEndDate().toLocalDate();
            if ((eventDate.isEqual(startDate) || eventDate.isAfter(startDate))
                    && (eventDate.isBefore(endDate) || eventDate.isEqual(endDate))) {
                newEventList.add(task);
                if (eventDate.isEqual(endDate)) {
                    eventList.remove(i);
                    i--;
                }
            }
        }

        return newEventList;
    }

    private String getDayString(DayOfWeek day) {
        return day.getDisplayName(TextStyle.FULL, Locale.US);
    }

    private ArrayList<Task> extractTaskDueBeforeThisDate(Queue<Task> taskQueue, LocalDate dueDate) {
        ArrayList<Task> dueList = new ArrayList<Task>();

        Task task = taskQueue.peek();
        LocalDate date;

        while (task != null) {
            date = task.getEndDate().toLocalDate();
            if (date.isBefore(dueDate)) {
                dueList.add(taskQueue.remove());
            } else {
                break;
            }
            task = taskQueue.peek();
        }
        return dueList;
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

    private TaskViewGroup createTaskOverdueGroup(ArrayList<Task> overdueList) {
        TaskViewGroup overdueGroup = createTaskGroupDetailed(GROUP_TITLE_OVERDUE, overdueList, false);
        return overdueGroup;
    }

    private TaskViewGroup createTaskTodayGroup(ArrayList<Task> todayList) {
        TaskViewGroup todayGroup = createTaskGroupDetailed(GROUP_TITLE_TODAY, todayList, true);
        return todayGroup;
    }

    private TaskViewGroup createTaskTomorrowGroup(ArrayList<Task> tomorrowList) {
        TaskViewGroup tomorrowGroup = createTaskGroupDetailed(GROUP_TITLE_TOMORROW, tomorrowList, true);
        return tomorrowGroup;
    }

    private TaskViewGroup createFloatingTaskGroup(ArrayList<Task> floatingTasks) {
        TaskViewGroup floatingGroup = createTaskGroup(GROUP_TITLE_FLOATING_TASKS, floatingTasks);
        return floatingGroup;
    }

    private LinkedList<Task> copyTasksIntoLinkedList(ArrayList<Task> list) {
        LinkedList<Task> linkedList = new LinkedList<Task>(list);
        return linkedList;
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
            TaskRow row = new TaskRow(index, t.getTitle(), t.isCompleted());
            viewGroup.addTaskRow(row);
            _viewOrderedTaskList.add(t);
            index++;
        }
        return viewGroup;
    }
}
