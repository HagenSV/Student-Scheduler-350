import React from 'react';

const CourseListing = ({ course }) => {
   return (
        <>
            <h2>{ course.title }({course.department} {course.code}{course.section})</h2>
            <p>{/* TODO Course Time */ } | { course.professor }</p>
        </>
    );
}