import React, { useState } from 'react';
import CourseListing from '../components/course_listing/CourseListing';
import scheduleAPI from '../api/schedule';
import { Course } from '../interface/course';

const Schedule = () => {
    const [courses,setCourses] = useState<Course[]>([]);

    const getSchedule = async () => {
        try {
            const result = await scheduleAPI.getSchedule()
            if (result !== courses){
                setCourses(result);
            }
        } catch (e){
            console.log(e)
        }
    }

    getSchedule();

    const removeCourse = (course) => {
        return async () => {
            await scheduleAPI.removeCourse(course)
            setCourses(await scheduleAPI.getSchedule())
        }
    }

    return (
        <main>
        <h1>My Schedule</h1>
        <p>Here is where you can view your schedule.</p>
        <h1>Classes</h1>
        <p>This is where you can see a list of your classes</p>
        {courses.map((course, index) => <CourseListing key={index} course={course} clickEvent={removeCourse(course)}/>)}
        <h1 id="export">Export</h1>
        </main>
    );
}

export default Schedule;