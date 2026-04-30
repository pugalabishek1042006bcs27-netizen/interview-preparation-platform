import { useMemo, useState } from 'react'
import axios from 'axios'
import Editor from '@monaco-editor/react'
import Navbar from '../components/Navbar'
import { toast } from 'react-hot-toast'

const starterCode = {
  javascript: `console.log('Hello, world!')`,
  python: `print('Hello, world!')`,
  java: `class Main {
  public static void main(String[] args) {
    System.out.println("Hello, world!");
  }
}`,
  cpp: `#include <iostream>
using namespace std;

int main() {
  cout << "Hello, world!" << endl;
  return 0;
}`
}

const languageLabels = {
  javascript: 'JavaScript',
  python: 'Python',
  java: 'Java',
  cpp: 'C++'
}

const editorLanguageMap = {
  javascript: 'javascript',
  python: 'python',
  java: 'java',
  cpp: 'cpp'
}

const CodePlayground = () => {
  const [language, setLanguage] = useState('javascript')
  const [code, setCode] = useState(starterCode.javascript)
  const [input, setInput] = useState('')
  const [output, setOutput] = useState('')
  const [loading, setLoading] = useState(false)

  const editorOptions = useMemo(
    () => ({
      minimap: { enabled: false },
      fontSize: 14,
      lineNumbersMinChars: 3,
      scrollBeyondLastLine: false,
      automaticLayout: true,
      padding: { top: 16, bottom: 16 }
    }),
    []
  )

  const handleLanguageChange = (nextLanguage) => {
    setLanguage(nextLanguage)
    setCode(starterCode[nextLanguage])
    setOutput('')
  }

  const handleRunCode = async () => {
    try {
      setLoading(true)
      setOutput('Executing remote runtime...')
      const response = await axios.post('http://localhost:8082/api/code/execute', {
        language,
        code,
        stdin: input
      })
      setOutput(response.data.output)
    } catch (err) {
      console.error(err)
      toast.error('Execution failed. Check console for details.')
      setOutput('Something went wrong while running your code.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="app-shell">
      <Navbar />
      
      <main className="page-content" style={{maxWidth: '1400px'}}>
        <header className="page-header" style={{alignItems: 'center'}}>
          <div className="page-heading">
            <span className="section-badge blue">Sandboxed Runtime</span>
            <h1 className="page-title">Code Playground</h1>
          </div>

          <div style={{display: 'flex', gap: '12px'}}>
             <div className="filter-row">
                {Object.entries(languageLabels).map(([key, label]) => (
                  <button
                    key={key}
                    type="button"
                    className={`filter-chip ${language === key ? 'active' : ''}`}
                    onClick={() => handleLanguageChange(key)}
                  >
                    {label}
                  </button>
                ))}
             </div>
             
             <div style={{width: '1px', background: 'var(--border)', height: '32px', marginInline: '8px'}} />

             <button
               type="button"
               className="button-secondary"
               onClick={() => setCode(starterCode[language])}
             >
               Reset
             </button>
             <button type="button" className="button-primary" onClick={handleRunCode} disabled={loading} style={{width: 'auto', paddingInline: '32px'}}>
               {loading ? 'Running...' : 'Run Code'}
             </button>
          </div>
        </header>

        <div style={{display: 'grid', gridTemplateColumns: '1fr 350px', gap: '24px', height: 'calc(100vh - 250px)'}}>
          <section className="panel" style={{padding: 0, overflow: 'hidden', border: '1px solid var(--border)'}}>
            <Editor
              height="100%"
              theme="vs-dark"
              language={editorLanguageMap[language]}
              value={code}
              onChange={(value) => setCode(value || '')}
              options={editorOptions}
            />
          </section>

          <aside style={{display: 'grid', gridTemplateRows: '1fr 1fr', gap: '24px'}}>
            <div className="panel" style={{display: 'flex', flexDirection: 'column'}}>
              <h4 className="section-title" style={{fontSize: '0.9rem', marginBottom: '12px'}}>Standard Input</h4>
              <textarea
                className="field-textarea"
                style={{flex: 1, resize: 'none', background: 'var(--bg-soft)', border: 'none', fontSize: '13px', fontFamily: 'monospace'}}
                placeholder="Enter stdin here..."
                value={input}
                onChange={(e) => setInput(e.target.value)}
              />
            </div>

            <div className="panel" style={{display: 'flex', flexDirection: 'column'}}>
              <h4 className="section-title" style={{fontSize: '0.9rem', marginBottom: '12px'}}>Output</h4>
              <div style={{flex: 1, background: 'var(--bg-dark)', color: '#fff', padding: '16px', borderRadius: 'var(--radius-sm)', overflowY: 'auto', fontFamily: 'monospace', fontSize: '13px'}}>
                 {output ? <pre style={{whiteSpace: 'pre-wrap'}}>{output}</pre> : <p style={{color: '#666'}}>Run code to see output.</p>}
              </div>
            </div>
          </aside>
        </div>
      </main>
    </div>
  )
}

export default CodePlayground