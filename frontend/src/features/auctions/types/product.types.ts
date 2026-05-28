export interface Product {
  id: number;
  title: string;        // Cambiamos 'name' por 'title'
  description: string;
  condition: string;    // Agregamos 'condition' (ej: NUEVO, USADO)
  imageUrl: string;     // Agregamos la URL de la imagen
  sellerId: number;     // Agregamos el ID del vendedor
  active: boolean;
}