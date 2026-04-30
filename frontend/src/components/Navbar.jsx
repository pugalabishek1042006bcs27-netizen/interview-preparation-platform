import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'

const Navbar = ({ darkMode = false }) => {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <nav className={`topbar ${darkMode ? 'topbar-dark' : ''}`}>
      <div className="topbar-inner">
        <div className="brand-block">
          <h1 className={`brand-title ${darkMode ? 'brand-title-dark' : ''}`}>
            Interview Prep Platform
          </h1>
          <p className={`brand-copy ${darkMode ? 'brand-copy-dark' : ''}`}>
            Practice smarter with focused interview workflows.
          </p>
        </div>

        <div className="nav-actions">
          <div className={`user-chip ${darkMode ? 'user-chip-dark' : ''}`}>
            <span>Welcome</span>
            <strong>{user?.name || 'Candidate'}</strong>
          </div>

          <button
            onClick={handleLogout}
            className={`button-secondary ${darkMode ? 'button-secondary-dark' : ''}`}
          >
            Logout
          </button>
        </div>
      </div>
    </nav>
  )
}

export default Navbar
