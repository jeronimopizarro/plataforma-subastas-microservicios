import { apiClient } from '../../../shared/services/api';
import type { Auction, CreateAuctionRequest } from '../types/auction.types';
import type { CreateProductRequest, Product } from '../types/product.types';

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

  // Obtener detalle de una subasta
  getAuctionById: async (auctionId: number): Promise<Auction> => {
    const response = await apiClient.get<Auction>(`/auctions/${auctionId}`);
    return response.data;
  },

  createProduct: async (productData: CreateProductRequest): Promise<Product> => {
    const response = await apiClient.post<Product>('/products', productData);
    return response.data;
  },

  createAuction: async (auctionData: CreateAuctionRequest): Promise<Auction> => {
    const response = await apiClient.post<Auction>('/auctions', auctionData);
    return response.data;
  },
  getWonAuctions: async (): Promise<Auction[]> => {
    const response = await apiClient.get<Auction[]>('/auctions/won');
    return response.data;
  }
};