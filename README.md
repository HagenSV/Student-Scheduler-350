Course Scheduler

Scrum Backlog Notes:
Only have MVP in production, the Automated Scheduler is in the Schedule class, but has not been integrated with search or the console driver yet, however there are tests to prove that it works. As for the rest of the sprint backlog, we have only included the MVP, the database has not yet been created as we didn't expect it to take as much time as it did. And the HTML Server and Frontend work that was completed has not been merged yet as there was not time to test integration and thus has been left for Sprint 2. 

How to work the code:

Step 1:
Run Program

Step 2:
Enter a username.
If there is a schedule for that user, the program will load that users schedule.
If there is not a schedule for that user, a blank one will be created

Step 3:
enter help to see commands
*   add <id> - adds class to schedule if there are no schedule conflicts
*   add <id> replace - adds class to schedule removing any courses with schedule conflicts
*   remove <id> - removes class from schedule
*   courses - display list of users classes
*   calendar - display schedule as calendar
*   search - search for classes
*   results <page> - view page of search results
*   exit - exits the program

search: 
-> search shows the results in pages, look at the results tab for more information
-> The following commands can be typed in any order into the search bar and each filter will be applied 
Ex. (search 14:00 comp 16:00) -> returns all computer science courses starting between 14:00 and 16:00.


search by department:
Type search and type the four letter abbreviation for the department
`search comp`

search by course code:
Type search and then type the course code that you are looking for 
`search comp 141`

search by keyword:
Type search and then type any string that is contained in a course name, if a course contains that string then it will be returned
* Note that if you type the name of a department it will not perform the keyword filter
(search comp will return all computer science courses, search compu will return all courses containing compu in the name)
ex -> `search data comm`

search by professor:
Type search and then the last name of any professor to see the courses that they are teaching
`search Hutchins`

search by time
type search followed by a time in military time -> returns all courses that start at that time
type search followed by a time in military time and another time in military time and it will return all courses that start in that time range


add:
type add followed by the CID of the course to add the course
* the CID is x in the search results -> CID(x) Course Name ...

add replace:
type add followed by the CID of the course to add the course and remove any conflicting courses
* the CID is x in the search results -> CID(x) Course Name ...

remove:
type remove followed by the CID of the course to remove the course
* the CID is x in the search results -> CID(x) Course Name ...

calendar:
type `calendar` to view your current calendar

courses:
type `courses` to see all the courses in your schedule

results:
type `results` followed by the page number you wish to view to see the search results for that page

exit:
type `exit` to close the program. Your schedule will be saved for the next time you log in.

