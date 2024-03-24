import { Order, OrderPage } from "../services/orderservice/order.model";
import { MOCKDATA_INVENTORY } from "./inventory.mockdata";

export const MOCKDATA_ORDER: Order[] = [
  {
    id: "mock_inventory_1",
    orderDateTime: new Date("2023-01-01T23:59:00"),
    product: MOCKDATA_INVENTORY[0],
  },
  {
    id: "mock_inventory_2",
    orderDateTime: new Date("2023-01-01T23:59:00"),
    product: MOCKDATA_INVENTORY[1],
  },
];

export const MOCKDATA_ORDER_PAGE: OrderPage = {
  content: MOCKDATA_ORDER,
  pageable: {
    pageNumber: 0,
    pageSize: 20,
    sort: {
      sorted: false,
      unsorted: true,
      empty: true,
    },
    offset: 0,
    paged: true,
    unpaged: false,
  },
  totalPages: 1,
  totalElements: 2,
  last: true,
  size: 20,
  number: 0,
  sort: {
    sorted: false,
    unsorted: true,
    empty: true,
  },
  numberOfElements: 2,
  first: true,
  empty: false,
};
