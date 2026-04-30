import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip as ChartTooltip,
  ResponsiveContainer,
  Radar,
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
} from 'recharts'

// --- Mock Data ---
const performanceHistory = [
  { name: 'Jan', avgScore: 45 },
  { name: 'Feb', avgScore: 52 },
  { name: 'Mar', avgScore: 58 },
  { name: 'Apr', avgScore: 65 },
  { name: 'May', avgScore: 78 },
  { name: 'Jun', avgScore: 85 },
]

const radarData = [
  { subject: 'DSA', A: 85, fullMark: 100 },
  { subject: 'Java Core', A: 92, fullMark: 100 },
  { subject: 'System Design', A: 65, fullMark: 100 },
  { subject: 'Databases', A: 78, fullMark: 100 },
  { subject: 'HR / Soft Skills', A: 90, fullMark: 100 },
  { subject: 'DevOps', A: 45, fullMark: 100 },
]

const recentActivity = [
  { date: 'Today', action: 'Completed TCS Interview Set', score: '82%', color: 'blue' },
  { date: 'Yesterday', action: 'Algorithm Practice (Trees)', score: '10/10', color: 'green' },
  { date: 'Mon', action: 'Failed Mock Interview Session', score: '40%', color: 'red' },
  { date: 'Sun', action: 'Resume Evaluated', score: '85 Match', color: 'blue' },
]

const ProgressTracker = () => {
  const navigate = useNavigate()

  return (
    <div className="app-shell">
      <Navbar />

      <main className="page-content">
        <header className="page-header">
          <div className="page-heading">
            <span className="section-badge blue">Performance Analytics</span>
            <h1 className="page-title">Analytics Overview</h1>
            <p className="page-subtitle">
              Track your interview performance, identify weak areas, and monitor your overall readiness for technical assessments over time.
            </p>
          </div>
        </header>

        <div className="stats-grid" style={{marginBottom: '30px'}}>
          <div className="stat-card">
            <p className="stat-label">Total Mock Interviews</p>
            <p className="stat-value">28</p>
            <p className="helper-text" style={{color: 'var(--green)'}}>+12% this month</p>
          </div>
          <div className="stat-card">
            <p className="stat-label">Average Score</p>
            <p className="stat-value">85%</p>
            <p className="helper-text" style={{color: 'var(--green)'}}>+8% improvement</p>
          </div>
          <div className="stat-card">
            <p className="stat-label">Questions Solved</p>
            <p className="stat-value">412</p>
          </div>
          <div className="stat-card">
            <p className="stat-label">Current Streak</p>
            <p className="stat-value">14 Days</p>
          </div>
        </div>

        <div style={{display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(450px, 1fr))', gap: '24px', marginBottom: '40px'}}>
          <section className="panel">
            <h3 className="section-title" style={{marginBottom: '20px'}}>Execution Readiness</h3>
            <div style={{height: '300px', width: '100%'}}>
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={performanceHistory}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border)" />
                  <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{fill: 'var(--text-muted)', fontSize: 12}} />
                  <YAxis axisLine={false} tickLine={false} tick={{fill: 'var(--text-muted)', fontSize: 12}} />
                  <ChartTooltip />
                  <Area type="monotone" dataKey="avgScore" stroke="var(--blue)" fill="var(--blue-soft)" strokeWidth={2} />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </section>

          <section className="panel">
            <h3 className="section-title" style={{marginBottom: '20px'}}>Skill Domain Analysis</h3>
            <div style={{height: '300px', width: '100%'}}>
              <ResponsiveContainer width="100%" height="100%">
                <RadarChart outerRadius="70%" data={radarData}>
                  <PolarGrid stroke="var(--border)" />
                  <PolarAngleAxis dataKey="subject" tick={{fill: 'var(--text-soft)', fontSize: 11, fontWeight: 600}} />
                  <Radar name="Proficiency" dataKey="A" stroke="var(--purple)" fill="var(--purple-soft)" fillOpacity={0.6} />
                </RadarChart>
              </ResponsiveContainer>
            </div>
          </section>
        </div>

        <section className="panel">
          <h3 className="section-title" style={{marginBottom: '20px'}}>Recent Tactical Activity</h3>
          <div className="questions-list">
            {recentActivity.map((act, i) => (
              <div key={i} className="question-card" style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px 24px'}}>
                <div style={{display: 'flex', alignItems: 'center', gap: '16px'}}>
                  <div className={`section-badge ${act.color}`} style={{width: '12px', height: '12px', padding: 0}} />
                  <div>
                    <p style={{fontWeight: 700, color: 'var(--text-main)'}}>{act.action}</p>
                    <p className="helper-text">{act.date}</p>
                  </div>
                </div>
                <div style={{fontWeight: 800, fontSize: '1.1rem', color: `var(--${act.color})`}}>
                  {act.score}
                </div>
              </div>
            ))}
          </div>
        </section>
      </main>
    </div>
  )
}

export default ProgressTracker
