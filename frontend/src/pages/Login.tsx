import React from "react";

const Login = () => {

    return (
        <form method='POST'>
            <h2>Student Scheduler</h2>
            <input name="username" type="text" placeholder="email" /><br />
            <input name="password" type="password" placeholder="password" /><br />
            <input className="btn primary" type="submit" value="Log In" /><br />
            <a className="btn secondary" href="/register">Create Account</a><br />
            <a href="/recover">Forgot Password?</a><br />
        </form>
    )
}

export default Login;