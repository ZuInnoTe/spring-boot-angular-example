import { SpringDataPaging } from "../general/paging.model";
import { Product } from "../inventoryservice/inventory.model";

export interface Order {
  id: string;
  orderDateTime: Date;
  product: Product;
}

export interface OrderPage extends SpringDataPaging<Order> {}
