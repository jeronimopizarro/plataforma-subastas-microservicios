import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../services/auth.service';

export const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);
    
    try {
      await authService.register({ email, password, role: 'USER' });
      setSuccess(true);
      setTimeout(() => navigate('/login'), 2000);
    } catch (err: any) {
      setError('Error al registrar usuario. Intenta con otro correo o verifica la conexión.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', backgroundColor: 'var(--color-dark)' }}>
      <div style={{ backgroundColor: 'var(--color-light)', padding: '40px', borderRadius: '12px', width: '100%', maxWidth: '400px', boxShadow: '0 4px 6px rgba(0,0,0,0.3)' }}>
        <h2 style={{ color: 'var(--color-secondary)', textAlign: 'center', marginBottom: '30px' }}>⚡ Crea tu Cuenta</h2>
        
        {success && (
          <div style={{ color: '#155724', backgroundColor: '#d4edda', border: '1px solid #c3e6cb', padding: '10px', borderRadius: '6px', marginBottom: '20px', textAlign: 'center', fontWeight: 'bold' }}>
            ¡Registro exitoso! Redirigiendo...
          </div>
        )}
        
        {error && (
          <div style={{ color: 'var(--color-light)', backgroundColor: 'var(--color-primary)', padding: '10px', borderRadius: '6px', marginBottom: '20px', textAlign: 'center' }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '8px', color: 'var(--color-dark)', fontWeight: 'bold' }}>Correo Electrónico</label>
            <input 
              type="email" 
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required 
              style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ccc', boxSizing: 'border-box' }}
              placeholder="ej: usuario@correo.com"
              disabled={isLoading || success}
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
              disabled={isLoading || success}
            />
          </div>

          <button 
            type="submit" 
            disabled={isLoading || success} 
            style={{ marginTop: '10px', padding: '14px', backgroundColor: (isLoading || success) ? '#ccc' : 'var(--color-primary)', color: 'var(--color-light)', border: 'none', borderRadius: '6px', fontSize: '1.1rem', fontWeight: 'bold', cursor: (isLoading || success) ? 'not-allowed' : 'pointer', transition: 'background-color 0.3s' }}
          >
            {isLoading ? 'Registrando...' : 'Registrarse'}
          </button>
        </form>

        <div style={{ marginTop: '20px', textAlign: 'center', color: 'var(--color-dark)', fontSize: '0.9rem' }}>
          ¿Ya tienes una cuenta?{' '}
          <Link to="/login" style={{ color: 'var(--color-secondary)', fontWeight: 'bold', textDecoration: 'none' }}>
            Inicia sesión aquí
          </Link>
        </div>
      </div>
    </div>
  );
};