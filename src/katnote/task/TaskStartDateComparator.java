//@@author A0131003J

package katnote.task;

import java.time.LocalDateTime;
import java.util.Comparator;

public class TaskStartDateComparator implements Comparator<Task> {

    public int compare(Task t1, Task t2) {
        LocalDateTime due1 = t1.getStartDate();
        LocalDateTime due2 = t2.getStartDate();

        if (due1.isAfter(due2)) {
            return 1;
        } else if (due1.isEqual(due2)) {
            return 0;
        } else {
            return -1;
        }
    }

}
