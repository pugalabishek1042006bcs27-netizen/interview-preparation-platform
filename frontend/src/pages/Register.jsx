import { useState } from 'react'
import API from '../services/api'
import { Link, useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'

const Register = () => {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [otp, setOtp] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [otpSent, setOtpSent] = useState(false)
  const [sendingOtp, setSendingOtp] = useState(false)
  const [timer, setTimer] = useState(0)
  const navigate = useNavigate()

  const startTimer = () => {
    setTimer(60)
    const interval = setInterval(() => {
      setTimer((prev) => {
        if (prev <= 1) {
          clearInterval(interval)
          return 0
        }
        return prev - 1
      })
    }, 1000)
  }

  const handleSendOtp = async () => {
    if (!email) {
      toast.error('Please enter your email first')
      return
    }
    setSendingOtp(true)
    try {
      await API.post('/auth/send-registration-otp', { email })
      toast.success('OTP sent to your email!')
      setOtpSent(true)
      startTimer()
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to send OTP')
    } finally {
      setSendingOtp(false)
    }
  }

  const handleRegister = async (e) => {
    e.preventDefault()
    setLoading(true)

    try {
      await API.post('/auth/register', {
        name,
        email,
        password,
        otp
      })

      toast.success('Registration successful! Please login.')
      navigate('/')
    } catch (error) {
      toast.error(error.response?.data?.message || 'Registration failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-shell">
      <div className="auth-card">
        <div className="auth-badge">Get started</div>

        <div className="auth-header">
          <h1 className="auth-title">Create your account</h1>
          <p className="auth-subtitle">
            Join the platform to practice interviews, analyze resumes, and build confidence with guided preparation tools.
          </p>
        </div>

        <form onSubmit={handleRegister} className="form-stack">
          <div className="field-group">
            <label className="field-label">Full name</label>
            <input
              type="text"
              className="field-input"
              placeholder="Enter your name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>

          <div className="field-group">
            <label className="field-label">Email address</label>
            <div className="input-with-button">
              <input
                type="email"
                className="field-input"
                placeholder="Enter your email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                disabled={otpSent && loading}
              />
              <button 
                type="button" 
                className="input-inline-button"
                onClick={handleSendOtp}
                disabled={sendingOtp || timer > 0}
              >
                {sendingOtp ? 'Sending...' : timer > 0 ? `Resend in ${timer}s` : otpSent ? 'Resend' : 'Verify'}
              </button>
            </div>
          </div>

          {otpSent && (
            <div className="field-group animate-slide-down">
              <label className="field-label">Verification Code (OTP)</label>
              <input
                type="text"
                className="field-input otp-field"
                placeholder="6-digit code"
                value={otp}
                onChange={(e) => setOtp(e.target.value)}
                required
                maxLength={6}
              />
              <p className="field-hint">Enter the 6-digit code sent to your email.</p>
            </div>
          )}

          <div className="field-group">
            <label className="field-label">Password</label>
            <input
              type="password"
              className="field-input"
              placeholder="Create a password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="button-primary" disabled={loading || !otpSent}>
            {loading ? 'Registering...' : 'Register'}
          </button>
        </form>

        <div className="auth-divider">Already registered?</div>

        <div className="auth-footer">
          <span>Have an account?</span>
          <Link to="/" className="auth-link">
            Sign in here
          </Link>
        </div>
      </div>
    </div>
  )
}

export default Register