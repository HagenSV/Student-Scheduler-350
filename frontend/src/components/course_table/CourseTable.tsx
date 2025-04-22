import React from 'react';
import './CourseTable.css'
import { Course, formatTime } from '../../interface/course';

interface CourseTableProps {
    courses: Course[]
    remove: Function
}

const CourseTable: React.FC<CourseTableProps> = ({ courses, remove }) => {
   return (
        <table className="courseTable">
            <thead>
            <tr>
                <th> </th>
                <th> Course Code </th>
                <th> Course Title </th>
                <th> Time </th>
                <th> Professor </th>
                <th> Credits </th>
             </tr>
            </thead>
            <tbody>
                {courses.map((course, index) => (
                    <tr key={index}>
                        <td onClick={remove(course)}> {index + 1} </td>
                        <td> {course.department} {course.courseCode}{course.section} </td>
                        <td> {course.name} </td>
                        <td> {formatTime(course)} </td>
                        <td> {course.professor} </td>
                        <td> {course.credits} </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
}

export default CourseTable;