import React, { useState } from 'react';
import { auctionService } from '../services/auction.service';

interface CreateAuctionModalProps {
  onClose: () => void;
  onSuccess: () => void; 
}

export const CreateAuctionModal: React.FC<CreateAuctionModalProps> = ({ onClose, onSuccess }) => {
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const [formData, setFormData] = useState({
    title: '', description: '', condition: 'NUEVO', imageUrl: '',
    startingPrice: '', startTime: '', endTime: ''
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (errorMessage) setErrorMessage('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(''); 

    // --- NUEVO: Validar campos vacíos en React ---
    if (!formData.title || !formData.description || !formData.startingPrice || !formData.startTime || !formData.endTime) {
      setErrorMessage('Por favor, completá todos los campos obligatorios.');
      return;
    }

    const start = new Date(formData.startTime);
    const end = new Date(formData.endTime);
    const now = new Date();

    if (end <= start) {
      setErrorMessage('La fecha de finalización debe ser posterior a la fecha de inicio.');
      return; 
    }

    if (end <= now) {
      setErrorMessage('La fecha de finalización no puede estar en el pasado.');
      return; 
    }

    setLoading(true);
    try {
      const product = await auctionService.createProduct({
        title: formData.title,
        description: formData.description,
        condition: formData.condition,
        imageUrl: formData.imageUrl || 'https://via.placeholder.com/300' 
      });

      await auctionService.createAuction({
        productId: product.id,
        startingPrice: parseFloat(formData.startingPrice),
        startTime: formData.startTime,
        endTime: formData.endTime
      });

      onSuccess(); 
      onClose();   
    } catch (error) {
      console.error('Error creando subasta:', error);
      setErrorMessage('Hubo un error al comunicar con el servidor. Revisá los datos e intentá nuevamente.');
    } finally {
      setLoading(false);
    }
  };

  const getCurrentDateTimeString = () => {
    const now = new Date();
    const tzOffset = now.getTimezoneOffset() * 60000; 
    return new Date(now.getTime() - tzOffset).toISOString().slice(0, 16);
  };

  const minDateTime = getCurrentDateTimeString();

  return (
    <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 }}>
      <div style={{ backgroundColor: 'white', padding: '30px', borderRadius: '12px', width: '100%', maxWidth: '500px', maxHeight: '90vh', overflowY: 'auto' }}>
        <h2 style={{ marginTop: 0, color: 'var(--color-dark)' }}>Crear Nueva Subasta</h2>
        
        {/* --- CAMBIO CLAVE: Agregamos noValidate al form --- */}
        <form onSubmit={handleSubmit} noValidate style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          
          {/* SECCIÓN PRODUCTO */}
          <h4 style={{ margin: '10px 0 0 0', color: 'var(--color-secondary)' }}>Detalles del Producto</h4>
          <input name="title" placeholder="Título del producto" value={formData.title} onChange={handleChange} required style={inputStyle} />
          <textarea name="description" placeholder="Descripción" value={formData.description} onChange={handleChange} required style={{...inputStyle, minHeight: '80px'}} />
          <select name="condition" value={formData.condition} onChange={handleChange} style={inputStyle}>
            <option value="NUEVO">Nuevo</option>
            <option value="USADO">Usado</option>
          </select>
          <input name="imageUrl" placeholder="URL de la imagen (opcional)" value={formData.imageUrl} onChange={handleChange} style={inputStyle} />

          {/* SECCIÓN SUBASTA */}
          <h4 style={{ margin: '10px 0 0 0', color: 'var(--color-secondary)' }}>Configuración de Subasta</h4>
          <input type="number" name="startingPrice" placeholder="Precio Base ($)" min="1" step="0.01" value={formData.startingPrice} onChange={handleChange} required style={inputStyle} />
          
          <div style={{ display: 'flex', gap: '10px' }}>
            <div style={{ flex: 1 }}>
              <label style={{ fontSize: '0.8rem', color: '#666' }}>Fecha de Inicio</label>
              <input 
                type="datetime-local" 
                name="startTime" 
                min={minDateTime} 
                value={formData.startTime} 
                onChange={handleChange} 
                required 
                style={inputStyle} 
              />
            </div>
            <div style={{ flex: 1 }}>
              <label style={{ fontSize: '0.8rem', color: '#666' }}>Fecha de Fin</label>
              <input 
                type="datetime-local" 
                name="endTime" 
                min={formData.startTime || minDateTime} 
                value={formData.endTime} 
                onChange={handleChange} 
                required 
                style={inputStyle} 
              />
            </div>
          </div>

          {/* --- CAJA DE MENSAJE DE ERROR VISUAL --- */}
          {errorMessage && (
            <div style={{ 
              backgroundColor: '#ffebee', 
              color: '#d32f2f', 
              padding: '10px', 
              borderRadius: '6px', 
              border: '1px solid #ffcdd2',
              fontSize: '0.9rem',
              fontWeight: 'bold',
              marginTop: '5px'
            }}>
              ⚠️ {errorMessage}
            </div>
          )}

          <div style={{ display: 'flex', gap: '10px', marginTop: '15px' }}>
            <button type="button" onClick={onClose} style={{ flex: 1, padding: '10px', backgroundColor: '#e0e0e0', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>Cancelar</button>
            <button type="submit" disabled={loading} style={{ flex: 1, padding: '10px', backgroundColor: 'var(--color-primary)', color: 'white', border: 'none', borderRadius: '6px', cursor: loading ? 'not-allowed' : 'pointer', fontWeight: 'bold' }}>
              {loading ? 'Creando...' : 'Publicar Subasta'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

const inputStyle = { width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ccc', boxSizing: 'border-box' as const };