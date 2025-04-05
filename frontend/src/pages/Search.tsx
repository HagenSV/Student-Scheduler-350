import React, { KeyboardEventHandler, MouseEventHandler, useState } from 'react';

import CourseListing from '../components/course_listing/CourseListing';
import CoursePreview from '../components/course_preview/CoursePreview';
import { Container, Row, Col } from 'react-bootstrap';
import { Course } from '../interface/course';
import search from '../api/search';

const Search = () => {
    const [results, setResults] = useState<Course[]>([]);
    const [selectedCourse, setCourse] = useState<Course|null>(null);

    const keyPress: KeyboardEventHandler<HTMLInputElement> = async (event: React.KeyboardEvent<HTMLInputElement>) => {
        const target = event.target as HTMLInputElement
        const res = await search(target.value)
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
        <Container>
            <Row>
                <Col md={6}>
                    <h1>Course Search</h1>
                    <input type="text" placeholder="Search for a course" onKeyUp={keyPress}/>
                    {results.map((course) => (<CourseListing course={course} clickEvent={selectCourse(course)}/>))}
                </Col>
                <Col md={6} style={{ borderLeft: "1px solid black" }}>
                    <CoursePreview course={ selectedCourse } />
                </Col>
            </Row>
        </Container>
        </main>
    );
}

export default Search;