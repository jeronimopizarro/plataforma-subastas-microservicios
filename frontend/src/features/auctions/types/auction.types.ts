export interface Auction {
  id: number;
  productId: number;
  sellerId: number;
  startingPrice: number;
  currentHighestBid: number;
  startTime: string;
  endTime: string;
  status: string;    // 'ACTIVE', 'FINISHED', 'CANCELLED'
  winnerId: number | null;
}

export interface CreateAuctionRequest {
  productId: number;
  startingPrice: number;
  startTime: string; 
  endTime: string;
}