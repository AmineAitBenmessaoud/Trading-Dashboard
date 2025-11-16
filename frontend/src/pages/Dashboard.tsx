import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { marketService, MarketData } from '../services/marketService';
import { watchlistService, WatchlistItem } from '../services/watchlistService';
import '../styles/Dashboard.css';

export const Dashboard: React.FC = () => {
  const [topMovers, setTopMovers] = useState<MarketData[]>([]);
  const [watchlist, setWatchlist] = useState<WatchlistItem[]>([]);
  const [searchResults, setSearchResults] = useState<any[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        const [movers, wl] = await Promise.all([
          marketService.getTopMovers(),
          watchlistService.getWatchlist(),
        ]);
        setTopMovers(movers);
        setWatchlist(wl.items || []);
      } catch (err) {
        setError('Failed to load dashboard data');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const handleSearch = async (query: string) => {
    setSearchQuery(query);
    if (query.length < 2) {
      setSearchResults([]);
      return;
    }

    try {
      const results = await marketService.searchSymbols(query);
      setSearchResults(results);
    } catch (err) {
      console.error('Search failed:', err);
    }
  };

  const handleAddToWatchlist = async (symbol: string) => {
    try {
      await watchlistService.addToWatchlist(symbol);
      const updatedWatchlist = await watchlistService.getWatchlist();
      setWatchlist(updatedWatchlist.items || []);
      setSearchResults([]);
      setSearchQuery('');
    } catch (err) {
      console.error('Failed to add to watchlist:', err);
    }
  };

  const handleRemoveFromWatchlist = async (itemId: string) => {
    try {
      await watchlistService.removeFromWatchlist(itemId);
      setWatchlist(watchlist.filter((item) => item.id !== itemId));
    } catch (err) {
      console.error('Failed to remove from watchlist:', err);
    }
  };

  if (loading) return <div className="loading">Loading dashboard...</div>;

  return (
    <div className="dashboard">
      <div className="search-section">
        <h2>Search Stocks</h2>
        <div className="search-container">
          <input
            type="text"
            className="search-input"
            placeholder="Search symbols (e.g., AAPL, GOOGL)"
            value={searchQuery}
            onChange={(e) => handleSearch(e.target.value)}
          />
          {searchResults.length > 0 && (
            <div className="search-results">
              {searchResults.map((result) => (
                <div key={result.symbol} className="search-result-item">
                  <div>
                    <strong>{result.symbol}</strong>
                    <p>{result.name}</p>
                  </div>
                  <button onClick={() => handleAddToWatchlist(result.symbol)}>
                    Add to Watchlist
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      <div className="dashboard-section">
        <h2>My Watchlist</h2>
        {watchlist.length === 0 ? (
          <p className="empty-state">No stocks in your watchlist. Search and add some!</p>
        ) : (
          <div className="stocks-grid">
            {watchlist.map((item) => (
              <div
                key={item.id}
                className="stock-card"
                onClick={() => navigate(`/stock/${item.symbol}`)}
              >
                <div className="stock-card-header">
                  <h3>{item.symbol}</h3>
                  <button
                    className="remove-btn"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleRemoveFromWatchlist(item.id);
                    }}
                  >
                    ✕
                  </button>
                </div>
                <p className="stock-name">{item.name}</p>
                <p className="stock-price">${item.currentPrice.toFixed(2)}</p>
                <p className="added-at">Added: {new Date(item.addedAt).toLocaleDateString()}</p>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="dashboard-section">
        <h2>Top Movers</h2>
        <div className="stocks-grid">
          {topMovers.slice(0, 6).map((stock) => (
            <div
              key={stock.symbol}
              className="stock-card"
              onClick={() => navigate(`/stock/${stock.symbol}`)}
            >
              <h3>{stock.symbol}</h3>
              <p className="stock-price">${stock.price.toFixed(2)}</p>
              <p className={`stock-change ${stock.change >= 0 ? 'positive' : 'negative'}`}>
                {stock.change >= 0 ? '+' : ''}{stock.changePercent.toFixed(2)}%
              </p>
              <p className="stock-volume">Vol: {(stock.volume / 1000000).toFixed(2)}M</p>
            </div>
          ))}
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}
    </div>
  );
};
