import React, { useEffect, useState } from 'react';
import { walletService } from '../services/wallet.service';
import { biddingService, type Bid } from '../../auctions/services/bidding.service';
import { auctionService } from '../../auctions/services/auction.service'; // <-- Importamos el servicio de subastas
import type { Wallet } from '../types/wallet.types';
import type { Auction } from '../../auctions/types/auction.types'; // <-- Importamos el tipo Auction
import { Link } from 'react-router-dom';

export const WalletPage = () => {
  const [wallet, setWallet] = useState<Wallet | null>(null);
  const [amountToAdd, setAmountToAdd] = useState('');
  const [myBids, setMyBids] = useState<Bid[]>([]);
  const [wonAuctions, setWonAuctions] = useState<Auction[]>([]); // <-- Nuevo estado para subastas ganadas
  const [loading, setLoading] = useState(true);

  const userIdFromStorage = localStorage.getItem('userId');
  const CURRENT_USER_ID = userIdFromStorage ? parseInt(userIdFromStorage, 10) : 0;

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      // Ejecutamos las 3 peticiones en paralelo
      const [walletData, bidsData, wonData] = await Promise.all([
        walletService.getWallet().catch(err => {
          if (err.response?.status === 404) {
            return { id: 0, userId: CURRENT_USER_ID, availableBalance: 0, heldFunds: 0 };
          }
          throw err;
        }),
        biddingService.getMyBids().catch(() => []),
        auctionService.getWonAuctions().catch(() => []) // <-- Traemos las ganadas
      ]);

      setWallet(walletData);
      setMyBids(bidsData);
      setWonAuctions(wonData); // <-- Guardamos en el estado
    } catch (err) {
      console.error("Error al cargar los datos del dashboard", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  const handleAddFunds = async (e: React.FormEvent) => {
    e.preventDefault();
    const amount = parseFloat(amountToAdd);
    if (isNaN(amount) || amount <= 0) return;

    try {
      await walletService.addFunds(amount); 
      setAmountToAdd('');
      loadDashboardData();
    } catch (err) {
      alert('Hubo un error al intentar agregar fondos. Verifica estar autenticado.');
    }
  };

  if (loading) return <h3 style={{ color: 'var(--color-dark)' }}>Cargando tu información... ⏳</h3>;

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
      <h2 style={{ color: 'var(--color-secondary)', marginBottom: '20px' }}>💳 Mi Billetera y Actividad</h2>

      {/* SECCIÓN SUPERIOR: TARJETAS DE SALDO */}
      <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap', marginBottom: '40px' }}>
        
        {/* Tarjeta de Saldos */}
        <div style={{ flex: '1 1 300px', backgroundColor: 'var(--color-dark)', color: 'var(--color-light)', padding: '30px', borderRadius: '12px', boxShadow: '0 4px 10px rgba(0,0,0,0.2)' }}>
          <h3 style={{ margin: '0 0 5px 0', color: 'var(--color-accent)' }}>Saldo Disponible</h3>
          <p style={{ fontSize: '2.5rem', fontWeight: 'bold', margin: '0 0 20px 0' }}>
            ${wallet?.availableBalance?.toFixed(2) || '0.00'}
          </p>
          <div style={{ borderTop: '1px solid rgba(255,255,255,0.2)', paddingTop: '15px' }}>
            <p style={{ margin: '0', fontSize: '0.9rem', color: '#ccc' }}>Fondos Retenidos en Pujas Activas</p>
            <p style={{ fontSize: '1.2rem', fontWeight: 'bold', margin: '5px 0 0 0', color: 'var(--color-primary)' }}>
              ${wallet?.heldFunds?.toFixed(2) || '0.00'}
            </p>
          </div>
        </div>

        {/* Tarjeta para Cargar Saldo */}
        <div style={{ flex: '1 1 300px', backgroundColor: 'white', padding: '30px', borderRadius: '12px', borderTop: '5px solid var(--color-primary)', boxShadow: '0 4px 6px rgba(0,0,0,0.05)' }}>
          <h3 style={{ margin: '0 0 20px 0', color: 'var(--color-dark)' }}>Cargar Dinero</h3>
          <form onSubmit={handleAddFunds} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '8px', color: '#666', fontWeight: 'bold' }}>Monto a ingresar ($)</label>
              <input 
                type="number" step="0.01" min="1" required
                value={amountToAdd}
                onChange={(e) => setAmountToAdd(e.target.value)}
                style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ccc', boxSizing: 'border-box', fontSize: '1.1rem' }}
                placeholder="Ej: 500.00"
              />
            </div>
            <button 
              type="submit" 
              style={{ padding: '14px', backgroundColor: 'var(--color-primary)', color: 'white', border: 'none', borderRadius: '6px', fontWeight: 'bold', fontSize: '1rem', cursor: 'pointer', transition: '0.2s' }}
            >
              Depositar Fondos
            </button>
          </form>
        </div>
      </div>

      {/* NUEVA SECCIÓN MEDIA: SUBASTAS GANADAS */}
      <div style={{ backgroundColor: 'white', padding: '30px', borderRadius: '12px', boxShadow: '0 4px 6px rgba(0,0,0,0.05)', marginBottom: '30px', borderLeft: '5px solid #28a745' }}>
        <h3 style={{ margin: '0 0 20px 0', color: 'var(--color-dark)', borderBottom: '2px solid #eee', paddingBottom: '10px' }}>
          🏆 Mis Subastas Ganadas
        </h3>
        
        {wonAuctions.length === 0 ? (
          <p style={{ color: '#888', textAlign: 'center', padding: '20px 0' }}>Aún no has ganado ninguna subasta.</p>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
              <thead>
                <tr style={{ backgroundColor: '#f9f9f9', color: '#555' }}>
                  <th style={{ padding: '12px', borderBottom: '2px solid #eee' }}>ID Subasta</th>
                  <th style={{ padding: '12px', borderBottom: '2px solid #eee' }}>Fecha de Cierre</th>
                  <th style={{ padding: '12px', borderBottom: '2px solid #eee' }}>Monto Ganador</th>
                </tr>
              </thead>
              <tbody>
                {wonAuctions.map((auction) => (
                  <tr key={auction.id} style={{ borderBottom: '1px solid #eee', transition: 'background-color 0.2s' }} onMouseOver={(e) => e.currentTarget.style.backgroundColor = '#f9f9f9'} onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}>
                    <td style={{ padding: '12px', fontWeight: 'bold' }}>
                      <Link 
                        to={`/auctions/${auction.id}`} 
                        style={{ color: '#28a745', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '5px' }}
                        title="Ver detalles del producto ganado"
                      >
                        #{auction.id} 🔗
                      </Link>
                    </td>
                    <td style={{ padding: '12px', color: '#666' }}>
                      {new Date(auction.endTime).toLocaleDateString()} a las {new Date(auction.endTime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}
                    </td>
                    <td style={{ padding: '12px', fontWeight: 'bold', color: 'var(--color-dark)' }}>
                      ${auction.currentHighestBid.toFixed(2)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* SECCIÓN INFERIOR: HISTORIAL DE PUJAS */}
      <div style={{ backgroundColor: 'white', padding: '30px', borderRadius: '12px', boxShadow: '0 4px 6px rgba(0,0,0,0.05)' }}>
        <h3 style={{ margin: '0 0 20px 0', color: 'var(--color-dark)', borderBottom: '2px solid #eee', paddingBottom: '10px' }}>
          🕒 Mi Historial de Pujas
        </h3>
        
        {myBids.length === 0 ? (
          <p style={{ color: '#888', textAlign: 'center', padding: '20px 0' }}>Aún no has participado en ninguna subasta.</p>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
              <thead>
                <tr style={{ backgroundColor: '#f9f9f9', color: '#555' }}>
                  <th style={{ padding: '12px', borderBottom: '2px solid #eee' }}>Fecha</th>
                  <th style={{ padding: '12px', borderBottom: '2px solid #eee' }}>ID Subasta</th>
                  <th style={{ padding: '12px', borderBottom: '2px solid #eee' }}>Monto Ofertado</th>
                </tr>
              </thead>
              <tbody>
                {[...myBids]
                  .sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())
                  .map((bid) => (
                  <tr key={bid.id} style={{ borderBottom: '1px solid #eee', transition: 'background-color 0.2s' }} onMouseOver={(e) => e.currentTarget.style.backgroundColor = '#f9f9f9'} onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}>
                    <td style={{ padding: '12px', color: '#666' }}>
                      {new Date(bid.timestamp).toLocaleDateString()} a las {new Date(bid.timestamp).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}
                    </td>
                    
                    <td style={{ padding: '12px', fontWeight: 'bold' }}>
                      <Link 
                        to={`/auctions/${bid.auctionId}`} 
                        style={{ color: 'var(--color-primary)', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '5px' }}
                        title="Ir a la subasta"
                      >
                        #{bid.auctionId} 🔗
                      </Link>
                    </td>

                    <td style={{ padding: '12px', fontWeight: 'bold', color: 'var(--color-dark)' }}>
                      ${bid.amount.toFixed(2)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

    </div>
  );
};