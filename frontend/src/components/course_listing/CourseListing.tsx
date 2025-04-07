import React, { MouseEventHandler } from 'react';
import './CourseListing.css'
import { Course, formatTime } from '../../interface/course';

interface CourseListingProps {
    course: Course
    clickEvent: MouseEventHandler | undefined
}

const CourseListing: React.FC<CourseListingProps> = ({ course, clickEvent }) => {
   return (
        <div className="course-listing" onClick={clickEvent}>
            <h4>{ course.name } ({course.department} {course.courseCode}{course.section})</h4>
            <p>{formatTime(course)}</p>
            <p>{ course.professor }</p>
        </div>
    );
}

export default CourseListing;