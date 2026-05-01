import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import API from '../services/api'
import { Link, useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'

const Login = () => {
  const { login } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const handleLogin = async (e) => {
    e.preventDefault()
    setLoading(true)

    try {
      const { data } = await API.post('/auth/login', {
        email,
        password
      })

      login(data)
      toast.success('Login successful!')
      navigate('/dashboard')
    } catch (error) {
      toast.error(error.response?.data?.message || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-shell">
      <div className="auth-card">
        <div className="auth-badge">Welcome back</div>

        <div className="auth-header">
          <h1 className="auth-title">Sign in to your account</h1>
          <p className="auth-subtitle">
            Continue your interview preparation with practice sessions, question banks, and AI-guided feedback.
          </p>
        </div>

        <form onSubmit={handleLogin} className="form-stack">
          <div className="field-group">
            <label className="field-label">Email address</label>
            <input
              type="email"
              className="field-input"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="field-group">
            <label className="field-label">Password</label>
            <input
              type="password"
              className="field-input"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <div style={{ textAlign: 'right', marginTop: '8px' }}>
              <Link to="/forgot-password" style={{ fontSize: '12px', color: '#6366f1', textDecoration: 'none', fontWeight: '500' }}>
                Forgot your password?
              </Link>
            </div>
          </div>

          <button type="submit" className="button-primary" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        <div className="auth-divider">New to the platform?</div>

        <div className="auth-footer">
          <span>Don't have an account?</span>
          <Link to="/register" className="auth-link">
            Create one here
          </Link>
        </div>
      </div>
    </div>
  )
}

export default Login