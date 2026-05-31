import React, { useEffect, useState } from 'react';
// IMPORTAMOS LINK
import { Link } from 'react-router-dom'; 
import type { Auction } from '../types/auction.types';
import type { Product } from '../types/product.types';
import { auctionService } from '../services/auction.service';

interface AuctionCardProps {
  auction: Auction;
}

export const AuctionCard: React.FC<AuctionCardProps> = ({ auction }) => {
  const [product, setProduct] = useState<Product | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Configuración de etiquetas y colores por estado
  const statusConfig = {
    ACTIVE: { label: 'EN CURSO', color: '#28a745' },
    SCHEDULED: { label: 'PRÓXIMA', color: '#17a2b8' },
    FINISHED: { label: 'FINALIZADA', color: '#6c757d' }
  };

  const config = statusConfig[auction.status as keyof typeof statusConfig] || { label: auction.status, color: '#ccc' };
  const isFinished = auction.status === 'FINISHED';

  useEffect(() => {
    const fetchProductDetails = async () => {
      try {
        const data = await auctionService.getProductById(auction.productId);
        setProduct(data);
      } catch (error) {
        console.error('Error cargando el producto:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchProductDetails();
  }, [auction.productId]);

  if (isLoading) return <div style={{ padding: '20px', textAlign: 'center' }}>Cargando detalles...</div>;
  if (!product) return null;

  return (
    <div style={{ 
      border: '1px solid #eaeaea', 
      borderRadius: '12px', 
      padding: '20px', 
      backgroundColor: '#fff',
      boxShadow: '0 4px 6px rgba(0,0,0,0.05)',
      opacity: isFinished ? 0.7 : 1,
      display: 'flex',
      flexDirection: 'column'
    }}>
      <div style={{ flex: 1 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '15px' }}>
          <span style={{ 
            backgroundColor: config.color, 
            color: 'white', 
            padding: '4px 8px', 
            borderRadius: '4px', 
            fontSize: '0.8rem', 
            fontWeight: 'bold' 
          }}>
            {config.label}
          </span>
          <span style={{ fontSize: '0.9rem', color: '#666' }}>{product.condition}</span>
        </div>
        
        {/* --- IMAGEN DEL PRODUCTO (USANDO LA URL DE TU BASE DE DATOS) --- */}
        <div style={{ 
          width: '100%', 
          height: '200px', 
          backgroundColor: '#f8f9fa', 
          borderRadius: '8px',
          marginBottom: '15px',
          overflow: 'hidden',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center'
        }}>
          <img 
            src={product.imageUrl || '/favicon.svg'} 
            alt={product.title}
            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
            onError={(e) => { e.currentTarget.src = '/favicon.svg'; e.currentTarget.style.objectFit = 'contain'; e.currentTarget.style.padding = '20px'; }}
          />
        </div>
        
        {/* Cambiamos product.name por product.title */}
        <h3 style={{ margin: '0 0 10px 0', fontSize: '1.2rem' }}>{product.title}</h3>
        <p style={{ color: '#555', fontSize: '0.95rem', marginBottom: '20px', WebkitLineClamp: 2, display: '-webkit-box', WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
          {product.description}
        </p>
      </div>

      <div style={{ borderTop: '1px solid #eee', paddingTop: '15px' }}>
        <p style={{ margin: '0 0 5px 0', fontSize: '0.9rem', color: '#666' }}>
          {auction.status === 'SCHEDULED' ? 'Precio base:' : 'Oferta actual:'}
        </p>
        <p style={{ margin: '0 0 15px 0', fontSize: '1.5rem', fontWeight: 'bold', color: 'var(--color-primary)' }}>
          ${auction.currentHighestBid.toFixed(2)}
        </p>
        
        <Link 
          to={`/auctions/${auction.id}`}
          style={{ 
            display: 'block',
            textAlign: 'center',
            width: '100%', 
            padding: '10px', 
            backgroundColor: 'var(--color-secondary)', 
            color: 'white', 
            textDecoration: 'none',
            border: 'none', 
            borderRadius: '6px', 
            fontWeight: 'bold',
            boxSizing: 'border-box'
          }}
        >
          Ver Detalles
        </Link>
      </div>
    </div>
  );
};