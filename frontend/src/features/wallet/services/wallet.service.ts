import { apiClient } from '../../../shared/services/api';
import type { Wallet, AddFundsRequest } from '../types/wallet.types';

export const walletService = {
  getWallet: async (userId: number): Promise<Wallet> => {
    const response = await apiClient.get<Wallet>(`/wallets/${userId}`);
    return response.data;
  },

  addFunds: async (userId: number, amount: number): Promise<void> => {
    // Armamos el payload con la referencia que pide el backend
    const payload: AddFundsRequest = { 
      amount, 
      reference: 'Recarga de saldo desde la web' 
    };
    
    // Le pegamos a la ruta correcta: /wallets/{userId}/deposit
    await apiClient.post(`/wallets/${userId}/deposit`, payload);
  }
};