export interface Wallet {
  id: number;
  userId: number;
  availableBalance: number;
  heldFunds: number;
}

export interface AddFundsRequest {
  amount: number;
  reference: string; 
}