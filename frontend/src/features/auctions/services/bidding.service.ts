import { apiClient } from '../../../shared/services/api';

export interface Bid {
  id: number;
  auctionId: number;
  bidderId: number;
  bidderEmail?: string;
  amount: number;
  createdAt: string; 
}

export const biddingService = {
  // Llama al POST /bids
  placeBid: async (auctionId: number, amount: number): Promise<void> => {
    await apiClient.post('/bids', { auctionId, amount });
  },

  // NUEVO: Llama al GET /bids/auction/{id}
  getBidsByAuctionId: async (auctionId: number): Promise<Bid[]> => {
    const response = await apiClient.get<Bid[]>(`/bids/auction/${auctionId}`);
    return response.data;
  }
};