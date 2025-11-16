import apiClient from '../config/apiClient';

export interface MarketData {
  symbol: string;
  price: number;
  change: number;
  changePercent: number;
  high: number;
  low: number;
  volume: number;
  timestamp: string;
}

export interface HistoricalData {
  date: string;
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
}

export const marketService = {
  getMarketData: async (symbol: string): Promise<MarketData> => {
    const response = await apiClient.get(`/api/market/data/${symbol}`);
    return response.data;
  },

  searchSymbols: async (query: string): Promise<any[]> => {
    const response = await apiClient.get('/api/market/search', {
      params: { q: query },
    });
    return response.data;
  },

  getHistoricalData: async (symbol: string, period: string = '1M'): Promise<HistoricalData[]> => {
    const response = await apiClient.get(`/api/market/history/${symbol}`, {
      params: { period },
    });
    return response.data;
  },

  getTopMovers: async (): Promise<MarketData[]> => {
    const response = await apiClient.get('/api/market/top-movers');
    return response.data;
  },

  getTrendingSymbols: async (): Promise<MarketData[]> => {
    const response = await apiClient.get('/api/market/trending');
    return response.data;
  },
};
