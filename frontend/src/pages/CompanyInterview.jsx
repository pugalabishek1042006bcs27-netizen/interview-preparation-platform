import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'

const COMPANIES = [
    {
        id: 'tcs',
        name: 'TCS',
        fullName: 'Tata Consultancy Services',
        tagColor: 'blue',
        questions: [
            { q: 'What is the difference between Array and LinkedList?', topic: 'DSA', difficulty: 'Easy' },
            { q: 'Explain OOP concepts with examples.', topic: 'Java', difficulty: 'Easy' },
            { q: 'What is the time complexity of Binary Search?', topic: 'DSA', difficulty: 'Easy' },
            { q: 'Tell me about yourself.', topic: 'HR', difficulty: 'Easy' },
            { q: 'What are your strengths and weaknesses?', topic: 'HR', difficulty: 'Easy' },
            { q: 'Write a program to reverse a string.', topic: 'DSA', difficulty: 'Easy' },
            { q: 'Explain method overloading vs method overriding.', topic: 'Java', difficulty: 'Easy' },
            { q: 'What is the difference between DELETE and TRUNCATE in SQL?', topic: 'Database', difficulty: 'Easy' },
            { q: 'Explain the Exception Handling hierarchy in Java.', topic: 'Java', difficulty: 'Medium' },
            { q: 'Check if a given string is a palindrome.', topic: 'DSA', difficulty: 'Easy' },
            { q: 'Are you willing to relocate or work in shifts?', topic: 'HR', difficulty: 'Easy' },
            { q: 'Find the missing number in an array of 1 to N.', topic: 'DSA', difficulty: 'Medium' },
            { q: 'What is SDLC? Explain its phases.', topic: 'Software Engineering', difficulty: 'Easy' },
            { q: 'Why do you want to join TCS?', topic: 'HR', difficulty: 'Easy' },
            { q: 'What are the main features of Java?', topic: 'Java', difficulty: 'Easy' },
        ]
    },
    {
        id: 'infosys',
        name: 'Infosys',
        fullName: 'Infosys Limited',
        tagColor: 'blue',
        questions: [
            { q: 'Explain the difference between JDK, JRE, and JVM.', topic: 'Java', difficulty: 'Easy' },
            { q: 'What is Spring Boot and why is it used?', topic: 'Java', difficulty: 'Medium' },
            { q: 'Explain Bubble Sort with time complexity.', topic: 'DSA', difficulty: 'Easy' },
            { q: 'Why do you want to join Infosys?', topic: 'HR', difficulty: 'Easy' },
            { q: 'Where do you see yourself in 5 years?', topic: 'HR', difficulty: 'Medium' },
            { q: 'Write a program to detect a loop in a linked list.', topic: 'DSA', difficulty: 'Medium' },
            { q: 'What are your greatest weaknesses?', topic: 'HR', difficulty: 'Easy' },
            { q: 'What are joins in SQL? Explain the different types.', topic: 'Database', difficulty: 'Medium' },
            { q: 'Explain garbage collection in Java.', topic: 'Java', difficulty: 'Medium' },
            { q: 'Remove duplicates from a sorted array.', topic: 'DSA', difficulty: 'Easy' },
            { q: 'What is the difference between an abstract class and an interface?', topic: 'Java', difficulty: 'Easy' },
            { q: 'Explain the different layers of the OSI model.', topic: 'Networking', difficulty: 'Medium' },
            { q: 'Find the nth Fibonacci number optimally.', topic: 'DSA', difficulty: 'Medium' },
            { q: 'How do you handle a disagreement with a team member?', topic: 'HR', difficulty: 'Medium' },
            { q: 'Write a program to reverse a number without converting to string.', topic: 'DSA', difficulty: 'Easy' },
        ]
    },
    {
        id: 'wipro',
        name: 'Wipro',
        fullName: 'Wipro Limited',
        tagColor: 'blue',
        questions: [
            { q: 'What is a Stack? Give a real world example.', topic: 'DSA', difficulty: 'Easy' },
            { q: 'Explain inheritance in Java with an example.', topic: 'Java', difficulty: 'Easy' },
            { q: 'What is the difference between HashMap and HashTable?', topic: 'Java', difficulty: 'Medium' },
            { q: 'Describe a situation where you worked in a team.', topic: 'HR', difficulty: 'Easy' },
            { q: 'How do you handle work pressure?', topic: 'HR', difficulty: 'Easy' },
            { q: 'Why should we hire you?', topic: 'HR', difficulty: 'Easy' },
            { q: 'What is a Singleton class? Write a thread-safe implementation.', topic: 'Java', difficulty: 'Medium' },
            { q: 'What is a deadlock and how can it be avoided?', topic: 'OS', difficulty: 'Easy' },
            { q: 'Find the factorial of a number using recursion.', topic: 'DSA', difficulty: 'Easy' },
            { q: 'What is the difference between Primary Key and Unique Key?', topic: 'Database', difficulty: 'Easy' },
            { q: 'Merge two sorted arrays into a single sorted array.', topic: 'DSA', difficulty: 'Medium' },
            { q: 'Explain the difference between String, StringBuilder, and StringBuffer.', topic: 'Java', difficulty: 'Medium' },
            { q: 'Explain Agile vs Waterfall methodology.', topic: 'Software Engineering', difficulty: 'Medium' },
            { q: 'Describe a challenging task you overcame recently.', topic: 'HR', difficulty: 'Medium' },
            { q: 'Check if two given strings are valid anagrams of each other.', topic: 'DSA', difficulty: 'Easy' },
        ]
    },
    {
        id: 'google',
        name: 'Google',
        fullName: 'Google LLC',
        tagColor: 'green',
        questions: [
            { q: 'Given an array, find two numbers that add up to a target sum.', topic: 'DSA', difficulty: 'Medium' },
            { q: 'Explain BFS and DFS. When would you use each?', topic: 'DSA', difficulty: 'Medium' },
            { q: 'What is Dynamic Programming? Give an example.', topic: 'DSA', difficulty: 'Hard' },
            { q: 'Design a URL shortener like bit.ly.', topic: 'System Design', difficulty: 'Hard' },
            { q: 'How would you improve Google Maps?', topic: 'HR', difficulty: 'Hard' },
            { q: 'Implement an LRU Cache.', topic: 'DSA', difficulty: 'Hard' },
            { q: 'Find the median of two sorted arrays.', topic: 'DSA', difficulty: 'Hard' },
            { q: 'Design Google Search Autocomplete functionality.', topic: 'System Design', difficulty: 'Hard' },
            { q: 'Tell me about a time you had to drastically pivot your project strategy.', topic: 'HR', difficulty: 'Medium' },
            { q: 'How do you confidently invert a binary tree?', topic: 'DSA', difficulty: 'Medium' },
            { q: 'Design a highly available YouTube video uploading service.', topic: 'System Design', difficulty: 'Hard' },
            { q: 'Find the longest palindromic substring in a string.', topic: 'DSA', difficulty: 'Medium' },
            { q: 'Tell me about a time you disagreed with your manager.', topic: 'HR', difficulty: 'Medium' },
            { q: 'Explain Paxos or Raft consensus algorithms conceptually.', topic: 'System Design', difficulty: 'Hard' },
            { q: 'Trapping rain water problem: given an array of heights, calculate water trapped.', topic: 'DSA', difficulty: 'Hard' },
        ]
    },
    {
        id: 'amazon',
        name: 'Amazon',
        fullName: 'Amazon.com Inc.',
        tagColor: 'amber',
        questions: [
            { q: 'Find the maximum subarray sum (Kadane\'s algorithm).', topic: 'DSA', difficulty: 'Medium' },
            { q: 'Explain the difference between SQL and NoSQL databases.', topic: 'Database', difficulty: 'Medium' },
            { q: 'Design Amazon\'s shopping cart system.', topic: 'System Design', difficulty: 'Hard' },
            { q: 'Tell me about a time you failed and what you learned.', topic: 'HR', difficulty: 'Medium' },
            { q: 'How do you prioritize tasks when everything is urgent?', topic: 'HR', difficulty: 'Medium' },
            { q: 'Design a recommendation system for Amazon products.', topic: 'System Design', difficulty: 'Hard' },
            { q: 'Merge K sorted linked lists.', topic: 'DSA', difficulty: 'Hard' },
            { q: 'Tell me about a time you showed "Customer Obsession".', topic: 'HR', difficulty: 'Medium' },
            { q: 'Design a distributed rate limiter.', topic: 'System Design', difficulty: 'Hard' },
            { q: 'Find the lowest common ancestor in a Binary Search Tree.', topic: 'DSA', difficulty: 'Medium' },
            { q: 'Tell me about a time you disagreed and committed to a decision.', topic: 'HR', difficulty: 'Medium' },
            { q: 'Explain how CAP Theorem applies to Amazon DynamoDB.', topic: 'Database', difficulty: 'Medium' },
            { q: 'Serialize and Deserialize a Binary Tree.', topic: 'DSA', difficulty: 'Hard' },
            { q: 'Tell me about a time you invented a simple solution for a complex problem.', topic: 'HR', difficulty: 'Medium' },
            { q: 'Design a parking lot management system.', topic: 'Object Oriented Design', difficulty: 'Medium' },
        ]
    },
    {
        id: 'microsoft',
        name: 'Microsoft',
        fullName: 'Microsoft Corporation',
        tagColor: 'blue',
        questions: [
            { q: 'Reverse a linked list. Write the code.', topic: 'DSA', difficulty: 'Medium' },
            { q: 'What is multithreading in Java? How is it implemented?', topic: 'Java', difficulty: 'Hard' },
            { q: 'Explain REST vs GraphQL.', topic: 'Java', difficulty: 'Medium' },
            { q: 'Design a notification system for millions of users.', topic: 'System Design', difficulty: 'Hard' },
            { q: 'Why do you want to work at Microsoft?', topic: 'HR', difficulty: 'Easy' },
            { q: 'Given a matrix, set its entire row and column to 0 if an element is 0.', topic: 'DSA', difficulty: 'Medium' },
            { q: 'Explain the core principles of Object Oriented Design (SOLID).', topic: 'OOD', difficulty: 'Medium' },
            { q: 'Design a real-time collaborative code editor like VS Code Live Share.', topic: 'System Design', difficulty: 'Hard' },
            { q: 'Tell me about a time you used feedback to improve yourself.', topic: 'HR', difficulty: 'Medium' },
            { q: 'Write a program to validate a Binary Search Tree.', topic: 'DSA', difficulty: 'Medium' },
            { q: 'What are microservices? Explain their pros and cons compared to monoliths.', topic: 'System Design', difficulty: 'Medium' },
            { q: 'Design a globally scalable ticketing system (like Ticketmaster).', topic: 'System Design', difficulty: 'Hard' },
            { q: 'Find the first non-repeating character in a string.', topic: 'DSA', difficulty: 'Easy' },
            { q: 'Tell me about a time you went above and beyond your core responsibilities.', topic: 'HR', difficulty: 'Medium' },
            { q: 'Explain continuous integration and continuous deployment (CI/CD).', topic: 'DevOps', difficulty: 'Medium' },
        ]
    },
]

