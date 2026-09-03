// pages/reseller.js - Reseller Dashboard
import { useState } from 'react'

export default function Reseller(){
  const [orders, setOrders] = useState([
    {id:1, item:"T-Shirt", price:499, status:"Pending Payout"},
    {id:2, item:"Shoes", price:1299, status:"Approved"}
  ])
  return (
    <div style={{padding:20}}>
      <h1>ReMart Reseller Dashboard</h1>
      <p>Yaha tujhe sirf tere orders dikhenge. Payout ke liye Admin approval lagega.</p>
      <table border="1" cellPadding="10">
        <tr><th>Order</th><th>Price</th><th>Commission (₹49 + 5%)</th><th>You Get</th><th>Status</th></tr>
        {orders.map(o=>{
          const comm = 49 + Math.round(o.price*0.05)
          return <tr key={o.id}><td>{o.item}</td><td>₹{o.price}</td><td>₹{comm}</td><td>₹{o.price-comm}</td><td>{o.status}</td></tr>
        })}
      </table>
      <p style={{color:"red"}}><b>Note:</b> Bina Owner ke PIN ke payout nahi hoga. Ye lock hai.</p>
    </div>
  )
    }
