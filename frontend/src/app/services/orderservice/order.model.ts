import { SpringDataPaging } from "../general/paging.model";
import { Product } from "../inventoryservice/inventory.model";

export class Order {
  id: string;
  orderDateTime: Date;
  product: Product;
}

export class OrderPage extends SpringDataPaging<Order> {}
