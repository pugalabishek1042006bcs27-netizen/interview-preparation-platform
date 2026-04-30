import { useState } from 'react'
import { resumeAPI } from '../services/api'
import Navbar from '../components/Navbar'
import { toast } from 'react-hot-toast'

const ResumeAnalyzer = () => {
  const [resume, setResume] = useState(null)
  const [jobDescription, setJobDescription] = useState('')
  const [loading, setLoading] = useState(false)
  const [analysis, setAnalysis] = useState(null)

  const handleFileChange = (e) => {
    setResume(e.target.files[0])
  }

  const handleAnalyze = async () => {
    if (!resume || !jobDescription.trim()) {
      toast.error('Please upload your resume and enter a job description.')
      return
    }

    const formData = new FormData()
    formData.append('resume', resume)
    formData.append('jobDescription', jobDescription)

    try {
      setLoading(true)
      const response = await resumeAPI.analyze(formData)
      setAnalysis(response.data)
      toast.success('Analysis complete!')
    } catch (err) {
      console.error(err)
      toast.error('Something went wrong while analyzing your resume.')
    } finally {
      setLoading(false)
    }
  }

  const scoreValue = analysis?.atsScore ?? analysis?.matchScore ?? analysis?.score

  const renderInsightItems = (items) => {
    if (!Array.isArray(items) || items.length === 0) {
      return <p className="helper-text">No insights available.</p>
    }

    return (
      <ul style={{listStyle: 'none', padding: 0, display: 'grid', gap: '8px'}}>
        {items.map((item, index) => (
          <li key={index} style={{display: 'flex', gap: '8px', alignItems: 'start'}}>
            <span style={{color: 'var(--blue)', fontWeight: 800}}>•</span>
            <span className="section-copy" style={{fontSize: '0.9rem'}}>{item}</span>
          </li>
        ))}
      </ul>
    )
  }

  return (
    <div className="app-shell">
      <Navbar />
      <main className="page-content">
        <header className="page-header">
          <div className="page-heading">
            <span className="section-badge blue">Resume Intelligence</span>
            <h1 className="page-title">Resume Analyzer</h1>
            <p className="page-subtitle">
              Upload your resume and paste the job description to get AI-driven match scoring and keyword optimization insights.
            </p>
          </div>
        </header>

        <div style={{display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(350px, 1fr))', gap: '24px', alignItems: 'start'}}>
          <section className="panel">
            <h2 className="section-title" style={{marginBottom: '20px'}}>Input Data</h2>
            <div className="form-stack">
              <div className="field-group">
                <label className="field-label">Resume (PDF/DOCX)</label>
                <label className="file-dropzone" style={{cursor: 'pointer'}}>
                  <input type="file" accept=".pdf,.doc,.docx" onChange={handleFileChange} hidden />
                  <span style={{fontWeight: 700}}>{resume ? resume.name : 'Select Resume File'}</span>
                  <p className="helper-text">{resume ? 'File ready' : 'Click to browse'}</p>
                </label>
              </div>

              <div className="field-group">
                <label className="field-label">Target Job Description</label>
                <textarea
                  className="field-textarea"
                  style={{minHeight: '240px'}}
                  placeholder="Paste the job description here..."
                  value={jobDescription}
                  onChange={(e) => setJobDescription(e.target.value)}
                />
              </div>

              <button
                type="button"
                className="button-primary"
                onClick={handleAnalyze}
                disabled={loading}
              >
                {loading ? 'Processing...' : 'Analyze Match Quality'}
              </button>
            </div>
          </section>

          <section className="panel">
            <h2 className="section-title" style={{marginBottom: '20px'}}>Analysis Results</h2>
            
            {!analysis ? (
              <div className="empty-state-card" style={{padding: '60px 20px'}}>
                <p>Complete the input form to see your match score and detailed feedback.</p>
              </div>
            ) : (
              <div style={{display: 'grid', gap: '20px'}}>
                <div className="stat-card" style={{textAlign: 'center', padding: '30px', borderTop: '4px solid var(--blue)'}}>
                   <p className="stat-label">Match Score</p>
                   <p className="stat-value" style={{fontSize: '3.5rem', color: scoreValue >= 75 ? 'var(--green)' : 'var(--amber)'}}>{scoreValue}%</p>
                </div>

                <div className="feedback-card" style={{borderLeft: '4px solid var(--purple)'}}>
                   <span className="section-badge blue" style={{marginBottom: '10px'}}>AI Rewritten Summary</span>
                   <p className="section-copy" style={{fontStyle: 'italic'}}>"{analysis.rewrittenSummary}"</p>
                </div>

                <div style={{display: 'grid', gap: '16px'}}>
                  <div className="panel" style={{padding: '20px', border: 'none', background: 'var(--bg-soft)'}}>
                    <h4 style={{fontSize: '0.8rem', textTransform: 'uppercase', color: 'var(--green)', fontWeight: 800, marginBottom: '12px'}}>Key Strengths</h4>
                    {renderInsightItems(analysis.strengths)}
                  </div>

                  <div className="panel" style={{padding: '20px', border: 'none', background: 'var(--bg-soft)'}}>
                    <h4 style={{fontSize: '0.8rem', textTransform: 'uppercase', color: 'var(--red)', fontWeight: 800, marginBottom: '12px'}}>Critical Gaps</h4>
                    {renderInsightItems(analysis.missingKeywords)}
                  </div>

                  <div className="panel" style={{padding: '20px', border: 'none', background: 'var(--bg-soft)'}}>
                    <h4 style={{fontSize: '0.8rem', textTransform: 'uppercase', color: 'var(--amber)', fontWeight: 800, marginBottom: '12px'}}>Optimizations</h4>
                    {renderInsightItems(analysis.improvements)}
                  </div>
                </div>
              </div>
            )}
          </section>
        </div>
      </main>
    </div>
  )
}

export default ResumeAnalyzer