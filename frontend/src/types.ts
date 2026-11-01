export type Employee = { id: number; name: string; mail: string; phoneNumber: string; manager_id?: number }
export type Staff = Employee & { usedPaidLeaves: number; allowedPaidLeaves: number }
export type LeaveDate = { date: string; isPaid: boolean }
export type Leave = {
  id?: number
  emp_name?: string
  applied_on?: string
  reason: string
  approval_status?: string
  dates: LeaveDate[]
  comments?: string
}
export type Quota = { usedPaidLeaves: number; allowedPaidLeaves: number }
