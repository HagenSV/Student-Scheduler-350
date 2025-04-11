import React from 'react';

const Register = () => {
    return (
        <form>
            <h2>Student Scheduler</h2>
            <input name="email" type="text" placeholder="email" /><br />
            <input name="confirm" type="text" placeholder="email" /><br />
            <input name="pass" type="password" placeholder="password" /><br />
            <input className="btn primary" type="submit" value="Register" /><br />
            <a className="btn secondary" href="/register">Login</a><br />
            <a href="/recover">Forgot Password?</a><br />
        </form>
    )
}

export default Register;