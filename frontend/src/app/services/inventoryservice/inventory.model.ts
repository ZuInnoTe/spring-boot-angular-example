import { SpringDataPaging } from "../general/paging.model";

export interface Product {
  id: string;
  name: string;
  price: number;
}

/* eslint  @typescript-eslint/no-empty-object-type: "off" -- More specialised data type with generics */
export interface ProductPage extends SpringDataPaging<Product> {}
