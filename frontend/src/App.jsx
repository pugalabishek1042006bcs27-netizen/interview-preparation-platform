import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { AuthProvider } from './context/AuthContext'
import Login from './pages/Login'
import Register from './pages/Register'
import ForgotPassword from './pages/ForgotPassword'
import Dashboard from './pages/Dashboard'
import QuestionBank from './pages/QuestionBank'
import MockInterview from './pages/MockInterview'
import CodePlayground from './pages/CodePlayground'
import CompanyInterview from './pages/CompanyInterview'
import ResumeAnalyzer from './pages/ResumeAnalyzer'
import ProgressTracker from './pages/ProgressTracker'
import DailyChallenge from './pages/DailyChallenge'
import LinkedInAnalyzer from './pages/LinkedInAnalyzer'
import ProtectedRoute from './components/ProtectedRoute'

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Toaster position="top-right" />
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/dashboard" element={
            <ProtectedRoute><Dashboard /></ProtectedRoute>
          } />
          <Route path="/questions" element={
            <ProtectedRoute><QuestionBank /></ProtectedRoute>
          } />
          <Route path="/interview" element={
            <ProtectedRoute><MockInterview /></ProtectedRoute>
          } />
          <Route path="/playground" element={
            <ProtectedRoute><CodePlayground /></ProtectedRoute>
          } />
          <Route path="/company" element={
            <ProtectedRoute><CompanyInterview /></ProtectedRoute>
          } />
          <Route path="/resume" element={
            <ProtectedRoute><ResumeAnalyzer /></ProtectedRoute>
          } />
          <Route path="/linkedin" element={
            <ProtectedRoute><LinkedInAnalyzer /></ProtectedRoute>
          } />
          <Route path="/progress" element={
            <ProtectedRoute><ProgressTracker /></ProtectedRoute>
          } />
          <Route path="/daily-challenge" element={
            <ProtectedRoute><DailyChallenge /></ProtectedRoute>
          } />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App