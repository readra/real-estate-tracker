// 아파트 실거래 타입
export interface AptTrade {
  id: number;
  lawdCode: string;
  apartmentName: string;
  transactionAmount: string;
  buildingYear: number;
  transactionDate: string;
  exclusiveArea: number;
  floor: string;
  dong: string;
  jibun: string;
  regionalCode: string;
  cancelDealType: string;
  registrationDate: string;
  requesterGbn: string;
  transactionType: string;
}

// 검색 조건 타입
export interface SearchCondition {
  lawdCode: string;
  startYearMonth?: string;
  endYearMonth?: string;
  startTransactionAmount?: number;
  endTransactionAmount?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

// 페이지네이션 응답 타입
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// 지역 코드 타입
export interface Region {
  code: string;
  name: string;
  parentCode?: string;
}
