export interface Wallet {
  id: number;
  userId: number;
  balance: number;
  heldFunds: number;
}

export interface AddFundsRequest {
  amount: number;
  reference: string; 
}