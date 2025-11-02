import React, { useState } from 'react';
import {
  Box,
  Button,
  TextField,
  Paper,
  Typography,
  Stack,
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import { SearchCondition } from '../types';

interface SearchFormProps {
  onSearch: (condition: SearchCondition) => void;
  loading?: boolean;
}

const SearchForm: React.FC<SearchFormProps> = ({ onSearch, loading = false }) => {
  const [lawdCode, setLawdCode] = useState('');
  const [startYearMonth, setStartYearMonth] = useState('');
  const [endYearMonth, setEndYearMonth] = useState('');
  const [minAmount, setMinAmount] = useState('');
  const [maxAmount, setMaxAmount] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    const condition: SearchCondition = {
      lawdCode,
      startYearMonth: startYearMonth || undefined,
      endYearMonth: endYearMonth || undefined,
      startTransactionAmount: minAmount ? Number(minAmount) : undefined,
      endTransactionAmount: maxAmount ? Number(maxAmount) : undefined,
    };

    onSearch(condition);
  };

  const handleReset = () => {
    setLawdCode('');
    setStartYearMonth('');
    setEndYearMonth('');
    setMinAmount('');
    setMaxAmount('');
  };

  return (
    <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
      <Typography variant="h6" gutterBottom>
        아파트 실거래가 검색
      </Typography>
      
      <form onSubmit={handleSubmit}>
        <Stack spacing={2}>
          <TextField
            fullWidth
            required
            label="지역 코드"
            value={lawdCode}
            onChange={(e) => setLawdCode(e.target.value)}
            placeholder="예: 11110 (서울 종로구)"
            helperText="5자리 법정동 코드 입력"
          />

          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              fullWidth
              type="month"
              label="시작 년월"
              value={startYearMonth}
              onChange={(e) => setStartYearMonth(e.target.value)}
              InputLabelProps={{ shrink: true }}
            />
            <TextField
              fullWidth
              type="month"
              label="종료 년월"
              value={endYearMonth}
              onChange={(e) => setEndYearMonth(e.target.value)}
              InputLabelProps={{ shrink: true }}
            />
          </Box>

          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              fullWidth
              type="number"
              label="최소 거래금액 (만원)"
              value={minAmount}
              onChange={(e) => setMinAmount(e.target.value)}
              placeholder="예: 50000"
            />
            <TextField
              fullWidth
              type="number"
              label="최대 거래금액 (만원)"
              value={maxAmount}
              onChange={(e) => setMaxAmount(e.target.value)}
              placeholder="예: 100000"
            />
          </Box>

          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
            <Button
              variant="outlined"
              onClick={handleReset}
              disabled={loading}
            >
              초기화
            </Button>
            <Button
              type="submit"
              variant="contained"
              startIcon={<SearchIcon />}
              disabled={loading}
            >
              {loading ? '검색 중...' : '검색'}
            </Button>
          </Box>
        </Stack>
      </form>
    </Paper>
  );
};

export default SearchForm;
