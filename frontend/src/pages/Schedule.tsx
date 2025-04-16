import React, { useState } from 'react';
import CourseListing from '../components/course_listing/CourseListing';
import scheduleAPI from '../api/schedule';
import search from '../api/search';
import { Course, toTimeString } from '../interface/course';
import CourseTable from '../components/course_stable/CourseTable';

const days = [0,1,2,3,4]
const times = [0, 15, 30, 45, 60, 75, 90, 105, 120, 135, 150, 165, 180, 195, 210, 225, 240, 255, 270, 285, 300, 315, 330, 345, 360, 375, 390, 405, 420, 435, 450, 465, 480, 495, 510, 525, 540, 555, 570, 585, 600, 615, 630, 645, 660, 675, 690, 705, 720, 735, 750, 765, 780, 795, 810]

const Schedule = () => {
    const [scheduleQueried, setQueried] = useState(false);
    const [courses,setCourses] = useState<Course[]>([]);

    const getSchedule = async () => {
        setQueried(true)
        try {
            const result = await search("COMP 141")
            //const result = await scheduleAPI.getSchedule()
            if (result !== courses){
                setCourses(result);
            }
        } catch (e){
            console.log(e)
        }
    }

    if (!scheduleQueried){
        getSchedule()
    }

    const getCourseByTime = (currentTime: number, day: number) => {
        for (const course of courses){
            const startTime = course.startTime[day];
            if (startTime === -1){ continue; }
            if (startTime <= currentTime && startTime+course.duration >= currentTime){
                //return course
                return `${course.department} ${course.courseCode}${course.section}`
            }
        }
        return ""
    }

    const removeCourse = (course: Course) => {
        return async () => {
            await scheduleAPI.removeCourse(course)
            setCourses(await scheduleAPI.getSchedule())
        }
    }

    return (
        <main>
        <h1>My Schedule</h1>
        <table style={{ tableLayout: "fixed", width: "80%", textAlign: "center" }}>
            <thead>
            <tr>
                <th>Time</th>
                <th>Monday</th>
                <th>Tuesday</th>
                <th>Wednesday</th>
                <th>Thursday</th>
                <th>Friday</th>
            </tr>
            </thead>
            <tbody>
            {times.map((time,timeIndex) => (
                <tr key={timeIndex}>
                    <td>{toTimeString(time)}</td>
                    {days.map((day, dayIndex) => (      
                        <td key={dayIndex}>{getCourseByTime(time,day)}</td>
                    ))}
                </tr>
            ))}
            </tbody>
        </table>
        <h1>Classes</h1>
        <CourseTable course={courses} />
        {courses && <p>Nothing to see here, try adding a course!</p>}
        <h1 id="export">Export</h1>
        <p>Email Schedule</p>
        <p>Export to PDF</p>
        <p>Export to Google Calendar</p>
        </main>
    );
}

export default Schedule;