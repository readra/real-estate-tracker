import axios from 'axios';
import { AptTrade, SearchCondition, PageResponse } from '../types';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 아파트 실거래 검색
export const searchAptTrades = async (
  condition: SearchCondition
): Promise<PageResponse<AptTrade>> => {
  const response = await apiClient.get('/api/v1/apt-trades', {
    params: condition,
  });
  return response.data.data; // ApiResponse의 data 필드 접근
};

// 특정 아파트 거래 이력 조회
export const getAptTradeHistory = async (
  apartmentName: string,
  dong: string
): Promise<AptTrade[]> => {
  const response = await apiClient.get('/api/v1/apt-trades/history', {
    params: { apartmentName, dong },
  });
  return response.data.data;
};

// 거래 빈도 높은 아파트 조회
export const getFrequentlyTradedApartments = async (
  lawdCode: string,
  months: number,
  minTransactionCount: number
): Promise<AptTrade[]> => {
  const response = await apiClient.get('/api/v1/apt-trades/risky', {
    params: { lawdCode, months, minTransactionCount },
  });
  return response.data.data;
};

export default apiClient;
