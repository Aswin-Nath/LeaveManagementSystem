import type { ReactNode } from 'react'
import type { Leave } from '../types'

export function NavButton({ active, onClick, icon, children }: { active: boolean; onClick: () => void; icon: string; children: string }) {
  return <button className={`nav-button ${active ? 'active' : ''}`} onClick={onClick}><span>{icon}</span>{children}</button>
}

export function ErrorBox({ message }: { message: string }) { return <div className="error-box">⚠ {message}</div> }

export function Status({ value }: { value?: string }) {
  const status = (value || 'PENDING').toUpperCase()
  return <span className={`status ${status.toLowerCase()}`}>{status}</span>
}

export function Stat({ label, value, accent }: { label: string; value: string | number; accent: string }) {
  return <div className={`stat-card ${accent}`}><span className="stat-icon">✦</span><p>{label}</p><strong>{value}</strong></div>
}

export function LeaveTable({ leaves, onView, empty = 'No requests found.' }: { leaves: Leave[]; onView?: (leave: Leave) => void; empty?: string }) {
  if (!leaves.length) return <div className="empty">{empty}</div>
  return <div className="table-wrap"><table><thead><tr><th>EMPLOYEE</th><th>DATES</th><th>REASON</th><th>STATUS</th><th>APPLIED</th><th /></tr></thead><tbody>
    {leaves.map((leave, index) => <tr key={`${leave.applied_on}-${index}`}><td><strong>{leave.emp_name || 'You'}</strong></td><td><strong>{leave.dates?.[0]?.date || '—'}</strong>{leave.dates?.length > 1 && <small> to {leave.dates[leave.dates.length - 1].date}</small>}</td><td>{leave.reason}</td><td><Status value={leave.approval_status} /></td><td>{leave.applied_on || '—'}</td><td>{onView && <button className="text-button" onClick={() => onView(leave)}>View →</button>}</td></tr>)}
  </tbody></table></div>
}

export function LeaveDetails({ leave, onClose, onWithdraw }: { leave: Leave; onClose: () => void; onWithdraw?: () => void }) {
  const canWithdraw = ['PENDING', 'ACCEPT', 'ACCEPTED'].includes((leave.approval_status || '').toUpperCase())
  return <div className="details-card"><div className="panel-heading"><div><p className="eyebrow">LEAVE DETAILS</p><h2>{leave.reason}</h2></div><button className="secondary" onClick={onClose}>Close</button></div><p><strong>Status:</strong> <Status value={leave.approval_status} /></p><p><strong>Applied:</strong> {leave.applied_on || '—'}</p><p><strong>Dates:</strong> {leave.dates.map((date) => `${date.date}${date.isPaid ? ' (paid)' : ''}`).join(', ')}</p>{leave.comments && <p><strong>Comments:</strong> {leave.comments}</p>}{canWithdraw && onWithdraw && <button className="secondary danger-button" onClick={onWithdraw}>Withdraw leave</button>}</div>
}

export function PagePanel({ children }: { children: ReactNode }) { return <section className="panel">{children}</section> }
