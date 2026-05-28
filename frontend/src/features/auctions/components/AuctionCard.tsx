import { useEffect, useState } from 'react';
import { auctionService } from '../services/auction.service';
import type { Auction } from '../types/auction.types';
import type { Product } from '../types/product.types';

export const AuctionCard = ({ auction }: { auction: Auction }) => {
  const [product, setProduct] = useState<Product | null>(null);

  useEffect(() => {
    auctionService.getProductById(auction.productId).then(setProduct);
  }, [auction.productId]);

  return (
    <div style={{
      backgroundColor: 'white', borderRadius: '12px', padding: '20px',
      boxShadow: '0 4px 6px rgba(0,0,0,0.05)', borderTop: '5px solid var(--color-primary)'
    }}>
      {/* Imagen del producto */}
      {product?.imageUrl && (
        <img src={product.imageUrl} alt={product.title} style={{ width: '100%', borderRadius: '8px', marginBottom: '15px' }} />
      )}
      
      <h3 style={{ margin: '0 0 10px 0', color: 'var(--color-dark)' }}>{product?.title || 'Cargando...'}</h3>
      <p style={{ color: '#666', fontSize: '0.9rem', marginBottom: '15px' }}>{product?.description}</p>
      
      <div style={{ backgroundColor: 'var(--color-light)', padding: '10px', borderRadius: '6px' }}>
        <p style={{ margin: '0', fontSize: '0.9rem', color: 'var(--color-dark)' }}>Oferta:</p>
        <p style={{ margin: '0', fontSize: '1.2rem', fontWeight: 'bold' }}>${auction.currentHighestBid?.toFixed(2)}</p>
      </div>
    </div>
  );
};