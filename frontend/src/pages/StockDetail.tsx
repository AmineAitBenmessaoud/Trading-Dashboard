import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { marketService, MarketData, HistoricalData } from '../services/marketService';
import '../styles/StockDetail.css';

export const StockDetail: React.FC = () => {
  const { symbol } = useParams<{ symbol: string }>();
  const [marketData, setMarketData] = useState<MarketData | null>(null);
  const [historicalData, setHistoricalData] = useState<HistoricalData[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [period, setPeriod] = useState('1M');

  useEffect(() => {
    const fetchData = async () => {
      if (!symbol) return;
      
      setLoading(true);
      setError('');
      
      try {
        const [market, historical] = await Promise.all([
          marketService.getMarketData(symbol),
          marketService.getHistoricalData(symbol, period),
        ]);
        
        setMarketData(market);
        setHistoricalData(historical);
      } catch (err) {
        setError('Failed to load stock data');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [symbol, period]);

  if (loading) return <div className="loading">Loading stock data...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!marketData) return <div className="error">Stock not found</div>;

  const isPositive = marketData.change >= 0;

  return (
    <div className="stock-detail">
      <div className="stock-header">
        <div>
          <h1>{marketData.symbol}</h1>
          <p className="stock-price">${marketData.price.toFixed(2)}</p>
          <p className={`stock-change ${isPositive ? 'positive' : 'negative'}`}>
            {isPositive ? '+' : ''}{marketData.change.toFixed(2)} ({marketData.changePercent.toFixed(2)}%)
          </p>
        </div>
        <div className="stock-stats">
          <div className="stat">
            <span className="label">High</span>
            <span className="value">${marketData.high !== undefined ? marketData.high.toFixed(2) : 'N/A'}</span>
          </div>
          <div className="stat">
            <span className="label">Low</span>
            <span className="value">${marketData.low !== undefined ? marketData.low.toFixed(2) : 'N/A'}</span>
          </div>
          <div className="stat">
            <span className="label">Volume</span>
            <span className="value">{marketData.volume !== undefined ? (marketData.volume / 1000000).toFixed(2) : 'N/A'}M</span>
          </div>
        </div>
      </div>

      <div className="period-selector">
        {['1D', '1W', '1M', '3M', '1Y'].map((p) => (
          <button
            key={p}
            className={`period-btn ${period === p ? 'active' : ''}`}
            onClick={() => setPeriod(p)}
          >
            {p}
          </button>
        ))}
      </div>

      {historicalData.length > 0 && (
        <div className="chart-container">
          <ResponsiveContainer width="100%" height={400}>
            <LineChart data={historicalData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis />
              <Tooltip
                formatter={(value) => `$${Number(value).toFixed(2)}`}
                labelStyle={{ color: '#000' }}
              />
              <Legend />
              <Line
                type="monotone"
                dataKey="close"
                stroke="#8884d8"
                name="Close Price"
                dot={false}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      )}
    </div>
  );
};
