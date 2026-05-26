import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { MainLayout } from '../shared/components/layout/MainLayout';

// Componentes temporales (Placeholders) hasta que armemos los reales
const LoginPage = () => (
  <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', backgroundColor: 'var(--color-dark)' }}>
    <div style={{ backgroundColor: 'var(--color-light)', padding: '40px', borderRadius: '8px', textAlign: 'center' }}>
      <h2 style={{ color: 'var(--color-primary)' }}>Iniciar Sesión</h2>
      <p style={{ color: 'var(--color-dark)' }}>Próximamente conectaremos el JWT aquí.</p>
    </div>
  </div>
);

const Placeholder = ({ title }: { title: string }) => (
  <div>
    <h1 style={{ color: 'var(--color-secondary)' }}>{title}</h1>
    <p style={{ color: 'var(--color-dark)' }}>Contenido en construcción...</p>
  </div>
);

export const AppRouter = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Ruta pública */}
        <Route path="/login" element={<LoginPage />} />

        {/* Rutas protegidas (Envueltas por el MainLayout) */}
        <Route path="/" element={<MainLayout />}>
          <Route index element={<Navigate to="/auctions" replace />} />
          <Route path="auctions" element={<Placeholder title="Catálogo de Subastas" />} />
          <Route path="wallet" element={<Placeholder title="Mi Billetera Virtual" />} />
          <Route path="bids" element={<Placeholder title="Historial de Pujas" />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};