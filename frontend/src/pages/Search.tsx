import React from 'react';

import CoursePreview from '../components/course_preview/CoursePreview.tsx';
import { Container, Row, Col } from 'react-bootstrap';

const example = {
    title: "Introduction to Computer Science",
    department: "COMP",
    code: 155,
    section: "B",
    credits: 3,
    description: "This course is an introduction to computer science.",
    professor: "Dr. Dickinson"
}

const Search = () => {
    return (
        <main>
        <Container>
            <Row>
                <Col md={6}>
                    <h1>Course Search</h1>
                    <h2>Search Bar</h2>
                </Col>
                <Col md={6} style={{ "border-left": "1px solid black" }}>
                    <CoursePreview course={null} />
                </Col>
            </Row>
        </Container>
        </main>
    );
}

export default Search;