import { apiClient } from '../../../shared/services/api';
import type { Wallet, AddFundsRequest } from '../types/wallet.types';

export const walletService = {
  getWallet: async (): Promise<Wallet> => {
    const response = await apiClient.get<Wallet>('/wallets/me');
    return response.data;
  },

  addFunds: async (amount: number): Promise<void> => {
    const payload: AddFundsRequest = { 
      amount, 
      reference: 'Recarga de saldo desde la web' 
    };
    
    await apiClient.post('/wallets/me/deposit', payload);
  }
};