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
| **add** `<task_name>` **by** `<due_date>` | Add new task with deadline | add submit math assignment by monday |
| **add** `<task_name>` **from** `<start_date>` **to** `<end_date>` | Add new task with start and end date | add "buy birthday cake" from 22/10 4pm to 23/10 |


Editing tasks
---------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **edit** `<task_id>` **due** `<due_time_value>` | Edit due time of the task | edit 1 due 20/11/2015 10:20am |
| **edit** `<task_id>` **start** `<start_time_value>` | Edit start time of the task | edit 1 start 20/11/2015 10:00am |
| **edit** `<task_id>` **end** `<end_time_value>` | Edit end time of the task | edit 1 end 20/11/2015 8:00pm |
| **edit** `<task_id>` **title** `<task_title_value>` | Edit task title | edit 1 title "buy milks" |
| **edit** `<task_id>` **description** `<task_description_value>` | Edit task title | edit 1 description "zzz" |
| **edit** `<task_id>` **category** `<category_name>` | Edit category name of the task | edit 1 category shopping |


Marking tasks as completed/incompleted
---------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **mark** `<task_id>` **completed** | Mark task as completed | mark 1 completed |
| **mark** `<task_id>` **incompleted** | Mark task as incompleted | mark 1 incompleted |

Postponing tasks to other day
---------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **postpone** `<task_id>` [**to**] `<new_start_date>` | Postpone task | postpone 1 23/11 3pm |


Deleting tasks
---------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **delete** `<task_id>` | Delete task with specific id | delete 1 |


Viewing and searching tasks
---------------------

| Syntax | Description | Example |
|------------------------------------------------------------|--------------------------------------------|-----------------------------------------------------|
| **view tasks** `[completed/incompleted]` | View all tasks (completed or not) | view tasks view tasks completed view tasks incompleted |
| **view tasks** `[completed/incompleted]` **on** `<date>` | View all tasks (completed or not) on a specific day | view tasks on monday view tasks completed on tuesday view task incompleted on 11/10 |
| **view tasks** `[completed/incompleted]` **from** `<start_time>` **to** `<end_time>` | View all tasks (completed or not) happened between `<start_time>` and `<end_time>` | view tasks from 11/10 2am to 20/10 3pm |
| **view task** `<task_id>` | View task with a specific id | view task 1 |
| **find** `<keyword>` | Search and filter tasks according to the title of the tasks | find cat |
| **find** `<keyword>` **in** `<category>` | Search for task of specific category with keyword | find cat in shopping |


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
