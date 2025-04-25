import React, { useState, useEffect } from 'react';
import scheduleAPI from '../api/schedule';
import CourseTable from '../components/course_table/CourseTable';

const Profile = () => {
    const [completedCourses, setCompleted] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const courses = await scheduleAPI.getCompleted();
                setCompleted(courses);
            } catch (e) {
                console.log(e);
                console.log("Failed to get completed courses");
            }
        }
        fetchData()
    })

    const removeCourse = (course: Course) => {
        return async () => {
            await scheduleAPI.removeCompleted(course);
            setCourses(await scheduleAPI.getCompleted());
        };
    };

    return (
        <main>
        <h1>My Info</h1>
        <h2>Major(s)</h2>
        <p>Not implemented</p>
        <ul>
        </ul>
        <h2>Minor(s)</h2>
        <p>Not implemented</p>
        <ul>
        </ul>
        <h1>Completed Courses</h1>
        <CourseTable courses={completedCourses} remove={removeCourse}/>
        </main>
    );
}

export default Profile;