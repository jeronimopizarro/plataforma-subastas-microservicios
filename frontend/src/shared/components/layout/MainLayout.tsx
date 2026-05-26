import { Outlet, Link } from 'react-router-dom';

export const MainLayout = () => {
  return (
    <div style={{ display: 'flex', height: '100vh', backgroundColor: 'var(--color-light)' }}>
      
      {/* Sidebar (Menú Lateral) */}
      <aside style={{ 
        width: '250px', 
        backgroundColor: 'var(--color-dark)', 
        color: 'var(--color-light)', 
        padding: '20px',
        display: 'flex',
        flexDirection: 'column'
      }}>
        <h2 style={{ color: 'var(--color-accent)', marginBottom: '40px' }}>⚡ SubastasApp</h2>
        
        <nav style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          <Link to="/auctions" style={{ color: 'var(--color-light)', textDecoration: 'none', fontSize: '1.1rem' }}>
            🛒 Subastas Activas
          </Link>
          <Link to="/wallet" style={{ color: 'var(--color-light)', textDecoration: 'none', fontSize: '1.1rem' }}>
            💳 Mi Billetera
          </Link>
          <Link to="/bids" style={{ color: 'var(--color-light)', textDecoration: 'none', fontSize: '1.1rem' }}>
            🔨 Mis Pujas
          </Link>
        </nav>
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
          <span style={{ fontWeight: 'bold' }}>👤 Usuario Demo</span>
        </header>

        {/* Aquí se inyectarán las páginas dinámicas (Subastas, Wallet, etc.) */}
        <main style={{ padding: '30px', overflowY: 'auto', flex: 1 }}>
          <Outlet />
        </main>
        
      </div>
    </div>
  );
};