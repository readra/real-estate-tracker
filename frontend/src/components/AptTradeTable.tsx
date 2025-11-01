import React from 'react';
import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  Typography,
  Box,
  Chip,
} from '@mui/material';
import { AptTrade } from '../types';

interface AptTradeTableProps {
  trades: AptTrade[];
  totalElements: number;
  page: number;
  size: number;
  onPageChange: (page: number) => void;
  onSizeChange: (size: number) => void;
}

const AptTradeTable: React.FC<AptTradeTableProps> = ({
  trades,
  totalElements,
  page,
  size,
  onPageChange,
  onSizeChange,
}) => {
  const formatAmount = (amount: string) => {
    const num = parseInt(amount.replace(/,/g, ''));
    if (num >= 10000) {
      return `${(num / 10000).toFixed(1)}억`;
    }
    return `${num.toLocaleString()}만`;
  };

  const formatDate = (dateStr: string) => {
    if (!dateStr || dateStr.length < 8) return dateStr;
    return `${dateStr.substring(0, 4)}-${dateStr.substring(4, 6)}-${dateStr.substring(6, 8)}`;
  };

  if (trades.length === 0) {
    return (
      <Paper sx={{ p: 3, textAlign: 'center' }}>
        <Typography color="text.secondary">
          검색 결과가 없습니다.
        </Typography>
      </Paper>
    );
  }

  return (
    <Paper>
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>아파트명</TableCell>
              <TableCell>거래금액</TableCell>
              <TableCell>전용면적</TableCell>
              <TableCell>층</TableCell>
              <TableCell>건축년도</TableCell>
              <TableCell>거래일</TableCell>
              <TableCell>지번</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {trades.map((trade) => (
              <TableRow
                key={trade.id}
                hover
                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
              >
                <TableCell>
                  <Box>
                    <Typography variant="body2" fontWeight="bold">
                      {trade.apartmentName}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {trade.dong}
                    </Typography>
                  </Box>
                </TableCell>
                <TableCell>
                  <Typography variant="body2" fontWeight="bold" color="primary">
                    {formatAmount(trade.transactionAmount)}
                  </Typography>
                </TableCell>
                <TableCell>{trade.exclusiveArea.toFixed(2)}㎡</TableCell>
                <TableCell>{trade.floor}</TableCell>
                <TableCell>{trade.buildingYear}</TableCell>
                <TableCell>{formatDate(trade.transactionDate)}</TableCell>
                <TableCell>
                  <Typography variant="caption">{trade.jibun}</Typography>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        component="div"
        count={totalElements}
        page={page}
        onPageChange={(_, newPage) => onPageChange(newPage)}
        rowsPerPage={size}
        onRowsPerPageChange={(e) => onSizeChange(parseInt(e.target.value, 10))}
        rowsPerPageOptions={[10, 20, 50, 100]}
        labelRowsPerPage="페이지당 행 수:"
        labelDisplayedRows={({ from, to, count }) =>
          `${from}-${to} / 총 ${count}개`
        }
      />
    </Paper>
  );
};

export default AptTradeTable;
