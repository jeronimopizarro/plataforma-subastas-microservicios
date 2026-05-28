import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/auth.service';

export const LoginPage = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState(''); // <-- Ahora usamos username
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      await authService.login({ username, password }); // <-- Pasamos username
      navigate('/auctions');
    } catch (err) {
      setError('Credenciales incorrectas o servidor no disponible.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', backgroundColor: 'var(--color-dark)' }}>
      <div style={{ backgroundColor: 'var(--color-light)', padding: '40px', borderRadius: '12px', width: '100%', maxWidth: '400px', boxShadow: '0 4px 6px rgba(0,0,0,0.3)' }}>
        <h2 style={{ color: 'var(--color-secondary)', textAlign: 'center', marginBottom: '30px' }}>⚡ SubastasApp</h2>
        
        {error && <div style={{ color: 'var(--color-light)', backgroundColor: 'var(--color-primary)', padding: '10px', borderRadius: '6px', marginBottom: '20px', textAlign: 'center' }}>{error}</div>}

        <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '8px', color: 'var(--color-dark)', fontWeight: 'bold' }}>Nombre de Usuario</label>
            <input 
              type="text" 
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ccc', boxSizing: 'border-box' }}
              placeholder="ej: admin"
            />
          </div>
          
          <div>
            <label style={{ display: 'block', marginBottom: '8px', color: 'var(--color-dark)', fontWeight: 'bold' }}>Contraseña</label>
            <input 
              type="password" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ccc', boxSizing: 'border-box' }}
              placeholder="••••••••"
            />
          </div>

          <button type="submit" disabled={isLoading} style={{ marginTop: '10px', padding: '14px', backgroundColor: isLoading ? '#ccc' : 'var(--color-primary)', color: 'var(--color-light)', border: 'none', borderRadius: '6px', fontSize: '1.1rem', fontWeight: 'bold', cursor: isLoading ? 'not-allowed' : 'pointer', transition: 'background-color 0.3s' }}>
            {isLoading ? 'Iniciando...' : 'Ingresar'}
          </button>
        </form>
      </div>
    </div>
  );
};