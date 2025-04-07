import React from 'react';
import { Course } from '../../interface/course';

interface CoursePreviewParams {
    course: Course | null
}

const CoursePreview: React.FC<CoursePreviewParams> = ({ course }) => {
    return (
        <>
        <h1>Info</h1>
        {course && (<><h2>{course.name}</h2>
        <h3>{course.department} {course.courseCode}{course.section}, {course.credits} credits</h3>
        <h3>{course.professor}</h3>
        <h3>Summary</h3>
        <p>{course.description}</p>
        </>
        )}
        {!course && <p>Select a course for more info</p>}
        </>
    );
}

export default CoursePreview;