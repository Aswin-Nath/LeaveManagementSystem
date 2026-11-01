import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { api } from '../api'
import type { Employee, Leave, Quota, Staff } from '../types'
import { ErrorBox, LeaveDetails, LeaveTable, Stat, Status } from '../components/Shared'

export function Dashboard({ go }: { go: (page: string) => void }) {
  const [profile, setProfile] = useState<Employee | null>(null); const [quota, setQuota] = useState<Quota | null>(null); const [leaves, setLeaves] = useState<Leave[]>([]); const [error, setError] = useState('')
  const id = Number(localStorage.getItem('employeeId') || 0)
  useEffect(() => { if (!id) return; Promise.all([api<Employee>(`/employees/${id}`), api<Quota>(`/employees/days/${id}`), api<Leave[]>('/employees/leave')]).then(([p, q, l]) => { setProfile(p); setQuota(q); setLeaves(l) }).catch((e) => setError(e.message)) }, [id])
  const availablePaidDays = quota ? quota.allowedPaidLeaves - quota.usedPaidLeaves : null
  return <><section className="welcome-card"><div><p className="eyebrow">OVERVIEW</p><h2>{profile ? `Welcome, ${profile.name.split(' ')[0]}` : 'Your leave workspace'}</h2><p className="muted">Keep your time off organized and your team in sync.</p></div><button className="primary" onClick={() => go('apply')}>＋ Apply for leave</button></section>{error && <ErrorBox message={error} />}<section className="stats-grid"><Stat label="Paid days available" value={availablePaidDays ?? '—'} accent="mint" /><Stat label="Paid days used" value={quota?.usedPaidLeaves ?? '—'} accent="peach" /><Stat label="Allowance this year" value={quota?.allowedPaidLeaves ?? '—'} accent="lavender" /></section><section className="panel"><div className="panel-heading"><div><p className="eyebrow">RECENT ACTIVITY</p><h2>Latest leave requests</h2></div><button className="text-button" onClick={() => go('my-leaves')}>View all →</button></div><LeaveTable leaves={leaves.slice(0, 4)} /></section></>
}

export function Login({ onLogin }: { onLogin: (token: string) => void }) {
  const [mail, setMail] = useState(''); const [password, setPassword] = useState(''); const [error, setError] = useState(''); const [loading, setLoading] = useState(false)
  const submit = async (event: FormEvent) => { event.preventDefault(); setError(''); setLoading(true); try { const result = await api<{ accessToken: string }>('/auth/login', { method: 'POST', body: JSON.stringify({ mail, password }) }); onLogin(result.accessToken) } catch (err) { setError(err instanceof Error ? err.message : 'Unable to sign in') } finally { setLoading(false) } }
  return <div className="login-page"><div className="login-card"><div className="brand centered"><span className="brand-mark">LM</span><span>LeaveMate</span></div><p className="eyebrow centered">TEAM LEAVE, SIMPLIFIED</p><h1>Welcome back</h1><p className="muted centered">Sign in to manage your leave and team.</p><form onSubmit={submit}><label>Email<input type="email" value={mail} onChange={(e) => setMail(e.target.value)} required placeholder="you@company.com" /></label><label>Password<input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required placeholder="••••••••" /></label>{error && <ErrorBox message={error} />}<button className="primary full" disabled={loading}>{loading ? 'Signing in…' : 'Sign in'}</button></form></div></div>
}

export function MyLeaves() {
  const [leaves, setLeaves] = useState<Leave[]>([]); const [error, setError] = useState(''); const [selected, setSelected] = useState<Leave | null>(null)
  const load = useCallback(() => api<Leave[]>('/employees/leave').then(setLeaves).catch((e) => setError(e.message)), [])
  useEffect(() => { load() }, [load])
  const viewLeave = async (leave: Leave) => { if (!leave.id) return; try { setSelected(await api<Leave>(`/employees/leave/${leave.id}`)) } catch (e) { setError(e instanceof Error ? e.message : 'Could not load leave details') } }
  const withdrawLeave = async () => { if (!selected?.id) return; try { await api(`/employees/leave/${selected.id}/withdraw`, { method: 'PUT' }); setSelected(null); load() } catch (e) { setError(e instanceof Error ? e.message : 'Could not withdraw leave') } }
  return <section className="panel"><div className="panel-heading"><div><p className="eyebrow">EMPLOYEE VIEW</p><h2>All requests</h2></div><button className="secondary" onClick={load}>↻ Refresh</button></div>{error && <ErrorBox message={error} />}<LeaveTable leaves={leaves} onView={viewLeave} empty="No leave requests yet." />{selected && <LeaveDetails leave={selected} onClose={() => setSelected(null)} onWithdraw={withdrawLeave} />}</section>
}

