import React, { useState } from 'react';
import { Box, Alert, CircularProgress } from '@mui/material';
import SearchForm from '../components/SearchForm';
import AptTradeTable from '../components/AptTradeTable';
import { searchAptTrades } from '../services/api';
import { AptTrade, SearchCondition } from '../types';

const SearchPage: React.FC = () => {
  const [trades, setTrades] = useState<AptTrade[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [currentCondition, setCurrentCondition] = useState<SearchCondition | null>(null);

  const handleSearch = async (condition: SearchCondition) => {
    setLoading(true);
    setError(null);
    setCurrentCondition(condition);
    setPage(0);

    console.log('Search request:', condition);

    try {
      const response = await searchAptTrades({
        ...condition,
        page: 0,
        size,
      });

      console.log('Search response:', response);
      setTrades(response.content);
      setTotalElements(response.totalElements);
    } catch (err: any) {
      console.error('Search error:', err);
      console.error('Error response:', err.response);
      setError(
        err.response?.data?.message || err.message || '검색 중 오류가 발생했습니다.'
      );
      setTrades([]);
      setTotalElements(0);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = async (newPage: number) => {
    if (!currentCondition) return;

    setLoading(true);
    setError(null);

    try {
      const response = await searchAptTrades({
        ...currentCondition,
        page: newPage,
        size,
      });

      setTrades(response.content);
      setPage(newPage);
    } catch (err: any) {
      setError(
        err.response?.data?.message || '페이지 이동 중 오류가 발생했습니다.'
      );
    } finally {
      setLoading(false);
    }
  };

  const handleSizeChange = async (newSize: number) => {
    if (!currentCondition) return;

    setLoading(true);
    setError(null);
    setSize(newSize);
    setPage(0);

    try {
      const response = await searchAptTrades({
        ...currentCondition,
        page: 0,
        size: newSize,
      });

      setTrades(response.content);
      setTotalElements(response.totalElements);
    } catch (err: any) {
      setError(
        err.response?.data?.message || '페이지 크기 변경 중 오류가 발생했습니다.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <SearchForm onSearch={handleSearch} loading={loading} />

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
          <CircularProgress />
        </Box>
      )}

      {!loading && trades.length > 0 && (
        <AptTradeTable
          trades={trades}
          totalElements={totalElements}
          page={page}
          size={size}
          onPageChange={handlePageChange}
          onSizeChange={handleSizeChange}
        />
      )}
    </Box>
  );
};

export default SearchPage;
