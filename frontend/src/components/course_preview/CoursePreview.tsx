import React from 'react';
import { Course, formatTime } from '../../interface/course';
import scheduleAPI from '../../api/schedule';

interface CoursePreviewParams {
    course: Course | null
    addCourse: MouseEventHandler
}

const CoursePreview: React.FC<CoursePreviewParams> = ({ course, addCourse }) => {

    const saveCompleted: MouseEventHandler = () => {
        //TODO: api endpoint for saving completed courses
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


            <h3 style={{ fontWeight: "bold" }}>Summary</h3>
            <p>{course.description}</p>
            <button style={{ float: "left"}} className="btn primary" onClick={addCourse}>Add to schedule</button>
            <button style={{ float: "left", margin: "0 20px"}} className="btn secondary" onClick={saveCompleted}>Save as completed</button>
        </>
        )}
        {!course && <p>Select a course for more info</p>}
        </>
    );
}

export default CoursePreview;