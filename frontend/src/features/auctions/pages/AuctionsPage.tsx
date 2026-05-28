import { useEffect, useState } from 'react';
import { auctionService } from '../services/auction.service';
import { AuctionCard } from '../components/AuctionCard'; // Importamos el nuevo componente
import type { Auction } from '../types/auction.types';

export const AuctionsPage = () => {
  const [auctions, setAuctions] = useState<Auction[]>([]);

  useEffect(() => {
    auctionService.getActiveAuctions().then(setAuctions);
  }, []);

  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
      {auctions.map((auction) => (
        <AuctionCard key={auction.id} auction={auction} />
      ))}
    </div>
  );
};