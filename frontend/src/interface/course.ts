interface Course {
    name: string;
    department: string;
    courseCode: number;
    section: string;
    credits: number;
    description: string;
    professor: string;
    MWForTR: boolean;
    daysMeet: boolean[];
    startTime: number[];
    duration: number;
}

export type { Course }