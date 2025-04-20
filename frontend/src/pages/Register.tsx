import React from 'react';

const Register = () => {
    return (
        <form method='POST'>
            <h2>Student Scheduler</h2>
            <input name="username" type="text" placeholder="username" /><br />
            <input name="password" type="password" placeholder="password" /><br />
            <input name="confirmPassword" type="password" placeholder="confirm password" /><br />
            <input className="btn primary" type="submit" value="Register" /><br />
            <a className="btn secondary" href="/login">Login</a><br />
            <a href="/recover">Forgot Password?</a><br />
        </form>
    )
}

export default Register;