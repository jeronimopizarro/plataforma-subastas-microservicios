import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { auctionService } from '../services/auction.service';
import type { Auction } from '../types/auction.types';
import { CreateAuctionModal } from '../components/CreateAuctionModal';
import { CreateAuctionButton } from '../components/CreateAuctionButton';

export const SellerDashboardPage = () => {
  const [myAuctions, setMyAuctions] = useState<Auction[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const fetchMyAuctions = async () => {
    try {
      setLoading(true);
      const data = await auctionService.getAuctionsBySeller();
      setMyAuctions(data);
    } catch (err) {
      console.error("Error al cargar mis publicaciones", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMyAuctions();
  }, []);

  if (loading) return <h3 style={{ color: 'var(--color-dark)', textAlign: 'center', padding: '40px' }}>Cargando tus publicaciones... ⏳</h3>;

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '20px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
        <h2 style={{ color: 'var(--color-secondary)', margin: 0 }}>📦 Mis Publicaciones (Vendedor)</h2>
        
        <CreateAuctionButton onSuccess={fetchMyAuctions} />
      </div>

      <div style={{ backgroundColor: 'white', padding: '30px', borderRadius: '12px', boxShadow: '0 4px 6px rgba(0,0,0,0.05)', borderTop: '5px solid var(--color-secondary)' }}>
        {myAuctions.length === 0 ? (
          <p style={{ color: '#888', textAlign: 'center', padding: '20px 0' }}>Aún no has creado ninguna subasta. ¡Anímate a publicar tu primer producto!</p>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
              <thead>
                <tr style={{ backgroundColor: '#f9f9f9', color: '#555' }}>
                  <th style={{ padding: '15px', borderBottom: '2px solid #eee' }}>ID</th>
                  <th style={{ padding: '15px', borderBottom: '2px solid #eee' }}>Estado</th>
                  <th style={{ padding: '15px', borderBottom: '2px solid #eee' }}>Fecha Cierre</th>
                  <th style={{ padding: '15px', borderBottom: '2px solid #eee' }}>Mayor Puja Actual</th>
                  <th style={{ padding: '15px', borderBottom: '2px solid #eee', textAlign: 'center' }}>Acción</th>
                </tr>
              </thead>
              <tbody>
                {[...myAuctions]
                  .sort((a, b) => new Date(b.endTime).getTime() - new Date(a.endTime).getTime())
                  .map((auction) => (
                  <tr key={auction.id} style={{ borderBottom: '1px solid #eee', transition: 'background-color 0.2s' }} onMouseOver={(e) => e.currentTarget.style.backgroundColor = '#f8f9fa'} onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}>
                    <td style={{ padding: '15px', fontWeight: 'bold', color: '#666' }}>#{auction.id}</td>
                    <td style={{ padding: '15px' }}>
                      <span style={{
                        padding: '5px 10px',
                        borderRadius: '6px',
                        fontSize: '0.85rem',
                        fontWeight: 'bold',
                        backgroundColor: auction.status === 'ACTIVE' ? '#e6f4ea' : auction.status === 'SCHEDULED' ? '#e3f2fd' : '#f8f9fa',
                        color: auction.status === 'ACTIVE' ? '#28a745' : auction.status === 'SCHEDULED' ? '#17a2b8' : '#6c757d'
                      }}>
                        {auction.status}
                      </span>
                    </td>
                    <td style={{ padding: '15px', color: '#555' }}>
                      {new Date(auction.endTime).toLocaleDateString()} a las {new Date(auction.endTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}
                    </td>
                    <td style={{ padding: '15px', fontWeight: 'bold', color: 'var(--color-dark)', fontSize: '1.1rem' }}>
                      ${auction.currentHighestBid.toFixed(2)}
                    </td>
                    <td style={{ padding: '15px', textAlign: 'center' }}>
                      <Link 
                        to={`/auctions/${auction.id}`} 
                        style={{ color: 'var(--color-primary)', textDecoration: 'none', fontWeight: 'bold', border: '1px solid var(--color-primary)', padding: '6px 12px', borderRadius: '6px' }}
                      >
                        Ver Detalle
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* RENDERIZADO CONDICIONAL DEL MODAL */}
      {isModalOpen && (
        <CreateAuctionModal 
          onClose={() => setIsModalOpen(false)} 
          onSuccess={() => {
            setIsModalOpen(false);
            fetchMyAuctions();
          }} 
        />
      )}

    </div>
  );
};