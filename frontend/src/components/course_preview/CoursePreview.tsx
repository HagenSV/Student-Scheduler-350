import React from 'react';

interface Course {
    title: string;
    department: string;
    code: number;
    section: string;
    credits: number;
    description: string;
    professor: string;
}

const CoursePreview = ({ course }) => {
    return (
        <>
        <h1>Info</h1>
        {course && (<><h2>{course.title}</h2>
        <h3>{course.department} {course.code}{course.section}, {course.credits} credits</h3>
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