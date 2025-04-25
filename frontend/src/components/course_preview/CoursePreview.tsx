import React, { useEffect, useState } from 'react';
import { Course, formatTime } from '../../interface/course';
import scheduleAPI from '../../api/schedule';

interface CoursePreviewParams {
    course: Course | null
    addCourse: MouseEventHandler
}

const CoursePreview: React.FC<CoursePreviewParams> = ({ course, addCourse }) => {
    const [schedule, setSchedule] = useState<Set<Number>>(new Set([]));
    const [completed, setCompleted] = useState<Set<Number>>(new Set([]));

    const fetchData = async () => {
       try {
           const schedule = await scheduleAPI.getSchedule();
           setSchedule(new Set(schedule.map((course) => { return course.cid })));

           const completed = await scheduleAPI.getCompleted();
           setCompleted(new Set(completed.map((course) => { return course.cid })));

       } catch (e) {
           console.log(e);
           console.log("Failed to get schedule");
       }
    };

    useEffect(() => {
       fetchData();
    }, []);

    const addToSchedule: MouseEventHandler = async () => {
        await addCourse();
        fetchData();
    }

    const saveCompleted: MouseEventHandler = async () => {
        await scheduleAPI.addCompleted(course);
        fetchData();
    }

    return (
        <>
        <h1>Info</h1>
        {course && (<>
            <h2 style={{ fontWeight: "bold" }}>{course.name}</h2>
            <h3>{course.department} {course.courseCode}{course.section}</h3>
            <h4>{course.professor}</h4>
            <h4>{formatTime(course)}</h4>
            <h4>{course.credits} Credits</h4>

            {/* <h3 style={{ fontWeight: "bold" }}>Summary</h3>
            <p>{course.description}</p> */}
            { (!schedule.has(course.cid) && !completed.has(course.cid)) && <>
                <button style={{ float: "left"}} className="btn primary" onClick={addToSchedule}>Add to schedule</button>
                <button style={{ float: "left", margin: "0 20px"}} className="btn secondary" onClick={saveCompleted}>Save as completed</button>
            </>}
            { schedule.has(course.cid) && <p>This course is already in your schedule</p> }
            { completed.has(course.cid) && <p>You already completed this course</p> }
        </>
        )}
        {!course && <p>Select a course for more info</p>}
        </>
    );
}

export default CoursePreview;