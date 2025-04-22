import React from 'react';
import CourseTable from '../components/course_table/CourseTable';

const Profile = () => {
    return (
        <main>
        <h1>My Info</h1>
        <h2>Major(s)</h2>
        <p>Here is where you can view your major.</p>
        <ul>
        </ul>
        <h2>Minor(s)</h2>
        <p>Here you can see your minor.</p>
        <ul>
        </ul>
        <h1>Completed Courses</h1>
        <CourseTable courses={[]} remove={undefined}/>
        </main>
    );
}

export default Profile;