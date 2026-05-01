import { useMemo, useState } from 'react'
import API from '../services/api'
import Navbar from '../components/Navbar'

const questions = [
  {
    id: 1,
    title: 'What is the difference between var, let, and const in JavaScript?',
    answer:
      'var is function-scoped and can be re-declared. let and const are block-scoped. const cannot be reassigned after declaration, while let can be updated.',
    topic: 'JavaScript',
    difficulty: 'Easy',
    tags: ['Scope', 'Variables', 'Basics']
  },
  {
    id: 2,
    title: 'Explain closures in JavaScript with a practical example.',
    answer:
      'A closure is created when an inner function retains access to variables from its outer lexical scope, even after the outer function has returned. This is often used for data privacy and function factories.',
    topic: 'JavaScript',
    difficulty: 'Medium',
    tags: ['Closures', 'Functions', 'Lexical Scope']
  },
  {
    id: 3,
    title: 'What is the virtual DOM in React and why is it useful?',
    answer:
      'The virtual DOM is a lightweight in-memory representation of the real DOM. React compares changes in the virtual DOM and updates only the necessary parts of the real DOM, improving performance.',
    topic: 'React',
    difficulty: 'Easy',
    tags: ['React', 'DOM', 'Performance']
  },
  {
    id: 4,
    title: 'What are React hooks and why were they introduced?',
    answer:
      'Hooks are functions like useState and useEffect that let functional components manage state and side effects. They were introduced to make component logic more reusable and reduce reliance on class components.',
    topic: 'React',
    difficulty: 'Medium',
    tags: ['Hooks', 'State', 'Lifecycle']
  },
  {
    id: 5,
    title: 'Explain event bubbling and event capturing in the DOM.',
    answer:
      'Event capturing travels from the root to the target element, while event bubbling moves from the target back up to the root. JavaScript event listeners can participate in either phase.',
    topic: 'Web',
    difficulty: 'Medium',
    tags: ['DOM', 'Events', 'Browser']
  },
  {
    id: 6,
    title: 'What is REST and what are common HTTP methods used in REST APIs?',
    answer:
      'REST is an architectural style for building web APIs around resources. Common HTTP methods are GET for reading, POST for creating, PUT or PATCH for updating, and DELETE for removing resources.',
    topic: 'Backend',
    difficulty: 'Easy',
    tags: ['REST', 'HTTP', 'APIs']
  },
  {
    id: 7,
    title: 'How does async/await work in JavaScript?',
    answer:
      'async functions return promises. await pauses execution inside an async function until a promise settles, making asynchronous code easier to read and maintain.',
    topic: 'JavaScript',
    difficulty: 'Easy',
    tags: ['Async', 'Promises', 'JavaScript']
  },
  {
    id: 8,
    title: 'What is memoization and where would you use it in React?',
    answer:
      'Memoization caches the result of expensive calculations so they can be reused. In React it is often used with useMemo, useCallback, or React.memo to prevent unnecessary recalculations or re-renders.',
    topic: 'React',
    difficulty: 'Hard',
    tags: ['Optimization', 'Memoization', 'Performance']
  },
  {
    id: 9,
    title: 'What is database indexing and what trade-offs does it introduce?',
    answer:
      'Indexes speed up read operations by providing faster lookup paths, but they add storage cost and can slow down write operations because indexes also need to be updated.',
    topic: 'Database',
    difficulty: 'Medium',
    tags: ['SQL', 'Indexing', 'Performance']
  }
]

const topicOptions = ['All', ...new Set(questions.map((question) => question.topic))]
const aiTopics = ['JavaScript', 'React', 'Java', 'Python', 'C++', 'DSA', 'Operating Systems', 'Computer Networks', 'Database', 'Spring Boot', 'Machine Learning', 'System Design', 'Web', 'Backend']
const difficultyOptions = ['All', 'Easy', 'Medium', 'Hard']

