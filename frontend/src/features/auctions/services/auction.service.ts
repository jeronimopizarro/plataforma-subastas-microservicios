import { apiClient } from '../../../shared/services/api';
import type { Auction } from '../types/auction.types';
import type { Product } from '../types/product.types';

export const auctionService = {
  getActiveAuctions: async (): Promise<Auction[]> => {
    const response = await apiClient.get<Auction[]>('/auctions', {
      params: { status: 'ACTIVE' }
    });
    return response.data;
  },

  // Nueva función para buscar el producto
  getProductById: async (productId: number): Promise<Product> => {
    const response = await apiClient.get<Product>(`/products/${productId}`);
    return response.data;
  }
};