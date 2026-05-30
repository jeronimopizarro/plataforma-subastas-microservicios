export interface Product {
  id: number;
  name: string;
  description: string;
  condition: string;
  ownerId: number;
  active: boolean;
}

export interface CreateProductRequest {
  title: string;
  description: string;
  condition: string;
  imageUrl: string;
}