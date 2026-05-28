export interface Auction {
  id: number;
  productId: number;
  sellerId: number;
  startingPrice: number;
  currentHighestBid: number
  startTime: string;
  endTime: string;
  status: 'ACTIVE' | 'FINISHED' | 'CANCELLED';
  winnerId: number | null;
}