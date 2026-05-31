export interface Product {
  id: number;
  title: string;
  description: string;
  condition: string;
  imageUrl: string;
  sellerId: number;
  active: boolean;
}

export interface CreateProductRequest {
  title: string;
  description: string;
  condition: string;
  imageUrl: string;
}