export function ApplyLeave({ onDone }: { onDone: () => void }) {
  const [reason, setReason] = useState(''); const [dates, setDates] = useState<string[]>([]); const [paidDates, setPaidDates] = useState<string[]>([]); const [month, setMonth] = useState(new Date()); const [quota, setQuota] = useState<Quota | null>(null); const [existingLeaves, setExistingLeaves] = useState<Leave[]>([]); const [message, setMessage] = useState(''); const [error, setError] = useState(''); const employeeId = Number(localStorage.getItem('employeeId') || 0)
  useEffect(() => { if (!employeeId) return; Promise.all([api<Quota>(`/employees/days/${employeeId}`), api<Leave[]>('/employees/leave')]).then(([nextQuota, nextLeaves]) => { setQuota(nextQuota); setExistingLeaves(nextLeaves) }).catch((e) => setError(e instanceof Error ? e.message : 'Could not load leave availability')) }, [employeeId])
  const unavailableDates = new Set(existingLeaves
    .filter((leave) => {
      const status = (leave.approval_status || 'PENDING').toUpperCase()
      return status === 'PENDING' || status === 'ACCEPT' || status === 'ACCEPTED'
    })
    .flatMap((leave) => leave.dates.map((date) => date.date)))
  const toggleDate = (date: string) => { if (unavailableDates.has(date)) return; setDates((current) => current.includes(date) ? current.filter((item) => item !== date) : [...current, date].sort()) }
  const togglePaid = (date: string) => setPaidDates((current) => current.includes(date) ? current.filter((item) => item !== date) : [...current, date])
  const availablePaidDays = quota ? quota.allowedPaidLeaves - quota.usedPaidLeaves : 0
  const submit = async (event: FormEvent) => { event.preventDefault(); setError(''); setMessage(''); if (!dates.length) { setError('Select at least one weekday.'); return } if (paidDates.length > availablePaidDays) { setError(`You selected ${paidDates.length} paid days but only have ${availablePaidDays} available. Change the paid dates below; your date selection is preserved.`); return } try { await api('/employees/leave', { method: 'POST', body: JSON.stringify({ reason, dates: dates.map((date) => ({ date, isPaid: paidDates.includes(date) })) }) }); setMessage('Leave request submitted successfully.'); setReason(''); setDates([]); setPaidDates([]); setTimeout(onDone, 900) } catch (e) { setError(e instanceof Error ? e.message : 'Could not submit leave') } }
  return <section className="form-panel"><p className="eyebrow">NEW REQUEST</p><h2>Plan your time away</h2><p className="muted">Weekends are unavailable. Dates already used in pending or accepted requests are marked unavailable.</p><form onSubmit={submit} className="form-grid"><label className="wide">Reason<textarea value={reason} onChange={(e) => setReason(e.target.value)} required placeholder="e.g. Family vacation" /></label><div className="calendar wide"><div className="calendar-head"><button type="button" className="text-button" onClick={() => setMonth(new Date(month.getFullYear(), month.getMonth() - 1, 1))}>←</button><strong>{month.toLocaleString(undefined, { month: 'long', year: 'numeric' })}</strong><button type="button" className="text-button" onClick={() => setMonth(new Date(month.getFullYear(), month.getMonth() + 1, 1))}>→</button></div><div className="calendar-grid weekdays">{['M','T','W','T','F','S','S'].map((day, index) => <span key={`${day}-${index}`}>{day}</span>)}</div><div className="calendar-grid">{calendar(month).map((date, index) => date ? <button type="button" disabled={unavailableDates.has(date)} key={date} className={`calendar-day ${dates.includes(date) ? 'selected' : ''} ${unavailableDates.has(date) ? 'unavailable' : ''}`} onClick={() => toggleDate(date)} title={unavailableDates.has(date) ? 'Already included in another leave request' : undefined}>{Number(date.slice(-2))}</button> : <span key={`empty-${index}`} />)}</div><small className="calendar-legend"><span className="legend-dot unavailable-dot" /> Already requested</small></div><div className="selected-dates wide"><strong>Selected dates</strong>{dates.length ? dates.map((date) => <button type="button" className={`date-chip ${paidDates.includes(date) ? 'paid' : ''}`} key={date} onClick={() => togglePaid(date)}>{date} · {paidDates.includes(date) ? 'Paid' : 'Unpaid'}</button>) : <span className="muted">None yet</span>}<small>Click a date chip to switch paid/unpaid. Paid selected: {paidDates.length} / {availablePaidDays} available.</small></div>{error && <ErrorBox message={error} />}{message && <div className="success">{message}</div>}<div className="wide form-actions"><button className="primary">Submit request</button></div></form></section>
}

