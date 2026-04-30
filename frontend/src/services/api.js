import axios from 'axios'

const API = axios.create({
  baseURL: 'http://localhost:8082/api'
})

API.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export const authAPI = {
  register: (data) => API.post('/auth/register', data),
  login: (data) => API.post('/auth/login', data),
}

export const questionAPI = {
  getAll: (params) => API.get('/questions', { params }),
}

export const interviewAPI = {
  start: (data) => API.post('/interview/start', data),
  answer: (data) => API.post('/interview/answer', data),
}

export const resumeAPI = {
  analyze: (formData) => API.post('/resume/analyze', formData),
}

export const dailyChallengeAPI = {
  getDailyChallenge: () => API.get('/daily-challenge'),
  completeDailyChallenge: (data) => API.post('/daily-challenge/complete', data),
}

export const linkedinAPI = {
  analyzeText: (data) => API.post('/linkedin/analyze-text', data),
  analyzeFile: (formData) => API.post('/linkedin/analyze-file', formData),
  matchJob: (data) => API.post('/linkedin/match-job', data),
}

export default API
