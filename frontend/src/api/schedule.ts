import { Course } from "../interface/course"

const scheduleAPI = {
    getSchedule: async () => {
        const response = await fetch("/api/v1/schedule")
        if (response.ok){
           return await response.json() as Course[];
        }
        return [];
    },
    addCourse: async (course: Course) => {
        const id = course.cid
        const response = await fetch("/api/v1/schedule/add",{
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ id })  
        })
        if (response.ok){
            return await response.json();
        }
        return {message: "Error adding course to schedule", success: false, conflicts: []}
    },
    removeCourse: async (course: Course) => {
        const id = course.cid
        await fetch("/api/v1/schedule/remove",{
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ id })
        })
    },

    getCompleted: async () => {
        const response = await fetch("/api/v1/completed",{
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            }
        })
        if (response.ok){
            return await response.json() as Course[];
        }
        return [];
    },

    addCompleted: async (course: Course) => {
        const id = course.cid
        await fetch("/api/v1/completed/add",{
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ id })
        })
    },

    removeCompleted: async (course: Course) => {
        const id = course.cid
        await fetch("/api/v1/completed/remove",{
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ id })
        })
    }
}


export default scheduleAPI