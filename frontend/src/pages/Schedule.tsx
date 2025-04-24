import React, { useState, useEffect } from 'react';
import scheduleAPI from '../api/schedule';
import { Course, toTimeString } from '../interface/course';
import CourseTable from '../components/course_table/CourseTable';
import Calendar from '../components/calendar/Calendar';
import { Modal } from 'react-bootstrap';

const Schedule = () => {
    const [courses, setCourses] = useState<Course[]>([]);
    const [show, setShow] = useState(false);
    const [email, setEmail] = useState("");
    const [emailError, setEmailError] = useState("");

    const handleShow = () => setShow(true);

    const validateEmail = (value: string) => {
        const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
        if (!emailRegex.test(value)) {
            setEmailError("Invalid email address");
        } else {
            setEmailError("");
        }
    };

    const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setEmail(value);
        validateEmail(value);
    };


    useEffect(() => {
        const fetchData = async () => {
            try {
                const result = await scheduleAPI.getSchedule();
                setCourses(result);
            } catch (e) {
                console.log(e);
                console.log("Failed to get schedule");
            }
        };
        fetchData();
    }, []);

    const removeCourse = (course: Course) => {
        return async () => {
            await scheduleAPI.removeCourse(course);
            setCourses(await scheduleAPI.getSchedule());
        };
    };

    const sendEmail = async () => {
        const response = await fetch("/api/v1/export/email?dest=" + email)
        setShow(false);
    }

    return (
        <main>
        <Modal show={show}>
            <Modal.Header closeButton>
              <Modal.Title>Email Schedule</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <input type="text"
                placeholder="Email"
                value={email}
                onChange={handleEmailChange}
                style={{ borderColor: emailError ? "red" : undefined }}/>
                {emailError && <p style={{ color: "red" }}>{emailError}</p>}

            </Modal.Body>
            <Modal.Footer>
                <button
                    className="btn primary"
                    onClick={sendEmail}
                    disabled={!!emailError || !email}
                >
                    Send
                </button>
            </Modal.Footer>
        </Modal>
            <h1>My Schedule</h1>
            <Calendar courses={courses} />
            <h1>Classes</h1>
            <CourseTable courses={courses} remove={removeCourse} />
            {!courses.length && <p>Nothing to see here, try adding a course!</p>}
            <h1 id="export">Export</h1>
            <p><button onClick={handleShow}
            style={{
                background: "none",
                color: "blue",
                border: "none",
                padding: "0",
                textDecoration: "underline",
                cursor: "pointer",
            }}>Email Schedule</button></p>
            <p><a href="/api/v1/export/pdf">Export to PDF</a></p>
            <p><a href="/api/v1/export/googleCalendar">Export to Google Calendar</a></p>
        </main>
    );
};

export default Schedule;