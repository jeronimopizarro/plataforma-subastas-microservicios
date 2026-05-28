import React, { useEffect, useState } from 'react';
import { walletService } from '../services/wallet.service';
import type { Wallet } from '../types/wallet.types';

export const WalletPage = () => {
  const [wallet, setWallet] = useState<Wallet | null>(null);
  const [amountToAdd, setAmountToAdd] = useState('');
  const [loading, setLoading] = useState(true);

  // Como el login es un mock, usaremos el ID 1 para probar la lógica en base de datos
  const currentUsername = localStorage.getItem('subastas_username') || '1';
  const CURRENT_USER_ID = parseInt(currentUsername, 10);

  const loadWallet = async () => {
    try {
      setLoading(true);
      const data = await walletService.getWallet(CURRENT_USER_ID);
      setWallet(data);
    } catch (err: any) {
      // Si el backend tira 404 es porque el usuario es nuevo y no tiene billetera.
      // Tu AddFundsUseCase la creará automáticamente cuando cargue saldo.
      if (err.response?.status === 404) {
        setWallet({ id: 0, userId: CURRENT_USER_ID, balance: 0, heldFunds: 0 });
      } else {
        console.error("Error al cargar la billetera", err);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadWallet();
  }, []);

  const handleAddFunds = async (e: React.FormEvent) => {
    e.preventDefault();
    const amount = parseFloat(amountToAdd);
    if (isNaN(amount) || amount <= 0) return;

    try {
      await walletService.addFunds(CURRENT_USER_ID, amount); 
      setAmountToAdd('');
      loadWallet(); 
    } catch (err) {
      alert('Hubo un error al intentar agregar fondos.');
      console.error(err);
    }
  };

  if (loading) return <h3 style={{ color: 'var(--color-dark)' }}>Cargando billetera... ⏳</h3>;

  return (
    <div>
      <h2 style={{ color: 'var(--color-secondary)', marginBottom: '20px' }}>💳 Mi Billetera Virtual</h2>

      <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap' }}>
        
        {/* Tarjeta de Saldos */}
        <div style={{ flex: '1 1 300px', backgroundColor: 'var(--color-dark)', color: 'var(--color-light)', padding: '30px', borderRadius: '12px', boxShadow: '0 4px 10px rgba(0,0,0,0.2)' }}>
          <h3 style={{ margin: '0 0 5px 0', color: 'var(--color-accent)' }}>Saldo Disponible</h3>
          <p style={{ fontSize: '2.5rem', fontWeight: 'bold', margin: '0 0 20px 0' }}>
            ${wallet?.balance?.toFixed(2) || '0.00'}
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
                type="number" 
                step="0.01"
                min="1"
                value={amountToAdd}
                onChange={(e) => setAmountToAdd(e.target.value)}
                required
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
    </div>
  );
};