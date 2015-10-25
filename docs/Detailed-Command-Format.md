# Detailed Command Format

Format notes:
- **Bold**: keyword, type exactly
- `<item inside angle bracket>`: replace with appropriate value
- `[item inside square bracket]`: optional argument
- a/b: use a or b but not both


Adding tasks
---------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **add** `<task_name>` | Add new floating task | add "go to school" |
| **add** `<task_name>` by `<due_date>` | Add new task with deadline | add "submit math assignment" by monday |
| **add** `<task_name>` from `<start_date>` to` <end_date>` | Add new task with start and end date | add "buy birthday cake" from 22/10 to 23/10 |


Editing tasks
---------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **edit [task]** `<task_id>` set due_time `<due_time_value>` | Edit due time of the task | edit task 1 set due_time 20/11/2015 10:20am |
| **edit [task]** `<task_id>` set start_time `<start_time_value>` | Edit start time of the task | edit task 1 set start_time 20/11/2015 10:00am |
| **edit [task]** `<task_id>` set end_time `<end_time_value>` | Edit end time of the task | edit task 1 set end_time 20/11/2015 8:00pm |
| **edit [task]** `<task_id>` set task_title `<task_title_value>` | Edit task title | edit task 1 set task_title "buy milks" |
| **edit [task]** `<task_id>` set task_description `<task_description_value>` | Edit task title | edit task 1 set task_description "zzz" |
| **edit [task]** `<task_id>` set category `<category_name>` | Edit category name of the task | edit task 1 set category shopping |


Marking tasks as completed/incompleted
---------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **mark [task]** `<task_id>` completed | Mark task as completed | mark task 1 completed |
| **mark [task]** `<task_id>` incompleted | Mark task as incompleted | mark 1 incompleted |


Deleting tasks
---------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **delete [task]** `<task_id>` | Delete task with specific id | delete task 1 |


Viewing and searching tasks
---------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **view tasks** `[completed/incompleted]` | View all tasks (completed or not) | view tasks view tasks completed view tasks incompleted |
| **view tasks** `[completed/incompleted]` on `<date>` | View all tasks (completed or not) on a specific day | view tasks on monday view tasks completed on tuesday view task incompleted on 11/10 |
| **view tasks** `[completed/incompleted]` from `<start_time>` to `<end_time>` | View all tasks (completed or not) happened between `<start_time>` and `<end_time>` | view tasks from 11/10 2am to 20/10 3pm |
| **view task** `<task_id>` | View task with a specific id | view task 1 |
| **find** `<keyword>` | Search and filter tasks according to the title of the tasks | find cat |
| **find** `<keyword>` in `<category>` | Search for task of specific category with keyword | find cat in shopping |


Undoing and Redoing
------------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **undo** | Undo the last action | undo |
| **redo** | Redo the last undo | redo |


Configuring save settings
-----------------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **set location** `<save_location>` | Set new data save location. KatNote will inform you if the location and setup if found. | set location "D:\Data\KatNote\schedule.txt" |
| **import** `<file_path>` | Update your KatNote with the schedule included in the imported file | import "D:\Data\KatNote\schedule.txt" |
| **export** `<file_path>` | Export your schedule into a text file | export "D:\Data\KatNote\schedule.txt" |


Getting help
---------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **help** | Show help for all commands | help |
| **redo** `<command>` | Show help for a specific command | help add |


Exiting the application
----------------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **exit** | Exit KatNote | exit |
