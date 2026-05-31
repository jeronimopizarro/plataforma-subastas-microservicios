import { apiClient } from '../../../shared/services/api';

export const biddingService = {
  // Llama al POST /bids de tu BiddingController
  placeBid: async (auctionId: number, amount: number): Promise<void> => {
    await apiClient.post('/bids', { auctionId, amount });
  }
};