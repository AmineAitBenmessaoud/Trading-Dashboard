import React from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Cell,
} from 'recharts';
import { MarketData } from '../../services/marketService';

interface TopMoversChartProps {
  data: MarketData[];
}

export const TopMoversChart: React.FC<TopMoversChartProps> = ({ data }) => {
  const chartData = data.slice(0, 10).map((stock) => ({
    symbol: stock.symbol,
    changePercent: stock.changePercent,
    change: stock.change,
    price: stock.price,
  }));

  const getBarColor = (value: number) => {
    if (value > 0) return '#10b981';
    if (value < 0) return '#ef4444';
    return '#6b7280';
  };

  return (
    <div className="top-movers-chart-container">
      <ResponsiveContainer width="100%" height={350}>
        <BarChart data={chartData} layout="vertical" margin={{ top: 5, right: 30, left: 80, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
          <XAxis type="number" stroke="#666" />
          <YAxis dataKey="symbol" type="category" stroke="#666" width={75} />
          <Tooltip
            formatter={(value) => `${Number(value).toFixed(2)}%`}
            contentStyle={{
              backgroundColor: '#ffffff',
              border: '1px solid #ccc',
              borderRadius: '4px',
              padding: '8px',
            }}
            labelStyle={{ color: '#000' }}
          />
          <Bar dataKey="changePercent" name="Change %">
            {chartData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={getBarColor(entry.changePercent)} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};
