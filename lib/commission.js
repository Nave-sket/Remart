// COMMISSION LOCK - DO NOT EDIT - Owner Only
export const COMMISSION_CONFIG = Object.freeze({
  FLAT: 49,
  PERCENT: 5,
  OWNER_PIN: "1234",
  LOCKED: true
});

export function calculateCommission(price){
  if(!COMMISSION_CONFIG.LOCKED) throw new Error("Commission tampered!");
  return COMMISSION_CONFIG.FLAT + Math.round(price * COMMISSION_CONFIG.PERCENT / 100);
}

export function verifyOwner(pin){
  return pin === COMMISSION_CONFIG.OWNER_PIN;
}
