import React, { KeyboardEventHandler, MouseEventHandler, useState } from 'react';

import CourseListing from '../components/course_listing/CourseListing';
import CoursePreview from '../components/course_preview/CoursePreview';
import { Container, Row, Col, Modal } from 'react-bootstrap';
import { Course } from '../interface/course';
import scheduleAPI from '../api/schedule';
import search from '../api/search';

const Search = () => {
    const [show, setShow] = useState(false);
    const [conflicts, setConflicts] = useState<String[]>([]);
    const [message, setMessage] = useState("");
    const [results, setResults] = useState<Course[]>([]);
    const [selectedCourse, setCourse] = useState<Course|null>(null);

    const handleClose = () => setShow(false);
   // const handleShow = () => setShow(true);

   const addCourse = (course: Course) => {
        const event: MouseEventHandler = async () => {
            console.log(course)
           const response = await scheduleAPI.addCourse(course)
           console.log(response)
           if (!response.success) {
               setMessage(response.message)
               setShow(true)
               if (response.conflicts.length > 0) {
                   setConflicts(conflicts.map((conflict) => {
                          return conflict.name
                     })
                   )
               }
           }
        }
        return event;
   }

    const keyPress: KeyboardEventHandler<HTMLInputElement> = async (event: React.KeyboardEvent<HTMLInputElement>) => {
        const target = event.target as HTMLInputElement
        const res = await search(target.value)
        console.log(res)
        setResults(res)
    }

    const selectCourse = (course: Course) => {
        const click: MouseEventHandler = () => {
            setCourse(course)
        }
        return click;
    }

    return (
        <main>
        <Modal show={show} onHide={handleClose}>
            <Modal.Header closeButton>
              <Modal.Title>Failed to add course</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <p>{ message }</p>
                {conflicts && <><p>Conflicts with:</p>
                <ul>
                    {conflicts.map((conflict, index) => (
                        <li key={index}>{conflict}</li>
                    ))}
                </ul></>}
            </Modal.Body>
            <Modal.Footer>
                {conflicts && <button variant="primary" onClick={addCourse}>Remove Conflicts Add</button>}
                <button variant="secondary" onClick={handleClose}>Close</button>
            </Modal.Footer>
        </Modal>
        <Container>
            <Row>
                <Col md={6}>
                    <h1>Course Search</h1>
                    <input type="text" placeholder="Search for a course" onKeyUp={keyPress}/>
                    {results.map((course, index) => (<CourseListing key={index} course={course} clickEvent={selectCourse(course)}/>))}
                </Col>
                <Col md={6} style={{ borderLeft: "1px solid black" }}>
                    <CoursePreview course={ selectedCourse } addCourse={ addCourse(selectedCourse) } />
                </Col>
            </Row>
        </Container>
        </main>
    );
}

export default Search;