const CompanyInterview = () => {
    const [selected, setSelected] = useState(null)
    const [step, setStep] = useState('list')
    const [current, setCurrent] = useState(0)
    const [answers, setAnswers] = useState([])
    const [answer, setAnswer] = useState('')
    const [timer, setTimer] = useState(120)
    const [scores, setScores] = useState([])
    const navigate = useNavigate()

    useEffect(() => {
        if (step !== 'question') return
        setTimer(120)
        const interval = setInterval(() => {
            setTimer(prev => {
                if (prev <= 1) {
                    clearInterval(interval)
                    handleNext(true)
                    return 0
                }
                return prev - 1
            })
        }, 1000)
        return () => clearInterval(interval)
    }, [current, step])

    const startInterview = (company) => {
        const shuffledPool = [...company.questions].sort(() => 0.5 - Math.random())
        const selectedQuestions = shuffledPool.slice(0, 5)

        setSelected({ ...company, questions: selectedQuestions })
        setCurrent(0)
        setAnswers([])
        setScores([])
        setAnswer('')
        setStep('intro')
    }

    const handleNext = (timedOut = false) => {
        const score = timedOut ? 0 : Math.min(10, Math.max(1, Math.floor(answer.trim().split(' ').length / 5)))
        const newAnswers = [...answers, { answer: timedOut ? '(Time expired)' : answer, score }]
        setAnswers(newAnswers)
        setScores([...scores, score])
        setAnswer('')

        if (current + 1 >= selected.questions.length) {
            setStep('result')
        } else {
            setCurrent(current + 1)
        }
    }

    const totalScore = scores.reduce((a, b) => a + b, 0)
    const maxScore = selected ? selected.questions.length * 10 : 0
    const percentage = maxScore > 0 ? Math.round((totalScore / maxScore) * 100) : 0

    if (step === 'list') return (
        <div className="app-shell">
            <Navbar />
            <main className="page-content">
                <header className="page-header">
                    <div className="page-heading">
                        <span className="section-badge blue">Company Interview Sets</span>
                        <h1 className="page-title">Corporate Prep</h1>
                        <p className="page-subtitle">
                           Practice curated questions from top tech giants. Each session dynamically picks 5 random questions from our extensive pool.
                        </p>
                    </div>
                </header>

                <div className="dashboard-grid">
                    {COMPANIES.map(company => (
                        <div key={company.id} onClick={() => startInterview(company)} className="action-card">
                            <span className={`action-card-label ${company.tagColor}`}>{company.name}</span>
                            <h3 className="action-card-title">{company.fullName}</h3>
                            <p className="action-card-copy">{company.questions.length} specialized questions in pool.</p>
                            <div style={{marginTop: '20px', fontWeight: 700, color: 'var(--blue)'}}>Launch Set →</div>
                        </div>
                    ))}
                </div>
            </main>
        </div>
    )

    if (step === 'intro') return (
        <div className="app-shell">
            <Navbar />
            <main className="page-content" style={{maxWidth: '600px'}}>
                <section className="panel" style={{textAlign: 'center', padding: '40px'}}>
                    <div className={`section-badge ${selected.tagColor}`} style={{marginBottom: '20px'}}>{selected.name}</div>
                    <h2 className="page-title" style={{fontSize: '2.5rem'}}>{selected.name} Protocol</h2>
                    <p className="section-copy" style={{margin: '20px 0 40px'}}>You will be presented with 5 random questions. You have 2 minutes for each.</p>
                    
                    <div className="form-stack" style={{textAlign: 'left', background: 'var(--bg-soft)', padding: '24px', borderRadius: 'var(--radius-sm)', marginBottom: '40px'}}>
                       <div style={{display: 'flex', gap: '12px', marginBottom: '12px'}}><span style={{color: 'var(--blue)'}}>✔</span> <p className="section-copy">Read carefully before typing.</p></div>
                       <div style={{display: 'flex', gap: '12px', marginBottom: '12px'}}><span style={{color: 'var(--blue)'}}>✔</span> <p className="section-copy">AI will evaluate your response structure.</p></div>
                       <div style={{display: 'flex', gap: '12px'}}><span style={{color: 'var(--blue)'}}>✔</span> <p className="section-copy">Scores are calculated instantly.</p></div>
                    </div>

                    <div style={{display: 'flex', gap: '16px'}}>
                        <button onClick={() => setStep('question')} className="button-primary">Initialize Session</button>
                        <button onClick={() => setStep('list')} className="button-secondary">Cancel</button>
                    </div>
                </section>
            </main>
        </div>
    )

    if (step === 'question') {
        const q = selected.questions[current]
        return (
            <div className="app-shell">
                <Navbar />
                <main className="page-content">
                    <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px'}}>
                        <div>
                           <p className="helper-text" style={{fontWeight: 800, textTransform: 'uppercase'}}>{selected.name} Session</p>
                           <h2 className="section-title">Question {current + 1} of {selected.questions.length}</h2>
                        </div>
                        <div className={`stat-value ${timer <= 30 ? 'text-red' : ''}`} style={{color: timer <= 30 ? 'var(--red)' : 'var(--text-main)'}}>
                           {Math.floor(timer / 60)}:{String(timer % 60).padStart(2, '0')}
                        </div>
                    </div>

                    <div className="panel" style={{marginBottom: '24px', borderLeft: '4px solid var(--blue)'}}>
                        <div className="filter-row" style={{marginBottom: '16px'}}>
                           <span className="section-badge blue">{q.topic}</span>
                           <span className="section-badge yellow">{q.difficulty}</span>
                        </div>
                        <p style={{fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-main)'}}>{q.q}</p>
                    </div>

                    <div className="field-group" style={{marginBottom: '24px'}}>
                        <label className="field-label">Your Response</label>
                        <textarea
                            value={answer}
                            onChange={e => setAnswer(e.target.value)}
                            className="field-textarea"
                            placeholder="Type your technical response here..."
                        />
                    </div>

                    <div style={{display: 'flex', gap: '16px'}}>
                        <button onClick={() => handleNext(false)} disabled={!answer.trim()} className="button-primary" style={{width: 'auto', paddingInline: '40px'}}>
                            {current + 1 === selected.questions.length ? 'Transmit Results' : 'Submit & Next'}
                        </button>
                        <button onClick={() => handleNext(true)} className="button-secondary">Skip Question</button>
                    </div>
                </main>
            </div>
        )
    }

    if (step === 'result') {
        return (
            <div className="app-shell">
                <Navbar />
                <main className="page-content">
                    <header className="page-header" style={{textAlign: 'center', display: 'block'}}>
                        <h1 className="page-title">Session Summary</h1>
                        <p className="page-subtitle" style={{margin: '10px auto'}}>{selected.name} Assessment Protocol Terminated</p>
                    </header>

                    <div className="stat-card" style={{maxWidth: '400px', margin: '0 auto 40px', textAlign: 'center', padding: '40px'}}>
                        <p className="stat-label">Execution Score</p>
                        <p className="stat-value" style={{fontSize: '4rem'}}>{percentage}%</p>
                        <p className={`section-badge ${percentage >= 70 ? 'green' : 'yellow'}`} style={{marginTop: '20px'}}>
                           {percentage >= 70 ? 'Ready for Interview' : 'Requires Optimization'}
                        </p>
                    </div>

                    <h3 className="section-title" style={{marginBottom: '20px'}}>Technical Telemetry</h3>
                    <div className="questions-list">
                        {selected.questions.map((q, i) => (
                            <div key={i} className="panel" style={{marginBottom: '16px'}}>
                                <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '12px'}}>
                                   <p style={{fontWeight: 700}}>{q.q}</p>
                                   <p style={{fontWeight: 800, color: scores[i] >= 7 ? 'var(--green)' : 'var(--amber)'}}>{scores[i]}/10</p>
                                </div>
                                <div style={{background: 'var(--bg-soft)', padding: '12px', borderRadius: 'var(--radius-sm)', fontStyle: 'italic', color: 'var(--text-soft)'}}>
                                   "{answers[i]?.answer}"
                                </div>
                            </div>
                        ))}
                    </div>

                    <div style={{display: 'flex', gap: '16px', marginTop: '40px'}}>
                        <button onClick={() => startInterview(selected)} className="button-primary">Retry Session</button>
                        <button onClick={() => setStep('list')} className="button-secondary">Select New Company</button>
                        <button onClick={() => navigate('/dashboard')} className="button-secondary">Dashboard</button>
                    </div>
                </main>
            </div>
        )
    }
}

export default CompanyInterview