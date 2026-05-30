import { apiClient } from '../../../shared/services/api';
import type { Auction } from '../types/auction.types';
import type { Product } from '../types/product.types';

export const auctionService = {
  // Obtener subastas filtradas por estado (ACTIVE o FINISHED)
  getAuctionsByStatus: async (status: string): Promise<Auction[]> => {
    const response = await apiClient.get<Auction[]>(`/auctions?status=${status}`);
    return response.data;
  },

  // Obtener el detalle de un producto específico (para mostrar en la Card)
  getProductById: async (productId: number): Promise<Product> => {
    const response = await apiClient.get<Product>(`/products/${productId}`);
    return response.data;
  },

  // (Preparando el terreno para después) Obtener detalle de una subasta
  getAuctionById: async (auctionId: number): Promise<Auction> => {
    const response = await apiClient.get<Auction>(`/auctions/${auctionId}`);
    return response.data;
  }
};