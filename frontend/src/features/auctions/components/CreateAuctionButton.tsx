import React, { useState } from 'react';
import { CreateAuctionModal } from './CreateAuctionModal';

interface CreateAuctionButtonProps {
  onSuccess: () => void;
}

export const CreateAuctionButton: React.FC<CreateAuctionButtonProps> = ({ onSuccess }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  return (
    <>
      <button 
        onClick={() => setIsModalOpen(true)}
        style={{ 
          backgroundColor: 'var(--color-primary)', 
          color: 'white', 
          padding: '10px 20px', 
          borderRadius: '8px', 
          border: 'none', 
          fontWeight: 'bold', 
          cursor: 'pointer',
          transition: 'background-color 0.2s'
        }}
      >
        + Nueva Subasta
      </button>

      {isModalOpen && (
        <CreateAuctionModal 
          onClose={() => setIsModalOpen(false)} 
          onSuccess={() => {
            setIsModalOpen(false);
            onSuccess();
          }} 
        />
      )}
    </>
  );
};