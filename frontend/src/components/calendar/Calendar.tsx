import React from 'react';
import { Course, toTimeString } from '../../interface/course';

const days = [0, 1, 2, 3, 4];
const times = [0, 60, 120, 180, 240, 300, 360, 420, 480, 540, 600, 660, 720, 780]; // Hourly intervals from 8:00 AM to 9:00 PM
const getCoursesByDay = (day: number, courses: Course[]) => {
    const coursesOnDay: { course: Course; startHourIdx: number; endHourIdx: number }[] = [];
    for (const course of courses) {
        const startTime = course.startTime[day];
        if (startTime === -1) {
            continue;
        }
        const endTime = startTime + course.duration;
        // Find the starting and ending hour indices
        const startHourIdx = times.findIndex(t => startTime < t + 60) || 0;
        const endHourIdx = times.findIndex(t => endTime <= t + 60) || times.length - 1;
        coursesOnDay.push({ course, startHourIdx, endHourIdx });
    }
    return coursesOnDay;
};

interface CalendarProps {
    courses: Course[]
}

const Calendar: React.FC<CalendarProps> = ({ courses }) => {
    return (
        <table
            style={{
                tableLayout: "fixed",
                width: "95%",
                textAlign: "center",
                borderCollapse: "collapse",
                border: "2px solid #333",
            }}
        >
            <thead>
                <tr style={{ borderBottom: "2px solid #333" }}>
                    <th style={{ borderRight: "2px solid #333", padding: "12px", width: "10%" }}>Time</th>
                    <th style={{ borderRight: "2px solid #333", padding: "12px", width: "18%" }}>Monday</th>
                    <th style={{ borderRight: "2px solid #333", padding: "12px", width: "18%" }}>Tuesday</th>
                    <th style={{ borderRight: "2px solid #333", padding: "12px", width: "18%" }}>Wednesday</th>
                    <th style={{ borderRight: "2px solid #333", padding: "12px", width: "18%" }}>Thursday</th>
                    <th style={{ padding: "12px", width: "18%" }}>Friday</th>
                </tr>
            </thead>
            <tbody>
                {times.map((time, timeIndex) => (
                    <tr
                        key={timeIndex}
                        style={{ borderTop: "1px solid #999" }}
                    >
                        <td
                            style={{
                                borderRight: "2px solid #333",
                                padding: "12px",
                                verticalAlign: "top",
                            }}
                        >
                            {toTimeString(time)}
                        </td>
                        {days.map((day, dayIndex) => {
                            const coursesOnDay = getCoursesByDay(day,courses);
                            return (
                                <td
                                    key={dayIndex}
                                    style={{
                                        padding: "12px",
                                        verticalAlign: "top",
                                        height: "120px",
                                        position: "relative",
                                        borderRight: dayIndex < days.length - 1 ? "2px solid #333" : "none",
                                    }}
                                >
                                    {coursesOnDay
                                        .filter(({ startHourIdx, endHourIdx }) =>
                                            timeIndex >= startHourIdx && timeIndex <= endHourIdx
                                        )
                                        .map(({ course, startHourIdx, endHourIdx }, idx) => {
                                            const startTime = course.startTime[day];
                                            const duration = course.duration;
                                            const startHourTime = times[startHourIdx];
                                            const topOffsetMinutes = startTime - startHourTime;
                                            const totalHeight = (endHourIdx - startHourIdx) * 120 + (duration - (endHourIdx - startHourIdx) * 60) * (120 / 60);
                                            // Only render the box in the starting hour
                                            if (timeIndex === startHourIdx) {
                                                return (
                                                    <div
                                                        key={`${dayIndex}-${idx}`}
                                                        style={{
                                                            position: "absolute",
                                                            top: `${(topOffsetMinutes * 120) / 60}px`,
                                                            left: "5%",
                                                            width: "90%",
                                                            height: `${totalHeight}px`,
                                                            backgroundColor: "#f0f0f0",
                                                            border: "1px solid #333",
                                                            borderRadius: "4px",
                                                            padding: "4px",
                                                            textAlign: "center",
                                                            boxSizing: "border-box",
                                                            display: "flex",
                                                            alignItems: "center",
                                                            justifyContent: "center",
                                                            fontSize: "0.9em",
                                                            zIndex: 1,
                                                        }}
                                                    >
                                                        {`${course.department} ${course.courseCode}${course.section}`}
                                                    </div>
                                                );
                                            }
                                            return null;
                                        })}
                                </td>
                            );
                        })}
                    </tr>
                ))}
            </tbody>
        </table>

    )


}

export default Calendar;