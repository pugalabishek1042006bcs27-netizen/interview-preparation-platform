import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import { dailyChallengeAPI } from '../services/api'
import { useState, useEffect } from 'react'
import Navbar from '../components/Navbar'

const Dashboard = () => {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [streak, setStreak] = useState(0)

  useEffect(() => {
    const fetchStreak = async () => {
      try {
        const response = await dailyChallengeAPI.getDailyChallenge()
        setStreak(response.data.userStreak)
      } catch (err) {
        console.error('Error fetching streak:', err)
      }
    }
    fetchStreak()
  }, [])

  const cards = [
    {
      tag: 'PRACTICE LIBRARY',
      tagColor: 'blue',
      title: 'Question Bank',
      desc: 'Browse DSA, Java, Spring Boot, HR questions by topic and difficulty.',
      path: '/questions',
    },
    {
      tag: 'AI SESSION',
      tagColor: 'green',
      title: 'AI Mock Interview',
      desc: 'Simulate a real interview and receive instant AI-driven feedback.',
      path: '/interview',
    },
    {
      tag: 'COMPANY SETS',
      tagColor: 'amber',
      title: 'Company Interview',
      desc: 'Practice curated sets from TCS, Infosys, Google, Amazon and more.',
      path: '/company',
    },
    {
      tag: 'CODING',
      tagColor: 'yellow',
      title: 'Code Playground',
      desc: 'Write and run Python, Java, JavaScript and C++ in the browser.',
      path: '/playground',
    },
    {
      tag: 'CAREER',
      tagColor: 'blue',
      title: 'Resume Analyzer',
      desc: 'Analyze your resume against job descriptions and improve match quality.',
      path: '/resume',
    },
    {
      tag: 'NETWORKING',
      tagColor: 'purple',
      title: 'LinkedIn Analyzer',
      desc: 'Optimize your LinkedIn profile for recruiter visibility and personal branding.',
      path: '/linkedin',
    },
    {
      tag: 'ANALYTICS',
      tagColor: 'green',
      title: 'Progress Tracker',
      desc: 'Visualize your mock scores, identify weak domains, and build execution readiness.',
      path: '/progress',
    },
    {
      tag: 'DAILY HABIT',
      tagColor: 'amber',
      title: 'Daily Challenge',
      desc: `Today's unique problem. Current Streak: ${streak} days.`,
      path: '/daily-challenge',
    },
  ]

  const stats = [
    { label: 'Questions Available', value: '50+' },
    { label: 'Companies Covered', value: '6' },
    { label: 'Languages Supported', value: '4' },
  ]

  return (
    <div className="app-shell">
      <Navbar />

      <main className="page-content">
        <header className="page-header">
          <div className="page-heading">
            <span className="section-badge blue">Candidate Workspace</span>
            <h1 className="page-title">Elevate Your Career</h1>
            <p className="page-subtitle">
              Welcome back, {user?.name}. Select a specialized module below to begin your focused preparation session.
            </p>
          </div>
        </header>

        <div className="dashboard-grid">
          {cards.map((card) => (
            <div
              key={card.path}
              onClick={() => navigate(card.path)}
              className="action-card"
            >
              <span className={`action-card-label ${card.tagColor}`}>{card.tag}</span>
              <h3 className="action-card-title">{card.title}</h3>
              <p className="action-card-copy">{card.desc}</p>
              
              <div style={{marginTop: '20px', display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--blue)', fontWeight: 700, fontSize: '0.9rem'}}>
                 Launch Module <span>→</span>
              </div>
            </div>
          ))}
        </div>

        <section style={{marginTop: '40px'}}>
           <h2 className="section-title" style={{marginBottom: '20px'}}>Platform Statistics</h2>
           <div className="stats-grid">
              {stats.map((s) => (
                <div key={s.label} className="stat-card">
                  <p className="stat-label">{s.label}</p>
                  <p className="stat-value">{s.value}</p>
                </div>
              ))}
           </div>
        </section>
      </main>
    </div>
  )
}

export default Dashboard