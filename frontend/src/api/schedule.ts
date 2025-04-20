import { Course } from "../interface/course"

const scheduleAPI = {
    getSchedule: async () => {
        const response = await fetch("http://localhost:8080/api/v1/schedule")
        if (response.ok){
           return await response.json() as Course[];
        }
        return [];
    },
    addCourse: async (course: Course) => {
        const id = course.cid
        await fetch("http://localhost:8080/api/v1/schedule/add",{
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ id })  
        })
    },
    removeCourse: async (course: Course) => {
        const id = course.cid
        await fetch("http://localhost:8080/api/v1/schedule/remove",{
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ id })
        })
    }
}


export default scheduleAPI