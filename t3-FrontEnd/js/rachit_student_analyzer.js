// Array of students for testing
const students = [
    {
        name: "Lalit",
        marks: [
            { subject: "Math",     score: 78 },
            { subject: "English",  score: 82 },
            { subject: "Science",  score: 74 },
            { subject: "History",  score: 69 },
            { subject: "Computer", score: 88 }
        ], 
        attendance: 82
    },
    {
        name: "Rahul",
        marks: [
        { subject: "Math",     score: 90 },
        { subject: "English",  score: 85 },
        { subject: "Science",  score: 80 },
        { subject: "History",  score: 76 },
        { subject: "Computer", score: 92 }
        ],
        attendance: 91
    },
    {
        name: "Aman",
        marks: [
        { subject: "Math",     score: 55 },
        { subject: "English",  score: 60 },
        { subject: "Science",  score: 58 },
        { subject: "History",  score: 52 },
        { subject: "Computer", score: 50 }
        ],
        attendance: 70
    },
    {
        name: "Riya",
        marks: [
        { subject: "Math",     score: 88 },
        { subject: "English",  score: 90 },
        { subject: "Science",  score: 38 },  // ≤ 40 → should trigger Fail
        { subject: "History",  score: 80 },
        { subject: "Computer", score: 85 }
        ],
        attendance: 95
    },
    {
        name: "Priya",
        marks: [
        { subject: "Math",     score: 92 },
        { subject: "English",  score: 88 },
        { subject: "Science",  score: 95 },
        { subject: "History",  score: 85 },
        { subject: "Computer", score: 91 }
        ],
        attendance: 98
    }
];
