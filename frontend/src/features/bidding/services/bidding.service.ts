import { apiClient } from '../../../shared/services/api';
import type { Bid } from '../types/bidding.types';

export const biddingService = {
  // Llama al POST /bids
  placeBid: async (auctionId: number, amount: number): Promise<void> => {
    await apiClient.post('/bids', { auctionId, amount });
  },

  // NUEVO: Llama al GET /bids/auction/{id}
  getBidsByAuctionId: async (auctionId: number): Promise<Bid[]> => {
    const response = await apiClient.get<Bid[]>(`/bids/auction/${auctionId}`);
    return response.data;
  },

  getMyBids: async (): Promise<Bid[]> => {
    const response = await apiClient.get<Bid[]>('/bids/mine');
    return response.data;
  }
};