export interface Bid {
  id?: number;
  amount: number;
  bidderId: number;
  bidderEmail?: string;
  auctionId: number;
  timestamp: string | Date;
}