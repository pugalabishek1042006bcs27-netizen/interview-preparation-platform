import { useState } from 'react'
import API from '../services/api'
import { Link, useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'

const ForgotPassword = () => {
    const [step, setStep] = useState(1)
    const [email, setEmail] = useState('')
    const [token, setToken] = useState('')
    const [newPassword, setNewPassword] = useState('')
    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()

    const handleRequestReset = async (e) => {
        e.preventDefault()
        setLoading(true)

        try {
            const { data } = await API.post('/auth/forgot-password', { email })
            
            // Backend no longer returns a demoCode. It actually sends a real email.
            toast.success(data.message || 'Check your inbox! We have sent an email containing your 6-digit secure code.')
            setStep(2)
        } catch (error) {
            toast.error(error.response?.data?.message || 'Failed to request password reset')
        } finally {
            setLoading(false)
        }
    }

    const handleResetPassword = async (e) => {
        e.preventDefault()
        setLoading(true)

        try {
            const { data } = await API.post('/auth/reset-password', {
                email,
                token,
                newPassword
            })
            toast.success(data.message || 'Password reset successfully!')
            navigate('/')
        } catch (error) {
            toast.error(error.response?.data?.message || 'Invalid or expired OTP')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="auth-shell">
            <div className="auth-card">
                <div className="auth-badge">Account Recovery</div>

                <div className="auth-header">
                    <h1 className="auth-title">Reset your password</h1>
                    <p className="auth-subtitle">
                        {step === 1 
                            ? "Enter the email associated with your account and we'll send a secure six-digit reset code."
                            : "Enter the secure code sent to your email and choose a strong new password."}
                    </p>
                </div>

                {step === 1 ? (
                    <form onSubmit={handleRequestReset} className="form-stack">
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

                        <button type="submit" className="button-primary" disabled={loading}>
                            {loading ? 'Processing...' : 'Send Reset Link'}
                        </button>
                    </form>
                ) : (
                    <form onSubmit={handleResetPassword} className="form-stack">
                        <div className="field-group">
                            <label className="field-label">6-Digit Secure Code</label>
                            <input
                                type="text"
                                className="field-input"
                                placeholder="Enter 6-digit code"
                                value={token}
                                onChange={(e) => setToken(e.target.value)}
                                required
                                maxLength={6}
                            />
                        </div>

                        <div className="field-group">
                            <label className="field-label">New Password</label>
                            <input
                                type="password"
                                className="field-input"
                                placeholder="Enter your new password"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                required
                                minLength={6}
                            />
                        </div>

                        <button type="submit" className="button-primary" disabled={loading}>
                            {loading ? 'Verifying...' : 'Reset Password'}
                        </button>
                    </form>
                )}

                <div className="auth-divider">Or</div>

                <div className="auth-footer">
                    <Link to="/" className="auth-link">
                        Return to login screen
                    </Link>
                </div>
            </div>
        </div>
    )
}

export default ForgotPassword
