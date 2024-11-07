import { HttpParams } from "@angular/common/http";

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

export class SpringDataPageRequest {
  size: number;
  page: number;
}

/**
 * Converts a SpringDataPageRequest to HttpParams to be passed to a request to the backend
 *
 * @param pageable SpringDataPageRequest object
 * @returns object converted to HttpParams to be used in an Angular HttpClient
 */
export function sddrToHttpParams(pageable?: SpringDataPageRequest): HttpParams {
  let httpParams = new HttpParams();
  if (pageable !== null && pageable !== undefined) {
    if (pageable.size !== null) {
      httpParams = httpParams.append("size", pageable.size);
    }
    if (pageable.page !== null) {
      httpParams = httpParams.append("page", pageable.page);
    }
  }
  return httpParams;
}
