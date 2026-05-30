import React, { useEffect, useState } from 'react';
import type { Auction } from '../types/auction.types';
import { auctionService } from '../services/auction.service';
import { AuctionCard } from '../components/AuctionCard';

export const AuctionsPage: React.FC = () => {
  const [auctions, setAuctions] = useState<Auction[]>([]);
 const [statusFilter, setStatusFilter] = useState<'ACTIVE' | 'FINISHED' | 'SCHEDULED'>('ACTIVE');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchAuctions = async () => {
      setIsLoading(true);
      setError('');
      try {
        const data = await auctionService.getAuctionsByStatus(statusFilter);
        setAuctions(data);
      } catch (err) {
        setError('No se pudieron cargar las subastas en este momento.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchAuctions();
  }, [statusFilter]);

  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
        <h1 style={{ color: 'var(--color-dark)', margin: 0 }}>Explorar Subastas</h1>
        <button style={{ backgroundColor: 'var(--color-primary)', color: 'white', padding: '10px 20px', borderRadius: '6px', border: 'none', fontWeight: 'bold', cursor: 'pointer' }}>
          + Crear Subasta
        </button>
      </div>

      {/* Filtros */}
      <div style={{ display: 'flex', gap: '10px', marginBottom: '30px' }}>
        <button 
          onClick={() => setStatusFilter('ACTIVE')}
          style={{ 
            padding: '8px 16px', 
            borderRadius: '20px', 
            border: 'none',
            fontWeight: 'bold',
            cursor: 'pointer',
            backgroundColor: statusFilter === 'ACTIVE' ? 'var(--color-secondary)' : '#e0e0e0',
            color: statusFilter === 'ACTIVE' ? 'white' : '#333'
          }}
        >
          En Curso
        </button>
        <button 
          onClick={() => setStatusFilter('SCHEDULED')}
          style={{ 
            padding: '8px 16px', borderRadius: '20px', border: 'none', fontWeight: 'bold', cursor: 'pointer',
            backgroundColor: statusFilter === 'SCHEDULED' ? '#17a2b8' : '#e0e0e0',
            color: statusFilter === 'SCHEDULED' ? 'white' : '#333'
            }}
          >
          Próximas
        </button>
        <button 
          onClick={() => setStatusFilter('FINISHED')}
          style={{ 
            padding: '8px 16px', 
            borderRadius: '20px', 
            border: 'none',
            fontWeight: 'bold',
            cursor: 'pointer',
            backgroundColor: statusFilter === 'FINISHED' ? '#6c757d' : '#e0e0e0',
            color: statusFilter === 'FINISHED' ? 'white' : '#333'
          }}
        >
          Historial Pasado
        </button>
      </div>

      {/* Estado de carga / Error / Grilla vacía */}
      {isLoading && <p style={{ textAlign: 'center', color: '#666' }}>Cargando catálogo...</p>}
      {error && <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>}
      
      {!isLoading && !error && auctions.length === 0 && (
        <div style={{ textAlign: 'center', padding: '50px', backgroundColor: '#f9f9f9', borderRadius: '8px' }}>
          <p style={{ color: '#666', fontSize: '1.1rem' }}>No hay subastas {statusFilter === 'ACTIVE' ? 'activas' : 'finalizadas'} en este momento.</p>
        </div>
      )}

      {/* Grilla de Tarjetas */}
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', 
        gap: '20px' 
      }}>
        {auctions.map(auction => (
          <AuctionCard key={auction.id} auction={auction} />
        ))}
      </div>
    </div>
  );
};