import { useState, useEffect } from 'react'
import { dailyChallengeAPI } from '../services/api'
import Navbar from '../components/Navbar'
import { toast } from 'react-hot-toast'

const DailyChallenge = () => {
  const [challenge, setChallenge] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [showAnswer, setShowAnswer] = useState(false)
  const [completedToday, setCompletedToday] = useState(false)
  const [streak, setStreak] = useState(0)
  const [longestStreak, setLongestStreak] = useState(0)
  const [userAnswer, setUserAnswer] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    fetchDailyChallenge()
  }, [])

  const fetchDailyChallenge = async () => {
    try {
      setLoading(true)
      const response = await dailyChallengeAPI.getDailyChallenge()
      setChallenge(response.data.question)
      setStreak(response.data.userStreak)
      setLongestStreak(response.data.longestStreak)
      setCompletedToday(response.data.isCompletedToday)
      setError(null)
    } catch (err) {
      console.error('Error fetching daily challenge:', err)
      setError('Failed to load daily challenge. Please try again later.')
    } finally {
      setLoading(false)
    }
  }

  const handleComplete = async () => {
    if (!userAnswer.trim() || userAnswer.trim().length < 20) {
      toast.error('Please provide a more detailed answer (at least 20 characters).')
      return
    }

    try {
      setSubmitting(true)
      const response = await dailyChallengeAPI.completeDailyChallenge({ answer: userAnswer })
      setCompletedToday(true)
      setStreak(response.data.currentStreak)
      setLongestStreak(response.data.longestStreak)
      setShowAnswer(true)
      toast.success('Excellent! Your streak has increased.')
    } catch (err) {
      const msg = err.response?.data?.message || 'Answer evaluation failed. Try to be more specific.'
      toast.error(msg)
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  return (
    <div className="app-shell">
      <Navbar />
      
      <main className="page-content">
        <header className="page-header">
          <div className="page-heading">
            <span className="section-badge blue">Habit Builder</span>
            <h1 className="page-title">Daily Challenge</h1>
            <p className="page-subtitle">
              Solve one problem every day to build consistent interview readiness. 
              The challenge resets every 24 hours.
            </p>
          </div>
          
          <div className="stats-grid" style={{minWidth: '300px'}}>
            <div className="stat-card">
              <p className="stat-label">Current Streak</p>
              <div style={{display: 'flex', alignItems: 'center', gap: '8px'}}>
                 <span className="stat-value">{streak}</span>
                 <span style={{fontSize: '1.5rem'}}>🔥</span>
              </div>
            </div>
            <div className="stat-card">
              <p className="stat-label">Best Streak</p>
              <p className="stat-value">{longestStreak}</p>
            </div>
          </div>
        </header>

        {error ? (
          <div className="empty-state-card">
            <p>{error}</p>
            <button 
              onClick={fetchDailyChallenge}
              className="button-secondary"
              style={{marginTop: '20px'}}
            >
              Try Again
            </button>
          </div>
        ) : (
          <div style={{display: 'grid', gap: '24px'}}>
            <section className="panel">
              <div className="panel-toolbar">
                <div>
                   <h2 className="section-title">{challenge?.title || challenge?.question}</h2>
                   <div className="filter-row" style={{marginTop: '10px'}}>
                      <span className="filter-chip active">{challenge?.topic}</span>
                      <span className="filter-chip">{challenge?.difficulty}</span>
                   </div>
                </div>
              </div>
              
              <div className="section-copy" style={{marginBottom: '24px', fontSize: '1.1rem'}}>
                 {challenge?.description || "Explain your approach to solve the problem stated above."}
              </div>

              {!completedToday ? (
                <div className="form-stack">
                  <div className="field-group">
                    <label className="field-label">Your Solution / Explanation</label>
                    <textarea
                      value={userAnswer}
                      onChange={(e) => setUserAnswer(e.target.value)}
                      placeholder="Type your detailed explanation here. Key technical terms will be used for verification..."
                      className="field-textarea"
                    />
                    <p className="helper-text">Minimum 20 characters required for verification.</p>
                  </div>
                  
                  <div style={{display: 'flex', gap: '12px'}}>
                    <button
                      onClick={handleComplete}
                      disabled={submitting}
                      className="button-primary"
                      style={{width: 'auto', paddingInline: '40px'}}
                    >
                      {submitting ? 'Verifying...' : 'Submit Solution'}
                    </button>
                    
                    <button
                      onClick={() => setShowAnswer(!showAnswer)}
                      className="button-secondary"
                    >
                      {showAnswer ? 'Hide Sample' : 'View Sample Answer'}
                    </button>
                  </div>
                </div>
              ) : (
                <div>
                  <div className="feedback-card green" style={{marginBottom: '24px', background: 'rgba(34, 197, 94, 0.05)'}}>
                    <div style={{display: 'flex', alignItems: 'center', gap: '12px'}}>
                       <span style={{fontSize: '1.2rem'}}>✅</span>
                       <div>
                          <p style={{fontWeight: 700, color: '#166534'}}>Challenge Completed</p>
                          <p className="section-copy">Great job! You've successfully completed today's challenge and maintained your streak.</p>
                       </div>
                    </div>
                  </div>
                  
                  <button
                    onClick={() => setShowAnswer(!showAnswer)}
                    className="button-secondary"
                  >
                    {showAnswer ? 'Hide Sample' : 'View Sample Answer'}
                  </button>
                </div>
              )}
            </section>

            {showAnswer && (
              <motion.section 
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                className="panel" 
                style={{borderColor: 'var(--border-strong)'}}
              >
                <h3 className="section-title" style={{marginBottom: '16px'}}>Sample Solution</h3>
                <div className="section-copy" style={{whiteSpace: 'pre-wrap', background: 'var(--bg-soft)', padding: '20px', borderRadius: 'var(--radius-sm)'}}>
                   {challenge?.answer}
                </div>
              </motion.section>
            )}
          </div>
        )}
      </main>
    </div>
  )
}

export default DailyChallenge