function calendar(month: Date) { const first = new Date(month.getFullYear(), month.getMonth(), 1); const days = new Date(month.getFullYear(), month.getMonth() + 1, 0).getDate(); const offset = (first.getDay() + 6) % 7; return [...Array(offset).fill(null), ...Array.from({ length: days }, (_, index) => { const date = new Date(month.getFullYear(), month.getMonth(), index + 1); return date.getDay() === 0 || date.getDay() === 6 ? null : `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(index + 1).padStart(2, '0')}` })] }

export function Team({ onSelect }: { onSelect: (id: number) => void }) {
  const [staff, setStaff] = useState<Staff[]>([]); const [error, setError] = useState('')
  const load = useCallback(() => {
    api<Staff[]>('/manager/employees')
      .then((nextStaff) => {
        setError('')
        setStaff(nextStaff)
      })
      .catch((e) => {
        const message = e instanceof Error ? e.message : 'Could not load your team'
        if (message.toLowerCase().includes('only managers can access team leave')) {
          setStaff([])
          return
        }
        setError(message)
      })
  }, [])
  useEffect(() => { load() }, [load])
  return <section className="panel"><div className="panel-heading"><div><p className="eyebrow">MANAGER VIEW</p><h2>People you manage</h2></div><button className="secondary" onClick={load}>↻ Refresh</button></div>{error && <ErrorBox message={error} />}<div className="team-grid">{staff.map((person) => <article className="person-card" key={person.id} onClick={() => onSelect(person.id)}><div className="person-avatar">{person.name.slice(0, 2).toUpperCase()}</div><div><h3>{person.name}</h3><p className="muted">{person.mail}</p><p className="muted">{person.phoneNumber}</p></div><div className="quota"><strong>{person.allowedPaidLeaves - person.usedPaidLeaves}</strong><small>paid days available</small></div></article>)}</div>{!staff.length && !error && <div className="empty">You don't have any employees under your management.</div>}</section>
}

export function TeamLeaves() {
  const [leaves, setLeaves] = useState<Leave[]>([]); const [status, setStatus] = useState(''); const [error, setError] = useState(''); const [selected, setSelected] = useState<Leave | null>(null)
  const load = useCallback(() => api<Leave[]>(`/manager/leave${status ? `?status=${encodeURIComponent(status)}` : ''}`).then(setLeaves).catch((e) => setError(e.message)), [status])
  useEffect(() => { load() }, [load])
  const viewLeave = async (leave: Leave) => { if (!leave.id) return; if (selected?.id === leave.id) { setSelected(null); return } setSelected(leave); try { setSelected(await api<Leave>(`/manager/leave/${leave.id}`)) } catch (e) { setError(e instanceof Error ? e.message : 'Could not load leave') } }
  const decide = async (leave: Leave, decision: 'ACCEPT' | 'REJECT', comments: string) => { try { await api(`/manager/leave/${leave.id}`, { method: 'PUT', body: JSON.stringify({ approval_status: decision, comments }) }); setSelected(null); load() } catch (e) { setError(e instanceof Error ? e.message : 'Could not update leave') } }
  return <section className="panel"><div className="panel-heading"><div><p className="eyebrow">MANAGER VIEW</p><h2>Team requests</h2></div><div className="toolbar"><select value={status} onChange={(e) => setStatus(e.target.value)}><option value="">All statuses</option><option value="PENDING">Pending</option><option value="ACCEPT">Accepted</option><option value="REJECT">Rejected</option><option value="WITHDRAWN">Withdrawn</option></select><button className="secondary" onClick={load}>↻ Refresh</button></div></div>{error && <ErrorBox message={error} />}<LeaveTable leaves={leaves} onView={viewLeave} expandedLeaveId={selected?.id} expandedContent={selected && <ManagerLeaveActions leave={selected} onClose={() => setSelected(null)} onDecision={decide} />} empty="No team leave requests found." /></section>
}

function ManagerLeaveActions({ leave, onClose, onDecision }: { leave: Leave; onClose: () => void; onDecision: (leave: Leave, decision: 'ACCEPT' | 'REJECT', comments: string) => void }) {
  const [comments, setComments] = useState(leave.comments || ''); const pending = (leave.approval_status || '').toUpperCase() === 'PENDING'
  return <div className="details-card"><div className="panel-heading"><div><p className="eyebrow">REVIEW REQUEST</p><h2>{leave.emp_name || 'Employee leave'}</h2></div><button className="secondary" onClick={onClose}>Close</button></div><p><strong>Reason:</strong> {leave.reason}</p><p><strong>Dates:</strong> {leave.dates.map((date) => `${date.date}${date.isPaid ? ' (paid)' : ' (unpaid)'}`).join(', ')}</p><Status value={leave.approval_status} />{pending && <><label className="comment-field">Comments<textarea value={comments} onChange={(e) => setComments(e.target.value)} placeholder="Add a note for the employee" /></label><div className="decision-actions"><button className="secondary danger-button" onClick={() => onDecision(leave, 'REJECT', comments)}>Reject</button><button className="primary" onClick={() => onDecision(leave, 'ACCEPT', comments)}>Accept</button></div></>}</div>
}

export function EmployeeDetail({ id }: { id: number }) {
  const [person, setPerson] = useState<Employee | null>(null); const [quota, setQuota] = useState<Quota | null>(null); const [leaves, setLeaves] = useState<Leave[]>([]); const [selected, setSelected] = useState<Leave | null>(null); const [days, setDays] = useState(''); const [message, setMessage] = useState(''); const [error, setError] = useState('')
  useEffect(() => { Promise.all([api<Employee>(`/employees/${id}`), api<Quota>(`/employees/days/${id}`), api<Leave[]>(`/manager/leave?empId=${id}`)]).then(([p, q, l]) => { setPerson(p); setQuota(q); setLeaves(l); setDays(String(q.allowedPaidLeaves)) }).catch((e) => setError(e.message)) }, [id])
  const update = async (e: FormEvent) => { e.preventDefault(); try { const q = await api<Quota>(`/manager/days/${id}`, { method: 'PUT', body: JSON.stringify({ paidDays: Number(days) }) }); setQuota(q); setMessage('Quota updated.'); setError('') } catch (e) { setError(e instanceof Error ? e.message : 'Update failed') } }
  const viewLeave = async (leave: Leave) => { if (!leave.id) return; try { setSelected(await api<Leave>(`/manager/leave/${leave.id}`)) } catch (e) { setError(e instanceof Error ? e.message : 'Could not load leave') } }
  const availablePaidDays = quota ? quota.allowedPaidLeaves - quota.usedPaidLeaves : null
  return <section className="profile-layout">{error && <ErrorBox message={error} />}<div className="profile-card">{person && <><div className="profile-avatar">{person.name.slice(0, 2).toUpperCase()}</div><h2>{person.name}</h2><p className="muted">{person.mail}</p><p className="muted">{person.phoneNumber}</p></>}</div><div className="form-panel"><p className="eyebrow">MANAGE QUOTA</p><h2>Paid leave allowance</h2><div className="stats-grid compact"><Stat label="Available" value={availablePaidDays ?? '—'} accent="mint" /><Stat label="Used" value={quota?.usedPaidLeaves ?? '—'} accent="peach" /><Stat label="Total" value={quota?.allowedPaidLeaves ?? '—'} accent="lavender" /></div><form onSubmit={update}><label>New allowance<input type="number" min="0" value={days} onChange={(e) => setDays(e.target.value)} required /></label>{message && <div className="success">{message}</div>}<button className="primary">Save allowance</button></form></div><div className="panel profile-leaves"><div className="panel-heading"><div><p className="eyebrow">AUTHORIZED VIEW</p><h2>Employee leave history</h2></div></div><LeaveTable leaves={leaves} onView={viewLeave} empty="No leave requests for this employee." />{selected && <LeaveDetails leave={selected} onClose={() => setSelected(null)} />}</div></section>
}
