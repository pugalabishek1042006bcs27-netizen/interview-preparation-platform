import { useState } from 'react'
import { linkedinAPI } from '../services/api'
import Navbar from '../components/Navbar'
import toast from 'react-hot-toast'

const LinkedInAnalyzer = () => {
  const [profileText, setProfileText] = useState('')
  const [jobDescription, setJobDescription] = useState('')
  const [file, setFile] = useState(null)
  const [loading, setLoading] = useState(false)
  const [analysis, setAnalysis] = useState(null)
  const [matchResult, setMatchResult] = useState(null)
  const [mode, setMode] = useState('text') // 'text' or 'file'
  const [activeTab, setActiveTab] = useState('audit') // 'audit' or 'match'

  const handleAnalyze = async () => {
    try {
      setLoading(true)
      let response
      if (mode === 'text') {
        if (!profileText.trim() || profileText.length < 50) {
          toast.error('Please paste profile text (min 50 chars).')
          setLoading(false)
          return
        }
        response = await linkedinAPI.analyzeText({ profileText })
      } else {
        if (!file) {
          toast.error('Please select a PDF file.')
          setLoading(false)
          return
        }
        const formData = new FormData()
        formData.append('file', file)
        response = await linkedinAPI.analyzeFile(formData)
      }
      setAnalysis(response.data)
      // Extract profile text from response if analyzing file for later job matching
      // Note: Realistically, the backend should return the text or we store it
      toast.success('Analysis complete!')
    } catch (err) {
      toast.error('Analysis failed. Try restarting the backend.')
    } finally {
      setLoading(false)
    }
  }

  const handleJobMatch = async () => {
    if (!profileText && !analysis) {
      toast.error('Please analyze your profile first.')
      return
    }
    if (!jobDescription.trim()) {
      toast.error('Please paste a Job Description.')
      return
    }

    try {
      setLoading(true)
      const response = await linkedinAPI.matchJob({
        profileText: profileText || 'Extracted LinkedIn Profile Data', // Fallback
        jobDescription
      })
      setMatchResult(response.data)
      toast.success('Job Match Analysis Ready!')
    } catch (err) {
      toast.error('Matching failed.')
    } finally {
      setLoading(false)
    }
  }

  const renderList = (items, type = 'blue') => {
    if (!items || items.length === 0) return <p className="helper-text">No data found.</p>
    return (
      <ul style={{listStyle: 'none', padding: 0, display: 'grid', gap: '8px'}}>
        {items.map((item, i) => (
          <li key={i} style={{display: 'flex', gap: '10px', alignItems: 'start'}}>
            <span style={{color: `var(--${type})`, fontWeight: 900}}>•</span>
            <span className="section-copy" style={{fontSize: '0.85rem'}}>{item}</span>
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
            <span className="section-badge blue">Professional Branding</span>
            <h1 className="page-title">LinkedIn Strategy Suite</h1>
          </div>
        </header>

        <div style={{display: 'grid', gridTemplateColumns: '1fr 1.2fr', gap: '32px', alignItems: 'start'}}>
          <div style={{display: 'grid', gap: '32px'}}>
            {/* Input Panel */}
            <section className="panel">
               <div style={{display: 'flex', gap: '12px', marginBottom: '24px', borderBottom: '1px solid var(--border)', paddingBottom: '16px'}}>
                  <button onClick={() => setMode('text')} className={`filter-chip ${mode === 'text' ? 'active' : ''}`}>Text Content</button>
                  <button onClick={() => setMode('file')} className={`filter-chip ${mode === 'file' ? 'active' : ''}`}>PDF Audit</button>
               </div>

               {mode === 'text' ? (
                 <textarea
                   className="field-textarea"
                   style={{minHeight: '280px'}}
                   placeholder="Paste your LinkedIn Summary and Experience here..."
                   value={profileText}
                   onChange={(e) => setProfileText(e.target.value)}
                 />
               ) : (
                 <label className="file-dropzone" style={{minHeight: '280px', cursor: 'pointer', display: 'flex', flexDirection: 'column', justifyContent: 'center', textAlign: 'center'}}>
                    <input type="file" accept=".pdf" onChange={(e) => setFile(e.target.files[0])} hidden />
                    <div style={{fontSize: '2.5rem', marginBottom: '12px'}}>📄</div>
                    <span style={{fontWeight: 700}}>{file ? file.name : 'Upload Profile.pdf'}</span>
                    <p className="helper-text">Save your profile to PDF on LinkedIn and upload here.</p>
                 </label>
               )}
               <button onClick={handleAnalyze} disabled={loading} className="button-primary" style={{marginTop: '24px', width: '100%'}}>
                 {loading ? 'Processing...' : 'Run Profile Audit'}
               </button>
            </section>

            {/* Job Matcher Input */}
            <section className="panel">
               <h3 className="section-title" style={{fontSize: '1.2rem', marginBottom: '12px'}}>Job Description Matcher</h3>
               <textarea
                 className="field-textarea"
                 style={{minHeight: '180px'}}
                 placeholder="Paste a Job Description from LinkedIn/Indeed to check your compatibility..."
                 value={jobDescription}
                 onChange={(e) => setJobDescription(e.target.value)}
               />
               <button onClick={handleJobMatch} disabled={loading || !analysis} className="button-secondary" style={{marginTop: '16px', width: '100%'}}>
                  Compare with Profile
               </button>
            </section>
          </div>

          <div style={{display: 'grid', gap: '32px'}}>
             {/* Results Panel */}
             <section className="panel" style={{minHeight: '600px'}}>
                <div style={{display: 'flex', gap: '24px', marginBottom: '24px'}}>
                   <button onClick={() => setActiveTab('audit')} style={{background: 'none', border: 'none', paddingBottom: '8px', borderBottom: activeTab === 'audit' ? '2px solid var(--blue)' : 'none', color: activeTab === 'audit' ? 'var(--blue)' : 'var(--text-soft)', fontWeight: 700, cursor: 'pointer'}}>Audit Report</button>
                   <button onClick={() => setActiveTab('match')} style={{background: 'none', border: 'none', paddingBottom: '8px', borderBottom: activeTab === 'match' ? '2px solid var(--blue)' : 'none', color: activeTab === 'match' ? 'var(--blue)' : 'var(--text-soft)', fontWeight: 700, cursor: 'pointer'}}>Job Match Analysis</button>
                </div>

                {!analysis && activeTab === 'audit' && (
                  <div className="empty-state-card" style={{padding: '100px 20px', textAlign: 'center'}}>
                     <p>Your optimization insights will appear here after analysis.</p>
                  </div>
                )}

                {analysis && activeTab === 'audit' && (
                   <div style={{display: 'grid', gap: '20px'}}>
                      <div className="stat-card" style={{textAlign: 'center', padding: '24px', borderTop: '4px solid var(--purple)'}}>
                         <p className="stat-label">Marketability</p>
                         <p className="stat-value" style={{fontSize: '3.5rem', color: 'var(--blue)'}}>{analysis.score}%</p>
                      </div>

                      <div style={{display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px'}}>
                         <div className="feedback-card" style={{margin: 0, padding: '16px'}}>
                            <h4 style={{fontSize: '0.7rem', color: 'var(--green)', textTransform: 'uppercase', marginBottom: '12px'}}>Working Sections</h4>
                            {renderList(analysis.strengths, 'green')}
                         </div>
                         <div className="feedback-card" style={{margin: 0, padding: '16px'}}>
                            <h4 style={{fontSize: '0.7rem', color: 'var(--amber)', textTransform: 'uppercase', marginBottom: '12px'}}>Critical Gaps</h4>
                            {renderList(analysis.improvements, 'amber')}
                         </div>
                      </div>

                      <div className="feedback-card" style={{margin: 0, borderLeft: '4px solid var(--blue)'}}>
                         <h4 style={{fontSize: '0.7rem', color: 'var(--blue)', textTransform: 'uppercase', marginBottom: '12px'}}>Suggested Headlines</h4>
                         <div style={{display: 'grid', gap: '8px'}}>
                            {analysis.headlines?.map((h, i) => (
                              <div key={i} style={{padding: '10px', background: 'var(--bg-soft)', borderRadius: '8px', fontSize: '0.9rem', border: '1px solid var(--border)'}}>
                                {h}
                              </div>
                            ))}
                         </div>
                      </div>

                      <div className="feedback-card" style={{margin: 0, background: 'var(--bg-soft)', border: 'none'}}>
                         <h4 style={{fontSize: '0.7rem', color: 'var(--text-soft)', textTransform: 'uppercase', marginBottom: '12px'}}>Keyword Recommendations</h4>
                         <div style={{display: 'flex', flexWrap: 'wrap', gap: '8px'}}>
                            {analysis.missingKeywords?.map((kw, i) => (
                              <span key={i} style={{padding: '4px 12px', background: 'var(--red-soft)', color: 'var(--red)', borderRadius: '20px', fontSize: '0.75rem', fontWeight: 700}}>
                                 {kw}
                              </span>
                            ))}
                         </div>
                      </div>
                   </div>
                )}

                {!matchResult && activeTab === 'match' && (
                  <div className="empty-state-card" style={{padding: '100px 20px', textAlign: 'center'}}>
                     <p>Paste a job description on the left and click "Compare" to see your match score.</p>
                  </div>
                )}

                {matchResult && activeTab === 'match' && (
                  <div style={{display: 'grid', gap: '20px'}}>
                     <div className="stat-card" style={{textAlign: 'center', padding: '24px', borderTop: '4px solid var(--blue)'}}>
                        <p className="stat-label">Compatibility Score</p>
                        <p className="stat-value" style={{fontSize: '3.5rem', color: 'var(--blue)'}}>{matchResult.matchScore}%</p>
                        <p style={{marginTop: '12px', fontWeight: 700, color: matchResult.matchScore > 70 ? 'var(--green)' : 'var(--amber)'}}>{matchResult.advice}</p>
                     </div>

                     <div className="feedback-card" style={{margin: 0}}>
                        <h4 style={{fontSize: '0.7rem', color: 'var(--green)', textTransform: 'uppercase', marginBottom: '12px'}}>Skills You Have</h4>
                        <div style={{display: 'flex', flexWrap: 'wrap', gap: '8px'}}>
                           {matchResult.matchedKeywords?.map((kw, i) => (
                             <span key={i} style={{padding: '4px 12px', background: 'var(--green-soft)', color: 'var(--green)', borderRadius: '20px', fontSize: '0.75rem', fontWeight: 700}}>
                                {kw}
                             </span>
                           ))}
                        </div>
                     </div>

                     <div className="feedback-card" style={{margin: 0}}>
                        <h4 style={{fontSize: '0.7rem', color: 'var(--red)', textTransform: 'uppercase', marginBottom: '12px'}}>Skills You are Missing</h4>
                        <div style={{display: 'flex', flexWrap: 'wrap', gap: '8px'}}>
                           {matchResult.missingKeywords?.map((kw, i) => (
                             <span key={i} style={{padding: '4px 12px', background: 'var(--red-soft)', color: 'var(--red)', borderRadius: '20px', fontSize: '0.75rem', fontWeight: 700}}>
                                {kw}
                             </span>
                           ))}
                        </div>
                     </div>
                  </div>
                )}
             </section>
          </div>
        </div>
      </main>
    </div>
  )
}

export default LinkedInAnalyzer
