import { Outlet, Link, useNavigate } from 'react-router-dom';
import { authService } from '../../../features/auth/services/auth.service';

export const MainLayout = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    // 1. Borramos el token y los datos del localStorage usando tu servicio
    authService.logout();
    
    // 2. Redirigimos al usuario a la pantalla de login
    navigate('/login');
  };

  // Recuperamos el email del usuario para mostrarlo en la barra superior
  const userEmail = localStorage.getItem('email') || 'Usuario Demo';

  return (
    <div style={{ display: 'flex', height: '100vh', backgroundColor: 'var(--color-light)' }}>
      
      {/* Sidebar (Menú Lateral) */}
      <aside style={{ 
        width: '250px', 
        backgroundColor: 'var(--color-dark)', 
        color: 'var(--color-light)', 
        padding: '20px',
        display: 'flex',
        flexDirection: 'column' // Importante para que el flex funcione en vertical
      }}>
        <h2 style={{ color: 'var(--color-accent)', marginBottom: '40px' }}>⚡ SubastasApp</h2>
        
        <nav style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          <Link to="/auctions" style={{ color: 'var(--color-light)', textDecoration: 'none', fontSize: '1.1rem' }}>
            🛒 Catálogo de Subastas
          </Link>
          <Link 
              to="/my-publications" 
              style={{ color: 'var(--color-light)', textDecoration: 'none', fontSize: '1.1rem' }}
            >
              📦 Mis Publicaciones
            </Link>
          <Link to="/wallet" style={{ color: 'var(--color-light)', textDecoration: 'none', fontSize: '1.1rem' }}>
            💳 Mi Billetera y Actividad
          </Link>
        </nav>

        {/* Contenedor del Botón de Cerrar Sesión con marginTop: auto */}
        <div style={{ marginTop: 'auto', paddingTop: '20px', borderTop: '1px solid #444' }}>
          <button 
            onClick={handleLogout}
            style={{
              width: '100%',
              padding: '10px',
              backgroundColor: 'transparent',
              color: '#ff4d4f', // Color rojo para acción destructiva
              border: '1px solid #ff4d4f',
              borderRadius: '6px',
              cursor: 'pointer',
              fontWeight: 'bold',
              fontSize: '1rem',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '10px',
              transition: 'all 0.2s'
            }}
            onMouseOver={(e) => {
              e.currentTarget.style.backgroundColor = '#ff4d4f';
              e.currentTarget.style.color = 'white';
            }}
            onMouseOut={(e) => {
              e.currentTarget.style.backgroundColor = 'transparent';
              e.currentTarget.style.color = '#ff4d4f';
            }}
          >
            🚪 Cerrar Sesión
          </button>
        </div>
      </aside>

      {/* Área de contenido principal */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
        
        {/* Topbar (Barra Superior) */}
        <header style={{ 
          height: '60px', 
          backgroundColor: 'var(--color-secondary)', 
          color: 'var(--color-light)', 
          display: 'flex', 
          alignItems: 'center', 
          padding: '0 20px', 
          justifyContent: 'flex-end',
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
        }}>
          <span style={{ fontWeight: 'bold' }}>👤 {userEmail}</span>
        </header>

        {/* Aquí se inyectarán las páginas dinámicas (Subastas, Wallet, etc.) */}
        <main style={{ padding: '30px', overflowY: 'auto', flex: 1 }}>
          <Outlet />
        </main>
        
      </div>
    </div>
  );
};