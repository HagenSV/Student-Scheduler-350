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

const CoursePreview = (course) => {
    return (
        <main>
        <h1>Info</h1>
        <h2>{course.title}</h2>
        <h3>{course.department} {course.number}{course.section}, {course.credits} credits</h3>
        <h3>{course.professor}</h3>
        <h3>Summary</h3>
        <p>{course.description}</p>
        </main>
    );
}