const QuestionBank = () => {
  const [selectedTopic, setSelectedTopic] = useState('All')
  const [selectedDifficulty, setSelectedDifficulty] = useState('All')
  const [expandedQuestion, setExpandedQuestion] = useState(null)
  const [aiTopic, setAiTopic] = useState('JavaScript')
  const [generatedQuestion, setGeneratedQuestion] = useState(null)
  const [showAiAnswer, setShowAiAnswer] = useState(false)
  const [loading, setLoading] = useState(false)

  const filteredQuestions = useMemo(() => {
    return questions.filter((question) => {
      const matchesTopic =
        selectedTopic === 'All' || question.topic === selectedTopic
      const matchesDifficulty =
        selectedDifficulty === 'All' ||
        question.difficulty === selectedDifficulty

      return matchesTopic && matchesDifficulty
    })
  }, [selectedTopic, selectedDifficulty])

  const handleGenerateQuestion = async () => {
    try {
      setLoading(true)
      setShowAiAnswer(false)

      const response = await API.post(
        '/questions/generate',
        {
          topic: aiTopic,
          difficulty: selectedDifficulty === 'All' ? 'Medium' : selectedDifficulty
        }
      )

      const payload = response.data
      setGeneratedQuestion(
        payload?.question && payload?.answer
          ? payload
          : {
              question: payload?.question || payload?.prompt || payload?.data?.question || 'No question generated',
              answer: payload?.answer || payload?.sampleAnswer || payload?.data?.answer || 'No answer available yet.'
            }
      )
    } catch (error) {
      console.error('Error generating question:', error)
      setGeneratedQuestion({
        question: 'Unable to generate a new AI question right now.',
        answer: 'Please try again in a moment.'
      })
    } finally {
      setLoading(false)
    }
  }

  const toggleQuestion = (questionId) => {
    setExpandedQuestion((current) => (current === questionId ? null : questionId))
  }

  return (
    <div className="app-shell">
      <Navbar />
      <main className="page-content">
        <header className="page-header">
          <div className="page-heading">
            <span className="section-badge">Interview practice</span>
            <h1 className="page-title">Question Bank</h1>
            <p className="page-subtitle">
              Explore common interview questions, filter by topic and difficulty,
              and generate fresh AI prompts when you want more practice.
            </p>
          </div>
        </header>

        <section className="panel">
          <div className="filter-group">
            <div>
              <h2 className="filter-title">Filter by topic</h2>
              <div className="filter-row">
                {topicOptions.map((topic) => (
                  <button
                    key={topic}
                    type="button"
                    className={
                      selectedTopic === topic ? 'filter-chip active' : 'filter-chip'
                    }
                    onClick={() => setSelectedTopic(topic)}
                  >
                    {topic}
                  </button>
                ))}
              </div>
            </div>

            <div>
              <h2 className="filter-title">Filter by difficulty</h2>
              <div className="filter-row">
                {difficultyOptions.map((difficulty) => (
                  <button
                    key={difficulty}
                    type="button"
                    className={
                      selectedDifficulty === difficulty
                        ? 'filter-chip active'
                        : 'filter-chip'
                    }
                    onClick={() => setSelectedDifficulty(difficulty)}
                  >
                    {difficulty}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </section>

        <section className="panel">
          <div className="page-header">
            <div className="page-heading">
              <h2 className="section-title">AI question generator</h2>
              <p className="section-copy">
                Create an extra practice prompt tailored to your current topic focus.
              </p>
            </div>
            <div className="filter-row" style={{flexWrap: 'wrap'}}>
              {aiTopics.map((topic) => (
                  <button
                    key={topic}
                    type="button"
                    className={aiTopic === topic ? 'filter-chip active' : 'filter-chip'}
                    onClick={() => setAiTopic(topic)}
                  >
                    {topic}
                  </button>
                ))}
            </div>
          </div>

          <div className="filter-row">
            <button
              type="button"
              className="button-primary"
              onClick={handleGenerateQuestion}
              disabled={loading}
            >
              {loading ? 'Generating...' : 'Generate AI Question'}
            </button>
          </div>

          {generatedQuestion && (
            <div className="ai-question-card">
              <div className="question-top">
                <h3 className="question-title">{generatedQuestion.question}</h3>
                <div className="tag-row">
                  <span className="tag">{aiTopic}</span>
                  <span className="tag">
                    {selectedDifficulty === 'All' ? 'Medium' : selectedDifficulty}
                  </span>
                </div>
              </div>

              <button
                type="button"
                className="ai-answer-toggle"
                onClick={() => setShowAiAnswer((current) => !current)}
              >
                {showAiAnswer ? 'Hide sample answer' : 'Show sample answer'}
              </button>

              {showAiAnswer && (
                <div className="ai-answer-block">
                  <span className="answer-label">Sample answer</span>
                  <p className="question-answer">{generatedQuestion.answer}</p>
                </div>
              )}
            </div>
          )}
        </section>

        <section className="panel">
          <div className="page-heading">
            <h2 className="section-title">Practice questions</h2>
            <p className="section-copy">
              Expand any question to review a concise answer and topic tags.
            </p>
          </div>

          {filteredQuestions.length > 0 ? (
            <div className="questions-list">
              {filteredQuestions.map((question) => {
                const isExpanded = expandedQuestion === question.id

                return (
                  <article
                    key={question.id}
                    className={isExpanded ? 'question-card expanded' : 'question-card'}
                  >
                    <div className="question-top">
                      <div>
                        <h3 className="question-title">{question.title}</h3>
                        <div className="tag-row">
                          <span className="tag">{question.topic}</span>
                          <span className="tag">{question.difficulty}</span>
                          {question.tags.map((tag) => (
                            <span key={tag} className="tag">
                              {tag}
                            </span>
                          ))}
                        </div>
                      </div>

                      <button
                        type="button"
                        className="button-secondary"
                        onClick={() => toggleQuestion(question.id)}
                      >
                        {isExpanded ? 'Hide Answer' : 'View Answer'}
                      </button>
                    </div>

                    {isExpanded && (
                      <p className="question-answer">{question.answer}</p>
                    )}
                  </article>
                )
              })}
            </div>
          ) : (
            <div className="empty-state-card">
              No questions match the current topic and difficulty filters.
            </div>
          )}
        </section>
      </main>
    </div>
  )
}

export default QuestionBank