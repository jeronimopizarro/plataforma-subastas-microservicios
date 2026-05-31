import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

import { auctionService } from '../services/auction.service';
import { walletService } from '../../wallet/services/wallet.service';
import { biddingService } from '../services/bidding.service';

import type { Auction } from '../types/auction.types';
import type { Product } from '../types/product.types';

export const AuctionDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [auction, setAuction] = useState<Auction | null>(null);
  const [product, setProduct] = useState<Product | null>(null);
  const [availableBalance, setAvailableBalance] = useState<number>(0);
  
  const [bidAmount, setBidAmount] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [bidError, setBidError] = useState('');
  
  // Nuevo estado para el indicador visual de conexión WebSocket
  const [isConnected, setIsConnected] = useState(false);

  const loadWallet = async () => {
    try {
      const walletData = await walletService.getWallet();
      setAvailableBalance(walletData.availableBalance || 0);
    } catch (err) {
      console.error("Error al cargar la billetera", err);
    }
  };

  // 1. Efecto inicial: Cargar datos REST
  useEffect(() => {
    const fetchDetails = async () => {
      if (!id) return;
      try {
        setLoading(true);
        const auctionData = await auctionService.getAuctionById(parseInt(id, 10));
        setAuction(auctionData);

        const productData = await auctionService.getProductById(auctionData.productId);
        setProduct(productData);

        await loadWallet();
      } catch (err) {
        setError('No se pudo cargar la información de la subasta.');
      } finally {
        setLoading(false);
      }
    };

    fetchDetails();
  }, [id]);

  // 2. Efecto WebSocket: Conectar y escuchar pujas en vivo
  useEffect(() => {
    if (!id) return;

    // Conectamos apuntando al API Gateway
    const socketUrl = 'http://localhost:9000/ws-bidding';
    
    const stompClient = new Client({
      webSocketFactory: () => new SockJS(socketUrl),
      reconnectDelay: 5000, // Intentar reconectar si se corta
      onConnect: () => {
        setIsConnected(true);
        // Nos suscribimos al topic publicado por tu WebSocketBidEventPublisher
        stompClient.subscribe(`/topic/auctions/${id}`, (message) => {
          const newBidData = JSON.parse(message.body);
          console.log("¡Nueva puja recibida en vivo!", newBidData);
          
          // Actualizamos mágicamente el estado sin recargar la página
          setAuction(prev => prev ? { ...prev, currentHighestBid: newBidData.amount } : null);
        });
      },
      onDisconnect: () => {
        setIsConnected(false);
      }
    });

    stompClient.activate();

    // Limpieza al desmontar el componente (salir de la vista)
    return () => {
      stompClient.deactivate();
    };
  }, [id]);

  const handleBidSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setBidError('');

    const amount = parseFloat(bidAmount);
    if (isNaN(amount) || amount <= 0) return;

    if (auction && amount <= auction.currentHighestBid) {
      setBidError(`La oferta debe ser mayor a $${auction.currentHighestBid.toFixed(2)}`);
      return;
    }
    if (amount > availableBalance) {
      setBidError('No tenés saldo suficiente en tu billetera.');
      return;
    }

    // Enviamos la petición real al Backend
    try {
      if (auction) {
        await biddingService.placeBid(auction.id, amount);
        setBidAmount(''); // Limpiamos el input
        
        // Recargamos la billetera porque ahora tenemos fondos retenidos
        await loadWallet(); 
        
        // No necesitamos actualizar el precio actual a mano porque el WebSocket 
        // nos lo va a avisar instantáneamente desde el servidor.
      }
    } catch (err: any) {
      // Capturamos las excepciones que arroja tu backend
      setBidError(err.response?.data?.message || 'Error al intentar procesar la puja.');
    }
  };

  if (loading) return <div style={{ padding: '40px', textAlign: 'center' }}>Cargando subasta... ⏳</div>;
  if (error || !auction || !product) return <div style={{ color: 'red', textAlign: 'center' }}>{error || 'Subasta no encontrada'}</div>;

  const isFinished = auction.status === 'FINISHED';

  return (
    <div style={{ maxWidth: '1100px', margin: '0 auto', padding: '20px' }}>
      
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <button onClick={() => navigate('/auctions')} style={{ background: 'none', border: 'none', color: 'var(--color-primary)', cursor: 'pointer', fontSize: '1rem', fontWeight: 'bold' }}>
          ← Volver al catálogo
        </button>
        
        {/* Indicador de conexión WebSocket */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.9rem', color: '#666' }}>
          <div style={{ width: '10px', height: '10px', borderRadius: '50%', backgroundColor: isConnected ? '#28a745' : '#dc3545', boxShadow: isConnected ? '0 0 5px #28a745' : 'none' }} />
          {isConnected ? 'Conectado en vivo' : 'Reconectando...'}
        </div>
      </div>

      <div style={{ display: 'flex', gap: '30px', flexWrap: 'wrap' }}>
        {/* Columna Izquierda: Producto (Igual que antes) */}
        <div style={{ flex: '1 1 500px', backgroundColor: 'white', padding: '40px', borderRadius: '12px', boxShadow: '0 4px 6px rgba(0,0,0,0.05)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '15px' }}>
            <span style={{ fontWeight: 'bold', color: '#666', border: '1px solid #ccc', padding: '5px 10px', borderRadius: '6px' }}>{product.condition}</span>
          </div>
          <h1 style={{ margin: '0 0 20px 0', color: 'var(--color-dark)', fontSize: '2.2rem' }}>{product.name}</h1>
          <p style={{ color: '#555', lineHeight: '1.6', fontSize: '1.1rem', whiteSpace: 'pre-line' }}>{product.description}</p>
        </div>

        {/* Columna Derecha: Panel de Pujas */}
        <div style={{ flex: '1 1 350px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
          
          <div style={{ backgroundColor: 'var(--color-dark)', color: 'white', padding: '30px', borderRadius: '12px', boxShadow: '0 4px 10px rgba(0,0,0,0.2)' }}>
            <h3 style={{ margin: '0 0 10px 0', color: 'var(--color-accent)' }}>
              {auction.status === 'SCHEDULED' ? 'Precio Base' : isFinished ? 'Precio Final' : 'Oferta Actual Ganadora'}
            </h3>
            
            {/* ESTE PRECIO SE ACTUALIZA SOLO VÍA WEBSOCKET */}
            <p style={{ fontSize: '3.5rem', fontWeight: 'bold', margin: '0', transition: 'color 0.3s' }}>
              ${auction.currentHighestBid.toFixed(2)}
            </p>
            
            <p style={{ margin: '15px 0 0 0', color: '#ccc', fontSize: '0.9rem', borderTop: '1px solid rgba(255,255,255,0.2)', paddingTop: '10px' }}>
              Estado del evento: <strong style={{ color: auction.status === 'ACTIVE' ? '#28a745' : 'white' }}>{auction.status}</strong>
            </p>
          </div>

          {!isFinished && (
            <div style={{ backgroundColor: 'white', padding: '30px', borderRadius: '12px', borderTop: '5px solid var(--color-primary)', boxShadow: '0 4px 6px rgba(0,0,0,0.05)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px', fontSize: '0.9rem', paddingBottom: '10px', borderBottom: '1px solid #eee' }}>
                <span style={{ color: '#666' }}>Tu saldo disponible:</span>
                <strong style={{ color: 'var(--color-secondary)' }}>${availableBalance.toFixed(2)}</strong>
              </div>

              <form onSubmit={handleBidSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                <div>
                  <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold', color: '#333' }}>Monto a ofertar ($)</label>
                  <input 
                    type="number" 
                    step="0.01"
                    value={bidAmount}
                    onChange={(e) => { setBidAmount(e.target.value); if (bidError) setBidError(''); }}
                    disabled={auction.status === 'SCHEDULED' || !isConnected}
                    placeholder={`Sugerido: $${(auction.currentHighestBid + 5).toFixed(2)}`}
                    style={{ width: '100%', padding: '14px', borderRadius: '6px', border: '1px solid #ccc', boxSizing: 'border-box', fontSize: '1.2rem' }}
                  />
                </div>

                {bidError && <div style={{ color: '#d32f2f', backgroundColor: '#ffebee', padding: '12px', borderRadius: '6px', fontSize: '0.9rem', fontWeight: 'bold' }}>⚠️ {bidError}</div>}

                <button 
                  type="submit" 
                  disabled={auction.status === 'SCHEDULED' || !isConnected}
                  style={{ padding: '16px', backgroundColor: auction.status === 'SCHEDULED' || !isConnected ? '#ccc' : 'var(--color-primary)', color: 'white', border: 'none', borderRadius: '8px', fontWeight: 'bold', fontSize: '1.2rem', cursor: auction.status === 'SCHEDULED' || !isConnected ? 'not-allowed' : 'pointer', marginTop: '10px' }}
                >
                  {auction.status === 'SCHEDULED' ? 'La subasta aún no inicia' : 'Realizar Puja Ahora'}
                </button>
              </form>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};