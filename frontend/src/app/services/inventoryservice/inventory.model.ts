import { SpringDataPaging } from "../general/paging.model";

export interface Product {
  id: string;
  name: string;
  price: number;
}

export interface ProductPage extends SpringDataPaging<Product> {}
