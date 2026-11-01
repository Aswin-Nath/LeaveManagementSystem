import { useEffect, useState } from 'react'
import './App.css'
import { API_URL, api } from './api'
import { NavButton } from './components/Shared'
import { ApplyLeave, Dashboard, EmployeeDetail, Login, MyLeaves, Team, TeamLeaves } from './pages/LeavePages'

function decodeTokenSubject(token: string) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))) as { sub?: string }
    return payload.sub
  } catch {
    return undefined
  }
}

function pageTitle(page: string) {
  return ({ dashboard: 'Good morning', 'my-leaves': 'My leave requests', apply: 'Apply for leave', team: 'Your team', 'team-leaves': 'Team leave', employee: 'Employee profile' }[page] || 'LeaveMate')
}

function App() {
  const [token, setToken] = useState(() => localStorage.getItem('accessToken'))
  const [page, setPage] = useState('dashboard')
  const [employeeId, setEmployeeId] = useState<number | null>(null)
  const [canViewTeamLeaves, setCanViewTeamLeaves] = useState(false)

  useEffect(() => {
    api('/manager/employees')
      .then(() => setCanViewTeamLeaves(true))
      .catch(() => setCanViewTeamLeaves(false))
  }, [token])

  const activePage = !canViewTeamLeaves && page === 'team-leaves' ? 'dashboard' : page

  if (!token) {
    return <Login onLogin={(nextToken) => {
      localStorage.setItem('accessToken', nextToken)
      const subject = decodeTokenSubject(nextToken)
      if (subject) localStorage.setItem('employeeId', subject)
      setToken(nextToken)
    }} />
  }

  const logout = async () => {
    try { await api('/auth/logout', { method: 'POST' }) } catch { /* clear local session even if refresh cookie expired */ }
    localStorage.removeItem('accessToken')
    setToken(null)
  }

  return <div className="app-shell">
    <aside className="sidebar">
      <div className="brand"><span className="brand-mark">LM</span><span>LeaveMate</span></div>
      <p className="eyebrow">WORKSPACE</p>
      <nav>
        <NavButton active={page === 'dashboard'} onClick={() => setPage('dashboard')} icon="⌂">Dashboard</NavButton>
        <NavButton active={page === 'my-leaves'} onClick={() => setPage('my-leaves')} icon="▣">My leave</NavButton>
        <NavButton active={page === 'apply'} onClick={() => setPage('apply')} icon="＋">Apply for leave</NavButton>
        <NavButton active={page === 'team'} onClick={() => setPage('team')} icon="♧">Team</NavButton>
        {canViewTeamLeaves && <NavButton active={page === 'team-leaves'} onClick={() => setPage('team-leaves')} icon="▤">Team leave</NavButton>}
      </nav>
      <div className="sidebar-bottom">
        <div className="api-pill"><span className="status-dot" /> API connected<br /><small>{API_URL}</small></div>
        <button className="nav-button" onClick={logout}><span>↪</span> Sign out</button>
      </div>
    </aside>
    <main className="main-content">
      <header className="topbar"><div><p className="eyebrow">LEAVE MANAGEMENT</p><h1>{pageTitle(activePage)}</h1></div><div className="topbar-actions"><span className="avatar">ME</span></div></header>
      {activePage === 'dashboard' && <Dashboard go={setPage} />}
      {activePage === 'my-leaves' && <MyLeaves />}
      {activePage === 'apply' && <ApplyLeave onDone={() => setPage('my-leaves')} />}
      {activePage === 'team' && <Team onSelect={(id) => { setEmployeeId(id); setPage('employee') }} />}
      {activePage === 'team-leaves' && <TeamLeaves />}
      {activePage === 'employee' && employeeId && <EmployeeDetail id={employeeId} />}
    </main>
  </div>
}

export default App
