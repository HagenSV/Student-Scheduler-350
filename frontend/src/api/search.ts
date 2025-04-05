import { Course } from "../interface/course";

const search = async (query: string) => {
    //Post query to localhost:8080/api/search
    const response = await fetch('http://localhost:8080/api/v1/search', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ query })
    });

    if (response.ok){
        const json = await response.json()
        return json;
    }

    return []
}

export default search;