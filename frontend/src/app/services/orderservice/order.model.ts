import { SpringDataPaging } from "../general/paging.model";
import { Product } from "../inventoryservice/inventory.model";

export interface Order {
  id: string;
  orderDateTime: Date;
  product: Product;
}

/* eslint  @typescript-eslint/no-empty-object-type: "off" -- More specialised data type with generics */
export interface OrderPage extends SpringDataPaging<Order> {}
