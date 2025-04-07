import React, { MouseEventHandler } from 'react';
import './CourseListing.css'
import { Course } from '../../interface/course';

interface CourseListingProps {
    course: Course
    clickEvent: MouseEventHandler
}

const formatTime = (course: Course) => {
    const mwf = formatTimeMWF(course);
    const tr = formatTimeTR(course);

    console.log(mwf.length)
    if (mwf.length !== 1){
        return tr.length === 1 ? mwf : `${mwf}, ${tr}`
    } else {
        return tr
    }
}

const toTimeString = (time: number) => {
    const hour = 8+Math.floor(time/60)
    const minute = time%60

    return `${(hour-1)%12+1}:${minute < 10 ? "0" : ""}${minute}${hour <= 12 ? "AM" : "PM"}`
}

const formatTimeMWF = (course: Course) => {
    const days = ["M","T","W","R","F"];
    let time = ["",""]
    for (let i = 0; i < 5; i += 2){
        if (course.startTime[i] != -1){
            time[0] += days[i];
            time[1] = `${toTimeString(course.startTime[i])} - ${toTimeString(course.startTime[i]+course.duration)}`
        }
    }
    return time.join(" ")
}

const formatTimeTR = (course: Course) => {
    const days = ["M","T","W","R","F"];
    let time = ["",""]
    for (let i = 1; i < 5; i += 2){
        if (course.startTime[i] != -1){
            time[0] += days[i];
            time[1] = `${toTimeString(course.startTime[i])} - ${toTimeString(course.startTime[i]+course.duration)}`
        }
    }
    return time.join(" ")
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