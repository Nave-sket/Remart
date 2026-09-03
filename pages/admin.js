// pages/admin.js - Sirf Owner ka Access
import { useState } from 'react'
const ADMIN_PIN = "1234"; // tera secret PIN
const COMMISSION_FLAT = 49;
const COMMISSION_PERCENT = 5;

export default function Admin(){
  const [pin, setPin] = useState("")
  const [ok, setOk] = useState(false)
  if(!ok) return (
    <div style={{padding:50}}>
      <h1>ReMart Admin Login</h1>
      <input type="password" placeholder="Enter PIN" value={pin} onChange={e=>setPin(e.target.value)} />
      <button onClick={()=> pin===ADMIN_PIN ? setOk(true) : alert("Wrong PIN")}>Login</button>
    </div>
  )
  return (
    <div style={{padding:20}}>
      <h1>ReMart Owner Dashboard</h1>
      <p>Commission Locked: ₹{COMMISSION_FLAT} + {COMMISSION_PERCENT}%</p>
      <p>AI bhi isko change nahi kar sakta - sirf tu.</p>
      <p>Payout: Owner approval ke bina koi payment nahi jayega</p>
    </div>
  )
    }
