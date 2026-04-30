import { useState, useEffect, useRef } from 'react'
import axios from 'axios'
import Navbar from '../components/Navbar'
import toast from 'react-hot-toast'

const topics = [
  'JavaScript', 'React', 'Java', 'Python', 'C++', 'DSA',
  'Operating Systems', 'Computer Networks', 'Database',
  'Spring Boot', 'Machine Learning', 'System Design',
  'Web', 'Backend', 'Behavioral'
]

const MockInterview = () => {
  const [step, setStep] = useState('select')
  const [selectedTopic, setSelectedTopic] = useState('JavaScript')
  const [question, setQuestion] = useState('')
  const [answer, setAnswer] = useState('')
  const [interimText, setInterimText] = useState('')
  const [feedback, setFeedback] = useState(null)
  const [loading, setLoading] = useState(false)
  const [isListening, setIsListening] = useState(false)
  const [lang, setLang] = useState('en-US')
  const [volume, setVolume] = useState(0)
  
  const recognitionRef = useRef(null)
  const audioContextRef = useRef(null)
  const analyserRef = useRef(null)
  const streamRef = useRef(null)
  const animationFrameRef = useRef(null)

  useEffect(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
    if (SpeechRecognition) {
      const recognition = new SpeechRecognition()
      recognition.continuous = true
      recognition.interimResults = true
      recognition.lang = lang

      recognition.onresult = (event) => {
        let finalTranscript = ''
        let currentInterim = ''
        for (let i = event.resultIndex; i < event.results.length; i++) {
          if (event.results[i].isFinal) finalTranscript += event.results[i][0].transcript
          else currentInterim += event.results[i][0].transcript
        }
        setInterimText(currentInterim)
        if (finalTranscript) {
          setAnswer((prev) => {
             const cleanPrev = prev.trim()
             return cleanPrev === '' ? finalTranscript : cleanPrev + ' ' + finalTranscript
          })
          setInterimText('')
        }
      }

      recognition.onerror = () => setIsListening(false)
      recognition.onend = () => setIsListening(false)
      recognitionRef.current = recognition
    }

    return () => {
      stopAudioMonitoring()
    }
  }, [lang])

  const startAudioMonitoring = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
      streamRef.current = stream
      const audioContext = new (window.AudioContext || window.webkitAudioContext)()
      const analyser = audioContext.createAnalyser()
      const source = audioContext.createMediaStreamSource(stream)
      source.connect(analyser)
      analyser.fftSize = 256
      analyserRef.current = analyser
      audioContextRef.current = audioContext

      const bufferLength = analyser.frequencyBinCount
      const dataArray = new Uint8Array(bufferLength)

      const updateVolume = () => {
        analyser.getByteFrequencyData(dataArray)
        let sum = 0
        for (let i = 0; i < bufferLength; i++) sum += dataArray[i]
        const average = sum / bufferLength
        setVolume(average)
        animationFrameRef.current = requestAnimationFrame(updateVolume)
      }
      updateVolume()
    } catch (err) {
      console.error('Audio monitor failed:', err)
    }
  }

  const stopAudioMonitoring = () => {
    if (animationFrameRef.current) cancelAnimationFrame(animationFrameRef.current)
    if (streamRef.current) streamRef.current.getTracks().forEach(track => track.stop())
    if (audioContextRef.current) audioContextRef.current.close()
    setVolume(0)
  }

  const speakQuestion = (text) => {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel()
      const utterance = new SpeechSynthesisUtterance(text)
      utterance.rate = 0.95
      window.speechSynthesis.speak(utterance)
    }
  }

  const toggleListening = () => {
    if (isListening) {
      recognitionRef.current.stop()
      stopAudioMonitoring()
    } else {
      try {
        recognitionRef.current.start()
        setIsListening(true)
        startAudioMonitoring()
        toast.success('Microphone active.')
      } catch (err) {
        console.error(err)
      }
    }
  }

  const startInterview = async () => {
    try {
      setLoading(true)
      const response = await axios.post('http://localhost:8082/api/mock/start', { topic: selectedTopic })
      const qText = response.data?.question || 'Tell me about yourself.'
      setQuestion(qText)
      setAnswer('')
      setFeedback(null)
      setStep('answer')
      setTimeout(() => speakQuestion(qText), 500)
    } catch (error) {
      toast.error('Failed to start interview.')
    } finally {
      setLoading(false)
    }
  }

  const submitAnswer = async () => {
    if (isListening) {
      recognitionRef.current.stop()
      stopAudioMonitoring()
    }
    try {
      setLoading(true)
      const response = await axios.post('http://localhost:8082/api/mock/feedback', {
        topic: selectedTopic,
        question,
        answer
      })
      setFeedback(response.data?.feedback || 'No feedback received.')
      setStep('feedback')
    } catch (error) {
      toast.error('Evaluation failed.')
    } finally {
      setLoading(false)
    }
  }

  const resetInterview = () => {
    if (isListening) {
      recognitionRef.current.stop()
      stopAudioMonitoring()
    }
    window.speechSynthesis.cancel()
    setStep('select')
    setQuestion('')
    setAnswer('')
    setInterimText('')
    setFeedback(null)
  }

  return (
    <div className="app-shell">
      <Navbar />
      <main className="page-content">
        <header className="page-header">
          <div className="page-heading">
            <span className="section-badge blue">Interactive AI Session</span>
            <h1 className="page-title">Mock Interview</h1>
          </div>
          {step === 'answer' && (
            <div className="filter-row">
               <label className="field-label" style={{margin: 0, fontSize: '12px'}}>Dialect:</label>
               <select value={lang} onChange={(e) => setLang(e.target.value)} className="filter-chip" style={{background: 'var(--bg-soft)', border: '1px solid var(--border)', padding: '4px 12px', outline: 'none'}}>
                  <option value="en-US">English (US)</option>
                  <option value="en-IN">English (India)</option>
                  <option value="en-GB">English (UK)</option>
               </select>
            </div>
          )}
        </header>

        {step === 'select' && (
          <section className="panel">
            <h2 className="section-title" style={{marginBottom: '20px'}}>Choose Topic</h2>
            <div className="filter-row" style={{flexWrap: 'wrap', gap: '10px', marginBottom: '32px'}}>
              {topics.map((topic) => (
                <button key={topic} className={`filter-chip ${selectedTopic === topic ? 'active' : ''}`} onClick={() => setSelectedTopic(topic)}>
                  {topic}
                </button>
              ))}
            </div>
            <button className="button-primary" onClick={startInterview} disabled={loading} style={{width: 'auto', paddingInline: '40px'}}>
              {loading ? 'Initializing...' : 'Start Session'}
            </button>
          </section>
        )}

        {step === 'answer' && (
          <section className="panel">
            <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: '32px'}}>
               <div style={{flex: 1}}>
                  <span className="section-badge blue" style={{marginBottom: '12px'}}>AI Interviewer</span>
                  <h2 className="section-title" style={{fontSize: '1.8rem', lineHeight: '1.3'}}>{question}</h2>
               </div>
               <button onClick={() => speakQuestion(question)} className="button-secondary" style={{padding: '12px', borderRadius: '50%'}}>🔊</button>
            </div>

            <div className="field-group">
              <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px'}}>
                <div style={{display: 'flex', alignItems: 'center', gap: '16px'}}>
                   <label className="field-label" style={{margin: 0}}>Your Response</label>
                   {isListening && (
                     <div style={{display: 'flex', gap: '3px', alignItems: 'center', height: '14px'}}>
                        {[...Array(6)].map((_, i) => (
                          <div 
                             key={i} 
                             style={{
                               width: '3px', 
                               height: `${Math.max(4, (volume / 2) * (1 - Math.abs(i - 2.5) / 3))}px`, 
                               background: 'var(--blue)', 
                               borderRadius: '2px',
                               transition: 'height 0.1s ease'
                             }} 
                          />
                        ))}
                     </div>
                   )}
                </div>
                <button 
                   onClick={toggleListening} 
                   className={`button-secondary ${isListening ? 'text-red animate-pulse' : ''}`}
                   style={{display: 'flex', alignItems: 'center', gap: '8px', minWidth: '160px'}}
                >
                  <span>{isListening ? '🛑 Stop' : '🎤 Start Voice'}</span>
                </button>
              </div>
              
              <div style={{position: 'relative'}}>
                <textarea
                  className="field-textarea"
                  rows="10"
                  placeholder="Speak your answer clearly..."
                  value={answer}
                  onChange={(e) => setAnswer(e.target.value)}
                  style={{border: isListening ? '2px solid var(--blue)' : '', transition: 'all 0.3s ease'}}
                />
                {interimText && (
                  <div style={{position: 'absolute', bottom: '12px', left: '20px', color: 'var(--text-muted)', fontStyle: 'italic', fontSize: '14px'}}>
                    {interimText}...
                  </div>
                )}
              </div>
            </div>

            <div style={{display: 'flex', gap: '16px', marginTop: '32px'}}>
              <button className="button-secondary" onClick={resetInterview}>Back</button>
              <button className="button-primary" onClick={submitAnswer} disabled={loading || (!answer.trim() && !interimText)}>
                {loading ? 'Analyzing...' : 'Finish & Submit'}
              </button>
            </div>
          </section>
        )}

        {step === 'feedback' && (
          <section className="panel">
            <h2 className="section-title" style={{marginBottom: '24px'}}>Session Analysis</h2>
            <div className="feedback-card" style={{marginBottom: '16px'}}><span className="section-badge blue">Question</span><p style={{fontWeight: 700, marginTop: '8px'}}>{question}</p></div>
            <div className="feedback-card" style={{marginBottom: '16px', borderLeft: '4px solid var(--blue)'}}><span className="section-badge blue">Transcript</span><p style={{marginTop: '8px'}}>{answer || '(No response captured)'}</p></div>
            <div className="feedback-card" style={{borderLeft: '4px solid var(--green)'}}><span className="section-badge green">AI Insights</span><p style={{marginTop: '8px', whiteSpace: 'pre-wrap'}}>{feedback}</p></div>
            <div style={{display: 'flex', gap: '16px', marginTop: '40px'}}>
              <button className="button-secondary" onClick={() => setStep('answer')}>Retry</button>
              <button className="button-primary" onClick={resetInterview}>New Session</button>
            </div>
          </section>
        )}
      </main>
    </div>
  )
}

export default MockInterview