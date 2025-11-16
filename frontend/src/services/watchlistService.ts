import apiClient from '../config/apiClient';

export interface WatchlistItem {
  id: string;
  symbol: string;
  name: string;
  currentPrice: number;
  addedAt: string;
}

export interface Watchlist {
  id: string;
  userId: string;
  items: WatchlistItem[];
  createdAt: string;
  updatedAt: string;
}

export const watchlistService = {
  getWatchlist: async (): Promise<Watchlist> => {
    const response = await apiClient.get('/api/watchlist');
    return response.data;
  },

  addToWatchlist: async (symbol: string): Promise<WatchlistItem> => {
    const response = await apiClient.post('/api/watchlist/add', { symbol });
    return response.data;
  },

  removeFromWatchlist: async (itemId: string): Promise<void> => {
    await apiClient.delete(`/api/watchlist/${itemId}`);
  },

  updateWatchlist: async (items: WatchlistItem[]): Promise<Watchlist> => {
    const response = await apiClient.put('/api/watchlist', { items });
    return response.data;
  },
};
