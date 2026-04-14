// Array of students for testing
const students = [
    {
        name: "Lalit",
        marks: [
            { subject: "Math", score: 78 },
            { subject: "English", score: 82 },
            { subject: "Science", score: 74 },
            { subject: "History", score: 69 },
            { subject: "Computer", score: 88 }
        ],
        attendance: 82
    },
    {
        name: "Rahul",
        marks: [
            { subject: "Math", score: 90 },
            { subject: "English", score: 85 },
            { subject: "Science", score: 80 },
            { subject: "History", score: 76 },
            { subject: "Computer", score: 92 }
        ],
        attendance: 91
    },
    {
        name: "Aman",
        marks: [
            { subject: "Math", score: 55 },
            { subject: "English", score: 60 },
            { subject: "Science", score: 58 },
            { subject: "History", score: 52 },
            { subject: "Computer", score: 50 }
        ],
        attendance: 70
    },
    {
        name: "Riya",
        marks: [
            { subject: "Math", score: 88 },
            { subject: "English", score: 90 },
            { subject: "Science", score: 38 }, 
            { subject: "History", score: 80 },
            { subject: "Computer", score: 85 }
        ],
        attendance: 95
    },
    {
        name: "Priya",
        marks: [
            { subject: "Math", score: 92 },
            { subject: "English", score: 88 },
            { subject: "Science", score: 95 },
            { subject: "History", score: 85 },
            { subject: "Computer", score: 91 }
        ],
        attendance: 98
    }
];

function getTotalMarks(student) {
    let total = 0;
    for (let i = 0; i < student.marks.length; i++) {
        total += student.marks[i].score;
    }
    return total;
}

function getAverage(student) {
    const total = getTotalMarks(student);
    const avg = total / student.marks.length;
    return Math.round(avg * 10) / 10;
}

function getFailedSubject(student) {
    let failedSubjects = [];
    for (let i = 0; i < student.marks.length; i++) {
        if (student.marks[i].score <= 40) {
            failedSubjects.push(student.marks[i].subject);
        }
    }
    return failedSubjects.length === 0 ? null : failedSubjects;
}

function getGrade(student) {
    const avg = getAverage(student);
    const failedSubjects = getFailedSubject(student);

    if (failedSubjects !== null) {
        let failedText = "";
        for (let i = 0; i < failedSubjects.length; i++) {
            failedText += failedSubjects[i];
            if (i < failedSubjects.length - 1) {
                failedText += ", ";
            }
        }
        if (student.attendance < 75) {
            return `Fail (Low Attendance) (Failed in ${failedText}) `;
        }
        return `Fail (Failed in ${failedText}) `;
    }

    if (student.attendance < 75) {
        return `Fail (Low Attendance: ${student.attendance})`;
    }

    if (avg >= 85) return "A";
    if (avg >= 70) return "B";
    if (avg >= 50) return "C";
    return "Fail";
}

console.log("TOTAL MARKS FOR EACH STUDENT");
students.forEach(function (student) {
    const total = getTotalMarks(student);
    console.log(`${student.name} Total Marks: ${total}`);
});

console.log("AVERAGE MARKS FOR EACH STUDENT");
students.forEach(function (student) {
    const average = getAverage(student);
    console.log(`${student.name} Average Marks: ${average}`);
});

console.log("SUBJECT-WISE HIGHEST SCORE IN THE CLASS");
const subjects = [];
for (let i = 0; i < students[0].marks.length; i++) {
    subjects.push(students[0].marks[i].subject);
}

for (let s = 0; s < subjects.length; s++) {
    const subject = subjects[s];
    let highestScore = -1;
    let topperName = "";

    for (let i = 0; i < students.length; i++) {
        const student = students[i];

        for (let j = 0; j < student.marks.length; j++) {
            const mark = student.marks[j];

            if (mark.subject === subject && mark.score > highestScore) {
                highestScore = mark.score;
                topperName = student.name;
            }
        }
    }

    console.log(`Highest in ${subject}: ${topperName} (${highestScore})`);
}

console.log("SUBJECT-WISE AVERAGE SCORE");
for (let s = 0; s < subjects.length; s++) {
    const subject = subjects[s];
    let totalScore = 0;

    for (let i = 0; i < students.length; i++) {
        const student = students[i];

        for (let j = 0; j < student.marks.length; j++) {
            if (student.marks[j].subject === subject) {
                totalScore += student.marks[j].score;
                break;
            }
        }
    }

    const subjectAvg = Math.round((totalScore / students.length) * 10) / 10;
    console.log(`Average ${subject} Score: ${subjectAvg}`);
}

console.log("OVERALL CLASS TOPPER");
let classTopper = students[0];
let highestTotal = getTotalMarks(students[0]);

for (let i = 1; i < students.length; i++) {
    const currentTotal = getTotalMarks(students[i]);
    if (currentTotal > highestTotal) {
        highestTotal = currentTotal;
        classTopper = students[i];
    }
}

console.log(`Class Topper: ${classTopper.name} with ${highestTotal} marks`);

console.log("GRADE ASSIGNMENT");

for (let i = 0; i < students.length; i++) {
    const student = students[i];
    console.log(student.name + " Grade: " + getGrade(student));
}

console.log("FULL STUDENT REPORT SUMMARY");

for (let i = 0; i < students.length; i++) {
    const student = students[i];
    const total = getTotalMarks(student);
    const avg = getAverage(student);
    const grade = getGrade(student);

    console.log("\n--- " + student.name + " ---");
    console.log("  Attendance : " + student.attendance + "%");

    for (let j = 0; j < student.marks.length; j++) {
        const markObj = student.marks[j];
        console.log("  " + markObj.subject.padEnd(10) + ": " + markObj.score);
    }

    console.log("  Total      : " + total);
    console.log("  Average    : " + avg);
    console.log("  Grade      : " + grade);
}

console.log("Analysis Complete");