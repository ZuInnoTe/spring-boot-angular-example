export class SpringDataSort {
  sorted: boolean;
  unsorted: boolean;
  empty: boolean;
}
export class SpringDataPageable {
  pageNumber: number;
  pageSize: number;
  sort: SpringDataSort;
  offset: number;
  paged: boolean;
  unpaged: boolean;
}

export class SpringDataPaging<Type> {
  content: Type[];
  pageable: SpringDataPageable;
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  sort: SpringDataSort;
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}
