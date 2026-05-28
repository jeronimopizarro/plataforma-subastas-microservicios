import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { MainLayout } from '../shared/components/layout/MainLayout';
import { LoginPage } from '../features/auth/pages/LoginPage';
import { authService } from '../features/auth/services/auth.service';
import { AuctionsPage } from '../features/auctions/pages/AuctionsPage';
import type { ReactNode } from 'react';
import { WalletPage } from '../features/wallet/pages/WalletPage';

// Un "Guard" que protege las rutas privadas
const ProtectedRoute = ({ children }: { children: ReactNode }) => {
  if (!authService.isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }
  return children; // Como ReactNode sabe manejar componentes, esto no da error
};

// Componentes temporales
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

        {/* Rutas protegidas */}
        <Route 
          path="/" 
          element={
            <ProtectedRoute>
              <MainLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<Navigate to="/auctions" replace />} />
          <Route path="auctions" element={<AuctionsPage />} />
          <Route path="wallet" element={<WalletPage />} />
          <Route path="bids" element={<Placeholder title="Historial de Pujas" />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};