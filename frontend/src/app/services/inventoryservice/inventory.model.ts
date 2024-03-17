import { SpringDataPaging } from "../general/paging.model";

export class Product {
  id: string;
  name: string;
  price: number;
}

export class ProductPage extends SpringDataPaging<Product> {}
